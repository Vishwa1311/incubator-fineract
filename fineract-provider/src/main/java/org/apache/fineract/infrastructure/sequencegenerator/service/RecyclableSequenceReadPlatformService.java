package org.apache.fineract.infrastructure.sequencegenerator.service;

import org.apache.fineract.infrastructure.sequencegenerator.data.RecyclableSequenceData;


public interface RecyclableSequenceReadPlatformService {

    RecyclableSequenceData getNextSequence(String sequenceName);
}
