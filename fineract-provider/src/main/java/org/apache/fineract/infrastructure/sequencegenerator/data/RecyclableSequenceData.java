package org.apache.fineract.infrastructure.sequencegenerator.data;

import org.apache.commons.lang.StringUtils;

public class RecyclableSequenceData {

    final Long sequenceValue;
    final String paddingChar;
    final int sequenceLength;
    final String sequence;
    public static RecyclableSequenceData instance(Long sequenceValue, String paddingChar, int sequenceLength){
        final String sequence = StringUtils.leftPad(sequenceValue.toString(), sequenceLength, paddingChar);
        return new RecyclableSequenceData(sequenceValue, paddingChar, sequenceLength, sequence);
    }
    
    private RecyclableSequenceData(Long sequenceValue, String paddingChar, int sequenceLength, String sequence) {
        this.sequenceValue = sequenceValue;
        this.paddingChar = paddingChar;
        this.sequenceLength = sequenceLength;
        this.sequence = sequence;
    }

    
    public String getSequence() {
        return this.sequence;
    }
    
}
