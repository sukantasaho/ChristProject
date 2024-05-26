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
@Table(name = "sys_function")
@Setter
@Getter
public class SysFunctionDBO implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sys_function_id")
    public int id;
	
	@Column(name="function_name")
    public String functionName;
	
	@ManyToOne
	@JoinColumn(name="sys_menu_id")
    public SysMenuDBO sysMenuDBO;
	
	@Column(name="function_description")
    public String functionDescription;
	
//	@Column(name="function_component_name")
//    public String functionComponentName;
	
	@Column(name="access_token")
    public String accessToken;
	
//	@Column(name="is_display")
//    public Boolean isDisplay;
	
	@Column(name="display_message")
    public String displayMessage;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(mappedBy = "sysFunctionDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SysRoleFunctionMapDBO> SysRoleFunctionMapDBOSet;
    
    @OneToMany(mappedBy = "sysFunctionDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SysUserFunctionOverrideDBO> SysUserFunctionOverrideDBOSet;
}

