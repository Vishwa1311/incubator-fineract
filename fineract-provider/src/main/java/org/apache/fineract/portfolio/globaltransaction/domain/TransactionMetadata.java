package org.apache.fineract.portfolio.globaltransaction.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_transaction_metadata", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "gbl_txn_id", "dataKey" }, name = "gbl_txn_id_dataKey_UNIQUE")})
public class TransactionMetadata extends AbstractPersistable<Long> {
    
    @ManyToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "gbl_txn_id", nullable = false)
    private GlobalTransactionReference gblTxnRef;
    
    @Column(name = "dataKey", length = 128, nullable = false)
    private String dataKey;

    @Column(name = "dataValue", nullable = false)
    private String dataValue;

    
    protected TransactionMetadata() {
        // TODO Auto-generated constructor stub
    }

    private TransactionMetadata(GlobalTransactionReference gblTxnRef, String dataKey, String dataValue) {
        this.gblTxnRef = gblTxnRef;
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }
    
    public static TransactionMetadata createNew(GlobalTransactionReference gblTxnRef, String dataKey, String dataValue){
        
        return new TransactionMetadata(gblTxnRef, dataKey, dataValue);
    }

    
    public GlobalTransactionReference getGblTxnRef() {
        return this.gblTxnRef;
    }

    
    public String getDataKey() {
        return this.dataKey;
    }

    
    public String getDataValue() {
        return this.dataValue;
    }

    
    public void setGblTxnRef(GlobalTransactionReference gblTxnRef) {
        this.gblTxnRef = gblTxnRef;
    }
    
}
