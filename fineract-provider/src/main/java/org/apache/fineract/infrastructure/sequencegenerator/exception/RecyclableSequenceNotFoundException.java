package org.apache.fineract.infrastructure.sequencegenerator.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when global transaction reference resources are not found.
 */
public class RecyclableSequenceNotFoundException extends AbstractPlatformResourceNotFoundException {

    public RecyclableSequenceNotFoundException(final String name) {
        super("error.msg.recyclable.sequence.name.not.found", "Recyclable Sequence for  " + name + " does not exist", name);
    }
}