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
@Table(name = "erp_state")
@Getter
@Setter
public class ErpStateDBO {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_state_id")
    public int id;
	
	@Column(name="state_name")
    public String stateName;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public Character recordStatus;
    
    @ManyToOne
    @JoinColumn(name="erp_country_id")
    private ErpCountryDBO erpCountryDBO;
}
