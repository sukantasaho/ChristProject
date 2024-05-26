package com.christ.erp.services.transactions.employee.attendance;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpRosterAllotmentDBO;

@SuppressWarnings("unchecked")
public class DutyRosterAllotmentTransaction {

	private static volatile DutyRosterAllotmentTransaction dutyRosterAllotmentTransaction = null;

    public static DutyRosterAllotmentTransaction getInstance() {
        if(dutyRosterAllotmentTransaction==null) {
        	dutyRosterAllotmentTransaction = new DutyRosterAllotmentTransaction();
        }
        return  dutyRosterAllotmentTransaction;
    }

	public List<Tuple> getDutyRosterData(String campusId, LocalDate fromDate, LocalDate toDate) throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                String str = "select emp_roster_allotment.emp_roster_allotment_id as empRosterAllotmentId,emp.emp_id as empId,emp.emp_name as empName,emp_roster_allotment.emp_shift_types_id as empShiftTypesId,emp_roster_allotment.roster_date as rosterDate from emp " + 
                		" inner join emp_job_details ON emp_job_details.emp_job_details_id = emp.emp_job_details_id " + 
                		" inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " + 
                		" inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " + 
                		" left join emp_roster_allotment on emp.emp_id = emp_roster_allotment.emp_id and emp_roster_allotment.record_status = 'A' and emp_roster_allotment.roster_date between :fromDate and :toDate"+
                		" left join emp_shift_types ON emp_shift_types.emp_shift_types_id = emp_roster_allotment.emp_shift_types_id "+
                		" where erp_campus_department_mapping.erp_campus_id=:campusId "+
                		" and emp_job_details.is_duty_roster_applicable=1  and emp.record_status = 'A' and emp_job_details.record_status = 'A'";
                Query query = context.createNativeQuery(str,Tuple.class);
                query.setParameter("campusId", Integer.parseInt(campusId));
               // query.setParameter("fromDate", Utils.getDateFormate(fromDate));
               // query.setParameter("toDate", new Date(toDate));
                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}

	public boolean saveOrUpdate(List<EmpRosterAllotmentDBO> dboList) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	dboList.forEach(dbo -> {
                    if(Utils.isNullOrEmpty(dbo.id) || dbo.id==0) {
                        context.persist(dbo);
                    }else {
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
}
