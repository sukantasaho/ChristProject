package com.christ.erp.services.dbobjects.hostel.leavesandattendance;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="hostel_block_leaves")
@Getter
@Setter
public class HostelBlockLeavesDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="hostel_block_leaves_id")
	public int id;
	
	@Column(name="block_reason")
	public String blockReason;
	
	@Column(name="block_date")
	public LocalDate blockDate;

	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name = "hostel_admissions_id")
	public HostelAdmissionsDBO hostelAdmissionsDBO;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id",updatable=false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
}
