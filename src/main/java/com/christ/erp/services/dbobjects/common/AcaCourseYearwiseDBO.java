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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name = "aca_course_yearwise")
@Getter
@Setter

public class AcaCourseYearwiseDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aca_course_yearwise_id")
    private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_acadmeic_year")
	private ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn (name ="aca_course_id")
	private AcaCourseDBO acaCourseDBO;
	 
	@Column(name="created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
    
    @OneToMany(mappedBy = "acaCourseYearwiseDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   	public Set<AcaCourseSessionwiseDBO> acaCourseSessionwiseDBOSet = new HashSet<>();
}
