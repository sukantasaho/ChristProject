package com.christ.erp.services.dbobjects.employee;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.ErpEmployeeTitleDBO;

@Entity
@Table(name = "emp_position_role_sub")
public class EmpPositionRoleSubDBO implements Serializable {
	private static final long serialVersionUID = -3480077399544145903L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_position_role_sub_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="emp_position_role_id")
	public EmpPositionRoleDBO empPositionRoleId;	
	
	@Column(name="display_order")
	public Integer displayOrder;
	
	@ManyToOne
	@JoinColumn(name = "emp_title_id")
	public ErpEmployeeTitleDBO empTitleId;
	
//	@Column(name="emp_id")
//	public Integer empId ;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	 @Column(name = "created_users_by")
     public Integer createdUsersId;

	 @Column(name = "modified_users_id")
	 public Integer modifiedUsersId;
	
	 @Column(name = "record_status")
	 public char recordStatus;
}
