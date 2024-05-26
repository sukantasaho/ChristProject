package com.christ.erp.services.dbobjects.employee.settings;
import java.util.List;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.SysMenuModuleDBO;
import com.christ.erp.services.dbobjects.common.SysMenuModuleSubDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDetailDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionGeneralDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="erp_template")
@Getter
@Setter
public class ErpTemplateDBO  {

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_template_id")
    public Integer id;
	
//	@ManyToOne
//	@JoinColumn(name="erp_module_sub_id")
//	public ErpModuleSubDBO erpModuleSubDBO;
	
	@ManyToOne
	@JoinColumn(name="sys_menu_module_sub_id")
	public SysMenuModuleSubDBO sysMenuModuleSubDBO;
	
	@Column (name="template_code")
	public String templateCode;
	
	@Column(name="template_name")
	public String templateName;
	
	@Column (name="template_description")
	public String templateDescription;
	
	@Column (name="template_type")
	public String templateType;
	
	@Column (name="template_content", columnDefinition = "mediumtext")
	public String templateContent;

	@Column (name="template_id")
	public String templateId;
	
	@Column (name="mail_subject")
	public String mailSubject;
	
	@Column (name="mail_from_name")
	public String mailFromName;
	
	@Column (name="available_tags", columnDefinition = "mediumtext")
	public String availableTags;
	
	@ManyToOne
	@JoinColumn(name="emp_employee_category_id ")
	public EmpEmployeeCategoryDBO empEmployeeCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_template_group_id ")
	public ErpTemplateGroupDBO erpTemplateGroupDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_programme_mapping_id ")
	public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

}
