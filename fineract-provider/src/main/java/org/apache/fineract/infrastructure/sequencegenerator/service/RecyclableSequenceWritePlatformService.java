package org.apache.fineract.infrastructure.sequencegenerator.service;


public interface RecyclableSequenceWritePlatformService {

    /**
     * generate the next incremented id for a given sequence number by name
     */
    public abstract boolean updateNextSequence(String sequenceName);
    
    /**
     * generate the next incremented id for a given sequence number by name
     */
    public abstract boolean updateNextSequenceByDay(String sequenceName);
    
    public abstract String getUpdateSequence(String sequenceName);
}