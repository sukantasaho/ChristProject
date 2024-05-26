package com.christ.erp.services.dbobjects.common;

import java.time.LocalDate;
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

@Entity
@Table(name = "erp_committee")
@Getter@Setter
public class ErpCommitteeDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_committee_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
    private ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_department_id")
    private ErpDepartmentDBO erpDepartmentDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_committee_type_id")
    private ErpCommitteeTypeDBO erpCommitteeTypeDBO;
	
    @Column(name = "programme_course_structure_entry_last_date")
    private LocalDate programmeCourseStructureEntryLastDate;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
    
    @OneToMany(mappedBy = "erpCommitteeDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ErpCommitteeMembersDBO> erpCommitteeMembersDBOSet;
    
    @OneToMany(mappedBy = "erpCommitteeDBO",fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private Set<ErpCommitteeProgrammeDBO> erpCommitteeProgrammeDBOSet;
    
    @OneToMany(mappedBy = "erpCommitteeDBO",fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private Set<ErpCommitteeProgrammeCourseReviewDBO> erpCommitteeProgrammeCourseReviewDBOSet;
    
    @OneToMany(mappedBy = "erpCommitteeDBO",fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private Set<ErpCommitteeCampusDBO> erpCommitteeCampusDBOSet;
    

}
