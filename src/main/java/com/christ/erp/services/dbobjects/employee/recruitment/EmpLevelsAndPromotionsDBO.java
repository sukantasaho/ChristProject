package com.christ.erp.services.dbobjects.employee.recruitment;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDesignationDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleLevelDBO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
@Table(name = "emp_levels_and_promotions")
public class EmpLevelsAndPromotionsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_levels_and_promotions_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	private EmpDBO empDBO;
	
	@Column(name = "applied_date_of_promotion")
	private LocalDate appliedDateOfPromotion;
	
	@Column(name = "effective_date_of_promotion")
	private LocalDate effectiveDateOfPromotion;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "is_promotion_approved")
	private boolean promotionApproved;
	
	@ManyToOne
	@JoinColumn(name = "emp_pay_scale_level_id")
	private EmpPayScaleLevelDBO empPayScaleLevelDBO;
	
	@Column(name = "cell")
	private Integer cell;
	
	@ManyToOne
	@JoinColumn(name = "emp_designation_id")
	private EmpDesignationDBO empDesignationDBO;

	@ManyToOne
	@JoinColumn(name = "emp_employee_letter_details_id")
	private EmpEmployeeLetterDetailsDBO empEmployeeLetterDetailsDBO;
	
	@Column(name = "record_status")
	private char recordStatus;

}