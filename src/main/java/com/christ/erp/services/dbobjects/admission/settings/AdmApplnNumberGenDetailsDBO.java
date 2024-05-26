package com.christ.erp.services.dbobjects.admission.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name="adm_appln_number_gen_details")
public class AdmApplnNumberGenDetailsDBO implements Serializable{
	
	private static final long serialVersionUID = 2118963355031111422L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_appln_number_gen_details_id")
    public Integer id;
	
	@ManyToOne
	@JoinColumn(name="adm_appln_number_generation_id")
	public AdmApplnNumberGenerationDBO admApplnNumberGenerationDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_programme_mapping_id")
	public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
	
	@Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;
 
    @Column(name="record_status")
    public char recordStatus;
    
}
