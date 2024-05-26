package com.christ.erp.services.transactions.employee.leave;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveTypeDTO;

public class LeaveTypeTransaction {
	
	private static volatile LeaveTypeTransaction leaveTypeTransaction=null;
	
	public static LeaveTypeTransaction getInstance() {
        if(leaveTypeTransaction==null) {
        	leaveTypeTransaction = new LeaveTypeTransaction();
        }
        return leaveTypeTransaction;
    }

	public  boolean delete(EmpLeaveTypeDTO leaveTypeMapping, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	 EmpLeaveTypeDBO header = null;
                 if(Utils.isNullOrEmpty(leaveTypeMapping.id) == false) {
                     header = context.find(EmpLeaveTypeDBO.class, leaveTypeMapping.id);
                 }
                 if(header != null) {
                     header.recordStatus = 'D';
                     header.modifiedUsersId=Integer.parseInt(userId);
                     if(header.id != 0) {
                         context.merge(header);
                     }
                 }
                return  true ;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	     });
	}

	public  EmpLeaveTypeDBO edit(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpLeaveTypeDBO>() {
            @Override
            public EmpLeaveTypeDBO onRun(EntityManager context) throws Exception {
            	EmpLeaveTypeDBO dbleavetypeMappingInfo = context.find(EmpLeaveTypeDBO.class, id);
                return dbleavetypeMappingInfo;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });
	}

	public  List<Tuple> getGridData() throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
            	String str="select LT.emp_leave_type_id AS `ID`, LT.leave_type_name AS `leaveTypeName` , LT.leave_type_code AS `leaveTypeCode`, LT.is_apply_online  AS `isApplyOnline`, LT.is_leave_exemption AS `Exemption`,LT.is_leave AS `CanAllotLeave` FROM emp_leave_type AS LT where LT.record_status='A'";
            	Query query = context.createNativeQuery(str.toString(), Tuple.class);
                List<Tuple> mappings = query.getResultList();
                return mappings;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });
	}

	public  Boolean duplicateCheckLeaveType(EmpLeaveTypeDTO leaveTypeMapping) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
            @SuppressWarnings("unchecked")
			@Override
            public Boolean onRun(EntityManager context) throws Exception {
            	 Boolean duplicate = false;
                 StringBuffer sb = new StringBuffer();
                 sb.append("SELECT * FROM emp_leave_type where  (leave_type_name=:leavetype or leave_type_code = :leaveTypeCode) and record_status='A'");
                 if(!Utils.isNullOrEmpty(leaveTypeMapping.id)) {
                     sb.append(" and emp_leave_type_id not in (:id) ");
                 }
                 Query q = context.createNativeQuery(sb.toString(), EmpLeaveTypeDBO.class);
                 q.setParameter("leavetype", leaveTypeMapping.leavetype);
                 q.setParameter("leaveTypeCode", leaveTypeMapping.leavecode);
                 if (!Utils.isNullOrEmpty(leaveTypeMapping.id)) {
                     q.setParameter("id", leaveTypeMapping.id);
                 }
                 EmpLeaveTypeDBO leavetype2 = null;
                 leavetype2 = (EmpLeaveTypeDBO) Utils.getUniqueResult(q.getResultList());
                 if(leavetype2 != null) {
                     duplicate = true;
                 }else {
                     duplicate = false;
                 }
                 return duplicate;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}

	public  Boolean saveOrUpdate(EmpLeaveTypeDBO header) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
              if(header.id == 0) {
                  context.persist(header);
              }else {
                  context.merge(header);
              }
              return  true ;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	   });
	}	
}
