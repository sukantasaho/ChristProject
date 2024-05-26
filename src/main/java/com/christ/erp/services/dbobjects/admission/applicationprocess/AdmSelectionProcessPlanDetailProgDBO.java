package com.christ.erp.services.dbobjects.admission.applicationprocess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_plan_detail_prog")
@Getter
@Setter
public class AdmSelectionProcessPlanDetailProgDBO  {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_plan_detail_prog_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_plan_detail_id")
	public AdmSelectionProcessPlanDetailDBO admSelectionProcessPlanDetailDBO;

	@ManyToOne
	@JoinColumn(name = "adm_programme_batch_id")
	public AdmProgrammeBatchDBO admProgrammeBatchDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_campus_programme_mapping_id")
	public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;

	@ManyToOne
	@JoinColumn(name = "aca_batch_id")
	public AcaBatchDBO acaBatchDBO;
	
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
