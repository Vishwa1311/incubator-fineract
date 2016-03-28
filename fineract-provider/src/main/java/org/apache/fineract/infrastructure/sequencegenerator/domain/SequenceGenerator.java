package org.apache.fineract.infrastructure.sequencegenerator.domain;


public interface SequenceGenerator {
    /**
     * get the next incremented id for a given sequence number by name and
     * client id
     */
    public abstract String getNextSequence(String sequenceName);

}
