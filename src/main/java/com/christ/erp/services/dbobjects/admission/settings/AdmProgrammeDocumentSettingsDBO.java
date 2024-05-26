package com.christ.erp.services.dbobjects.admission.settings;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@SuppressWarnings("serial")
@Entity
@Table(name = "adm_programme_document_settings")
@Setter
@Getter
public class AdmProgrammeDocumentSettingsDBO implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_programme_document_settings_id")
    public int id;
    
    @ManyToOne
    @JoinColumn(name="adm_programme_settings_id")
    public AdmProgrammeSettingsDBO admProgrammeSettingsDBO;
    
    @Column(name = "document_order")
    public Integer documentOrder;
    
    @ManyToOne
    @JoinColumn(name="adm_qualification_list_id")
    public AdmQualificationListDBO admQualificationListDBO;
    
    @Column(name = "is_to_be_submitted")
    public boolean isToBeSubmitted;
    
    @Column(name = "is_online_upload_required")
    public boolean isOnlineUploadRequired;
    
    @Column(name = "is_upload_mandatory")
    public boolean isUploadMandatory;
    
    @Column(name = "is_additional_for_foreign_national")
    public boolean isAdditionalForForeignNational;
    
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;

}
