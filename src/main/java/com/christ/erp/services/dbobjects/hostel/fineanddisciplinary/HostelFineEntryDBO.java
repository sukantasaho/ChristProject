package com.christ.erp.services.dbobjects.hostel.fineanddisciplinary;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelAttendanceDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="hostel_fine_entry")
@Getter
@Setter
public class HostelFineEntryDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="hostel_fine_entry_id")
	private int id;
	
	@ManyToOne 
	@JoinColumn(name ="hostel_admissions_id")
	private HostelAdmissionsDBO hostelAdmissionsDBO;
	
	@ManyToOne
	@JoinColumn(name= "hostel_fine_category_id")
	private HostelFineCategoryDBO hostelFineCategoryDBO;
	
	@Column(name ="date")
	private LocalDate date;
	
	@Column(name ="fine_amount")
	private Integer fineAmount;
	
	@Column(name ="remarks")
	private String remarks;
	
	@ManyToOne
	@JoinColumn(name= "hostel_disciplinary_actions_id")
	private HostelDisciplinaryActionsDBO hostelDisciplinaryActionsDBO;
	
	@ManyToOne
	@JoinColumn(name= "hostel_attendance_id_morning")
	private HostelAttendanceDBO morningHostelAttendanceDBO;
	
	@ManyToOne
	@JoinColumn(name= "hostel_attendance_id_evening")
	private HostelAttendanceDBO eveningHostelAttendanceDBO;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id",updatable=false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name = "is_completely_paid")
	public Boolean isCompletelyPaid;
}
