package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import java.util.HashSet;
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
@Table(name = "sys_menu_module_sub")
@Setter
@Getter
public class SysMenuModuleSubDBO implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sys_menu_module_sub_id")
    public int id;
	
	@Column(name="sub_module_name")
    public String subModuleName;
	
	@Column(name="sub_module_display_order")
    public Integer menuScreenDisplayOrder;
	
	@ManyToOne
	@JoinColumn(name="sys_menu_module_id")
    public SysMenuModuleDBO sysMenuModuleDBO;
	
	@Column(name="is_displayed")
    public Boolean isDisplayed;
	
// 	@Column(name="icon_class_name")
//  public String iconClassName;
	
	@Column(name="erp_type")
    public String erpType;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sysMenuModuleSubDBO", cascade = CascadeType.ALL)
   	public Set<SysMenuDBO> sysMenuDBOs = new HashSet<>();
	
}
