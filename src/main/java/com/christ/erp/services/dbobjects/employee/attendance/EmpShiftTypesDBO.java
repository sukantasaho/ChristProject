package com.christ.erp.services.dbobjects.employee.attendance;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusDBO;

@Entity
@Table(name = "emp_shift_types")
public class EmpShiftTypesDBO implements Serializable {

	private static final long serialVersionUID = -8695222273442428536L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_shift_types_id")
    public Integer id;

	@Column(name = "shift_name")
	public String shiftName;
	
	@Column(name = "shift_short_name")
	public String shiftShortName;
	
	@Column(name = "is_weekly_off")
	public Boolean isWeeklyOff;
	
	@ManyToOne
    @JoinColumn(name = "erp_campus_id")
    public ErpCampusDBO erpCampusDBO;
	
	@ManyToOne
    @JoinColumn(name = "emp_time_zone_id")
    public EmpTimeZoneDBO empTimeZoneDBO;
	
    @Column(name = "created_users_id", updatable = false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}