package com.christ.erp.services.dbobjects.support.settings;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter
@Table(name = "support_category")
public class SupportCategoryDBO implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "support_category_id")
	private int id;
	
	@Column(name = "support_category_name")
	private String supportCategoryName;
	
	@ManyToOne
	@JoinColumn(name = "support_category_group_id")
	private SupportCategoryGroupDBO supportCategoryGroupDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_department_id")
	private ErpDepartmentDBO erpDepartmentDBO;
	
	@Column(name ="is_upload_required")
	private Boolean isUploadRequired;
	
	@Column(name = "notification_email_required")
	private Boolean notificationEmailRequired;
	
	@Column(name = "notification_sms_required")
	private Boolean notificationSmsRequired;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supportCategoryDBO", cascade = CascadeType.ALL)
    private Set<SupportCategoryCampusDBO> supportCategoryCampusDBOSet;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supportCategoryDBO", cascade = CascadeType.ALL)
    private Set<SupportCategoryUserGroupDBO> supportCategoryUserGroupDBOSet; 
    
}
