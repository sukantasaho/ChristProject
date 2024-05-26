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

import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.ErpEmployeeTitleDBO;



@Entity
@Table(name="emp_appln_dignitaries_feedback")
public class EmpApplnDignitariesFeedbackDBO implements Serializable {
	private static final long serialVersionUID = -8522954910017760202L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_appln_dignitaries_feedback_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empId;
	
	@ManyToOne
	@JoinColumn(name = "emp_title_id")
	public ErpEmployeeTitleDBO empTitleId;
	
	@Column(name="dignitaries_feedback")
	public String dignitariesFeedback;
	
	 @Column(name = "created_users_id")
     public Integer createdUsersId;

	 @Column(name = "modified_users_id")
	 public Integer modifiedUsersId;
	
	 @Column(name = "record_status")
	 public char recordStatus;
	
	
}
