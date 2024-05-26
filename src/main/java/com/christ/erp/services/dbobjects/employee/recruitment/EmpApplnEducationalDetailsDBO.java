package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpInstitutionDBO;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;
import com.christ.erp.services.dbobjects.common.ErpUniversityBoardDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_appln_educational_details")
@Setter
@Getter
public class EmpApplnEducationalDetailsDBO implements Serializable{

	private static final long serialVersionUID = -754929255320064553L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_educational_details_id")
    public int empApplnEducationalDetailsId;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@Column(name="qualification_others")
    public String qualificationOthers;
	
	@ManyToOne
	@JoinColumn(name="erp_qualification_level_id")
	public ErpQualificationLevelDBO erpQualificationLevelDBO;
	
	@Column(name="state_others")
	public String stateOthers;
	
	@Column(name="course")
    public String course;
	
	@Column(name="specialization")
    public String specialization;
	
	@Column(name = "year_of_completion")
	public Integer yearOfCompletion;
	
	@Column(name="grade_or_percentage")
    public String gradeOrPercentage;
	
	@Column(name="institute")
    public String institute;
	
	@Column(name="board_or_university")
    public String boardOrUniversity;
	
	@ManyToOne
	@JoinColumn(name = "erp_state_id")
	public ErpStateDBO erpStateDBO; 
	
	@ManyToOne
	@JoinColumn(name = "erp_country_id")
	public ErpCountryDBO erpCountryDBO;
	
	@Column(name="current_status")
    public String currentStatus;

	@OneToMany(mappedBy = "empApplnEducationalDetailsDBO", cascade = CascadeType.ALL)
	public Set<EmpApplnEducationalDetailsDocumentsDBO> documentsDBOSet = new HashSet<>();
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @ManyToOne
	@JoinColumn(name = "erp_institution_id")
	private ErpInstitutionDBO erpInstitutionDBO;
    
    @ManyToOne
	@JoinColumn(name = "erp_university_board_id")
	private ErpUniversityBoardDBO erpUniversityBoardDBO;
}
