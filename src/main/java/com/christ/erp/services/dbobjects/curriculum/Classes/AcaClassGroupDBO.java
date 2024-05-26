package com.christ.erp.services.dbobjects.curriculum.Classes;

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
import com.christ.erp.services.dbobjects.common.AcaCourseDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.curriculum.common.AttActivityDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aca_class_group")
@Setter
@Getter
public class AcaClassGroupDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_class_group_id")
	private int id;
	
	@Column(name ="class_group_name")
	private String classGroupName;
	
	@ManyToOne
	@JoinColumn(name ="aca_duration_id")
	private AcaDurationDBO acaDurationDBO;
	
	@ManyToOne
	@JoinColumn(name ="aca_course_id")
	private AcaCourseDBO acaCourseDBO;
	
	@ManyToOne
	@JoinColumn(name="att_activity_id")
	private AttActivityDBO attActivityDBO;
	
	@ManyToOne
	@JoinColumn(name ="erp_campus_id")
	private ErpCampusDBO erpCampusDBO;
	
	@ManyToOne
	@JoinColumn(name ="erp_department_id")
	private ErpDepartmentDBO erpDepartmentDBO;
	
	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

	@OneToMany(mappedBy = "acaClassGroupDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<AcaClassGroupDetailsDBO> acaClassGroupDetailsDBOSet = new HashSet<>();
}
