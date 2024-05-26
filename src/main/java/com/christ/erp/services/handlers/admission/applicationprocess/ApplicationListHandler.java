package com.christ.erp.services.handlers.admission.applicationprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
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
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dto.admission.applicationprocess.ApplicationListDTO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentApplnPreferenceDTO;
import com.christ.erp.services.dto.student.common.StudentApplnPrerequisiteDTO;
import com.christ.erp.services.dto.student.common.StudentApplnSelectionProcessDatesDTO;
import com.christ.erp.services.dto.student.common.StudentEducationalDetailsDTO;
import com.christ.erp.services.dto.student.common.StudentExtraCurricularDetailsDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddressDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddtnlDTO;
import com.christ.erp.services.dto.student.common.StudentWorkExperienceDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.admission.applicationprocess.ApplicationListTransaction;
import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")
@Service
public class ApplicationListHandler {

	@Autowired
	private ApplicationListTransaction applicationListTransaction;
	
	@Autowired
	AWSS3FileStorageService aWSS3FileStorageService;
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> privilegeEnabled() {
		ApiResult result = new ApiResult();
		result.setSuccess(true);
		return Mono.just(result);
	}

	public Mono<List<ApplicationListDTO>> getGridData(Mono<ApplicationListDTO> data, String userId) {
		return data.handle((applicationListDTO,synchronousSink) -> {
			List<Tuple> list = null;
			Integer cancelId = null;
			Integer statusId = null ;
			String Submited = "ADM_APPLN_SUBMITTED";
			String Selected = "ADM_APPLN_SELECTED";
			String NotSelected = "ADM_APPLN_NOT_SELECTED";
			String Waitlisted = "ADM_APPLN_WAITLISTED";
			String Admitted = "ADM_APPLN_ADMITTED";
			String WithdrawnOffer = "ADM_APPLN_OFFER_WITHDRAWL";
			String Cancelled  = "STUDENT_CANCELLED";
			String Drafted = "ADM_APPLN_DRAFT";
			Map<String,Integer> statusData = applicationListTransaction.getStatusDetails().stream().collect(Collectors.toMap(s -> s.getProcessCode(), s -> s.getId()));
			if(!Utils.isNullOrEmpty(applicationListDTO.getStatus())) {
				if(applicationListDTO.getStatus().replace(" ", "").equals("Submited")) {
					statusId = statusData.get(Submited);
				} else if(applicationListDTO.getStatus().replace(" ", "").equals("Selected")) {
					statusId = statusData.get(Selected);
				} else if(applicationListDTO.getStatus().replace(" ", "").equals("NotSelected")) {
					statusId = statusData.get(NotSelected);
				} else if(applicationListDTO.getStatus().replace(" ", "").equals("Waitlisted")) {
					statusId = statusData.get(Waitlisted);
				} else if(applicationListDTO.getStatus().replace(" ", "").equals("Admitted")) {
					statusId = statusData.get(Admitted);
				} else if(applicationListDTO.getStatus().replace(" ", "").equals("WithdrawnOffer")) {
					statusId = statusData.get(WithdrawnOffer);
				}else if(applicationListDTO.getStatus().replace(" ", "").equals("Draft")){
					statusId = statusData.get(Drafted);
				}else if(applicationListDTO.getStatus().replace(" ", "").equals("Cancelled")) {
					ErpStatusDBO erpStatus = applicationListTransaction.getErpStatus(Cancelled);
					if(!Utils.isNullOrEmpty(erpStatus)){
						cancelId = erpStatus.getId();
					}
				}
			}
			list = applicationListTransaction.getStudentApplicationData1(applicationListDTO,statusId,cancelId);
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

	public Mono<List<ApplicationListDTO>> convertDBOToDTO( List<Tuple> dbos, String userId) {
		List<ApplicationListDTO> dtoData = new ArrayList<ApplicationListDTO>();
		dbos.forEach(dbo -> {
			if(!Utils.isNullOrEmpty(dbo)) {
				ApplicationListDTO dto1 = new ApplicationListDTO();
				dto1.setStudentEntries(Integer.parseInt(String.valueOf(dbo.get("id"))));
				if(!Utils.isNullOrEmpty(dbo.get("applicationNo"))) {
					dto1.setApplicationNo(String.valueOf(dbo.get("applicationNo")));
				}
				if(!Utils.isNullOrEmpty(dbo.get("name"))) {
					dto1.setApplicantName(String.valueOf(dbo.get("name")));
				}
				if(!Utils.isNullOrEmpty(dbo.get("dob"))) {
					dto1.setDateofBirth(Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(String.valueOf(dbo.get("dob")))));
				}
				if(!Utils.isNullOrEmpty(dbo.get("progName"))) {
					dto1.setProgramme(String.valueOf(dbo.get("progName")));
				}
				if(!Utils.isNullOrEmpty(dbo.get("campusName"))){
					dto1.setCampus(String.valueOf(dbo.get("campusName")));
				}
				else if(!Utils.isNullOrEmpty(dbo.get("locationName"))) {
					dto1.setCampus(String.valueOf(dbo.get("locationName")));
				}
				//dto1.setCampus(!Utils.isNullOrEmpty(dbo.get("campusName")) ? String.valueOf(dbo.get("campusName")) : String.valueOf(dbo.get("locationName")));
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

	public Mono<ResponseEntity<InputStreamResource>>  getStudentApplicationData(Mono<ApplicationListDTO> dto, String userId) {	
		List<ApplicationListDTO> dtos = new ArrayList<ApplicationListDTO>();
		return dto.handle((applicationListDTO,synchronousSink) -> {
			ApplicationListDTO applistDTO =  applicationListDTO;
			dtos.add(applistDTO);		
			List<StudentApplnEntriesDTO> list1 = applicationListTransaction.getStudentAppEntries(applistDTO.getStudentEntriesIds());
			if(Utils.isNullOrEmpty(list1)) {
				synchronousSink.error(new NotFoundException(null));
			} else {
				synchronousSink.next(list1);
			}
		}).cast(ArrayList.class)
				.map(data -> convertDBOToExcel(data,dtos.get(0),userId))
				.flatMap( s -> {
					return s ;
				});
	}	

	public Mono<ResponseEntity<InputStreamResource>> convertDBOToExcel(List<StudentApplnEntriesDTO> dbos,ApplicationListDTO dto,String userId) {
		List<Integer> studEntriesIds = new ArrayList<Integer>();
		dbos.forEach(dbo -> {
			studEntriesIds.add(Integer.parseInt(dbo.getStudentApplnEntriesId()));
		});
		int preferencesCount = 0;
		int educationalCount = 0;
		int sportsCount = 0;
		Integer workcount = 0;
		Map<Integer,AdmQualificationListDBO> qualificationMap = new HashMap<Integer, AdmQualificationListDBO>();
		List<AdmQualificationListDBO> dataList = new ArrayList<AdmQualificationListDBO>();
		AdmQualificationListDBO processOrder = null;
		Integer processOrder1 = 0;
		Map<Integer, AdmQualificationListDBO> qualificationOrder = new HashMap<Integer, AdmQualificationListDBO>();
		Map<Integer, String> sportsDetails = new HashMap<Integer, String>();
		List<Integer> studentPersonalDataAddtnlIds = new ArrayList<Integer>();
		List<Integer> studentPersonalDataAddressIds = new ArrayList<Integer>();
		Map<Integer,List<StudentApplnPreferenceDTO>> studentApplnPreferenceMap = new HashMap<Integer, List<StudentApplnPreferenceDTO>>();
		Map<Integer,List<StudentApplnSelectionProcessDatesDTO>> studentApplnSelectionProcessDatesMap = new HashMap<Integer, List<StudentApplnSelectionProcessDatesDTO>>();
		Map<Integer,List<StudentEducationalDetailsDTO>> studentEducationalDetailsMap = new HashMap<Integer, List<StudentEducationalDetailsDTO>>();
		Map<Integer,StudentPersonalDataAddtnlDTO> studentPersonalDataAddtnlMap = new HashMap<Integer, StudentPersonalDataAddtnlDTO>();
		Map<Integer,StudentApplnPrerequisiteDTO> studentApplnPrerequisiteMap = new HashMap<Integer, StudentApplnPrerequisiteDTO>();
		Map<Integer,Integer> personalDataAddtnlIdsMap = new HashMap<Integer, Integer>();
		Map<Integer,List<StudentExtraCurricularDetailsDTO>> studentExtraCurricularDetailsMap = new HashMap<Integer, List<StudentExtraCurricularDetailsDTO>>();
		Map<Integer,List<StudentWorkExperienceDTO>> studentWorkExperienceMap = new HashMap<Integer, List<StudentWorkExperienceDTO>>();
		Set<Integer> studentSportsIds = new HashSet<>();
		Map<Integer,StudentPersonalDataAddressDTO> studentPersonalDataAddressMap = new HashMap<Integer, StudentPersonalDataAddressDTO>();
		Map<Integer,Integer> studentPersonalDataAddressIdsMap = new HashMap<Integer, Integer>();
		dbos.forEach(entries -> {
			
			//studentPersonalDataAddtnl
			personalDataAddtnlIdsMap.put(Integer.parseInt(entries.getStudentApplnEntriesId()), entries.getStudentPersonalDataAddtnlId());
			studentPersonalDataAddtnlIds.add(entries.getStudentPersonalDataAddtnlId());
			//studentPersonalDataAddress
			studentPersonalDataAddressIdsMap.put(Integer.parseInt(entries.getStudentApplnEntriesId()), entries.getStudentPersonalDataAddressId());
			studentPersonalDataAddressIds.add(entries.getStudentPersonalDataAddressId());
			
		});
		
		
		List<StudentApplnSelectionProcessDatesDTO> studentApplnSelectionProcessDatesList = applicationListTransaction.getStudentApplnSelectionProcessDatesDTO(studEntriesIds);
		if(!Utils.isNullOrEmpty(studentApplnSelectionProcessDatesList)) {
			studentApplnSelectionProcessDatesMap = studentApplnSelectionProcessDatesList.stream().collect(Collectors.groupingBy(b -> b.getStudentApplnEntriesId()));
		}
		List<StudentEducationalDetailsDTO> StudentEducationalDetails = applicationListTransaction.getStudentEducationalDetailsDTO(studEntriesIds);
		if(!Utils.isNullOrEmpty(StudentEducationalDetails)) {
			studentEducationalDetailsMap = StudentEducationalDetails.stream().collect(Collectors.groupingBy(b -> b.getStudentApplnEntriesDTO().getId()));
		}
		
		List<StudentPersonalDataAddtnlDTO> StudentPersonalDataAddtnlList = applicationListTransaction.getStudentPersonalDataAddtnl(studentPersonalDataAddtnlIds);
		if(!Utils.isNullOrEmpty(StudentPersonalDataAddtnlList)) {
			studentPersonalDataAddtnlMap = StudentPersonalDataAddtnlList.stream().collect(Collectors.toMap(s -> s.getId(),  s -> s));
		}
		List<StudentPersonalDataAddressDTO> StudentPersonalDataAddressList = applicationListTransaction.getStudentPersonalDataAddress(studentPersonalDataAddressIds);
		if(!Utils.isNullOrEmpty(StudentPersonalDataAddressList)) {
			studentPersonalDataAddressMap = StudentPersonalDataAddressList.stream().collect(Collectors.toMap(s -> s.getStudentPersonalDataAddressId(), s -> s));
		}
		
		List<StudentApplnPrerequisiteDTO> studentApplnPrerequisite = applicationListTransaction.getStudentApplnPrerequisiteDTO(studEntriesIds);
		if(!Utils.isNullOrEmpty(studentApplnPrerequisite)) {
			studentApplnPrerequisiteMap = studentApplnPrerequisite.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.getStudentApplnEntriesDTO().getValue()), s -> s));
		}
		List<StudentExtraCurricularDetailsDTO> studentExtraCurricularDetails = applicationListTransaction.getStudentExtraCurricularDetailsDTO(studEntriesIds);
		studentExtraCurricularDetails.forEach( stud -> {
			if(!Utils.isNullOrEmpty(stud.getErpSports())){
				studentSportsIds.add(Integer.parseInt(stud.getErpSports().getValue()));
			}
		});
		if(!Utils.isNullOrEmpty(studentExtraCurricularDetails)) {
			studentExtraCurricularDetailsMap = studentExtraCurricularDetails.stream().collect(Collectors.groupingBy(b -> b.getStudentApplnEntriesId()));
		}
		List<StudentWorkExperienceDTO> StudentWorkExperience = applicationListTransaction.getStudentWorkExperienceDTO(studEntriesIds);
		if(!Utils.isNullOrEmpty(StudentWorkExperience)) {
			studentWorkExperienceMap = StudentWorkExperience.stream().collect(Collectors.groupingBy(b -> b.getStudentApplnEntries().getId())); 
		}
		List<StudentApplnPreferenceDTO> StudentApplnPreferenceList = applicationListTransaction.getStudentApplnPreferenceDTO(studEntriesIds);
		if(!Utils.isNullOrEmpty(StudentApplnPreferenceList)) {
			studentApplnPreferenceMap = StudentApplnPreferenceList.stream().collect(Collectors.groupingBy(b -> b.getStudentApplnEntriesId()));
		}
		if(!Utils.isNullOrEmpty(dto.getPreferences())) {
			preferencesCount = applicationListTransaction.getPreferences(studEntriesIds);
		}
		
		
		if(!Utils.isNullOrEmpty(dto.getEducationDetails())) {
			Tuple value = applicationListTransaction.getEducationalCount(studEntriesIds);
//			educationalCount = !Utils.isNullOrEmpty(value.get(0))? Integer.parseInt(String.valueOf(value.get(0))):0;
			dataList = applicationListTransaction.getQualificationDetails();
			qualificationMap = dataList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
//			processOrder = qualificationMap.get(educationalCount);
			processOrder1 = Integer.parseInt(String.valueOf(value.get(0)));
			qualificationOrder = dataList.stream().filter(a -> a.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getQualificationOrder(), s -> s));
		}
		if(!Utils.isNullOrEmpty(dto.getExtraCurricularDetail())) {
			Tuple value = applicationListTransaction.getSports(studEntriesIds);
			sportsCount = !Utils.isNullOrEmpty(value.get(0))? Integer.parseInt(String.valueOf(value.get(0))):0;
			sportsDetails = applicationListTransaction.getSportsDetails().stream().collect(Collectors.toMap(s -> s.getId(), s -> s.getSportsName()));
		}
		if(!Utils.isNullOrEmpty(dto.getWorkExperienceDetails())) {
			List<Integer> workdata = applicationListTransaction.getWorkExp(studEntriesIds);
			if(!Utils.isNullOrEmpty(workdata)) {
				Object p = Collections.max(workdata);
				workcount = Integer.parseInt(p.toString());
			}
			
		}
		XSSFWorkbook workbook = new XSSFWorkbook();
		int rowCount = 0;
		XSSFSheet sheet = workbook.createSheet("ApplicationListSheet");
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		style.setFont(font); 
		XSSFRow rowhead = sheet.createRow((short)rowCount++);
		int headingIndex = 0;
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
		if(!Utils.isNullOrEmpty(dto.getDateofBirth())) {
			rowhead.createCell(headingIndex).setCellValue("Date of Birth");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}	
		if(!Utils.isNullOrEmpty(dto.getGender())) {
			rowhead.createCell(headingIndex).setCellValue("Gender");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMobileNo())) {
			rowhead.createCell(headingIndex).setCellValue("Mobile No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmailID())) {
			rowhead.createCell(headingIndex).setCellValue("Email ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSubmissionDate())) {
			rowhead.createCell(headingIndex).setCellValue("Submission Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getProgramme())) {
			rowhead.createCell(headingIndex).setCellValue("Programme");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getAdmissionYear())) {
			rowhead.createCell(headingIndex).setCellValue("Admission Year");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getIntakeBatch())) {
			rowhead.createCell(headingIndex).setCellValue("Intake Batch");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getAdmissionType())) {
			rowhead.createCell(headingIndex).setCellValue("Admission Type");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCampus())) {
			rowhead.createCell(headingIndex).setCellValue("Campus");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPreferences())) {
			for(int j=1;j<=preferencesCount;j++) {
				rowhead.createCell(headingIndex).setCellValue("Preferences"+" "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
			}
		}
		if(!Utils.isNullOrEmpty(dto.getSpecialisation())) {
			rowhead.createCell(headingIndex).setCellValue("Specialisation");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessRound1())) {
			rowhead.createCell(headingIndex).setCellValue("Selection Process Round 1");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessDateRound1())) {
			rowhead.createCell(headingIndex).setCellValue("Selection Process Date Round 1");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessTimeRound1())) {
			rowhead.createCell(headingIndex).setCellValue("Selection Process Time Round 1");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessVenueRound1())) {
			rowhead.createCell(headingIndex).setCellValue("Selection Process Venue Round 1");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessRound2())) {
			rowhead.createCell(headingIndex).setCellValue("Selection Process Round 2");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessDateRound2())) {
			rowhead.createCell(headingIndex).setCellValue("Selection Process Date Round 2");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessTimeRound2())) {
			rowhead.createCell(headingIndex).setCellValue("Selection Process Time Round 2");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessVenueRound2())) {
			rowhead.createCell(headingIndex).setCellValue("Selection Process Venue Round 2");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}

		if(!Utils.isNullOrEmpty(dto.getEducationDetails())) {
			for(int j=1;j<=processOrder1;j++) {
				if(qualificationOrder.containsKey(j)) {
					AdmQualificationListDBO eDetials = qualificationOrder.get(j);
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+" Exam ");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+" Board/University");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+" Country");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+" State");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+"  Insitution");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+" Year & month of pass");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+" Max marks");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+" Marks Obtained");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
					rowhead.createCell(headingIndex).setCellValue(eDetials.getQualificationName()+" "+" %");
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
				}
			}
		}
		if(!Utils.isNullOrEmpty(dto.getResidentCategory())) {
			rowhead.createCell(headingIndex).setCellValue("Resident Category");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
//		if(!Utils.isNullOrEmpty(dto.getAppliedBatch())) {
//			rowhead.createCell(headingIndex).setCellValue("Applied Batch");
//			rowhead.getCell(headingIndex).setCellStyle(style);
//			headingIndex++;
//		}
		if(!Utils.isNullOrEmpty(dto.getVerificationStatus())) {
			rowhead.createCell(headingIndex).setCellValue("Verification Status");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getVerifiedDate())) {
			rowhead.createCell(headingIndex).setCellValue("Verified Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getVerifiedUser())) {
			rowhead.createCell(headingIndex).setCellValue("Verified User");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getVerificationRemarks())) {
			rowhead.createCell(headingIndex).setCellValue("Verification Remarks ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getVerificationAdditionalRemarks())) {
			rowhead.createCell(headingIndex).setCellValue("Verification Additional Remarks ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTotalWeightage())) {
			rowhead.createCell(headingIndex).setCellValue("Total Weightage");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getLastdateofFeePayment())) {
			rowhead.createCell(headingIndex).setCellValue("Last date of Fee Payment");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getApplicationStatus())) {
			rowhead.createCell(headingIndex).setCellValue("Application Status");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getApplicationStatusRemarks())) {
			rowhead.createCell(headingIndex).setCellValue("Application Status Remarks");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getApplicationStatusUpdatedOn())) {
			rowhead.createCell(headingIndex).setCellValue("Application Status Updated on");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getApplicantStatus())) {
			rowhead.createCell(headingIndex).setCellValue("Applicant Status");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getApplicantStatusUpdatedOn())) {
			rowhead.createCell(headingIndex).setCellValue("Applicant Status Updated on");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPrerequisiteExamName())) {
			rowhead.createCell(headingIndex).setCellValue("Prerequisite Exam Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPrerequisiteExamMonthYear())) {
			rowhead.createCell(headingIndex).setCellValue("Prerequisite Exam Month & Year");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPrerequisiteMaxmarks())) {
			rowhead.createCell(headingIndex).setCellValue("Prerequisite Max marks");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPrerequisiteMarks())) {
			rowhead.createCell(headingIndex).setCellValue("Prerequisite Marks");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPrerequisiteRollNo())) {
			rowhead.createCell(headingIndex).setCellValue("Prerequisite Roll No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCitizenship())) {
			rowhead.createCell(headingIndex).setCellValue("Citizenship");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getBloodGroup())) {
			rowhead.createCell(headingIndex).setCellValue("Blood Group");
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
		if(!Utils.isNullOrEmpty(dto.getMotherTongue())) {
			rowhead.createCell(headingIndex).setCellValue("Mother Tongue");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDifferentlyAbled())) {
			rowhead.createCell(headingIndex).setCellValue("Differently Abled");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDifferentlyAbledDescription())) {
			rowhead.createCell(headingIndex).setCellValue("Differently Abled Description");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getAadhaarNo())) {
			rowhead.createCell(headingIndex).setCellValue("Aadhaar No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getAadhaarEnrollmentNo())) {
			rowhead.createCell(headingIndex).setCellValue("Aadhaar Enrollment No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPioOrOci())) {
			rowhead.createCell(headingIndex).setCellValue("PIO or OCI");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPioOrOciCardNo())) {
			rowhead.createCell(headingIndex).setCellValue("PIO or OCI Card No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPlaceofBirth())) {
			rowhead.createCell(headingIndex).setCellValue("Place of Birth");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getBirthCountry())) {
			rowhead.createCell(headingIndex).setCellValue("Birth Country");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getBirthState())) {
			rowhead.createCell(headingIndex).setCellValue("Birth State");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getBirthCity())) {
			rowhead.createCell(headingIndex).setCellValue("Birth City");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getBirthPincode())) {
			rowhead.createCell(headingIndex).setCellValue("Birth Pincode");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSponsershipName())) {
			rowhead.createCell(headingIndex).setCellValue("Sponsership Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSponsershipEmail())) {
			rowhead.createCell(headingIndex).setCellValue("Sponsership Email");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSponsershipPhoneNumber())) {
			rowhead.createCell(headingIndex).setCellValue("Sponsership PhoneNumber");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSponsershipCountry())) {
			rowhead.createCell(headingIndex).setCellValue("Sponsership Country");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPassportNo())) {
			rowhead.createCell(headingIndex).setCellValue("Passport No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPassportIssuedDate())) {
			rowhead.createCell(headingIndex).setCellValue("Passport Issued Date ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPassportExpiryDate())) {
			rowhead.createCell(headingIndex).setCellValue("Passport Expiry Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPassportIssuedFrom())) {
			rowhead.createCell(headingIndex).setCellValue("Passport Issued From");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSecondLanguage())) {
			rowhead.createCell(headingIndex).setCellValue("Second Language");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getResearchTopicDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Research Topic Details");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getInformationOnInstitution())) {
			rowhead.createCell(headingIndex).setCellValue("Information on Institution ");
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
		if(!Utils.isNullOrEmpty(dto.getCurrentAddressCountry())) {
			rowhead.createCell(headingIndex).setCellValue("Current Address Country");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getCurrentAddressPin())) {
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
		if(!Utils.isNullOrEmpty(dto.getPermanentAddressCountry())) {
			rowhead.createCell(headingIndex).setCellValue("Permanent Address Country");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPermanentAddressPin())) {
			rowhead.createCell(headingIndex).setCellValue("Permanent Address PIN Code");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFathersName())) {
			rowhead.createCell(headingIndex).setCellValue("Father’s Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFatherEmailID())) {
			rowhead.createCell(headingIndex).setCellValue("Father’s Email ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFatherMobileNo())) {
			rowhead.createCell(headingIndex).setCellValue("Father’s Mobile No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFathersQualification())) {
			rowhead.createCell(headingIndex).setCellValue("Father’s Qualification");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFathersOccupation())) {
			rowhead.createCell(headingIndex).setCellValue("Father’s Occupation ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
//		if(!Utils.isNullOrEmpty(dto.getFathersIncome())) {
//			rowhead.createCell(headingIndex).setCellValue("Father’s Income");
//			rowhead.getCell(headingIndex).setCellStyle(style);
//			headingIndex++;
//		}
		if(!Utils.isNullOrEmpty(dto.getMothersName())) {
			rowhead.createCell(headingIndex).setCellValue("Mother’s Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMotherEmailID())) {
			rowhead.createCell(headingIndex).setCellValue("Mother’s Email ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMotherMobileNo())) {
			rowhead.createCell(headingIndex).setCellValue("Mother’s Mobile No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMothersQualification())) {
			rowhead.createCell(headingIndex).setCellValue("Mother’s Qualification");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMothersOccupation())) {
			rowhead.createCell(headingIndex).setCellValue("Mother’s Occupation");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
//		if(!Utils.isNullOrEmpty(dto.getMothersIncome())) {
//			rowhead.createCell(headingIndex).setCellValue("Mother’s Income");
//			rowhead.getCell(headingIndex).setCellStyle(style);
//			headingIndex++;
//		}
		if(!Utils.isNullOrEmpty(dto.getFamilyIncome())) {
			rowhead.createCell(headingIndex).setCellValue("Family Income");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getGuardiansName())) {
			rowhead.createCell(headingIndex).setCellValue("Guardian’s Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getGuardiansEmailID())) {
			rowhead.createCell(headingIndex).setCellValue("Guardian’s Email ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getGuardiansMobileNo())) {
			rowhead.createCell(headingIndex).setCellValue("Guardian’s Mobile No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getExtraCurricularDetail())) {
			for(int j=1;j<=sportsCount;j++) {
				if(sportsDetails.containsKey(j) && studentSportsIds.contains(j)) {
					rowhead.createCell(headingIndex).setCellValue(sportsDetails.get(j));
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
				}
			}
		}
		if(!Utils.isNullOrEmpty(dto.getWorkExperienceDetails())) {
			for(int j=1;j<=workcount;j++) {
				rowhead.createCell(headingIndex).setCellValue("Orgianisation name"+" "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
				rowhead.createCell(headingIndex).setCellValue("Organisation " +j+" Functional Area");
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
				rowhead.createCell(headingIndex).setCellValue("Organisation " +j+" Address");
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
				rowhead.createCell(headingIndex).setCellValue("Organisation "+j+" Designation");
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
				rowhead.createCell(headingIndex).setCellValue("Organisation  "+j+" Experience from");
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
				rowhead.createCell(headingIndex).setCellValue("Organisation "+ j +" Experience till");
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
				rowhead.createCell(headingIndex).setCellValue("Organisation "+j+" Total Experience");
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
			}
		}
		if(!Utils.isNullOrEmpty(dto.getTotalPartTimeExperience())) {
			rowhead.createCell(headingIndex).setCellValue("Total Part Time Experience");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTotalFullTimeExperience())) {
			rowhead.createCell(headingIndex).setCellValue("Total Full Time Experience");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getChristiteRegisterNo())) {
			rowhead.createCell(headingIndex).setCellValue("Christite Register No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}

		for(StudentApplnEntriesDTO dbo : dbos) {
			int dataIndexs = 0 ;
			rowhead = sheet.createRow(rowCount);
			
			StudentPersonalDataAddtnlDTO studentPersonalDataAddtnlDTO  = null;
			if(!Utils.isNullOrEmpty(personalDataAddtnlIdsMap) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlMap)  && personalDataAddtnlIdsMap.containsKey(Integer.parseInt(dbo.getStudentApplnEntriesId()))) {
				Integer personalDataAddtId = personalDataAddtnlIdsMap.get(Integer.parseInt(dbo.getStudentApplnEntriesId()));
				if(studentPersonalDataAddtnlMap.containsKey(personalDataAddtId)) {
					 studentPersonalDataAddtnlDTO = studentPersonalDataAddtnlMap.get(personalDataAddtId);
				}
			}
			
			StudentPersonalDataAddressDTO studentPersonalDataAddressDTO = null;
			if(!Utils.isNullOrEmpty(studentPersonalDataAddressIdsMap) && !Utils.isNullOrEmpty(studentPersonalDataAddressMap)  && studentPersonalDataAddressIdsMap.containsKey(Integer.parseInt(dbo.getStudentApplnEntriesId()))) {
				Integer studentPersonalDataAddressId = studentPersonalDataAddressIdsMap.get(Integer.parseInt(dbo.getStudentApplnEntriesId()));
				if(studentPersonalDataAddressMap.containsKey(studentPersonalDataAddressId)) {
					studentPersonalDataAddressDTO = studentPersonalDataAddressMap.get(studentPersonalDataAddressId);
				}
			}
			
			StudentApplnPrerequisiteDTO studentApplnPrerequisite1 = null;
			if(!Utils.isNullOrEmpty(studentApplnPrerequisiteMap) && studentApplnPrerequisiteMap.containsKey(Integer.parseInt(dbo.getStudentApplnEntriesId()))) {
				studentApplnPrerequisite1 = studentApplnPrerequisiteMap.get(Integer.parseInt(dbo.getStudentApplnEntriesId()));
			}
			
			
			if(!Utils.isNullOrEmpty(dto.getApplicationNo())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicationNumber())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicationNumber());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getApplicantName())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicantName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDateofBirth())) {
				if(!Utils.isNullOrEmpty(dbo.getDob())) {
					rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(dbo.getDob()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getGender())) {
				if(!Utils.isNullOrEmpty(dbo.getGender())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getGender().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMobileNo())) {
				if(!Utils.isNullOrEmpty(dbo.getMobileNo())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getMobileNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmailID())) {
				if(!Utils.isNullOrEmpty(dbo.getPersonalEmailId())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getPersonalEmailId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSubmissionDate())) {
				if(!Utils.isNullOrEmpty(dbo.getSubmissionDateTime())) {
					rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateTimeToStringDateTime(dbo.getSubmissionDateTime()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getProgramme())) {
				if(!Utils.isNullOrEmpty(dbo.getProgramme())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getProgramme());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getAdmissionYear())) {
				if(!Utils.isNullOrEmpty(dbo.getAdmissionYear())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getAdmissionYear());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getIntakeBatch())) {
				if(!Utils.isNullOrEmpty(dbo.getIntakeBatch())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getIntakeBatch().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getAdmissionType())) {
				if(!Utils.isNullOrEmpty(dbo.getProgramme())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getAdmissionType().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCampus())) {
				if(!Utils.isNullOrEmpty(dbo.getCampusOrLocation())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getCampusOrLocation());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPreferences())) {
				Map<Integer,String> preferenceOrder = new HashMap<Integer, String>();
				if(!Utils.isNullOrEmpty(studentApplnPreferenceMap) && studentApplnPreferenceMap.containsKey(Integer.parseInt(dbo.getStudentApplnEntriesId()))) {
					List<StudentApplnPreferenceDTO> data = studentApplnPreferenceMap.get(Integer.parseInt(dbo.getStudentApplnEntriesId()));
					data.forEach( order -> {
							String programmeName = "";
							if(!Utils.isNullOrEmpty(order.getProgramePreference())){
								programmeName = order.getProgramePreference().getLabel();
							}
							if(!Utils.isNullOrEmpty(programmeName)){
								if(!Utils.isNullOrEmpty(order.getLocationName())){
									programmeName += "("+ order.getLocationName()+")";
								} else if(!Utils.isNullOrEmpty(order.getCampusName())) {
									programmeName += "("+ order.getCampusName()+")";
								}
							}

//							preferenceOrder.put(order.getPreferenceOrder(), order.getProgramePreference().getLabel()+ (!Utils.isNullOrEmpty(order.getLocationName())?"("+ order.getLocationName()+")":
//						"("+ order.getCampusName()+")"));
						preferenceOrder.put(order.getPreferenceOrder(),programmeName );
					});
				}
				for(int r=1; r<=preferencesCount;r++) {
					if(preferenceOrder.containsKey(r)) {
						rowhead.createCell(dataIndexs).setCellValue(preferenceOrder.get(r));
						dataIndexs++;
					} 
					else {
						rowhead.createCell(dataIndexs).setCellValue("");
						dataIndexs++;
					}	
				}
			}
			if(!Utils.isNullOrEmpty(dto.getSpecialisation())) {
				if(!Utils.isNullOrEmpty(dbo.getSpecialization())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getSpecialization().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSelectionProcessRound1())||!Utils.isNullOrEmpty(dto.getSelectionProcessRound2())) {
				Map<Integer,StudentApplnSelectionProcessDatesDTO> processDatesDetails = new HashMap<Integer, StudentApplnSelectionProcessDatesDTO>();
				if(!Utils.isNullOrEmpty(studentApplnSelectionProcessDatesMap)  && studentApplnSelectionProcessDatesMap.containsKey(Integer.parseInt(dbo.getStudentApplnEntriesId()))) {
					List<StudentApplnSelectionProcessDatesDTO> studentApplnSelectionProcessDatesList1 = studentApplnSelectionProcessDatesMap.get(Integer.parseInt(dbo.getStudentApplnEntriesId()));
					
					studentApplnSelectionProcessDatesList1.forEach( s -> {
							processDatesDetails.put(s.getProcessOrder(), s);
					});
				}
				StudentApplnSelectionProcessDatesDTO data;
				if(processDatesDetails.containsKey(1)) {
					data = processDatesDetails.get(1);
					if(!Utils.isNullOrEmpty(dto.getSelectionProcessRound1())) {
						if(!Utils.isNullOrEmpty(data.getSelectionStageName())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getSelectionStageName());
						}
						dataIndexs++;
					}
					if(!Utils.isNullOrEmpty(dto.getSelectionProcessDateRound1())) {
						if(!Utils.isNullOrEmpty(data.getSelectionProcessDate1())) {
							rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(data.getSelectionProcessDate1()));
						}
						dataIndexs++;
					}
					if(!Utils.isNullOrEmpty(dto.getSelectionProcessTimeRound1())) {
						if(!Utils.isNullOrEmpty(data.getSelectionProcessTime())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getSelectionProcessTime().toString());
						}
						dataIndexs++;
					}
					if(!Utils.isNullOrEmpty(dto.getSelectionProcessVenueRound1())) {
						if(!Utils.isNullOrEmpty(data.getVenueName())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getVenueName());
						}
						dataIndexs++;
					}
				}  else {
					dataIndexs += 4;
				}
				if(processDatesDetails.containsKey(2)) {
					data = processDatesDetails.get(2);
					if(!Utils.isNullOrEmpty(dto.getSelectionProcessRound2())) {
						if(!Utils.isNullOrEmpty(data.getSelectionStageName())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getSelectionStageName());
						}
						dataIndexs++;
					}
					if(!Utils.isNullOrEmpty(dto.getSelectionProcessDateRound2())) {
						if(!Utils.isNullOrEmpty(data.getSelectionProcessDate1())) {
							rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(data.getSelectionProcessDate1()));
						}
						dataIndexs++;
					}
					if(!Utils.isNullOrEmpty(dto.getSelectionProcessTimeRound2())) {
						if(!Utils.isNullOrEmpty(data.getSelectionProcessTime())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getSelectionProcessTime().toString());
						}
						dataIndexs++;
					}
					if(!Utils.isNullOrEmpty(dto.getSelectionProcessVenueRound2())) {
						if(!Utils.isNullOrEmpty(data.getVenueName())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getVenueName());
						}
						dataIndexs++;
					}
				} else {
					dataIndexs += 4;
				}
			}
			
			
			if(!Utils.isNullOrEmpty(dto.getEducationDetails())) {
				Map<Integer,StudentEducationalDetailsDTO> educationaldDetails = new HashMap<Integer, StudentEducationalDetailsDTO>();
				if(!Utils.isNullOrEmpty(studentEducationalDetailsMap) && !Utils.isNullOrEmpty(studentEducationalDetailsMap.containsKey(Integer.parseInt(dbo.getStudentApplnEntriesId())))) {
					List<StudentEducationalDetailsDTO> StudentEducationalDetailsList = studentEducationalDetailsMap.get(Integer.parseInt(dbo.getStudentApplnEntriesId()));
					
					if(!Utils.isNullOrEmpty(StudentEducationalDetailsList)) {
						StudentEducationalDetailsList.forEach( eductionalDetails -> {
							educationaldDetails.put(eductionalDetails.getAdmQualificationListDTO().getQualificationOrder(), eductionalDetails);
					});
					}
				
				}
				for(int j=1;j<=processOrder1;j++) {
					if(educationaldDetails.containsKey(j)) {
						StudentEducationalDetailsDTO data = educationaldDetails.get(j);
						if(!Utils.isNullOrEmpty(data.getAdmQualificationDegreeListDTO().getDegreeName())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getAdmQualificationDegreeListDTO().getDegreeName());
						}
						dataIndexs++; 

						if(!Utils.isNullOrEmpty(data.getUniversityBoard()) && !Utils.isNullOrEmpty(data.getUniversityBoard().getLabel())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getUniversityBoard().getLabel()); 
						}
						dataIndexs++;

						if(!Utils.isNullOrEmpty(data.getCountry()) && !Utils.isNullOrEmpty(data.getCountry().getLabel())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getCountry().getLabel());
						}
						dataIndexs++;

						rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(data.getState()) ? data.getState().getLabel(): data.getInstitutionOthersState()));
						dataIndexs++;

						rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(data.getErpInstitutionDTO()) ? data.getErpInstitutionDTO().getInstitutionName():data.getInstitutionOthers()));
						dataIndexs++;

						rowhead.createCell(dataIndexs).setCellValue( (!Utils.isNullOrEmpty(data.getYearOfPassing())?String.valueOf(data.getYearOfPassing())+" & ":"")+
																		(!Utils.isNullOrEmpty(data.getMonthOfPassing())? String.valueOf(Month.of(data.getMonthOfPassing())) :""));
						dataIndexs++;

						if(!Utils.isNullOrEmpty(data.getConsolidatedMaximumMarks())) {
							rowhead.createCell(dataIndexs).setCellValue(String.valueOf(data.getConsolidatedMaximumMarks()));
						}
						dataIndexs++;

						if(!Utils.isNullOrEmpty(data.getConsolidatedMarksObtained())) {
							rowhead.createCell(dataIndexs).setCellValue(String.valueOf(data.getConsolidatedMarksObtained()));
						}
						dataIndexs++;

						if(!Utils.isNullOrEmpty(data.getPercentage())) {
							rowhead.createCell(dataIndexs).setCellValue(data.getPercentage());
						}
						dataIndexs++;
					} else {
						dataIndexs += 9;
					}
				}
			}
			if(!Utils.isNullOrEmpty(dto.getResidentCategory())) {
				if(!Utils.isNullOrEmpty(dbo.getErpResidentCategoryDTO()) && !Utils.isNullOrEmpty(dbo.getErpResidentCategoryDTO().getLabel())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getErpResidentCategoryDTO().getLabel());
				}
				dataIndexs++;
			}
//			if(!Utils.isNullOrEmpty(dto.getAppliedBatch())) {
//				if(!Utils.isNullOrEmpty(dbo.getAppliedBatch()) && !Utils.isNullOrEmpty(dbo.getAppliedBatch().getLabel())) {
//					rowhead.createCell(dataIndexs).setCellValue(dbo.getAppliedBatch().getLabel());
//				}
//				dataIndexs++;
//			}
			if(!Utils.isNullOrEmpty(dto.getVerificationStatus())) {
				if(dbo.getApplicationVerificationStatus().equalsIgnoreCase("VE")) {	
					rowhead.createCell(dataIndexs).setCellValue("Verified");
					dataIndexs++;
				} else if(dbo.getApplicationVerificationStatus().equalsIgnoreCase("NV")) {
					rowhead.createCell(dataIndexs).setCellValue("Not Verified");
					dataIndexs++;
				} else {
					rowhead.createCell(dataIndexs).setCellValue("Not Eligible");
					dataIndexs++;
				}
			}
			if(!Utils.isNullOrEmpty(dto.getVerifiedDate())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicationVerifiedDate())) {
					rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(dbo.getApplicationVerifiedDate()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getVerifiedUser())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicationVerifiedUserId())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicationVerifiedUserId());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getVerificationRemarks())) {
				if(!Utils.isNullOrEmpty(dbo.getStudentApplnVerificationRemarksId()) && !Utils.isNullOrEmpty(dbo.getStudentApplnVerificationRemarksId().getLabel())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getStudentApplnVerificationRemarksId().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getVerificationAdditionalRemarks())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicationVerificationAddtlRemarks())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicationVerificationAddtlRemarks());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getTotalWeightage())) {
				if(!Utils.isNullOrEmpty(dbo.getTotalWeightage())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getTotalWeightage().toString());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getLastdateofFeePayment())) {
				if(!Utils.isNullOrEmpty(dbo.getFeePaymentFinalDateTime())) {
					rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateTimeToStringDateTime(dbo.getFeePaymentFinalDateTime()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getApplicationStatus())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicantionStatusDisplayText())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicantionStatusDisplayText());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getApplicationStatusRemarks())) {
				if(!Utils.isNullOrEmpty(dbo.getSelectionStatusRemarks())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getSelectionStatusRemarks());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getApplicationStatusUpdatedOn())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicationStatusTime())) {
					rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateTimeToStringDateTime(dbo.getApplicationStatusTime()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getApplicantStatus())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicantStatusDisplayText1())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getApplicantStatusDisplayText1());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getApplicantStatusUpdatedOn())) {
				if(!Utils.isNullOrEmpty(dbo.getApplicantStatusTime())) {
					rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateTimeToStringDateTime(dbo.getApplicantStatusTime()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPrerequisiteExamName())) {
//				if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO())) {
//					if(dbo.getStudentApplnPrerequisiteDBO().getRecordStatus() == 'A') {
//						rowhead.createCell(dataIndexs).setCellValue(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().getAdmPrerequisiteSettingsDBO().getAdmPrerequisiteExamDBO().getExamName());
//						sheet.autoSizeColumn(dataIndexs);
//					}
//				}
				if(!Utils.isNullOrEmpty(studentApplnPrerequisite1)  && !Utils.isNullOrEmpty(studentApplnPrerequisite1.getExamName())) {
						rowhead.createCell(dataIndexs).setCellValue(studentApplnPrerequisite1.getExamName());
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getPrerequisiteExamMonthYear())) {
//				if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO())) {
					//					if(dbo.getStudentApplnPrerequisiteDBO().getRecordStatus() == 'A') {
					//						String monthString = new DateFormatSymbols().getMonths()[dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().getExamMonth()-1];
					//						rowhead.createCell(dataIndexs).setCellValue(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().getExamYear().toString()
					//								+" & "+ monthString);
					//						sheet.autoSizeColumn(dataIndexs);
					//					}
					if(!Utils.isNullOrEmpty(studentApplnPrerequisite1) && !Utils.isNullOrEmpty(studentApplnPrerequisite1.getExamMonth())) {
						String monthString = new DateFormatSymbols().getMonths()[studentApplnPrerequisite1.getExamMonth()-1];
						rowhead.createCell(dataIndexs).setCellValue(monthString +" & "+ studentApplnPrerequisite1.getExamYear().toString()
								 );
					}
//				}
				dataIndexs++;

			}
//			if(!Utils.isNullOrEmpty(dto.getPrerequisiteMaxmarks())) {
//				if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO())) {
//					if(dbo.getStudentApplnPrerequisiteDBO().getRecordStatus() == 'A') {
//						rowhead.createCell(dataIndexs).setCellValue(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().getAdmPrerequisiteSettingsDBO().getTotalMarks());
//						sheet.autoSizeColumn(dataIndexs);
//					}
//				}
//				dataIndexs++;
//			}
			
			if(!Utils.isNullOrEmpty(dto.getPrerequisiteMaxmarks())) {
				if(!Utils.isNullOrEmpty(studentApplnPrerequisite1) && !Utils.isNullOrEmpty(studentApplnPrerequisite1.getTotalMarks())) {
						rowhead.createCell(dataIndexs).setCellValue(studentApplnPrerequisite1.getTotalMarks());
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getPrerequisiteMarks())) {
				if(!Utils.isNullOrEmpty(studentApplnPrerequisite1) && !Utils.isNullOrEmpty(studentApplnPrerequisite1.getMarksObtained())) {
						rowhead.createCell(dataIndexs).setCellValue(String.valueOf(studentApplnPrerequisite1.getMarksObtained()));
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getPrerequisiteRollNo())) {
				if(!Utils.isNullOrEmpty(studentApplnPrerequisite1) && !Utils.isNullOrEmpty(studentApplnPrerequisite1.getExamRollNo())) {
						rowhead.createCell(dataIndexs).setCellValue(studentApplnPrerequisite1.getExamRollNo());
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getCitizenship())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) &&  !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpCountry()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpCountry().getLabel())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getErpCountry().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getBloodGroup())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpBloodGroup()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpBloodGroup().getLabel())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getErpBloodGroup().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getReligion())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpReligionId()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpReligionId().getLabel())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getErpReligionId().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getReservationCategory())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpReservationCategory()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpReservationCategory().getLabel())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getErpReservationCategory().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMotherTongue())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpMotherTounge()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpMotherTounge().getLabel())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getErpMotherTounge().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDifferentlyAbled())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getDifferentlyAbled())) {
					rowhead.createCell(dataIndexs).setCellValue( studentPersonalDataAddtnlDTO.getDifferentlyAbled() ? "Yes":"No");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDifferentlyAbledDescription())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpDifferentlyAbled()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpDifferentlyAbled().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getErpDifferentlyAbled().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getAadhaarNo())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getAadharCardNo())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getAadharCardNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getAadhaarEnrollmentNo())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getAadharEnrolmentNumber())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getAadharEnrolmentNumber());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPioOrOci())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getPioOrOci())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getPioOrOci());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPioOrOciCardNo())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getPioOrOciCardNo())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getPioOrOciCardNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPlaceofBirth())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getPlaceOfBirth())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getPlaceOfBirth());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getBirthCountry())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getBirthCountry())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getBirthCountry().getLabel());
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getBirthState())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO)) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getBirthState()) ? studentPersonalDataAddtnlDTO.getBirthState().getLabel(): studentPersonalDataAddtnlDTO.getBirthStateOthers()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getBirthCity())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO)) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getBirthCity()) ? studentPersonalDataAddtnlDTO.getBirthCity().getLabel() : studentPersonalDataAddtnlDTO.getBirthCityOthers()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getBirthPincode())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getBirthPincode())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getBirthPincode());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSponsershipName())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getSponsershipName())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getSponsershipName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSponsershipEmail())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getSponsershipEmail())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getSponsershipEmail());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSponsershipPhoneNumber())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getSponsershipPhoneNumber())) {
					rowhead.createCell(dataIndexs).setCellValue( (!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getSponsershipNoCountryCode()) ? studentPersonalDataAddtnlDTO.getSponsershipNoCountryCode()+ "-" :" ")
							+studentPersonalDataAddtnlDTO.getSponsershipPhoneNumber());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSponsershipCountry())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getSponsershipCountry())) {
					rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getSponsershipCountry().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPassportNo())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getPassportNo())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getPassportNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPassportIssuedDate())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getPassportIssuedDate())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getPassportIssuedDate());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPassportExpiryDate())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getPassportDateOfExpiry())) {
						rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(studentPersonalDataAddtnlDTO.getPassportDateOfExpiry()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPassportIssuedFrom())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getPassportIssuedCountry()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getPassportIssuedCountry().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getPassportIssuedCountry().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSecondLanguage())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpSecondLanguage()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpSecondLanguage().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getErpSecondLanguage().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getResearchTopicDetails())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getResearchTopicDetails())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getResearchTopicDetails());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getInformationOnInstitution())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpInstitutionReference()) && !Utils.isNullOrEmpty(studentPersonalDataAddtnlDTO.getErpInstitutionReference().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddtnlDTO.getErpInstitutionReference().getLabel());
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getCurrentAddress1())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getCurrentAddressLine1())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getCurrentAddressLine1());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddress2())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getCurrentAddressLine2())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getCurrentAddressLine2());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressCity())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO)) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getCurrentCity())
							? studentPersonalDataAddressDTO.getCurrentCity().getLabel() : studentPersonalDataAddressDTO.getCurrentCityOthers()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressState())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO)) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getCurrentState())
							? studentPersonalDataAddressDTO.getCurrentState().getLabel() : studentPersonalDataAddressDTO.getCurrentStateOthers()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressCountry())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getCurrentCountry())  && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getCurrentCountry().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getCurrentCountry().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressPin())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getCurrentPincode())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getCurrentPincode());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddress1())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getPermanentAddressLine1())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getPermanentAddressLine1());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddress2())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getPermanentAddressLine2())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getPermanentAddressLine2());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressCity())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO)) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getPermanentCity())
							? studentPersonalDataAddressDTO.getPermanentCity().getLabel() : studentPersonalDataAddressDTO.getPermanentCityOthers()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressState())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO)) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getPermanentState())
							? studentPersonalDataAddressDTO.getPermanentState().getLabel() : studentPersonalDataAddressDTO.getPermanentStateOthers()));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressCountry())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getPermanentCountry())  && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getPermanentCountry().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getPermanentCountry().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressPin())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getPermanentPincode())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getPermanentPincode());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFathersName())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherName())) {
						rowhead.createCell(dataIndexs).setCellValue( (!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherSalutation())?
								studentPersonalDataAddressDTO.getFatherSalutation().getLabel():"")
								+" "+studentPersonalDataAddressDTO.getFatherName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFatherEmailID())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherEmail())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getFatherEmail());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFatherMobileNo())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherMobileNo())) {
						rowhead.createCell(dataIndexs).setCellValue( (!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherMobileNoCountryCode()) ? studentPersonalDataAddressDTO.getFatherMobileNoCountryCode()+ "-" :" ")
								 +studentPersonalDataAddressDTO.getFatherMobileNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFathersQualification())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherErpQualificationLevel()) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherErpQualificationLevel().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getFatherErpQualificationLevel().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFathersOccupation())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherOccupation()) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherOccupation().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getFatherOccupation().getLabel());
				}
				dataIndexs++;
			}
//			if(!Utils.isNullOrEmpty(dto.getFathersIncome())) {
//				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO)) {
//						rowhead.createCell(dataIndexs).setCellValue( (!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherYearlyIncomeRangeFrom())?
//								studentPersonalDataAddressDTO.getFatherYearlyIncomeRangeFrom()+" - ":" ")+
//								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherYearlyIncomeRangeTo())?studentPersonalDataAddressDTO.getFatherYearlyIncomeRangeTo()+" ":" ")+
//								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFatherIncomeCurrency().getLabel())?studentPersonalDataAddressDTO.getFatherIncomeCurrency().getLabel():" ")
//								);
//				}
//				dataIndexs++;
//			}
			if(!Utils.isNullOrEmpty(dto.getMothersName())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherName())) {
						rowhead.createCell(dataIndexs).setCellValue( (!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherSalutation())?
								studentPersonalDataAddressDTO.getMotherSalutation().getLabel():"")
							    +" "+studentPersonalDataAddressDTO.getMotherName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMotherEmailID())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherEmail())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getMotherEmail());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMotherMobileNo())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherMobileNo())) {
						rowhead.createCell(dataIndexs).setCellValue(
								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherMobileNoCountryCode())? studentPersonalDataAddressDTO.getMotherMobileNoCountryCode()+ "-":"" )
								+studentPersonalDataAddressDTO.getMotherMobileNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMothersQualification())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherErpQualificationLevel()) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherErpQualificationLevel().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getMotherErpQualificationLevel().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMothersOccupation())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherOccupation())  && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherOccupation().getLabel())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getMotherOccupation().getLabel());
				}
				dataIndexs++;
			}
//			if(!Utils.isNullOrEmpty(dto.getMothersIncome())) {
//				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO)) {
//						rowhead.createCell(dataIndexs).setCellValue(
//								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherYearlyIncomeRangeFrom())? studentPersonalDataAddressDTO.getMotherYearlyIncomeRangeFrom()+" - ":"")+
//								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherYearlyIncomeRangeTo())? studentPersonalDataAddressDTO.getMotherYearlyIncomeRangeTo()+" ":"")+
//								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getMotherIncomeCurrency().getLabel())? studentPersonalDataAddressDTO.getMotherIncomeCurrency().getLabel():""));
//				}
//				dataIndexs++;
//			}
			if(!Utils.isNullOrEmpty(dto.getFamilyIncome())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO)) {
						rowhead.createCell(dataIndexs).setCellValue(
								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFamilyAnnualIncome())? String.valueOf(studentPersonalDataAddressDTO.getFamilyAnnualIncome())+" ":"")+
								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getFamilyAnnualIncomeCurrency())? studentPersonalDataAddressDTO.getFamilyAnnualIncomeCurrency().getLabel():""));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getGuardiansName())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getGuardianName())) {
						rowhead.createCell(dataIndexs).setCellValue(
								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getGuardianSalutation())?studentPersonalDataAddressDTO.getGuardianSalutation().getLabel()+" ":""  )  
								+studentPersonalDataAddressDTO.getGuardianName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getGuardiansEmailID())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getGuardianEmail())) {
						rowhead.createCell(dataIndexs).setCellValue(studentPersonalDataAddressDTO.getGuardianEmail());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getGuardiansMobileNo())) {
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getGuardianMobileNo())) {
						rowhead.createCell(dataIndexs).setCellValue(
								(!Utils.isNullOrEmpty(studentPersonalDataAddressDTO.getGuardianMobileNoCountryCode())?studentPersonalDataAddressDTO.getGuardianMobileNoCountryCode()+"-":"" )
								+studentPersonalDataAddressDTO.getGuardianMobileNo());
				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getExtraCurricularDetail())) {
				Map<Integer,StudentExtraCurricularDetailsDTO> extraCurricularDeatails = new HashMap<Integer, StudentExtraCurricularDetailsDTO>();
				if(!Utils.isNullOrEmpty(studentExtraCurricularDetailsMap) && studentExtraCurricularDetailsMap.containsKey(Integer.parseInt(dbo.getStudentApplnEntriesId()))) {
					List<StudentExtraCurricularDetailsDTO> StudentExtraCurricularDetailsList = studentExtraCurricularDetailsMap.get(Integer.parseInt(dbo.getStudentApplnEntriesId()));
					StudentExtraCurricularDetailsList.forEach( value -> {
							if(!Utils.isNullOrEmpty(value.getErpSports())) {
								extraCurricularDeatails.put(Integer.parseInt(value.getErpSports().getValue()), value);
						}
					});
				}	
				for(int j=1;j<=sportsCount;j++) {
					if(studentSportsIds.contains(j)){
						if(extraCurricularDeatails.containsKey(j)) {
							StudentExtraCurricularDetailsDTO studentCurricular = extraCurricularDeatails.get(j);
							if(!Utils.isNullOrEmpty(studentCurricular) && !Utils.isNullOrEmpty(studentCurricular.getErpSportsLevel()) && !Utils.isNullOrEmpty(studentCurricular.getErpSportsLevel().getLabel())) {
								rowhead.createCell(dataIndexs).setCellValue(studentCurricular.getErpSportsLevel().getLabel());
							}
						}
						dataIndexs++;
					}
				}
			}
			
			if(!Utils.isNullOrEmpty(dto.getWorkExperienceDetails())) {
				int count = 0;
				if(!Utils.isNullOrEmpty(studentWorkExperienceMap) && studentWorkExperienceMap.containsKey(Integer.parseInt(dbo.getStudentApplnEntriesId()))) {
					 List<StudentWorkExperienceDTO> studentWorkExperienceList = studentWorkExperienceMap.get(Integer.parseInt(dbo.getStudentApplnEntriesId()));
					for(StudentWorkExperienceDTO data :studentWorkExperienceList) {
							rowhead.createCell(dataIndexs).setCellValue(data.getOrganizationName());
							dataIndexs++;
							rowhead.createCell(dataIndexs).setCellValue(data.getFunctionalArea());
							dataIndexs++;
							rowhead.createCell(dataIndexs).setCellValue(data.getOrganizationAddress());
							dataIndexs++;
							rowhead.createCell(dataIndexs).setCellValue(data.getDesignation());
							dataIndexs++;
							if(!Utils.isNullOrEmpty(data.getWorkExperienceFromDate())){
								rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(data.getWorkExperienceFromDate()));
							}
							dataIndexs++;
							if(!Utils.isNullOrEmpty(data.getWorkExperienceToDate())){
								rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(data.getWorkExperienceToDate()));
							}
							dataIndexs++;
//							if(!Utils.isNullOrEmpty(data.getWorkExperienceYears())) {
								rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(data.getWorkExperienceYears())?data.getWorkExperienceYears().toString()+" Years ":"")
										+(!Utils.isNullOrEmpty(data.getWorkExperienceMonth()) ? data.getWorkExperienceMonth()+" Months":""));
//							}
							dataIndexs++;
							count++;
					}
				}
				while(count < workcount) {
					dataIndexs += 7;
					count++;
				}
			}
			if(!Utils.isNullOrEmpty(dto.getTotalPartTimeExperience())) {
//				if(!Utils.isNullOrEmpty(dbo.getRegisterNo())) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(dbo.getTotalPartTimePreviousExperienceYears())? dbo.getTotalPartTimePreviousExperienceYears()+" Years ":"")
							+(!Utils.isNullOrEmpty(dbo.getTotalPartTimePreviousExperienceMonths()) ? dbo.getTotalPartTimePreviousExperienceMonths()+" Months":""));
//				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getTotalFullTimeExperience())) {
//				if(!Utils.isNullOrEmpty(dbo.getRegisterNo())) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(dbo.getTotalPreviousExperienceYears())?dbo.getTotalPreviousExperienceYears()+" Years ":"")
							+(!Utils.isNullOrEmpty(dbo.getTotalPreviousExperienceMonths()) ? dbo.getTotalPreviousExperienceMonths()+" Months":""));
//				}
				dataIndexs++;
			}
			
			if(!Utils.isNullOrEmpty(dto.getChristiteRegisterNo())) {
				if(!Utils.isNullOrEmpty(dbo.getRegisterNo())) {
						rowhead.createCell(dataIndexs).setCellValue(dbo.getRegisterNo());
				}
				dataIndexs++;
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
