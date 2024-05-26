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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "sys_menu_module")
@Setter
@Getter
public class SysMenuModuleDBO implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sys_menu_module_id")
    public int id;
	
	@Column(name="module_name")
    public String moduleName;
	
	@Column(name="module_display_order")
    public Integer moduleDisplayOrder;
	
	@Column(name="is_displayed")
    public Integer isDisplayed;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
     
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sysMenuModuleDBO", cascade = CascadeType.ALL)
   	public Set<SysMenuModuleSubDBO> sysMenuModuleSubDBOs = new HashSet<>();
    
 	@Column(name="icon_class_name")
 	private String iconClassName;
}
