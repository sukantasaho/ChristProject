package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aca_course_type")
@Getter
@Setter
public class AcaCourseTypeDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_course_type_id")
	private int id;
	
	@Column(name = "course_type")
    private String courseType;
	
	@Column(name = "course_type_combination")
    private String courseTypeCombination;
	
	@Column(name = "is_for_assessment_display")
    private Boolean isForAssessmentDisplay;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;

}
