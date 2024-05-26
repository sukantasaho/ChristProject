package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;

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
@Table(name="emp_appointment_letter")
public class EmpAppointmentLetterDBO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7567834570124282660L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appointment_letter_id")
    public Integer empAppointmentLetterId;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
//	@ManyToOne
//	@JoinColumn(name="emp_appointment_letter_template_id")
//	public EmpAppointmentLetterTemplateDBO empAppointmentLetterTemplateDBO; 
	
	@Column(name="generated_appointment_letter_url")
	public String generatedAppointmentLetterUrl;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

}
