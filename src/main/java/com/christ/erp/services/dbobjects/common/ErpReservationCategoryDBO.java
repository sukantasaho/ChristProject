package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "erp_reservation_category")
public class ErpReservationCategoryDBO{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_reservation_category_id")
    public Integer id;

	@Column(name="reservation_category_name")
    public String reservationCategoryName;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
}
