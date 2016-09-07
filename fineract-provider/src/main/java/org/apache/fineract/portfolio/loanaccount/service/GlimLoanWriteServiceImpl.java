package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanGlimRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanGlimRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlimLoanWriteServiceImpl implements GlimLoanWriteService, BusinessEventListner {

    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(GlimLoanWriteServiceImpl.class);
    private final BusinessEventNotifierService businessEventNotifierService;
    private final LoanGlimRepaymentScheduleInstallmentRepository loanGlimRepaymentScheduleInstallmentRepository;

    @Autowired
    public GlimLoanWriteServiceImpl(final BusinessEventNotifierService businessEventNotifierService,
            final LoanGlimRepaymentScheduleInstallmentRepository loanGlimRepaymentScheduleInstallmentRepository) {
        this.businessEventNotifierService = businessEventNotifierService;
        this.loanGlimRepaymentScheduleInstallmentRepository = loanGlimRepaymentScheduleInstallmentRepository;
    }

    @PostConstruct
    public void registerForNotification() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL,
                new GlimLoanRepaymentSchedulaEventListner());
    }

    @SuppressWarnings("unused")
    @Override
    public void generateGlimLoanRepaymentSchedule(final Loan loan) {
        if (loan.isGLIMLoan()) {
            final List<LoanRepaymentScheduleInstallment> installments = loan.getRepaymentScheduleInstallments();
            final List<GroupLoanIndividualMonitoring> glimMembers = loan.getGroupLoanIndividualMonitoringList();
            final List<LoanGlimRepaymentScheduleInstallment> loanGlimRepaymentScheduleInstallments = new LinkedList<>();
            for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
                for (GroupLoanIndividualMonitoring glimMember : glimMembers) {
                    Map<String, BigDecimal> installmentPaidMap = new HashMap<String, BigDecimal>();
                    installmentPaidMap.put("unpaidCharge", BigDecimal.ZERO);
                    installmentPaidMap.put("unpaidInterest", BigDecimal.ZERO);
                    installmentPaidMap.put("unpaidPrincipal", BigDecimal.ZERO);
                    installmentPaidMap.put("installmentTransactionAmount", BigDecimal.ZERO);
                    Map<String, BigDecimal> paidInstallmentMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                            glimMember.getInstallmentAmount(), loan, currentInstallment.getInstallmentNumber(), installmentPaidMap, null);
                    final BigDecimal principal = paidInstallmentMap.get("unpaidPrincipal");
                    final BigDecimal interestCharged = paidInstallmentMap.get("unpaidInterest");
                    final BigDecimal feeChargesCharged = paidInstallmentMap.get("unpaidCharge");
                    final LoanGlimRepaymentScheduleInstallment loanGlimRepaymentScheduleInstallment = LoanGlimRepaymentScheduleInstallment
                            .create(glimMember, currentInstallment, principal, interestCharged, feeChargesCharged, null);
                    loanGlimRepaymentScheduleInstallments.add(loanGlimRepaymentScheduleInstallment);
                }
            }
            if (!loanGlimRepaymentScheduleInstallments.isEmpty()) {
                this.loanGlimRepaymentScheduleInstallmentRepository.save(loanGlimRepaymentScheduleInstallments);
            }
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unused")
    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    private class GlimLoanRepaymentSchedulaEventListner implements BusinessEventListner {

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub

        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object loanEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN);
            if (loanEntity != null) {
                Loan loan = (Loan) loanEntity;
                generateGlimLoanRepaymentSchedule(loan);
            }
        }

    }
}