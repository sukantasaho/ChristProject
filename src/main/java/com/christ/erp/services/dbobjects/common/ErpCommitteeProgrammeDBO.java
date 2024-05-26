package com.christ.erp.services.dbobjects.common;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_committee_programme")
@Getter
@Setter
public class ErpCommitteeProgrammeDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_committee_programme_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_committee_id")
    private ErpCommitteeDBO erpCommitteeDBO;

	@ManyToOne
	@JoinColumn(name = "erp_programme_id")
    private ErpProgrammeDBO ErpProgrammeDBO ;
	
	@ManyToOne
	@JoinColumn(name = "programme_coordinator_id")
    private EmpDBO programmeCoordinatorId;
	
	@Column(name = "programme_structure_entry_last_date")
	private LocalDate programme_structure_entry_last_date;
	
    @Column(name = "created_users_id")
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;

}
