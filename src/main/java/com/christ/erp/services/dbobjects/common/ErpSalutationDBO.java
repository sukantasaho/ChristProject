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
@Table(name = "erp_salutation")
@Getter
@Setter
public class ErpSalutationDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_salutation_id")
	public Integer id;
	
	@Column(name="erp_salutation_name")
	public String  erpSalutationName;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer  createdUsersId;

	@Column(name = "modified_users_id")
	public Integer  modifiedUsersId;

	@Column(name = "record_status")
	public Character recordStatus;

}
