package com.christ.erp.services.dbobjects.curriculum.settings;

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
import com.christ.erp.services.dbobjects.common.AcaCourseTypeDBO;
import com.christ.erp.services.dbobjects.common.ExamAssessmentRatioDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exam_assessment_template")
@Getter
@Setter
public class ExamAssessmentTemplateDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exam_assessment_template_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "exam_assessment_ratio_id")
    private ExamAssessmentRatioDBO examAssessmentRatioDBO;
	
	@ManyToOne
	@JoinColumn(name = "aca_course_type_id")
    private AcaCourseTypeDBO acaCourseTypeDBO;
	
	@Column(name = "template_name")
    private String templateName;
	
	@Column(name = "cia_total_marks")
    private Integer ciaTotalMarks;
	
	@Column(name = "cia_scale_down_to")
    private Integer ciaScaleDownTo;
	
	@Column(name = "cia_min_marks")
    private Integer ciaMinMarks;
	
	@Column(name = "ese_total_marks")
    private Integer eseTotalMarks;
	
	@Column(name = "ese_scale_down_to")
    private Integer eseScaleDownTo;
	
	@Column(name = "ese_min_marks")
    private Integer eseMinMarks;
	
	@Column(name = "attendance_total")
    private Integer attendanceTotal;
	
	@Column(name = "total_scale_down_marks")
    private Integer totalScaleDownMarks;
	
	@Column(name = "total_min_marks")
    private Integer totalMinMarks;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
    
    @OneToMany(mappedBy = "examAssessmentTemplateDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ExamAssessmentTemplateAttendanceDBO> examAssessmentTemplateAttendanceDBOSet;
    
    @OneToMany(mappedBy = "examAssessmentTemplateDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ExamAssessmentTemplateDetailsDBO> ExamAssessmentTemplateDetailsDBOSet;
    
}
