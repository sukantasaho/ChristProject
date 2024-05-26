package com.christ.erp.services.transactions.common;

import com.christ.erp.services.common.*;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessVenueCityDBO;
import com.christ.erp.services.dbobjects.common.*;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelPublishApplicationDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnCancellationReasonsDBO;
import com.christ.erp.services.dto.common.*;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked"})
@Repository
public class CommonApiTransaction {

    private static volatile CommonApiTransaction commonApiTransaction = null;

    public static CommonApiTransaction getInstance() {
        if(commonApiTransaction==null) {
            commonApiTransaction = new CommonApiTransaction();
        }
        return commonApiTransaction;
    }
    
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
    
    public ApiResult<List<LookupItemDTO>> getUsers() throws Exception {
		ApiResult<List<LookupItemDTO>> users = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Utils.getDropdownData(users, context, "select erp_users_id as ID,user_name as Text from erp_users where record_status='A'", null);
				return users;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public List<Tuple> getMotherTongue() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select e.erp_mother_tounge_id as id, e.mother_tounge_name as text from erp_mother_tounge e where e.record_status='A' order by e.mother_tounge_name asc", Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public List<Tuple> getOccupation() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select e.erp_occupation_id as id, e.occupation_name as text from erp_occupation e where e.record_status='A' order by e.occupation_name asc", Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public List<Tuple> getSports() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select e.erp_sports_id as id, e.sports_name as text from erp_sports e where e.record_status='A' order by e.sports_name asc", Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public List<Tuple> getSportsLevel() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select e.erp_sports_level_id as id, e.sports_level_name as text from erp_sports_level e where e.record_status='A' order by e.sports_level_name asc", Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public List<Tuple> getExtraCurricular() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select e.erp_extra_curricular_id as id, e.extra_curricular_name as text from erp_extra_curricular e where e.record_status='A' order by e.extra_curricular_name asc", Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public List<Tuple> getSalutations() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select e.erp_salutation_id as id, e.erp_salutation_name as text from erp_salutation e where e.record_status='A' order by e.erp_salutation_name asc", Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public List<Tuple> getUniversityBoard() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select e.erp_university_board_id as id, e.university_board_name as text, e.board_type as boardType from erp_university_board e where e.record_status='A' order by e.university_board_name asc", Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public List<Tuple> getInstitutionReference() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("select e.erp_institution_reference_id as id, e.institution_reference_name as text from erp_institution_reference e where e.record_status='A' order by e.institution_reference_name asc", Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public List<Tuple> getDepartmentbyLocation(String locationId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_DEPARTMENT_BY_LOCATION = "select distinct erp_department.erp_department_id as ID, erp_department.department_name as Text" + 
			       		" from erp_department" + 
			       		" inner join erp_campus_department_mapping on erp_campus_department_mapping.erp_department_id = erp_department.erp_department_id" + 
			       		" inner join erp_campus on erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id" + 
			       		" inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id" + 
			       		" where erp_location.erp_location_id=:locationId and erp_department.record_status='A'" + 
			       		" order by erp_department.department_name";
				Query query = context.createNativeQuery(SELECT_DEPARTMENT_BY_LOCATION, Tuple.class);				
				query.setParameter("locationId", locationId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public <T> T find(Class<T> className, Integer primaryKey) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<T>() {
			@Override
			public T onRun(EntityManager context) throws Exception {
				T obj = context.find(className, primaryKey);
				return obj;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Tuple getErpWorkFlowProcessIdbyProcessCode(String processCode) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String queryString = "select w.erp_work_flow_process_id as erp_work_flow_process_id, w.applicant_status_display_text as applicant_status_display_text, w.application_status_display_text as application_status_display_text "
						+ " from erp_work_flow_process w where w.process_code= :processCode and w.record_status='A' ";
				Query query = context.createNativeQuery(queryString, Tuple.class);
				query.setParameter("processCode", processCode);
				return (Tuple) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public boolean saveErpWorkFlowProcessStatusLogDBO(ErpWorkFlowProcessStatusLogDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (!Utils.isNullOrEmpty(dbo)) {
					context.persist(dbo);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	

	public Integer getCurrentAcademicYear() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Integer>() {
			@Override
			public Integer onRun(EntityManager context) throws Exception {
				Query currentYearQuery = context.createQuery("select bo.academicYear from ErpAcademicYearDBO bo where bo.recordStatus='A' "
						+ " and  bo.isCurrentAcademicYear=1");
				return (Integer) Utils.getUniqueResult(currentYearQuery.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ApiResult<List<LookupItemDTO>> getCampus(List<Integer> str) throws Exception {
		ApiResult<List<LookupItemDTO>> campus = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				if(Utils.isNullOrEmpty(str)){
					Utils.getDropdownData(campus, context, "select erp_campus_id as 'ID',campus_name as 'Text'   from erp_campus where record_status='A' order by campus_name", null);
				}else {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("str", str);
					String queryString = " select erp_campus_id as 'ID',campus_name as 'Text'   from erp_campus where record_status='A' and erp_campus_id IN (:str) order by campus_name ";
					Query query = context.createNativeQuery(queryString, Tuple.class);
					query.setParameter("str", str);
					Utils.getDropdownData(campus,context,queryString,args);
				}
				return campus;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	public List<LookupItemDTO> getEmployeesOrUsers() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<LookupItemDTO>>() {
			@Override
			public List<LookupItemDTO> onRun(EntityManager context) throws Exception {
				Query query = context.createQuery( "select new com.christ.erp.services.dto.common.LookupItemDTO"
						+ " (cast(bo.id as java.lang.String), CASE WHEN bo.empDBO is NULL THEN bo.userName ELSE bo.empDBO.empName END AS label)"
						+ " from ErpUsersDBO bo"
						+ " left join bo.empDBO"
						+ " where bo.recordStatus='A' order by bo.id");
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<ErpDepartmentCategoryDBO> getDepartmentCategory()throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpDepartmentCategoryDBO>>() {
			@Override
			public List<ErpDepartmentCategoryDBO> onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from ErpDepartmentCategoryDBO where recordStatus='A'");
				return query.getResultList() ;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public ApiResult<List<LookupItemDTO>> getDeanery() throws Exception {
		ApiResult<List<LookupItemDTO>> deanery = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Utils.getDropdownData(deanery, context,
						"select erp_deanery_id as ID,deanery_name as Text from erp_deanery where record_status='A'",
						null);
				return deanery;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	/* no longer needed erp_module is dropping from DB
	public Tuple getDisplayOrder(String moduleId, String processId) throws Exception {
		return (Tuple) DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
//				Utils.getDropdownData(deanery, context,
//						"select erp_deanery_id as ID,deanery_name as Text from erp_deanery where record_status='A'",
//						null);
				 String s=" select e.menu_screen_display_order as DisplayOrder from erp_menu_screen e inner join erp_module_sub ems ON ems.erp_module_sub_id = e.erp_module_sub_id " + 
	               	   		" inner join erp_module em on em.erp_module_id =ems.erp_module_id where e.erp_module_sub_id=:processId and ems.erp_module_id =:moduleId  and e.record_status='A'  ORDER BY e.menu_screen_display_order DESC limit 1";
	               	 //  Utils.getDropdownData(location, context, s, args);
	             Query query = context.createNativeQuery(s, Tuple.class);
	             query.setParameter("moduleId", Integer.parseInt(moduleId));
	             query.setParameter("processId", Integer.parseInt(processId));
	             Tuple  mappings = (Tuple) Utils.getUniqueResult(query.getResultList());
				return mappings;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	*/
	public ApiResult<List<LookupItemDTO>> getCampuses() throws Exception {
		ApiResult<List<LookupItemDTO>> campus = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Utils.getDropdownData(campus, context, "select erp_campus_id as 'ID',REPLACE(campus_name,'Campus','')  as 'Text'   from erp_campus where record_status='A' order by campus_name", null);
				return campus;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getOfferLetterTemplate(Integer employeeCategoryId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				 String str=" select e.erp_template_id as ID, e.template_name as Text,e.emp_employee_category_id as categoryId, "
					  	  + " e2.template_group_code as templateGroupCode,e.template_content as templateContent"
					  	  + " from erp_template e "
		           	      + " inner join erp_template_group e2 on e.erp_template_group_id = e2.erp_template_group_id "
		           	      + " where e2.template_group_code=\"EMP_OFFER_LETTER\" and e.emp_employee_category_id=:pEmployeeCategoryId and e2.record_status='A' and e.record_status='A'"; 
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("pEmployeeCategoryId", employeeCategoryId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public ErpWorkFlowProcessNotificationsDBO getErpWorkFlowProcessNotificationsByWorkFlowProcessId(Integer workFlowProcessId, String notificationCode) throws Exception {
    	return DBGateway.runJPA(new ISelectGenericTransactional<ErpWorkFlowProcessNotificationsDBO>() {
			@Override
			public ErpWorkFlowProcessNotificationsDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery(" from ErpWorkFlowProcessNotificationsDBO bo where bo.recordStatus='A' and bo.erpWorkFlowProcessDBO.id=:workFlowProcessId and bo.notificationCode like :notificationCode",ErpWorkFlowProcessNotificationsDBO.class);
				query.setParameter("workFlowProcessId",workFlowProcessId);
				query.setParameter("notificationCode",notificationCode);
				return (ErpWorkFlowProcessNotificationsDBO) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}

	public List<ErpNotificationUserPrefernceDBO> getErpNotificationUserPreferenceByErpUserId(Set<Integer> userIds,Integer erpWorkFlowProcessNotificationId) throws Exception {
    	return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpNotificationUserPrefernceDBO>> () {

			@Override
			public List<ErpNotificationUserPrefernceDBO> onRun(EntityManager context) throws Exception {
				Query query = context.createQuery(" from ErpNotificationUserPrefernceDBO bo where bo.recordStatus='A' and " +
						" bo.erpWorkFlowProcessNotificationsDBO.id=:erpWorkFlowProcessNotificationId and bo.erpUsersDBO.id in (:userIds)",ErpNotificationUserPrefernceDBO.class);
				query.setParameter("userIds", userIds);
				query.setParameter("erpWorkFlowProcessNotificationId", erpWorkFlowProcessNotificationId);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}

	public boolean sendBulkNotification(List<ErpNotificationsDBO> notificationList) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				notificationList.forEach(dbo -> {
					context.persist(dbo);
				});
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}

	public boolean sendBulkSms(List<ErpSmsDBO> smsList) throws Exception {
    	return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				smsList.forEach(dbo -> {
					context.persist(dbo);
				});
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}

	public boolean sendBulkEmail(List<ErpEmailsDBO> emailList) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				emailList.forEach(dbo -> {
					context.persist(dbo);
				});
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}
	
	public List<Tuple> getProgramme() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select erp_programme_id as 'ID',programme_name as 'Text'  from erp_programme where record_status='A' order by programme_name";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
 
	public List<Tuple> getPrerequisiteName() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select adm_prerequisite_exam_id as 'ID',exam_name as 'Text' from adm_prerequisite_exam where record_status='A' order by exam_name";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getBlockByCampus(String campusId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_BLOCK = "select distinct erp_block.erp_block_id as ID, erp_block.block_name as Text" + 
			       		" from erp_block" + 
			       		" where erp_block.erp_campus_id=:campusId and erp_block.record_status='A'" + 
			       		" order by erp_block_id";
				Query query = context.createNativeQuery(SELECT_BLOCK, Tuple.class);				
				query.setParameter("campusId", campusId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getRoomsByBlock(String blockId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_ROOMS = "select distinct erp_rooms_id as ID,room_no as Text" + 
			       		" from erp_rooms" + 
			       		" where erp_block_id=:blockId and record_status='A'" + 
			       		" order by erp_rooms_id";
				Query query = context.createNativeQuery(SELECT_ROOMS, Tuple.class);				
				query.setParameter("blockId", blockId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getFloorsByBlock(String blockId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_ROOMS = "select distinct erp_floors_id as ID,floor_no as Text" + 
			       		" from erp_floors" + 
			       		" where erp_block_id=:blockId and record_status='A'" + 
			       		" order by erp_floors_id";
				Query query = context.createNativeQuery(SELECT_ROOMS, Tuple.class);				
				query.setParameter("blockId", blockId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public Tuple getAreaAndDistrict(String pincode) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String SELECT_AREA_DISTRICT = "select block,district,erp_pincode_id,erp_city.erp_city_id as erp_city_id,erp_city.city_name as city_name,erp_city.erp_state_id as erp_state_id,erp_state.state_name as state_name\r\n"
						+ "from erp_pincode\r\n"
						+ "inner join erp_city ON erp_city.erp_city_id = erp_pincode.erp_city_id and erp_city.record_status= 'A' \r\n"
						+ "inner join erp_state ON erp_city.erp_state_id = erp_state.erp_state_id and erp_state.record_status= 'A' \r\n"
						+ "where pincode=:pincode and erp_pincode.record_status='A' ";
				Query query = context.createNativeQuery(SELECT_AREA_DISTRICT, Tuple.class);				
				query.setParameter("pincode", pincode);
				return (Tuple) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getProgramPreference(Integer yearValue ) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str =  " select distinct erp_campus_programme_mapping.erp_campus_programme_mapping_id as MappingId, " +
						" erp_campus_programme_mapping.erp_campus_id as CampusID,  erp_campus.campus_name as CampusName,   " + 
						" erp_campus_programme_mapping.erp_location_id as LocID, erp_location.location_name as LocName,   " + 
						" erp_programme.erp_programme_id as ProgramID, erp_programme.programme_name_for_application as ProgramName, adm_programme_settings.preference_option as ProgramOption" +
						" from adm_programme_settings   " + 
						" inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id = adm_programme_settings.erp_programme_id   " + 
						" and if(adm_programme_settings.preference_option='C', erp_campus_programme_mapping.erp_campus_id is not null,   " + 
						" erp_campus_programme_mapping.erp_location_id is not null)          " + 
						" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id   " + 
						" left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id             " + 
						" inner join erp_programme ON erp_programme.erp_programme_id = adm_programme_settings.erp_programme_id   " +
						" where adm_programme_settings.record_status='A' and erp_campus_programme_mapping.record_status='A' "+
						" and :yearValue >= programme_commence_year and (:yearValue < programme_inactivated_year or programme_inactivated_year = 0 or programme_inactivated_year is null) "+
						" order by  ProgramName  asc , CampusName asc ";
				Query query = context.createNativeQuery(str, Tuple.class).setParameter("yearValue", yearValue);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});}
	
	public List<Tuple> getBlocks() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_BLOCK = "select  erp_block_id as ID,block_name as Text from erp_block where record_status='A' ";
				Query query = context.createNativeQuery(SELECT_BLOCK, Tuple.class);				
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getPaymentMode() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select acc_fee_payment_mode_id as 'ID',payment_mode as 'Text' from acc_fee_payment_mode where record_status='A' order by payment_mode";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getSelectionProcessType() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select bo.adm_selection_process_type_id as 'ID', bo.selection_stage_name as 'Text', bo.mode as 'Mode', bo.is_shortlist_after_this_stage as 'ShortList' " + 
						" from adm_selection_process_type bo where bo.record_status = 'A' order by bo.selection_stage_name asc";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<AdmSelectionProcessVenueCityDBO> getVenueCityList() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<AdmSelectionProcessVenueCityDBO>>() {
			@Override
			public List<AdmSelectionProcessVenueCityDBO> onRun(EntityManager context) throws Exception {
				String str = " from AdmSelectionProcessVenueCityDBO bo where bo.recordStatus='A' order by bo.erpStateDBO.stateName asc, bo.venueName asc ";
				Query query = context.createQuery(str);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getTemplateNamesforaGroup(String groupCode) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select erp_template.erp_template_id as ID,erp_template.template_name as Text from erp_template inner join erp_template_group" + 
						" on erp_template.erp_template_group_id = erp_template_group.erp_template_group_id" + 
						" where erp_template_group.template_group_code =:groupCode" + 
						" and erp_template.record_status = 'A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("groupCode", groupCode);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getAllAccAccount() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_Query = "select acc_accounts.acc_accounts_id as id, acc_accounts.account_no as label,acc_accounts.account_name as name " +
						" from acc_accounts where acc_accounts.record_status = 'A'";
				Query query = context.createNativeQuery(SELECT_Query, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ErpAcademicYearDBO getCurrentAcademicYearDBO() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpAcademicYearDBO>() {
			@Override
			public ErpAcademicYearDBO onRun(EntityManager context) throws Exception {
				Query currentYearQuery = context.createQuery(" from ErpAcademicYearDBO bo where bo.recordStatus='A' "
						+ " and  bo.isCurrentAcademicYear=1");
				return (ErpAcademicYearDBO) Utils.getUniqueResult(currentYearQuery.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getUserRoleMapCampusByUserId(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str ="select sys_user_role_map.erp_users_id,sys_user_role_map.erp_campus_id, erp_users_campus.erp_users_campus_id," +
						" erp_users_campus.erp_campus_id as preferredCampusId,erp_users_campus.record_status," +
						" erp_campus.short_name" +
						" from sys_user_role_map" +
						" inner join erp_campus ON erp_campus.erp_campus_id = sys_user_role_map.erp_campus_id" +
						" left join erp_users_campus on sys_user_role_map.erp_users_id = erp_users_campus.erp_users_id and sys_user_role_map.erp_campus_id = erp_users_campus.erp_campus_id" +
						" where sys_user_role_map.record_status ='A' and erp_campus.record_status = 'A' and sys_user_role_map.erp_users_id =:userId";
				Query qry = context.createNativeQuery(str, Tuple.class);
				qry.setParameter("userId",Integer.parseInt(id));
				return qry.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw  error;
			}
		});
	}

	/*public List<ErpUsersCampusDBO> getUserCampusPreferenceByUserId(String userId) throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpUsersCampusDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpUsersCampusDBO> onRun(EntityManager context) throws Exception {
				String str ="from ErpUsersPreferredCampusDBO where ErpUsersDBO.id = :userId" ;
				Query qry = context.createQuery(str, ErpUsersCampusDBO.class);
				qry.setParameter("userId",Integer.parseInt(userId));
				return qry.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw  error;
			}
		});
	}*/

	/*public Boolean savePreferredCampus(List<ErpUsersCampusDBO> campusList, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				for (ErpUsersCampusDBO campusObj : campusList){
					if(Utils.isNullOrEmpty(campusObj.id)) {
						context.persist(campusObj);
					}
					else {
						context.merge(campusObj);
					}
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}*/
	

	public List<Tuple> getSysRoleGroup() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_Query = "select s.sys_role_group_id as ID, s.role_group_name as Name from sys_role_group s where s.record_status = 'A'";
				Query query = context.createNativeQuery(SELECT_Query, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getRoles() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_Query = "select s.sys_role_id as ID, s.role_name  as Name from sys_role s where s.record_status = 'A' order by role_name asc";
				Query query = context.createNativeQuery(SELECT_Query, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<ErpUsersDBO> getUserAndRoles() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpUsersDBO>>() {
			@Override
			public List<ErpUsersDBO> onRun(EntityManager context) {
				List<ErpUsersDBO> usersDBOsList = null;
				Query query = context.createQuery("from ErpUsersDBO bo where bo.recordStatus='A' or bo.recordStatus='I' order by bo.userName asc ");
				usersDBOsList = query.getResultList();
				return usersDBOsList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<ErpUsersDTO> getEmployeesAndUsers(String employeeId, boolean isActive) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpUsersDTO>>() {
			@Override
			public List<ErpUsersDTO> onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("select new com.christ.erp.services.dto.common.ErpUsersDTO"
						+ " (bo.userName ,bo.id ,bo.empDBO.empName ,bo.empDBO.id)"
						+ " from ErpUsersDBO bo"
						+ " where bo.recordStatus=:isActive and bo.empDBO.id=:EmployeeId ");
				query.setParameter("EmployeeId", Integer.parseInt(employeeId.trim()));
				if(!Utils.isNullOrEmpty(isActive)) {
					if(isActive) {
						query.setParameter("isActive", 'A');
					}
					else if(!isActive) {
						query.setParameter("isActive", 'I');
					}
				}
				return (List<ErpUsersDTO>) query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<ErpCampusDBO> getCampusDetails() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCampusDBO>>() {
			@Override
			public List<ErpCampusDBO> onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from ErpCampusDBO bo where bo.recordStatus='A' order by bo.id asc ");
				return (List<ErpCampusDBO>) query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public ApiResult<List<LookupItemDTO>> getEmployees() throws Exception {
		ApiResult<List<LookupItemDTO>> employee = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Utils.getDropdownData(employee, context, "select e.emp_id as ID, if(e.emp_no is null,e.emp_name,concat(e.emp_name,'(',e.emp_no,')'))  as Text from emp e where e.record_status='A' order by e.emp_name", null);
				return employee;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getUserRoleAndCampusDetails() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select sys_user_role_map.erp_users_id,sys_user_role_map.erp_campus_id,sys_user_role_map.sys_role_id from sys_user_role_map where record_status = 'A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}

	public List<Tuple> getRoleAccessTokenDetails() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "  select sys_role_function_map.sys_role_id, sys_function.sys_function_id, sys_function.access_token  from sys_role_function_map" +
						" inner join sys_function ON sys_function.sys_function_id = sys_role_function_map.sys_function_id" +
						" where sys_role_function_map.record_status = 'A' and sys_function.record_status = 'A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}

	public List<Tuple> getUserOverrideFunctionDetails() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select sys_user_function_override.erp_users_id,sys_user_function_override.erp_campus_id," +
						" sys_user_function_override.sys_function_id,sys_user_function_override.is_allowed,sys_function.access_token from sys_user_function_override" +
						" inner join sys_function ON sys_function.sys_function_id = sys_user_function_override.sys_function_id" +
						" where sys_user_function_override.record_status='A' and sys_function.record_status = 'A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}

	public Integer getEmployeesByUserId(String userId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Integer>() {
			@Override
			public Integer onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery("select emp.emp_id from erp_users emp where emp.erp_users_id=:userId");
				query.setParameter("userId",userId);
				return (Integer) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
			}
		});
	}
	
	public List<Tuple> getCampusProgrammeMapping() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str =  " select erp_campus_programme_mapping.erp_campus_programme_mapping_id as MappingId,  " + 
						" erp_programme.erp_programme_id as ProgramId, erp_programme.programme_name as ProgrameName,  " + 
						" erp_location.erp_location_id as LocId, erp_location.location_name as LocName,   " + 
						" erp_campus.erp_campus_id as CampusId, erp_campus.campus_name as CampusName,  " + 
						" concat(erp_programme.programme_name, ' (', ifnull(erp_campus.campus_name, erp_location.location_name), ')') as CombinedName  " + 
						" from erp_campus_programme_mapping  " + 
						" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id  " + 
						" left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id  " + 
						" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id  " + 
						" where erp_campus_programme_mapping.record_status='A'  " + 
						" order by erp_programme.programme_name, erp_location.location_name, erp_campus.campus_name ";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public Mono<List<Tuple>> getStudentApplicationNumbers1(String applicationNumber, String yearId) {
		String query = "";
		if(!Utils.isNullOrEmpty(applicationNumber))
			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.application_no as applicationNo from student_appln_entries e where e.application_no like :applicationNumber "
				   +" and e.applied_academic_year_id =:yearId and e.record_status='A' and e.application_no is not null";
		else
			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.application_no as applicationNo from student_appln_entries e "
				   +" where e.applied_academic_year_id =:yearId and e.record_status='A' and e.application_no is not null";
		String finalQuery = query;
		if(!Utils.isNullOrEmpty(applicationNumber)){
			return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(finalQuery, Tuple.class)
					.setParameter("applicationNumber", applicationNumber+"%").setParameter("yearId", yearId)
					.getResultList()).subscribeAsCompletionStage());
		}else{
			return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(finalQuery, Tuple.class)
					.setParameter("yearId", yearId)
					.getResultList()).subscribeAsCompletionStage());
		}
	}
	
	public Mono<List<ErpProgrammeLevelDBO>> getProgrammeLevel() {
	      String str = "select bo from ErpProgrammeLevelDBO bo where bo.recordStatus = 'A'";
	      return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpProgrammeLevelDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpProgrammeDegreeDBO>> getProgrammeDegreeByLevel(int id) {
	    String str = "select bo from ErpProgrammeDegreeDBO bo where bo.recordStatus = 'A' and bo.erpProgrammeLevelDBO.id=:erpProgrammeLevelId";
	    return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpProgrammeDegreeDBO.class).setParameter("erpProgrammeLevelId", id).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpProgrammeDBO>> getProgrammeByDegreeAndLevel(int degreeId) {
		/*  programme level removed from programme table */
	  //  String str = "select bo from ErpProgrammeDBO bo where bo.recordStatus = 'A' and bo.erpProgrammeDegreeDBO.id=:erpProgrammeDegreeId and bo.erpProgrammeLevelDBO.id=:erpProgrammeLevelId"; 
	    String str = "select bo from ErpProgrammeDBO bo where bo.recordStatus = 'A' and bo.erpProgrammeDegreeDBO.id=:erpProgrammeDegreeId";
	    return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpProgrammeDBO.class)
	    		.setParameter("erpProgrammeDegreeId", degreeId)
	    		.getResultList()).subscribeAsCompletionStage());	
	}
	
	public Mono<List<ErpReservationCategoryDBO>> getErpReservationCategory() {
		 String str = "select bo from ErpReservationCategoryDBO bo where bo.recordStatus = 'A'";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpReservationCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<ErpInstitutionDBO>> getErpInstitution() {
		 String str = "select bo from ErpInstitutionDBO bo where bo.recordStatus = 'A'";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpInstitutionDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpResidentCategoryDBO>> getErpResidentCategory() {
		 String str = "select bo from ErpResidentCategoryDBO bo where bo.recordStatus = 'A'";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpResidentCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Object[]>> getProgramPreferenceByProgram(int programId) {
		 String str ="select erp_campus_programme_mapping.erp_campus_programme_mapping_id as MappingId,   " + 
		 		"	 erp_campus_programme_mapping.erp_campus_id as CampusID,  erp_campus.campus_name as CampusName,     " + 
		 		"	 erp_campus_programme_mapping.erp_location_id as LocID, erp_location.location_name as LocName,     " + 
		 		"	 adm_programme_settings.preference_option as ProgramOption,  "+
		 		"    erp_programme.erp_programme_id as ProgramID, erp_programme.programme_name as ProgramName" + 
		 		"	 from adm_programme_settings     " + 
		 		"	 inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id = adm_programme_settings.erp_programme_id     " + 
		 		"	 and if(adm_programme_settings.preference_option='C', erp_campus_programme_mapping.erp_campus_id is not null,     " + 
		 		"	 erp_campus_programme_mapping.erp_location_id is not null)            " + 
		 		"	 left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id     " + 
		 		"	 left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id               " + 
		 		"	 inner join erp_programme ON erp_programme.erp_programme_id = adm_programme_settings.erp_programme_id     " + 
		 		"	 where adm_programme_settings.record_status='A' and erp_campus_programme_mapping.record_status='A'" + 
		 		"    and erp_programme.erp_programme_id = :programId" + 
		 		"    order by  ProgramName  asc , CampusName asc  ";
		     return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Object[].class)
		    		 .setParameter("programId", programId)
		    		 .getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpAcademicYearDBO>> getAcademicYear() {
		 String str = "select bo from ErpAcademicYearDBO bo where bo.recordStatus = 'A' ORDER BY bo.academicYear desc";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpAcademicYearDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public ErpAcademicYearDBO getCurrentAcademicYearNew() {
		String query = "from ErpAcademicYearDBO bo where bo.recordStatus='A' and  bo.isCurrentAcademicYear=1";
		return sessionFactory.withSession(s->s.createQuery(query,ErpAcademicYearDBO.class).getSingleResultOrNull()).await().indefinitely();
	}
	//public ErpWorkFlowProcessNotificationsDBO getErpWorkFlowProcessNotificationbyNotificationCode(String code) {
		// String query = "select bo from ErpWorkFlowProcessNotificationsDBO bo where bo.recordStatus = 'A' and bo.notificationCode=':notificationCode'";
		 // return  sessionFactory.withSession(s->s.createQuery(query,ErpWorkFlowProcessNotificationsDBO.class).setParameter("notificationCode", code).getSingleResultOrNull()).await().indefinitely();
	//}
	
	public ErpWorkFlowProcessNotificationsDBO getErpWorkFlowProcessNotificationbyNotificationCode(String code) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpWorkFlowProcessNotificationsDBO>() {
			@Override
			public ErpWorkFlowProcessNotificationsDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("select bo from ErpWorkFlowProcessNotificationsDBO bo where bo.recordStatus = 'A' and bo.notificationCode=:notificationCode");
				query.setParameter("notificationCode",code);
				return (ErpWorkFlowProcessNotificationsDBO) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Tuple getApproversIdByEmployeeId(int empId)  {
		String str = "  select erp_users.erp_users_id as usersId,emp.emp_university_email as personalEmailId ,emp.emp_id as emp_id from erp_users  "
				+ "	inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A'  "
				+ "	inner join emp_approvers ON emp_approvers.leave_approver_id = emp.emp_id and emp_approvers.record_status='A'  "
				+ "	where emp_approvers.emp_id=:empId and erp_users.record_status='A' ";
		Tuple list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("empId", empId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return list;
	
	}

//	public ErpTemplateDBO getErpTemplateByTemplateCodeAndTemplateType(String templateType, String templateCode) {
//		  String str = "select bo from ErpTemplateDBO bo where bo.recordStatus = 'A' and bo.templateType=:templateType and bo.templateCode=:templateCode";
//		    return (ErpTemplateDBO) sessionFactory.withSession(s->s.createQuery(str, ErpTemplateDBO.class)
//		    		.setParameter("templateType", templateType)
//		    		.setParameter("templateCode", templateCode)
//		    		.getResultList()).await().indefinitely();	
//	}
	
	
	public ErpTemplateDBO getErpTemplateByTemplateCodeAndTemplateType(String templateType, String templateCode) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateDBO>() {
			@Override
			public ErpTemplateDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("select bo from ErpTemplateDBO bo where bo.recordStatus = 'A' and bo.templateType=:templateType and bo.templateCode=:templateCode");
				query.setParameter("templateType", templateType);
				query.setParameter("templateCode", templateCode);
				return (ErpTemplateDBO) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public Tuple getErpWorkFlowProcessIdbyProcessCode1(String processCode) {
		String str = "select w.erp_work_flow_process_id as erp_work_flow_process_id, w.applicant_status_display_text as applicant_status_display_text, w.application_status_display_text as application_status_display_text,"
				+" w.process_order as processOrder from erp_work_flow_process w where w.process_code= :processCode and w.record_status='A' ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("processCode", processCode).getSingleResultOrNull()).await().indefinitely();
	}
	
	public void saveErpWorkFlowProcessStatusLogDBO1(List<ErpWorkFlowProcessStatusLogDBO> dbo) {
	 sessionFactory.withTransaction((session, tx) -> session.persistAll(dbo.toArray())).subscribeAsCompletionStage();
	}	
	
    public boolean sendBulkNotification1(List<ErpNotificationsDBO> dbo)  {
    sessionFactory.withTransaction((session, tx) -> session.persistAll(dbo.toArray())).subscribeAsCompletionStage();
    return true;
	}

	public boolean sendBulkSms1(List<ErpSmsDBO> dbo) {
    sessionFactory.withTransaction((session, tx) -> session.persistAll(dbo.toArray())).subscribeAsCompletionStage();
    return true;
	}

	public boolean sendBulkEmail1(List<ErpEmailsDBO> dbo)  {
    sessionFactory.withTransaction((session, tx) -> session.persistAll(dbo.toArray())).subscribeAsCompletionStage();
    return true;
	}
	 
	public Tuple getWorkDiaryApproversIdByEmployeeIdByUserId(int userId)  {
		String str = " select emp.emp_id as empId from erp_users "
				+" inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+" inner join emp_approvers ON emp_approvers.work_diary_approver_id  = emp.emp_id and emp_approvers.record_status='A' "
				+" where erp_users.erp_users_id=:userId and erp_users.record_status='A'";
		String finalStr = str;
		Tuple list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
			query.setParameter("userId", userId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return list;
	}

	public Tuple getApproversEmployeeIdByUserId(int userId) {
		String str = " select distinct emp.emp_id as empId from erp_users   "
				+ "	inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A'   "
				+ "	inner join emp_approvers ON emp_approvers.leave_approver_id = emp.emp_id and emp_approvers.record_status='A' "
				+ "	where erp_users.erp_users_id=:userId and erp_users.record_status='A' ";
		String finalStr = str;
		Tuple list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
			query.setParameter("userId", userId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return list;
	}
	
	public ErpWorkFlowProcessNotificationsDBO getErpWorkFlowProcessNotificationsByWorkFlowProcessId1(Integer workFlowProcessId, String notificationCode) {
		String str = " from ErpWorkFlowProcessNotificationsDBO bo where bo.recordStatus='A' and bo.erpWorkFlowProcessDBO.id = :workFlowProcessId and bo.notificationCode = :notificationCode";
		ErpWorkFlowProcessNotificationsDBO list = sessionFactory.withSession(s -> {
					Mutiny.Query<ErpWorkFlowProcessNotificationsDBO> query = s.createQuery(str, ErpWorkFlowProcessNotificationsDBO.class);
					query.setParameter("workFlowProcessId", workFlowProcessId);
					query.setParameter("notificationCode", notificationCode.trim());
					return query.getSingleResultOrNull();
				}).await().indefinitely();
		return list;
	}

	public List<ErpTemplateDBO> getErpTemplateByTemplateCodeAndTemplateType1(List<String> templateTypes, List<String> templateNames)  {
		String str = "select bo from ErpTemplateDBO bo where bo.recordStatus = 'A' and bo.templateType in (:templateType) and bo.templateCode in (:templateCode)";
		String finalStr = str;
		 List<ErpTemplateDBO> list = sessionFactory.withSession(s-> {
				Mutiny.Query<ErpTemplateDBO> query = s.createQuery(finalStr,ErpTemplateDBO.class);
				query.setParameter("templateType", templateTypes);
				query.setParameter("templateCode", templateNames);
				return query.getResultList();
			}).await().indefinitely();
			return list;	
		}
				
	public Tuple getAuthoriserUserIdByEmployeeId(int employeeId) {
		String str = " select erp_users.erp_users_id as usersId,emp.emp_personal_email as personalEmailId  from erp_users"
				+ "	inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A'"
				+ "	inner join emp_approvers ON emp_approvers.leave_authoriser_id = emp.emp_id and emp_approvers.record_status='A'"
				+ "	where emp_approvers.emp_id=:employeeId and erp_users.record_status='A'";
		Tuple value = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("employeeId", employeeId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return value;
	}

	public List<ErpNotificationUserPrefernceDBO> getErpNotificationUserPreferenceByErpUserId1(Set<Integer> userIds,int erpWorkFlowProcessNotificationId) {
		String str = " from ErpNotificationUserPrefernceDBO bo where bo.recordStatus='A' and"
				   + " bo.erpWorkFlowProcessNotificationsDBO.id=:erpWorkFlowProcessNotificationId and bo.erpUsersDBO.id in (:userIds) ";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpNotificationUserPrefernceDBO.class).setParameter("userIds", userIds).setParameter("erpWorkFlowProcessNotificationId", erpWorkFlowProcessNotificationId).getResultList()).await().indefinitely();
	}
	
	public Tuple getErpTemplateByWorkFlowAndNotificationCode(Integer workFlowProcessId, String notificationCode) {
		String str = " select erp_template.template_code as template_code, erp_template.template_type as template_type, erp_template.template_content as template_content from erp_work_flow_process_notifications dbo"
				   + " inner join erp_template  on  erp_template.erp_template_id = dbo.email_template_id"
				   + " where dbo.erp_work_flow_process_id =:workFlowProcessId  and dbo.notification_code =:notificationCode and erp_template.record_status='A' and dbo.record_status='A' ";
		Tuple list = sessionFactory.withSession(s-> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("workFlowProcessId", workFlowProcessId);
			query.setParameter("notificationCode", notificationCode);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return list;	
	}

	public Integer getCampusIdByUserId(int userId) {
		String str = " select erp_campus_department_mapping.erp_campus_id as campusId from erp_users"
			     	+" inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = erp_users.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status='A'"
				    +" where erp_users.erp_users_id =:userId and erp_users.record_status='A'";
		String finalStr = str;
		Integer value = sessionFactory.withSession(s -> {
			Mutiny.Query<Integer> query = s.createNativeQuery(finalStr, Integer.class);
			query.setParameter("userId", userId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return value;
	}
	
	//
	//	public Mono<List<Tuple>> getStudentNames(String studentName) {
	//		String query = "";
	//		if(!Utils.isNullOrEmpty(studentName))
	//			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.applicant_name as applicant_name from student_appln_entries e where e.applicant_name like :studentName and e.record_status='A'";
	//		else
	//			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.applicant_name as applicant_name from student_appln_entries e where e.record_status='A'";
	//		String finalQuery = query;
	//		if(!Utils.isNullOrEmpty(studentName)){
	//			return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(finalQuery, Tuple.class)
	//					.setParameter("studentName", studentName+"%")
	//					.getResultList()).subscribeAsCompletionStage());
	//		}else{
	//			return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(finalQuery, Tuple.class)
	//					.getResultList()).subscribeAsCompletionStage());
	//		}
	//	}

	public List<Tuple> getStudentApplicationNumbers(String applicationNumber, String yearId) {
		String query = "";
		if(!Utils.isNullOrEmpty(applicationNumber)) {
			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.application_no as applicationNo , e.applicant_name as applicantName from student_appln_entries e where e.application_no like :applicationNumber " +
					"	and e.record_status='A' and e.application_no is not null";
			if(!Utils.isNullOrEmpty(yearId)) {
				query += " and e.applied_academic_year_id =:yearId";
			}
		}
		else
			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.application_no as applicationNo from student_appln_entries e where e.record_status='A' and e.application_no is not null";
		String finalQuery = query;
		if(!Utils.isNullOrEmpty(applicationNumber)) {
			List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalQuery, Tuple.class);
			if(!Utils.isNullOrEmpty(yearId)) {
				query1.setParameter("yearId",yearId);
			}
			query1.setParameter("applicationNumber", applicationNumber+"%");
			return query1.getResultList();
			}).await().indefinitely();
			return list;	
		}
			//return sessionFactory.withSession(s->s.createNativeQuery(finalQuery, Tuple.class).setParameter("applicationNumber",applicationNumber+"%").getResultList()).await().indefinitely();
		else
			return sessionFactory.withSession(s->s.createNativeQuery(finalQuery, Tuple.class).getResultList()).await().indefinitely();
	}

	public List<Tuple> getApplicantNames(String applicantName,String yearId) {
		String query = "";
		if(!Utils.isNullOrEmpty(applicantName)) {
			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.applicant_name as applicantName, e.application_no as applicationNo from student_appln_entries e where e.applicant_name like :applicantName " +
					"	and e.record_status='A' and e.application_no is not null";
			if(!Utils.isNullOrEmpty(yearId)) {
				query += " and e.applied_academic_year_id =:yearId";
			}
		}
		else
			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.applicant_name as applicantName, e.application_no as applicationNo from student_appln_entries e where e.record_status='A' and e.application_no is not null";
		String finalQuery = query;
		if(!Utils.isNullOrEmpty(applicantName)) {
			List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalQuery, Tuple.class);
			if(!Utils.isNullOrEmpty(yearId)) {
				query1.setParameter("yearId",yearId);
			}
			query1.setParameter("applicantName", applicantName+"%");
			return query1.getResultList();
			}).await().indefinitely();
			return list;	
		}
			//return sessionFactory.withSession(s->s.createNativeQuery(finalQuery, Tuple.class).setParameter("applicantName",applicantName+"%").getResultList()).await().indefinitely();
		else
			return sessionFactory.withSession(s->s.createNativeQuery(finalQuery, Tuple.class).getResultList()).await().indefinitely();
	}

	public List<Tuple> getSelectionProcessPrefferedDates(String erpCampusProgrammeMappingId) throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery(" select adm_selection_process_plan_detail.adm_selection_process_plan_detail_id as admSelectionProcessPlanDetailId,adm_selection_process_plan_detail.selection_process_date as selectionProcessDate,  " +
						"   adm_selection_process_venue_city.adm_selection_process_venue_city_id as admSelectionProcessVenueCityId,adm_selection_process_venue_city.venue_name as venueName,  " +
						"   (adm_selection_process_plan_center_based.venue_available_seats - count(student_appln_selection_process_dates.student_appln_entries_id )) as totalAvailableSeatCount,  " +
						"   1 as isCandidateChooseSpVenue,  " +
						"   0 as isCandidateChooseSpDate,  " +
						"   adm_selection_process_venue_city.selection_process_mode as selectionProcessMode,adm_selection_process_plan.is_conducted_in_india as isConductedInIndia,  " +
						"   adm_selection_process_venue_city.erp_state_id as erpStateId,erp_state.state_name as stateName" +
						"   from adm_selection_process_plan_detail  " +
						"   inner join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id and adm_selection_process_plan.record_status like 'A'" +
						"   inner join adm_selection_process_plan_detail_prog on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_prog.adm_selection_process_plan_detail_id  " +
						"   inner join erp_campus_programme_mapping on adm_selection_process_plan_detail_prog.erp_campus_programme_mapping_id = erp_campus_programme_mapping.erp_campus_programme_mapping_id" +
						"    and  erp_campus_programme_mapping.record_status like 'A'  " +
						"   left join adm_selection_process_plan_center_based on adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail.adm_selection_process_plan_detail_id  " +
						"   and adm_selection_process_plan_center_based.record_status like 'A'  " +
						"   inner join adm_selection_process_venue_city ON adm_selection_process_venue_city.adm_selection_process_venue_city_id = adm_selection_process_plan_center_based.adm_selection_process_venue_city_id  " +
						"   left join student_appln_selection_process_dates on student_appln_selection_process_dates.adm_selection_process_plan_detail_id=adm_selection_process_plan_detail.adm_selection_process_plan_detail_id  " +
						"   and adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id = student_appln_selection_process_dates.adm_selection_process_plan_center_based_id  " +
						"   and student_appln_selection_process_dates.record_status like 'A'  " +
						"   left join erp_state on adm_selection_process_venue_city.erp_state_id = erp_state.erp_state_id and erp_state.record_status like 'A'" +
						"   where erp_campus_programme_mapping.erp_campus_programme_mapping_id=:erpCampusProgrammeMappingId  " +
						"   and adm_selection_process_plan_detail.process_order=1  " +
						"   and adm_selection_process_venue_city.selection_process_mode='Center Based Entrance'  " +
						"   and adm_selection_process_plan_detail.selection_process_date>date(now())  " +
						"   and date(now())<=adm_selection_process_plan.application_open_till " +
						"   and adm_selection_process_plan_detail.record_status like 'A'  " +
						"   group by adm_selection_process_plan_detail.selection_process_date,adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id,adm_selection_process_venue_city.adm_selection_process_venue_city_id  " +
						"" +
						"   union  " +
						"" +
						"   select adm_selection_process_plan_detail.adm_selection_process_plan_detail_id as admSelectionProcessPlanDetailId,adm_selection_process_plan_detail.selection_process_date as selectionProcessDate,  " +
						"   adm_selection_process_venue_city.adm_selection_process_venue_city_id as admSelectionProcessVenueCityId,adm_selection_process_venue_city.venue_name as venueName,  " +
						"   (adm_selection_process_plan_detail.available_seats - count(student_appln_selection_process_dates.student_appln_entries_id )) as totalAvailableSeatCount,  " +
						"   adm_selection_process_plan_detail.is_candidate_choose_sp_venue as isCandidateChooseSpVenue,  " +
						"   adm_selection_process_plan_detail.is_candidate_choose_sp_date as isCandidateChooseSpDate,  " +
						"   adm_selection_process_venue_city.selection_process_mode  as selectionProcessMode,adm_selection_process_plan.is_conducted_in_india as isConductedInIndia,  " +
						"   adm_selection_process_venue_city.erp_state_id as erpStateId,erp_state.state_name as stateName" +
						"   from adm_selection_process_plan_detail  " +
						"   inner join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id and adm_selection_process_plan.record_status like 'A'" +
						"   inner join adm_selection_process_plan_detail_prog on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_prog.adm_selection_process_plan_detail_id  " +
						"   inner join erp_campus_programme_mapping on adm_selection_process_plan_detail_prog.erp_campus_programme_mapping_id = erp_campus_programme_mapping.erp_campus_programme_mapping_id" +
						"    and  erp_campus_programme_mapping.record_status like 'A'  " +
						"   inner join adm_selection_process_venue_city ON adm_selection_process_venue_city.adm_selection_process_venue_city_id = adm_selection_process_plan_detail.adm_selection_process_venue_city_id  " +
						"   and adm_selection_process_venue_city.record_status like 'A'  " +
						"   left join student_appln_selection_process_dates on student_appln_selection_process_dates.adm_selection_process_plan_detail_id=adm_selection_process_plan_detail.adm_selection_process_plan_detail_id  " +
						"   and student_appln_selection_process_dates.record_status like 'A'  " +
						"   left join erp_state on adm_selection_process_venue_city.erp_state_id = erp_state.erp_state_id and erp_state.record_status like 'A'" +
						"   where erp_campus_programme_mapping.erp_campus_programme_mapping_id=:erpCampusProgrammeMappingId  " +
						"   and adm_selection_process_plan_detail.process_order=1  " +
						"   and adm_selection_process_venue_city.selection_process_mode not like 'Center Based Entrance'  " +
						"   and adm_selection_process_plan_detail.selection_process_date>date(now())  " +
						"   and date(now())<=adm_selection_process_plan.application_open_till " +
						"   and adm_selection_process_plan_detail.record_status like 'A'  " +
						"   group by adm_selection_process_plan_detail.adm_selection_process_plan_detail_id,adm_selection_process_venue_city.adm_selection_process_venue_city_id", Tuple.class);
				query.setParameter("erpCampusProgrammeMappingId" , Integer.parseInt(erpCampusProgrammeMappingId));
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public Mono<SelectDTO> getCampusByEmployee(String userId) {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (cdbo.id,cdbo.campusName)"
				+ " from ErpUsersDBO dbo"
				+ " inner join dbo.empDBO edbo on edbo.recordStatus ='A'"
				+ " inner join edbo.erpCampusDepartmentMappingDBO ecdbo on ecdbo.recordStatus ='A'"
				+ " inner join ecdbo.erpCampusDBO cdbo on cdbo.recordStatus ='A'"
				+ " where dbo.id =:userId and dbo.recordStatus='A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class).setParameter("userId", Integer.parseInt(userId)).getSingleResultOrNull()).subscribeAsCompletionStage());
	}
	
	public Mono<List<ErpProgrammeLevelDBO>> getLevels() {
		String queryString = "select dbo from ErpProgrammeLevelDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpProgrammeLevelDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<Tuple>> getProgrammeByLevelAndCampus(String level, String campus) {
		String queryString =" select erp_campus_programme_mapping.erp_campus_programme_mapping_id as id ,erp_programme.programme_name as programmeName  from erp_campus_programme_mapping"
				+ " inner join erp_campus on erp_campus_programme_mapping.erp_campus_id = erp_campus.erp_campus_id"
				+ " inner join erp_programme on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id"
				+ " where erp_campus_programme_mapping.record_status = 'A' "
				+ " and erp_campus.erp_campus_id = :campus and erp_campus.record_status = 'A'"
				+ " and erp_programme.erp_programme_level_id = :level and erp_programme.record_status = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class)
				.setParameter("campus", Integer.parseInt(campus)).setParameter("level", Integer.parseInt(level)).getResultList()).subscribeAsCompletionStage());
	}

	public Tuple getUserIdByEmployeeId(String employeeId) {
		String str = " select erp_users.erp_users_id as userId from erp_users "
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ " where emp.emp_id=:employeeId and erp_users.record_status='A'";
		Tuple value = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("employeeId", employeeId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return value;
	}

	public Mono<List<ErpWorkFlowProcessDBO>> getErpWorkFlowProcess() {
		 String str = "select bo from ErpWorkFlowProcessDBO bo where bo.recordStatus = 'A'";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpWorkFlowProcessDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpTemplateDBO>> getSMSorEmailTemplate(String templateType) {
		 String str = "select bo from ErpTemplateDBO bo where bo.recordStatus = 'A' and  bo.templateType=:templateType ";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpTemplateDBO.class).setParameter("templateType", templateType).getResultList()).subscribeAsCompletionStage());
	}

	public ErpTemplateDBO getErpTemplateByCodeAndTypeAndCampusProgMapping(String templateType, Integer groupTemplateId,String erpCampusProgrameMappingId) {
		String  str = " select bo from ErpTemplateDBO bo where bo.recordStatus='A' and bo.erpCampusProgrammeMappingDBO.id =:erpCampusProgrameMappingId and bo.templateType=:templateType  and bo.erpTemplateGroupDBO.id=:groupTemplateId";
		ErpTemplateDBO erpTemplateDBO = sessionFactory.withSession(s-> {
				Mutiny.Query<ErpTemplateDBO> query = s.createQuery(str,ErpTemplateDBO.class);
				query.setParameter("templateType", templateType);
				query.setParameter("groupTemplateId", groupTemplateId);
				query.setParameter("erpCampusProgrameMappingId", Integer.parseInt(erpCampusProgrameMappingId));
				return query.getSingleResultOrNull();
			}).await().indefinitely();
		 if(erpTemplateDBO!=null) {
			 return erpTemplateDBO;	
		 }else {
			 return null;
		 }
	}
	
	public Tuple getTemplateGroupIdByAdmissionCategory(Integer erpAdmissionCategoryId) {
		String str = " select erp_admission_category.erp_template_group_id as templateGroupId from erp_admission_category "
				+ " left join erp_template_group ON  erp_template_group.erp_template_group_id=erp_admission_category.erp_template_group_id and erp_template_group.record_status='A' "
				+ " where erp_admission_category.erp_admission_category_id=:erpAdmissionCategoryId and erp_admission_category.record_status='A'  ";
		Tuple value = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("erpAdmissionCategoryId", erpAdmissionCategoryId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return value;
	}

	public Mono<List<ErpAdmissionCategoryDBO>> getAdmissionCategory() {
		String queryString = "select dbo from ErpAdmissionCategoryDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpAdmissionCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public ErpTemplateDBO getErpTemplateByTypeAndCampusProgMappingIdAndTemplateName(String templateType,String erpCampusProgrameMappingId, String templateName, String groupTemplateName) {
		String str = " select bo from ErpTemplateDBO bo where bo.recordStatus='A' and bo.erpCampusProgrammeMappingDBO.id =:erpCampusProgrameMappingId "
				+ "and bo.templateType=:templateType and bo.erpTemplateGroupDBO.templateGroupName=:groupTemplateName and bo.templateName=:templateName ";
		ErpTemplateDBO erpTemplateDBO = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpTemplateDBO> query = s.createQuery(str, ErpTemplateDBO.class);
			query.setParameter("templateType", templateType);
			query.setParameter("erpCampusProgrameMappingId", Integer.parseInt(erpCampusProgrameMappingId));
			query.setParameter("groupTemplateName", groupTemplateName);
			query.setParameter("templateName", templateName);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		if (erpTemplateDBO != null) {
			return erpTemplateDBO;
		} else {
			return null;
		}
	}
	
	public List<ErpTemplateDBO> getErpTemplateByTypeAndCampusProgMappingIdAndTemplateName1(List<String> templateType,List<Integer> erpCampusProgrameMappingId, List<String> templateName, List<String> groupTemplateName) {
		String str = " select bo from ErpTemplateDBO bo where bo.recordStatus='A' and bo.erpCampusProgrammeMappingDBO.id in (:erpCampusProgrameMappingId) "
				+ "and bo.templateType in (:templateType) and bo.erpTemplateGroupDBO.templateGroupName in (:groupTemplateName) and bo.templateName in (:templateName) ";
		List<ErpTemplateDBO> erpTemplateDBO = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpTemplateDBO> query = s.createQuery(str, ErpTemplateDBO.class);
			query.setParameter("templateType", templateType);
			query.setParameter("erpCampusProgrameMappingId", erpCampusProgrameMappingId);
			query.setParameter("groupTemplateName", groupTemplateName);
			query.setParameter("templateName", templateName);
			return query.getResultList();
		}).await().indefinitely();
		if (erpTemplateDBO != null) {
			return erpTemplateDBO;
		} else {
			return null;
		}
	}

	public String getErpWorkFlowProcessTextbyProcessCode(String processCode) {
		String str = " select w.applicant_status_display_text as applicant_status_display_text "
				    +" from erp_work_flow_process w where w.process_code= :processCode and w.record_status='A' "; 
		return sessionFactory.withSession(s -> s.createNativeQuery(str,String.class).setParameter("processCode", processCode).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<List<HostelPublishApplicationDBO>> getOfflinePrefix(String hostelId, String yearId) {
	     String str = " select distinct dbo from HostelPublishApplicationDBO dbo "
	     		     +" where dbo.erpAcademicYearDBO.id =:yearId and dbo.hostelDBO.id =:hostelId and dbo.recordStatus ='A'";
	     return  Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelPublishApplicationDBO.class).setParameter("hostelId", Integer.parseInt(hostelId))
	    		 .setParameter("yearId", Integer.parseInt(yearId))
	    		 .getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getERPProperties() {
		String str = "select sys_properties.property_name, sys_properties.property_value, sys_properties.is_common_property, " +
				" sys_properties_details.erp_campus_id,sys_properties_details.erp_location_id,sys_properties_details.property_detail_value from sys_properties " +
				" left join sys_properties_details on sys_properties.sys_properties_id = sys_properties_details.sys_properties_id and sys_properties_details.record_status = 'A' " +
				"  where sys_properties.record_status = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getActiveProgrammeByYearValue(String yearValue) {
		String queryString =" select distinct erp_campus_programme_mapping.erp_programme_id ,erp_programme.programme_name from erp_campus_programme_mapping"
				+ " inner join erp_programme on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id"
				+ " where  erp_campus_programme_mapping.record_status = 'A' and erp_programme.record_status ='A' and :yearValue >= programme_commence_year and (:yearValue < programme_inactivated_year or programme_inactivated_year = 0 or programme_inactivated_year is null) order by erp_programme.programme_name ";
		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("yearValue",Integer.parseInt(yearValue)).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<Tuple>> getCampusBySelectedProgramme(String programmeId) {
		String queryString = " select distinct erp_campus_programme_mapping_id, erp_campus.campus_name from erp_campus_programme_mapping"
				+ " inner join erp_campus on erp_campus_programme_mapping.erp_campus_id = erp_campus.erp_campus_id"
				+ " where erp_campus_programme_mapping.record_status = 'A' and erp_campus.record_status = 'A' and erp_campus_programme_mapping.erp_programme_id = :programmeId order by  erp_campus.campus_name";
		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("programmeId",Integer.parseInt(programmeId)).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<Tuple>> getDegreeByLevelAndLocationOrCampus(String levelId, List<Integer> campusId,List<Integer> locationId) {
		String queryString =" select distinct erp_programme_degree.erp_programme_degree_id ,erp_programme_degree.programme_degree from  erp_programme_degree "
				+ " inner join erp_programme_level ON erp_programme_level.erp_programme_level_id = erp_programme_degree.erp_programme_level_id and erp_programme_level.record_status = 'A'"
				+ " left join  erp_programme on  erp_programme.erp_programme_level_id = erp_programme_level.erp_programme_level_id and erp_programme.record_status = 'A'"
				+ " left join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id and erp_campus_programme_mapping.record_status = 'A'"
				+ " left join erp_campus on erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status = 'A'"
				+ " left join erp_location on erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id  and erp_location.record_status = 'A'"
				+ " where erp_programme_degree.record_status = 'A'"
				+ " and erp_programme_level.erp_programme_level_id = :levelId";
		if(!Utils.isNullOrEmpty(locationId) && !Utils.isNullOrEmpty(campusId)){
			queryString += " and (erp_location.erp_location_id in (:locationId) or erp_campus.erp_campus_id in (:campusId))";
		} else if(!Utils.isNullOrEmpty(locationId)) {
			queryString += " and erp_location.erp_location_id in (:locationId)";
		}
//		else if(!Utils.isNullOrEmpty(campusId)) {
//			queryString += " and  erp_campus.erp_campus_id in (:campusId)";
//		}
		String finalquery = queryString;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalquery, Tuple.class);
		query.setParameter("levelId",Integer.parseInt(levelId));
		if(!Utils.isNullOrEmpty(campusId)) {
			query.setParameter("campusId",campusId);
		}
		if(!Utils.isNullOrEmpty(locationId)) {
			query.setParameter("locationId",locationId);
		} 
		return  query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}

	public Mono<List<Tuple>> getProgrammeByDegreeAndLocationOrCampus(String degreeId, List<Integer> campusId,List<Integer> locationId) {
		String queryString =" select distinct erp_programme.erp_programme_id,erp_programme.programme_name from erp_campus_programme_mapping"
				+ " inner join erp_programme on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id and erp_programme.record_status = 'A'"
				+ " inner join erp_programme_degree on erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id and erp_programme_degree.record_status = 'A'"
				+ " left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status = 'A'"
				+ " left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id  and erp_location.record_status = 'A'"
				+ " where  erp_campus_programme_mapping.record_status = 'A'  and erp_programme_degree.erp_programme_degree_id = :degreeId";
		if(!Utils.isNullOrEmpty(locationId) && !Utils.isNullOrEmpty(campusId)){
			queryString += "  and (erp_campus.erp_campus_id in (:campusId) or erp_location.erp_location_id in (:locationId))";
		} else if(!Utils.isNullOrEmpty(locationId)) {
			queryString += " and erp_location.erp_location_id in (:locationId)";
		}
//		if(!Utils.isNullOrEmpty(campusId)) {
//			queryString += " and erp_campus.erp_campus_id in (:campusId)";
//		}


		String finalquery = queryString;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalquery, Tuple.class);
		query.setParameter("degreeId",degreeId);
		if(!Utils.isNullOrEmpty(campusId)) {
			query.setParameter("campusId", campusId);
		} 
		if(!Utils.isNullOrEmpty(locationId)) {
			query.setParameter("locationId", locationId);
		} 
		return  query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}

	public Mono<List<Tuple>> getProgramPreferenceWithBatch(String academicYear) {
		String queryString = " select erp_campus_programme_mapping.erp_campus_programme_mapping_id as MappingId, " +
			" erp_campus_programme_mapping.erp_campus_id as CampusID,  erp_campus.campus_name as CampusName,   " +
			" erp_campus_programme_mapping.erp_location_id as LocID, erp_location.location_name as LocName,   " +
			" erp_programme.erp_programme_id as ProgramID, erp_programme.programme_name as ProgramName, adm_programme_settings.preference_option as ProgramOption," +
			" aca_batch.aca_batch_id as acaBatchId ,aca_batch.batch_name as batchName"+
			" from adm_programme_settings   " +
			" inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id = adm_programme_settings.erp_programme_id   " +
			" and if(adm_programme_settings.preference_option='C', erp_campus_programme_mapping.erp_campus_id is not null,   " +
			" erp_campus_programme_mapping.erp_location_id is not null)          " +
			" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id   " +
			" left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id             " +
			" inner join erp_programme ON erp_programme.erp_programme_id = adm_programme_settings.erp_programme_id   " +
			" left join aca_batch on aca_batch.erp_campus_programme_mapping_id = erp_campus_programme_mapping.erp_campus_programme_mapping_id "+
			" left join erp_programme_batchwise_settings on erp_programme_batchwise_settings.erp_programme_batchwise_settings_id = aca_batch.erp_programme_batchwise_settings_id"+
			" where adm_programme_settings.record_status='A' and erp_campus_programme_mapping.record_status='A' "+
			" and erp_programme_batchwise_settings.batch_year_id=:academicYear "+
			" group by erp_campus_programme_mapping.erp_campus_programme_mapping_id, erp_campus_programme_mapping.erp_campus_id, erp_campus.campus_name, erp_campus_programme_mapping.erp_location_id, "+
			" erp_location.location_name, erp_programme.erp_programme_id, erp_programme.programme_name, adm_programme_settings.preference_option, aca_batch.aca_batch_id, aca_batch.batch_name "+
			" order by  ProgramName  asc , CampusName asc ";
		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("academicYear",Integer.parseInt(academicYear)).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<Tuple>> getProgrammeByLevelOrCampus(String level, String campus) {
		String queryString =" select erp_campus_programme_mapping.erp_campus_programme_mapping_id as id ,erp_programme.programme_name as programmeName  from erp_campus_programme_mapping"
			+ " inner join erp_campus on erp_campus_programme_mapping.erp_campus_id = erp_campus.erp_campus_id"
			+ " inner join erp_programme on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id"
			+ " where erp_campus_programme_mapping.record_status = 'A' "
			+ " and erp_campus.erp_campus_id = :campus and erp_campus.record_status = 'A'";
		if(!Utils.isNullOrEmpty(level)) {
			queryString = queryString + " and erp_programme.erp_programme_level_id = :level ";
		}
		queryString = queryString  + "  and erp_programme.record_status = 'A' order by programmeName ";
		String queryFinal = queryString;
		List<Tuple> value = sessionFactory.withSession( s-> { Mutiny.Query<Tuple> str = s.createNativeQuery(queryFinal,Tuple.class);
		if(!Utils.isNullOrEmpty(level)) {
			str.setParameter("level", level);
		}
		str.setParameter("campus",campus);
		return str.getResultList();
		}).await().indefinitely();
		return Mono.just(value);
	}
	
	public List<Integer> getTemplateGroupIdByAdmissionCategory1(List<Integer> admissionCategoryIds) {
		String str = " select erp_admission_category.erp_template_group_id as templateGroupId from erp_admission_category "
				+ " left join erp_template_group ON  erp_template_group.erp_template_group_id=erp_admission_category.erp_template_group_id and erp_template_group.record_status='A' "
				+ " where erp_admission_category.erp_admission_category_id in (:erpAdmissionCategoryId) and erp_admission_category.record_status='A'  ";
		List<Tuple> value = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("erpAdmissionCategoryId", admissionCategoryIds);
			return query.getResultList();
		}).await().indefinitely();
		List<Integer> list=new ArrayList<Integer>();
		for (Tuple tuple : value) {
			list.add(Integer.valueOf(String.valueOf(tuple.get("templateGroupId"))));
		}
		return list;
	}

	public List<ErpTemplateDBO> getErpTemplateByCodeAndTypeAndCampusProgMapping1(String templateType,List<Integer> erpGroupTemplateId, List<Integer> erpProgCampusIds) {
		String  str = " select bo from ErpTemplateDBO bo where bo.recordStatus='A' and bo.erpCampusProgrammeMappingDBO.id in (:erpCampusProgrameMappingId) and bo.templateType=:templateType  and bo.erpTemplateGroupDBO.id in (:groupTemplateId)";
		List<ErpTemplateDBO> erpTemplateDBO = sessionFactory.withSession(s-> {
				Mutiny.Query<ErpTemplateDBO> query = s.createQuery(str,ErpTemplateDBO.class);
				query.setParameter("templateType", templateType);
				query.setParameter("groupTemplateId", erpGroupTemplateId);
				query.setParameter("erpCampusProgrameMappingId", erpProgCampusIds);
				return query.getResultList();
			}).await().indefinitely();
		 if(erpTemplateDBO!=null) {
			 return erpTemplateDBO;	
		 }else {
			 return null;
		 }
	}
	
	public String getErpWorkFlowProcessCodebyId(int processCodeId) {
		String str = " select erp_work_flow_process.process_code from erp_work_flow_process "
				+" where erp_work_flow_process.erp_work_flow_process_id =:processCodeId and erp_work_flow_process.record_status ='A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str, String.class).setParameter("processCodeId", processCodeId).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<List<Tuple>> getRegisterNumbers(String registerNumber, String yearId) {
		String query = "";
		if(!Utils.isNullOrEmpty(registerNumber))
			query = " select student.student_id as student_id, student.register_no as register_no from student "
					+" where student.register_no like :registerNumber and student.admitted_year_id =:yearId and student.record_status ='A' and student.register_no is not null";
		else
			query = "select student.student_id as student_id, student.register_no as register_no from student "
					+" where student.admitted_year_id =:yearId and student.record_status='A' and student.register_no is not null";
		String finalQuery = query;
		if(!Utils.isNullOrEmpty(registerNumber)){
			return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(finalQuery, Tuple.class)
					.setParameter("registerNumber", registerNumber+"%").setParameter("yearId", Integer.parseInt(yearId))
					.getResultList()).subscribeAsCompletionStage());
		}else{
			return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(finalQuery, Tuple.class)
					.setParameter("yearId", Integer.parseInt(yearId))
					.getResultList()).subscribeAsCompletionStage());
		}
	}

	public Mono<List<Tuple>> getDataByApplnNoOrRegisterNumbersOrName(String data, String yearId) {
		String query = "";
		if(!Utils.isNullOrEmpty(data)) 
			query = "select e.student_appln_entries_id as studentApplnEntriesId, e.applicant_name as applicantName,"
					+" e.application_no as applicationNo, s.register_no as register_no, s.student_name as student_name from student_appln_entries e"
					+" inner join student as s on e.student_appln_entries_id = s.student_appln_entries_id "
					+" where (e.applicant_name like :data or e.application_no like :data or s.register_no like :data)"
					+" and e.record_status='A' and e.applied_academic_year_id =:yearId";
		else 
			query = " select e.student_appln_entries_id as studentApplnEntriesId, e.applicant_name as applicantName,"
					+" e.application_no as applicationNo, s.register_no as register_no, s.student_name as student_name  from student_appln_entries e"
					+" inner join student as s on e.student_appln_entries_id = s.student_appln_entries_id "
					+" where e.record_status='A' and e.applied_academic_year_id =:yearId";
		String finalQuery = query;
		if(!Utils.isNullOrEmpty(data)) {
		return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(finalQuery, Tuple.class)
				.setParameter("data", data+"%").setParameter("yearId", Integer.parseInt(yearId))
				.getResultList()).subscribeAsCompletionStage());
		} else  {
		return	Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(finalQuery, Tuple.class).setParameter("yearId", Integer.parseInt(yearId)).getResultList()).subscribeAsCompletionStage());
	 }	
	}
	
	public void saveStatusLogDBO(List<ErpStatusLogDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persistAll(dbo.toArray())).subscribeAsCompletionStage();
	}
	
	public ErpTemplateDBO getErpTemplateByTemplateCodeAndTemplateTypes(String templateType, String templateName)  {
		String str = "select bo from ErpTemplateDBO bo where bo.recordStatus = 'A' and bo.templateType =:templateType and bo.templateCode =:templateCode";
		String finalStr = str;
		 ErpTemplateDBO list = sessionFactory.withSession(s-> {
				Mutiny.Query<ErpTemplateDBO> query = s.createQuery(finalStr,ErpTemplateDBO.class);
				query.setParameter("templateType", templateType);
				query.setParameter("templateCode", templateName);
				return query.getSingleResultOrNull();
			}).await().indefinitely();
			return list;	
		}

	public List<ErpCampusDBO> getCampusForLocation(String locId) {
		String str = " from ErpCampusDBO dbo where dbo.recordStatus = 'A' and dbo.erpLocationDBO.recordStatus = 'A' and "
				+ " dbo.erpLocationDBO.id = :locId";
		List<ErpCampusDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCampusDBO> query = s.createQuery(str, ErpCampusDBO.class);
			query.setParameter("locId", Integer.parseInt(locId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public boolean isUserAdded(Integer userId) {
		String str = "select distinct dbo from ErpCampusDepartmentUserTitleDBO dbo where dbo.recordStatus ='A' and dbo.erpUsersDBO.id =:userId";
		List<ErpCampusDepartmentUserTitleDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCampusDepartmentUserTitleDBO> query = s.createQuery(str, ErpCampusDepartmentUserTitleDBO.class);
			query.setParameter("userId", userId);
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}
	
	public List<ErpPincodeDBO> getPincodeDetailsOrValidate(String pincode) {
		String str = "select dbo from ErpPincodeDBO dbo"
				+ " where dbo.recordStatus = 'A' and dbo.pincode =:pincode";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpPincodeDBO.class).setParameter("pincode", pincode).getResultList()).await().indefinitely();
	}

	public List<ErpCityDBO> selectCityByNames(Set<String> districtSet, Set<String> stateSet) {
		String str = "select dbo from ErpCityDBO dbo "
				+ " inner join dbo.erpStateDBO bo"
				+ " where dbo.recordStatus ='A' and bo.recordStatus ='A' and dbo.cityName in (:districtSet) and bo.stateName in (:stateSet)";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpCityDBO.class).setParameter("districtSet",districtSet).setParameter("stateSet",stateSet).getResultList()).await().indefinitely();	       
	}
	
	public List<ErpPincodeDBO> saveNewPincode(List<ErpPincodeDBO> pincodeDboList) {
		sessionFactory.withTransaction((session, tx) -> session.persistAll(pincodeDboList.toArray())).await().indefinitely();	
		return pincodeDboList;
	}
	
	public List<ErpStateDBO> getStatesByName(Set<String> stateSet) {
		String str = "select dbo from ErpStateDBO dbo"
				+ " where dbo.recordStatus = 'A' and dbo.stateName in (:stateSet)";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpStateDBO.class).setParameter("stateSet", stateSet).getResultList()).await().indefinitely();
	}
	
	public LinkedHashSet<ErpStateDBO> saveNewState(LinkedHashSet<ErpStateDBO> newStateDBOSet) {
		sessionFactory.withTransaction((session, tx) -> session.persistAll(newStateDBOSet.toArray())).await().indefinitely();	
		return newStateDBOSet;
	}

	public LinkedHashSet<ErpCityDBO> saveNewDistricts(LinkedHashSet<ErpCityDBO> newDistrictDBOSet) {
		sessionFactory.withTransaction((session, tx) -> session.persistAll(newDistrictDBOSet.toArray())).await().indefinitely();	
		return newDistrictDBOSet;
	}

	public Mono<List<ErpRoomsDBO>> getErpRoomByBlock(String blockId) {
		String str = " select distinct dbo from ErpRoomsDBO dbo "
				    +" where dbo.erpRoomTypeDBO.isClassRoom = 1 and dbo.erpBlockDBO.id =:blockId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpRoomsDBO.class).setParameter("blockId", Integer.parseInt(blockId)).getResultList()).subscribeAsCompletionStage());		
	}
	
	public Mono<List<ErpCampusDepartmentMappingDBO>> getErpCampusDepartmentMapping() {
		String str = " select distinct dbo from ErpCampusDepartmentMappingDBO dbo "
				    +" where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpCampusDepartmentMappingDBO.class).getResultList()).subscribeAsCompletionStage());		
	}
	
	public Mono<List<ErpCampusDBO>> getUserSpecificCampusList(String userId) {
		String str = " select distinct dbo from ErpCampusDBO dbo "
				+ " inner join ErpUsersCampusDBO bo on dbo.id = bo.erpCampusDBO.id"
			    + " where dbo.recordStatus = 'A' and bo.recordStatus = 'A' and bo.ErpUsersDBO.id =:userId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpCampusDBO.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).subscribeAsCompletionStage());		
	}

	public Mono<List<Tuple>> getInstitutionList(String name, String countryId, String stateId , String boardType) {
		String str = " select dbo.institutionName as NAME, dbo.id as ID from ErpInstitutionDBO dbo "
			    + " where dbo.recordStatus = 'A' and dbo.boardType like :boardType ";
		if(!Utils.isNullOrEmpty(name)) {
			str+= " and dbo.institutionName like :name";
		}
		if(!Utils.isNullOrEmpty(countryId)) {
			str+= " and dbo.erpCountryId.id = :countryId";
		} 
		if (!Utils.isNullOrEmpty(stateId)){
			str+= " and dbo.erpStateDBO.id= :stateId";
		}
		str+=" order by dbo.institutionName";
		
		String finalStr = str;
		Mono<List<Tuple>> list = Mono.fromFuture (sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createQuery(finalStr, Tuple.class);
			if(!Utils.isNullOrEmpty(name)) {
				query.setParameter("name", "%" + name + "%");
			}			
			if(!Utils.isNullOrEmpty(countryId)) {
				query.setParameter("countryId",Integer.parseInt(countryId));
			}
			if(!Utils.isNullOrEmpty(stateId)) {
				query.setParameter("stateId",Integer.parseInt(stateId));
			}
			query.setParameter("boardType", boardType);
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}
	
	public ErpAcademicYearDBO getCurrentAdmissionYear() {
		String query = "from ErpAcademicYearDBO bo where bo.recordStatus='A' and  bo.isCurrentAdmissionYear = 1";
		return sessionFactory.withSession(s->s.createQuery(query,ErpAcademicYearDBO.class).getSingleResultOrNull()).await().indefinitely();
	}
	
	public Mono<ErpPincodeDBO> getCityAndStateByPincode(String pincodeId) {		
		 return Mono.fromFuture(sessionFactory.withSession(s->s.find(ErpPincodeDBO.class, Integer.parseInt(pincodeId))).subscribeAsCompletionStage());
	}

	public Mono<List<ErpCampusProgrammeMappingDBO>> getLocationOrCampusByProgramme(String yearValue, String programId, Boolean isLocation) {
		String str = " select dbo from ErpCampusProgrammeMappingDBO dbo"
	                +" where :yearValue >= programme_commence_year and (:yearValue < programme_inactivated_year or programme_inactivated_year = 0 or programme_inactivated_year is null) and dbo.erpProgrammeDBO.id =:programId and dbo.recordStatus ='A'";
				if(isLocation) {
					str+= " and dbo.erpLocationDBO.recordStatus ='A' order by dbo.erpLocationDBO.locationName";
				} else {
					str+= " and dbo.erpCampusDBO.recordStatus ='A' order by dbo.erpCampusDBO.campusName";
				}
				String finalquery = str;
				Mono<List<ErpCampusProgrammeMappingDBO>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<ErpCampusProgrammeMappingDBO> query = s.createQuery(finalquery, ErpCampusProgrammeMappingDBO.class);
				if(!Utils.isNullOrEmpty(programId)) {
					query.setParameter("programId",Integer.parseInt(programId));
				}
				query.setParameter("yearValue", Integer.parseInt(yearValue));
				return query.getResultList();
				}).subscribeAsCompletionStage());
				return list;	
	}
	
	public List<EmpDBO> getEmployeeOrUsers1() {
		String str = "from  EmpDBO dbo where dbo.recordStatus='A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpDBO.class).getResultList()).await().indefinitely();
	}
	
	public Mono<List<AcaGraduateAttributesDBO>> getGraduateAttributes() {
		String str = " select distinct dbo from AcaGraduateAttributesDBO dbo  where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, AcaGraduateAttributesDBO.class).getResultList()).subscribeAsCompletionStage());		
	}
	
	public Mono<List<ErpApprovalLevelsDBO>> getApproverLevel() {
		String str = " select distinct dbo from ErpApprovalLevelsDBO dbo  where dbo.recordStatus = 'A' and dbo.isForProgramme = 1";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpApprovalLevelsDBO.class).getResultList()).subscribeAsCompletionStage());		
	}
	
	public Mono<List<Tuple>> getDepartmentByUser(String userId) {
		String queryString =" select distinct erp_department.erp_department_id as id , erp_department.department_name as deptName from  erp_users"
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status = 'A'"
				+ " inner join  erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id"
				+ " or erp_campus_department_mapping.erp_campus_department_mapping_id = emp.deputation_erp_campus_department_mapping_id and   erp_campus_department_mapping.record_status = 'A'"
				+ " inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id and erp_department.record_status = 'A'"
				+ " where erp_users.record_status = 'A'  and erp_users.erp_users_id = :userId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).subscribeAsCompletionStage());
	}
	
	public Integer getWorkFlowProcessId(String str) {
		String query = "select erp_work_flow_process.erp_work_flow_process_id as id from erp_work_flow_process where erp_work_flow_process.record_status = 'A' and process_code=:str";
		Integer workFlowProcessId = sessionFactory.withSession(s -> s.createNativeQuery(query,Integer.class).setParameter("str", str.trim()).getSingleResultOrNull()).await().indefinitely();
		return workFlowProcessId;
	}
	
	public ErpWorkFlowProcessDBO getErpWorkFlowProcess(Integer workflowProcessId) {
		String str = " select dbo from ErpWorkFlowProcessDBO dbo where dbo.recordStatus='A' and dbo.id=:workflowProcessId ";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpWorkFlowProcessDBO.class).setParameter("workflowProcessId", workflowProcessId).getSingleResultOrNull()).await().indefinitely();
	}
	
	public Mono<List<StudentApplnCancellationReasonsDBO>> getApplicationCancellationReasons() {
		String str = " select distinct dbo from StudentApplnCancellationReasonsDBO dbo  where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, StudentApplnCancellationReasonsDBO.class).getResultList()).subscribeAsCompletionStage());		
	}
	
	public Mono<List<ErpTemplateDBO>> getErpTemplateByGroupCode(String groupCode) {
		String str =  " select bo from ErpTemplateDBO bo"
				     +" inner join bo.erpTemplateGroupDBO edbo "
				     +" where bo.recordStatus='A'"
				     +" and edbo.templateGroupCode =:groupCode and edbo.recordStatus='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpTemplateDBO.class).setParameter("groupCode", groupCode).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<ErpUniversityBoardDBO>> getUniversityBoardList(Integer countryId, Integer stateId, String boardType, String name) {
		String str = " select dbo from ErpUniversityBoardDBO dbo"
				+" where dbo.recordStatus ='A' and dbo.boardType like :boardType";
		if(!Utils.isNullOrEmpty(name)) {
			str+= " and dbo.universityBoardName like :name";
		}
		if(!Utils.isNullOrEmpty(countryId) && !boardType.equalsIgnoreCase("Board")) {
			str+= " and dbo.erpCountryDBO.id = :countryId";
		} 
		if (!Utils.isNullOrEmpty(stateId) && !boardType.equalsIgnoreCase("Board")){
			str+= " and dbo.erpStateDBO.id= :stateId";
		}
		String finalquery = str+" order by dbo.universityBoardName";
		Mono<List<ErpUniversityBoardDBO>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<ErpUniversityBoardDBO> query = s.createQuery(finalquery, ErpUniversityBoardDBO.class);
		if(!Utils.isNullOrEmpty(countryId) && !boardType.equalsIgnoreCase("Board")) {
			query.setParameter("countryId",countryId);
		}
		if(!Utils.isNullOrEmpty(stateId) && !boardType.equalsIgnoreCase("Board")) {
			query.setParameter("stateId", stateId);
		}	
		if(!Utils.isNullOrEmpty(name)) {
			query.setParameter("name","%" + name + "%");
		}
		query.setParameter("boardType", boardType);
		return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;	
	}

	public SelectDTO getEmployeemByUserId(Integer userId) {
		String str = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ "(dbo.empDBO.id, CASE WHEN dbo.empDBO is NULL THEN dbo.userName ELSE dbo.empDBO.empName END)"
				+ " from ErpUsersDBO dbo left join dbo.empDBO where dbo.recordStatus = 'A' and dbo.id = :userId ";
		return sessionFactory.withSession(s -> s.createQuery(str, SelectDTO.class).setParameter("userId", userId).getSingleResultOrNull()).await().indefinitely();		 
	}
	
	public List<SelectDTO> getEmployeeListmByUserIds(Set<Integer> userIds) {
		String str = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, CASE WHEN dbo.empDBO is NULL THEN dbo.userName ELSE edbo.empName END)"
				+ " from ErpUsersDBO dbo left join dbo.empDBO edbo where dbo.recordStatus = 'A' and dbo.id in (:userIds) ";
		return sessionFactory.withSession(s -> s.createQuery(str, SelectDTO.class).setParameter("userIds", userIds).getResultList()).await().indefinitely();		 
	}

	public Mono<List<Tuple>> getSubModulesList() {
		String str = " select distinct e.sys_menu_module_sub_id as id, e.sub_module_name as subModuleName, sys_menu_module.module_name as moduleName from sys_menu_module_sub e "
				   +" inner join sys_menu_module ON sys_menu_module.sys_menu_module_id = e.sys_menu_module_id "
				   +" where e.record_status='A' and sys_menu_module.record_status='A' order by e.sub_module_name asc ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).getResultList()).subscribeAsCompletionStage());	
	}
	
	public Integer getIndiaId() {
		return sessionFactory.withSession(s -> s.createQuery("select dbo.id from ErpCountryDBO dbo where dbo.recordStatus like 'A' and dbo.countryName like 'India'", Integer.class).getSingleResultOrNull()).await().indefinitely();
	}
	
	public Mono<List<UrlFolderListDBO>> getFolderListForMenu(String processCode) {
		List<String> stringList = Arrays.stream(processCode.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
		String str = " from UrlFolderListDBO dbo where dbo.recordStatus = 'A' and dbo.uploadProcessCode in (:processCode) ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, UrlFolderListDBO.class).setParameter("processCode", stringList).getResultList()).subscribeAsCompletionStage());		 
	}
	
	public List<Tuple> getEmployeeOrUserWithDepartment() {
		String str = "SELECT erp_users.erp_users_id as erpUserId,emp.emp_name AS name, CONCAT('(', erp_department.department_name, ')') AS department"
				+ " FROM erp_users"
				+ " LEFT JOIN emp ON emp.emp_id = IFNULL(erp_users.emp_id, erp_users.erp_users_name) AND emp.record_status = 'A'"
				+ " LEFT JOIN erp_campus_department_mapping ON emp.erp_campus_department_mapping_id = erp_campus_department_mapping.erp_campus_department_mapping_id"
				+ " AND erp_campus_department_mapping.record_status = 'A'"
				+ " LEFT JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id"
				+ " WHERE erp_users.record_status = 'A'  Order By emp.emp_name asc";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			return query.getResultList();
		}).await().indefinitely();
		return list; 
	}
	
	public Mono<SelectDTO> getUserDepartment(String userId) {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (cdbo.id,cdbo.departmentName)"
				+ " from ErpUsersDBO dbo"
				+ " inner join dbo.empDBO edbo on edbo.recordStatus ='A'"
				+ " inner join edbo.erpCampusDepartmentMappingDBO ecdbo on ecdbo.recordStatus ='A'"
				+ " inner join ecdbo.erpDepartmentDBO cdbo on cdbo.recordStatus ='A'"
				+ " where dbo.id =:userId and dbo.recordStatus='A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class).setParameter("userId", Integer.parseInt(userId)).getSingleResultOrNull()).subscribeAsCompletionStage());
	}
	
	public Mono<SelectDTO> getUserLocation(String userId) {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (ldbo.id,ldbo.locationName)"
				+ " from ErpUsersDBO dbo"
				+ " inner join dbo.empDBO edbo on edbo.recordStatus ='A'"
				+ " inner join edbo.erpCampusDepartmentMappingDBO ecdbo on ecdbo.recordStatus ='A'"
				+ " inner join ecdbo.erpCampusDBO cdbo on cdbo.recordStatus ='A'"
				+ " inner join cdbo.erpLocationDBO ldbo on ldbo.recordStatus ='A'"
				+ " where dbo.id =:userId and dbo.recordStatus='A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class).setParameter("userId", Integer.parseInt(userId)).getSingleResultOrNull()).subscribeAsCompletionStage());
	}
	public List<ErpNotificationEmailSenderSettingsDBO> getEmailSenderSettings() {
		return sessionFactory.withSession(s->s.createQuery("from ErpNotificationEmailSenderSettingsDBO where recordStatus='A' ", ErpNotificationEmailSenderSettingsDBO.class)
				.getResultList()).await().indefinitely();
	}
	public UrlFolderListDBO getFolderListWithProcessCode(String processCode) {
		String str = " from UrlFolderListDBO dbo where dbo.recordStatus = 'A' and dbo.uploadProcessCode = :processCode ";
		return sessionFactory.withSession(s -> s.createQuery(str, UrlFolderListDBO.class).setParameter("processCode", processCode).getSingleResult()).await().indefinitely();		 
	}
	
	public List<SysRoleDTO> getErpUsers() {
		String query = " select new com.christ.erp.services.dto.common.SysRoleDTO"
				+ " (dbo.id, dbo.loginId, dbo.recordStatus)"
				+ " from ErpUsersDBO dbo where dbo.recordStatus  = 'A'";
		return  sessionFactory.withSession(s->s.createQuery(query,SysRoleDTO.class).getResultList()).await().indefinitely();
	}
	
	public List<SysUserRoleMapDTO> getRoleNames(Set<Integer> erpUserIds) {
		String query = " select new com.christ.erp.services.dto.common.SysUserRoleMapDTO"
				+ " (dbo.erpUsersDBO.id, sr.roleName)"
				+ " from SysUserRoleMapDBO dbo "
				+ " left join dbo.sysRoleDBO sr"
				+ " where dbo.recordStatus  = 'A' and dbo.erpUsersDBO.id in (:erpUserIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,SysUserRoleMapDTO.class).setParameter("erpUserIds", erpUserIds).getResultList()).await().indefinitely();
	}
	public List<UrlFolderListDBO> getAllForlderListForUpload() {
		String str = " from UrlFolderListDBO dbo where dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(str, UrlFolderListDBO.class).getResultList()).await().indefinitely();		 
	}
	
	public Mono<List<UrlFolderListDBO>> getAwsConfig() {
		String str = " from UrlFolderListDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, UrlFolderListDBO.class).getResultList()).subscribeAsCompletionStage());		 
	}

	public Mono<List<Tuple>> getSubjectCategorySpecialization(List<Integer> subjectCategoryIds) {
		String str = " select emp_appln_subject_category_specialization_id as ID, subject_category_specialization_name as name"
				+ " from emp_appln_subject_category_specialization "
				+ " where emp_appln_subject_category_specialization.record_status='A' and "
				+ " emp_appln_subject_category_specialization.emp_appln_subject_category_id in (:subjectCategoryIds) order by subject_category_specialization_name asc";
		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("subjectCategoryIds", subjectCategoryIds).getResultList()).subscribeAsCompletionStage());			
	}
	
	public Mono<SelectDTO> getDepartmentFromCampusDeptMap(String campusDeptMappingId) {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dept.id,dept.departmentName)"
				+ " from  ErpCampusDepartmentMappingDBO dbo"
				+ " inner join dbo.erpDepartmentDBO dept "
				+ " where dbo.id =:campusDeptMappingId";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class).setParameter("campusDeptMappingId", Integer.parseInt(campusDeptMappingId)).getSingleResultOrNull()).subscribeAsCompletionStage());
	}

	public List<Tuple> getUsersEmployeeDepartment() {
		String str = "select erp_users.erp_users_id as ID, CONCAT(emp.emp_name, ' (', erp_department.department_name, ')') as Text from erp_users "
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ " inner join erp_campus_department_mapping on emp.erp_campus_department_mapping_id = erp_campus_department_mapping.erp_campus_department_mapping_id "
				+ " inner join erp_campus on erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
				+ " inner join erp_department on erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id "
				+ " where erp_users.record_status='A' order by emp.emp_name ASC ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
	public Mono<List<SelectDTO>> getCountryCode() {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, dbo.phoneCode)"
				+ " from ErpCountryDBO dbo"
				+ " where dbo.recordStatus='A' and dbo.phoneCode is not null";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<SelectDTO>> getErpBlocks(Integer campusId) {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, dbo.blockName)"
				+ " from ErpBlockDBO dbo"
				+ " where dbo.recordStatus='A' and dbo.erpCampusDBO.id = :campusId";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class).setParameter("campusId", campusId).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<SelectDTO>> getErpFloors(Integer blockId) {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, dbo.floorName)"
				+ " from ErpFloorsDBO dbo"
				+ " where dbo.recordStatus='A' and dbo.erpBlockDBO.id = :blockId";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class).setParameter("blockId", blockId).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<SelectDTO>> getErpRoomTypes() {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, dbo.roomType)"
				+ " from ErpRoomTypeDBO dbo"
				+ " where dbo.recordStatus='A' and (dbo.isClassRoom = 0 or dbo.isClassRoom is null)";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<SelectDTO>> getErpRooms(Integer roomTypeId, Integer blockId, Integer floorId) {
		String queryString = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, CAST(dbo.roomNo as string))"
				+ " from ErpRoomsDBO dbo"
				+ " where dbo.recordStatus='A' and dbo.erpBlockDBO.id = :blockId and dbo.erpFloorsDBO.id = :floorId and dbo.erpRoomTypeDBO.id = :roomTypeId";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SelectDTO.class)
				.setParameter("roomTypeId", roomTypeId)
				.setParameter("blockId",blockId)
				.setParameter("floorId", floorId).getResultList()).subscribeAsCompletionStage());
	}

    public Mono<List<Tuple>> getDepartmentByCampusId(int campusId) {
		String str = " select dpt.erp_department_id as ID , dpt.department_name as Text from erp_department dpt " +
				"inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_department_id = dpt.erp_department_id " +
				"where dpt.record_status = 'A' and erp_campus_department_mapping.record_status = 'A' and erp_campus_department_mapping.erp_campus_id=:campusId ORDER BY Text";
		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("campusId", campusId).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpTemplateDBO>> getErpTemplateByGroupCodeAndProg(String groupCode, String programmeId) {
		String str =  " select bo from ErpTemplateDBO bo"
				+" inner join bo.erpTemplateGroupDBO edbo " +
				" inner join bo.erpCampusProgrammeMappingDBO ecpm" +
				" inner join ecpm.erpProgrammeDBO ep"
				+" where bo.recordStatus='A'"
				+" and edbo.templateGroupCode =:groupCode and edbo.recordStatus='A' and ep.id = :programmeId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpTemplateDBO.class).setParameter("groupCode", groupCode).setParameter("programmeId",Integer.parseInt(programmeId))
				.getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<SelectDTO>> getProgrammeByLevelAndYear(String levelId, String yearId) {
		var academicYear = academicYear(Integer.parseInt(yearId));
		String str = "select distinct new com.christ.erp.services.dto.common.SelectDTO" +
				" (dbo.id,dbo.programmeName) " +
				" from ErpProgrammeDBO dbo" +
				" left join dbo.erpCampusProgrammeMappingDBOSet cmp" +
				" where dbo.recordStatus ='A'" +
				" and cmp.recordStatus = 'A' " +
				" and (:yearId) between cmp.programmeCommenceYear and cmp.programmeInactivatedYear";
		if(!Utils.isNullOrEmpty(levelId)) {
			str += " and dbo.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id = :levelId";
		}
		str += " order by dbo.programmeName";
		String finalStr = str;
		return Mono.fromFuture(sessionFactory.withSession(s ->{
			var query = s.createQuery(finalStr, SelectDTO.class) ;
			if(!Utils.isNullOrEmpty(levelId)) {
				query.setParameter("levelId", Integer.parseInt(levelId));
			}
			query.setParameter("yearId",academicYear);
			return query.getResultList();
		}).subscribeAsCompletionStage());
	}

	public Integer academicYear(Integer academicYearId) {
		String str1 = "select eay.academicYear from ErpAcademicYearDBO eay where eay.recordStatus = 'A' and eay.id = :academicYearId";
		return sessionFactory.withSession(s -> s.createQuery(str1, Integer.class).setParameter("academicYearId", academicYearId).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<List<SelectDTO>> getApplicationDeclarations() {
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery("select distinct new com.christ.erp.services.dto.common.SelectDTO (dbo.id,dbo.studentApplnDeclarations) " +
				" from StudentApplnDeclarationsTemplateDBO dbo where dbo.recordStatus = 'A' order by dbo.studentApplnDeclarations", SelectDTO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<SelectDTO>> getCampusProgrammeByYearAndProgrammeLevel(String levelId, String yearId) {
		var academicYear = academicYear(Integer.parseInt(yearId));
		String str = " select new com.christ.erp.services.dto.common.SelectDTO" +
				" (dbo.id, concat(dbo.erpProgrammeDBO.programmeNameForApplication,'(', case when lc is  not null then lc.locationName else cmp.campusName end , ')')) from ErpCampusProgrammeMappingDBO dbo" +
				" left join dbo.erpCampusDBO cmp" +
				" left join dbo.erpLocationDBO lc" +
				" where dbo.recordStatus ='A'" +
				" and (:academicYear) between dbo.programmeCommenceYear and dbo.programmeInactivatedYear";
		if(!Utils.isNullOrEmpty(levelId)) {
			str += " and dbo.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id =:levelId";
		}
		str += " order by dbo.erpProgrammeDBO.programmeNameForApplication";
		String finalstr = str;
		return Mono.fromFuture(sessionFactory.withSession(s -> {
			var query = s.createQuery(finalstr, SelectDTO.class);
			if(!Utils.isNullOrEmpty(levelId)) {
				query.setParameter("levelId", Integer.parseInt(levelId));
			}
			return query.setParameter("academicYear",academicYear).getResultList();
		}).subscribeAsCompletionStage());
	}

	public List<ErpCampusDBO> getCampusForLocations(List<Integer> locIds) {
		String str = " from ErpCampusDBO dbo where dbo.recordStatus = 'A' and dbo.erpLocationDBO.recordStatus = 'A' and "
				+ " dbo.erpLocationDBO.id in (:locId)";
		List<ErpCampusDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCampusDBO> query = s.createQuery(str, ErpCampusDBO.class);
			query.setParameter("locId", locIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Mono<List<Tuple>> getHolidaysByCampusAndYear(String campusId,String year) {
		String str = " select emp_holiday_events.emp_holiday_events_id as id , emp_holiday_events.holiday_events_start_date as startDate  "
				+ "from  emp_holiday_events "
				+ "inner join erp_location ON erp_location.erp_location_id = emp_holiday_events.erp_location_id "
				+ "inner join erp_campus ON erp_location.erp_location_id = erp_campus.erp_location_id "
				+ "inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_holiday_events.erp_academic_year_id "
				+ "where emp_holiday_events.emp_holiday_events_type_name='Holiday' "
				+ "and erp_campus.erp_campus_id=:campusId and erp_academic_year.academic_year=:year and emp_holiday_events.record_status='A' "
				+ "and erp_location.record_status='A' and erp_campus.record_status='A' and erp_academic_year.record_status='A' order by emp_holiday_events.holiday_events_start_date";
		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("campusId", Integer.parseInt(campusId)).setParameter("year", Integer.parseInt(year)).getResultList()).subscribeAsCompletionStage());			
	}
	
	public void save (UrlAccessLinkDBO dbo) {
		sessionFactory.withTransaction((session, txt) -> session.merge(dbo)).subscribeAsCompletionStage();
	}
}