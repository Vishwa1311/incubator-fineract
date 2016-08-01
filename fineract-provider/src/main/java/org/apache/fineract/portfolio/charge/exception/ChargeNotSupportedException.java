package org.apache.fineract.portfolio.charge.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class ChargeNotSupportedException extends AbstractPlatformResourceNotFoundException {

    public ChargeNotSupportedException(final Long chargeId) {
        super("error.msg.charge.not.support.for.glim.loan", "Charge With identifier " + chargeId +" is not supported for GLIM Loan ", chargeId);
    }

}
