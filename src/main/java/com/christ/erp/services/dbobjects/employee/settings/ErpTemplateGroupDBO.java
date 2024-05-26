package com.christ.erp.services.dbobjects.employee.settings;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name="erp_template_group")
public class ErpTemplateGroupDBO  {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_template_group_id")
    public int id;
	
	@Column (name="template_group_name")
	public String templateGroupName;
	
	@Column(name="template_group_code")
	public String templateGroupCode;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "erpTemplateGroupDBO",cascade=CascadeType.ALL)
	public Set<ErpTemplateDBO> erpTemplateDBOSet = new HashSet<>();
    
    @Column(name="template_purpose")
    public String templatePurpose;
}
