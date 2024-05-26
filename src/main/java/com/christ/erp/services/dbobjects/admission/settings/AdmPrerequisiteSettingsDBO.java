package com.christ.erp.services.dbobjects.admission.settings;

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "adm_prerequisite_settings")
@Getter
@Setter
public class AdmPrerequisiteSettingsDBO  {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_prerequisite_settings_id")
    private int id;

    @ManyToOne
    @JoinColumn(name="erp_academic_year_id")
    private ErpAcademicYearDBO erpAcademicYearDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_programme_id")
    private ErpProgrammeDBO erpProgrammeDBO;
    
    @Column(name = "is_exam_mandatory")
    private Boolean isExamMandatory;

	@Column(name = "record_status")
	private char recordStatus;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

    @ManyToOne
    @JoinColumn(name="adm_admission_type_id")
    private AdmAdmissionTypeDBO admAdmissionTypeDBO;

    @Column(name = "is_score_details_mandatory")
    private Boolean isScoreDetailsMandatory;

    @Column(name = "is_document_upload_mandatory")
    private Boolean isDocumentUploadMandatory;

    @Column(name = "is_register_no_mandatory")
    private Boolean isRegisterNoMandatory;

    @OneToMany(fetch =FetchType.LAZY,mappedBy = "admPrerequisiteSettingsDBO", cascade = CascadeType.ALL)
    private Set<AdmPrerequisiteSettingsDetailsDBO> admPrerequisiteSettingsDetailsDBOSet =  new HashSet<>();

    @ManyToOne
    @JoinColumn(name="erp_admission_year_id")
    private ErpAcademicYearDBO admBatchYear;
}
