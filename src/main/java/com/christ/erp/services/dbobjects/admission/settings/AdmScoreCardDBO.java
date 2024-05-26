package com.christ.erp.services.dbobjects.admission.settings;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "adm_scorecard")
public class AdmScoreCardDBO  {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_scorecard_id")
    public int id;

    @Column(name="scorecard_template_name")
    public String scorecardTemplateName;
       
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admScoreCardDBO", cascade = CascadeType.ALL)
    public Set<AdmScoreCardQualitativeParameterDBO> admScoreCardQualitativeParameterDBO = new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admScoreCardDBO", cascade = CascadeType.ALL)
    public Set<AdmScoreCardQuantitativeParameterDBO> admScoreCardQuantitativeParameterDBO = new HashSet<>();

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
