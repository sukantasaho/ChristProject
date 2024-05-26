package com.christ.erp.services.dbobjects.student.recruitment;


import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanCenterBasedDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="student_appln_selection_process_dates")
@Data
public class StudentApplnSelectionProcessDatesDBO implements Serializable {

    private static final long serialVersionUID = 8946981880946358347L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_appln_selection_process_dates_id")
    private int id;

//    @Column(name = "selection_process_date")
//    private LocalDateTime selectionProcessDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_appln_entries_id")
    private StudentApplnEntriesDBO studentApplnEntriesDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_selection_process_plan_detail_id")
    private AdmSelectionProcessPlanDetailDBO admSelectionProcessPlanDetailDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_selection_process_plan_center_based_id")
    private AdmSelectionProcessPlanCenterBasedDBO admSelectionProcessPlanCenterBasedDBO;

    /*@ManyToOne
    @JoinColumn(name="adm_selection_process_center_details_id")
    public Integer adm_selection_process_center_details_id;

    @ManyToOne
    @JoinColumn(name="adm_selection_process_plan_detail_allotment_id")
    public Integer adm_selection_process_plan_detail_allotment_id;*/

    @Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}
