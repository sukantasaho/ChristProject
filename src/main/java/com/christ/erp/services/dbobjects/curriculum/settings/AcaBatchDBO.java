package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;
import java.time.LocalDate;
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

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aca_batch")
@Getter
@Setter
public class AcaBatchDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="aca_batch_id")
    private int id;
    
    @Column(name = "batch_name")
   	private String batchName;
    
    @Column(name = "in_take_batch_number")
   	private Integer inTakeBatchNumber;
    
    @Column(name = "is_multiple_exit")
   	private Boolean isMultipleExit;
    
    @Column(name = "batch_commencement_date")
   	private LocalDate batchCommencementDate;
    
    @Column(name = "batch_commencement_month")
   	private Integer batchCommencementMonth;
    
    @Column(name = "programme_completion_month")
   	private Integer programmeCompletionMonth;
    
    @Column(name = "approved_intake_for_batch")
   	private Integer approvedIntakeForBatch;
    
    @Column(name = "revised_intake_for_batch")
   	private Integer revisedIntakeForBatch;
    
    @Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "erp_programme_batchwise_settings_id")
	private ErpProgrammeBatchwiseSettingsDBO erpProgrammeBatchwiseSettingsDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "erp_campus_programme_mapping_id")
	private ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "batch_starting_aca_duration_detail_id")
	private AcaDurationDetailDBO batchStartingAcaDurationDetail;
	
	@OneToMany(mappedBy = "acaBatchDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AcaDurationDetailDBO> acaDurationDetailDBO;
}
