package com.christ.erp.services.dbobjects.employee.letter;
import java.io.Serializable;
import java.sql.Time;
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
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

@Entity
@Table(name="emp_letter_request")
public class EmpLetterRequestDBO implements Serializable {

	private static final long serialVersionUID = -5252952225659074117L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_letter_request_id")
	public int id;

	@ManyToOne
	@JoinColumn(name="emp_id ")
	public EmpDBO empDBO;

	@ManyToOne
	@JoinColumn(name="emp_letter_request_type_id ")
	public EmpLetterRequestTypeDBO empLetterRequestTypeDBO;

	@ManyToOne
	@JoinColumn(name="emp_letter_request_reason_id ")
	public EmpLetterRequestReasonDBO empLetterRequestReasonDBO;

	@Column (name="letter_request_details")
	public String letterRequestDetails;

	@Column(name="letter_request_applied_date")
	public LocalDate letterRequestAppliedDate;

	@Column(name="letter_issued_date")
	public LocalDate letterIssuedDate;

	@Column (name="letter_request_po_comment")
	public String letterRequestPoComment;

	@Column (name="letter_request_url")
	public String letterRequestUrl;

	@ManyToOne
	@JoinColumn(name="erp_applicant_work_flow_process_id ")
	public ErpWorkFlowProcessDBO erpApplicantWorkFlowProcessDBO;

	@Column(name = "applicant_status_log_time")
	public LocalDate applicantStatusLogTime;

	@ManyToOne
	@JoinColumn(name="erp_application_work_flow_process_id ")
	public ErpWorkFlowProcessDBO erpApplicationWorkFlowProcessDBO;

	@Column(name = "application_status_log_time")
	public LocalDate applicationStatusLogTime;

	@Column(name="created_users_id", updatable=false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;

	@Column(name="record_status")
	public char recordStatus;
}
