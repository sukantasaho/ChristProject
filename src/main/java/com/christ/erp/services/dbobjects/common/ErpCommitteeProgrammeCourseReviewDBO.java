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
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_committee_programme_course_review")
@Getter
@Setter
public class ErpCommitteeProgrammeCourseReviewDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_committee_programme_course_review_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_committee_id")
    private ErpCommitteeDBO erpCommitteeDBO;
	
	@ManyToOne
	@JoinColumn(name = "course_structure_reviewer1_id")
    private EmpDBO courseStructureReviewer1Id;
	
	@ManyToOne
	@JoinColumn(name = "course_structure_reviewer2_id")
    private EmpDBO courseStructureReviewer2Id;
	
	@Column(name = "course_structure_review_last_date")
    private LocalDate  courseStructureReviewLastDate;
	
    @Column(name = "created_users_id")
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
    
    @OneToMany(mappedBy = "erpCommitteeProgrammeCourseReviewDBO",fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private Set<ErpCommitteeProgrammeCourseReviewDetailsDBO> erpCommitteeProgrammeCourseReviewDetailsDBOSet;

}
