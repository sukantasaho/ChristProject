package com.christ.erp.services.dbobjects.curriculum.timeTable;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "timetable_template_day")
@Getter
@Setter
public class TimeTableTemplateDayDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="timetable_template_day_id")
    private int id;
    
	@ManyToOne
	@JoinColumn(name= "timetable_template_id")
	private TimeTableTemplateDBO timeTableTemplateDBO;
	
    @Column(name = "day_name")
	private String dayName;
    
    @Column(name = "day_of_week")
	private Integer dayOfWeek;
    
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy = "timeTableTemplateDayDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<TimeTableTemplatePeriodDBO> timeTableTemplatePeriodDBOSet;
	
}
