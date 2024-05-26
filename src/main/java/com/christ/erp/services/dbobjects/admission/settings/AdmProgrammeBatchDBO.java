package com.christ.erp.services.dbobjects.admission.settings;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "adm_programme_batch")
public class AdmProgrammeBatchDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_programme_batch_id")
    public int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_programme_settings_id")
    public AdmProgrammeSettingsDBO admProgrammeSettingsDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_campus_programme_mapping_id")
    public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;

    @Column(name = "campus_location_display_name")
    public String campusLocationDisplayName;

    @Column(name = "is_specialisation_required")
    public Boolean isSpecialisationRequired;

    @Column(name = "created_user_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
