package com.christ.erp.services.dbobjects.employee.recruitment;
import java.io.Serializable;
import java.time.LocalDate;
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
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "emp_appln_advertisement")
@Setter
@Getter
public class EmpApplnAdvertisementDBO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_appln_advertisement_id")
	public Integer id;

	@ManyToOne
	@JoinColumn(name="erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;
	
	@Column(name="advertisement_no")
	public String advertisementNo;
	
	@Column(name="advertisement_start_date")
	public LocalDate advertisementStartDate;
	
	@Column(name="advertisement_end_date")
	public LocalDate advertisementEndDate;
	
	@Column(name="advertisement_content", columnDefinition = "mediumtext")
	public  String advertisementContent;
	
	@Column(name="other_info")
	public String otherInfo;
	
	@Column(name ="is_common_advertisement")
	public Boolean isCommonAdvertisement;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id",updatable = false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@OneToMany(mappedBy ="empApplnAdvertisementId", fetch = FetchType.LAZY,cascade = CascadeType.ALL )
	public Set<EmpApplnAdvertisementImagesDBO> empApplnAdvertisementImagesSet;

}