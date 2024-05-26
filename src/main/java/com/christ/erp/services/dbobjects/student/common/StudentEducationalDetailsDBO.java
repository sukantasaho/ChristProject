package com.christ.erp.services.dbobjects.student.common;

import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpInstitutionDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;
import com.christ.erp.services.dbobjects.common.ErpUniversityBoardDBO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="student_educational_details")

@Getter
@Setter
public class StudentEducationalDetailsDBO implements Serializable {

    private static final long serialVersionUID = 6635178211096277034L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_educational_details_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_appln_entries_id")
    private StudentApplnEntriesDBO studentApplnEntriesDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id")
    private StudentDBO studentDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_qualification_list_id")
    private AdmQualificationListDBO admQualificationListDBO;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_qualification_degree_list_id")
    private AdmQualificationDegreeListDBO admQualificationDegreeListDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_university_board_id")
    private ErpUniversityBoardDBO erpUniversityBoardDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_institution_id")
    private ErpInstitutionDBO erpInstitutionDBO;

    @Column(name="institution_others")
    private String institutionOthers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="institution_country_id")
    private ErpCountryDBO institutionCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="institution_state_id")
    private ErpStateDBO institutionState;

    @Column(name="institution_others_state")
    private String institutionOthersState;

    @Column(name="is_result_declared")
    private Boolean isResultDeclared;

    @Column(name = "no_of_pending_backlogs")
    private Integer noOfPendingBacklogs;

    @Column(name = "year_of_passing")
    private Integer yearOfPassing;

    @Column(name="month_of_passing")
    private Integer monthOfPassing;

    @Column(name="exam_register_no")
    private String examRegisterNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="aca_session_type_id")
    private AcaSessionTypeDBO acaSessionTypeId;

    @Column(name="total_semesters")
    private Integer totalSemesters;

    @Column(name="consolidated_marks_obtained")
    private BigDecimal consolidatedMarksObtained;

    @Column(name="consolidated_maximum_marks")
    private BigDecimal consolidatedMaximumMarks;
   
    @Column(name="is_level_completed")
    private Boolean isLevelCompleted;
    
    @Column(name="percentage")
    private BigDecimal percentage;
  
    @Column(name="created_users_id", updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
 
    @OneToMany(mappedBy = "studentEducationalDetailsDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<StudentEducationalMarkDetailsDBO> studentEducationalMarkDetailsDBOSet;

    @OneToMany(mappedBy = "studentEducationalDetailsDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<StudentEducationalDetailsDocumentsDBO> studentEducationalDetailsDocumentsDBOSet = new HashSet<>();
}
