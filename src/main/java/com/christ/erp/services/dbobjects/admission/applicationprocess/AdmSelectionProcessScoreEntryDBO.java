package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.math.BigDecimal;
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
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_score_entry")
@Setter
@Getter
public class AdmSelectionProcessScoreEntryDBO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_score_entry_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "adm_selection_process_score_id")
	private AdmSelectionProcessScoreDBO admSelectionProcessScoreDBO;
	
	@ManyToOne
	@JoinColumn(name = "adm_scorecard_id")
	private AdmScoreCardDBO admScoreCardDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "erp_users_id")
	private ErpUsersDBO erpUsersDBO;
		
	@Column(name="score_entered")
	public BigDecimal scoreEntered;
	
	@Column(name="max_score")
	public BigDecimal maxScore;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private Character recordStatus;
	
	@OneToMany(mappedBy ="admSelectionProcessScoreEntryDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<AdmSelectionProcessScoreEntryDetailsDBO> AdmSelectionProcessScoreEntryDetailsDBOSet;
}
