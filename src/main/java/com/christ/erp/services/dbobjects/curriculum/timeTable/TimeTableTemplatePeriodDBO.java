package com.christ.erp.services.dbobjects.curriculum.timeTable;

import java.math.BigDecimal;
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
@Table(name = "timetable_template_period")
@Getter
@Setter
public class TimeTableTemplatePeriodDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="timetable_template_period_id")
    private int id;
    
	@ManyToOne
	@JoinColumn(name= "timetable_template_day_id")
	private TimeTableTemplateDayDBO timeTableTemplateDayDBO;
	
    @Column(name = "period_name")
	private String periodName;
    
    @Column(name = "period_order")
	private Integer periodOrder;
    
    @Column(name = "period_start_time")
	private LocalTime periodStartTime;
    
    @Column(name = "period_end_time")
	private LocalTime periodEndTime;
    
    @Column(name = "duration_in_hour")
	private BigDecimal durationInHour;
    
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
    
}
