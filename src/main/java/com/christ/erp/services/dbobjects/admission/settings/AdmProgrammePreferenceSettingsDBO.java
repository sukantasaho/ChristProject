package com.christ.erp.services.dbobjects.admission.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

@SuppressWarnings("serial")
@Entity
@Table(name = "adm_programme_preference_settings")
public class AdmProgrammePreferenceSettingsDBO implements Serializable{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_programme_preference_settings_id")
    public int id;
    
    @ManyToOne
    @JoinColumn(name="erp_campus_programme_mapping_id")
    public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
    
    @ManyToOne
    @JoinColumn(name="adm_programme_settings_id")
    public AdmProgrammeSettingsDBO admProgrammeSettingsDBO;
    
    @Column(name = "is_specialisation_required")
    public boolean isSpecialisationRequired;
    
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
    
}
