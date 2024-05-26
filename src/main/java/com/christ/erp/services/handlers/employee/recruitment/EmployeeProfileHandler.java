package com.christ.erp.services.handlers.employee.recruitment;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.*;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.common.*;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.*;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleLevelDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDetailsDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.*;
import com.christ.erp.services.dto.employee.*;
import com.christ.erp.services.dto.employee.common.*;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllocationDTO;
import com.christ.erp.services.dto.employee.profile.*;
import com.christ.erp.services.dto.employee.recruitment.*;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleDetailsDTO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDetailsDTO;
import com.christ.erp.services.dto.support.settings.SupportCategoryDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.handlers.employee.common.CommonEmployeeHandler;
import com.christ.erp.services.helpers.employee.recruitment.EmployeeProfileHelper;
import com.christ.erp.services.helpers.employee.recruitment.FinalInterviewCommentsHelper;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.common.CommonEmployeeTransaction;
import com.christ.erp.services.transactions.employee.recruitment.EmployeeProfileTransaction;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.Tika;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.persistence.Tuple;
import java.io.File;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
@SuppressWarnings("unchecked")

@Service
public class EmployeeProfileHandler {
	//	private static volatile EmployeeProfileHandler employeeProfileHandler = null;
	//
	//	public static EmployeeProfileHandler getInstance() {
	//		if (employeeProfileHandler == null) {
	//			employeeProfileHandler = new EmployeeProfileHandler();
	//		}
	//		return employeeProfileHandler;
	//	}

	//EmployeeProfileTransaction employeeProfileTransaction  = EmployeeProfileTransaction.getInstance();
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();
	FinalInterviewCommentsHandler finalInterviewCommentsHandler = FinalInterviewCommentsHandler.getInstance();
	FinalInterviewCommentsHelper finalInterviewCommentsHelper = FinalInterviewCommentsHelper.getInstance();

	@Autowired
	EmployeeProfileTransaction employeeProfileTransaction;
	@Autowired
	CommonEmployeeHandler commonEmployeeHandler;
	@Autowired
	CommonEmployeeTransaction commonEmployeeTransaction;
	@Autowired
	FinalInterviewCommentsHandler finalInterviewCommentsHandler1;
	@Autowired
	FinalInterviewCommentsHelper finalInterviewCommentsHelper1;
	@Autowired
	EmployeeProfileHelper employeeProfileHelper;
	@Autowired
	AWSS3FileStorageServiceHandler aWSS3FileStorageServiceHandler;

	public List<EmpDTO> searchEmployees(EmpDTO empDTO) {
		List<EmpDTO> employeeList = new ArrayList<>();
		try {
			List<Tuple> list = employeeProfileTransaction.searchEmployees(empDTO);
			for (Tuple tuple : list) {
				EmpDTO emp = new EmpDTO();
				if (!Utils.isNullOrEmpty(tuple.get("campus_name"))) {
					emp.campusName = tuple.get("campus_name").toString().trim();
				}
				if (!Utils.isNullOrEmpty(tuple.get("emp_designation_name"))) {
					emp.empDesignation = tuple.get("emp_designation_name").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("emp_id"))) {
					emp.empId = tuple.get("emp_id").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("emp_no"))) {
					emp.empNo = tuple.get("emp_no").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("emp_name"))) {
					emp.empName = tuple.get("emp_name").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("emp_no"))) {
					emp.empNo = tuple.get("emp_no").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("emp_university_email"))) {
					emp.empUniversityMail = tuple.get("emp_university_email").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("profile_photo_url"))) {
					emp.employeePhoto = tuple.get("profile_photo_url").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("department_name"))) {
					emp.departmentName = tuple.get("department_name").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("employee_job_name"))) {
					emp.jobCategoryName = tuple.get("employee_job_name").toString();
				}
				if (!Utils.isNullOrEmpty(tuple.get("record_status"))) {
					if ((char) tuple.get("record_status") == 'A') {
						emp.isActive = true;
					} else {
						emp.isActive = false;
					}
					if (!Utils.isNullOrEmpty(tuple.get("employee_job_name"))) {
						emp.jobCategoryName = tuple.get("employee_job_name").toString();
					}
				}
				employeeList.add(emp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeList;
	}

	@SuppressWarnings("unused")
	public EmployeeProfileDTO getEmployeeProfileDetails(String empId) {
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		try {
			String personalDataId = "";
			Tuple employee = employeeProfileTransaction.getEmployee(empId);
			if (!Utils.isNullOrEmpty(employee)) {
				if (!Utils.isNullOrEmpty(employee.get("emp_personal_data_id"))) {
					personalDataId = employee.get("emp_personal_data_id").toString();
				}
				String jobDetailsId = "";
				int fullTimeYears = 0;
				int fullTimeMonths = 0;
				int partTimeYears = 0;
				int partTimeMonths = 0;
				int recognizedExperienceYears = 0;
				int recognizedExperienceMonth = 0;
				JobInformationDTO jobInformationDTO = new JobInformationDTO();
				jobInformationDTO.jobInformation = new JobDetailsDTO();
				BasicInformationDTO basicInformationDTO = new BasicInformationDTO();
				EmployeeProfileAddressDTO currentAddress = new EmployeeProfileAddressDTO();
				EmployeeProfileAddressDTO permanentAddress = new EmployeeProfileAddressDTO();
				GovernmentDocumentDetailsDTO passportDetails = new GovernmentDocumentDetailsDTO();
				GovernmentDocumentDetailsDTO visaDetails = new GovernmentDocumentDetailsDTO();
				GovernmentDocumentDetailsDTO frroDetails = new GovernmentDocumentDetailsDTO();
				PfAndGratutyDetailsDTO pfNomineeDetailsDTO = new PfAndGratutyDetailsDTO();
				PfAndGratutyDetailsDTO gratuityNomineeDetailsDTO = new PfAndGratutyDetailsDTO();
				EmpDTO empDTO = new EmpDTO();
				EmployeeProfileJobDetailsDTO employeeProfileJobDetailsDTO = new EmployeeProfileJobDetailsDTO();
				ErpRoomsDTO erpRoomEmpMappingDTO = new ErpRoomsDTO();
				EmpResignationDTO empResignationDTO = new EmpResignationDTO();
				WorkTimeDetailsDTO workTimeDetails = new WorkTimeDetailsDTO();
				EmpApproversDetailsDTO empApproversDetailsDTO = new EmpApproversDetailsDTO();
				EmployeeProfileFamilyOrDependencyInformationDTO familyOrDependencyInformationDTO = new EmployeeProfileFamilyOrDependencyInformationDTO();
				List<EmployeeProfileAdditionalInformation> additionalInfoList = new ArrayList<>();
				Map<String, String> emergencyContact = new HashMap<>();
				familyOrDependencyInformationDTO.dependentList = new ArrayList<>();
				familyOrDependencyInformationDTO.childrenList = new ArrayList<>();
				HashSet<String> relation = new HashSet<>();
				EmpGuestContractDetailsDTO empGuestContractDetailsDTO = new EmpGuestContractDetailsDTO();
				familyOrDependencyInformationDTO.father = new EmpFamilyDetailsAddtnlDTO();
				familyOrDependencyInformationDTO.mother = new EmpFamilyDetailsAddtnlDTO();
				familyOrDependencyInformationDTO.spouse = new EmpFamilyDetailsAddtnlDTO();
				familyOrDependencyInformationDTO.childrenList = new ArrayList<>();
				familyOrDependencyInformationDTO.dependentList = new ArrayList<>();
				//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				if (!Utils.isNullOrEmpty(employee.get("four_wheeler_no"))) {
					basicInformationDTO.fourWheelerNo = employee.get("four_wheeler_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("two_wheeler_no"))) {
					basicInformationDTO.tworWheelerNo = employee.get("two_wheeler_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("four_wheeler_document_url"))) {
					basicInformationDTO.fourWheelerDocumentUrl = employee.get("four_wheeler_document_url").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("two_wheeler_document_url"))) {
					basicInformationDTO.tworWheelerDocumentUrl = employee.get("two_wheeler_document_url").toString();
				}
				basicInformationDTO.disabilityType = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_differently_abled_id"))) {
					basicInformationDTO.disabilityType.value = employee.get("erp_differently_abled_id").toString();
					basicInformationDTO.disabilityType.label = String.valueOf(employee.get("differently_abled_name"));
				}
				if (!Utils.isNullOrEmpty(employee.get("emp_name"))) {
					basicInformationDTO.employeeName = employee.get("emp_name").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("aadhar_no"))) {
					basicInformationDTO.adharNo = employee.get("aadhar_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("adhar_upload_url"))) {
					basicInformationDTO.setAdharUploadUrl(employee.get("adhar_upload_url").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("emp_id"))) {
					basicInformationDTO.employeeId = employee.get("emp_id").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("emp_no"))) {
					basicInformationDTO.empNo = employee.get("emp_no").toString();
				}
				basicInformationDTO.nationality = new CommonDTO();
				if (!Utils.isNullOrEmpty(employee.get("pd_country_id"))) {
					basicInformationDTO.nationality.value = employee.get("pd_country_id").toString();
					basicInformationDTO.nationality.label = String.valueOf(employee.get("pd_country_name"));
					basicInformationDTO.nationality.nationality = String.valueOf(employee.get("pd_nationality_name"));
				}
				basicInformationDTO.gender = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_gender_id"))) {
					basicInformationDTO.gender.value = employee.get("erp_gender_id").toString();
					basicInformationDTO.gender.label = String.valueOf(employee.get("gender_name"));
				}
				basicInformationDTO.maritalStatus = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_marital_status_id"))) {
					basicInformationDTO.maritalStatus.value = employee.get("erp_marital_status_id").toString();
					basicInformationDTO.maritalStatus.label = String.valueOf(employee.get("marital_status_name"));
				}
				if (!Utils.isNullOrEmpty(employee.get("dob"))) {
					basicInformationDTO.dateOfBirth = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("dob").toString()));
				}
				basicInformationDTO.bloodGroup = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_blood_group_id"))) {
					basicInformationDTO.bloodGroup.value = employee.get("erp_blood_group_id").toString();
					basicInformationDTO.bloodGroup.label = String.valueOf(employee.get("blood_group_name"));
				}
				basicInformationDTO.religion = new CommonDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_religion_id"))) {
					basicInformationDTO.religion.value = employee.get("erp_religion_id").toString();
					basicInformationDTO.religion.label = String.valueOf(employee.get("religion_name"));
					basicInformationDTO.religion.isMinority = Boolean.parseBoolean(String.valueOf(employee.get("is_minority")));
				}
				if (!Utils.isNullOrEmpty(employee.get("pan_no"))) {
					basicInformationDTO.panNo = employee.get("pan_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("pan_upload_url"))) {
					basicInformationDTO.setPanUploadUrl(employee.get("pan_upload_url").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("emp_university_email"))) {
					basicInformationDTO.universityEmailId = employee.get("emp_university_email").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("emp_personal_email"))) {
					basicInformationDTO.personalEmailId = employee.get("emp_personal_email").toString();
				}
				basicInformationDTO.reservationCategory = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_reservation_category_id"))) {
					basicInformationDTO.reservationCategory.value = employee.get("erp_reservation_category_id").toString();
					basicInformationDTO.reservationCategory.label = String.valueOf(employee.get("reservation_category_name"));
				}
				if (!Utils.isNullOrEmpty(employee.get("is_differently_abled"))) {
					basicInformationDTO.isDifferentiallyAbled = employee.get("is_differently_abled").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("is_minority"))) {
					basicInformationDTO.isMinority = Boolean.parseBoolean(employee.get("is_minority").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("mobile_no"))) {
					basicInformationDTO.mobileNo = employee.get("mobile_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("moble_no_country_code"))) {
					basicInformationDTO.countryCode = new CommonDTO();
					basicInformationDTO.countryCode.label = employee.get("moble_no_country_code").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("smart_card_no"))) {
					basicInformationDTO.smartCardNo = employee.get("smart_card_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("branch_ifsc_code"))) {
					basicInformationDTO.ifscCode = employee.get("branch_ifsc_code").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("sib_account_bank"))) {
					basicInformationDTO.bankAccountNo = employee.get("sib_account_bank").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("profile_photo_url"))) {
					basicInformationDTO.employeePhoto = employee.get("profile_photo_url").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("orcid_no"))) {
					basicInformationDTO.orcidNo = employee.get("orcid_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("scopus_no"))) {
					basicInformationDTO.setScopusNo(String.valueOf(employee.get("scopus_no")));
				}
				if (!Utils.isNullOrEmpty(employee.get("vidwan_no"))) {
					basicInformationDTO.setVidwnNo(String.valueOf(employee.get("vidwan_no")));
				}
				employeeProfileDTO.personalDetails = new EmployeeProfilePersonalDetailsDTO();
				employeeProfileDTO.personalDetails.basicInformation = basicInformationDTO;
				if (!Utils.isNullOrEmpty(employee.get("current_address_line_1"))) {
					currentAddress.addressLineOne = employee.get("current_address_line_1").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("current_address_line_2"))) {
					currentAddress.addressLineTwo = employee.get("current_address_line_2").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("current_pincode"))) {
					currentAddress.pincode = employee.get("current_pincode").toString();
				}
				currentAddress.setDistrict(new SelectDTO());
				if (!Utils.isNullOrEmpty(employee.get("current_city_id")) && !Utils.isNullOrEmpty(employee.get("currentCityname"))) {
					currentAddress.getDistrict().setLabel(employee.get("currentCityname").toString());
					currentAddress.getDistrict().setValue(employee.get("current_city_id").toString());
				} else if (!Utils.isNullOrEmpty(employee.get("current_city_others")) && Utils.isNullOrEmpty(employee.get("current_city_id")) && Utils.isNullOrEmpty(employee.get("currentCityname"))) {
					currentAddress.getDistrict().setLabel(employee.get("current_city_others").toString());
				}
				currentAddress.state = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("es_current_id"))) {
					currentAddress.state.value = employee.get("es_current_id").toString();
					currentAddress.state.label = String.valueOf(employee.get("es_current_name"));
				}
				currentAddress.country = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("ec_current_id"))) {
					currentAddress.country.value = employee.get("ec_current_id").toString();
					currentAddress.country.label = String.valueOf(employee.get("ec_current_name"));

				}
				if (!Utils.isNullOrEmpty(employee.get("is_permanent_equals_current"))) {
					if (employee.get("is_permanent_equals_current").toString().equalsIgnoreCase("true")) {
						currentAddress.sameAsPermenent = true;
					} else {
						currentAddress.sameAsPermenent = false;
					}
				}
				employeeProfileDTO.personalDetails.currentAddress = currentAddress;
				if (!Utils.isNullOrEmpty(employee.get("permanent_address_line_1"))) {
					permanentAddress.addressLineOne = employee.get("permanent_address_line_1").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("permanent_address_line_2"))) {
					permanentAddress.addressLineTwo = employee.get("permanent_address_line_2").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("permanent_pincode"))) {
					permanentAddress.pincode = employee.get("permanent_pincode").toString();
				}
				permanentAddress.setDistrict(new SelectDTO());
				if (!Utils.isNullOrEmpty(employee.get("permanent_city_id")) && !Utils.isNullOrEmpty(employee.get("permenantCityname"))) {
					permanentAddress.getDistrict().setLabel(employee.get("permenantCityname").toString());
					permanentAddress.getDistrict().setValue(employee.get("permanent_city_id").toString());
				} else if (!Utils.isNullOrEmpty(employee.get("permanent_city_others"))) {
					permanentAddress.getDistrict().setLabel(employee.get("permanent_city_others").toString());
				}
				permanentAddress.state = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("es_permanent_id"))) {
					permanentAddress.state.value = employee.get("es_permanent_id").toString();
					permanentAddress.state.label = String.valueOf(employee.get("es_permanent_name"));
				}
				permanentAddress.country = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("ec_permanent_id"))) {
					permanentAddress.country.value = employee.get("ec_permanent_id").toString();
					permanentAddress.country.label = String.valueOf(employee.get("ec_permanent_name"));
				}
				employeeProfileDTO.personalDetails.permanentAddress = permanentAddress;
				if (!Utils.isNullOrEmpty(employee.get("passport_no"))) {
					passportDetails.no = employee.get("passport_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("passport_issued_place"))) {
					passportDetails.placeOfIssue = employee.get("passport_issued_place").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("passport_status"))) {
					passportDetails.status = employee.get("passport_status").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("passport_issued_date"))) {
					passportDetails.issuedDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("passport_issued_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("passport_date_of_expiry"))) {
					passportDetails.expiryDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("passport_date_of_expiry").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("passport_upload_url"))) {
					passportDetails.documentUrl = employee.get("passport_upload_url").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("passport_comments"))) {
					passportDetails.comments = employee.get("passport_comments").toString();
				}
				employeeProfileDTO.personalDetails.passportDetails = passportDetails;
				if (!Utils.isNullOrEmpty(employee.get("visa_no"))) {
					visaDetails.no = employee.get("visa_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("visa_status"))) {
					visaDetails.status = employee.get("visa_status").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("visa_issued_date"))) {
					visaDetails.issuedDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("visa_issued_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("visa_date_of_expiry"))) {
					visaDetails.expiryDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("visa_date_of_expiry").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("visa_upload_url"))) {
					passportDetails.documentUrl = employee.get("visa_upload_url").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("visa_comments"))) {
					visaDetails.comments = employee.get("visa_comments").toString();
				}
				employeeProfileDTO.personalDetails.visaAndFrroDetails = new HashMap<>();
				employeeProfileDTO.personalDetails.visaAndFrroDetails.put("visaDetails", visaDetails);
				if (!Utils.isNullOrEmpty(employee.get("frro_no"))) {
					frroDetails.no = employee.get("frro_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("frro_status"))) {
					frroDetails.status = employee.get("frro_status").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("frro_issued_date"))) {
					frroDetails.issuedDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("frro_issued_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("frro_date_of_expiry"))) {
					frroDetails.expiryDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("frro_date_of_expiry").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("frro_upload_url"))) {
					frroDetails.documentUrl = employee.get("frro_upload_url").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("frro_comments"))) {
					frroDetails.comments = employee.get("frro_comments").toString();
				}
				employeeProfileDTO.personalDetails.visaAndFrroDetails.put("frroDetails", frroDetails);
				if (!Utils.isNullOrEmpty(employee.get("emergency_contact_name"))) {
					emergencyContact.put("name", employee.get("emergency_contact_name").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("emergency_contact_address"))) {
					emergencyContact.put("address", employee.get("emergency_contact_address").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("emergency_contact_relatonship"))) {
					emergencyContact.put("relationship", employee.get("emergency_contact_relatonship").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("emergency_mobile_no"))) {
					emergencyContact.put("mobileNo", employee.get("emergency_mobile_no").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("emergency_contact_work"))) {
					emergencyContact.put("telephoneWork", employee.get("emergency_contact_work").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("emergency_contact_home"))) {
					emergencyContact.put("telephoneHome", employee.get("emergency_contact_home").toString());
				}
				if (!Utils.isNullOrEmpty(employee.get("family_background_brief"))) {
					familyOrDependencyInformationDTO.familyBackgroundBrief = employee.get("family_background_brief").toString();
				}
				employeeProfileDTO.personalDetails.emergencyContact = emergencyContact;
				if (!Utils.isNullOrEmpty(personalDataId)) {
					List<Tuple> list = employeeProfileTransaction.getEmployeeFamilyDetails(personalDataId);
					if (!Utils.isNullOrEmpty(list)) {
						for (Tuple tuple : list) {
							EmpFamilyDetailsAddtnlDTO familyDetails = new EmpFamilyDetailsAddtnlDTO();
							//		String relationship = tuple.get("relationship").toString();
							if (!Utils.isNullOrEmpty(tuple.get("emp_family_details_addtnl_id"))) {
								familyDetails.empFamilyDetailsAddtnlId = tuple.get("emp_family_details_addtnl_id").toString();
							}
							//						if(!Utils.isNullOrEmpty(tuple.get("relationship"))) {
							//							familyDetails.relationship =  tuple.get("relationship").toString();
							//						}
							if (!Utils.isNullOrEmpty(tuple.get("dependent_name"))) {
								familyDetails.dependentName = tuple.get("dependent_name").toString();
							}
							if (!Utils.isNullOrEmpty(tuple.get("dependent_dob"))) {
								familyDetails.dependentDob = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(tuple.get("dependent_dob").toString()));
							}
							if (!Utils.isNullOrEmpty(tuple.get("dependent_qualification"))) {
								familyDetails.dependentQualification = tuple.get("dependent_qualification").toString();
							}
							if (!Utils.isNullOrEmpty(tuple.get("dependent_profession"))) {
								familyDetails.dependentProfession = tuple.get("dependent_profession").toString();
							}
							if (!Utils.isNullOrEmpty(tuple.get("relationship"))) {
								String relationship = tuple.get("relationship").toString();
								familyDetails.relationship = tuple.get("relationship").toString();
								if (relation.contains(relationship)) {
									if (relationship.equalsIgnoreCase("CHILD")) {
										familyOrDependencyInformationDTO.childrenList.add(familyDetails);
									} else {
										familyOrDependencyInformationDTO.dependentList.add(familyDetails);
									}
								} else {
									if (relationship.equalsIgnoreCase("FATHER")) {
										familyOrDependencyInformationDTO.father = familyDetails;
									} else if (relationship.equalsIgnoreCase("MOTHER")) {
										familyOrDependencyInformationDTO.mother = familyDetails;
									} else if (relationship.equalsIgnoreCase("SPOUSE")) {
										familyOrDependencyInformationDTO.spouse = familyDetails;
									} else if (relationship.equalsIgnoreCase("CHILD")) {
										familyOrDependencyInformationDTO.childrenList.add(familyDetails);
									} else {
										familyOrDependencyInformationDTO.dependentList.add(familyDetails);
									}
									relation.add(relationship);
								}
							}
						}
					}
				}
				empDTO.employeeDesignationForStaffAlbum = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("sa_id"))) {
					empDTO.employeeDesignationForStaffAlbum.value = employee.get("sa_id").toString();
					empDTO.employeeDesignationForStaffAlbum.label = String.valueOf(employee.get("sa_name"));
				}
				employeeProfileDTO.personalDetails.familyOrDependencyInformation = familyOrDependencyInformationDTO;
				empDTO.employeeCategory = new EmployeeApplicationDTO();
				if (!Utils.isNullOrEmpty(employee.get("emp_employee_category_id"))) {
					empDTO.employeeCategory.value = employee.get("emp_employee_category_id").toString();
					empDTO.employeeCategory.label = String.valueOf(employee.get("employee_category_name"));
					empDTO.employeeCategory.isEmployeeCategoryAcademic = Boolean.parseBoolean(String.valueOf(employee.get("is_employee_category_academic")));
				}
				empDTO.jobCategory = new EmployeeApplicationDTO();
				if (!Utils.isNullOrEmpty(employee.get("emp_employee_job_category_id"))) {
					empDTO.jobCategory.value = employee.get("emp_employee_job_category_id").toString();
					empDTO.jobCategory.label = String.valueOf(employee.get("employee_job_name"));
					empDTO.jobCategory.jobCategoryCode = String.valueOf(employee.get("job_category_code"));
				}
				empDTO.employeeGroup = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("emp_employee_group_id"))) {
					empDTO.employeeGroup.value = employee.get("emp_employee_group_id").toString();
					empDTO.employeeGroup.label = String.valueOf(employee.get("employee_group_name"));
				}
				empDTO.employeeCampus = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_campus_id"))) {
					empDTO.employeeCampus = new LookupItemDTO();
					empDTO.employeeCampus.value = employee.get("erp_campus_id").toString();
					empDTO.employeeCampus.label = String.valueOf(employee.get("campus_name"));
				}
				empDTO.employeeDepartment = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_department_id"))) {
					empDTO.employeeDepartment.value = employee.get("erp_department_id").toString();
					empDTO.employeeDepartment.label = String.valueOf(employee.get("department_name"));
				}
				empDTO.employeeDesignation = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("emp_designation_id"))) {
					empDTO.employeeDesignation.value = employee.get("emp_designation_id").toString();
					empDTO.employeeDesignation.label = String.valueOf(employee.get("emp_designation_name"));
				}
				empDTO.employeeTitle = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("emp_title_id"))) {
					empDTO.employeeTitle.value = employee.get("emp_title_id").toString();
					empDTO.employeeTitle.label = String.valueOf(employee.get("title_name"));
				}
				empDTO.employeeDeputedDepartmentTitle = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("emp_title_id"))) {
					empDTO.employeeDeputedDepartmentTitle.value = String.valueOf(employee.get("emp_title_id"));
					empDTO.employeeDeputedDepartmentTitle.label = String.valueOf(employee.get("title_name"));
				}
				if (!Utils.isNullOrEmpty(employee.get("deputation_start_date"))) {
					empDTO.deputationStartDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("deputation_start_date").toString()));
				}
				empDTO.employeeDeputedDepartment = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_department_id"))) {
					empDTO.employeeDeputedDepartment.value = employee.get("erp_department_id").toString();
					empDTO.employeeDeputedDepartment.label = String.valueOf(employee.get("department_name"));
				}
				jobInformationDTO.jobInformation.subjectCategory = new LookupItemDTO();
				jobInformationDTO.jobInformation.subjectSpecialization = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("emp_appln_subject_category_id"))) {
					jobInformationDTO.jobInformation.subjectCategory.value = employee.get("emp_appln_subject_category_id").toString();
					jobInformationDTO.jobInformation.subjectCategory.label = String.valueOf(employee.get("subject_category_name"));
				}
				if (!Utils.isNullOrEmpty(employee.get("emp_appln_subject_category_specialization_id"))) {
					jobInformationDTO.jobInformation.subjectSpecialization.value = employee.get("emp_appln_subject_category_specialization_id").toString();
					jobInformationDTO.jobInformation.subjectSpecialization.label = String.valueOf(employee.get("subject_category_specialization_name"));
				}
				jobInformationDTO.jobInformation.empDTO = empDTO;
				jobInformationDTO.promotionDetails = new ArrayList<>();
				List<EmpEmployeeLetterDetailsDBO> employeeLetterDBOList = employeeProfileTransaction.getEmployeeLetterDetails(empId);
				if (!Utils.isNullOrEmpty(employeeLetterDBOList)) {
					for (EmpEmployeeLetterDetailsDBO empEmployeeLetterDetailsDBO : employeeLetterDBOList) {
						EmployeeProfileLetterDTO employeeLetter = new EmployeeProfileLetterDTO();
						String letterType = empEmployeeLetterDetailsDBO.letterType;
						if (!Utils.isNullOrEmpty(letterType)) {
							if (letterType.equalsIgnoreCase("APPOINTMENT_LETTER")) {
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.id)) {
									employeeProfileJobDetailsDTO.appointmentLetterId = empEmployeeLetterDetailsDBO.id.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterDate)) {
									employeeProfileJobDetailsDTO.appointmentLetterDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empEmployeeLetterDetailsDBO.letterDate.toString()));
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterRefNo)) {
									employeeProfileJobDetailsDTO.appointmentLetterRfNo = empEmployeeLetterDetailsDBO.letterRefNo.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterUrl)) {
									employeeProfileJobDetailsDTO.appointmentLetterDocumentUrl = empEmployeeLetterDetailsDBO.letterUrl;
								}
							} else if (letterType.equalsIgnoreCase("APPOINTMENT_LETTER_EXTENDED")) {
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.id)) {
									employeeProfileJobDetailsDTO.appointmentLetterExtendedId = empEmployeeLetterDetailsDBO.id.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterDate)) {
									employeeProfileJobDetailsDTO.appointmentLetterExtendedDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empEmployeeLetterDetailsDBO.letterDate.toString()));
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterRefNo)) {
									employeeProfileJobDetailsDTO.appointmentLetterExtendedRfNo = empEmployeeLetterDetailsDBO.letterRefNo.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterUrl)) {
									employeeProfileJobDetailsDTO.appointmentLetterExtendedDocumentUrl = empEmployeeLetterDetailsDBO.letterUrl;
								}
							} else if (letterType.equalsIgnoreCase("REGULAR_APPOINTMENT_LETTER")) {
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.id)) {
									employeeProfileJobDetailsDTO.regularAppointmentLetterId = empEmployeeLetterDetailsDBO.id.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterDate)) {
									employeeProfileJobDetailsDTO.regularAppointmentLetterDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empEmployeeLetterDetailsDBO.letterDate.toString()));
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterRefNo)) {
									employeeProfileJobDetailsDTO.regularAppointmentLetterRfNo = empEmployeeLetterDetailsDBO.letterRefNo.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterUrl)) {
									employeeProfileJobDetailsDTO.regularAppointmentLetterDocumentUrl = empEmployeeLetterDetailsDBO.letterUrl;
								}
							} else if (letterType.equalsIgnoreCase("CONFIRMATION_LETTER")) {
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.id)) {
									employeeProfileJobDetailsDTO.confirmationLetterId = empEmployeeLetterDetailsDBO.id.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterDate)) {
									employeeProfileJobDetailsDTO.confirmationLetterDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empEmployeeLetterDetailsDBO.letterDate.toString()));
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterRefNo)) {
									employeeProfileJobDetailsDTO.confirmationLetterRfNo = empEmployeeLetterDetailsDBO.letterRefNo.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterUrl)) {
									employeeProfileJobDetailsDTO.confirmationLetterDocumentUrl = empEmployeeLetterDetailsDBO.letterUrl;
								}
							} else if (letterType.equalsIgnoreCase("CONTRACT_LETTER")) {
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.id)) {
									employeeProfileJobDetailsDTO.contractLetterId = empEmployeeLetterDetailsDBO.id.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterDate)) {
									employeeProfileJobDetailsDTO.contractLetterDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empEmployeeLetterDetailsDBO.letterDate.toString()));
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterRefNo)) {
									employeeProfileJobDetailsDTO.contractLetterRfNo = empEmployeeLetterDetailsDBO.letterRefNo.toString();
								}
								if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterUrl)) {
									employeeProfileJobDetailsDTO.contractLetterDocumentUrl = empEmployeeLetterDetailsDBO.letterUrl;
								}
							}
							//						else if(letterType.equalsIgnoreCase("PROMOTION_LETTER")) {
							//							if(!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.id)) {
							//								employeeLetter.id = empEmployeeLetterDetailsDBO.id.toString();
							//							}
							//							if(!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterDate)) {
							//								employeeLetter.date = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empEmployeeLetterDetailsDBO.letterDate.toString()));
							//							}
							//							if(!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterRefNo)) {
							//								employeeLetter.referenceNo = empEmployeeLetterDetailsDBO.letterRefNo.toString();
							//							}
							//							if(!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.letterUrl)) {
							//								employeeLetter.documents = empEmployeeLetterDetailsDBO.letterUrl;
							//							}
							//							jobInformationDTO.promotionDetails.add(employeeLetter);
							//						}
						}
					}
				}
				List<EmpLevelsAndPromotionsDBO> empLevelAndPromotionsDBOList = employeeProfileTransaction.getLevelAndPromotionDetails(empId);
				if (!Utils.isNullOrEmpty(empLevelAndPromotionsDBOList)) {
					EmpLevelsAndPromotionsDTO empLevelsAndPromotionsDTO = new EmpLevelsAndPromotionsDTO();
					empLevelAndPromotionsDBOList.forEach(data -> {
						if (!Utils.isNullOrEmpty(data.getId())) {
							empLevelsAndPromotionsDTO.setId(data.getId());
						}
						if (!Utils.isNullOrEmpty(data.getEmpPayScaleLevelDBO())) {
							empLevelsAndPromotionsDTO.setEmpPayScaleLevel(new SelectDTO());
							empLevelsAndPromotionsDTO.getEmpPayScaleLevel().setValue(data.getEmpPayScaleLevelDBO().getId().toString());
							empLevelsAndPromotionsDTO.getEmpPayScaleLevel().setLabel(data.getEmpPayScaleLevelDBO().getEmpPayScaleLevel());
						}
						if (!Utils.isNullOrEmpty(data.getCell())) {
							empLevelsAndPromotionsDTO.setCell(new SelectDTO());
							empLevelsAndPromotionsDTO.getCell().setLabel(data.getCell().toString());
						}
						if (!Utils.isNullOrEmpty(data.getEffectiveDateOfPromotion())) {
							empLevelsAndPromotionsDTO.setEffectiveDateOfPromotion(data.getEffectiveDateOfPromotion());
						}
						if (!Utils.isNullOrEmpty(data.getRemarks())) {
							empLevelsAndPromotionsDTO.setRemarks(data.getRemarks());
						}
						if (!Utils.isNullOrEmpty(data.getEmpDesignationDBO())) {
							empLevelsAndPromotionsDTO.setEmpDesignation(new SelectDTO());
							empLevelsAndPromotionsDTO.getEmpDesignation().setValue(String.valueOf(data.getEmpDesignationDBO().getId()));
							empLevelsAndPromotionsDTO.getEmpDesignation().setLabel(data.getEmpDesignationDBO().getEmpDesignationName());
						}
						if (!Utils.isNullOrEmpty(data.getEmpEmployeeLetterDetailsDBO())) {
							empLevelsAndPromotionsDTO.setEmployeeProfileLetterDTO(new EmployeeProfileLetterDTO());
							if (!Utils.isNullOrEmpty(data.getEmpEmployeeLetterDetailsDBO().getId())) {
								empLevelsAndPromotionsDTO.getEmployeeProfileLetterDTO().setId(String.valueOf(data.getEmpEmployeeLetterDetailsDBO().getId()));
							}
							if (!Utils.isNullOrEmpty(data.getEmpEmployeeLetterDetailsDBO().getLetterDate())) {
								empLevelsAndPromotionsDTO.getEmployeeProfileLetterDTO().setDate(Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(data.getEmpEmployeeLetterDetailsDBO().getLetterDate().toString())));
							}
							if (!Utils.isNullOrEmpty(data.getEmpEmployeeLetterDetailsDBO().getLetterRefNo())) {
								empLevelsAndPromotionsDTO.getEmployeeProfileLetterDTO().setReferenceNo(String.valueOf(data.getEmpEmployeeLetterDetailsDBO().getLetterRefNo()));
							}
							if (!Utils.isNullOrEmpty(data.getEmpEmployeeLetterDetailsDBO().getLetterUrl())) {
								empLevelsAndPromotionsDTO.getEmployeeProfileLetterDTO().setDocuments(data.getEmpEmployeeLetterDetailsDBO().getLetterUrl());
							}
						}
						jobInformationDTO.getEmpLevelsAndPromotionsDTO().add(empLevelsAndPromotionsDTO);
					});
				}
				if (jobInformationDTO.promotionDetails.size() == 0) {
					jobInformationDTO.promotionDetails.add(new EmployeeProfileLetterDTO());
				}
				if (!Utils.isNullOrEmpty(employee.get("emp_job_details_id"))) {
					jobDetailsId = employee.get("emp_job_details_id").toString();
					employeeProfileJobDetailsDTO.jobDetailsId = jobDetailsId;
				}
				//				if(!Utils.isNullOrEmpty(employee.get("guest_subject_specilization"))) {
				//					employeeProfileJobDetailsDTO.subjectOrSpecialization = employee.get("guest_subject_specilization").toString() ;
				//				}
				if (!Utils.isNullOrEmpty(employee.get("is_employee_active"))) {
					employeeProfileJobDetailsDTO.isEmployeeActive = employee.get("is_employee_active").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("is_vacation_applicable"))) {
					employeeProfileJobDetailsDTO.isVacationApplicable = employee.get("is_vacation_applicable").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("is_display_website"))) {
					employeeProfileJobDetailsDTO.isDisplayWebsite = employee.get("is_display_website").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("doj"))) {
					employeeProfileJobDetailsDTO.joiningDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("doj").toString()));
					//	employeeProfileJobDetailsDTO.joiningDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateTimeToLocalDate(employee.get("joining_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("retirement_date"))) {
					employeeProfileJobDetailsDTO.retirementDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("retirement_date").toString()));
				}
				jobInformationDTO.jobInformation.empJobDetails = employeeProfileJobDetailsDTO;
				List<EmpRemarksDetailsDBO> empRemarksDetailsDBOList = employeeProfileTransaction.getEmployeeRemarksDetails(empId);
				List<EmpRemarksDTO> remarks = new ArrayList<>();
				if (!Utils.isNullOrEmpty(empRemarksDetailsDBOList)) {
					for (EmpRemarksDetailsDBO empRemarksDetailsDBO : empRemarksDetailsDBOList) {
						EmpRemarksDTO remarksDTO = new EmpRemarksDTO();
						if (!Utils.isNullOrEmpty(empRemarksDetailsDBO.id)) {
							remarksDTO.id = empRemarksDetailsDBO.id.toString();
						}
						if (!Utils.isNullOrEmpty(empRemarksDetailsDBO.remarksDetails)) {
							remarksDTO.remarksDetails = empRemarksDetailsDBO.remarksDetails;
						}
						if (!Utils.isNullOrEmpty(empRemarksDetailsDBO.remarksDate)) {
							remarksDTO.remarksDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empRemarksDetailsDBO.remarksDate.toString()));
						}
						if (!Utils.isNullOrEmpty(empRemarksDetailsDBO.remarksUploadUrl)) {
							remarksDTO.remarksUploadUrl = empRemarksDetailsDBO.remarksUploadUrl;
						}
						if (!Utils.isNullOrEmpty(empRemarksDetailsDBO.remarksRefNo)) {
							remarksDTO.remarksRefNo = empRemarksDetailsDBO.remarksRefNo;
						}
						if (!Utils.isNullOrEmpty(empRemarksDetailsDBO.createdUsersId)) {
							ErpUsersDBO erpuser = commonApiTransaction.find(ErpUsersDBO.class, empRemarksDetailsDBO.createdUsersId);
							if (!Utils.isNullOrEmpty(erpuser.userName)) {
								remarksDTO.enteredBy = erpuser.userName;
							} else {
								remarksDTO.enteredBy = "";
							}
						}
						if (empRemarksDetailsDBO.isForOfficeUse) {
							remarks.add(remarksDTO);
						} else {
							jobInformationDTO.otherInfo = remarksDTO;
						}
					}
				}
				if (remarks.size() == 0) {
					remarks.add(new EmpRemarksDTO());
				}
				if (Utils.isNullOrEmpty(jobInformationDTO.otherInfo)) {
					jobInformationDTO.otherInfo = new EmpRemarksDTO();
				}
				jobInformationDTO.remarks = remarks;
				erpRoomEmpMappingDTO.block = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_block_id"))) {
					erpRoomEmpMappingDTO.block.value = employee.get("erp_block_id").toString();
					erpRoomEmpMappingDTO.block.label = String.valueOf(employee.get("block_name"));
				}
				erpRoomEmpMappingDTO.room = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_rooms_id"))) {
					erpRoomEmpMappingDTO.room.value = employee.get("erp_rooms_id").toString();
					erpRoomEmpMappingDTO.room.label = String.valueOf(employee.get("room_no"));
				}
				erpRoomEmpMappingDTO.floor = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_floors_id"))) {
					erpRoomEmpMappingDTO.floor.value = employee.get("erp_floors_id").toString();
					erpRoomEmpMappingDTO.floor.label = String.valueOf(employee.get("floor_name"));
				}
				if (!Utils.isNullOrEmpty(employee.get("telephone_number"))) {
					erpRoomEmpMappingDTO.telephoneOffice = employee.get("telephone_number").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("telephone_extension"))) {
					erpRoomEmpMappingDTO.telephoneExtention = employee.get("telephone_extension").toString();
				}
				jobInformationDTO.jobInformation.erpRoomEmpMappingDTO = erpRoomEmpMappingDTO;
				if (!Utils.isNullOrEmpty(employee.get("emp_resignation_id"))) {
					empResignationDTO.resignationId = employee.get("emp_resignation_id").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("submission_date"))) {
					empResignationDTO.submissionDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("submission_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("date_of_leaving"))) {
					empResignationDTO.dateOfLeaving = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("date_of_leaving").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("relieving_date"))) {
					empResignationDTO.relieavingOrderDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("relieving_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("hod_recommended_relieving_date"))) {
					empResignationDTO.hodRecomendedRelievingDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("hod_recommended_relieving_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("resignation_name"))) {
					empResignationDTO.reasonForLeaving = employee.get("resignation_name").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("is_serving_notice_period"))) {
					empResignationDTO.isServingNoticePeriod = employee.get("is_serving_notice_period").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("reference_no"))) {
					empResignationDTO.referenceNo = employee.get("reference_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("po_remarks"))) {
					empResignationDTO.poRemarks = employee.get("po_remarks").toString();
				}
				jobInformationDTO.resignationDetails = empResignationDTO;

				if (!Utils.isNullOrEmpty(employee.get("is_duty_roster_applicable"))) {
					workTimeDetails.isDutyRosterApplicable = employee.get("is_duty_roster_applicable").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("is_holiday_time_zone_applicable"))) {
					workTimeDetails.isHolidayTimeZoneApplicable = employee.get("is_holiday_time_zone_applicable").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("is_vacation_time_zone_applicable"))) {
					workTimeDetails.isVacationTimeZoneApplicable = employee.get("is_vacation_time_zone_applicable").toString();
				}
				workTimeDetails.generalTimeZone = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("e_time_id"))) {
					workTimeDetails.generalTimeZone.value = employee.get("e_time_id").toString();
					workTimeDetails.generalTimeZone.label = String.valueOf(employee.get("e_time_name"));
				}
				workTimeDetails.holidayTimeZone = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("h_time_id"))) {
					workTimeDetails.holidayTimeZone.value = employee.get("h_time_id").toString();
					workTimeDetails.holidayTimeZone.label = String.valueOf(employee.get("h_time_name"));
				}
				workTimeDetails.vacationTimeZone = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("v_time_id"))) {
					workTimeDetails.vacationTimeZone.value = employee.get("v_time_id").toString();
					workTimeDetails.vacationTimeZone.label = String.valueOf(employee.get("v_time_name"));
				}
				jobInformationDTO.workTimeDetails = workTimeDetails;
				List<EmpGuestContractDetailsDBO> guestContratcDetailsDBO = employeeProfileTransaction.getGuestContractDetails(empId);
				if (!Utils.isNullOrEmpty(guestContratcDetailsDBO)) {
					for (EmpGuestContractDetailsDBO guestContractDetails : guestContratcDetailsDBO) {
						EmployeeProfileAdditionalInformation additionalInformation = new EmployeeProfileAdditionalInformation();
						if (!Utils.isNullOrEmpty(guestContractDetails.id)) {
							additionalInformation.id = guestContractDetails.id.toString();
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.getIsCurrent())) {
							if (guestContractDetails.isCurrent) {
								additionalInformation.isCurrentDetails = "Yes";
							} else {
								additionalInformation.isCurrentDetails = "No";
							}
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.contractEmpStartDate)) {
							additionalInformation.startDate = Utils.convertLocalDateToStringDate6(guestContractDetails.contractEmpStartDate);
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.contractEmpEndDate)) {
							additionalInformation.endDate = Utils.convertLocalDateToStringDate6(guestContractDetails.contractEmpEndDate);
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.guestTutoringSemester)) {
							additionalInformation.semester = new LookupItemDTO();
							additionalInformation.semester.label = guestContractDetails.guestTutoringSemester;
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.guestWorkingHoursWeek)) {
							additionalInformation.workHourPerWeek = guestContractDetails.guestWorkingHoursWeek.toString();
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.getErpCampusDepartmentMappingDBO())) {
							additionalInformation.setCampusDepartment(new SelectDTO());
							additionalInformation.getCampusDepartment().setValue(String.valueOf(guestContractDetails.getErpCampusDepartmentMappingDBO().getId()));
							additionalInformation.getCampusDepartment().setLabel(guestContractDetails.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName().concat("(").concat(guestContractDetails.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName()).concat(")"));
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.empPayScaleDetailsDBO)) {
							if (!Utils.isNullOrEmpty(guestContractDetails.empPayScaleDetailsDBO.id)) {
								additionalInformation.payscaleDetailsId = guestContractDetails.empPayScaleDetailsDBO.id.toString();
							}
							if (!Utils.isNullOrEmpty(guestContractDetails.empPayScaleDetailsDBO.payScaleType)) {
								additionalInformation.paymentType = new LookupItemDTO();
								additionalInformation.paymentType.label = guestContractDetails.empPayScaleDetailsDBO.payScaleType;
							}
							if (!Utils.isNullOrEmpty(guestContractDetails.empPayScaleDetailsDBO.wageRatePerType)) {
								additionalInformation.honararium = guestContractDetails.empPayScaleDetailsDBO.wageRatePerType.toString();
							}
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.guestContractRemarks)) {
							additionalInformation.comments = guestContractDetails.guestContractRemarks;
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.getGuestReferredBy())) {
							empGuestContractDetailsDTO.guestReferredBy = guestContractDetails.getGuestReferredBy();
						}
						if (!Utils.isNullOrEmpty(guestContractDetails.getGuestSubjectSpecialization())) {
							employeeProfileJobDetailsDTO.subjectOrSpecialization = guestContractDetails.getGuestSubjectSpecialization();
						}
						additionalInfoList.add(additionalInformation);
					}
				}
				//				if(!Utils.isNullOrEmpty(employee.get("emp_guest_contract_details_id"))) {
				//					empGuestContractDetailsDTO.id =  employee.get("emp_guest_contract_details_id").toString();
				//				}
				//				if(!Utils.isNullOrEmpty(employee.get("guest_reffered_by"))) {
				//					empGuestContractDetailsDTO.guestReferredBy =  employee.get("guest_reffered_by").toString();
				//				}
				if (additionalInfoList.size() == 0) {
					EmployeeProfileAdditionalInformation employeeProfileAdditionalInformation = new EmployeeProfileAdditionalInformation();
					employeeProfileAdditionalInformation.semester = new LookupItemDTO();
					additionalInfoList.add(employeeProfileAdditionalInformation);
				}
				jobInformationDTO.additionalInformation = additionalInfoList;
				if (!Utils.isNullOrEmpty(employee.get("emp_approvers_id"))) {
					empApproversDetailsDTO.id = employee.get("emp_approvers_id").toString();
				}
				empApproversDetailsDTO.leaveApprover = new SelectDTO();
				if (!Utils.isNullOrEmpty(employee.get("leave_approver_id"))) {
					empApproversDetailsDTO.leaveApprover.value = employee.get("leave_approver_id").toString();
					empApproversDetailsDTO.leaveApprover.label = String.valueOf(employee.get("leave_approver"));
				}
				empApproversDetailsDTO.leaveAuthorizer = new SelectDTO();
				if (!Utils.isNullOrEmpty(employee.get("leave_authoriser_id"))) {
					empApproversDetailsDTO.leaveAuthorizer.value = employee.get("leave_authoriser_id").toString();
					empApproversDetailsDTO.leaveAuthorizer.label = String.valueOf(employee.get("leave_authriser"));
				}
				empApproversDetailsDTO.levelOneAppraiser = new SelectDTO();
				if (!Utils.isNullOrEmpty(employee.get("level_one_appraiser_id"))) {
					empApproversDetailsDTO.levelOneAppraiser.value = employee.get("level_one_appraiser_id").toString();
					empApproversDetailsDTO.levelOneAppraiser.label = employee.get("level1_appraiser").toString();
				}
				empApproversDetailsDTO.levelTwoAppraiser = new SelectDTO();
				if (!Utils.isNullOrEmpty(employee.get("level_two_appraiser_id"))) {
					empApproversDetailsDTO.levelTwoAppraiser.value = employee.get("level_two_appraiser_id").toString();
					empApproversDetailsDTO.levelTwoAppraiser.label = employee.get("leve12_appraiser").toString();
				}
				empApproversDetailsDTO.workDairyApprover = new SelectDTO();
				if (!Utils.isNullOrEmpty(employee.get("work_diary_approver_id"))) {
					empApproversDetailsDTO.workDairyApprover.value = employee.get("work_diary_approver_id").toString();
					empApproversDetailsDTO.workDairyApprover.label = employee.get("workdiary_approver").toString();
				}
				jobInformationDTO.approverDetails = empApproversDetailsDTO;
				if (!Utils.isNullOrEmpty(employee.get("contract_start_date"))) {
					empGuestContractDetailsDTO.contractStartDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("contract_start_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("contract_end_date"))) {
					empGuestContractDetailsDTO.contractEndDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("contract_end_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("contract_remarks"))) {
					empGuestContractDetailsDTO.comments = employee.get("contract_remarks").toString();
				}
				jobInformationDTO.jobInformation.empGuestContractDetailsDTO = empGuestContractDetailsDTO;
				employeeProfileDTO.jobDetails = jobInformationDTO;
				employeeProfileDTO.educationAndExperienceDetails = new EducationAndExperienceDetailsDTO();
				employeeProfileDTO.educationAndExperienceDetails.qualificationDetails = new EducationalDetailsDTO();
				employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.highestQualification = new LookupItemDTO();
				if (!Utils.isNullOrEmpty(employee.get("erp_qualification_level_id"))) {
					employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.highestQualification.value = employee.get("erp_qualification_level_id").toString();
					employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.highestQualification.label = employee.get("qualification_level_name").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("highest_qualification_album"))) {
					employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.highestQualificationAlbum = employee.get("highest_qualification_album").toString();
				}
				List<EmpEducationalDetailsDBO> educationalDetailsListDBO = employeeProfileTransaction.getEmployeeEducationalDetails(empId);
				List<EmpEducationalDetailsDTO> qualificationLevelList = new ArrayList<>();
				if (!Utils.isNullOrEmpty(educationalDetailsListDBO)) {
					educationalDetailsListDBO.forEach(empEducationalDetailsDBO -> {
						EmpEducationalDetailsDTO empEducationalDetailsDTO = new EmpEducationalDetailsDTO();
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.empEducationalDetailsId)) {
							empEducationalDetailsDTO.id = String.valueOf(empEducationalDetailsDBO.empEducationalDetailsId);
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.empDBO.id)) {
							empEducationalDetailsDTO.empId = empEducationalDetailsDBO.empDBO.id;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.erpQualificationLevelDBO)) {
							empEducationalDetailsDTO.qualificationLevel = new LookupItemDTO();
							empEducationalDetailsDTO.qualificationLevel.value = String.valueOf(empEducationalDetailsDBO.erpQualificationLevelDBO.id);
							empEducationalDetailsDTO.qualificationLevel.label = empEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelName;
							empEducationalDetailsDTO.qualificationLevelOrder = String.valueOf(empEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelDegreeOrder);
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.course)) {
							empEducationalDetailsDTO.course = empEducationalDetailsDBO.course;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.specialization)) {
							empEducationalDetailsDTO.specialization = empEducationalDetailsDBO.specialization;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.yearOfCompletion)) {
							empEducationalDetailsDTO.yearOfCompletion = new LookupItemDTO();
							empEducationalDetailsDTO.yearOfCompletion.value = empEducationalDetailsDBO.yearOfCompletion.toString();
							empEducationalDetailsDTO.yearOfCompletion.label = empEducationalDetailsDBO.yearOfCompletion.toString();
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.gradeOrPercentage)) {
							empEducationalDetailsDTO.gradeOrPercentage = empEducationalDetailsDBO.gradeOrPercentage;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.institute)) {
							empEducationalDetailsDTO.institute = empEducationalDetailsDBO.institute;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.boardOrUniversity)) {
							empEducationalDetailsDTO.boardOrUniversity = empEducationalDetailsDBO.boardOrUniversity;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.erpStateDBO)) {
							empEducationalDetailsDTO.state = new LookupItemDTO();
							empEducationalDetailsDTO.state.value = String.valueOf(empEducationalDetailsDBO.erpStateDBO.id);
							empEducationalDetailsDTO.state.label = empEducationalDetailsDBO.erpStateDBO.stateName;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.stateOthers)) {
							empEducationalDetailsDTO.stateOthers = empEducationalDetailsDBO.stateOthers;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.erpCountryDBO)) {
							empEducationalDetailsDTO.country = new LookupItemDTO();
							empEducationalDetailsDTO.country.value = String.valueOf(empEducationalDetailsDBO.erpCountryDBO.id);
							empEducationalDetailsDTO.country.label = empEducationalDetailsDBO.erpCountryDBO.countryName;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.erpQualificationLevelDBO.isAddMore)) {
							empEducationalDetailsDTO.isAddMore = empEducationalDetailsDBO.erpQualificationLevelDBO.isAddMore;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.currentStatus)) {
							empEducationalDetailsDTO.currentStatus = empEducationalDetailsDBO.currentStatus;
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.getErpUniversityBoardDBO())) {
							empEducationalDetailsDTO.setErpBoardOrUniversity(new SelectDTO());
							empEducationalDetailsDTO.getErpBoardOrUniversity().setValue(String.valueOf(empEducationalDetailsDBO.getErpUniversityBoardDBO().getId()));
							empEducationalDetailsDTO.getErpBoardOrUniversity().setLabel(empEducationalDetailsDBO.getErpUniversityBoardDBO().getUniversityBoardName());
						}
						if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.getErpInstitutionDBO())) {
							empEducationalDetailsDTO.setErpInstitute(new SelectDTO());
							empEducationalDetailsDTO.getErpInstitute().setValue(String.valueOf(empEducationalDetailsDBO.getErpInstitutionDBO().getId()));
							empEducationalDetailsDTO.getErpInstitute().setLabel(empEducationalDetailsDBO.getErpInstitutionDBO().getInstitutionName());
						}
						empEducationalDetailsDTO.documentList = new ArrayList<>();
						empEducationalDetailsDBO.documentsDBOSet.forEach(document -> {
							if (document.getRecordStatus() == 'A') {
								EmpEducationalDetailsDocumentsDTO empEducationalDetailsDocumentsDTO = new EmpEducationalDetailsDocumentsDTO();
								empEducationalDetailsDocumentsDTO.educationalDocumentsUrl = document.educationalDocumentsUrl;
								empEducationalDetailsDocumentsDTO.empEducationalDetailsDocumentsId = document.id;
								empEducationalDetailsDTO.documentList.add(empEducationalDetailsDocumentsDTO);
							}
						});
						if (empEducationalDetailsDTO.documentList.size() == 0) {
							empEducationalDetailsDTO.documentList.add(new EmpEducationalDetailsDocumentsDTO());
						}
						//						if(employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsMap.containsKey(qualification)) {
						//							employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsMap.get(qualification).add(empEducationalDetailsDTO);
						//						}
						//						else {
						//							List<EmpEducationalDetailsDTO> educationalDetailsList = new ArrayList<>();
						//							educationalDetailsList.add(empEducationalDetailsDTO);
						//							employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsMap.put(qualification, educationalDetailsList);
						//						}
						qualificationLevelList.add(empEducationalDetailsDTO);
						//						if(!Utils.isNullOrEmpty(empEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelName)) {
						//							String qualification =  empEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelName;
						//							empEducationalDetailsDTO.qualificationLevelName = qualification;
						//							if(employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsMap.containsKey(qualification)) {
						//								employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsMap.get(qualification).add(empEducationalDetailsDTO);
						//							}
						//							else {
						//								List<EmpEducationalDetailsDTO> educationalDetailsList = new ArrayList<>();
						//								educationalDetailsList.add(empEducationalDetailsDTO);
						//								employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsMap.put(qualification, educationalDetailsList);
						//							}
						//						}
					});
					if (!Utils.isNullOrEmpty(qualificationLevelList)) {
						qualificationLevelList.sort((o1, o2) -> {
							int comp = o1.qualificationLevelOrder.compareTo(o2.qualificationLevelOrder);
							if (!Utils.isNullOrEmpty(o1.yearOfCompletion) && !Utils.isNullOrEmpty(o1.yearOfCompletion.value)
									&& !Utils.isNullOrEmpty(o2.yearOfCompletion) && !Utils.isNullOrEmpty(o2.yearOfCompletion.value) && comp == 0)
								return o1.yearOfCompletion.value.compareTo(o2.yearOfCompletion.value);
							else
								return comp;
						});
						employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsDTOS = qualificationLevelList;
					}
				}
				//				List<ErpQualificationLevelDBO> erpQualificationLevelDBOList = employeeProfileTransaction.getErpQualificationLevel();
				//				if(!Utils.isNullOrEmpty(erpQualificationLevelDBOList)) {
				//					erpQualificationLevelDBOList.forEach(erpQualificationLevelDBO->{
				//						List<EmpEducationalDetailsDTO> empEducationalDetailsDTOList = new ArrayList<>();
				//						EmpEducationalDetailsDTO empEducationalDetailsDTO = new EmpEducationalDetailsDTO();
				//						if(!Utils.isNullOrEmpty(erpQualificationLevelDBO.qualificationLevelName)) {
				//							empEducationalDetailsDTO.qualificationLevelName = erpQualificationLevelDBO.qualificationLevelName;
				//						}
				//						if(!Utils.isNullOrEmpty(erpQualificationLevelDBO.isAddMore)) {
				//							empEducationalDetailsDTO.isAddMore = erpQualificationLevelDBO.isAddMore;
				//						}
				//						if(!Utils.isNullOrEmpty(erpQualificationLevelDBO.id)) {
				//							empEducationalDetailsDTO.qualificationLevel = new LookupItemDTO();
				//							empEducationalDetailsDTO.qualificationLevel.value = String.valueOf(erpQualificationLevelDBO.id);
				//							empEducationalDetailsDTO.qualificationLevel.label = erpQualificationLevelDBO.qualificationLevelName;
				//						}
				//						empEducationalDetailsDTO.state = new LookupItemDTO();
				//						empEducationalDetailsDTO.country = new LookupItemDTO();
				//						empEducationalDetailsDTO.documentList = new ArrayList<>();
				//						empEducationalDetailsDTO.documentList.add(new EmpEducationalDetailsDocumentsDTO());
				//						empEducationalDetailsDTOList.add(empEducationalDetailsDTO);
				//						if(!employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsMap.containsKey(erpQualificationLevelDBO.qualificationLevelName)) {
				//							employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.empEducationalDetailsMap.put(erpQualificationLevelDBO.qualificationLevelName, empEducationalDetailsDTOList);
				//						}
				//					});
				//				}
				List<EmpEligibilityTestDBO> empEligibilityTestDBOList = employeeProfileTransaction.getEmployeeEligibilityTestDetails(empId);
				employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.eligibilityTestDetails = new ArrayList<>();
				if (!Utils.isNullOrEmpty(empEligibilityTestDBOList)) {
					empEligibilityTestDBOList.forEach(empEligibilityTestDBO -> {
						EmpEligiblityTestDTO empEligiblityTestDTO = new EmpEligiblityTestDTO();
						if (!Utils.isNullOrEmpty(empEligibilityTestDBO.empEligibilityTestId)) {
							empEligiblityTestDTO.empEligibilityTestId = empEligibilityTestDBO.empEligibilityTestId;
						}
						if (!Utils.isNullOrEmpty(empEligibilityTestDBO.testYear)) {
							empEligiblityTestDTO.testYear = empEligibilityTestDBO.testYear.toString();
						}
						empEligiblityTestDTO.eligibilityTest = new LookupItemDTO();
						if (!Utils.isNullOrEmpty(empEligibilityTestDBO.empEligibilityExamListDBO)) {
							empEligiblityTestDTO.eligibilityTest.value = String.valueOf(empEligibilityTestDBO.empEligibilityExamListDBO.empEligibilityExamListId);
							empEligiblityTestDTO.eligibilityTest.label = String.valueOf(empEligibilityTestDBO.empEligibilityExamListDBO.eligibilityExamName);
						}
						employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.eligibilityTestDetails.add(empEligiblityTestDTO);
					});
				}
				if (employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.eligibilityTestDetails.size() == 0) {
					employeeProfileDTO.educationAndExperienceDetails.qualificationDetails.eligibilityTestDetails.add(new EmpEligiblityTestDTO());
				}
				List<EmpWorkExperienceDBO> empWorkExperienceDBOList = employeeProfileTransaction.getEmployeeExperienceDetails(empId);
				employeeProfileDTO.educationAndExperienceDetails.experienceDetails = new ProfessionalExperienceDTO();
				employeeProfileDTO.educationAndExperienceDetails.experienceDetails.experienceInformation = new ArrayList<>();
				if (!Utils.isNullOrEmpty(empWorkExperienceDBOList)) {
					for (EmpWorkExperienceDBO empWorkExperienceDBO : empWorkExperienceDBOList) {
						EmpWorkExperienceDTO empWorkExperienceDTO = new EmpWorkExperienceDTO();
						empWorkExperienceDTO.experienceDocumentList = new ArrayList<>();

						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.empWorkExperienceId)) {
							empWorkExperienceDTO.empApplnWorkExperienceId = empWorkExperienceDBO.empWorkExperienceId;
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.isRecognized)) {
							if (empWorkExperienceDBO.isRecognized) {
								empWorkExperienceDTO.isRecognised = "Yes";
							} else {
								empWorkExperienceDTO.isRecognised = "No";
							}
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.empApplnWorkExperienceTypeDBO)) {
							empWorkExperienceDTO.workExperienceType = new LookupItemDTO();
							empWorkExperienceDTO.workExperienceType.value = String.valueOf(empWorkExperienceDBO.empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId);
							empWorkExperienceDTO.workExperienceType.label = empWorkExperienceDBO.empApplnWorkExperienceTypeDBO.workExperienceTypeName;
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.isPartTime) && empWorkExperienceDBO.isPartTime) {
							empWorkExperienceDTO.employmentType = "parttime";
							if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceYears)) {
								partTimeMonths = partTimeMonths + (Integer.parseInt(empWorkExperienceDBO.workExperienceYears.toString()) * 12);
							}
							if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceMonth)) {
								partTimeMonths += Integer.parseInt(empWorkExperienceDBO.workExperienceMonth.toString());
							}
							if (!Utils.isNullOrEmpty(empWorkExperienceDBO.isRecognized) && empWorkExperienceDBO.isRecognized) {
								if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceYears)) {
									recognizedExperienceMonth = recognizedExperienceMonth + (Integer.parseInt(empWorkExperienceDBO.workExperienceYears.toString()) * 12);
								}
								if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceMonth)) {
									recognizedExperienceMonth += Integer.parseInt(empWorkExperienceDBO.workExperienceMonth.toString());
								}
							}
						} else {
							empWorkExperienceDTO.employmentType = "fulltime";
							if (!Utils.isNullOrEmpty(empWorkExperienceDBO.isRecognized) && empWorkExperienceDBO.isRecognized) {
								if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceYears)) {
									recognizedExperienceMonth = recognizedExperienceYears + (Integer.parseInt(empWorkExperienceDBO.workExperienceYears.toString()) * 12);
								}
								if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceMonth)) {
									recognizedExperienceMonth += Integer.parseInt(empWorkExperienceDBO.workExperienceMonth.toString());
								}
							}
							if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceYears)) {
								fullTimeMonths = fullTimeYears + (Integer.parseInt(empWorkExperienceDBO.workExperienceYears.toString()) * 12);
							}
							if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceMonth)) {
								fullTimeMonths += Integer.parseInt(empWorkExperienceDBO.workExperienceMonth.toString());
							}
						}
						empWorkExperienceDTO.functionalArea = new LookupItemDTO();
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.empApplnSubjectCategoryDBO) && !Utils.isNullOrEmpty(empWorkExperienceDBO.empApplnSubjectCategoryDBO.id)) {
							empWorkExperienceDTO.functionalArea.value = String.valueOf(empWorkExperienceDBO.empApplnSubjectCategoryDBO.id);
							empWorkExperienceDTO.functionalArea.label = empWorkExperienceDBO.empApplnSubjectCategoryDBO.subjectCategory;
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.empDesignation)) {
							empWorkExperienceDTO.designation = empWorkExperienceDBO.empDesignation;
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceFromDate)) {
							empWorkExperienceDTO.fromDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empWorkExperienceDBO.workExperienceFromDate.toString()));
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceToDate)) {
							empWorkExperienceDTO.toDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empWorkExperienceDBO.workExperienceToDate.toString()));
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceYears)) {
							empWorkExperienceDTO.years = String.valueOf(empWorkExperienceDBO.workExperienceYears);
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceMonth)) {
							empWorkExperienceDTO.months = String.valueOf(empWorkExperienceDBO.workExperienceMonth);
						}
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.institution)) {
							empWorkExperienceDTO.institution = empWorkExperienceDBO.institution;
						}
						empWorkExperienceDTO.experienceDocumentList = new ArrayList<>();
						List<EmpWorkExperienceDocumentDTO> empWorkExperienceDocumentDTOList = new ArrayList<>();
						if (!Utils.isNullOrEmpty(empWorkExperienceDBO.workExperienceDocumentsDBOSet)) {
							empWorkExperienceDBO.workExperienceDocumentsDBOSet.forEach(empWorkExperienceDocumentDBO -> {
								if (empWorkExperienceDocumentDBO.recordStatus == 'A') {
									EmpWorkExperienceDocumentDTO empWorkExperienceDocumentDTO = new EmpWorkExperienceDocumentDTO();
									if (!Utils.isNullOrEmpty(empWorkExperienceDocumentDBO.id))
										empWorkExperienceDocumentDTO.empWorkExperienceDocumentId = empWorkExperienceDocumentDBO.id;
									if (!Utils.isNullOrEmpty(empWorkExperienceDocumentDBO.empWorkExperienceDBO) && !Utils.isNullOrEmpty(empWorkExperienceDocumentDBO.empWorkExperienceDBO.empWorkExperienceId))
										empWorkExperienceDocumentDTO.empWorkExperienceId = empWorkExperienceDocumentDBO.empWorkExperienceDBO.empWorkExperienceId;
									if (!Utils.isNullOrEmpty(empWorkExperienceDocumentDBO.experienceDocumentsUrl))
										empWorkExperienceDocumentDTO.documentUrl = empWorkExperienceDocumentDBO.experienceDocumentsUrl;
									empWorkExperienceDocumentDTOList.add(empWorkExperienceDocumentDTO);
								}
							});
						}
						if (empWorkExperienceDocumentDTOList.size() == 0) {
							empWorkExperienceDocumentDTOList.add(new EmpWorkExperienceDocumentDTO());
						}
						empWorkExperienceDTO.experienceDocumentList = empWorkExperienceDocumentDTOList;
						employeeProfileDTO.educationAndExperienceDetails.experienceDetails.fullTimeYears = String.valueOf(fullTimeMonths / 12);
						employeeProfileDTO.educationAndExperienceDetails.experienceDetails.fullTimeMonths = String.valueOf(fullTimeMonths % 12);
						employeeProfileDTO.educationAndExperienceDetails.experienceDetails.partTimeYears = String.valueOf(partTimeMonths / 12);
						employeeProfileDTO.educationAndExperienceDetails.experienceDetails.partTimeMonths = String.valueOf(partTimeMonths % 12);
						employeeProfileDTO.educationAndExperienceDetails.experienceDetails.recognisedExpYears = String.valueOf(recognizedExperienceMonth / 12);  // recognized experience is storing in EmpJobDetailsDBO
						employeeProfileDTO.educationAndExperienceDetails.experienceDetails.recognisedExpMonths = String.valueOf(recognizedExperienceMonth % 12); // recognized experience is storing in EmpJobDetailsDBO
						employeeProfileDTO.educationAndExperienceDetails.experienceDetails.experienceInformation.add(empWorkExperienceDTO);
					}
				}
				if (!Utils.isNullOrEmpty(employee.get("recognised_exp_years"))) {
					employeeProfileDTO.educationAndExperienceDetails.experienceDetails.recognisedExpYears = String.valueOf(employee.get("recognised_exp_years"));
				}
				if (!Utils.isNullOrEmpty(employee.get("recognised_exp_months"))) {
					employeeProfileDTO.educationAndExperienceDetails.experienceDetails.recognisedExpMonths = String.valueOf(employee.get("recognised_exp_months"));
				}
				List<EmpMajorAchievementsDTO> majorAchievemntsList = new ArrayList<>();
				List<EmpMajorAchievementsDBO> majorAchievementsDBOList = employeeProfileTransaction.getEmpMajorAchievemnts(empId);
				if (!Utils.isNullOrEmpty(majorAchievementsDBOList)) {
					majorAchievementsDBOList.forEach(achievement -> {
						EmpMajorAchievementsDTO empMajorAchievementsDTO = new EmpMajorAchievementsDTO();
						empMajorAchievementsDTO.id = achievement.id.toString();
						empMajorAchievementsDTO.name = achievement.achievements;
						majorAchievemntsList.add(empMajorAchievementsDTO);
					});
				}
				if (majorAchievemntsList.size() == 0) {
					majorAchievemntsList.add(new EmpMajorAchievementsDTO());
				}
				employeeProfileDTO.educationAndExperienceDetails.experienceDetails.majorAchievementsList = majorAchievemntsList;
				employeeProfileDTO.salaryAndLeaveDetails = new SalaryAndLeaveDetailsDTO();
				if (!Utils.isNullOrEmpty(employee.get("application_no"))) {
					FinalInterviewCommentsDTO finalInterviewCommentsDTO = finalInterviewCommentsHandler1.editFinalInterviewComments(Integer.parseInt(employee.get("application_no").toString()));
					if (!Utils.isNullOrEmpty(finalInterviewCommentsDTO)) {
						if (Utils.isNullOrEmpty(finalInterviewCommentsDTO.cell)) {
							finalInterviewCommentsDTO.cell = new ExModelBaseDTO();
						}
						if (Utils.isNullOrEmpty(finalInterviewCommentsDTO.level)) {
							finalInterviewCommentsDTO.level = new ExModelBaseDTO();
						}
						if (Utils.isNullOrEmpty(finalInterviewCommentsDTO.scale)) {
							finalInterviewCommentsDTO.scale = new ExModelBaseDTO();
						}
						if (Utils.isNullOrEmpty(finalInterviewCommentsDTO.cellValue)) {
							finalInterviewCommentsDTO.cellValue = new ExModelBaseDTO();
						}
						employeeProfileDTO.salaryAndLeaveDetails.finalInterviewCommentsDTO = finalInterviewCommentsDTO;
					} else {
						employeeProfileDTO.salaryAndLeaveDetails.finalInterviewCommentsDTO = new FinalInterviewCommentsDTO();
					}
				}
				if (!Utils.isNullOrEmpty(employee.get("pf_account_no"))) {
					pfNomineeDetailsDTO.accountNo = employee.get("pf_account_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("pf_date"))) {
					pfNomineeDetailsDTO.accountDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("pf_date").toString()));
				}
				if (!Utils.isNullOrEmpty(employee.get("uan_no"))) {
					pfNomineeDetailsDTO.uanNo = employee.get("uan_no").toString();
				}
				if (!Utils.isNullOrEmpty(jobDetailsId)) {
					pfNomineeDetailsDTO.nomineeDetails = new ArrayList<>();
					List<EmpPfGratuityNomineesDBO> empPfNomineesDBOList = employeeProfileTransaction.getEmpPfNomineesDBOList(jobDetailsId);
					if (!Utils.isNullOrEmpty(empPfNomineesDBOList)) {
						empPfNomineesDBOList.forEach(empPfNomineesDBO -> {
							EmpPfGratuityNomineesDTO empPfGratuityNomineesDTO = new EmpPfGratuityNomineesDTO();
							if (!Utils.isNullOrEmpty(empPfNomineesDBO.empPfGratuityNomineesId)) {
								empPfGratuityNomineesDTO.empPfGratuityNomineesId = String.valueOf(empPfNomineesDBO.empPfGratuityNomineesId);
							}
							if (!Utils.isNullOrEmpty(empPfNomineesDBO.nomineeAddress)) {
								empPfGratuityNomineesDTO.nomineeAddress = empPfNomineesDBO.nomineeAddress;
							}
							if (!Utils.isNullOrEmpty(empPfNomineesDBO.nomineeRelationship)) {
								empPfGratuityNomineesDTO.nomineeRelationship = empPfNomineesDBO.nomineeRelationship;
							}
							if (!Utils.isNullOrEmpty(empPfNomineesDBO.nomineeDob)) {
								empPfGratuityNomineesDTO.nomineeDob = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empPfNomineesDBO.nomineeDob.toString()));
							}
							if (!Utils.isNullOrEmpty(empPfNomineesDBO.sharePercentage)) {
								empPfGratuityNomineesDTO.sharePercentage = String.valueOf(empPfNomineesDBO.sharePercentage);
							}
							if (!Utils.isNullOrEmpty(empPfNomineesDBO.under18GuardName)) {
								empPfGratuityNomineesDTO.under18GuardName = String.valueOf(empPfNomineesDBO.under18GuardName);
							}
							if (!Utils.isNullOrEmpty(empPfNomineesDBO.under18GuardianAddress)) {
								empPfGratuityNomineesDTO.under18GuardianAddress = String.valueOf(empPfNomineesDBO.under18GuardianAddress);
							}
							if (!Utils.isNullOrEmpty(empPfNomineesDBO.nominee)) {
								empPfGratuityNomineesDTO.nominee = empPfNomineesDBO.nominee;
								pfNomineeDetailsDTO.nomineeDetails.add(empPfGratuityNomineesDTO);
							}
						});
					}
					if (!Utils.isNullOrEmpty(pfNomineeDetailsDTO)) {
						employeeProfileDTO.salaryAndLeaveDetails.pfAndNomineeDetails = pfNomineeDetailsDTO;
					}
					if (employeeProfileDTO.salaryAndLeaveDetails.pfAndNomineeDetails.nomineeDetails.size() == 0) {
						employeeProfileDTO.salaryAndLeaveDetails.pfAndNomineeDetails.nomineeDetails.add(new EmpPfGratuityNomineesDTO());
					}
				}
				if (!Utils.isNullOrEmpty(employee.get("gratuity_no"))) {
					gratuityNomineeDetailsDTO.accountNo = employee.get("gratuity_no").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("pf_date"))) {
					gratuityNomineeDetailsDTO.accountDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(employee.get("pf_date").toString()));
				}
				if (!Utils.isNullOrEmpty(jobDetailsId)) {
					gratuityNomineeDetailsDTO.nomineeDetails = new ArrayList<>();
					List<EmpPfGratuityNomineesDBO> empGratuityNomineesDBOList = employeeProfileTransaction.getEmpGratuityNomineesDBOList(jobDetailsId);
					if (!Utils.isNullOrEmpty(empGratuityNomineesDBOList)) {
						empGratuityNomineesDBOList.forEach(empGratuityNominees -> {
							EmpPfGratuityNomineesDTO gratuityNomineesDTO = new EmpPfGratuityNomineesDTO();
							if (!Utils.isNullOrEmpty(empGratuityNominees.empPfGratuityNomineesId)) {
								gratuityNomineesDTO.empPfGratuityNomineesId = String.valueOf(empGratuityNominees.empPfGratuityNomineesId);
							}
							if (!Utils.isNullOrEmpty(empGratuityNominees.nomineeAddress)) {
								gratuityNomineesDTO.nomineeAddress = empGratuityNominees.nomineeAddress;
							}
							if (!Utils.isNullOrEmpty(empGratuityNominees.nomineeRelationship)) {
								gratuityNomineesDTO.nomineeRelationship = empGratuityNominees.nomineeRelationship;
							}
							if (!Utils.isNullOrEmpty(empGratuityNominees.nomineeDob)) {
								gratuityNomineesDTO.nomineeDob = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(empGratuityNominees.nomineeDob.toString()));
							}
							if (!Utils.isNullOrEmpty(empGratuityNominees.sharePercentage)) {
								gratuityNomineesDTO.sharePercentage = String.valueOf(empGratuityNominees.sharePercentage);
							}
							if (!Utils.isNullOrEmpty(empGratuityNominees.under18GuardName)) {
								gratuityNomineesDTO.under18GuardName = String.valueOf(empGratuityNominees.under18GuardName);
							}
							if (!Utils.isNullOrEmpty(empGratuityNominees.under18GuardianAddress)) {
								gratuityNomineesDTO.under18GuardianAddress = String.valueOf(empGratuityNominees.under18GuardianAddress);
							}
							if (!Utils.isNullOrEmpty(empGratuityNominees.nominee)) {
								gratuityNomineesDTO.nominee = empGratuityNominees.nominee;
								gratuityNomineeDetailsDTO.nomineeDetails.add(gratuityNomineesDTO);
							}
						});
					}
					if (!Utils.isNullOrEmpty(gratuityNomineeDetailsDTO)) {
						employeeProfileDTO.salaryAndLeaveDetails.gratuvityAndNomineeDetails = gratuityNomineeDetailsDTO;
					}
					if (employeeProfileDTO.salaryAndLeaveDetails.gratuvityAndNomineeDetails.nomineeDetails.size() == 0) {
						employeeProfileDTO.salaryAndLeaveDetails.gratuvityAndNomineeDetails.nomineeDetails.add(new EmpPfGratuityNomineesDTO());
					}
				}
				//ErpAcademicYearDBO acadmeicYearDBO = employeeProfileTransaction.getCurrentAcademicYear();
				//				List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList =  employeeProfileTransaction.getEmployeeLeaveAllocation(Integer.parseInt(empId) ,acadmeicYearDBO.academicYear);
				List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = employeeProfileTransaction.getEmployeeLeaveAllocation(Integer.parseInt(empId));
				List<EmpLeaveAllocationDTO> empLeaveAllocationDTOList = new ArrayList<>();
				employeeProfileDTO.salaryAndLeaveDetails.leaveDetails = new EmployeeProfileLeaveDetailsDTO();
				employeeProfileDTO.salaryAndLeaveDetails.leaveDetails.leaveCategory = new LookupItemDTO();
				employeeProfileDTO.salaryAndLeaveDetails.leaveDetails.leaveTypeDetails = new ArrayList<>();
				if (!Utils.isNullOrEmpty(employee.get("emp_leave_category_allotment_id"))) {
					employeeProfileDTO.salaryAndLeaveDetails.leaveDetails.leaveCategory.value = employee.get("emp_leave_category_allotment_id").toString();
				}
				if (!Utils.isNullOrEmpty(employee.get("emp_leave_category_allotment_name"))) {
					employeeProfileDTO.salaryAndLeaveDetails.leaveDetails.leaveCategory.label = employee.get("emp_leave_category_allotment_name").toString();
				}
				if (!Utils.isNullOrEmpty(empLeaveAllocationDBOList)) {
					empLeaveAllocationDBOList.forEach(empLeaveAllocationDBO -> {
						EmpLeaveAllocationDTO empLeaveAllocationDTO = new EmpLeaveAllocationDTO();
						//					if(!Utils.isNullOrEmpty(empLeaveAllocationDBO.getYear())) {
						//						empLeaveAllocationDTO.setYearSelect(new SelectDTO());
						//						empLeaveAllocationDTO.getYearSelect().setLabel(empLeaveAllocationDBO.getYear().toString());
						//					}
						if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.id)) {
							empLeaveAllocationDTO.id = empLeaveAllocationDBO.id.toString();
						}
						if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.leaveType)) {
							if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.leaveType.leaveTypeName)) {
								empLeaveAllocationDTO.leaveTypeName = empLeaveAllocationDBO.leaveType.leaveTypeName;
							}
							if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.leaveType.id)) {
								empLeaveAllocationDTO.leaveTypeId = String.valueOf(empLeaveAllocationDBO.leaveType.id);
							}
						}
						if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.allottedLeaves)) {
							empLeaveAllocationDTO.leavesAllocated = empLeaveAllocationDBO.allottedLeaves.toString();
						}
						if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.sanctionedLeaves)) {
							empLeaveAllocationDTO.leavesSanctioned = empLeaveAllocationDBO.sanctionedLeaves.toString();
						}
						if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.leavesRemaining)) {
							empLeaveAllocationDTO.leavesRemaining = empLeaveAllocationDBO.leavesRemaining.toString();
						}
						if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.year)) {
							empLeaveAllocationDTO.setYearSelect(new SelectDTO());
							empLeaveAllocationDTO.getYearSelect().setLabel(empLeaveAllocationDBO.year.toString());
							//empLeaveAllocationDTO.year = empLeaveAllocationDBO.year.toString();
						}
						empLeaveAllocationDTOList.add(empLeaveAllocationDTO);
					});
				}
				if (empLeaveAllocationDTOList.size() > 0) {
					employeeProfileDTO.salaryAndLeaveDetails.leaveDetails.leaveTypeDetails = empLeaveAllocationDTOList;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeProfileDTO;
	}

	@SuppressWarnings({"unused", "unlikely-arg-type"})
	public boolean savePersonalDetails(EmployeeProfileDTO employeeProfile, String userId) {
		boolean isSaved = true;
		LocalDate localDate;
		EmpDBO empDBO = null;
		EmpPersonalDataDBO empPersonalDataDBO = null;
		EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO = null;
		EmpJobDetailsDBO empJobDetailsDBO = null;
		String empId = employeeProfile.personalDetails.basicInformation.employeeId;
		try {
			if (!Utils.isNullOrEmpty(empId)) {
				empDBO = commonApiTransaction.find(EmpDBO.class, Integer.parseInt(empId));
				if (!Utils.isNullOrEmpty(empDBO.getEmpPersonalDataDBO())) {
					//	empPersonalDataDBO = empDBO.empPersonalDataDBO;
					empPersonalDataDBO = Hibernate.unproxy(empDBO.getEmpPersonalDataDBO(), EmpPersonalDataDBO.class);
				} else
					empPersonalDataDBO = new EmpPersonalDataDBO();
				if (!Utils.isNullOrEmpty(empPersonalDataDBO.getEmpAddtnlPersonalDataDBO()))
					empAddtnlPersonalDataDBO = empPersonalDataDBO.getEmpAddtnlPersonalDataDBO();
				else
					empAddtnlPersonalDataDBO = new EmpAddtnlPersonalDataDBO();
				if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO())) {
//					empJobDetailsDBO = empDBO.empJobDetailsDBO;
					empJobDetailsDBO = Hibernate.unproxy(empDBO.getEmpJobDetailsDBO(), EmpJobDetailsDBO.class);
				} else
					empJobDetailsDBO = new EmpJobDetailsDBO();
				BasicInformationDTO basicInformation = employeeProfile.personalDetails.basicInformation;
				EmployeeProfileFamilyOrDependencyInformationDTO familyDependenyInformation = employeeProfile.personalDetails.familyOrDependencyInformation;
				if (!Utils.isNullOrEmpty(basicInformation.empNo)) {
					empDBO.empNumber = basicInformation.empNo;
				} else {
					empDBO.empNumber = null;
				}
				if (!Utils.isNullOrEmpty(basicInformation.employeeName)) {
					empDBO.empName = basicInformation.employeeName;
				}
				if (!Utils.isNullOrEmpty(basicInformation.gender)) {
					if (!Utils.isNullOrEmpty(basicInformation.gender.value)) {
						empDBO.erpGenderDBO = new ErpGenderDBO();
						empDBO.erpGenderDBO.erpGenderId = Integer.parseInt(basicInformation.gender.value);
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.dateOfBirth)) {
					empDBO.empDOB = LocalDate.parse(basicInformation.dateOfBirth, DateTimeFormatter.ofPattern("M/d/yyyy"));
				}
				if (!Utils.isNullOrEmpty(basicInformation.universityEmailId)) {
					empDBO.empUniversityEmail = basicInformation.universityEmailId;
				}
				if (!Utils.isNullOrEmpty(basicInformation.personalEmailId)) {
					empDBO.empPersonalEmail = basicInformation.personalEmailId;
				}
				if (!Utils.isNullOrEmpty(basicInformation.mobileNo)) {
					empDBO.empMobile = basicInformation.mobileNo;
				}
				if (!Utils.isNullOrEmpty(basicInformation.countryCode)) {
					if (!Utils.isNullOrEmpty(basicInformation.countryCode.label)) {
						empDBO.countryCode = basicInformation.countryCode.label;
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.nationality)) {
					if (!Utils.isNullOrEmpty(basicInformation.nationality.value)) {
						empPersonalDataDBO.erpCountryDBO = new ErpCountryDBO();
						empPersonalDataDBO.erpCountryDBO.id = Integer.parseInt(basicInformation.nationality.value);
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.maritalStatus)) {
					if (!Utils.isNullOrEmpty(basicInformation.maritalStatus.value)) {
						empPersonalDataDBO.erpMaritalStatusDBO = new ErpMaritalStatusDBO();
						empPersonalDataDBO.erpMaritalStatusDBO.id = Integer.parseInt(basicInformation.maritalStatus.value);
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.bloodGroup)) {
					if (!Utils.isNullOrEmpty(basicInformation.bloodGroup.value)) {
						empPersonalDataDBO.erpBloodGroupDBO = new ErpBloodGroupDBO();
						empPersonalDataDBO.erpBloodGroupDBO.id = Integer.parseInt(basicInformation.bloodGroup.value);
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.religion)) {
					if (!Utils.isNullOrEmpty(basicInformation.religion.value)) {
						empPersonalDataDBO.erpReligionDBO = new ErpReligionDBO();
						empPersonalDataDBO.erpReligionDBO.id = Integer.parseInt(basicInformation.religion.value);
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.reservationCategory)) {
					if (!Utils.isNullOrEmpty(basicInformation.reservationCategory.value)) {
						empPersonalDataDBO.erpReservationCategoryDBO = new ErpReservationCategoryDBO();
						empPersonalDataDBO.erpReservationCategoryDBO.id = Integer.parseInt(basicInformation.reservationCategory.value);
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.orcidNo)) {
					empPersonalDataDBO.orcidNo = basicInformation.orcidNo;
				}
				if (!Utils.isNullOrEmpty(basicInformation.getScopusNo())) {
					empPersonalDataDBO.setScopusNo(basicInformation.getScopusNo());
				}
				if (!Utils.isNullOrEmpty(basicInformation.getVidwnNo())) {
					empPersonalDataDBO.setVidwnNo(basicInformation.getVidwnNo());
				}
				if (!Utils.isNullOrEmpty(basicInformation.employeePhoto)) {              // doubt
					empPersonalDataDBO.profilePhotoUrl = basicInformation.employeePhoto;
				}
				if (!Utils.isNullOrEmpty(basicInformation)) {
					empPersonalDataDBO.profilePhotoUrl = basicInformation.employeePhoto;
				}
				if (!Utils.isNullOrEmpty(basicInformation.isDifferentiallyAbled)) {
					if (basicInformation.isDifferentiallyAbled.equalsIgnoreCase("yes")) {
						empPersonalDataDBO.isDifferentlyAbled = true;
						if (!Utils.isNullOrEmpty(basicInformation.disabilityType)) {
							if (!Utils.isNullOrEmpty(basicInformation.disabilityType.value)) {
								empPersonalDataDBO.erpDifferentlyAbledDBO = new ErpDifferentlyAbledDBO();
								empPersonalDataDBO.erpDifferentlyAbledDBO.id = Integer.parseInt(basicInformation.disabilityType.value);
							}
						}
					} else {
						empPersonalDataDBO.isDifferentlyAbled = false;
						empPersonalDataDBO.erpDifferentlyAbledDBO = null;
					}
				}
				//				System.out.println(employeeProfile.personalDetails.currentAddress.sameAsPermenent);
				EmployeeProfileAddressDTO address = employeeProfile.personalDetails.currentAddress;
				if (!Utils.isNullOrEmpty(address.addressLineOne)) {
					empPersonalDataDBO.currentAddressLine1 = address.addressLineOne;
				}
				if (!Utils.isNullOrEmpty(address.addressLineTwo)) {
					empPersonalDataDBO.currentAddressLine2 = address.addressLineTwo;
				}
				if (!Utils.isNullOrEmpty(address.country)) {
					if (!Utils.isNullOrEmpty(address.country.value)) {
						empPersonalDataDBO.currentCountry = new ErpCountryDBO();
						empPersonalDataDBO.currentCountry.id = Integer.parseInt(address.country.value);
					}
				}
				if (!Utils.isNullOrEmpty(address.pincode)) {
					empPersonalDataDBO.currentPincode = address.pincode;
				}
				if (!Utils.isNullOrEmpty(address.getDistrict().getValue()) && !Utils.isNullOrEmpty(address.getDistrict().getLabel())) {
					empPersonalDataDBO.setCurrentCityOthers(null);
					empPersonalDataDBO.setCurrentCity(new ErpCityDBO());
					empPersonalDataDBO.getCurrentCity().setId(Integer.parseInt(address.getDistrict().getValue()));
				}
				if (Utils.isNullOrEmpty(address.getDistrict().getValue()) && !Utils.isNullOrEmpty(address.getDistrict().getLabel())) {
					empPersonalDataDBO.setCurrentCity(null);
					empPersonalDataDBO.setCurrentCityOthers(address.getDistrict().getLabel());
				}
				if (!Utils.isNullOrEmpty(address.state)) {
					if (!Utils.isNullOrEmpty(address.state.value)) {
						empPersonalDataDBO.currentState = new ErpStateDBO();
						empPersonalDataDBO.currentState.id = Integer.parseInt(address.state.value);
					}
				}
				if (!Utils.isNullOrEmpty(address.sameAsPermenent)) {
					if (address.sameAsPermenent) {
						empPersonalDataDBO.isPermanentEqualsCurrent = true;
					} else {
						empPersonalDataDBO.isPermanentEqualsCurrent = false;
						address = employeeProfile.personalDetails.permanentAddress;
					}
				}
				if (!Utils.isNullOrEmpty(address.addressLineOne)) {
					empPersonalDataDBO.permanentAddressLine1 = address.addressLineOne;
				}
				if (!Utils.isNullOrEmpty(address.addressLineTwo)) {
					empPersonalDataDBO.permanentAddressLine2 = address.addressLineTwo;
				}
				if (!Utils.isNullOrEmpty(address.country)) {
					if (!Utils.isNullOrEmpty(address.country.value)) {
						empPersonalDataDBO.permanentCountry = new ErpCountryDBO();
						empPersonalDataDBO.permanentCountry.id = Integer.parseInt(address.country.value);
					}
				}
				if (!Utils.isNullOrEmpty(address.pincode)) {
					empPersonalDataDBO.permanentPincode = address.pincode;
				}
				if (!Utils.isNullOrEmpty(address.getDistrict().getValue()) && !Utils.isNullOrEmpty(address.getDistrict().getLabel())) {
					empPersonalDataDBO.setPermanentCityOthers(null);
					empPersonalDataDBO.setPermanentCity(new ErpCityDBO());
					empPersonalDataDBO.getPermanentCity().setId(Integer.parseInt(address.getDistrict().getValue()));
				}
				if (Utils.isNullOrEmpty(address.getDistrict().getValue()) && !Utils.isNullOrEmpty(address.getDistrict().getLabel())) {
					empPersonalDataDBO.setPermanentCity(null);
					empPersonalDataDBO.setPermanentCityOthers(address.getDistrict().getLabel());
				}
				if (!Utils.isNullOrEmpty(address.state)) {
					if (!Utils.isNullOrEmpty(address.state.value)) {
						empPersonalDataDBO.permanentState = new ErpStateDBO();
						empPersonalDataDBO.permanentState.id = Integer.parseInt(address.state.value);
					}
				}
				Set<EmpFamilyDetailsAddtnlDBO> empFamilyDetailsAddtnlDBOSet = new HashSet<>();
				EmpFamilyDetailsAddtnlDBO empFamilyDetailsAddtnlDBO = null;
				EmpFamilyDetailsAddtnlDTO familyDetails = null;
				if (!Utils.isNullOrEmpty(familyDependenyInformation) && !Utils.isNullOrEmpty(familyDependenyInformation.father)) {
					familyDetails = familyDependenyInformation.father;
					empFamilyDetailsAddtnlDBO = new EmpFamilyDetailsAddtnlDBO();
					if (!Utils.isNullOrEmpty(familyDetails.empFamilyDetailsAddtnlId)) {
						empFamilyDetailsAddtnlDBO.empFamilyDetailsAddtnlId = Integer.parseInt(familyDetails.empFamilyDetailsAddtnlId);
					}
					empFamilyDetailsAddtnlDBO.relationship = "FATHER";
					if (!Utils.isNullOrEmpty(familyDetails.dependentName)) {
						empFamilyDetailsAddtnlDBO.dependentName = familyDetails.dependentName;
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentDob)) {
						empFamilyDetailsAddtnlDBO.dependentDob = LocalDate.parse(familyDetails.dependentDob, DateTimeFormatter.ofPattern("M/d/yyyy"));
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentQualification)) {
						empFamilyDetailsAddtnlDBO.dependentQualification = familyDetails.dependentQualification;
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentProfession)) {
						empFamilyDetailsAddtnlDBO.dependentProfession = familyDetails.dependentProfession;
					}
					empPersonalDataDBO.setRecordStatus('A');
					empFamilyDetailsAddtnlDBO.empPersonalDataDBO = empPersonalDataDBO;
					if (!Utils.isNullOrEmpty(familyDetails.dependentName)) {
						empFamilyDetailsAddtnlDBOSet.add(empFamilyDetailsAddtnlDBO);
					}
				}

				if (!Utils.isNullOrEmpty(familyDependenyInformation) && !Utils.isNullOrEmpty(familyDependenyInformation.mother)) {
					empFamilyDetailsAddtnlDBO = new EmpFamilyDetailsAddtnlDBO();
					familyDetails = familyDependenyInformation.mother;
					if (!Utils.isNullOrEmpty(familyDetails.empFamilyDetailsAddtnlId)) {
						empFamilyDetailsAddtnlDBO.empFamilyDetailsAddtnlId = Integer.parseInt(familyDetails.empFamilyDetailsAddtnlId);
					}
					empFamilyDetailsAddtnlDBO.relationship = "MOTHER";
					if (!Utils.isNullOrEmpty(familyDetails.dependentName)) {
						empFamilyDetailsAddtnlDBO.dependentName = familyDetails.dependentName;
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentDob)) {
						empFamilyDetailsAddtnlDBO.dependentDob = LocalDate.parse(familyDetails.dependentDob, DateTimeFormatter.ofPattern("M/d/yyyy"));
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentQualification)) {
						empFamilyDetailsAddtnlDBO.dependentQualification = familyDetails.dependentQualification;
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentProfession)) {
						empFamilyDetailsAddtnlDBO.dependentProfession = familyDetails.dependentProfession;
					}
					empFamilyDetailsAddtnlDBO.empPersonalDataDBO = empPersonalDataDBO;
					if (!Utils.isNullOrEmpty(familyDetails.dependentName)) {
						empFamilyDetailsAddtnlDBOSet.add(empFamilyDetailsAddtnlDBO);
					}
				}

				if (!Utils.isNullOrEmpty(familyDependenyInformation) && !Utils.isNullOrEmpty(familyDependenyInformation.spouse)) {
					empFamilyDetailsAddtnlDBO = new EmpFamilyDetailsAddtnlDBO();
					familyDetails = familyDependenyInformation.spouse;
					if (!Utils.isNullOrEmpty(familyDetails.empFamilyDetailsAddtnlId)) {
						empFamilyDetailsAddtnlDBO.empFamilyDetailsAddtnlId = Integer.parseInt(familyDetails.empFamilyDetailsAddtnlId);
					}
					empFamilyDetailsAddtnlDBO.relationship = "SPOUSE";
					if (!Utils.isNullOrEmpty(familyDetails.dependentName)) {
						empFamilyDetailsAddtnlDBO.dependentName = familyDetails.dependentName;
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentDob)) {
						empFamilyDetailsAddtnlDBO.dependentDob = LocalDate.parse(familyDetails.dependentDob, DateTimeFormatter.ofPattern("M/d/yyyy"));
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentQualification)) {
						empFamilyDetailsAddtnlDBO.dependentQualification = familyDetails.dependentQualification;
					}
					if (!Utils.isNullOrEmpty(familyDetails.dependentProfession)) {
						empFamilyDetailsAddtnlDBO.dependentProfession = familyDetails.dependentProfession;
					}
					empFamilyDetailsAddtnlDBO.empPersonalDataDBO = empPersonalDataDBO;
					if (!Utils.isNullOrEmpty(familyDetails.dependentName)) {
						empFamilyDetailsAddtnlDBOSet.add(empFamilyDetailsAddtnlDBO);
					}
				}


				if (!Utils.isNullOrEmpty(familyDependenyInformation) && !Utils.isNullOrEmpty(familyDependenyInformation.childrenList)) {
					for (EmpFamilyDetailsAddtnlDTO children : familyDependenyInformation.childrenList) {
						EmpFamilyDetailsAddtnlDBO familyDetailsAddtnlDBO = new EmpFamilyDetailsAddtnlDBO();
						if (!Utils.isNullOrEmpty(children.empFamilyDetailsAddtnlId)) {
							familyDetailsAddtnlDBO.empFamilyDetailsAddtnlId = Integer.parseInt(children.empFamilyDetailsAddtnlId);
						}
						familyDetailsAddtnlDBO.relationship = "CHILD";
						if (!Utils.isNullOrEmpty(children.dependentName)) {
							familyDetailsAddtnlDBO.dependentName = children.dependentName;
						}
						if (!Utils.isNullOrEmpty(children.dependentDob)) {
							familyDetailsAddtnlDBO.dependentDob = LocalDate.parse(familyDetails.dependentDob, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(children.dependentQualification)) {
							familyDetailsAddtnlDBO.dependentQualification = children.dependentQualification;
						}
						if (!Utils.isNullOrEmpty(familyDetails.dependentProfession)) {
							familyDetailsAddtnlDBO.dependentProfession = children.dependentProfession;
						}
						familyDetailsAddtnlDBO.empPersonalDataDBO = empPersonalDataDBO;
						if (!Utils.isNullOrEmpty(children.dependentName)) {
							empFamilyDetailsAddtnlDBOSet.add(familyDetailsAddtnlDBO);
						}
					}
				}

				if (!Utils.isNullOrEmpty(familyDependenyInformation) && !Utils.isNullOrEmpty(familyDependenyInformation.dependentList)) {
					for (EmpFamilyDetailsAddtnlDTO dependent : familyDependenyInformation.dependentList) {
						EmpFamilyDetailsAddtnlDBO familyDetailsAddtnlDBO = new EmpFamilyDetailsAddtnlDBO();
						if (!Utils.isNullOrEmpty(dependent.empFamilyDetailsAddtnlId)) {
							familyDetailsAddtnlDBO.empFamilyDetailsAddtnlId = Integer.parseInt(dependent.empFamilyDetailsAddtnlId);
						}
						if (!Utils.isNullOrEmpty(dependent.relationship)) {
							familyDetailsAddtnlDBO.relationship = dependent.relationship.toUpperCase();
						}
						if (!Utils.isNullOrEmpty(dependent.dependentName)) {
							familyDetailsAddtnlDBO.dependentName = dependent.dependentName;
						}
						if (!Utils.isNullOrEmpty(dependent.dependentDob)) {
							familyDetailsAddtnlDBO.dependentDob = LocalDate.parse(dependent.dependentDob, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(dependent.dependentQualification)) {
							familyDetailsAddtnlDBO.dependentQualification = dependent.dependentQualification;
						}
						if (!Utils.isNullOrEmpty(familyDetails.dependentProfession)) {
							familyDetailsAddtnlDBO.dependentProfession = dependent.dependentProfession;
						}
						familyDetailsAddtnlDBO.empPersonalDataDBO = empPersonalDataDBO;
						if (!Utils.isNullOrEmpty(dependent.dependentName)) {
							empFamilyDetailsAddtnlDBOSet.add(familyDetailsAddtnlDBO);
						}
					}
				}

				Set<EmpFamilyDetailsAddtnlDBO> orginalFamilyAddtnDBOSet = empPersonalDataDBO.empFamilyDetailsAddtnlDBOS;
				Set<EmpFamilyDetailsAddtnlDBO> updateFamilyAddtnDBOSet = new HashSet<>();
				Map<Integer, EmpFamilyDetailsAddtnlDBO> existDBOMap = new HashMap<>();
				if (!Utils.isNullOrEmpty(orginalFamilyAddtnDBOSet)) {
					orginalFamilyAddtnDBOSet.forEach(dbo -> {
						if (dbo.recordStatus == 'A') {
							existDBOMap.put(dbo.empFamilyDetailsAddtnlId, dbo);
						}
					});
				}
				if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBOSet)) {
					for (EmpFamilyDetailsAddtnlDBO item : empFamilyDetailsAddtnlDBOSet) {
						EmpFamilyDetailsAddtnlDBO family = null;
						if (existDBOMap.containsKey(item.empFamilyDetailsAddtnlId)) {
							family = item;
							family.recordStatus = 'A';
							family.modifiedUsersId = Integer.parseInt(userId);
							existDBOMap.remove(item.empFamilyDetailsAddtnlId);
						} else {
							family = item;
							family.recordStatus = 'A';
							family.createdUsersId = Integer.parseInt(userId);
							family.empPersonalDataDBO = empPersonalDataDBO;
						}
						updateFamilyAddtnDBOSet.add(family);
					}
				}

				if (!Utils.isNullOrEmpty(existDBOMap)) {
					existDBOMap.forEach((entry, value) -> {
						value.modifiedUsersId = Integer.parseInt(userId);
						value.recordStatus = 'D';
						updateFamilyAddtnDBOSet.add(value);
					});
				}
				if (!Utils.isNullOrEmpty(updateFamilyAddtnDBOSet)) {
					empPersonalDataDBO.empFamilyDetailsAddtnlDBOS = updateFamilyAddtnDBOSet;
				}
				if (!Utils.isNullOrEmpty(basicInformation.adharNo)) {
					empAddtnlPersonalDataDBO.aadharNo = basicInformation.adharNo;
				}
				if (!Utils.isNullOrEmpty(basicInformation.getAdharUploadUrl())) {
					if (!Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getAdharUploadUrl())) {
						if (!empAddtnlPersonalDataDBO.getAdharUploadUrl().equals(basicInformation.getAdharUploadUrl())) {
							String item = empAddtnlPersonalDataDBO.getAdharUploadUrl();
							String[] res = item.split("[.]", 0);
							String fileName = res[0];
							String extension = res[1];
							File file = new File("ImageUpload//" + fileName + "." + extension);
							file = new File(file.getAbsolutePath());
							if (file.exists()) {
								file.delete();
							}
							empAddtnlPersonalDataDBO.setAdharUploadUrl(basicInformation.getAdharUploadUrl());
						} else {
							empAddtnlPersonalDataDBO.setAdharUploadUrl(basicInformation.getAdharUploadUrl());
						}
					} else {
						empAddtnlPersonalDataDBO.setAdharUploadUrl(basicInformation.getAdharUploadUrl());
					}
				} else {
					if (!Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getAdharUploadUrl())) {
						String item = empAddtnlPersonalDataDBO.getAdharUploadUrl();
						String[] res = item.split("[.]", 0);
						String fileName = res[0];
						String extension = res[1];
						File file = new File("ImageUpload//" + fileName + "." + extension);
						file = new File(file.getAbsolutePath());
						if (file.exists()) {
							file.delete();
						}
						empAddtnlPersonalDataDBO.setAdharUploadUrl(null);
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.panNo)) {
					empAddtnlPersonalDataDBO.panNo = basicInformation.panNo;
				}
				if (!Utils.isNullOrEmpty(basicInformation.getPanUploadUrl())) {
					if (!Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getPanUploadUrl())) {
						if (!Utils.isNullOrEmpty(basicInformation.getPanUploadUrl())) {
							if (!empAddtnlPersonalDataDBO.getPanUploadUrl().equals(basicInformation.getPanUploadUrl())) {
								String item = empAddtnlPersonalDataDBO.getPanUploadUrl();
								String[] res = item.split("[.]", 0);
								String fileName = res[0];
								String extension = res[1];
								File file = new File("ImageUpload//" + fileName + "." + extension);
								file = new File(file.getAbsolutePath());
								if (file.exists()) {
									file.delete();
								}
								empAddtnlPersonalDataDBO.setPanUploadUrl(basicInformation.getPanUploadUrl());
							} else {
								empAddtnlPersonalDataDBO.setPanUploadUrl(basicInformation.getPanUploadUrl());
							}
						}
					} else {
						empAddtnlPersonalDataDBO.setPanUploadUrl(basicInformation.getPanUploadUrl());
					}
				} else {
					if (!Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getPanUploadUrl())) {
						String item = empAddtnlPersonalDataDBO.getPanUploadUrl();
						String[] res = item.split("[.]", 0);
						String fileName = res[0];
						String extension = res[1];
						File file = new File("ImageUpload//" + fileName + "." + extension);
						file = new File(file.getAbsolutePath());
						if (file.exists()) {
							file.delete();
						}
						empAddtnlPersonalDataDBO.setPanUploadUrl(null);
					}
				}
				if (!Utils.isNullOrEmpty(basicInformation.fourWheelerNo)) {
					empAddtnlPersonalDataDBO.fourWheelerNo = basicInformation.fourWheelerNo;
				}
				if (!Utils.isNullOrEmpty(basicInformation.tworWheelerNo)) {
					empAddtnlPersonalDataDBO.twoWheelerNo = basicInformation.tworWheelerNo;
				}
				if (!Utils.isNullOrEmpty(basicInformation.fourWheelerDocumentUrl)) {
					empAddtnlPersonalDataDBO.fourWheelerDocumentUrl = basicInformation.fourWheelerDocumentUrl;
				}
				if (!Utils.isNullOrEmpty(basicInformation.tworWheelerDocumentUrl)) {
					empAddtnlPersonalDataDBO.twoWheelerDocumentUrl = basicInformation.tworWheelerDocumentUrl;
				}
				GovernmentDocumentDetailsDTO governmentDocumentDetails = employeeProfile.personalDetails.passportDetails;
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.no)) {
					empAddtnlPersonalDataDBO.passportNo = governmentDocumentDetails.no;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.placeOfIssue)) {
					empAddtnlPersonalDataDBO.passportIssuedPlace = governmentDocumentDetails.placeOfIssue;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.status)) {
					empAddtnlPersonalDataDBO.passportStatus = governmentDocumentDetails.status;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.issuedDate)) {
					empAddtnlPersonalDataDBO.passportIssuedDate = LocalDate.parse(governmentDocumentDetails.issuedDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.expiryDate)) {
					empAddtnlPersonalDataDBO.passportDateOfExpiry = LocalDate.parse(governmentDocumentDetails.expiryDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.documentUrl)) {
					empAddtnlPersonalDataDBO.passportUploadUrl = governmentDocumentDetails.documentUrl;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.comments)) {
					empAddtnlPersonalDataDBO.passportComments = governmentDocumentDetails.comments;
				}
				governmentDocumentDetails = employeeProfile.personalDetails.visaAndFrroDetails.get("frroDetails");
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.no)) {
					empAddtnlPersonalDataDBO.frroNo = governmentDocumentDetails.no;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.issuedDate)) {
					empAddtnlPersonalDataDBO.frroIssuedDate = LocalDate.parse(governmentDocumentDetails.issuedDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.expiryDate)) {
					empAddtnlPersonalDataDBO.frroDateOfExpiry = LocalDate.parse(governmentDocumentDetails.expiryDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.status)) {
					empAddtnlPersonalDataDBO.frroStatus = governmentDocumentDetails.status;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.documentUrl)) {
					empAddtnlPersonalDataDBO.frroUploadUrl = governmentDocumentDetails.documentUrl;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.comments)) {
					empAddtnlPersonalDataDBO.frroComments = governmentDocumentDetails.comments;
				}
				governmentDocumentDetails = employeeProfile.personalDetails.visaAndFrroDetails.get("visaDetails");
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.no)) {
					empAddtnlPersonalDataDBO.visaNo = governmentDocumentDetails.no;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.issuedDate)) {
					empAddtnlPersonalDataDBO.visaIssuedDate = LocalDate.parse(governmentDocumentDetails.issuedDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.expiryDate)) {
					empAddtnlPersonalDataDBO.visaDateOfExpiry = LocalDate.parse(governmentDocumentDetails.expiryDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.status)) {
					empAddtnlPersonalDataDBO.visaStatus = governmentDocumentDetails.status;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.documentUrl)) {
					empAddtnlPersonalDataDBO.visaUploadUrl = governmentDocumentDetails.documentUrl;
				}
				if (!Utils.isNullOrEmpty(governmentDocumentDetails.comments)) {
					empAddtnlPersonalDataDBO.visaComments = governmentDocumentDetails.comments;
				}
				if (!Utils.isNullOrEmpty(employeeProfile.personalDetails.emergencyContact)) {
					Map<String, String> emergencyContact = employeeProfile.personalDetails.emergencyContact;
					if (!Utils.isNullOrEmpty(emergencyContact.containsKey(address))) {
						empAddtnlPersonalDataDBO.emergencyContactAddress = emergencyContact.get("address");
					}
					if (!Utils.isNullOrEmpty(emergencyContact.containsKey("telephoneWork"))) {
						empAddtnlPersonalDataDBO.emergencyContactWork = emergencyContact.get("telephoneWork");
					}
					if (!Utils.isNullOrEmpty(emergencyContact.containsKey("name"))) {
						empAddtnlPersonalDataDBO.emergencyContactName = emergencyContact.get("name");
					}
					if (!Utils.isNullOrEmpty(emergencyContact.containsKey("mobileNo"))) {
						empAddtnlPersonalDataDBO.emergencyMobileNo = emergencyContact.get("mobileNo");
					}
					if (!Utils.isNullOrEmpty(emergencyContact.containsKey("relationship"))) {
						empAddtnlPersonalDataDBO.emergencyContactRelationship = emergencyContact.get("relationship");
					}
					if (!Utils.isNullOrEmpty(emergencyContact.containsKey("telephoneHome"))) {
						empAddtnlPersonalDataDBO.emergencyContactHome = emergencyContact.get("telephoneHome");
					}
				}
				if (!Utils.isNullOrEmpty(familyDependenyInformation.familyBackgroundBrief)) {
					empAddtnlPersonalDataDBO.familyBackgroundBrief = familyDependenyInformation.familyBackgroundBrief;
				}
				empAddtnlPersonalDataDBO.setRecordStatus('A');
				empPersonalDataDBO.empAddtnlPersonalDataDBO = empAddtnlPersonalDataDBO;
				if (!Utils.isNullOrEmpty(basicInformation.smartCardNo)) {
					empJobDetailsDBO.setEmpDBO(empDBO);
					empJobDetailsDBO.smartCardNo = basicInformation.smartCardNo;
				} else {
					empJobDetailsDBO.smartCardNo = basicInformation.smartCardNo;
				}
				if (!Utils.isNullOrEmpty(basicInformation.bankAccountNo)) {
					empJobDetailsDBO.sibAccountBank = basicInformation.bankAccountNo;
				} else {
					empJobDetailsDBO.sibAccountBank = basicInformation.bankAccountNo;
				}
				if (!Utils.isNullOrEmpty(basicInformation.ifscCode)) {
					empJobDetailsDBO.branchIfscCode = basicInformation.ifscCode;
				} else {
					empJobDetailsDBO.branchIfscCode = basicInformation.ifscCode;
				}
				if (!Utils.isNullOrEmpty(empPersonalDataDBO)) {
					empPersonalDataDBO.recordStatus = 'A';
					empAddtnlPersonalDataDBO.setEmpPersonalDataDBO(empPersonalDataDBO);
					empDBO.setEmpPersonalDataDBO(empPersonalDataDBO);
				}
				if (!Utils.isNullOrEmpty(empAddtnlPersonalDataDBO)) {
					empAddtnlPersonalDataDBO.setRecordStatus('A');
					empPersonalDataDBO.setEmpAddtnlPersonalDataDBO(empAddtnlPersonalDataDBO);
				}
				if (!Utils.isNullOrEmpty(empJobDetailsDBO)) {
					empJobDetailsDBO.setRecordStatus('A');
					empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
				}
				employeeProfileTransaction.saveData(empDBO);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSaved;
	}

	@SuppressWarnings({"unused"})
	public boolean saveJobDetails(EmployeeProfileDTO employeeProfile, String userId) {
		boolean Saved = false;
		EmpDBO empDBO = null;
		EmpPersonalDataDBO empPersonalDataDBO = null;
		EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO = null;
		EmpJobDetailsDBO empJobDetailsDBO = null;
		EmpGuestContractDetailsDTO guestContractDetailsDBO = null;
		ErpRoomsDBO erpRoomsDBO = null;
		ErpRoomEmpMappingDBO roomMappingDBO = null;
		EmpApproversDBO approversDBO = null;
		EmpResignationDBO empResignationDBO = null;
		EmpEducationalDetailsDBO educationalDetailsDBO = null;
		Set<EmpApproversDetailsDBO> empApproversDetailsDBOSet = null;
		String empId = employeeProfile.personalDetails.basicInformation.employeeId;
		try {
			if (!Utils.isNullOrEmpty(empId)) {
				empDBO = commonApiTransaction.find(EmpDBO.class, Integer.parseInt(empId));
				if (!Utils.isNullOrEmpty(empDBO.getEmpPersonalDataDBO())) {
					//				empPersonalDataDBO = empDBO.empPersonalDataDBO;
					empPersonalDataDBO = Hibernate.unproxy(empDBO.getEmpPersonalDataDBO(), EmpPersonalDataDBO.class);
				}
				if (!Utils.isNullOrEmpty(empPersonalDataDBO.getEmpAddtnlPersonalDataDBO())) {
					empAddtnlPersonalDataDBO = empPersonalDataDBO.getEmpAddtnlPersonalDataDBO();
				}
				if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO())) {
					empJobDetailsDBO = empDBO.getEmpJobDetailsDBO();
					empJobDetailsDBO.setEmpDBO(empDBO);
				}
				if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO())) {
					//				empJobDetailsDBO = empDBO.empJobDetailsDBO;
					empJobDetailsDBO = Hibernate.unproxy(empDBO.getEmpJobDetailsDBO(), EmpJobDetailsDBO.class);
				} else {
					empJobDetailsDBO = new EmpJobDetailsDBO();
					empJobDetailsDBO.empDBO = empDBO;
					if (!Utils.isNullOrEmpty(empDBO.getEmpApplnEntriesDBO())) {
						empJobDetailsDBO.empApplnEntriesId = empDBO.getEmpApplnEntriesDBO();
					}
					empJobDetailsDBO.recordStatus = 'A';
				}
				JobDetailsDTO jobDetails = employeeProfile.jobDetails.jobInformation;
				EmployeeProfileJobDetailsDTO empJobDetails = jobDetails.empJobDetails;
				EmpGuestContractDetailsDTO empGuestContractDetailsDTO = employeeProfile.jobDetails.jobInformation.empGuestContractDetailsDTO;
				ErpRoomsDTO erpRooms = jobDetails.erpRoomEmpMappingDTO;
				EmpApproversDetailsDTO approversDetails = employeeProfile.jobDetails.approverDetails;
				EmpResignationDTO resignationDetails = employeeProfile.jobDetails.resignationDetails;
				WorkTimeDetailsDTO workTimeDetails = employeeProfile.jobDetails.workTimeDetails;
				List<EmployeeProfileAdditionalInformation> additionalInformationList = employeeProfile.jobDetails.additionalInformation;
				List<EmployeeProfileLetterDTO> promotionDetails = employeeProfile.jobDetails.promotionDetails;
				List<EmpLevelsAndPromotionsDTO> empLevelsAndPromotionsDTOList = employeeProfile.getJobDetails().getEmpLevelsAndPromotionsDTO();
				EmpDTO empDTO = jobDetails.empDTO;
				if (!Utils.isNullOrEmpty(empDTO.employeeCampus) && !Utils.isNullOrEmpty(empDTO.employeeDepartment)) {
					if (!Utils.isNullOrEmpty(empDTO.employeeCampus.value) && !Utils.isNullOrEmpty(empDTO.employeeDepartment.value)) {
						empDBO.erpCampusDepartmentMappingDBO = new ErpCampusDepartmentMappingDBO();
						empDBO.erpCampusDepartmentMappingDBO = employeeProfileTransaction.getCampusDepartmentMapping(empDTO.employeeCampus.value, empDTO.employeeDepartment.value);
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.employeeCategory)) {
					if (!Utils.isNullOrEmpty(empDTO.employeeCategory.value)) {
						empDBO.empEmployeeCategoryDBO = new EmpEmployeeCategoryDBO();
						empDBO.empEmployeeCategoryDBO.id = Integer.parseInt(empDTO.employeeCategory.value);
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.jobCategory)) {
					if (!Utils.isNullOrEmpty(empDTO.jobCategory.value)) {
						empDBO.empEmployeeJobCategoryDBO = new EmpEmployeeJobCategoryDBO();
						empDBO.empEmployeeJobCategoryDBO.id = Integer.parseInt(empDTO.jobCategory.value);
					}
				}
				if (!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode))) {
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") ||
							(empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE")) ||
							(empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL"))) {
						if (!Utils.isNullOrEmpty(empDTO.employeeGroup)) {
							if (!Utils.isNullOrEmpty(empDTO.employeeGroup.value)) {
								empDBO.empEmployeeGroupDBO = new EmpEmployeeGroupDBO();
								empDBO.empEmployeeGroupDBO.id = Integer.parseInt(empDTO.employeeGroup.value);
							}
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.empEmployeeGroupDBO))
							empDBO.empEmployeeGroupDBO = null;
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.employeeDesignation)) {
					if (!Utils.isNullOrEmpty(empDTO.employeeDesignation.value)) {
						empDBO.empDesignationDBO = new EmpDesignationDBO();
						empDBO.empDesignationDBO.id = Integer.parseInt(empDTO.employeeDesignation.value);
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.employeeTitle)) {
					if (!Utils.isNullOrEmpty(empDTO.employeeTitle.value)) {
						empDBO.erpEmployeeTitleDBO = new ErpEmployeeTitleDBO();
						empDBO.erpEmployeeTitleDBO.id = Integer.parseInt(empDTO.employeeTitle.value);
					}
				}
				if (!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode))) {
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL")
							|| empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("VISITING") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING")) {
						if (!Utils.isNullOrEmpty(empDTO.employeeDeputedDepartmentTitle)) {
							if (!Utils.isNullOrEmpty(empDTO.employeeDeputedDepartmentTitle.value)) {
								empDBO.deputationDepartmentTitleDBO = new EmpTitleDBO();
								empDBO.deputationDepartmentTitleDBO.id = Integer.parseInt(empDTO.employeeDeputedDepartmentTitle.value);
							}
						}
						if (!Utils.isNullOrEmpty(empDTO.deputationStartDate)) {
							empDBO.depautationStartDate = LocalDate.parse(empDTO.deputationStartDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.deputationDepartmentTitleDBO)) {
							empDBO.deputationDepartmentTitleDBO = null;
						}
						if (!Utils.isNullOrEmpty(empDBO.depautationStartDate)) {
							empDBO.depautationStartDate = null;
						}
					}
				}
				if (!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode))) {
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL")
							|| empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("VISITING") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING")) {
						if (!Utils.isNullOrEmpty(empDTO.employeeDesignationForStaffAlbum)) {
							if (!Utils.isNullOrEmpty(empDTO.employeeDesignationForStaffAlbum.value)) {
								empDBO.empAlbumDesignationDBO = new EmpDesignationDBO();
								empDBO.empAlbumDesignationDBO.id = Integer.parseInt(empDTO.employeeDesignationForStaffAlbum.value);
							}
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.empAlbumDesignationDBO)) {
							empDBO.empAlbumDesignationDBO = null;
						}
					}
				}
				if (!Utils.isNullOrEmpty(empJobDetails.isEmployeeActive)) {
					if (empJobDetails.isEmployeeActive.equalsIgnoreCase("Yes")) {
						empDBO.recordStatus = 'A';
					} else {
						empDBO.recordStatus = 'I';
					}
				}
				if (!Utils.isNullOrEmpty(workTimeDetails.generalTimeZone)) {
					if (!Utils.isNullOrEmpty(workTimeDetails.generalTimeZone.value)) {
						empDBO.empTimeZoneDBO = new EmpTimeZoneDBO();
						empDBO.empTimeZoneDBO.setId(Integer.parseInt(workTimeDetails.generalTimeZone.value));
					}
				}
				if (!Utils.isNullOrEmpty(jobDetails.subjectCategory)) {
					if (!Utils.isNullOrEmpty(jobDetails.subjectCategory.value)) {
						empDBO.empApplnSubjectCategoryDBO = new EmpApplnSubjectCategoryDBO();
						empDBO.empApplnSubjectCategoryDBO.id = Integer.parseInt(jobDetails.subjectCategory.value);
					}
				}
				if (!Utils.isNullOrEmpty(jobDetails.subjectSpecialization)) {
					if (!Utils.isNullOrEmpty(jobDetails.subjectSpecialization.value)) {
						empDBO.empApplnSubjectCategorySpecializationDBO = new EmpApplnSubjectCategorySpecializationDBO();
						empDBO.empApplnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId = Integer.parseInt(jobDetails.subjectSpecialization.value);
					}
				}
				if (!Utils.isNullOrEmpty(empJobDetails.isVacationApplicable)) {
					if (empJobDetails.isVacationApplicable.equalsIgnoreCase("Yes")) {
						empJobDetailsDBO.isVacationApplicable = true;
					} else {
						empJobDetailsDBO.isVacationApplicable = false;
					}
				}
				if (!Utils.isNullOrEmpty(empJobDetails.isDisplayWebsite)) {
					if (empJobDetails.isDisplayWebsite.equalsIgnoreCase("Yes")) {
						empJobDetailsDBO.isDisplayWebsite = true;
					} else {
						empJobDetailsDBO.isDisplayWebsite = false;
					}
				}
				if (!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode))) {
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL")
							|| empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("VISITING") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING")) {
						if (!Utils.isNullOrEmpty(empJobDetails.joiningDate)) {
							LocalDate localDate = LocalDate.parse(empJobDetails.joiningDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
							LocalTime timePart = LocalTime.parse("00:00:00");
							LocalDateTime localDateTime = LocalDateTime.of(localDate, timePart);
							empDBO.setEmpDOJ(localDate);
							//		empJobDetailsDBO.joiningDate = localDateTime;
						}
					} else {
						if (!Utils.isNullOrEmpty(empJobDetailsDBO.joiningDate)) {
							empJobDetailsDBO.joiningDate = null;
						}
					}
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("CONTRACT") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL")) {
						if (!Utils.isNullOrEmpty(empJobDetails.retirementDate)) {
							empJobDetailsDBO.retirementDate = LocalDate.parse(empJobDetails.retirementDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
					} else {
						if (!Utils.isNullOrEmpty(empJobDetailsDBO.retirementDate)) {
							empJobDetailsDBO.setRetirementDate(null);
						}
					}
				}
				if (!Utils.isNullOrEmpty(workTimeDetails.isHolidayTimeZoneApplicable)) {
					if (workTimeDetails.isHolidayTimeZoneApplicable.equalsIgnoreCase("Yes")) {
						empJobDetailsDBO.isHolidayTimeZoneApplicable = true;
						if (!Utils.isNullOrEmpty(workTimeDetails.holidayTimeZone)) {
							if (!Utils.isNullOrEmpty(workTimeDetails.holidayTimeZone.value)) {
								empJobDetailsDBO.holidayTimeZoneDBO = new EmpTimeZoneDBO();
								empJobDetailsDBO.holidayTimeZoneDBO.setId(Integer.parseInt(workTimeDetails.holidayTimeZone.value));
							}
						}
					} else {
						empJobDetailsDBO.isHolidayTimeZoneApplicable = false;
						empJobDetailsDBO.holidayTimeZoneDBO = null;
					}
				}
				if (!Utils.isNullOrEmpty(workTimeDetails.isVacationTimeZoneApplicable)) {
					if (workTimeDetails.isVacationTimeZoneApplicable.equalsIgnoreCase("Yes")) {
						empJobDetailsDBO.isVacationTimeZoneApplicable = true;
						if (!Utils.isNullOrEmpty(workTimeDetails.vacationTimeZone)) {
							if (!Utils.isNullOrEmpty(workTimeDetails.vacationTimeZone.value)) {
								empJobDetailsDBO.vacationTimeZoneDBO = new EmpTimeZoneDBO();
								empJobDetailsDBO.vacationTimeZoneDBO.setId(Integer.parseInt(workTimeDetails.vacationTimeZone.value));
							}
						}
					} else {
						empJobDetailsDBO.isVacationTimeZoneApplicable = false;
						empJobDetailsDBO.setVacationTimeZoneDBO(null);
					}
				}
				if (!Utils.isNullOrEmpty(workTimeDetails.isDutyRosterApplicable)) {
					if (workTimeDetails.isDutyRosterApplicable.equalsIgnoreCase("Yes")) {
						empJobDetailsDBO.isDutyRosterApplicable = true;
					} else {
						empJobDetailsDBO.isDutyRosterApplicable = false;
					}
				}
//				System.out.println(empJobDetailsDBO.getId());
				empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
				if (!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode))) {
					if (!empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("CONTRACT")) {
						if (!Utils.isNullOrEmpty(empDBO.erpRoomEmpMappingDBO)) {
							if (!Utils.isNullOrEmpty(erpRooms.getRoom())) {
								if (!Utils.isNullOrEmpty(erpRooms.getRoom().getValue())) {
									roomMappingDBO = empDBO.erpRoomEmpMappingDBO;
									roomMappingDBO.setRecordStatus('A');
									if (!Utils.isNullOrEmpty(empDBO.erpRoomEmpMappingDBO.erpRoomsDBO)) {
										erpRoomsDBO = empDBO.erpRoomEmpMappingDBO.erpRoomsDBO;
									} else {
										roomMappingDBO.erpRoomsDBO = new ErpRoomsDBO();
									}
									///erpRoomsDBO.setId(Integer.parseInt(erpRooms.getRoom().getValue()));
									roomMappingDBO.erpRoomsDBO.id = (Integer.parseInt(erpRooms.getRoom().getValue()));
								}
							}
						} else if (!Utils.isNullOrEmpty(erpRooms.telephoneExtention) || !Utils.isNullOrEmpty(erpRooms.telephoneOffice) ||
								(!Utils.isNullOrEmpty(erpRooms.getRoom()) && !Utils.isNullOrEmpty(erpRooms.getRoom().getValue()))) {
							roomMappingDBO = new ErpRoomEmpMappingDBO();
							if (!Utils.isNullOrEmpty(erpRooms.getRoom())) {
								if (!Utils.isNullOrEmpty(erpRooms.getRoom().getValue())) {
									roomMappingDBO.erpRoomsDBO = new ErpRoomsDBO();
									roomMappingDBO.erpRoomsDBO.id = Integer.parseInt(erpRooms.getRoom().getValue());
								}
							}
							if (!Utils.isNullOrEmpty(empId)) {
								roomMappingDBO.setEmpDBO(empDBO);
							}
							roomMappingDBO.setRecordStatus('A');
						}
						if (!Utils.isNullOrEmpty(erpRooms.telephoneExtention)) {
							roomMappingDBO.telephoneExtension = Integer.parseInt(erpRooms.telephoneExtention);
						}
						if (!Utils.isNullOrEmpty(erpRooms.telephoneOffice)) {
							roomMappingDBO.telephoneNumber = erpRooms.telephoneOffice;
						}
						if (!Utils.isNullOrEmpty(roomMappingDBO)) {
							empDBO.erpRoomEmpMappingDBO = roomMappingDBO;
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.erpRoomEmpMappingDBO)) {
							empDBO.erpRoomEmpMappingDBO.recordStatus = 'D';
						}
					}
				}
				Set<EmpRemarksDetailsDBO> empRemarksDetailsDBOSet = new HashSet<>();
				if (!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode))) {
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL")
							|| empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING")) {
						if (!Utils.isNullOrEmpty(employeeProfile.jobDetails.remarks)) {
							for (EmpRemarksDTO remarks : employeeProfile.jobDetails.remarks) {
								if (!Utils.isNullOrEmpty(remarks.remarksDate) || !Utils.isNullOrEmpty(remarks.remarksDetails) || !Utils.isNullOrEmpty(remarks.remarksUploadUrl) ||
										!Utils.isNullOrEmpty(remarks.remarksRefNo)) {
									EmpRemarksDetailsDBO remarksDetailsDBO = new EmpRemarksDetailsDBO();
									remarksDetailsDBO.empDBO = empDBO;
									if (!Utils.isNullOrEmpty(remarks.id)) {
										remarksDetailsDBO.id = Integer.parseInt(remarks.id);
									}
									if (!Utils.isNullOrEmpty(remarks.remarksDate)) {
										remarksDetailsDBO.remarksDate = LocalDate.parse(remarks.remarksDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
									}
									if (!Utils.isNullOrEmpty(remarks.remarksDetails)) {
										remarksDetailsDBO.remarksDetails = remarks.remarksDetails;
									}
									if (!Utils.isNullOrEmpty(remarks.remarksUploadUrl)) {
										remarksDetailsDBO.remarksUploadUrl = remarks.remarksUploadUrl;
									}
									if (!Utils.isNullOrEmpty(remarks.remarksRefNo)) {
										remarksDetailsDBO.remarksRefNo = remarks.remarksRefNo;
									}
									remarksDetailsDBO.isForOfficeUse = true;
									empRemarksDetailsDBOSet.add(remarksDetailsDBO);
								}
							}
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.getEmpRemarksDetailsDBOSet())) {
							List<EmpRemarksDetailsDBO> list = employeeProfileTransaction.getRemarksDetails(empId);
							if (!Utils.isNullOrEmpty(list)) {
								list.forEach(details -> {
									details.recordStatus = 'D';
								});
								empRemarksDetailsDBOSet = list.stream().collect(Collectors.toSet());
								empDBO.setEmpRemarksDetailsDBOSet(empRemarksDetailsDBOSet);
							}
						}
					}
				}
				if (!Utils.isNullOrEmpty(employeeProfile) && !Utils.isNullOrEmpty(employeeProfile.jobDetails) && !Utils.isNullOrEmpty(employeeProfile.jobDetails.otherInfo)) {
					EmpRemarksDTO remarks = employeeProfile.jobDetails.otherInfo;
					if (!Utils.isNullOrEmpty(remarks.remarksDate) || !Utils.isNullOrEmpty(remarks.remarksDetails) || !Utils.isNullOrEmpty(remarks.remarksUploadUrl) || !Utils.isNullOrEmpty(remarks.remarksRefNo)) {
						EmpRemarksDetailsDBO remarksDetailsDBO = new EmpRemarksDetailsDBO();
						remarksDetailsDBO.empDBO = empDBO;
						if (!Utils.isNullOrEmpty(remarks.id)) {
							remarksDetailsDBO.id = Integer.parseInt(remarks.id);
						}
						if (!Utils.isNullOrEmpty(remarks.remarksDate)) {
							remarksDetailsDBO.remarksDate = LocalDate.parse(remarks.remarksDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(remarks.remarksDetails)) {
							remarksDetailsDBO.remarksDetails = remarks.remarksDetails;
						}
						if (!Utils.isNullOrEmpty(remarks.remarksUploadUrl)) {
							remarksDetailsDBO.remarksUploadUrl = remarks.remarksUploadUrl;
						}
						if (!Utils.isNullOrEmpty(remarks.remarksRefNo)) {
							remarksDetailsDBO.remarksRefNo = remarks.remarksRefNo;
						}
						remarksDetailsDBO.isForOfficeUse = false;
						if (!Utils.isNullOrEmpty(remarksDetailsDBO)) {
							empRemarksDetailsDBOSet.add(remarksDetailsDBO);
						}
					}
				}
				List<EmpRemarksDetailsDBO> orginalRemarskDBOList = employeeProfileTransaction.getRemarksDetails(empId);
				Set<EmpRemarksDetailsDBO> updatedRemarksDBOSet = new HashSet<>();
				Map<Integer, EmpRemarksDetailsDBO> existDBOMap = new HashMap<>();
				if (orginalRemarskDBOList.size() > 0) {
					for (EmpRemarksDetailsDBO dbo : orginalRemarskDBOList) {
						if (dbo.recordStatus == 'A') {
							existDBOMap.put(dbo.id, dbo);
						}
					}
					;
				}
				for (EmpRemarksDetailsDBO item : empRemarksDetailsDBOSet) {
					EmpRemarksDetailsDBO empRemarks = null;
					if (existDBOMap.containsKey(item.id)) {
						empRemarks = item;
						empRemarks.recordStatus = 'A';
						empRemarks.modifiedUsersId = Integer.parseInt(userId);
						existDBOMap.remove(item.id);
					} else {
						empRemarks = item;
						empRemarks.recordStatus = 'A';
						empRemarks.createdUsersId = Integer.parseInt(userId);
						empRemarks.empDBO = empDBO;
					}
					if (!Utils.isNullOrEmpty(empRemarks.remarksDetails)) {
						updatedRemarksDBOSet.add(empRemarks);
					}
				}
				if (!Utils.isNullOrEmpty(existDBOMap)) {
					existDBOMap.forEach((entry, value) -> {
						value.modifiedUsersId = Integer.parseInt(userId);
						value.recordStatus = 'D';
						updatedRemarksDBOSet.add(value);
					});
				}
				empDBO.empRemarksDetailsDBOSet = updatedRemarksDBOSet;
				//					}else {
				//						if(!Utils.isNullOrEmpty(empDBO.getEmpRemarksDetailsDBOSet())) {
				//							List<EmpRemarksDetailsDBO> list = employeeProfileTransaction.getRemarksDetails(empId);
				//							if(!Utils.isNullOrEmpty(list)) {
				//								list.forEach(details -> {
				//									details.recordStatus = 'D';
				//								});
				//								empRemarksDetailsDBOSet = list.stream().collect(Collectors.toSet());
				//								empDBO.setEmpRemarksDetailsDBOSet(empRemarksDetailsDBOSet);
				//							}
				//						}
				//					}
				//				}
				if (!Utils.isNullOrEmpty(approversDetails.levelOneAppraiser.value) || !Utils.isNullOrEmpty(approversDetails.levelTwoAppraiser.value) ||
						!Utils.isNullOrEmpty(approversDetails.leaveApprover.value) || !Utils.isNullOrEmpty(approversDetails.leaveAuthorizer.value) ||
						!Utils.isNullOrEmpty(approversDetails.workDairyApprover.value)) {
					boolean isTrue1 = false;
					if (!Utils.isNullOrEmpty(empDBO.empApproversDBO)) {
						if (empDBO.empApproversDBO.recordStatus == 'A') {
							isTrue1 = true;
						}
					}
					if (isTrue1) {
						approversDBO = empDBO.empApproversDBO;
						approversDBO.setModifiedUsersId(Integer.parseInt(userId));
					} else {
						approversDBO = new EmpApproversDBO();
						approversDBO.empDBO = empDBO;
						approversDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					Set<EmpApproversDetailsDBO> detailsDboSet = null;
					Map<String, EmpApproversDetailsDBO> subMap = new HashMap<>();
					if (!Utils.isNullOrEmpty(approversDBO.getEmpApproversDetailsDBOSet())) {
						detailsDboSet = approversDBO.getEmpApproversDetailsDBOSet();
						detailsDboSet.forEach(subDbo -> {
							subMap.put(subDbo.getApprovalType().trim(), subDbo);
						});
					} else {
						detailsDboSet = new HashSet<>();
					}
					if (!Utils.isNullOrEmpty(approversDetails.levelOneAppraiser.value) || !Utils.isNullOrEmpty(approversDetails.levelTwoAppraiser.value)) {
						String type = "Appraiser";
						if (!Utils.isNullOrEmpty(approversDetails.levelOneAppraiser.value)) {
							approversDBO.setLevelOneAppraiserId(new EmpDBO());
							approversDBO.getLevelOneAppraiserId().setId(Integer.parseInt(approversDetails.levelOneAppraiser.value));
						}
						if (!Utils.isNullOrEmpty(approversDetails.levelTwoAppraiser.value)) {
							approversDBO.setLevelTwoAppraiserId(new EmpDBO());
							approversDBO.getLevelTwoAppraiserId().setId(Integer.parseInt(approversDetails.levelTwoAppraiser.value));
						}
						detailsDboSet.add(setDetails(approversDetails, type, subMap, userId, approversDBO));
					}
					if (!Utils.isNullOrEmpty(approversDetails.leaveApprover.value) || !Utils.isNullOrEmpty(approversDetails.leaveAuthorizer.value)) {
						String type = "Leave";
						if (!Utils.isNullOrEmpty(approversDetails.leaveApprover)) {
							if (!Utils.isNullOrEmpty(approversDetails.leaveApprover.value)) {
								approversDBO.setLeaveApproverId(new EmpDBO());
								approversDBO.getLeaveApproverId().setId(Integer.parseInt(approversDetails.leaveApprover.value));
							}
						}
						if (!Utils.isNullOrEmpty(approversDetails.leaveAuthorizer)) {
							if (!Utils.isNullOrEmpty(approversDetails.leaveAuthorizer.value)) {
								approversDBO.setLeaveAuthorizerId(new EmpDBO());
								approversDBO.getLeaveAuthorizerId().setId(Integer.parseInt(approversDetails.leaveAuthorizer.value));
							}
						}
						detailsDboSet.add(setDetails(approversDetails, type, subMap, userId, approversDBO));
					}
					if (!Utils.isNullOrEmpty(approversDetails.workDairyApprover)) {
						if (!Utils.isNullOrEmpty(approversDetails.workDairyApprover.value)) {
							String type = "Work Dairy";
							approversDBO.setWorkDairyApproverId(new EmpDBO());
							approversDBO.getWorkDairyApproverId().setId(Integer.parseInt(approversDetails.workDairyApprover.value));
							detailsDboSet.add(setDetails(approversDetails, type, subMap, userId, approversDBO));
						}
					}
					approversDBO.setEmpApproversDetailsDBOSet(detailsDboSet);
					approversDBO.setRecordStatus('A');
					empDBO.empApproversDBO = approversDBO;
				}
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					if (!empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("GUEST")) {
						if (!Utils.isNullOrEmpty(empDBO.empresignationDBO) && !Utils.isNullOrEmpty(resignationDetails.resignationId)) {
							empResignationDBO = empDBO.empresignationDBO;
							if (!Utils.isNullOrEmpty(resignationDetails.reasonForLeaving)) {
								empResignationDBO.empResignationReasonDBO = new EmpResignationReasonDBO();
								empResignationDBO.empResignationReasonDBO.resignationName = resignationDetails.reasonForLeaving;
								empResignationDBO.empResignationReasonDBO.recordStatus = 'A';
							}
						} else if (!Utils.isNullOrEmpty(resignationDetails.poRemarks) || !Utils.isNullOrEmpty(resignationDetails.relieavingOrderDate) ||
								!Utils.isNullOrEmpty(resignationDetails.referenceNo) || !Utils.isNullOrEmpty(resignationDetails.isServingNoticePeriod) ||
								!Utils.isNullOrEmpty(resignationDetails.hodRecomendedRelievingDate) || !Utils.isNullOrEmpty(resignationDetails.dateOfLeaving) ||
								!Utils.isNullOrEmpty(resignationDetails.submissionDate) || !Utils.isNullOrEmpty(resignationDetails.resignationId) ||
								!Utils.isNullOrEmpty(resignationDetails.reasonForLeaving)) {
							empResignationDBO = new EmpResignationDBO();
							empResignationDBO.empDBO = empDBO;
							empResignationDBO.recordStatus = 'A';
							EmpResignationReasonDBO resignationReasonDBO = new EmpResignationReasonDBO();
							if (!Utils.isNullOrEmpty(resignationDetails.reasonForLeaving)) {
								resignationReasonDBO.resignationName = resignationDetails.reasonForLeaving;
								empResignationDBO.empResignationReasonDBO = resignationReasonDBO;
							}
						}
						if (!Utils.isNullOrEmpty(resignationDetails.resignationId)) {
							empResignationDBO.id = Integer.parseInt(resignationDetails.resignationId);
						}
						if (!Utils.isNullOrEmpty(resignationDetails.submissionDate)) {
							empResignationDBO.submissionDate = LocalDate.parse(resignationDetails.submissionDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(resignationDetails.dateOfLeaving)) {
							empResignationDBO.dateOfLeaving = LocalDate.parse(resignationDetails.dateOfLeaving, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(resignationDetails.hodRecomendedRelievingDate)) {
							empResignationDBO.hodRecomendedRelievingDate = LocalDate.parse(resignationDetails.hodRecomendedRelievingDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(resignationDetails.isServingNoticePeriod)) {
							if (resignationDetails.isServingNoticePeriod.equalsIgnoreCase("Yes")) {
								empResignationDBO.isServingNoticePeriod = true;
							} else {
								empResignationDBO.isServingNoticePeriod = false;
							}
						}
						if (!Utils.isNullOrEmpty(resignationDetails.referenceNo)) {
							empResignationDBO.referenceNo = Integer.parseInt(resignationDetails.referenceNo);
						}
						if (!Utils.isNullOrEmpty(resignationDetails.relieavingOrderDate)) {
							empResignationDBO.relievingDate = LocalDate.parse(resignationDetails.relieavingOrderDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(resignationDetails.poRemarks)) {
							empResignationDBO.poRemarks = resignationDetails.poRemarks;
						}
						if (!Utils.isNullOrEmpty(empResignationDBO)) {
							empResignationDBO.recordStatus = 'A';
							empResignationDBO.empDBO = empDBO;
							empDBO.empresignationDBO = empResignationDBO;
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.empresignationDBO)) {
							EmpResignationDBO empResignationDBO1 = employeeProfileTransaction.resignationDetails(empId);
							if (!Utils.isNullOrEmpty(empResignationDBO1)) {
								empResignationDBO1.recordStatus = 'D';
							}
							empDBO.empresignationDBO = empResignationDBO1;
						}
					}
				}
				List<EmpEmployeeLetterDetailsDBO> empLetterDetailsDBOList = new ArrayList<>();
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					EmpEmployeeLetterDetailsDBO appointmentLetterDBO = new EmpEmployeeLetterDetailsDBO();
					EmpEmployeeLetterDetailsDBO appointmentLetterExtendedDBO = new EmpEmployeeLetterDetailsDBO();
					Set<EmpEmployeeLetterDetailsDBO> appointmentEmployeeLetterDetailsDBOSet = new HashSet<EmpEmployeeLetterDetailsDBO>();
					Set<EmpEmployeeLetterDetailsDBO> appointmentLetterExtendedEmployeeLetterDetailsDBOSet = new HashSet<EmpEmployeeLetterDetailsDBO>();
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL")
							|| empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("VISITING") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING")) {
						if (!Utils.isNullOrEmpty(empJobDetails.appointmentLetterId)) {
							appointmentLetterDBO.id = Integer.parseInt(empJobDetails.appointmentLetterId);
						}
						if (!Utils.isNullOrEmpty(empJobDetails.appointmentLetterDate)) {
							appointmentLetterDBO.letterDate = LocalDate.parse(empJobDetails.appointmentLetterDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(empJobDetails.appointmentLetterDocumentUrl)) {
							appointmentLetterDBO.letterUrl = empJobDetails.appointmentLetterDocumentUrl;
							;
						}
						appointmentLetterDBO.letterType = "APPOINTMENT_LETTER";
						appointmentLetterDBO.recordStatus = 'A';
						if (!Utils.isNullOrEmpty(empJobDetails.appointmentLetterRfNo)) {
							appointmentLetterDBO.letterRefNo = Integer.parseInt(empJobDetails.appointmentLetterRfNo);
							empLetterDetailsDBOList.add(appointmentLetterDBO);
						}
						if (!Utils.isNullOrEmpty(empJobDetails.appointmentLetterExtendedId)) {
							appointmentLetterExtendedDBO.id = Integer.parseInt(empJobDetails.appointmentLetterExtendedId);
						}
						if (!Utils.isNullOrEmpty(empJobDetails.appointmentLetterExtendedDate)) {
							appointmentLetterExtendedDBO.letterDate = LocalDate.parse(empJobDetails.appointmentLetterExtendedDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(empJobDetails.appointmentLetterDocumentUrl)) {
							appointmentLetterExtendedDBO.letterUrl = empJobDetails.appointmentLetterDocumentUrl;
						}
						appointmentLetterExtendedDBO.letterType = "APPOINTMENT_LETTER_EXTENDED";
						appointmentLetterExtendedDBO.recordStatus = 'A';
						if (!Utils.isNullOrEmpty(empJobDetails.appointmentLetterExtendedRfNo)) {
							appointmentLetterExtendedDBO.letterRefNo = Integer.parseInt(empJobDetails.appointmentLetterExtendedRfNo);
							empLetterDetailsDBOList.add(appointmentLetterExtendedDBO);
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.getEmpEmployeeLetterDetailsDBOSet())) {
							List<EmpEmployeeLetterDetailsDBO> appointmentLetterDBOList = employeeProfileTransaction.empEmployeeLetterDetails(empId, "APPOINTMENT_LETTER");
							if (!Utils.isNullOrEmpty(appointmentLetterDBOList)) {
								appointmentLetterDBOList.forEach(details -> {
									details.setRecordStatus('D');
									appointmentEmployeeLetterDetailsDBOSet.add(details);
								});
							}
							empDBO.setEmpEmployeeLetterDetailsDBOSet(appointmentEmployeeLetterDetailsDBOSet);
							List<EmpEmployeeLetterDetailsDBO> appointmentLetterExtendedDBOList = employeeProfileTransaction.empEmployeeLetterDetails(empId, "APPOINTMENT_LETTER_EXTENDED");
							if (!Utils.isNullOrEmpty(appointmentLetterExtendedDBOList)) {
								appointmentLetterExtendedDBOList.forEach(details -> {
									details.setRecordStatus('D');
									appointmentLetterExtendedEmployeeLetterDetailsDBOSet.add(details);
								});
							}
							empDBO.setEmpEmployeeLetterDetailsDBOSet(appointmentLetterExtendedEmployeeLetterDetailsDBOSet);
						}
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					EmpEmployeeLetterDetailsDBO regularAppointmentDBO = new EmpEmployeeLetterDetailsDBO();
					EmpEmployeeLetterDetailsDBO confirmationLetterDBO = new EmpEmployeeLetterDetailsDBO();
					Set<EmpEmployeeLetterDetailsDBO> regularEmployeeLetterDetailsDBOSet = new HashSet<EmpEmployeeLetterDetailsDBO>();
					Set<EmpEmployeeLetterDetailsDBO> confirmationEmployeeLetterDetailsDBOSet = new HashSet<EmpEmployeeLetterDetailsDBO>();
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL")
							|| empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING")) {
						if (!Utils.isNullOrEmpty(empJobDetails.regularAppointmentLetterId)) {
							regularAppointmentDBO.id = Integer.parseInt(empJobDetails.regularAppointmentLetterId);
						}
						if (!Utils.isNullOrEmpty(empJobDetails.regularAppointmentLetterDate)) {
							regularAppointmentDBO.letterDate = LocalDate.parse(empJobDetails.regularAppointmentLetterDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(empJobDetails.regularAppointmentLetterDocumentUrl)) {
							regularAppointmentDBO.letterUrl = empJobDetails.regularAppointmentLetterDocumentUrl;
							;
						}
						regularAppointmentDBO.letterType = "REGULAR_APPOINTMENT_LETTER";
						regularAppointmentDBO.recordStatus = 'A';
						if (!Utils.isNullOrEmpty(empJobDetails.regularAppointmentLetterRfNo)) {
							regularAppointmentDBO.letterRefNo = Integer.parseInt(empJobDetails.regularAppointmentLetterRfNo);
							empLetterDetailsDBOList.add(regularAppointmentDBO);
						}
						if (!Utils.isNullOrEmpty(empJobDetails.confirmationLetterId)) {
							confirmationLetterDBO.id = Integer.parseInt(empJobDetails.confirmationLetterId);
						}
						if (!Utils.isNullOrEmpty(empJobDetails.confirmationLetterDate)) {
							confirmationLetterDBO.letterDate = LocalDate.parse(empJobDetails.confirmationLetterDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(empJobDetails.confirmationLetterDocumentUrl)) {
							confirmationLetterDBO.letterUrl = empJobDetails.confirmationLetterDocumentUrl;
							;
						}
						confirmationLetterDBO.letterType = "CONFIRMATION_LETTER";
						confirmationLetterDBO.recordStatus = 'A';
						if (!Utils.isNullOrEmpty(empJobDetails.confirmationLetterRfNo)) {
							confirmationLetterDBO.letterRefNo = Integer.parseInt(empJobDetails.confirmationLetterRfNo);
							empLetterDetailsDBOList.add(confirmationLetterDBO);
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.getEmpEmployeeLetterDetailsDBOSet())) {
							List<EmpEmployeeLetterDetailsDBO> regularAppointmentDBOList = employeeProfileTransaction.empEmployeeLetterDetails(empId, "REGULAR_APPOINTMENT_LETTER");
							if (!Utils.isNullOrEmpty(regularAppointmentDBOList)) {
								regularAppointmentDBOList.forEach(details -> {
									details.setRecordStatus('D');
									regularEmployeeLetterDetailsDBOSet.add(details);
								});
							}
							empDBO.setEmpEmployeeLetterDetailsDBOSet(regularEmployeeLetterDetailsDBOSet);
							List<EmpEmployeeLetterDetailsDBO> confirmationLetterList = employeeProfileTransaction.empEmployeeLetterDetails(empId, "CONFIRMATION_LETTER");
							if (!Utils.isNullOrEmpty(confirmationLetterList)) {
								confirmationLetterList.forEach(details -> {
									details.setRecordStatus('D');
									confirmationEmployeeLetterDetailsDBOSet.add(details);
								});
							}
							empDBO.setEmpEmployeeLetterDetailsDBOSet(confirmationEmployeeLetterDetailsDBOSet);
						}
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					EmpEmployeeLetterDetailsDBO contractLetterDBO = new EmpEmployeeLetterDetailsDBO();
					Set<EmpEmployeeLetterDetailsDBO> contractEmployeeLetterDetailsDBO = new HashSet<EmpEmployeeLetterDetailsDBO>();
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("CONTRACT")) {
						if (!Utils.isNullOrEmpty(empJobDetails.contractLetterId)) {
							contractLetterDBO.id = Integer.parseInt(empJobDetails.contractLetterId);
						}
						if (!Utils.isNullOrEmpty(empJobDetails.contractLetterDate)) {
							contractLetterDBO.letterDate = LocalDate.parse(empJobDetails.contractLetterDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(empJobDetails.contractLetterDocumentUrl)) {
							contractLetterDBO.letterUrl = empJobDetails.contractLetterDocumentUrl;
							;
						}
						contractLetterDBO.letterType = "CONTRACT_LETTER";
						contractLetterDBO.recordStatus = 'A';
						contractLetterDBO.empDBO = empDBO;
						if (!Utils.isNullOrEmpty(empJobDetails.contractLetterRfNo)) {
							contractLetterDBO.letterRefNo = Integer.parseInt(empJobDetails.contractLetterRfNo);
							empLetterDetailsDBOList.add(contractLetterDBO);
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.getEmpEmployeeLetterDetailsDBOSet())) {
							List<EmpEmployeeLetterDetailsDBO> contractLetterDBOList = employeeProfileTransaction.empEmployeeLetterDetails(empId, "CONTRACT_LETTER");
							if (!Utils.isNullOrEmpty(contractLetterDBOList)) {
								contractLetterDBOList.forEach(details -> {
									details.setRecordStatus('D');
									contractEmployeeLetterDetailsDBO.add(details);
								});
							}
							empDBO.setEmpEmployeeLetterDetailsDBOSet(contractEmployeeLetterDetailsDBO);
						}
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("CONTRACT") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("GUEST") ||
							empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("VISITING")) {
						String jobId = null;
						if (!Utils.isNullOrEmpty(empJobDetailsDBO)) {
							empJobDetailsDBO.setPfAccountNo(null);
							empJobDetailsDBO.setPfDate(null);
							empJobDetailsDBO.setUanNo(null);
							empJobDetailsDBO.setGratuityDate(null);
							empJobDetailsDBO.setGratuityNo(null);
						}
						if (!Utils.isNullOrEmpty(employeeProfile.jobDetails.jobInformation.empJobDetails.jobDetailsId)) {
							jobId = employeeProfile.jobDetails.jobInformation.empJobDetails.jobDetailsId;
							List<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOList = employeeProfileTransaction.getEmpGratuityNomineesDBOList(jobId);
							if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBOList)) {
								empPfGratuityNomineesDBOList.forEach(list -> {
									list.setRecordStatus('D');
								});
								Set<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOSet = new HashSet<EmpPfGratuityNomineesDBO>();
								empPfGratuityNomineesDBOSet = empPfGratuityNomineesDBOList.stream().collect(Collectors.toSet());
								empJobDetailsDBO.setEmpPfGratuityNomineesDBOS(empPfGratuityNomineesDBOSet);
							}
						}
						empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("GUEST")) {
						ErpAcademicYearDBO acadmeicYearDBO = employeeProfileTransaction.getCurrentAcademicYear();
						//						List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = employeeProfileTransaction.getEmployeeLeaveAllocation(Integer.parseInt(empId) ,acadmeicYearDBO.academicYear);
						List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = employeeProfileTransaction.getEmployeeLeaveAllocation(Integer.parseInt(empId));
						Set<EmpLeaveAllocationDBO> empLeaveAllocationDBOSet = new HashSet<EmpLeaveAllocationDBO>();
						if (!Utils.isNullOrEmpty(empLeaveAllocationDBOList)) {
							empLeaveAllocationDBOList.forEach(empLeave -> {
								empLeave.recordStatus = 'D';
							});
							empLeaveAllocationDBOSet = empLeaveAllocationDBOList.stream().collect(Collectors.toSet());
							empDBO.empLeaveAllocationDBOSet = empLeaveAllocationDBOSet;
						}
						if (!Utils.isNullOrEmpty(empJobDetailsDBO.empLeaveCategoryAllotmentId)) {
							empJobDetailsDBO.empLeaveCategoryAllotmentId = null;
							empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
						}
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					Set<EmpEmployeeLetterDetailsDBO> promotionLetterDetailsDBOSet = new HashSet<EmpEmployeeLetterDetailsDBO>();
					Set<EmpLevelsAndPromotionsDBO> empLevelsAndPromotionsDBOSet = new HashSet<EmpLevelsAndPromotionsDBO>();
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL")
							|| empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") || empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING")) {
						//						if(!Utils.isNullOrEmpty(promotionDetails)) {
						//							for(EmployeeProfileLetterDTO promotion : promotionDetails){
						//								EmpEmployeeLetterDetailsDBO promotionLetterDBO = new EmpEmployeeLetterDetailsDBO();
						//								if(!Utils.isNullOrEmpty(promotion.id)) {
						//									promotionLetterDBO.id = Integer.parseInt(promotion.id);
						//								}
						//								if(!Utils.isNullOrEmpty(promotion.date)) {
						//									promotionLetterDBO.letterDate = LocalDate.parse(promotion.date,DateTimeFormatter.ofPattern("M/d/yyyy"));
						//								}
						//								if(!Utils.isNullOrEmpty(promotion.documents)) {
						//									promotionLetterDBO.letterUrl = promotion.documents;;
						//								}
						//								promotionLetterDBO.letterType = "PROMOTION_LETTER";
						//								promotionLetterDBO.recordStatus = 'A';
						//								if(!Utils.isNullOrEmpty(promotion.referenceNo)) {
						//									promotionLetterDBO.letterRefNo = Integer.parseInt(promotion.referenceNo);
						//									empLetterDetailsDBOList.add(promotionLetterDBO);
						//								}
						//							}
						//						}
						if (!Utils.isNullOrEmpty(empLevelsAndPromotionsDTOList)) {
							EmpDBO em = empDBO;
							empLevelsAndPromotionsDTOList.forEach(data -> {
								if (!Utils.isNullOrEmpty(data.getEffectiveDateOfPromotion())) {
									EmpLevelsAndPromotionsDBO empLevelsAndPromotionsDBO = new EmpLevelsAndPromotionsDBO();
									if (!Utils.isNullOrEmpty(data.getId())) {
										empLevelsAndPromotionsDBO.setId(data.getId());
									}
									if (!Utils.isNullOrEmpty(data.getEffectiveDateOfPromotion())) {
										empLevelsAndPromotionsDBO.setEffectiveDateOfPromotion(data.getEffectiveDateOfPromotion());
									}
									if (!Utils.isNullOrEmpty(data.getEmpDesignation())) {
										if (!Utils.isNullOrEmpty(data.getEmpDesignation().getValue())) {
											empLevelsAndPromotionsDBO.setEmpDesignationDBO(new EmpDesignationDBO());
											empLevelsAndPromotionsDBO.getEmpDesignationDBO().setId(Integer.parseInt(data.getEmpDesignation().getValue()));
										}
									}
									if (!Utils.isNullOrEmpty(data.getEmpPayScaleLevel())) {
										if (!Utils.isNullOrEmpty(data.getEmpPayScaleLevel().getValue())) {
											empLevelsAndPromotionsDBO.setEmpPayScaleLevelDBO(new EmpPayScaleLevelDBO());
											empLevelsAndPromotionsDBO.getEmpPayScaleLevelDBO().setId(Integer.parseInt(data.getEmpPayScaleLevel().getValue()));
										}
									}
									if (!Utils.isNullOrEmpty(data.getCell())) {
										if (!Utils.isNullOrEmpty(data.getCell().getLabel()))
											empLevelsAndPromotionsDBO.setCell(Integer.parseInt(data.getCell().getLabel()));
									}
									if (!Utils.isNullOrEmpty(data.getRemarks())) {
										empLevelsAndPromotionsDBO.setRemarks(data.getRemarks());
									}
									if (!Utils.isNullOrEmpty(data.getEmployeeProfileLetterDTO())) {
										if (!Utils.isNullOrEmpty(data.getEmployeeProfileLetterDTO().getDate())) {
											EmpEmployeeLetterDetailsDBO empEmployeeLetterDetailsDBO = new EmpEmployeeLetterDetailsDBO();
											if (!Utils.isNullOrEmpty(data.getEmployeeProfileLetterDTO().getId())) {
												empEmployeeLetterDetailsDBO.setId(Integer.parseInt(data.getEmployeeProfileLetterDTO().getId()));
											}
											if (!Utils.isNullOrEmpty(data.getEmployeeProfileLetterDTO().getDate())) {
												empEmployeeLetterDetailsDBO.setLetterDate(LocalDate.parse(data.getEmployeeProfileLetterDTO().getDate(), DateTimeFormatter.ofPattern("M/d/yyyy")));
											}
											if (!Utils.isNullOrEmpty(data.getEmployeeProfileLetterDTO().getDocuments())) {
												empEmployeeLetterDetailsDBO.setLetterUrl(data.getEmployeeProfileLetterDTO().getDocuments());
											}
											empEmployeeLetterDetailsDBO.setLetterType("PROMOTION_LETTER");
											empEmployeeLetterDetailsDBO.setRecordStatus('A');
											if (!Utils.isNullOrEmpty(data.getEmployeeProfileLetterDTO().getReferenceNo())) {
												empEmployeeLetterDetailsDBO.setLetterRefNo(Integer.parseInt(data.getEmployeeProfileLetterDTO().getReferenceNo()));
												empLetterDetailsDBOList.add(empEmployeeLetterDetailsDBO);
											}
											empLevelsAndPromotionsDBO.setEmpEmployeeLetterDetailsDBO(empEmployeeLetterDetailsDBO);
										}
									}
									if (!Utils.isNullOrEmpty(empLevelsAndPromotionsDBO)) {
										empLevelsAndPromotionsDBO.setEmpDBO(em);
										empLevelsAndPromotionsDBOSet.add(empLevelsAndPromotionsDBO);
									}
								}
							});
							empDBO.setEmpLevelsAndPromotionsDBOSet(empLevelsAndPromotionsDBOSet);
						}
					} else {
						if (!Utils.isNullOrEmpty(empDBO.getEmpEmployeeLetterDetailsDBOSet())) {
							List<EmpEmployeeLetterDetailsDBO> promotionLetterDBO = employeeProfileTransaction.empEmployeeLetterDetails(empId, "PROMOTION_LETTER");
							if (!Utils.isNullOrEmpty(promotionLetterDBO)) {
								promotionLetterDBO.forEach(details -> {
									details.setRecordStatus('D');
									promotionLetterDetailsDBOSet.add(details);
								});
							}
							empDBO.setEmpEmployeeLetterDetailsDBOSet(promotionLetterDetailsDBOSet);
						}
					}
				}
				List<EmpEmployeeLetterDetailsDBO> orginalletterDBOList = employeeProfileTransaction.getEmployeeLetterDetails(empId);
				Set<EmpEmployeeLetterDetailsDBO> updateLetterDBOSet = new HashSet<>();
				Map<Integer, EmpEmployeeLetterDetailsDBO> letterDBOMap = new HashMap<>();
				if (!Utils.isNullOrEmpty(orginalletterDBOList)) {
					orginalletterDBOList.forEach(dbo -> {
						if (dbo.recordStatus == 'A') {
							letterDBOMap.put(dbo.id, dbo);
						}
					});
				}
				if (!Utils.isNullOrEmpty(empLetterDetailsDBOList)) {
					for (EmpEmployeeLetterDetailsDBO item : empLetterDetailsDBOList) {
						EmpEmployeeLetterDetailsDBO letter = null;
						if (letterDBOMap.containsKey(item.id)) {
							letter = item;
							letter.modifiedUsersId = Integer.parseInt(userId);
							letterDBOMap.remove(item.id);
						} else {
							letter = item;
							letter.recordStatus = 'A';
							letter.createdUsersId = Integer.parseInt(userId);
							letter.empDBO = empDBO;
						}
						letter.empDBO = empDBO;
						updateLetterDBOSet.add(letter);
					}
				}
				if (!Utils.isNullOrEmpty(letterDBOMap)) {
					if (!Utils.isNullOrEmpty(letterDBOMap)) {
						letterDBOMap.forEach((entry, value) -> {
							value.modifiedUsersId = Integer.parseInt(userId);
							value.recordStatus = 'D';
							updateLetterDBOSet.add(value);
						});
					}
				}
				if (!Utils.isNullOrEmpty(updateLetterDBOSet)) {
					empDBO.empEmployeeLetterDetailsDBOSet = updateLetterDBOSet;
				}
				Set<EmpGuestContractDetailsDBO> empGuestContractDetailsDBOSet = new HashSet<>();
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					List<Integer> empPayScaleIds = new ArrayList<Integer>();
					if (!Utils.isNullOrEmpty(additionalInformationList)) {
						additionalInformationList.forEach(additionalInformation -> {
							if (!Utils.isNullOrEmpty(additionalInformation.payscaleDetailsId)) {
								empPayScaleIds.add(Integer.parseInt(additionalInformation.payscaleDetailsId));
							}
						});
					}
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("GUEST")) {
						if (!Utils.isNullOrEmpty(additionalInformationList)) {
							for (EmployeeProfileAdditionalInformation additionalInformation : additionalInformationList) {
								if (!Utils.isNullOrEmpty(additionalInformation)) {
									EmpGuestContractDetailsDBO empGuestContractDetailsDBO = new EmpGuestContractDetailsDBO();
									empGuestContractDetailsDBO.empDBO = empDBO;
									if (!Utils.isNullOrEmpty(additionalInformation.id)) {
										empGuestContractDetailsDBO.id = Integer.parseInt(additionalInformation.id);
									}
									if (!Utils.isNullOrEmpty(additionalInformation.startDate)) {
										empGuestContractDetailsDBO.contractEmpStartDate = Utils.convertStringDateToLocalDate1(additionalInformation.startDate);
									}
									if (!Utils.isNullOrEmpty(additionalInformation.endDate)) {
										empGuestContractDetailsDBO.contractEmpEndDate = Utils.convertStringDateToLocalDate1(additionalInformation.endDate);
									}
									if (!Utils.isNullOrEmpty(additionalInformation.semester)) {
										if (!Utils.isNullOrEmpty(additionalInformation.semester.label)) {
											empGuestContractDetailsDBO.guestTutoringSemester = additionalInformation.semester.label;
										}
									}
									if (!Utils.isNullOrEmpty(additionalInformation.workHourPerWeek)) {
										empGuestContractDetailsDBO.guestWorkingHoursWeek = new BigDecimal(additionalInformation.workHourPerWeek);
									}
									if (!Utils.isNullOrEmpty(empJobDetails)) {
										if (!Utils.isNullOrEmpty(empJobDetails.subjectOrSpecialization))
											empGuestContractDetailsDBO.guestSubjectSpecialization = empJobDetails.subjectOrSpecialization;
									}
									if (!Utils.isNullOrEmpty(additionalInformation.isCurrentDetails)) {
										if (additionalInformation.isCurrentDetails.equalsIgnoreCase("Yes")) {
											empGuestContractDetailsDBO.isCurrent = true;
											empJobDetailsDBO.setEmpGuestContractDetailsDBO(empGuestContractDetailsDBO);
											empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
										} else {
											empGuestContractDetailsDBO.isCurrent = false;
											if (!Utils.isNullOrEmpty(empJobDetailsDBO.getEmpGuestContractDetailsDBO())) {
												if (!Utils.isNullOrEmpty(empJobDetailsDBO.getEmpGuestContractDetailsDBO())) {
													empJobDetailsDBO.setEmpGuestContractDetailsDBO(null);
												}
												empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
											}
										}
									}
									if (!Utils.isNullOrEmpty(empGuestContractDetailsDTO.guestReferredBy)) {
										empGuestContractDetailsDBO.guestReferredBy = empGuestContractDetailsDTO.guestReferredBy;
									}
									if (!Utils.isNullOrEmpty(additionalInformation.getCampusDepartment())) {
										empGuestContractDetailsDBO.setErpCampusDepartmentMappingDBO(new ErpCampusDepartmentMappingDBO());
										empGuestContractDetailsDBO.getErpCampusDepartmentMappingDBO().setId(Integer.parseInt(additionalInformation.getCampusDepartment().getValue()));
									}
									if (!Utils.isNullOrEmpty(additionalInformation.comments)) {
										empGuestContractDetailsDBO.guestContractRemarks = additionalInformation.comments;
									}
									if (!Utils.isNullOrEmpty(additionalInformation.payscaleDetailsId)) {
										empGuestContractDetailsDBO.empPayScaleDetailsDBO = employeeProfileTransaction.getEmpGuestPayScaleDetails(additionalInformation.payscaleDetailsId);
										if (!Utils.isNullOrEmpty(additionalInformation.honararium)) {
											empGuestContractDetailsDBO.empPayScaleDetailsDBO.wageRatePerType = new BigDecimal(additionalInformation.honararium);
										}

										if (!Utils.isNullOrEmpty(additionalInformation.paymentType)) {
											if (!Utils.isNullOrEmpty(additionalInformation.paymentType.label)) {
												empGuestContractDetailsDBO.empPayScaleDetailsDBO.payScaleType = additionalInformation.paymentType.label;
											}
										}
										if (!Utils.isNullOrEmpty(additionalInformation.isCurrentDetails)) {
											if (additionalInformation.isCurrentDetails.equalsIgnoreCase("No")) {
												empGuestContractDetailsDBO.empPayScaleDetailsDBO.current = false;
											} else {
												empGuestContractDetailsDBO.empPayScaleDetailsDBO.current = true;
											}
										}
									} else {
										empGuestContractDetailsDBO.empPayScaleDetailsDBO = new EmpPayScaleDetailsDBO();
										if (!Utils.isNullOrEmpty(additionalInformation.isCurrentDetails)) {
											if (additionalInformation.isCurrentDetails.equalsIgnoreCase("No")) {
												empGuestContractDetailsDBO.empPayScaleDetailsDBO.current = false;
											} else {
												empGuestContractDetailsDBO.empPayScaleDetailsDBO.current = true;
											}
										}
										if (!Utils.isNullOrEmpty(additionalInformation.honararium)) {
											empGuestContractDetailsDBO.empPayScaleDetailsDBO.wageRatePerType = new BigDecimal(additionalInformation.honararium);
										}
										if (!Utils.isNullOrEmpty(additionalInformation.paymentType)) {
											if (!Utils.isNullOrEmpty(additionalInformation.paymentType.label)) {
												empGuestContractDetailsDBO.empPayScaleDetailsDBO.payScaleType = additionalInformation.paymentType.label;
											}
										}
										if (!Utils.isNullOrEmpty(empGuestContractDetailsDBO.empPayScaleDetailsDBO)) {
											empGuestContractDetailsDBO.empPayScaleDetailsDBO.empDBO = empDBO;
											empGuestContractDetailsDBO.empPayScaleDetailsDBO.recordStatus = 'A';
											empGuestContractDetailsDBO.empPayScaleDetailsDBO.createdUsersId = Integer.parseInt(userId);
										}
									}
									empGuestContractDetailsDBO.recordStatus = 'A';
									empDBO.setEmpPayScaleDetailsDBOSet(new HashSet<EmpPayScaleDetailsDBO>());
									empDBO.getEmpPayScaleDetailsDBOSet().add(empGuestContractDetailsDBO.empPayScaleDetailsDBO);
									empGuestContractDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
									empGuestContractDetailsDBOSet.add(empGuestContractDetailsDBO);
								}

								List<EmpGuestContractDetailsDBO> orginalGuestContractDBOList = employeeProfileTransaction.getGuestContractDetails(empId);
								Set<EmpGuestContractDetailsDBO> updateGuestContractDBOSet = new HashSet<>();
								Map<Integer, EmpGuestContractDetailsDBO> existContractDBOMap = new HashMap<>();
								if (!Utils.isNullOrEmpty(orginalGuestContractDBOList)) {
									orginalGuestContractDBOList.forEach(dbo -> {
										if (dbo.recordStatus == 'A') {
											existContractDBOMap.put(dbo.id, dbo);
										}
									});
								}
								if (!Utils.isNullOrEmpty(empGuestContractDetailsDBOSet)) {
									for (EmpGuestContractDetailsDBO item : empGuestContractDetailsDBOSet) {
										EmpGuestContractDetailsDBO guestContract = null;
										if (existContractDBOMap.containsKey(item.id)) {
											guestContract = item;
											guestContract.modifiedUsersId = Integer.parseInt(userId);
											existContractDBOMap.remove(item.id);
										} else {
											guestContract = item;
											guestContract.recordStatus = 'A';
											guestContract.createdUsersId = Integer.parseInt(userId);
											guestContract.empDBO = empDBO;
										}
										updateGuestContractDBOSet.add(guestContract);
									}
								}
								if (!Utils.isNullOrEmpty(existContractDBOMap)) {
									existContractDBOMap.forEach((entry, value) -> {
										value.modifiedUsersId = Integer.parseInt(userId);
										value.recordStatus = 'D';
										//										if(!Utils.isNullOrEmpty(value.isCurrent())) {
										//											value.setCurrent(false);
										//											value.empPayScaleDetailsDBO.current = false;
										//										}
										updateGuestContractDBOSet.add(value);
									});
								}
								if (!Utils.isNullOrEmpty(updateGuestContractDBOSet)) {
									empDBO.empGuestContractDetailsDBOSet = updateGuestContractDBOSet;
								}
							}
						}

					} else {
						if (!Utils.isNullOrEmpty(empJobDetailsDBO)) {
							if (!Utils.isNullOrEmpty(empJobDetailsDBO.getEmpGuestContractDetailsDBO())) {
								empJobDetailsDBO.setEmpGuestContractDetailsDBO(null);
							}
							empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
						}
						List<EmpGuestContractDetailsDBO> empGuestContractDetailsDBOList = employeeProfileTransaction.getGuestContractDetails(empId);
						if (!Utils.isNullOrEmpty(empGuestContractDetailsDBOList)) {
							empGuestContractDetailsDBOList.forEach(list -> {
								list.recordStatus = 'D';
							});
							empGuestContractDetailsDBOSet = empGuestContractDetailsDBOList.stream().collect(Collectors.toSet());
							empDBO.setEmpGuestContractDetailsDBOSet(empGuestContractDetailsDBOSet);
						}
						List<EmpPayScaleDetailsDBO> empPayScaleDetailsDBOList = employeeProfileTransaction.getPayScaledetails(empPayScaleIds);
						if (!Utils.isNullOrEmpty(empPayScaleDetailsDBOList)) {
							empPayScaleDetailsDBOList.forEach(list -> {
								list.recordStatus = 'D';
							});
							Set<EmpPayScaleDetailsDBO> empPayScaleDetailsDBOSet = new HashSet<EmpPayScaleDetailsDBO>();
							empPayScaleDetailsDBOSet = empPayScaleDetailsDBOList.stream().collect(Collectors.toSet());
							empDBO.setEmpPayScaleDetailsDBOSet(empPayScaleDetailsDBOSet);
						}
					}
				}
				if (!Utils.isNullOrEmpty(empDTO.jobCategory.jobCategoryCode)) {
					if (empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("CONTRACT")) {
						if (!Utils.isNullOrEmpty(empGuestContractDetailsDTO.getContractStartDate())) {
							empJobDetailsDBO.setContractStartDate(Utils.convertStringDateToLocalDate1(empGuestContractDetailsDTO.getContractStartDate()));
						}
						if (!Utils.isNullOrEmpty(empGuestContractDetailsDTO.getContractEndDate())) {
							empJobDetailsDBO.setContractEndDate(Utils.convertStringDateToLocalDate1(empGuestContractDetailsDTO.getContractEndDate()));
						}
						if (!Utils.isNullOrEmpty(empGuestContractDetailsDTO.getComments())) {
							empJobDetailsDBO.setContractRemarks(empGuestContractDetailsDTO.getComments());
						}
						empJobDetailsDBO.empDBO = empDBO;
						empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
					} else {
						if (!Utils.isNullOrEmpty(empJobDetailsDBO)) {
							empJobDetailsDBO.setContractStartDate(null);
							empJobDetailsDBO.setContractEndDate(null);
							empJobDetailsDBO.setContractRemarks(null);
							empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
						}
					}
				}
				Saved = employeeProfileTransaction.saveData(empDBO);
			}
		} catch (Exception e) {
		}
		return Saved;
	}

	public boolean saveEducationAndExperienceDetails(EmployeeProfileDTO employeeProfile, String userId) {
		boolean isSaved = false;
		EmpDBO empDBO = null;
		EmpPersonalDataDBO empPersonalDataDBO = null;
		EmpJobDetailsDBO empJobDetailsDBO = null;
		List<Integer> educationIds = new ArrayList<Integer>();
		String empId = employeeProfile.personalDetails.basicInformation.employeeId;
		try {
			empDBO = commonApiTransaction.find(EmpDBO.class, Integer.parseInt(empId));
			//		empPersonalDataDBO = empDBO.empPersonalDataDBO;
			empPersonalDataDBO = Hibernate.unproxy(empDBO.getEmpPersonalDataDBO(), EmpPersonalDataDBO.class);
			if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO())) {
				empJobDetailsDBO = Hibernate.unproxy(empDBO.getEmpJobDetailsDBO(), EmpJobDetailsDBO.class);
			}
			EducationalDetailsDTO qualificationDetails = employeeProfile.educationAndExperienceDetails.qualificationDetails;
			ProfessionalExperienceDTO experienceDetails = employeeProfile.educationAndExperienceDetails.experienceDetails;
			if (!Utils.isNullOrEmpty(qualificationDetails.highestQualification)) {
				if (!Utils.isNullOrEmpty(qualificationDetails.highestQualification.value)) {
					if (qualificationDetails.highestQualification.value != null) {
						empPersonalDataDBO.erpQualificationLevelDBO = new ErpQualificationLevelDBO();
						empPersonalDataDBO.erpQualificationLevelDBO.id = Integer.parseInt(qualificationDetails.highestQualification.value);
					}
				}
			}
			if (!Utils.isNullOrEmpty(qualificationDetails.highestQualificationAlbum)) {
				empPersonalDataDBO.highestQualificationAlbum = qualificationDetails.highestQualificationAlbum;
			}
			empDBO.setEmpPersonalDataDBO(empPersonalDataDBO);
			Set<EmpEducationalDetailsDBO> educationDetailsDBOList = new HashSet<>();
			//			List<EmpEducationalDetailsDTO> educationDetailsList1 =  qualificationDetails.empEducationalDetailsMap.values().stream().collect(Collectors.toList()).stream()
			//					.flatMap(List::stream)
			//					.collect(Collectors.toList());
			//
			//			if(!Utils.isNullOrEmpty(educationDetailsList1)) {
			//				educationDetailsList1.forEach(ids -> {
			//					if(!Utils.isNullOrEmpty(ids.getId())) {
			//						educationIds.add(Integer.parseInt(ids.getId()));
			//					}
			//				});
			//			}
			List<EmpEducationalDetailsDTO> educationDetailsList = qualificationDetails.getEmpEducationalDetailsDTOS();
			if (!Utils.isNullOrEmpty(educationDetailsList)) {
				educationDetailsList.forEach(ids -> {
					if (!Utils.isNullOrEmpty(ids.getId())) {
						educationIds.add(Integer.parseInt(ids.getId()));
					}
				});
			}
			List<EmpEducationalDetailsDocumentsDBO> empEducationalDetailsDocumentsDBOList = employeeProfileTransaction.getEmployeeEducationalDocumentsDetails(educationIds);
			if (!Utils.isNullOrEmpty(educationDetailsList)) {
				for (EmpEducationalDetailsDTO educationDetails : educationDetailsList) {
					EmpEducationalDetailsDBO educationDetailsDBO = new EmpEducationalDetailsDBO();
					if (!Utils.isNullOrEmpty(educationDetails.id)) {
						educationDetailsDBO.empEducationalDetailsId = Integer.parseInt(educationDetails.id);
					}
					if (!Utils.isNullOrEmpty(educationDetails.empId)) {
						educationDetailsDBO.empDBO = new EmpDBO();
						educationDetailsDBO.empDBO.id = educationDetails.empId;
					} else {
						educationDetailsDBO.empDBO = new EmpDBO();
						educationDetailsDBO.empDBO.id = Integer.parseInt(empId);
					}
					if (!Utils.isNullOrEmpty(educationDetails.qualificationLevel)) {
						educationDetailsDBO.erpQualificationLevelDBO = new ErpQualificationLevelDBO();
						educationDetailsDBO.erpQualificationLevelDBO.id = Integer.parseInt(educationDetails.qualificationLevel.value);
					}
					if (!Utils.isNullOrEmpty(educationDetails.course)) {
						educationDetailsDBO.course = educationDetails.course;
					}
					if (!Utils.isNullOrEmpty(educationDetails.specialization)) {
						educationDetailsDBO.specialization = educationDetails.specialization;
					}
					if (!Utils.isNullOrEmpty(educationDetails.yearOfCompletion)) {
						educationDetailsDBO.yearOfCompletion = Integer.parseInt(educationDetails.yearOfCompletion.label);
					}
					if (!Utils.isNullOrEmpty(educationDetails.gradeOrPercentage)) {
						educationDetailsDBO.gradeOrPercentage = educationDetails.gradeOrPercentage;
					}
					if (!Utils.isNullOrEmpty(educationDetails.institute)) {
						educationDetailsDBO.institute = educationDetails.institute;
					}
					if (!Utils.isNullOrEmpty(educationDetails.boardOrUniversity)) {
						educationDetailsDBO.boardOrUniversity = educationDetails.boardOrUniversity;
					}
					if (!Utils.isNullOrEmpty(educationDetails.state)) {
						if (!Utils.isNullOrEmpty(educationDetails.state.value)) {
							educationDetailsDBO.erpStateDBO = new ErpStateDBO();
							educationDetailsDBO.erpStateDBO.id = Integer.parseInt(educationDetails.state.value);
							educationDetailsDBO.stateOthers = null;
						}
					}
					if (!Utils.isNullOrEmpty(educationDetails.stateOthers)) {
						educationDetailsDBO.stateOthers = educationDetails.stateOthers;
						educationDetailsDBO.setErpStateDBO(null);
					}
					if (!Utils.isNullOrEmpty(educationDetails.country)) {
						if (!Utils.isNullOrEmpty(educationDetails.country.value)) {
							educationDetailsDBO.erpCountryDBO = new ErpCountryDBO();
							educationDetailsDBO.erpCountryDBO.id = Integer.parseInt(educationDetails.country.value);
						}
					}
					if (!Utils.isNullOrEmpty(educationDetails.currentStatus)) {
						educationDetailsDBO.currentStatus = educationDetails.currentStatus;
					}
					if (!Utils.isNullOrEmpty(educationDetails.getErpInstitute())) {
						educationDetailsDBO.setErpInstitutionDBO(new ErpInstitutionDBO());
						educationDetailsDBO.getErpInstitutionDBO().setId(Integer.parseInt(educationDetails.getErpInstitute().getValue()));
					}
					if (!Utils.isNullOrEmpty(educationDetails.getErpBoardOrUniversity())) {
						educationDetailsDBO.setErpUniversityBoardDBO(new ErpUniversityBoardDBO());
						educationDetailsDBO.getErpUniversityBoardDBO().setId(Integer.parseInt(educationDetails.getErpBoardOrUniversity().getValue()));
					}
					if (!Utils.isNullOrEmpty(educationDetails.documentList)) {
						for (EmpEducationalDetailsDocumentsDTO educationalDocuments : educationDetails.documentList) {
							if (!Utils.isNullOrEmpty(empEducationalDetailsDocumentsDBOList)) {
								empEducationalDetailsDocumentsDBOList.forEach(datas -> {
									if (!Utils.isNullOrEmpty(educationalDocuments.documentUrl)) {
										datas.educationalDocumentsUrl = educationalDocuments.documentUrl;
									}
									datas.setModifiedUsersId(Integer.parseInt(userId));
									if (!Utils.isNullOrEmpty(educationalDocuments.documentUrl)) {
										educationDetailsDBO.documentsDBOSet.add(datas);
									}
								});
							} else {
								EmpEducationalDetailsDocumentsDBO educationalDocumentsDBO = new EmpEducationalDetailsDocumentsDBO();
								if (!Utils.isNullOrEmpty(educationalDocuments.educationalDocumentsUrl)) {
									educationalDocumentsDBO.educationalDocumentsUrl = educationalDocuments.educationalDocumentsUrl;
								}
								if (!Utils.isNullOrEmpty(educationalDocuments.educationalDocumentsUrl)) {
									educationDetailsDBO.documentsDBOSet.add(educationalDocumentsDBO);
								}
								educationalDocumentsDBO.setEmpEducationalDetailsDBO(educationDetailsDBO);
								educationalDocumentsDBO.setRecordStatus('A');
								educationalDocumentsDBO.setCreatedUsersId(Integer.parseInt(userId));
							}
						}
					}
					if (!Utils.isNullOrEmpty(educationDetails.yearOfCompletion)) {
						educationDetailsDBOList.add(educationDetailsDBO);
					}
				}
			}
			List<EmpEducationalDetailsDBO> orginalEducationalDetailsDBOSet = employeeProfileTransaction.getEmployeeEducationalDetails(empId);
			Set<EmpEducationalDetailsDBO> updatedEducationalDBOSet = new HashSet<>();
			Map<Integer, EmpEducationalDetailsDBO> existEducationalMap = new HashMap<>();
			if (!Utils.isNullOrEmpty(orginalEducationalDetailsDBOSet)) {
				if (!Utils.isNullOrEmpty(orginalEducationalDetailsDBOSet)) {
					orginalEducationalDetailsDBOSet.forEach(dbo -> {
						if (dbo.recordStatus == 'A') {
							existEducationalMap.put(dbo.empEducationalDetailsId, dbo);
						}
					});
				}
			}
			if (!Utils.isNullOrEmpty(educationDetailsDBOList)) {
				for (EmpEducationalDetailsDBO item : educationDetailsDBOList) {
					EmpEducationalDetailsDBO education = null;
					if (existEducationalMap.containsKey(item.empEducationalDetailsId)) {
						education = item;
						education.recordStatus = 'A';
						education.modifiedUsersId = Integer.parseInt(userId);
						existEducationalMap.remove(item.empEducationalDetailsId);
					} else {
						education = item;
						education.recordStatus = 'A';
						education.createdUsersId = Integer.parseInt(userId);
					}
					education.empDBO = empDBO;
					updatedEducationalDBOSet.add(education);
				}
			}
			if (!Utils.isNullOrEmpty(existEducationalMap)) {
				if (!Utils.isNullOrEmpty(existEducationalMap)) {
					existEducationalMap.forEach((entry, value) -> {
						value.modifiedUsersId = Integer.parseInt(userId);
						value.recordStatus = 'D';
						updatedEducationalDBOSet.add(value);
					});
				}
			}
			if (!Utils.isNullOrEmpty(updatedEducationalDBOSet)) {
				empDBO.empEducationalDetailsDBOSet = updatedEducationalDBOSet;
			}
			Set<EmpWorkExperienceDBO> empWorkExperienceDBOSet = new HashSet<>();
			if (!Utils.isNullOrEmpty(experienceDetails.experienceInformation)) {
				if (!Utils.isNullOrEmpty(empJobDetailsDBO)) {
					empJobDetailsDBO.setRecognisedExpYears(Integer.valueOf(experienceDetails.getRecognisedExpYears()));
					empJobDetailsDBO.setRecognisedExpMonths(Integer.valueOf(experienceDetails.getRecognisedExpMonths()));
				}
				for (EmpWorkExperienceDTO workExperience : experienceDetails.experienceInformation) {
					EmpWorkExperienceDBO empWorkExperienceDBO = new EmpWorkExperienceDBO();
					if (!Utils.isNullOrEmpty(workExperience.empApplnWorkExperienceId)) {
						empWorkExperienceDBO.empWorkExperienceId = workExperience.empApplnWorkExperienceId;
					}
					if (!Utils.isNullOrEmpty(workExperience.employmentType)) {
						if (workExperience.employmentType.equalsIgnoreCase("parttime")) {
							empWorkExperienceDBO.isPartTime = true;
						} else {
							empWorkExperienceDBO.isPartTime = false;
						}
					}
					if (!Utils.isNullOrEmpty(workExperience.isRecognised)) {
						if (workExperience.isRecognised.equalsIgnoreCase("yes")) {
							empWorkExperienceDBO.isRecognized = true;
						} else {
							empWorkExperienceDBO.isRecognized = false;
						}
					}
					if (!Utils.isNullOrEmpty(workExperience.workExperienceType)) {
						empWorkExperienceDBO.empApplnWorkExperienceTypeDBO = new EmpApplnWorkExperienceTypeDBO();
						empWorkExperienceDBO.empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId = Integer.parseInt(workExperience.workExperienceType.value);
					}
					if (!Utils.isNullOrEmpty(workExperience.functionalArea)) {
						empWorkExperienceDBO.empApplnSubjectCategoryDBO = new EmpApplnSubjectCategoryDBO();
						empWorkExperienceDBO.empApplnSubjectCategoryDBO.id = Integer.parseInt(workExperience.functionalArea.value);
					}
					if (!Utils.isNullOrEmpty(workExperience.designation)) {
						empWorkExperienceDBO.empDesignation = workExperience.designation;
					}
					if (!Utils.isNullOrEmpty(workExperience.institution)) {
						empWorkExperienceDBO.institution = workExperience.institution;
					}
					if (!Utils.isNullOrEmpty(workExperience.years)) {
						empWorkExperienceDBO.workExperienceYears = Integer.parseInt(workExperience.years);
					}
					if (!Utils.isNullOrEmpty(workExperience.months)) {
						empWorkExperienceDBO.workExperienceMonth = Integer.parseInt(workExperience.months);
					}
					if (!Utils.isNullOrEmpty(workExperience.fromDate)) {
						empWorkExperienceDBO.workExperienceFromDate = LocalDate.parse(workExperience.fromDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
					}
					if (!Utils.isNullOrEmpty(workExperience.toDate)) {
						empWorkExperienceDBO.workExperienceToDate = LocalDate.parse(workExperience.toDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
					}
					empWorkExperienceDBOSet.add(empWorkExperienceDBO);
				}
			}
			List<EmpWorkExperienceDBO> orginalWorkExperienceDBOSet = employeeProfileTransaction.getEmployeeExperienceDetails(empId);
			Set<EmpWorkExperienceDBO> updatedWorkExperienceDBOSet = new HashSet<>();
			Map<Integer, EmpWorkExperienceDBO> existWorkExperienceMap = new HashMap<>();
			if (!Utils.isNullOrEmpty(orginalWorkExperienceDBOSet)) {
				if (!Utils.isNullOrEmpty(orginalWorkExperienceDBOSet)) {
					orginalWorkExperienceDBOSet.forEach(dbo -> {
						if (dbo.recordStatus == 'A') {
							existWorkExperienceMap.put(dbo.empWorkExperienceId, dbo);
						}
					});
				}
			}
			if (!Utils.isNullOrEmpty(empWorkExperienceDBOSet)) {
				for (EmpWorkExperienceDBO item : empWorkExperienceDBOSet) {
					EmpWorkExperienceDBO workExperience = null;
					if (existWorkExperienceMap.containsKey(item.empWorkExperienceId)) {
						workExperience = item;
						workExperience.recordStatus = 'A';
						workExperience.modifiedUsersId = Integer.parseInt(userId);
						existWorkExperienceMap.remove(item.empWorkExperienceId);
					} else {
						workExperience = item;
						workExperience.recordStatus = 'A';
						workExperience.createdUsersId = Integer.parseInt(userId);
					}
					workExperience.empDBO = empDBO;
					if (!Utils.isNullOrEmpty(empDBO.getEmpApplnEntriesDBO())) {
						workExperience.empApplnEntriesDBO = empDBO.getEmpApplnEntriesDBO();
					}
					updatedWorkExperienceDBOSet.add(workExperience);
				}
			}
			if (!Utils.isNullOrEmpty(existWorkExperienceMap)) {
				if (!Utils.isNullOrEmpty(existWorkExperienceMap)) {
					existWorkExperienceMap.forEach((entry, value) -> {
						value.modifiedUsersId = Integer.parseInt(userId);
						value.recordStatus = 'D';
						updatedWorkExperienceDBOSet.add(value);
					});
				}
			}
			if (!Utils.isNullOrEmpty(updatedWorkExperienceDBOSet)) {
				empDBO.empWorkExperienceDBOSet = updatedWorkExperienceDBOSet;
			}
			Set<EmpEligibilityTestDBO> empEligibilityTestDBOSet = new HashSet<>();
			if (!Utils.isNullOrEmpty(qualificationDetails) && !Utils.isNullOrEmpty(qualificationDetails.eligibilityTestDetails)) {
				for (EmpEligiblityTestDTO eligiblityTest : qualificationDetails.eligibilityTestDetails) {
					EmpEligibilityTestDBO empEligibilityTestDBO = new EmpEligibilityTestDBO();
					if (!Utils.isNullOrEmpty(eligiblityTest.empEligibilityTestId)) {
						empEligibilityTestDBO.empEligibilityTestId = eligiblityTest.empEligibilityTestId;
					}
					if (!Utils.isNullOrEmpty(eligiblityTest.eligibilityTest)) {
						if (!Utils.isNullOrEmpty(eligiblityTest.eligibilityTest.value)) {
							empEligibilityTestDBO.empEligibilityExamListDBO = new EmpEligibilityExamListDBO();
							empEligibilityTestDBO.empEligibilityExamListDBO.empEligibilityExamListId = Integer.parseInt(eligiblityTest.eligibilityTest.value);
						}
					}
					if (!Utils.isNullOrEmpty(eligiblityTest.testYear)) {
						empEligibilityTestDBO.testYear = Integer.parseInt(eligiblityTest.testYear);
					}
					empEligibilityTestDBOSet.add(empEligibilityTestDBO);
				}
			}
			List<EmpEligibilityTestDBO> orginalEligiblityTestDBOSet = employeeProfileTransaction.getEmployeeEligibilityTestDetails(empId);
			Set<EmpEligibilityTestDBO> updatedEligblityDBOSet = new HashSet<>();
			Map<Integer, EmpEligibilityTestDBO> existEligiblityMap = new HashMap<>();
			if (!Utils.isNullOrEmpty(orginalEligiblityTestDBOSet)) {
				if (!Utils.isNullOrEmpty(orginalEligiblityTestDBOSet)) {
					orginalEligiblityTestDBOSet.forEach(dbo -> {
						if (dbo.recordStatus == 'A') {
							existEligiblityMap.put(dbo.empEligibilityTestId, dbo);
						}
					});
				}
			}
			if (!Utils.isNullOrEmpty(empEligibilityTestDBOSet)) {
				for (EmpEligibilityTestDBO item : empEligibilityTestDBOSet) {
					EmpEligibilityTestDBO eligiblityTest = null;
					if (existEligiblityMap.containsKey(item.empEligibilityTestId)) {
						eligiblityTest = item;
						eligiblityTest.recordStatus = 'A';
						eligiblityTest.modifiedUsersId = Integer.parseInt(userId);
						existEligiblityMap.remove(item.empEligibilityTestId);
					} else {
						eligiblityTest = item;
						eligiblityTest.recordStatus = 'A';
						eligiblityTest.createdUsersId = Integer.parseInt(userId);
					}
					eligiblityTest.empDBO = empDBO;
					updatedEligblityDBOSet.add(eligiblityTest);
				}
			}
			if (!Utils.isNullOrEmpty(existEligiblityMap)) {
				if (!Utils.isNullOrEmpty(existEligiblityMap)) {
					existEligiblityMap.forEach((entry, value) -> {
						value.modifiedUsersId = Integer.parseInt(userId);
						value.recordStatus = 'D';
						updatedEligblityDBOSet.add(value);
					});
				}
			}
			if (!Utils.isNullOrEmpty(updatedEligblityDBOSet)) {
				empDBO.empEligibilityTestDBOSet = updatedEligblityDBOSet;
			}
			//			Set<EmpMajorAchievementsDBO> majorAchievementsDBOSet = new HashSet<>();
			//			if(!Utils.isNullOrEmpty(experienceDetails.majorAchievementsList)) {
			//				for(EmpMajorAchievementsDTO majorAchievements:experienceDetails.majorAchievementsList) {
			//					EmpMajorAchievementsDBO majorAchievemnstDBO = new EmpMajorAchievementsDBO();
			//					if(!Utils.isNullOrEmpty(majorAchievements.id)) {
			//						majorAchievemnstDBO.id = Integer.parseInt(majorAchievements.id);
			//					}
			//					if(!Utils.isNullOrEmpty(majorAchievements.name)) {
			//						majorAchievemnstDBO.achievements = majorAchievements.name;
			//					}
			//					majorAchievemnstDBO.empDBO = empDBO;
			//					majorAchievemnstDBO.recordStatus = 'A';
			//					majorAchievementsDBOSet.add(majorAchievemnstDBO);
			//				}
			//			}
			Set<EmpMajorAchievementsDBO> updatedEmpMajorAchievementsDBOSet = new HashSet<>();
			List<EmpMajorAchievementsDBO> existEmpMajorAchievementsDBOSet = employeeProfileTransaction.getEmpMajorAchievemnts(empId);
			Map<Integer, EmpMajorAchievementsDBO> empMajorAchievementsMap = new HashMap<>();
			if (!Utils.isNullOrEmpty(existEmpMajorAchievementsDBOSet)) {
				if (!Utils.isNullOrEmpty(existEmpMajorAchievementsDBOSet)) {
					existEmpMajorAchievementsDBOSet.forEach(dbo -> {
						if (dbo.recordStatus == 'A') {
							empMajorAchievementsMap.put(dbo.id, dbo);
						}
					});
				}
			}
			if (!Utils.isNullOrEmpty(experienceDetails.majorAchievementsList)) {
				for (EmpMajorAchievementsDTO majorAchievements : experienceDetails.majorAchievementsList) {
					EmpMajorAchievementsDBO majorAchievemnstDBO = null;
					if (!Utils.isNullOrEmpty(majorAchievements.id)) {
						if (empMajorAchievementsMap.containsKey(Integer.parseInt(majorAchievements.id))) {
							majorAchievemnstDBO = empMajorAchievementsMap.get(Integer.parseInt(majorAchievements.id));
							majorAchievemnstDBO.modifiedUsersId = Integer.parseInt(userId);
							empMajorAchievementsMap.remove(Integer.parseInt(majorAchievements.id));
						}
					} else {
						majorAchievemnstDBO = new EmpMajorAchievementsDBO();
						majorAchievemnstDBO.createdUsersId = Integer.parseInt(userId);
						majorAchievemnstDBO.enteredDate = LocalDateTime.now();
						majorAchievemnstDBO.recordStatus = 'A';
						majorAchievemnstDBO.empDBO = empDBO;
					}
					if (!Utils.isNullOrEmpty(majorAchievements.name)) {
						majorAchievemnstDBO.achievements = majorAchievements.name;
					}
					updatedEmpMajorAchievementsDBOSet.add(majorAchievemnstDBO);
				}
			}
			//			if(!Utils.isNullOrEmpty(majorAchievementsDBOSet)) {
			//				for(EmpMajorAchievementsDBO item : majorAchievementsDBOSet) {
			//					EmpMajorAchievementsDBO achievement = null;
			//					if(empMajorAchievementsMap.containsKey(item.id)) {	
			//						achievement = item;
			//						achievement.modifiedUsersId = Integer.parseInt(userId);
			//						empMajorAchievementsMap.remove(item.id);
			//					}
			//					else {
			//						achievement = item;
			//						achievement.recordStatus = 'A';
			//						achievement.enteredDate = LocalDateTime.now();
			//						achievement.createdUsersId = Integer.parseInt(userId);
			//						achievement.empDBO = empDBO;
			//					}
			//					updatedEmpMajorAchievementsDBOSet.add(achievement);
			//				}
			//			}
			if (!Utils.isNullOrEmpty(empMajorAchievementsMap)) {
				if (!Utils.isNullOrEmpty(empMajorAchievementsMap)) {
					empMajorAchievementsMap.forEach((entry, value) -> {
						value.modifiedUsersId = Integer.parseInt(userId);
						value.recordStatus = 'D';
						updatedEmpMajorAchievementsDBOSet.add(value);
					});
				}
			}
			if (!Utils.isNullOrEmpty(updatedEmpMajorAchievementsDBOSet)) {
				empDBO.empMajorAchievementsDBOSet = updatedEmpMajorAchievementsDBOSet;
			}
			isSaved = employeeProfileTransaction.saveData(empDBO);
		} catch (Exception e) {
		}
		return isSaved;
	}

	@SuppressWarnings("unused")
	public boolean saveSalaryAndLeaveDetails(EmployeeProfileDTO employeeProfile, String userId) {
		boolean isSaved = false;
		try {
			PfAndGratutyDetailsDTO pfDetails = null;
			PfAndGratutyDetailsDTO gratuityDetails = null;
			EmpJobDetailsDBO empJobDetailsDBO = null;
			EmployeeProfileLeaveDetailsDTO leaveDetails = null;
			String empId = employeeProfile.personalDetails.basicInformation.employeeId;
			FinalInterviewCommentsDTO finalInterviewCommentsDTO = employeeProfile.salaryAndLeaveDetails.finalInterviewCommentsDTO;
			if (!Utils.isNullOrEmpty(employeeProfile.salaryAndLeaveDetails.leaveDetails)) {
				leaveDetails = employeeProfile.salaryAndLeaveDetails.leaveDetails;
			}
			if (!Utils.isNullOrEmpty(employeeProfile.salaryAndLeaveDetails.pfAndNomineeDetails)) {
				pfDetails = employeeProfile.salaryAndLeaveDetails.pfAndNomineeDetails;
			}
			if (!Utils.isNullOrEmpty(employeeProfile.salaryAndLeaveDetails.gratuvityAndNomineeDetails)) {
				gratuityDetails = employeeProfile.salaryAndLeaveDetails.gratuvityAndNomineeDetails;
			}
			EmpDBO empDBO;
			empDBO = commonApiTransaction.find(EmpDBO.class, Integer.parseInt(empId));
			if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO())) {
				empJobDetailsDBO = Hibernate.unproxy(empDBO.getEmpJobDetailsDBO(), EmpJobDetailsDBO.class);
				//			empJobDetailsDBO = empDBO.empJobDetailsDBO;
			} else {
				empJobDetailsDBO = new EmpJobDetailsDBO();
			}
			EmpPayScaleDetailsDBO dbo = null;
			List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOsList = null;
			if (!Utils.isNullOrEmpty(employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode)) {
				if (!employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("Guest")) {
					dbo = employeeProfileTransaction.getEmpPayScaleDetails(empId);
					if (!Utils.isNullOrEmpty(dbo)) {
						empPayScaleDetailsComponentsDBOsList = employeeProfileTransaction.getEmpPayScaleDetailsComponentsDBOs(dbo.id);
					}
					empPayScaleDetailsComponentsDBOsList = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBOsList) ? empPayScaleDetailsComponentsDBOsList : new ArrayList<EmpPayScaleDetailsComponentsDBO>();
					finalInterviewCommentsDTO = !Utils.isNullOrEmpty(finalInterviewCommentsDTO) ? finalInterviewCommentsDTO : new FinalInterviewCommentsDTO();
					dbo = finalInterviewCommentsHelper1.convertEmpPayScaleDetailsDTOToDBO(empDBO.getEmpApplnEntriesDBO(), dbo, empPayScaleDetailsComponentsDBOsList, finalInterviewCommentsDTO, userId);
					if (!Utils.isNullOrEmpty(dbo)) {
						dbo.empDBO = empDBO;
						employeeProfileTransaction.saveOrUpdateEmpPay(dbo);
						//						empDBO.empPayScaleDetailsDBOSet.add(dbo);
					}
				}
			}
			if (!Utils.isNullOrEmpty(employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode)) {
				if (employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("CONTRACT")) {
					if (!Utils.isNullOrEmpty(leaveDetails.leaveCategory)) {
						if (!Utils.isNullOrEmpty(leaveDetails.leaveCategory.value)) {
							empJobDetailsDBO.empLeaveCategoryAllotmentId = new EmpLeaveCategoryAllotmentDBO();
							empJobDetailsDBO.empLeaveCategoryAllotmentId.empLeaveCategoryAllotmentId = Integer.parseInt(leaveDetails.leaveCategory.value);
						}
					}
					Set<EmpLeaveAllocationDBO> leaveAllocationDBOSet = new HashSet<>();
					if (!Utils.isNullOrEmpty(leaveDetails.leaveTypeDetails)) {
						for (EmpLeaveAllocationDTO leaveAllocation : leaveDetails.leaveTypeDetails) {
							EmpLeaveAllocationDBO leaveAllocationDBO = new EmpLeaveAllocationDBO();
							if (!Utils.isNullOrEmpty(leaveAllocation.leavesAllocated)) {
								leaveAllocationDBO.allottedLeaves = new BigDecimal(leaveAllocation.leavesAllocated);
							}
							if (!Utils.isNullOrEmpty(leaveAllocation.leavesRemaining)) {
								leaveAllocationDBO.leavesRemaining = new BigDecimal(leaveAllocation.leavesRemaining);
							}
							if (!Utils.isNullOrEmpty(leaveAllocation.leavesSanctioned)) {
								leaveAllocationDBO.sanctionedLeaves = new BigDecimal(leaveAllocation.leavesSanctioned);
							}
							//							if(!Utils.isNullOrEmpty(leaveAllocation.getYearSelect())) {
							//								if(!Utils.isNullOrEmpty(leaveAllocation.getYearSelect().getLabel())) {
							//									leaveAllocationDBO.setYear(Integer.parseInt(leaveAllocation.getYearSelect().getLabel()));
							//								}
							//							}
							leaveAllocationDBOSet.add(leaveAllocationDBO);
						}
					}
					empDBO.empLeaveAllocationDBOSet = leaveAllocationDBOSet;
				} else {
					ErpAcademicYearDBO acadmeicYearDBO = employeeProfileTransaction.getCurrentAcademicYear();
					//					List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = employeeProfileTransaction.getEmployeeLeaveAllocation(Integer.parseInt(empId) ,acadmeicYearDBO.academicYear);
					List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = employeeProfileTransaction.getEmployeeLeaveAllocation(Integer.parseInt(empId));
					Set<EmpLeaveAllocationDBO> empLeaveAllocationDBOSet = new HashSet<EmpLeaveAllocationDBO>();
					if (!Utils.isNullOrEmpty(empLeaveAllocationDBOList)) {
						empLeaveAllocationDBOList.forEach(empLeave -> {
							empLeave.recordStatus = 'D';
						});
						empLeaveAllocationDBOSet = empLeaveAllocationDBOList.stream().collect(Collectors.toSet());
						empDBO.empLeaveAllocationDBOSet = empLeaveAllocationDBOSet;
					}
					if (!Utils.isNullOrEmpty(empJobDetailsDBO.empLeaveCategoryAllotmentId)) {
						empJobDetailsDBO.empLeaveCategoryAllotmentId = null;
					}
				}
			}
			if (!Utils.isNullOrEmpty(employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode)) {
				if (employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING")) {
					Set<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOSet = new HashSet<>();
					if (!Utils.isNullOrEmpty(pfDetails)) {
						if (!Utils.isNullOrEmpty(pfDetails.accountNo)) {
							empJobDetailsDBO.pfAccountNo = pfDetails.accountNo;
						}
						if (!Utils.isNullOrEmpty(pfDetails.accountDate)) {
							empJobDetailsDBO.pfDate = LocalDate.parse(pfDetails.accountDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
						if (!Utils.isNullOrEmpty(pfDetails.uanNo)) {
							empJobDetailsDBO.uanNo = pfDetails.uanNo;
						}
						if (!Utils.isNullOrEmpty(pfDetails.nomineeDetails)) {
							for (EmpPfGratuityNomineesDTO nomineeDetails : pfDetails.nomineeDetails) {
								if (!Utils.isNullOrEmpty(nomineeDetails.nominee) || !Utils.isNullOrEmpty(nomineeDetails.nomineeAddress) ||
										!Utils.isNullOrEmpty(nomineeDetails.nomineeRelationship) || !Utils.isNullOrEmpty(nomineeDetails.nomineeDob) ||
										!Utils.isNullOrEmpty(nomineeDetails.sharePercentage) || !Utils.isNullOrEmpty(nomineeDetails.under18GuardName)) {
									EmpPfGratuityNomineesDBO empPfGratuityNomineesDBO = new EmpPfGratuityNomineesDBO();
									empPfGratuityNomineesDBO.isPf = true;
									if (!Utils.isNullOrEmpty(nomineeDetails.empPfGratuityNomineesId)) {
										empPfGratuityNomineesDBO.empPfGratuityNomineesId = Integer.parseInt(nomineeDetails.empPfGratuityNomineesId);
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.nominee)) {
										empPfGratuityNomineesDBO.nominee = nomineeDetails.nominee;
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.nomineeAddress)) {
										empPfGratuityNomineesDBO.nomineeAddress = nomineeDetails.nomineeAddress;
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.nomineeRelationship)) {
										empPfGratuityNomineesDBO.nomineeRelationship = nomineeDetails.nomineeRelationship;
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.nomineeDob)) {
										empPfGratuityNomineesDBO.nomineeDob = LocalDate.parse(nomineeDetails.nomineeDob, DateTimeFormatter.ofPattern("M/d/yyyy"));
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.sharePercentage)) {
										empPfGratuityNomineesDBO.sharePercentage = new BigDecimal(nomineeDetails.sharePercentage);
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.under18GuardName)) {
										empPfGratuityNomineesDBO.under18GuardName = nomineeDetails.under18GuardName;
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.under18GuardianAddress)) {
										empPfGratuityNomineesDBO.under18GuardianAddress = nomineeDetails.under18GuardianAddress;
									}
									empPfGratuityNomineesDBO.recordStatus = 'A';
									empPfGratuityNomineesDBO.empJobDetailsDBO = empJobDetailsDBO;
									if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO)) {
										empPfGratuityNomineesDBOSet.add(empPfGratuityNomineesDBO);
									}
								}
							}
						}
					}
					if (!Utils.isNullOrEmpty(gratuityDetails)) {
						if (!Utils.isNullOrEmpty(gratuityDetails.accountNo)) {
							empJobDetailsDBO.gratuityNo = gratuityDetails.accountNo;
						}
					}
					if (!Utils.isNullOrEmpty(pfDetails)) {
						if (!Utils.isNullOrEmpty(pfDetails.accountDate)) {
							empJobDetailsDBO.gratuityDate = LocalDate.parse(pfDetails.accountDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
						}
					}
					if (!Utils.isNullOrEmpty(gratuityDetails)) {
						if (!Utils.isNullOrEmpty(gratuityDetails.nomineeDetails)) {
							for (EmpPfGratuityNomineesDTO nomineeDetails : gratuityDetails.nomineeDetails) {
								if (!Utils.isNullOrEmpty(nomineeDetails.nominee) || !Utils.isNullOrEmpty(nomineeDetails.nomineeAddress) ||
										!Utils.isNullOrEmpty(nomineeDetails.nomineeRelationship) || !Utils.isNullOrEmpty(nomineeDetails.nomineeDob) ||
										!Utils.isNullOrEmpty(nomineeDetails.sharePercentage) || !Utils.isNullOrEmpty(nomineeDetails.under18GuardName) ||
										!Utils.isNullOrEmpty(nomineeDetails.under18GuardianAddress)) {
									EmpPfGratuityNomineesDBO empPfGratuityNomineesDBO = new EmpPfGratuityNomineesDBO();
									empPfGratuityNomineesDBO.isGratuity = true;
									if (!Utils.isNullOrEmpty(nomineeDetails.empPfGratuityNomineesId)) {
										empPfGratuityNomineesDBO.empPfGratuityNomineesId = Integer.parseInt(nomineeDetails.empPfGratuityNomineesId);
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.nominee)) {
										empPfGratuityNomineesDBO.nominee = nomineeDetails.nominee;
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.nomineeAddress)) {
										empPfGratuityNomineesDBO.nomineeAddress = nomineeDetails.nomineeAddress;
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.nomineeRelationship)) {
										empPfGratuityNomineesDBO.nomineeRelationship = nomineeDetails.nomineeRelationship;
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.nomineeDob)) {
										empPfGratuityNomineesDBO.nomineeDob = LocalDate.parse(nomineeDetails.nomineeDob, DateTimeFormatter.ofPattern("M/d/yyyy"));
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.sharePercentage)) {
										empPfGratuityNomineesDBO.sharePercentage = new BigDecimal(nomineeDetails.sharePercentage);
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.under18GuardName)) {
										empPfGratuityNomineesDBO.under18GuardName = nomineeDetails.under18GuardName;
									}
									if (!Utils.isNullOrEmpty(nomineeDetails.under18GuardianAddress)) {
										empPfGratuityNomineesDBO.under18GuardianAddress = nomineeDetails.under18GuardianAddress;
									}
									empPfGratuityNomineesDBO.empJobDetailsDBO = empJobDetailsDBO;
									empPfGratuityNomineesDBO.recordStatus = 'A';
									if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO)) {
										empPfGratuityNomineesDBOSet.add(empPfGratuityNomineesDBO);
									}
								}
							}
						}
					}
					empJobDetailsDBO.empPfGratuityNomineesDBOS = empPfGratuityNomineesDBOSet;
				} else {
					String jobId = null;
					if (!Utils.isNullOrEmpty(empJobDetailsDBO)) {
						empJobDetailsDBO.setPfAccountNo(null);
						empJobDetailsDBO.setPfDate(null);
						empJobDetailsDBO.setUanNo(null);
						empJobDetailsDBO.setGratuityDate(null);
						empJobDetailsDBO.setGratuityNo(null);
					}
					if (!Utils.isNullOrEmpty(employeeProfile.jobDetails.jobInformation.empJobDetails.jobDetailsId)) {
						jobId = employeeProfile.jobDetails.jobInformation.empJobDetails.jobDetailsId;
						List<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOList = employeeProfileTransaction.getEmpGratuityNomineesDBOList(jobId);
						if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBOList)) {
							empPfGratuityNomineesDBOList.forEach(list -> {
								list.setRecordStatus('D');
							});
							Set<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOSet = new HashSet<EmpPfGratuityNomineesDBO>();
							empPfGratuityNomineesDBOSet = empPfGratuityNomineesDBOList.stream().collect(Collectors.toSet());
							empJobDetailsDBO.setEmpPfGratuityNomineesDBOS(empPfGratuityNomineesDBOSet);
						}
					}
				}
			}
			if (!Utils.isNullOrEmpty(employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode)) {
				if (employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("REGULAR") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("ADMINISTRATIVE") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("TECHNICAL") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("WAGE_STAFF") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("MULTI_TASKING") ||
						employeeProfile.jobDetails.jobInformation.empDTO.jobCategory.jobCategoryCode.equalsIgnoreCase("CONTRACT")) {
					Set<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = new HashSet<>();
					if (!Utils.isNullOrEmpty(employeeProfile.salaryAndLeaveDetails.leaveDetails.leaveCategory.value)) {
						empJobDetailsDBO.empLeaveCategoryAllotmentId = new EmpLeaveCategoryAllotmentDBO();
						empJobDetailsDBO.empLeaveCategoryAllotmentId.empLeaveCategoryAllotmentId = Integer.parseInt(employeeProfile.salaryAndLeaveDetails.leaveDetails.leaveCategory.value);
					}
					for (EmpLeaveAllocationDTO leaveAllocationDTO : employeeProfile.salaryAndLeaveDetails.leaveDetails.leaveTypeDetails) {
						if (!Utils.isNullOrEmpty(leaveAllocationDTO)) {
							EmpLeaveAllocationDBO leaveAllocationDBO = new EmpLeaveAllocationDBO();
							if (!Utils.isNullOrEmpty(leaveAllocationDTO.id)) {
								leaveAllocationDBO.id = Integer.parseInt(leaveAllocationDTO.id);
							}
							if (!Utils.isNullOrEmpty(leaveAllocationDTO.leavesAllocated)) {
								leaveAllocationDBO.allottedLeaves = new BigDecimal(leaveAllocationDTO.leavesAllocated);
							}
							if (!Utils.isNullOrEmpty(leaveAllocationDTO.leavesSanctioned)) {
								leaveAllocationDBO.sanctionedLeaves = new BigDecimal(leaveAllocationDTO.leavesSanctioned);
							}
							if (!Utils.isNullOrEmpty(leaveAllocationDTO.leavesRemaining)) {
								leaveAllocationDBO.leavesRemaining = new BigDecimal(leaveAllocationDTO.leavesRemaining);
							}
							//						if(!Utils.isNullOrEmpty(leaveAllocationDTO.year)) {
							//							leaveAllocationDBO.year = Integer.parseInt(leaveAllocationDTO.year);
							//						}
							if (!Utils.isNullOrEmpty(leaveAllocationDTO.getYearSelect())) {
								if (!Utils.isNullOrEmpty(leaveAllocationDTO.getYearSelect().getLabel())) {
									leaveAllocationDBO.year = Integer.parseInt(leaveAllocationDTO.getYearSelect().getLabel());
								}
							}
							if (!Utils.isNullOrEmpty(leaveAllocationDTO.leaveTypeId)) {
								leaveAllocationDBO.leaveType = new EmpLeaveTypeDBO();
								leaveAllocationDBO.leaveType.id = Integer.parseInt(leaveAllocationDTO.leaveTypeId);
							}
							leaveAllocationDBO.recordStatus = 'A';
							leaveAllocationDBO.empDBO = empDBO;
							empLeaveAllocationDBOList.add(leaveAllocationDBO);
						}
					}
					empDBO.empLeaveAllocationDBOSet = empLeaveAllocationDBOList;
				} else {
					ErpAcademicYearDBO acadmeicYearDBO = employeeProfileTransaction.getCurrentAcademicYear();
					//					List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = employeeProfileTransaction.getEmployeeLeaveAllocation(Integer.parseInt(empId) ,acadmeicYearDBO.academicYear);
					List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = employeeProfileTransaction.getEmployeeLeaveAllocation(Integer.parseInt(empId));
					Set<EmpLeaveAllocationDBO> empLeaveAllocationDBOSet = new HashSet<EmpLeaveAllocationDBO>();
					if (!Utils.isNullOrEmpty(empLeaveAllocationDBOList)) {
						empLeaveAllocationDBOList.forEach(empLeave -> {
							empLeave.recordStatus = 'D';
						});
						empLeaveAllocationDBOSet = empLeaveAllocationDBOList.stream().collect(Collectors.toSet());
						empDBO.empLeaveAllocationDBOSet = empLeaveAllocationDBOSet;
					}
					if (!Utils.isNullOrEmpty(empJobDetailsDBO.empLeaveCategoryAllotmentId)) {
						empJobDetailsDBO.empLeaveCategoryAllotmentId = null;
					}
				}
			}
			empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
			return employeeProfileTransaction.saveData(empDBO);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSaved;
	}

	public Integer createEmployee(EmpDTO empDTO, String userId) {
		EmpDBO empDBO;
		try {
			if (!Utils.isNullOrEmpty(empDTO.getApplnEntriesId())) {
				empDBO = commonEmployeeHandler.copyApplnDataToEmpData(Integer.parseInt(empDTO.getApplnEntriesId()), userId);
				empDBO.setCreatedUsersId(Integer.parseInt(userId));
				empDBO.setRecordStatus('A');
			} else {
				empDBO = new EmpDBO();
				EmpPersonalDataDBO personalDBO = new EmpPersonalDataDBO();
				if (!Utils.isNullOrEmpty(empDTO.empName)) {
					empDBO.empName = empDTO.empName;
				}
				if (!Utils.isNullOrEmpty(empDTO.nationality)) {
					personalDBO.erpCountryDBO = new ErpCountryDBO();
					personalDBO.erpCountryDBO.id = Integer.parseInt(empDTO.nationality.value);
				}
				if (!Utils.isNullOrEmpty(empDTO.maritalStatus)) {
					personalDBO.erpMaritalStatusDBO = new ErpMaritalStatusDBO();
					personalDBO.erpMaritalStatusDBO.id = Integer.parseInt(empDTO.maritalStatus.value);
				}
				if (!Utils.isNullOrEmpty(empDTO.applnEntriesId)) {
					empDBO.empApplnEntriesDBO = new EmpApplnEntriesDBO();
					empDBO.empApplnEntriesDBO.id = Integer.parseInt(empDTO.applnEntriesId);
				}
				empDBO.recordStatus = 'A';
				personalDBO.recordStatus = 'A';
				empDBO.setEmpPersonalDataDBO(personalDBO);
			}
			if (!Utils.isNullOrEmpty(empDTO.empId)) {
				empDBO.empNumber = empDTO.empId;
			}
			if (!Utils.isNullOrEmpty(empDTO.employeeCategoryId)) {
				empDBO.empEmployeeCategoryDBO = new EmpEmployeeCategoryDBO();
				empDBO.empEmployeeCategoryDBO.id = Integer.parseInt(empDTO.employeeCategoryId);
			}
			if (!Utils.isNullOrEmpty(empDTO.jobCategoryId)) {
				empDBO.empEmployeeJobCategoryDBO = new EmpEmployeeJobCategoryDBO();
				empDBO.empEmployeeJobCategoryDBO.id = Integer.parseInt(empDTO.jobCategoryId);
			}
			if (!Utils.isNullOrEmpty(empDTO.campusId) && !Utils.isNullOrEmpty(empDTO.departmentId)) {
				empDBO.erpCampusDepartmentMappingDBO = new ErpCampusDepartmentMappingDBO();
				empDBO.erpCampusDepartmentMappingDBO = employeeProfileTransaction.getCampusDepartmentMapping(empDTO.campusId, empDTO.departmentId);
			}
			if (!Utils.isNullOrEmpty(empDTO.getApplnEntriesId())) {
				int empid = commonEmployeeTransaction.mergeEmployee(empDBO);
				return empid;
			} else {
				employeeProfileTransaction.saveData(empDBO);
				return empDBO.id;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings({"rawtypes", "unchecked", "unused"})
	public EmpDTO getApplicantDetails(String applicationNo) {
		EmpApplnEntriesDBO applnDetails;
		EmpDTO dto = new EmpDTO();
		Tuple tuple;
		try {
			boolean isTrue = employeeProfileTransaction.duplicateCheck(applicationNo);
			if (isTrue) {
				ApiResult result = new ApiResult();
				result.success = false;
				EmpDTO empDTO = null;
				return empDTO;
			}
			applnDetails = employeeProfileTransaction.getApplicantDetails(applicationNo);
			if (applnDetails != null) {
				if (!Utils.isNullOrEmpty(applnDetails.id)) {
					dto.applnEntriesId = applnDetails.id.toString();
				}
				if (!Utils.isNullOrEmpty(applnDetails.applicantName)) {
					dto.empName = applnDetails.applicantName;
				}
				if (!Utils.isNullOrEmpty(applnDetails.erpCampusDBO)) {
					dto.employeeCampus = new LookupItemDTO();
					dto.employeeCampus.value = String.valueOf(applnDetails.erpCampusDBO.id);
					dto.employeeCampus.label = applnDetails.erpCampusDBO.campusName;
				}
				if (!Utils.isNullOrEmpty(applnDetails.empEmployeeCategoryDBO)) {
					dto.employeeCategory = new EmployeeApplicationDTO();
					dto.employeeCategory.value = applnDetails.empEmployeeCategoryDBO.id.toString();
					dto.employeeCategory.label = applnDetails.empEmployeeCategoryDBO.employeeCategoryName;
					dto.employeeCategory.isEmployeeCategoryAcademic = applnDetails.empEmployeeCategoryDBO.isEmployeeCategoryAcademic;
				}
				if (!Utils.isNullOrEmpty(applnDetails.empEmployeeJobCategoryDBO)) {
					dto.jobCategory = new EmployeeApplicationDTO();
					dto.jobCategory.value = applnDetails.empEmployeeJobCategoryDBO.id.toString();
					dto.jobCategory.label = applnDetails.empEmployeeJobCategoryDBO.employeeJobName;
				}
				if (!Utils.isNullOrEmpty(applnDetails.empApplnPersonalDataDBO.erpCountryDBO)) {
					dto.nationality = new LookupItemDTO();
					dto.nationality.value = Integer.toString(applnDetails.empApplnPersonalDataDBO.erpCountryDBO.id);
					dto.nationality.label = applnDetails.empApplnPersonalDataDBO.erpCountryDBO.countryName;
				}
				if (!Utils.isNullOrEmpty(applnDetails.empApplnPersonalDataDBO.erpMaritalStatusDBO)) {
					dto.maritalStatus = new LookupItemDTO();
					dto.maritalStatus.value = Integer.toString(applnDetails.empApplnPersonalDataDBO.erpMaritalStatusDBO.id);
					dto.maritalStatus.label = applnDetails.empApplnPersonalDataDBO.erpMaritalStatusDBO.maritalStatusName;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Mono<ApiResult> uploadFiles(Flux<FilePart> data, String directory, String[] fileTypeAccept, boolean isHashFileName, String uploadFor) throws Exception {
		List<Tuple2<String, String>> hashedFineNamesList = new ArrayList<>();
		Mono<ApiResult> result = uploadFiles(data, directory, fileTypeAccept, isHashFileName, hashedFineNamesList);
		result.subscribe(res -> {
			res.dto = hashedFineNamesList;
			if (!res.success) {
				res.failureMessage = uploadFor;
			}
		});
		return result;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Mono<ApiResult> uploadFiles(Flux<FilePart> data, String filePath, String[] fileTypeAccept, boolean isHashFileName, List<Tuple2<String, String>> hashedFineNamesList) throws Exception {
		Tika tika = new Tika();
		ApiResult result = new ApiResult();
		Map<String, String> hashedFileNameMap = new HashMap<>();
		return data.takeWhile(item -> {
			String fileName = item.filename().substring(item.filename().indexOf("_") + 1);
			try {
				if (Utils.isNullOrEmpty(hashedFineNamesList) || (!Utils.isNullOrEmpty(hashedFineNamesList) && !checkDuplicateFileName(hashedFineNamesList, fileName))) {
					byte[] md5CheckSumSalt = Utils.getMD5CheckSumSalt();
					String md5HashFileName = Utils.createMD5CheckSum(Utils.removeFileExtension(fileName), md5CheckSumSalt);
					md5HashFileName = md5HashFileName + Utils.getFileExtension(fileName);
					hashedFileNameMap.put(fileName, md5HashFileName);
					hashedFineNamesList.add(Tuples.of(fileName, md5HashFileName));
					File file = new File(filePath + md5HashFileName);
					item.transferTo(file);
					String detectFileType = tika.detect(file);
					result.success = Arrays.stream(fileTypeAccept).anyMatch(detectFileType::contains);//----Improves performance if size of the array is less.
				}
			} catch (Exception e) {
				result.success = false;
				result.failureMessage = e.getMessage();
			} finally {
				if (!result.success) {
					data.map(item1 -> {
						String fname = item1.filename().substring(item1.filename().indexOf("_") + 1);
						if (!Utils.isNullOrEmpty(hashedFileNameMap) && hashedFileNameMap.containsKey(fname)) {
							fname = hashedFileNameMap.get(fname);
							File file1 = new File(filePath + fname);
							if (file1.exists()) {
								file1.delete();
							}
						}
						return Mono.just(result);
					}).subscribe();
					return false;
				}
			}
			return true;
		}).then(Mono.just(result));
	}

	public boolean checkDuplicateFileName(List<Tuple2<String, String>> hashedFineNamesList, String fileName) {
		boolean isDuplicate = false;
		for (Tuple2<String, String> tuple : hashedFineNamesList) {
			if (fileName.equalsIgnoreCase(tuple.getT1())) {
				isDuplicate = true;
				break;
			}
		}
		return isDuplicate;
	}

	public ApiResult<List<LookupItemDTO>> getCampusByDepartment(String departmentId) {
		ApiResult<List<LookupItemDTO>> campusList = new ApiResult<List<LookupItemDTO>>();
		try {
			campusList = employeeProfileTransaction.getCampusByDepartment(departmentId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return campusList;
	}

	public EmpApproversDetailsDBO setDetails(EmpApproversDetailsDTO approversDetails, String type, Map<String, EmpApproversDetailsDBO> map, String userId, EmpApproversDBO approversDBO) {
		EmpApproversDetailsDBO subDbo = null;
		if (map.containsKey(type)) {
			subDbo = map.get(type);
		} else {
			subDbo = new EmpApproversDetailsDBO();
			subDbo.setEmpApproversId(approversDBO);
			subDbo.getEmpApproversId().setId(approversDBO.getId());
			subDbo.setCreatedUsersId(Integer.parseInt(userId));
		}
		if (type.equalsIgnoreCase("Appraiser")) {
			if (!Utils.isNullOrEmpty(approversDetails.getLevelOneAppraiser())) {
				if (!Utils.isNullOrEmpty(approversDetails.getLevelOneAppraiser().getValue())) {
					subDbo.setLevelOneAppraiserId(new EmpDBO());
					subDbo.getLevelOneAppraiserId().setId(Integer.parseInt(approversDetails.getLevelOneAppraiser().getValue()));
				}
			}
			if (!Utils.isNullOrEmpty(approversDetails.getLevelTwoAppraiser())) {
				if (!Utils.isNullOrEmpty(approversDetails.getLevelTwoAppraiser().getValue())) {
					subDbo.setLevelTwoAppraiserId(new EmpDBO());
					subDbo.getLevelTwoAppraiserId().setId(Integer.parseInt(approversDetails.getLevelTwoAppraiser().getValue()));
				}
			}
		} else if (type.equalsIgnoreCase("Leave")) {
			if (!Utils.isNullOrEmpty(approversDetails.getLeaveApprover())) {
				if (!Utils.isNullOrEmpty(approversDetails.getLeaveApprover().getValue())) {
					subDbo.setLeaveApproverId(new EmpDBO());
					subDbo.getLeaveApproverId().setId(Integer.parseInt(approversDetails.getLeaveApprover().getValue()));
				}
			}
			if (!Utils.isNullOrEmpty(approversDetails.getLeaveAuthorizer())) {
				if (!Utils.isNullOrEmpty(approversDetails.getLeaveAuthorizer().getValue())) {
					subDbo.setLeaveAuthorizerId(new EmpDBO());
					subDbo.getLeaveAuthorizerId().setId(Integer.parseInt(approversDetails.getLeaveAuthorizer().getValue()));
				}
			}
		} else if (type.equalsIgnoreCase("Work Dairy")) {
			if (!Utils.isNullOrEmpty(approversDetails.getWorkDairyApprover())) {
				if (!Utils.isNullOrEmpty(approversDetails.getWorkDairyApprover().getValue())) {
					subDbo.setWorkDairyApproverId(new EmpDBO());
					subDbo.getWorkDairyApproverId().setId(Integer.parseInt(approversDetails.getWorkDairyApprover().getValue()));
				}
			}
		}
		subDbo.setApprovalType(type);
		subDbo.setRecordStatus('A');
		return subDbo;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> duplicateCheckEmpNo(String empNO) {
		return employeeProfileTransaction.duplicateCheckEmpNo(empNO).map(Utils::responseResult).flatMap(s -> {
			if (!s.success) {
				s.setFailureMessage("Duplicate Emp no");
			}
			return Mono.just(s);
		});
	}

	public Flux<SelectDTO> getEmployeeList(String campusId) {
		List<EmpDBO> list = employeeProfileTransaction.getEmployeeList(campusId);
		return this.convertEmpDBOToDTO(list);
	}

	private Flux<SelectDTO> convertEmpDBOToDTO(List<EmpDBO> list) {
		List<SelectDTO> empSelectDTOList = new ArrayList<SelectDTO>();
		if (!Utils.isNullOrEmpty(list)) {
			List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
			list.forEach(empList -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setValue(String.valueOf(empList.getId()));
				selectDTO.setLabel(String.valueOf(empList.getEmpName()));
				selectDTOList.add(selectDTO);
			});
			empSelectDTOList.addAll(selectDTOList);
		}
		return Flux.fromIterable(empSelectDTOList);
	}

	public Flux<EmpProfileGridDTO> getEmpProfileGridData() {
		return employeeProfileTransaction.getEmpProfileGridData().flatMapMany(Flux::fromIterable).map(employeeProfileHelper::convertEmpDTO);
	}

	public Mono<EmpPersonalDataTabDTO> getEmplProfilePersonalDataTabDTO(int empId, String userId, String userCampusIds) {
		EmpPersonalDataTabDTO empPersonalDataTabDTO = employeeProfileTransaction.getEmployeePersonalDataTabDetails(empId);
		if (!Utils.isNullOrEmpty(empPersonalDataTabDTO)) {
			employeeProfileHelper.setPersonalDataOtherDetails(empPersonalDataTabDTO, userId, userCampusIds); //setting the remaining information like photo url
			//---dependent details setting
			List<EmpProfileFamilyDependentDTO> dependentList = employeeProfileTransaction.getEmpProfileFamilyDependentDetails(empPersonalDataTabDTO.getPersonalDataId());
			if (!Utils.isNullOrEmpty(dependentList)) {
				Comparator<EmpProfileFamilyDependentDTO> customComparator = Comparator.comparing(
						EmpProfileFamilyDependentDTO::getRelationship,
						Comparator.nullsLast(Comparator.naturalOrder())
				).thenComparing(
						EmpProfileFamilyDependentDTO::getOtherDependentRelationship,
						Comparator.nullsLast(Comparator.naturalOrder())
				);
				Collections.sort(dependentList, customComparator);
				empPersonalDataTabDTO.setEmpFamilyDependentDTOList(dependentList);
			}
			//----pf/gratuity details
			/*if (!Utils.isNullOrEmpty(empPersonalDataTabDTO.getJobDetailsId())) {
				List<EmpProfilePFandGratuityDTO> pfGratuityDetailsList = employeeProfileTransaction.getPFandGratuityDetails(empPersonalDataTabDTO.getJobDetailsId());
				empPersonalDataTabDTO.setEmpProfilePFandGratuityDTOList(pfGratuityDetailsList);
			}*/
			return Mono.just(empPersonalDataTabDTO);
		}
		return null;
	}

	public Mono<ApiResult> saveOrUpdatePersonalDataTab(Mono<EmpPersonalDataTabDTO> data, String userId) {
		return data.handle((empPersonalDataTabDTO, synchronousSink) -> {
					synchronousSink.next(empPersonalDataTabDTO);
				}).cast(EmpPersonalDataTabDTO.class)
				.map(data1 -> convertDtoToEmpDbo(data1, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.get(0))) {
						EmpDBO empDBO = (EmpDBO) s.get(0);
						employeeProfileTransaction.updateEmpDBO(empDBO);
						if (!Utils.isNullOrEmpty(s.get(1))) {
							List<FileUploadDownloadDTO> uniqueFileNameList = (List<FileUploadDownloadDTO>) s.get(1);
							if ((!Utils.isNullOrEmpty(uniqueFileNameList))) {
								aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList).subscribe();
							}
						}
					}
					return Mono.just(Boolean.TRUE);
				})
				.map(Utils::responseResult);
	}

	public List<Object> convertDtoToEmpDbo(EmpPersonalDataTabDTO dto, String userId) {
		EmpDBO empDBO = employeeProfileTransaction.getEmployeeDBO(dto.getEmpId());
		dto.setUniqueFileNameList(new ArrayList<FileUploadDownloadDTO>());
		if (!Utils.isNullOrEmpty(empDBO)) {
			employeeProfileHelper.setEmployeeDBO(empDBO, dto, Integer.parseInt(userId));
			employeeProfileHelper.setPersonalDataDBO(empDBO.getEmpPersonalDataDBO(), dto, Integer.parseInt(userId));
			employeeProfileHelper.setAdditionalPersonalDataDBO(empDBO.getEmpPersonalDataDBO(), dto, Integer.parseInt(userId));
			employeeProfileHelper.setDependentDBO(dto.getEmpFamilyDependentDTOList(), empDBO.getEmpPersonalDataDBO(), Integer.parseInt(userId));
			employeeProfileHelper.setPfAndGratuityDBO(dto.getEmpProfilePFandGratuityDTOList(), empDBO, Integer.parseInt(userId));
		}
		List<Object> objects = new ArrayList<Object>();
		objects.add(empDBO);
		objects.add(dto.getUniqueFileNameList());
		return objects;
	}

	public Mono<EmpProfileSidePanelDTO> getEmpProfileSidePanel(int empId) {
		EmpProfileSidePanelDTO empProfileSidePanelDTO = employeeProfileTransaction.getEmployeeSidePanelDetails(empId);
		if (!Utils.isNullOrEmpty(empProfileSidePanelDTO)) {
			employeeProfileHelper.setSidePanelOtherDetails(empProfileSidePanelDTO);//setting the remaining information like photo url
			return Mono.just(empProfileSidePanelDTO);
		}
		return null;
	}

	public Mono<EmpEmploymentTabDTO> getEmploymentDetails(Integer empId, String userId, String userCampusIds) {
		EmpEmploymentTabDTO empEmploymentTabDTO = employeeProfileTransaction.getEmploymentDetails(empId);
		List<EmpRemarksDetailsDTO> empRemarksDetailsDTOList = employeeProfileTransaction.getRemarksDetails(empId);
		if (!Utils.isNullOrEmpty(empRemarksDetailsDTOList)) {
			List<EmpRemarksDetailsDTO> empRemarksDetailsDTOListNew = employeeProfileHelper.updateRemarkListDTO(empRemarksDetailsDTOList, userId, userCampusIds);
			empEmploymentTabDTO.setEmpRemarksDetailsDTOList(empRemarksDetailsDTOListNew);
		}
		List<EmpEmploymentHistoryDTO> employmentHistoryList = employeeProfileTransaction.getEmploymentHistory(empId);
		empEmploymentTabDTO.setEmploymentHistoryDTOList(employeeProfileHelper.updateEmploymentHistoryDTO(employmentHistoryList));//to come in single row
		empEmploymentTabDTO.setLetterDetailsDTOList(employeeProfileTransaction.getLetterDetails(empId));
		List<EmpProfileGuestContractDetailsDTO> guestContractList = employeeProfileTransaction.getContractDetails(empId);
		//guest contract details
		if (!Utils.isNullOrEmpty(guestContractList)) {
			Comparator<EmpProfileGuestContractDetailsDTO> customComparator = Comparator.comparing(EmpProfileGuestContractDetailsDTO::getIsCurrent, Comparator.reverseOrder()).
					thenComparing(EmpProfileGuestContractDetailsDTO::getContractEmpStartDate, Comparator.reverseOrder())
					.thenComparing(EmpProfileGuestContractDetailsDTO::getContractEmpEndDate, Comparator.reverseOrder());
			guestContractList.sort(customComparator);
			empEmploymentTabDTO.setGuestContractList(guestContractList);
		}
		if(!Utils.isNullOrEmpty(empEmploymentTabDTO.getLeaveInitializeMonth())) {
			Month targetMonth = Month.of(empEmploymentTabDTO.getLeaveInitializeMonth());
			LocalDate currentDate = LocalDate.now();
			int currentYear = currentDate.getYear();
			Integer year = null;
			YearMonth targetYearMonth = YearMonth.of(currentYear, targetMonth);
			if (targetYearMonth.isAfter(YearMonth.from(currentDate))) {
				year = currentYear - 1;
			} else {
				year = currentYear;
			}
			List<EmpProfileLeaveAllotmentDTO> empProfileLeaveAllotmentDTOList = employeeProfileTransaction.getAssignedLeaves(empId, year);
			empEmploymentTabDTO.setEmpProfileLeaveAllotmentDTOList(empProfileLeaveAllotmentDTOList);
		}
		return Mono.just(empEmploymentTabDTO);
	}

	public Mono<ApiResult> saveOrUpdateEmployment(Mono<EmpEmploymentTabDTO> data, String userId, String userCampusIds) {
		return data.handle((empEmploymentTabDTO, synchronousSink) -> {
					synchronousSink.next(empEmploymentTabDTO);
				}).cast(EmpEmploymentTabDTO.class)
				.map(data1 -> convertEmploymentDtoToEmpDbo(data1, userId, userCampusIds))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.get(0))) {
						EmpDBO empDBO = (EmpDBO) s.get(0);
						employeeProfileTransaction.updateEmpDBO(empDBO);
						if (!Utils.isNullOrEmpty(s.get(1))) {
							List<FileUploadDownloadDTO> uniqueFileNameList = (List<FileUploadDownloadDTO>) s.get(1);
							if ((!Utils.isNullOrEmpty(uniqueFileNameList))) {
								aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList).subscribe();
							}
						}
					}
					return Mono.just(Boolean.TRUE);
				})
				.map(Utils::responseResult);
	}

	public List<Object> convertEmploymentDtoToEmpDbo(EmpEmploymentTabDTO dto, String userId, String userCampusIds) {
		EmpDBO empDBO = employeeProfileTransaction.getEmployeeDBOForEmploymentTab(dto.getId());
		String authKey = "/Secured/Employee/Recruitment/EmployeeProfile/empProfileOfficeRemarksEnable";
		boolean isRemarksPrivilegeEnabled = employeeProfileHelper.isPrivilegeEnabled(authKey, userId, userCampusIds);
		dto.setUniqueFileNameList(new ArrayList<FileUploadDownloadDTO>());
		if (!Utils.isNullOrEmpty(empDBO)) {
			employeeProfileHelper.setDBOForEmploymentTab(empDBO, dto, Integer.parseInt(userId), isRemarksPrivilegeEnabled);
		}
		List<Object> objects = new ArrayList<Object>();
		objects.add(empDBO);
		objects.add(dto.getUniqueFileNameList());
		return objects;
	}

	public Mono<EmpQualificationTabDTO> getEducationDetails(Integer empId) {
		EmpQualificationTabDTO empQualificationTabDTO = employeeProfileTransaction.getQualificationTabDetails(empId);
		if (Utils.isNullOrEmpty(empQualificationTabDTO)) {
			empQualificationTabDTO = new EmpQualificationTabDTO();
		}
		List<EmpProfileEducationalDetailsDTO> empProfileEducationalDetailsDTOList = employeeProfileTransaction.getEducationDetails(empId);
		if (!Utils.isNullOrEmpty(empProfileEducationalDetailsDTOList)) {
			Set<Integer> ednIdSet = empProfileEducationalDetailsDTOList.stream().map(EmpProfileEducationalDetailsDTO::getId).collect(Collectors.toSet());
			List<EmpProfileEdnDetailsDocumentsDTO> empProfileEdnDetailsDocumentsDTOList = employeeProfileTransaction.getEducationDetailsDocuments(ednIdSet);
			Map<Integer, List<EmpProfileEdnDetailsDocumentsDTO>> empProfileEdnDetailsDocumentsDTOMap = empProfileEdnDetailsDocumentsDTOList.stream().collect(Collectors.groupingBy(EmpProfileEdnDetailsDocumentsDTO::getDocumentId));
			employeeProfileHelper.updateEducationalDetailsDTOList(empProfileEducationalDetailsDTOList, empProfileEdnDetailsDocumentsDTOMap);
			Comparator<EmpProfileEducationalDetailsDTO> customComparator = Comparator.comparing(
					EmpProfileEducationalDetailsDTO::getQualificationId,
					Comparator.nullsLast(Comparator.naturalOrder())
			).thenComparing(EmpProfileEducationalDetailsDTO::getYearOfRegistration);
			empProfileEducationalDetailsDTOList.sort(customComparator);

			empQualificationTabDTO.setEmpEducationalDetailsDTOList(empProfileEducationalDetailsDTOList);
			//eligibility test details
			List<EmpEligibilityTestDTO> empEligibilityTestDTOList = employeeProfileTransaction.getEligibilityDetails(empId);
			if (!Utils.isNullOrEmpty(empEligibilityTestDTOList)) {
				Set<Integer> eligibilityIdSet = empEligibilityTestDTOList.stream().map(EmpEligibilityTestDTO::getId).collect(Collectors.toSet());
				List<EmpEligibilityTestDocumentDTO> empEligibilityTestDocumentDTOList = employeeProfileTransaction.getEligibilityTestDocuments(eligibilityIdSet);
				Map<Integer, List<EmpEligibilityTestDocumentDTO>> empEligibilityTestDocumentDTOMap = empEligibilityTestDocumentDTOList.stream().collect(Collectors.groupingBy(EmpEligibilityTestDocumentDTO::getDocumentId));
				employeeProfileHelper.updateEligibilityTestDTOList(empEligibilityTestDTOList, empEligibilityTestDocumentDTOMap);
				empQualificationTabDTO.setEmpEligibilityTestDTOList(empEligibilityTestDTOList);
			}
		}
		return Mono.justOrEmpty(empQualificationTabDTO);
	}

	public Mono<ApiResult> saveOrUpdateQualificationDetails(Mono<EmpQualificationTabDTO> data, String userId) {
		return data.handle((empQualificationTabDTO, synchronousSink) -> {
					synchronousSink.next(empQualificationTabDTO);
				}).cast(EmpQualificationTabDTO.class)
				.map(data1 -> convertQualificationDtoToDBO(data1, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.get(0))) {
						EmpDBO empDBO = (EmpDBO) s.get(0);
						employeeProfileTransaction.updateEmpDBO(empDBO);
						if (!Utils.isNullOrEmpty(s.get(1))) {
							List<FileUploadDownloadDTO> uniqueFileNameList = (List<FileUploadDownloadDTO>) s.get(1);
							if ((!Utils.isNullOrEmpty(uniqueFileNameList))) {
								aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList).subscribe();
							}
						}
					}
					return Mono.just(Boolean.TRUE);
				})
				.map(Utils::responseResult);
	}

	public List<Object> convertQualificationDtoToDBO(EmpQualificationTabDTO dto, String userId) {
		EmpDBO empDBO = employeeProfileTransaction.getEmpDBOForQualificationTab(dto.getEmpId());
		dto.setUniqueFileNameList(new ArrayList<FileUploadDownloadDTO>());
		if (!Utils.isNullOrEmpty(empDBO)) {
			employeeProfileHelper.setDBOForQualificationTab(empDBO, dto, Integer.parseInt(userId));
		}
		List<Object> objects = new ArrayList<Object>();
		objects.add(empDBO);
		objects.add(dto.getUniqueFileNameList());
		return objects;
	}

	public Mono<EmpSalaryTabDTO> getSalaryDetails(Integer empId) {
		List<SalaryDetailsDTO> salaryDetailsDTOList = employeeProfileTransaction.getSalaryDetails(empId);
		if (!Utils.isNullOrEmpty(salaryDetailsDTOList)) {
			Set<Integer> payScaleIds = salaryDetailsDTOList.stream().map(SalaryDetailsDTO::getId).collect(Collectors.toSet());
			List<PayScaleDetailsDTO> payScaleDetailsDTOList = employeeProfileTransaction.getPayScaleDetailsComponents(payScaleIds);
			Map<Integer, List<PayScaleDetailsDTO>> payScaleDetailsDTOMap = new HashMap<>();
			if (!Utils.isNullOrEmpty(payScaleDetailsDTOList)) {
				payScaleDetailsDTOMap = payScaleDetailsDTOList.stream().collect(Collectors.groupingBy(PayScaleDetailsDTO::getPayScaleId));
			}
			List<PayScaleDetailsDTO> payScaleComponentDTOList = employeeProfileTransaction.getSalaryComponents();
			Map<Integer, List<PayScaleDetailsDTO>> payScaleComponentMap = new HashMap<>();
			if (!Utils.isNullOrEmpty(payScaleComponentDTOList)) {
				payScaleComponentMap = payScaleComponentDTOList.stream().collect(Collectors.groupingBy(PayScaleDetailsDTO::getId));
			}
			employeeProfileHelper.updateSalaryDetailsDTOList(salaryDetailsDTOList, payScaleDetailsDTOMap, payScaleComponentMap);

		}
		EmpSalaryTabDTO empSalaryTabDTO = new EmpSalaryTabDTO();
		empSalaryTabDTO.setSalaryDetailsDTOList(salaryDetailsDTOList);
		empSalaryTabDTO.setEmpId(empId);
		EmpPFandGratuityDTO empPFandGratuityDTO = employeeProfileTransaction.getPFGratuityDetails(empId);
		if(!Utils.isNullOrEmpty(empPFandGratuityDTO)){
			List<EmpProfilePFandGratuityDTO> pfGratuityDetailsList = employeeProfileTransaction.getPFandGratuityDetails(empPFandGratuityDTO.getJobDetailsId());
			Comparator<EmpProfilePFandGratuityDTO> orderComparator = Comparator.comparing(EmpProfilePFandGratuityDTO::getIsPf, Comparator.reverseOrder()).thenComparing(EmpProfilePFandGratuityDTO::getIsGratuity, Comparator.reverseOrder());
			pfGratuityDetailsList.sort(orderComparator);
			empPFandGratuityDTO.setEmpPfGratuityNomineesDTOList(pfGratuityDetailsList);
		}
		empSalaryTabDTO.setEmpPFandGratuityDTO(empPFandGratuityDTO);
		return Mono.justOrEmpty(empSalaryTabDTO);
	}

	public Mono<List<PayScaleDetailsDTO>> getPayScaleDetails(Integer cellId) {
		List<PayScaleDetailsDTO> payScaleComponentDTOList = employeeProfileTransaction.getSalaryComponents();
		PayScaleDetailsDTO matrixDTO = employeeProfileTransaction.getMatrixDetailsForAmount(cellId);
		employeeProfileHelper.updatePayScaleDTOList(payScaleComponentDTOList, matrixDTO);
		Comparator<PayScaleDetailsDTO> orderComparator = Comparator.comparing(PayScaleDetailsDTO::getDisplayOrder);
		payScaleComponentDTOList.sort(orderComparator);
		return Mono.justOrEmpty(payScaleComponentDTOList);
	}

	public Mono<ApiResult> saveOrUpdateSalaryDetails(Mono<EmpSalaryTabDTO> data, String userId, String userCampusIds) {
		return data.handle((empSalaryTabDTO, synchronousSink) -> {
					synchronousSink.next(empSalaryTabDTO);
				}).cast(EmpSalaryTabDTO.class)
				.map(data1 -> convertSalaryTabDtoToDBO(data1, userId, userCampusIds))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.get(0))) {
						EmpDBO empDBO = (EmpDBO) s.get(0);
						employeeProfileTransaction.updateEmpDBO(empDBO);
						if (!Utils.isNullOrEmpty(s.get(1))) {
							List<FileUploadDownloadDTO> uniqueFileNameList = (List<FileUploadDownloadDTO>) s.get(1);
							if ((!Utils.isNullOrEmpty(uniqueFileNameList))) {
								aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList).subscribe();
							}
						}
					}
					return Mono.just(Boolean.TRUE);
				})
				.map(Utils::responseResult);
	}

	public List<Object> convertSalaryTabDtoToDBO(EmpSalaryTabDTO dto, String userId, String userCampusIds) {
		EmpDBO empDBO = employeeProfileTransaction.getEmpDBOForSalaryTab(dto.getEmpId());
		dto.setUniqueFileNameList(new ArrayList<FileUploadDownloadDTO>());
		String authKey = "/Secured/Employee/Recruitment/EmployeeProfile/empProfileOfficeRemarksEnable";
		boolean isSalaryPrivilegeEnabled = employeeProfileHelper.isPrivilegeEnabled(authKey, userId, userCampusIds);
		if (!Utils.isNullOrEmpty(empDBO)) {
			employeeProfileHelper.setDBOSalaryTab(empDBO, dto, Integer.parseInt(userId), isSalaryPrivilegeEnabled);
		}
		List<Object> objects = new ArrayList<Object>();
		objects.add(empDBO);
		objects.add(dto.getUniqueFileNameList());
		return objects;
	}
	public Mono<EmpWorkExperienceTabDTO> getExperienceDetails(Integer empId) {
		List<EmpProfileWorkExperienceDTO> empProfileWorkExperienceDTOList = employeeProfileTransaction.getWorkExperienceDetails(empId);
		EmpWorkExperienceTabDTO empWorkExperienceTabDTO = new EmpWorkExperienceTabDTO();
		empWorkExperienceTabDTO.setEmpId(empId);
		if (!Utils.isNullOrEmpty(empProfileWorkExperienceDTOList)) {
			Set<Integer> expIdSet = empProfileWorkExperienceDTOList.stream().map(EmpProfileWorkExperienceDTO::getId).collect(Collectors.toSet());
			List<EmpProfileWorkExpDocDTO> empProfileWorkExpDocDTOList = employeeProfileTransaction.getWorkExperienceDocuments(expIdSet);
			Map<Integer, List<EmpProfileWorkExpDocDTO>> empProfileEdnDetailsDocumentsDTOMap = empProfileWorkExpDocDTOList.stream().collect(Collectors.groupingBy(EmpProfileWorkExpDocDTO::getWorkExperienceId));
			employeeProfileHelper.updateEmpExperienceDTOList(empProfileWorkExperienceDTOList, empProfileEdnDetailsDocumentsDTOMap);
			Comparator<EmpProfileWorkExperienceDTO> customComparator = Comparator.comparing(EmpProfileWorkExperienceDTO::getWorkExperienceFromDate);
			empProfileWorkExperienceDTOList.sort(customComparator);
			empWorkExperienceTabDTO.setEmpProfileWorkExperienceDTOList(empProfileWorkExperienceDTOList);
			List<EmpProfileMajorAchievementsDTO> empMajorAchievementsDTOList = employeeProfileTransaction.getMajorAchievements(empId);
			empWorkExperienceTabDTO.setEmpMajorAchievementsDTOList(empMajorAchievementsDTOList);
		}
		return Mono.justOrEmpty(empWorkExperienceTabDTO);
	}
	public Mono<ApiResult> saveOrUpdateExperience(Mono<EmpWorkExperienceTabDTO> data, String userId) {
		return data.handle((empWorkExperienceTabDTO, synchronousSink) -> {
					synchronousSink.next(empWorkExperienceTabDTO);
				}).cast(EmpWorkExperienceTabDTO.class)
				.map(data1 -> convertExperienceDtoToDBO(data1, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.get(0))) {
						EmpDBO empDBO = (EmpDBO) s.get(0);
						employeeProfileTransaction.updateEmpDBO(empDBO);
						if (!Utils.isNullOrEmpty(s.get(1))) {
							List<FileUploadDownloadDTO> uniqueFileNameList = (List<FileUploadDownloadDTO>) s.get(1);
							if ((!Utils.isNullOrEmpty(uniqueFileNameList))) {
								aWSS3FileStorageServiceHandler.moveMultipleObjects( uniqueFileNameList).subscribe();
							}
						}
					}
					return Mono.just(Boolean.TRUE);
				})
				.map(Utils::responseResult);
	}
	public List<Object> convertExperienceDtoToDBO(EmpWorkExperienceTabDTO dto, String userId) {
		EmpDBO empDBO = employeeProfileTransaction.getEmpDBOForExperienceTab(dto.getEmpId());
		dto.setUniqueFileNameList(new ArrayList<FileUploadDownloadDTO>());
		if (!Utils.isNullOrEmpty(empDBO)) {
			employeeProfileHelper.setDBOForExperienceTab(empDBO, dto, Integer.parseInt(userId));
		}
		List<Object> objects = new ArrayList<Object>();
		objects.add(empDBO);
		objects.add(dto.getUniqueFileNameList());
		return objects;
	}
	public Mono<EmpProfileCreateEmployeeDTO> getApplicantDetails(Integer applicationNo) {
		EmpProfileCreateEmployeeDTO empProfileCreateEmployeeDTO = employeeProfileTransaction.getApplicantDetForNewEmployee(applicationNo);
		return Mono.just(empProfileCreateEmployeeDTO);
	}
	public Mono<ApiResult> saveOrUpdateNewEmployee(Mono<EmpProfileCreateEmployeeDTO> dto, String userId){
		return dto
				.handle((employeeDTO, synchronousink)->{
					boolean isTrue = employeeProfileTransaction.empDuplicateCheck(employeeDTO.getEmployeeId());
					if(isTrue) {
						synchronousink.error(new DuplicateException("Employee ID already exists "));
					}
					else {
						synchronousink.next(employeeDTO);
					}
				}).cast(EmpProfileCreateEmployeeDTO.class).map(data->convertDTOtoEmpDBO(data, userId))
				.flatMap(supportCategoryDbo->{
					employeeProfileTransaction.saveOrUpdateEmp(supportCategoryDbo);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}
	public EmpDBO convertDTOtoEmpDBO(EmpProfileCreateEmployeeDTO empProfileCreateEmployeeDTO, String userId) {
		EmpDBO empDBO = new EmpDBO();
		empDBO.setCreatedUsersId(Integer.parseInt(userId));
		empDBO.setRecordStatus('A');
		empDBO.setEmpNumber(empProfileCreateEmployeeDTO.getEmployeeId());
		empDBO.setEmpName(empProfileCreateEmployeeDTO.getApplicantName());
		if(!Utils.isNullOrEmpty(empProfileCreateEmployeeDTO.getDepartment()) && !Utils.isNullOrEmpty(empProfileCreateEmployeeDTO.getDepartment().getValue())){
			ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO = new ErpCampusDepartmentMappingDBO();
			erpCampusDepartmentMappingDBO.setId(Integer.parseInt(empProfileCreateEmployeeDTO.getDepartment().getValue()));
			empDBO.setErpCampusDepartmentMappingDBO(erpCampusDepartmentMappingDBO);
		}
		if(!Utils.isNullOrEmpty(empProfileCreateEmployeeDTO.getEmployeeCategory()) && !Utils.isNullOrEmpty(empProfileCreateEmployeeDTO.getEmployeeCategory().getValue())) {
			EmpEmployeeCategoryDBO empEmployeeCategoryDBO = new EmpEmployeeCategoryDBO();
			empEmployeeCategoryDBO.setId(Integer.parseInt(empProfileCreateEmployeeDTO.getEmployeeCategory().getValue()));
			empDBO.setEmpEmployeeCategoryDBO(empEmployeeCategoryDBO);
		}
		if(!Utils.isNullOrEmpty(empProfileCreateEmployeeDTO.getJobCategory()) && !Utils.isNullOrEmpty(empProfileCreateEmployeeDTO.getJobCategory().getValue())){
			EmpEmployeeJobCategoryDBO empEmployeeJobCategoryDBO = new EmpEmployeeJobCategoryDBO();
			empEmployeeJobCategoryDBO.setId(Integer.parseInt(empProfileCreateEmployeeDTO.getJobCategory().getValue()));
			empDBO.setEmpEmployeeJobCategoryDBO(empEmployeeJobCategoryDBO);
		}
		if(!Utils.isNullOrEmpty(empProfileCreateEmployeeDTO.getSpecialisation()) && !Utils.isNullOrEmpty(empProfileCreateEmployeeDTO.getSpecialisation().getValue())){
			EmpApplnSubjectCategorySpecializationDBO empApplnSubjectCategorySpecializationDBO = new EmpApplnSubjectCategorySpecializationDBO();
			empApplnSubjectCategorySpecializationDBO.setEmpApplnSubjectCategorySpecializationId(Integer.parseInt(empProfileCreateEmployeeDTO.getSpecialisation().getValue()));
			empDBO.setEmpApplnSubjectCategorySpecializationDBO(empApplnSubjectCategorySpecializationDBO);
		}
		return empDBO;
	}
	public Mono<List<EmpProfileLeaveAllotmentDTO>> getLeaveAllotmentDetails(Integer leaveCategoryId, LocalDate doj) {
		List<EmpProfileLeaveAllotmentDTO> empProfileLeaveAllotmentDTOList = new ArrayList<>();
		empProfileLeaveAllotmentDTOList = employeeProfileTransaction.getLeaveAllotmentDetails(leaveCategoryId);
		employeeProfileHelper.calculateNoOfLeaveByMonth(empProfileLeaveAllotmentDTOList, doj);
		return Mono.just(empProfileLeaveAllotmentDTOList);
	}
	public Mono<List<EmpProfileLeaveAllotmentDTO>> getLeaveSummary(Integer empId, Integer doj) {
		List<EmpProfileLeaveAllotmentDTO> empProfileLeaveAllotmentDTOList = new ArrayList<>();
		empProfileLeaveAllotmentDTOList = employeeProfileTransaction.getAssignedLeaves(empId, doj);
		return Mono.just(empProfileLeaveAllotmentDTOList);
	}
}