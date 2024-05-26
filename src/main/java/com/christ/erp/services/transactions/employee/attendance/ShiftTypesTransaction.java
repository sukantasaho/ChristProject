package com.christ.erp.services.transactions.employee.attendance;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.employee.attendance.EmpShiftTypesDBO;

public class ShiftTypesTransaction {
	
	private static volatile ShiftTypesTransaction shiftTypesTransaction = null;

    public static ShiftTypesTransaction getInstance() {
        if(shiftTypesTransaction==null) {
        	shiftTypesTransaction = new ShiftTypesTransaction();
        }
        return  shiftTypesTransaction;
    }
    
    
    public List<EmpShiftTypesDBO> getGridData() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpShiftTypesDBO>>() {
        	@Override
        	public List<EmpShiftTypesDBO> onRun(EntityManager context) throws Exception  {
        		Query query = context.createQuery("select bo from EmpShiftTypesDBO bo where bo.recordStatus='A'  order by bo.id");
        		@SuppressWarnings("unchecked")
				List <EmpShiftTypesDBO> mappings = query.getResultList();
        		return mappings;
        	}         
        	@Override
        	public void onError(Exception error) throws Exception {
        		throw error;
        	}
        });
    }
    
    public boolean saveOrUpdate(EmpShiftTypesDBO empShiftTypesDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (empShiftTypesDBO.id == null) {
					context.persist(empShiftTypesDBO);
				} else {
					context.merge(empShiftTypesDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
	public EmpShiftTypesDBO getEmpShiftType(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpShiftTypesDBO>() {
			@Override
			public EmpShiftTypesDBO onRun(EntityManager context) throws Exception {
				return context.find(EmpShiftTypesDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<EmpShiftTypesDBO> getShiftTypeByShiftName(String shiftName,String id,String campusId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpShiftTypesDBO> >() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpShiftTypesDBO>  onRun(EntityManager context) throws Exception {
				String hql = "from EmpShiftTypesDBO bo where bo.recordStatus='A' and bo.shiftName=:shiftName and bo.erpCampusDBO.id=:campusId";
				if(id != null && !id.isEmpty()) {
					hql += " and id!=:id";
				}
				Query query = context.createQuery(hql);
				query.setParameter("shiftName", shiftName.trim());
				query.setParameter("campusId", Integer.parseInt(campusId));
				if(id != null && !id.isEmpty()) {
					query.setParameter("id", Integer.parseInt(id));
				}
				return  query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<EmpShiftTypesDBO> getShiftTypeByShiftShortName(String shiftShortName,String id,String campusId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpShiftTypesDBO> >() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpShiftTypesDBO>  onRun(EntityManager context) throws Exception {
				String hql = "from EmpShiftTypesDBO bo where bo.recordStatus='A' and bo.shiftShortName=:shiftShortName and bo.erpCampusDBO.id=:campusId";
				if(id != null && !id.isEmpty()) {
					hql += " and id!=:id";
				}
				Query query = context.createQuery(hql);
				query.setParameter("shiftShortName", shiftShortName.trim());
				query.setParameter("campusId", Integer.parseInt(campusId));
				if(id != null && !id.isEmpty()) {
					query.setParameter("id", Integer.parseInt(id));
				}
				return  query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

}
