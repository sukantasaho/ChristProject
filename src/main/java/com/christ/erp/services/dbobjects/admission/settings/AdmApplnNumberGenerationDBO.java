package com.christ.erp.services.dbobjects.admission.settings;

import java.io.Serializable;
import java.util.HashSet;
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

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="adm_appln_number_generation")
public class AdmApplnNumberGenerationDBO implements Serializable{
	
	private static final long serialVersionUID = 2118963355031111422L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_appln_number_generation_id")
    public Integer id;
	
	@ManyToOne
	@JoinColumn(name="erp_academic_year_id")
	public ErpAcademicYearDBO academicYearDBO;
	
	@Column(name="online_appln_no_prefix")
	public String onlineApplnNoPrefix;
	
	@Column(name="online_appln_no_from")
	public Integer onlineApplnNoFrom;
	
	@Column(name="online_appln_no_to")
	public Integer onlineApplnNoTo;

	@Column(name="offline_appln_current_no")
	public Integer offlineApplnCurrentNo;

	@Column(name="online_appln_current_no")
	public Integer onlineApplnCurrentNo;
	
	@Column(name="offline_appln_no_prefix")
	public String offlineApplnNoPrefix;
	
	@Column(name="offline_appln_no_from")
	public Integer offlineApplnNoFrom;
	
	@Column(name="offline_appln_no_to")
	public Integer offlineApplnNoTo;
	
	@Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;
 
    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(mappedBy = "admApplnNumberGenerationDBO",fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    public Set<AdmApplnNumberGenDetailsDBO> admApplnNumberGenDetailsDBOSet = new HashSet<>();
}
