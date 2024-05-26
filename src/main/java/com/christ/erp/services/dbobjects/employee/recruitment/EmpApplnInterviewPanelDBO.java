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
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "emp_appln_interview_panel")
public class EmpApplnInterviewPanelDBO implements Serializable {
	
	private static final long serialVersionUID = 7997747508470249186L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_appln_interview_panel_id")
	public Integer id;		
	
	@ManyToOne
	@JoinColumn(name="emp_appln_interview_schedules_id")
	public EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO;
	
	@ManyToOne
    @JoinColumn(name="erp_users_id")
    public ErpUsersDBO erpUsersDBO;
	
	@Column(name="is_internal_panel")
	public boolean isInternalPanel;
	
	@ManyToOne
	@JoinColumn(name="emp_interview_university_externals_id")
	public EmpInterviewUniversityExternalsDBO empInterviewUniversityExternalsDBO;	
	
	@Column(name="created_users_id",updatable=false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus; 		
}
