package com.christ.erp.services.dbobjects.student.recruitment;

import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpSpecializationDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="student_appln_preference")
@Data
public class StudentApplnPreferenceDBO implements Serializable {

    private static final long serialVersionUID = -3442769631636951311L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_appln_preference_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_appln_entries_id")
    private StudentApplnEntriesDBO studentApplnEntriesDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_programme_batch_id")
    public AdmProgrammeBatchDBO admProgrammeBatchDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_campus_programme_mapping_id")
    private ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;

    @ManyToOne
    @JoinColumn(name="erp_specialization_id")
    private ErpSpecializationDBO erpSpecializationDBO;

    @Column(name="preference_order")
    private Integer preferenceOrder;

    @Column(name="created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}
