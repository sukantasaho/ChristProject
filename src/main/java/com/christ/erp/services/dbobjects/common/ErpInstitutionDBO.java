package com.christ.erp.services.dbobjects.common;

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
@Table(name="erp_institution")
@Getter
@Setter
public class ErpInstitutionDBO{
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_institution_id")
    private int id;

    @Column(name="institution_name")
    private String institutionName;
    
    @ManyToOne
    @JoinColumn(name="erp_pincode_id")
    private ErpPincodeDBO erpPincodeId;
    
    @ManyToOne
    @JoinColumn(name="erp_country_id")
    private ErpCountryDBO erpCountryId;

    @ManyToOne
    @JoinColumn(name="erp_state_id")
    private ErpStateDBO erpStateDBO;

    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private Character recordStatus;

    @Column(name="board_type")
    private String boardType;
}
