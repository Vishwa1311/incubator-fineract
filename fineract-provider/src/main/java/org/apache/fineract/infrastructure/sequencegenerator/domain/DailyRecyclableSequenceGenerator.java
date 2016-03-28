package org.apache.fineract.infrastructure.sequencegenerator.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyRecyclableSequenceGenerator implements SequenceGenerator{
    
    final RecyclableSequenceRepository recyclableSequenceRepository;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    @Autowired
    public DailyRecyclableSequenceGenerator(RecyclableSequenceRepository recyclableSequenceRepository) {
        this.recyclableSequenceRepository = recyclableSequenceRepository;
    }

    @Override
    public String getNextSequence(String sequenceName) {
        String today = sdf.format(new Date());
        int updated = recyclableSequenceRepository.updateNextSequenceForDate(sequenceName, today);
        
        if(updated == 0){
            updated = recyclableSequenceRepository.updateNextSequenceAndDateForSequence(new Date(), sequenceName);
        }

        return this.recyclableSequenceRepository.getUpdatedSequence(sequenceName);
    }
    
}