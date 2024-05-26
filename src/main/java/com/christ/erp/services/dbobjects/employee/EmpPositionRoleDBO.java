package com.christ.erp.services.dbobjects.employee;
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
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;

@Entity
@Table(name = "emp_position_role")
public class EmpPositionRoleDBO implements Serializable {
	private static final long serialVersionUID = -7464583519812637478L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_position_role_id")
	public Integer id;	

	@ManyToOne
	@JoinColumn(name = "erp_campus_id")
	public ErpCampusDBO erpCampusId;
	
	@Column(name="process_type")
	public String processType;
	
	@Column(name = "created_users_id")
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "empPositionRoleId",cascade=CascadeType.ALL)
	public Set<EmpPositionRoleSubDBO> empPositionSubAssignmentDBOSet = new HashSet<>();
}
