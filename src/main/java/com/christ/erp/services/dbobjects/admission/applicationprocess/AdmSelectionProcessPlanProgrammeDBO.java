package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.util.List;
import java.util.Set;

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
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDetailDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionGeneralDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_plan_programme")
@Getter
@Setter
public class AdmSelectionProcessPlanProgrammeDBO {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_selection_process_plan_programme_id")
    public int id;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_plan_id")
	public AdmSelectionProcessPlanDBO admSelectionProcessPlanDBO;

    @ManyToOne
    @JoinColumn(name = "erp_campus_programme_mapping_id")
    public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
	
	@ManyToOne
	@JoinColumn(name = "adm_programme_batch_id")
	public AdmProgrammeBatchDBO admProgrammeBatchDBO;

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
