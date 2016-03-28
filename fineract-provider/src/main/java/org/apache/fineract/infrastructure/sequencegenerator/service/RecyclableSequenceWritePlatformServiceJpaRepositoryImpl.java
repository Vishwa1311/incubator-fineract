package org.apache.fineract.infrastructure.sequencegenerator.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.fineract.infrastructure.sequencegenerator.domain.RecyclableSequenceRepository;
import org.apache.fineract.infrastructure.sequencegenerator.exception.SequenceGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecyclableSequenceWritePlatformServiceJpaRepositoryImpl implements RecyclableSequenceWritePlatformService {

    final RecyclableSequenceRepository recyclableSequenceRepository;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired    
    public RecyclableSequenceWritePlatformServiceJpaRepositoryImpl(RecyclableSequenceRepository recyclableSequenceRepository) {
        super();
        this.recyclableSequenceRepository = recyclableSequenceRepository;
    }

    @Transactional
    @Override
    public boolean updateNextSequence(String sequenceName) {
        int updated = recyclableSequenceRepository.updateNextSequence(sequenceName);
        if (updated == 0) { throw new SequenceGenerationException(sequenceName); }

        return !(updated == 0);
    }

    @Transactional
    @Override
    public boolean updateNextSequenceByDay(String sequenceName) {

        String today = sdf.format(new Date());
        int updated = recyclableSequenceRepository.updateNextSequenceForDate(sequenceName, today);

        if (updated == 0) {
            updated = recyclableSequenceRepository.updateNextSequenceAndDateForSequence(new Date(), sequenceName);
        }

        
        return !(updated == 0);
    }

    @Override
    public String getUpdateSequence(String sequenceName) {
        return this.recyclableSequenceRepository.getUpdatedSequence(sequenceName);
    }
    
}