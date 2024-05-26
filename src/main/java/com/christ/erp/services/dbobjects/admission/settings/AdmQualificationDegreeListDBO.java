package com.christ.erp.services.dbobjects.admission.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_qualification_degree_list")
@Getter
@Setter
public class AdmQualificationDegreeListDBO{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_qualification_degree_list_id")
    private int id;
    
    @Column(name = "degree_name")
    private String degreeName;
    
    @Column(name="is_no_marks_details")
    private boolean isNoMarksDetails;
    
    @ManyToOne
    @JoinColumn(name = "adm_qualification_list_id")
    private AdmQualificationListDBO admQualificationListDBO;
    
    @Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}

