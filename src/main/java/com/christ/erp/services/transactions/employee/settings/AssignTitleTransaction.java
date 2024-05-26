package com.christ.erp.services.transactions.employee.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentUserTitleDBO;
import com.christ.erp.services.dto.employee.settings.ErpAssignTitleDTO;
@Repository
public class AssignTitleTransaction {

	private static volatile AssignTitleTransaction assignTitleTransaction = null;
	public static AssignTitleTransaction getInstance() {
		if(assignTitleTransaction == null) {
			assignTitleTransaction = new AssignTitleTransaction();
		}
		return assignTitleTransaction;
		
	}
	public List<ErpCampusDepartmentUserTitleDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCampusDepartmentUserTitleDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpCampusDepartmentUserTitleDBO> onRun(EntityManager context) throws Exception {
				List<ErpCampusDepartmentUserTitleDBO> dboList = null;
					try{
						// bo.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id ASC
						 Query query = context.createQuery("from ErpCampusDepartmentUserTitleDBO bo where bo.recordStatus='A' ORDER BY bo.erpUsersDBO.id ASC");
						 dboList = query.getResultList();
					}catch (Exception e) {
						e.printStackTrace();
					}
				return dboList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
	
	public List<ErpCampusDepartmentUserTitleDBO> getEmpAssignTitle(int userBoId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCampusDepartmentUserTitleDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpCampusDepartmentUserTitleDBO> onRun(EntityManager context) throws Exception {
				List<ErpCampusDepartmentUserTitleDBO> dboList = null;
					try{
						 Query query = context.createQuery("from ErpCampusDepartmentUserTitleDBO bo where bo.erpUsersDBO.id=:UserBoId and bo.recordStatus='A'");
						 query.setParameter("UserBoId", userBoId);
						 dboList = (List<ErpCampusDepartmentUserTitleDBO>) query.getResultList();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				return dboList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
	
	public boolean saveOrUpdate(List<ErpCampusDepartmentUserTitleDBO> dboList) throws Exception {
		 return DBGateway.runJPA(new ICommitTransactional() {
	            @Override
	            public boolean onRun(EntityManager context) throws Exception {
	            	if(dboList.size()>0) {
	            		for (ErpCampusDepartmentUserTitleDBO dbo : dboList) {
		            		if(Utils.isNullOrEmpty(dbo.id)) {
		                        context.persist(dbo);
		                    }
		                    else {
		                        context.merge(dbo);
		                    }	  					
						}    
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
	
	public List<ErpCampusDepartmentUserTitleDBO> duplicateCheck(ErpAssignTitleDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCampusDepartmentUserTitleDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpCampusDepartmentUserTitleDBO> onRun(EntityManager context) throws Exception {
				List<ErpCampusDepartmentUserTitleDBO> dbo = null;
					try{			
						StringBuffer buffer =  new StringBuffer();
						buffer.append("from ErpCampusDepartmentUserTitleDBO bo where bo.recordStatus='A' ");
						if(!Utils.isNullOrEmpty(data.user.id)) {
							buffer.append(" and bo.erpUsersDBO.id=:UserTableId");
						}
						if(!Utils.isNullOrEmpty(data.editIds)) {
							buffer.append(" and id not in(:Ids)");
						}
						Query query = context.createQuery(buffer.toString());
						if(!Utils.isNullOrEmpty(data.user.id)) {
							query.setParameter("UserTableId", (int)Integer.valueOf(data.user.id));
						}
						if(!Utils.isNullOrEmpty(data.editIds)) {
							query.setParameter("Ids", data.editIds);
						}
						dbo = (List<ErpCampusDepartmentUserTitleDBO>) query.getResultList();	
					}catch (Exception e) {
						e.printStackTrace();
					}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
}
