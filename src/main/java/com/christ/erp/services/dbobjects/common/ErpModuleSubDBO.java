//package com.christ.erp.services.dbobjects.common;
//import java.io.Serializable;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//@Entity
//@Table(name="erp_module_sub")
//public class ErpModuleSubDBO  implements Serializable {
//
//	private static final long serialVersionUID = -8083968689181090790L;
//	
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name="erp_module_sub_id")
//    public int id;
//	
//	@Column(name="sub_module_name")
//    public String subModuleName;
//	
//	@Column(name="sub_module_display_order")
//    public Integer subModuleDisplayOrder;
//	
//	@Column(name="erp_module_id")
//    public Integer erpModuleId;
//	
//	@Column(name="is_displayed")
//	public Boolean isDisplayed;
//	
//	@Column(name="erp_type ")
//	public String erpType;
//	
//	@Column(name="created_users_id", updatable=false)
//    public Integer createdUsersId;
//
//    @Column(name="modified_users_id")
//    public Integer modifiedUsersId;
//
//    @Column(name="record_status")
//    public char recordStatus;
//	
//}
