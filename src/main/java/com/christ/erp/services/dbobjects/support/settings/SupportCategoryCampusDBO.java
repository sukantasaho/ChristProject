package com.christ.erp.services.dbobjects.support.settings;
import java.io.Serializable;
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

import com.christ.erp.services.dbobjects.common.ErpCampusDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter
@Table(name = "support_category_campus")
public class SupportCategoryCampusDBO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "support_category_campus_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "support_category_id")
	private SupportCategoryDBO supportCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_campus_id")
	private ErpCampusDBO erpCampusDBO;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supportCategoryCampusDBO", cascade = CascadeType.ALL)
    private Set<SupportCategoryCampusDetailsDBO> supportCategoryCampusDetailsDBOs;

}
