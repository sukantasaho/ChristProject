package com.christ.erp.services.dbobjects.curriculum.timeTable;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "timetable_template")
@Getter
@Setter
public class TimeTableTemplateDBO {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="timetable_template_id")
    private int id;
    
    @Column(name = "time_table_name")
	private String timeTableName;
    
    @Column(name = "is_static")
	private Boolean isStatic;
    
    @Column(name = "period_duration_in_minutes")
	private Integer periodDurationInMinutes;
    
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy = "timeTableTemplateDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<TimeTableTemplateCampusDBO> timeTableTemplateCampusDBOSet;
	
	@OneToMany(mappedBy = "timeTableTemplateDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<TimeTableTemplateDayDBO> timeTableTemplateDayDBOSet;

}
