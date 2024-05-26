package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.AcaSessionDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "aca_duration_detail")
@Getter
@Setter
public class AcaDurationDetailDBO implements Serializable {

	private static final long serialVersionUID = 1130683620491428987L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="aca_duration_detail_id")
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "aca_duration_id")
	private AcaDurationDBO acaDurationDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "aca_session_id")
	private AcaSessionDBO acaSessionDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "aca_batch_id")
	private AcaBatchDBO acaBatchDBO;
	
	@Column(name = "session_start_date")
	private LocalDate sessionStartDate;
	
	@Column(name = "session_end_date")
	private LocalDate sessionEndDate;
	
	@Column(name = "session_first_instruction_date")
	private LocalDate sessionFirstInstructionDate;
	
	@Column(name = "session_last_instruction_date")
	private LocalDate sessionLastInstructionDate;
	
	@Column(name = "session_final_exam_start_date")
	private LocalDate sessionFinalExamStartDate;
	
	@Column(name = "session_final_exam_end_date")
	private LocalDate sessionFinalExamEndDate;
	
	@Column(name = "vacation_start_date")
	private LocalDate vacationStartDate;
	
	@Column(name = "vacation_end_date")
	private LocalDate vacationEndDate;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	  
	@Column( name = "record_status")
    private char recordStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "erp_campus_programme_mapping_id")
	private ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
}
