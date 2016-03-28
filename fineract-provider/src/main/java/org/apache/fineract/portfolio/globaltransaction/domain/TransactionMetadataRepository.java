package org.apache.fineract.portfolio.globaltransaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionMetadataRepository extends JpaRepository<TransactionMetadata, Long>, JpaSpecificationExecutor<TransactionMetadata> {
    // no behaviour
}