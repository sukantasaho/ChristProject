package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_committee_programme_course_review_details")
@Getter
@Setter
public class ErpCommitteeProgrammeCourseReviewDetailsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_committee_programme_course_review_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_committee_programme_course_review_id")
    private ErpCommitteeProgrammeCourseReviewDBO erpCommitteeProgrammeCourseReviewDBO;

	@ManyToOne
	@JoinColumn(name = "erp_programme_department_mapping_id")
    private ErpProgrammeDepartmentMappingDBO erpProgrammeDepartmentMappingDBO;
	
    @Column(name = "created_users_id")
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;

}
