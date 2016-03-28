package org.apache.fineract.portfolio.globaltransaction.domain;

import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.externalRefNoParamName;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.transactionDateParamName;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.transactionIdParamName;
import static org.apache.fineract.portfolio.globaltransaction.GlobalTransactionReferenceApiConstants.transactionRefNoParamName;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.security.service.RandomPasswordGenerator;
import org.apache.fineract.infrastructure.sequencegenerator.SequenceGeneratorType;
import org.apache.fineract.infrastructure.sequencegenerator.data.RecyclableSequenceData;
import org.apache.fineract.infrastructure.sequencegenerator.service.RecyclableSequenceReadPlatformService;
import org.apache.fineract.infrastructure.sequencegenerator.service.RecyclableSequenceWritePlatformService;
import org.apache.fineract.portfolio.savings.SavingsReversalType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GlobalTransactionReferenceAssembler {

    private final GlobalTransactionReferenceRepository transactionReferenceRepository;
    private final RecyclableSequenceWritePlatformService sequenceGeneratorWritePlatformService;
    private final RecyclableSequenceReadPlatformService sequenceReadPlatformService;

    @Autowired
    public GlobalTransactionReferenceAssembler(final GlobalTransactionReferenceRepository transactionReferenceRepository,
            final RecyclableSequenceWritePlatformService sequenceGeneratorWritePlatformService,
            final RecyclableSequenceReadPlatformService sequenceReadPlatformService) {
        this.transactionReferenceRepository = transactionReferenceRepository;
        this.sequenceGeneratorWritePlatformService = sequenceGeneratorWritePlatformService;
        this.sequenceReadPlatformService = sequenceReadPlatformService;
    }

    public GlobalTransactionReference assembleFrom(final JsonCommand command) {
        final LocalDate transactionDate = command.localDateValueOfParameterNamed(transactionDateParamName);
        return this.assembleFrom(command, transactionDate);
    }

    public GlobalTransactionReference assembleFrom(final JsonCommand command, final LocalDate transactionDate) {
        final Long transactionId = command.longValueOfParameterNamed(transactionIdParamName);
        String externalRefNo = command.stringValueOfParameterNamed(externalRefNoParamName);
        String transactionRefNo = command.stringValueOfParameterNamed(transactionRefNoParamName);
        //String metaData = command.stringValueOfParameterNamed(metaDataParamName);

        GlobalTransactionReference transactionReference = null;
        if (transactionId == null) {

            if (StringUtils.isEmpty(transactionRefNo)) {
                this.sequenceGeneratorWritePlatformService.updateNextSequenceByDay(SequenceGeneratorType.TRANSACTIONS.name());
                final RecyclableSequenceData sequence = this.sequenceReadPlatformService.getNextSequence(SequenceGeneratorType.TRANSACTIONS
                        .name());
                transactionRefNo = new TransactionRefNoGenerator(sequence.getSequence()).generate();
            }

            if (StringUtils.isEmpty(externalRefNo)) {
                externalRefNo = new RandomPasswordGenerator(16).generateRandom();
            }
            

            transactionReference = GlobalTransactionReference.createNew(externalRefNo, transactionDate, transactionRefNo);

        } else {
            transactionReference = this.transactionReferenceRepository.findOne(transactionId);
        }

        return transactionReference;
    }

    public GlobalTransactionReference generateTransactionReference(final LocalDate transactionDate) {

        this.sequenceGeneratorWritePlatformService.updateNextSequenceByDay(SequenceGeneratorType.TRANSACTIONS.name());
        final RecyclableSequenceData sequence = this.sequenceReadPlatformService.getNextSequence(SequenceGeneratorType.TRANSACTIONS.name());

        final String transactionRefNo = new TransactionRefNoGenerator(sequence.getSequence()).generate();
        final String externalRefNo = new RandomPasswordGenerator(16).generateRandom();

        return GlobalTransactionReference.createNew(externalRefNo, transactionDate, transactionRefNo);
    }

    public GlobalTransactionReference transactionReversal(final GlobalTransactionReference transactionReference) {
        
        final String externalRefNo = "R_" + transactionReference.getExtranalRefNo();
        this.sequenceGeneratorWritePlatformService.updateNextSequenceByDay(SequenceGeneratorType.TRANSACTIONS.name());
        final RecyclableSequenceData sequence = this.sequenceReadPlatformService.getNextSequence(SequenceGeneratorType.TRANSACTIONS
                .name());
        final String transactionRefNo = new TransactionRefNoGenerator(sequence.getSequence()).generate();

        final Integer reversalType = SavingsReversalType.COMPLETE.getValue();
        final GlobalTransactionReference newTransactionReference = GlobalTransactionReference.reverse(externalRefNo,
                transactionRefNo, transactionReference, reversalType);
        return newTransactionReference;
    }

}