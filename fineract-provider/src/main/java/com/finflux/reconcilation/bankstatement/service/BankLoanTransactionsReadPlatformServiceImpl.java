package com.finflux.reconcilation.bankstatement.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;

@Service
public class BankLoanTransactionsReadPlatformServiceImpl implements BankLoanTransactionsReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public BankLoanTransactionsReadPlatformServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class LoanTransactionsForBankStatementDetailsMapper implements RowMapper<LoanTransactionData> {

        public String LoanTransactionsForBankStatementDetailsSchema() {

            return " tr.id as id, tr.transaction_type_enum as transactionType, tr.transaction_date as `date`, "
                    + " tr.amount as amount, tr.office_id as officeId, office.name as officeName, tr.external_id as externalId, "
                    + " l.submittedon_date as submittedOnDate,pt.value as paymentTypeName, "
                    + " pd.payment_type_id as paymentType, pd.account_number as accountNumber,pd.check_number as checkNumber, "
                    + " pd.receipt_number as receiptNumber, pd.bank_number as bankNumber,pd.routing_code as routingCode, g.account_no as groupAccountNumber, l.account_no as loanAccountNumber "
                    + " from m_loan l " + " join m_loan_transaction tr on tr.loan_id = l.id "
                    + " left JOIN m_payment_detail pd ON tr.payment_detail_id = pd.id "
                    + " left join m_payment_type pt on pd.payment_type_id = pt.id "
                    + " left join m_office office on office.id=tr.office_id " 
                    + " LEFT JOIN m_group g ON g.id=l.group_id ";
        }

        @Override
        public LoanTransactionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(transactionTypeInt);

            PaymentDetailData paymentDetailData = null;

            if (transactionType.isPaymentOrReceipt()) {
                final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentType");
                if (paymentTypeId != null) {
                    final String typeName = rs.getString("paymentTypeName");
                    final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, typeName);
                    final String accountNumber = rs.getString("accountNumber");
                    final String checkNumber = rs.getString("checkNumber");
                    final String routingCode = rs.getString("routingCode");
                    final String receiptNumber = rs.getString("receiptNumber");
                    final String bankNumber = rs.getString("bankNumber");
                    paymentDetailData = new PaymentDetailData(id, paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                            bankNumber);
                }
            }
            final LocalDate date = JdbcSupport.getLocalDate(rs, "date");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final BigDecimal totalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amount");
            final String externalId = rs.getString("externalId");
            final String groupAccountNumber = rs.getString("groupAccountNumber");
            final String loanAccountNumber = rs.getString("loanAccountNumber");
            return LoanTransactionData.LoanTransactionDataTemplate(id, officeId, officeName, transactionType, paymentDetailData, date,
                    totalAmount, null, externalId, submittedOnDate, groupAccountNumber, loanAccountNumber);
        }
    }
    
    private static final class LoanTransactionDataMapper implements RowMapper<LoanTransactionData> {

   	 public String schema() {
       	
       return	" tr.id AS id, tr.transaction_type_enum AS transactionType, tr.transaction_date AS transactionDate,"+
       " tr.amount AS amount, office.id as officeId,office.name AS officeName,g.external_id AS groupExternalId,l.account_no AS loanAccountNumber"+
       " FROM m_loan l "+" JOIN m_loan_transaction tr ON tr.loan_id = l.id and tr.id = ? "+
       " LEFT JOIN m_office office ON office.id=tr.office_id "+
       " LEFT JOIN m_group g ON g.id=l.group_id ";

       }

     @Override
     public LoanTransactionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

    	 final Long id = JdbcSupport.getLong(rs, "id");
           final String loanAccountNumber = rs.getString("loanAccountNumber");
           LoanTransactionEnumData transactionType = null;
           if (JdbcSupport.getInteger(rs, "transactionType") != null) {
               int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
               transactionType = LoanEnumerations.transactionType(transactionTypeInt);
           }
           final String officeName = rs.getString("officeName");
           final String groupExternalId = rs.getString("groupExternalId");            
           final BigDecimal amount = rs.getBigDecimal("amount");
           final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
           final Long officeId = rs.getLong("officeId");
           
           return LoanTransactionData.LoanTransactionDataForReconciliationLoanTransactionData(id,
        		   officeId, officeName, transactionType, transactionDate, amount, groupExternalId, loanAccountNumber);

       }
   }
 
    private static final class LoanTransactionDataOptionsMapper implements RowMapper<LoanTransactionData> {

      	 public String schema() {
          	
          return	" tr.id AS id, tr.transaction_type_enum AS transactionType, tr.transaction_date AS transactionDate,"+
          " tr.amount AS amount, office.id as officeId,office.name AS officeName,g.external_id AS groupExternalId,l.account_no AS loanAccountNumber"+
          " FROM m_loan l "+" JOIN m_loan_transaction tr ON (tr.loan_id = l.id and tr.transaction_type_enum in (1,2,8) and tr.is_reversed = 0 and tr.is_reconciled = 0 ) "+
          " LEFT JOIN m_office office ON office.id=tr.office_id "+
          " LEFT JOIN m_group g ON g.id=l.group_id ";

          }

        @Override
        public LoanTransactionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

       	 final Long id = JdbcSupport.getLong(rs, "id");
              final String loanAccountNumber = rs.getString("loanAccountNumber");
              LoanTransactionEnumData transactionType = null;
              if (JdbcSupport.getInteger(rs, "transactionType") != null) {
                  int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
                  transactionType = LoanEnumerations.transactionType(transactionTypeInt);
              }
              final String officeName = rs.getString("officeName");
              final String groupExternalId = rs.getString("groupExternalId");            
              final BigDecimal amount = rs.getBigDecimal("amount");
              final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
              final Long officeId = rs.getLong("officeId");
              
              return LoanTransactionData.LoanTransactionDataForReconciliationLoanTransactionData(id,
           		   officeId, officeName, transactionType, transactionDate, amount, groupExternalId, loanAccountNumber);

          }
      }

    
    @Override
    public Collection<LoanTransactionData> retrieveLoanTransactionsForGroup(BigDecimal amount, String groupExternalId,
            String transactionType) {
        try {
            this.context.authenticatedUser();

            final LoanTransactionsForBankStatementDetailsMapper rm = new LoanTransactionsForBankStatementDetailsMapper();

           /* final String sql = "select " + rm.LoanTransactionsForBankStatementDetailsSchema() + " where tr.amount = ? and "
                    + " g.external_id = ?  and tr.is_reversed = 0 and tr.is_reconciled = 0 and (tr.transaction_type_enum = ?)";*/

            if (transactionType != null
                    && (transactionType.trim().replaceAll(" ", "")).equalsIgnoreCase(ReconciliationApiConstants.DISBURSAL.trim()
                            .replaceAll(" ", ""))) { 
            	final String sql = "select " + rm.LoanTransactionsForBankStatementDetailsSchema() + " where tr.amount = ? and "
                        + " g.external_id = ?  and tr.is_reversed = 0 and tr.is_reconciled = 0 and (tr.transaction_type_enum = ?)";
            	return this.jdbcTemplate.query(sql, rm, new Object[] { amount, groupExternalId,
                    LoanTransactionType.DISBURSEMENT.getValue() }); 
            	
            	}
            final String sql = "select " + rm.LoanTransactionsForBankStatementDetailsSchema() + " where tr.amount = ? and "
                    + " g.external_id = ?  and tr.is_reversed = 0 and tr.is_reconciled = 0 and (tr.transaction_type_enum = ? or tr.transaction_type_enum = ?)";
            return this.jdbcTemplate.query(sql, rm,
                    new Object[] { amount, groupExternalId, LoanTransactionType.REPAYMENT.getValue(), LoanTransactionType.RECOVERY_REPAYMENT.getValue()});

        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<LoanTransactionData> retrieveLoanTransactionsForBankDetails(Long loanTransactionId) {
        try {
            this.context.authenticatedUser();

            final LoanTransactionsForBankStatementDetailsMapper rm = new LoanTransactionsForBankStatementDetailsMapper();

            final String sql = "select " + rm.LoanTransactionsForBankStatementDetailsSchema() + " where tr.id = ? ";

            return this.jdbcTemplate.query(sql, rm, new Object[] { loanTransactionId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

	@Override
	public LoanTransactionData getReconciledLoanTransaction(
			Long loanTransactionId) {
		try {
            this.context.authenticatedUser();

            final LoanTransactionDataMapper rm = new LoanTransactionDataMapper();

            final String sql = "select " + rm.schema();

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanTransactionId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
	}

	@Override
	public List<LoanTransactionData> getLoanTransactionOptions(BankStatementDetailsData bankStatementDetailData) {
		try {
            this.context.authenticatedUser();

            final LoanTransactionDataOptionsMapper rm = new LoanTransactionDataOptionsMapper();

            String sql = "select " + rm.schema()+ " where tr.amount = ? and g.external_id = ? and tr.transaction_date = \'"+bankStatementDetailData.getTransactionDate()+"\' ";
            if(bankStatementDetailData.getAmount().intValue()==1646){
            	System.out.println("sql: "+sql);
            }
            if(bankStatementDetailData.getTransactionType().equalsIgnoreCase(ReconciliationApiConstants.DISBURSAL)){
            	sql = sql + " and tr.transaction_type_enum = ? ";
            	return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementDetailData.getAmount(), bankStatementDetailData.getGroupExternalId(),
            			LoanTransactionType.DISBURSEMENT.getValue() });
            }
            sql = sql + " and tr.transaction_type_enum in (? , ?) ";
            return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementDetailData.getAmount(), bankStatementDetailData.getGroupExternalId(),
            			LoanTransactionType.REPAYMENT.getValue(),LoanTransactionType.RECOVERY_REPAYMENT.getValue() });

        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
	}
}
