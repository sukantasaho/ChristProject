package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "att_type")
@Getter
@Setter
public class AttTypeDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "att_type_id")
	private int id;
	
	@Column(name = "attendance_type")
    private String attendanceType;
	
	@Column(name =" is_non_course_activity")
	private Boolean isNonCourseActivity;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;

}
