package org.apache.fineract.portfolio.client.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.office.domain.OrganisationCurrency;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeAppliesTo;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

@Entity
@Table(name = "m_client_recurring_charge")
public class ClientRecurringCharge extends AbstractAuditableCustom<AppUser,Long> {
	@Column(name = "name", length = 100)
	private String name;

	@ManyToOne(optional = false)
	@JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
	private Client client;

	@ManyToOne(optional = false)
	@JoinColumn(name = "charge_id", referencedColumnName = "id", nullable = false)
	private Charge charge;

	@Column(name = "amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal amount;

	@Column(name = "currency_code", length = 3)
	private String currencyCode;

	@Column(name = "charge_applies_to_enum", nullable = false)
	private Integer chargeAppliesTo;

	@Column(name = "charge_time_enum", nullable = false)
	private Integer chargeTimeType;

	@Column(name = "charge_calculation_enum")
	private Integer chargeCalculation;

	@Column(name = "charge_payment_mode_enum", nullable = true)
	private Integer chargePaymentMode;

	@Column(name = "fee_on_day", nullable = true)
	private Integer feeOnDay;

	@Column(name = "fee_interval", nullable = true)
	private Integer feeInterval;

	@Column(name = "fee_on_month", nullable = true)
	private Integer feeOnMonth;

	@Column(name = "is_penalty", nullable = false)
	private boolean penalty;

	@Column(name = "is_active", nullable = false)
	private boolean active;

	@Column(name = "min_cap", scale = 6, precision = 19, nullable = true)
	private BigDecimal minCap;

	@Column(name = "max_cap", scale = 6, precision = 19, nullable = true)
	private BigDecimal maxCap;

	@Column(name = "fee_frequency", nullable = true)
	private Integer feeFrequency;

	@Temporal(TemporalType.DATE)
	@Column(name = "charge_due_date")
	private Date dueDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "inactivated_on_date")
	private Date inactivationDate;

	@Column(name = "is_synch_meeting", nullable = false)
	private boolean synchMeeting;

	public boolean isSynchMeeting() {
		return synchMeeting;
	}

	@Transient
	private OrganisationCurrency currency;

	protected ClientRecurringCharge() {
	}

	public static ClientRecurringCharge createNew(final Client client,
			final Charge charge, final JsonCommand command) {
		final String name = charge.getName();
		final String currencyCode = charge.getCurrencyCode();
		final Boolean synchMeeting = command
				.booleanPrimitiveValueOfParameterNamed(ClientApiConstants.chargesynchmeetingParamName);
		BigDecimal amount = command
				.bigDecimalValueOfParameterNamed(ClientApiConstants.amountParamName);
		amount = (amount == null) ? charge.getAmount() : amount;

		final LocalDate dueDate = command
				.localDateValueOfParameterNamed(ClientApiConstants.dueAsOfDateParamName);

		return new ClientRecurringCharge(client, charge, name, amount,
				currencyCode, dueDate, synchMeeting);

	}

	private ClientRecurringCharge(final Client client, final Charge charge,
			final String name, final BigDecimal amount,
			final String currencyCode, LocalDate dueDate,
			final Boolean synchMeeting) {
		this.charge = charge;
		this.client = client;
		this.name = name;
		this.amount = amount;
		this.currencyCode = currencyCode;
		this.chargeAppliesTo = ChargeAppliesTo.CLIENT.getValue();
		this.chargeTimeType = charge.getChargeTimeType();
		this.chargeCalculation = charge.getChargeCalculation();
		this.penalty = charge.isPenalty();
		this.active = charge.isActive();
		if (isMonthlyFee() || isAnnualFee()) {
			this.feeOnMonth = charge.getFeeOnMonthDay().getMonthOfYear();
			this.feeOnDay = charge.getFeeOnMonthDay().getDayOfMonth();
		}
		this.feeInterval = charge.getFeeInterval();
		this.feeFrequency = charge.feeFrequency();
		this.dueDate = dueDate.toDate();
		this.minCap = charge.getMinCap();
		this.maxCap = charge.getMaxCap();
		this.synchMeeting = synchMeeting;

	}

	public String getName() {
		return this.name;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public Integer getChargeTimeType() {
		return this.chargeTimeType;
	}

	public boolean isPenalty() {
		return this.penalty;
	}

	public BigDecimal getMinCap() {
		return this.minCap;
	}

	public BigDecimal getMaxCap() {
		return this.maxCap;
	}

	public Charge getCharge() {
		return this.charge;
	}

	public ChargeData toData() {

		final EnumOptionData chargeTimeType = ChargeEnumerations
				.chargeTimeType(this.chargeTimeType);
		final EnumOptionData chargeAppliesTo = ChargeEnumerations
				.chargeAppliesTo(this.chargeAppliesTo);
		final EnumOptionData chargeCalculationType = ChargeEnumerations
				.chargeCalculationType(this.chargeCalculation);
		final EnumOptionData chargePaymentmode = ChargeEnumerations
				.chargePaymentMode(this.chargePaymentMode);
		final EnumOptionData feeFrequencyType = ChargeEnumerations
				.chargePaymentMode(this.feeFrequency);
		GLAccountData accountData = null;
		final CurrencyData currency = new CurrencyData(this.currencyCode, null,
				0, 0, null, null);
		return ChargeData.instance(getId(), this.name, this.amount, currency,
				chargeTimeType, chargeAppliesTo, chargeCalculationType,
				chargePaymentmode, getFeeOnMonthDay(), this.feeInterval,
				this.penalty, this.active, this.minCap, this.maxCap,
				feeFrequencyType, accountData);
	}

	public Integer getChargePaymentMode() {
		return this.chargePaymentMode;
	}

	public Integer getFeeInterval() {
		return this.feeInterval;
	}

	public boolean isMonthlyFee() {
		return ChargeTimeType.fromInt(this.chargeTimeType).isMonthlyFee();
	}

	public boolean isAnnualFee() {
		return ChargeTimeType.fromInt(this.chargeTimeType).isAnnualFee();
	}

	public boolean isWeeklyFee() {
		return ChargeTimeType.fromInt(this.chargeTimeType).isWeeklyFee();
	}

	public boolean isOverdueInstallment() {
		return ChargeTimeType.fromInt(this.chargeTimeType)
				.isOverdueInstallment();
	}

	public MonthDay getFeeOnMonthDay() {
		MonthDay feeOnMonthDay = null;
		if (this.feeOnDay != null && this.feeOnMonth != null) {
			feeOnMonthDay = new MonthDay(this.feeOnMonth, this.feeOnDay);
		}
		return feeOnMonthDay;
	}

	public Integer feeInterval() {
		return this.feeInterval;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer feeFrequency() {
		return this.feeFrequency;
	}

	public Date getInactivationDate() {
		return this.inactivationDate;
	}

	public void setCurrency(OrganisationCurrency currency) {
		this.currency = currency;
	}

	public MonetaryCurrency getCurrency() {
		return this.currency.toMonetaryCurrency();
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public Client getClient() {
		return this.client;
	}

	public LocalDate getDueLocalDate() {
		LocalDate dueDate = null;
		if (this.dueDate != null) {
			dueDate = new LocalDate(this.dueDate);
		}
		return dueDate;
	}

	public void inactiavateCharge(final LocalDate inactivationOnDate) {
		this.inactivationDate = inactivationOnDate.toDate();
		this.active = false;
	}

	public void actiavateCharge() {
		this.inactivationDate = null;
		this.active = true;
	}
    
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    
    public void setInactivationDate(Date inactivationDate) {
        this.inactivationDate = inactivationDate;
    }


}