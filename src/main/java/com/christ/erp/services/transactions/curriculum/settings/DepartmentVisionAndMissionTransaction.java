package com.christ.erp.services.transactions.curriculum.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpDepartmentMissionVisionDBO;
import com.christ.erp.services.dto.curriculum.settings.DepartmentMissionVisionDTO;

public class DepartmentVisionAndMissionTransaction {
	public static volatile DepartmentVisionAndMissionTransaction departmentVisionAndMissionTransaction = null;

	public static DepartmentVisionAndMissionTransaction getInstance() {
		if (departmentVisionAndMissionTransaction == null) {
			departmentVisionAndMissionTransaction = new DepartmentVisionAndMissionTransaction();
		}
		return departmentVisionAndMissionTransaction;
	}

	public List<ErpDepartmentMissionVisionDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpDepartmentMissionVisionDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpDepartmentMissionVisionDBO> onRun(EntityManager context) throws Exception {
				String str = "select dbo from ErpDepartmentMissionVisionDBO dbo where dbo.recordStatus='A'";
				Query query = context.createQuery(str);
				List<ErpDepartmentMissionVisionDBO> list = query.getResultList();
				return list;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;	
			}
		});
	}

	public ErpDepartmentMissionVisionDBO edit(int id,Boolean isDepart) {
        try {
            return DBGateway.runJPA(new ISelectGenericTransactional<ErpDepartmentMissionVisionDBO>() {
                @SuppressWarnings("unchecked")
				@Override
                public ErpDepartmentMissionVisionDBO onRun(EntityManager context) throws Exception {
                	Query query= null;                	
                	if(isDepart) {
                		query = context.createQuery("select dbo from ErpDepartmentMissionVisionDBO dbo where dbo.erpDepartmentDBO.id = :id and recordStatus = 'A'");                		
                	} else {
                		query = context.createQuery("select dbo from ErpDepartmentMissionVisionDBO dbo where dbo.id = :id and recordStatus = 'A'");                		
                	}
                	query.setParameter("id", id);
                	ErpDepartmentMissionVisionDBO dbo = (ErpDepartmentMissionVisionDBO)  Utils.getUniqueResult(query.getResultList());
                	return dbo;
                }
                @Override
                public void onError(Exception error) throws Exception {
                    throw error;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	public boolean saveOrUpdate(ErpDepartmentMissionVisionDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (Utils.isNullOrEmpty(dbo) || Utils.isNullOrEmpty(dbo.id)) {
					context.persist(dbo);
				} else {
					context.merge(dbo);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
		
    public List<ErpDepartmentMissionVisionDBO> getDuplicate(DepartmentMissionVisionDTO data) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpDepartmentMissionVisionDBO>>() {
            @SuppressWarnings("unchecked")
			@Override
            public List<ErpDepartmentMissionVisionDBO> onRun(EntityManager context) throws Exception  {
                Query query = context.createNativeQuery("select *  from erp_department_mission_vision "
                		+ "where erp_department_mission_vision_id != :erpMissionVisionId and erp_department_id = :erpDepartmentId and record_status = 'A'");
                query.setParameter("erpMissionVisionId", data.id);
                query.setParameter("erpDepartmentId", data.department.value);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
}
