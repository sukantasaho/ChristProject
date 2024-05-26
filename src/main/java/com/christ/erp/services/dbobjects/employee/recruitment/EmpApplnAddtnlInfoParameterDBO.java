package com.christ.erp.services.dbobjects.employee.recruitment;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "emp_appln_addtnl_info_parameter")
public class EmpApplnAddtnlInfoParameterDBO implements Serializable,Comparable<EmpApplnAddtnlInfoParameterDBO> {
	
	private static final long serialVersionUID = -6987276642873487287L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_appln_addtnl_info_parameter_id")
	public Integer id;

	@Column(name = "addtnl_info_parameter_name")
	public String addtnlInfoParameterName;

	@Column(name = "parameter_display_order")
	public Integer parameterDisplayOrder;

	@Column(name = "is_display_in_application")
	public Boolean isDisplayInApplication;

	@ManyToOne
	@JoinColumn(name = "emp_appln_addtnl_info_heading_id")
	public EmpApplnAddtnlInfoHeadingDBO empApplnAddtnlInfoHeading;

	@Column(name = "created_users_id")
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_status")
	public char recordStatus;
	
	@Override
	public int compareTo(EmpApplnAddtnlInfoParameterDBO ob) {
		if(ob!=null){
			if((ob.parameterDisplayOrder>(this.parameterDisplayOrder))){
				return 1;
			}if((ob.parameterDisplayOrder<(this.parameterDisplayOrder))){
				return -1;
			}
		}
		return 1;
	}
}