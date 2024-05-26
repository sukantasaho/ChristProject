package com.christ.erp.services.dbobjects.common;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_admission_category")
@Getter
@Setter
public class ErpAdmissionCategoryDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_admission_category_id")
	private int id;
	
	@Column(name = "admission_category_name")
    private String admissionCategoryName;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "erpAdmissionCategoryDBO", cascade = CascadeType.ALL)
    private Set<ErpAdmissionCategoryCampusMappingDBO> erpAdmissionCategoryCampusMappingDBOSet;

}
