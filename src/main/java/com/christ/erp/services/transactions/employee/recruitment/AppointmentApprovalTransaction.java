package com.christ.erp.services.transactions.employee.recruitment;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;

@Repository
public class AppointmentApprovalTransaction {
	private static volatile AppointmentApprovalTransaction appointmentApprovalTransaction = null;
    public static AppointmentApprovalTransaction getInstance() {
        if(appointmentApprovalTransaction==null) {
        	appointmentApprovalTransaction = new AppointmentApprovalTransaction();
        }
        return appointmentApprovalTransaction;
    }  
    public EmpApplnEntriesDBO getApplicationEntriesDetails(String applicationNumber, String applicantName) throws Exception {
    	return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnEntriesDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public EmpApplnEntriesDBO onRun(EntityManager context) throws Exception {
				EmpApplnEntriesDBO dbo = null;
				if(!Utils.isNullOrEmpty(applicationNumber) || !Utils.isNullOrEmpty(applicantName)) {
					try{
						StringBuffer sf = new StringBuffer();
						sf.append("from EmpApplnEntriesDBO e where e.recordStatus='A' ");
						if(!Utils.isNullOrEmpty(applicationNumber)) {
							sf.append(" and e.applicationNo=:ApplicationNo ");
						}
						if(!Utils.isNullOrEmpty(applicantName)) {
							sf.append(" and e.applicantName=:ApplicantName ");
						}
						Query query  = context.createQuery(sf.toString());
						if(!Utils.isNullOrEmpty(applicationNumber)) {
						    query.setParameter("ApplicationNo", Integer.parseInt(applicationNumber.trim()));
						}
						if(!Utils.isNullOrEmpty(applicantName)) {
							query.setParameter("ApplicantName", applicantName.trim());
						}
						dbo = (EmpApplnEntriesDBO) Utils.getUniqueResult(query.getResultList());
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
    
    public List<EmpDBO> checkIsEmployee(Integer applicationNumber) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpDBO> onRun(EntityManager context) throws Exception {
				List<EmpDBO> dbo = null;
				if(!Utils.isNullOrEmpty(applicationNumber)) {
					try{
					Query query = context.createQuery("from EmpDBO e where e.empApplnEntriesDBO.applicationNo=:ApplicationNumber ");
					query.setParameter("ApplicationNumber", applicationNumber);
					dbo = (List<EmpDBO>) query.getResultList();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
    }
    
    public EmpPayScaleDetailsDBO getEmpPayScaleDetails(Integer applicationNumber) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpPayScaleDetailsDBO>() {
			EmpPayScaleDetailsDBO  dbo = null;
			@SuppressWarnings("unchecked")
			@Override
			public EmpPayScaleDetailsDBO onRun(EntityManager context) throws Exception {
				try {
				    Query query = context.createQuery(" from EmpPayScaleDetailsDBO e where e.empApplnEntriesDBO.applicationNo=:ApplicationNumber and e.recordStatus='A' and e.current=1");
					query.setParameter("ApplicationNumber", applicationNumber);
					dbo = (EmpPayScaleDetailsDBO) Utils.getUniqueResult(query.getResultList());
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
      
    public boolean saveOrUpdateEmployee(EmpDBO empDBO) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                    if(Utils.isNullOrEmpty(empDBO.id)) {
                        context.persist(empDBO);
                    }else {
                        context.merge(empDBO);
                    }	            
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public boolean saveOrUpdatePayScaleDetails(EmpPayScaleDetailsDBO empPayScaleDetailsDBO) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                    if(Utils.isNullOrEmpty(empPayScaleDetailsDBO.id)) {
                        context.persist(empPayScaleDetailsDBO);
                    } else {
                        context.merge(empPayScaleDetailsDBO);
                    }	            
                return true;
            }
            @Override
            
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

	public boolean saveOrUpdatePayScaleDeatials(List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDbos) throws Exception {
		  return DBGateway.runJPA(new ICommitTransactional() {
	            @Override
	            public boolean onRun(EntityManager context) throws Exception {
	            	for (EmpPayScaleDetailsComponentsDBO dbo : empPayScaleDetailsComponentsDbos) {
	            		if(!Utils.isNullOrEmpty(dbo.id)) {
	                        context.merge(dbo);
	                    }else {
	                        context.persist(dbo);
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

	public List<EmpPayScaleDetailsComponentsDBO> getEmpPayScaleDetailsComponentsDBO(Integer id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpPayScaleDetailsComponentsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpPayScaleDetailsComponentsDBO> onRun(EntityManager context) throws Exception {
				List<EmpPayScaleDetailsComponentsDBO> dboList = null;
					try{
						 Query query = context.createQuery("from EmpPayScaleDetailsComponentsDBO e where e.empPayScaleDetailsDBO.id=:Id and e.recordStatus='A' ");
						 query.setParameter("Id", id);
						 dboList = (List<EmpPayScaleDetailsComponentsDBO>) query.getResultList();
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
}
