package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import com.christ.erp.services.dbobjects.employee.letter.EmpLetterTypeDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="emp_employee_letter_details")
@Setter
@Getter
public class EmpEmployeeLetterDetailsDBO implements Serializable
{
	private static final long serialVersionUID = 7567834570124282660L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_employee_letter_details_id")
    public Integer id;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "letter_url_id")
	public UrlAccessLinkDBO letterUrlDBO;

	@ManyToOne
	@JoinColumn(name= "emp_letter_type_id")
	public EmpLetterTypeDBO empLetterTypeDBO;

	@Column(name="letter_type")
	public String letterType;
	
	@Column(name="letter_ref_no")
	public Integer letterRefNo;
	
	@Column(name="letter_date")
	public LocalDate letterDate;
	
	@Column(name="letter_url")
	public String letterUrl;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
