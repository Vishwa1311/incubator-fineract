package org.apache.fineract.portfolio.globaltransaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GlobalTransactionReferenceRepository extends JpaRepository<GlobalTransactionReference, Long>, JpaSpecificationExecutor<GlobalTransactionReference> {
    // no behaviour
}