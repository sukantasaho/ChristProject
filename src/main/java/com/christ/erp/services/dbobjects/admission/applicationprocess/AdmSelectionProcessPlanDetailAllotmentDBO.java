package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.time.LocalTime;

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
@Table(name = "adm_selection_process_plan_detail_allotment")
@Getter
@Setter
public class AdmSelectionProcessPlanDetailAllotmentDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_plan_detail_allotment_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_plan_detail_id")
	public AdmSelectionProcessPlanDetailDBO admSelectionProcessPlanDetailDBO;
	
	@Column(name = "selection_process_time")
	public LocalTime selectionProcessTime;
	
	@Column(name = "selection_process_seats")
	public Integer selectionProcessSeats;
	
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public Character recordStatus;
}
