package com.christ.erp.services.dbobjects.admission.applicationprocess;

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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_group")
@Setter
@Getter
public class AdmSelectionProcessGroupDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_group_id")
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "adm_selection_process_plan_detail_allotment_id")
	private AdmSelectionProcessPlanDetailAllotmentDBO admSelectionProcessPlanDetailAllotmentDBO;
	
	@Column(name = "selection_process_group_name")
	private String selectionProcessGroupName;
	
	@Column(name = "selection_process_group_no")
	private Integer selectionProcessGroupNo;

	@Column(name = "total_participants_in_group")
	private Integer totalParticipantsInGroup;

	@Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private Character recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admSelectionProcessGroupDBO", cascade = CascadeType.ALL)
   	private Set<AdmSelectionProcessGroupDetailDBO> admSelectionProcessGroupDetailDBOsSet = new HashSet<AdmSelectionProcessGroupDetailDBO>();
}