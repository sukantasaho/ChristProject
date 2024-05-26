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
@Table(name = "exam_assessment_ratio")
@Getter
@Setter
public class ExamAssessmentRatioDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exam_assessment_ratio_id")
	private int id;
	
	@Column(name = "assessment_ratio")
    private String assessmentRatio;
	
	@Column(name = "cia_percentage")
    private Integer ciaPercentage;
	
	@Column(name = "ese_percentage")
    private Integer esePercentage;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;

}
