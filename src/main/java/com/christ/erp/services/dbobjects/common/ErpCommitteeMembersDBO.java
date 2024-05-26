package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.curriculum.settings.ExternalsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_committee_members")
@Getter
@Setter
public class ErpCommitteeMembersDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_committee_members_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_committee_id")
    private ErpCommitteeDBO erpCommitteeDBO;
	

	@ManyToOne
	@JoinColumn(name = "emp_id")
    private EmpDBO empDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_committee_role_id")
    private ErpCommitteeRoleDBO erpCommitteeRoleDBO;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
    private StudentDBO studentDBO;
	
	@ManyToOne
	@JoinColumn(name = "batch_year_id")
    private ErpAcademicYearDBO batchYearId;
	
	@ManyToOne
    @JoinColumn(name = "externals_id")
    private ExternalsDBO externalsDBO ;
	
    @Column(name = "created_users_id")
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;

}
