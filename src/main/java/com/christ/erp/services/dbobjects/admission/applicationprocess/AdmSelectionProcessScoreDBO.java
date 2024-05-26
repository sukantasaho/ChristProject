package com.christ.erp.services.dbobjects.admission.applicationprocess;

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
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_score")
@Getter
@Setter
public class AdmSelectionProcessScoreDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_score_id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "adm_selection_process_id")
	private AdmSelectionProcessDBO admSelectionProcessDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "adm_selection_process_type_details_id")
	private AdmSelectionProcessTypeDetailsDBO admSelectionProcessTypeDetailsDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "adm_selection_process_group_id")
	private AdmSelectionProcessGroupDBO admSelectionProcessGroupDBO;
	
    @Column(name = "is_absent")
	private Boolean isAbsent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "erp_work_flow_process_id")
	private ErpWorkFlowProcessDBO erpWorkFlowProcessDBO;

	@Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
    
    @OneToMany(mappedBy ="admSelectionProcessScoreDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<AdmSelectionProcessScoreEntryDBO> AdmSelectionProcessScoreEntryDBOSet;
	
}
