package com.christ.erp.services.dbobjects.support.settings;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter

@Table(name = "support_category_campus_details")
public class SupportCategoryCampusDetailsDBO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "support_category_campus_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "support_category_campus_id")
	private SupportCategoryCampusDBO supportCategoryCampusDBO;
	
	@ManyToOne
	@JoinColumn(name = "support_role_id")
	private SupportRoleDBO supportRoleDBO;
	
	@Column(name = "max_time_to_resolve")
	private Integer maxTimeToResolve; 
	
	@Column(name = "group_email_id")
	private String groupEmailId;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supportCategoryCampusDetailsDBO", cascade = CascadeType.ALL)
    private Set<SupportCategoryCampusDetailsEmployeeDBO> supportCategoryCampusDetailsEmployeeDBOs;
	
}
