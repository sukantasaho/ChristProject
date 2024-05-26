package com.christ.erp.services.dbobjects.curriculum.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.AttTypeDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exam_assessment_template_attendance")
@Getter
@Setter
public class ExamAssessmentTemplateAttendanceDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exam_assessment_template_attendance_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "exam_assessment_template_id")
    private ExamAssessmentTemplateDBO examAssessmentTemplateDBO;
	
	@ManyToOne
	@JoinColumn(name = "att_type_id")
    private AttTypeDBO attTypeDBO;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
}
