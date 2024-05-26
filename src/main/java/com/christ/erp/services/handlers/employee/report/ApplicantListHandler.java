package com.christ.erp.services.handlers.employee.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoHeadingDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoParameterDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEligiblityTestDTO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoEntriesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnEducationalDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnLocationPrefDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnPersonalDataDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnWorkExperienceDTO;
import com.christ.erp.services.dto.employee.report.ApplicantListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.employee.report.ApplicantListTransaction;
import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")
@Service
public class ApplicantListHandler {
	
	@Autowired
	private ApplicantListTransaction applicantListTransaction;
	
	@Autowired
	AWSS3FileStorageService aWSS3FileStorageService;
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> locationEnable() {
		ApiResult result = new ApiResult();
		result.setSuccess(true);
		return Mono.just(result);
	}
	
	public Mono<List<ApplicantListDTO>> getGridData(Mono<ApplicantListDTO> data, String userId) {
		return data.handle((applicantListDTO,synchronousSink) -> {
			List<Tuple> list = null;
			list = applicantListTransaction.getApplicantData(applicantListDTO);
			if(Utils.isNullOrEmpty(list)) {
				synchronousSink.error(new NotFoundException(null));
			} else {
				synchronousSink.next(list);
			}
		}).cast(ArrayList.class)
				.map(data1 -> convertDBOToDTO(data1,userId))
				.flatMap( s -> {
					return  s;
				});
	}

	public Mono<List<ApplicantListDTO>> convertDBOToDTO( List<Tuple> dbos, String userId) {
		List<ApplicantListDTO> dtoData = new ArrayList<ApplicantListDTO>();
		dbos.forEach(dbo -> {
			if(!Utils.isNullOrEmpty(dbo)) {
				ApplicantListDTO dto1 = new ApplicantListDTO();
				dto1.setEmpApplnEntriesId(Integer.parseInt(String.valueOf(dbo.get("id"))));
				dto1.setApplicationNo(String.valueOf(dbo.get("applicationNo")));
				dto1.setApplicantName(String.valueOf(dbo.get("name")));
				dto1.setGender(String.valueOf(dbo.get("gender")));
				dto1.setEmail(!Utils.isNullOrEmpty(dbo.get("mail"))? String.valueOf(dbo.get("mail")):"");
				String mobile = "";
				if(!Utils.isNullOrEmpty(dbo.get("code"))) {
					mobile = String.valueOf(dbo.get("code"))+"-";
				}
				if(!Utils.isNullOrEmpty(dbo.get("mobile"))) {
					mobile += String.valueOf(dbo.get("mobile"));
				}
				dto1.setMobile(mobile);
				dto1.setStatus(!Utils.isNullOrEmpty(dbo.get("status"))? String.valueOf(dbo.get("status")):"");
				if(!Utils.isNullOrEmpty(dbo.get("file_name_unique")) && !Utils.isNullOrEmpty(dbo.get("upload_process_code")) && !Utils.isNullOrEmpty(dbo.get("file_name_original"))) {
					FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
					fileUploadDownloadDTO.setActualPath(dbo.get("file_name_unique").toString());
					fileUploadDownloadDTO.setProcessCode(dbo.get("upload_process_code").toString());
					fileUploadDownloadDTO.setOriginalFileName(dbo.get("file_name_original").toString());
					aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
					dto1.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
				}
				dtoData.add(dto1);
			}
		});
		return Mono.just(dtoData);
	}
	
	public Mono<ResponseEntity<InputStreamResource>>  getEmpApplicantsDetails(Mono<ApplicantListDTO> data1, String userId) {
		List<ApplicantListDTO> dtos = new ArrayList<ApplicantListDTO>();
		return data1.handle((employeeListDTO,synchronousSink) -> {
			ApplicantListDTO dto =  employeeListDTO;
			dtos.add(dto);
			List<EmpApplnEntriesDTO> list = applicantListTransaction.empDBO(employeeListDTO.getEmpIds());
			if(Utils.isNullOrEmpty(list)) {
				synchronousSink.error(new NotFoundException(null));
			} else {
				synchronousSink.next(list);
			}
		}).cast(ArrayList.class)
				.map(data -> convertDBOToExcel(data,dtos.get(0),userId))
				.flatMap( s -> {
					return s ;
				});
	}

	public Mono<ResponseEntity<InputStreamResource>> convertDBOToExcel(List<EmpApplnEntriesDTO> dbos,ApplicantListDTO dto,String userId) {
		List<Integer> empIds = new ArrayList<Integer>();
		dbos.forEach(dbo -> {
			empIds.add(Integer.parseInt(dbo.getApplicantId()));
		});
		int rowCount = 0;
		int maxQulification = 0;
		int otherMaxCount = 0;
		XSSFWorkbook   workbook = new XSSFWorkbook  ();
		XSSFSheet  sheet = workbook.createSheet("EmployeeListSheet");
		XSSFRow  rowhead = sheet.createRow((short)rowCount++);
		CellStyle style =  workbook.createCellStyle();
		Font font =  workbook.createFont();
		font.setBold(true);
		style.setFont(font); 
		int headingIndex = 0;
		Integer workCount = 0;
		sheet.createFreezePane(0,1);
		Set<Integer> qualificationOrders = new HashSet<Integer>();
		Map<Integer,List<EmpApplnLocationPrefDTO>> empApplnLocationPrefDTOMap =null;
		Map<Integer, EmpApplnPersonalDataDTO> empApplnPersonalDataDTOMap= null;
		Map<Integer,List<Tuple>> empApplnSubjSpecializationPrefDTOMap =null;
		Map<Integer, Integer> qualificationsCountsMap = new HashMap<Integer, Integer>();
		Map<Integer, ErpQualificationLevelDBO> qualificationLevelMap = new HashMap<Integer, ErpQualificationLevelDBO>();
		Map<Integer,List<EmpApplnEducationalDetailsDTO>> empApplnEducationalDetailsDTOMap =null;
		Map<Integer, List<EmpEligiblityTestDTO>> empEligibilityTestDBOMap = null;
		Map<Integer, List<EmpApplnWorkExperienceDTO>> currentworkMap = null;
		Map<Integer, List<EmpApplnWorkExperienceDTO>> otherworkMap = null;
		Map<Integer, EmpApplnAddtnlInfoHeadingDBO> parameterNamesMap = null;
		Set<Integer> researchParameterIds = new LinkedHashSet<Integer>();
		Map<Integer, Map<Integer,String>> empApplnAddtnlInfoEntriesMap = new HashMap<Integer, Map<Integer,String>>();
	
		List<EmpApplnPersonalDataDTO> personalList =  applicantListTransaction.getPersonalData(empIds);
		if(!Utils.isNullOrEmpty(personalList)) {
			empApplnPersonalDataDTOMap = personalList.stream().collect(Collectors.toMap(s -> s.getEmpApplnEntriesId(), s -> s));
		}

		if(!Utils.isNullOrEmpty(dto.getSubjectCategoryName()) || !Utils.isNullOrEmpty(dto.getSpecialization())) {
			//empApplnSubjSpecializationPrefDTOMap = applicantListTransaction.getEmpApplnSubjSpecializationPrefDBO(empIds).stream().collect(Collectors.groupingBy(b -> b.getEmpApplnEntriesId()));
			List<Tuple>  list = applicantListTransaction.getEmpApplnSubjSpecializationPrefDBO(empIds);
			if(!Utils.isNullOrEmpty(list)) {
				empApplnSubjSpecializationPrefDTOMap = list.stream().collect(Collectors.groupingBy(b -> Integer.parseInt(String.valueOf(b.get("entriesId")))));
			}
		}
		
		if(!Utils.isNullOrEmpty(dto.getPreferredLocation())) {
			List<EmpApplnLocationPrefDTO> list = applicantListTransaction.getApplnLocationPrefDBO(empIds);
			if(!Utils.isNullOrEmpty(list)) {
				empApplnLocationPrefDTOMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpApplnEntriesId()));
			}
		}

		if(!Utils.isNullOrEmpty(dto.getQualificationDetails())) {
			maxQulification =  applicantListTransaction.getEmpMaxQualification(empIds);
			List<ErpQualificationLevelDBO> list = applicantListTransaction.getQualificationDetails();
			if(!Utils.isNullOrEmpty(list)) {
				qualificationLevelMap = list.stream().collect(Collectors.toMap(s -> s.getQualificationLevelDegreeOrder(), s -> s));
			}
			List<Tuple> eduCountsList = applicantListTransaction.getEduCounts(empIds);
			if(!Utils.isNullOrEmpty(eduCountsList)) {
				qualificationsCountsMap = eduCountsList.stream().collect(Collectors.toMap(s -> !Utils.isNullOrEmpty(s.get("displayOrder"))?  Integer.parseInt(String.valueOf(s.get("displayOrder"))):null,
						s -> Integer.parseInt(s.get("maxCount").toString())));
			}
			List<EmpApplnEducationalDetailsDTO> empEducationalDetailsList = applicantListTransaction.getEmpEducationalDetailsDBOSet(empIds);
			if(!Utils.isNullOrEmpty(empEducationalDetailsList)) {
				empApplnEducationalDetailsDTOMap = applicantListTransaction.getEmpEducationalDetailsDBOSet(empIds).stream().collect(Collectors.groupingBy(b -> b.getEmpApplnEntriesId()));
			}
			if(!Utils.isNullOrEmpty(qualificationsCountsMap) && qualificationsCountsMap.containsKey(null)) {
				otherMaxCount = qualificationsCountsMap.get(null);
			}
		}
		if(!Utils.isNullOrEmpty(dto.getEligibilityTest())) {
			List<EmpEligiblityTestDTO> list = applicantListTransaction.empEligibilityTestDBOSet(empIds);
			if(!Utils.isNullOrEmpty(list)) {
				empEligibilityTestDBOMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpId()));
			}

		}
		if(!Utils.isNullOrEmpty(dto.getWorkExperience()) || !Utils.isNullOrEmpty(dto.getCurrentlyWorking())) {
			List<Integer> empWorkExpCount = applicantListTransaction.getWorkExp(empIds); 
			if(!Utils.isNullOrEmpty(empWorkExpCount)) {
				Object p = Collections.max(empWorkExpCount);
				workCount = Integer.parseInt(p.toString());
			}
			List<EmpApplnWorkExperienceDTO> list = applicantListTransaction.getEmpWorkExperienceDBOSet(empIds,true);
			if(!Utils.isNullOrEmpty(list)) {
				currentworkMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpApplnEntriesId()));
				otherworkMap =   list.stream().collect(Collectors.groupingBy(b -> b.getEmpApplnEntriesId()));
			}
		}
		if(!Utils.isNullOrEmpty(dto.getResearchExperience())) {
			List<EmpApplnAddtnlInfoHeadingDBO> list = applicantListTransaction.getApplnAddInfoHeading(dto.getEmployeeCategory());
			if(!Utils.isNullOrEmpty(list)) {
				list.sort(Comparator.comparing(s -> s.getHeadingDisplayOrder()));
				parameterNamesMap = list.stream().collect(Collectors.toMap(s -> s.id, s -> s,(existingValue, newValue) -> existingValue, LinkedHashMap::new));
			}
			List<EmpApplnAddtnlInfoEntriesDTO> data = applicantListTransaction.empApplnAddtnlInfoEntries(empIds);
			
			 if(!Utils.isNullOrEmpty(data)) {
				 data.forEach( details -> {
					 if(!empApplnAddtnlInfoEntriesMap.containsKey(details.getEntriesId())) {
						 Map<Integer,String> parameterDetailsMap = new HashMap<Integer,String>();
						 parameterDetailsMap.put(details.getParameterId(), details.getAddtnlInfoValue());
						 empApplnAddtnlInfoEntriesMap.put(details.getEntriesId(), parameterDetailsMap);
					 } else {
						 Map<Integer,String> parameterDetailsMap = empApplnAddtnlInfoEntriesMap.get(details.getEntriesId());
						 parameterDetailsMap.put(details.getParameterId(), details.getAddtnlInfoValue());
						 empApplnAddtnlInfoEntriesMap.replace(details.getEntriesId(), parameterDetailsMap);
					 }
				 });
			 }
		}
		
		if(!Utils.isNullOrEmpty(dto.getApplicationNo())) {
			rowhead.createCell(headingIndex).setCellValue("Application No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getApplicantName())) {
			rowhead.createCell(headingIndex).setCellValue("Applicant Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getGender())) {
			rowhead.createCell(headingIndex).setCellValue("Gender");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmail())) {
			rowhead.createCell(headingIndex).setCellValue("Personal Mail");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMobile())) {
			rowhead.createCell(headingIndex).setCellValue("Mobile");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getAlternateNo())) {
			rowhead.createCell(headingIndex).setCellValue("Alternate No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDob())) {
			rowhead.createCell(headingIndex).setCellValue("DOB");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getBloodGroup())) {
			rowhead.createCell(headingIndex).setCellValue("Blood Group");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDifferentlyAbled())) {
			rowhead.createCell(headingIndex).setCellValue("Differently Abled");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMaritalStatus())) {
			rowhead.createCell(headingIndex).setCellValue("Marital status");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getReligion())) {
			rowhead.createCell(headingIndex).setCellValue("Religion");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getReservationCategory())) {
			rowhead.createCell(headingIndex).setCellValue("Reservation Category");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getNationality())) {
			rowhead.createCell(headingIndex).setCellValue("Nationality");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getAadharNo())) {
			rowhead.createCell(headingIndex).setCellValue("Aadhar No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPassportNo())) {
			rowhead.createCell(headingIndex).setCellValue("Passport No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSourcingChannel())) {
			rowhead.createCell(headingIndex).setCellValue("Sourcing Channel");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmployeeCategoryName())) {
			rowhead.createCell(headingIndex).setCellValue("Employee Category");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSubjectCategoryName())) {
			rowhead.createCell(headingIndex).setCellValue("Subject/Category");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSpecialization())) {
			rowhead.createCell(headingIndex).setCellValue("Specialization");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPreferredLocation())) {
			rowhead.createCell(headingIndex).setCellValue("Preferred Location");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCurrentAddress1())) {
			rowhead.createCell(headingIndex).setCellValue("Current Address 1");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCurrentAddress2())) {
			rowhead.createCell(headingIndex).setCellValue("Current Address 2");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCurrentAddressCountry())) {
			rowhead.createCell(headingIndex).setCellValue("Current Address Country");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCurrentAddressCity())) {
			rowhead.createCell(headingIndex).setCellValue("Current Address City");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCurrentAddressState())) {
			rowhead.createCell(headingIndex).setCellValue("Current Address State");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCurrentAddressPinCode())) {
			rowhead.createCell(headingIndex).setCellValue("Current Address PIN Code");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPermanentAddress1())) {
			rowhead.createCell(headingIndex).setCellValue("Permanent Address 1");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPermanentAddress2())) {
			rowhead.createCell(headingIndex).setCellValue("Permanent Address 2");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPermanentAddressCountry())) {
			rowhead.createCell(headingIndex).setCellValue("Permanent Address Country");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPermanentAddressCity())) {
			rowhead.createCell(headingIndex).setCellValue("Permanent Address City");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPermanentAddressState())) {
			rowhead.createCell(headingIndex).setCellValue("Permanent Address State");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPermanentAddressPinCode())) {
			rowhead.createCell(headingIndex).setCellValue("Permanent Address PIN Code");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getHighestQualificationLevel())) {
			rowhead.createCell(headingIndex).setCellValue("Highest Qualification Level");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}

		
		if(!Utils.isNullOrEmpty(dto.getQualificationDetails())) {
			for(Integer qualification: qualificationLevelMap.keySet()) {
				if(!Utils.isNullOrEmpty(qualification)  && qualification <= maxQulification && qualificationsCountsMap.containsKey(qualification)) {
					for(int i = 1; i<=qualificationsCountsMap.get(qualification); i++) {
						ErpQualificationLevelDBO erpQualificationLevelDBO = qualificationLevelMap.get(qualification);
						qualificationOrders.add(qualification);

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+ (i > 1 ? i:"") +" Course");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+ (i > 1 ? i:"")+" Specialisation ");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+ (i > 1 ? i:"")+" Year of Completion");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+(i > 1 ? i:"")+" Percentage");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+(i > 1 ? i:"")+" Institution");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+(i > 1 ? i:"")+" University");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+(i > 1 ? i:"")+" Country");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+(i > 1 ? i:"")+" State");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;
					}
				}
			}
			if(!Utils.isNullOrEmpty(qualificationsCountsMap) && qualificationsCountsMap.containsKey(null)) {
				for(int i = 1; i<=qualificationsCountsMap.get(null); i++) {

					rowhead.createCell(headingIndex).setCellValue("Other "+" "+ (i > 1 ? i:"") +" Course");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;

					rowhead.createCell(headingIndex).setCellValue("Other "+" "+ (i > 1 ? i:"")+" Specialisation ");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;

					rowhead.createCell(headingIndex).setCellValue("Other "+" "+ (i > 1 ? i:"")+" Year of Completion");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;

					rowhead.createCell(headingIndex).setCellValue("Other "+" "+(i > 1 ? i:"")+" Percentage");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;

					rowhead.createCell(headingIndex).setCellValue("Other "+" "+(i > 1 ? i:"")+" Institution");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;

					rowhead.createCell(headingIndex).setCellValue("Other "+" "+(i > 1 ? i:"")+" University");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;

					rowhead.createCell(headingIndex).setCellValue("Other "+" "+(i > 1 ? i:"")+" Country");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;

					rowhead.createCell(headingIndex).setCellValue("Other "+" "+(i > 1 ? i:"")+" State");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
				}
			}
			
		}
		if(!Utils.isNullOrEmpty(dto.getEligibilityTest())) {
			rowhead.createCell(headingIndex).setCellValue("Eligibility Test");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCurrentlyWorking())) {
			rowhead.createCell(headingIndex).setCellValue("Currently Working ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}

		if(!Utils.isNullOrEmpty(dto.getWorkExperience())) {
		
			rowhead.createCell(headingIndex).setCellValue("Current Experience Type ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Current Functional Area ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Current Experience From ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Current Experience Till ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Years");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("Months");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Current Designation ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Current Institution ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Part Time/Full Time ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Current Salary");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("Notice Period");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			for(int j=1;j<=workCount;j++) {
				
				rowhead.createCell(headingIndex).setCellValue("Experience Type "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Functional Area "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Experience From "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Experience Till "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Work Experience Years "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
				
				rowhead.createCell(headingIndex).setCellValue("Work Experience Months "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Designation "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Institution "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Part Time/Full Time "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

			}
		}
		if(!Utils.isNullOrEmpty(dto.getTotalFulltimeExperience())) {
			rowhead.createCell(headingIndex).setCellValue("Total Full-time Experience");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTotalParttimeExperience())) {
			rowhead.createCell(headingIndex).setCellValue("Total Part-time Experience");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMajorAchievements())) {
			rowhead.createCell(headingIndex).setCellValue("MajorAchievements");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getExpectedSalarymonth())) {
			rowhead.createCell(headingIndex).setCellValue("Expected Salary/month");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}

		if(!Utils.isNullOrEmpty(dto.getExperienceInChrist())) {
			
			rowhead.createCell(headingIndex).setCellValue("Interviewed At Christ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("Interviewed Department");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("Interviewed Year");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("Interviewed Application No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("Interviewed Subject");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		
		if(!Utils.isNullOrEmpty(dto.getResearchExperience())) {
			rowhead.createCell(headingIndex).setCellValue("Have research experience");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("ORCID ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("VIDWAN ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("Scopus ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			rowhead.createCell(headingIndex).setCellValue("Current Scorpus h- Index");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
			
			
			if(!Utils.isNullOrEmpty(parameterNamesMap)) {
				for (Entry<Integer, EmpApplnAddtnlInfoHeadingDBO> entry : parameterNamesMap.entrySet()) {
					
					Set<EmpApplnAddtnlInfoParameterDBO> parameters = entry.getValue().getEmpApplnAddtnlInfoParameterMap();
					
					for(EmpApplnAddtnlInfoParameterDBO name:parameters) {
						if(name.getRecordStatus() == 'A') {
							rowhead.createCell(headingIndex).setCellValue(name.getEmpApplnAddtnlInfoHeading().getAddtnlInfoHeadingName()+" - "+name.getAddtnlInfoParameterName());
							rowhead.getCell(headingIndex).setCellStyle(style);
							headingIndex++;
							researchParameterIds.add(name.getId());
						}
					}
				}
			}
		}
		
		for(EmpApplnEntriesDTO dbo : dbos) {
			int dataIndexs =  0 ;
			rowhead = sheet.createRow(rowCount);
			
			EmpApplnPersonalDataDTO personalData = null;
			List<EmpApplnLocationPrefDTO> locationPrefList = null;
			List<Tuple> empApplnSubjSpecializationPrefList = null;
			if(!Utils.isNullOrEmpty(empApplnPersonalDataDTOMap) && empApplnPersonalDataDTOMap.containsKey(Integer.parseInt(dbo.getApplicantId()))) {
				personalData = empApplnPersonalDataDTOMap.get(Integer.parseInt(dbo.getApplicantId()));
			}
			if(!Utils.isNullOrEmpty(empApplnLocationPrefDTOMap) && empApplnLocationPrefDTOMap.containsKey(Integer.parseInt(dbo.getApplicantId()))) {
				locationPrefList = empApplnLocationPrefDTOMap.get(Integer.parseInt(dbo.getApplicantId()));
			}
			if(!Utils.isNullOrEmpty(empApplnSubjSpecializationPrefDTOMap) && empApplnSubjSpecializationPrefDTOMap.containsKey(Integer.parseInt(dbo.getApplicantId()))) {
				empApplnSubjSpecializationPrefList = empApplnSubjSpecializationPrefDTOMap.get(Integer.parseInt(dbo.getApplicantId()));
			}
			
			if(!Utils.isNullOrEmpty(dto.getApplicationNo())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicantNumber())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicantNumber());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getApplicantName())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicantName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getGender())) {
				if(!Utils.isNullOrEmpty(dbo.getGender())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getGender().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmail())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicantEmail())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicantEmail());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMobile())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicantMobile())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicantMobile());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getAlternateNo())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getAlternateNo())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getAlternateNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDob())) {
				if(!Utils.isNullOrEmpty(dbo.getDob())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getDob());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getBloodGroup())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getBloodGroupId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getBloodGroupId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDifferentlyAbled())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getDifferentlyAbledDetails())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getDifferentlyAbledDetails());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMaritalStatus())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getMaritalStatusId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getMaritalStatusId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getReligion())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getReligionId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getReligionId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getReservationCategory())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getReservationCategoryId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getReservationCategoryId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getNationality())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getNationalityId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getNationalityId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getAadharNo())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getAadharNo())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getAadharNo());
				}
				dataIndexs++;
			}	
			if(!Utils.isNullOrEmpty(dto.getPassportNo())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getPassportNo())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getPassportNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSourcingChannel())) {
				if(!Utils.isNullOrEmpty(dbo.getVacancyInformation())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getVacancyInformation());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmployeeCategoryName())) {
				if(!Utils.isNullOrEmpty(dbo.getCategory())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getCategory().getText());
				}
				dataIndexs++;
			}
//			if(!Utils.isNullOrEmpty(dto.getSubjectCategoryName())) {
//				if(!Utils.isNullOrEmpty(dbo.getSubjectCategoryName())) {
//					rowhead.createCell(dataIndexs).setCellValue(dbo.getSubjectCategoryName());
//				}
//				dataIndexs++;
//			}
			
			if(!Utils.isNullOrEmpty(dto.getSubjectCategoryName())) {
				if(!Utils.isNullOrEmpty(empApplnSubjSpecializationPrefList)) {
					for(Tuple loc : empApplnSubjSpecializationPrefList) {
						if(!Utils.isNullOrEmpty(loc.get("categoryName"))) {
							if(rowhead.getCell(dataIndexs) == null) {
								String cellData = String.valueOf(loc.get("categoryName"));
								rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(cellData)?cellData:"");
							} else {
								String existingValue = rowhead.getCell(dataIndexs).getStringCellValue();
								existingValue += ","+String.valueOf(loc.get("categoryName"));
								rowhead.getCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(existingValue)?existingValue:"");
							}
						}
					}
				}
				dataIndexs++;
			}
			
//			if(!Utils.isNullOrEmpty(dto.getSpecialization())) {
//				if(!Utils.isNullOrEmpty(dbo.getSubjectCategorySpecializationName())) {
//					rowhead.createCell(dataIndexs).setCellValue(dbo.getSubjectCategorySpecializationName());
//				}
//				dataIndexs++;
//			}
			
			if(!Utils.isNullOrEmpty(dto.getSpecialization())) {
				if(!Utils.isNullOrEmpty(empApplnSubjSpecializationPrefList)) {
					for(Tuple loc : empApplnSubjSpecializationPrefList) {
						if(!Utils.isNullOrEmpty(loc.get("specialName"))) {
							if(rowhead.getCell(dataIndexs) == null) {
								String cellData = String.valueOf(loc.get("specialName"));
								rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(cellData)?cellData:"");
							} else {
								String existingValue = rowhead.getCell(dataIndexs).getStringCellValue();
								existingValue += ","+String.valueOf(loc.get("specialName"));
								rowhead.getCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(existingValue)?existingValue:"");
							}
						}
					}
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getPreferredLocation())) {
				if(!Utils.isNullOrEmpty(locationPrefList)) {
					for(EmpApplnLocationPrefDTO loc : locationPrefList) {
						if(rowhead.getCell(dataIndexs) == null) {
							String cellData = loc.getLocationName();
							rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(cellData)?cellData:"");
						} else {
							String existingValue = rowhead.getCell(dataIndexs).getStringCellValue();
							existingValue += ","+loc.getLocationName();
							rowhead.getCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(existingValue)?existingValue:"");
						}
					}
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddress1())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getCurrentAddressLine1())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getCurrentAddressLine1());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddress2())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getCurrentAddressLine2())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getCurrentAddressLine2());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressCountry())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getCurrentCountryId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getCurrentCountryId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressState())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getCurrentStateId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getCurrentStateId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressCity())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getCurrentCityId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getCurrentCityId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressPinCode())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getCurrentPincode())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getCurrentPincode());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddress1())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getPermanentAddressLine1())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getPermanentAddressLine1());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddress2())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getPermanentAddressLine2())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getPermanentAddressLine2());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressCountry())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getPermanentCountryId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getPermanentCountryId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressState())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getPermanentStateId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getPermanentStateId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressCity())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getPermanentCityId())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getPermanentCityId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressPinCode())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getPermanentPincode())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getPermanentPincode());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getHighestQualificationLevel())) {
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getHighestQualificationLevel())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getHighestQualificationLevel().getLabel());
				}
				dataIndexs++;
			}
			//
			
			if(!Utils.isNullOrEmpty(dto.getQualificationDetails())) {
				Map<Integer,List<EmpApplnEducationalDetailsDTO>> empEducationalDetailsDBOMap = new HashMap<Integer,List<EmpApplnEducationalDetailsDTO>>();
				List<EmpApplnEducationalDetailsDTO> otherEmpEducationalDetailsDBOMap = new ArrayList<EmpApplnEducationalDetailsDTO>();
				if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTOMap) && empApplnEducationalDetailsDTOMap.containsKey(Integer.parseInt(dbo.getApplicantId()))) {	

					empApplnEducationalDetailsDTOMap.get(Integer.parseInt(dbo.getApplicantId())).forEach(data -> {

						if(!Utils.isNullOrEmpty(data.getQualificationLevelId()) ) {
							if(!empEducationalDetailsDBOMap.containsKey(Integer.parseInt(data.getQualificationLevelId()))) {
								List<EmpApplnEducationalDetailsDTO> list = new ArrayList<EmpApplnEducationalDetailsDTO>();
								list.add(data);
								empEducationalDetailsDBOMap.put(Integer.parseInt(data.getQualificationLevelId()),list );
							} else {
								List<EmpApplnEducationalDetailsDTO> list = empEducationalDetailsDBOMap.get(Integer.parseInt(data.getQualificationLevelId()));
								list.add(data);
								empEducationalDetailsDBOMap.replace(Integer.parseInt(data.getQualificationLevelId()), list);	
							}
						} else {
							otherEmpEducationalDetailsDBOMap.add(data);
						}
					});
				}

				for( Integer order  :qualificationOrders) {
					int count = 0 ;
					int maxCount = qualificationsCountsMap.get(order);
					if(empEducationalDetailsDBOMap.containsKey(order)) {

						List<EmpApplnEducationalDetailsDTO> empEducationalDetailsDBOList = empEducationalDetailsDBOMap.get(order);
						for (EmpApplnEducationalDetailsDTO empEducationalDetailsDBO:empEducationalDetailsDBOList) {


							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getCourse())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getCourse());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getSpecialization())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getSpecialization());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getYearOfCompletion())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getYearOfCompletion());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getGradeOrPercentage())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getGradeOrPercentage());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getInstitute())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getInstitute());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getBoardOrUniversity())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getBoardOrUniversity());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getCountryId())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getCountryId());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getStateId())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getStateId());
							}
							dataIndexs++;
							count++;
						}
					} else {
						dataIndexs += 8;
						count++;
					}
					while(count < maxCount) {
						dataIndexs += 8;
						count++;
					}
				}
				
					int otherCount = 0 ;
					int maxCount = otherMaxCount;
					if(!Utils.isNullOrEmpty(otherEmpEducationalDetailsDBOMap)) {
						for (EmpApplnEducationalDetailsDTO empEducationalDetailsDBO:otherEmpEducationalDetailsDBOMap) {

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getCourse())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getCourse());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getSpecialization())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getSpecialization());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getYearOfCompletion())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getYearOfCompletion());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getGradeOrPercentage())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getGradeOrPercentage());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getInstitute())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getInstitute());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getBoardOrUniversity())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getBoardOrUniversity());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getCountryId())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getCountryId());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getStateId())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getStateId());
							}
							dataIndexs++;
							otherCount++;
						}
					} else {
						if(!Utils.isNullOrEmpty(maxCount)) {
							dataIndexs += 8;
							otherCount++;
						}
	
					}
					while(otherCount < maxCount) {
						dataIndexs += 8;
						otherCount++;
					}
			}
			//
			if(!Utils.isNullOrEmpty(dto.getEligibilityTest())) {
				if(!Utils.isNullOrEmpty(empEligibilityTestDBOMap) && empEligibilityTestDBOMap.containsKey(Integer.parseInt(dbo.getApplicantId()))) {	
					List<EmpEligiblityTestDTO> Eligibilitytest = empEligibilityTestDBOMap.get(Integer.parseInt(dbo.getApplicantId()));
					for(EmpEligiblityTestDTO test :Eligibilitytest) {
						if(rowhead.getCell(dataIndexs) == null) {
							rowhead.createCell(dataIndexs).setCellValue(test.getEligibilityExamName()
									+  (!Utils.isNullOrEmpty(test.getTestYear()) ? " ("+ test.getTestYear()+" )"+ "\n":"")
									);
						} else {
							String existingValue = rowhead.getCell(dataIndexs).getStringCellValue();
							existingValue += "\n"+test.getEligibilityExamName()
							+ (!Utils.isNullOrEmpty(test.getTestYear()) ? " ("+ test.getTestYear()+" )"+ "\n":"");
							rowhead.getCell(dataIndexs).setCellValue(existingValue);
						}
					}
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getCurrentlyWorking())) {
				if(!Utils.isNullOrEmpty(currentworkMap) &&  currentworkMap.containsKey(Integer.parseInt(dbo.getApplicantId())) && !Utils.isNullOrEmpty(currentworkMap.get(Integer.parseInt(dbo.getApplicantId())))) {
					rowhead.createCell(dataIndexs).setCellValue("Yes");
				}
				else {
					rowhead.createCell(dataIndexs).setCellValue("No");
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getWorkExperience())) {
				if(!Utils.isNullOrEmpty(currentworkMap) &&  currentworkMap.containsKey(Integer.parseInt(dbo.getApplicantId())) && !Utils.isNullOrEmpty(currentworkMap.get(Integer.parseInt(dbo.getApplicantId())))) {	
					
					EmpApplnWorkExperienceDTO data = currentworkMap.get(Integer.parseInt(dbo.getApplicantId())).get(0);
					
					
					if(!Utils.isNullOrEmpty(data.getWorkExperienceTypeId())) {
						rowhead.createCell(dataIndexs).setCellValue(data.getWorkExperienceTypeId());
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getFunctionalAreaId())) {
						rowhead.createCell(dataIndexs).setCellValue(data.getFunctionalAreaId());
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getFromDate())) {
						rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(data.getFromDate()));
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getToDate())) {
						rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(data.getToDate()));
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getYears())) {
						rowhead.createCell(dataIndexs).setCellValue(data.getYears());
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getMonths())) {
						rowhead.createCell(dataIndexs).setCellValue(data.getMonths());
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getDesignation())) {
						rowhead.createCell(dataIndexs).setCellValue(data.getDesignation());
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getInstitution())) {
						rowhead.createCell(dataIndexs).setCellValue(data.getInstitution());
					}
					dataIndexs++;
					
					if(data.getIsPartTime()) {
						rowhead.createCell(dataIndexs).setCellValue("Part-Time");
					} else {
						rowhead.createCell(dataIndexs).setCellValue("Full-Time");
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getCurrentSalary())) {
						rowhead.createCell(dataIndexs).setCellValue(data.getCurrentSalary());
					}
					dataIndexs++;
					
					if(!Utils.isNullOrEmpty(data.getNoticePeriod())) {
						rowhead.createCell(dataIndexs).setCellValue(data.getNoticePeriod());
					}
					dataIndexs++;
				} else {
					dataIndexs += 11;
				}
				
				
				int count = 0;
				if(!Utils.isNullOrEmpty(otherworkMap) &&  otherworkMap.containsKey(Integer.parseInt(dbo.getApplicantId())) && !Utils.isNullOrEmpty(otherworkMap.get(Integer.parseInt(dbo.getApplicantId())))) {	

					for(EmpApplnWorkExperienceDTO work:  otherworkMap.get(Integer.parseInt(dbo.getApplicantId()))) {
						
						if(!work.getIsCurrentExperience()) {
							if(!Utils.isNullOrEmpty(work.getWorkExperienceTypeId())) {
								rowhead.createCell(dataIndexs).setCellValue(work.getWorkExperienceTypeId());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(work.getFunctionalAreaId())) {
								rowhead.createCell(dataIndexs).setCellValue(work.getFunctionalAreaId());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(work.getFromDate())) {
								rowhead.createCell(dataIndexs).setCellValue(work.getFromDate());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(work.getToDate())) {
								rowhead.createCell(dataIndexs).setCellValue(work.getToDate());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(work.getYears())) {
								rowhead.createCell(dataIndexs).setCellValue(work.getYears());
							}
							dataIndexs++;
							
							if(!Utils.isNullOrEmpty(work.getMonths())) {
								rowhead.createCell(dataIndexs).setCellValue(work.getMonths());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(work.getDesignation())) {
								rowhead.createCell(dataIndexs).setCellValue(work.getDesignation());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(work.getInstitution())) {
								rowhead.createCell(dataIndexs).setCellValue(work.getInstitution());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(work.getIsPartTime())) {
								rowhead.createCell(dataIndexs).setCellValue( work.getIsPartTime() ? "Part Time":"Full Time");
							}
							dataIndexs++;

							count++;
						}

			
					}
				}
				while(count < workCount) {
					dataIndexs += 9;
					count++;
				}
			}
			
			
			if(!Utils.isNullOrEmpty(dto.getTotalFulltimeExperience())) {
				AtomicInteger year = new AtomicInteger(0);
				AtomicInteger month = new AtomicInteger(0);

				List<EmpApplnWorkExperienceDTO> empWorkExpSet = null;
				if(!Utils.isNullOrEmpty(otherworkMap) && otherworkMap.containsKey(Integer.parseInt(dbo.getApplicantId()))) {
					empWorkExpSet = otherworkMap.get(Integer.parseInt(dbo.getApplicantId())).stream().filter(s ->  s.getIsPartTime() == null || !s.getIsPartTime())
							.collect(Collectors.toList());
				}

				if(!Utils.isNullOrEmpty(empWorkExpSet)) {
					empWorkExpSet.forEach( work -> {
						if(!Utils.isNullOrEmpty(work.getYears())) {
							year.addAndGet(Integer.parseInt(work.getYears()));
						}
						if(!Utils.isNullOrEmpty(work.getMonths())) {
							month.addAndGet(Integer.parseInt(work.getMonths()));
						}						
					});
					year.addAndGet(month.get() / 12);
					month.set(month.get() % 12);
					rowhead.createCell(dataIndexs).setCellValue(year+" years "+ month+" months");
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getTotalParttimeExperience())) {
				AtomicInteger year = new AtomicInteger(0);
				AtomicInteger month = new AtomicInteger(0);
				List<EmpApplnWorkExperienceDTO> empWorkExpSet = null;
				if(!Utils.isNullOrEmpty(otherworkMap) && otherworkMap.containsKey(Integer.parseInt(dbo.getApplicantId()))) {
					empWorkExpSet = otherworkMap.get(Integer.parseInt(dbo.getApplicantId())).stream().filter(s -> !Utils.isNullOrEmpty(s.getIsPartTime()) && s.getIsPartTime())
							.collect(Collectors.toList());
				}

				if(!Utils.isNullOrEmpty(empWorkExpSet)) {
					empWorkExpSet.forEach( work -> {
						if(!Utils.isNullOrEmpty(work.getYears())) {
							year.addAndGet(Integer.parseInt(work.getYears()));
						}
						if(!Utils.isNullOrEmpty(work.getMonths())) {
							month.addAndGet(Integer.parseInt(work.getMonths()));
						}						
					});
					year.addAndGet(month.get() / 12);
					month.set(month.get() % 12);
					rowhead.createCell(dataIndexs).setCellValue(year+" years "+ month+" months");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMajorAchievements())) {
				if(!Utils.isNullOrEmpty(dbo.getMajorAchievements())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getMajorAchievements());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getExpectedSalarymonth())) {
				if(!Utils.isNullOrEmpty(dbo.getExpectedSalary())) {
					rowhead.createCell(dataIndexs).setCellValue(String.valueOf(dbo.getExpectedSalary()));
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getExperienceInChrist())) {
				
				if(!Utils.isNullOrEmpty(dbo.getIsInterviewedBefore())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getIsInterviewedBefore()?"Yes":"No");
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(dbo.getInterviewedBeforeDepartment())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getInterviewedBeforeDepartment());
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(dbo.getInterviewedBeforeYear())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getInterviewedBeforeYear());
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(dbo.getInterviewedBeforeApplicationNo())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getInterviewedBeforeApplicationNo());
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(dbo.getInterviewedBeforeSubject())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getInterviewedBeforeSubject());
				}
				dataIndexs++;
				
			}
			
			if(!Utils.isNullOrEmpty(dto.getResearchExperience())) {
				if(!Utils.isNullOrEmpty(dbo.getIsResearchExperiencePresent())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getIsResearchExperiencePresent()?"Yes":"No");
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getOrcidNo())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getOrcidNo());
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getVidwanNo())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getVidwanNo());
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getScopusNo())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getScopusNo());
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(personalData) && !Utils.isNullOrEmpty(personalData.getHIndexNo())) {
					rowhead.createCell(dataIndexs).setCellValue(personalData.getHIndexNo());
				}
				dataIndexs++;
				
				if(!Utils.isNullOrEmpty(empApplnAddtnlInfoEntriesMap) && empApplnAddtnlInfoEntriesMap.containsKey(Integer.parseInt(dbo.getApplicantId()))) {
					Map<Integer, String> parameterDetailsMap = empApplnAddtnlInfoEntriesMap.get(Integer.parseInt(dbo.getApplicantId()));
					if(!Utils.isNullOrEmpty(researchParameterIds)) {
						for(Integer paramId :researchParameterIds) {
							if(!Utils.isNullOrEmpty(parameterDetailsMap)&& parameterDetailsMap.containsKey(paramId)) {
								rowhead.createCell(dataIndexs).setCellValue(parameterDetailsMap.get(paramId));
							} 
							dataIndexs++;
						}
					}
				}
			}
			
			rowCount++;
		}
		
		
		ResponseEntity<InputStreamResource> outputResource = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
			InputStreamResource file = new InputStreamResource(inputStream);
			outputResource = ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=")
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
					.body(file);

			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return Mono.just(outputResource);

		
	}
	
	

}
