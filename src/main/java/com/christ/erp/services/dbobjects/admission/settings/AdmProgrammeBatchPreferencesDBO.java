package com.christ.erp.services.dbobjects.admission.settings;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "adm_programme_batch_preferences")
public class AdmProgrammeBatchPreferencesDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_programme_batch_preferences_id")
    public int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_programme_settings_id")
    public AdmProgrammeSettingsDBO admProgrammeSettingsDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_programme_batch_id")
    public AdmProgrammeBatchDBO admProgrammeBatchDBO;

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
