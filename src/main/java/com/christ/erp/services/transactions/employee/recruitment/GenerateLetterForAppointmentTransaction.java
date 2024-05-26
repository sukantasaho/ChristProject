package com.christ.erp.services.transactions.employee.recruitment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpAppointmentLetterDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

public class GenerateLetterForAppointmentTransaction {
	
	private static volatile GenerateLetterForAppointmentTransaction generateLetterForAppointmentTransaction = null;

    public static GenerateLetterForAppointmentTransaction getInstance() {
        if(generateLetterForAppointmentTransaction==null) {
        	generateLetterForAppointmentTransaction = new GenerateLetterForAppointmentTransaction();
        }
        return generateLetterForAppointmentTransaction; 
    }
	
    public  ErpTemplateDBO getAppointmentLetterTemplateData(String id) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateDBO>() {
            @Override
            public ErpTemplateDBO onRun(EntityManager context) throws Exception {
            	ErpTemplateDBO dberpTemplateDBO = context.find(ErpTemplateDBO.class, Integer.parseInt(id));
                return dberpTemplateDBO;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
     }
	 
	 public  Tuple getEmpDetailsBasedOnEmpId(String empid) throws Exception {
         return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
            @SuppressWarnings("unchecked")
			@Override
            public Tuple onRun(EntityManager context) throws Exception {
            	String str =" select distinct e.emp_id as ID, "
            			+ " e.emp_name as EmployeeName, "
            			+ " e.doj as DateofJoining, "
            			+ " el.emp_designation_name as Designation, "
            			+ " epmd.level_cell_value as Basic, "
            			+ " epgmd.pay_scale as Scale,\r\n"
            			+ " ed.department_name as DepartmentName "
            			+ " from emp e "
            			+ " inner join emp_designation el ON el.emp_designation_id = e.emp_designation_id "
            			+ " inner join erp_campus_department_mapping ecdm ON ecdm.erp_campus_department_mapping_id = e.erp_campus_department_mapping_id "
            			+ " inner join erp_department ed ON ed.erp_department_id = ecdm.erp_department_id "
            			+ " inner join emp_pay_scale_details epd ON epd.emp_id = e.emp_id "
            			+ " inner join emp_pay_scale_matrix_detail epmd ON epmd.emp_pay_scale_matrix_detail_id = epd.emp_pay_scale_matrix_detail_id "
            			+ " inner join emp_pay_scale_grade_mapping_detail epgmd ON epgmd.emp_pay_scale_grade_mapping_detail_id = epmd.emp_pay_scale_grade_mapping_detail_id "
            			+ " where e.emp_no=:EmployeeId ";
        		Query qry =context.createNativeQuery(str, Tuple.class);
            	qry.setParameter("EmployeeId", empid);
            	Tuple employee=(Tuple) Utils.getUniqueResult(qry.getResultList());
                return employee;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
         });
	 }
	 
	 public  EmpAppointmentLetterDBO getEmpAppointmentLetter(String empid) throws Exception {
		 return DBGateway.runJPA(new ISelectGenericTransactional<EmpAppointmentLetterDBO>() {
			 @SuppressWarnings("unchecked")
			@Override
             public EmpAppointmentLetterDBO onRun(EntityManager context) throws Exception {
                 Query query =  context.createQuery("from EmpAppointmentLetterDBO e where e.empDBO.empNumber=:empNumber",EmpAppointmentLetterDBO.class);
                 query.setParameter("empNumber",empid);
                 EmpAppointmentLetterDBO header= (EmpAppointmentLetterDBO) Utils.getUniqueResult(query.getResultList());
                return header;
             }
             @Override
             public void onError(Exception error) throws Exception {
                throw error;
             }
	     });
	 }

	public  Boolean delete(EmpAppointmentLetterDBO empAppointmentLetter) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	if(empAppointmentLetter.empAppointmentLetterId!=null) {
            		context.remove(context.find(EmpAppointmentLetterDBO.class, empAppointmentLetter.empAppointmentLetterId));
            	}
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
	
   public  List<Tuple> getGenerateLetterofAppointmentList(LocalDate joiningFromDate,LocalDate joiningToDate,String location,String campus ) throws Exception {
	   return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
           @SuppressWarnings("unchecked")
		   @Override
           public List<Tuple> onRun(EntityManager context) throws Exception {
        	  StringBuilder sb=new StringBuilder();
              sb.append("select e.emp_id as Id, e.emp_name as EmployeeName,  e.emp_no as EmployeeID, e.doj as JoiningDate, " + 
               			" e1.wait_for_document as DocumentSubmissionStatus, e2.emp_id as GenerationStatus from emp e " + 
               			" left join emp_appointment_letter e2 on e.emp_id = e2.emp_id " + 
               			" inner join emp_document_verification e1 on e1.emp_id=e.emp_id " + 
               			" inner join erp_campus_department_mapping ecdm ON ecdm.erp_campus_department_mapping_id = e.erp_campus_department_mapping_id " + 
               			" inner join erp_campus ec ON ec.erp_campus_id = ecdm.erp_campus_id " + 
               			" inner join erp_location el ON el.erp_location_id = ec.erp_location_id " + 
               			" where e.doj>=:joiningFromDate and e.doj<=:joiningToDate and e1.is_in_draft_mode=false and  e.record_status='A'");
              if(!Utils.isNullOrEmpty(location) && !Utils.isNullOrEmpty(campus)) {
               	  sb.append("and el.location_name=:location and ec.campus_name=:campus");
              }
              Query query = context.createNativeQuery(sb.toString(), Tuple.class);
              query.setParameter("joiningFromDate",joiningFromDate);
              query.setParameter("joiningToDate",joiningToDate);
              if(!Utils.isNullOrEmpty(location) && !Utils.isNullOrEmpty(campus)) {
            	  query.setParameter("location", location);
            	  query.setParameter("campus", campus);	
           	  }
            return query.getResultList();
           }
           @Override
           public void onError(Exception error) throws Exception {
             throw error;
           }
       });
    }

	public  List<Tuple> getGenerateLetterofAppointmentPending(LocalDate date, LocalDate date2, String location,
			String campus) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
	        public List<Tuple> onRun(EntityManager context) throws Exception {
				StringBuilder sb=new StringBuilder();
	        	sb.append("select e.emp_name as EmployeeName,  e.emp_no as EmployeeID, e.doj as JoiningDate, " + 
	        	    		"	e1.wait_for_document as DocumentSubmissionStatus, e2.emp_id as GenerationStatus from emp e " + 
	        	    		"	left join emp_appointment_letter e2 on e.emp_id = e2.emp_id " + 
	        	    		"	inner join emp_document_verification e1 on e1.emp_id=e.emp_id " + 
	        	    		"   inner join erp_campus_department_mapping ecdm ON ecdm.erp_campus_department_mapping_id = e.erp_campus_department_mapping_id " + 
	        				"   inner join erp_campus ec ON ec.erp_campus_id = ecdm.erp_campus_id " + 
	        				"   inner join erp_location el ON el.erp_location_id = ec.erp_location_id " + 
	        	    		"	where e.doj>=:joiningFromDate and e.doj<=:joiningToDate and e1.is_in_draft_mode=false and  e.record_status='A' and e2.emp_id IS NULL ");
	        	if(!Utils.isNullOrEmpty(location) && !Utils.isNullOrEmpty(campus)) {
	        	   	sb.append("and el.location_name=:location and ec.campus_name=:campus");
	        	}
	        	Query query = context.createNativeQuery(sb.toString(), Tuple.class);
	        	query.setParameter("joiningFromDate",date);
	        	query.setParameter("joiningToDate",date2);
	        	if(!Utils.isNullOrEmpty(location) && !Utils.isNullOrEmpty(campus)) {
	        		query.setParameter("location", location);
	        	    query.setParameter("campus", campus);
	        	}
	        	List<Tuple> mappings = query.getResultList();
	        	return mappings;
	        }
			@Override
			public void onError(Exception error) throws Exception {	
			}
		 });
	}

	public Boolean saveEmpAppointmentLetter(EmpAppointmentLetterDBO empAppointmentLetterDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() { 
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                context.persist(empAppointmentLetterDBO);
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	     });
	}
}
