package com.christ.erp.services.dbobjects.support.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpUserGroupDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter

@Table(name = "support_category_user_group")
public class SupportCategoryUserGroupDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "support_category_user_group_id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "support_category_id")
	private SupportCategoryDBO supportCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_user_group_id")
	private ErpUserGroupDBO erpUserGroupDBO;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}
