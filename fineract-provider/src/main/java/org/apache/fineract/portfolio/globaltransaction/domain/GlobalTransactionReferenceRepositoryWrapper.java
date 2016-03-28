package org.apache.fineract.portfolio.globaltransaction.domain;

import org.apache.fineract.portfolio.globaltransaction.exception.GlobalTransactionReferenceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link GlobalTransactionReferenceRepository} that adds NULL checking and Error
 * handling capabilities
 * </p>
 */
@Service
public class GlobalTransactionReferenceRepositoryWrapper {

    private final GlobalTransactionReferenceRepository repository;

    @Autowired
    public GlobalTransactionReferenceRepositoryWrapper(final GlobalTransactionReferenceRepository repository) {
        this.repository = repository;
    }

    public GlobalTransactionReference findOneWithNotFoundDetection(final Long id) {
        final GlobalTransactionReference transactionReference = this.repository.findOne(id);
        if (transactionReference == null) { throw new GlobalTransactionReferenceNotFoundException(id); }
        return transactionReference;
    }
  
    public void save(final GlobalTransactionReference transactionReference) {
        this.repository.save(transactionReference);
    }

    public void saveAndFlush(final GlobalTransactionReference transactionReference) {
        this.repository.saveAndFlush(transactionReference);
    }

    public void delete(final GlobalTransactionReference transactionReference) {
        this.repository.delete(transactionReference);
    }
}