package com.christ.erp.services.helpers.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpInstitutionDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDegreeDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeLevelDBO;
import com.christ.erp.services.dbobjects.common.ErpReservationCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpResidentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuple7;
import reactor.util.function.Tuples;

@Service
public class CommonApiHelper {

    private static volatile CommonApiHelper commonApiHelper = null;
    CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

    public static CommonApiHelper getInstance() {
        if(commonApiHelper==null) {
            commonApiHelper = new CommonApiHelper();
        }
        return commonApiHelper;
    }
	
    public Map<Integer, Map<Integer, Set<Integer>>> getUserRoleAndCampusDetails() throws Exception {
        List<Tuple> list = commonApiTransaction.getUserRoleAndCampusDetails();
        Map<Integer,Map<Integer,Set<Integer>>> map = new HashMap<>();
        if(!Utils.isNullOrEmpty(list)) {
            list.forEach(tuple -> {
                if(!Utils.isNullOrEmpty(tuple.get("erp_users_id")) &&
                        !Utils.isNullOrEmpty(tuple.get("erp_campus_id")) && !Utils.isNullOrEmpty(tuple.get("sys_role_id"))) {
                    if(map.containsKey(Integer.parseInt(tuple.get("erp_users_id").toString()))) {
                        Map<Integer,Set<Integer>> subMap = map.get(Integer.parseInt(tuple.get("erp_users_id").toString()));
                        if(subMap.containsKey(Integer.parseInt(tuple.get("erp_campus_id").toString()))) {
                            Set<Integer> roleIds = subMap.get(Integer.parseInt(tuple.get("erp_campus_id").toString()));
                            if(!roleIds.contains(Integer.parseInt(tuple.get("sys_role_id").toString()))) {
                                roleIds.add(Integer.parseInt(tuple.get("sys_role_id").toString()));
                                subMap.put(Integer.parseInt(tuple.get("erp_campus_id").toString()), roleIds);
                                map.put(Integer.parseInt(tuple.get("erp_users_id").toString()), subMap);
                            }
                        }
                        else {
                            Set<Integer> roleIds = new HashSet<>();
                            roleIds.add(Integer.parseInt(tuple.get("sys_role_id").toString()));
                            subMap.put(Integer.parseInt(tuple.get("erp_campus_id").toString()), roleIds);
                            map.put(Integer.parseInt(tuple.get("erp_users_id").toString()), subMap);
                        }
                    }
                    else {
                        Map<Integer,Set<Integer>> subMap = new HashMap<>();
                        Set<Integer> roleIds = new HashSet<>();
                        roleIds.add(Integer.parseInt(tuple.get("sys_role_id").toString()));
                        subMap.put(Integer.parseInt(tuple.get("erp_campus_id").toString()), roleIds);
                        map.put(Integer.parseInt(tuple.get("erp_users_id").toString()), subMap);
                    }
                }
            });
        }
        return map;
    }

    public Map<Integer, Map<Integer, String>>  getRoleAccessTokenDetails() throws Exception {
        List<Tuple> list = commonApiTransaction.getRoleAccessTokenDetails();
        Map<Integer, Map<Integer, String>> map = new HashMap<>();
        if(!Utils.isNullOrEmpty(list)) {
            list.forEach(tuple -> {
                if(!Utils.isNullOrEmpty(tuple.get("sys_role_id")) && !Utils.isNullOrEmpty(tuple.get("sys_function_id")) &&
                        !Utils.isNullOrEmpty(tuple.get("access_token"))) {
                    if(map.containsKey(Integer.parseInt(tuple.get("sys_role_id").toString()))) {
                        Map<Integer,String> subMap = map.get(Integer.parseInt(tuple.get("sys_role_id").toString()));
                        if(!subMap.containsKey(Integer.parseInt(tuple.get("sys_function_id").toString()))) {
                            subMap.put(Integer.parseInt(tuple.get("sys_function_id").toString()), tuple.get("access_token").toString());
                            map.put(Integer.parseInt(tuple.get("sys_role_id").toString()), subMap);
                        }
                    }
                    else {
                        Map<Integer,String> subMap = new HashMap<>();
                        subMap.put(Integer.parseInt(tuple.get("sys_function_id").toString()), tuple.get("access_token").toString());
                        map.put(Integer.parseInt(tuple.get("sys_role_id").toString()), subMap);
                    }
                }
            });
        }
        return map;
    }

    public Map<Integer,Map<Integer,Map<Integer, Tuple2<Boolean, String>>>>  getUserOverrideFunctionDetails() throws Exception {
        List<Tuple> list = commonApiTransaction.getUserOverrideFunctionDetails();
        Map<Integer,Map<Integer,Map<Integer, Tuple2<Boolean, String>>>> map = new HashMap<>();//userId,campusId,functionId,<isAllowed,accessToken>
        if(!Utils.isNullOrEmpty(list)) {
            list.forEach(tuple -> {
                if(!Utils.isNullOrEmpty(tuple.get("erp_users_id")) && !Utils.isNullOrEmpty(tuple.get("erp_campus_id")) &&
                !Utils.isNullOrEmpty(tuple.get("sys_function_id")) && !Utils.isNullOrEmpty(tuple.get("is_allowed"))) {
                    if(map.containsKey(Integer.parseInt(tuple.get("erp_users_id").toString()))) {
                        Map<Integer,Map<Integer,Tuple2<Boolean, String>>> subMap = map.get(Integer.parseInt(tuple.get("erp_users_id").toString()));
                        if(subMap.containsKey(Integer.parseInt(tuple.get("erp_campus_id").toString()))) {
                            Map<Integer,Tuple2<Boolean, String>> functionIsAllowedOrNotMap = subMap.get(Integer.parseInt(tuple.get("erp_campus_id").toString()));
                            if(!functionIsAllowedOrNotMap.containsKey(Integer.parseInt(tuple.get("sys_function_id").toString()))) {
                                functionIsAllowedOrNotMap.put(Integer.parseInt(tuple.get("sys_function_id").toString()), Tuples.of(Boolean.valueOf(tuple.get("is_allowed").toString()),tuple.get("access_token").toString()));
                                subMap.put(Integer.parseInt(tuple.get("erp_campus_id").toString()),functionIsAllowedOrNotMap);
                                map.put(Integer.parseInt(tuple.get("erp_users_id").toString()), subMap);
                            }
                        }
                        else {
                            Map<Integer,Tuple2<Boolean, String>> functionIsAllowedOrNotMap = new HashMap<>();
                            functionIsAllowedOrNotMap.put(Integer.parseInt(tuple.get("sys_function_id").toString()), Tuples.of(Boolean.valueOf(tuple.get("is_allowed").toString()),tuple.get("access_token").toString()));
                            subMap.put(Integer.parseInt(tuple.get("erp_campus_id").toString()),functionIsAllowedOrNotMap);
                            map.put(Integer.parseInt(tuple.get("erp_users_id").toString()), subMap);
                        }
                    }
                    else {
                        Map<Integer,Map<Integer,Tuple2<Boolean, String>>> subMap = new HashMap<>();
                        Map<Integer,Tuple2<Boolean, String>> functionIsAllowedOrNotMap = new HashMap<>();
                        functionIsAllowedOrNotMap.put(Integer.parseInt(tuple.get("sys_function_id").toString()), Tuples.of(Boolean.valueOf(tuple.get("is_allowed").toString()),tuple.get("access_token").toString()));
                        subMap.put(Integer.parseInt(tuple.get("erp_campus_id").toString()),functionIsAllowedOrNotMap);
                        map.put(Integer.parseInt(tuple.get("erp_users_id").toString()), subMap);
                    }
                }
            });
        }
        return map;
    }

    public Map<Integer, Map<Integer, Map<Integer, String>>> getUserCampusFunctionAccessTokenMap() throws Exception {
        Map<Integer,Map<Integer,Set<Integer>>> userRoleCampusDetailsMap = getUserRoleAndCampusDetails();
        Map<Integer, Map<Integer, String>> roleFunctionAccessTokenDetailsMap = getRoleAccessTokenDetails();
        Map<Integer,Map<Integer,Map<Integer, Tuple2<Boolean, String>>>>  userOverrideDetailsMap = getUserOverrideFunctionDetails();
        Map<Integer, Map<Integer, Map<Integer, String>>> userCampusFunctionAccessTokenMap = new HashMap<>();// final map

        if(!Utils.isNullOrEmpty(userRoleCampusDetailsMap) && !Utils.isNullOrEmpty(roleFunctionAccessTokenDetailsMap)) {
            userRoleCampusDetailsMap.forEach((userId, campusRoleDetails) -> {
                campusRoleDetails.forEach((campusId, roleIds) -> {
                    roleIds.forEach(roleId -> {
                        if(roleFunctionAccessTokenDetailsMap.containsKey(roleId)) {
                            Map<Integer, String> functionAccessTokenDetailsMap = roleFunctionAccessTokenDetailsMap.get(roleId);
                            functionAccessTokenDetailsMap.forEach((functionId, accessToken) -> {
                                setFinalMap(userCampusFunctionAccessTokenMap,userId,campusId,functionId,accessToken);
                            });
                        }
                    });
                });
            });
        }
        if(!Utils.isNullOrEmpty(userOverrideDetailsMap)) {
            userOverrideDetailsMap.forEach((userId, value) -> {
                value.forEach((campusId, value1) -> {
                    value1.forEach((functionId, value2) -> {
                        if(value2.getT1().booleanValue()) {
                            setFinalMap(userCampusFunctionAccessTokenMap,userId,campusId,functionId,value2.getT2());
                        }
                        else {
                            if(userCampusFunctionAccessTokenMap.containsKey(userId) && userCampusFunctionAccessTokenMap.get(userId).containsKey(campusId)) {
                                Map<Integer,String> functionAccessToken = userCampusFunctionAccessTokenMap.get(userId).get(campusId);
                                if(functionAccessToken.containsKey(functionId)) {
                                    functionAccessToken.remove(functionId);
                                }
                            }
                        }
                    });
                });
            });
        }
        return userCampusFunctionAccessTokenMap;
    }

    public void setFinalMap(Map<Integer, Map<Integer, Map<Integer, String>>> userCampusFunctionAccessTokenMap, int userId, int campusId, int functionId, String accessToken) {
        if(userCampusFunctionAccessTokenMap.containsKey(userId)) {
            Map<Integer, Map<Integer, String>> subMap = userCampusFunctionAccessTokenMap.get(userId);
            if(subMap.containsKey(campusId)) {
                Map<Integer, String> functionAccessTokenMap = subMap.get(campusId);
                if(!functionAccessTokenMap.containsKey(functionId)) {
                    functionAccessTokenMap.put(functionId,accessToken);
                    subMap.put(campusId,functionAccessTokenMap);
                    userCampusFunctionAccessTokenMap.put(userId, subMap);
                }
            }
            else {
                Map<Integer, String> functionAccessTokenMap = new HashMap<>();
                functionAccessTokenMap.put(functionId,accessToken);
                subMap.put(campusId,functionAccessTokenMap);
                userCampusFunctionAccessTokenMap.put(userId, subMap);
            }
        }
        else {
            Map<Integer, Map<Integer, String>> subMap = new HashMap<>();
            Map<Integer, String> functionAccessTokenMap = new HashMap<>();
            functionAccessTokenMap.put(functionId,accessToken);
            subMap.put(campusId,functionAccessTokenMap);
            userCampusFunctionAccessTokenMap.put(userId, subMap);
        }
    }
    
    public LookupItemDTO convertAdmQualificationListDBOtoDTO(ErpProgrammeLevelDBO dbo) {
    	return new LookupItemDTO(String.valueOf(dbo.getId()),dbo.getProgrammeLevel());
    }
    
    public LookupItemDTO convertErpProgrammeDegreeDTOtoDTO(ErpProgrammeDegreeDBO dbo) {
    	return new LookupItemDTO(String.valueOf(dbo.getId()),dbo.getProgrammeDegree());
    }
    
    public LookupItemDTO convertProgrammeDBOtoDTO(ErpProgrammeDBO dbo) {
		return new LookupItemDTO(String.valueOf(dbo.getId()),dbo.getProgrammeName());
    }
    
    public LookupItemDTO convertErpReservationCategoryDBOtoDTO(ErpReservationCategoryDBO dbo) {
    	return new LookupItemDTO(String.valueOf(dbo.getId()),dbo.getReservationCategoryName());
    }
    
    public LookupItemDTO convertErpInstitutionDBOtoDTO(ErpInstitutionDBO dbo) {
    	return new LookupItemDTO(String.valueOf(dbo.getId()),dbo.getInstitutionName());
    }
    
    public LookupItemDTO convertErpResidentCategoryDBOtoDTO(ErpResidentCategoryDBO dbo) {
    	return new LookupItemDTO(String.valueOf(dbo.getId()),dbo.getResidentCategoryName());
    }
    
    public ProgramPreferenceDTO convertProgramPreferenceByProgramDBOtoDTO(Object[] obj) {
    	ProgramPreferenceDTO preferenceDTO = new ProgramPreferenceDTO();
		preferenceDTO.setCampusMappingId(!Utils.isNullOrEmpty(obj[0])?obj[0].toString():"");
    	preferenceDTO.setValue(!Utils.isNullOrEmpty(obj[0])?obj[0].toString():"");
		if(!Utils.isNullOrEmpty(obj[5])) {
            if(!Utils.isNullOrEmpty(obj[2])) {
            	preferenceDTO.setLabel(obj[2].toString());
    			preferenceDTO.preferenceOption = 'C';
            } else if(!Utils.isNullOrEmpty(obj[4])) {
            	preferenceDTO.setLabel(obj[4].toString());
    			preferenceDTO.preferenceOption = 'L';
            }
		}
		return preferenceDTO;
	}
    
    public ErpAcademicYearDTO  convertAcademicYearDBOtoDTO(ErpAcademicYearDBO dbo) {
    	ErpAcademicYearDTO dto = new ErpAcademicYearDTO();
    	if(!Utils.isNullOrEmpty(dbo.getAcademicYear()) && !Utils.isNullOrEmpty(dbo.getAcademicYearName())) {
        	dto.setValue(String.valueOf(dbo.getId()));
        	dto.setLabel(String.valueOf(dbo.getAcademicYearName()));
        	dto.setAcademicYear(dbo.getAcademicYear());
    	}
    	if(!Utils.isNullOrEmpty(dbo.getIsCurrentAcademicYear()))
    		dto.setIsCurrentAcademicYear(dbo.getIsCurrentAcademicYear());
    	if(!Utils.isNullOrEmpty(dbo.getIsCurrentAdmissionYear()))
    		dto.setIsCurrentAdmissionYear(dbo.getIsCurrentAdmissionYear());
    	return dto;
    }

    public LookupItemDTO convertStudentApplicationNumbersToDTO(Tuple tuple) {
        LookupItemDTO itemDTO = new LookupItemDTO();
        if(!Utils.isNullOrEmpty(tuple.get("studentApplnEntriesId")) && !Utils.isNullOrEmpty(tuple.get("applicationNo"))){
            itemDTO.value = String.valueOf(tuple.get("studentApplnEntriesId"));
            itemDTO.label = String.valueOf(tuple.get("applicationNo"));
        }
        return itemDTO;
    }
    
    public SelectDTO convertRegisterNumbersToDTO(Tuple tuple) {
    	SelectDTO itemDTO = new SelectDTO();
        if(!Utils.isNullOrEmpty(tuple.get("student_id")) && !Utils.isNullOrEmpty(tuple.get("register_no"))){
            itemDTO.value = String.valueOf(tuple.get("student_id"));
            itemDTO.label = String.valueOf(tuple.get("register_no"));
        }
        return itemDTO;
    }

    public LookupItemDTO convertStudentApplicationNamesToDTO(Tuple tuple) {
        LookupItemDTO itemDTO = new LookupItemDTO();
        if(!Utils.isNullOrEmpty(tuple.get("studentApplnEntriesId")) && !Utils.isNullOrEmpty(tuple.get("applicant_name"))){
            itemDTO.value = String.valueOf(tuple.get("studentApplnEntriesId"));
            itemDTO.label = String.valueOf(tuple.get("applicant_name"));
        }
        return itemDTO;
    }

    public void setSelectionProcessDatesData(List<Tuple7<String, String, String, String, String, String, String>> tuple7s, Tuple tuple) {
        tuple7s.add(Tuples.of(!Utils.isNullOrEmpty(tuple.get("admSelectionProcessPlanDetailId")) ? tuple.get("admSelectionProcessPlanDetailId").toString() : "",
                !Utils.isNullOrEmpty(tuple.get("admSelectionProcessVenueCityId")) ? tuple.get("admSelectionProcessVenueCityId").toString() : "",
                !Utils.isNullOrEmpty(tuple.get("venueName")) ? tuple.get("venueName").toString() : "",
                !Utils.isNullOrEmpty(tuple.get("totalAvailableSeatCount")) ? tuple.get("totalAvailableSeatCount").toString() : "",
                !Utils.isNullOrEmpty(tuple.get("selectionProcessMode")) ? tuple.get("selectionProcessMode").toString() : "",
                !Utils.isNullOrEmpty(tuple.get("isConductedInIndia")) && "1".equalsIgnoreCase(tuple.get("isConductedInIndia").toString()) ? "true" : "false",
                !Utils.isNullOrEmpty(tuple.get("stateName")) ? tuple.get("stateName").toString() : ""));
    }
    
    public LookupItemDTO convertErpWorkFlowProcessDBOtoDTO(ErpWorkFlowProcessDBO erpWorkFlowProcessDBO) {
        LookupItemDTO itemDTO = new LookupItemDTO();
        if(!Utils.isNullOrEmpty(erpWorkFlowProcessDBO)){
            itemDTO.value = String.valueOf(erpWorkFlowProcessDBO.id);
            itemDTO.label = String.valueOf(erpWorkFlowProcessDBO.processCode);
        }
        return itemDTO;
    }
    
    public LookupItemDTO convertSMSorEmailTemplateDBOtoDTO(ErpTemplateDBO erpTemplateDBO) {
        LookupItemDTO itemDTO = new LookupItemDTO();
        if(!Utils.isNullOrEmpty(erpTemplateDBO)){
            itemDTO.value = String.valueOf(erpTemplateDBO.id);
            itemDTO.label = String.valueOf(erpTemplateDBO.templateName);
        }
        return itemDTO;
    }
}
