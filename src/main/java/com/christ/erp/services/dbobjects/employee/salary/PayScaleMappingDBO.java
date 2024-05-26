package com.christ.erp.services.dbobjects.employee.salary;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "emp_pay_scale_grade_mapping")
public class PayScaleMappingDBO implements Serializable {
	private static final long serialVersionUID = -7464583519812637478L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_pay_scale_grade_mapping_id")
	public Integer id;
	
	@Column(name="pay_scale_revised_year")
	public Integer revisedYear;

	@Column(name="emp_pay_scale_grade_id")
	public Integer grade;
	
	@Column(name = "created_users_id")
    public Integer createdUserId;

    @Column(name = "modified_users_id")
    public Integer modifiedUserId;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mapping")
	public Set<PayScaleMappingItemDBO> levels = new HashSet<>();
}
