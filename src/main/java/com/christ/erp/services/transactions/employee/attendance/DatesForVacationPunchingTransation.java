package com.christ.erp.services.transactions.employee.attendance;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpDateForVacationPunchingDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpVacationPunchingDatesCDMapDBO;
import com.christ.erp.services.dbqueries.employee.AttendanceQueries;
import com.christ.erp.services.dto.employee.attendance.EmpDateForVacationPunchingDTO;

public class DatesForVacationPunchingTransation {
	private static volatile DatesForVacationPunchingTransation datesForVacationPunchingTransation = null; 
    public static DatesForVacationPunchingTransation getInstance() {
        if(datesForVacationPunchingTransation==null) {
        	datesForVacationPunchingTransation = new DatesForVacationPunchingTransation();
        }
        return datesForVacationPunchingTransation;
    }
	    
	public List<EmpDateForVacationPunchingDBO> getGridData() throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpDateForVacationPunchingDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpDateForVacationPunchingDBO> onRun(EntityManager context) throws Exception {
				List<EmpDateForVacationPunchingDBO> dbo = null;
					try{
						 Query query = context.createQuery(AttendanceQueries.DATE_FOR_VACATION_PUNCHING_GRID_DATA);
						 dbo = query.getResultList();
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
	
	
	public EmpDateForVacationPunchingDBO getEmpDateForVacationPunchingDTO(Integer id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpDateForVacationPunchingDBO>() {
			@Override
			public  EmpDateForVacationPunchingDBO onRun(EntityManager context) throws Exception {
			    EmpDateForVacationPunchingDBO dbo = null;
				try{
					dbo = context.find(EmpDateForVacationPunchingDBO.class, id);	
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
	
	
	public boolean saveOrUpdate(EmpDateForVacationPunchingDBO dbo) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                    if(Utils.isNullOrEmpty(dbo.id)) {
                        context.persist(dbo);
                    }
                    else {
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

	public List<EmpDateForVacationPunchingDBO> duplicateCheck(EmpDateForVacationPunchingDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpDateForVacationPunchingDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpDateForVacationPunchingDBO> onRun(EntityManager context) throws Exception {
				List<EmpDateForVacationPunchingDBO> dbo = null;
					try{
						Query query = context.createQuery(AttendanceQueries.DATE_FOR_VACATION_PUNCHING_DUPLICATE_CHECK);
				        query.setParameter("ID", !Utils.isNullOrEmpty(data.id) ? Integer.parseInt(data.id.trim()) : 0);
						query.setParameter("EmployeeCategoryID", Integer.parseInt(data.empCategory.id));
						query.setParameter("StartDate", Utils.convertStringDateTimeToLocalDate(data.vacationPunchingStartDate));
						query.setParameter("EndDate", Utils.convertStringDateTimeToLocalDate(data.vacationPunchingEndDate));				
						dbo =  query.getResultList();
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

	public boolean delete(String id, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	EmpDateForVacationPunchingDBO dbo = null;
             	if (id != null && !id.isEmpty()) {
					if (Utils.isNullOrWhitespace(id) == false) {
						 dbo  = context.find(EmpDateForVacationPunchingDBO.class, Integer.parseInt(id));
						if (!Utils.isNullOrEmpty(dbo)) {
							dbo.recordStatus = 'D';
							dbo.modifiedUsersId = Integer.parseInt(userId);	
							if (!Utils.isNullOrEmpty(dbo.cdMapDBOs)) {
								for (EmpVacationPunchingDatesCDMapDBO obj:dbo.cdMapDBOs) {
									if (obj.recordStatus=='A') {
									    obj.recordStatus='D';
									    obj.modifiedUsersId=Integer.parseInt(userId);	
									}
								}
							}
							context.merge(dbo);
						}
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
}
