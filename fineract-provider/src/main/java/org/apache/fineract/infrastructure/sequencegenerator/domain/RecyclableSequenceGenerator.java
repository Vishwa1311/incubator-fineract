package org.apache.fineract.infrastructure.sequencegenerator.domain;

import org.apache.fineract.infrastructure.sequencegenerator.exception.SequenceGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecyclableSequenceGenerator implements SequenceGenerator{
    
    final RecyclableSequenceRepository recyclableSequenceRepository;
    
    @Autowired
    public RecyclableSequenceGenerator(RecyclableSequenceRepository recyclableSequenceRepository) {
        this.recyclableSequenceRepository = recyclableSequenceRepository;
    }

    @Override
    public String getNextSequence(String sequenceName) {
        
        int updated = recyclableSequenceRepository.updateNextSequence(sequenceName);
        if(updated == 0){
            throw new SequenceGenerationException(sequenceName);
        }
        
        return this.recyclableSequenceRepository.getSequence(sequenceName);
    }
    
}