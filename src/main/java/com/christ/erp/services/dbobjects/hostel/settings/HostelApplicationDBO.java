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
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hostel_application")
@SuppressWarnings("serial")
@Getter
@Setter 
public class HostelApplicationDBO implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_application_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;
	
	@Column(name= "date_of_application")
	public LocalDateTime dateOfApplication;
	
	@Column(name = "application_no")
	public Integer applicationNo;
	
	@ManyToOne
	@JoinColumn(name = "student_appln_entries_id")
	public StudentApplnEntriesDBO studentApplnEntriesDBO;

	@ManyToOne
	@JoinColumn(name = "student_id")
	public StudentDBO studentDBO;
	
	@ManyToOne
	@JoinColumn(name = "hostel_id")
	public HostelDBO hostelDBO;
	
	@ManyToOne
	@JoinColumn(name = "allotted_hostel_room_type_id")
	public HostelRoomTypeDBO allottedHostelRoomTypeDBO;
	
	@Column(name= "application_prefix")
	public String applicationPrefix;
	
	@Column(name = "remarks")
	public String remarks;
	
	@Column(name = "is_offline")
	public Boolean isOffline;
	
	@Column(name ="hostel_applicant_status_time")
	public LocalDateTime hostelApplicantStatusTime;
	
	@ManyToOne
	@JoinColumn(name = "hostel_applicant_current_process_status")
	public ErpWorkFlowProcessDBO hostelApplicantCurrentProcessStatus;
	
	@Column(name ="hostel_application_status_time")
	public LocalDateTime hostelApplicationStatusTime;
	
	@ManyToOne
	@JoinColumn(name = "hostel_application_current_process_status")
	public ErpWorkFlowProcessDBO hostelApplicationCurrentProcessStatus;
	
	@Column(name ="fee_payment_end_date")
	public LocalDate feePaymentEndDate;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(mappedBy = "hostelApplicationDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<HostelApplicationRoomTypePreferenceDBO> hostelApplicationRoomTypePreferenceDBO = new HashSet<>();
}
