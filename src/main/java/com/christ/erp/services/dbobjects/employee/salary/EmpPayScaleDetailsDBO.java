package com.christ.erp.services.dbobjects.employee.salary;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("serial")
@Entity
@Table(name = "emp_pay_scale_details")
public class EmpPayScaleDetailsDBO implements Serializable {
	private static final long serialVersionUID = 5975033411785554272L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_pay_scale_details_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	public EmpDBO empDBO;
	
	@Column(name="pay_scale_effective_date")
	public LocalDate payScaleEffectiveDate;
	
	@Column(name="pay_scale_comments")
	public String payScaleComments;
	
	@ManyToOne
	@JoinColumn(name="erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;
	
	@Column(name="pay_scale_type")
	public String payScaleType;
	
	@ManyToOne
	@JoinColumn(name = "emp_pay_scale_matrix_detail_id")
	public EmpPayScaleMatrixDetailDBO empPayScaleMatrixDetailDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_daily_wage_slab_id")
	public EmpDailyWageSlabDBO empDailyWageSlabDBO;
	
	@Column(name = "wage_rate_per_type")
	public BigDecimal wageRatePerType;
	
	@Column(name = "gross_pay")
	public BigDecimal grossPay;

	@Column(name = "previous_year_gross_pay")
	public BigDecimal previousGrossPay;
	
	@Column(name = "is_published")
	public Boolean isPublished;
	
	@Column(name = "status_id")
	public Integer status_id;

	@Column(name="created_users_id")
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;;
	
	@Column(name="record_status")
	public Character recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empPayScaleDetailsDBO",cascade=CascadeType.ALL )
	public Set<EmpPayScaleDetailsComponentsDBO>  empPayScaleDetailsComponentsDBOs= new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empPayScaleDetailsDBO",cascade=CascadeType.ALL )
	public Set<EmpPayScaleDetailsCommentsDBO>  empPayScaleDetailsCommentsDBOS= new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empPayScaleDetailsDBO",cascade=CascadeType.ALL )
	public Set<EmpPayScaleDetailsReviewerAndApproverDBO>  empPayScaleDetailsReviewerAndApproverDBOS= new HashSet<>();

	@Column(name = "is_current")
	public boolean current;
}