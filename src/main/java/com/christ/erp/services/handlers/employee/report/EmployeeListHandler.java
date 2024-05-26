package com.christ.erp.services.handlers.employee.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEligiblityTestDTO;
import com.christ.erp.services.dto.common.ErpCampusDepartmentMappingDTO;
import com.christ.erp.services.dto.employee.common.EmpDTO;
import com.christ.erp.services.dto.employee.common.EmpGuestContractDetailsDTO;
import com.christ.erp.services.dto.employee.common.EmpMajorAchievementsDTO;
import com.christ.erp.services.dto.employee.common.EmpResignationDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpAddtnlPersonalDataDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpEducationalDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpEmployeeLetterDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpFamilyDetailsAddtnlDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpJobDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpPersonalDataDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpPfGratuityNomineesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpWorkExperienceDTO;
import com.christ.erp.services.dto.employee.report.EmployeeListDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleDetailsComponentsDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleDetailsDTO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDetailsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.employee.report.EmployeeListTransaction;
import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")
@Service
public class EmployeeListHandler {

	@Autowired
	EmployeeListTransaction employeeListTransaction;
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> payScaleEnabled() {
		ApiResult result = new ApiResult();
		result.setSuccess(true);
		return Mono.just(result);
	}

	public Mono<List<EmployeeListDTO>> getGridData(Mono<EmployeeListDTO> data, String userId) {
		return data.handle((employeeListDTO,synchronousSink) -> {
			List<Tuple> list = null;
			list = employeeListTransaction.getEmployeesData1(employeeListDTO);
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

	public Mono<List<EmployeeListDTO>> convertDBOToDTO( List<Tuple> dbos, String userId) {
		List<EmployeeListDTO> dtoData = new ArrayList<EmployeeListDTO>();
		dbos.forEach(dbo -> {
			if(!Utils.isNullOrEmpty(dbo)) {
				EmployeeListDTO dto1 = new EmployeeListDTO();
				dto1.setEmpId(Integer.parseInt(dbo.get("empId").toString()));
				dto1.setEmpNo(!Utils.isNullOrEmpty(dbo.get("empNo"))?  dbo.get("empNo").toString():null);
				dto1.setEmpName(dbo.get("name").toString());
				dto1.setEmpGender(!Utils.isNullOrEmpty(dbo.get("gender"))? dbo.get("gender").toString():null);
				dto1.setEmpDob(!Utils.isNullOrEmpty(dbo.get("dob"))? Utils.convertLocalDateToStringDate( Utils.convertStringDateToLocalDate(dbo.get("dob").toString())):null);
				dtoData.add(dto1);
			}
		});
		return Mono.just(dtoData);
	}

	public Mono<ResponseEntity<InputStreamResource>>  getEmployeeDetails(Mono<EmployeeListDTO> data1, String userId) {
		List<EmployeeListDTO> dtos = new ArrayList<EmployeeListDTO>();
		return data1.handle((employeeListDTO,synchronousSink) -> {
			EmployeeListDTO dto =  employeeListDTO;
			dtos.add(dto);
//			System.out.println("Main query Excution Start"+LocalDateTime.now());
			//			List<EmpDBO> list  = employeeListTransaction.getEmployeesData(employeeListDTO.getEmpIds(),employeeListDTO);

			List<EmpDTO> list = employeeListTransaction.empDBO(employeeListDTO.getEmpIds());

//			System.out.println("Main query Excution end"+LocalDateTime.now());
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

	public Mono<ResponseEntity<InputStreamResource>> convertDBOToExcel(List<EmpDTO> dbos,EmployeeListDTO dto,String userId) {
		List<Integer> empIds = new ArrayList<Integer>();
		List<Integer> empApplnEntriesIds = new ArrayList<Integer>();
		List<Integer> empPersonalDataIds = new ArrayList<Integer>();
		List<Integer> empDeputationErpCampusDepartmentMappingIds = new ArrayList<Integer>();
		List<Integer> empPayScaleDetailsIds = new ArrayList<Integer>();
		int rowCount = 0;
		XSSFWorkbook   workbook = new XSSFWorkbook  ();
		XSSFSheet  sheet = workbook.createSheet("EmployeeListSheet");
		XSSFRow  rowhead = sheet.createRow((short)rowCount++);
		CellStyle style =  workbook.createCellStyle();
		Font font =  workbook.createFont();
		//		sheet.trackAllColumnsForAutoSizing();
		font.setBold(true);
		style.setFont(font); 
		int headingIndex = 0;
		int maxQulification = 0;
		dbos.forEach(dtos -> {
			empIds.add(Integer.parseInt(dtos.getEmpId()));
			if(!Utils.isNullOrEmpty(dtos.getApplnEntriesId())) {
				empApplnEntriesIds.add(Integer.parseInt(dtos.getApplnEntriesId()));
			}
			if(!Utils.isNullOrEmpty(dtos.getEmpPersonalDataId())) {
				empPersonalDataIds.add(dtos.getEmpPersonalDataId());
			}
			if(!Utils.isNullOrEmpty(dtos.getEmployeeDeputedDepartment()) && !Utils.isNullOrEmpty(dtos.getEmployeeDeputedDepartment().getValue())) {
				empDeputationErpCampusDepartmentMappingIds.add(Integer.parseInt(dtos.getEmployeeDeputedDepartment().getValue()));
			}
		});
		sheet.createFreezePane(0,1);
		Map<Integer, String> empComponentsMap = new HashMap<Integer, String>();
		Map<Integer, ErpQualificationLevelDBO> qualificationLevelMap = new HashMap<Integer, ErpQualificationLevelDBO>();
		Map<Integer, Integer> qualificationsCountsMap = new HashMap<Integer, Integer>();
		Set<Integer> qualificationOrders = new HashSet<Integer>();
		Integer workCount = 0;
		Integer familyDependentCount = 0;
		Integer guestCount = 0;
		Map<Integer,Integer> empAppNo = null;
		Map<Integer, EmpJobDetailsDTO> empJobDetailsMap= null;
		Map<Integer, List<EmpEmployeeLetterDetailsDTO>> empLetterDetailsMap = null;
		Map<Integer, List<EmpPfGratuityNomineesDTO>> empPfGratuityNomineesDetailsMap = null;
		Map<Integer, EmpApproversDetailsDTO> empApproversMap = null;
		Map<Integer, List<EmpEducationalDetailsDTO>> empEduDetailsMap = null;
		Map<Integer, List<EmpGuestContractDetailsDTO>> empGuestContractDetailsDBOMap = null;
		Map<Integer, List<EmpWorkExperienceDTO>> empWorkExperienceDBOMap = null;
		Map<Integer, List<EmpEligiblityTestDTO>> empEligibilityTestDBOMap = null;
		Map<Integer, List<EmpMajorAchievementsDTO>> empMajorAchievementsDBOMap = null;
		Map<Integer, EmpResignationDTO> empResignationDBOMap = null;
		Map<Integer, EmpPayScaleDetailsDTO> empPayScaleDetailsDBOMap = null;
		Map<Integer, List<EmpPayScaleDetailsComponentsDTO>> empComponentDetailsMap =null;
		Map<Integer,List<EmpFamilyDetailsAddtnlDTO>> empFamilyDetailsMap = null;
		Map<Integer,ErpCampusDepartmentMappingDTO> empDeputationErpCampusDepartmentMap = null;
		Map<Integer, EmpPersonalDataDTO> empPersonalDataMap = null;
		Map<Integer, EmpAddtnlPersonalDataDTO> empAddtnlPersonalDataMap = null;

//		System.out.println("sub query Excution Start"+LocalDateTime.now());
		if(!Utils.isNullOrEmpty(dto.getDateOfRetirement()) || !Utils.isNullOrEmpty(dto.getContractStartDate()) || !Utils.isNullOrEmpty(dto.getContractEndDate()) || !Utils.isNullOrEmpty(dto.getContractRemarks())
				|| !Utils.isNullOrEmpty(dto.getPfAccountNo()) || !Utils.isNullOrEmpty(dto.getPfDate()) || !Utils.isNullOrEmpty(dto.getUanNo()) || !Utils.isNullOrEmpty(dto.getLicGratuityNo())
				|| !Utils.isNullOrEmpty(dto.getLicGratuityDate()) || !Utils.isNullOrEmpty(dto.getBankAccountNo()) || !Utils.isNullOrEmpty(dto.getIfscCode()) || !Utils.isNullOrEmpty(dto.getSmartCardNo())
				|| !Utils.isNullOrEmpty(dto.getCanDisplayInWebsite()) || !Utils.isNullOrEmpty(dto.getHasVacation()) || !Utils.isNullOrEmpty(dto.getHasVacationTimeZone()) || !Utils.isNullOrEmpty(dto.getVacationTimeZone()) 
				|| !Utils.isNullOrEmpty(dto.getHasHolidayTimeZone()) || !Utils.isNullOrEmpty(dto.getHolidayTimezone()) || !Utils.isNullOrEmpty(dto.getRoasterAllotmentApplicable()) 
				|| !Utils.isNullOrEmpty(dto.getLeaveCategory())|| !Utils.isNullOrEmpty(dto.getRecognisedExperience()) ) {
			List<EmpJobDetailsDTO> list1 = employeeListTransaction.empJobDetailsDBO(empIds);
			empJobDetailsMap = list1.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.getEmpId()), s -> s));
		}

		if(!Utils.isNullOrEmpty(dto.getNationality()) || !Utils.isNullOrEmpty(dto.getMaritalStatus()) || !Utils.isNullOrEmpty(dto.getReligion()) || !Utils.isNullOrEmpty(dto.getReservationCategory()) || !Utils.isNullOrEmpty(dto.getBloodGroup())
				|| !Utils.isNullOrEmpty(dto.getDifferentlyAbled()) || !Utils.isNullOrEmpty(dto.getDisabilityType()) ||	!Utils.isNullOrEmpty(dto.getCurrentAddress1()) || !Utils.isNullOrEmpty(dto.getCurrentAddress2())
				|| !Utils.isNullOrEmpty(dto.getCurrentAddressCountry()) || !Utils.isNullOrEmpty(dto.getCurrentAddressState()) || !Utils.isNullOrEmpty(dto.getCurrentAddressCity()) || !Utils.isNullOrEmpty(dto.getCurrentAddressPinCode())
				|| !Utils.isNullOrEmpty(dto.getPermanentAddress1()) || !Utils.isNullOrEmpty(dto.getPermanentAddress2()) || !Utils.isNullOrEmpty(dto.getPermanentAddressCountry()) || !Utils.isNullOrEmpty(dto.getPermanentAddressState())
				|| !Utils.isNullOrEmpty(dto.getPermanentAddressCity()) || !Utils.isNullOrEmpty(dto.getPermanentAddressPinCode()) || !Utils.isNullOrEmpty(dto.getHighestQualification()) || !Utils.isNullOrEmpty(dto.getHighestQualificationForStaffAlbum())
				|| !Utils.isNullOrEmpty(dto.getOrcidNo()) || !Utils.isNullOrEmpty(dto.getScopusId())) {
			List<EmpPersonalDataDTO> list = employeeListTransaction.empPersonalDataDBO(empPersonalDataIds);
			empPersonalDataMap = list.stream().collect(Collectors.toMap(s -> s.getId(), s ->s ));
		}

		if(!Utils.isNullOrEmpty(dto.getPanNo()) || !Utils.isNullOrEmpty(dto.getAadharNo()) || !Utils.isNullOrEmpty(dto.getEmergencyContactDetails()) || !Utils.isNullOrEmpty(dto.getPassportDetails())
				|| !Utils.isNullOrEmpty(dto.getVisaDetails()) || !Utils.isNullOrEmpty(dto.getFrroDetails()) || !Utils.isNullOrEmpty(dto.getFourWheelerNo()) || !Utils.isNullOrEmpty(dto.getTwoWheelerNo())
				|| !Utils.isNullOrEmpty(dto.getFamilyDetails())) {
			List<EmpAddtnlPersonalDataDTO> list = employeeListTransaction.empAddtnlPersonalDataDBO(empPersonalDataIds);
			empAddtnlPersonalDataMap = list.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.getEmpPersonalDataId()), s ->s ));
		}

		if(!Utils.isNullOrEmpty(dto.getApplicationNo())) {
			List<EmpApplnEntriesDTO> list = employeeListTransaction.empApplnEntriesDBOs(empApplnEntriesIds);
			empAppNo = list.stream().collect(Collectors.toMap(s ->Integer.parseInt(s.getApplicantId()), s ->Integer.parseInt(s.getApplicantNumber())));
		}
		if(!Utils.isNullOrEmpty(dto.getDeputedDepartmentAndCampus())) {
			List<ErpCampusDepartmentMappingDTO> list = employeeListTransaction.deputationErpCampusDepartmentMappingDBO(empDeputationErpCampusDepartmentMappingIds);
			empDeputationErpCampusDepartmentMap = list.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
		}
		if(!Utils.isNullOrEmpty(dto.getLetterDetails())) {
			List<EmpEmployeeLetterDetailsDTO> employeeList = employeeListTransaction.getEmployeeLetterDetailsDBOSet(empIds);
			empLetterDetailsMap = employeeList.stream().collect(Collectors.groupingBy(b -> b.getEmpId())); 
		}

		if(!Utils.isNullOrEmpty(dto.getPfNomineeDetails())) {
			List<EmpPfGratuityNomineesDTO> list = employeeListTransaction.getEmpPfGratuityNomineesDBOS(empIds);
			empPfGratuityNomineesDetailsMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpId()));
		}

		if(!Utils.isNullOrEmpty(dto.getApproverDetails())) {
			List<EmpApproversDetailsDTO> list = employeeListTransaction.getEmpApproversDBO(empIds);
			empApproversMap = list.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.getEmpId()),s -> s));
		}
		if(!Utils.isNullOrEmpty(dto.getQualificationDetails())) {
			List<EmpEducationalDetailsDTO> list = employeeListTransaction.getEmpEducationalDetailsDBOSet(empIds);
			empEduDetailsMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpId()));
		}

		if(!Utils.isNullOrEmpty(dto.getGuestAndContractDetails())) {
			 List<EmpGuestContractDetailsDTO> list =  employeeListTransaction.getEmpGuestContractDetailsDBOSet(empIds);
			 empGuestContractDetailsDBOMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpId()));
		}

		if(!Utils.isNullOrEmpty(dto.getPreviousExperienceFullTime())  || !Utils.isNullOrEmpty(dto.getPreviousExperiencePartTime()) || !Utils.isNullOrEmpty(dto.getWorkExperience())) {
			List<EmpWorkExperienceDTO> list = employeeListTransaction.getEmpWorkExperienceDBOSet(empIds);
			empWorkExperienceDBOMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpId()));
		}

		if(!Utils.isNullOrEmpty(dto.getEligibilityTest())) {
			List<EmpEligiblityTestDTO> list = employeeListTransaction.empEligibilityTestDBOSet(empIds);
			empEligibilityTestDBOMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpId()));
		}
		if(!Utils.isNullOrEmpty(dto.getMajorAchievements())) {
			List<EmpMajorAchievementsDTO> list = employeeListTransaction.empMajorAchievementsDBOSet(empIds);
			empMajorAchievementsDBOMap = list.stream().collect(Collectors.groupingBy(b -> b.getEmpId()));
		}
		if(!Utils.isNullOrEmpty(dto.getResignationDetails())) {
			List<EmpResignationDTO> list = employeeListTransaction.empresignationDBO(empIds);
			empResignationDBOMap = list.stream().collect(Collectors.toMap(s -> s.getEmpId(), s -> s));
		}
		if(!Utils.isNullOrEmpty(dto.getPayScaleDetails())) {
			List<EmpPayScaleDetailsDTO> list = employeeListTransaction.empPayScaleDetails(empIds);
			if(!Utils.isNullOrEmpty(list)) {
				list.forEach( data -> {
					empPayScaleDetailsIds.add(data.getId());
				});
			}
			empPayScaleDetailsDBOMap = list.stream().collect(Collectors.toMap(s -> s.getEmpId(), s -> s));

			empComponentDetailsMap = employeeListTransaction.empPayScaleDetailsComponentsDBO(empPayScaleDetailsIds).stream()
					.collect(Collectors.groupingBy(s -> s.getPayScaleDetailsId()));
		}
		if(!Utils.isNullOrEmpty(dto.getFathersName()) || !Utils.isNullOrEmpty(dto.getMothersName()) || !Utils.isNullOrEmpty(dto.getFamilyDependentDetails())) {
			List<EmpFamilyDetailsAddtnlDTO> list = employeeListTransaction.empFamilyDetailsAddtnlDBOS(empPersonalDataIds);
			empFamilyDetailsMap = list.stream().collect(Collectors.groupingBy(b ->Integer.parseInt(b.getEmpApplnPersonalDataId())));
		}
//		System.out.println("sub query Excution End"+LocalDateTime.now());



		if(!Utils.isNullOrEmpty(dto.getPayScaleDetails())) {
			empComponentsMap = employeeListTransaction.getpayComponents(empIds).stream().collect(Collectors.toMap(s ->Integer.parseInt(s.get("displayOrder").toString()), s -> s.get("name").toString()));
		}
		if(!Utils.isNullOrEmpty(dto.getQualificationDetails())) {
			maxQulification =  employeeListTransaction.getEmpMaxQualification(empIds);
			qualificationLevelMap = employeeListTransaction.getQualificationDetails().stream().collect(Collectors.toMap(s -> s.getQualificationLevelDegreeOrder(), s -> s));
			qualificationsCountsMap = employeeListTransaction.getEduCounts(empIds).stream().collect(Collectors.toMap(s -> Integer.parseInt(s.get("level").toString()), s -> Integer.parseInt(s.get("maxCount").toString())));
		}
		if(!Utils.isNullOrEmpty(dto.getWorkExperience())) {
			List<Integer> empWorkExpCount = employeeListTransaction.getWorkExp(empIds); 
			if(!Utils.isNullOrEmpty(empWorkExpCount)) {
				Object p = Collections.max(empWorkExpCount);
				workCount = Integer.parseInt(p.toString());
			}
		}

		if(!Utils.isNullOrEmpty(dto.getFamilyDependentDetails())) {
			List<Integer> empFamilyCountList = employeeListTransaction.getFamilyCount(empIds);
			if(!Utils.isNullOrEmpty(empFamilyCountList)) {
				Object p = Collections.max(empFamilyCountList);
				familyDependentCount = Integer.parseInt(p.toString());
			}
		}
		if(!Utils.isNullOrEmpty(dto.getGuestAndContractDetails())) {
			List<Integer> list = employeeListTransaction.getGuestCounts(empIds).stream().map(s ->Integer.parseInt(s.get("countLevel").toString())).collect(Collectors.toList());
			guestCount = Collections.max(list);
		}

//		System.out.println("Excel heading  Creation Start"+LocalDateTime.now());
		if(!Utils.isNullOrEmpty(dto.getEmpNo())) {
			rowhead.createCell(headingIndex).setCellValue("Employee ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmpName())) {
			rowhead.createCell(headingIndex).setCellValue("Employee Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmpGender())) {
			rowhead.createCell(headingIndex).setCellValue("Gender");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmpDob())) {
			rowhead.createCell(headingIndex).setCellValue("Date of Birth");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMobileNo())) {
			rowhead.createCell(headingIndex).setCellValue("Mobile No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDateOfJoin())) {
			rowhead.createCell(headingIndex).setCellValue("Date Of Join");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDateOfRetirement())) {
			rowhead.createCell(headingIndex).setCellValue("Date Of Retirement");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getContractStartDate())) {
			rowhead.createCell(headingIndex).setCellValue("Contract Start Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getContractEndDate())) {
			rowhead.createCell(headingIndex).setCellValue("Contract End Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getContractRemarks())) {
			rowhead.createCell(headingIndex).setCellValue("Contract Remarks");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getActive())) {
			rowhead.createCell(headingIndex).setCellValue("Active");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPersonalMailID())) {
			rowhead.createCell(headingIndex).setCellValue("Personal Mail ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getOfficeMailID())) {
			rowhead.createCell(headingIndex).setCellValue("Office Mail ID");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getNationality())) {
			rowhead.createCell(headingIndex).setCellValue("Nationality");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMaritalStatus())) {
			rowhead.createCell(headingIndex).setCellValue("Marital Status");
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
		if(!Utils.isNullOrEmpty(dto.getDisabilityType())) {
			rowhead.createCell(headingIndex).setCellValue("Disability Type");
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
		if(!Utils.isNullOrEmpty(dto.getEmpCategory())) {
			rowhead.createCell(headingIndex).setCellValue("Employee Category");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmpJobcategory())) {
			rowhead.createCell(headingIndex).setCellValue("Job Category");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSubjectOrCategory())) {
			rowhead.createCell(headingIndex).setCellValue("Subject/Category");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSpecialisation())) {
			rowhead.createCell(headingIndex).setCellValue("Specialisation");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmployeeGroup())) {
			rowhead.createCell(headingIndex).setCellValue("Employee Group");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmpDesignation())) {
			rowhead.createCell(headingIndex).setCellValue("Designation");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDesignationforStaffAlbum())) {
			rowhead.createCell(headingIndex).setCellValue("Designation For Staff Album");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTitle())) {
			rowhead.createCell(headingIndex).setCellValue("Title");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmpDepartment())) {
			rowhead.createCell(headingIndex).setCellValue("Department");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmpCampus())) {
			rowhead.createCell(headingIndex).setCellValue("Campus");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDeputedDepartmentAndCampus())) {
			rowhead.createCell(headingIndex).setCellValue("Deputed Department And Campus");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTitleInDeputedDepartment())) {
			rowhead.createCell(headingIndex).setCellValue("Title In Deputed Department");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSeatingLocation())) {
			rowhead.createCell(headingIndex).setCellValue("Seating Location");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getExtensionNo())) {
			rowhead.createCell(headingIndex).setCellValue("Extension No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTelephone())) {
			rowhead.createCell(headingIndex).setCellValue("Telephone");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getDeputationStartDate())) {
			rowhead.createCell(headingIndex).setCellValue("Deputation Start Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getApplicationNo())) {
			rowhead.createCell(headingIndex).setCellValue("Application No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getLetterDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Appointment Letter Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Appointment Letter Ref No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Appointment Letter Extended Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Appointment Letter Extended Ref No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Regular Appointment Letter Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Regular Appointment Letter Ref No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Confirmation Letter Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Confirmation Letter Ref No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPfAccountNo())) {
			rowhead.createCell(headingIndex).setCellValue("PF Account No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPfDate())) {
			rowhead.createCell(headingIndex).setCellValue("PF Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getUanNo())) {
			rowhead.createCell(headingIndex).setCellValue("UAN No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPfNomineeDetails())) {
			rowhead.createCell(headingIndex).setCellValue("PF Nominee Details");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getLicGratuityNo())) {
			rowhead.createCell(headingIndex).setCellValue("LIC Gratuity No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getLicGratuityDate())) {
			rowhead.createCell(headingIndex).setCellValue("LIC Gratuity Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getGratuityNomineeDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Gratuity Nominee Details");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getBankAccountNo())) {
			rowhead.createCell(headingIndex).setCellValue("Bank Account No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getIfscCode())) {
			rowhead.createCell(headingIndex).setCellValue("IFSC Code");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getSmartCardNo())) {
			rowhead.createCell(headingIndex).setCellValue("Smart Card No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPanNo())) {
			rowhead.createCell(headingIndex).setCellValue("Pan No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getAadharNo())) {
			rowhead.createCell(headingIndex).setCellValue("Aadhar No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getEmergencyContactDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Emergency Contact Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Emergency Contact Address");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Emergency Contact Relationship");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Emergency Contact Mobile No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Emergency Contact Home Telephone");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Emergency Contact Work Telephone");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPassportDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Passport No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Passport Issued Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Passport Status");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Passport Expiry Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Passport Issued Place ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Passport Comments");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getVisaDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Visa No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Visa Issued Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Visa Status");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Visa Expiry Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Visa Comments");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFrroDetails())) {
			rowhead.createCell(headingIndex).setCellValue("FRRO No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("FRRO Issued Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("FRRO Status");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("FRRO Expiry Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("FRRO Comments");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPayScaleDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Pay Scale Effective Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Pay Scale Comments");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Pay Scale Type");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Pay Scale");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Non Scale Pay/Amount per Pay Scale Type");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Gross Pay");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			//			empComponentsMap.forEach((order,name) -> {
			for(String name :  empComponentsMap.values()) {
				rowhead.createCell(headingIndex).setCellValue(name);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

			}	
			//			});
		}
		if(!Utils.isNullOrEmpty(dto.getCanDisplayInWebsite())) {
			rowhead.createCell(headingIndex).setCellValue("Can Display In Website");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTimeZone())) {
			rowhead.createCell(headingIndex).setCellValue("Time Zone");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getHasVacation())) {
			rowhead.createCell(headingIndex).setCellValue("Has Vacation");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getHasVacationTimeZone())) {
			rowhead.createCell(headingIndex).setCellValue("Has Vacation Zone");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getVacationTimeZone())) {
			rowhead.createCell(headingIndex).setCellValue("Vacation Time Zone");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getHasHolidayTimeZone())) {
			rowhead.createCell(headingIndex).setCellValue("Has Holiday Time Zone");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getHolidayTimezone())) {
			rowhead.createCell(headingIndex).setCellValue(" Holiday Time Zone");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getRoasterAllotmentApplicable())) {
			rowhead.createCell(headingIndex).setCellValue("Roaster Allotment Applicable");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getApproverDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Leave Approver");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Leave Authoriser");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Appraisal 1st Level Review");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Appraisal 2nd Level Review");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Work Diary Approver");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getLeaveCategory())) {
			rowhead.createCell(headingIndex).setCellValue("Leave Category");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getGuestAndContractDetails())) {
			for(int i =1 ; i<=guestCount;i++) {
				rowhead.createCell(headingIndex).setCellValue("Guest/Contract Campus & Department"+" "+i);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Guest Tutoring Semester"+" "+i);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Guest weekly working hours"+" "+i);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Guest Referred by"+" "+i);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Contract Start Date"+" "+i);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Contract End Date"+" "+i);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Contract Letter No"+" "+i);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;

				rowhead.createCell(headingIndex).setCellValue("Guest/Contract Remarks"+" "+i);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
			}
		}
		if(!Utils.isNullOrEmpty(dto.getPreviousExperienceFullTime())) {
			rowhead.createCell(headingIndex).setCellValue("Previous Experience Full Time");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getPreviousExperiencePartTime())) {
			rowhead.createCell(headingIndex).setCellValue("Previous Experience Part Time");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getRecognisedExperience())) {
			rowhead.createCell(headingIndex).setCellValue("Recognised Experience ");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getExperienceInCU())) {
			rowhead.createCell(headingIndex).setCellValue("Experience in CU");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTotalCurrentExperience())) {
			rowhead.createCell(headingIndex).setCellValue("Total Current Experience");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getHighestQualification())) {
			rowhead.createCell(headingIndex).setCellValue("Highest Qualification");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getHighestQualificationForStaffAlbum())) {
			rowhead.createCell(headingIndex).setCellValue("Highest Qualification For Staff Album");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getQualificationDetails())) {
			for(Integer qualification: qualificationLevelMap.keySet()) {
				if(!Utils.isNullOrEmpty(qualification)  && qualification <= maxQulification && qualificationsCountsMap.containsKey(qualification)) {
					for(int i = 1; i<=qualificationsCountsMap.get(qualification); i++) {
						ErpQualificationLevelDBO erpQualificationLevelDBO = qualificationLevelMap.get(qualification);
						qualificationOrders.add(qualification);

						rowhead.createCell(headingIndex).setCellValue("Qualification Level "+erpQualificationLevelDBO.getQualificationLevelName()+" "+ (i > 1 ? i:""));
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;

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

						rowhead.createCell(headingIndex).setCellValue(erpQualificationLevelDBO.getQualificationLevelName()+" "+(i > 1 ? i:"")+" Status");
						rowhead.getCell(headingIndex).setCellStyle(style);
						headingIndex++;
					}
				}
			}
		}
		if(!Utils.isNullOrEmpty(dto.getEligibilityTest())) {
			rowhead.createCell(headingIndex).setCellValue("Eligibility Test");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getWorkExperience())) {
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

				rowhead.createCell(headingIndex).setCellValue("Work Experience "+j);
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

				rowhead.createCell(headingIndex).setCellValue("Recognised Experience "+j);
				rowhead.getCell(headingIndex).setCellStyle(style);
				headingIndex++;
			}
		}
		if(!Utils.isNullOrEmpty(dto.getMajorAchievements())) {
			rowhead.createCell(headingIndex).setCellValue("Major Achievements");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFourWheelerNo())) {
			rowhead.createCell(headingIndex).setCellValue("FourWheeler No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getTwoWheelerNo())) {
			rowhead.createCell(headingIndex).setCellValue("TwoWheeler No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFathersName())) {
			rowhead.createCell(headingIndex).setCellValue("Father Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getMothersName())) {
			rowhead.createCell(headingIndex).setCellValue("Mother Name");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFamilyDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Family Details");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getFamilyDependentDetails())) {
			for(int j=1; j<=familyDependentCount; j++) {
				if(!Utils.isNullOrEmpty(dto.getMothersName())) {
					rowhead.createCell(headingIndex).setCellValue("Dependent "+j);
					rowhead.getCell(headingIndex).setCellStyle(style);
					headingIndex++;
				}
			}
		}
		if(!Utils.isNullOrEmpty(dto.getResignationDetails())) {
			rowhead.createCell(headingIndex).setCellValue("Date of Resignation");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Date of Leaving");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Resignation Approval Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Relieving Order Date");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Reason for Leaving");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;

			rowhead.createCell(headingIndex).setCellValue("Resignation Recommendation");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getOrcidNo())) {
			rowhead.createCell(headingIndex).setCellValue("Orcid No");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
		if(!Utils.isNullOrEmpty(dto.getScopusId())) {
			rowhead.createCell(headingIndex).setCellValue("Scopus Id");
			rowhead.getCell(headingIndex).setCellStyle(style);
			headingIndex++;
		}
//		System.out.println("Excel heading  Creation End"+LocalDateTime.now());

//		System.out.println("Java  Creation Start"+LocalDateTime.now());
		for(EmpDTO dbo : dbos) {
			//		dbos.forEach(dbo -> {			int dataIndexs =  0 ;

			EmpJobDetailsDTO jobDetails = null;
			if(!Utils.isNullOrEmpty(empJobDetailsMap) && empJobDetailsMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {
				jobDetails = empJobDetailsMap.get(Integer.parseInt(dbo.getEmpId()));
			}

			EmpPersonalDataDTO empPersonalDataDTO = null;
			if(!Utils.isNullOrEmpty(empPersonalDataMap) && empPersonalDataMap.containsKey(dbo.getEmpPersonalDataId())) {
				empPersonalDataDTO = empPersonalDataMap.get(dbo.getEmpPersonalDataId());
			}

			EmpAddtnlPersonalDataDTO empAddtnlPersonalDataDTO = null;
			if(!Utils.isNullOrEmpty(empAddtnlPersonalDataMap) && empAddtnlPersonalDataMap.containsKey(dbo.getEmpPersonalDataId())) {
				empAddtnlPersonalDataDTO = empAddtnlPersonalDataMap.get(dbo.getEmpPersonalDataId());
			}


			rowhead = sheet.createRow(rowCount);
			if(!Utils.isNullOrEmpty(dto.getEmpNo())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpNo())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmpNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmpName())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpName())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmpName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmpGender())) {
				if(!Utils.isNullOrEmpty(dbo.getErpGender())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getErpGender().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmpDob())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpDOB())) {
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(dbo.getEmpDOB())? Utils.convertLocalDateToStringDate(dbo.getEmpDOB()):"");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMobileNo())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpMobile())) {
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(dbo.getCountryCode())? dbo.getCountryCode() : "" + " " + dbo.getEmpMobile());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDateOfJoin())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpDOJ())) {
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(dbo.getEmpDOJ())? Utils.convertLocalDateToStringDate(dbo.getEmpDOJ()):"");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDateOfRetirement())) {
				if(!Utils.isNullOrEmpty(jobDetails)  &&  !Utils.isNullOrEmpty(jobDetails.getReportingDate())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getReportingDate());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getContractStartDate())) {
				if(!Utils.isNullOrEmpty(jobDetails) && !Utils.isNullOrEmpty(jobDetails.getContractStartDate())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getContractStartDate());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getContractEndDate())) {
				if(!Utils.isNullOrEmpty(jobDetails) && !Utils.isNullOrEmpty(jobDetails.getContractEndDate())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getContractEndDate());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getContractRemarks())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getContractRemarks())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getContractRemarks());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getActive())) {
				if(!Utils.isNullOrEmpty(dbo.getRecordStatus())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getRecordStatus() == 'A' ? "Active":"Inactive");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalMailID())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpPersonalEmail())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmpPersonalEmail());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getOfficeMailID())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpUniversityMail())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmpUniversityMail());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getNationality())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getErpCountry())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getErpCountry().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getMaritalStatus())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getErpMaritalStatus())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getErpMaritalStatus().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getReligion())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getErpReligion())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getErpReligion().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getReservationCategory())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getErpReservationCategory())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getErpReservationCategory().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getBloodGroup())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getErpBloodGroup())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getErpBloodGroup().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDifferentlyAbled())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getIsDifferentlyAbled())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getIsDifferentlyAbled() ? "Yes":"No");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDisabilityType())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getErpDifferentlyAbled())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getErpDifferentlyAbled().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddress1())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getCurrentAddressLine1())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getCurrentAddressLine1());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddress2())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getCurrentAddressLine2())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getCurrentAddressLine2());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressCountry())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getCurrentCountry())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getCurrentCountry().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressState())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO)) {
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(empPersonalDataDTO.getCurrentState()) ?
							empPersonalDataDTO.getCurrentState().getLabel():empPersonalDataDTO.getCurrentStateOthers());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressCity())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO)) {
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(empPersonalDataDTO.getCurrentCity()) ?
							empPersonalDataDTO.getCurrentCity().getLabel() : empPersonalDataDTO.getCurrentCityOthers());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getCurrentAddressPinCode())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getCurrentPincode())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getCurrentPincode());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddress1())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getPermanentAddressLine1())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getPermanentAddressLine1());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddress2())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getPermanentAddressLine2())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getPermanentAddressLine2());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressCountry())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getPermanentCountry())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getPermanentCountry().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressState())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO)) {
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(empPersonalDataDTO.getPermanentState()) ?
							empPersonalDataDTO.getPermanentState().getLabel() : empPersonalDataDTO.getPermanentStateOthers());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressCity())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO)) {
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(empPersonalDataDTO.getPermanentCity()) ?
							empPersonalDataDTO.getPermanentCity().getLabel() : empPersonalDataDTO.getPermanentCityOthers());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPermanentAddressPinCode())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getPermanentPincode())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getPermanentPincode());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmpCategory())) {
				if(!Utils.isNullOrEmpty(dbo.getEmployeeCategory())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmployeeCategory().label);
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmpJobcategory())) {
				if(!Utils.isNullOrEmpty(dbo.getJobCategory())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getJobCategory().label);
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSubjectOrCategory())) {
				if(!Utils.isNullOrEmpty(dbo.getSubjectCategory())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getSubjectCategory().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSpecialisation())) {
				if(!Utils.isNullOrEmpty(dbo.getSubjectCategorySpecializationName())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getSubjectCategorySpecializationName().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmployeeGroup())) {
				if(!Utils.isNullOrEmpty(dbo.getEmployeeGroup())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmployeeGroup().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmpDesignation())) {
				if(!Utils.isNullOrEmpty(dbo.getEmployeeDesignation())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmployeeDesignation().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDesignationforStaffAlbum())) {
				if(!Utils.isNullOrEmpty(dbo.getEmployeeDesignationForStaffAlbum())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmployeeDesignationForStaffAlbum().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getTitle())) {
				if(!Utils.isNullOrEmpty(dbo.getEmployeeTitle())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmployeeTitle().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmpDepartment())) {
				if(!Utils.isNullOrEmpty(dbo.getEmployeeDepartment())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmployeeDepartment().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getEmpCampus())) {
				if(!Utils.isNullOrEmpty(dbo.getEmployeeCampus())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmployeeCampus().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDeputedDepartmentAndCampus())) {
				if(!Utils.isNullOrEmpty(empDeputationErpCampusDepartmentMap)  &&  !Utils.isNullOrEmpty(dbo.getEmployeeDeputedDepartment()) 
						&&  !Utils.isNullOrEmpty(dbo.getEmployeeDeputedDepartment().getValue()) &&  empDeputationErpCampusDepartmentMap.containsKey(Integer.parseInt(dbo.getEmployeeDeputedDepartment().getValue()))) {
					ErpCampusDepartmentMappingDTO erpCampusDepartmentMappingDTO = empDeputationErpCampusDepartmentMap.get(Integer.parseInt(dbo.getEmployeeDeputedDepartment().getValue()));
					rowhead.createCell(dataIndexs).setCellValue(erpCampusDepartmentMappingDTO.getErpDepartmentSelect().getLabel() +"("+erpCampusDepartmentMappingDTO.getErpCampus().getLabel()+")");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getTitleInDeputedDepartment())) {
				if(!Utils.isNullOrEmpty(dbo.getEmployeeDeputedDepartmentTitle())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmployeeDeputedDepartmentTitle().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSeatingLocation())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpRoom())) {
					rowhead.createCell(dataIndexs).setCellValue(
							(!Utils.isNullOrEmpty(dbo.getEmpBlock().getLabel())?dbo.getEmpBlock().getLabel():"")+" "
									+ (!Utils.isNullOrEmpty(dbo.getEmpFloor().getLabel())?dbo.getEmpFloor().getLabel()+"Floor":"")+" "
									+ (!Utils.isNullOrEmpty(dbo.getEmpRoom().getLabel())?dbo.getEmpRoom().getLabel():"") +" "
									+ (!Utils.isNullOrEmpty(dbo.getCabinNo())?dbo.getCabinNo():""));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getExtensionNo())) {
				if(!Utils.isNullOrEmpty(dbo.getTelephoneExtension())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getTelephoneExtension());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getTelephone())) {
				if(!Utils.isNullOrEmpty(dbo.getTelephoneNumber())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getTelephoneNumber());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getDeputationStartDate())) {
				if(!Utils.isNullOrEmpty(dbo.getDeputationStartDate())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getDeputationStartDate());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getApplicationNo())) {
				if(!Utils.isNullOrEmpty(empAppNo)  && !Utils.isNullOrEmpty(dbo.getApplnEntriesId()) &&  empAppNo.containsKey(Integer.parseInt(dbo.getApplnEntriesId()))) {
					rowhead.createCell(dataIndexs).setCellValue(empAppNo.get(Integer.parseInt(dbo.getApplnEntriesId())));
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getLetterDetails())) {
				if(!Utils.isNullOrEmpty(empLetterDetailsMap) && empLetterDetailsMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {
					//				if(!Utils.isNullOrEmpty(dbo.getEmpEmployeeLetterDetailsDBOSet())) {

					//for(EmpEmployeeLetterDetailsDBO letterDetails :dbo.getEmpEmployeeLetterDetailsDBOSet()) {
					//dbo.getEmpEmployeeLetterDetailsDBOSet().forEach(letterDetails -> {


					//					Map<String,EmpEmployeeLetterDetailsDBO> mapLetterDetails = dbo.getEmpEmployeeLetterDetailsDBOSet().stream().filter(s -> s.recordStatus == 'A' && 
					Map<String,EmpEmployeeLetterDetailsDTO> mapLetterDetails = empLetterDetailsMap.get(Integer.parseInt(dbo.getEmpId())).stream().collect(Collectors.toMap(s -> s.getLetterType(), s -> s,(existingValue, newValue) -> existingValue));




					if(mapLetterDetails.containsKey("APPOINTMENT_LETTER")) {
						EmpEmployeeLetterDetailsDTO letter = mapLetterDetails.get("APPOINTMENT_LETTER");
						if(!Utils.isNullOrEmpty(letter.getLetterDate()) && letter.getLetterType().equalsIgnoreCase("APPOINTMENT_LETTER")) {
							rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(letter.getLetterDate()));
						}
						dataIndexs++;

						if(!Utils.isNullOrEmpty(letter.getLetterRefNo()) && letter.getLetterType().equalsIgnoreCase("APPOINTMENT_LETTER")) {
							rowhead.createCell(dataIndexs).setCellValue(letter.getLetterRefNo());
						}
						dataIndexs++;
					} else {
						dataIndexs += 2;
					}

					if(mapLetterDetails.containsKey("APPOINTMENT_LETTER_EXTENDED")) {
						EmpEmployeeLetterDetailsDTO letter = mapLetterDetails.get("APPOINTMENT_LETTER_EXTENDED");
						if(!Utils.isNullOrEmpty(letter.getLetterDate()) && letter.getLetterType().equalsIgnoreCase("APPOINTMENT_LETTER_EXTENDED")) {
							rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(letter.getLetterDate()));
						}
						dataIndexs++;

						if(!Utils.isNullOrEmpty(letter.getLetterRefNo()) && letter.getLetterType().equalsIgnoreCase("APPOINTMENT_LETTER_EXTENDED")) {
							rowhead.createCell(dataIndexs).setCellValue(letter.getLetterRefNo());
						}
						dataIndexs++;
					} else {
						dataIndexs += 2;
					}

					if(mapLetterDetails.containsKey("REGULAR_APPOINTMENT_LETTER")) {
						EmpEmployeeLetterDetailsDTO letter = mapLetterDetails.get("REGULAR_APPOINTMENT_LETTER");
						if(!Utils.isNullOrEmpty(letter.getLetterDate()) && letter.getLetterType().equalsIgnoreCase("REGULAR_APPOINTMENT_LETTER")) {
							rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(letter.getLetterDate()));
						}
						dataIndexs++;

						if(!Utils.isNullOrEmpty(letter.getLetterRefNo()) && letter.getLetterType().equalsIgnoreCase("REGULAR_APPOINTMENT_LETTER")) {
							rowhead.createCell(dataIndexs).setCellValue(letter.getLetterRefNo());
						}
						dataIndexs++;
					} else {
						dataIndexs += 2;
					}

					if(mapLetterDetails.containsKey("CONFIRMATION_LETTER")) {
						EmpEmployeeLetterDetailsDTO letter = mapLetterDetails.get("CONFIRMATION_LETTER");
						if(!Utils.isNullOrEmpty(letter.getLetterDate()) && letter.getLetterType().equalsIgnoreCase("CONFIRMATION_LETTER")) {
							rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(letter.getLetterDate()));
						}
						dataIndexs++;

						if(!Utils.isNullOrEmpty(letter.getLetterRefNo()) && letter.getLetterType().equalsIgnoreCase("CONFIRMATION_LETTER")) {
							rowhead.createCell(dataIndexs).setCellValue(letter.getLetterRefNo());
						}
						dataIndexs++;
					} else {
						dataIndexs += 2;
					}

					//});
					//}
				} else {
					dataIndexs +=8;
				}
			}
			if(!Utils.isNullOrEmpty(dto.getPfAccountNo())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getPfAccountNo())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getPfAccountNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPfDate())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getPfDate())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getPfDate());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getUanNo())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getUanNo())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getUanNo());
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getPfNomineeDetails())) {
				//				if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO().getEmpPfGratuityNomineesDBOS())) {
				if(!Utils.isNullOrEmpty(empPfGratuityNomineesDetailsMap) && empPfGratuityNomineesDetailsMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {



					//					Set<EmpPfGratuityNomineesDBO> pfNominees = dbo.getEmpJobDetailsDBO().getEmpPfGratuityNomineesDBOS();
					List<EmpPfGratuityNomineesDTO> pfNominees = empPfGratuityNomineesDetailsMap.get(Integer.parseInt(dbo.getEmpId()));


					for(EmpPfGratuityNomineesDTO details : pfNominees) {
						if(!Utils.isNullOrEmpty(details.getIsPf()) && details.getIsPf()) {
							//							if(details.isPf) {
							if(rowhead.getCell(dataIndexs) == null) {
								String cellData =(!Utils.isNullOrEmpty(details.getNominee())?details.getNominee():"")
										+ (!Utils.isNullOrEmpty(details.getNomineeAddress())?details.getNomineeAddress()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getNomineeRelationship())?details.getNomineeRelationship()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getNomineeDob()) ?details.getNomineeDob()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getSharePercentage())?details.getSharePercentage()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getUnder18GuardName())?details.getUnder18GuardName()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getUnder18GuardianAddress())?details.getUnder18GuardianAddress()+ "\n":"");

								rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(cellData)?cellData:"");
							} else {
								String existingValue = rowhead.getCell(dataIndexs).getStringCellValue();
								existingValue += (!Utils.isNullOrEmpty(details.getNominee())?"\n"+details.getNominee():"")
										+ (!Utils.isNullOrEmpty(details.getNomineeAddress())?details.getNomineeAddress()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getNomineeRelationship())?details.getNomineeRelationship()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getNomineeDob()) ?details.getNomineeDob()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getSharePercentage())?details.getSharePercentage()	+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getUnder18GuardName())?details.getUnder18GuardName()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getUnder18GuardianAddress())?details.getUnder18GuardianAddress()+"\n":"");
								rowhead.getCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(existingValue)?existingValue:"");
							}
							//								Cell cell = rowhead.getCell(dataIndexs);
							//								CellStyle style1 = workbook.createCellStyle();
							//								style1.setWrapText(true);
							//								cell.setCellStyle(style1);
							//							}
						}
					}
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getLicGratuityNo())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getGratuityNo())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getGratuityNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getLicGratuityDate())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(dto.getLicGratuityDate()) && !Utils.isNullOrEmpty(jobDetails.getGratuityDate())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getGratuityDate());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getGratuityNomineeDetails())) {

				if(!Utils.isNullOrEmpty(empPfGratuityNomineesDetailsMap) && empPfGratuityNomineesDetailsMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {
					//				if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO().getEmpPfGratuityNomineesDBOS())) {

					//					Set<EmpPfGratuityNomineesDBO> pfNominees = dbo.getEmpJobDetailsDBO().getEmpPfGratuityNomineesDBOS();
					List<EmpPfGratuityNomineesDTO> pfNominees = empPfGratuityNomineesDetailsMap.get(Integer.parseInt(dbo.getEmpId()));

					for(EmpPfGratuityNomineesDTO details : pfNominees) {
						if(!Utils.isNullOrEmpty(details.getIsGratuity()) && details.getIsGratuity()) {
							//							if(details.isGratuity) {
							if(rowhead.getCell(dataIndexs) == null) {
								String cellData = (!Utils.isNullOrEmpty(details.getNominee())?details.getNominee():"") 
										+  (!Utils.isNullOrEmpty(details.getNomineeAddress())?details.getNomineeAddress()+"\n":"") 
										+ (!Utils.isNullOrEmpty(details.getNomineeRelationship())?details.getNomineeRelationship()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getNomineeDob()) ?details.getNomineeDob()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getSharePercentage())?details.getSharePercentage()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getUnder18GuardName())?details.getUnder18GuardName()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getUnder18GuardianAddress())?details.getUnder18GuardianAddress()+"\n":"");

								rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(cellData)?cellData:"");
							} else {
								String existingValue = rowhead.getCell(dataIndexs).getStringCellValue();
								existingValue +=(!Utils.isNullOrEmpty(details.getNominee())? "\n"+details.getNominee():"") 
										+(!Utils.isNullOrEmpty(details.getNomineeAddress())?details.getNomineeAddress()+"\n":"") 
										+ (!Utils.isNullOrEmpty(details.getNomineeRelationship())?details.getNomineeRelationship()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getNomineeDob()) ?details.getNomineeDob()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getSharePercentage())?details.getSharePercentage()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getUnder18GuardName())?details.getUnder18GuardName()+"\n":"")
										+ (!Utils.isNullOrEmpty(details.getUnder18GuardianAddress())?details.getUnder18GuardianAddress()+"\n":"");
								rowhead.getCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(existingValue)?existingValue:"");
							}
							//								Cell cell = rowhead.getCell(dataIndexs);
							//								CellStyle style1 = workbook.createCellStyle();
							//								style1.setWrapText(true);
							//								cell.setCellStyle(style1);
							//							}
						}
					}
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getBankAccountNo())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getSibAccountBank())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getSibAccountBank());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getIfscCode())) {
				if(!Utils.isNullOrEmpty(jobDetails) && !Utils.isNullOrEmpty(jobDetails.getIfscCode())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getIfscCode());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getSmartCardNo())) {
				if(!Utils.isNullOrEmpty(jobDetails) && !Utils.isNullOrEmpty(jobDetails.getSmartCardNo())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getSmartCardNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPanNo())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO)&& !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getPanNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getPanNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getAadharNo())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getAadharNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getAadharNo());
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getEmergencyContactDetails())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getEmergencyContactName())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getEmergencyContactName());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getEmergencyContactAddress())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getEmergencyContactAddress());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getEmergencyContactRelatonship())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getEmergencyContactRelatonship());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getEmergencyMobileNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getEmergencyMobileNo());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getEmergencyContactHome())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getEmergencyContactHome());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getEmergencyContactWork())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getEmergencyContactWork());
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getPassportDetails())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getPassportNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getPassportNo());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getPassportIssuedDate())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getPassportIssuedDate());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getPassportStatus())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getPassportStatus());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getPassportDateOfExpiry())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getPassportDateOfExpiry());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getPassportIssuedPlace())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getPassportIssuedPlace());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getPassportComments())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getPassportComments());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getVisaDetails())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getVisaNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getVisaNo());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getVisaIssuedDate())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getVisaIssuedDate());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getVisaStatus())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getVisaStatus());
				}
				dataIndexs++;

				if( !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getVisaDateOfExpiry())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getVisaDateOfExpiry());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getVisaComments())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getVisaComments());
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getFrroDetails())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getFrroNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getFrroNo());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getFrroIssuedDate())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getFrroIssuedDate());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getFrroStatus())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getFrroStatus());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getFrroDateOfExpiry())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getFrroDateOfExpiry());
				}
				dataIndexs++;

				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getFrroComments())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getFrroComments());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPayScaleDetails())) {
				//				if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO()) && !Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO().getEmpPayScaleDetailsId())) {
				if(!Utils.isNullOrEmpty(empPayScaleDetailsDBOMap) && empPayScaleDetailsDBOMap.containsKey(Integer.parseInt(dbo.getEmpId())) && !Utils.isNullOrEmpty(empPayScaleDetailsDBOMap.get(Integer.parseInt(dbo.getEmpId())))) {

					EmpPayScaleDetailsDTO empPayScaleDetailsDTO = empPayScaleDetailsDBOMap.get(Integer.parseInt(dbo.getEmpId()));

					if(!Utils.isNullOrEmpty(empPayScaleDetailsDTO.getPayScaleEffectiveDate())) {
						rowhead.createCell(dataIndexs).setCellValue(Utils.convertLocalDateToStringDate(empPayScaleDetailsDTO.getPayScaleEffectiveDate()));
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empPayScaleDetailsDTO.getPayScaleComments())) {
						rowhead.createCell(dataIndexs).setCellValue(empPayScaleDetailsDTO.getPayScaleComments());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empPayScaleDetailsDTO.getPayScaleType())) {
						rowhead.createCell(dataIndexs).setCellValue(empPayScaleDetailsDTO.getPayScaleType());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empPayScaleDetailsDTO.getPayScale())) {
						rowhead.createCell(dataIndexs).setCellValue(empPayScaleDetailsDTO.getPayScale());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empPayScaleDetailsDTO.getWageRatePerType())) {
						rowhead.createCell(dataIndexs).setCellValue(String.valueOf(empPayScaleDetailsDTO.getWageRatePerType()));
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empPayScaleDetailsDTO.getGrossPay())) {
						rowhead.createCell(dataIndexs).setCellValue(String.valueOf(empPayScaleDetailsDTO.getGrossPay()));
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empComponentDetailsMap) && empComponentDetailsMap.containsKey(empPayScaleDetailsDTO.getId())) {

						//						Map<Integer, BigDecimal> empComponentDetailsMap = dbo.getEmpJobDetailsDBO().getEmpPayScaleDetailsId().getEmpPayScaleDetailsComponentsDBOs().stream().filter(s -> s.recordStatus == 'A')
						//								.collect(Collectors.toMap(s -> s.getEmpPayScaleComponentsDBO().getSalaryComponentDisplayOrder(), s -> s.getEmpSalaryComponentValue()));

						Map<Integer, BigDecimal> empComponentDetailsValueMap = empComponentDetailsMap.get(empPayScaleDetailsDTO.getId()).stream()
								.collect(Collectors.toMap(s -> s.getEmpPayScaleComponentsDisplayOrder(),s -> s.getEmpSalaryComponentValue()));


						for(Integer key:empComponentsMap.keySet()) {
							if(empComponentDetailsValueMap.containsKey(key)) {
								rowhead.createCell(dataIndexs).setCellValue(String.valueOf(empComponentDetailsValueMap.get(key)));
							}
							dataIndexs++;

						}
					} else {
						dataIndexs += empComponentsMap.size();
					}
				} else {
					dataIndexs += (6 + empComponentsMap.size());
				}
			}
			if(!Utils.isNullOrEmpty(dto.getCanDisplayInWebsite())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getIsDisplayWebsite())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getIsDisplayWebsite());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getTimeZone())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpTimeZone())) {
					rowhead.createCell(dataIndexs).setCellValue(dbo.getEmpTimeZone().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getHasVacation())) {
				if(!Utils.isNullOrEmpty(jobDetails) && !Utils.isNullOrEmpty(jobDetails.getIsVacationApplicable())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getIsVacationApplicable());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getHasVacationTimeZone())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getIsVacationTimeZoneApplicable())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getIsVacationTimeZoneApplicable());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getVacationTimeZone())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getTimeZoneName())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getTimeZoneName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getHasHolidayTimeZone())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getIsHolidayTimeZoneApplicable())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getIsHolidayTimeZoneApplicable());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getHolidayTimezone())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getHolidayTimeZoneName())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getHolidayTimeZoneName());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getRoasterAllotmentApplicable())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getIsDutyRosterApplicable())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getIsDutyRosterApplicable());
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getApproverDetails())) {
				//				if(!Utils.isNullOrEmpty(dbo.getEmpApproversDBO())) {
				if(!Utils.isNullOrEmpty(empApproversMap) &&  empApproversMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {	

					EmpApproversDetailsDTO empApproversDTO =   empApproversMap.get(Integer.parseInt(dbo.getEmpId()));
					//					EmpApproversDBO empApproversDBO =   dbo.getEmpApproversDBO();

					if(!Utils.isNullOrEmpty(empApproversDTO.getLeaveApprover())) {
						rowhead.createCell(dataIndexs).setCellValue(empApproversDTO.getLeaveApprover().getLabel());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empApproversDTO.getLeaveAuthorizer())) {
						rowhead.createCell(dataIndexs).setCellValue(empApproversDTO.getLeaveAuthorizer().getLabel());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empApproversDTO.getLevelOneAppraiser())) {
						rowhead.createCell(dataIndexs).setCellValue(empApproversDTO.getLevelOneAppraiser().getLabel());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empApproversDTO.getLevelTwoAppraiser())) {
						rowhead.createCell(dataIndexs).setCellValue(empApproversDTO.getLevelTwoAppraiser().getLabel());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empApproversDTO.getWorkDairyApprover())) {
						rowhead.createCell(dataIndexs).setCellValue(empApproversDTO.getWorkDairyApprover().getLabel());
					}
					dataIndexs++;

				} else {
					dataIndexs += 5;
				}
			}
			if(!Utils.isNullOrEmpty(dto.getLeaveCategory())) {
				if(!Utils.isNullOrEmpty(jobDetails)  && !Utils.isNullOrEmpty(jobDetails.getEmpLeaveCategoryAllotmentName())) {
					rowhead.createCell(dataIndexs).setCellValue(jobDetails.getEmpLeaveCategoryAllotmentName());
				}
				dataIndexs++;
			}


			if(!Utils.isNullOrEmpty(dto.getGuestAndContractDetails())) {
				Integer count =0;
				//				if(!Utils.isNullOrEmpty(dbo.getEmpGuestContractDetailsDBOSet())) {
				if(!Utils.isNullOrEmpty(empGuestContractDetailsDBOMap) &&  empGuestContractDetailsDBOMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {

					//					EmpGuestContractDetailsDBO details = dbo.getEmpGuestContractDetailsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A' && s.isCurrent).findFirst().orElse(null);	

					List<EmpGuestContractDetailsDTO> guestdetails =  empGuestContractDetailsDBOMap.get(Integer.parseInt(dbo.getEmpId()));

					if(!Utils.isNullOrEmpty(guestdetails)) {
						
						for(EmpGuestContractDetailsDTO details:guestdetails) {
							if(!Utils.isNullOrEmpty(details.getErpCampusDepartment())) {
								rowhead.createCell(dataIndexs).setCellValue(details.getErpCampusDepartment());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(details.getSemester())) {
								rowhead.createCell(dataIndexs).setCellValue(details.getSemester());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(details.getWorkHourPerWeek())) {
								rowhead.createCell(dataIndexs).setCellValue(String.valueOf(details.getWorkHourPerWeek()));
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(details.getGuestReferredBy())) {
								rowhead.createCell(dataIndexs).setCellValue(details.getGuestReferredBy());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(details.getContractStartDate())) {
								rowhead.createCell(dataIndexs).setCellValue(details.getContractStartDate());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(details.getContractEndDate())) {
								rowhead.createCell(dataIndexs).setCellValue(details.getContractEndDate());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(details.getContractEmpLetterNo())) {
								rowhead.createCell(dataIndexs).setCellValue(details.getContractEmpLetterNo());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(details.getGuestContractRemarks())) {
								rowhead.createCell(dataIndexs).setCellValue(details.getGuestContractRemarks());
							}
							dataIndexs++;
							count++;
						}
					} else {
						dataIndexs += 8;
						count++;
					}
				}
				while(count<guestCount) {
					dataIndexs += 8;
					count++;
				}
			}
			if(!Utils.isNullOrEmpty(dto.getPreviousExperienceFullTime())) {
				AtomicInteger year = new AtomicInteger(0);
				AtomicInteger month = new AtomicInteger(0);
				//				Set<EmpWorkExperienceDBO> empWorkExpSet = dbo.getEmpWorkExperienceDBOSet().stream().filter(s -> s.getRecordStatus() == 'A' &&  ( s.getIsPartTime() == null || !s.getIsPartTime()))
				//						.collect(Collectors.toSet());

				List<EmpWorkExperienceDTO> empWorkExpSet = null;
				if(!Utils.isNullOrEmpty(empWorkExperienceDBOMap) && empWorkExperienceDBOMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {
					empWorkExpSet = empWorkExperienceDBOMap.get(Integer.parseInt(dbo.getEmpId())).stream().filter(s ->  s.getIsPartTime() == null || !s.getIsPartTime())
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
					//					System.out.println(year);
					//					System.out.println(month);
					//					System.out.println(month.get() / 12);
					//					System.out.println(month.get() % 12);
					year.addAndGet(month.get() / 12);
					month.set(month.get() % 12);
					//					System.out.println(year);
					//					System.out.println(month);
					//					
					//					System.out.println();
					rowhead.createCell(dataIndexs).setCellValue(year+" years "+ month+" months");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getPreviousExperiencePartTime())) {
				AtomicInteger year = new AtomicInteger(0);
				AtomicInteger month = new AtomicInteger(0);
				//				Set<EmpWorkExperienceDBO> empWorkExpSet = dbo.getEmpWorkExperienceDBOSet().stream().filter(s -> s.getRecordStatus() == 'A' && !Utils.isNullOrEmpty(s.getIsPartTime()) && s.getIsPartTime())
				//						.collect(Collectors.toSet());

				List<EmpWorkExperienceDTO> empWorkExpSet = null;
				if(!Utils.isNullOrEmpty(empWorkExperienceDBOMap) && empWorkExperienceDBOMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {
					empWorkExpSet = empWorkExperienceDBOMap.get(Integer.parseInt(dbo.getEmpId())).stream().filter(s -> !Utils.isNullOrEmpty(s.getIsPartTime()) && s.getIsPartTime())
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

			int totalYears = 0;
			int totalMonths = 0;
			if(!Utils.isNullOrEmpty(dto.getRecognisedExperience())) {
				if(!Utils.isNullOrEmpty(jobDetails)) {
					rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(jobDetails.getRecognisedExpYears())?
							jobDetails.getRecognisedExpYears():0)+" Years "
							+(!Utils.isNullOrEmpty(jobDetails.getRecognisedExpMonths())?
									jobDetails.getRecognisedExpMonths() :"0")+" Months");

					totalYears += !Utils.isNullOrEmpty(jobDetails.getRecognisedExpYears()) ? jobDetails.getRecognisedExpYears() : 0 ;
					totalMonths += !Utils.isNullOrEmpty(jobDetails.getRecognisedExpMonths()) ? jobDetails.getRecognisedExpMonths() : 0 ;
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getExperienceInCU())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpDOJ())) {

					Period period = Period.between(dbo.getEmpDOJ(), LocalDate.now());
					int years = period.getYears();
					int months = period.getMonths();
					rowhead.createCell(dataIndexs).setCellValue(years+" years "+ months+" months");

					totalYears += !Utils.isNullOrEmpty(years) ? years : 0 ;
					totalMonths += !Utils.isNullOrEmpty(months) ? months : 0 ;
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getTotalCurrentExperience())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpDOJ())) {
					totalYears += totalMonths / 12;
					totalMonths = totalMonths % 12;
					rowhead.createCell(dataIndexs).setCellValue(totalYears+" years "+ totalMonths+" months");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getHighestQualification())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getErpQualificationLevel())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getErpQualificationLevel().getLabel());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getHighestQualificationForStaffAlbum())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getHighestQualificationAlbum())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getHighestQualificationAlbum());
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getQualificationDetails())) {
				Map<Integer,List<EmpEducationalDetailsDTO>> empEducationalDetailsDBOMap = new HashMap<Integer,List<EmpEducationalDetailsDTO>>();

				//				if(!Utils.isNullOrEmpty(dbo.getEmpEducationalDetailsDBOSet())) {
				if(!Utils.isNullOrEmpty(empEduDetailsMap) && empEduDetailsMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {	


					//										empEducationalDetailsDBOMap = dbo.getEmpEducationalDetailsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A' &&  !Utils.isNullOrEmpty(s.getErpQualificationLevelDBO()))
					//												.collect(Collectors.toMap(s -> s.getErpQualificationLevelDBO().getQualificationLevelDegreeOrder(), s -> s  ));


					//					dbo.getEmpEducationalDetailsDBOSet().forEach(data -> {
					empEduDetailsMap.get(Integer.parseInt(dbo.getEmpId())).forEach(data -> {

						//						if(!empEducationalDetailsDBOMap.containsKey(data.getErpQualificationLevelDBO().getId())) {
						//							List<EmpEducationalDetailsDBO> list = new ArrayList<EmpEducationalDetailsDBO>();
						//							list.add(data);
						//							empEducationalDetailsDBOMap.put(data.getErpQualificationLevelDBO().getId(),list );
						//						} else {
						//							List<EmpEducationalDetailsDBO> list = empEducationalDetailsDBOMap.get(data.getErpQualificationLevelDBO().getId());
						//							list.add(data);
						//							empEducationalDetailsDBOMap.replace(data.getErpQualificationLevelDBO().getId(), list);	
						//						}


						if(!empEducationalDetailsDBOMap.containsKey(Integer.parseInt(data.getQualificationLevelId()))) {
							List<EmpEducationalDetailsDTO> list = new ArrayList<EmpEducationalDetailsDTO>();
							list.add(data);
							empEducationalDetailsDBOMap.put(Integer.parseInt(data.getQualificationLevelId()),list );
						} else {
							List<EmpEducationalDetailsDTO> list = empEducationalDetailsDBOMap.get(Integer.parseInt(data.getQualificationLevelId()));
							list.add(data);
							empEducationalDetailsDBOMap.replace(Integer.parseInt(data.getQualificationLevelId()), list);	
						}



					});
				}

				for( Integer order  :qualificationOrders) {
					int count = 0 ;
					int maxCount = qualificationsCountsMap.get(order);
					if(empEducationalDetailsDBOMap.containsKey(order)) {

						List<EmpEducationalDetailsDTO> empEducationalDetailsDBOList = empEducationalDetailsDBOMap.get(order);
						for (EmpEducationalDetailsDTO empEducationalDetailsDBO:empEducationalDetailsDBOList) {
							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getQualificationLevelName())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getQualificationLevelName());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getCourse())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getCourse());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getSpecialization())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getSpecialization());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getYearOfCompletion())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getYearOfCompletion().getLabel());
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

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getCountry())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getCountry().getLabel());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getState())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getState().getLabel());
							}
							dataIndexs++;

							if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.getCurrentStatus())) {
								rowhead.createCell(dataIndexs).setCellValue(empEducationalDetailsDBO.getCurrentStatus());
							}
							dataIndexs++;
							count++;
						}
					} else {
						dataIndexs += 10;
						count++;
					}
					while(count < maxCount) {
						dataIndexs += 10;
						count++;
					}
				}
			}
			if(!Utils.isNullOrEmpty(dto.getEligibilityTest())) {
				//				if(!Utils.isNullOrEmpty(dbo.getEmpEligibilityTestDBOSet())) {
				if(!Utils.isNullOrEmpty(empEligibilityTestDBOMap) && empEligibilityTestDBOMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {	

					//					Set<EmpEligibilityTestDBO> Eligibilitytest = dbo.getEmpEligibilityTestDBOSet();
					List<EmpEligiblityTestDTO> Eligibilitytest = empEligibilityTestDBOMap.get(Integer.parseInt(dbo.getEmpId()));

					for(EmpEligiblityTestDTO test :Eligibilitytest) {
						//						if(test.getRecordStatus() == 'A') {
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
						//							Cell cell = rowhead.getCell(dataIndexs);
						//							CellStyle style1 = workbook.createCellStyle();
						//							style1.setWrapText(true);
						//							cell.setCellStyle(style1);
						//						}
					}
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getWorkExperience())) {
				int count = 0;
				//				if(!Utils.isNullOrEmpty(dbo.getEmpWorkExperienceDBOSet())) {
				if(!Utils.isNullOrEmpty(empWorkExperienceDBOMap) &&  empWorkExperienceDBOMap.containsKey(Integer.parseInt(dbo.getEmpId())) && !Utils.isNullOrEmpty(empWorkExperienceDBOMap.get(Integer.parseInt(dbo.getEmpId())))) {	


					//					for(EmpWorkExperienceDBO work:  dbo.getEmpWorkExperienceDBOSet()) {
					for(EmpWorkExperienceDTO work:  empWorkExperienceDBOMap.get(Integer.parseInt(dbo.getEmpId()))) {
						//						if(work.getRecordStatus() == 'A') {

						if(!Utils.isNullOrEmpty(work.getWorkExperienceType())) {
							rowhead.createCell(dataIndexs).setCellValue(work.getWorkExperienceType().getLabel());
						}
						dataIndexs++;

						if(!Utils.isNullOrEmpty(work.getFunctionalArea())) {
							rowhead.createCell(dataIndexs).setCellValue(work.getFunctionalArea().getLabel());
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
							rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(work.getYears())?
									work.getYears():0)+" Years "
									+(!Utils.isNullOrEmpty(work.getMonths())?
											work.getMonths() :"0")+" Months");
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

						if(!Utils.isNullOrEmpty(work.getIsRecognised())) {
							rowhead.createCell(dataIndexs).setCellValue(work.getIsRecognised());
						}
						dataIndexs++;
						count++;
						//						}
					}
				}
				while(count < workCount) {
					dataIndexs += 9;
					count++;
				}
			}
			if(!Utils.isNullOrEmpty(dto.getMajorAchievements())) {
				//				if(!Utils.isNullOrEmpty(dbo.getEmpMajorAchievementsDBOSet())) {
				if(!Utils.isNullOrEmpty(empMajorAchievementsDBOMap) && empMajorAchievementsDBOMap.containsKey(Integer.parseInt(dbo.getEmpId()))) {	

					//					for(EmpMajorAchievementsDBO achievements :dbo.getEmpMajorAchievementsDBOSet()) {
					for(EmpMajorAchievementsDTO achievements :empMajorAchievementsDBOMap.get(Integer.parseInt(dbo.getEmpId()))) {	

						//						if(achievements.recordStatus == 'A') {
						if(rowhead.getCell(dataIndexs) == null) {
							rowhead.createCell(dataIndexs).setCellValue(achievements.getName());
						} else {
							String existingValue = rowhead.getCell(dataIndexs).getStringCellValue();
							existingValue += "\n"+achievements.getName();
							rowhead.getCell(dataIndexs).setCellValue(existingValue);
						}
						//							Cell cell = rowhead.getCell(dataIndexs);
						//							CellStyle style1 = workbook.createCellStyle();
						//							style1.setWrapText(true);
						//							cell.setCellStyle(style1);
						//						}
					}
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFourWheelerNo())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getFourWheelerNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getFourWheelerNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getTwoWheelerNo())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getTwoWheelerNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getTwoWheelerNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFathersName())) {
				if(!Utils.isNullOrEmpty(empFamilyDetailsMap) && !Utils.isNullOrEmpty(empPersonalDataDTO) && empFamilyDetailsMap.containsKey(empPersonalDataDTO.getId())) {
					EmpFamilyDetailsAddtnlDTO data = empFamilyDetailsMap.get(empPersonalDataDTO.getId()).stream()
							.filter(s -> s.getRelationship().equalsIgnoreCase("FATHER")).findFirst().orElse(null);
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(data)?  data.getDependentName():"");
				}
				dataIndexs++;
			}

			if(!Utils.isNullOrEmpty(dto.getMothersName())) {
				if(!Utils.isNullOrEmpty(empFamilyDetailsMap) && !Utils.isNullOrEmpty(empPersonalDataDTO) && empFamilyDetailsMap.containsKey(empPersonalDataDTO.getId())) {
					EmpFamilyDetailsAddtnlDTO data = empFamilyDetailsMap.get(empPersonalDataDTO.getId()).stream()
							.filter(s -> !Utils.isNullOrEmpty(s.getRelationship()) &&   s.getRelationship().equalsIgnoreCase("MOTHER")).findFirst().orElse(null);
					rowhead.createCell(dataIndexs).setCellValue(!Utils.isNullOrEmpty(data)?  data.getDependentName():"");
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFamilyDetails())) {
				if(!Utils.isNullOrEmpty(empAddtnlPersonalDataDTO) && !Utils.isNullOrEmpty(empAddtnlPersonalDataDTO.getFamilyBackgroundBrief())) {
					rowhead.createCell(dataIndexs).setCellValue(empAddtnlPersonalDataDTO.getFamilyBackgroundBrief());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getFamilyDependentDetails())) {
				int count=0;
				if(!Utils.isNullOrEmpty(empFamilyDetailsMap) && !Utils.isNullOrEmpty(empPersonalDataDTO) && empFamilyDetailsMap.containsKey(empPersonalDataDTO.getId())) {
					Set<EmpFamilyDetailsAddtnlDTO> data = empFamilyDetailsMap.get(empPersonalDataDTO.getId()).stream()
							.filter(s -> !Utils.isNullOrEmpty(s.getRelationship()) &&  !s.getRelationship().equalsIgnoreCase("FATHER") && !s.getRelationship().equalsIgnoreCase("MOTHER")).collect(Collectors.toSet());
					for(EmpFamilyDetailsAddtnlDTO value: data) {
						rowhead.createCell(dataIndexs).setCellValue((!Utils.isNullOrEmpty(value.getDependentName())?  value.getDependentName()+"\n":"")
								+(!Utils.isNullOrEmpty(value.getRelationship())?  value.getRelationship()+"\n":"")
								+(!Utils.isNullOrEmpty(value.getDependentDob())?  value.getDependentDob()+"\n":"")
								+(!Utils.isNullOrEmpty(value.getDependentQualification())?  value.getDependentQualification()+"\n":"")
								+(!Utils.isNullOrEmpty(value.getDependentProfession())?  value.getDependentProfession()+"\n":"")
								);
						//						Cell cell = rowhead.getCell(dataIndexs);
						//						CellStyle style1 = workbook.createCellStyle();
						//						style1.setWrapText(true);
						//						cell.setCellStyle(style1);
						dataIndexs++;
						count++;
					}
				}
				while(count < familyDependentCount) {
					dataIndexs++;
					count++;
				}
			}

			if(!Utils.isNullOrEmpty(dto.getResignationDetails())) {
				//				if(!Utils.isNullOrEmpty(dbo.getEmpresignationDBO())) {
				if(!Utils.isNullOrEmpty(empResignationDBOMap) && empResignationDBOMap.containsKey(Integer.parseInt(dbo.getEmpId())) && !Utils.isNullOrEmpty(empResignationDBOMap.get(Integer.parseInt(dbo.getEmpId())))) {	

					EmpResignationDTO empResignationDBO = empResignationDBOMap.get(Integer.parseInt(dbo.getEmpId()));
					//					EmpResignationDBO empResignationDBO = dbo.getEmpresignationDBO();

					if(!Utils.isNullOrEmpty(empResignationDBO.getSubmissionDate())) {
						rowhead.createCell(dataIndexs).setCellValue(empResignationDBO.getSubmissionDate());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empResignationDBO.getDateOfLeaving())) {
						rowhead.createCell(dataIndexs).setCellValue(empResignationDBO.getDateOfLeaving());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empResignationDBO.getHodRecomendedRelievingDate())) {
						rowhead.createCell(dataIndexs).setCellValue(empResignationDBO.getHodRecomendedRelievingDate());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empResignationDBO.getRelieavingOrderDate())) {
						rowhead.createCell(dataIndexs).setCellValue(empResignationDBO.getRelieavingOrderDate());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empResignationDBO.getReasonForLeaving())) {
						rowhead.createCell(dataIndexs).setCellValue(empResignationDBO.getReasonForLeaving());
					}
					dataIndexs++;

					if(!Utils.isNullOrEmpty(empResignationDBO.getPoRemarks())) {
						rowhead.createCell(dataIndexs).setCellValue(empResignationDBO.getPoRemarks());
					}
					dataIndexs++;
				} else {
					dataIndexs += 6;
				}
			}
			if(!Utils.isNullOrEmpty(dto.getOrcidNo())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getOrcidNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getOrcidNo());
				}
				dataIndexs++;
			}
			if(!Utils.isNullOrEmpty(dto.getScopusId())) {
				if(!Utils.isNullOrEmpty(empPersonalDataDTO) && !Utils.isNullOrEmpty(empPersonalDataDTO.getScopusNo())) {
					rowhead.createCell(dataIndexs).setCellValue(empPersonalDataDTO.getScopusNo());
				}
				dataIndexs++;
			}

			rowCount++;
			//		});
		}
//		System.out.println("Java  Creation End -"+LocalDateTime.now());

//		System.out.println("Sending Data  Start -"+LocalDateTime.now());
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
//		System.out.println("Sending Data  End"+LocalDateTime.now());
		return Mono.just(outputResource);

	}
}
