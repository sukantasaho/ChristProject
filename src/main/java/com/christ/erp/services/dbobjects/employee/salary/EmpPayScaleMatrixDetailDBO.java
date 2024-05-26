package com.christ.erp.services.dbobjects.employee.salary;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="emp_pay_scale_matrix_detail")
@Setter
@Getter
public class EmpPayScaleMatrixDetailDBO implements Serializable {

    private static final long serialVersionUID = 3076138785825196848L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_pay_scale_matrix_detail_id")
    public int id;

    @ManyToOne
    @JoinColumn(name="emp_pay_scale_grade_mapping_detail_id")
    public EmpPayScaleGradeMappingDetailDBO empPayScaleGradeMappingDetailDBO;

    @Column(name="level_cell_no")
    public Integer levelCellNo;

    @Column(name="level_cell_value")
    public BigDecimal levelCellValue;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}