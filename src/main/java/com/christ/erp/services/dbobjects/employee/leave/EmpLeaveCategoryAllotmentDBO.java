package com.christ.erp.services.dbobjects.employee.leave;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_leave_category_allotment")
@Setter
@Getter
public class EmpLeaveCategoryAllotmentDBO implements Serializable{

	private static final long serialVersionUID = 8208338453441931152L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_leave_category_allotment_id")
    public Integer empLeaveCategoryAllotmentId;
	
	@Column(name="emp_leave_category_allotment_name")
    public String empLeaveCategoryAllotmentName;
	
	@Column(name="leave_initialize_month")
    public Integer leaveIinitializeMonth;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(mappedBy ="empLeaveCategoryAllotmentDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<EmpLeaveCategoryAllotmentDetailsDBO> empLeaveCategoryAllotmentDetailsDBO;
}
