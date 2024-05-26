package com.christ.erp.services.dbobjects.employee.letter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

@Entity
@Table(name="emp_letter_request_type")
public class EmpLetterRequestTypeDBO implements Serializable {

	private static final long serialVersionUID = 1293784316749931229L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_letter_request_type_id")
    public Integer id;
	
	@ManyToOne
	@JoinColumn(name="erp_template_id")
	public ErpTemplateDBO erptemplateId;
	
	@Column (name="letter_type_name")
	public String letterTypeName;
	
	@Column(name="letter_type_prefix")
	public String letterTypePrefix;
	
	@Column (name="letter_type_start_no")
	public String letterTypeStartNo;
	
	@Column (name="letter_type_current_no")
	public String LetterTypeCurrentNo;
	
	@Column (name="letter_help_text")
	public String letterHelpText;
	
	@Column(name="is_available_online")
	public Boolean isAvailableOnline;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
	
	

}
