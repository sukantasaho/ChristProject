package com.christ.erp.services.dbobjects.hostel.fineanddisciplinary;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDisciplinaryActionsTypeDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hostel_disciplinary_actions")
@Getter
@Setter
public class HostelDisciplinaryActionsDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_disciplinary_actions_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
	private ErpAcademicYearDBO academicYearDBO;
	
	@ManyToOne
	@JoinColumn(name = "hostel_admissions_id")
	private HostelAdmissionsDBO hostelAdmissionsDBO;
	
	@ManyToOne
	@JoinColumn(name ="hostel_disciplinary_actions_type_id")
	public HostelDisciplinaryActionsTypeDBO hostelDisciplinaryActionsTypeDBO;
	
	@Column(name ="disciplinary_acation_date")
	private LocalDate disciplinaryActionsDate;
	
	@Column(name ="remarks")
	private String remarks;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

	@OneToOne(mappedBy = "hostelDisciplinaryActionsDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public HostelFineEntryDBO hostelFineEntryDBO ;
}
