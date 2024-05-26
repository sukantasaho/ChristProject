package com.christ.erp.services.dbobjects.common;

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
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "sys_menu")
@Setter
@Getter
public class SysMenuDBO implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sys_menu_id")
    public int id;
	
	@Column(name="menu_screen_name")
    public String menuScreenName;
	
	@Column(name="menu_component_path")
    public String menuComponentPath;
	
	@Column(name="menu_screen_display_order")
    public Integer menuScreenDisplayOrder;
    
	@ManyToOne
    @JoinColumn(name="sys_menu_module_sub_id")
    public SysMenuModuleSubDBO sysMenuModuleSubDBO;
	
	@ManyToOne
    @JoinColumn(name="erp_screen_config_mast_id")
    public ErpScreenConfigMastDBO erpScreenConfigMastDBO;
	
	@Column(name="is_menu_link")
    public Boolean isMenuLink;
	
	@Column(name="is_displayed")
    public Boolean isDisplayed;
	
	@Column(name="is_report_menu")
    public Boolean isReportMenu;
	
	@Column(name="is_user_specific_report")
    public Boolean isUserSpecificReport;
	 
	@Column(name="is_create_new_tab")
	public Boolean isCreateNewTab;
	
	@Column(name="is_otp_required")
    public Boolean isOtpRequired;
	 
	@Column(name="is_otp_required_on_every_instance")
	public Boolean isOtpRequiredOnEveryInstance;
	
	@Column(name="is_location_specific")
	public Boolean isLocationSpecific;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
	
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sysMenuDBO", cascade = CascadeType.ALL)
   	public Set<SysFunctionDBO> sysFunctionDBOSet;
}
