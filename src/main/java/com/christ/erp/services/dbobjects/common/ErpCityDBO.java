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

@Setter
@Getter
@Entity
@Table(name = "erp_city")
public class ErpCityDBO {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_city_id")
    public int id;

	@Column(name="city_name")
    public String cityName;
	
	@ManyToOne
    @JoinColumn(name="erp_state_id")
    public ErpStateDBO erpStateDBO;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
