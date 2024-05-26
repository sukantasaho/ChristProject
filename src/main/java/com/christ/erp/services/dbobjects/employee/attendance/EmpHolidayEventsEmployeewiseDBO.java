package com.christ.erp.services.dbobjects.employee.attendance;
import java.io.Serializable;
import javax.persistence.*;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_holiday_events_employeewise")
@Setter
@Getter
public class EmpHolidayEventsEmployeewiseDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_holiday_events_employeewise_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="emp_holiday_events_id")
	public EmpHolidayEventsDBO empHolidayEventsDBO;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;	
	
	 @Column(name = "created_users_id")
     public Integer createdUsersId;

	 @Column(name = "modified_users_id")
	 public Integer modifiedUsersId;
	
	 @Column(name = "record_status")
	 public char recordStatus;
}
