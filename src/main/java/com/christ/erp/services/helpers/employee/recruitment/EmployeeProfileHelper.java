package com.christ.erp.services.helpers.employee.recruitment;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.christ.erp.services.dbobjects.common.*;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.common.*;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterTypeDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.*;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleMatrixDetailDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDBO;
import com.christ.erp.services.dto.common.CommonDTO;
import com.christ.erp.services.dto.employee.*;
import com.christ.erp.services.dto.employee.profile.*;
import com.christ.erp.services.transactions.employee.recruitment.EmployeeProfileTransaction;
import com.christ.utility.lib.Constants;
import com.christ.utility.lib.caching.CacheUtils;
import jdk.jshell.execution.Util;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.asn1.x509.sigi.PersonalData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.Utils;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.handlers.employee.recruitment.EmployeeApplicationHandler;


import javax.persistence.Column;

@SuppressWarnings({ "rawtypes"})
@Service
public class EmployeeProfileHelper {
	@Autowired
	AWSS3FileStorageService aWSS3FileStorageService;
	@Autowired
	EmployeeApplicationHandler employeeApplicationHandler;

	@Autowired
	EmployeeProfileTransaction employeeProfileTransaction;

	public EmpProfileGridDTO convertEmpDTO(EmpProfileGridDTO empProfileGridDTO) {
		if (!Utils.isNullOrEmpty(empProfileGridDTO.getProfilePhotoUrl()) && !Utils.isNullOrEmpty(empProfileGridDTO.getFileNameOriginal())) {
			FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
			fileUploadDownloadDTO.setActualPath(empProfileGridDTO.getProfilePhotoUrl());
			fileUploadDownloadDTO.setProcessCode(empProfileGridDTO.getProcessCode());
			fileUploadDownloadDTO.setOriginalFileName(empProfileGridDTO.getFileNameOriginal());
			aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
			empProfileGridDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
		}
		return empProfileGridDTO;
	}

	public void setPersonalDataOtherDetails(EmpPersonalDataTabDTO empPersonalDataTabDTO, String userId, String userCampusIds) {
		String authKey = "/Secured/Employee/Recruitment/EmployeeProfile/empProfileAadharAndPanEnable";
		boolean isAadarEnabled = isPrivilegeEnabled(authKey, userId, userCampusIds);
		if (!isAadarEnabled) {
			if (!Utils.isNullOrEmpty(empPersonalDataTabDTO.getAadharNo())) {
				if (empPersonalDataTabDTO.getAadharNo().length() >= 4) {
					empPersonalDataTabDTO.setAadharNo("xxxx-xxxx-" + empPersonalDataTabDTO.getAadharNo().substring(empPersonalDataTabDTO.getAadharNo().length() - 4));
				} else {
					empPersonalDataTabDTO.setAadharNo("xxxx-xxxx");
				}
			}
			String pan = "";
			if (!Utils.isNullOrEmpty(empPersonalDataTabDTO.getPanNo())) {
				pan = empPersonalDataTabDTO.getPanNo().replaceAll(".", "x");
			}
			empPersonalDataTabDTO.setPanNo(pan);
		}
		if (!Utils.isNullOrEmpty(empPersonalDataTabDTO) && !Utils.isNullOrEmpty(empPersonalDataTabDTO.getProfilePhotoUrl()) && !Utils.isNullOrEmpty(empPersonalDataTabDTO.getFileNameOriginal())) {
			FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
			fileUploadDownloadDTO.setActualPath(empPersonalDataTabDTO.getProfilePhotoUrl());
			fileUploadDownloadDTO.setProcessCode(empPersonalDataTabDTO.getUploadProcessCode());
			fileUploadDownloadDTO.setOriginalFileName(empPersonalDataTabDTO.getFileNameOriginal());
			aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
			empPersonalDataTabDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
		}
	}

	public boolean isPrivilegeEnabled(String authKey, String userId, String userCampusIds) {
		boolean result = false;
		userId = "986";
		String authMap = Constants.AUTH_MAP_PREFIX.concat(userId);
		String authCampusIds = null;
		if (!Utils.isNullOrEmpty(authMap)) {
			authCampusIds = CacheUtils.instance.get(authMap, authKey);
			if (!Utils.isNullOrEmpty(userCampusIds) && !Utils.isNullOrEmpty(authCampusIds)) {
				if (!Collections.disjoint(Arrays.asList(userCampusIds.split(",")), Arrays.asList(authCampusIds.split(",")))) {
					result = true;
				}
			}
		}
		return result;
	}

	public void setEmployeeDBO(EmpDBO empDBO, EmpPersonalDataTabDTO personalDataTabDto, Integer userId) {
		empDBO.setEmpName(personalDataTabDto.getEmpName());
		empDBO.setEmpNumber(personalDataTabDto.getEmployeeNo());
		if (!Utils.isNullOrEmpty(personalDataTabDto.getGender()) && !Utils.isNullOrEmpty(personalDataTabDto.getGender().getValue())) {
			ErpGenderDBO erpGenderDBO = new ErpGenderDBO();
			erpGenderDBO.setErpGenderId(Integer.parseInt(personalDataTabDto.getGender().getValue()));
			empDBO.setErpGenderDBO(erpGenderDBO);
		}
		if (!Utils.isNullOrEmpty(personalDataTabDto.getDob())) {
			empDBO.setEmpDOB(personalDataTabDto.getDob());
		}
		if (!Utils.isNullOrEmpty(personalDataTabDto.getDoj())) {
			empDBO.setEmpDOJ(personalDataTabDto.getDoj());
		}
		empDBO.setEmpMobile(personalDataTabDto.getMobileNo());
		empDBO.setCountryCode(personalDataTabDto.getMobCountryCode());
		empDBO.setEmpPersonalEmail(personalDataTabDto.getPersonalEmailId());
		if (!Utils.isNullOrEmpty(userId)) {
			empDBO.setModifiedUsersId(userId);
		}
		setSignatureUrl(empDBO, personalDataTabDto, userId);
	}

	public void setSignatureUrl(EmpDBO empDBO, EmpPersonalDataTabDTO personalDataTabDto, Integer userId) {
		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		UrlAccessLinkDBO signatureUrlDBO = null;
		if (Utils.isNullOrEmpty(empDBO.getEmpSignatureUrlDBO())) {
			if (!Utils.isNullOrEmpty(personalDataTabDto.getSignatureUploadDTO()) && !Utils.isNullOrEmpty(personalDataTabDto.getSignatureUploadDTO().getNewFile()) && personalDataTabDto.getSignatureUploadDTO().getNewFile() &&
					!Utils.isNullOrEmpty(personalDataTabDto.getSignatureUploadDTO().getOriginalFileName())) {
				signatureUrlDBO = new UrlAccessLinkDBO();
				signatureUrlDBO.setCreatedUsersId(userId);
				signatureUrlDBO.setRecordStatus('A');
			}
		} else {
			signatureUrlDBO = empDBO.getEmpSignatureUrlDBO();
			signatureUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(personalDataTabDto.getSignatureUploadDTO()) && !Utils.isNullOrEmpty(personalDataTabDto.getSignatureUploadDTO().getNewFile()) && personalDataTabDto.getSignatureUploadDTO().getNewFile()) {
			signatureUrlDBO = employeeApplicationHandler.createURLAccessLinkDBO(signatureUrlDBO, personalDataTabDto.getSignatureUploadDTO().getProcessCode(), personalDataTabDto.getSignatureUploadDTO().getUniqueFileName(),
					personalDataTabDto.getSignatureUploadDTO().getOriginalFileName(), userId, null);
		}
		if (!Utils.isNullOrEmpty(personalDataTabDto.getSignatureUploadDTO()) && !Utils.isNullOrEmpty(personalDataTabDto.getSignatureUploadDTO().getProcessCode()) &&
				!Utils.isNullOrEmpty(personalDataTabDto.getSignatureUploadDTO().getUniqueFileName()) && personalDataTabDto.getSignatureUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(personalDataTabDto.getSignatureUploadDTO().getProcessCode(),
					personalDataTabDto.getSignatureUploadDTO().getUniqueFileName()));
		}
		empDBO.setEmpSignatureUrlDBO(signatureUrlDBO);
		if (!Utils.isNullOrEmpty(uniqueFileNameList)) {
			personalDataTabDto.getUniqueFileNameList().addAll(uniqueFileNameList);
		}

	}

	public EmpPersonalDataDBO setPersonalDataDBO(EmpPersonalDataDBO empPersonalDataDBO, EmpPersonalDataTabDTO personalDataDto, Integer userId) {
		ErpCountryDBO erpCountryDBO = null;
		if (!Utils.isNullOrEmpty(personalDataDto.getNationality()) && !Utils.isNullOrEmpty(personalDataDto.getNationality().getValue())) {
			erpCountryDBO = new ErpCountryDBO();
			erpCountryDBO.setId(Integer.parseInt(personalDataDto.getNationality().getValue()));
		}
		empPersonalDataDBO.setErpCountryDBO(erpCountryDBO);

		ErpMaritalStatusDBO erpMaritalStatusDBO = null;
		if (!Utils.isNullOrEmpty(personalDataDto.getMaritalStatus()) && !Utils.isNullOrEmpty(personalDataDto.getMaritalStatus().getValue())) {
			erpMaritalStatusDBO = new ErpMaritalStatusDBO();
			erpMaritalStatusDBO.setId(Integer.parseInt(personalDataDto.getMaritalStatus().getValue()));
		}
		empPersonalDataDBO.setErpMaritalStatusDBO(erpMaritalStatusDBO);

		ErpReligionDBO erpReligionDBO = null;
		if (!Utils.isNullOrEmpty(personalDataDto.getReligion()) && !Utils.isNullOrEmpty(personalDataDto.getReligion().getValue())) {
			erpReligionDBO = new ErpReligionDBO();
			erpReligionDBO.setId(Integer.parseInt(personalDataDto.getReligion().getValue()));
		}
		empPersonalDataDBO.setErpReligionDBO(erpReligionDBO);
		ErpBloodGroupDBO bloodGroupDBO = null;
		if (!Utils.isNullOrEmpty(personalDataDto.getBloodGroup()) && !Utils.isNullOrEmpty(personalDataDto.getBloodGroup().getValue())) {
			bloodGroupDBO = new ErpBloodGroupDBO();
			bloodGroupDBO.setId(Integer.parseInt(personalDataDto.getBloodGroup().getValue()));
		}
		empPersonalDataDBO.setErpBloodGroupDBO(bloodGroupDBO);
		ErpReservationCategoryDBO erpReservationCategoryDBO = null;
		if (!Utils.isNullOrEmpty(personalDataDto.getReservationCategory()) && !Utils.isNullOrEmpty(personalDataDto.getReservationCategory().getValue())) {
			erpReservationCategoryDBO = new ErpReservationCategoryDBO();
			erpReservationCategoryDBO.setId(Integer.parseInt(personalDataDto.getReservationCategory().getValue()));
		}
		empPersonalDataDBO.setErpReservationCategoryDBO(erpReservationCategoryDBO);
		ErpDifferentlyAbledDBO differentlyAbledDBO = null;
		if (!Utils.isNullOrEmpty(personalDataDto.getErpDifferentlyAbled()) && !Utils.isNullOrEmpty(personalDataDto.getErpDifferentlyAbled().getValue())) {
			differentlyAbledDBO = new ErpDifferentlyAbledDBO();
			differentlyAbledDBO.setId(Integer.parseInt(personalDataDto.getErpDifferentlyAbled().getValue()));
		}
		empPersonalDataDBO.setErpDifferentlyAbledDBO(differentlyAbledDBO);
		empPersonalDataDBO.setIsDifferentlyAbled(personalDataDto.getIsDifferentlyAbled());
		empPersonalDataDBO.setDifferentlyAbledDetails(personalDataDto.getDifferentlyAbledDetails());
		empPersonalDataDBO.setAlternateNo(personalDataDto.getAlternateContactNo());
		//current address setting
		empPersonalDataDBO.setCurrentAddressLine1(personalDataDto.getCurrentAddressLine1());
		empPersonalDataDBO.setCurrentAddressLine2(personalDataDto.getCurrentAddressLine2());
		empPersonalDataDBO.setCurrentCity(createCityDBO(personalDataDto.getCurrentAddressCity()));
		empPersonalDataDBO.setCurrentCityOthers(personalDataDto.getCurrentAddressCityOthers());
		empPersonalDataDBO.setCurrentCountry(createCountryDBO(personalDataDto.getCurrentAddressCountry()));
		empPersonalDataDBO.setCurrentState(createStateDBO(personalDataDto.getCurrentAddressState()));
		empPersonalDataDBO.setCurrentStateOthers(personalDataDto.getCurrentAddressStateOthers());
		empPersonalDataDBO.setCurrentPincode(personalDataDto.getCurrentAddressPinCode());
		empPersonalDataDBO.setIsPermanentEqualsCurrent(personalDataDto.getIsPermanentEqualsCurrent());
		//permanent address setting
		empPersonalDataDBO.setPermanentAddressLine1(personalDataDto.getPermanentAddressLine1());
		empPersonalDataDBO.setPermanentAddressLine2(personalDataDto.getPermanentAddressLine2());
		empPersonalDataDBO.setPermanentCity(createCityDBO(personalDataDto.getPermanentAddressCity()));
		empPersonalDataDBO.setPermanentCityOthers(personalDataDto.getPermanentAddressCityOthers());
		empPersonalDataDBO.setPermanentCountry(createCountryDBO(personalDataDto.getPermanentAddressCountry()));
		empPersonalDataDBO.setPermanentState(createStateDBO(personalDataDto.getPermanentAddressState()));
		empPersonalDataDBO.setPermanentStateOthers(personalDataDto.getPermanentAddressStateOthers());
		empPersonalDataDBO.setPermanentPincode(personalDataDto.getPermanentAddressPinCode());
		empPersonalDataDBO.setModifiedUsersId(userId);
		return empPersonalDataDBO;
	}

	public void setAdditionalPersonalDataDBO(EmpPersonalDataDBO empPersonalDataDBO, EmpPersonalDataTabDTO personalDataDto, Integer userId) {
		EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO = null;
//		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		if (!Utils.isNullOrEmpty(empPersonalDataDBO.getEmpAddtnlPersonalDataDBO())) {
			empAddtnlPersonalDataDBO = empPersonalDataDBO.getEmpAddtnlPersonalDataDBO();
			empAddtnlPersonalDataDBO.setModifiedUsersId(userId);
		} else {
			empAddtnlPersonalDataDBO = new EmpAddtnlPersonalDataDBO();
			empAddtnlPersonalDataDBO.setCreatedUsersId(userId);
		}
		empAddtnlPersonalDataDBO.setPanNo(personalDataDto.getPanNo());
		empAddtnlPersonalDataDBO.setFourWheelerNo(personalDataDto.getFourWheelerNo());
		empAddtnlPersonalDataDBO.setTwoWheelerNo(personalDataDto.getTwoWheelerNo());
		if (!Utils.isNullOrEmpty(personalDataDto.getIsAadharAvailable()) && personalDataDto.getIsAadharAvailable()) {
			empAddtnlPersonalDataDBO.setIsAadharAvailable(personalDataDto.getIsAadharAvailable());
		} else {
			empAddtnlPersonalDataDBO.setIsAadharAvailable(false);
		}

		if (!Utils.isNullOrEmpty(personalDataDto.getIsAadharEnrolled()) && personalDataDto.getIsAadharEnrolled()) {
			empAddtnlPersonalDataDBO.setIsAadharEnrolled(personalDataDto.getIsAadharEnrolled());
		} else {
			empAddtnlPersonalDataDBO.setIsAadharEnrolled(false);
		}
		empAddtnlPersonalDataDBO.setAadharNo(personalDataDto.getAadharNo());
		empAddtnlPersonalDataDBO.setAadharEnrolledNo(personalDataDto.getAadharEnrolledNo());
		empAddtnlPersonalDataDBO.setEmergencyContactName(personalDataDto.getEmergencyContactName());
		empAddtnlPersonalDataDBO.setEmergencyContactAddress(personalDataDto.getEmergencyContactAddress());
		empAddtnlPersonalDataDBO.setEmergencyContactHome(personalDataDto.getEmergencyContactHome());
		empAddtnlPersonalDataDBO.setEmergencyContactRelationship(personalDataDto.getEmergencyContactRelatonship());
		empAddtnlPersonalDataDBO.setEmergencyContactWork(personalDataDto.getEmergencyContactWork());
		empAddtnlPersonalDataDBO.setEmergencyMobileNo(personalDataDto.getEmergencyMobileNo());
		//passport details
		empAddtnlPersonalDataDBO.setPassportNo(personalDataDto.getPassportNo());
		if (!Utils.isNullOrEmpty(personalDataDto.getPassportIssuedDate())) {
			empAddtnlPersonalDataDBO.setPassportIssuedDate(personalDataDto.getPassportIssuedDate());
		} else {
			empAddtnlPersonalDataDBO.setPassportIssuedDate(null);
		}
		empAddtnlPersonalDataDBO.setPassportIssuedPlace(personalDataDto.getPassportIssuedPlace());
		empAddtnlPersonalDataDBO.setPassportStatus(personalDataDto.getPassportStatus());
		if (!Utils.isNullOrEmpty(personalDataDto.getPassportDateOfExpiry())) {
			empAddtnlPersonalDataDBO.setPassportDateOfExpiry(personalDataDto.getPassportDateOfExpiry());
		} else {
			empAddtnlPersonalDataDBO.setPassportDateOfExpiry(null);
		}
		empAddtnlPersonalDataDBO.setPassportComments(personalDataDto.getPassportComments());
		//visa details
		empAddtnlPersonalDataDBO.setVisaNo(personalDataDto.getVisaNo());
		if (!Utils.isNullOrEmpty(personalDataDto.getVisaIssuedDate())) {
			empAddtnlPersonalDataDBO.setVisaIssuedDate(personalDataDto.getVisaIssuedDate());
		} else {
			empAddtnlPersonalDataDBO.setVisaIssuedDate(null);
		}
		empAddtnlPersonalDataDBO.setVisaStatus(personalDataDto.getVisaStatus());
		if (!Utils.isNullOrEmpty(personalDataDto.getVisaDateOfExpiry())) {
			empAddtnlPersonalDataDBO.setVisaDateOfExpiry(personalDataDto.getVisaDateOfExpiry());
		} else {
			empAddtnlPersonalDataDBO.setVisaDateOfExpiry(null);
		}
		empAddtnlPersonalDataDBO.setVisaComments(personalDataDto.getVisaComments());
		//frr details
		empAddtnlPersonalDataDBO.setFrroNo(personalDataDto.getFrroNo());
		if (!Utils.isNullOrEmpty(personalDataDto.getFrroIssuedDate())) {
			empAddtnlPersonalDataDBO.setFrroIssuedDate(personalDataDto.getFrroIssuedDate());
		} else {
			empAddtnlPersonalDataDBO.setFrroIssuedDate(null);
		}

		empAddtnlPersonalDataDBO.setFrroStatus(personalDataDto.getFrroStatus());
		if (!Utils.isNullOrEmpty(personalDataDto.getFrroDateOfExpiry())) {
			empAddtnlPersonalDataDBO.setFrroDateOfExpiry(personalDataDto.getFrroDateOfExpiry());
		} else {
			empAddtnlPersonalDataDBO.setFrroDateOfExpiry(null);
		}
		empAddtnlPersonalDataDBO.setFrroComments(personalDataDto.getFrroComments());
		empAddtnlPersonalDataDBO.setFamilyBackgroundBrief(personalDataDto.getFamilyBackgroundBrief());
		empAddtnlPersonalDataDBO.setRecordStatus('A');
		empAddtnlPersonalDataDBO.setEmpPersonalDataDBO(empPersonalDataDBO);
		empPersonalDataDBO.setEmpAddtnlPersonalDataDBO(empAddtnlPersonalDataDBO);
		personalDataUploadDboSetting(personalDataDto, empAddtnlPersonalDataDBO, userId); //upload settings
	}

	public void setDependentDBO(List<EmpProfileFamilyDependentDTO> empFamilyDependentDTOList, EmpPersonalDataDBO empPersonalDataDBO, Integer userId) {
		Map<Integer, EmpFamilyDetailsAddtnlDBO> savedEmpFamilyDetailsAddtnlDBOSMap = !Utils.isNullOrEmpty(empPersonalDataDBO.getEmpFamilyDetailsAddtnlDBOS())
				? empPersonalDataDBO.getEmpFamilyDetailsAddtnlDBOS().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(dep -> dep.getEmpFamilyDetailsAddtnlId(), dep -> dep)) : new HashMap<Integer, EmpFamilyDetailsAddtnlDBO>();
		Set<EmpFamilyDetailsAddtnlDBO> empFamilyDetailsAddtnlDBOSet = new HashSet<EmpFamilyDetailsAddtnlDBO>();
		Set<Integer> empFamilyDetailsAddtnlDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empFamilyDependentDTOList)) {
			empFamilyDependentDTOList.forEach(empFamilyDependentDTO -> {
				EmpFamilyDetailsAddtnlDBO empFamilyDetailsAddtnlDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpFamilyDetailsAddtnlDBOSMap) && savedEmpFamilyDetailsAddtnlDBOSMap.containsKey(empFamilyDependentDTO.getEmpFamilyDetailsAddtnlId())) {
					empFamilyDetailsAddtnlDBO = savedEmpFamilyDetailsAddtnlDBOSMap.get(empFamilyDependentDTO.getEmpFamilyDetailsAddtnlId());
					empFamilyDetailsAddtnlDBO.setModifiedUsersId(userId);
					empFamilyDetailsAddtnlDBOIdsSet.add(empFamilyDetailsAddtnlDBO.getEmpFamilyDetailsAddtnlId());
				} else {
					empFamilyDetailsAddtnlDBO = new EmpFamilyDetailsAddtnlDBO();
					empFamilyDetailsAddtnlDBO.setCreatedUsersId(userId);
				}
				if (!Utils.isNullOrEmpty(empFamilyDependentDTO.getDependentDob())) {
					empFamilyDetailsAddtnlDBO.setDependentDob(empFamilyDependentDTO.getDependentDob());
				} else {
					empFamilyDetailsAddtnlDBO.setDependentDob(null);
				}
				empFamilyDetailsAddtnlDBO.setDependentName(empFamilyDependentDTO.getDependentName());
				empFamilyDetailsAddtnlDBO.setDependentProfession(empFamilyDependentDTO.getDependentProfession());
				empFamilyDetailsAddtnlDBO.setDependentQualification(empFamilyDependentDTO.getDependentQualification());
				empFamilyDetailsAddtnlDBO.setEmpPersonalDataDBO(empPersonalDataDBO);
				empFamilyDetailsAddtnlDBO.setOtherDependentRelationship(empFamilyDependentDTO.getOtherDependentRelationship());
				empFamilyDetailsAddtnlDBO.setRecordStatus('A');
				empFamilyDetailsAddtnlDBO.setRelationship(empFamilyDependentDTO.getRelationship());
				empFamilyDetailsAddtnlDBOSet.add(empFamilyDetailsAddtnlDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empPersonalDataDBO.getEmpFamilyDetailsAddtnlDBOS())) {
			empPersonalDataDBO.getEmpFamilyDetailsAddtnlDBOS().forEach(empFamilyDetailsAddtnlDBODel -> {
				if (!empFamilyDetailsAddtnlDBOIdsSet.contains(empFamilyDetailsAddtnlDBODel.getEmpFamilyDetailsAddtnlId())) {
					empFamilyDetailsAddtnlDBODel.setRecordStatus('D');
					empFamilyDetailsAddtnlDBODel.setModifiedUsersId(userId);
					empFamilyDetailsAddtnlDBOSet.add(empFamilyDetailsAddtnlDBODel);
				}
			});

		}
		if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBOSet) && empFamilyDetailsAddtnlDBOSet.size() > 0) {
			empPersonalDataDBO.setEmpFamilyDetailsAddtnlDBOS(empFamilyDetailsAddtnlDBOSet);
		} else {
			empPersonalDataDBO.setEmpFamilyDetailsAddtnlDBOS(null);
		}
	}

	public void setPfAndGratuityDBO(List<EmpProfilePFandGratuityDTO> empProfilePFandGratuityDTOList, EmpDBO empDBO, Integer userId) {
		EmpJobDetailsDBO empJobDetailsDBO = null;
		Map<Integer, EmpPfGratuityNomineesDBO> savedEmpPfGratuityNomineesDBOMap = null;
		Set<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOSet = new HashSet<EmpPfGratuityNomineesDBO>();
		Set<Integer> empPfGratuityNomineesIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO())) {
			empJobDetailsDBO = empDBO.getEmpJobDetailsDBO();
			if (!Utils.isNullOrEmpty(empJobDetailsDBO) && !Utils.isNullOrEmpty(empJobDetailsDBO.getEmpPfGratuityNomineesDBOS())) {
				savedEmpPfGratuityNomineesDBOMap = !Utils.isNullOrEmpty(empJobDetailsDBO.getEmpPfGratuityNomineesDBOS())
						? empJobDetailsDBO.getEmpPfGratuityNomineesDBOS().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(pf -> pf.getEmpPfGratuityNomineesId(), pf -> pf)) : new HashMap<Integer, EmpPfGratuityNomineesDBO>();
			}
		}
		if (!Utils.isNullOrEmpty(empProfilePFandGratuityDTOList)) {
			if (Utils.isNullOrEmpty(empJobDetailsDBO)) {
				empJobDetailsDBO = new EmpJobDetailsDBO();
				empJobDetailsDBO.setEmpDBO(empDBO);
				empJobDetailsDBO.setRecordStatus('A');
				empJobDetailsDBO.setCreatedUsersId(userId);
			}
			Map<Integer, EmpPfGratuityNomineesDBO> savedEmpPfGratuityNomineesDBOMapNew = savedEmpPfGratuityNomineesDBOMap; //created new due to final error
			EmpJobDetailsDBO empJobDetailsDBONew = empJobDetailsDBO; //created new due to final error
			empProfilePFandGratuityDTOList.forEach(empProfilePFandGratuityDTO -> {
				EmpPfGratuityNomineesDBO empPfGratuityNomineesDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpPfGratuityNomineesDBOMapNew) && savedEmpPfGratuityNomineesDBOMapNew.containsKey(empProfilePFandGratuityDTO.getEmpPfGratuityNomineesId())) {
					empPfGratuityNomineesDBO = savedEmpPfGratuityNomineesDBOMapNew.get(empProfilePFandGratuityDTO.getEmpPfGratuityNomineesId());
					empPfGratuityNomineesDBO.setModifiedUsersId(userId);
					empPfGratuityNomineesIdsSet.add(empPfGratuityNomineesDBO.getEmpPfGratuityNomineesId());
				} else {
					empPfGratuityNomineesDBO = new EmpPfGratuityNomineesDBO();
					empPfGratuityNomineesDBO.setCreatedUsersId(userId);
				}
				empPfGratuityNomineesDBO.setEmpJobDetailsDBO(empJobDetailsDBONew);
				empPfGratuityNomineesDBO.setIsGratuity(empProfilePFandGratuityDTO.getIsGratuity());
				empPfGratuityNomineesDBO.setIsPf(empProfilePFandGratuityDTO.getIsPf());
				empPfGratuityNomineesDBO.setNominee(empProfilePFandGratuityDTO.getNomineeName());
				empPfGratuityNomineesDBO.setNomineeAddress(empProfilePFandGratuityDTO.getNomineeAddress());
				empPfGratuityNomineesDBO.setNomineeDob(empProfilePFandGratuityDTO.getNomineeDob());
				empPfGratuityNomineesDBO.setNomineeRelationship(empProfilePFandGratuityDTO.getNomineeRelationship());
				empPfGratuityNomineesDBO.setRecordStatus('A');
				if (!Utils.isNullOrEmpty(empProfilePFandGratuityDTO.getSharePercentage())) {
					empPfGratuityNomineesDBO.setSharePercentage(new BigDecimal(empProfilePFandGratuityDTO.getSharePercentage()));
				} else {
					empPfGratuityNomineesDBO.setSharePercentage(null);
				}
				empPfGratuityNomineesDBO.setUnder18GuardianAddress(empProfilePFandGratuityDTO.getUnder18GuardianAddress());
				empPfGratuityNomineesDBO.setUnder18GuardName(empProfilePFandGratuityDTO.getUnder18GuardName());
				empPfGratuityNomineesDBOSet.add(empPfGratuityNomineesDBO);
			});
		}
		if (!Utils.isNullOrEmpty(empJobDetailsDBO) && !Utils.isNullOrEmpty(empJobDetailsDBO.getEmpPfGratuityNomineesDBOS())) {
			empJobDetailsDBO.getEmpPfGratuityNomineesDBOS().forEach(empProfilePFandGratuityDTODel -> {
				if (!empPfGratuityNomineesIdsSet.contains(empProfilePFandGratuityDTODel.getEmpPfGratuityNomineesId())) {
					empProfilePFandGratuityDTODel.setRecordStatus('D');
					empProfilePFandGratuityDTODel.setModifiedUsersId(userId);
					empPfGratuityNomineesDBOSet.add(empProfilePFandGratuityDTODel);
				}
			});
		}
		if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBOSet) && empPfGratuityNomineesDBOSet.size() > 0) {
			if (Utils.isNullOrEmpty(empJobDetailsDBO)) {
				empJobDetailsDBO = new EmpJobDetailsDBO();
				empJobDetailsDBO.setRecordStatus('A');
				empJobDetailsDBO.setCreatedUsersId(userId);
			}
			empJobDetailsDBO.setEmpPfGratuityNomineesDBOS(empPfGratuityNomineesDBOSet);
		} else {
			if (!Utils.isNullOrEmpty(empJobDetailsDBO)) {
				empJobDetailsDBO.setEmpPfGratuityNomineesDBOS(null);
			}
		}
		empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);

	}

	public ErpCityDBO createCityDBO(SelectDTO cityDTO) {
		ErpCityDBO cityDBO = null;
		if (!Utils.isNullOrEmpty(cityDTO) && !Utils.isNullOrEmpty(cityDTO.getValue())) {
			cityDBO = new ErpCityDBO();
			cityDBO.setId(Integer.parseInt(cityDTO.getValue()));
		}
		return cityDBO;

	}

	public ErpCountryDBO createCountryDBO(CommonDTO countryDTO) {
		ErpCountryDBO erpCountryDBO = null;
		if (!Utils.isNullOrEmpty(countryDTO) && !Utils.isNullOrEmpty(countryDTO.getValue())) {
			erpCountryDBO = new ErpCountryDBO();
			erpCountryDBO.setId(Integer.parseInt(countryDTO.getValue()));
		}
		return erpCountryDBO;

	}

	public ErpStateDBO createStateDBO(SelectDTO stateDTO) {
		ErpStateDBO stateDBO = null;
		if (!Utils.isNullOrEmpty(stateDTO) && !Utils.isNullOrEmpty(stateDTO.getValue())) {
			stateDBO = new ErpStateDBO();
			stateDBO.setId(Integer.parseInt(stateDTO.getValue()));
		}
		return stateDBO;

	}

	public void personalDataUploadDboSetting(EmpPersonalDataTabDTO personalDataDto, EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO, Integer userId) {
		//passport url setting
		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		passportUploadSetting(personalDataDto, empAddtnlPersonalDataDBO, userId, uniqueFileNameList);
		visaUploadSetting(personalDataDto, empAddtnlPersonalDataDBO, userId, uniqueFileNameList);
		frrUploadSetting(personalDataDto, empAddtnlPersonalDataDBO, userId, uniqueFileNameList);
		twoWheelerUploadSetting(personalDataDto, empAddtnlPersonalDataDBO, userId, uniqueFileNameList);
		fourWheelerUploadSetting(personalDataDto, empAddtnlPersonalDataDBO, userId, uniqueFileNameList);
		aadharUploadSetting(personalDataDto, empAddtnlPersonalDataDBO, userId, uniqueFileNameList);
		panUploadSetting(personalDataDto, empAddtnlPersonalDataDBO, userId, uniqueFileNameList);
		if (!Utils.isNullOrEmpty(uniqueFileNameList)) {
			personalDataDto.getUniqueFileNameList().addAll(uniqueFileNameList);
		}
	}

	public void passportUploadSetting(EmpPersonalDataTabDTO personalDataDto, EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO passportUrlDBO = null;
		if (Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getPassportUploadUrlDBO())) {
			if (!Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO().getNewFile())
					&& personalDataDto.getPassportUploadDTO().getNewFile() && !Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO().getOriginalFileName())) {
				passportUrlDBO = new UrlAccessLinkDBO();
				passportUrlDBO.setCreatedUsersId(userId);
				passportUrlDBO.setRecordStatus('A');
			}
		} else {
			passportUrlDBO = empAddtnlPersonalDataDBO.getPassportUploadUrlDBO();
			if (Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO())) {
				passportUrlDBO.setRecordStatus('D');
			}
			passportUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO().getNewFile()) && personalDataDto.getPassportUploadDTO().getNewFile()) {
			passportUrlDBO = employeeApplicationHandler.createURLAccessLinkDBO(passportUrlDBO, personalDataDto.getPassportUploadDTO().getProcessCode(), personalDataDto.getPassportUploadDTO().getUniqueFileName(), personalDataDto.getPassportUploadDTO().getOriginalFileName(), userId, null);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO().getProcessCode()) &&
				!Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO().getUniqueFileName()) && !Utils.isNullOrEmpty(personalDataDto.getPassportUploadDTO().getNewFile())
				&& personalDataDto.getPassportUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(personalDataDto.getPassportUploadDTO().getProcessCode(), personalDataDto.getPassportUploadDTO().getUniqueFileName()));
		}
		empAddtnlPersonalDataDBO.setPassportUploadUrlDBO(passportUrlDBO);
	}

	public void visaUploadSetting(EmpPersonalDataTabDTO personalDataDto, EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO visaUrlDBO = null;
		if (Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getVisaUploadUrlDBO())) {
			if (!Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO().getNewFile())
					&& personalDataDto.getVisaUploadDTO().getNewFile() && !Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO().getOriginalFileName())) {
				visaUrlDBO = new UrlAccessLinkDBO();
				visaUrlDBO.setCreatedUsersId(userId);
				visaUrlDBO.setRecordStatus('A');
			}
		} else {
			visaUrlDBO = empAddtnlPersonalDataDBO.getVisaUploadUrlDBO();
			if (Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO())) {
				visaUrlDBO.setRecordStatus('D');
			}
			visaUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO().getNewFile()) && personalDataDto.getVisaUploadDTO().getNewFile()) {
			visaUrlDBO = employeeApplicationHandler.createURLAccessLinkDBO(visaUrlDBO, personalDataDto.getVisaUploadDTO().getProcessCode(), personalDataDto.getVisaUploadDTO().getUniqueFileName(), personalDataDto.getVisaUploadDTO().getOriginalFileName(), userId, null);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO().getProcessCode())
				&& !Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO().getUniqueFileName()) && !Utils.isNullOrEmpty(personalDataDto.getVisaUploadDTO().getNewFile()) && personalDataDto.getVisaUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(personalDataDto.getVisaUploadDTO().getProcessCode(), personalDataDto.getVisaUploadDTO().getUniqueFileName()));
		}
		empAddtnlPersonalDataDBO.setVisaUploadUrlDBO(visaUrlDBO);
	}

	public void frrUploadSetting(EmpPersonalDataTabDTO personalDataDto, EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO frroUrlDBO = null;
		if (Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getFrroUploadUrlDBO())) {
			if (!Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO().getNewFile()) && personalDataDto.getFrroUploadDTO().getNewFile() &&
					!Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO().getOriginalFileName())) {
				frroUrlDBO = new UrlAccessLinkDBO();
				frroUrlDBO.setCreatedUsersId(userId);
				frroUrlDBO.setRecordStatus('A');
			}
		} else {
			frroUrlDBO = empAddtnlPersonalDataDBO.getFrroUploadUrlDBO();
			if (Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO())) {
				frroUrlDBO.setRecordStatus('D');
			}
			frroUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO().getNewFile()) && personalDataDto.getFrroUploadDTO().getNewFile()) {
			frroUrlDBO = employeeApplicationHandler.createURLAccessLinkDBO(frroUrlDBO, personalDataDto.getFrroUploadDTO().getProcessCode(), personalDataDto.getFrroUploadDTO().getUniqueFileName(), personalDataDto.getFrroUploadDTO().getOriginalFileName(), userId, null);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO().getProcessCode()) &&
				!Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO().getUniqueFileName()) && !Utils.isNullOrEmpty(personalDataDto.getFrroUploadDTO().getNewFile()) && personalDataDto.getFrroUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(personalDataDto.getFrroUploadDTO().getProcessCode(), personalDataDto.getFrroUploadDTO().getUniqueFileName()));
		}
		empAddtnlPersonalDataDBO.setFrroUploadUrlDBO(frroUrlDBO);
	}

	public void twoWheelerUploadSetting(EmpPersonalDataTabDTO personalDataDto, EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO twoWheelerUrlDBO = null;
		if (Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getTwoWheelerDocumentUrlDBO())) {
			if (!Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO().getNewFile()) && personalDataDto.getTwoWheelerUploadDTO().getNewFile() &&
					!Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO().getOriginalFileName())) {
				twoWheelerUrlDBO = new UrlAccessLinkDBO();
				twoWheelerUrlDBO.setCreatedUsersId(userId);
				twoWheelerUrlDBO.setRecordStatus('A');
			}
		} else {
			twoWheelerUrlDBO = empAddtnlPersonalDataDBO.getTwoWheelerDocumentUrlDBO();
			if (Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO())) {
				twoWheelerUrlDBO.setRecordStatus('D');
			}
			twoWheelerUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO().getNewFile()) && personalDataDto.getTwoWheelerUploadDTO().getNewFile()) {
			twoWheelerUrlDBO = employeeApplicationHandler.createURLAccessLinkDBO(twoWheelerUrlDBO, personalDataDto.getTwoWheelerUploadDTO().getProcessCode(), personalDataDto.getTwoWheelerUploadDTO().getUniqueFileName(), personalDataDto.getTwoWheelerUploadDTO().getOriginalFileName(), userId, null);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO().getProcessCode()) &&
				!Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO().getUniqueFileName()) && !Utils.isNullOrEmpty(personalDataDto.getTwoWheelerUploadDTO().getNewFile()) && personalDataDto.getTwoWheelerUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(personalDataDto.getTwoWheelerUploadDTO().getProcessCode(), personalDataDto.getTwoWheelerUploadDTO().getUniqueFileName()));
		}
		empAddtnlPersonalDataDBO.setTwoWheelerDocumentUrlDBO(twoWheelerUrlDBO);
	}

	public void fourWheelerUploadSetting(EmpPersonalDataTabDTO personalDataDto, EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO fourWheelerUrlDBO = null;
		if (Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getFourWheelerDocumentUrlDBO())) {
			if (!Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO().getNewFile()) && personalDataDto.getFourWheelerUploadDTO().getNewFile() &&
					!Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO().getOriginalFileName())) {
				fourWheelerUrlDBO = new UrlAccessLinkDBO();
				fourWheelerUrlDBO.setCreatedUsersId(userId);
				fourWheelerUrlDBO.setRecordStatus('A');
			}
		} else {
			fourWheelerUrlDBO = empAddtnlPersonalDataDBO.getFourWheelerDocumentUrlDBO();
			if (Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO())) {
				fourWheelerUrlDBO.setRecordStatus('D');
			}
			fourWheelerUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO().getNewFile()) && personalDataDto.getFourWheelerUploadDTO().getNewFile()) {
			fourWheelerUrlDBO = employeeApplicationHandler.createURLAccessLinkDBO(fourWheelerUrlDBO, personalDataDto.getFourWheelerUploadDTO().getProcessCode(), personalDataDto.getFourWheelerUploadDTO().getUniqueFileName(), personalDataDto.getFourWheelerUploadDTO().getOriginalFileName(), userId, null);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO().getProcessCode()) && !Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO().getUniqueFileName())
				&& !Utils.isNullOrEmpty(personalDataDto.getFourWheelerUploadDTO().getNewFile()) && personalDataDto.getFourWheelerUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(personalDataDto.getFourWheelerUploadDTO().getProcessCode(), personalDataDto.getFourWheelerUploadDTO().getUniqueFileName()));
		}
		empAddtnlPersonalDataDBO.setFourWheelerDocumentUrlDBO(fourWheelerUrlDBO);
	}

	public void aadharUploadSetting(EmpPersonalDataTabDTO personalDataDto, EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO adharUrlDBO = null;
		if (Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getAdharUploadUrlDBO())) {
			if (!Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO().getNewFile()) && personalDataDto.getAdharUploadDTO().getNewFile() &&
					!Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO().getOriginalFileName())) {
				adharUrlDBO = new UrlAccessLinkDBO();
				adharUrlDBO.setCreatedUsersId(userId);
				adharUrlDBO.setRecordStatus('A');
			}
		} else {
			adharUrlDBO = empAddtnlPersonalDataDBO.getAdharUploadUrlDBO();
			if (Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO())) {
				adharUrlDBO.setRecordStatus('D');
			}
			adharUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO().getNewFile()) && personalDataDto.getAdharUploadDTO().getNewFile()) {
			adharUrlDBO = aWSS3FileStorageService.createURLAccessLinkDBO(adharUrlDBO, personalDataDto.getAdharUploadDTO().getProcessCode(), personalDataDto.getAdharUploadDTO().getUniqueFileName(), personalDataDto.getAdharUploadDTO().getOriginalFileName(), userId);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO().getProcessCode()) && !Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO().getUniqueFileName()) && !Utils.isNullOrEmpty(personalDataDto.getAdharUploadDTO().getNewFile()) && personalDataDto.getAdharUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(aWSS3FileStorageService.createFileListForActualCopy(personalDataDto.getAdharUploadDTO().getProcessCode(), personalDataDto.getAdharUploadDTO().getUniqueFileName()));
		}
		empAddtnlPersonalDataDBO.setAdharUploadUrlDBO(adharUrlDBO);
	}

	public void panUploadSetting(EmpPersonalDataTabDTO personalDataDto, EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO panUrlDBO = null;
		if (Utils.isNullOrEmpty(empAddtnlPersonalDataDBO.getPanUploadUrlDBO())) {
			if (!Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO().getNewFile()) && personalDataDto.getPanUploadDTO().getNewFile() &&
					!Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO().getOriginalFileName())) {
				panUrlDBO = new UrlAccessLinkDBO();
				panUrlDBO.setCreatedUsersId(userId);
				panUrlDBO.setRecordStatus('A');
			}
		} else {
			panUrlDBO = empAddtnlPersonalDataDBO.getPanUploadUrlDBO();
			panUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO().getNewFile()) && personalDataDto.getPanUploadDTO().getNewFile()) {
			panUrlDBO = employeeApplicationHandler.createURLAccessLinkDBO(panUrlDBO, personalDataDto.getPanUploadDTO().getProcessCode(), personalDataDto.getPanUploadDTO().getUniqueFileName(), personalDataDto.getPanUploadDTO().getOriginalFileName(), userId, null);
		}
		if (!Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO()) && !Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO().getProcessCode()) && !Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO().getUniqueFileName()) &&
				!Utils.isNullOrEmpty(personalDataDto.getPanUploadDTO().getNewFile()) && personalDataDto.getPanUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(personalDataDto.getPanUploadDTO().getProcessCode(), personalDataDto.getPanUploadDTO().getUniqueFileName()));
		}
		empAddtnlPersonalDataDBO.setPanUploadUrlDBO(panUrlDBO);
	}

	public void setSidePanelOtherDetails(EmpProfileSidePanelDTO empProfileSidePanelDTO) {
		if (!Utils.isNullOrEmpty(empProfileSidePanelDTO) && !Utils.isNullOrEmpty(empProfileSidePanelDTO.getProfilePhotoUrl()) && !Utils.isNullOrEmpty(empProfileSidePanelDTO.getFileNameOriginal())) {
			FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
			fileUploadDownloadDTO.setActualPath(empProfileSidePanelDTO.getProfilePhotoUrl());
			fileUploadDownloadDTO.setProcessCode(empProfileSidePanelDTO.getUploadProcessCode());
			fileUploadDownloadDTO.setOriginalFileName(empProfileSidePanelDTO.getFileNameOriginal());
			aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
			empProfileSidePanelDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
		}
	}

	public List<EmpEmploymentHistoryDTO> updateEmploymentHistoryDTO(List<EmpEmploymentHistoryDTO> employmentHistoryList) {
		List<EmpEmploymentHistoryDTO> employmentHistoryListNew = new ArrayList<EmpEmploymentHistoryDTO>();
		if (!Utils.isNullOrEmpty(employmentHistoryList)) {
			Map<Pair<LocalDate, LocalDate>, List<EmpEmploymentHistoryDTO>> historyMap = employmentHistoryList.stream().collect(Collectors.groupingBy(h -> Pair.of(h.getPeriodFrom(), h.getPeriodTo())));
			if (!Utils.isNullOrEmpty(historyMap)) {
				historyMap.forEach((k, list) -> {
					EmpEmploymentHistoryDTO empEmploymentHistoryDTO = new EmpEmploymentHistoryDTO();
					list.forEach(dto -> {
						if (!Utils.isNullOrEmpty(dto.getDepartment())) {
							empEmploymentHistoryDTO.setDepartment(dto.getDepartment());
						}
						if (!Utils.isNullOrEmpty(dto.getEmployeeCategory())) {
							empEmploymentHistoryDTO.setEmployeeCategory(dto.getEmployeeCategory());
						}
						if (!Utils.isNullOrEmpty(dto.getJobCategory())) {
							empEmploymentHistoryDTO.setJobCategory(dto.getJobCategory());
						}
						if (!Utils.isNullOrEmpty(dto.getJobTitle())) {
							empEmploymentHistoryDTO.setJobTitle(dto.getJobTitle());
						}
						if (!Utils.isNullOrEmpty(dto.getDesignation())) {
							empEmploymentHistoryDTO.setDesignation(dto.getDesignation());
						}
						if (!Utils.isNullOrEmpty(dto.getCampus())) {
							empEmploymentHistoryDTO.setCampus(dto.getCampus());
						}
						if (!Utils.isNullOrEmpty(dto.getPeriodFrom())) {
							empEmploymentHistoryDTO.setPeriodFrom(dto.getPeriodFrom());
						}
						if (!Utils.isNullOrEmpty(dto.getPeriodTo())) {
							empEmploymentHistoryDTO.setPeriodTo(dto.getPeriodTo());
						}
					});
					employmentHistoryListNew.add(empEmploymentHistoryDTO);
				});
			}
		}
		return employmentHistoryListNew;
	}

	public void setDBOForEmploymentTab(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId, Boolean isRemarksPrivilegeEnabled) {
		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		setEmploymentHistory(empDBO, empEmploymentTabDTO, userId);
		setEmpDBOForEmploymentTab(empDBO, empEmploymentTabDTO, userId);
		setEmployeeJobDetails(empDBO, empEmploymentTabDTO.getJobDetailsDTO(), userId);
		setEmployeeLeaveAuthorizerDetails(empDBO, empEmploymentTabDTO, userId);
		setEmployeeRoomDetails(empDBO, empEmploymentTabDTO, userId);
		setEmployeeResignationDetails(empDBO, empEmploymentTabDTO.getResignationDTO(), userId);
		setRemarkDetails(empDBO, empEmploymentTabDTO, userId, uniqueFileNameList, isRemarksPrivilegeEnabled);
		empEmploymentTabDTO.setUniqueFileNameList(uniqueFileNameList);
		setLetterDBO(empEmploymentTabDTO, empDBO, userId, uniqueFileNameList);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory().getValue())) {
			if (!empEmploymentTabDTO.getJobCategory().getValue().equals("REGULAR")) {
				SelectDTO selectDTO = employeeProfileTransaction.getJobCategoryCode(Integer.parseInt(empEmploymentTabDTO.getJobCategory().getValue()));
				if (!Utils.isNullOrEmpty(selectDTO) && !Utils.isNullOrEmpty(selectDTO.getValue())) {
					setContractDetailsDBO(empEmploymentTabDTO, empDBO, userId, uniqueFileNameList, selectDTO.getValue());
				}
			}
		}
		if(!Utils.isNullOrEmpty(empEmploymentTabDTO.getEmpProfileLeaveAllotmentDTOList())) {
			setLeaveAllotmentDBO(empDBO, empEmploymentTabDTO.getEmpProfileLeaveAllotmentDTOList(), userId);
		}
		empEmploymentTabDTO.setUniqueFileNameList(uniqueFileNameList);
	}

	public void setEmpDBOForEmploymentTab(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId) {
		empDBO.setEmpNumber(empEmploymentTabDTO.getEmpNo());
		empDBO.setEmpEmployeeCategoryDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getEmployeeCategory()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getEmployeeCategory().getValue())) {
			EmpEmployeeCategoryDBO empEmployeeCategoryDBO = new EmpEmployeeCategoryDBO();
			empEmployeeCategoryDBO.setId(Integer.parseInt(empEmploymentTabDTO.getEmployeeCategory().getValue()));
			empDBO.setEmpEmployeeCategoryDBO(empEmployeeCategoryDBO);
		}
		empDBO.setEmpEmployeeJobCategoryDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory().getValue())) {
			EmpEmployeeJobCategoryDBO empEmployeeJobCategoryDBO = new EmpEmployeeJobCategoryDBO();
			empEmployeeJobCategoryDBO.setId(Integer.parseInt(empEmploymentTabDTO.getJobCategory().getValue()));
			empDBO.setEmpEmployeeJobCategoryDBO(empEmployeeJobCategoryDBO);
		}
		empDBO.setEmpEmployeeGroupDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getEmployeeGroup()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getEmployeeGroup().getValue())) {
			EmpEmployeeGroupDBO empEmployeeGroupDBO = new EmpEmployeeGroupDBO();
			empEmployeeGroupDBO.setId(Integer.parseInt(empEmploymentTabDTO.getEmployeeGroup().getValue()));
			empDBO.setEmpEmployeeGroupDBO(empEmployeeGroupDBO);
		}
		empDBO.setEmpDesignationDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignation()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignation().getValue())) {
			EmpDesignationDBO empDesignationDBO = new EmpDesignationDBO();
			empDesignationDBO.setId(Integer.parseInt(empEmploymentTabDTO.getDesignation().getValue()));
			empDBO.setEmpDesignationDBO(empDesignationDBO);
		}
		empDBO.setErpEmployeeTitleDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobTitle()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getJobTitle().getValue())) {
			ErpEmployeeTitleDBO erpEmployeeTitleDBO = new ErpEmployeeTitleDBO();
			erpEmployeeTitleDBO.setId(Integer.parseInt(empEmploymentTabDTO.getJobTitle().getValue()));
			empDBO.setErpEmployeeTitleDBO(erpEmployeeTitleDBO);
		}
		empDBO.setEmpApplnSubjectCategoryDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getSubjectOrCategory()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getSubjectOrCategory().getValue())) {
			EmpApplnSubjectCategoryDBO empApplnSubjectCategoryDBO = new EmpApplnSubjectCategoryDBO();
			empApplnSubjectCategoryDBO.setId(Integer.parseInt(empEmploymentTabDTO.getSubjectOrCategory().getValue()));
			empDBO.setEmpApplnSubjectCategoryDBO(empApplnSubjectCategoryDBO);
		}
		empDBO.setEmpApplnSubjectCategorySpecializationDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getSpecialization()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getSpecialization().getValue())) {
			EmpApplnSubjectCategorySpecializationDBO empApplnSubjectCategorySpecializationDBO = new EmpApplnSubjectCategorySpecializationDBO();
			empApplnSubjectCategorySpecializationDBO.setEmpApplnSubjectCategorySpecializationId(Integer.parseInt(empEmploymentTabDTO.getSpecialization().getValue()));
			empDBO.setEmpApplnSubjectCategorySpecializationDBO(empApplnSubjectCategorySpecializationDBO);
		}
		empDBO.setDeputationErpCampusDepartmentMappingDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getDeputedDepartment()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDeputedDepartment().getValue())) {
			ErpCampusDepartmentMappingDBO deputationErpCampusDepartmentMappingDBO = new ErpCampusDepartmentMappingDBO();
			deputationErpCampusDepartmentMappingDBO.setId(Integer.parseInt(empEmploymentTabDTO.getDeputedDepartment().getValue()));
			empDBO.setDeputationErpCampusDepartmentMappingDBO(deputationErpCampusDepartmentMappingDBO);
		}
		empDBO.setErpCampusDepartmentMappingDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartment()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartment().getValue())) {
			ErpCampusDepartmentMappingDBO empCampusDepartmentMappingDBO = new ErpCampusDepartmentMappingDBO();
			empCampusDepartmentMappingDBO.setId(Integer.parseInt(empEmploymentTabDTO.getDepartment().getValue()));
			empDBO.setErpCampusDepartmentMappingDBO(empCampusDepartmentMappingDBO);
		}
		empDBO.setEmpAlbumDesignationDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignationForAlbum()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignationForAlbum().getValue())) {
			EmpDesignationDBO empDesignationDBO = new EmpDesignationDBO();
			empDesignationDBO.setId(Integer.parseInt(empEmploymentTabDTO.getDesignationForAlbum().getValue()));
			empDBO.setEmpAlbumDesignationDBO(empDesignationDBO);
		}
		empDBO.setEmpTimeZoneDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getGeneralTimeZone()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getGeneralTimeZone().getValue())) {
			EmpTimeZoneDBO empTimeZoneDBO = new EmpTimeZoneDBO();
			empTimeZoneDBO.setId(Integer.parseInt(empEmploymentTabDTO.getGeneralTimeZone().getValue()));
			empDBO.setEmpTimeZoneDBO(empTimeZoneDBO);
		}
		if (empEmploymentTabDTO.getIsActive().equalsIgnoreCase("A")) {
			empDBO.setRecordStatus('A');
		} else {
			empDBO.setRecordStatus('I');
		}
		empDBO.setEmpDOJ(empEmploymentTabDTO.getDoj());
	}

	public void setEmployeeJobDetails(EmpDBO empDBO, EmploymentTabJobDetailsDTO jobDetailsDTO, Integer userId) {
		boolean areAllFieldsNull = areAllFieldsNull(jobDetailsDTO);
		EmpJobDetailsDBO empJobDetailsDBO;
		if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO())) {
			empJobDetailsDBO = empDBO.getEmpJobDetailsDBO();
			empJobDetailsDBO.setModifiedUsersId(userId);
		} else {
			if (areAllFieldsNull) {
				return;
			}
			empJobDetailsDBO = new EmpJobDetailsDBO();
			empJobDetailsDBO.setCreatedUsersId(userId);
			empJobDetailsDBO.setRecordStatus('A');
		}
		empJobDetailsDBO.setEmpLeaveCategoryAllotmentId(null);
		if (!Utils.isNullOrEmpty(jobDetailsDTO.getLeaveCategory()) && !Utils.isNullOrEmpty(jobDetailsDTO.getLeaveCategory().getValue())) {
			EmpLeaveCategoryAllotmentDBO empLeaveCategoryAllotmentDBO = new EmpLeaveCategoryAllotmentDBO();
			empLeaveCategoryAllotmentDBO.setEmpLeaveCategoryAllotmentId(Integer.parseInt(jobDetailsDTO.getLeaveCategory().getValue()));
			empJobDetailsDBO.setEmpLeaveCategoryAllotmentId(empLeaveCategoryAllotmentDBO);
		}
		empJobDetailsDBO.setIsVacationApplicable(jobDetailsDTO.getVacation());
		empJobDetailsDBO.setIsDisplayWebsite(jobDetailsDTO.getShowInWebsite());
		empJobDetailsDBO.setIsHolidayWorking(jobDetailsDTO.getIsHolidayWorking());
		empJobDetailsDBO.setIsHolidayTimeZoneApplicable(jobDetailsDTO.getHolidayTimeZoneApplicable());
		empJobDetailsDBO.setIsDutyRosterApplicable(jobDetailsDTO.getIsRosterAllotmentApplicable());
		empJobDetailsDBO.setRetirementDate(jobDetailsDTO.getRetirementDate());
		empJobDetailsDBO.setIsPunchingExempted(jobDetailsDTO.getIsPunchingExempted());
		empJobDetailsDBO.setHolidayTimeZoneDBO(null);
		if (!Utils.isNullOrEmpty(jobDetailsDTO.getHolidayTimeZone()) && !Utils.isNullOrEmpty(jobDetailsDTO.getHolidayTimeZone().getValue())) {
			EmpTimeZoneDBO holidayEmpTimeZoneDBO = new EmpTimeZoneDBO();
			holidayEmpTimeZoneDBO.setId(Integer.parseInt(jobDetailsDTO.getHolidayTimeZone().getValue()));
			empJobDetailsDBO.setHolidayTimeZoneDBO(holidayEmpTimeZoneDBO);
		}
		empJobDetailsDBO.setEmpDBO(empDBO);
		empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
	}

	public void setEmployeeRoomDetails(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId) {
		ErpRoomEmpMappingDBO erpRoomEmpMappingDBO;
		boolean newRec = false;
		if (!Utils.isNullOrEmpty(empDBO.getErpRoomEmpMappingDBO())) {
			erpRoomEmpMappingDBO = empDBO.getErpRoomEmpMappingDBO();
			erpRoomEmpMappingDBO.setModifiedUsersId(null);
		} else {
			erpRoomEmpMappingDBO = new ErpRoomEmpMappingDBO();
			erpRoomEmpMappingDBO.setRecordStatus('A');
			erpRoomEmpMappingDBO.setCreatedUsersId(userId);
			newRec = true;
		}
		erpRoomEmpMappingDBO.setTelephoneExtension(empEmploymentTabDTO.getExtensionNumber());
		erpRoomEmpMappingDBO.setErpRoomsDBO(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getRoom()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getRoom().getValue())) {
			ErpRoomsDBO erpRoomsDBO = new ErpRoomsDBO();
			erpRoomsDBO.setId(Integer.parseInt(empEmploymentTabDTO.getRoom().getValue()));
			erpRoomEmpMappingDBO.setErpRoomsDBO(erpRoomsDBO);
		}
		erpRoomEmpMappingDBO.setCabinNo(empEmploymentTabDTO.getCabinNo());
		if (!newRec || (newRec && !areAllFieldsNull(erpRoomEmpMappingDBO))) {
			erpRoomEmpMappingDBO.setEmpDBO(empDBO);
			empDBO.setErpRoomEmpMappingDBO(erpRoomEmpMappingDBO);
		}
	}

	public void setEmployeeResignationDetails(EmpDBO empDBO, EmploymentTabResignationDTO resignationDTO, Integer userId) {
		boolean areAllFieldsNull = areAllFieldsNull(resignationDTO);
		EmpResignationDBO empResignationDBO;
		if (!Utils.isNullOrEmpty(empDBO.getEmpresignationDBO())) {
			empResignationDBO = empDBO.getEmpresignationDBO();
			empResignationDBO.setModifiedUsersId(userId);
		} else {
			if (areAllFieldsNull) {
				return;
			}
			empResignationDBO = new EmpResignationDBO();
			empResignationDBO.setCreatedUsersId(userId);
			empResignationDBO.setRecordStatus('A');
		}
		empResignationDBO.setSubmissionDate(resignationDTO.getResignationDate());
		empResignationDBO.setDateOfLeaving(resignationDTO.getDateOfLeaving());
		empResignationDBO.setReasonOther(resignationDTO.getReasonForLeavingOther());
		empResignationDBO.setNoticePeriodServedDays(resignationDTO.getNoticePeriodServedDays());
		empResignationDBO.setVcAcceptedDate(resignationDTO.getApprovalDate());
		empResignationDBO.setRelievingDate(resignationDTO.getRelievingOrderDate());
		empResignationDBO.setPoRemarks(resignationDTO.getRecommendation());
		empResignationDBO.setIsExitInterviewCompleted(resignationDTO.getIsExitInterviewAttended());
		empResignationDBO.setEmpResignationReasonDBO(null);
		if (!Utils.isNullOrEmpty(resignationDTO.getReasonForLeaving()) && !Utils.isNullOrEmpty(resignationDTO.getReasonForLeaving().getValue())) {
			EmpResignationReasonDBO empResignationReasonDBO = new EmpResignationReasonDBO();
			empResignationReasonDBO.setId(Integer.parseInt(resignationDTO.getReasonForLeaving().getValue()));
			empResignationDBO.setEmpResignationReasonDBO(empResignationReasonDBO);
		}
		empResignationDBO.setEmpDBO(empDBO);
		empDBO.setEmpresignationDBO(empResignationDBO);
	}

	public void setEmployeeLeaveAuthorizerDetails(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId) {
		EmpApproversDBO empApproversDBO = null;
		boolean newRec = false;
		boolean isValueExists = false;
		if (!Utils.isNullOrEmpty(empDBO.getEmpApproversDBO())) {
			empApproversDBO = empDBO.getEmpApproversDBO();
			empApproversDBO.setModifiedUsersId(userId);
		} else {
			empApproversDBO = new EmpApproversDBO();
			empApproversDBO.setCreatedUsersId(userId);
			empApproversDBO.setRecordStatus('A');
			newRec = true;
		}
		empApproversDBO.setLeaveApproverId(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getLeaveApprover()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getLeaveApprover().getValue())) {
			EmpDBO leaveApproverDBO = new EmpDBO();
			leaveApproverDBO.setId(Integer.parseInt(empEmploymentTabDTO.getLeaveApprover().getValue()));
			empApproversDBO.setLeaveApproverId(leaveApproverDBO);
		}
		empApproversDBO.setLeaveAuthorizerId(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getLeaveAuthorizer()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getLeaveAuthorizer().getValue())) {
			EmpDBO leaveAuthorizerDBO = new EmpDBO();
			leaveAuthorizerDBO.setId(Integer.parseInt(empEmploymentTabDTO.getLeaveAuthorizer().getValue()));
			empApproversDBO.setLeaveAuthorizerId(leaveAuthorizerDBO);
		}
		empApproversDBO.setLevelOneAppraiserId(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getLevelOneAppraiser()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getLevelOneAppraiser().getValue())) {
			EmpDBO levelOneAppraiserDBO = new EmpDBO();
			levelOneAppraiserDBO.setId(Integer.parseInt(empEmploymentTabDTO.getLevelOneAppraiser().getValue()));
			empApproversDBO.setLevelOneAppraiserId(levelOneAppraiserDBO);
		}
		empApproversDBO.setLevelTwoAppraiserId(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getLeveltwoAppraiser()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getLeveltwoAppraiser().getValue())) {
			EmpDBO levelTwoAppraiserDBO = new EmpDBO();
			levelTwoAppraiserDBO.setId(Integer.parseInt(empEmploymentTabDTO.getLeveltwoAppraiser().getValue()));
			empApproversDBO.setLevelTwoAppraiserId(levelTwoAppraiserDBO);
		}
		empApproversDBO.setWorkDairyApproverId(null);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getWorkDiaryAprover()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getWorkDiaryAprover().getValue())) {
			EmpDBO workDiaryApproverDBO = new EmpDBO();
			workDiaryApproverDBO.setId(Integer.parseInt(empEmploymentTabDTO.getWorkDiaryAprover().getValue()));
			empApproversDBO.setWorkDairyApproverId(workDiaryApproverDBO);
		}
		boolean isAllFieldNull = areAllFieldsNull(empApproversDBO);
		if (!newRec || (newRec && !isAllFieldNull)) {
			empApproversDBO.setEmpDBO(empDBO);
			empDBO.setEmpApproversDBO(empApproversDBO);
		}
	}

	public void setRemarkDetails(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList, Boolean isRemarksPrivilegeEnabled) {
		Map<Integer, EmpRemarksDetailsDBO> savedEmpRemarksDetailsDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpRemarksDetailsDBOSet())
				? empDBO.getEmpRemarksDetailsDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(rem -> rem.getId(), rem -> rem)) : new HashMap<Integer, EmpRemarksDetailsDBO>();
		Set<EmpRemarksDetailsDBO> empRemarksDetailsDBOSet = new HashSet<EmpRemarksDetailsDBO>();
		Set<Integer> empRemarksDetailsDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getEmpRemarksDetailsDTOList())) {
			empEmploymentTabDTO.getEmpRemarksDetailsDTOList().forEach(empRemarksDetailsDTO -> {
				EmpRemarksDetailsDBO empRemarksDetailsDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpRemarksDetailsDBOMap) && savedEmpRemarksDetailsDBOMap.containsKey(empRemarksDetailsDTO.getId())) {
					empRemarksDetailsDBO = savedEmpRemarksDetailsDBOMap.get(empRemarksDetailsDTO.getId());
					empRemarksDetailsDBO.setModifiedUsersId(userId);
					empRemarksDetailsDBOIdsSet.add(empRemarksDetailsDBO.getId());
				} else {
					empRemarksDetailsDBO = new EmpRemarksDetailsDBO();
					empRemarksDetailsDBO.setRecordStatus('A');
					empRemarksDetailsDBO.setCreatedUsersId(userId);
				}
				empRemarksDetailsDBO.setEmpDBO(empDBO);
				empRemarksDetailsDBO.setRemarksDetails(empRemarksDetailsDTO.getRemarksDetails());
				empRemarksDetailsDBO.setRemarksDate(empRemarksDetailsDTO.getRemarksDate());
				empRemarksDetailsDBO.setForOfficeUse(empRemarksDetailsDTO.getIsForOfficeUse());
				remarksUploadSetting(empRemarksDetailsDTO, empRemarksDetailsDBO, userId, uniqueFileNameList);
				empRemarksDetailsDBOSet.add(empRemarksDetailsDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empDBO.getEmpRemarksDetailsDBOSet())) {
			empDBO.getEmpRemarksDetailsDBOSet().forEach(remarkDel -> {
				if (!empRemarksDetailsDBOIdsSet.contains(remarkDel.getId())) {
					if (remarkDel.isForOfficeUse) {
						if (isRemarksPrivilegeEnabled) {
							remarkDel.setRecordStatus('D');
							remarkDel.setModifiedUsersId(userId);
							empRemarksDetailsDBOSet.add(remarkDel);
						}
					} else {
						remarkDel.setRecordStatus('D');
						remarkDel.setModifiedUsersId(userId);
						empRemarksDetailsDBOSet.add(remarkDel);
					}
				}
			});

		}
		empDBO.setEmpRemarksDetailsDBOSet(empRemarksDetailsDBOSet);
	}
	public <T> boolean areAllFieldsNull(T dto) {
		if (dto == null) {
			return true; // If the DTO itself is null, all fields are considered null
		}
		Field[] fields = dto.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				boolean check = !field.getName().equals("id") && !field.getName().equals("createdUsersId") && !field.getName().equals("modifiedUsersId") &&
						!field.getName().equals("recordStatus");
				if (field.get(dto) != null && !field.get(dto).equals(0) && check) {
					return false; // At least one field is not null
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return true; // All fields are null
	}

	public void setEmploymentHistory(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId) {
		boolean isDepartmentChanged = false;
		boolean isDesignationChanged = false;
		boolean isEmpTitleChanged = false;
		boolean isEmpJobCategoryChanged = false;
		if ((Utils.isNullOrEmpty(empDBO.getErpCampusDepartmentMappingDBO()) &&
				!Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartment()) &&
				!Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartment().getValue())) ||
				(!Utils.isNullOrEmpty(empDBO.getErpCampusDepartmentMappingDBO()) && Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartment())) ||
				((!Utils.isNullOrEmpty(empDBO.getErpCampusDepartmentMappingDBO()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartment())) && empDBO.getErpCampusDepartmentMappingDBO().getId() != Integer.parseInt(empEmploymentTabDTO.getDepartment().getValue()))) {
			isDepartmentChanged = true;
		}
		if ((Utils.isNullOrEmpty(empDBO.getEmpDesignationDBO()) &&
				!Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignation()) &&
				!Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignation().getValue())) ||
				(!Utils.isNullOrEmpty(empDBO.getEmpDesignationDBO()) && Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignation())) ||
				((!Utils.isNullOrEmpty(empDBO.getEmpDesignationDBO()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignation())) && empDBO.getEmpDesignationDBO().getId() != Integer.parseInt(empEmploymentTabDTO.getDesignation().getValue()))) {
			isDesignationChanged = true;
		}
		if ((Utils.isNullOrEmpty(empDBO.getErpEmployeeTitleDBO()) &&
				!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobTitle()) &&
				!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobTitle().getValue())) ||
				(!Utils.isNullOrEmpty(empDBO.getErpEmployeeTitleDBO()) && Utils.isNullOrEmpty(empEmploymentTabDTO.getJobTitle())) ||
				((!Utils.isNullOrEmpty(empDBO.getErpEmployeeTitleDBO()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getJobTitle())) && empDBO.getErpEmployeeTitleDBO().getId() != Integer.parseInt(empEmploymentTabDTO.getJobTitle().getValue()))) {
			isEmpTitleChanged = true;
		}
		if ((Utils.isNullOrEmpty(empDBO.getEmpEmployeeJobCategoryDBO()) &&
				!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory()) &&
				!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory().getValue())) ||
				(!Utils.isNullOrEmpty(empDBO.getEmpEmployeeJobCategoryDBO()) && Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory())) ||
				((!Utils.isNullOrEmpty(empDBO.getEmpEmployeeJobCategoryDBO()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory())) && empDBO.getEmpEmployeeJobCategoryDBO().getId() != Integer.parseInt(empEmploymentTabDTO.getEmployeeCategory().getValue()))) {
			isEmpJobCategoryChanged = true;
		}
		if (isDepartmentChanged && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartmentEffectiveDate())) {
			updateDepartmentChange(empDBO, empEmploymentTabDTO, userId);
		}
		if (isDesignationChanged && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignationEffectiveDate())) {
			updateDesignationChange(empDBO, empEmploymentTabDTO, userId);
		}
		if (isEmpTitleChanged && !Utils.isNullOrEmpty(empEmploymentTabDTO.getTitleEffectiveDate())) {
			updateEmpTitleChange(empDBO, empEmploymentTabDTO, userId);
		}
		if (isEmpJobCategoryChanged && !Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategoryEffectiveDate())) {
			updateJobCategory(empDBO, empEmploymentTabDTO, userId);
		}
	}

	private void updateDepartmentChange(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId) {
		Set<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOSet = null;
		List<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOList = null;
		if (!Utils.isNullOrEmpty(empDBO.getEmpDepartmentDesignationHistoryDBOSet())) {
			empDepartmentDesignationHistoryDBOSet = empDBO.getEmpDepartmentDesignationHistoryDBOSet();
			empDepartmentDesignationHistoryDBOList = empDepartmentDesignationHistoryDBOSet.stream()
					.filter(d -> d.getErpCampusDepartmentMappingDBO() != null && d.getIsCurrent() != null && d.getIsCurrent()).collect(Collectors.toList());
		}
		//---updating the end date of old department

		if (!Utils.isNullOrEmpty(empDepartmentDesignationHistoryDBOList)) {
			EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBO = empDepartmentDesignationHistoryDBOList.get(0);
			if (!Utils.isNullOrEmpty(empDepartmentDesignationHistoryDBO)) {
				//if the from date equal no history needed as it can be a correction
				if (!empDepartmentDesignationHistoryDBO.getFromDate().equals(empEmploymentTabDTO.getDepartmentEffectiveDate())) {
					empDepartmentDesignationHistoryDBO.setToDate(empEmploymentTabDTO.getDepartmentEffectiveDate().minusDays(1));
					empDepartmentDesignationHistoryDBO.setIsCurrent(false);
					empDepartmentDesignationHistoryDBO.setModifiedUsersId(userId);
				}
			}
			if (!empDepartmentDesignationHistoryDBO.getFromDate().equals(empEmploymentTabDTO.getDepartmentEffectiveDate())) {
				if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartment())) {
					EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBONew = getEmpDepartmentDesignationHistoryDBO(empDBO, Integer.parseInt(empEmploymentTabDTO.getDepartment().getValue()));
					empDepartmentDesignationHistoryDBONew.setFromDate(empEmploymentTabDTO.getDepartmentEffectiveDate());
					empDepartmentDesignationHistoryDBONew.setIsCurrent(true);
					empDepartmentDesignationHistoryDBONew.setCreatedUsersId(userId);
					//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBONew);
					empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBONew);
				}
			}
			//depStartDate = empDepartmentDesignationHistoryDBOSet.stream().filter(d->d.getEmpDesignationDBO()!=null).map(EmpDepartmentDesignationHistoryDBO::getToDate).max(Comparator.naturalOrder()).orElse(null);
		} else {
			//when there is no record in the history table
			empDepartmentDesignationHistoryDBOSet = new HashSet<EmpDepartmentDesignationHistoryDBO>();
			if (!Utils.isNullOrEmpty(empDBO.getErpCampusDepartmentMappingDBO()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartmentEffectiveDate())) {
				EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBOOld = getEmpDepartmentDesignationHistoryDBO(empDBO, empDBO.getErpCampusDepartmentMappingDBO().getId());
				empDepartmentDesignationHistoryDBOOld.setFromDate(empDBO.getEmpDOJ());
				empDepartmentDesignationHistoryDBOOld.setToDate(empEmploymentTabDTO.getDepartmentEffectiveDate().minusDays(1));
				empDepartmentDesignationHistoryDBOOld.setIsCurrent(false);
				empDepartmentDesignationHistoryDBOOld.setCreatedUsersId(userId);
				empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBOOld);
				//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBOOld);
			}
			if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartment()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDepartmentEffectiveDate())) {
				EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBONew = getEmpDepartmentDesignationHistoryDBO(empDBO, Integer.parseInt(empEmploymentTabDTO.getDepartment().getValue()));
				empDepartmentDesignationHistoryDBONew.setFromDate(empEmploymentTabDTO.getDepartmentEffectiveDate());
				empDepartmentDesignationHistoryDBONew.setIsCurrent(true);
				empDepartmentDesignationHistoryDBONew.setCreatedUsersId(userId);
				empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBONew);
				//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBONew);
			}
		}
		//empDBO.setEmpDepartmentDesignationHistoryDBOSet(empDepartmentDesignationHistoryDBOSet);
	}

	private void updateDesignationChange(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId) {
		Set<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOSet = empDBO.getEmpDepartmentDesignationHistoryDBOSet();
		;
		List<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOList = empDepartmentDesignationHistoryDBOSet.stream()
				.filter(d -> d.getEmpDesignationDBO() != null && d.getIsCurrent() != null && d.getIsCurrent()).collect(Collectors.toList());
		if (!Utils.isNullOrEmpty(empDepartmentDesignationHistoryDBOList)) {
			//---updating the end date of old designation
			EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBO = empDepartmentDesignationHistoryDBOList.get(0);
			//if the from date equal no history needed as it can be a correction
			if (!Utils.isNullOrEmpty(empDepartmentDesignationHistoryDBO)) {
				if (!empDepartmentDesignationHistoryDBO.getFromDate().equals(empEmploymentTabDTO.getDesignationEffectiveDate())) {
					empDepartmentDesignationHistoryDBO.setToDate(empEmploymentTabDTO.getDesignationEffectiveDate().minusDays(1));
					empDepartmentDesignationHistoryDBO.setIsCurrent(false);
					empDepartmentDesignationHistoryDBO.setModifiedUsersId(userId);
				}
			}
			//adding new record with new designation
			//if the from date equal no history needed as it can be a correction
			if (!empDepartmentDesignationHistoryDBO.getFromDate().equals(empEmploymentTabDTO.getDesignationEffectiveDate())) {
				if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignation())) {
					EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBONew = getDesignationHistoryDBO(empDBO, empEmploymentTabDTO.getDesignation());
					empDepartmentDesignationHistoryDBONew.setFromDate(empEmploymentTabDTO.getDesignationEffectiveDate());
					empDepartmentDesignationHistoryDBONew.setIsCurrent(true);
					empDepartmentDesignationHistoryDBONew.setCreatedUsersId(userId);
					//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBONew);
					empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBONew);
				}
			}
		} else {
			//when there is no record in the history table
			empDepartmentDesignationHistoryDBOSet = new HashSet<EmpDepartmentDesignationHistoryDBO>();
			if (!Utils.isNullOrEmpty(empDBO.getEmpDesignationDBO()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignationEffectiveDate())) {
				EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBOOld = getDesignationHistoryDBO(empDBO, new SelectDTO(Integer.toString(empDBO.getEmpDesignationDBO().getId()), ""));
				empDepartmentDesignationHistoryDBOOld.setFromDate(empDBO.getEmpDOJ());
				empDepartmentDesignationHistoryDBOOld.setToDate(empEmploymentTabDTO.getDesignationEffectiveDate().minusDays(1));
				empDepartmentDesignationHistoryDBOOld.setIsCurrent(false);
				empDepartmentDesignationHistoryDBOOld.setCreatedUsersId(userId);
				//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBOOld);
				empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBOOld);
			}
			if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignation()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getDesignationEffectiveDate())) {
				EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBONew = getDesignationHistoryDBO(empDBO, empEmploymentTabDTO.getDesignation());
				empDepartmentDesignationHistoryDBONew.setFromDate(empEmploymentTabDTO.getDesignationEffectiveDate());
				empDepartmentDesignationHistoryDBONew.setIsCurrent(true);
				empDepartmentDesignationHistoryDBONew.setCreatedUsersId(userId);
				empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBONew);
				//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBONew);
			}
		}
		// empDBO.setEmpDepartmentDesignationHistoryDBOSet(empDepartmentDesignationHistoryDBOSet);
	}

	private void updateEmpTitleChange(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId) {
		Set<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOSet = empDBO.getEmpDepartmentDesignationHistoryDBOSet();
		List<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOList = empDepartmentDesignationHistoryDBOSet.stream()
				.filter(d -> d.getEmpTitleDBO() != null && d.getIsCurrent() != null && d.getIsCurrent()).collect(Collectors.toList());
		if (!Utils.isNullOrEmpty(empDepartmentDesignationHistoryDBOList)) {
			//---updating the end date of old title
			EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBO = empDepartmentDesignationHistoryDBOList.get(0);
			if (!Utils.isNullOrEmpty(empDepartmentDesignationHistoryDBO)) {
				if (!empDepartmentDesignationHistoryDBO.getFromDate().equals(empEmploymentTabDTO.getTitleEffectiveDate())) {
					empDepartmentDesignationHistoryDBO.setToDate(empEmploymentTabDTO.getTitleEffectiveDate().minusDays(1));
					empDepartmentDesignationHistoryDBO.setIsCurrent(false);
					empDepartmentDesignationHistoryDBO.setModifiedUsersId(userId);
				}
			}
			//adding new record with new title
			if (!empDepartmentDesignationHistoryDBO.getFromDate().equals(empEmploymentTabDTO.getTitleEffectiveDate())) {
				if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobTitle())) {
					EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBONew = getJobTitleHistoryDBO(empDBO, empEmploymentTabDTO.getJobTitle());
					empDepartmentDesignationHistoryDBONew.setFromDate(empEmploymentTabDTO.getTitleEffectiveDate());
					empDepartmentDesignationHistoryDBONew.setIsCurrent(true);
					empDepartmentDesignationHistoryDBONew.setCreatedUsersId(userId);
					empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBONew);
					//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBONew);
				}
			}
		} else {
			//when there is no record in the history table
			empDepartmentDesignationHistoryDBOSet = new HashSet<EmpDepartmentDesignationHistoryDBO>();
			if (!Utils.isNullOrEmpty(empDBO.getErpEmployeeTitleDBO()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getTitleEffectiveDate())) {
				EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBOOld = getJobTitleHistoryDBO(empDBO, new SelectDTO(Integer.toString(empDBO.getErpEmployeeTitleDBO().getId()), ""));
				empDepartmentDesignationHistoryDBOOld.setFromDate(empDBO.getEmpDOJ());
				empDepartmentDesignationHistoryDBOOld.setToDate(empEmploymentTabDTO.getTitleEffectiveDate().minusDays(1));
				empDepartmentDesignationHistoryDBOOld.setIsCurrent(false);
				empDepartmentDesignationHistoryDBOOld.setCreatedUsersId(userId);
				empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBOOld);
				//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBOOld);
			}
			if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobTitle()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getTitleEffectiveDate())) {
				EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBONew = getJobTitleHistoryDBO(empDBO, empEmploymentTabDTO.getJobTitle());
				empDepartmentDesignationHistoryDBONew.setFromDate(empEmploymentTabDTO.getTitleEffectiveDate());
				empDepartmentDesignationHistoryDBONew.setIsCurrent(true);
				empDepartmentDesignationHistoryDBONew.setCreatedUsersId(userId);
				empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBONew);
				//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBONew);
			}
		}
		//empDBO.setEmpDepartmentDesignationHistoryDBOSet(empDepartmentDesignationHistoryDBOSet);
	}

	private void updateJobCategory(EmpDBO empDBO, EmpEmploymentTabDTO empEmploymentTabDTO, Integer userId) {
		Set<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOSet = empDBO.getEmpDepartmentDesignationHistoryDBOSet();
		List<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOList = empDepartmentDesignationHistoryDBOSet.stream()
				.filter(d -> d.getEmpEmployeeJobCategoryDBO() != null && d.getIsCurrent() != null && d.getIsCurrent()).collect(Collectors.toList());
		if (!Utils.isNullOrEmpty(empDepartmentDesignationHistoryDBOList)) {
			//---updating the end date of old job category
			EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBO = empDepartmentDesignationHistoryDBOList.get(0);
			if (!Utils.isNullOrEmpty(empDepartmentDesignationHistoryDBO)) {
				if (!empDepartmentDesignationHistoryDBO.getFromDate().equals(empEmploymentTabDTO.getJobCategoryEffectiveDate())) {
					empDepartmentDesignationHistoryDBO.setToDate(empEmploymentTabDTO.getJobCategoryEffectiveDate().minusDays(1));
					empDepartmentDesignationHistoryDBO.setIsCurrent(false);
					empDepartmentDesignationHistoryDBO.setModifiedUsersId(userId);
				}
			}
			//adding new record with new job category
			if (!empDepartmentDesignationHistoryDBO.getFromDate().equals(empEmploymentTabDTO.getJobCategoryEffectiveDate())) {
				if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory())) {
					EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBONew = getEmpEmployeeJobCategoryDBO(empDBO, empEmploymentTabDTO.getJobCategory());
					empDepartmentDesignationHistoryDBONew.setFromDate(empEmploymentTabDTO.getJobCategoryEffectiveDate());
					empDepartmentDesignationHistoryDBONew.setIsCurrent(true);
					empDepartmentDesignationHistoryDBONew.setCreatedUsersId(userId);
					empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBONew);
					//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBONew);
				}
			}
		} else {
			//when there is no record in the history table
			empDepartmentDesignationHistoryDBOSet = new HashSet<EmpDepartmentDesignationHistoryDBO>();
			if (!Utils.isNullOrEmpty(empDBO.getEmpEmployeeJobCategoryDBO()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategoryEffectiveDate())) {
				EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBOOld = getEmpEmployeeJobCategoryDBO(empDBO, new SelectDTO(Integer.toString(empDBO.getEmpEmployeeJobCategoryDBO().getId()), ""));
				empDepartmentDesignationHistoryDBOOld.setFromDate(empDBO.getEmpDOJ());
				empDepartmentDesignationHistoryDBOOld.setToDate(empEmploymentTabDTO.getJobCategoryEffectiveDate().minusDays(1));
				empDepartmentDesignationHistoryDBOOld.setIsCurrent(false);
				empDepartmentDesignationHistoryDBOOld.setCreatedUsersId(userId);
				empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBOOld);
				//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBOOld);
			}
			if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategory()) && !Utils.isNullOrEmpty(empEmploymentTabDTO.getJobCategoryEffectiveDate())) {
				EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBONew = getEmpEmployeeJobCategoryDBO(empDBO, empEmploymentTabDTO.getJobCategory());
				empDepartmentDesignationHistoryDBONew.setFromDate(empEmploymentTabDTO.getJobCategoryEffectiveDate());
				empDepartmentDesignationHistoryDBONew.setIsCurrent(true);
				empDepartmentDesignationHistoryDBONew.setCreatedUsersId(userId);
				empDBO.getEmpDepartmentDesignationHistoryDBOSet().add(empDepartmentDesignationHistoryDBONew);
				//empDepartmentDesignationHistoryDBOSet.add(empDepartmentDesignationHistoryDBONew);
			}
		}
		//empDBO.setEmpDepartmentDesignationHistoryDBOSet(empDepartmentDesignationHistoryDBOSet);
	}

	private static EmpDepartmentDesignationHistoryDBO getEmpDepartmentDesignationHistoryDBO(EmpDBO empDBO, Integer departmentId) {
		EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBO = new EmpDepartmentDesignationHistoryDBO();
		ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBOOld = new ErpCampusDepartmentMappingDBO();
		erpCampusDepartmentMappingDBOOld.setId(departmentId);
		empDepartmentDesignationHistoryDBO.setErpCampusDepartmentMappingDBO(erpCampusDepartmentMappingDBOOld);
		empDepartmentDesignationHistoryDBO.setRecordStatus('A');
		empDepartmentDesignationHistoryDBO.setEmpDBO(empDBO);
		return empDepartmentDesignationHistoryDBO;
	}

	private static EmpDepartmentDesignationHistoryDBO getDesignationHistoryDBO(EmpDBO empDBO, SelectDTO designationDTO) {
		EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBO = new EmpDepartmentDesignationHistoryDBO();
		EmpDesignationDBO empDesignationDBO = null;
		if (!Utils.isNullOrEmpty(designationDTO)) {
			empDesignationDBO = new EmpDesignationDBO();
			empDesignationDBO.setId(Integer.parseInt(designationDTO.getValue()));
		}
		empDepartmentDesignationHistoryDBO.setEmpDesignationDBO(empDesignationDBO);
		empDepartmentDesignationHistoryDBO.setRecordStatus('A');
		empDepartmentDesignationHistoryDBO.setEmpDBO(empDBO);
		return empDepartmentDesignationHistoryDBO;
	}

	private static EmpDepartmentDesignationHistoryDBO getJobTitleHistoryDBO(EmpDBO empDBO, SelectDTO jobTitle) {
		EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBO = new EmpDepartmentDesignationHistoryDBO();
		EmpTitleDBO empTitleDBO = null;
		if (!Utils.isNullOrEmpty(jobTitle)) {
			empTitleDBO = new EmpTitleDBO();
			empTitleDBO.setId(Integer.parseInt(jobTitle.getValue()));
		}
		empDepartmentDesignationHistoryDBO.setEmpTitleDBO(empTitleDBO);

		empDepartmentDesignationHistoryDBO.setRecordStatus('A');
		empDepartmentDesignationHistoryDBO.setEmpDBO(empDBO);
		return empDepartmentDesignationHistoryDBO;
	}

	private static EmpDepartmentDesignationHistoryDBO getEmpEmployeeJobCategoryDBO(EmpDBO empDBO, SelectDTO jobCategory) {
		EmpDepartmentDesignationHistoryDBO empDepartmentDesignationHistoryDBO = new EmpDepartmentDesignationHistoryDBO();
		EmpEmployeeJobCategoryDBO empEmployeeJobCategoryDBO = null;
		if (!Utils.isNullOrEmpty(jobCategory)) {
			empEmployeeJobCategoryDBO = new EmpEmployeeJobCategoryDBO();
			empEmployeeJobCategoryDBO.setId(Integer.parseInt(jobCategory.getValue()));
		}
		empDepartmentDesignationHistoryDBO.setEmpEmployeeJobCategoryDBO(empEmployeeJobCategoryDBO);

		empDepartmentDesignationHistoryDBO.setRecordStatus('A');
		empDepartmentDesignationHistoryDBO.setEmpDBO(empDBO);
		return empDepartmentDesignationHistoryDBO;
	}

	public void remarksUploadSetting(EmpRemarksDetailsDTO empRemarksDetailsDTO, EmpRemarksDetailsDBO empRemarksDetailsDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO remarkUrlDBO = null;
		if (Utils.isNullOrEmpty(empRemarksDetailsDBO.getRemarksUploadUrlDBO())) {
			if (!Utils.isNullOrEmpty(empRemarksDetailsDTO.getRemarksUploadUrlDTO()) && !Utils.isNullOrEmpty(empRemarksDetailsDTO.getRemarksUploadUrlDTO().getNewFile()) && empRemarksDetailsDTO.getRemarksUploadUrlDTO().getNewFile() &&
					!Utils.isNullOrEmpty(empRemarksDetailsDTO.getRemarksUploadUrlDTO().getOriginalFileName())) {
				remarkUrlDBO = new UrlAccessLinkDBO();
				remarkUrlDBO.setCreatedUsersId(userId);
				remarkUrlDBO.setRecordStatus('A');
			}
		} else {
			remarkUrlDBO = empRemarksDetailsDBO.getRemarksUploadUrlDBO();
			remarkUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(empRemarksDetailsDTO.getRemarksUploadUrlDTO()) && empRemarksDetailsDTO.getRemarksUploadUrlDTO().getNewFile()) {
			remarkUrlDBO = aWSS3FileStorageService.createURLAccessLinkDBO(remarkUrlDBO, empRemarksDetailsDTO.getRemarksUploadUrlDTO().getProcessCode(), empRemarksDetailsDTO.getRemarksUploadUrlDTO().getUniqueFileName(), empRemarksDetailsDTO.getRemarksUploadUrlDTO().getOriginalFileName(), userId);
		}
		if (!Utils.isNullOrEmpty(empRemarksDetailsDTO.getRemarksUploadUrlDTO()) && !Utils.isNullOrEmpty(empRemarksDetailsDTO.getRemarksUploadUrlDTO().getProcessCode()) && !Utils.isNullOrEmpty(empRemarksDetailsDTO.getRemarksUploadUrlDTO().getUniqueFileName()) && empRemarksDetailsDTO.getRemarksUploadUrlDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(empRemarksDetailsDTO.getRemarksUploadUrlDTO().getProcessCode(), empRemarksDetailsDTO.getRemarksUploadUrlDTO().getUniqueFileName()));
		}
		empRemarksDetailsDBO.setRemarksUploadUrlDBO(remarkUrlDBO);
	}

	public void setLetterDBO(EmpEmploymentTabDTO empEmploymentTabDTO, EmpDBO empDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		Map<Integer, EmpEmployeeLetterDetailsDBO> savedEmpEmployeeLetterDetailsDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpEmployeeLetterDetailsDBOSet())
				? empDBO.getEmpEmployeeLetterDetailsDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(EmpEmployeeLetterDetailsDBO::getId, dep -> dep)) : new HashMap<Integer, EmpEmployeeLetterDetailsDBO>();
		Set<EmpEmployeeLetterDetailsDBO> empEmployeeLetterDetailsDBOSet = new HashSet<EmpEmployeeLetterDetailsDBO>();
		Set<Integer> empEmployeeLetterDetailsDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getLetterDetailsDTOList())) {
			empEmploymentTabDTO.getLetterDetailsDTOList().forEach(empLetterDetailsDTO -> {
				EmpEmployeeLetterDetailsDBO empEmployeeLetterDetailsDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpEmployeeLetterDetailsDBOMap) && savedEmpEmployeeLetterDetailsDBOMap.containsKey(empLetterDetailsDTO.getId())) {
					empEmployeeLetterDetailsDBO = savedEmpEmployeeLetterDetailsDBOMap.get(empLetterDetailsDTO.getId());
					empEmployeeLetterDetailsDBO.setModifiedUsersId(userId);
					empEmployeeLetterDetailsDBOIdsSet.add(empEmployeeLetterDetailsDBO.getId());
				} else {
					empEmployeeLetterDetailsDBO = new EmpEmployeeLetterDetailsDBO();
					empEmployeeLetterDetailsDBO.setCreatedUsersId(userId);
				}
				empEmployeeLetterDetailsDBO.setEmpDBO(empDBO);
				empEmployeeLetterDetailsDBO.setLetterRefNo(empLetterDetailsDTO.getLetterRefNo());
				empEmployeeLetterDetailsDBO.setLetterDate(empLetterDetailsDTO.getLetterDate());
				EmpLetterTypeDBO letterTypeDBO = null;
				if (!Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterTypeDTO()) && !Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterTypeDTO().getValue())) {
					letterTypeDBO = new EmpLetterTypeDBO();
					letterTypeDBO.setId(Integer.parseInt(empLetterDetailsDTO.getLetterTypeDTO().getValue()));
				}
				empEmployeeLetterDetailsDBO.setEmpLetterTypeDBO(letterTypeDBO);
				empEmployeeLetterDetailsDBO.setRecordStatus('A');
				empEmployeeLetterDetailsDBO.setEmpLetterTypeDBO(letterTypeDBO);
				letterUploadSetting(empLetterDetailsDTO, empEmployeeLetterDetailsDBO, userId, uniqueFileNameList);//upload setting
				empEmployeeLetterDetailsDBOSet.add(empEmployeeLetterDetailsDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empDBO.getEmpEmployeeLetterDetailsDBOSet())) {
			empDBO.getEmpEmployeeLetterDetailsDBOSet().forEach(empEmployeeLetterDetailsDBODel -> {
				if (!empEmployeeLetterDetailsDBOIdsSet.contains(empEmployeeLetterDetailsDBODel.getId())) {
					if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBODel.getLetterUrlDBO())) {
						UrlAccessLinkDBO urlAccessLinkDBO = empEmployeeLetterDetailsDBODel.getLetterUrlDBO();
						if (!Utils.isNullOrEmpty(urlAccessLinkDBO)) {
							urlAccessLinkDBO.setRecordStatus('D');
							urlAccessLinkDBO.setModifiedUsersId(userId);
							empEmployeeLetterDetailsDBODel.setLetterUrlDBO(urlAccessLinkDBO);
						}
					}
					empEmployeeLetterDetailsDBODel.setRecordStatus('D');
					empEmployeeLetterDetailsDBODel.setModifiedUsersId(userId);
					empEmployeeLetterDetailsDBOSet.add(empEmployeeLetterDetailsDBODel);
				}
			});
		}
		if (!Utils.isNullOrEmpty(empEmployeeLetterDetailsDBOSet)) {
			empDBO.setEmpEmployeeLetterDetailsDBOSet(empEmployeeLetterDetailsDBOSet);
		} else {
			empDBO.setEmpEmployeeLetterDetailsDBOSet(null);
		}
	}

	public void letterUploadSetting(EmpLetterDetailsDTO empLetterDetailsDTO, EmpEmployeeLetterDetailsDBO empEmployeeLetterDetailsDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO letterUrlDBO = null;
		if (Utils.isNullOrEmpty(empEmployeeLetterDetailsDBO.getLetterUrlDBO())) {
			if (!Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO()) && !Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO().getNewFile()) && empLetterDetailsDTO.getLetterUploadDTO().getNewFile() &&
					!Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO().getOriginalFileName())) {
				letterUrlDBO = new UrlAccessLinkDBO();
				letterUrlDBO.setCreatedUsersId(userId);
				letterUrlDBO.setRecordStatus('A');
			}
		} else {
			letterUrlDBO = empEmployeeLetterDetailsDBO.getLetterUrlDBO();
			if (Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO())) {
				letterUrlDBO.setRecordStatus('D');
			}
			letterUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO()) && !Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO().getNewFile()) && empLetterDetailsDTO.getLetterUploadDTO().getNewFile()) {
			letterUrlDBO = employeeApplicationHandler.createURLAccessLinkDBO(letterUrlDBO, empLetterDetailsDTO.getLetterUploadDTO().getProcessCode(), empLetterDetailsDTO.getLetterUploadDTO().getUniqueFileName(), empLetterDetailsDTO.getLetterUploadDTO().getOriginalFileName(), userId, null);
		}
		if (!Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO()) && !Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO().getProcessCode()) && !Utils.isNullOrEmpty(empLetterDetailsDTO.getLetterUploadDTO().getUniqueFileName()) && empLetterDetailsDTO.getLetterUploadDTO().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(empLetterDetailsDTO.getLetterUploadDTO().getProcessCode(), empLetterDetailsDTO.getLetterUploadDTO().getUniqueFileName()));
		}
		empEmployeeLetterDetailsDBO.setLetterUrlDBO(letterUrlDBO);
	}

	public List<EmpRemarksDetailsDTO> updateRemarkListDTO(List<EmpRemarksDetailsDTO> remarksDetailsDTOList, String userId, String userCampusIds) {
		String authKey = "/Secured/Employee/Recruitment/EmployeeProfile/empProfileOfficeRemarksEnable";
		boolean isPrivilegeEnabled = isPrivilegeEnabled(authKey, userId, userCampusIds);
		List<EmpRemarksDetailsDTO> remarksDetailsDTOListNew = new ArrayList<EmpRemarksDetailsDTO>();
		if (!Utils.isNullOrEmpty(remarksDetailsDTOList)) {
			remarksDetailsDTOList.forEach(r -> {
				if (r.getIsForOfficeUse()) {
					if (isPrivilegeEnabled) {
						remarksDetailsDTOListNew.add(r);
					}
				} else {
					remarksDetailsDTOListNew.add(r);
				}
			});
		}
		return remarksDetailsDTOListNew;
	}
	public void updateEducationalDetailsDTOList(List<EmpProfileEducationalDetailsDTO> empProfileEducationalDetailsDTOList, Map<Integer, List<EmpProfileEdnDetailsDocumentsDTO>> empProfileEdnDetailsDocumentsDTOMap) {
		empProfileEducationalDetailsDTOList.forEach(ednDetails -> {
			if (!Utils.isNullOrEmpty(empProfileEdnDetailsDocumentsDTOMap)) {
				List<EmpProfileEdnDetailsDocumentsDTO> documentList = empProfileEdnDetailsDocumentsDTOMap.get(ednDetails.getId());
				if (!Utils.isNullOrEmpty(documentList)) {
					ednDetails.setEmpProfileEdnDetailsDocumentsDTOList(documentList);
				}
			}
		});
	}
	public void setDBOForQualificationTab(EmpDBO empDBO, EmpQualificationTabDTO epQualificationTabDTO, Integer userId) {
		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		EmpPersonalDataDBO empPersonalDataDBO = empDBO.getEmpPersonalDataDBO();
		if (Utils.isNullOrEmpty(empDBO.getEmpPersonalDataDBO())) {
			empPersonalDataDBO = new EmpPersonalDataDBO();
			empPersonalDataDBO.setCreatedUsersId(userId);
			empPersonalDataDBO.setRecordStatus('A');
		}
		empPersonalDataDBO.setHighestQualificationAlbum(epQualificationTabDTO.getHighestQualificationForAlbum());
		empPersonalDataDBO.setErpQualificationLevelDBO(null);
		if (!Utils.isNullOrEmpty(epQualificationTabDTO.getHighestQualification()) && !Utils.isNullOrEmpty(epQualificationTabDTO.getHighestQualification().getValue())) {
			ErpQualificationLevelDBO erpQualificationLevelDBO = new ErpQualificationLevelDBO();
			erpQualificationLevelDBO.setId(Integer.parseInt(epQualificationTabDTO.getHighestQualification().getValue()));
			empPersonalDataDBO.setErpQualificationLevelDBO(erpQualificationLevelDBO);
		}
		empDBO.setEmpPersonalDataDBO(empPersonalDataDBO);
		setEducationalDetailsDBO(empDBO, epQualificationTabDTO, userId, uniqueFileNameList);
		setEligibilityTestDBO(empDBO, epQualificationTabDTO, userId, uniqueFileNameList);
		epQualificationTabDTO.setUniqueFileNameList(uniqueFileNameList);
	}

	public void setEducationalDetailsDBO(EmpDBO empDBO, EmpQualificationTabDTO epQualificationTabDTO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		Map<Integer, EmpEducationalDetailsDBO> savedEmpEducationalDetailsDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpEducationalDetailsDBOSet())
				? empDBO.getEmpEducationalDetailsDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(edu -> edu.getEmpEducationalDetailsId(), edu -> edu)) : new HashMap<Integer, EmpEducationalDetailsDBO>();
		Set<EmpEducationalDetailsDBO> empEducationalDetailsDBOSet = new HashSet<EmpEducationalDetailsDBO>();
		Set<Integer> empEducationalDetailsDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(epQualificationTabDTO.getEmpEducationalDetailsDTOList())) {
			epQualificationTabDTO.getEmpEducationalDetailsDTOList().forEach(educationalDetailsDTO -> {
				EmpEducationalDetailsDBO empEducationalDetailsDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpEducationalDetailsDBOMap) && savedEmpEducationalDetailsDBOMap.containsKey(educationalDetailsDTO.getId())) {
					empEducationalDetailsDBO = savedEmpEducationalDetailsDBOMap.get(educationalDetailsDTO.getId());
					empEducationalDetailsDBO.setModifiedUsersId(userId);
					empEducationalDetailsDBOIdsSet.add(empEducationalDetailsDBO.getEmpEducationalDetailsId());
				} else {
					empEducationalDetailsDBO = new EmpEducationalDetailsDBO();
					empEducationalDetailsDBO.setRecordStatus('A');
					empEducationalDetailsDBO.setCreatedUsersId(userId);
				}
				empEducationalDetailsDBO.setEmpDBO(empDBO);
				empEducationalDetailsDBO.setErpQualificationLevelDBO(null);
				if (!Utils.isNullOrEmpty(educationalDetailsDTO.getQualification()) && !Utils.isNullOrEmpty(educationalDetailsDTO.getQualification().getValue())) {
					ErpQualificationLevelDBO erpQualificationLevelDBO = new ErpQualificationLevelDBO();
					erpQualificationLevelDBO.setId(Integer.parseInt(educationalDetailsDTO.getQualification().getValue()));
					empEducationalDetailsDBO.setErpQualificationLevelDBO(erpQualificationLevelDBO);
				}
				empEducationalDetailsDBO.setQualificationOthers(educationalDetailsDTO.getQualificationOthers());
				empEducationalDetailsDBO.setCurrentStatus(educationalDetailsDTO.getStatus());
				empEducationalDetailsDBO.setCourse(educationalDetailsDTO.getCourse());
				empEducationalDetailsDBO.setSpecialization(educationalDetailsDTO.getSpecialisation());
				empEducationalDetailsDBO.setYearOfCompletion(educationalDetailsDTO.getYearOfRegistration());
				empEducationalDetailsDBO.setGradeOrPercentage(educationalDetailsDTO.getGradeOrPercentage());
				empEducationalDetailsDBO.setErpCountryDBO(null);
				if (!Utils.isNullOrEmpty(educationalDetailsDTO.getCountry()) && !Utils.isNullOrEmpty(educationalDetailsDTO.getCountry().getValue())) {
					ErpCountryDBO erpCountryDBO = new ErpCountryDBO();
					erpCountryDBO.setId(Integer.parseInt(educationalDetailsDTO.getCountry().getValue()));
					empEducationalDetailsDBO.setErpCountryDBO(erpCountryDBO);
				}
				empEducationalDetailsDBO.setErpStateDBO(null);
				if (!Utils.isNullOrEmpty(educationalDetailsDTO.getState()) && !Utils.isNullOrEmpty(educationalDetailsDTO.getState().getValue())) {
					ErpStateDBO erpStateDBO = new ErpStateDBO();
					erpStateDBO.setId(Integer.parseInt(educationalDetailsDTO.getState().getValue()));
					empEducationalDetailsDBO.setErpStateDBO(erpStateDBO);
				}
				empEducationalDetailsDBO.setBoardOrUniversity(educationalDetailsDTO.getBoardOther());
				empEducationalDetailsDBO.setErpUniversityBoardDBO(null);
				if (!Utils.isNullOrEmpty(educationalDetailsDTO.getBoard()) && !Utils.isNullOrEmpty(educationalDetailsDTO.getBoard().getValue())) {
					ErpUniversityBoardDBO erpUniversityBoardDBO = new ErpUniversityBoardDBO();
					erpUniversityBoardDBO.setId(Integer.parseInt(educationalDetailsDTO.getBoard().getValue()));
					empEducationalDetailsDBO.setErpUniversityBoardDBO(erpUniversityBoardDBO);
				}
				empEducationalDetailsDBO.setErpInstitutionDBO(null);
				if (!Utils.isNullOrEmpty(educationalDetailsDTO.getInstitute()) && !Utils.isNullOrEmpty(educationalDetailsDTO.getInstitute().getValue())) {
					ErpInstitutionDBO erpInstitutionDBO = new ErpInstitutionDBO();
					erpInstitutionDBO.setId(Integer.parseInt(educationalDetailsDTO.getInstitute().getValue()));
					empEducationalDetailsDBO.setErpInstitutionDBO(erpInstitutionDBO);
				}
				empEducationalDetailsDBO.setInstitute(educationalDetailsDTO.getInstituteOther());
				empEducationalDetailsDBO.setStateOthers(educationalDetailsDTO.getStateOther());
				updateEducationDocuments(empEducationalDetailsDBO, educationalDetailsDTO, userId, uniqueFileNameList); //update document table
				empEducationalDetailsDBOSet.add(empEducationalDetailsDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empDBO.getEmpEducationalDetailsDBOSet())) {
			empDBO.getEmpEducationalDetailsDBOSet().forEach(edn -> {
				if (!empEducationalDetailsDBOIdsSet.contains(edn.getEmpEducationalDetailsId())) {
					edn.setRecordStatus('D');
					edn.setModifiedUsersId(userId);
					empEducationalDetailsDBOSet.add(edn);
				}
			});

		}
		empDBO.setEmpEducationalDetailsDBOSet(empEducationalDetailsDBOSet);
	}

	public void updateEducationDocuments(EmpEducationalDetailsDBO empEducationalDetailsDBO, EmpProfileEducationalDetailsDTO educationalDetailsDTO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		Map<Integer, EmpEducationalDetailsDocumentsDBO> savedEmpEducationalDetailsDocumentsDBOMap = !Utils.isNullOrEmpty(empEducationalDetailsDBO.getDocumentsDBOSet())
				? empEducationalDetailsDBO.getDocumentsDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(doc -> doc.getId(), edu -> edu)) : new HashMap<Integer, EmpEducationalDetailsDocumentsDBO>();
		Set<EmpEducationalDetailsDocumentsDBO> empEducationalDetailsDocumentsDBOSet = new HashSet<EmpEducationalDetailsDocumentsDBO>();
		Set<Integer> empEducationalDetailsDocumentsIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(educationalDetailsDTO.getEmpProfileEdnDetailsDocumentsDTOList())) {
			educationalDetailsDTO.getEmpProfileEdnDetailsDocumentsDTOList().forEach(documentsDTO -> {
				EmpEducationalDetailsDocumentsDBO empEducationalDetailsDocumentsDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpEducationalDetailsDocumentsDBOMap) && savedEmpEducationalDetailsDocumentsDBOMap.containsKey(documentsDTO.getId())) {
					empEducationalDetailsDocumentsDBO = savedEmpEducationalDetailsDocumentsDBOMap.get(documentsDTO.getId());
					empEducationalDetailsDocumentsDBO.setModifiedUsersId(userId);
					empEducationalDetailsDocumentsIdsSet.add(empEducationalDetailsDocumentsDBO.getId());
				} else {
					empEducationalDetailsDocumentsDBO = new EmpEducationalDetailsDocumentsDBO();
					empEducationalDetailsDocumentsDBO.setRecordStatus('A');
					empEducationalDetailsDocumentsDBO.setCreatedUsersId(userId);
				}
				empEducationalDetailsDocumentsDBO.setEmpEducationalDetailsDBO(empEducationalDetailsDBO);
				qualificationUploadSetting(documentsDTO, empEducationalDetailsDocumentsDBO, userId, uniqueFileNameList);//upload setting
				empEducationalDetailsDocumentsDBOSet.add(empEducationalDetailsDocumentsDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empEducationalDetailsDBO.getDocumentsDBOSet())) {
			empEducationalDetailsDBO.getDocumentsDBOSet().forEach(doc -> {
				if (!empEducationalDetailsDocumentsIdsSet.contains(doc.getId())) {
					if (!Utils.isNullOrEmpty(doc.getEducationalDocumentsUrlDBO())) {
						UrlAccessLinkDBO urlAccessLinkDBO = doc.getEducationalDocumentsUrlDBO();
						if (!Utils.isNullOrEmpty(urlAccessLinkDBO)) {
							urlAccessLinkDBO.setRecordStatus('D');
							urlAccessLinkDBO.setModifiedUsersId(userId);
							doc.setEducationalDocumentsUrlDBO(urlAccessLinkDBO);
						}
					}
					doc.setRecordStatus('D');
					doc.setModifiedUsersId(userId);
					empEducationalDetailsDocumentsDBOSet.add(doc);
				}
			});

		}
		empEducationalDetailsDBO.setDocumentsDBOSet(empEducationalDetailsDocumentsDBOSet);
	}

	public void qualificationUploadSetting(EmpProfileEdnDetailsDocumentsDTO documentsDTO, EmpEducationalDetailsDocumentsDBO empEducationalDetailsDocumentsDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO docUrlDBO = null;
		if (Utils.isNullOrEmpty(empEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())) {
			if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile() &&
					!Utils.isNullOrEmpty(documentsDTO.getDocument().getOriginalFileName())) {
				docUrlDBO = new UrlAccessLinkDBO();
				docUrlDBO.setCreatedUsersId(userId);
				docUrlDBO.setRecordStatus('A');
			}
		} else {
			docUrlDBO = empEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
			docUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile()) {
			docUrlDBO = aWSS3FileStorageService.createURLAccessLinkDBO(docUrlDBO, documentsDTO.getDocument().getProcessCode(), documentsDTO.getDocument().getUniqueFileName(), documentsDTO.getDocument().getOriginalFileName(), userId);
		}
		if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getProcessCode()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getUniqueFileName()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(documentsDTO.getDocument().getProcessCode(), documentsDTO.getDocument().getUniqueFileName()));
		}
		empEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(docUrlDBO);
	}

	public void setContractDetailsDBO(EmpEmploymentTabDTO empEmploymentTabDTO, EmpDBO empDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList, String jobCategoryCode) {
		Map<Integer, EmpGuestContractDetailsDBO> savedEmpGuestContractDetailsDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpGuestContractDetailsDBOSet())
				? empDBO.getEmpGuestContractDetailsDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(EmpGuestContractDetailsDBO::getId, dep -> dep)) : new HashMap<Integer, EmpGuestContractDetailsDBO>();
		Set<EmpGuestContractDetailsDBO> empGuestContractDetailsDBOSet = null;
		List<EmpGuestContractDetailsDBO> empGuestContractDetailsDBOList = null;
		if (!Utils.isNullOrEmpty(empDBO.getEmpGuestContractDetailsDBOSet())) {
			empGuestContractDetailsDBOSet = empDBO.getEmpGuestContractDetailsDBOSet();
			empGuestContractDetailsDBOList = empGuestContractDetailsDBOSet.stream()
					.filter(contract -> contract.getIsCurrent() != null && contract.getIsCurrent()).collect(Collectors.toList());
		}
		Set<EmpGuestContractDetailsDBO> empEmpGuestContractDetailsDBOSet = new HashSet<EmpGuestContractDetailsDBO>();
		Set<Integer> empEmpGuestContractDetailsIdsSet = new HashSet<>();
		AtomicBoolean newRec = new AtomicBoolean(false);
		if (!Utils.isNullOrEmpty(empEmploymentTabDTO.getGuestContractList())) {
			empEmploymentTabDTO.getGuestContractList().forEach(contractDetailsDTO -> {
				EmpGuestContractDetailsDBO empGuestContractDetailsDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpGuestContractDetailsDBOMap) && savedEmpGuestContractDetailsDBOMap.containsKey(contractDetailsDTO.getId())) {
					empGuestContractDetailsDBO = savedEmpGuestContractDetailsDBOMap.get(contractDetailsDTO.getId());
					empGuestContractDetailsDBO.setModifiedUsersId(userId);
					empEmpGuestContractDetailsIdsSet.add(empGuestContractDetailsDBO.getId());
				} else {
					empGuestContractDetailsDBO = new EmpGuestContractDetailsDBO();
					empGuestContractDetailsDBO.setCreatedUsersId(userId);
					empGuestContractDetailsDBO.setRecordStatus('A');
					if (!jobCategoryCode.equals("GUEST")) {
						empGuestContractDetailsDBO.setIsCurrent(true);
					}
					newRec.set(true);
				}
				empGuestContractDetailsDBO.setEmpDBO(empDBO);
				empGuestContractDetailsDBO.setContractEmpLetterNo(contractDetailsDTO.getContractEmpLetterNo());
				empGuestContractDetailsDBO.setContractEmpEndDate(contractDetailsDTO.getContractEmpEndDate());
				empGuestContractDetailsDBO.setContractEmpStartDate(contractDetailsDTO.getContractEmpStartDate());

				ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO = null;
				if (!Utils.isNullOrEmpty(contractDetailsDTO.getDepartment()) && !Utils.isNullOrEmpty(contractDetailsDTO.getDepartment().getValue())) {
					erpCampusDepartmentMappingDBO = new ErpCampusDepartmentMappingDBO();
					erpCampusDepartmentMappingDBO.setId(Integer.parseInt(contractDetailsDTO.getDepartment().getValue()));
				}
				empGuestContractDetailsDBO.setErpCampusDepartmentMappingDBO(erpCampusDepartmentMappingDBO);
				if (jobCategoryCode.equals("GUEST")) {
					empGuestContractDetailsDBO.setIsCurrent(contractDetailsDTO.getIsCurrent());
				}
				empGuestContractDetailsDBO.setGuestContractRemarks(contractDetailsDTO.getRemarks());
				empGuestContractDetailsDBO.setGuestReferredBy(contractDetailsDTO.getGuestRefferedBy());
				empGuestContractDetailsDBO.setGuestSubjectSpecialization(contractDetailsDTO.getSpecialisation());
				empGuestContractDetailsDBO.setGuestTutoringSemester(contractDetailsDTO.getSemester());
				empGuestContractDetailsDBO.setGuestWorkingHoursWeek(contractDetailsDTO.getGuestWorkingHoursWeek());
				if (!Utils.isNullOrEmpty(contractDetailsDTO.getPayType())) {
					empGuestContractDetailsDBO.setPayScaleType(contractDetailsDTO.getPayType().getValue());
				}
				empGuestContractDetailsDBO.setPayAmount(contractDetailsDTO.getPayAmount());
				contractDocumentUploadSetting(contractDetailsDTO, empGuestContractDetailsDBO, userId, uniqueFileNameList);//upload setting
				empEmpGuestContractDetailsDBOSet.add(empGuestContractDetailsDBO);
			});
			if (newRec.get()) {
				if (!jobCategoryCode.equals("GUEST")) {
					if (!Utils.isNullOrEmpty(empGuestContractDetailsDBOList)) {
						EmpGuestContractDetailsDBO empGuestContractDetailsDBO = empGuestContractDetailsDBOList.get(0);
						empGuestContractDetailsDBO.setIsCurrent(false);
					}
				}
			}
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empDBO.getEmpGuestContractDetailsDBOSet())) {
			empDBO.getEmpGuestContractDetailsDBOSet().forEach(empGuestContractDetailsDBO -> {
				if (!empEmpGuestContractDetailsIdsSet.contains(empGuestContractDetailsDBO.getId())) {
					if (!Utils.isNullOrEmpty(empGuestContractDetailsDBO.getContractEmpDocumentUrlDBO())) {
						UrlAccessLinkDBO urlAccessLinkDBO = empGuestContractDetailsDBO.getContractEmpDocumentUrlDBO();
						if (!Utils.isNullOrEmpty(urlAccessLinkDBO)) {
							urlAccessLinkDBO.setRecordStatus('D');
							urlAccessLinkDBO.setModifiedUsersId(userId);
							empGuestContractDetailsDBO.setContractEmpDocumentUrlDBO(urlAccessLinkDBO);
						}
					}
					empGuestContractDetailsDBO.setRecordStatus('D');
					empGuestContractDetailsDBO.setModifiedUsersId(userId);
					empEmpGuestContractDetailsDBOSet.add(empGuestContractDetailsDBO);
				}
			});
		}
		if (!Utils.isNullOrEmpty(empEmpGuestContractDetailsDBOSet)) {
			empDBO.setEmpGuestContractDetailsDBOSet(empEmpGuestContractDetailsDBOSet);
		} else {
			empDBO.setEmpGuestContractDetailsDBOSet(null);
		}
	}

	public void setEligibilityTestDBO(EmpDBO empDBO, EmpQualificationTabDTO epQualificationTabDTO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		Map<Integer, EmpEligibilityTestDBO> savedEmpEligibilityTestDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpEligibilityTestDBOSet())
				? empDBO.getEmpEligibilityTestDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(elg -> elg.getEmpEligibilityTestId(), edu -> edu)) : new HashMap<Integer, EmpEligibilityTestDBO>();
		Set<EmpEligibilityTestDBO> empEligibilityTestDBOSet = new HashSet<EmpEligibilityTestDBO>();
		Set<Integer> empEligibilityTestDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(epQualificationTabDTO.getEmpEligibilityTestDTOList())) {
			epQualificationTabDTO.getEmpEligibilityTestDTOList().forEach(eligibilityTestDTO -> {
				EmpEligibilityTestDBO empEligibilityTestDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpEligibilityTestDBOMap) && savedEmpEligibilityTestDBOMap.containsKey(eligibilityTestDTO.getId())) {
					empEligibilityTestDBO = savedEmpEligibilityTestDBOMap.get(eligibilityTestDTO.getId());
					empEligibilityTestDBO.setModifiedUsersId(userId);
					empEligibilityTestDBOIdsSet.add(empEligibilityTestDBO.getEmpEligibilityTestId());
				} else {
					empEligibilityTestDBO = new EmpEligibilityTestDBO();
					empEligibilityTestDBO.setRecordStatus('A');
					empEligibilityTestDBO.setCreatedUsersId(userId);
				}
				empEligibilityTestDBO.setEmpDBO(empDBO);
				empEligibilityTestDBO.setEmpDBO(empDBO);
				empEligibilityTestDBO.setEmpDBO(empDBO);
				empEligibilityTestDBO.setTestYear(eligibilityTestDTO.getTestYear());
				empEligibilityTestDBO.setEmpEligibilityExamListDBO(null);
				if (!Utils.isNullOrEmpty(eligibilityTestDTO.getExam()) && !Utils.isNullOrEmpty(eligibilityTestDTO.getExam().getValue())) {
					EmpEligibilityExamListDBO empEligibilityExamListDBO = new EmpEligibilityExamListDBO();
					empEligibilityExamListDBO.setEmpEligibilityExamListId(Integer.parseInt(eligibilityTestDTO.getExam().getValue()));
					empEligibilityTestDBO.setEmpEligibilityExamListDBO(empEligibilityExamListDBO);
				}

				updateEligibilityTestDocuments(empEligibilityTestDBO, eligibilityTestDTO, userId, uniqueFileNameList); //update document table
				empEligibilityTestDBOSet.add(empEligibilityTestDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empDBO.getEmpEligibilityTestDBOSet())) {
			empDBO.getEmpEligibilityTestDBOSet().forEach(elig -> {
				if (!empEligibilityTestDBOIdsSet.contains(elig.getEmpEligibilityTestId())) {
					elig.setRecordStatus('D');
					elig.setModifiedUsersId(userId);
					empEligibilityTestDBOSet.add(elig);
				}
			});

		}
		empDBO.setEmpEligibilityTestDBOSet(empEligibilityTestDBOSet);
	}

	public void updateEligibilityTestDocuments(EmpEligibilityTestDBO empEligibilityTestDBO, EmpEligibilityTestDTO empEligibilityTestDTO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		Map<Integer, EmpEligibilityTestDocumentDBO> savedEmpEligibilityTestDocumentDBOMap = !Utils.isNullOrEmpty(empEligibilityTestDBO.getEmpEligibilityTestDocumentDBOSet())
				? empEligibilityTestDBO.getEmpEligibilityTestDocumentDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(doc -> doc.getId(), edu -> edu)) : new HashMap<Integer, EmpEligibilityTestDocumentDBO>();
		Set<EmpEligibilityTestDocumentDBO> empEligibilityTestDocumentDBOSet = new HashSet<EmpEligibilityTestDocumentDBO>();
		Set<Integer> empEligibilityTestDocumentIdSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empEligibilityTestDTO.getEmpEligibilityTestDocumentDTOList())) {
			empEligibilityTestDTO.getEmpEligibilityTestDocumentDTOList().forEach(documentsDTO -> {
				EmpEligibilityTestDocumentDBO empEligibilityTestDocumentDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpEligibilityTestDocumentDBOMap) && savedEmpEligibilityTestDocumentDBOMap.containsKey(documentsDTO.getId())) {
					empEligibilityTestDocumentDBO = savedEmpEligibilityTestDocumentDBOMap.get(documentsDTO.getId());
					empEligibilityTestDocumentDBO.setModifiedUsersId(userId);
					empEligibilityTestDocumentIdSet.add(empEligibilityTestDocumentDBO.getId());
				} else {
					empEligibilityTestDocumentDBO = new EmpEligibilityTestDocumentDBO();
					empEligibilityTestDocumentDBO.setRecordStatus('A');
					empEligibilityTestDocumentDBO.setCreatedUsersId(userId);
				}
				empEligibilityTestDocumentDBO.setEmpEligibilityTestDBO(empEligibilityTestDBO);
				eligibilityTestUploadSetting(documentsDTO, empEligibilityTestDocumentDBO, userId, uniqueFileNameList);//upload setting
				empEligibilityTestDocumentDBOSet.add(empEligibilityTestDocumentDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empEligibilityTestDBO.getEmpEligibilityTestDocumentDBOSet())) {
			empEligibilityTestDBO.getEmpEligibilityTestDocumentDBOSet().forEach(doc -> {
				if (!empEligibilityTestDocumentIdSet.contains(doc.getId())) {
					if (!Utils.isNullOrEmpty(doc.getEligibilityDocumentUrlDBO())) {
						UrlAccessLinkDBO urlAccessLinkDBO = doc.getEligibilityDocumentUrlDBO();
						if (!Utils.isNullOrEmpty(urlAccessLinkDBO)) {
							urlAccessLinkDBO.setRecordStatus('D');
							urlAccessLinkDBO.setModifiedUsersId(userId);
							doc.setEligibilityDocumentUrlDBO(urlAccessLinkDBO);
						}
					}
					doc.setRecordStatus('D');
					doc.setModifiedUsersId(userId);
					empEligibilityTestDocumentDBOSet.add(doc);
				}
			});

		}
		empEligibilityTestDBO.setEmpEligibilityTestDocumentDBOSet(empEligibilityTestDocumentDBOSet);
	}

	public void eligibilityTestUploadSetting(EmpEligibilityTestDocumentDTO documentsDTO, EmpEligibilityTestDocumentDBO empEligibilityTestDocumentDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO docUrlDBO = null;
		if (Utils.isNullOrEmpty(empEligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO())) {
			if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile() &&
					!Utils.isNullOrEmpty(documentsDTO.getDocument().getOriginalFileName())) {
				docUrlDBO = new UrlAccessLinkDBO();
				docUrlDBO.setCreatedUsersId(userId);
				docUrlDBO.setRecordStatus('A');
			}
		} else {
			docUrlDBO = empEligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO();
			docUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile()) {
			docUrlDBO = aWSS3FileStorageService.createURLAccessLinkDBO(docUrlDBO, documentsDTO.getDocument().getProcessCode(), documentsDTO.getDocument().getUniqueFileName(), documentsDTO.getDocument().getOriginalFileName(), userId);
		}
		if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getProcessCode()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getUniqueFileName()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile()) {
			uniqueFileNameList.addAll(aWSS3FileStorageService.createFileListForActualCopy(documentsDTO.getDocument().getProcessCode(), documentsDTO.getDocument().getUniqueFileName()));
		}
		empEligibilityTestDocumentDBO.setEligibilityDocumentUrlDBO(docUrlDBO);
	}

	public void contractDocumentUploadSetting(EmpProfileGuestContractDetailsDTO empProfileGuestContractDetailsDTO, EmpGuestContractDetailsDBO empGuestContractDetailsDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO documentUrlDBO = null;
		if (Utils.isNullOrEmpty(empGuestContractDetailsDBO.getContractEmpDocumentUrlDBO())) {
			if (!Utils.isNullOrEmpty(empProfileGuestContractDetailsDTO.getDocument()) && !Utils.isNullOrEmpty(empProfileGuestContractDetailsDTO.getDocument().getNewFile()) && empProfileGuestContractDetailsDTO.getDocument().getNewFile() &&
					!Utils.isNullOrEmpty(empProfileGuestContractDetailsDTO.getDocument().getOriginalFileName())) {
				documentUrlDBO = new UrlAccessLinkDBO();
				documentUrlDBO.setCreatedUsersId(userId);
				documentUrlDBO.setRecordStatus('A');
			}
		} else {
			documentUrlDBO = empGuestContractDetailsDBO.getContractEmpDocumentUrlDBO();
			if (Utils.isNullOrEmpty(empProfileGuestContractDetailsDTO.getDocument())) {
				documentUrlDBO.setRecordStatus('D');
			}
			documentUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(empProfileGuestContractDetailsDTO.getDocument()) && empProfileGuestContractDetailsDTO.getDocument().getNewFile()) {
			documentUrlDBO = aWSS3FileStorageService.createURLAccessLinkDBO(documentUrlDBO, empProfileGuestContractDetailsDTO.getDocument().getProcessCode(), empProfileGuestContractDetailsDTO.getDocument().getUniqueFileName(), empProfileGuestContractDetailsDTO.getDocument().getOriginalFileName(), userId);
		}
		if (!Utils.isNullOrEmpty(empProfileGuestContractDetailsDTO.getDocument()) && !Utils.isNullOrEmpty(empProfileGuestContractDetailsDTO.getDocument().getProcessCode()) && !Utils.isNullOrEmpty(empProfileGuestContractDetailsDTO.getDocument().getUniqueFileName()) && empProfileGuestContractDetailsDTO.getDocument().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(empProfileGuestContractDetailsDTO.getDocument().getProcessCode(), empProfileGuestContractDetailsDTO.getDocument().getUniqueFileName()));
		}
		empGuestContractDetailsDBO.setContractEmpDocumentUrlDBO(documentUrlDBO);
	}

	public void updateSalaryDetailsDTOList(List<SalaryDetailsDTO> salaryDetailsDTOList, Map<Integer, List<PayScaleDetailsDTO>> empPayScaleDetailsMap, Map<Integer, List<PayScaleDetailsDTO>> payScaleComponentMap) {
		salaryDetailsDTOList.forEach(salDetails -> {
			if (!salDetails.getPayScaleType().getValue().equals("SCALE PAY")) {
				return;
			}
			PayScaleDetailsDTO matrixDTO = employeeProfileTransaction.getMatrixDetailsForAmount(Integer.parseInt(salDetails.getCell().getValue()));
			AtomicReference<BigDecimal> basicAmt = new AtomicReference<BigDecimal>();
			if (!Utils.isNullOrEmpty(matrixDTO)) {
				basicAmt.set(matrixDTO.getComponentValue());
			}
			List<PayScaleDetailsDTO> payScaleDetList = empPayScaleDetailsMap.get(salDetails.getId());
			Map<Integer, List<PayScaleDetailsDTO>> componentMap = new HashMap<Integer, List<PayScaleDetailsDTO>>(payScaleComponentMap);
			if (!Utils.isNullOrEmpty(payScaleDetList)) {
				payScaleDetList.forEach(det -> {
					if (!Utils.isNullOrEmpty(det.getComponent())) {
						componentMap.remove(Integer.parseInt(det.getComponent().getValue()));
					}
				});
				salDetails.setPayScaleDetailsDTOList(payScaleDetList);
			}
			if (!Utils.isNullOrEmpty(componentMap)) {
				componentMap.forEach((compId, dtoList) -> {
					PayScaleDetailsDTO payScaleDetailsDTO = new PayScaleDetailsDTO();
					dtoList.forEach(c -> {
						if (!Utils.isNullOrEmpty(c.getComponent())) {
							SelectDTO compDTO = new SelectDTO();
							compDTO.setLabel(c.getComponent().getLabel());
							compDTO.setValue(c.getComponent().getValue());
							payScaleDetailsDTO.setComponent(compDTO);
							payScaleDetailsDTO.setDisplayOrder(c.getDisplayOrder());
							payScaleDetailsDTO.setPayScaleType(c.getPayScaleType());
							payScaleDetailsDTO.setIsCalculationTypePercentage(c.getIsCalculationTypePercentage());
							payScaleDetailsDTO.setPercentage(c.getPercentage());
							payScaleDetailsDTO.setIsPayScaleBasic(c.getIsPayScaleBasic());
							if (c.getIsPayScaleBasic() && !Utils.isNullOrEmpty(basicAmt)) {
								payScaleDetailsDTO.setComponentValue(basicAmt.get());
							}
							if (!Utils.isNullOrEmpty(c.getIsCalculationTypePercentage()) && !Utils.isNullOrEmpty(c.getPercentage()) && !Utils.isNullOrEmpty(basicAmt)) {
								BigDecimal result = basicAmt.get().multiply(c.getPercentage().divide(new BigDecimal("100.0"), 2, RoundingMode.HALF_EVEN));
								payScaleDetailsDTO.setComponentValue(result.setScale(2, RoundingMode.HALF_EVEN));
							}
							if (!Utils.isNullOrEmpty(salDetails.getPayScaleDetailsDTOList())) {
								salDetails.getPayScaleDetailsDTOList().add(payScaleDetailsDTO);
							} else {
								salDetails.setPayScaleDetailsDTOList(new ArrayList<>(Collections.singletonList(payScaleDetailsDTO)));
							}
						}
					});

				});
			}
			if (!Utils.isNullOrEmpty(salDetails.getPayScaleDetailsDTOList())) {
				BigDecimal total = salDetails.getPayScaleDetailsDTOList().stream()
						.map(PayScaleDetailsDTO::getComponentValue)
						.filter(componentValue -> !Utils.isNullOrEmpty(componentValue))
						.reduce(BigDecimal.ZERO, BigDecimal::add);
				salDetails.setGrossPay(total);
				Comparator<PayScaleDetailsDTO> orderComparator = Comparator.comparing(PayScaleDetailsDTO::getDisplayOrder);
				salDetails.getPayScaleDetailsDTOList().sort(orderComparator);
			}
		});
	}

	public void updatePayScaleDTOList(List<PayScaleDetailsDTO> payScaleComponentDTOList, PayScaleDetailsDTO matrixDTO) {
		AtomicReference<BigDecimal> basicAmt = new AtomicReference<BigDecimal>();
		if (!Utils.isNullOrEmpty(matrixDTO)) {
			basicAmt.set(matrixDTO.getComponentValue());
		}
		if (!Utils.isNullOrEmpty(payScaleComponentDTOList)) {
			payScaleComponentDTOList.forEach(componentDTO -> {
				if (componentDTO.getIsPayScaleBasic() && !Utils.isNullOrEmpty(basicAmt.get())) {
					componentDTO.setComponentValue(basicAmt.get());
				}
				if (!Utils.isNullOrEmpty(componentDTO.getIsCalculationTypePercentage()) && !Utils.isNullOrEmpty(componentDTO.getPercentage()) && !Utils.isNullOrEmpty(basicAmt.get())) {
					BigDecimal result = basicAmt.get().multiply(componentDTO.getPercentage().divide(new BigDecimal("100.0"), 2, RoundingMode.HALF_EVEN));
					componentDTO.setComponentValue(result.setScale(2, RoundingMode.HALF_EVEN));
				}
			});
		}
		;
	}
	public void setDBOSalaryTab(EmpDBO empDBO, EmpSalaryTabDTO empSalaryTabDTO, Integer userId, Boolean salaryPrivilegeEnabled) {
		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		setSalaryTabDBO(empDBO, empSalaryTabDTO.getSalaryDetailsDTOList(), userId, uniqueFileNameList, salaryPrivilegeEnabled);
		if(!Utils.isNullOrEmpty(empSalaryTabDTO.getEmpPFandGratuityDTO())) {
			setPfAndGratuityDetailsDBO(empDBO, userId, empSalaryTabDTO.getEmpPFandGratuityDTO());
		}
		//epQualificationTabDTO.setUniqueFileNameList(uniqueFileNameList);
	}

	public void setSalaryTabDBO(EmpDBO empDBO, List<SalaryDetailsDTO> salaryDetailsDTOList, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList, Boolean salaryPrivilegeEnabled) {
		Map<Integer, EmpPayScaleDetailsDBO> savedPayScaleDetailsDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpPayScaleDetailsDBOSet())
				? empDBO.getEmpPayScaleDetailsDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(EmpPayScaleDetailsDBO::getId, empPayScaleDetails -> empPayScaleDetails)) : new HashMap<Integer, EmpPayScaleDetailsDBO>();
		Set<EmpPayScaleDetailsDBO> empPayScaleDetailsDBOSet = new HashSet<EmpPayScaleDetailsDBO>();
		Set<Integer> empPayScaleDetailsDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(salaryDetailsDTOList)) {
			salaryDetailsDTOList.forEach(salaryDetailsDTO -> {
				EmpPayScaleDetailsDBO empPayScaleDetailsDBO = null;
				if (!Utils.isNullOrEmpty(savedPayScaleDetailsDBOMap) && savedPayScaleDetailsDBOMap.containsKey(salaryDetailsDTO.getId())) {
					empPayScaleDetailsDBO = savedPayScaleDetailsDBOMap.get(salaryDetailsDTO.getId());
					empPayScaleDetailsDBO.setModifiedUsersId(userId);
					empPayScaleDetailsDBOIdsSet.add(empPayScaleDetailsDBO.getId());
				} else {
					empPayScaleDetailsDBO = new EmpPayScaleDetailsDBO();
					empPayScaleDetailsDBO.setRecordStatus('A');
					empPayScaleDetailsDBO.setCreatedUsersId(userId);
					empPayScaleDetailsDBO.setCurrent(true);
				}
				EmpPayScaleMatrixDetailDBO empPayScaleMatrixDetailDBO = null;
				if (!Utils.isNullOrEmpty(salaryDetailsDTO.getCell()) && !Utils.isNullOrEmpty(salaryDetailsDTO.getCell().getValue())) {
					empPayScaleMatrixDetailDBO = new EmpPayScaleMatrixDetailDBO();
					empPayScaleMatrixDetailDBO.setId(Integer.parseInt(salaryDetailsDTO.getCell().getValue()));
					empPayScaleDetailsDBO.setEmpPayScaleMatrixDetailDBO(empPayScaleMatrixDetailDBO);
				}
				empPayScaleDetailsDBO.setEmpDBO(empDBO);
				empPayScaleDetailsDBO.setWageRatePerType(salaryDetailsDTO.getWageAmount());
				empPayScaleDetailsDBO.setGrossPay(salaryDetailsDTO.getGrossPay());
				empPayScaleDetailsDBO.setPayScaleType(salaryDetailsDTO.getPayScaleType().getValue());
				Set<Integer> empPayComponentDBOIdsSet = new HashSet<>();

				Map<Integer, EmpPayScaleDetailsComponentsDBO> savedComponentDBOMap = !Utils.isNullOrEmpty(empPayScaleDetailsDBO.getEmpPayScaleDetailsComponentsDBOs())
						? empPayScaleDetailsDBO.getEmpPayScaleDetailsComponentsDBOs().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(EmpPayScaleDetailsComponentsDBO::getId, compDetails -> compDetails)) : new HashMap<Integer, EmpPayScaleDetailsComponentsDBO>();
				Set<EmpPayScaleDetailsComponentsDBO> epPayScaleDetailsComponentsDBOSet = new HashSet<EmpPayScaleDetailsComponentsDBO>();
				EmpPayScaleDetailsDBO finalEmpPayScaleDetailsDBO = empPayScaleDetailsDBO;
				if(!Utils.isNullOrEmpty(salaryDetailsDTO.getPayScaleDetailsDTOList())) {
					salaryDetailsDTO.getPayScaleDetailsDTOList().forEach(det -> {
						EmpPayScaleDetailsComponentsDBO empPayScaleDetailsComponentsDBO = null;
						if (!Utils.isNullOrEmpty(savedComponentDBOMap) && savedComponentDBOMap.containsKey(det.getId())) {
							empPayScaleDetailsComponentsDBO = savedComponentDBOMap.get(det.getId());
							empPayScaleDetailsComponentsDBO.setModifiedUsersId(userId);
							empPayComponentDBOIdsSet.add(empPayScaleDetailsComponentsDBO.getId());
						} else {
							empPayScaleDetailsComponentsDBO = new EmpPayScaleDetailsComponentsDBO();
							empPayScaleDetailsComponentsDBO.setRecordStatus('A');
							empPayScaleDetailsComponentsDBO.setCreatedUsersId(userId);
						}
						EmpPayScaleComponentsDBO empPayScaleComponentsDBO = null;
						if (!Utils.isNullOrEmpty(det.getComponent()) && !Utils.isNullOrEmpty(det.getComponent().getValue())) {
							empPayScaleComponentsDBO = new EmpPayScaleComponentsDBO();
							empPayScaleComponentsDBO.setId(Integer.parseInt(det.getComponent().getValue()));
						}
						empPayScaleDetailsComponentsDBO.setEmpPayScaleComponentsDBO(empPayScaleComponentsDBO);
						empPayScaleDetailsComponentsDBO.setEmpSalaryComponentValue(det.getComponentValue());
						empPayScaleDetailsComponentsDBO.setEmpPayScaleDetailsDBO(finalEmpPayScaleDetailsDBO);
						epPayScaleDetailsComponentsDBOSet.add(empPayScaleDetailsComponentsDBO);
					});
				}
				if (!Utils.isNullOrEmpty(empPayScaleDetailsDBO.getEmpPayScaleDetailsComponentsDBOs())) {
					empPayScaleDetailsDBO.getEmpPayScaleDetailsComponentsDBOs().forEach(comDel -> {
						if (!empPayComponentDBOIdsSet.contains(comDel.getId())) {
							if (salaryPrivilegeEnabled) {
								comDel.setRecordStatus('D');
								comDel.setModifiedUsersId(userId);
								epPayScaleDetailsComponentsDBOSet.add(comDel);
							}
						}
					});
				}
				empPayScaleDetailsDBO.setEmpPayScaleDetailsComponentsDBOs(epPayScaleDetailsComponentsDBOSet);
				//}
				empPayScaleDetailsDBOSet.add(empPayScaleDetailsDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empDBO.getEmpPayScaleDetailsDBOSet())) {
			empDBO.getEmpPayScaleDetailsDBOSet().forEach(payScaleDel -> {
				if (!empPayScaleDetailsDBOIdsSet.contains(payScaleDel.getId())) {
					if (salaryPrivilegeEnabled) {
						payScaleDel.setRecordStatus('D');
						payScaleDel.setModifiedUsersId(userId);
						empPayScaleDetailsDBOSet.add(payScaleDel);
					}
				}
			});
		}
		empDBO.setEmpPayScaleDetailsDBOSet(empPayScaleDetailsDBOSet);
	}

	public void updateEligibilityTestDTOList(List<EmpEligibilityTestDTO> empEligibilityTestDTOList, Map<Integer, List<EmpEligibilityTestDocumentDTO>> empEligibilityTestDocumentDTOMap) {
		empEligibilityTestDTOList.forEach(eligibilityTestDTO -> {
			if (!Utils.isNullOrEmpty(empEligibilityTestDocumentDTOMap)) {
				List<EmpEligibilityTestDocumentDTO> documentList = empEligibilityTestDocumentDTOMap.get(eligibilityTestDTO.getId());
				if (!Utils.isNullOrEmpty(documentList)) {
					eligibilityTestDTO.setEmpEligibilityTestDocumentDTOList(documentList);
				}
			}
		});
	}
	public void setPfAndGratuityDetailsDBO(EmpDBO empDBO, Integer userId, EmpPFandGratuityDTO empPFandGratuityDTO) {
		EmpJobDetailsDBO empJobDetailsDBO = null;
		if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO())) {
			empJobDetailsDBO = empDBO.getEmpJobDetailsDBO();
		} else {
			empJobDetailsDBO = new EmpJobDetailsDBO();
		}
		empJobDetailsDBO.setIsWithGratuity(empPFandGratuityDTO.getIsWithGratuity());
		empJobDetailsDBO.setGratuityNo(empPFandGratuityDTO.getLicGratuityNo());
		empJobDetailsDBO.setGratuityDate(empPFandGratuityDTO.getLicGratuityDate());
		empJobDetailsDBO.setUanNo(empPFandGratuityDTO.getUanNo());
		empJobDetailsDBO.setIsWithPf(empPFandGratuityDTO.getIsWithPF());
		empJobDetailsDBO.setPfAccountNo(empPFandGratuityDTO.getPfAccountNo());
		empJobDetailsDBO.setPfDate(empPFandGratuityDTO.getPfDate());
		empJobDetailsDBO.setIsEsiApplicable(empPFandGratuityDTO.getIsEsiApplicable());
		empJobDetailsDBO.setEsiInsuranceNo(empPFandGratuityDTO.getEsiInsuranceNo());
		empJobDetailsDBO.setIsSibAccountAvailable(empPFandGratuityDTO.getIsBankAccountAvailable());
		empJobDetailsDBO.setSibAccountBank(empJobDetailsDBO.getSibAccountBank());
		empJobDetailsDBO.setBranchIfscCode(empJobDetailsDBO.getBranchIfscCode());
		empJobDetailsDBO.setIsUanNoAvailable(empJobDetailsDBO.getIsUanNoAvailable());
		if(!Utils.isNullOrEmpty(empPFandGratuityDTO.getEmpPfGratuityNomineesDTOList())){
			setNomineeDetailsDBO(empJobDetailsDBO, empPFandGratuityDTO.getEmpPfGratuityNomineesDTOList(), userId);
		}
	}
	public void setNomineeDetailsDBO(EmpJobDetailsDBO empJobDetailsDBO, List<EmpProfilePFandGratuityDTO> empPfGratuityNomineesDTOList, Integer userId) {
		Map<Integer, EmpPfGratuityNomineesDBO> savedEmpPfGratuityNomineesDBOMap = !Utils.isNullOrEmpty(empJobDetailsDBO.getEmpPfGratuityNomineesDBOS())
				? empJobDetailsDBO.getEmpPfGratuityNomineesDBOS().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(nom -> nom.getEmpPfGratuityNomineesId(), rem -> rem)) : new HashMap<Integer, EmpPfGratuityNomineesDBO>();
		Set<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOSet = new HashSet<EmpPfGratuityNomineesDBO>();
		Set<Integer> empRemarksDetailsDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTOList)) {
			empPfGratuityNomineesDTOList.forEach(pFandGratuityDTO -> {
				EmpPfGratuityNomineesDBO empPfGratuityNomineesDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpPfGratuityNomineesDBOMap) && savedEmpPfGratuityNomineesDBOMap.containsKey(pFandGratuityDTO.getEmpPfGratuityNomineesId())) {
					empPfGratuityNomineesDBO = savedEmpPfGratuityNomineesDBOMap.get(pFandGratuityDTO.getEmpPfGratuityNomineesId());
					empPfGratuityNomineesDBO.setModifiedUsersId(userId);
					empRemarksDetailsDBOIdsSet.add(empPfGratuityNomineesDBO.getEmpPfGratuityNomineesId());
				} else {
					empPfGratuityNomineesDBO = new EmpPfGratuityNomineesDBO();
					empPfGratuityNomineesDBO.setRecordStatus('A');
					empPfGratuityNomineesDBO.setCreatedUsersId(userId);
				}
				empPfGratuityNomineesDBO.setEmpJobDetailsDBO(empJobDetailsDBO);
				empPfGratuityNomineesDBO.setIsGratuity(pFandGratuityDTO.getIsGratuity());
				empPfGratuityNomineesDBO.setIsPf(pFandGratuityDTO.getIsPf());
				empPfGratuityNomineesDBO.setNominee(pFandGratuityDTO.getNomineeName());
				empPfGratuityNomineesDBO.setNomineeAddress(pFandGratuityDTO.getNomineeAddress());
				empPfGratuityNomineesDBO.setNomineeDob(pFandGratuityDTO.getNomineeDob());
				empPfGratuityNomineesDBO.setNomineeRelationship(pFandGratuityDTO.getNomineeRelationship());
				empPfGratuityNomineesDBO.setRecordStatus('A');
				if (!Utils.isNullOrEmpty(pFandGratuityDTO.getSharePercentage())) {
					empPfGratuityNomineesDBO.setSharePercentage(new BigDecimal(pFandGratuityDTO.getSharePercentage()));
				} else {
					empPfGratuityNomineesDBO.setSharePercentage(null);
				}
				empPfGratuityNomineesDBO.setUnder18GuardianAddress(pFandGratuityDTO.getUnder18GuardianAddress());
				empPfGratuityNomineesDBO.setUnder18GuardName(pFandGratuityDTO.getUnder18GuardName());
				empPfGratuityNomineesDBOSet.add(empPfGratuityNomineesDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empJobDetailsDBO.getEmpPfGratuityNomineesDBOS())) {
			empJobDetailsDBO.getEmpPfGratuityNomineesDBOS().forEach(empProfilePFandGratuityDTODel -> {
				if (!empRemarksDetailsDBOIdsSet.contains(empProfilePFandGratuityDTODel.getEmpPfGratuityNomineesId())) {
					empProfilePFandGratuityDTODel.setRecordStatus('D');
					empProfilePFandGratuityDTODel.setModifiedUsersId(userId);
					empPfGratuityNomineesDBOSet.add(empProfilePFandGratuityDTODel);
				}
			});
		}
		empJobDetailsDBO.setEmpPfGratuityNomineesDBOS(empPfGratuityNomineesDBOSet);
	}
	public void updateEmpExperienceDTOList(List<EmpProfileWorkExperienceDTO> empProfileWorkExperienceDTOList, Map<Integer, List<EmpProfileWorkExpDocDTO>> empProfileEdnDetailsDocumentsDTOMap) {
		empProfileWorkExperienceDTOList.forEach(expDetails -> {
			if (!Utils.isNullOrEmpty(empProfileEdnDetailsDocumentsDTOMap)) {
				List<EmpProfileWorkExpDocDTO> documentList = empProfileEdnDetailsDocumentsDTOMap.get(expDetails.getId());
				if (!Utils.isNullOrEmpty(documentList)) {
					expDetails.setEmpProfileWorkExpDocDTOList(documentList);
				}
			}
		});
	}
	public void setDBOForExperienceTab(EmpDBO empDBO, EmpWorkExperienceTabDTO empWorkExperienceTabDTO, Integer userId) {
		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		if(!Utils.isNullOrEmpty(empWorkExperienceTabDTO)){
			setExperienceDBO(empDBO,empWorkExperienceTabDTO.getEmpProfileWorkExperienceDTOList(), userId, uniqueFileNameList);
			setMajorAchievementsDBO(empDBO, empWorkExperienceTabDTO.getEmpMajorAchievementsDTOList(), userId, uniqueFileNameList);
		}
		empWorkExperienceTabDTO.setUniqueFileNameList(uniqueFileNameList);
	}
	public void setExperienceDBO(EmpDBO empDBO, List<EmpProfileWorkExperienceDTO> empProfileWorkExperienceDTOList, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		Map<Integer, EmpWorkExperienceDBO> savedEmpWorkExperienceDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpWorkExperienceDBOSet())
				? empDBO.getEmpWorkExperienceDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(wrk -> wrk.getEmpWorkExperienceId(), edu -> edu)) : new HashMap<Integer, EmpWorkExperienceDBO>();
		Set<EmpWorkExperienceDBO> empWorkExperienceDBOSet = new HashSet<EmpWorkExperienceDBO>();
		Set<Integer> empWorkExperienceDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empProfileWorkExperienceDTOList)) {
			empProfileWorkExperienceDTOList.forEach(workExperienceDTO -> {
				EmpWorkExperienceDBO empWorkExperienceDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpWorkExperienceDBOMap) && savedEmpWorkExperienceDBOMap.containsKey(workExperienceDTO.getId())) {
					empWorkExperienceDBO = savedEmpWorkExperienceDBOMap.get(workExperienceDTO.getId());
					empWorkExperienceDBO.setModifiedUsersId(userId);
					empWorkExperienceDBOIdsSet.add(empWorkExperienceDBO.getEmpWorkExperienceId());
				} else {
					empWorkExperienceDBO = new EmpWorkExperienceDBO();
					empWorkExperienceDBO.setRecordStatus('A');
					empWorkExperienceDBO.setCreatedUsersId(userId);
				}
				empWorkExperienceDBO.setEmpDBO(empDBO);
				empWorkExperienceDBO.setEmpApplnSubjectCategoryDBO(null);
				if (!Utils.isNullOrEmpty(workExperienceDTO.getSubjectCategory()) && !Utils.isNullOrEmpty(workExperienceDTO.getSubjectCategory().getValue())) {
					EmpApplnSubjectCategoryDBO empApplnSubjectCategoryDBO = new EmpApplnSubjectCategoryDBO();
					empApplnSubjectCategoryDBO.setId(Integer.parseInt(workExperienceDTO.getSubjectCategory().getValue()));
					empWorkExperienceDBO.setEmpApplnSubjectCategoryDBO(empApplnSubjectCategoryDBO);
				}
				empWorkExperienceDBO.setWorkExperienceFromDate(workExperienceDTO.getWorkExperienceFromDate());
				empWorkExperienceDBO.setWorkExperienceToDate(workExperienceDTO.getWorkExperienceToDate());

				empWorkExperienceDBO.setEmpApplnWorkExperienceTypeDBO(null);
				if (!Utils.isNullOrEmpty(workExperienceDTO.getWorkExperienceType()) && !Utils.isNullOrEmpty(workExperienceDTO.getWorkExperienceType().getValue())) {
					EmpApplnWorkExperienceTypeDBO empApplnWorkExperienceTypeDBO = new EmpApplnWorkExperienceTypeDBO();
					empApplnWorkExperienceTypeDBO.setEmpApplnWorkExperienceTypeId(Integer.parseInt(workExperienceDTO.getWorkExperienceType().getValue()));
					empWorkExperienceDBO.setEmpApplnWorkExperienceTypeDBO(empApplnWorkExperienceTypeDBO);
				}
				empWorkExperienceDBO.setEmpDesignation(workExperienceDTO.getDesignation());
				empWorkExperienceDBO.setWorkExperienceYears(workExperienceDTO.getWorkExperienceYears());
				empWorkExperienceDBO.setWorkExperienceMonth(workExperienceDTO.getWorkExperienceMonth());
				empWorkExperienceDBO.setIsPartTime(workExperienceDTO.getIsPartTime());
				empWorkExperienceDBO.setInstitution(workExperienceDTO.getInstitution());
				empWorkExperienceDBO.setEmpDesignation(workExperienceDTO.getDesignation());

				updateWorkExperienceDocuments(empWorkExperienceDBO, workExperienceDTO.getEmpProfileWorkExpDocDTOList(), userId, uniqueFileNameList); //update document table
				empWorkExperienceDBOSet.add(empWorkExperienceDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empDBO.getEmpWorkExperienceDBOSet())) {
			empDBO.getEmpWorkExperienceDBOSet().forEach(wrk -> {
				if (!empWorkExperienceDBOIdsSet.contains(wrk.getEmpWorkExperienceId())) {
					wrk.setRecordStatus('D');
					wrk.setModifiedUsersId(userId);
					empWorkExperienceDBOSet.add(wrk);
				}
			});

		}
		empDBO.setEmpWorkExperienceDBOSet(empWorkExperienceDBOSet);
	}
	public void updateWorkExperienceDocuments(EmpWorkExperienceDBO empWorkExperienceDBO, List<EmpProfileWorkExpDocDTO> empProfileWorkExpDocDTOList, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		Map<Integer, EmpWorkExperienceDocumentDBO> savedEmpWorkExperienceDocumentDBOMap = !Utils.isNullOrEmpty(empWorkExperienceDBO.getWorkExperienceDocumentsDBOSet())
				? empWorkExperienceDBO.getWorkExperienceDocumentsDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(exp -> exp.getId(), edu -> edu)) : new HashMap<Integer, EmpWorkExperienceDocumentDBO>();
		Set<EmpWorkExperienceDocumentDBO> empWorkExperienceDocumentDBOSet = new HashSet<EmpWorkExperienceDocumentDBO>();
		Set<Integer> empWorkExperienceDocumentDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empProfileWorkExpDocDTOList)) {
			empProfileWorkExpDocDTOList.forEach(documentsDTO -> {
				EmpWorkExperienceDocumentDBO empWorkExperienceDocumentDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpWorkExperienceDocumentDBOMap) && savedEmpWorkExperienceDocumentDBOMap.containsKey(documentsDTO.getId())) {
					empWorkExperienceDocumentDBO = savedEmpWorkExperienceDocumentDBOMap.get(documentsDTO.getId());
					empWorkExperienceDocumentDBO.setModifiedUsersId(userId);
					empWorkExperienceDocumentDBOIdsSet.add(empWorkExperienceDocumentDBO.getId());
				} else {
					empWorkExperienceDocumentDBO = new EmpWorkExperienceDocumentDBO();
					empWorkExperienceDocumentDBO.setRecordStatus('A');
					empWorkExperienceDocumentDBO.setCreatedUsersId(userId);
				}
				empWorkExperienceDocumentDBO.setEmpWorkExperienceDBO(empWorkExperienceDBO);
				experienceUploadSetting(documentsDTO, empWorkExperienceDocumentDBO, userId, uniqueFileNameList);//upload setting
				empWorkExperienceDocumentDBOSet.add(empWorkExperienceDocumentDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empWorkExperienceDBO.getWorkExperienceDocumentsDBOSet())) {
			empWorkExperienceDBO.getWorkExperienceDocumentsDBOSet().forEach(doc -> {
				if (!empWorkExperienceDocumentDBOIdsSet.contains(doc.getId())) {
					if (!Utils.isNullOrEmpty(doc.getExperienceDocumentsUrlDBO())) {
						UrlAccessLinkDBO urlAccessLinkDBO = doc.getExperienceDocumentsUrlDBO();
						if (!Utils.isNullOrEmpty(urlAccessLinkDBO)) {
							urlAccessLinkDBO.setRecordStatus('D');
							urlAccessLinkDBO.setModifiedUsersId(userId);
							doc.setExperienceDocumentsUrlDBO(urlAccessLinkDBO);
						}
					}
					doc.setRecordStatus('D');
					doc.setModifiedUsersId(userId);
					empWorkExperienceDocumentDBOSet.add(doc);
				}
			});

		}
		empWorkExperienceDBO.setWorkExperienceDocumentsDBOSet(empWorkExperienceDocumentDBOSet);
	}
	public void experienceUploadSetting(EmpProfileWorkExpDocDTO documentsDTO, EmpWorkExperienceDocumentDBO empWorkExperienceDocumentDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		UrlAccessLinkDBO docUrlDBO = null;
		if (Utils.isNullOrEmpty(empWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
			if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile() &&
					!Utils.isNullOrEmpty(documentsDTO.getDocument().getOriginalFileName())) {
				docUrlDBO = new UrlAccessLinkDBO();
				docUrlDBO.setCreatedUsersId(userId);
				docUrlDBO.setRecordStatus('A');
			}
		} else {
			docUrlDBO = empWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO();
			docUrlDBO.setModifiedUsersId(userId);
		}
		if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile()) {
			docUrlDBO = aWSS3FileStorageService.createURLAccessLinkDBO(docUrlDBO, documentsDTO.getDocument().getProcessCode(), documentsDTO.getDocument().getUniqueFileName(), documentsDTO.getDocument().getOriginalFileName(), userId);
		}
		if (!Utils.isNullOrEmpty(documentsDTO.getDocument()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getProcessCode()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getUniqueFileName()) && !Utils.isNullOrEmpty(documentsDTO.getDocument().getNewFile()) && documentsDTO.getDocument().getNewFile()) {
			uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(documentsDTO.getDocument().getProcessCode(), documentsDTO.getDocument().getUniqueFileName()));
		}
		empWorkExperienceDocumentDBO.setExperienceDocumentsUrlDBO(docUrlDBO);
	}
	public void setMajorAchievementsDBO(EmpDBO empDBO, List<EmpProfileMajorAchievementsDTO> empProfileMajorAchievementsDTOList, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
		Map<Integer, EmpMajorAchievementsDBO> savedEmpMajorAchievementsDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpMajorAchievementsDBOSet())
				? empDBO.getEmpMajorAchievementsDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(majorAchievementsDBO -> majorAchievementsDBO.getId(), edu -> edu)) : new HashMap<Integer, EmpMajorAchievementsDBO>();
		Set<EmpMajorAchievementsDBO> empMajorAchievementsDBOSet = new HashSet<EmpMajorAchievementsDBO>();
		Set<Integer> empMajorAchievementsDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empProfileMajorAchievementsDTOList)) {
			empProfileMajorAchievementsDTOList.forEach(majorAchievementsDTO -> {
				EmpMajorAchievementsDBO majorAchievementsDBO = null;
				if (!Utils.isNullOrEmpty(savedEmpMajorAchievementsDBOMap) && savedEmpMajorAchievementsDBOMap.containsKey(majorAchievementsDTO.getId())) {
					majorAchievementsDBO = savedEmpMajorAchievementsDBOMap.get(majorAchievementsDTO.getId());
					majorAchievementsDBO.setModifiedUsersId(userId);
					empMajorAchievementsDBOIdsSet.add(majorAchievementsDBO.getId());
				} else {
					majorAchievementsDBO = new EmpMajorAchievementsDBO();
					majorAchievementsDBO.setRecordStatus('A');
					majorAchievementsDBO.setCreatedUsersId(userId);
				}
				majorAchievementsDBO.setEmpDBO(empDBO);
				majorAchievementsDBO.setAchievements(majorAchievementsDTO.getAchievements());
				empMajorAchievementsDBOSet.add(majorAchievementsDBO);
			});
		}
		//removing the deleted record from db
		if (!Utils.isNullOrEmpty(empDBO.getEmpMajorAchievementsDBOSet())) {
			empDBO.getEmpMajorAchievementsDBOSet().forEach(major -> {
				if (!empMajorAchievementsDBOIdsSet.contains(major.getId())) {
					major.setRecordStatus('D');
					major.setModifiedUsersId(userId);
					empMajorAchievementsDBOSet.add(major);
				}
			});
 		}
		empDBO.setEmpMajorAchievementsDBOSet(empMajorAchievementsDBOSet);
	}
	public void calculateNoOfLeaveByMonth(List<EmpProfileLeaveAllotmentDTO> empProfileLeaveAllotmentDTOList, LocalDate doj) {
		Integer joiningMonth = doj.getMonthValue();
		empProfileLeaveAllotmentDTOList.forEach(allotment->{
			if(Utils.isNullOrEmpty(allotment.getLeavesAllotted()) || Utils.isNullOrEmpty(allotment.getLeaveInitializationMonth()) ||
				Utils.isNullOrEmpty(joiningMonth)){
				allotment.setLeavesAllotted(new BigDecimal(0));
				return;
			}
			BigDecimal leavesPerMonth = allotment.getLeavesAllotted().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
			Integer leaveInitializationMonth = allotment.getLeaveInitializationMonth();
			Integer monthsRemaining = 0;
			if (joiningMonth < leaveInitializationMonth) {
				monthsRemaining = leaveInitializationMonth - joiningMonth;
			} else {
				monthsRemaining = 12 - (joiningMonth - leaveInitializationMonth);
			}
			BigDecimal leavesRemaining = leavesPerMonth.multiply(BigDecimal.valueOf(monthsRemaining));
			BigDecimal roundedLeaves = BigDecimal.valueOf(leavesRemaining.doubleValue()).setScale(0, RoundingMode.HALF_UP);
			allotment.setLeavesAllotted(roundedLeaves);
			allotment.setLeavesRemaining(roundedLeaves);
			if(!Utils.isNullOrEmpty(leaveInitializationMonth)) {
				allotment.setMonth(Month.of(leaveInitializationMonth).toString());
			}
			Month targetMonth = Month.of(leaveInitializationMonth);
			int joiningYear = doj.getYear();
			YearMonth targetYearMonth = YearMonth.of(joiningYear, targetMonth);
			if (targetYearMonth.isAfter(YearMonth.from(doj))) {
				allotment.setYear(joiningYear - 1);
			} else {
				allotment.setYear(joiningYear);
			}
		});
	}
	public void setLeaveAllotmentDBO(EmpDBO empDBO, List<EmpProfileLeaveAllotmentDTO> empProfileLeaveAllotmentDTOList, Integer userId) {
		Map<Integer, EmpLeaveAllocationDBO> empLeaveCategoryAllotmentDBOMap = !Utils.isNullOrEmpty(empDBO.getEmpLeaveAllocationDBOSet())
				? empDBO.getEmpLeaveAllocationDBOSet().stream().filter(r -> r.getRecordStatus() == 'A').collect(Collectors.toMap(al -> al.getId(), al -> al)) : new HashMap<Integer, EmpLeaveAllocationDBO>();
		Set<EmpLeaveAllocationDBO> empLeaveCategoryAllotmentDBOSet = new HashSet<EmpLeaveAllocationDBO>();
		//Set<Integer> empRemarksDetailsDBOIdsSet = new HashSet<>();
		if (!Utils.isNullOrEmpty(empProfileLeaveAllotmentDTOList)) {
			empProfileLeaveAllotmentDTOList.forEach(leaveAllotmentDTO -> {
				EmpLeaveAllocationDBO empLeaveAllocationDBO = null;
				if (!Utils.isNullOrEmpty(empLeaveCategoryAllotmentDBOMap) && empLeaveCategoryAllotmentDBOMap.containsKey(leaveAllotmentDTO.getLeaveAllotmentId())) {
					empLeaveAllocationDBO = empLeaveCategoryAllotmentDBOMap.get(leaveAllotmentDTO.getLeaveAllotmentId());
					empLeaveAllocationDBO.setModifiedUsersId(userId);
				} else {
					empLeaveAllocationDBO = new EmpLeaveAllocationDBO();
					empLeaveAllocationDBO.setRecordStatus('A');
					empLeaveAllocationDBO.setCreatedUsersId(userId);
				}
				empLeaveAllocationDBO.setEmpDBO(empDBO);
				empLeaveAllocationDBO.setAllottedLeaves(leaveAllotmentDTO.getLeavesAllotted());
				empLeaveAllocationDBO.setSanctionedLeaves(leaveAllotmentDTO.getLeavesSanctioned());
				empLeaveAllocationDBO.setLeavesRemaining(leaveAllotmentDTO.getLeavesRemaining());
				empLeaveAllocationDBO.setLeavesPending(leaveAllotmentDTO.getLeavesPending());
				empLeaveAllocationDBO.setYear(leaveAllotmentDTO.getYear());
				empLeaveAllocationDBO.setMonth(leaveAllotmentDTO.getMonth());
				empLeaveAllocationDBO.setLeaveType(null);
				if (!Utils.isNullOrEmpty(leaveAllotmentDTO.getLeaveType()) && !Utils.isNullOrEmpty(leaveAllotmentDTO.getLeaveType().getValue())) {
					EmpLeaveTypeDBO empLeaveTypeDBO = new EmpLeaveTypeDBO();
					empLeaveTypeDBO.setId(Integer.parseInt(leaveAllotmentDTO.getLeaveType().getValue()));
					empLeaveAllocationDBO.setLeaveType(empLeaveTypeDBO);
				}
				empLeaveCategoryAllotmentDBOSet.add(empLeaveAllocationDBO);
			});
		}
		empDBO.setEmpLeaveAllocationDBOSet(empLeaveCategoryAllotmentDBOSet);
	}

}