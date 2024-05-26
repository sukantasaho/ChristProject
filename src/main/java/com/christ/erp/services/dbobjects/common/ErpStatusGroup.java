/**
 * 
 */
package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@SuppressWarnings("serial")
@Entity
@Table(name = "erp_status_group")
public class ErpStatusGroup implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_status_group_id")
	public Integer id;
	
	@Column(name = "status_group_name")
	public String statusGroupName;
	
	@Column(name = "erp_status_table_name")
	public String erpStatusTableName;
	
    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

}
