package com.christ.erp.services.dbobjects.admission.settings;

import java.io.Serializable;
import java.math.BigDecimal;

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

@Getter
@Setter

@Entity
@Table(name = " adm_programme_qualification_subject_eligibility")
public class AdmProgrammeQualificationSubjectEligibilityDBO implements Serializable {

	private static final long serialVersionUID = -473316487351726741L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="adm_programme_qualification_subject_eligibility_id")
	public int id;

	@ManyToOne
	@JoinColumn(name="adm_programme_qualification_settings_id")
	public AdmProgrammeQualificationSettingsDBO admProgrammeQualificationSettingsDBO;

	@Column(name = "subject_name")
	public String subjectName;

	@Column(name = "eligibility_percentage")
	public BigDecimal eligibilityPercentage;

	@Column(name = "created_users_id",updatable=false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_status")
	public char recordStatus;
}
