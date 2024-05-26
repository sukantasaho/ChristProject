package com.christ.erp.services.transactions.employee.salary;
import com.christ.erp.services.common.*;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsCommentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsReviewerAndApproverDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.employee.salary.AnnualIncrementSearchDTO;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;

public class AnnualIncrementTransaction {

    private static volatile AnnualIncrementTransaction annualIncrementTransaction = null;

    public static AnnualIncrementTransaction getInstance() {
        if(annualIncrementTransaction==null) {
            annualIncrementTransaction = new AnnualIncrementTransaction();
        }
        return  annualIncrementTransaction;
    }

    public List<Tuple> getAllGradeLevelCell() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context){
                String str = "SELECT level_cell_no as cellName," +
                        " emp_pay_scale_matrix_detail_id as cellId," +
                        " level_cell_value as basicAmount," +
                        " emp_pay_scale_level.emp_pay_scale_level_id as scaleId , emp_pay_scale_level.emp_pay_scale_level as scaleLevel," + // query changed
                        " emp_pay_scale_grade_mapping_detail.pay_scale_level as levelName," +
                        " emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_detail_id as levelId," +
                        " emp_pay_scale_grade.emp_pay_scale_grade_id as gradeId" +
                        " FROM emp_pay_scale_matrix_detail" +
                        " inner join emp_pay_scale_grade_mapping_detail ON emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_detail_id = emp_pay_scale_matrix_detail.emp_pay_scale_grade_mapping_detail_id" +
                        " inner join emp_pay_scale_grade_mapping ON emp_pay_scale_grade_mapping.emp_pay_scale_grade_mapping_id = emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_id" +
                        " inner join emp_pay_scale_level on emp_pay_scale_level.emp_pay_scale_level_id = emp_pay_scale_grade_mapping_detail.emp_pay_scale_level_id"+ // query changed
                        " inner join emp_pay_scale_grade ON emp_pay_scale_grade.emp_pay_scale_grade_id = emp_pay_scale_grade_mapping.emp_pay_scale_grade_id where emp_pay_scale_matrix_detail.record_status = 'A' and " +
                        " emp_pay_scale_grade_mapping_detail.record_status = 'A' and emp_pay_scale_grade_mapping.record_status = 'A' and emp_pay_scale_grade.record_status = 'A'";
                Query query = context.createNativeQuery(str,Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getAllEmployee(AnnualIncrementSearchDTO data, Boolean isCurrent, List<Integer> year, Integer statusId) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<Tuple> onRun(EntityManager context) {
                String commonSelectQuery="select `emp_pay_scale_details`.emp_pay_scale_details_id as ID,`emp_pay_scale_details`.emp_id as empID, `emp_pay_scale_details`.pay_scale_effective_date as effectiveDate,\n" +
                        " erp_academic_year.academic_year as academicYear, `emp_pay_scale_details`.pay_scale_type as payScaleType,`emp_pay_scale_details`.gross_pay as currentTotal,\n" +
                        " `emp_pay_scale_details`.is_published,emp_job_details.joining_date as joiningDate,emp.emp_name as name,emp_pay_scale_details.emp_appln_entries_id as applnId,\n" +
                        " erp_campus.erp_location_id as locationValue,erp_location.location_name as locationLabel,emp_designation.emp_designation_id as designationValue, emp_designation.emp_designation_name as designationLabel," +
                        " erp_department.erp_department_id as departmentValue,erp_department.department_name as departmentLabel,\n" +
                        " (select erp_work_flow_process.process_code from erp_work_flow_process where erp_work_flow_process.erp_work_flow_process_id = emp_pay_scale_details.status_id) as statusId,\n" +
                        " `emp_pay_scale_details`.modified_time as modifiedDate,emp_pay_scale_details.previous_year_gross_pay as previousTotal, emp.doj as dateOfJoining ";
                String commonTableQuery = " from emp_pay_scale_details \n" +
                        " inner join emp on emp.emp_id = emp_pay_scale_details.emp_id\n" +
                        " inner join emp_job_details ON emp_job_details.emp_job_details_id = emp.emp_job_details_id" +
                        " inner join emp_designation ON emp_designation.emp_designation_id = emp.emp_designation_id\n" +
                        " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id\n"+
                        " inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id\n" +
                        " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id\n" +
                        " inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id\n" +
                        " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_pay_scale_details.erp_academic_year_id";
                String commonWhereConditionQueryString = " where emp_pay_scale_details.record_status = 'A' and emp.record_status = 'A' " +
                        " and emp_job_details.record_status = 'A' and emp_pay_scale_details.pay_scale_type =:payScaleId and emp_designation.record_status = 'A' " +
                        " and erp_campus_department_mapping.record_status = 'A' and erp_department.record_status = 'A'";
                String latestDateQueryString = " and if( \n" +
                        " ( select  erp_academic_year.erp_academic_year_id from emp_pay_scale_details\n" +
                        " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_pay_scale_details.erp_academic_year_id\n" +
                        "  where emp_pay_scale_details.emp_id = emp.emp_id \n" +
                        " and emp_pay_scale_details.record_status = 'A'\n" +
                        " and erp_academic_year.erp_academic_year_id=:yearId\n" +
                        " and emp_pay_scale_details.status_id is not null\n" +
                        " )=:yearId,1=0,\n" +
                        " erp_academic_year.academic_year = (select MAX(academic_year) from erp_academic_year\n" +
                        " inner join emp_pay_scale_details ON erp_academic_year.erp_academic_year_id = emp_pay_scale_details.erp_academic_year_id\n" +
                        " where emp_pay_scale_details.emp_id = emp.emp_id \n" +
                        " and erp_academic_year.record_status = 'A'\n" +
                        " and emp_pay_scale_details.record_status = 'A' \n" +
                        " and emp_pay_scale_details.is_published=1))";
                String scalePaySelectQueryString = " ,emp_pay_scale_matrix_detail.level_cell_no as cellLabel, emp_pay_scale_matrix_detail.level_cell_value as amount,emp_pay_scale_matrix_detail.emp_pay_scale_matrix_detail_id as cellValue,\n" +
                        " emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_detail_id as levelValue,emp_pay_scale_grade_mapping_detail.pay_scale_level as levelLabel,\n" +
                        " emp_pay_scale_grade.emp_pay_scale_grade_id as gradeValue, emp_pay_scale_grade.grade_name as gradeLabel," +
                        " emp_pay_scale_level.emp_pay_scale_level as scaleLevel"; // query changed
                String scalePayTableQueryString = " inner join emp_pay_scale_matrix_detail ON emp_pay_scale_matrix_detail.emp_pay_scale_matrix_detail_id = emp_pay_scale_details.emp_pay_scale_matrix_detail_id\n" +
                        " inner join emp_pay_scale_grade_mapping_detail on emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_detail_id = emp_pay_scale_matrix_detail.emp_pay_scale_grade_mapping_detail_id\n" +
                        " inner join emp_pay_scale_grade_mapping ON emp_pay_scale_grade_mapping.emp_pay_scale_grade_mapping_id = emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_id\n" +
                        " inner join emp_pay_scale_grade ON emp_pay_scale_grade.emp_pay_scale_grade_id = emp_pay_scale_grade_mapping.emp_pay_scale_grade_id" +
                		" inner join emp_pay_scale_level on emp_pay_scale_level.emp_pay_scale_level_id = emp_pay_scale_grade_mapping_detail.emp_pay_scale_level_id"; // query changed
                String scalePayWhereConditionQueryString = " and emp_pay_scale_matrix_detail.record_status = 'A'\n" +
                        " and emp_pay_scale_grade_mapping_detail.record_status = 'A' and emp_pay_scale_grade_mapping.record_status = 'A' and emp_pay_scale_grade.record_status = 'A'";
                String dailyPaySelectQueryString = " ,emp_pay_scale_details.wage_rate_per_type as currentWagePerDay";
                if (!Utils.isNullOrEmpty(data.payScaleType) && !Utils.isNullOrEmpty(data.payScaleType.value)){
                    switch (data.payScaleType.value) {
                        case "SCALE PAY":
                            commonSelectQuery = commonSelectQuery + scalePaySelectQueryString + commonTableQuery + scalePayTableQueryString +
                                    commonWhereConditionQueryString + scalePayWhereConditionQueryString;
                            break;
                        case "DAILY":
                            commonSelectQuery = commonSelectQuery + dailyPaySelectQueryString + commonTableQuery +
                                    commonWhereConditionQueryString;
                            break;
                        case "CONSOLIDATED":
                            commonSelectQuery = commonSelectQuery + commonTableQuery +
                                    commonWhereConditionQueryString;
                            break;
                    }
                }
                if(isCurrent){
                    commonSelectQuery += latestDateQueryString;
                }else{
                    if(!Utils.isNullOrEmpty(year)){
                        commonSelectQuery += " and emp_pay_scale_details.erp_academic_year_id in (:yearId)";
                    }
                }
                if(!Utils.isNullOrEmpty(statusId)){
                    if(!data.status.value.equals("0")){
                        commonSelectQuery += " and emp_pay_scale_details.status_id =:statusId";
                    }
                }
                if(!Utils.isNullOrEmpty(data.campus)){
                    commonSelectQuery += " and erp_campus_department_mapping.erp_campus_id in (:campusId)";
                }
                if(!Utils.isNullOrEmpty(data.employeeCategory) && !Utils.isNullOrEmpty(data.employeeCategory.value)){
                    commonSelectQuery += " and emp.emp_employee_category_id =:empCategoryId";
                }
                if(!Utils.isNullOrEmpty(data.jobCategory) && !Utils.isNullOrEmpty(data.jobCategory.value)){
                    commonSelectQuery += " and emp.emp_employee_job_category_id =:jobCategoryId";
                }
                if(!Utils.isNullOrEmpty(data.location) && !Utils.isNullOrEmpty(data.location.value)){
                    commonSelectQuery += " and erp_campus.erp_location_id =:locationId";
                }

                Query query = context.createNativeQuery(commonSelectQuery, Tuple.class);

                if(!Utils.isNullOrEmpty(statusId)){
                    if(!data.status.value.equals("0"))
                        query.setParameter("statusId",statusId);
                }
                if(!Utils.isNullOrEmpty(data.campus)){
                    List campus = new ArrayList();
                    for (LookupItemDTO campusid: data.campus){
                        campus.add(Integer.parseInt(campusid.value));
                    }
                    query.setParameter("campusId",campus);
                }
                if(!Utils.isNullOrEmpty(data.employeeCategory) && !Utils.isNullOrEmpty(data.employeeCategory.value)){
                    query.setParameter("empCategoryId",Integer.parseInt(data.employeeCategory.value));
                }
                if(!Utils.isNullOrEmpty(data.jobCategory) && !Utils.isNullOrEmpty(data.jobCategory.value)){
                    query.setParameter("jobCategoryId",Integer.parseInt(data.jobCategory.value));
                }
                if(!Utils.isNullOrEmpty(data.payScaleType) && !Utils.isNullOrEmpty(data.payScaleType.value)){
                    query.setParameter("payScaleId",data.payScaleType.value);
                }
                if(!Utils.isNullOrEmpty(data.location) && !Utils.isNullOrEmpty(data.location.value)){
                    query.setParameter("locationId",Integer.parseInt(data.location.value));
                }
                if(!Utils.isNullOrEmpty(year) && !Utils.isNullOrEmpty(year)){
                    query.setParameter("yearId",year);
                }
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getEmpPayScaleComponentsForIncrement(String payScaleType) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) {
                String str = " select SC.emp_pay_scale_components_id AS ID, SC.salary_component_name AS salaryComponentName , SC.salary_component_short_name AS salaryComponentShortName," +
                        " SC.salary_component_display_order  AS salaryComponentDisplayOrder,is_component_basic AS isComponentBasic,SC.percentage  AS percentage,SC.is_caculation_type_percentage AS isCaculationTypePercentage,SC.pay_scale_type AS payScaleType" +
                        " FROM emp_pay_scale_components AS SC where SC.record_status='A' and SC.pay_scale_type =:payScaleType order by salaryComponentDisplayorder; ";
                Query query = context.createNativeQuery(str, Tuple.class);
                query.setParameter("payScaleType",payScaleType);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getPayScaleDetailComponent(String id) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) {
                String str = "select emp_pay_scale_details_components_id as payScaleDetailsComponentId, emp_pay_scale_components.emp_pay_scale_components_id as id, emp_pay_scale_components.salary_component_name as salaryName, \n" +
                        " emp_pay_scale_components.salary_component_short_name as shortName, emp_pay_scale_components.pay_scale_type as scaleType, emp_pay_scale_components.is_component_basic as isBasic, \n" +
                        " emp_pay_scale_components.salary_component_display_order as displayOrder, emp_pay_scale_components.is_caculation_type_percentage as isPercentage, emp_pay_scale_components.percentage as percentage, \n" +
                        " emp_pay_scale_details_components.emp_salary_component_value as amount,emp_pay_scale_details_components.emp_pay_scale_details_id as payscaleId \n" +
                        " from  emp_pay_scale_details_components \n" +
                        " inner join emp_pay_scale_components ON emp_pay_scale_components.emp_pay_scale_components_id = emp_pay_scale_details_components.emp_pay_scale_components_id \n" +
                        " where emp_pay_scale_components.record_status = 'A' and emp_pay_scale_details_components.record_status = 'A' \n" +
                        " and  emp_pay_scale_details_components.emp_pay_scale_details_id =:id";
                Query query = context.createNativeQuery(str, Tuple.class);
                query.setParameter("id",Integer.parseInt(id));
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<ErpWorkFlowProcessDBO> getIncrementStatus() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpWorkFlowProcessDBO>>() {
            @Override
            public List<ErpWorkFlowProcessDBO> onRun(EntityManager context){
                Query currentYearQuery = context.createQuery("select b from ErpWorkFlowProcessDBO b where b.recordStatus = 'A' and b.erpWorkFlowProcessGroupDBO.id =:groupId ");
                currentYearQuery.setParameter("groupId",Integer.parseInt("8"));
                return currentYearQuery.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getReviewersAndApproversList() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context){
                Query currentYearQuery = context.createNativeQuery("select distinct erp.emp_id as id, erp.user_name as label from erp_users erp \n" +
                        " inner join erp_campus_department_user_title ON erp.erp_users_id = erp_campus_department_user_title.erp_users_id \n" +
                        " where erp_campus_department_user_title.erp_users_id = erp.erp_users_id and erp_campus_department_user_title.record_status = 'A' \n" +
                        " and erp.record_status = 'A' ",Tuple.class);
                return currentYearQuery.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    //for change to hql
    public boolean saveOrUpdateComments(EmpPayScaleDetailsCommentsDBO empPayScaleDetailsCommentsDBOS) throws Exception {
        return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context) {
                context.persist(empPayScaleDetailsCommentsDBOS);
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getUpdatedComments(String payscaleId) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context){
                Query query = context.createNativeQuery("select emp_pay_scale_details_comments.emp_pay_scale_details_comments_id as ID,emp_pay_scale_details_comments.emp_pay_scale_details_id as payScaleId,\n" +
                        " emp_pay_scale_details_comments.pay_scale_comments as comments,emp_pay_scale_details_comments.pay_scale_commented_timestamp as times,\n" +
                        " (select emp_name from emp where emp.emp_id = emp_pay_scale_details_comments.emp_id and emp.record_status = 'A') as userName from emp_pay_scale_details_comments  \n" +
                        " where emp_pay_scale_details_comments.emp_pay_scale_details_id=:paysacaleid and emp_pay_scale_details_comments.record_status = 'A'",Tuple.class);
                query.setParameter("paysacaleid",payscaleId);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public Integer getOpenAcademicYearByLocationId(String locationId) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<Integer>() {
            @Override
            public Integer onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select erp_academic_year.erp_academic_year_id from erp_academic_year inner join emp_pay_scale_sessions \n" +
                        " ON emp_pay_scale_sessions.erp_academic_year_id = erp_academic_year.erp_academic_year_id \n" +
                        " where emp_pay_scale_sessions.erp_location_id =:locationId and emp_pay_scale_sessions.is_open=1 and emp_pay_scale_sessions.is_current=1");
                query.setParameter("locationId",locationId);
                return (Integer) Utils.getUniqueResult(query.getResultList());
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean saveOrUpdateEmpData(List<EmpPayScaleDetailsDBO> dboList) throws Exception {
        return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context) {
                for (EmpPayScaleDetailsDBO dbo: dboList){
                    if(Utils.isNullOrEmpty(dbo.id) || dbo.id == 0){
                        context.persist(dbo);
                    }else{
                        context.merge(dbo);
                    }
                }
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public EmpPayScaleDetailsReviewerAndApproverDBO getApproverOrReviewerIdByEmpIdAndPayScaleId(String value, Integer id) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<EmpPayScaleDetailsReviewerAndApproverDBO>() {
            @Override
            public EmpPayScaleDetailsReviewerAndApproverDBO onRun(EntityManager context) throws Exception {
                Query query = context.createQuery(" select dbo  from EmpPayScaleDetailsReviewerAndApproverDBO dbo \n" +
                        " where dbo.empDBO.id =:empId and dbo.empPayScaleDetailsDBO.id =:payscaleId \n" +
                        " and dbo.recordStatus = 'A'");
                query.setParameter("empId",Integer.parseInt(value));
                query.setParameter("payscaleId",id);
//                return (EmpPayScaleDetailsReviewerAndApproverDBO) Utils.getUniqueResult(query.getResultList());
                return (EmpPayScaleDetailsReviewerAndApproverDBO) Utils.getUniqueResult(query.getResultList());
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getReviewOrApprovalRequests(String empId,String status_code) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) {
                Query query = context.createNativeQuery(" select pay.pay_scale_type as payScale, count(pay.pay_scale_type) as notificationCount,\n" +
                        " erp_campus.erp_location_id as locationId,\n" +
                        " emp_pay_scale_reviewer_and_approver.reviewer_approver_type as isReviewOrApprove,emp_pay_scale_reviewer_and_approver.emp_id as id\n" +
                        " FROM emp_pay_scale_details pay\n" +
                        " inner join emp_pay_scale_reviewer_and_approver on emp_pay_scale_reviewer_and_approver.emp_pay_scale_details_id = pay.emp_pay_scale_details_id\n" +
                        " inner join emp ON emp.emp_id = pay.emp_id\n" +
                        " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id\n" +
                        " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id\n" +
                        " left join erp_work_flow_process on pay.status_id = erp_work_flow_process.erp_work_flow_process_id\n" +
                        " where emp_pay_scale_reviewer_and_approver.emp_id =:empId and emp_pay_scale_reviewer_and_approver.record_status = 'A' and pay.record_status = 'A'\n" +
                        " and erp_work_flow_process.process_code =:statusCode\n" +
                        " group by pay.pay_scale_type, erp_campus.erp_location_id, emp_pay_scale_reviewer_and_approver.reviewer_approver_type, " +
                        " emp_pay_scale_reviewer_and_approver.emp_id",Tuple.class);
                query.setParameter("empId",empId);
                query.setParameter("statusCode",status_code);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getEmployeeIsReviewerOrApprover(String empId) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) {
                Query query = context.createNativeQuery(" select pay.reviewer_approver_type, pay.emp_id from emp_pay_scale_reviewer_and_approver pay where pay.emp_id=:empId and pay.record_status = 'A'" +
                        " group by pay.reviewer_approver_type, pay.emp_id",Tuple.class);
                query.setParameter("empId",empId);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getAllLocation() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) {
                Query query = context.createNativeQuery("select erp_location.erp_location_id as value, erp_location.location_name as label from erp_location where erp_location.record_status = 'A'",Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean updatePayscaleDetailsTableWhenCommetnsAreUpdated(EmpPayScaleDetailsDBO dbo) throws Exception {
        return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context) {
                if(!Utils.isNullOrEmpty(dbo.id)){
                    context.merge(dbo);
                    return true;
                }else{
                    return false;
                }
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public EmpPayScaleDetailsDBO getPayScaleDetailsDbo(int id) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<EmpPayScaleDetailsDBO>() {
            @Override
            public EmpPayScaleDetailsDBO onRun(EntityManager context) throws Exception {
                Query query = context.createQuery(" select bo  from EmpPayScaleDetailsDBO bo \n" +
                        " where bo.id=:payscaleId \n" +
                        " and bo.recordStatus = 'A'",EmpPayScaleDetailsDBO.class);
                query.setParameter("payscaleId",id);
                return (EmpPayScaleDetailsDBO) Utils.getUniqueResult(query.getResultList());
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
}