package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_users")
@Getter
@Setter
public class ErpUsersDBO implements Serializable{

	private static final long serialVersionUID = 144835130806685114L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_users_id")
    public Integer id;

	@Column(name="erp_users_name")
    public String userName;
	
	@Column(name="passwd")
    public String loginPassword;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_department_mapping_id")
	public ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO;
	
	@Transient
	public ErpDepartmentDBO erpDepartmentDBO;
	
	@Column(name="user_name")
    public String loginId;
	
	@Column(name="user_valid_upto")
    public LocalDateTime userValidUpto;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "erpUsersDBO", cascade = CascadeType.ALL)
   	public List<SysUserRoleMapDBO> sysUserRoleMapDBOs = new ArrayList<SysUserRoleMapDBO>();
    
    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "erpUsersDBO", cascade = CascadeType.ALL)
   	public Set<SysUserFunctionOverrideDBO> sysUserFunctionOverrideDBOSet;
    
    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "erpUsersDBO", cascade = CascadeType.ALL)
   	private Set<ErpUsersCampusDBO> erpUsersCampusDBOSet;

}
