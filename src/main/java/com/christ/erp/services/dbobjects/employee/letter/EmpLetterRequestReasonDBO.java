package com.christ.erp.services.dbobjects.employee.letter;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="emp_letter_request_reason")
public class EmpLetterRequestReasonDBO implements Serializable {

	private static final long serialVersionUID = -5252952225659074117L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_letter_request_reason_id")
	public Integer id;

	@Column (name="letter_request_reason_name")
	public String letterRequestReasonName;

	@Column(name="created_users_id", updatable=false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;

	@Column(name="record_status")
	public char recordStatus;

}
