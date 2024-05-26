package com.christ.erp.services.handlers.employee.recruitment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisAwsConfig;
import com.christ.erp.services.common.RedisSysPropertiesData;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.common.UrlFolderListDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpInterviewPanelistDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpInterviewUniversityExternalsDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpInterviewExternalPanelDocumentUploadDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpInterviewExternalPanelistDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpInterviewPanelistDTO;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.transactions.employee.recruitment.InterviewPanelistTransaction;

@Service
public class InterviewPanelistHandler {
	//private static volatile InterviewPanelistHandler interviewPanellistHandler = null;
	
	@Autowired
	InterviewPanelistTransaction interviewPanelistTransaction;
	
	@Autowired
	RedisAwsConfig redisAwsConfig;

	@Autowired
	AWSS3FileStorageServiceHandler aWSS3FileStorageServiceHandler;
	
	@Autowired
	RedisSysPropertiesData redisSysPropertiesData;
	
	/*public static InterviewPanelistHandler getInstance() {
		if (interviewPanellistHandler == null) {
			interviewPanellistHandler = new InterviewPanelistHandler();
		}
		return interviewPanellistHandler;
	}*/
	
	public void getUniversityExternalPanelist(ApiResult<List<LookupItemDTO>> result) throws Exception {					
		List<Tuple> universityExternalPanelMappings = interviewPanelistTransaction.getGridDataUniversityExternalPanel();
		if(universityExternalPanelMappings.size()>0) {
			if(universityExternalPanelMappings != null && universityExternalPanelMappings.size() > 0) {
		        result.success = true;
		        result.dto = new ArrayList<>();
		        for(Tuple mapping : universityExternalPanelMappings) {
		            LookupItemDTO itemInfo = new LookupItemDTO();
		            itemInfo.value = !Utils.isNullOrEmpty(mapping.get("id")) ? mapping.get("id").toString() : "";
		            itemInfo.label = !Utils.isNullOrEmpty(mapping.get("panelName")) ? mapping.get("panelName").toString() : "";
		            result.dto.add(itemInfo);
		        }
		    }
		}
	}

	public List<LookupItemDTO> getEmployee(String departmentId, String locationId, String employeeType,ApiResult<List<LookupItemDTO>> result) throws Exception {
		if(employeeType.equalsIgnoreCase("Internal")) {			
			List<Tuple> internalPanelMappings = interviewPanelistTransaction.getInternalPanelMembers(departmentId, locationId);
			if(internalPanelMappings.size()>0) {
		        if(internalPanelMappings != null && internalPanelMappings.size() > 0) {
		        	result.success = true;
		        	result.dto = new ArrayList<>();
		            for(Tuple mapping : internalPanelMappings) {
		            	LookupItemDTO itemInfo = new LookupItemDTO();
		                itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
		                itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
		                result.dto.add(itemInfo);
		            }
		        }
			}
		}
		else{
			List<Tuple> externalPanelMappings = interviewPanelistTransaction.getExternalPanelMembers(departmentId, locationId);
			if(externalPanelMappings.size()>0) {
		        if(externalPanelMappings != null && externalPanelMappings.size() > 0) {
		        	result.success = true;
		        	result.dto = new ArrayList<>();
		            for(Tuple mapping : externalPanelMappings) {
		            	LookupItemDTO itemInfo = new LookupItemDTO();
		                itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
		                itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
		                result.dto.add(itemInfo);
		            }
		        }
			}			
		}
		return result.dto;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(EmpInterviewPanelistDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if(!Utils.isNullOrEmpty(data)) {
			// Duplicate check
			if(Utils.isNullOrEmpty(data.id)) {
				List<EmpInterviewPanelistDBO> mappings = interviewPanelistTransaction.getDuplicate(data);
				if(mappings.size() > 0) {
					result.failureMessage = "Panelist already entered for selected department";
					result.success = false;
				} else {
					List<EmpInterviewPanelistDBO> dboList = new ArrayList<>();
					if(!Utils.isNullOrEmpty(data.internalPanelList)) {
						for(ExModelBaseDTO modelInternalpanel : data.internalPanelList) {
							EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
							ErpAcademicYearDBO year = new ErpAcademicYearDBO();
							year.id = Integer.parseInt(data.academicYear.id);
							dbo.erpAcademicYearDBO = year;
							ErpLocationDBO location = new ErpLocationDBO();
							location.id = Integer.parseInt(data.location.id);
							dbo.erpLocationDBO = location;
							ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
							departmentDBO.id = Integer.parseInt(data.department.id);
							dbo.erpDepartmentDBO = departmentDBO;
							ErpUsersDBO usersDBO = new ErpUsersDBO();
							usersDBO.id = Integer.parseInt(modelInternalpanel.id);
							dbo.internalErpUsersDBO = usersDBO;
							dbo.createdUsersId = Integer.parseInt(userId);
							dbo.recordStatus = 'A';
							dboList.add(dbo);
						}
					}
					if(!Utils.isNullOrEmpty(data.externalPanelList)) {
						for(ExModelBaseDTO modelExternalpanel : data.externalPanelList) {
							EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
							ErpAcademicYearDBO year = new ErpAcademicYearDBO();
							year.id = Integer.parseInt(data.academicYear.id);
							dbo.erpAcademicYearDBO = year;
							ErpLocationDBO location = new ErpLocationDBO();
							location.id = Integer.parseInt(data.location.id);
							dbo.erpLocationDBO = location;
							ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
							departmentDBO.id = Integer.parseInt(data.department.id);
							dbo.erpDepartmentDBO = departmentDBO;
							ErpUsersDBO usersDBO = new ErpUsersDBO();
							usersDBO.id = Integer.parseInt(modelExternalpanel.id);
							dbo.externalErpUsersDBO = usersDBO;
							dbo.createdUsersId = Integer.parseInt(userId);
							dbo.recordStatus = 'A';
							dboList.add(dbo);
						}
					}
					if(!Utils.isNullOrEmpty(data.universityExternalPanelList)) {
						for(ExModelBaseDTO modelUniversityExternalpanel : data.universityExternalPanelList) {
							EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
							ErpAcademicYearDBO year = new ErpAcademicYearDBO();
							year.id = Integer.parseInt(data.academicYear.id);
							dbo.erpAcademicYearDBO = year;
							ErpLocationDBO location = new ErpLocationDBO();
							location.id = Integer.parseInt(data.location.id);
							dbo.erpLocationDBO = location;
							ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
							departmentDBO.id = Integer.parseInt(data.department.id);
							dbo.erpDepartmentDBO = departmentDBO;
							EmpInterviewUniversityExternalsDBO usersDBO = new EmpInterviewUniversityExternalsDBO();
							usersDBO.id = Integer.parseInt(modelUniversityExternalpanel.id);
							dbo.empInterviewUniversityExternalsDBO = usersDBO;
							dbo.createdUsersId = Integer.parseInt(userId);
							dbo.recordStatus = 'A';
							dboList.add(dbo);
						}						
					}
					result.success = interviewPanelistTransaction.saveOrUpdate(dboList);
				}
			} else {
				List<EmpInterviewPanelistDBO> dboList = new ArrayList<>();
				List<Tuple> listInternalPanel = interviewPanelistTransaction.getInternalPanel(data);
				if(listInternalPanel.size() > 0) {
					Map<Integer, Integer> newInternalPanelMap = new HashMap<Integer, Integer>();
					Map<Integer, Integer> deleteInternalPanelMap = new HashMap<Integer, Integer>();
					for(ExModelBaseDTO model : data.internalPanelList) {
						newInternalPanelMap.put(Integer.parseInt(model.id), Integer.parseInt(model.id));
					}
					for(Tuple tuple : listInternalPanel) {
						Integer interviewPanelId = (Integer) tuple.get("empInterviewPanelId");
						Integer internalPanelUserId = (Integer) tuple.get("internalPanelId");
						if(newInternalPanelMap.containsKey(internalPanelUserId)) {
							newInternalPanelMap.remove(internalPanelUserId);
						} else {
							deleteInternalPanelMap.put(interviewPanelId, internalPanelUserId);
						}
					}
					for(Entry<Integer, Integer> map : deleteInternalPanelMap.entrySet()) {
						EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
						dbo.id = map.getKey();
						ErpAcademicYearDBO year = new ErpAcademicYearDBO();
						year.id = Integer.parseInt(data.academicYear.id);
						dbo.erpAcademicYearDBO = year;
						ErpLocationDBO location = new ErpLocationDBO();
						location.id = Integer.parseInt(data.location.id);
						dbo.erpLocationDBO = location;
						ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
						departmentDBO.id = Integer.parseInt(data.department.id);
						dbo.erpDepartmentDBO = departmentDBO;
						ErpUsersDBO usersDBO = new ErpUsersDBO();
						usersDBO.id = map.getValue();
						dbo.internalErpUsersDBO = usersDBO;
						dbo.modifiedUsersId = Integer.parseInt(userId);
						dbo.recordStatus = 'D';
						dboList.add(dbo);
					}
					for(Entry<Integer, Integer> map : newInternalPanelMap.entrySet()) {
						EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
						ErpAcademicYearDBO year = new ErpAcademicYearDBO();
						year.id = Integer.parseInt(data.academicYear.id);
						dbo.erpAcademicYearDBO = year;
						ErpLocationDBO location = new ErpLocationDBO();
						location.id = Integer.parseInt(data.location.id);
						dbo.erpLocationDBO = location;
						ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
						departmentDBO.id = Integer.parseInt(data.department.id);
						dbo.erpDepartmentDBO = departmentDBO;
						ErpUsersDBO usersDBO = new ErpUsersDBO();
						usersDBO.id = map.getKey();
						dbo.internalErpUsersDBO = usersDBO;
						dbo.createdUsersId = Integer.parseInt(userId);
						dbo.recordStatus = 'A';
						dboList.add(dbo);
					}
					// External Panel
					List<Tuple> listExternalPanel = interviewPanelistTransaction.getExternalPanel(data);
					if(listExternalPanel.size() > 0) {
						Map<Integer, Integer> newExternalPanelMap = new HashMap<Integer, Integer>();
						Map<Integer, Integer> deleteExternalPanelMap = new HashMap<Integer, Integer>();
						for(ExModelBaseDTO model : data.externalPanelList) {
							newExternalPanelMap.put(Integer.parseInt(model.id), Integer.parseInt(model.id));
						}
						for(Tuple tuple : listExternalPanel) {
							Integer interviewPanelId = (Integer) tuple.get("empInterviewPanelId");
							Integer externalPanelUserId = (Integer) tuple.get("externalPanelId");
							if(newExternalPanelMap.containsKey(externalPanelUserId)) {
								newExternalPanelMap.remove(externalPanelUserId);
							} else {
								deleteExternalPanelMap.put(interviewPanelId, externalPanelUserId);
							}
						}
						for(Entry<Integer, Integer> map : deleteExternalPanelMap.entrySet()) {
							EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
							dbo.id = map.getKey();
							ErpAcademicYearDBO year = new ErpAcademicYearDBO();
							year.id = Integer.parseInt(data.academicYear.id);
							dbo.erpAcademicYearDBO = year;
							ErpLocationDBO location = new ErpLocationDBO();
							location.id = Integer.parseInt(data.location.id);
							dbo.erpLocationDBO = location;
							ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
							departmentDBO.id = Integer.parseInt(data.department.id);
							dbo.erpDepartmentDBO = departmentDBO;
							ErpUsersDBO usersDBO = new ErpUsersDBO();
							usersDBO.id = map.getValue();
							dbo.externalErpUsersDBO = usersDBO;
							dbo.modifiedUsersId = Integer.parseInt(userId);
							dbo.recordStatus = 'D';
							dboList.add(dbo);
						}
						for(Entry<Integer, Integer> map : newExternalPanelMap.entrySet()) {
							EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
							ErpAcademicYearDBO year = new ErpAcademicYearDBO();
							year.id = Integer.parseInt(data.academicYear.id);
							dbo.erpAcademicYearDBO = year;
							ErpLocationDBO location = new ErpLocationDBO();
							location.id = Integer.parseInt(data.location.id);
							dbo.erpLocationDBO = location;
							ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
							departmentDBO.id = Integer.parseInt(data.department.id);
							dbo.erpDepartmentDBO = departmentDBO;
							ErpUsersDBO usersDBO = new ErpUsersDBO();
							usersDBO.id = map.getKey();
							dbo.externalErpUsersDBO = usersDBO;
							dbo.createdUsersId = Integer.parseInt(userId);
							dbo.recordStatus = 'A';
							dboList.add(dbo);
						}
					} 					
					//University External Panelist
					List<Tuple> listUniversityExternalPanel = interviewPanelistTransaction.getUniversityExternalPanel(data);
					if(listUniversityExternalPanel.size() > 0) {
						Map<Integer, Integer> newUniversityExternalPanelMap = new HashMap<Integer, Integer>();
						Map<Integer, Integer> deleteUniversityExternalPanelMap = new HashMap<Integer, Integer>();
						for(ExModelBaseDTO model : data.universityExternalPanelList) {
							newUniversityExternalPanelMap.put(Integer.parseInt(model.id), Integer.parseInt(model.id));
						}
						for(Tuple tuple : listUniversityExternalPanel) {
							Integer interviewPanelId = (Integer) tuple.get("empInterviewPanelId");
							Integer universityExternalPanelUserId = (Integer) tuple.get("empUniversityExternalPanelId");
							if (newUniversityExternalPanelMap.containsKey(universityExternalPanelUserId)) {
								newUniversityExternalPanelMap.remove(universityExternalPanelUserId);
							} else {
								deleteUniversityExternalPanelMap.put(interviewPanelId, universityExternalPanelUserId);
							}
						}
						for(Entry<Integer, Integer> map : deleteUniversityExternalPanelMap.entrySet()) {
							EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
							dbo.id = map.getKey();
							ErpAcademicYearDBO year = new ErpAcademicYearDBO();
							year.id = Integer.parseInt(data.academicYear.id);
							dbo.erpAcademicYearDBO = year;
							ErpLocationDBO location = new ErpLocationDBO();
							location.id = Integer.parseInt(data.location.id);
							dbo.erpLocationDBO = location;
							ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
							departmentDBO.id = Integer.parseInt(data.department.id);
							dbo.erpDepartmentDBO = departmentDBO;
							EmpInterviewUniversityExternalsDBO usersDBO = new EmpInterviewUniversityExternalsDBO();
							usersDBO.id = map.getValue();
							dbo.empInterviewUniversityExternalsDBO = usersDBO;							
							dbo.modifiedUsersId = Integer.parseInt(userId);
							dbo.recordStatus = 'D';
							dboList.add(dbo);
						}
						for(Entry<Integer, Integer> map : newUniversityExternalPanelMap.entrySet()) {
							EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
							ErpAcademicYearDBO year = new ErpAcademicYearDBO();
							year.id = Integer.parseInt(data.academicYear.id);
							dbo.erpAcademicYearDBO = year;
							ErpLocationDBO location = new ErpLocationDBO();
							location.id = Integer.parseInt(data.location.id);
							dbo.erpLocationDBO = location;
							ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
							departmentDBO.id = Integer.parseInt(data.department.id);
							dbo.erpDepartmentDBO = departmentDBO;
							EmpInterviewUniversityExternalsDBO usersDBO = new EmpInterviewUniversityExternalsDBO();
							usersDBO.id = map.getKey();
							dbo.empInterviewUniversityExternalsDBO = usersDBO;
							dbo.createdUsersId = Integer.parseInt(userId);
							dbo.recordStatus = 'A';
							dboList.add(dbo);
						}
					} 
					else {
						if(!Utils.isNullOrEmpty(data.universityExternalPanelList)) {
							for(ExModelBaseDTO modelUniversityExternalpanel : data.universityExternalPanelList) {
								EmpInterviewPanelistDBO dbo = new EmpInterviewPanelistDBO();
								ErpAcademicYearDBO year = new ErpAcademicYearDBO();
								year.id = Integer.parseInt(data.academicYear.id);
								dbo.erpAcademicYearDBO = year;
								ErpLocationDBO location = new ErpLocationDBO();
								location.id = Integer.parseInt(data.location.id);
								dbo.erpLocationDBO = location;
								ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO();
								departmentDBO.id = Integer.parseInt(data.department.id);
								dbo.erpDepartmentDBO = departmentDBO;
								EmpInterviewUniversityExternalsDBO usersDBO = new EmpInterviewUniversityExternalsDBO();
								usersDBO.id = Integer.parseInt(modelUniversityExternalpanel.id);
								dbo.empInterviewUniversityExternalsDBO = usersDBO;								
								dbo.createdUsersId = Integer.parseInt(userId);
								dbo.recordStatus = 'A';
								dboList.add(dbo);
							}							
						}						
					}					
					result.success = interviewPanelistTransaction.saveOrUpdate(dboList);
				} 
			} 
		}
		return result;
	}
	
	public ApiResult<ModelBaseDTO> saveOrUpdateUniversityExternalPanel(EmpInterviewExternalPanelistDTO data,String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if(!Utils.isNullOrEmpty(data)) {
			// Duplicate check
			List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
			if(Utils.isNullOrEmpty(data.id)) {
				List<EmpInterviewUniversityExternalsDBO> mappings = interviewPanelistTransaction.getDuplicateExternalPanel(data);
				if(mappings.size() > 0) {
					result.failureMessage = "Already entered external panel";
					result.success = false;
				} else{
					EmpInterviewUniversityExternalsDBO dbo =new EmpInterviewUniversityExternalsDBO();
					dbo.panelName = data.name;
					dbo.panelEmail = data.email;
					dbo.panelMblCountryCode = data.countryCode.text;
					dbo.panelMblNo = data.mobile;
					for(EmpInterviewExternalPanelDocumentUploadDTO item:data.empDocumentImages) {
						//File file = new File("ImageUpload//"+item.fileName+"."+item.extension);
						//dbo.panelDocumentUrl = file.getAbsolutePath();
						UrlAccessLinkDBO urlAccessLinkDBO = new UrlAccessLinkDBO();
						if(item.getNewFile()) {
							FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
							String[] awsConfig = redisAwsConfig.getAwsProperties(item.getProcessCode());
							fileUploadDownloadDTO.setTempPath(awsConfig[RedisAwsConfig.TEMP_PATH]);
							fileUploadDownloadDTO.setActualPath(awsConfig[RedisAwsConfig.ACTUAL_PATH]);
					        fileUploadDownloadDTO.setUniqueFileName(item.getUniqueFileName());
					        fileUploadDownloadDTO.setBucketName(awsConfig[RedisAwsConfig.BUCKET_NAME]);
					        fileUploadDownloadDTO.setTempBucketName(awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME]);
							if(item.getNewFile()) {
								uniqueFileNameList.add(fileUploadDownloadDTO);
							}
							urlAccessLinkDBO.setFileNameUnique(awsConfig[RedisAwsConfig.ACTUAL_PATH] + item.getUniqueFileName());
							urlAccessLinkDBO.setTempFileNameUnique(awsConfig[RedisAwsConfig.TEMP_PATH]+ item.getUniqueFileName());
							urlAccessLinkDBO.setFileNameOriginal(item.getOriginalFileName());
							UrlFolderListDBO urlFolderListDBO = new UrlFolderListDBO();
							urlFolderListDBO.setId(Integer.parseInt(awsConfig[RedisAwsConfig.FOLDER_LIST_ID]));
							urlAccessLinkDBO.setUrlFolderListDBO(urlFolderListDBO);
							urlAccessLinkDBO.setRecordStatus('A');
							urlAccessLinkDBO.setCreatedUsersId(Integer.parseInt(userId));
							urlAccessLinkDBO.setIsQueued(false);
							urlAccessLinkDBO.setIsServiced(true);
						}
						dbo.setPanelistDocumentUrlDBO(urlAccessLinkDBO);
					}
					dbo.recordStatus = 'A';
					dbo.createdUsersId = Integer.parseInt(userId);
					result.success = interviewPanelistTransaction.saveOrUpdateUniversityExternalPanel(dbo);
					if(uniqueFileNameList.size() > 0) {
						aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList)
					    .subscribe(res -> {
					        if (res.success()) {
					            System.out.println("Move operation succeeded");
					        } else {
					            System.out.println("Move operation failed: " + res.message());
					        }
					    });
					}
				}
			}
			else{
				EmpInterviewUniversityExternalsDBO dbo = interviewPanelistTransaction.getExternalDBO(Integer.parseInt(data.id));
				//EmpInterviewUniversityExternalsDBO dbo =new EmpInterviewUniversityExternalsDBO();
				dbo.id =Integer.parseInt(data.id);
				dbo.panelName = data.name;
				dbo.panelEmail = data.email;
				dbo.panelMblCountryCode = data.countryCode.text;
				dbo.panelMblNo = data.mobile;
				for(EmpInterviewExternalPanelDocumentUploadDTO item:data.empDocumentImages) {
					//File file = new File("ImageUpload//"+item.fileName+"."+item.extension);
					//dbo.panelDocumentUrl = file.getAbsolutePath();
					UrlAccessLinkDBO urlAccessLinkDBO;
					if(Utils.isNullOrEmpty(dbo.getPanelistDocumentUrlDBO())) {
						urlAccessLinkDBO = new UrlAccessLinkDBO();
						urlAccessLinkDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					else {
						urlAccessLinkDBO = dbo.getPanelistDocumentUrlDBO();
					}
					if(item.getNewFile()) {
						FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
						String[] awsConfig = redisAwsConfig.getAwsProperties(item.getProcessCode());
						fileUploadDownloadDTO.setTempPath(awsConfig[RedisAwsConfig.TEMP_PATH]);
						fileUploadDownloadDTO.setActualPath(awsConfig[RedisAwsConfig.ACTUAL_PATH]);
				        fileUploadDownloadDTO.setUniqueFileName(item.getUniqueFileName());
				        fileUploadDownloadDTO.setBucketName(awsConfig[RedisAwsConfig.BUCKET_NAME]);
				        fileUploadDownloadDTO.setTempBucketName(awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME]);
						if(item.getNewFile()) {
							uniqueFileNameList.add(fileUploadDownloadDTO);
						}
						urlAccessLinkDBO.setFileNameUnique(awsConfig[RedisAwsConfig.ACTUAL_PATH] + item.getUniqueFileName());
						urlAccessLinkDBO.setTempFileNameUnique(awsConfig[RedisAwsConfig.TEMP_PATH]+ item.getUniqueFileName());
						urlAccessLinkDBO.setFileNameOriginal(item.getOriginalFileName());
						UrlFolderListDBO urlFolderListDBO = new UrlFolderListDBO();
						urlFolderListDBO.setId(Integer.parseInt(awsConfig[RedisAwsConfig.FOLDER_LIST_ID]));
						urlAccessLinkDBO.setUrlFolderListDBO(urlFolderListDBO);
						urlAccessLinkDBO.setRecordStatus('A');
						urlAccessLinkDBO.setCreatedUsersId(Integer.parseInt(userId));
						urlAccessLinkDBO.setIsQueued(false);
						urlAccessLinkDBO.setIsServiced(true);
					}
					dbo.setPanelistDocumentUrlDBO(urlAccessLinkDBO);
				}
				dbo.recordStatus = 'A';
				dbo.modifiedUsersId = Integer.parseInt(userId);
				result.success = interviewPanelistTransaction.saveOrUpdateUniversityExternalPanel(dbo);	
				if(uniqueFileNameList.size() > 0) {
					aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList)
				    .subscribe(res -> {
				        if (res.success()) {
				            System.out.println("Move operation succeeded");
				        } else {
				            System.out.println("Move operation failed: " + res.message());
				        }
				    });
				}
				
			}
		}
		return result;
	}

	public List<EmpInterviewPanelistDTO> getGridData() throws Exception {
		List<EmpInterviewPanelistDTO> gridList = null;
		List<Tuple> list = interviewPanelistTransaction.getGridData();
		if (!Utils.isNullOrEmpty(list)) {
			gridList = new ArrayList<EmpInterviewPanelistDTO>();
			for(Tuple tuple : list) {
				EmpInterviewPanelistDTO dto = new EmpInterviewPanelistDTO();
				dto.academicYear = new ExModelBaseDTO();
				dto.academicYear.id = String.valueOf(tuple.get("academicYearId"));
				dto.academicYear.text = String.valueOf(tuple.get("academicYearName"));
				dto.location = new ExModelBaseDTO();
				dto.location.id = String.valueOf(tuple.get("locationId"));
				dto.location.text = String.valueOf(tuple.get("locationName"));
				dto.department = new ExModelBaseDTO();
				dto.department.id = String.valueOf(tuple.get("deptId"));
				dto.department.text = String.valueOf(tuple.get("deptName"));
				gridList.add(dto);
			}
		}
		return gridList;
	}
	
	public List<EmpInterviewExternalPanelistDTO> getGridDataUniversityExternalPanel() throws Exception {
		List<EmpInterviewExternalPanelistDTO> gridList = null;
		List<Tuple> list = interviewPanelistTransaction.getGridDataUniversityExternalPanel();
		if(!Utils.isNullOrEmpty(list)) {
			gridList = new ArrayList<EmpInterviewExternalPanelistDTO>();
			for(Tuple tuple : list) {
				EmpInterviewExternalPanelistDTO dto = new EmpInterviewExternalPanelistDTO();				
				dto.id = String.valueOf(tuple.get("id"));
				dto.name= String.valueOf(tuple.get("panelName"));				
				gridList.add(dto);
			}
		}
		return gridList;
	}
	
	public boolean deleteUniversityExternalPanel(EmpInterviewExternalPanelistDTO data) throws Exception {
		Boolean result = false;
		result = interviewPanelistTransaction.deleteUniversityExternalPanel(data);
		return result;
	}

	public boolean delete(EmpInterviewPanelistDTO data) throws Exception {
		Boolean result = false;
		result = interviewPanelistTransaction.delete(data);
		return result;
	}

	public EmpInterviewPanelistDTO edit(EmpInterviewPanelistDTO data) throws Exception {
		EmpInterviewPanelistDTO dto = null;
		if (!Utils.isNullOrEmpty(data)) {
			List<Tuple> listInternalPanel = interviewPanelistTransaction.getInternalPanel(data);
			if(listInternalPanel.size() > 0) {
				dto = new EmpInterviewPanelistDTO();
				dto.academicYear = new ExModelBaseDTO();
				dto.academicYear.id = data.academicYear.id;
				dto.academicYear.text = data.academicYear.text;
				dto.location = new ExModelBaseDTO();
				dto.location.id = data.location.id;
				dto.location.text = data.location.text;
				dto.department = new ExModelBaseDTO();
				dto.department.id = data.department.id;
				dto.department.text = data.department.text;
				dto.internalPanelList = new ArrayList<ExModelBaseDTO>();
				for(Tuple tuple : listInternalPanel) {
					ExModelBaseDTO internalPanel = new ExModelBaseDTO();
					dto.id = String.valueOf(tuple.get("internalPanelId"));
					internalPanel.id = String.valueOf(tuple.get("internalPanelId"));
					internalPanel.text = String.valueOf(tuple.get("empName"));
					internalPanel.tag = String.valueOf(tuple.get("empInterviewPanelId"));
					dto.internalPanelList.add(internalPanel);
				}
				List<Tuple> listExternalPanel = interviewPanelistTransaction.getExternalPanel(data);
				if(listExternalPanel.size() > 0) {
					dto.externalPanelList = new ArrayList<ExModelBaseDTO>();
					for(Tuple tuple : listExternalPanel) {
						ExModelBaseDTO externalPanel = new ExModelBaseDTO();
						externalPanel.id = String.valueOf(tuple.get("externalPanelId"));
						externalPanel.text = String.valueOf(tuple.get("empName"));
						externalPanel.tag = String.valueOf(tuple.get("empInterviewPanelId"));
						dto.externalPanelList.add(externalPanel);
					}
				}
				List<Tuple> listUniversityExternalPanel = interviewPanelistTransaction.getUniversityExternalPanel(data);
				if(listUniversityExternalPanel.size() > 0) {
					dto.universityExternalPanelList = new ArrayList<ExModelBaseDTO>();
					for(Tuple tuple : listUniversityExternalPanel) {
						ExModelBaseDTO universityExternalPanel = new ExModelBaseDTO();
						universityExternalPanel.id = String.valueOf(tuple.get("empUniversityExternalPanelId"));
						universityExternalPanel.text = String.valueOf(tuple.get("empName"));
						dto.universityExternalPanelList.add(universityExternalPanel);
					}
				}
			}
		}
		return dto;
	}
	
	public EmpInterviewExternalPanelistDTO editUniversityExternalPanel(String id) throws Exception {
		EmpInterviewExternalPanelistDTO dto=null;
		if(!Utils.isNullOrEmpty(id)) {
			EmpInterviewUniversityExternalsDBO dbo = interviewPanelistTransaction.getUniversityExtrenalPanelProfile(id);
			dto = new EmpInterviewExternalPanelistDTO();
			dto.id = id;
			dto.name = dbo.panelName;
			dto.email = dbo.panelEmail;
			dto.mobile = dbo.panelMblNo;			
			dto.countryCode=new ExModelBaseDTO();
			dto.countryCode.text = dbo.panelMblCountryCode;
			dto.uploadDocumentUrl = dbo.panelDocumentUrl;	
			dto.empDocumentImages = new ArrayList<>();
			/*if(!Utils.isNullOrEmpty(dto.uploadDocumentUrl)) {
				EmpInterviewExternalPanelDocumentUploadDTO empInterviewExternalPanelDocumentUploadDTO = new EmpInterviewExternalPanelDocumentUploadDTO();		
				File file = new File(dto.uploadDocumentUrl);
				if(file.exists() && !file.isDirectory()) { 
					empInterviewExternalPanelDocumentUploadDTO.extension = dto.uploadDocumentUrl.substring(dto.uploadDocumentUrl.lastIndexOf(".")+1);
					String fileName = new File(dto.uploadDocumentUrl).getName();
					empInterviewExternalPanelDocumentUploadDTO.url =dto.uploadDocumentUrl;
					empInterviewExternalPanelDocumentUploadDTO.fileName = fileName.replaceFirst("[.][^.]+$", "");
					empInterviewExternalPanelDocumentUploadDTO.recordStatus = 'A';
					dto.empDocumentImages.add(empInterviewExternalPanelDocumentUploadDTO);
				}
			}*/
			if(!Utils.isNullOrEmpty(dbo.getPanelistDocumentUrlDBO())) {
				EmpInterviewExternalPanelDocumentUploadDTO empInterviewExternalPanelDocumentUploadDTO = new EmpInterviewExternalPanelDocumentUploadDTO();
				empInterviewExternalPanelDocumentUploadDTO.id = String.valueOf(dto.id);
				empInterviewExternalPanelDocumentUploadDTO.setActualPath(dbo.getPanelistDocumentUrlDBO().getFileNameUnique());
				empInterviewExternalPanelDocumentUploadDTO.setOriginalFileName(dbo.getPanelistDocumentUrlDBO().getFileNameOriginal());
				empInterviewExternalPanelDocumentUploadDTO.setNewFile(false);
				empInterviewExternalPanelDocumentUploadDTO.setProcessCode(dbo.getPanelistDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
				dto.empDocumentImages.add(empInterviewExternalPanelDocumentUploadDTO); 
			}
		}
		return dto;
	}
}
