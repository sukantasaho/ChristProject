package com.christ.erp.services.dbobjects.student.recruitment;

import com.christ.erp.services.dbobjects.admission.settings.AdmPrerequisiteSettingsDetailsPeriodDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="student_appln_prerequisite")
@Getter
@Setter
public class StudentApplnPrerequisiteDBO implements Serializable {

    private static final long serialVersionUID = 2676832896566542004L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_appln_prerequisite_id")
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="student_appln_entries_id")
    private StudentApplnEntriesDBO studentApplnEntriesDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_prerequisite_settings_details_period_id")
    private AdmPrerequisiteSettingsDetailsPeriodDBO admPrerequisiteSettingsDetailsPeriodDBO;

    @Column(name="marks_obtained")
    private BigDecimal marksObtained;

    @Column(name="exam_roll_no")
    private String examRollNo;

    @Column(name="created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}
