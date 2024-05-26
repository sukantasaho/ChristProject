package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "obe_programme_outcome_details")
@Getter
@Setter
public class ObeProgrammeOutcomeDetailsDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="obe_programme_outcome_details_id")
    private int id;

	@ManyToOne
	@JoinColumn(name= "obe_programme_outcome_id")
	private ObeProgrammeOutcomeDBO obeProgrammeOutcomeDBO;

	@ManyToOne
	@JoinColumn(name= "obe_outcome_detail_parent_id")
	private ObeProgrammeOutcomeDetailsDBO obeOutcomeDetailParentId;
    
    @Column(name = "reference_no")
	private String referenceNo;
    
    @Column(name = "reference_no_order")
	private BigDecimal referenceNoOrder;
    
//  @Column(name = "category") removed from db
//	private String category;
    
    @Column(name = "statements")
	private String statements;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

	@OneToMany(mappedBy = "obeProgrammeOutcomeDetailsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammePeoMissionMatrixDBO> erpProgrammePeoMissionMatrixDBOSet;
	
	@OneToMany(mappedBy = "obeProgrammeOutcomeDetailsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ObeProgrammeOutcomeDetailsAttributeDBO> obeProgrammeOutcomeDetailsAttributeDBOSet;
}
