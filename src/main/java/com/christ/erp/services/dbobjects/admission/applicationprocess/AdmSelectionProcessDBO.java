package com.christ.erp.services.dbobjects.admission.applicationprocess;

import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessCenterDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="adm_selection_process")
public class AdmSelectionProcessDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_selection_process_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erp_academic_year_id")
    private ErpAcademicYearDBO erpAcademicYearDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_appln_entries_id")
    private StudentApplnEntriesDBO studentApplnEntriesDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adm_selection_process_plan_detail_id")
    private AdmSelectionProcessPlanDetailDBO admSelectionProcessPlanDetailDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adm_selection_process_plan_detail_allotment_id")
    private AdmSelectionProcessPlanDetailAllotmentDBO admSelectionProcessPlanDetailAllotmentDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adm_selection_process_center_details_id")
    private AdmSelectionProcessCenterDetailsDBO admSelectionProcessCenterDetailsDBO;

    @Column(name="sp_admit_card_url")
    private String spAdmitCardUrl;

    @Column(name="is_admit_card_nill")
    private Boolean isAdmitCardNill;

    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private Character recordStatus;

    @OneToMany(mappedBy = "admSelectionProcessDBO", cascade = CascadeType.ALL)
    public Set<AdmSelectionProcessScoreDBO> admSelectionProcessScoreDBOS = new HashSet<>();
}
