package com.christ.erp.services.dbobjects.curriculum.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.AcaGraduateAttributesDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "obe_programme_outcome_details_attribute")
@Setter
@Getter
public class ObeProgrammeOutcomeDetailsAttributeDBO {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="obe_programme_outcome_details_attribute_id")
    private int id;
    
	@ManyToOne
	@JoinColumn(name= "obe_programme_outcome_details_id")
	private ObeProgrammeOutcomeDetailsDBO obeProgrammeOutcomeDetailsDBO;
	
	@ManyToOne
	@JoinColumn(name= "aca_graduate_attributes_id")
	private AcaGraduateAttributesDBO acaGraduateAttributesDBO;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
    
}
