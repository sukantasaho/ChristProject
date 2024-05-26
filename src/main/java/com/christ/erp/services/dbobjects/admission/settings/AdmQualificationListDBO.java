package com.christ.erp.services.dbobjects.admission.settings;

import javax.persistence.*;

import com.christ.erp.services.dbobjects.student.common.ApplicationVerificationStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "adm_qualification_list")
@Getter
@Setter
public class AdmQualificationListDBO{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_qualification_list_id")
    public int id;
    
    @Column(name = "qualification_name")
    public String qualificationName;
    
    @Column(name = "qualification_order")
    public Integer qualificationOrder;
    
    @Column(name = "short_name")
    public String shortName;
    
    @Column(name = "is_additional_document")
    public Boolean isAdditionalDocument;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_type",columnDefinition="ENUM('Board','University','Board_X','Board_XI','Board_XII')")
    public Boardtype boardtype;

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
