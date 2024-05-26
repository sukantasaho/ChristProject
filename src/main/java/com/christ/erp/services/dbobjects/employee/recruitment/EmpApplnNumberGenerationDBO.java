package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_appln_number_generation")
@Getter
@Setter
public class EmpApplnNumberGenerationDBO implements Serializable{

	private static final long serialVersionUID = 8397140155789086044L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_number_generation_id")
    public int empApplnNumberGenerationId;
	
	@Column(name="appln_number_from")
    public Integer applnNumberFrom;
	
	@Column(name="appln_number_to")
	public Integer applnNumberTo;
	
	@Column(name="current_appln_no")
	public Integer currentApplnNo;
	
	@Column(name="calendar_year")
    public Integer calendarYear;
	
	@Column(name="is_current_range")
    public Boolean isCurrentRange;
	
	@Column(name="created_users_id", updatable=false)
	public Integer createdUsersId;

    @Column(name="modified_users_id")
	public Integer modifiedUsersId;

    @Column(name="record_status")
	public char recordStatus;
}
