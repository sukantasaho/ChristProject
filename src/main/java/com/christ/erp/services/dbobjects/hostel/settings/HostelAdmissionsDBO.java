package com.christ.erp.services.dbobjects.hostel.settings;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@SuppressWarnings("serial")
@Entity
@Table(name = "hostel_admissions")

public class HostelAdmissionsDBO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_admissions_id")
	public int id;
	
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name="hostel_application_id")
	public HostelApplicationDBO hostelApplicationDBO;
	
	@Column(name = "date_of_admission")
	public LocalDateTime dateOfAdmission;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="student_appln_entries_id")
	public StudentApplnEntriesDBO studentApplnEntriesDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="student_id")
	public StudentDBO studentDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hostel_id")
	public HostelDBO hostelDBO;
	
	@ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
	@JoinColumn(name="hostel_bed_id")
	public HostelBedDBO hostelBedDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hostel_room_type_id")
	public HostelRoomTypeDBO hostelRoomTypeDBO;
	
	@Column(name = "erp_current_status_time")
	public LocalDateTime erpCurrentStatusTime;
	
	@Column(name = "cancelled_by_user_id")
	public Integer cancelledByUserId;
	
	@Column(name = "cancelled_reason")
	public String cancelledReason;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "erp_status_id")
	public ErpStatusDBO erpStatusDBO;
	
	@Column(name ="biometric_id")
	public Integer biometricId;
	
	@Column(name ="check_in_date")
	public LocalDate checkInDate;
	
	@Column(name ="checkout_date")
	public LocalDate checkOutDate;
	
	@Column(name ="checkout_remarks")
	public String checkOutRemarks;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hostelAdmissionsDBO", cascade = CascadeType.ALL)
	public Set<HostelAdmissionsFacilityDBO> hostelFacilityDBOSet =  new HashSet<>();

}
