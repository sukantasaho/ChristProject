package com.christ.erp.services.dbobjects.admission.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="adm_selection_process_type_details")
@Setter
@Getter
public class AdmSelectionProcessTypeDetailsDBO {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_selection_process_type_details_id")
    public Integer id;
	
	@Column(name="process_order")
    public Integer order;
	
	@Column(name="sub_process_name")
	public String subProcessName;
	
	@Column(name="panelist_count")
    public Integer panelistCount;
	
	@ManyToOne
	@JoinColumn(name="adm_scorecard_id")
	public AdmScoreCardDBO admissionScoreCardDBO;
	
	@ManyToOne
    @JoinColumn(name="adm_selection_process_type_id")
    public AdmSelectionProcessTypeDBO adminSelectionProcessTypeDBO;
	
	@Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;
 
    @Column(name="record_status")
    public char recordStatus;

}
