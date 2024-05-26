package com.christ.erp.services.transactions.employee.salary;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleMatrixDetailDBO;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Set;

public class PayScaleMatrixTransaction {

    private static volatile PayScaleMatrixTransaction payScaleMatrixTransaction = null;

    public static PayScaleMatrixTransaction getInstance() {
        if(payScaleMatrixTransaction==null) {
            payScaleMatrixTransaction = new PayScaleMatrixTransaction();
        }
        return  payScaleMatrixTransaction;
    }

    public boolean saveOrUpdate(List<EmpPayScaleMatrixDetailDBO> matrixDetails) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                matrixDetails.forEach(dbo -> {
                    if(dbo.id==0) {
                        context.persist(dbo);
                    }
                    else {
                        context.merge(dbo);
                    }
                });
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getGridData() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
         @Override
         public List<Tuple> onRun(EntityManager context) throws Exception {
             String str = "select emp_pay_scale_grade_mapping.pay_scale_revised_year as pay_scale_revised_year,emp_employee_category.emp_employee_category_id,emp_employee_category.employee_category_name as employee_category_name," +
                     " emp_pay_scale_grade.emp_pay_scale_grade_id as emp_pay_scale_grade_id,emp_pay_scale_grade.grade_name as grade_name from emp_employee_category" +
                     " inner join emp_pay_scale_grade on emp_pay_scale_grade.emp_employee_category_id = emp_employee_category.emp_employee_category_id" +
                     " inner join emp_pay_scale_grade_mapping on emp_pay_scale_grade_mapping.emp_pay_scale_grade_id = emp_pay_scale_grade.emp_pay_scale_grade_id" +
                     " inner join emp_pay_scale_grade_mapping_detail on emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_id = emp_pay_scale_grade_mapping.emp_pay_scale_grade_mapping_id" +
                     " inner join emp_pay_scale_matrix_detail on emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_detail_id = emp_pay_scale_matrix_detail.emp_pay_scale_grade_mapping_detail_id" +
                     " where emp_employee_category.record_status='A' and  emp_pay_scale_grade.record_status='A' and emp_pay_scale_grade_mapping.record_status='A'" +
                     " and emp_pay_scale_grade_mapping_detail.record_status='A' and emp_pay_scale_matrix_detail.record_status='A'" +
                     " group by emp_pay_scale_grade_mapping.pay_scale_revised_year,emp_employee_category.emp_employee_category_id,emp_pay_scale_grade.emp_pay_scale_grade_id";
             Query query = context.createNativeQuery(str,Tuple.class);
             return query.getResultList();
         }
         @Override
         public void onError(Exception error) throws Exception {
            throw error;
         }
     });
    }

    public List<Tuple> getMatrixDataList(String payScaleGradeId, String revisedYear) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                String str = "select emp_pay_scale_matrix_detail.emp_pay_scale_matrix_detail_id as emp_pay_scale_matrix_detail_id," +
                        " emp_pay_scale_matrix_detail.emp_pay_scale_grade_mapping_detail_id as emp_pay_scale_grade_mapping_detail_id," +
                        " emp_pay_scale_matrix_detail.level_cell_no as level_cell_no,emp_pay_scale_matrix_detail.level_cell_value as level_cell_value" +
                        " from emp_pay_scale_matrix_detail" +
                        " inner join emp_pay_scale_grade_mapping_detail ON emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_detail_id = emp_pay_scale_matrix_detail.emp_pay_scale_grade_mapping_detail_id" +
                        " inner join emp_pay_scale_grade_mapping ON emp_pay_scale_grade_mapping.emp_pay_scale_grade_mapping_id = emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_id" +
                        " where emp_pay_scale_matrix_detail.record_status='A' and emp_pay_scale_grade_mapping_detail.record_status='A' and emp_pay_scale_grade_mapping.record_status='A'" +
                        " and emp_pay_scale_grade_mapping.emp_pay_scale_grade_id =:payScaleGradeId" +
                        " and emp_pay_scale_grade_mapping.pay_scale_revised_year=:revisedYear order by emp_pay_scale_grade_mapping_detail.pay_scale_display_order";
                Query query = context.createNativeQuery(str,Tuple.class);
                query.setParameter("payScaleGradeId",payScaleGradeId);
                query.setParameter("revisedYear",revisedYear);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getPayScaleMappingData(String payScaleGradeId, String revisedYear) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                String str = "select emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_detail_id,emp_pay_scale_grade_mapping_detail.pay_scale_display_order," +
                        " emp_pay_scale_grade_mapping_detail.pay_scale,emp_pay_scale_grade_mapping_detail.pay_scale_level, " +
                        " emp_pay_scale_level.emp_pay_scale_level_id, emp_pay_scale_level.emp_pay_scale_level" + // query changed
                        " from emp_pay_scale_grade_mapping_detail" +
                        " inner join emp_pay_scale_grade_mapping ON emp_pay_scale_grade_mapping.emp_pay_scale_grade_mapping_id = emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_id" +
                        " inner join emp_pay_scale_level on emp_pay_scale_level.emp_pay_scale_level_id = emp_pay_scale_grade_mapping_detail.emp_pay_scale_level_id"+  // query changed
                        " where emp_pay_scale_grade_mapping_detail.record_status='A' and emp_pay_scale_grade_mapping.record_status='A' and emp_pay_scale_level.record_status='A'" +
                        " and emp_pay_scale_grade_mapping.emp_pay_scale_grade_id =:payScaleGradeId" + 
                        " and emp_pay_scale_grade_mapping.pay_scale_revised_year=:revisedYear order by emp_pay_scale_grade_mapping_detail.pay_scale_display_order";
                Query query = context.createNativeQuery(str,Tuple.class);
                query.setParameter("payScaleGradeId",payScaleGradeId);
                query.setParameter("revisedYear",revisedYear);
                return query.getResultList();
            }

            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean delete(Set<Integer> matrixIds) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("update emp_pay_scale_matrix_detail set record_status='D' where emp_pay_scale_matrix_detail_id in (:ids)");
                query.setParameter("ids",matrixIds);
                return query.executeUpdate() > 0 ? true : false;
            }

            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
}