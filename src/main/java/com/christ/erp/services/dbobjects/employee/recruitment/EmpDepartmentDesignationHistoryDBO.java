package com.christ.erp.services.dbobjects.employee.recruitment;

import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDesignationDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpTitleDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "emp_department_designation_history")
public class EmpDepartmentDesignationHistoryDBO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_department_designation_history_id")
    public Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    public EmpDBO empDBO;

    @ManyToOne
    @JoinColumn(name = "emp_designation_id")
    public EmpDesignationDBO empDesignationDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erp_campus_department_mapping_id")
    public ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_employee_job_category_id")
    public EmpEmployeeJobCategoryDBO empEmployeeJobCategoryDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_title_id")
    public EmpTitleDBO empTitleDBO;

    @Column(name="from_date")
    public LocalDate fromDate;

    @Column(name="to_date")
    public LocalDate toDate;

    @Column(name = "is_current")
    public Boolean isCurrent;

    @Column(name="created_users_id",updatable = false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public Character recordStatus;

}
