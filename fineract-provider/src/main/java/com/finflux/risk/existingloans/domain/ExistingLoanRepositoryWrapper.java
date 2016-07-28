package com.finflux.risk.existingloans.domain;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepository;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.existingloans.exception.ExistingLoanNotFoundException;

@Service
public class ExistingLoanRepositoryWrapper {
    private final ExistingLoanRepository repository;
    private final PlatformSecurityContext context;

    @Autowired
    public ExistingLoanRepositoryWrapper(final ExistingLoanRepository repository, final PlatformSecurityContext context) {
        this.repository = repository;
        this.context = context;
    }

    public ExistingLoan findOneWithNotFoundDetection(final Long id) {
        final ExistingLoan existingLoan = this.repository.findOne(id);
        if (existingLoan == null) { throw new ExistingLoanNotFoundException(id); }
        return existingLoan;
    }

    public void save(final ExistingLoan existingLoan) {
        this.repository.save(existingLoan);
    }

    public void saveAndFlush(final ExistingLoan existingLoan) {
        this.repository.saveAndFlush(existingLoan);
    }

    public void delete(final ExistingLoan existingLoan) {
        this.repository.delete(existingLoan);
    }

   



}
