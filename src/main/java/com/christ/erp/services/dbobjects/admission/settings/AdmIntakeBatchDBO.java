package com.christ.erp.services.dbobjects.admission.settings;

import com.christ.erp.services.dbobjects.common.AcaSessionGroupDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "adm_intake_batch")
public class AdmIntakeBatchDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_intake_batch_id")
    private int id;

    @Column(name = "adm_intake_batch_name")
    private String admIntakeBatchName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aca_session_group_id")
    private AcaSessionGroupDBO acaSessionGroupDBO;

    @Column(name = "created_user_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}
