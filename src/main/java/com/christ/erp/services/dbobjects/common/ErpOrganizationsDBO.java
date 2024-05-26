package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="erp_organizations")
public class ErpOrganizationsDBO implements Serializable {

	private static final long serialVersionUID = 3430816799123697257L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_organizations_id")  
	public int id;
	
	@Column(name="organization_name")
	public String organizationName;
	
	@Column(name="address_1")
	public String address1;
	
	@Column(name="address_2")
	public String address2;
	
	@Column(name="address_3")
	public String address3;
	
	@Column(name="organization_vision",columnDefinition = "mediumtext")
	public String organizationVision;
	
	@Column(name="organization_mission",columnDefinition = "mediumtext")
	public String organizationMission;
	
	@Column(name="organization_core_values",columnDefinition = "mediumtext")
	public String organizationCoreValues;
	
	@Column(name="created_users_id",updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public char recordStatus;

}
