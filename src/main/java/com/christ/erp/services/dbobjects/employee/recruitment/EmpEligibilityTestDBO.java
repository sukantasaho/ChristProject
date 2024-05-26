package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
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
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_eligibility_test")
@Setter
@Getter
public class EmpEligibilityTestDBO implements Serializable{

	private static final long serialVersionUID = 7487771773943945385L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_eligibility_test_id")
    public int empEligibilityTestId;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_eligibility_exam_list_id")
    public EmpEligibilityExamListDBO empEligibilityExamListDBO;
	
	@OneToMany(mappedBy = "empEligibilityTestDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<EmpEligibilityTestDocumentDBO> empEligibilityTestDocumentDBOSet = new HashSet<>();
	
	@Column(name="test_year")
    public Integer testYear;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}