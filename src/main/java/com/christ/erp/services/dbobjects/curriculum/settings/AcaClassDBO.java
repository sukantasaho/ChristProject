package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;
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

import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpRoomsDBO;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassVirtualClassMapDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aca_class")
@Getter
@Setter

public class AcaClassDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="aca_class_id")
    private int id;
    
    @Column(name = "class_name")
   	private String className;
    
    @Column(name = "created_users_id",updatable=false)
  	private Integer createdUsersId;
  	
  	@Column(name = "modified_users_id")
  	private Integer modifiedUsersId;
  	
  	@Column(name = "record_status")
  	private char recordStatus;
  	
	@OneToMany(mappedBy = "acaVirtualClassDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<AcaClassVirtualClassMapDBO> acaClassVirtualClassMapDBOSet;
	
	@Column(name = "class_code")
	private String classCode;
	
	@Column(name = "specialization_name")
	private String specializationName;
	
	@ManyToOne
	@JoinColumn(name = "aca_duration_detail_id")
	private AcaDurationDetailDBO acaDurationDetailDBO;
	
	@ManyToOne
	@JoinColumn(name = "aca_duration_id")
	private AcaDurationDBO acaDurationDBO; 
	
//	@ManyToOne
//	@JoinColumn(name = "erp_campus_programme_mapping_id")
//	private ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_campus_department_mapping_id")
	private ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO;
	
	@Column(name = "is_virtual_class")
	private boolean virtualClass;
	
	@Column(name = "is_having_virtual_class")
	private boolean havingVirtualClass;
	
	@Column(name = "section")
	private String section;
	
	@Column(name ="campus_code")
	private String campusCode;
	
	@ManyToOne
	@JoinColumn(name = " erp_rooms_id")
	private ErpRoomsDBO erpRoomsDBO;
}
