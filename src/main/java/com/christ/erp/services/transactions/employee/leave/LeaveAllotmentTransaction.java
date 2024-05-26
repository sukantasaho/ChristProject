package com.christ.erp.services.transactions.employee.leave;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDetailsDBO;
import com.christ.erp.services.dbqueries.employee.LeaveQueries;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentDTO;

public class LeaveAllotmentTransaction {

	private static volatile LeaveAllotmentTransaction leaveAllotmentTransaction=null;
	public static LeaveAllotmentTransaction getInstance() {
		if(leaveAllotmentTransaction==null) {
			leaveAllotmentTransaction = new LeaveAllotmentTransaction();
		}
		return leaveAllotmentTransaction;
	}

	public List<Tuple> getGridData() throws Exception  {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery(LeaveQueries.LEAVE_ALLOTMENT_MAPPING_SEARCH_ALL, Tuple.class);
				List<Tuple> mappings = query.getResultList();
				return mappings;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpLeaveCategoryAllotmentDetailsDBO> edit(String id) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<EmpLeaveCategoryAllotmentDetailsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpLeaveCategoryAllotmentDetailsDBO> onRun(EntityManager context) throws Exception {
				List<EmpLeaveCategoryAllotmentDetailsDBO> allotmentMappings = null;
				Query query = context.createQuery("from EmpLeaveCategoryAllotmentDetailsDBO E where E.empLeaveCategoryAllotmentDBO.id =:id ");
				query.setParameter("id", Integer.parseInt(id));
				allotmentMappings = (List<EmpLeaveCategoryAllotmentDetailsDBO>) query.getResultList();
				return allotmentMappings;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean delete(String id, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				var dbo =context.find(EmpLeaveCategoryAllotmentDBO.class, Integer.parseInt(id));				
				if(!Utils.isNullOrEmpty(dbo)) {
					dbo.setRecordStatus('D');
					dbo.setModifiedUsersId(Integer.parseInt(userId));
					dbo.getEmpLeaveCategoryAllotmentDetailsDBO().forEach(s->{
						s.setRecordStatus('D');
						s.setModifiedUsersId(Integer.parseInt(userId));
					});
					context.merge(dbo);
					return true;
				}
				return false;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpLeaveCategoryAllotmentDetailsDBO> duplicateCheck(EmpLeaveAllotmentDTO data) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<EmpLeaveCategoryAllotmentDetailsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpLeaveCategoryAllotmentDetailsDBO> onRun(EntityManager context) throws Exception {
				List<EmpLeaveCategoryAllotmentDetailsDBO> duplicateChecking = null;
				StringBuffer sb = new StringBuffer();
				if(data.leaveCategoryName != null && !data.leaveCategoryName.isEmpty())
					sb.append("SELECT c FROM EmpLeaveCategoryAllotmentDetailsDBO c WHERE c.empLeaveCategoryAllotmentDBO.empLeaveCategoryAllotmentName=:LeaveCategoryName and c.recordStatus='A' ");
				if(data.allomentIds != null && !data.allomentIds.isEmpty())
					sb.append(" and c.id NOT IN (:ids) ");
				Query queryDuplicateCheck = context.createQuery(sb.toString());
				if(data.leaveCategoryName != null && !data.leaveCategoryName.isEmpty())
					queryDuplicateCheck.setParameter("LeaveCategoryName", data.leaveCategoryName);
				if(data.allomentIds != null && !data.allomentIds.isEmpty())
					queryDuplicateCheck.setParameter("ids", data.allomentIds);
				duplicateChecking = (List<EmpLeaveCategoryAllotmentDetailsDBO>) queryDuplicateCheck.getResultList();
				return duplicateChecking;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpLeaveCategoryAllotmentDBO getLeaveCategory(Integer parentCategoryId) throws Exception  {
		return  DBGateway.runJPA(new ISelectGenericTransactional<EmpLeaveCategoryAllotmentDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public EmpLeaveCategoryAllotmentDBO onRun(EntityManager context) throws Exception {
				EmpLeaveCategoryAllotmentDBO dbo = null;
				Query query = context.createQuery("FROM EmpLeaveCategoryAllotmentDBO c WHERE c.empLeaveCategoryAllotmentId=:ID and c.recordStatus ='A' ");
				query.setParameter("ID", (int) parentCategoryId);
				dbo = (EmpLeaveCategoryAllotmentDBO) Utils.getUniqueResult(query.getResultList());
				return dbo; 
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpLeaveCategoryAllotmentDBO getLeaveCategory(String LeaveCategory) throws Exception  {
		return  DBGateway.runJPA(new ISelectGenericTransactional<EmpLeaveCategoryAllotmentDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public EmpLeaveCategoryAllotmentDBO onRun(EntityManager context) throws Exception {
				EmpLeaveCategoryAllotmentDBO dbo = null;
				Query query = context.createQuery("FROM EmpLeaveCategoryAllotmentDBO c WHERE c.empLeaveCategoryAllotmentName=:LeaveCategoryAllotmentName and c.recordStatus ='A' ");
				query.setParameter("LeaveCategoryAllotmentName", LeaveCategory);
				dbo = (EmpLeaveCategoryAllotmentDBO) Utils.getUniqueResult(query.getResultList());
				return dbo; 
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveLeaveCategoryAndAlloted(EmpLeaveCategoryAllotmentDBO dboParentAndChild) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if(Utils.isNullOrEmpty(dboParentAndChild.empLeaveCategoryAllotmentId) ||dboParentAndChild.empLeaveCategoryAllotmentId == 0) {
					context.persist(dboParentAndChild);
				}else {
					context.merge(dboParentAndChild);
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
