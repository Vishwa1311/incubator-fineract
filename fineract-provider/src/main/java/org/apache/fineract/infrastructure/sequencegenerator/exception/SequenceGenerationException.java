package org.apache.fineract.infrastructure.sequencegenerator.exception;

import org.apache.fineract.infrastructure.core.exception.PlatformInternalServerException;

public class SequenceGenerationException extends PlatformInternalServerException {

    public SequenceGenerationException(final String sequenceName) {
        super("error.msg." + sequenceName.toLowerCase() + ".sequence.generation.failed", sequenceName);
    }
}