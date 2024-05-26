package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualitativeParamterOptionDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardQualitativeParameterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardQuantitativeParameterDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_score_entry_details")
@Setter
@Getter
public class AdmSelectionProcessScoreEntryDetailsDBO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_score_entry_details_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_score_entry_id")
	private AdmSelectionProcessScoreEntryDBO admSelectionProcessScoreEntryDBO;
	
	@ManyToOne
	@JoinColumn(name = "adm_scorecard_qualitative_parameter_id")
	private AdmScoreCardQualitativeParameterDBO admScoreCardQualitativeParameterDBO;
	
	@ManyToOne
	@JoinColumn(name = "adm_qualitative_parameter_option_id")
	private AdmQualitativeParamterOptionDBO admQualitativeParamterOptionDBO;
	
	@ManyToOne
	@JoinColumn(name = "adm_scorecard_quantitative_parameter_id")
	private AdmScoreCardQuantitativeParameterDBO admScoreCardQuantitativeParameterDBO;
	
	@Column(name="qualitative_parameter_score_entered_text")
	public String qualitativeParameterScoreEnteredText;
	
	@Column(name="quantitative_parameter_max_score")
	public BigDecimal quantitativeParameterMaxScore;
	
	@Column(name="quantitative_parameter_score_entered")
	public BigDecimal quantitativeParameterScoreEntered;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private Character recordStatus;
}
