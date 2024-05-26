package com.christ.erp.services.transactions.employee.settings;

import java.util.List;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDBO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDTO;

@Repository
public class AssignApproversTransaction {

	// in some query record status = A is not checked because in EmpDBO table EmpApproversDBO is @OneToOne relationship.
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public List<Tuple> getGridData() {
		String str = " select data.type as type_Name , data.typeId as type_Id , erp_campus.campus_name as campus_name,  "
				+ "				 erp_campus.erp_campus_id as erp_campus_id ,erp_department.erp_department_id as erp_department_id,  "
				+ "				 erp_department.department_name as department_name , erp_campus_department_mapping.erp_campus_department_mapping_id as erp_campus_department_mapping_id , "
				+ "        data.empCategoryName as emp_Category_Name, data.empCategoryId as emp_Category_Id"
				+ "				 from  "
				+ "				 (select distinct erp_campus_department_mapping.erp_campus_department_mapping_id as approvers_camp_dept,'Leave' as type , 2 as typeId,emp_employee_category.employee_category_name as empCategoryName, emp_employee_category.emp_employee_category_id as empCategoryId  "
				+ "				 from emp_approvers  "
				+ "				 inner join emp ON emp.emp_id = emp_approvers.emp_id and emp.record_status='A'  "
				+ "				 inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status='A'  "
				+ "				 inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id and emp_employee_category.record_status='A' "
				+ "         where emp_approvers.leave_approver_id is not null and emp_approvers.record_status='A'  "
				+ "				 union  "
				+ "				 select distinct erp_campus_department_mapping.erp_campus_department_mapping_id as approvers_camp_dept,'Appraiser' as type , 1 as typeId,emp_employee_category.employee_category_name as empCategoryName, emp_employee_category.emp_employee_category_id as empCategoryId  "
				+ "				 from emp_approvers  "
				+ "				 inner join emp ON emp.emp_id = emp_approvers.emp_id and emp.record_status='A'  "
				+ "				 inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status='A'  "
				+ "				 inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id and emp_employee_category.record_status='A' "
				+ "         where emp_approvers.level_one_appraiser_id is not null and emp_approvers.record_status='A'  "
				+ "				 union  "
				+ "				 select distinct erp_campus_department_mapping.erp_campus_department_mapping_id as approvers_camp_dept,'Work Dairy' as type ,  3 as typeId,emp_employee_category.employee_category_name as empCategoryName, emp_employee_category.emp_employee_category_id as empCategoryId  "
				+ "				 from emp_approvers  "
				+ "				 inner join emp ON emp.emp_id = emp_approvers.emp_id and emp.record_status='A'  "
				+ "				 inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status='A'  "
				+ "				inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id and emp_employee_category.record_status='A' "
				+ "         where emp_approvers.work_diary_approver_id is not null and emp_approvers.record_status='A' )  "
				+ "				 as data  "
				+ "				 inner join erp_campus_department_mapping on data.approvers_camp_dept = erp_campus_department_mapping.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status = 'A'   "
				+ "				 inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status = 'A'  "
				+ "				 inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id and erp_department.record_status='A' ; "
				+ "          ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> edit(EmpApproversDTO data) {
		String str = " select emp_approvers.emp_approvers_id as emp_approvers_id,"
				+ " emp_approvers.leave_approver_id as leave_approver_id, "
				+ " emp_approvers.leave_authoriser_id as leave_authoriser_id, "
				+ " emp_approvers.level_one_appraiser_id as level_one_appraiser_id, "
				+ " emp_approvers.level_two_appraiser_id as level_two_appraiser_id,"
				+ " emp_approvers.work_diary_approver_id as work_diary_approver_id, "
				+ " emp_leave_approver.emp_name as leave_approver_name , "
				+ " emp_leave_authorizer.emp_name as leave_authoriser_name, "
				+ " emp_lvl_one_approver.emp_name as level_one_appraiser_name, "
				+ " emp_lvl_two_approver.emp_name as level_two_appraiser_name, "
				+ " emp_work_dry_appvr.emp_name as work_diary_approver_name, "
				+ " emp.emp_name as emp_name, "
				+ " emp.emp_id as emp_id, "
				+ " erp_campus.erp_campus_id as erp_campus_id, "
				+ " erp_campus.campus_name as campus_name, "
				+ " erp_department.department_name as department_name,"
				+ " erp_department.erp_department_id as erp_department_id, emp_employee_category.employee_category_name as employee_category_name "
				+ " from emp "
				+ " left join  emp_approvers ON emp.emp_id = emp_approvers.emp_id "
				+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status='A' "
				+ " inner join erp_campus on erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status ='A' "
				+ " inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id and erp_department.record_status='A' "
				+ " left join emp as emp_leave_approver on emp_approvers.leave_approver_id=emp_leave_approver.emp_id  and emp.record_status='A' "
				+ " left join emp as emp_leave_authorizer on emp_approvers.leave_authoriser_id =emp_leave_authorizer.emp_id and emp.record_status='A' "
				+ " left join emp as emp_lvl_one_approver on emp_approvers.level_one_appraiser_id = emp_lvl_one_approver.emp_id and emp.record_status='A' "
				+ " left join emp as emp_lvl_two_approver on emp_approvers.level_two_appraiser_id = emp_lvl_two_approver.emp_id and emp.record_status='A' "
				+ " left join emp as emp_work_dry_appvr on emp_approvers.work_diary_approver_id = emp_work_dry_appvr.emp_id and emp.record_status='A' "
				+ "	inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id and emp_employee_category.record_status='A' "
				+ " where  emp.record_status = 'A' and emp_approvers.record_status = 'A'"
				+ " and erp_campus_department_mapping.erp_campus_id=:campusId ";
		if(data.getDepartment().getValue() != null && !data.getDepartment().getValue().isEmpty()) {
			str = str + " and erp_campus_department_mapping.erp_department_id=:departmentId ";
		}
		if(data.getEmpCategory().getValue() != null && !data.getEmpCategory().getValue().isEmpty()) {
			str = str + " and emp_employee_category.emp_employee_category_id=:empCategoryId ";
		}
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr,Tuple.class)
			.setParameter("campusId", data.getCampus().getValue());
			if(data.getDepartment().getValue() != null && !data.getDepartment().getValue().isEmpty()) {
				query.setParameter("departmentId", data.getDepartment().getValue());
			}
			if(data.getEmpCategory().getValue() != null && !data.getEmpCategory().getValue().isEmpty()) {
				query.setParameter("empCategoryId", data.getEmpCategory().getValue());
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}	
	
	public List<Tuple> getAssignApproversDetails(EmpApproversDTO data){
		String str = "select  emp_approvers.emp_id as EMPID , "
				+ " emp_approvers.emp_approvers_id as empApprId "
				+ " from emp_approvers "
				+ "	inner join emp ON emp.emp_id = emp_approvers.emp_id "
				+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id "
				+ "	inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
				+ "	inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id "
				+ "  inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id and emp_employee_category.record_status='A' "
				+ "	where erp_campus_department_mapping.erp_campus_id =:campustId and emp.record_status = 'A' and emp_approvers.record_status = 'A' "
				+ "	and if('Leave'=:type , emp_approvers.leave_approver_id   is not null , "
				+ "	if('Appraiser'=:type , emp_approvers.level_one_appraiser_id is not null , "
				+ "	if('Work Dairy'=:type , emp_approvers.work_diary_approver_id is not null,null)))";
		if(data.getDepartment().getValue() != null && !data.getDepartment().getValue().isEmpty()) {
			str = str + " and erp_campus_department_mapping.erp_department_id=:departmentId";
		}
		if(data.getEmpCategory().getValue() != null && !data.getEmpCategory().getValue().isEmpty()) {
			str = str + " and emp_employee_category.emp_employee_category_id=:empCategoryId";
		}
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr,Tuple.class)
			.setParameter("campustId", data.getCampus().getValue())
			.setParameter("type", data.getTypes().getLabel());
			if(data.getDepartment().getValue() != null && !data.getDepartment().getValue().isEmpty()) {
				query.setParameter("departmentId", data.getDepartment().getValue());
			}
			if(data.getEmpCategory().getValue() != null && !data.getEmpCategory().getValue().isEmpty()) {
				query.setParameter("empCategoryId", data.getEmpCategory().getValue());
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public List<Tuple> getEmpDetails(EmpApproversDTO data) {
		String str = "select erp_department.department_name as department_name,emp.emp_name as emp_name,emp.emp_id as emp_id, "
				+ " emp_approvers.emp_approvers_id as emp_approvers_id, emp_employee_category.emp_employee_category_id as emp_Category_Id, emp_employee_category.employee_category_name as emp_Category_Name  "
				+ " from emp "
				+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status='A' "
				+ " inner join erp_campus on erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status = 'A' "
				+ " inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id and erp_department.record_status = 'A' "
				+ " left join emp_approvers on emp.emp_id = emp_approvers.emp_id  and emp_approvers.record_status ='A' "
				+ "  inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id and emp_employee_category.record_status='A' "
				+ " where  emp.record_status = 'A' and erp_campus_department_mapping.erp_campus_id=:campusId ";
		if(data.getDepartment().getValue() != null && !data.getDepartment().getValue().isEmpty()) {
			str = str + " and erp_campus_department_mapping.erp_department_id=:departmentId";
		}
		if(data.getEmpCategory().getValue() != null && !data.getEmpCategory().getValue().isEmpty()) {
			str = str + " and emp_employee_category.emp_employee_category_id=:empCategoryId";
		}
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr,Tuple.class)
					.setParameter("campusId", data.getCampus().getValue());
			if(data.getDepartment().getValue() != null && !data.getDepartment().getValue().isEmpty()) {
				query.setParameter("departmentId", data.getDepartment().getValue());
			}
			if(data.getEmpCategory().getValue() != null && !data.getEmpCategory().getValue().isEmpty()) {
				query.setParameter("empCategoryId", data.getEmpCategory().getValue());
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public List<Tuple> getEmployeeList(EmpApproversDTO data) {
		String str = " select emp.emp_id as emp_id , emp.emp_name as emp_name "
				+ " from emp " 
				+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status = 'A'"
				+ " where  emp.record_status = 'A' and "
				+ " erp_campus_department_mapping.erp_campus_id=:campusId and emp_title_id is not null "
				+ " order by emp.emp_name asc";
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr,Tuple.class)
					.setParameter("campusId", data.getCampus().getValue());
			return query.getResultList();
		}).await().indefinitely();
		return list;		
	}

	public void update(List<EmpApproversDBO> empApproversId) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(empApproversId.toArray())).await().indefinitely();	
	}
	
	public List<EmpApproversDBO> getEmpApproversData(List<Integer> empApproverIds, String type) {
		String str = "from EmpApproversDBO dbo "
				+ " left join fetch dbo.empApproversDetailsDBOSet as details "
				+ " where dbo.empDBO.id IN (:empApproverIds) and dbo.empDBO.recordStatus = 'A' "; 
		if(!Utils.isNullOrEmpty(type)) {
			str += " and details.approvalType = :type";
		}	
		String finalStr = str;
		List<EmpApproversDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApproversDBO> query = s.createQuery(finalStr, EmpApproversDBO.class);
			query.setParameter("empApproverIds", empApproverIds);
			if(!Utils.isNullOrEmpty(type)) {
				query.setParameter("type", type);
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApproversDBO> getEmpApproversDetails(List<Integer> empApproversIds) {
		String str = "from EmpApproversDBO dbo "
				+ " left join fetch dbo.empApproversDetailsDBOSet as eadds "
				+ " where dbo.recordStatus ='A' and dbo.id IN (:empApproversIds) and eadds.recordStatus ='A'";
		String finalStr = str;
		List<EmpApproversDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApproversDBO> query = s.createQuery(finalStr, EmpApproversDBO.class);
			query.setParameter("empApproversIds", empApproversIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpDBO> getNewEmpDetails(String campusId, String departmentId, String empCategoryId) {
		String str = " select DISTINCT dbo from EmpDBO dbo "
				+ " where dbo.recordStatus = 'A' and dbo.erpCampusDepartmentMappingDBO.recordStatus = 'A' and "
				+ " dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.recordStatus ='A' and "
				+ " dbo.erpCampusDepartmentMappingDBO.erpDepartmentDBO.recordStatus ='A' and "
				+ " dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.id =:campusId and "
				+ " dbo.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id =:departmentId and"
				+ " dbo.empEmployeeCategoryDBO.id =:empCategoryId ";
		List<EmpDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpDBO> query = s.createQuery(str, EmpDBO.class);
			query.setParameter("campusId", Integer.parseInt(campusId));
			query.setParameter("departmentId", Integer.parseInt(departmentId));
			query.setParameter("empCategoryId", Integer.parseInt(empCategoryId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
}