package com.christ.erp.services.handlers.employee.recruitment;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;

import com.christ.erp.services.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.common.UrlFolderListDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAdvertisementDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAdvertisementImagesDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.aws.URLFolderListDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.employee.recruitment.PublishJobAdvertisementTransaction;

@Service
public class PublishJobAdvertisementHandler {
	private static volatile PublishJobAdvertisementHandler publishJobAdvertisementHandler = null;
	
	@Autowired
	PublishJobAdvertisementTransaction publishJobAdvertisementTransaction;;

	@Autowired
	PublishJobAdvertisementTransaction publishJobAdvertisementTransaction1;

	@Autowired
	AWSS3FileStorageServiceHandler aWSS3FileStorageServiceHandler;
	
	@Autowired
	CommonApiHandler commonApiHandler;

	public static PublishJobAdvertisementHandler getInstance() {
		if(publishJobAdvertisementHandler==null) {
			publishJobAdvertisementHandler = new PublishJobAdvertisementHandler();
		}
		return publishJobAdvertisementHandler;
	}

	public List<EmpApplnAdvertisementDTO> getGridData() {
		List<EmpApplnAdvertisementDTO> academicYearDTO = new ArrayList<>();
		List<Tuple> list;
		try {
			list = publishJobAdvertisementTransaction.getGridData();
			for(Tuple tuple : list) {
				EmpApplnAdvertisementDTO gridDTO = new EmpApplnAdvertisementDTO();
				if(!Utils.isNullOrEmpty(tuple.get("advertisementNo"))) {
					gridDTO.advertisementNo = tuple.get("advertisementNo").toString();
				}

				gridDTO.id = String.valueOf(tuple.get("ID"));
				if(!Utils.isNullOrEmpty(tuple.get("startDate"))) {
					gridDTO.setStartDate(Utils.convertStringDateToLocalDate(tuple.get("startDate").toString()));
				//	gridDTO.startDate = tuple.get("startDate").toString();
				}
				if(!Utils.isNullOrEmpty(tuple.get("endDate"))) {
					gridDTO.setEndDate(Utils.convertStringDateToLocalDate(tuple.get("endDate").toString()));
				//	gridDTO.endDate = Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(tuple.get("endDate").toString()));
				}
				if(!Utils.isNullOrEmpty(tuple.get("academicYear"))) {
					gridDTO.year = tuple.get("academicYear").toString();
				}
				if(Utils.isNullOrEmpty(tuple.get("isCommonAdvertisement")) || tuple.get("isCommonAdvertisement").equals(true)) {
					gridDTO.templateType = "Common Template";	
				}
				academicYearDTO.add(gridDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return academicYearDTO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ApiResult<ModelBaseDTO> saveOrUpdate(EmpApplnAdvertisementDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		if(data.getIsCommonAdvertisement().equals(true)) {
			Boolean isDuplicateAdvertisement = publishJobAdvertisementTransaction.isDuplicateAdvertisement(data.id);
			if(!isDuplicateAdvertisement) {
				saveFile(data, userId , result); 
			} else {
				result.failureMessage= "Content Already exists for Common Advertisement";
				result.success = false;	
			}
		} else {
//			LocalDate start = Utils.convertStringDateToLocalDate(data.startDate);
//			LocalDate end =  Utils.convertStringDateToLocalDate(data.endDate);
			LocalDate start = data.getStartDate();
			LocalDate end  = data.getEndDate();
			Boolean isDuplicateDates = publishJobAdvertisementTransaction.duplicateDate(start, end, data.id);
			Boolean isDuplicate = publishJobAdvertisementTransaction.isDuplicate(data.advertisementNo,data.id);
			if(!isDuplicateDates) {
				if(!isDuplicate) {
					saveFile(data, userId, result);
				}
				else {
					//deleteFile(data.empApplnAdvertisementImages);
					result.failureMessage="Duplicate record exists for Advertisement No:"+data.advertisementNo;
					result.success = false;
				}
			}
			else {
				//deleteFile(data.empApplnAdvertisementImages);
				result.failureMessage="Duplicate record exists for these selected Dates";
				result.success = false;
			}
		}
		return result;
	}

//	public void saveFile(EmpApplnAdvertisementDTO data, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
//		EmpApplnAdvertisementDBO header = null;
//		if(Utils.isNullOrWhitespace(data.id) == false) {
//			header =  publishJobAdvertisementTransaction.getEmpJobAdvertisement(Integer.parseInt(data.id));
//		}
//		if(header == null) {
//			header = new EmpApplnAdvertisementDBO();
//			header.createdUsersId = Integer.parseInt(userId);
//		}
//		try {
//			if(!Utils.isNullOrEmpty(data.getAcademicYear())) {
//				ErpAcademicYearDBO erpAcademicYearDBO = new ErpAcademicYearDBO();
//				erpAcademicYearDBO.id = Integer.parseInt(data.getAcademicYear().getValue());
//				header.erpAcademicYearDBO = erpAcademicYearDBO;
//			}
//			if(!Utils.isNullOrEmpty(data.advertisementContent)) {
//				header.advertisementContent = data.advertisementContent;
//			}
//			if(!Utils.isNullOrEmpty(data.advertisementNo)) {
//				header.advertisementNo = data.advertisementNo;
//			}
//			if(!Utils.isNullOrEmpty(data.otherInfo) || Utils.isNullOrEmpty(data.getOtherInfo())) {
//				header.otherInfo = data.otherInfo;
//			}
//			if(data.isCommonAdvertisement.equals(true)) {
//				header.isCommonAdvertisement = true;
//			} else {
//				header.isCommonAdvertisement = false;	
//			}
//			header.recordStatus = 'A';
//			if(!Utils.isNullOrEmpty(data.endDate)) {
//				header.advertisementEndDate = Utils.convertStringDateToLocalDate(data.endDate); 
//			}
//			if(!Utils.isNullOrEmpty(data.startDate)) {
//				header.advertisementStartDate = Utils.convertStringDateToLocalDate(data.startDate);  
//			}
//			if(header.id != null) {
//				header.modifiedUsersId = Integer.parseInt(userId);
//			} 
//			publishJobAdvertisementTransaction.saveOrUpdate(header);
//			if(header.id != 0) {
//				result.success = true;
//				result.dto = new ModelBaseDTO();
//				result.dto.id = header.id.toString();
//			}
//			if(header.id!=0) {
//				for(EmpApplnAdvertisementImagesDTO item:data.empApplnAdvertisementImages) {
//					EmpApplnAdvertisementImagesDBO empApplnAdvertisementImagesDBO = null;
//					if(Utils.isNullOrWhitespace(item.id) == false) {
//						empApplnAdvertisementImagesDBO = publishJobAdvertisementTransaction.getEmpJobAdvertisementImages(Integer.parseInt(item.id));
//					}
//					if(empApplnAdvertisementImagesDBO == null) {
//						empApplnAdvertisementImagesDBO = new EmpApplnAdvertisementImagesDBO();
//						empApplnAdvertisementImagesDBO.createdUsersId = Integer.parseInt(userId);
//					}
//					empApplnAdvertisementImagesDBO.empApplnAdvertisementId = header;
//					empApplnAdvertisementImagesDBO.recordStatus = item.recordStatus;
//					File file = new File("ImageUpload//"+item.fileName+"."+item.extension);
//					empApplnAdvertisementImagesDBO.uploadAdvertisementUrl=file.getAbsolutePath();
//					if(empApplnAdvertisementImagesDBO.id != null) {
//						empApplnAdvertisementImagesDBO.modifiedUsersId = Integer.parseInt(userId);
//					}
//					publishJobAdvertisementTransaction.saveOrUpdate(empApplnAdvertisementImagesDBO);
//				}
//			}
//			result.success=true;
//		} catch (java.text.ParseException error) {
//			Utils.log(error.getMessage());
//		}	
//	}
	
	public void saveFile(EmpApplnAdvertisementDTO data, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
		EmpApplnAdvertisementDBO  header = null;
			if (!Utils.isNullOrWhitespace(data.id)) {
				header =  publishJobAdvertisementTransaction.getEmpJobAdvertisement(Integer.parseInt(data.id));
			}
	  
			if (header == null) {
				header = new EmpApplnAdvertisementDBO();
				header.createdUsersId = Integer.parseInt(userId);
			} else {
				header.modifiedUsersId = Integer.parseInt(userId);
			}
			if(!Utils.isNullOrEmpty(data.getAcademicYear())) {
				ErpAcademicYearDBO erpAcademicYearDBO = new ErpAcademicYearDBO();
				erpAcademicYearDBO.id = Integer.parseInt(data.getAcademicYear().getValue());
				header.erpAcademicYearDBO = erpAcademicYearDBO;
			}
			if(!Utils.isNullOrEmpty(data.advertisementContent)) {
				header.advertisementContent = data.advertisementContent;
			}
			if(!Utils.isNullOrEmpty(data.advertisementNo)) {
				header.advertisementNo = data.advertisementNo;
			}
			if(!Utils.isNullOrEmpty(data.otherInfo) || Utils.isNullOrEmpty(data.getOtherInfo())) {
				header.otherInfo = data.otherInfo;
			}
			if(data.isCommonAdvertisement.equals(true)) {
				header.isCommonAdvertisement = true;
				if(!Utils.isNullOrEmpty(data.advertisementContent))
					AppConstants.ADVERTISEMENT_COMMON_CONTENT = data.advertisementContent;
			} else {
				header.isCommonAdvertisement = false;
				if(!Utils.isNullOrEmpty(data.getStartDate()) && !Utils.isNullOrEmpty(data.getEndDate()) && !Utils.isNullOrEmpty(data.advertisementContent)) {
					if(!LocalDate.now().isBefore(data.getStartDate()) && !LocalDate.now().isAfter(data.getEndDate())){
						AppConstants.ADVERTISEMENT_CONTENT = data.advertisementContent;
					}
				}
			}
			header.recordStatus = 'A';
//			if(!Utils.isNullOrEmpty(data.endDate)) {
//				header.advertisementEndDate = Utils.convertStringDateToLocalDate(data.endDate); 
//			}
//			if(!Utils.isNullOrEmpty(data.startDate)) {
//				header.advertisementStartDate = Utils.convertStringDateToLocalDate(data.startDate);  
//			}
			if(!Utils.isNullOrEmpty(data.getStartDate())) {
				header.advertisementStartDate = data.getStartDate(); 
			}
			if(!Utils.isNullOrEmpty(data.getEndDate())) {
				header.advertisementEndDate = data.getEndDate();  
			}
			if(header.id != null) {
				header.modifiedUsersId = Integer.parseInt(userId);
			} 
			Set<EmpApplnAdvertisementImagesDBO> empApplnAdvertisementImagesDBOUpdate = new HashSet<EmpApplnAdvertisementImagesDBO>();
			Set<EmpApplnAdvertisementImagesDBO> existDBOSet= header.empApplnAdvertisementImagesSet;
			Map<Integer,EmpApplnAdvertisementImagesDBO> existDBOMap = new HashMap<Integer, EmpApplnAdvertisementImagesDBO>();
			if (!Utils.isNullOrEmpty(existDBOSet)) {
				existDBOSet.forEach(dbo-> {
					if (dbo.recordStatus=='A') {
						existDBOMap.put(dbo.id, dbo);
					}
				});
			}
			List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
			if(!Utils.isNullOrEmpty(data.empApplnAdvertisementImages)) {
				for (EmpApplnAdvertisementImagesDTO sub : data.empApplnAdvertisementImages) {
		        	URLFolderListDTO folderListDTO = commonApiHandler.getAllFolderListForMenu(sub.getProcessCode());

					EmpApplnAdvertisementImagesDBO dbo = null;
					if (!Utils.isNullOrWhitespace(sub.id) && existDBOMap.containsKey(Integer.parseInt(sub.id))) {	
		    			dbo = existDBOMap.get((Integer.parseInt(sub.id)));
		    			existDBOMap.remove(Integer.parseInt(sub.id));
	                } else {
						dbo = new EmpApplnAdvertisementImagesDBO();
	        			dbo.createdUsersId = Integer.parseInt(userId);
					}
	    			dbo.modifiedUsersId = Integer.parseInt(userId);
					dbo.empApplnAdvertisementId = header;
					dbo.recordStatus = sub.recordStatus;
					//File file = new File("ImageUpload//"+sub.fileName+"."+sub.extension);
					//dbo.uploadAdvertisementUrl=file.getAbsolutePath();	
					UrlAccessLinkDBO urlAccessLinkDBO;
					if(Utils.isNullOrEmpty(dbo.getUrlAccessLinkDBO())) {
						urlAccessLinkDBO = new UrlAccessLinkDBO();
						urlAccessLinkDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					else {
						urlAccessLinkDBO = dbo.getUrlAccessLinkDBO();
					}
					urlAccessLinkDBO.setModifiedUsersId(Integer.parseInt(userId));
					if(sub.getNewFile()) {
						FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
						fileUploadDownloadDTO.setTempPath(folderListDTO.getTempFolderPath());
						fileUploadDownloadDTO.setActualPath(folderListDTO.getFolderPath());
				        fileUploadDownloadDTO.setUniqueFileName(sub.getUniqueFileName());
				        fileUploadDownloadDTO.setBucketName(folderListDTO.getBucketName());
				        fileUploadDownloadDTO.setTempBucketName(folderListDTO.getTempBucketName());
						if(sub.getNewFile()) {
							uniqueFileNameList.add(fileUploadDownloadDTO);
						}
						urlAccessLinkDBO.setFileNameUnique(folderListDTO.getFolderPath() + sub.getUniqueFileName());
						urlAccessLinkDBO.setTempFileNameUnique(folderListDTO.getTempFolderPath()+ sub.getUniqueFileName());
						urlAccessLinkDBO.setFileNameOriginal(sub.getOriginalFileName());
						UrlFolderListDBO urlFolderListDBO = new UrlFolderListDBO();
						urlFolderListDBO.setId(folderListDTO.getFolderListId());
						urlAccessLinkDBO.setUrlFolderListDBO(urlFolderListDBO);
						urlAccessLinkDBO.setRecordStatus('A');
						urlAccessLinkDBO.setIsQueued(false);
						urlAccessLinkDBO.setIsServiced(true);
					}
					dbo.setUrlAccessLinkDBO(urlAccessLinkDBO);
					dbo.recordStatus = 'A';
					empApplnAdvertisementImagesDBOUpdate.add(dbo);
				}
			}
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
			
			 
			if (!Utils.isNullOrEmpty(existDBOMap)) {
				existDBOMap.forEach((entry, value)-> {
					value.modifiedUsersId = Integer.parseInt(userId);
					value.recordStatus = 'D';
					UrlAccessLinkDBO urlAccessLinkDBO = value.getUrlAccessLinkDBO();
					if (!Utils.isNullOrEmpty(urlAccessLinkDBO)) {
						urlAccessLinkDBO.setRecordStatus('D');
						value.setUrlAccessLinkDBO(urlAccessLinkDBO);
						empApplnAdvertisementImagesDBOUpdate.add(value);
					}
				});
			}
			header.empApplnAdvertisementImagesSet = empApplnAdvertisementImagesDBOUpdate;
			if (publishJobAdvertisementTransaction.saveOrUpdate(header)) {
				result.success = true;
			}
		}

	public void deleteFile(List<EmpApplnAdvertisementImagesDTO> empappAdvertisementImagesDTO) {
		for(EmpApplnAdvertisementImagesDTO item:empappAdvertisementImagesDTO) {
			if(!Utils.isNullOrEmpty(item.newFile)) {
				File file = new File("ImageUpload//"+item.fileName+"."+item.extension);
				file = new File(file.getAbsolutePath());
				if(file.exists()) { 
					file.delete();
				}
			}
		}
	}

	public EmpApplnAdvertisementDTO selectEmpApplnAdvertisement(String id) throws Exception {
		EmpApplnAdvertisementDTO empApplnAdvertisementDTO = new EmpApplnAdvertisementDTO();
		EmpApplnAdvertisementDBO empApplnAdvertisementDBO = publishJobAdvertisementTransaction.getEmpJobAdvertisement(Integer.parseInt(id));
		if(empApplnAdvertisementDBO != null) {
			empApplnAdvertisementDTO = new EmpApplnAdvertisementDTO();
			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.id)) {
				empApplnAdvertisementDTO.id = empApplnAdvertisementDBO.id.toString();
			}
			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.erpAcademicYearDBO)) {
				empApplnAdvertisementDTO.academicYear = new SelectDTO();
				empApplnAdvertisementDTO.academicYear.setValue(String.valueOf(empApplnAdvertisementDBO.erpAcademicYearDBO.id));
				empApplnAdvertisementDTO.academicYear.setLabel(empApplnAdvertisementDBO.erpAcademicYearDBO.getAcademicYearName());
			}
			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.advertisementNo)) {
				empApplnAdvertisementDTO.advertisementNo = empApplnAdvertisementDBO.advertisementNo;
			}
			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.advertisementContent)) {
				empApplnAdvertisementDTO.advertisementContent = empApplnAdvertisementDBO.advertisementContent;
			}
			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.otherInfo)) {
				empApplnAdvertisementDTO.otherInfo = empApplnAdvertisementDBO.otherInfo;
			}
			if(Utils.isNullOrEmpty(empApplnAdvertisementDBO.isCommonAdvertisement)  ||empApplnAdvertisementDBO.isCommonAdvertisement.equals(true)) {
				empApplnAdvertisementDTO.isCommonAdvertisement = true;	
			} else {
				empApplnAdvertisementDTO.isCommonAdvertisement = false;	
			}
//			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.advertisementStartDate)) {
//				String formattedDate  = Utils.convertLocalDateToStringDate(empApplnAdvertisementDBO.advertisementStartDate);
//				empApplnAdvertisementDTO.startDate = formattedDate;
//			}
//			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.advertisementEndDate)) {
//				String formattedDate1 = Utils.convertLocalDateToStringDate(empApplnAdvertisementDBO.advertisementEndDate);
//				empApplnAdvertisementDTO.endDate = formattedDate1;
//			}
			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.advertisementStartDate)) {
				LocalDate formattedDate  = empApplnAdvertisementDBO.advertisementStartDate;
				empApplnAdvertisementDTO.setStartDate(formattedDate);
			}
			if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.advertisementEndDate)) {
				LocalDate formattedDate1 = empApplnAdvertisementDBO.advertisementEndDate;
				empApplnAdvertisementDTO.setEndDate(formattedDate1);
			}
			empApplnAdvertisementDTO.empApplnAdvertisementImages = new ArrayList<>();
			try {
				for(EmpApplnAdvertisementImagesDBO item:empApplnAdvertisementDBO.empApplnAdvertisementImagesSet) {
					if(item.recordStatus == 'A') {
						EmpApplnAdvertisementImagesDTO empApplnAdvertisementImagesDTO = new EmpApplnAdvertisementImagesDTO();
						empApplnAdvertisementImagesDTO.id = String.valueOf(item.id);
						empApplnAdvertisementImagesDTO.setActualPath(item.getUrlAccessLinkDBO().getFileNameUnique());
						empApplnAdvertisementImagesDTO.setOriginalFileName(item.getUrlAccessLinkDBO().getFileNameOriginal());
						empApplnAdvertisementImagesDTO.setNewFile(false);
						empApplnAdvertisementImagesDTO.setProcessCode(item.getUrlAccessLinkDBO().getUrlFolderListDBO().getUploadProcessCode());
						empApplnAdvertisementDTO.empApplnAdvertisementImages.add(empApplnAdvertisementImagesDTO);
						
						/*File file = new File(item.urlAccessLinkDBO.getFileNameUnique());
						if(file.exists() && !file.isDirectory()) { 
							empApplnAdvertisementImagesDTO.extension = item.uploadAdvertisementUrl.substring(item.uploadAdvertisementUrl.lastIndexOf(".")+1);
							String fileName = new File(item.uploadAdvertisementUrl).getName();
							empApplnAdvertisementImagesDTO.url = item.uploadAdvertisementUrl;
							empApplnAdvertisementImagesDTO.fileName = fileName.replaceFirst("[.][^.]+$", "");
							empApplnAdvertisementImagesDTO.recordStatus = item.recordStatus;
							empApplnAdvertisementDTO.empApplnAdvertisementImages.add(empApplnAdvertisementImagesDTO);
						}*/
					}
				}
			} catch (Exception error) {
				Utils.log(error.getMessage());
			}
		}
		return empApplnAdvertisementDTO;
	}

	public boolean deleteJobAdvertisement(String  id,String userId) {
		try {
			EmpApplnAdvertisementDBO empApplnAdvertisementDBO =  publishJobAdvertisementTransaction.getEmpJobAdvertisement(Integer.parseInt(id));
			if(empApplnAdvertisementDBO != null) {
				empApplnAdvertisementDBO.recordStatus = 'D';
				empApplnAdvertisementDBO.modifiedUsersId = Integer.parseInt(userId);
				for (EmpApplnAdvertisementImagesDBO item : empApplnAdvertisementDBO.empApplnAdvertisementImagesSet) {
					item.recordStatus = 'D';
					item.modifiedUsersId = Integer.parseInt(userId);
				}
				if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.getIsCommonAdvertisement()) && empApplnAdvertisementDBO.getIsCommonAdvertisement()){
					AppConstants.ADVERTISEMENT_COMMON_CONTENT = "";
				}
				if(!Utils.isNullOrEmpty(empApplnAdvertisementDBO.getAdvertisementStartDate()) && !Utils.isNullOrEmpty(empApplnAdvertisementDBO.getAdvertisementEndDate())) {
					if(!LocalDate.now().isBefore(empApplnAdvertisementDBO.getAdvertisementStartDate()) && !LocalDate.now().isAfter(empApplnAdvertisementDBO.getAdvertisementEndDate())){
						AppConstants.ADVERTISEMENT_CONTENT = "";
					}
				}
				if(empApplnAdvertisementDBO.id != null) {
					return publishJobAdvertisementTransaction.saveOrUpdate(empApplnAdvertisementDBO);
				}
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
