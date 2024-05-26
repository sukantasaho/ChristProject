package com.christ.erp.services.dbobjects.employee.salary;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "emp_pay_scale_grade_mapping_detail")
public class PayScaleMappingItemDBO implements Serializable {
	private static final long serialVersionUID = -3480077399544145903L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_pay_scale_grade_mapping_detail_id")
	public Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="emp_pay_scale_grade_mapping_id")
	public PayScaleMappingDBO mapping;
	
//	@Column(name="pay_scale_level")
//	public String level;
	
	@Column(name="pay_scale")
	public String scale;
	
	@Column(name = "created_users_id")
    public Integer createdUserId;

    @Column(name = "modified_users_id")
    public Integer modifiedUserId;
	
	@Column(name="pay_scale_display_order")
	public Integer order;
}
