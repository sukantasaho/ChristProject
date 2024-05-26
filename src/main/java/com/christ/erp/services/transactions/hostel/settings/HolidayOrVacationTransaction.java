package com.christ.erp.services.transactions.hostel.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelHolidayEventsDBO;
import com.christ.erp.services.dto.hostel.settings.HostelHolidayEventsDTO;

public class HolidayOrVacationTransaction {
	private static volatile HolidayOrVacationTransaction holidayOrVacationTransaction = null;
    public static HolidayOrVacationTransaction getInstance() {
        if(holidayOrVacationTransaction==null) {
        	holidayOrVacationTransaction = new HolidayOrVacationTransaction();
        }
        return holidayOrVacationTransaction;
    }
    
	public List<HostelHolidayEventsDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelHolidayEventsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<HostelHolidayEventsDBO> onRun(EntityManager context) throws Exception {
				StringBuffer str = new StringBuffer(" from HostelHolidayEventsDBO P where P.recordStatus='A' ");
				Query query = context.createQuery(str.toString());
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public HostelHolidayEventsDBO edit(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelHolidayEventsDBO>() {
			@Override
			public HostelHolidayEventsDBO onRun(EntityManager context) throws Exception {
				HostelHolidayEventsDBO dbo =null;
				try {
				String str = " from HostelHolidayEventsDBO P where P.recordStatus='A' and P.id=:Id ";
				Query query = context.createQuery(str);
				query.setParameter("Id", id);
				dbo = (HostelHolidayEventsDBO) Utils.getUniqueResult(query.getResultList());
				}catch (Exception e) {}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public boolean saveOrUpadte(HostelHolidayEventsDBO dbo) throws Exception {
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

	public boolean delete(String id, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	HostelHolidayEventsDBO dbo = null;
             	if (id != null && !id.isEmpty()) {
					if (Utils.isNullOrWhitespace(id) == false) {
						 dbo  = context.find(HostelHolidayEventsDBO.class, Integer.parseInt(id));
						if (!Utils.isNullOrEmpty(dbo)) {
							dbo.recordStatus = 'D';
							dbo.modifiedUsersId = Integer.parseInt(userId);	
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

	public List<Object[]> getDuplicateCheck(HostelHolidayEventsDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Object[]> onRun(EntityManager context) throws Exception {
				StringBuffer sb = new StringBuffer();
				sb.append(" select hostel_holiday_events_id from hostel_holiday_events where record_status ='A' ");
				if(!Utils.isNullOrEmpty(data.academicYear.id)) 
					sb.append(" and erp_academic_year_id =:erpAcademicYearId ");
				if(!Utils.isNullOrEmpty(data.hostel.id)) 
					sb.append(" and hostel_id =:hostelDBOId ");
				if(!Utils.isNullOrEmpty(data.id)) 
					sb.append(" and hostel_holiday_events_id not in (:Id) ");
				if(!Utils.isNullOrEmpty(data.fromDate)	&& !Utils.isNullOrEmpty(data.toDate)) {
					sb.append(" and ((:StartDate <= holiday_from_date  and holiday_to_date <=:EndDate) " + 
							"or (:StartDate between holiday_from_date and holiday_to_date) " + 
							"or (:EndDate between holiday_to_date and holiday_to_date))  ");
				}
				Query query = context.createNativeQuery(sb.toString());
				if(!Utils.isNullOrEmpty(data.academicYear.id)) 
					query.setParameter("erpAcademicYearId", Integer.parseInt(data.academicYear.id));
				if(!Utils.isNullOrEmpty(data.hostel.id)) 
					query.setParameter("hostelDBOId", Integer.parseInt(data.hostel.id));
				if(!Utils.isNullOrEmpty(data.id)) 
					query.setParameter("Id", Integer.parseInt(data.id));
				if(!Utils.isNullOrEmpty(data.fromDate)	&& !Utils.isNullOrEmpty(data.toDate)) {
					query.setParameter("StartDate",  Utils.convertStringDateTimeToLocalDate(data.fromDate));
					query.setParameter("EndDate",  Utils.convertStringDateTimeToLocalDate(data.toDate));
				}
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
