package com.christ.erp.services.dbobjects.student.common;

import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeQualificationSubjectEligibilityDBO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="student_educational_mark_details")
@Getter
@Setter

public class StudentEducationalMarkDetailsDBO implements Serializable {

    private static final long serialVersionUID = -6987737720829813848L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_educational_mark_details_id")
    private int id;

    @ManyToOne
    @JoinColumn(name="student_educational_details_id")
    private StudentEducationalDetailsDBO studentEducationalDetailsDBO;

    @ManyToOne
    @JoinColumn(name="adm_programme_qualification_subject_eligibility_id")
    public AdmProgrammeQualificationSubjectEligibilityDBO admProgrammeQualificationSubjectEligibilityDBO;

    @Column(name="semester_name")
    private String semesterName;

    @Column(name="is_result_declared")
    private Boolean isResultDeclared;

    @Column(name="marks_obtained")
    private BigDecimal marksObtained;

    @Column(name="maximum_marks")
    private BigDecimal maximumMarks;

    @Column(name="sgpa")
    private BigDecimal sgpa;

    @Column(name="cgpa")
    private BigDecimal cgpa;

    @Column(name="total_pending_backlogs")
    private Integer totalPendingBacklogs;

    @Column(name="display_order")
    private Integer displayOrder;

    @Column(name="created_users_id", updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}
