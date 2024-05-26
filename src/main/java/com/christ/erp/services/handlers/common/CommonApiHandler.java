package com.christ.erp.services.handlers.common;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.Tuple;

import com.christ.erp.services.common.*;
import com.christ.erp.services.dto.common.*;
import com.christ.utility.lib.Constants;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessVenueCityDBO;
import com.christ.erp.services.dbobjects.common.AcaGraduateAttributesDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpApprovalLevelsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationUserPrefernceDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpPincodeDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeLevelDBO;
import com.christ.erp.services.dbobjects.common.ErpRoomsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;
import com.christ.erp.services.dbobjects.common.ErpUniversityBoardDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.common.UrlFolderListDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnCancellationReasonsDBO;
//import com.christ.erp.services.dbobjects.common.SysUserRoleMapDBO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.admission.settings.AdmissionSelectionProcessTypeDTO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.aws.URLFolderListDTO;
import com.christ.erp.services.dto.employee.attendance.LetterTemplatesDTO;
import com.christ.erp.services.dto.employee.settings.ErpTemplateDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.helpers.common.CommonApiHelper;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings({ "rawtypes", "unchecked"})
@Service
public class CommonApiHandler {

	private static volatile CommonApiHandler commonApiHandler = null;
	CommonApiHelper commonApiHelper = CommonApiHelper.getInstance();
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

	public static CommonApiHandler getInstance() {
		if(commonApiHandler==null) {
			commonApiHandler = new CommonApiHandler();
		}
		return commonApiHandler;
	}

	@Autowired
	private CommonApiTransaction commonApiTransaction1;
	
	@Autowired
	WebClient postalDatas;
	
	@Autowired
	RedisSysPropertiesData redisSysPropertiesData;

	@Autowired
	private RedisVaultKeyConfig redisVaultKeyConfig;

	public ApiResult<List<LookupItemDTO>> getUsers() {
		ApiResult<List<LookupItemDTO>> users = new ApiResult<List<LookupItemDTO>>();
		try {
			users = commonApiTransaction.getUsers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
	
    public void getMotherTongue(ApiResult<List<SelectDTO>> result) throws Exception{
        List<Tuple> list = commonApiTransaction.getMotherTongue();
        if(!Utils.isNullOrEmpty(list)) {
            result.success = true;
            result.dto = new ArrayList<>();
            for(Tuple tuple : list) {
                SelectDTO itemDTO = new SelectDTO();
                itemDTO.value = !Utils.isNullOrEmpty(tuple.get("id")) ? tuple.get("id").toString() : "";
                itemDTO.label = !Utils.isNullOrEmpty(tuple.get("text")) ? tuple.get("text").toString() : "";
                result.dto.add(itemDTO);
            }
        }
    }
    
    public void getOccupation(ApiResult<List<SelectDTO>> result) throws Exception{
        List<Tuple> list = commonApiTransaction.getOccupation();
        if(!Utils.isNullOrEmpty(list)) {
            result.success = true;
            result.dto = new ArrayList<>();
            for(Tuple tuple : list) {
                SelectDTO itemDTO = new SelectDTO();
                itemDTO.value = !Utils.isNullOrEmpty(tuple.get("id")) ? tuple.get("id").toString() : "";
                itemDTO.label = !Utils.isNullOrEmpty(tuple.get("text")) ? tuple.get("text").toString() : "";
                result.dto.add(itemDTO);
            }
        }
    }
    
    public void getSports(ApiResult<List<SelectDTO>> result) throws Exception{
        List<Tuple> list = commonApiTransaction.getSports();
        if(!Utils.isNullOrEmpty(list)) {
            result.success = true;
            result.dto = new ArrayList<>();
            for(Tuple tuple : list) {
                SelectDTO itemDTO = new SelectDTO();
                itemDTO.value = !Utils.isNullOrEmpty(tuple.get("id")) ? tuple.get("id").toString() : "";
                itemDTO.label = !Utils.isNullOrEmpty(tuple.get("text")) ? tuple.get("text").toString() : "";
                result.dto.add(itemDTO);
            }
        }
    }
    
    public void getSportsLevel(ApiResult<List<SelectDTO>> result) throws Exception{
        List<Tuple> list = commonApiTransaction.getSportsLevel();
        if(!Utils.isNullOrEmpty(list)) {
            result.success = true;
            result.dto = new ArrayList<>();
            for(Tuple tuple : list) {
                SelectDTO itemDTO = new SelectDTO();
                itemDTO.value = !Utils.isNullOrEmpty(tuple.get("id")) ? tuple.get("id").toString() : "";
                itemDTO.label = !Utils.isNullOrEmpty(tuple.get("text")) ? tuple.get("text").toString() : "";
                result.dto.add(itemDTO);
            }
        }
    }
    
    public void getExtraCurricular(ApiResult<List<SelectDTO>> result) throws Exception{
        List<Tuple> list = commonApiTransaction.getExtraCurricular();
        if(!Utils.isNullOrEmpty(list)) {
            result.success = true;
            result.dto = new ArrayList<>();
            for(Tuple tuple : list) {
                SelectDTO itemDTO = new SelectDTO();
                itemDTO.value = !Utils.isNullOrEmpty(tuple.get("id")) ? tuple.get("id").toString() : "";
                itemDTO.label = !Utils.isNullOrEmpty(tuple.get("text")) ? tuple.get("text").toString() : "";
                result.dto.add(itemDTO);
            }
        }
    }
    
    public void getSalutations(ApiResult<List<SelectDTO>> result) throws Exception{
        List<Tuple> list = commonApiTransaction.getSalutations();
        if(!Utils.isNullOrEmpty(list)) {
            result.success = true;
            result.dto = new ArrayList<>();
            for(Tuple tuple : list) {
                SelectDTO itemDTO = new SelectDTO();
                itemDTO.value = !Utils.isNullOrEmpty(tuple.get("id")) ? tuple.get("id").toString() : "";
                itemDTO.label = !Utils.isNullOrEmpty(tuple.get("text")) ? tuple.get("text").toString() : "";
                result.dto.add(itemDTO);
            }
        }
    }
    
    public void getUniversityBoard(ApiResult<List<ErpUniversityBoardDTO>> result) throws Exception{
        List<Tuple> list = commonApiTransaction.getUniversityBoard();
        if(!Utils.isNullOrEmpty(list)) {
            result.success = true;
            result.dto = new ArrayList<>();
            for(Tuple tuple : list) {
                ErpUniversityBoardDTO itemDTO = new ErpUniversityBoardDTO();
                itemDTO.setId(!Utils.isNullOrEmpty(tuple.get("id")) ? Integer.parseInt(tuple.get("id").toString()) : 0);
                itemDTO.setUniversityBoardName(!Utils.isNullOrEmpty(tuple.get("text")) ? tuple.get("text").toString() : null);
                itemDTO.setBoardType(!Utils.isNullOrEmpty(tuple.get("boardType")) ? tuple.get("boardType").toString() : null);
                result.dto.add(itemDTO);
            }
        }
    }
    
    public void getInstitutionReference(ApiResult<List<SelectDTO>> result) throws Exception{
        List<Tuple> list = commonApiTransaction.getInstitutionReference();
        if(!Utils.isNullOrEmpty(list)) {
            result.success = true;
            result.dto = new ArrayList<>();
            for(Tuple tuple : list) {
                SelectDTO itemDTO = new SelectDTO();
                itemDTO.value = !Utils.isNullOrEmpty(tuple.get("id")) ? tuple.get("id").toString() : "";
                itemDTO.label = !Utils.isNullOrEmpty(tuple.get("text")) ? tuple.get("text").toString() : "";
                result.dto.add(itemDTO);
            }
        }
    }

	public void getDepartmentbyLocation(String locationId,ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> mappings = commonApiTransaction.getDepartmentbyLocation(locationId);
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}


	public ApiResult<List<LookupItemDTO>> getGeneratedYear() {
		ApiResult<List<LookupItemDTO>> generatedYear = new ApiResult<List<LookupItemDTO>>();
		try {
			Integer currentAcademicYear = commonApiTransaction.getCurrentAcademicYear();
			int minYear=currentAcademicYear-70;
			int maxYear=currentAcademicYear+1;
			generatedYear.dto = new ArrayList<>();
			for (int i = maxYear; i >= minYear; i--) {
				LookupItemDTO itemInfo = new LookupItemDTO();
				itemInfo.value = String.valueOf(i);
				itemInfo.label = String.valueOf(i);
				generatedYear.dto.add(itemInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return generatedYear;
	}

	public ApiResult<List<LookupItemDTO>> getCampus(List<Integer> list) {
		ApiResult<List<LookupItemDTO>> campus = new ApiResult<List<LookupItemDTO>>();
		try {
			campus = commonApiTransaction.getCampus(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return campus;
	}

	public boolean send_ERP_Notification_SMS_Email_By_UserId(Integer workFlowProcessId, String notificationCode, Set<Integer> userIds, List<ErpNotificationsDBO> notificationList, List<ErpSmsDBO> smsList, List<ErpEmailsDBO> emailList) {
		boolean isSent = false;
		if(!Utils.isNullOrEmpty(workFlowProcessId) && !Utils.isNullOrEmpty(notificationCode) && (!Utils.isNullOrEmpty(notificationList) || !Utils.isNullOrEmpty(smsList) || !Utils.isNullOrEmpty(emailList))) {
			List<ErpNotificationsDBO> finalNotificationList = new ArrayList<>();
			List<ErpSmsDBO> finalSmsList = new ArrayList<>();
			List<ErpEmailsDBO> finalEmailList = new ArrayList<>();
			Set<Integer> notificationDeActivatedUsers = new HashSet<>();
			Set<Integer> smsDeActivatedUsers = new HashSet<>();
			Set<Integer> emailDeActivatedUsers = new HashSet<>();
			ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO = commonApiTransaction1.getErpWorkFlowProcessNotificationsByWorkFlowProcessId1(workFlowProcessId,notificationCode);
			if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO)) {
				if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isNotificationActivated) && erpWorkFlowProcessNotificationsDBO.isNotificationActivated ||
						!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isSmsActivated) && erpWorkFlowProcessNotificationsDBO.isSmsActivated ||
						!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isEmailActivated) && erpWorkFlowProcessNotificationsDBO.isEmailActivated) {
					if(!Utils.isNullOrEmpty(userIds)) {
						List<ErpNotificationUserPrefernceDBO> erpNotificationUserPrefernceDBOList = commonApiTransaction1.getErpNotificationUserPreferenceByErpUserId1(userIds,erpWorkFlowProcessNotificationsDBO.id);
						if(!Utils.isNullOrEmpty(erpNotificationUserPrefernceDBOList)) {
							erpNotificationUserPrefernceDBOList.forEach(erpNotificationUserPrefernceDBO-> {
								if(!Utils.isNullOrEmpty(erpNotificationUserPrefernceDBO.isNotificationDeactivated) && erpNotificationUserPrefernceDBO.isNotificationDeactivated) {
									notificationDeActivatedUsers.add(erpNotificationUserPrefernceDBO.erpUsersDBO.id);
								}
								if(!Utils.isNullOrEmpty(erpNotificationUserPrefernceDBO.isSmsDeactivated) && erpNotificationUserPrefernceDBO.isSmsDeactivated) {
									smsDeActivatedUsers.add(erpNotificationUserPrefernceDBO.erpUsersDBO.id);
								}
								if(!Utils.isNullOrEmpty(erpNotificationUserPrefernceDBO.isEmailDeactivated) && erpNotificationUserPrefernceDBO.isEmailDeactivated) {
									emailDeActivatedUsers.add(erpNotificationUserPrefernceDBO.erpUsersDBO.id);
								}
							});
						}
					}
					if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isNotificationActivated) && erpWorkFlowProcessNotificationsDBO.isNotificationActivated) {
						if(!Utils.isNullOrEmpty(notificationList)) {
							notificationList.forEach(erpNotificationsDBO -> {
								if(!Utils.isNullOrEmpty(userIds)) {
									if(!notificationDeActivatedUsers.contains(erpNotificationsDBO.erpUsersDBO.id)) {
										erpNotificationsDBO.erpWorkFlowProcessNotificationsDBO = new ErpWorkFlowProcessNotificationsDBO();
										erpNotificationsDBO.erpWorkFlowProcessNotificationsDBO.id = erpWorkFlowProcessNotificationsDBO.id;
										finalNotificationList.add(erpNotificationsDBO);
									}
								}else {
									erpNotificationsDBO.erpWorkFlowProcessNotificationsDBO = new ErpWorkFlowProcessNotificationsDBO();
									erpNotificationsDBO.erpWorkFlowProcessNotificationsDBO.id = erpWorkFlowProcessNotificationsDBO.id;
									finalNotificationList.add(erpNotificationsDBO);
								}
							});
						}
					}
					if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isSmsActivated) && erpWorkFlowProcessNotificationsDBO.isSmsActivated) {
						if(!Utils.isNullOrEmpty(smsList)) {
							smsList.forEach(erpSmsDBO -> {
								if(!Utils.isNullOrEmpty(userIds)) {
									if(!smsDeActivatedUsers.contains(erpSmsDBO.erpUsersDBO.id)) {
										erpSmsDBO.erpWorkFlowProcessNotificationsDBO = new ErpWorkFlowProcessNotificationsDBO();
										erpSmsDBO.erpWorkFlowProcessNotificationsDBO.id = erpWorkFlowProcessNotificationsDBO.id;
										finalSmsList.add(erpSmsDBO);
									}
								}else {
									erpSmsDBO.erpWorkFlowProcessNotificationsDBO = new ErpWorkFlowProcessNotificationsDBO();
									erpSmsDBO.erpWorkFlowProcessNotificationsDBO.id = erpWorkFlowProcessNotificationsDBO.id;
									finalSmsList.add(erpSmsDBO);
								}
							});
						}
					}
					if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isEmailActivated) && erpWorkFlowProcessNotificationsDBO.isEmailActivated) {
						if(!Utils.isNullOrEmpty(emailList)) {
							emailList.forEach(erpEmailsDBO -> {
								if(!Utils.isNullOrEmpty(userIds)) {
									if(!emailDeActivatedUsers.contains(erpEmailsDBO.erpUsersDBO.id)) {
										if(Utils.isNullOrEmpty(erpEmailsDBO.getPriorityLevelOrder())){
											erpEmailsDBO.setPriorityLevelOrder(2);
										}
										erpEmailsDBO.erpWorkFlowProcessNotificationsDBO = new ErpWorkFlowProcessNotificationsDBO();
										erpEmailsDBO.erpWorkFlowProcessNotificationsDBO.id = erpWorkFlowProcessNotificationsDBO.id;
										finalEmailList.add(erpEmailsDBO);
									}
								}else {
									if(Utils.isNullOrEmpty(erpEmailsDBO.getPriorityLevelOrder())){
										erpEmailsDBO.setPriorityLevelOrder(2);
									}
									erpEmailsDBO.erpWorkFlowProcessNotificationsDBO = new ErpWorkFlowProcessNotificationsDBO();
									erpEmailsDBO.erpWorkFlowProcessNotificationsDBO.id = erpWorkFlowProcessNotificationsDBO.id;
									finalEmailList.add(erpEmailsDBO);
								}
							});
						}
					}
					if(!Utils.isNullOrEmpty(finalNotificationList)) {
						isSent = commonApiTransaction1.sendBulkNotification1(finalNotificationList);

					}
					if(!Utils.isNullOrEmpty(finalSmsList)) {
						isSent = commonApiTransaction1.sendBulkSms1(finalSmsList);

					}
					if(!Utils.isNullOrEmpty(finalEmailList)) {
						isSent = commonApiTransaction1.sendBulkEmail1(finalEmailList);
					}
				}
			}
		}
		return isSent;
	}

	public boolean send_ERP_Notification_SMS_Email_By_EmpApplnRegistrationsId(Integer workFlowProcessId, String notificationCode, Set<Integer> empApplnRegistrationsIds, List<ErpNotificationsDBO> notificationList, List<ErpSmsDBO> smsList, List<ErpEmailsDBO> emailList) throws Exception {
		boolean isSent = false;
		if(!Utils.isNullOrEmpty(workFlowProcessId) && !Utils.isNullOrEmpty(notificationCode) && !Utils.isNullOrEmpty(empApplnRegistrationsIds) && (!Utils.isNullOrEmpty(notificationList) || !Utils.isNullOrEmpty(smsList) || !Utils.isNullOrEmpty(emailList))) {
			ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO = commonApiTransaction.getErpWorkFlowProcessNotificationsByWorkFlowProcessId(workFlowProcessId,notificationCode);
			if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO)) {
				if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isNotificationActivated) && erpWorkFlowProcessNotificationsDBO.isNotificationActivated) {
					if(!Utils.isNullOrEmpty(notificationList)) {
						isSent = commonApiTransaction.sendBulkNotification(notificationList);
					}
				}
				if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isSmsActivated) && erpWorkFlowProcessNotificationsDBO.isSmsActivated) {
					if(!Utils.isNullOrEmpty(smsList)) {
						isSent = commonApiTransaction.sendBulkSms(smsList);
					}
				}
				if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBO.isEmailActivated) && erpWorkFlowProcessNotificationsDBO.isEmailActivated) {
					if(!Utils.isNullOrEmpty(emailList)) {
						isSent = commonApiTransaction.sendBulkEmail(emailList);
					}
				}
			}
		}
		return isSent;
	}

	public List<LookupItemDTO> getMonthList() {
		List<LookupItemDTO> monthList = new ArrayList<>();
		monthList.add(new LookupItemDTO("1","January"));
		monthList.add(new LookupItemDTO("2","February"));
		monthList.add(new LookupItemDTO("3","March"));
		monthList.add(new LookupItemDTO("4","April"));
		monthList.add(new LookupItemDTO("5","May"));
		monthList.add(new LookupItemDTO("6","June"));
		monthList.add(new LookupItemDTO("7","July"));
		monthList.add(new LookupItemDTO("8","August"));
		monthList.add(new LookupItemDTO("9","September"));
		monthList.add(new LookupItemDTO("10","October"));
		monthList.add(new LookupItemDTO("11","November"));
		monthList.add(new LookupItemDTO("12","December"));
		return monthList;
	}

	public ApiResult<List<LookupItemDTO>> getEmployeesOrUsers() {
		ApiResult<List<LookupItemDTO>> employeeOrusers = new ApiResult<List<LookupItemDTO>>();
		try {
			var dboList = commonApiTransaction.getEmployeesOrUsers();
			if(!Utils.isNullOrEmpty(dboList)) {
				employeeOrusers.setDto(dboList);
			}
//			List<LookupItemDTO> dtoList = new ArrayList<LookupItemDTO>();
//			List<ErpUsersDBO> dboList = commonApiTransaction.getEmployeesOrUsers();
//			if(!Utils.isNullOrEmpty(dboList)) {
//				for (ErpUsersDBO erpUsersDBO : dboList) {
//					LookupItemDTO itemInfo = new LookupItemDTO();
//					if(!Utils.isNullOrEmpty(erpUsersDBO.id)) {
//						itemInfo.value = String.valueOf(erpUsersDBO.id);
//						if(!Utils.isNullOrEmpty(erpUsersDBO.empDBO) && !Utils.isNullOrEmpty(erpUsersDBO.empDBO.empName)) {
//							itemInfo.label = !Utils.isNullOrEmpty(erpUsersDBO.empDBO.empName) ? erpUsersDBO.empDBO.empName : "";
//						}else {
//							itemInfo.label = !Utils.isNullOrEmpty(erpUsersDBO.userName) ? erpUsersDBO.userName : "";
//						}
//					}
//					dtoList.add(itemInfo);
//				}
//				employeeOrusers.dto = dtoList;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeOrusers;
	}

	public ApiResult<List<LookupItemDTO>> getDepartmentCategory() {
		ApiResult<List<LookupItemDTO>> departmentCategory = new ApiResult<List<LookupItemDTO>>();
		try {
			List<ErpDepartmentCategoryDBO> departmentCategoryDBO = commonApiTransaction.getDepartmentCategory();
			departmentCategory.dto = new ArrayList<>();
			for (ErpDepartmentCategoryDBO dbo :departmentCategoryDBO) {
				LookupItemDTO itemInfo = new LookupItemDTO();
				itemInfo.value = String.valueOf(dbo.id);
				itemInfo.label = dbo.departmentCategoryName;
				itemInfo.status = dbo.isCategoryAcademic;
				departmentCategory.dto.add(itemInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return departmentCategory;
	}

	public ApiResult<List<LookupItemDTO>> getDeanery() {
		ApiResult<List<LookupItemDTO>> deanery = new ApiResult<List<LookupItemDTO>>();
		try {
			deanery = commonApiTransaction.getDeanery();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deanery;
	}
/*
	public LookupItemDTO getDisplayOrder(String moduleId, String processId) throws Exception {
		// TODO Auto-generated method stub
		LookupItemDTO deanery = new LookupItemDTO();
		Tuple deanery1 = commonApiTransaction.getDisplayOrder(moduleId,processId);
		if(!Utils.isNullOrEmpty(deanery1)) {
			deanery.value=deanery1.get("DisplayOrder").toString();
			deanery.label=deanery1.get("DisplayOrder").toString();
		}
		return deanery;
		//return null;
	}
*/
	public ApiResult<List<LookupItemDTO>> getCampuses() {
		ApiResult<List<LookupItemDTO>> campus = new ApiResult<List<LookupItemDTO>>();
		try {
			campus = commonApiTransaction.getCampuses();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return campus;
	}

	public List<LetterTemplatesDTO> getOfferLetterTemplate(Integer employeeCategoryId) throws Exception {
		List<LetterTemplatesDTO> letterTemplatesDTOs = new ArrayList<LetterTemplatesDTO>();
		List<Tuple> templates = commonApiTransaction.getOfferLetterTemplate(employeeCategoryId);
		if(!Utils.isNullOrEmpty(templates)) {
			if(templates != null && templates.size() > 0) {
				for(Tuple mapping : templates) {
					LetterTemplatesDTO dto = new LetterTemplatesDTO();
					dto.id = String.valueOf(mapping.get("ID"));
					dto.empCategory = new ExModelBaseDTO();
					dto.isEdit=true;
					dto.empCategory.id = !Utils.isNullOrEmpty(mapping.get("categoryId")) ? mapping.get("categoryId").toString() : "";
					dto.groupCode = !Utils.isNullOrEmpty(mapping.get("templateGroupCode")) ? mapping.get("templateGroupCode").toString() : "";
					dto.groupName = !Utils.isNullOrEmpty(mapping.get("TEXT")) ? mapping.get("TEXT").toString() : "";
					dto.ckTemplate = !Utils.isNullOrEmpty(mapping.get("templateContent")) ? mapping.get("templateContent").toString() : "";
					letterTemplatesDTOs.add(dto);
				}
			}
		}
		return letterTemplatesDTOs;
	}

	public boolean validatePincode(String pincode) throws Exception{
		boolean isValid = false;
		InputStream inputStream = null;
		try {
			URLConnection connection = new URL("https://api.postalpincode.in/pincode/"+pincode).openConnection();
			inputStream = connection.getInputStream();
			JsonReader jsonReader = Json.createReader(inputStream);
			JsonArray array = jsonReader.readArray();
			for(Object ob : array){
				JsonObject obj = (JsonObject) ob;
				if(!Utils.isNullOrEmpty(obj) && !Utils.isNullOrEmpty(obj.get("Status")) && !Utils.isNullOrEmpty(obj.get("Status").toString()) && "\"Success\"".equalsIgnoreCase(obj.get("Status").toString())){
					isValid = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					throw e;
				}
			}
		}
		return isValid;
	}


	public void getProgramme(ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> programmeMappings = commonApiTransaction.getProgramme();
		if(programmeMappings.size()>0) {
			if(programmeMappings != null && programmeMappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : programmeMappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getPrerequisiteName(ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> prerequisiteMappings = commonApiTransaction.getPrerequisiteName();
		if(prerequisiteMappings.size()>0) {
			if(prerequisiteMappings != null && prerequisiteMappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : prerequisiteMappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getBlockByCampus(String campusId,ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> mappings = commonApiTransaction.getBlockByCampus(campusId);
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getRoomsByBlock(String blockId,ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> mappings = commonApiTransaction.getRoomsByBlock(blockId);
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getFloorsByBlock(String blockId,ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> mappings = commonApiTransaction.getFloorsByBlock(blockId);
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getAreaAndDistrict(String pincode,ApiResult<ErpPincodeDTO> result) throws Exception {
		Tuple mapping = commonApiTransaction.getAreaAndDistrict(pincode);
		if(mapping != null) {
			result.success = true;
			result.dto = new ErpPincodeDTO();
			result.dto.block  = !Utils.isNullOrEmpty(mapping.get("block")) ? mapping.get("block").toString() : "";
			result.dto.district = !Utils.isNullOrEmpty(mapping.get("district")) ? mapping.get("district").toString() : "";
			result.dto.hostelId = Integer.valueOf(!Utils.isNullOrEmpty(mapping.get("erp_pincode_id")) ? mapping.get("erp_pincode_id").toString() : "");
			result.dto.setErpCity(new SelectDTO());
			result.dto.getErpCity().setLabel(!Utils.isNullOrEmpty(mapping.get("city_name")) ? mapping.get("city_name").toString() : "");
			result.dto.getErpCity().setValue(!Utils.isNullOrEmpty(mapping.get("erp_city_id")) ? mapping.get("erp_city_id").toString() : "");
			result.dto.setState(new SelectDTO());
			result.dto.getState().setLabel(!Utils.isNullOrEmpty(mapping.get("state_name")) ? mapping.get("state_name").toString() : "");
			result.dto.getState().setValue(!Utils.isNullOrEmpty(mapping.get("erp_state_id")) ? mapping.get("erp_state_id").toString() : "");

		}
	}

	public ApiResult<List<LookupItemDTO>> getSemesters() {
		ApiResult<List<LookupItemDTO>> semesters = new ApiResult<List<LookupItemDTO>>();
		try {
			int min=1;
			int max=10;
			semesters.dto = new ArrayList<>();
			for (int i = min; i <= max; i++) {
				LookupItemDTO itemInfo = new LookupItemDTO();
				itemInfo.value = String.valueOf(i);
				itemInfo.label = String.valueOf(i);
				semesters.dto.add(itemInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return semesters;
	}

	public void getProgramPreference(ApiResult<List<ProgramPreferenceDTO>> result,Integer yearValue ) throws Exception {
		List<Tuple> list = commonApiTransaction.getProgramPreference(yearValue );
		if(!Utils.isNullOrEmpty(list)) {
			if(list != null && list.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				String campusOrLocation = "";
				for (Tuple tuple : list) {
					if(!Utils.isNullOrEmpty(tuple.get("MappingId"))) {
						ProgramPreferenceDTO itemInfo = new ProgramPreferenceDTO();
						campusOrLocation = "";
						if(!Utils.isNullOrEmpty(tuple.get("ProgramID"))) {
							if(!Utils.isNullOrEmpty(tuple.get("ProgramOption")) && tuple.get("ProgramOption").toString().trim().equals("C")) {
								itemInfo.preferenceOption = 'C';
								campusOrLocation =  !Utils.isNullOrEmpty(tuple.get("CampusName")) ? tuple.get("CampusName").toString() : "";
								itemInfo.campusMappingId = String.valueOf(tuple.get("MappingId"));
							}else if(!Utils.isNullOrEmpty(tuple.get("ProgramOption")) && tuple.get("ProgramOption").toString().trim().equals("L")) {
								itemInfo.preferenceOption = 'L';
								campusOrLocation =  !Utils.isNullOrEmpty(tuple.get("LocName")) ? tuple.get("LocName").toString() : "";
								itemInfo.campusMappingId =  String.valueOf(tuple.get("MappingId"));
							}
							itemInfo.programId = !Utils.isNullOrEmpty(tuple.get("ProgramID")) ? tuple.get("ProgramID").toString() : "";
							itemInfo.value = !Utils.isNullOrEmpty(tuple.get("MappingId")) ? tuple.get("MappingId").toString() : "";
							itemInfo.label = !Utils.isNullOrEmpty(tuple.get("ProgramName")) ? tuple.get("ProgramName").toString()+" ("+campusOrLocation+")" : "";
							result.dto.add(itemInfo);
						}
					}
				}
			}
		}
	}


	public void getBlocks(ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> mappings = commonApiTransaction.getBlocks();
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getPaymentMode(ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> mappings = commonApiTransaction.getPaymentMode();
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}


	public void getSelectionProcessType(ApiResult<List<AdmissionSelectionProcessTypeDTO>> result) throws Exception {
		AdmissionSelectionProcessTypeDTO dto = null;
		List<Tuple> data = commonApiTransaction.getSelectionProcessType();
		if(data != null && data.size() > 0) {
			result.success = true;
			result.dto = new ArrayList<>();
			for(Tuple mapping : data) {
				dto = new AdmissionSelectionProcessTypeDTO();
				dto.id = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
				dto.selectionProcessName = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString():"";
				dto.mode = !Utils.isNullOrEmpty(mapping.get("Mode")) ? mapping.get("Mode").toString():"";
				dto.isShortist = !Utils.isNullOrEmpty(mapping.get("ShortList")) ? Boolean.parseBoolean(mapping.get("ShortList").toString()):null;
				result.dto.add(dto);
			}
		}
	}

	public void getVenueCityList(ApiResult<List<AdmSelectionProcessVenueCityDTO>> result) throws Exception {
		AdmSelectionProcessVenueCityDTO dto = null;
		List<AdmSelectionProcessVenueCityDBO> data = commonApiTransaction.getVenueCityList();
		if(data != null && data.size() > 0) {
			result.success = true;
			result.dto = new LinkedList<>();
			for(AdmSelectionProcessVenueCityDBO dbo : data) {
				if(!Utils.isNullOrEmpty(dbo.selectionProcessMode)) {
					dto = new AdmSelectionProcessVenueCityDTO();
					dto.id = !Utils.isNullOrEmpty(dbo.id) ? String.valueOf(dbo.id) : "";
					dto.venue = !Utils.isNullOrEmpty(dbo.venueName) ? String.valueOf(dbo.venueName) : "";
					dto.maxSeats = !Utils.isNullOrEmpty(dbo.venueMaxSeats) ?  String.valueOf(dbo.venueMaxSeats) : "";
					ExModelBaseDTO stateModel = new ExModelBaseDTO();
					stateModel.id =  !Utils.isNullOrEmpty(dbo.erpStateDBO) && !Utils.isNullOrEmpty(dbo.erpStateDBO.id) ?  String.valueOf(dbo.erpStateDBO.id) : "";
					stateModel.text = !Utils.isNullOrEmpty(dbo.erpStateDBO) && !Utils.isNullOrEmpty(dbo.erpStateDBO.stateName) ?  String.valueOf(dbo.erpStateDBO.stateName) : "";
					dto.state = stateModel;
					ExModelBaseDTO modeModel = new ExModelBaseDTO();
					modeModel.id =   !Utils.isNullOrEmpty(dbo.id) ?  String.valueOf(dbo.id) : "";
					modeModel.text = !Utils.isNullOrEmpty(dbo.selectionProcessMode) ?  String.valueOf(dbo.selectionProcessMode) : "";
					dto.mode = modeModel;
					result.dto.add(dto);
				}
			}
		}

	}

	public void getTemplateNamesforaGroup(ApiResult<List<LookupItemDTO>> result,String groupCode) throws Exception {
		List<Tuple> mappings = commonApiTransaction.getTemplateNamesforaGroup(groupCode);
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public List<LookupItemDTO> getAllAccAccount() throws Exception {
		List<LookupItemDTO> allAccountsData = new ArrayList<>();
		List<Tuple> list;
		list = commonApiTransaction.getAllAccAccount();
		for(Tuple dbo : list) {
			LookupItemDTO accDto = new LookupItemDTO();
			accDto.value = String.valueOf(dbo.get("ID"));
			accDto.label = dbo.get("label")+" ("+dbo.get("name")+")";
			allAccountsData.add(accDto);
		}
		return allAccountsData;
	}

	public LookupItemDTO getCurrentAcademicYear() throws Exception {
		LookupItemDTO academicYear = new LookupItemDTO();
		ErpAcademicYearDBO dbo = commonApiTransaction.getCurrentAcademicYearDBO();
		academicYear.value = String.valueOf(dbo.id);
		academicYear.label = dbo.academicYearName;
		return academicYear;
	}

	/*public List<ErpUsersPreferredCampusDTO> getPreferredCampus(String id) throws Exception {
		List<ErpUsersPreferredCampusDTO> erpUsersPreferredCampusDTOS = new ArrayList<>();
		List<Tuple> sysUserRoleMapDBOS = commonApiTransaction.getUserRoleMapCampusByUserId(id);
		if(!Utils.isNullOrEmpty(sysUserRoleMapDBOS)){
			for (Tuple sysRole: sysUserRoleMapDBOS){
				ErpUsersPreferredCampusDTO erpUsersPreferredCampusDTO = new ErpUsersPreferredCampusDTO();
				if(!Utils.isNullOrEmpty(sysRole.get("erp_campus_id"))){
					erpUsersPreferredCampusDTO.campusId = String.valueOf(sysRole.get("erp_campus_id"));
				}
				if(!Utils.isNullOrEmpty(sysRole.get("record_status")) && sysRole.get("record_status").equals('A')) {
					erpUsersPreferredCampusDTO.isPreferred = true;
				}else {
					erpUsersPreferredCampusDTO.isPreferred = false;
				}
				if(!Utils.isNullOrEmpty(sysRole.get("short_name"))){
					erpUsersPreferredCampusDTO.shortName = String.valueOf(sysRole.get("short_name"));
				}
				if(!Utils.isNullOrEmpty(sysRole.get("erp_users_campus_id"))){
					erpUsersPreferredCampusDTO.id = String.valueOf(sysRole.get("erp_users_campus_id"));
				}
				else{
					erpUsersPreferredCampusDTO.id= String.valueOf(0);
				}
				erpUsersPreferredCampusDTO.userId = id;
				erpUsersPreferredCampusDTOS.add(erpUsersPreferredCampusDTO);
			}
		}
		return erpUsersPreferredCampusDTOS;
	}*/

	/*public ApiResult<ModelBaseDTO> savePreferredCampus(@NotNull List<ErpUsersPreferredCampusDTO> campusList, String userId)throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		List <ErpUsersCampusDBO> dboList = new ArrayList<>();
		List<ErpUsersCampusDBO> erpUsersPreferredCampusDBOList = commonApiTransaction.getUserCampusPreferenceByUserId(userId);
		Map<Integer, ErpUsersCampusDBO> map  = new HashMap<>();
		for(ErpUsersCampusDBO bo : erpUsersPreferredCampusDBOList) {
			if(!Utils.isNullOrEmpty(bo.id)) {
				map.put(bo.id, bo);
			}
		}
		for( ErpUsersPreferredCampusDTO dto : campusList) {
			ErpUsersCampusDBO dboObject  = null;
			if(!Utils.isNullOrEmpty(dto.id) && map.containsKey(Integer.parseInt(dto.id))) {
				dboObject  = map.get(Integer.parseInt(dto.id));
				dboObject.modifiedUsersId = Integer.parseInt(userId);
			}
			else {
				dboObject = new ErpUsersCampusDBO();
				dboObject.createdUsersId = Integer.parseInt(userId);
			}
			if(dto.isPreferred){
				dboObject.recordStatus = 'A';
			}else{
				dboObject.recordStatus = 'D';
			}
			if (!Utils.isNullOrEmpty(dto.campusId)) {
				ErpCampusDBO campus = new ErpCampusDBO();
				campus.id = Integer.parseInt(dto.campusId);
				dboObject.erpCampusDBO = campus;
			}
			if (!Utils.isNullOrEmpty(dto.userId)) {
				ErpUsersDBO usersDBO = new ErpUsersDBO();
				usersDBO.id = Integer.parseInt(dto.userId);
				dboObject.ErpUsersDBO = usersDBO;
			} else {
				ErpUsersDBO usersDBO = new ErpUsersDBO();
				usersDBO.id = Integer.parseInt(userId);
				dboObject.ErpUsersDBO = usersDBO;
			}
			dboList.add(dboObject);
		}
		Boolean isSaved = commonApiTransaction.savePreferredCampus(dboList, userId);
		if (isSaved) {
			result.success = true;
			result.dto = new ModelBaseDTO();
		}
		return result;
	}*/

	public List<LookupItemDTO> getSysRoleGroup() throws Exception {
		List<LookupItemDTO> sysRoleGroup = new ArrayList<>();
		List<Tuple> list = null;
		list = commonApiTransaction.getSysRoleGroup();
		for(Tuple dbo : list) {
			LookupItemDTO roleDto = new LookupItemDTO();
			roleDto.value = String.valueOf(dbo.get("ID"));
			roleDto.label = String.valueOf(dbo.get("Name"));
			sysRoleGroup.add(roleDto);
		}
		return sysRoleGroup;
	}

	public List<LookupItemDTO> getRoles() throws Exception {
		List<LookupItemDTO> sysRoleList = new ArrayList<>();
		List<Tuple> list = null;
		list = commonApiTransaction.getRoles();
		for(Tuple dbo : list) {
			LookupItemDTO roleDto = new LookupItemDTO();
			roleDto.value = String.valueOf(dbo.get("ID"));
			roleDto.label = String.valueOf(dbo.get("Name"));
			sysRoleList.add(roleDto);
		}
		return sysRoleList;
	}

//	@SuppressWarnings("unused")
//	public List<SysRoleDTO> getUserAndRoles() throws Exception {
//		List<SysRoleDTO> roleDtoList = null;
//		SysRoleDTO roleDto = null;
//		Set<String> roleName = null;
//		List<ErpUsersDBO> list = null;
//		list = commonApiTransaction.getUserAndRoles();
//		List<Integer> userIds = null;
//		if(!Utils.isNullOrEmpty(list)) {
//			userIds = new ArrayList<Integer>();
//			roleDtoList = new ArrayList<SysRoleDTO>();
//			for(ErpUsersDBO dbo : list) {
//				if(!userIds.contains(dbo.id)) {
//					userIds.add(dbo.id);
//					roleDto = new SysRoleDTO();
//					roleDto.id = String.valueOf(dbo.id);
//					roleDto.userName = dbo.loginId;
//					roleDto.setRecordStatus(dbo.getRecordStatus());
//					if(!Utils.isNullOrEmpty(dbo.sysUserRoleMapDBOs)) {
//						roleName = new HashSet<String>();
//						for (SysUserRoleMapDBO role : dbo.sysUserRoleMapDBOs) {
//							if(!Utils.isNullOrEmpty(role) && !Utils.isNullOrEmpty(role.sysRoleDBO)
//									&& !Utils.isNullOrEmpty(role.sysRoleDBO.roleName) && !Utils.isNullOrEmpty(role.recordStatus) && role.recordStatus == 'A') {
//								roleName.add(role.sysRoleDBO.roleName);
//							}
//						}
//						if(!Utils.isNullOrEmpty(roleDto)) {
//							roleDto.roles = roleName;
//						}
//					}
//					roleDtoList.add(roleDto);
//				}
//			}
//		}
//		Collections.sort(roleDtoList);
//		return roleDtoList;
//	}

	public List<ErpUsersDTO> getEmployeesAndUsers(String employeeId, boolean isActive) {
		List<ErpUsersDTO> dtoList = new ArrayList<ErpUsersDTO>();
//		List<ErpUsersDBO> dboList = null;
		try {
			if(!Utils.isNullOrEmpty(employeeId))
				dtoList = commonApiTransaction.getEmployeesAndUsers(employeeId, isActive);
//			dboList = commonApiTransaction.getEmployeesAndUsers(employeeId, isActive);
//			if(!Utils.isNullOrEmpty(dboList)) {
//				for (ErpUsersDBO erpUsersDBO : dboList) {
//					ErpUsersDTO  erpUsersDTO= new ErpUsersDTO();
//					if(!Utils.isNullOrEmpty(erpUsersDBO.id)) {
//						erpUsersDTO.id = String.valueOf(erpUsersDBO.id);
//						if(!Utils.isNullOrEmpty(erpUsersDBO.empDBO)) {
//							LookupItemDTO itemDTO = new LookupItemDTO();
//							itemDTO.value = !Utils.isNullOrEmpty(erpUsersDBO.empDBO.id) ? String.valueOf(erpUsersDBO.empDBO.id) : "";
//							itemDTO.label = !Utils.isNullOrEmpty(erpUsersDBO.empDBO.empName) ? erpUsersDBO.empDBO.empName : "";
//							erpUsersDTO.employee = itemDTO;
//						}
//						erpUsersDTO.userName = !Utils.isNullOrEmpty(erpUsersDBO.userName) ? erpUsersDBO.userName : "";
//					}
//					dtoList.add(erpUsersDTO);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dtoList;
	}

	public List<ErpCampusDTO> getCampusDetails() {
		List<ErpCampusDTO> dtoList = new ArrayList<ErpCampusDTO>();
		List<ErpCampusDBO> dboList = null;
		try {
			dboList = commonApiTransaction.getCampusDetails();
			if(!Utils.isNullOrEmpty(dboList)) {
				for (ErpCampusDBO erpCampusDBO : dboList) {
					ErpCampusDTO  erpCampusDTO = new ErpCampusDTO();
					if(!Utils.isNullOrEmpty(erpCampusDBO.id)) {
						erpCampusDTO.id = String.valueOf(erpCampusDBO.id);
						erpCampusDTO.campusName = !Utils.isNullOrEmpty(erpCampusDBO.campusName) ? String.valueOf(erpCampusDBO.campusName) : "";
						erpCampusDTO.shortName = !Utils.isNullOrEmpty(erpCampusDBO.shortName) ? erpCampusDBO.shortName : "";
					}
					dtoList.add(erpCampusDTO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dtoList;

	}

	public ApiResult<List<LookupItemDTO>> getEmployees(ApiResult<List<LookupItemDTO>> employeeOrusersList) throws Exception {
		return commonApiTransaction.getEmployees();
	}

	public Map<Integer, Map<String,String>> getUserDataForRedis() {
		Map<Integer,Map<String,String>> newMap = new HashMap<>();//userId,accessToken,campusIds
		try {
			Map<Integer, Map<Integer, Map<Integer, String>>> userCampusFunctionAccessTokenMap = commonApiHelper.getUserCampusFunctionAccessTokenMap();
			if(!Utils.isNullOrEmpty(userCampusFunctionAccessTokenMap)) {
				userCampusFunctionAccessTokenMap.forEach((usersId, value)-> {
					value.forEach((campusId, value1) -> {
						value1.forEach((functionId,accessToken)-> {
							//userId-accesstoken-campusId
							if(newMap.containsKey(usersId)) {
								Map<String,String> subMap = newMap.get(usersId);
								if(subMap.containsKey(accessToken)) {
									String campusIds = subMap.get(accessToken);
									campusIds+=","+campusId;
									subMap.put(accessToken, campusIds);
									newMap.put(usersId,subMap);
								}
								else {
									subMap.put(accessToken, String.valueOf(campusId));
									newMap.put(usersId,subMap);
								}
							}
							else {
								Map<String,String> subMap = new HashMap<>();
								subMap.put(accessToken, String.valueOf(campusId));
								newMap.put(usersId,subMap);
							}
						});
					});
				});
			}
		}
		catch (Exception e){e.printStackTrace();};
		return newMap;
	}

	public List<ErpCampusProgrammeMappingDTO> getCampusProgrammeMapping() throws Exception {
		ErpCampusProgrammeMappingDTO campusProgrammeMappingDTO = null;
		List<ErpCampusProgrammeMappingDTO> campusProgrammeMappingDTOs= null;
		List<Tuple> list = commonApiTransaction.getCampusProgrammeMapping();
		if(!Utils.isNullOrEmpty(list)) {
			campusProgrammeMappingDTOs = new ArrayList<ErpCampusProgrammeMappingDTO>();
			for (Tuple tuple : list) {
				if(!Utils.isNullOrEmpty(tuple.get("MappingId"))) {
					campusProgrammeMappingDTO = new ErpCampusProgrammeMappingDTO();
					campusProgrammeMappingDTO.mappingId = tuple.get("MappingId").toString();
					campusProgrammeMappingDTO.combinedName =  !Utils.isNullOrEmpty(tuple.get("CombinedName"))?tuple.get("CombinedName").toString():"";
					if(!Utils.isNullOrEmpty(tuple.get("ProgramId")) && !Utils.isNullOrEmpty(tuple.get("ProgrameName"))) {
						ExModelBaseDTO programModelBaseDTO = new ExModelBaseDTO();
						programModelBaseDTO.id = tuple.get("ProgramId").toString();
						programModelBaseDTO.text = tuple.get("ProgrameName").toString();
						campusProgrammeMappingDTO.program = programModelBaseDTO;
					}
					if(!Utils.isNullOrEmpty(tuple.get("CampusId")) && !Utils.isNullOrEmpty(tuple.get("CampusName"))) {
						ExModelBaseDTO campusModelBaseDTO = new ExModelBaseDTO();
						campusModelBaseDTO.id = tuple.get("CampusId").toString();
						campusModelBaseDTO.text = tuple.get("CampusName").toString();
						campusProgrammeMappingDTO.campus = campusModelBaseDTO;
					}else if(!Utils.isNullOrEmpty(tuple.get("LocId")) && !Utils.isNullOrEmpty(tuple.get("LocName"))) {
						ExModelBaseDTO locationModelBaseDTO = new ExModelBaseDTO();
						locationModelBaseDTO.id = tuple.get("LocId").toString();
						locationModelBaseDTO.text = tuple.get("LocName").toString();
						campusProgrammeMappingDTO.location = locationModelBaseDTO;
					}
					campusProgrammeMappingDTOs.add(campusProgrammeMappingDTO);
				}
			}
		}
		return campusProgrammeMappingDTOs;
	}

	public Flux<LookupItemDTO> getProgrammeLevel() {
		return  commonApiTransaction1.getProgrammeLevel()
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertAdmQualificationListDBOtoDTO);
	}

	public Flux<LookupItemDTO> getProgrammeDegreeByLevel(int id) {
		return  commonApiTransaction1.getProgrammeDegreeByLevel(id)
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertErpProgrammeDegreeDTOtoDTO);
	}

	public Flux<LookupItemDTO> getProgrammeByDegreeAndLevel(int degreeId) {
		return  commonApiTransaction1.getProgrammeByDegreeAndLevel(degreeId)
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertProgrammeDBOtoDTO);
	}

	public Flux<LookupItemDTO> getErpReservationCategory() {
		return  commonApiTransaction1.getErpReservationCategory()
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertErpReservationCategoryDBOtoDTO);
	}

	public Flux<LookupItemDTO> getErpInstitution() {
		return  commonApiTransaction1.getErpInstitution()
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertErpInstitutionDBOtoDTO);
	}

	public Flux<LookupItemDTO> getErpResidentCategory() {
		return  commonApiTransaction1.getErpResidentCategory()
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertErpResidentCategoryDBOtoDTO);
	}

	public Flux<ProgramPreferenceDTO> getProgramPreferenceByProgram(int programId) {
		return  commonApiTransaction1.getProgramPreferenceByProgram(programId)
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertProgramPreferenceByProgramDBOtoDTO);
	}

	public Flux<ErpAcademicYearDTO> getAcademicYear() {
		return  commonApiTransaction1.getAcademicYear()
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertAcademicYearDBOtoDTO);
	}

	public Flux<LookupItemDTO> getStudentApplicationNumbers1(String applicationNumber, String yearId) {
		return commonApiTransaction1.getStudentApplicationNumbers1(applicationNumber, yearId)
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertStudentApplicationNumbersToDTO);
	}
	//
	//	public Flux<LookupItemDTO> getStudentNames(String studentName) {
	//		return commonApiTransaction1.getStudentNames(studentName)
	//			.flatMapMany(Flux::fromIterable)
	//			.map(commonApiHelper::convertStudentApplicationNamesToDTO);
	//	}

	public void getStudentApplicationNumbers(String applicationNumber, ApiResult<List<LookupItemDTO>> list, String yearId) {
		List<Tuple> tuples = commonApiTransaction1.getStudentApplicationNumbers(applicationNumber,yearId);
		if(!Utils.isNullOrEmpty(tuples)){
			list.dto = new ArrayList<>();
			tuples.forEach(tuple -> {
				LookupItemDTO itemDTO = new LookupItemDTO();
				if(!Utils.isNullOrEmpty(tuple.get("studentApplnEntriesId")) && !Utils.isNullOrEmpty(tuple.get("applicationNo"))){
					itemDTO.value = String.valueOf(tuple.get("studentApplnEntriesId"));
					itemDTO.label = String.valueOf(tuple.get("applicationNo")+"("+tuple.get("applicantName")+")");
				}
				list.dto.add(itemDTO);
			});
		}
		if(!Utils.isNullOrEmpty(list.dto)){
			list.success = true;
		}
	}

	public void getApplicantNames(String applicantName, ApiResult<List<LookupItemDTO>> list, String yearId) {
		List<Tuple> tuples = commonApiTransaction1.getApplicantNames(applicantName,yearId);
		if(!Utils.isNullOrEmpty(tuples)){
			list.dto = new ArrayList<>();
			tuples.forEach(tuple -> {
				LookupItemDTO itemDTO = new LookupItemDTO();
				if(!Utils.isNullOrEmpty(tuple.get("studentApplnEntriesId")) && !Utils.isNullOrEmpty(tuple.get("applicantName")) && !Utils.isNullOrEmpty(tuple.get("applicationNo"))){
					itemDTO.value = String.valueOf(tuple.get("studentApplnEntriesId"));
					itemDTO.label = String.valueOf(tuple.get("applicantName"))+" ("+String.valueOf(tuple.get("applicationNo"))+")";
				}
				list.dto.add(itemDTO);
			});
		}
		if(!Utils.isNullOrEmpty(list.dto)){
			list.success = true;
		}
	}
	public Mono<SelectDTO> getCampusByEmployee(String userId) {
		return commonApiTransaction1.getCampusByEmployee(userId);
	}

//	public SelectDTO convertErpCampusDepartmentMappingDBOtoDto(ErpUsersDBO dbo) {
//		SelectDTO dto = new SelectDTO();
//		if(!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(dbo.getEmpDBO().getErpCampusDepartmentMappingDBO()) && !Utils.isNullOrEmpty(dbo.getEmpDBO().getErpCampusDepartmentMappingDBO().getErpCampusDBO()))) {
//			if(dbo.getEmpDBO().recordStatus == 'A' && dbo.getEmpDBO().getErpCampusDepartmentMappingDBO().recordStatus == 'A'
//					&& dbo.getEmpDBO().getErpCampusDepartmentMappingDBO().getErpCampusDBO().recordStatus == 'A') {
//				dto.setValue(String.valueOf(dbo.getEmpDBO().getErpCampusDepartmentMappingDBO().getErpCampusDBO().getId()));
//				dto.setLabel(dbo.getEmpDBO().getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName());
//			}
//		}
//		return dto;
//	}

	public Flux<SelectDTO> getLevels() {
		return commonApiTransaction1.getLevels().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}

	public SelectDTO convertDBOToDTO(ErpProgrammeLevelDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getProgrammeLevel());
		}
		return dto;
	}

	public Flux<SelectDTO> getProgrammeByLevelAndCampus(String level, String campus) {
		return commonApiTransaction1.getProgrammeByLevelAndCampus(level,campus).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}
	public Flux<SelectDTO> getProgrammeByLevelOrCampus(String level, String campus) {
		return commonApiTransaction1.getProgrammeByLevelOrCampus(level,campus).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}

	public SelectDTO convertDBOToDTO(Tuple dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.get(0)));
			dto.setLabel(String.valueOf(dbo.get(1)));
		}
		return dto;
	}

	public Flux<LookupItemDTO> getErpWorkFlowProcess() {
		return  commonApiTransaction1.getErpWorkFlowProcess()
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertErpWorkFlowProcessDBOtoDTO);
	}

	public Flux<LookupItemDTO> getSMSorEmailTemplate(String templateType) {
		return  commonApiTransaction1.getSMSorEmailTemplate(templateType)
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertSMSorEmailTemplateDBOtoDTO);
	}

	public Mono<ApiResult<List<LookupItemDTO>>> getAdmissionCategory(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Flux<SelectDTO> getAdmissionCategory() {
		return commonApiTransaction1.getAdmissionCategory().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}

	public SelectDTO convertDBOToDTO(ErpAdmissionCategoryDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAdmissionCategoryName());
		}
		return dto;
	}

	public Flux<SelectDTO> getActiveProgrammeByYearValue(String yearValue) {
		return  commonApiTransaction1.getActiveProgrammeByYearValue(yearValue).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public Flux<SelectDTO> getCampusBySelectedProgramme(String programmeId) {
		return commonApiTransaction1.getCampusBySelectedProgramme(programmeId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public Flux<ProgramPreferenceDTO> getProgramPreferenceWithBatch(String academicYear) {
		return commonApiTransaction1.getProgramPreferenceWithBatch(academicYear).flatMapMany(Flux::fromIterable).map(this::convertProgramPreference);
	}

	public ProgramPreferenceDTO convertProgramPreference(Tuple tuple){
		ProgramPreferenceDTO itemInfo = new ProgramPreferenceDTO();
		if (!Utils.isNullOrEmpty(tuple)) {
			String campusOrLocation = "";
			if(!Utils.isNullOrEmpty(tuple.get("MappingId"))) {
				campusOrLocation = "";
				String batchName = "";
				if(!Utils.isNullOrEmpty(tuple.get("ProgramID"))) {
					if(!Utils.isNullOrEmpty(tuple.get("ProgramOption")) && tuple.get("ProgramOption").toString().trim().equals("C")) {
						itemInfo.preferenceOption = 'C';
						campusOrLocation =  !Utils.isNullOrEmpty(tuple.get("CampusName")) ? tuple.get("CampusName").toString() : "";
						itemInfo.campusMappingId =  tuple.get("MappingId").toString();
					}else if(!Utils.isNullOrEmpty(tuple.get("ProgramOption")) && tuple.get("ProgramOption").toString().trim().equals("L")) {
						itemInfo.preferenceOption = 'L';
						campusOrLocation =  !Utils.isNullOrEmpty(tuple.get("LocName")) ? tuple.get("LocName").toString() : "";
						itemInfo.campusMappingId =  tuple.get("MappingId").toString();
					}
					if(!Utils.isNullOrEmpty(tuple.get("batchName"))) {
						batchName = tuple.get("batchName").toString();
					}
					itemInfo.acaBatchId = !Utils.isNullOrEmpty(tuple.get("acaBatchId")) ? tuple.get("acaBatchId").toString() : "";
					itemInfo.programId = !Utils.isNullOrEmpty(tuple.get("ProgramID")) ? tuple.get("ProgramID").toString() : "";
					itemInfo.value = !Utils.isNullOrEmpty(tuple.get("MappingId")) ? tuple.get("MappingId").toString() : "";
					//itemInfo.label = !Utils.isNullOrEmpty(tuple.get("ProgramName")) ? tuple.get("ProgramName").toString()+" ("+campusOrLocation+")" : "";
					itemInfo.label = !Utils.isNullOrEmpty(tuple.get("ProgramName")) ? !Utils.isNullOrEmpty(batchName) ? (tuple.get("ProgramName").toString()+" ("+batchName+")") :
						(tuple.get("ProgramName").toString()+" ("+campusOrLocation+")") : "";
				}
			}
		}
		return itemInfo;
	}
	//	public SelectDTO convertDBOToDTO(Tuple dbo){
	//		SelectDTO dto = new SelectDTO();
	//		if (!Utils.isNullOrEmpty(dbo)) {
	//			dto.setValue(String.valueOf(dbo.get(0)));
	//			dto.setLabel(dbo.get(1).toString());
	//		}
	//		return dto;
	//	}


	public Flux<SelectDTO> getDegreeByLevelAndLocationOrCampus(String levelId,List<Integer> campusId,List<Integer> locationId) {
		return commonApiTransaction1.getDegreeByLevelAndLocationOrCampus(levelId,campusId,locationId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public Flux<SelectDTO>getProgrammeByDegreeAndLocationOrCampus(String degreeId,List<Integer> campusId, List<Integer> locationId) {
		return commonApiTransaction1.getProgrammeByDegreeAndLocationOrCampus(degreeId,campusId,locationId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public Flux<SelectDTO> getRegisterNumbers(String registerNumber, String yearId) {
		return commonApiTransaction1.getRegisterNumbers(registerNumber, yearId)
				.flatMapMany(Flux::fromIterable)
				.map(commonApiHelper::convertRegisterNumbersToDTO);	
	}

	public Flux<StudentApplnEntriesDTO> getDataByApplnNoOrRegisterNumbersOrName(String data, String yearId) {
		return commonApiTransaction1.getDataByApplnNoOrRegisterNumbersOrName(data, yearId).flatMapMany(Flux::fromIterable).map(this::convertfilterDboToDto); 
	}

	private StudentApplnEntriesDTO convertfilterDboToDto(Tuple dbo) {
		StudentApplnEntriesDTO dto = new StudentApplnEntriesDTO();
		if(!Utils.isNullOrEmpty(dbo.get("studentApplnEntriesId"))) {
			dto.setId(Integer.parseInt(dbo.get("studentApplnEntriesId").toString()));
		}
		if(!Utils.isNullOrEmpty(dbo.get("applicationNo"))) {
			dto.setApplicationNumber(dbo.get("applicationNo").toString());
		}
		if(!Utils.isNullOrEmpty(dbo.get("applicantName"))) {
			dto.setApplicantName(dbo.get("applicantName").toString());
		}
		if(!Utils.isNullOrEmpty(dbo.get("register_no"))) {
			dto.setStudent(new StudentDTO());
			dto.getStudent().setRegisterNo(dbo.get("register_no").toString());
			dto.getStudent().setStudentName(dbo.get("student_name").toString());
		}
		return dto;
	}

	public Flux<SelectDTO> getCampusForLocation(String locId) {
		List<ErpCampusDBO> list = commonApiTransaction1.getCampusForLocation(locId); 
		return convertDboToDto(list);
	}
	private Flux<SelectDTO> convertDboToDto(List<ErpCampusDBO> list ) {
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setLabel(data.campusName);
				selectDTO.setValue(String.valueOf(data.getId()));
				selectDTOList.add(selectDTO);
			});
		}
		return Flux.fromIterable(selectDTOList);
	}

	public Mono<ApiResult> isUserAdded(Integer userId) {
		var apiResult = new ApiResult();
		boolean result = commonApiTransaction1.isUserAdded(userId);
		if(result) {
			apiResult.setFailureMessage("User Already added for a title");	
		} else {
			apiResult.setSuccess(true);		
	}
	return Mono.just(apiResult);	
	}
	
	public Flux<Object> getPincodeDetailsOrValidate(String pincode, Boolean isPincodeList) {
		var pinCodeDboList = commonApiTransaction1.getPincodeDetailsOrValidate(pincode);
		if(!Utils.isNullOrEmpty(pinCodeDboList)) {
			return Flux.fromIterable(convertPinCodeDbotoDto(pinCodeDboList,isPincodeList));
		}
		else {
			return  this.getUrlresponse(pincode).map(p->saveAndGetPincode(p)).map(s-> convertPinCodeDbotoDto(s,isPincodeList)).flatMapMany(Flux::fromIterable);				
		}
	}

	private ArrayList<Object> convertPinCodeDbotoDto(List<ErpPincodeDBO> pinCodeDboList, Boolean isPincodeList) {
		if(isPincodeList) {
			List picodeList = new ArrayList<SelectDTO>();
			pinCodeDboList.forEach(s->{
				if(!Utils.isNullOrEmpty(s)) {
					if(!Utils.isNullOrEmpty(s.getId())) {
						var selectDTO = new SelectDTO();
						selectDTO.setValue(String.valueOf(s.getId()));
						if(!Utils.isNullOrEmpty(s.getPincode()) && (!Utils.isNullOrEmpty(s.getPostoffice()))) {
							selectDTO.setLabel(s.getPincode() +" - "+ s.getPostoffice());
							picodeList.add(selectDTO);
						}
					}
				}
			});
			return (ArrayList<Object>) picodeList;
		} else {
			List cityList = new ArrayList<ErpCityDTO>();
			var set =  pinCodeDboList.stream().map(s->s.getErpCityDBO()).collect(Collectors.toSet());
			set.forEach(s->{
				if(!Utils.isNullOrEmpty(s)) {
					if(!Utils.isNullOrEmpty(s.getCityName()) && !Utils.isNullOrEmpty(s.getId())) {
						var ctyDto = new ErpCityDTO();
						ctyDto.setCityName(s.getCityName());
						ctyDto.setId(s.getId());
						if(!Utils.isNullOrEmpty(s.getErpStateDBO())) {
							ctyDto.setErpStateDTO(new SelectDTO());
							ctyDto.getErpStateDTO().setLabel(s.getErpStateDBO().getStateName());
							ctyDto.getErpStateDTO().setValue(String.valueOf(s.getErpStateDBO().getId()));
						}
						cityList.add(ctyDto);
					}
				}
			});	
			return (ArrayList<Object>) cityList;
		}
	}
	
	public  Mono<LinkedHashMap> getUrlresponse(String pincode) {
		return postalDatas
				.get()
				.uri(uribuilder -> uribuilder.queryParam("api-key","579b464db66ec23bdd000001fc658bb8705e47c577e753c8a76daa41")
						.queryParam("format", "json")
						.queryParam("limit", 50)
						.queryParam("filters[pincode]",pincode)
						.build())				
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve()
				.bodyToMono(LinkedHashMap.class);
	}
    

	public List<ErpPincodeDBO> saveAndGetPincode(LinkedHashMap response) {
		var result = new ArrayList<LinkedHashMap>();
		String userId = redisSysPropertiesData.getSysProperties(SysProperties.COMMON_RECRUITMENT_USER_ID.name(), null, null); 
		var indiaId = commonApiTransaction1.getIndiaId();
		var pincodeDboList = new ArrayList<ErpPincodeDBO>();
		if(!Utils.isNullOrEmpty(response)) {
			if(!Utils.isNullOrEmpty(response.get("records"))) {
				result = (ArrayList<LinkedHashMap>) response.get("records");
				if(!Utils.isNullOrEmpty(result)) {
					result.forEach(s-> {
						if(!Utils.isNullOrEmpty(s)) {
							var pinCodeDbo = new ErpPincodeDBO();
							pinCodeDbo.setCircle(String.valueOf(s.get("circlename")));
							pinCodeDbo.setRegion(String.valueOf(s.get("regionname")));
							pinCodeDbo.setDivision(String.valueOf(s.get("divisionname")));
							pinCodeDbo.setPostoffice(String.valueOf(s.get("officename")));
							pinCodeDbo.setPincode(String.valueOf(s.get("pincode")));
							pinCodeDbo.setBranchType(String.valueOf(s.get("officetype")));
							pinCodeDbo.setDeliveryStatus(String.valueOf(s.get( "delivery")));
							pinCodeDbo.setDistrict(String.valueOf(s.get("district")));
							pinCodeDbo.setState(String.valueOf(s.get("statename")));
							pinCodeDbo.setLatitude(String.valueOf(s.get("latitude")));
							pinCodeDbo.setLongitude(String.valueOf(s.get( "longitude")));
							pinCodeDbo.setCountry("INDIA");
							pinCodeDbo.setErp_country_id(indiaId);
							pinCodeDbo.setRecordStatus('A');
							if(!Utils.isNullOrEmpty(userId)) {
								pinCodeDbo.setCreatedUsersId(Integer.parseInt(userId));
							}
							pincodeDboList.add(pinCodeDbo);
						}
					});
					Set<String> districtSet = new LinkedHashSet<String>();   
					Set<String> stateSet = new LinkedHashSet<String>();        
					Map<String, Set<String>> stateDistrict = pincodeDboList.stream()
							.peek(s-> {
								stateSet.add(s.getState().toUpperCase());
								districtSet.add(s.getDistrict().toUpperCase());
							})
							.collect(Collectors.groupingBy(
									ErpPincodeDBO::getState, 
									Collectors.mapping(ErpPincodeDBO::getDistrict, Collectors.toSet())));		
					var stateList = commonApiTransaction1.getStatesByName(stateSet);
					Set<String> newStatesNames = new LinkedHashSet<String>();
					if(!Utils.isNullOrEmpty(stateList)) {
						newStatesNames = stateSet.stream().filter(s->stateList.stream().noneMatch(p-> p.getStateName().equalsIgnoreCase(s))).collect(Collectors.toSet());					
					} else {
						newStatesNames = stateSet;
					}
					var newStateDBOSet = new LinkedHashSet<ErpStateDBO>();
					if(!Utils.isNullOrEmpty(newStatesNames)) {
						newStatesNames.forEach(p-> {
							if(!Utils.isNullOrEmpty(p)) {
								var newStateDbo = new ErpStateDBO();
								newStateDbo.setStateName(p.toUpperCase());
								if(!Utils.isNullOrEmpty(userId)) {
									newStateDbo.setCreatedUsersId(Integer.parseInt(userId));
								}
								newStateDbo.setErpCountryDBO(new ErpCountryDBO());
								newStateDbo.getErpCountryDBO().setId(indiaId);
								newStateDbo.setRecordStatus('A');    
								newStateDBOSet.add(newStateDbo);
							}
						});
						stateList.addAll(commonApiTransaction1.saveNewState(newStateDBOSet));
					}      
					var cityList = commonApiTransaction1.selectCityByNames(districtSet,stateSet); 
					Set<Entry<String, Set<String>>> newDistrict = new LinkedHashSet<Map.Entry<String,Set<String>>>();
					if(!Utils.isNullOrEmpty(cityList)) {
						newDistrict = stateDistrict.entrySet().stream().filter(s->cityList.stream().noneMatch(p->
						p.getErpStateDBO().getStateName().equalsIgnoreCase(s.getKey()) && s.getValue().contains(p.getCityName().toUpperCase()))).collect(Collectors.toSet());
					} else {
						newDistrict = stateDistrict.entrySet();
					}
					var newDistrictDBOSet = new LinkedHashSet<ErpCityDBO>();
					var stateId = stateList.stream().collect(Collectors.toMap(s->s.getStateName().toUpperCase(), s->s));
					if(!Utils.isNullOrEmpty(newDistrict)) {
						newDistrict.forEach(s-> {
							if(!Utils.isNullOrEmpty(s)) {
								s.getValue().forEach(p-> {
									if(!Utils.isNullOrEmpty(p)) {
										var cityDbo = new ErpCityDBO();
										cityDbo.setCityName(p.toUpperCase());
										if(!Utils.isNullOrEmpty(userId)) {
											cityDbo.setCreatedUsersId(Integer.parseInt(userId));
										}
										if(!Utils.isNullOrEmpty(stateId)) {
											if(!Utils.isNullOrEmpty(s.getKey())) {
												cityDbo.setErpStateDBO(new ErpStateDBO());
												cityDbo.setErpStateDBO(stateId.get(s.getKey().toUpperCase()));
											}
										}
										cityDbo.setRecordStatus('A');
										newDistrictDBOSet.add(cityDbo);
									}
								});
							}
						});
						cityList.addAll(commonApiTransaction1.saveNewDistricts(newDistrictDBOSet));
					}
					Map<String, Map<String, ErpCityDBO>> map4 = cityList.stream()
							.collect(Collectors.groupingBy(p -> p.getErpStateDBO().getStateName().toUpperCase(), Collectors.toMap(p -> p.getCityName().toUpperCase(), p-> p)));
					pincodeDboList.forEach(s-> {
						if(!Utils.isNullOrEmpty(s)) {
							ErpCityDBO dbo =  !Utils.isNullOrEmpty(map4.get(s.getState().toUpperCase())) ? map4.get(s.getState().toUpperCase()).get(s.getDistrict().toUpperCase()): null;
							if(!Utils.isNullOrEmpty(dbo)) {
								s.setErpCityDBO(new ErpCityDBO());
								s.setErpCityDBO(dbo);
							}
						}
					});				
				}
			}
		}
		if(!pincodeDboList.isEmpty()) {
			return commonApiTransaction1.saveNewPincode(pincodeDboList);		
		} else {
			return pincodeDboList;
		}	
	}

	public Flux<SelectDTO> getErpRoomByBlock(String blockId) {
		return commonApiTransaction1.getErpRoomByBlock(blockId).flatMapMany(Flux::fromIterable).map(this::convertRoomDBOToDTO); 
	}
	
	public SelectDTO convertRoomDBOToDTO(ErpRoomsDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(String.valueOf(dbo.getRoomNo()));
		return dto;
	}
	
	public Flux<SelectDTO> getErpCampusDepartmentMapping() {
		return commonApiTransaction1.getErpCampusDepartmentMapping().flatMapMany(Flux::fromIterable).map(this::convertRoomDBOToDTO); 
	}
	
	public SelectDTO convertRoomDBOToDTO(ErpCampusDepartmentMappingDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(String.valueOf(dbo.getErpDepartmentDBO().getDepartmentName())+"("+dbo.getErpCampusDBO().getCampusName()+")");
		return dto;
	}
	
	public Flux<SelectDTO> getUserSpecificCampusList(String userId) {
		return commonApiTransaction1.getUserSpecificCampusList(userId).flatMapMany(Flux::fromIterable).map(this::convertUserCampusDboToDto); 
	}
	
	public SelectDTO convertUserCampusDboToDto(ErpCampusDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(String.valueOf(dbo.getCampusName()));
		return dto;
	}

	public Flux<SelectDTO> getInstitutionList(String name, String countryId, String stateId, String boardType) {		
		return commonApiTransaction1.getInstitutionList(name,countryId,stateId,boardType).flatMapMany(Flux::fromIterable).map(this::convertInstitutionDboToDto); 
	}
	
	public SelectDTO convertInstitutionDboToDto(Tuple dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.get("NAME"))) {
			dto.setLabel(String.valueOf(dbo.get("NAME")));
		}
		if(!Utils.isNullOrEmpty(dbo.get("ID"))) {
			dto.setValue(String.valueOf(dbo.get("ID")));
		}		
		return dto;
	}

	public Mono<ErpCityDTO> getCityAndStateByPincode(String pincodeId) {
		return commonApiTransaction1.getCityAndStateByPincode(pincodeId).map(s->convertDboToCityDto(s));		 
	}

	private ErpCityDTO convertDboToCityDto(ErpPincodeDBO dbo) {
		var cityDto = new ErpCityDTO();
		cityDto.setId(dbo.getErpCityDBO().getId());
		cityDto.setCityName(dbo.getErpCityDBO().getCityName());
		cityDto.setErpStateDTO(new SelectDTO());
		cityDto.getErpStateDTO().setValue(String.valueOf(dbo.getErpCityDBO().getErpStateDBO().getId()));
		cityDto.getErpStateDTO().setLabel(dbo.getErpCityDBO().getErpStateDBO().getStateName());
		return cityDto;
	}

	public Flux<SelectDTO> getLocationOrCampusByProgramme(String yearValue, String programId, Boolean isLocation) {
	    return commonApiTransaction1.getLocationOrCampusByProgramme(yearValue, programId, isLocation)
	            .flatMapMany(Flux::fromIterable)
	            .map(mapping -> convertLocationDTO(mapping, isLocation)); 
	}

	private SelectDTO convertLocationDTO(ErpCampusProgrammeMappingDBO dbo, Boolean isLocation) {
	    SelectDTO dto = new SelectDTO();
	    if(isLocation && !Utils.isNullOrEmpty(dbo.getErpLocationDBO())) {
	        dto.setValue(String.valueOf(dbo.getErpLocationDBO().getId()));
	        dto.setLabel(dbo.getErpLocationDBO().getLocationName());
	    } else if(!isLocation && !Utils.isNullOrEmpty(dbo.getErpCampusDBO())) {
	        dto.setValue(String.valueOf(dbo.getErpCampusDBO().getId()));
	        dto.setLabel(dbo.getErpCampusDBO().getCampusName());
	    }
	    return dto;
	}
	
	public Flux<SelectDTO> getEmployeeOrUsers1() {
		List<EmpDBO> empDboList = commonApiTransaction1.getEmployeeOrUsers1();
		return this.convertEmployeeDboToDto(empDboList); 
	}

	private Flux<SelectDTO> convertEmployeeDboToDto(List<EmpDBO> empDboList) {
		List<SelectDTO> empList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(empDboList)) {
			empDboList.forEach(emp -> {
				if(!Utils.isNullOrEmpty(emp.getId()) && (!Utils.isNullOrEmpty(emp.getEmpName()))) {
					SelectDTO selectDTO = new SelectDTO();
					selectDTO.setValue(emp.getId().toString());
					selectDTO.setLabel(emp.getEmpName().toString());
					empList.add(selectDTO);
				}
			});
		}
		return Flux.fromIterable(empList);
	}	
	
	public Flux<SelectDTO> getGraduateAttributes() {		
		return commonApiTransaction1.getGraduateAttributes().flatMapMany(Flux::fromIterable).map(this::convertGraduateAttributesDboToDto); 
	}

	public SelectDTO convertGraduateAttributesDboToDto(AcaGraduateAttributesDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(String.valueOf(dbo.getGraduateAttributes())+" ("+dbo.getAcaGraduateLevelsDBO().getGraduateLevels()+")");
		return dto;
	}
	
	public Flux<SelectDTO> getApproverLevel() {		
		return commonApiTransaction1.getApproverLevel().flatMapMany(Flux::fromIterable).map(this::convertErpApprovalLevelsDboToDto); 
	}

	public SelectDTO convertErpApprovalLevelsDboToDto(ErpApprovalLevelsDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(String.valueOf(dbo.getApprover()));
		return dto;
	}
	
	public Flux<SelectDTO> getDepartmentByUser(String userId) {
		return commonApiTransaction1.getDepartmentByUser(userId).flatMapMany(Flux::fromIterable).map(this::convertUserDBOToDTO);
	}

	public SelectDTO convertUserDBOToDTO(Tuple  dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.get("id")));
		dto.setLabel(String.valueOf(dbo.get("deptName")));
		return dto;
	}
	
	public Flux<SelectDTO> getApplicationCancellationReasons() {
		return commonApiTransaction1.getApplicationCancellationReasons().flatMapMany(Flux::fromIterable).map(this::convertUserDBOToDTO);
	}
	
	public SelectDTO convertUserDBOToDTO(StudentApplnCancellationReasonsDBO  dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getReasonName());
		return dto;
	}

	public Flux<ErpTemplateDTO> getErpTemplateByGroupCode(String groupCode) {
		return commonApiTransaction1.getErpTemplateByGroupCode(groupCode).flatMapMany(Flux::fromIterable).map(this::convertTemplateDBOToDTO);
	}
	
	public ErpTemplateDTO convertTemplateDBOToDTO(ErpTemplateDBO dbo) {
		ErpTemplateDTO dto = new ErpTemplateDTO();
		dto.setId(String.valueOf(dbo.getId()));
		dto.setTemplateName(dbo.getTemplateName());
		dto.setTemplateContent(dbo.getTemplateContent());
		return dto;
	}
	
	public Flux<SelectDTO> getUniversityBoardList(Integer countryId, Integer stateId, String boardType, String name) {
		return commonApiTransaction1.getUniversityBoardList(countryId,stateId,boardType,name).flatMapMany(Flux::fromIterable).map(this::getUniversityBoardList);
	}
	
	public SelectDTO getUniversityBoardList(ErpUniversityBoardDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getUniversityBoardName());
		return dto;
	}

	public Mono<SelectDTO> getEmployeemByUserId(String userId) {
		return Mono.just(commonApiTransaction1.getEmployeemByUserId(Integer.parseInt(userId)));
	}

	public Flux<SelectDTO> getSubModulesList() {
		return commonApiTransaction1.getSubModulesList().flatMapMany(Flux::fromIterable).map(this::getSubModuleDboToDto);
	}
	
	public SelectDTO getSubModuleDboToDto(Tuple dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.get("id")));
		dto.setLabel(String.valueOf(dbo.get("subModuleName")).concat(" - " +String.valueOf(dbo.get("moduleName"))));
		return dto;
	}
	public Flux<URLFolderListDTO> getFolderListForMenu(String processCode) {
		return commonApiTransaction1.getFolderListForMenu(processCode).flatMapMany(Flux::fromIterable).map(this::convertToFinalDTO);
	}
	public URLFolderListDTO convertToFinalDTO(UrlFolderListDBO dbo) {
		URLFolderListDTO dto = new URLFolderListDTO();
		//dto.setFolderListId(dbo.getId());
		dto.setUploadProcessCode(dbo.getUploadProcessCode());
		//dto.setTempFolderPath(dbo.getTempFolderPath());
		//dto.setFolderPath(dbo.getFolderPath());
		dto.setFileSizeInKB(dbo.getFileSizeKb());
		if(!Utils.isNullOrEmpty(dbo.getFileType())) {
			List<String> stringList = Arrays.stream(dbo.getFileType().split(","))
	                .map(String::trim)
	                .collect(Collectors.toList());
			dto.setFileTypeList(stringList);
		}
		return dto;
	}
	
	public Flux<SelectDTO> getEmployeeOrUserWithDepartment() {
		List<Tuple> list =  commonApiTransaction1.getEmployeeOrUserWithDepartment();
		return convertEmployeeOrUser(list);
	}
	
	public Flux<SelectDTO> convertEmployeeOrUser(List<Tuple> list) {
//		SelectDTO dto = new SelectDTO();
//		if((!Utils.isNullOrEmpty(dbo.get("erpUserId"))) && ((!Utils.isNullOrEmpty(dbo.get("name"))) && (!Utils.isNullOrEmpty(dbo.get("department"))))){
//			if(dbo.get("erpUserId").toString()!=null) {
//				dto.setValue(String.valueOf(dbo.get("erpUserId")));
//				dto.setLabel(String.valueOf(dbo.get("name")) + (String.valueOf(dbo.get("department"))));		
//			}
//		}
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				if((!Utils.isNullOrEmpty(data.get("erpUserId"))) && ((!Utils.isNullOrEmpty(data.get("name"))) && (!Utils.isNullOrEmpty(data.get("department"))))){
					if(data.get("erpUserId").toString()!=null) {
						SelectDTO dto = new SelectDTO();
						dto.setValue(String.valueOf(data.get("erpUserId")));
						dto.setLabel(String.valueOf(data.get("name")) + (String.valueOf(data.get("department"))));
						selectDTOList.add(dto);
					}
				}
			});
		}
		return Flux.fromIterable(selectDTOList);
	}

	public Mono<SelectDTO> getUserDepartment(String userId) {
		return commonApiTransaction1.getUserDepartment(userId);
	}
	
	public Mono<SelectDTO> getUserLocation(String userId) {
		return commonApiTransaction1.getUserLocation(userId);
	}
	
	public URLFolderListDTO getAllFolderListForMenu(String processCode) {
		return convertToFinalDTOAll(commonApiTransaction1.getFolderListWithProcessCode(processCode));
	}
	public URLFolderListDTO convertToFinalDTOAll(UrlFolderListDBO dbo) {
		URLFolderListDTO dto = new URLFolderListDTO();
		dto.setFolderListId(dbo.getId());
		dto.setUploadProcessCode(dbo.getUploadProcessCode());
		dto.setTempFolderPath(dbo.getTempFolderPath());
		dto.setFolderPath(dbo.getFolderPath());
		dto.setFileSizeInKB(dbo.getFileSizeKb());
		dto.setBucketName(dbo.getUrlAwsConfigDBO().getBucketName());
		dto.setTempBucketName(dbo.getUrlAwsConfigDBO().getTempBucketName());
		dto.setRegion(dbo.getUrlAwsConfigDBO().getRegion());
		dto.setEndPoint(dbo.getUrlAwsConfigDBO().getEndpoint());
		if(!Utils.isNullOrEmpty(dbo.getFileType())) {
			List<String> stringList = Arrays.stream(dbo.getFileType().split(","))
	                .map(String::trim)
	                .collect(Collectors.toList());
			dto.setFileTypeList(stringList);
		}
		return dto;
	}

	public Mono<List<SysRoleDTO>> getUserAndRoles() {
		Map<Integer, SysRoleDTO> list = commonApiTransaction1.getErpUsers().stream().collect(Collectors.toMap(s -> Integer.parseInt(s.id), s -> s));
		Set<Integer> erpUserIds = list.keySet();
		Map<Integer, List<SysUserRoleMapDTO>> userRole = commonApiTransaction1.getRoleNames(erpUserIds).stream().collect(Collectors.groupingBy(s ->Integer.parseInt(s.id)));
		return this.convertDboToDto(list,userRole);
	}
	
	public Mono<List<SysRoleDTO>> convertDboToDto(Map<Integer, SysRoleDTO> list, Map<Integer, List<SysUserRoleMapDTO>> userRole){
		List<SysRoleDTO> roleDtoList = null;
		SysRoleDTO roleDto = null;
		Set<String> roleName = null;
		List<Integer> userIds = null;
		if(!Utils.isNullOrEmpty(list)) {
			userIds = new ArrayList<Integer>();
			roleDtoList = new ArrayList<SysRoleDTO>();
			for (Map.Entry<Integer, SysRoleDTO> entry : list.entrySet()) {
				if(!userIds.contains(entry.getKey())) {
					userIds.add(entry.getKey());
					roleDto = entry.getValue();
					if(!Utils.isNullOrEmpty(userRole)) {
						roleName = new HashSet<String>();
						List<SysUserRoleMapDTO> roleList = userRole.get(entry.getKey());
						if(!Utils.isNullOrEmpty(roleList)) {
							for(SysUserRoleMapDTO role :roleList) {
								roleName.add(role.getRole().getLabel());
							}
							roleDto.setRoles(roleName);
						}
					}
					roleDtoList.add(roleDto);
				}
			}
		}
		Collections.sort(roleDtoList);
		return Mono.just(roleDtoList);
	}

	public Flux<SelectDTO> getSubjectCategorySpecialization(List<Integer> subjectCategoryIds) {
		return commonApiTransaction1.getSubjectCategorySpecialization(subjectCategoryIds).flatMapMany(Flux::fromIterable).map(this::convertSubjectCategorySpecializationDboToDto);
	}
	
	public SelectDTO convertSubjectCategorySpecializationDboToDto(Tuple list) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(String.valueOf(list.get("ID"))));
		dto.setLabel(String.valueOf(list.get("name")));
		return dto;
	}
	
	public Mono<SelectDTO> getDepartmentFromCampusDeptMap(String campusDeptMappingId) {
		return commonApiTransaction1.getDepartmentFromCampusDeptMap(campusDeptMappingId);
	}

	public Flux<SelectDTO> getUsersEmployeeDepartment() {
		List<Tuple> list = commonApiTransaction1.getUsersEmployeeDepartment();
		return convertPanel(list);
	}

	private Flux<SelectDTO> convertPanel(List<Tuple> list) {
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				if(!Utils.isNullOrEmpty(data.get("ID")) && !Utils.isNullOrEmpty(data.get("Text"))) {
					SelectDTO selectDTO = new SelectDTO();
					selectDTO.setValue(data.get("ID").toString());
					selectDTO.setLabel(data.get("Text").toString());
					selectDTOList.add(selectDTO);
				}

			});		
		}
		return Flux.fromIterable(selectDTOList);
	}

	public Mono<List<SelectDTO>> getCountryCode() {
		return commonApiTransaction1.getCountryCode();
	}

	public Mono<ApiResult> privilegeEnable() {
		ApiResult result = new ApiResult();
		result.setSuccess(true);
		return Mono.just(result);
	}
	public Mono<List<SelectDTO>> getErpBlocks(Integer campusId) {
		return commonApiTransaction1.getErpBlocks(campusId);
	}
	public Mono<List<SelectDTO>> getErpFloors(Integer blockId) {
		return commonApiTransaction1.getErpFloors(blockId);
	}
	public Mono<List<SelectDTO>> getErpRoomTypes() {
		return commonApiTransaction1.getErpRoomTypes();
	}
	public Mono<List<SelectDTO>> getErpRooms(Integer roomTypeId, Integer blockId, Integer floorId) {
		return commonApiTransaction1.getErpRooms(roomTypeId, blockId, floorId);
	}

	public Flux<SelectDTO> getDepartmentByCampusId(int campusId) {
		return commonApiTransaction1.getDepartmentByCampusId(campusId).flatMapMany(Flux::fromIterable).map(this::convertDepartToDto);
	}

	public SelectDTO convertDepartToDto(Tuple list) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(String.valueOf(list.get("ID"))));
		dto.setLabel(String.valueOf(list.get("Text")));
		return dto;
	}

	public Flux<ErpTemplateDTO> getErpTemplateByGroupCodeAndProg(String groupCode, String programmeId) {
		return commonApiTransaction1.getErpTemplateByGroupCodeAndProg(groupCode,programmeId).flatMapMany(Flux::fromIterable).map(this::convertTemplateDBOToDTO1);
	}

	public ErpTemplateDTO convertTemplateDBOToDTO1(ErpTemplateDBO dbo) {
		ErpTemplateDTO dto = null;
		if(!Utils.isNullOrEmpty(dbo)){
			dto = new ErpTemplateDTO();
			dto.setId(String.valueOf(dbo.getId()));
			dto.setTemplateName(dbo.getTemplateName());
			if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO())){
				String value = dto.getTemplateName()+" (" +dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName()+")";
				dto.setTemplateName(value);
			}
			dto.setTemplateContent(dbo.getTemplateContent());
		}
		return dto;
	}

	public Flux<SelectDTO> getProgrammeByLevelAndYear(String levelId, String yearId) {
		return commonApiTransaction1.getProgrammeByLevelAndYear(levelId,yearId).flatMapMany(Flux::fromIterable);
	}

	public Flux<SelectDTO> getApplicationDeclarations() {
		return commonApiTransaction1.getApplicationDeclarations().flatMapMany(Flux::fromIterable);
	}

	public Flux<SelectDTO> getCampusProgrammeByYearAndProgrammeLevel(String levelId, String yearId) {
		return commonApiTransaction1.getCampusProgrammeByYearAndProgrammeLevel(levelId,yearId).flatMapMany(Flux::fromIterable);
	}

	public Flux<SelectDTO> getCampusForLocations(List<Integer> locIds) {
		List<ErpCampusDBO> list = commonApiTransaction1.getCampusForLocations(locIds);
		return convertDboToDto(list);
	}

	public Flux<SelectDTO> getHolidaysByCampusAndYear(String campusId,String year) {
		return commonApiTransaction1.getHolidaysByCampusAndYear(campusId,year).flatMapMany(Flux::fromIterable).map(this::convertHolidaysToDto);
	}

	public SelectDTO convertHolidaysToDto(Tuple list) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(list.get("startDate")));
		return dto;
	}

	public String createJwsObject(String jsonString) {
		try {
			String secretKey = redisVaultKeyConfig.getServiceKeys(Constants.SERVICE_SIGN_KEY);
			JWSSigner signer = new MACSigner(secretKey.getBytes());
			JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(jsonString));
			jwsObject.sign(signer);
			return jwsObject.serialize();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	public JwsObjectDTO verifySignedRequest(String jwsObjectString)  {
		JwsObjectDTO jwsObjectDTO = new JwsObjectDTO();
		try {
			String secretKey = redisVaultKeyConfig.getServiceKeys(Constants.SERVICE_SIGN_KEY);
			JWSObject jwsObject = JWSObject.parse(jwsObjectString);
			if (!Utils.isNullOrEmpty(jwsObject.getPayload())) {
				String payload = jwsObject.getPayload().toString();
				jwsObjectDTO.setPayload(payload);
				JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
				jwsObjectDTO.setIsVerified(jwsObject.verify(verifier));
				return jwsObjectDTO;
			}
		} catch (Exception e) {
			System.out.println("verifySignedRequest exception "+e.getMessage());
		}
		return jwsObjectDTO;
	}
	public  <T> List<T> convertJwsObjectToDTO(JwsObjectDTO jwsObjectDTO, Class<T> classOfT) {
		Gson gson = new Gson();
		Type listType = TypeToken.getParameterized(List.class, classOfT).getType();
		return gson.fromJson(jwsObjectDTO.getPayload(), listType);
	}
}