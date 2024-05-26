package com.christ.erp.services.dbobjects.employee.attendance;

import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_work_diary_entries_details")
@Setter
@Getter

public class EmpWorkDiaryEntriesDetailsDBO {
	 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "emp_work_diary_entries_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name ="emp_work_diary_entries_id")
	private EmpWorkDiaryEntriesDBO empWorkDiaryEntriesDBO;
	
	@Column(name= "from_time")
	private LocalTime fromTime;
	
	@Column(name= "to_time")
	private LocalTime toTime;
	
	@Column(name ="total_time")
	private LocalTime totalTime;
	
	@ManyToOne
	@JoinColumn(name ="emp_work_diary_activity_id")
    private EmpWorkDiaryActivityDBO empWorkDiaryActivityDBO;
	
	@Column(name= "other_activity")
	private String otherActivity;
	
	@Column(name= "remarks")
	private String remarks;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
}
