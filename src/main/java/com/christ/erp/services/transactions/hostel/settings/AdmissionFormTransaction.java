package com.christ.erp.services.transactions.hostel.settings;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDBO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;

public class AdmissionFormTransaction {
	private static volatile AdmissionFormTransaction admissionFormTransaction = null;
	
    public static AdmissionFormTransaction getInstance() {
        if(admissionFormTransaction==null) {
        	admissionFormTransaction = new AdmissionFormTransaction();
        }
        return admissionFormTransaction;
    }

	public HostelApplicationDBO getHostelApplicationData(String acadamicYearId, String hostelApplicationNum, String registerNo, String applicationNo, ApiResult<HostelApplicationDTO> result) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelApplicationDBO>() {
			@Override
	        public HostelApplicationDBO onRun(EntityManager context) throws Exception {
				StringBuffer str = null;
				HostelApplicationDBO dbo = null;
				try{
					str = new StringBuffer("from HostelApplicationDBO bo where bo.recordStatus='A'");
					if(!Utils.isNullOrEmpty(acadamicYearId)) {
						str.append(" and bo.erpAcademicYearDBO.id=:academicYearId");
					}
					if(!Utils.isNullOrEmpty(hostelApplicationNum)) {
						str.append(" and bo.applicationNo=:hostelApplicationNum");
					}else if(!Utils.isNullOrEmpty(applicationNo)) {
						str.append(" and bo.studentApplnEntriesDBO.applicationNo=:applicationNo");
					}else if(!Utils.isNullOrEmpty(registerNo)) {
						str.append(" and bo.studentDBO.registerNo=:registerNo");
					}
				    Query qry = context.createQuery(str.toString());
				    if(!Utils.isNullOrEmpty(acadamicYearId)) {
						qry.setParameter("academicYearId", Integer.parseInt(acadamicYearId.trim()));
					}
				    if(!Utils.isNullOrEmpty(hostelApplicationNum)) {
						qry.setParameter("hostelApplicationNum", Integer.parseInt(hostelApplicationNum.trim()));
					}else if(!Utils.isNullOrEmpty(applicationNo)) {
						qry.setParameter("applicationNo", Integer.parseInt(applicationNo.trim()));
					}else if(!Utils.isNullOrEmpty(registerNo)) {
						qry.setParameter("registerNo",registerNo.trim());
					}
				    dbo = (HostelApplicationDBO) Utils.getUniqueResult(qry.getResultList());
				}
			    catch (NoResultException nre){
			    	result.failureMessage = "No result found";
			    }
				return dbo;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}

	public HostelSeatAvailabilityDBO getHostelSeatesAvailblity(Integer acadamicYearId, Integer hostelId, Integer hostelRoomTypeId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelSeatAvailabilityDBO>() {
			@SuppressWarnings("unchecked")
			@Override
	        public HostelSeatAvailabilityDBO onRun(EntityManager context) throws Exception {
				HostelSeatAvailabilityDBO dbo = null;
				try {
					StringBuffer str = new StringBuffer("from HostelSeatAvailabilityDBO bo left join fetch bo.hostelSeatAvailabilityDetailsDBO cbo where bo.recordStatus='A' and"
							+ " (cbo is null or cbo.recordStatus = 'A') ");
					if(!Utils.isNullOrEmpty(acadamicYearId)) {
						str.append(" and bo.academicYearDBO.id=:acadamicYearId");
					}
					if(!Utils.isNullOrEmpty(hostelRoomTypeId)) {
						str.append(" and bo.hostelDBO.id=:hostelId");
					}
					if(!Utils.isNullOrEmpty(hostelRoomTypeId)) {
						str.append(" and cbo.hostelRoomTypeDBO.id=:hostelRoomTypeId");
					}
				    Query qry = context.createQuery(str.toString());
				    if(!Utils.isNullOrEmpty(acadamicYearId)) {
						qry.setParameter("acadamicYearId", acadamicYearId);
					}
					if(!Utils.isNullOrEmpty(hostelId)) {
						qry.setParameter("hostelId", hostelId);
					}
					if(!Utils.isNullOrEmpty(hostelRoomTypeId)) {
						qry.setParameter("hostelRoomTypeId", hostelRoomTypeId);
					}
				    dbo = (HostelSeatAvailabilityDBO) Utils.getUniqueResult(qry.getResultList());
				}
				catch (Exception e) {
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

	public  List<HostelAdmissionsDBO> DuplicateCheck(HostelApplicationDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelAdmissionsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
	        public List<HostelAdmissionsDBO> onRun(EntityManager context) throws Exception {
				    StringBuffer str = new StringBuffer("from HostelAdmissionsDBO bo where bo.recordStatus='A'");
//					if(!Utils.isNullOrEmpty(data.academicYear.value)) {
//						str.append(" and bo.erpAcademicYearDBO.id=:academicYearId");
//					}
					if(!Utils.isNullOrEmpty(data.hostelApplicationNum)) {
						str.append(" and bo.hostelApplicationDBO.applicationNo =:applicationNo");
					}
					if(!Utils.isNullOrEmpty(data.hostelAdmissionId)) {
						str.append(" and bo.id !=:hostelAdmissionId");
					}
					str.append(" and bo.erpStatusDBO.statusCode!='HOSTEL_CANCELLED'");
				    Query qry = context.createQuery(str.toString());
//				    if(!Utils.isNullOrEmpty(data.academicYear.value)) {
//						qry.setParameter("academicYearId", Integer.parseInt(data.academicYear.value.trim()));
//					}
				    if(!Utils.isNullOrEmpty(data.hostelApplicationNum)) {
						qry.setParameter("applicationNo", data.hostelApplicationNum);
					}
				    if(!Utils.isNullOrEmpty(data.hostelAdmissionId)) {
				    	qry.setParameter("hostelAdmissionId", Integer.parseInt(data.hostelAdmissionId));
					}
				    return qry.getResultList();
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}

	public boolean saveOrUpdate(HostelAdmissionsDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context){
            	boolean flag = false;
                if(Utils.isNullOrEmpty(dbo.id)){
                    context.persist(dbo);
                    flag = true;
                }else{
                	context.merge(dbo);
                	flag = true;
                }
				return flag;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
	
	public HostelAdmissionsDBO getHostelAdmissionData(String acadamicYearId, String hostelApplicationNum,
			String registerNo, String applicationNo) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelAdmissionsDBO>() {
			@SuppressWarnings("unchecked")
			@Override
	        public HostelAdmissionsDBO onRun(EntityManager context) throws Exception {
				StringBuffer str = null;
				HostelAdmissionsDBO dbo = null;
				str = new StringBuffer("from HostelAdmissionsDBO bo where bo.recordStatus='A'  ");
				if(!Utils.isNullOrEmpty(acadamicYearId)) {
					str.append(" and bo.erpAcademicYearDBO.id=:academicYearId");
				}
				if(!Utils.isNullOrEmpty(hostelApplicationNum)) {
					str.append(" and bo.hostelApplicationDBO.applicationNo=:hostelApplicationNum");
				}else if(!Utils.isNullOrEmpty(applicationNo)) {
					str.append(" and bo.studentApplnEntriesDBO.applicationNo=:applicationNo");
				}else if(!Utils.isNullOrEmpty(registerNo)) {
					str.append(" and bo.studentDBO.registerNo=:registerNo and bo.studentDBO.recordStatus='A'");
				}
				str.append(" and bo.erpStatusDBO.statusCode!='HOSTEL_CANCELLED'");
			    Query qry = context.createQuery(str.toString());
			    if(!Utils.isNullOrEmpty(acadamicYearId)) {
					qry.setParameter("academicYearId", Integer.parseInt(acadamicYearId.trim()));
				}
			    if(!Utils.isNullOrEmpty(hostelApplicationNum)) {
					qry.setParameter("hostelApplicationNum", Integer.parseInt(hostelApplicationNum.trim()));
				}else if(!Utils.isNullOrEmpty(applicationNo)) {
					qry.setParameter("applicationNo", Integer.parseInt(applicationNo.trim()));
				}else if(!Utils.isNullOrEmpty(registerNo)) {
					qry.setParameter("registerNo",registerNo.trim());
				}
			    dbo = (HostelAdmissionsDBO) Utils.getUniqueResult(qry.getResultList());
				return dbo;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}

	public HostelAdmissionsDBO getHostelAdmissionDBO(String id) {
		try {
            return DBGateway.runJPA(new ISelectGenericTransactional<HostelAdmissionsDBO>() {
                @Override
                public HostelAdmissionsDBO onRun(EntityManager context) throws Exception {
                	HostelAdmissionsDBO dbo = context.find(HostelAdmissionsDBO.class, Integer.parseInt(id));
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
	
	public HostelSeatAvailabilityDBO getHostelSeatsAvailableDetatils(String academicYearId, String hostelId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelSeatAvailabilityDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public HostelSeatAvailabilityDBO onRun(EntityManager context) throws Exception {
				StringBuffer str = new StringBuffer(" from HostelSeatAvailabilityDBO bo where bo.recordStatus='A'");
				if(!Utils.isNullOrEmpty(hostelId))	{
					str.append("  and bo.hostelDBO.id=:hostelId");
				}
				if(!Utils.isNullOrEmpty(academicYearId))	{
					str.append("  and bo.academicYearDBO.id=:academicYearId");
				}
				Query q = context.createQuery(str.toString());
				if(!Utils.isNullOrEmpty(hostelId))	{
					q.setParameter("hostelId", Integer.parseInt(hostelId));
				}
				if(!Utils.isNullOrEmpty(academicYearId))	{
					q.setParameter("academicYearId", Integer.parseInt(academicYearId));
				}
				return (HostelSeatAvailabilityDBO) Utils.getUniqueResult(q.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public BigInteger getTotalHostelSeatsFilled(Integer academicYearId, Integer hosetId, Integer hostelRoomTypeId) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<BigInteger>() {
			@Override
            public BigInteger onRun(EntityManager context) throws Exception {
				BigInteger mappings = null;
            	if(!Utils.isNullOrEmpty(academicYearId) && !Utils.isNullOrEmpty(hosetId) && !Utils.isNullOrEmpty(hostelRoomTypeId)) {
                	StringBuffer sqlQuery  = new StringBuffer(" select count(*) from hostel_admissions where record_status = 'A'"
                			+ " and hostel_id =:hostelId and erp_academic_year_id=:acadamicYearId and hostel_room_type_id=:roomTypeId"
                			+ " and erp_status_id!= 'HOSTEL_CANCELLED'");
                	//		+ "and is_cancelled is null or is_cancelled=0 ");
					Query query = context.createNativeQuery(sqlQuery.toString());
						query.setParameter("hostelId", hosetId);
						query.setParameter("acadamicYearId", academicYearId);
					query.setParameter("roomTypeId", hostelRoomTypeId);
					mappings = (BigInteger) Utils.getUniqueResult(query.getResultList());
		    	}
            	return mappings;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });	
	}

	public Integer getStatusId(String HOSTEL_CANCELLED) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<Integer>() {
			@Override
            public Integer onRun(EntityManager context) throws Exception {
            	Query query = context.createNativeQuery(" select erp_status_id from erp_status where status_code=:statusCode and record_status = 'A'");
				query.setParameter("statusCode", HOSTEL_CANCELLED);
            	return (Integer) Utils.getUniqueResult(query.getResultList());
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });	
	}
}
