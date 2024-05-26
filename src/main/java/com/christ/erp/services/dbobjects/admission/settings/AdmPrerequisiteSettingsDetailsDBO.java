package com.christ.erp.services.dbobjects.admission.settings;

import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "adm_prerequisite_settings_details")
public class AdmPrerequisiteSettingsDetailsDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adm_prerequisite_settings_details_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "erp_location_id")
    private ErpLocationDBO erpLocationDBO;

    @ManyToOne
    @JoinColumn(name = "adm_prerequisite_exam_id")
    private AdmPrerequisiteExamDBO admPrerequisiteExamDBO;

    @ManyToOne
    @JoinColumn(name = "adm_prerequisite_settings_id")
    private AdmPrerequisiteSettingsDBO admPrerequisiteSettingsDBO;

    @Column(name = "min_marks")
    private Integer minMarks;

    @Column(name = "min_marks_for_christite")
    private Integer minMarksForChristite;

    @Column(name = "total_marks")
    private Integer totalMarks;

    @Column(name = "created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;

    @OneToMany(fetch =FetchType.LAZY,mappedBy = "admPrerequisiteSettingsDetailsDBO", cascade = CascadeType.ALL)
    private Set<AdmPrerequisiteSettingsDetailsPeriodDBO> admPrerequisiteSettingsDetailsPeriodDBOSet =  new HashSet<>();
}
