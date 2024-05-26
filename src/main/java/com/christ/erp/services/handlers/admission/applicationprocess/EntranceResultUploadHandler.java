package com.christ.erp.services.handlers.admission.applicationprocess;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisAwsConfig;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreEntryDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreEntryDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardQuantitativeParameterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.common.UrlUploadedFilesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.transactions.admission.applicationprocess.EntranceResultUploadTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class EntranceResultUploadHandler {
	
	
	@Autowired
	private EntranceResultUploadTransaction entranceResultUploadTransaction;
	
	@Autowired
	RedisAwsConfig redisAwsConfig;
	
	@Autowired
	AWSS3FileStorageServiceHandler aWSS3FileStorageServiceHandler;
	
    @Autowired 
    AWSS3FileStorageService aWSS3FileStorageService;
    
    @Autowired 
    CommonApiTransaction commonApiTransaction;
	
	public Flux<SelectDTO> getEntranceTest() {
		return entranceResultUploadTransaction.getEntranceTest().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	
	public SelectDTO convertDBOToDTO(AdmSelectionProcessTypeDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getSelectionStageName());
		}
		return dto;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Mono<ApiResult> entranceResultUpload(String yearId, String selectionTypeId,Mono<EmpApplnAdvertisementImagesDTO> details, String userId)   {
		Map<Integer, List<String>> map1 = new HashMap<>();
		Map<Integer, Integer> map2 = new HashMap<>();
		List<Integer> duplicateApplicationNo = new ArrayList<Integer>();
		List<Integer> applicationNos  = new ArrayList<Integer>();
		List<Integer> values = new ArrayList<Integer>();
		List<EmpApplnAdvertisementImagesDTO> dto= new ArrayList<EmpApplnAdvertisementImagesDTO>();
		return details.handle((data1,synchronousSink) -> {
			dto.add(data1);
			// Set up S3 client
			S3Client s3Client=aWSS3FileStorageServiceHandler.getS3Client(data1.getProcessCode());
			try {
					// Get the GetObjectRequest
					GetObjectRequest getObjectRequest = aWSS3FileStorageServiceHandler.getObjectRequest(data1.getProcessCode(),data1.getUniqueFileName());
				
					// Get the ResponseInputStream from the GetObjectResponse
					ResponseInputStream<GetObjectResponse> getObjectResponse = s3Client.getObject(getObjectRequest);
			     
					// Convert the ResponseInputStream to a regular InputStream
					InputStream objectData = getObjectResponse;
				 
		            // Read the Excel file using Apache POI from the input stream
		            Workbook workbook = WorkbookFactory.create(objectData);
		            Sheet sheet = workbook.getSheetAt(0); // Assuming it's the first sheet

		        	if(!Utils.isNullOrEmpty(sheet.getRow(0))) {
						int rowLength = sheet.getRow(0).getLastCellNum();
						Integer p = 1;
						for(Row row : sheet) {
							if(p != 1) {
								map1.put(p,  new ArrayList<String>());
								for(int cn=0; cn<rowLength; cn++) {
									if(cn == 0) {
										Cell cell = row.getCell(cn); 
										if(cell != null) {
											if(!map2.containsKey((int)cell.getNumericCellValue())) {
											    map2.put((int)cell.getNumericCellValue(), (int)cell.getNumericCellValue());
											    applicationNos.add((int)cell.getNumericCellValue());
											} else {
												duplicateApplicationNo.add((int)cell.getNumericCellValue());
											}	
										}
									}
									Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
								    if(cell == null) {
								    	String cellValue = null;
								    	map1.get(p).add(cellValue);
								    } else if(cn == 0)  {
								    		map1.get(p).add(String.valueOf((int)cell.getNumericCellValue()));
								    } else {
								    	map1.get(p).add(String.valueOf(cell.getNumericCellValue()));
								    }
							    }
							}
							p++;
						}
					}
		        	workbook.close();
		            objectData.close();
				 
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
	            // Close the S3 client
	            s3Client.close();
	        }
		
			List<String> applicationNoEmpty = new ArrayList<String>();
			List<String> emptyScore = new ArrayList<String>();
			List<String> noSelectionType = new ArrayList<String>();
			List<String> applicationNotValid = new ArrayList<String>();
			List<String> marksNotValid = new ArrayList<String>();
			Tuple ScoreCardId = entranceResultUploadTransaction.getScoreCardId(Integer.parseInt(selectionTypeId));
			List<Tuple> quantitativeList = entranceResultUploadTransaction.scorecardQuantitativeCheck(Integer.parseInt(ScoreCardId.get(0).toString()));
		//	List<String> qualitativeList = entranceResultUploadTransaction.scorecardQualitativeCheck(Integer.parseInt(ScoreCardId.get(0).toString()));
			Integer admSelectionProcessTypeDetailsId = Integer.parseInt(ScoreCardId.get(1).toString());
		    values.add(admSelectionProcessTypeDetailsId);
		    Integer quantitativeParameterId = Integer.parseInt(quantitativeList.get(0).get(0).toString());
		    values.add(quantitativeParameterId);
		    Integer quantitativeParameterMaxValue = Integer.parseInt(quantitativeList.get(0).get(1).toString());
			values.add(quantitativeParameterMaxValue);
			values.add(Integer.parseInt(ScoreCardId.get(0).toString()));
			values.add(Integer.parseInt(yearId));
			values.add(Integer.parseInt(selectionTypeId));
			Map<Integer, StudentApplnEntriesDBO> mapList =  entranceResultUploadTransaction.getApplicantsDetails(applicationNos).stream().collect(Collectors.toMap(s -> s.getApplicationNo(), s -> s));
			List<AdmSelectionProcessDBO> allList = entranceResultUploadTransaction.getApplicantsSelectionProcessDetails(applicationNos,Integer.parseInt(yearId));
			Map<String, Integer> allDatas = null;
			if(Utils.isNullOrEmpty(applicationNotValid)) {
				allDatas = allList.stream().collect(Collectors.toMap(s ->
				(s.getErpAcademicYearDBO().getId().toString()+s.getStudentApplnEntriesDBO().getApplicationNo().toString()+s.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getId())
			             , s -> s.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getId()));
			}
			Map<String, Integer> allDatas1 = allDatas;
			Map<String, Integer> selectionDbo = allList.stream().collect(Collectors.toMap(s -> (s.getErpAcademicYearDBO().getId().toString()+s.getStudentApplnEntriesDBO().getApplicationNo().toString()+
					s.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getId()),s -> s.getId()));
			map1.forEach((k,v) -> {
				if(Utils.isNullOrEmpty(v.get(0))) {
					applicationNoEmpty.add(k.toString());
				}
				if(Utils.isNullOrEmpty(v.get(1))){
					emptyScore.add(v.get(0));
				}
				if(!Utils.isNullOrEmpty(v.get(0))) {
					if(!mapList.containsKey(Integer.parseInt(v.get(0)))) {
						applicationNotValid.add(v.get(0));
					}
				}
				if(Utils.isNullOrEmpty(applicationNotValid)) {
					if(allDatas1.containsKey(String.valueOf(yearId+v.get(0)+selectionTypeId))) {
						if(allDatas1.get(String.valueOf(yearId+v.get(0)+selectionTypeId)) != Integer.parseInt(selectionTypeId)) {
							noSelectionType.add(v.get(0));
						}
					} else {
					    noSelectionType.add(v.get(0));
					}
				}
				if(!Utils.isNullOrEmpty(v.get(1))) {
					if(Double.parseDouble(v.get(1)) > Double.parseDouble(values.get(2).toString())) {
						marksNotValid.add(v.get(0));
					}
				}
			});
			if(Utils.isNullOrEmpty(map1)) {
				synchronousSink.error(new GeneralException("Warning  Excel Sheet is Empty" ));
			} else if(!Utils.isNullOrEmpty(applicationNoEmpty)) {
				synchronousSink.error(new GeneralException("Warning  Application Number is Empty For the row " + applicationNoEmpty));
			} else if(!Utils.isNullOrEmpty(emptyScore)) {
				synchronousSink.error(new GeneralException("Warning  Score is Empty For these Application Number " + emptyScore));
			} else if(!Utils.isNullOrEmpty(applicationNotValid)) {
				synchronousSink.error(new GeneralException("Warning  Invalid  Application Number " + applicationNotValid));
			} else if((quantitativeList.size() !=1) ) { //|| (qualitativeList.size() !=0)
				synchronousSink.error(new GeneralException("Warning  Error in Scorecard Setting Check the scorecard settings in Selection Process Types"));
			} else if(!Utils.isNullOrEmpty(noSelectionType)) {
				synchronousSink.error(new GeneralException("Warning  Below application numbers does not have 'selected type' " +noSelectionType));
			} else if(!Utils.isNullOrEmpty(marksNotValid)){
				synchronousSink.error(new GeneralException("Warning  Below application numbers marks are more than defined Max Score " +marksNotValid));
			} else {
				synchronousSink.next(selectionDbo);
			}
		}).map(data2 -> convertDtoToDbo( (Map<String, Integer>) data2,map1, values,userId))
				.flatMap( s ->{ 
					EmpApplnAdvertisementImagesDTO empApplnAdvertisementImagesDTO=	dto.get(0);
					List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
					Set<UrlUploadedFilesDBO> urlUploadedFilesDBOSet= new HashSet<UrlUploadedFilesDBO>();
					UrlAccessLinkDBO urlAccessLinkDBO = null;
					       if(!Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO) && !Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO.getNewFile()) && empApplnAdvertisementImagesDTO.getNewFile() &&
					             !Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO.getOriginalFileName())) {
					    	   urlAccessLinkDBO = new UrlAccessLinkDBO();
					    	   urlAccessLinkDBO.setCreatedUsersId(Integer.parseInt(userId));
					    	   urlAccessLinkDBO.setRecordStatus('A');
					       }
					    if(!Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO) && !Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO.getNewFile()) && empApplnAdvertisementImagesDTO.getNewFile()) {
					    	urlAccessLinkDBO = aWSS3FileStorageService.createURLAccessLinkDBO(urlAccessLinkDBO, empApplnAdvertisementImagesDTO.getProcessCode(), empApplnAdvertisementImagesDTO.getUniqueFileName(), empApplnAdvertisementImagesDTO.getOriginalFileName(), Integer.parseInt(userId));
					    	UrlUploadedFilesDBO dbo=new UrlUploadedFilesDBO();
					    	dbo.setUrlAccessLinkDBO(urlAccessLinkDBO);
					    	dbo.setErpUsersDBO( new ErpUsersDBO());
					    	dbo.getErpUsersDBO().setId(Integer.parseInt(userId));
					    	dbo.setUploadedTime("10:00");
					    	dbo.setCreatedUsersId(Integer.parseInt(userId));
					    	dbo.setModifiedUsersId(Integer.parseInt(userId));
					    	dbo.setRecordStatus('A');
					    	urlUploadedFilesDBOSet.add(dbo);
					    	urlAccessLinkDBO.setUrlUploadedFilesDBO(urlUploadedFilesDBOSet);
					    }
					    if(!Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO) && !Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO.getUniqueFileName()) && !Utils.isNullOrEmpty(empApplnAdvertisementImagesDTO.getNewFile()) && empApplnAdvertisementImagesDTO.getNewFile()) {
					       uniqueFileNameList.addAll(aWSS3FileStorageService.createFileListForActualCopy(empApplnAdvertisementImagesDTO.getProcessCode(), empApplnAdvertisementImagesDTO.getUniqueFileName()));
					    }
					    entranceResultUploadTransaction.save(s);
					 if((!Utils.isNullOrEmpty(uniqueFileNameList)) ) {
						 commonApiTransaction.save(urlAccessLinkDBO);
						 aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList).subscribe();
						}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}
	
	private List<AdmSelectionProcessScoreDBO> convertDtoToDbo( Map<String, Integer> mapList, Map<Integer, List<String>> map1,List<Integer> values, String userId) {
		List<AdmSelectionProcessScoreDBO> value = new ArrayList<AdmSelectionProcessScoreDBO>();
		List<Integer> selectionProcessId = mapList.values().stream().collect(Collectors.toList());
		//fetch records which r already in table
		Map<String, AdmSelectionProcessScoreDBO> admSelectionProcessScoreDBOMap =  entranceResultUploadTransaction.getAdmSelectionProcessScoreDBODetails(selectionProcessId,values.get(0)).stream().collect(Collectors.toMap(s -> s.getAdmSelectionProcessDBO().getId()+"-"+s.getAdmSelectionProcessTypeDetailsDBO().getId(), s -> s));
		map1.forEach((k,v)-> {
			if(mapList.containsKey(values.get(4)+v.get(0)+values.get(5))) { 
				Integer admSelectionProcessDBOId = mapList.get(values.get(4)+v.get(0)+values.get(5));
				if(!admSelectionProcessScoreDBOMap.containsKey(admSelectionProcessDBOId+"-"+values.get(0))) {
				AdmSelectionProcessScoreDBO admSelectionProcessScoreDBO = new AdmSelectionProcessScoreDBO();
				admSelectionProcessScoreDBO.setAdmSelectionProcessDBO(new AdmSelectionProcessDBO());
				admSelectionProcessScoreDBO.getAdmSelectionProcessDBO().setId(admSelectionProcessDBOId);
				admSelectionProcessScoreDBO.setAdmSelectionProcessTypeDetailsDBO(new AdmSelectionProcessTypeDetailsDBO());
				admSelectionProcessScoreDBO.getAdmSelectionProcessTypeDetailsDBO().setId(values.get(0));
				admSelectionProcessScoreDBO.setCreatedUsersId(Integer.parseInt(userId));
				admSelectionProcessScoreDBO.setRecordStatus('A');
				admSelectionProcessScoreDBO.setAdmSelectionProcessScoreEntryDBOSet(new HashSet<AdmSelectionProcessScoreEntryDBO>());
				BigDecimal maxScore = new BigDecimal("000000.00");
				BigDecimal scoreEntered = new BigDecimal("000000.00");
				AdmSelectionProcessScoreEntryDBO subDbo =  new AdmSelectionProcessScoreEntryDBO();
				subDbo.setAdmSelectionProcessScoreDBO(admSelectionProcessScoreDBO);
				subDbo.setAdmScoreCardDBO(new AdmScoreCardDBO());
				subDbo.getAdmScoreCardDBO().setId(values.get(3));
			    subDbo.setErpUsersDBO( new ErpUsersDBO());
			    subDbo.getErpUsersDBO().setId(Integer.parseInt(userId));
			    maxScore = maxScore.add(BigDecimal.valueOf(values.get(2)));
			    subDbo.setMaxScore(maxScore);
			    scoreEntered =scoreEntered.add(BigDecimal.valueOf(Double.parseDouble(v.get(1))));
			    subDbo.setScoreEntered(scoreEntered);
			    subDbo.setCreatedUsersId(Integer.parseInt(userId));
			    subDbo.setRecordStatus('A');
			    subDbo.setAdmSelectionProcessScoreEntryDetailsDBOSet(new HashSet<AdmSelectionProcessScoreEntryDetailsDBO>());
			    AdmSelectionProcessScoreEntryDetailsDBO subDbo1 = new AdmSelectionProcessScoreEntryDetailsDBO();
			    subDbo1.setAdmSelectionProcessScoreEntryDBO(subDbo);
			    subDbo1.setAdmScoreCardQuantitativeParameterDBO(new AdmScoreCardQuantitativeParameterDBO());
				subDbo1.getAdmScoreCardQuantitativeParameterDBO().setId(values.get(1));
				subDbo1.setQuantitativeParameterMaxScore(maxScore);
				subDbo1.setQuantitativeParameterScoreEntered(scoreEntered);
				subDbo1.setCreatedUsersId(Integer.parseInt(userId));
				subDbo1.setRecordStatus('A');
				subDbo.getAdmSelectionProcessScoreEntryDetailsDBOSet().add(subDbo1);
				admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet().add(subDbo);
				value.add(admSelectionProcessScoreDBO);
				}else {
					AdmSelectionProcessScoreDBO admSelectionProcessScoreDBO = admSelectionProcessScoreDBOMap.get(admSelectionProcessDBOId+"-"+values.get(0));
					BigDecimal maxScore = new BigDecimal("000000.00");
					BigDecimal scoreEntered = new BigDecimal("000000.00");
					AdmSelectionProcessScoreEntryDBO subDbo = admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet().iterator().next();
				    maxScore = maxScore.add(BigDecimal.valueOf(values.get(2)));
				    subDbo.setMaxScore(maxScore);
				    scoreEntered =scoreEntered.add(BigDecimal.valueOf(Double.parseDouble(v.get(1))));
				    subDbo.setScoreEntered(scoreEntered);
				    AdmSelectionProcessScoreEntryDetailsDBO subDbo1 = subDbo.getAdmSelectionProcessScoreEntryDetailsDBOSet().iterator().next();
					subDbo1.setQuantitativeParameterMaxScore(maxScore);
					subDbo1.setQuantitativeParameterScoreEntered(scoreEntered);
					subDbo.getAdmSelectionProcessScoreEntryDetailsDBOSet().add(subDbo1);
					admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet().add(subDbo);
					value.add(admSelectionProcessScoreDBO);
				}
			}
		});
		return value;
	}

	public Mono<FileUploadDownloadDTO> entranceResultUploadDownloadFormat() {
		Tuple tuple = entranceResultUploadTransaction.entranceResultUploadDownloadFormat();
		var apiResult = new FileUploadDownloadDTO();
		apiResult.setActualPath(tuple.get("fileNameUnique").toString());
		apiResult.setProcessCode(tuple.get("uploadProcessCode").toString());
		apiResult.setOriginalFileName(tuple.get("fileNameOriginal").toString());
		return Mono.just(apiResult);
	}
}