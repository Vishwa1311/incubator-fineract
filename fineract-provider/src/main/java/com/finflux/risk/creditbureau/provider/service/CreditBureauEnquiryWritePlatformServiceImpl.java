package com.finflux.risk.creditbureau.provider.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepository;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProductRepositoryWrapper;
import com.finflux.risk.creditbureau.provider.data.CreditBureauExistingLoan;
import com.finflux.risk.creditbureau.provider.data.CreditBureauReportFile;
import com.finflux.risk.creditbureau.provider.data.CreditBureauResponse;
import com.finflux.risk.creditbureau.provider.data.EnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.data.EnquiryResponse;
import com.finflux.risk.creditbureau.provider.data.LoanEnquiryReferenceData;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryRepository;
import com.finflux.risk.creditbureau.provider.domain.LoanCreditBureauEnquiry;
import com.finflux.risk.creditbureau.provider.domain.LoanCreditBureauEnquiryRepository;
import com.finflux.risk.existingloans.domain.ExistingLoan;
import com.finflux.risk.existingloans.domain.ExistingLoanRepositoryWrapper;

@Service
public class CreditBureauEnquiryWritePlatformServiceImpl implements CreditBureauEnquiryWritePlatformService {

    private final CreditBureauEnquiryRepository creditBureauEnquiryRepository;
    private final LoanCreditBureauEnquiryRepository loanCreditBureauEnquiryMappingRepository;
    private final ExistingLoanRepositoryWrapper existingLoanRepository;
    private final CodeValueRepository codeValueRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final CreditBureauProductRepositoryWrapper creditBureauProductRepository;

    @Autowired
    public CreditBureauEnquiryWritePlatformServiceImpl(final CreditBureauEnquiryRepository creditBureauEnquiryRepository,
            final LoanCreditBureauEnquiryRepository creditBureauRequestDetailsRepository,
            final ExistingLoanRepositoryWrapper existingLoanRepository, final CodeValueRepository codeValueRepository,
            final ClientRepositoryWrapper clientRepository, final CreditBureauProductRepositoryWrapper creditBureauProductRepository) {
        this.creditBureauEnquiryRepository = creditBureauEnquiryRepository;
        this.loanCreditBureauEnquiryMappingRepository = creditBureauRequestDetailsRepository;
        this.existingLoanRepository = existingLoanRepository;
        this.codeValueRepository = codeValueRepository;
        this.clientRepository = clientRepository;
        this.creditBureauProductRepository = creditBureauProductRepository;
    }

    @Transactional
    @Override
    public void saveEnquiryResponseDetails(EnquiryReferenceData enquiryReferenceData, CreditBureauResponse responseData) {

        EnquiryResponse response = responseData.getEnquiryResponse();
        CreditBureauEnquiry creditBureauEnquiry = this.creditBureauEnquiryRepository.findOne(enquiryReferenceData.getEnquiryId());
        creditBureauEnquiry.setAcknowledgementNumber(response.getAcknowledgementNumber());
        creditBureauEnquiry.setStatus(response.getStatus().getValue());
        creditBureauEnquiry.setResponse(response.getResponse());

        List<LoanCreditBureauEnquiry> creditBureauLoanEnquiries = creditBureauEnquiry.getLoanCreditBureauEnquiryMapping();
        if (creditBureauLoanEnquiries != null && !creditBureauLoanEnquiries.isEmpty()) {
            LoanCreditBureauEnquiry loanEnquiry = creditBureauLoanEnquiries.get(0);
            loanEnquiry.setStatus(response.getStatus().getValue());
            loanEnquiry.setCbReportId(response.getReportId());
            // this.loanCreditBureauEnquiryMappingRepository.save(loanEnquiry);
        }
        this.creditBureauEnquiryRepository.save(creditBureauEnquiry);
    }

    @Transactional
    @Override
    public void saveReportResponseDetails(LoanEnquiryReferenceData loanEnquiryReferenceData, CreditBureauResponse responseData) {

        EnquiryResponse response = responseData.getEnquiryResponse();
        CreditBureauEnquiry creditBureauEnquiry = this.creditBureauEnquiryRepository.findOne(loanEnquiryReferenceData.getEnquiryId());
        creditBureauEnquiry.setStatus(response.getStatus().getValue());
        this.creditBureauEnquiryRepository.save(creditBureauEnquiry);

        List<LoanCreditBureauEnquiry> creditBureauLoanEnquiries = creditBureauEnquiry.getLoanCreditBureauEnquiryMapping();
        if (creditBureauLoanEnquiries != null && !creditBureauLoanEnquiries.isEmpty()) {
            LoanCreditBureauEnquiry loanEnquiry = creditBureauLoanEnquiries.get(0);
            loanEnquiry.setResponse(response.getResponse());
            loanEnquiry.setStatus(response.getStatus().getValue());

            if (responseData.getCreditBureauReportFile() != null) {
                CreditBureauReportFile reportFile = responseData.getCreditBureauReportFile();
                loanEnquiry.setFileName(reportFile.getFileName());
                loanEnquiry.setFileType(reportFile.getFileType().getValue());
                loanEnquiry.setFileContent(reportFile.getFileContent());
            }
            this.loanCreditBureauEnquiryMappingRepository.save(loanEnquiry);
        }
        if (responseData.getCreditBureauExistingLoans() != null) {
            saveCreditBureauExistingLoans(loanEnquiryReferenceData.getLoanApplicationId(), responseData.getCreditBureauExistingLoans());
        }
    }

    @SuppressWarnings("unused")
    private void saveCreditBureauExistingLoans(final Long loanApplicationId, final List<CreditBureauExistingLoan> creditBureauExistingLoans) {
        final CodeValue source = this.codeValueRepository.findByCodeNameAndLabel("ExistingLoanSource", "Credit Bureau");
        final List<ExistingLoan> existingLoans = this.existingLoanRepository
                .findByLoanApplicationIdAndSource(loanApplicationId, source);
        if (!existingLoans.isEmpty()) {
            this.existingLoanRepository.delete(existingLoans);
        }
        final List<ExistingLoan> newExistingLoans = new ArrayList<ExistingLoan>();
        Client client = null;
        CodeValue lender = null;
        CodeValue loanType = null;
        for (final CreditBureauExistingLoan creditBureauExistingLoan : creditBureauExistingLoans) {
            final Long clientId = creditBureauExistingLoan.getClientId();
            /**
             * Same Client then no need to fetch again client object
             */
            if (client == null || client.getId() != clientId) {
                client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            }
            final Long loanId = creditBureauExistingLoan.getLoanId();
            final Long creditBureauProductId = creditBureauExistingLoan.getCreditBureauProductId();
            final CreditBureauProduct creditBureauProduct = this.creditBureauProductRepository
                    .findOneWithNotFoundDetection(creditBureauProductId);
            final Long loanEnquiryId = creditBureauExistingLoan.getLoanEnquiryId();
            final String lenderName = creditBureauExistingLoan.getLenderName();
            if (lenderName != null && (lender == null || !lender.label().equalsIgnoreCase(lenderName))) {
                lender = this.codeValueRepository.findByCodeNameAndLabel("LenderOption", lenderName);
            }
            final String loanTypeName = creditBureauExistingLoan.getLoanType();
            if (loanTypeName != null && (loanType == null || !loanType.label().equalsIgnoreCase(loanTypeName))) {
                loanType = this.codeValueRepository.findByCodeNameAndLabel("LoanType", loanTypeName);
            }
            BigDecimal amountBorrowed = null;
            if (creditBureauExistingLoan.getAmountDisbursed() != null) {
                amountBorrowed = new BigDecimal(creditBureauExistingLoan.getAmountDisbursed());
            }
            BigDecimal currentOutstanding = null;
            if (creditBureauExistingLoan.getCurrentOutstanding() != null) {
                currentOutstanding = new BigDecimal(creditBureauExistingLoan.getCurrentOutstanding());
            }
            BigDecimal amtOverdue = null;
            if (creditBureauExistingLoan.getAmountOverdue() != null) {
                amtOverdue = new BigDecimal(creditBureauExistingLoan.getAmountOverdue());
            }
            BigDecimal writtenoffamount = null;
            if (creditBureauExistingLoan.getWrittenOffAmount() != null) {
                writtenoffamount = new BigDecimal(creditBureauExistingLoan.getWrittenOffAmount());
            }
            BigDecimal installmentAmount = null;
            if (creditBureauExistingLoan.getInstallmentAmount() != null) {
                installmentAmount = new BigDecimal(creditBureauExistingLoan.getInstallmentAmount());
            }

            final CodeValue externalLoanPurpose = null;
            final Integer status = creditBureauExistingLoan.getLoanStatus().getValue();
            LocalDate disbursedDate = null;
            if (creditBureauExistingLoan.getDisbursedDate() != null) {
                disbursedDate = new LocalDate(creditBureauExistingLoan.getDisbursedDate());
            }
            final Integer loanTenure = null;
            Integer loanTenurePeriodType = null;
            if (creditBureauExistingLoan.getRepaymentFrequency() != null) {
                loanTenurePeriodType = creditBureauExistingLoan.getRepaymentFrequency().getValue();
            }
            final Integer repaymentFrequency = null;
            final Integer repaymentFrequencyMultipleOf = null;
            final LocalDate maturityDate = null;
            final Integer gt0dpd3mths = null;
            final Integer dpd30mths12 = null;
            final Integer dpd30mths24 = null;
            final Integer dpd60mths24 = null;
            final String remark = null;
            final Integer archive = null;

            final ExistingLoan existingLoan = ExistingLoan.saveExistingLoan(client, loanApplicationId, loanId, source, creditBureauProduct,
                    loanEnquiryId, lender, lenderName, loanType, amountBorrowed, currentOutstanding, amtOverdue, writtenoffamount,
                    loanTenure, loanTenurePeriodType, repaymentFrequency, repaymentFrequencyMultipleOf, installmentAmount,
                    externalLoanPurpose, status, disbursedDate, maturityDate, gt0dpd3mths, dpd30mths12, dpd30mths24, dpd60mths24, remark,
                    archive);
            newExistingLoans.add(existingLoan);
        }

        if (!newExistingLoans.isEmpty()) {
            this.existingLoanRepository.save(newExistingLoans);
        }
    }

    @Transactional
    @Override
    public CreditBureauEnquiry createNewEnquiry(final CreditBureauEnquiry creditBureauEnquiry) {
        return creditBureauEnquiryRepository.save(creditBureauEnquiry);
    }
}