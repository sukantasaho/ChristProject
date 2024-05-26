package com.christ.erp.services.dbobjects.common;

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
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name =" aca_course_specific_schedule")
@Setter
@Getter
public class AcaCourseSpecificScheduleDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_course_specific_schedule_id")
	private int id;
	
	@Column(name ="course_specific_schedule_name")
	private String courseSpecificScheduleName;
	
	@ManyToOne
	@JoinColumn(name ="erp_academic_year_id")
	private ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name ="aca_duration_id")
	private AcaDurationDBO acaDurationDBO;
	
	@ManyToOne
	@JoinColumn(name ="erp_rooms_id")
	private ErpRoomsDBO erpRoomsDBO;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "acaCourseSpecificScheduleDBO", cascade = CascadeType.ALL)
    public Set<AcaCourseSpecificScheduleTeachersDBO> acaCourseSpecificScheduleTeachersDBOSet = new HashSet<>();
	
}
