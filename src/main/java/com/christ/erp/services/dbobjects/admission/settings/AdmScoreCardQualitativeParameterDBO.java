package com.christ.erp.services.dbobjects.admission.settings;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "adm_scorecard_qualitative_parameter")
@Setter
@Getter
public class AdmScoreCardQualitativeParameterDBO  {
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_scorecard_qualitative_parameter_id")
    public int id;

    @ManyToOne
    @JoinColumn(name="adm_qualitative_parameter_id")
    public AdmQualitativeParamterDBO admQualitativeParamterDBO;
    
    @ManyToOne
    @JoinColumn(name="adm_scorecard_id")
    public AdmScoreCardDBO admScoreCardDBO;
    
    @Column(name = "order_no")
    public Integer orderNo;

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
    
   
}
