package com.christ.erp.services.dbobjects.employee.attendance;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

@Entity
@Table(name = "emp_roster_allotment")
public class EmpRosterAllotmentDBO implements Serializable {

	private static final long serialVersionUID = 8115884136278659687L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_roster_allotment_id")
    public Integer id;

	@Column(name="roster_date")
	public LocalDate rosterDate;
	
    @ManyToOne
    @JoinColumn(name = "emp_id")
    public EmpDBO empDBO;
    
    @ManyToOne
    @JoinColumn(name = "emp_shift_types_id")
    public EmpShiftTypesDBO empShiftTypeDBO;
    
    @Column(name = "created_users_id", updatable = false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
