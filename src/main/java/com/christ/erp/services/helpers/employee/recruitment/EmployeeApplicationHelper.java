package com.christ.erp.services.helpers.employee.recruitment;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.*;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.MenuScreenDTO;
import com.christ.erp.services.dto.common.NestedSelectDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.*;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.recruitment.EmployeeApplicationTransaction;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({ "rawtypes"})
@Service
public class EmployeeApplicationHelper {

	private static volatile EmployeeApplicationHelper employeeApplicationHelper = null;

    public static EmployeeApplicationHelper getInstance() {
        if(employeeApplicationHelper==null) {
        	employeeApplicationHelper = new EmployeeApplicationHelper();
        }
        return employeeApplicationHelper;
    }
    @Autowired
	EmployeeApplicationTransaction employeeApplicationTransaction;


	public boolean validateApplicantData(EmployeeApplicantDTO employeeApplicantDTO) throws Exception{
		boolean isValid = true;
		try {
			JobDetailsDTO jobDetailDTO = employeeApplicantDTO.jobDetailDTO;
			EmpApplnPersonalDataDTO empApplnPersonalDataDTO = employeeApplicantDTO.empApplnPersonalDataDTO;
			EmpApplnPersonalDataDTO addressDetailDTO = employeeApplicantDTO.addressDetailDTO;
			EducationalDetailsDTO educationalDetailDTO = employeeApplicantDTO.educationalDetailDTO;
			ProfessionalExperienceDTO professionalExperienceDTO = employeeApplicantDTO.professionalExperienceDTO;
			//Personal Details
			if(!Utils.isNullOrEmpty(empApplnPersonalDataDTO)){
				if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.applicantName))
					isValid= false;
				if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.genderId)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.dateOfBirth)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.mobileNo) || Utils.isNullOrEmpty(empApplnPersonalDataDTO.mobileNoCountryCode)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.maritalStatusId)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.nationalityId)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.religionId)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.reservationCategoryId)) {
					isValid= false;
				}
			}
			//Job Details
			if(!Utils.isNullOrEmpty(jobDetailDTO)){
				if(Utils.isNullOrEmpty(jobDetailDTO.postAppliedFor)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(jobDetailDTO.empApplnSubjectCategoryDTO)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(jobDetailDTO.subjectCategoryIds)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(jobDetailDTO.preferredLocationIds)) {
					isValid= false;
				}
			}
			//Address Details
			if(!Utils.isNullOrEmpty(addressDetailDTO)){
				if(Utils.isNullOrEmpty(addressDetailDTO.currentAddressLine1)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(addressDetailDTO.currentCountryId)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(addressDetailDTO.currentStateId)) {
					boolean isValidState = true;
					if(Utils.isNullOrEmpty(addressDetailDTO.currentStateOthers)) {
						isValidState= false;
					}
					if(isValid){
						if(!isValidState)
							isValid = false;
					}
				}
				if(Utils.isNullOrEmpty(addressDetailDTO.currentCityId)) {
					boolean isValidCity = true;
					if(Utils.isNullOrEmpty(addressDetailDTO.currentCityOthers)) {
						isValidCity= false;
					}
					if(isValid){
						if(!isValidCity)
							isValid = false;
					}
				}
				if(Utils.isNullOrEmpty(addressDetailDTO.currentPincode)) {
					isValid= false;
				}
				if("No".equalsIgnoreCase(addressDetailDTO.isPermanentEqualsCurrent)) {
					if(Utils.isNullOrEmpty(addressDetailDTO.permanentAddressLine1)) {
						isValid= false;
					}
					if(Utils.isNullOrEmpty(addressDetailDTO.permanentCountryId)) {
						isValid= false;
					}
					if(Utils.isNullOrEmpty(addressDetailDTO.permanentStateId)) {
						boolean isValidState = true;
						if(Utils.isNullOrEmpty(addressDetailDTO.permanentStateOthers)) {
							isValidState= false;
						}
						if(isValid){
							if(!isValidState)
								isValid = false;
						}
					}
					if(Utils.isNullOrEmpty(addressDetailDTO.permanentCityId)) {
						boolean isValidCity = true;
						if(Utils.isNullOrEmpty(addressDetailDTO.permanentCityOthers)) {
							isValidCity= false;
						}
						if(isValid){
							if(!isValidCity)
								isValid = false;
						}
					}
					if(Utils.isNullOrEmpty(addressDetailDTO.permanentPincode)) {
						isValid= false;
					}
				}
			}
			//Educational Details
//			if(!Utils.isNullOrEmpty(educationalDetailDTO)){
//				if(Utils.isNullOrEmpty(educationalDetailDTO.highestQualificationLevelId)) {
//					isValid= false;
//				}else if(!Utils.isNullOrEmpty(educationalDetailDTO.qualificationLevelsList)) {
//					int count = 0;
//					for(EmpApplnEducationalDetailsDTO empApplnEducationalDetailsDTO : educationalDetailDTO.qualificationLevelsList) {
//						if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.qualificationLevelId)) {
//							count++;
//							if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.course))
//								isValid= false;
//							/*if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.specialization))
//								isValid= false;*/
//							if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.yearOfCompletion))
//								isValid= false;
//							if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.gradeOrPercentage))
//								isValid= false;
//							if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.institute) && Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getErpInstitute()))
//								isValid= false;
//							if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.boardOrUniversity) && Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getErpBoardOrUniversity()))
//								isValid= false;
//							if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getCountryId())) {
//								isValid= false;
//							}
//							if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getStateId()) && Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getStateOther())) {
//								isValid= false;
//							}
//							/*if(Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.currentStatus))
//								isValid= false;*/
//						}
//					}
//					if(count == 0){
//						isValid = false;
//					}
//				}
//			}
			//Professional Experience
//			if(!Utils.isNullOrEmpty(professionalExperienceDTO)){
//				if(Utils.isNullOrEmpty(professionalExperienceDTO.expectedSalary)) {
//					isValid= false;
//				}
//				if(Utils.isNullOrEmpty(professionalExperienceDTO.isCurrentlyWorking)) {
//					isValid= false;
//				}else {
//					if("Yes".equalsIgnoreCase(professionalExperienceDTO.isCurrentlyWorking)) {
//						EmpApplnWorkExperienceDTO currentExperience = professionalExperienceDTO.currentExperience;
//						if(!Utils.isNullOrEmpty(currentExperience)){
//							if(Utils.isNullOrEmpty(currentExperience.workExperienceTypeId)) {
//								isValid= false;
//							}
//							if(Utils.isNullOrEmpty(currentExperience.functionalAreaId)) {
//								boolean isValidFunctionalArea = true;
//								if(Utils.isNullOrEmpty(currentExperience.functionalAreaOthers)) {
//									isValidFunctionalArea= false;
//								}
//								if(isValid){
//									if(!isValidFunctionalArea)
//										isValid = false;
//								}
//							}
//							if(Utils.isNullOrEmpty(currentExperience.employmentType))
//								isValid= false;
//							if(Utils.isNullOrEmpty(currentExperience.designation))
//								isValid= false;
//							if(Utils.isNullOrEmpty(currentExperience.getFromDate())) {
//								isValid= false;
//							}
//							if(Utils.isNullOrEmpty(currentExperience.getToDate())) {
//								isValid= false;
//							}
//							if(Utils.isNullOrEmpty(currentExperience.years))
//								isValid= false;
//							if(Utils.isNullOrEmpty(currentExperience.months))
//								isValid= false;
//							if(Utils.isNullOrEmpty(currentExperience.noticePeriod))
//								isValid= false;
//							if(Utils.isNullOrEmpty(currentExperience.currentSalary))
//								isValid= false;
//							if(Utils.isNullOrEmpty(currentExperience.institution))
//								isValid= false;
//						}else{
//							isValid= false;
//						}
//					}
//				}
//			}
			/*if(Utils.isNullOrEmpty(researchDetailDTO.isResearchExperience)) {
				isValid= false;
			}else if("Yes".equalsIgnoreCase(researchDetailDTO.isResearchExperience)) {
				if(Utils.isNullOrEmpty(researchDetailDTO.inflibnetVidwanNo)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(researchDetailDTO.scopusId)) {
					isValid= false;
				}
				if(Utils.isNullOrEmpty(researchDetailDTO.hIndex)) {
					isValid= false;
				}
			}
			if(Utils.isNullOrEmpty(researchDetailDTO.isInterviewedBefore)) {
				isValid= false;
			}else if("Yes".equalsIgnoreCase(researchDetailDTO.isInterviewedBefore)) {
				if(Utils.isNullOrEmpty(researchDetailDTO.interviewedBeforeDepartment))
					isValid= false;
				if(Utils.isNullOrEmpty(researchDetailDTO.interviewedBeforeYear))
					isValid= false;
				if(Utils.isNullOrEmpty(researchDetailDTO.interviewedBeforeApplicationNo))
					isValid= false;
				if(Utils.isNullOrEmpty(researchDetailDTO.interviewedBeforeSubject))
					isValid= false;
			}*/
//			if(Utils.isNullOrEmpty(researchDetailDTO.vacancyInformationId)) {
//				isValid= false;
//			}
//			if(Utils.isNullOrEmpty(researchDetailDTO.aboutVacancyOthers)) {
//				isValid= false;
//			}
//			if(Utils.isNullOrEmpty(researchDetailDTO.otherInformation)) {
//				isValid= false;
//			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return isValid;
	}

	public List<EmpApplnAddtnlInfoHeadingDTO> getResearchHeadingDTOs(
			List<EmpApplnAddtnlInfoHeadingDBO> researchEntriesHeadings, Map<Integer, EmpApplnAddtnlInfoEntriesDBO> parameterEntriesMap, boolean prevApplicationDataFlag) throws Exception{
		List<EmpApplnAddtnlInfoHeadingDTO> addtnlInfoHeadingDTOsList = new ArrayList<>();
		if(!Utils.isNullOrEmpty(researchEntriesHeadings)) {
			for(EmpApplnAddtnlInfoHeadingDBO dbo : researchEntriesHeadings) {
				if(dbo.recordStatus == 'A') {
					EmpApplnAddtnlInfoHeadingDTO headingDTO = new EmpApplnAddtnlInfoHeadingDTO();
					getResearchHeadingParameterDetails(dbo,headingDTO,parameterEntriesMap,prevApplicationDataFlag);
					addtnlInfoHeadingDTOsList.add(headingDTO);
				}
			}
			if(!Utils.isNullOrEmpty(addtnlInfoHeadingDTOsList)){
				addtnlInfoHeadingDTOsList.sort((o1,o2) -> {
					int comp = 0;
					if(!Utils.isNullOrEmpty(o1.displayOrder) && !Utils.isNullOrEmpty(o2.displayOrder)){
						comp = o1.displayOrder.compareTo(o2.displayOrder);
					}
					return comp;
				});
			}
			//addtnlInfoHeadingDTOsList.sort(Comparator.comparing(o -> o.displayOrder));
		}
		return addtnlInfoHeadingDTOsList;
	}

	public void getResearchHeadingParameterDetails(EmpApplnAddtnlInfoHeadingDBO dbo, EmpApplnAddtnlInfoHeadingDTO headingDTO,
			Map<Integer, EmpApplnAddtnlInfoEntriesDBO> parameterEntriesMap, boolean prevApplicationDataFlag) throws Exception{
		if(!Utils.isNullOrEmpty(dbo.id)) {
			headingDTO.id = String.valueOf(dbo.id);
		}
		if(!Utils.isNullOrEmpty(dbo.addtnlInfoHeadingName)) {
			headingDTO.groupHeading = dbo.addtnlInfoHeadingName;
		}
		if(!Utils.isNullOrEmpty(dbo.headingDisplayOrder)) {
			headingDTO.displayOrder = String.valueOf(dbo.headingDisplayOrder);
		}
		headingDTO.isTypeResearch = dbo.isTypeResearch;
		if(!Utils.isNullOrEmpty(dbo.empEmployeeCategoryId) && !Utils.isNullOrEmpty(dbo.empEmployeeCategoryId.id)) {
			headingDTO.category = new ExModelBaseDTO();
			headingDTO.category.id = String.valueOf(dbo.empEmployeeCategoryId.id);
		}
		if(!Utils.isNullOrEmpty(dbo.empApplnAddtnlInfoParameterMap)) {
			List<EmpApplnAddtnlInfoParameterDTO> parameterDTOsList = new ArrayList<>();
			for(EmpApplnAddtnlInfoParameterDBO parameterDBO : dbo.empApplnAddtnlInfoParameterMap) {
				if(!Utils.isNullOrEmpty(parameterDBO.isDisplayInApplication) && parameterDBO.isDisplayInApplication) {
					if(parameterEntriesMap.containsKey(parameterDBO.id)) {
						EmpApplnAddtnlInfoEntriesDBO applnAddtnlInfoEntriesDBO = parameterEntriesMap.get(parameterDBO.id);
						EmpApplnAddtnlInfoParameterDTO parameterDTO = new EmpApplnAddtnlInfoParameterDTO();
						if(!Utils.isNullOrEmpty(parameterDBO.id)) {
							parameterDTO.id = String.valueOf(parameterDBO.id);
						}
						if(!Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoEntriesId) && !prevApplicationDataFlag) {
							parameterDTO.empApplnAddtnlInfoEntriesId = String.valueOf(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoEntriesId);
						}
						if(!Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnEntriesDBO) && !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnEntriesDBO.id) && !prevApplicationDataFlag) {
							parameterDTO.empApplnEntriesId = String.valueOf(applnAddtnlInfoEntriesDBO.empApplnEntriesDBO.id);
						}
						if(!Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.addtnlInfoValue)) {
							parameterDTO.parameterValue = applnAddtnlInfoEntriesDBO.addtnlInfoValue;
						}
						parameterDTO.isDisplayInApplication = true;
						if(!Utils.isNullOrEmpty(parameterDBO.addtnlInfoParameterName)) {
							parameterDTO.researchParameter = parameterDBO.addtnlInfoParameterName;
						}
						if(!Utils.isNullOrEmpty(parameterDBO.parameterDisplayOrder)) {
							parameterDTO.displayOrder = String.valueOf(parameterDBO.parameterDisplayOrder);
						}
						parameterDTOsList.add(parameterDTO);
					}
				}
			}
			if(!Utils.isNullOrEmpty(parameterDTOsList)){
				parameterDTOsList.sort((o1,o2) -> {
					int comp = 0;
					if(!Utils.isNullOrEmpty(o1.displayOrder) && !Utils.isNullOrEmpty(o2.displayOrder)){
						comp = o1.displayOrder.compareTo(o2.displayOrder);
					}
					return comp;
				});
			}
			//parameterDTOsList.sort(Comparator.comparing(o -> o.displayOrder));
			headingDTO.parameters = parameterDTOsList;
		}
	}

	public Mono<ApiResult> uploadFiles(Flux<FilePart> data, String filePath, String[] fileTypeAccept, boolean isHashFileName, List<Tuple2<String, String>> hashedFineNamesList) throws Exception{
        Tika tika = new Tika();
        ApiResult result = new ApiResult();
        Map<String, String> hashedFileNameMap = new HashMap<>();
        return data.takeWhile(item -> {
			String fileName = item.filename().substring(item.filename().indexOf("_")+1);
            try {
    		    if(Utils.isNullOrEmpty(hashedFineNamesList) || (!Utils.isNullOrEmpty(hashedFineNamesList) && !checkDuplicateFileName(hashedFineNamesList,fileName))){
					byte[] md5CheckSumSalt = Utils.getMD5CheckSumSalt();
					String md5HashFileName = Utils.createMD5CheckSum(Utils.removeFileExtension(fileName),md5CheckSumSalt);
					md5HashFileName = md5HashFileName + Utils.getFileExtension(fileName);
					hashedFileNameMap.put(fileName, md5HashFileName);
					hashedFineNamesList.add(Tuples.of(fileName, md5HashFileName));
//					File file = new File(filePath+md5HashFileName);
					File file = new File(filePath+fileName);
					OutputStream out = new FileOutputStream(file);
					item.transferTo(file);
					out.close();
					String detectFileType = tika.detect(file);
					result.success = Arrays.stream(fileTypeAccept).anyMatch(detectFileType::contains);//----Improves performance if size of the array is less.
					//result.success = (Arrays.stream(fileTypeAccept).parallel().anyMatch(detectFileType::contains)) ? true : false;
				}
            } catch (Exception e) {
                result.success = false;
                result.failureMessage= e.getMessage();
            } finally {
                if(!result.success) {
                    data.map(item1 -> {
            			String fname = item1.filename().substring(item1.filename().indexOf("_")+1);
                    	if(!Utils.isNullOrEmpty(hashedFileNameMap) && hashedFileNameMap.containsKey(fname)) {
                    		fname = hashedFileNameMap.get(fname);
                            File file1 = new File(filePath + fname);
                            if(file1.exists()) {
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

    public boolean checkDuplicateFileName(List<Tuple2<String, String>> hashedFineNamesList, String fileName){
    	boolean isDuplicate = false;
		for(Tuple2<String, String> tuple : hashedFineNamesList){
			if(fileName.equalsIgnoreCase(tuple.getT1())){
				isDuplicate = true;
				break;
			}
		}
    	return isDuplicate;
	}

	public void submitApplicantProcessCodeAndLog(CommonApiTransaction commonApiTransaction, Tuple workFlowProcess, int userId, EmpApplnEntriesDBO empApplnEntriesDBO) throws Exception{
		if(!Utils.isNullOrEmpty(workFlowProcess)) {
			Integer workFlowProcessId = Integer.parseInt(String.valueOf(workFlowProcess.get("erp_work_flow_process_id")));
			if(workFlowProcess.get("applicant_status_display_text") != null && !Utils.isNullOrWhitespace(workFlowProcess.get("applicant_status_display_text").toString())) {
				empApplnEntriesDBO.applicantCurrentProcessStatus = new ErpWorkFlowProcessDBO();
				empApplnEntriesDBO.applicantCurrentProcessStatus.id = workFlowProcessId;
				empApplnEntriesDBO.applicantStatusTime = LocalDateTime.now();
			}
			if(workFlowProcess.get("application_status_display_text") != null && !Utils.isNullOrWhitespace(workFlowProcess.get("application_status_display_text").toString())) {
				empApplnEntriesDBO.applicationCurrentProcessStatus = new ErpWorkFlowProcessDBO();
				empApplnEntriesDBO.applicationCurrentProcessStatus.id = workFlowProcessId;
				empApplnEntriesDBO.applicationStatusTime = LocalDateTime.now();
			}
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
			erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO.id = workFlowProcessId;
			erpWorkFlowProcessStatusLogDBO.entryId = empApplnEntriesDBO.id;
			erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
			erpWorkFlowProcessStatusLogDBO.createdUsersId = userId;
			commonApiTransaction.saveErpWorkFlowProcessStatusLogDBO(erpWorkFlowProcessStatusLogDBO);
		}
	}

	public EmpFamilyDetailsAddtnlDTO convertFamilyDetailsAddtnlDBOToDTO(EmpFamilyDetailsAddtnlDBO empFamilyDetailsAddtnlDBO) throws Exception{
		EmpFamilyDetailsAddtnlDTO empFamilyDetailsAddtnlDTO = new EmpFamilyDetailsAddtnlDTO();
		if(!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.empFamilyDetailsAddtnlId)){
			empFamilyDetailsAddtnlDTO.empFamilyDetailsAddtnlId = String.valueOf(empFamilyDetailsAddtnlDBO.empFamilyDetailsAddtnlId);
		}
		if(!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.empApplnPersonalDataDBO) && !Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.empApplnPersonalDataDBO.empApplnPersonalDataId)){
			empFamilyDetailsAddtnlDTO.empApplnPersonalDataId = String.valueOf(empFamilyDetailsAddtnlDBO.empApplnPersonalDataDBO.empApplnPersonalDataId);
		}
		if(!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.relationship)){
			empFamilyDetailsAddtnlDTO.relationship = empFamilyDetailsAddtnlDBO.relationship;
		}
		if(!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.dependentName)){
			empFamilyDetailsAddtnlDTO.dependentName = empFamilyDetailsAddtnlDBO.dependentName;
		}
		if(!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.dependentDob)){
			empFamilyDetailsAddtnlDTO.dependentDob = Utils.convertLocalDateToStringDate(empFamilyDetailsAddtnlDBO.dependentDob);
		}
		if(!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.dependentQualification)){
			empFamilyDetailsAddtnlDTO.dependentQualification = empFamilyDetailsAddtnlDBO.dependentQualification;
		}
		if(!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.dependentProfession)){
			empFamilyDetailsAddtnlDTO.dependentProfession = empFamilyDetailsAddtnlDBO.dependentProfession;
		}
		if(!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.otherDependentRelationship)){
			empFamilyDetailsAddtnlDTO.otherDependentRelationship = empFamilyDetailsAddtnlDBO.otherDependentRelationship;
		}
		return empFamilyDetailsAddtnlDTO;
	}

	public EmployeeApplicantDTO getApplicantDetails(EmployeeApplicantDTO employeeApplicantDTO, EmpApplnEntriesDBO empApplnEntriesDBO, boolean prevApplicationDataFlag, EntityManager context) throws Exception{
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnRegistrationsDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnRegistrationsDBO.id)) {
			employeeApplicantDTO.empApplicationRegistrationId = String.valueOf(empApplnEntriesDBO.empApplnRegistrationsDBO.id);
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO())) {
			employeeApplicantDTO.setJobCategoryDTO(new SelectDTO());
			employeeApplicantDTO.getJobCategoryDTO().setValue(String.valueOf(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO().getId()));
			employeeApplicantDTO.getJobCategoryDTO().setLabel(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO().getEmployeeJobName());
		}
		//Personal Details
		EmpApplnPersonalDataDBO empApplnPersonalDataDBO = empApplnEntriesDBO.empApplnPersonalDataDBO;
		if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO) && empApplnPersonalDataDBO.recordStatus == 'A') {
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.empApplnPersonalDataId) && !prevApplicationDataFlag)
				employeeApplicantDTO.empApplnPersonalDataDTO.empApplnPersonalDataId = empApplnPersonalDataDBO.empApplnPersonalDataId;
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.empApplnEntriesDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.empApplnEntriesDBO.id) && !prevApplicationDataFlag)
				employeeApplicantDTO.empApplnPersonalDataDTO.empApplnEntriesId = empApplnPersonalDataDBO.empApplnEntriesDBO.id;
			if (!Utils.isNullOrEmpty(empApplnEntriesDBO.applicantName))
				employeeApplicantDTO.empApplnPersonalDataDTO.applicantName = empApplnEntriesDBO.applicantName;
			if (!Utils.isNullOrEmpty(empApplnEntriesDBO.erpGenderDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.erpGenderDBO.erpGenderId)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.genderId = String.valueOf(empApplnEntriesDBO.erpGenderDBO.erpGenderId);
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.fatherName)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.fatherName = empApplnPersonalDataDBO.fatherName;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.motherName)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.motherName = empApplnPersonalDataDBO.motherName;
			}
			if (!Utils.isNullOrEmpty(empApplnEntriesDBO.dob)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.dateOfBirth = Utils.convertLocalDateToStringDate(empApplnEntriesDBO.dob);
			}
			if (!Utils.isNullOrEmpty(empApplnEntriesDBO.personalEmailId)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.emailId = empApplnEntriesDBO.personalEmailId;
			}else if(!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnRegistrationsDBO.email)){
				employeeApplicantDTO.empApplnPersonalDataDTO.emailId = empApplnEntriesDBO.empApplnRegistrationsDBO.email;
			}
			if (!Utils.isNullOrEmpty(empApplnEntriesDBO.mobileNoCountryCode)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.mobileNoCountryCode = empApplnEntriesDBO.mobileNoCountryCode;
			}
			if (!Utils.isNullOrEmpty(empApplnEntriesDBO.mobileNo)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.mobileNo = empApplnEntriesDBO.mobileNo;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.alternateNo)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.alternateNo = empApplnPersonalDataDBO.alternateNo;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.aadharNo)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.aadharNo = empApplnPersonalDataDBO.aadharNo;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpMaritalStatusDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpMaritalStatusDBO.id)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.maritalStatusId = String.valueOf(empApplnPersonalDataDBO.erpMaritalStatusDBO.id);
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpCountryDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpCountryDBO.id)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.nationalityId = String.valueOf(empApplnPersonalDataDBO.erpCountryDBO.id);
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.passportNo)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.passportNo = empApplnPersonalDataDBO.passportNo;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpReligionDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpReligionDBO.id)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.religionId = String.valueOf(empApplnPersonalDataDBO.erpReligionDBO.id);
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.isMinority)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.isMinority = empApplnPersonalDataDBO.isMinority ? "Yes" : "No";
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpReservationCategoryDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpReservationCategoryDBO.id)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.reservationCategoryId = String.valueOf(empApplnPersonalDataDBO.erpReservationCategoryDBO.id);
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpBloodGroupDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpBloodGroupDBO.id)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.bloodGroupId = String.valueOf(empApplnPersonalDataDBO.erpBloodGroupDBO.id);
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.isDifferentlyAbled)) {
				if (empApplnPersonalDataDBO.isDifferentlyAbled) {
					employeeApplicantDTO.empApplnPersonalDataDTO.isDifferentlyAbled = "Yes";
					if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpDifferentlyAbledDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpDifferentlyAbledDBO.id)) {
						employeeApplicantDTO.empApplnPersonalDataDTO.differentlyAbledId = String.valueOf(empApplnPersonalDataDBO.erpDifferentlyAbledDBO.id);
					}
				} else {
					employeeApplicantDTO.empApplnPersonalDataDTO.isDifferentlyAbled = "No";
				}
			}
			/*
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.profilePhotoUrl)) {
				employeeApplicantDTO.empApplnPersonalDataDTO.profilePhotoUrl = empApplnPersonalDataDBO.profilePhotoUrl;
			}*/
			
			if(!Utils.isNullOrEmpty(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO()) && (empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getRecordStatus() == 'A') && !prevApplicationDataFlag) {
				employeeApplicantDTO.getEmpApplnPersonalDataDTO().setNewFile(false);
				employeeApplicantDTO.getEmpApplnPersonalDataDTO().setProcessCode(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
				employeeApplicantDTO.getEmpApplnPersonalDataDTO().setTempPath(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getTempFileNameUnique());
				employeeApplicantDTO.getEmpApplnPersonalDataDTO().setOriginalFileName(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getFileNameOriginal());
				if(!Utils.isNullOrEmpty(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getTempFileNameUnique())) {
					String uniqueFileName = getRemainingAfterLastSlash(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getTempFileNameUnique());
					employeeApplicantDTO.getEmpApplnPersonalDataDTO().setUniqueFileName(uniqueFileName);
				}
			}
			
			//Address Details
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.empApplnPersonalDataId) && !prevApplicationDataFlag)
				employeeApplicantDTO.addressDetailDTO.empApplnPersonalDataId = empApplnPersonalDataDBO.empApplnPersonalDataId;
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentAddressLine1)) {
				employeeApplicantDTO.addressDetailDTO.currentAddressLine1 = empApplnPersonalDataDBO.currentAddressLine1;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentAddressLine2)) {
				employeeApplicantDTO.addressDetailDTO.currentAddressLine2 = empApplnPersonalDataDBO.currentAddressLine2;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCountry) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCountry.id)) {
				employeeApplicantDTO.addressDetailDTO.currentCountryId = String.valueOf(empApplnPersonalDataDBO.currentCountry.id);
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentState) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentState.id)) {
				employeeApplicantDTO.addressDetailDTO.currentStateId = String.valueOf(empApplnPersonalDataDBO.currentState.id);
				employeeApplicantDTO.addressDetailDTO.currentStateOthers = null;
			} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentStateOthers)) {
				employeeApplicantDTO.addressDetailDTO.currentStateId = null;
				employeeApplicantDTO.addressDetailDTO.currentStateOthers = empApplnPersonalDataDBO.currentStateOthers;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCity) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCity.id)) {
				employeeApplicantDTO.addressDetailDTO.currentCityOthers = null;
				employeeApplicantDTO.addressDetailDTO.currentCityId = String.valueOf(empApplnPersonalDataDBO.currentCity.id);
			} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCityOthers)) {
				employeeApplicantDTO.addressDetailDTO.currentCityId = null;
				employeeApplicantDTO.addressDetailDTO.currentCityOthers = empApplnPersonalDataDBO.currentCityOthers;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentPincode)) {
				employeeApplicantDTO.addressDetailDTO.currentPincode = empApplnPersonalDataDBO.currentPincode;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.isPermanentEqualsCurrent)) {
				employeeApplicantDTO.addressDetailDTO.isPermanentEqualsCurrent = empApplnPersonalDataDBO.isPermanentEqualsCurrent ? "Yes" : "No";
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentAddressLine1)) {
				employeeApplicantDTO.addressDetailDTO.permanentAddressLine1 = empApplnPersonalDataDBO.permanentAddressLine1;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentAddressLine2)) {
				employeeApplicantDTO.addressDetailDTO.permanentAddressLine2 = empApplnPersonalDataDBO.permanentAddressLine2;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCountry) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCountry.id)) {
				employeeApplicantDTO.addressDetailDTO.permanentCountryId = String.valueOf(empApplnPersonalDataDBO.permanentCountry.id);
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentState) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentState.id)) {
				employeeApplicantDTO.addressDetailDTO.permanentStateOthers = null;
				employeeApplicantDTO.addressDetailDTO.permanentStateId = String.valueOf(empApplnPersonalDataDBO.permanentState.id);
			} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentStateOthers)) {
				employeeApplicantDTO.addressDetailDTO.permanentStateId = null;
				employeeApplicantDTO.addressDetailDTO.permanentStateOthers = empApplnPersonalDataDBO.permanentStateOthers;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCity) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCity.id)) {
				employeeApplicantDTO.addressDetailDTO.permanentCityOthers = null;
				employeeApplicantDTO.addressDetailDTO.permanentCityId = String.valueOf(empApplnPersonalDataDBO.permanentCity.id);
			} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCityOthers)) {
				employeeApplicantDTO.addressDetailDTO.permanentCityId = null;
				employeeApplicantDTO.addressDetailDTO.permanentCityOthers = empApplnPersonalDataDBO.permanentCityOthers;
			}
			if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentPincode)) {
				employeeApplicantDTO.addressDetailDTO.permanentPincode = empApplnPersonalDataDBO.permanentPincode;
			}
		}
		//Educational Details
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnEducationalDetailsDBOs)) {
			//qualification level list
			List<EmpApplnEducationalDetailsDTO> qualificationLevelList = new ArrayList<>();
			List<EmpApplnEducationalDetailsDTO> otherQualificationLevelList = new ArrayList<>();
			if (!Utils.isNullOrEmpty(empApplnEntriesDBO.highestQualificationLevel))
				employeeApplicantDTO.educationalDetailDTO.highestQualificationLevelId = String.valueOf(empApplnEntriesDBO.highestQualificationLevel);
			for (EmpApplnEducationalDetailsDBO empApplnEducationalDetailsDBO : empApplnEntriesDBO.empApplnEducationalDetailsDBOs) {
				if (empApplnEducationalDetailsDBO.recordStatus == 'A') {
					EmpApplnEducationalDetailsDTO applnEducationalDetailsDTO = new EmpApplnEducationalDetailsDTO();
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.empApplnEducationalDetailsId) && !prevApplicationDataFlag) {
						applnEducationalDetailsDTO.empApplnEducationalDetailsId = empApplnEducationalDetailsDBO.empApplnEducationalDetailsId;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.empApplnEntriesDBO) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.empApplnEntriesDBO.id) && !prevApplicationDataFlag) {
						applnEducationalDetailsDTO.empApplnEntriesId = empApplnEducationalDetailsDBO.empApplnEntriesDBO.id;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpQualificationLevelDBO) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpQualificationLevelDBO.id)) {
						applnEducationalDetailsDTO.qualificationLevelId = String.valueOf(empApplnEducationalDetailsDBO.erpQualificationLevelDBO.id);
					} else if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.qualificationOthers)) {
						applnEducationalDetailsDTO.qualificationOthers = empApplnEducationalDetailsDBO.qualificationOthers;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.currentStatus)) {
						applnEducationalDetailsDTO.currentStatus = empApplnEducationalDetailsDBO.currentStatus;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.course)) {
						applnEducationalDetailsDTO.course = empApplnEducationalDetailsDBO.course;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.specialization)) {
						applnEducationalDetailsDTO.specialization = empApplnEducationalDetailsDBO.specialization;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.yearOfCompletion)) {
						applnEducationalDetailsDTO.yearOfCompletion = String.valueOf(empApplnEducationalDetailsDBO.yearOfCompletion);
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.institute)) {
						applnEducationalDetailsDTO.institute = empApplnEducationalDetailsDBO.institute;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.boardOrUniversity)) {
						applnEducationalDetailsDTO.boardOrUniversity = empApplnEducationalDetailsDBO.boardOrUniversity;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.gradeOrPercentage)) {
						applnEducationalDetailsDTO.gradeOrPercentage = empApplnEducationalDetailsDBO.gradeOrPercentage;
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpCountryDBO())) {
						applnEducationalDetailsDTO.setCountryId(String.valueOf(empApplnEducationalDetailsDBO.getErpCountryDBO().getId()));
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpStateDBO())) {
						applnEducationalDetailsDTO.setStateId(String.valueOf(empApplnEducationalDetailsDBO.getErpStateDBO().getId()));
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getStateOthers())) {
						applnEducationalDetailsDTO.setStateOther(empApplnEducationalDetailsDBO.getStateOthers());
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO())) {
						applnEducationalDetailsDTO.setErpBoardOrUniversity(new SelectDTO());
						applnEducationalDetailsDTO.getErpBoardOrUniversity().setValue(String.valueOf(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().getId()));
						applnEducationalDetailsDTO.getErpBoardOrUniversity().setLabel(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().getUniversityBoardName());
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpInstitutionDBO())) {
						applnEducationalDetailsDTO.setErpInstitute(new SelectDTO());
						applnEducationalDetailsDTO.getErpInstitute().setValue(String.valueOf(empApplnEducationalDetailsDBO.getErpInstitutionDBO().getId()));
						applnEducationalDetailsDTO.getErpInstitute().setLabel(empApplnEducationalDetailsDBO.getErpInstitutionDBO().getInstitutionName());
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.documentsDBOSet)) {
						List<EmpApplnEducationalDetailsDocumentsDTO> documentsList = new ArrayList<>();
						for (EmpApplnEducationalDetailsDocumentsDBO documentsDBO : empApplnEducationalDetailsDBO.documentsDBOSet) {
							if (documentsDBO.recordStatus == 'A') {
								EmpApplnEducationalDetailsDocumentsDTO empApplnEducationalDetailsDocumentsDTO = new EmpApplnEducationalDetailsDocumentsDTO();
								if (!Utils.isNullOrEmpty(documentsDBO.id) && !prevApplicationDataFlag)
									empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId = documentsDBO.id;
								if (!Utils.isNullOrEmpty(documentsDBO.empApplnEducationalDetailsDBO) && !Utils.isNullOrEmpty(documentsDBO.empApplnEducationalDetailsDBO.empApplnEducationalDetailsId) && !prevApplicationDataFlag)
									empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsId = documentsDBO.empApplnEducationalDetailsDBO.empApplnEducationalDetailsId;
								/*if (!Utils.isNullOrEmpty(documentsDBO.educationalDocumentsUrl))
									empApplnEducationalDetailsDocumentsDTO.educationalDocumentsUrl = documentsDBO.educationalDocumentsUrl;*/
								//------
								if(!Utils.isNullOrEmpty(documentsDBO.getEducationalDocumentsUrlDBO())) {
									empApplnEducationalDetailsDocumentsDTO.setNewFile(false);
									empApplnEducationalDetailsDocumentsDTO.setProcessCode(documentsDBO.getEducationalDocumentsUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
									empApplnEducationalDetailsDocumentsDTO.setTempPath(documentsDBO.getEducationalDocumentsUrlDBO().getTempFileNameUnique());
									empApplnEducationalDetailsDocumentsDTO.setOriginalFileName(documentsDBO.getEducationalDocumentsUrlDBO().getFileNameOriginal());
									if(!Utils.isNullOrEmpty(documentsDBO.getEducationalDocumentsUrlDBO().getTempFileNameUnique())) {
										String uniqueFileName = getRemainingAfterLastSlash(documentsDBO.getEducationalDocumentsUrlDBO().getTempFileNameUnique());
										empApplnEducationalDetailsDocumentsDTO.setUniqueFileName(uniqueFileName);
									}
								}
								//-------------
								documentsList.add(empApplnEducationalDetailsDocumentsDTO);
							}
						}
						if(!prevApplicationDataFlag) {
							applnEducationalDetailsDTO.documentList = documentsList;
						}
					}
					if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.qualificationOthers)) {
						otherQualificationLevelList.add(applnEducationalDetailsDTO);
					} else {
						qualificationLevelList.add(applnEducationalDetailsDTO);
					}
				}
			}
			if (!Utils.isNullOrEmpty(qualificationLevelList)) {
				qualificationLevelList.sort((o1, o2) -> {
					int comp = 0;
					if (!Utils.isNullOrEmpty(o1.qualificationLevelId) && !Utils.isNullOrEmpty(o2.qualificationLevelId)) {
						comp = o1.qualificationLevelId.compareTo(o2.qualificationLevelId);
						if (!Utils.isNullOrEmpty(o1.yearOfCompletion) && !Utils.isNullOrEmpty(o2.yearOfCompletion) && comp == 0)
							return o1.yearOfCompletion.compareTo(o2.yearOfCompletion);
						else
							return comp;
					}
					return comp;
				});
				employeeApplicantDTO.educationalDetailDTO.qualificationLevelsList = qualificationLevelList;
			}
			if (!Utils.isNullOrEmpty(otherQualificationLevelList)) {
				otherQualificationLevelList.sort((o1, o2) -> {
					int comp = 0;
					if (!Utils.isNullOrEmpty(o1.yearOfCompletion) && !Utils.isNullOrEmpty(o2.yearOfCompletion)) {
						comp = o1.yearOfCompletion.compareTo(o2.yearOfCompletion);
					}
					return comp;
				});
				employeeApplicantDTO.educationalDetailDTO.otherQualificationLevelsList = otherQualificationLevelList;
			}
			//Eligibility Test list
			List<EmpApplnEligibilityTestDTO> eligibilityTestList = new ArrayList<>();
			for (EmpApplnEligibilityTestDBO empApplnEligibilityTestDBO : empApplnEntriesDBO.empApplnEligibilityTestDBOs) {
				if (empApplnEligibilityTestDBO.recordStatus == 'A') {
					EmpApplnEligibilityTestDTO applnEligibilityTestDTO = new EmpApplnEligibilityTestDTO();
					if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.empApplnEligibilityTestId) && !prevApplicationDataFlag) {
						applnEligibilityTestDTO.empApplnEligibilityTestId = empApplnEligibilityTestDBO.empApplnEligibilityTestId;
					}
					if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.empApplnEntriesDBO) && !Utils.isNullOrEmpty(empApplnEligibilityTestDBO.empApplnEntriesDBO.id) && !prevApplicationDataFlag) {
						applnEligibilityTestDTO.empApplnEntriesId = empApplnEligibilityTestDBO.empApplnEntriesDBO.id;
					}
					if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.empEligibilityExamListDBO) && !Utils.isNullOrEmpty(empApplnEligibilityTestDBO.empEligibilityExamListDBO.empEligibilityExamListId)) {
						applnEligibilityTestDTO.eligibilityTestId = String.valueOf(empApplnEligibilityTestDBO.empEligibilityExamListDBO.empEligibilityExamListId);
					}
					if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.testYear)) {
						applnEligibilityTestDTO.testYear = String.valueOf(empApplnEligibilityTestDBO.testYear);
					}
					if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.eligibilityTestDocumentDBOSet)) {
						List<EmpApplnEligibilityTestDocumentDTO> eligibilityTestDocumentsList = new ArrayList<>();
						for (EmpApplnEligibilityTestDocumentDBO eligibilityTestDocumentDBO : empApplnEligibilityTestDBO.eligibilityTestDocumentDBOSet) {
							if (eligibilityTestDocumentDBO.recordStatus == 'A') {
								EmpApplnEligibilityTestDocumentDTO empApplnEligibilityTestDocumentDTO = new EmpApplnEligibilityTestDocumentDTO();
								if (!Utils.isNullOrEmpty(eligibilityTestDocumentDBO.id) && !prevApplicationDataFlag)
									empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestDocumentId = eligibilityTestDocumentDBO.id;
								if (!Utils.isNullOrEmpty(eligibilityTestDocumentDBO.empApplnEligibilityTestDBO) && !Utils.isNullOrEmpty(eligibilityTestDocumentDBO.empApplnEligibilityTestDBO.empApplnEligibilityTestId) && !prevApplicationDataFlag)
									empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestId = eligibilityTestDocumentDBO.empApplnEligibilityTestDBO.empApplnEligibilityTestId;
								
								/*if (!Utils.isNullOrEmpty(eligibilityTestDocumentDBO.eligibilityDocumentUrl))
									empApplnEligibilityTestDocumentDTO.eligibilityDocumentUrl = eligibilityTestDocumentDBO.eligibilityDocumentUrl;*/
								//-----
								if(!Utils.isNullOrEmpty(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO())) {
									empApplnEligibilityTestDocumentDTO.setNewFile(false);
									empApplnEligibilityTestDocumentDTO.setProcessCode(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
									empApplnEligibilityTestDocumentDTO.setTempPath(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO().getTempFileNameUnique());
									empApplnEligibilityTestDocumentDTO.setOriginalFileName(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO().getFileNameOriginal());
									if(!Utils.isNullOrEmpty(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO().getTempFileNameUnique())) {
										String uniqueFileName = getRemainingAfterLastSlash(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO().getTempFileNameUnique());
										empApplnEligibilityTestDocumentDTO.setUniqueFileName(uniqueFileName);
									}
				
								}
								//--------
								eligibilityTestDocumentsList.add(empApplnEligibilityTestDocumentDTO);
							}
						}
						if(!prevApplicationDataFlag) {
							applnEligibilityTestDTO.eligibilityTestDocumentsList = eligibilityTestDocumentsList;
						}
					}
					eligibilityTestList.add(applnEligibilityTestDTO);
				}
			}
			if (!Utils.isNullOrEmpty(eligibilityTestList)) {
				eligibilityTestList.sort((o1, o2) -> {
					int comp = 0;
					if (!Utils.isNullOrEmpty(o1.testYear) && !Utils.isNullOrEmpty(o2.testYear)) {
						comp = o1.testYear.compareTo(o2.testYear);
					}
					return comp;
				});
				employeeApplicantDTO.educationalDetailDTO.eligibilityTestList = eligibilityTestList;
			}
		}
		//Professional Experience
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnWorkExperienceDBOs)) {
			List<EmpApplnWorkExperienceDTO> professionalExperienceList = new ArrayList<>();
			for (EmpApplnWorkExperienceDBO applnWorkExperienceDBO : empApplnEntriesDBO.empApplnWorkExperienceDBOs) {
				if (applnWorkExperienceDBO.recordStatus == 'A') {
					if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.isCurrentExperience) && applnWorkExperienceDBO.isCurrentExperience) {
						EmpApplnWorkExperienceDTO currentExperienceDTO = new EmpApplnWorkExperienceDTO();
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceId) && !prevApplicationDataFlag)
							currentExperienceDTO.empApplnWorkExperienceId = applnWorkExperienceDBO.empApplnWorkExperienceId;
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnEntriesDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnEntriesDBO.id) && !prevApplicationDataFlag)
							currentExperienceDTO.empApplnEntriesId = applnWorkExperienceDBO.empApplnEntriesDBO.id;
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId)) {
							currentExperienceDTO.workExperienceTypeId = String.valueOf(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO.id)) {
							currentExperienceDTO.functionalAreaId = String.valueOf(applnWorkExperienceDBO.empApplnSubjectCategoryDBO.id);
						} else if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.functionalAreaOthers)) {
							currentExperienceDTO.functionalAreaOthers = applnWorkExperienceDBO.functionalAreaOthers;
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.isPartTime)) {
							currentExperienceDTO.employmentType = applnWorkExperienceDBO.isPartTime ? "parttime" : "fulltime";
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empDesignation)) {
							currentExperienceDTO.designation = applnWorkExperienceDBO.empDesignation;
						}
//						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
//							currentExperienceDTO.fromDate = Utils.convertLocalDateToStringDate(applnWorkExperienceDBO.workExperienceFromDate);
//						}
//						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
//							currentExperienceDTO.toDate = Utils.convertLocalDateToStringDate(applnWorkExperienceDBO.workExperienceToDate);
//						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
							currentExperienceDTO.setFromDate(applnWorkExperienceDBO.workExperienceFromDate);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
							currentExperienceDTO.setToDate(applnWorkExperienceDBO.workExperienceToDate);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceYears)) {
							currentExperienceDTO.years = String.valueOf(applnWorkExperienceDBO.workExperienceYears);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceMonth)) {
							currentExperienceDTO.months = String.valueOf(applnWorkExperienceDBO.workExperienceMonth);
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.noticePeriod)) {
							currentExperienceDTO.noticePeriod = empApplnEntriesDBO.noticePeriod;
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.currentMonthlySalary)) {
							currentExperienceDTO.currentSalary = String.valueOf(empApplnEntriesDBO.currentMonthlySalary);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.institution)) {
							currentExperienceDTO.institution = applnWorkExperienceDBO.institution;
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceDocumentsDBOSet)) {
							List<EmpApplnWorkExperienceDocumentDTO> workExperienceDocumentsList = new ArrayList<>();
							for (EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO : applnWorkExperienceDBO.workExperienceDocumentsDBOSet) {
								if (empApplnWorkExperienceDocumentDBO.recordStatus == 'A') {
									EmpApplnWorkExperienceDocumentDTO empApplnWorkExperienceDocumentDTO = new EmpApplnWorkExperienceDocumentDTO();
									if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.id) && !prevApplicationDataFlag)
										empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceDocumentId = empApplnWorkExperienceDocumentDBO.id;
									if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO) && !Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO.empApplnWorkExperienceId) && !prevApplicationDataFlag)
										empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceId = empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO.empApplnWorkExperienceId;
									
									/*if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.experienceDocumentsUrl))
										empApplnWorkExperienceDocumentDTO.experienceDocumentsUrl = empApplnWorkExperienceDocumentDBO.experienceDocumentsUrl;
										*/
									//------
									if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
										empApplnWorkExperienceDocumentDTO.setNewFile(false);
										empApplnWorkExperienceDocumentDTO.setProcessCode(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
										empApplnWorkExperienceDocumentDTO.setTempPath(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getTempFileNameUnique());
										empApplnWorkExperienceDocumentDTO.setOriginalFileName(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getFileNameOriginal());
										if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getTempFileNameUnique())) {
											String uniqueFileName = getRemainingAfterLastSlash(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getTempFileNameUnique());
											empApplnWorkExperienceDocumentDTO.setUniqueFileName(uniqueFileName);
										}
					
									}
									workExperienceDocumentsList.add(empApplnWorkExperienceDocumentDTO);
								}
							}
							if(!prevApplicationDataFlag) {
								currentExperienceDTO.experienceDocumentList = workExperienceDocumentsList;
							}
						}
						employeeApplicantDTO.professionalExperienceDTO.currentExperience = currentExperienceDTO;
					} else {
						EmpApplnWorkExperienceDTO professionExperienceDTO = new EmpApplnWorkExperienceDTO();
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceId) && !prevApplicationDataFlag)
							professionExperienceDTO.empApplnWorkExperienceId = applnWorkExperienceDBO.empApplnWorkExperienceId;
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnEntriesDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnEntriesDBO.id) && !prevApplicationDataFlag)
							professionExperienceDTO.empApplnEntriesId = applnWorkExperienceDBO.empApplnEntriesDBO.id;
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId)) {
							professionExperienceDTO.workExperienceTypeId = String.valueOf(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO.id)) {
							professionExperienceDTO.functionalAreaId = String.valueOf(applnWorkExperienceDBO.empApplnSubjectCategoryDBO.id);
						} else if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.functionalAreaOthers)) {
							professionExperienceDTO.functionalAreaOthers = applnWorkExperienceDBO.functionalAreaOthers;
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.isPartTime)) {
							professionExperienceDTO.employmentType = applnWorkExperienceDBO.isPartTime ? "parttime" : "fulltime";
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empDesignation)) {
							professionExperienceDTO.designation = applnWorkExperienceDBO.empDesignation;
						}
//						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
//							professionExperienceDTO.fromDate = Utils.convertLocalDateToStringDate(applnWorkExperienceDBO.workExperienceFromDate);
//						}
//						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
//							professionExperienceDTO.toDate = Utils.convertLocalDateToStringDate(applnWorkExperienceDBO.workExperienceToDate);
//						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
							professionExperienceDTO.setFromDate(applnWorkExperienceDBO.workExperienceFromDate);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
							professionExperienceDTO.setToDate(applnWorkExperienceDBO.workExperienceToDate);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceYears)) {
							professionExperienceDTO.years = String.valueOf(applnWorkExperienceDBO.workExperienceYears);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceMonth)) {
							professionExperienceDTO.months = String.valueOf(applnWorkExperienceDBO.workExperienceMonth);
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.institution)) {
							professionExperienceDTO.institution = applnWorkExperienceDBO.institution;
						}
						if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceDocumentsDBOSet)) {
							List<EmpApplnWorkExperienceDocumentDTO> workExperienceDocumentsList = new ArrayList<>();
							for (EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO : applnWorkExperienceDBO.workExperienceDocumentsDBOSet) {
								if (empApplnWorkExperienceDocumentDBO.recordStatus == 'A') {
									EmpApplnWorkExperienceDocumentDTO empApplnWorkExperienceDocumentDTO = new EmpApplnWorkExperienceDocumentDTO();
									if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.id) && !prevApplicationDataFlag)
										empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceDocumentId = empApplnWorkExperienceDocumentDBO.id;
									if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO) && !Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO.empApplnWorkExperienceId) && !prevApplicationDataFlag)
										empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceId = empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO.empApplnWorkExperienceId;
									
									/*if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.experienceDocumentsUrl))
										empApplnWorkExperienceDocumentDTO.experienceDocumentsUrl = empApplnWorkExperienceDocumentDBO.experienceDocumentsUrl;
										*/
									if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
										empApplnWorkExperienceDocumentDTO.setNewFile(false);
										empApplnWorkExperienceDocumentDTO.setProcessCode(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
										empApplnWorkExperienceDocumentDTO.setTempPath(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getTempFileNameUnique());
										empApplnWorkExperienceDocumentDTO.setOriginalFileName(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getFileNameOriginal());
										if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getTempFileNameUnique())) {
											String uniqueFileName = getRemainingAfterLastSlash(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getTempFileNameUnique());
											empApplnWorkExperienceDocumentDTO.setUniqueFileName(uniqueFileName);
										}
					
									}
									workExperienceDocumentsList.add(empApplnWorkExperienceDocumentDTO);
								}
							}
							if(!prevApplicationDataFlag) {
								professionExperienceDTO.experienceDocumentList = workExperienceDocumentsList;
							}
						}
						professionalExperienceList.add(professionExperienceDTO);
					}
				}
			}
			if (!Utils.isNullOrEmpty(professionalExperienceList)) {
				professionalExperienceList.sort((o1, o2) -> {
					int comp = 0;
					if (!Utils.isNullOrEmpty(o1.getFromDate()) && !Utils.isNullOrEmpty(o2.getFromDate())) {
						comp = o1.getFromDate().compareTo(o2.getFromDate());
					}
					return comp;
				});
				employeeApplicantDTO.professionalExperienceDTO.professionalExperienceList = professionalExperienceList;
			}
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.isCurrentlyWorking)) {
			employeeApplicantDTO.professionalExperienceDTO.isCurrentlyWorking = empApplnEntriesDBO.isCurrentlyWorking ? "Yes" : "No";
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.totalPreviousExperienceYears)) {
			employeeApplicantDTO.professionalExperienceDTO.totalPreviousExperienceYears = String.valueOf(empApplnEntriesDBO.totalPreviousExperienceYears);
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.totalPreviousExperienceMonths)) {
			employeeApplicantDTO.professionalExperienceDTO.totalPreviousExperienceMonths = String.valueOf(empApplnEntriesDBO.totalPreviousExperienceMonths);
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.totalPartTimePreviousExperienceYears)) {
			employeeApplicantDTO.professionalExperienceDTO.totalPartTimePreviousExperienceYears = String.valueOf(empApplnEntriesDBO.totalPartTimePreviousExperienceYears);
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.totalPartTimePreviousExperienceMonths)) {
			employeeApplicantDTO.professionalExperienceDTO.totalPartTimePreviousExperienceMonths = String.valueOf(empApplnEntriesDBO.totalPartTimePreviousExperienceMonths);
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.majorAchievements)) {
			employeeApplicantDTO.professionalExperienceDTO.majorAchievements = empApplnEntriesDBO.majorAchievements;
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.expectedSalary)) {
			employeeApplicantDTO.professionalExperienceDTO.expectedSalary = String.valueOf(empApplnEntriesDBO.expectedSalary);
		}

		//Research Details
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.isResearchExperiencePresent)) {
			if (empApplnEntriesDBO.isResearchExperiencePresent) {
				boolean isTypeResearch = true;
				employeeApplicantDTO.researchDetailDTO.isResearchExperience = "Yes";
				if(!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO)){
					if(!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.orcidNo))
						employeeApplicantDTO.researchDetailDTO.orcidId = empApplnEntriesDBO.empApplnPersonalDataDBO.orcidNo;
					if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getVidwanNo())) {
						employeeApplicantDTO.researchDetailDTO.inflibnetVidwanNo = String.valueOf(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getVidwanNo());
					}
					if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getScopusNo())) {
						employeeApplicantDTO.researchDetailDTO.scopusId = empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getScopusNo();
					}
					if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getHIndexNo())) {
						employeeApplicantDTO.researchDetailDTO.hIndex = String.valueOf(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getHIndexNo());
					}
				}
				if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs)) {
					EmpApplnAddtnlInfoEntriesDTO researchEntries = new EmpApplnAddtnlInfoEntriesDTO();
					//List<EmpApplnAddtnlInfoHeadingDBO> researchEntriesHeadings = employeeApplicationTransaction.getResearchDetails(empApplnEntriesDBO.empEmployeeCategoryDBO.id,isTypeResearch);
					List<EmpApplnAddtnlInfoHeadingDBO> researchEntriesHeadings = employeeApplicationTransaction.getResearchDetails(empApplnEntriesDBO.empEmployeeCategoryDBO.id, isTypeResearch, context);
					Map<Integer, EmpApplnAddtnlInfoEntriesDBO> parameterEntriesMap = new HashMap<>();
					for (EmpApplnAddtnlInfoEntriesDBO applnAddtnlInfoEntriesDBO : empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs) {
						if (applnAddtnlInfoEntriesDBO.recordStatus == 'A'
								&& !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO) && !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.id)
								&& !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading) && !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading.isTypeResearch)
								&& applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading.isTypeResearch) {
							parameterEntriesMap.put(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.id, applnAddtnlInfoEntriesDBO);
						}
					}
					List<EmpApplnAddtnlInfoHeadingDTO> researchEntriesHeadingsDTOs = getResearchHeadingDTOs(researchEntriesHeadings,parameterEntriesMap,prevApplicationDataFlag);
					if (!Utils.isNullOrEmpty(researchEntriesHeadingsDTOs)) {
						researchEntries.researchEntriesHeadings = researchEntriesHeadingsDTOs;
						employeeApplicantDTO.researchDetailDTO.researchEntries = researchEntries;
					}
				}
			} else {
				employeeApplicantDTO.researchDetailDTO.isResearchExperience = "No";
			}
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.isInterviewedBefore)) {
			if (empApplnEntriesDBO.isInterviewedBefore) {
				employeeApplicantDTO.researchDetailDTO.isInterviewedBefore = "Yes";
				if (!Utils.isNullOrEmpty(empApplnEntriesDBO.interviewedBeforeDepartment)) {
					employeeApplicantDTO.researchDetailDTO.interviewedBeforeDepartment = empApplnEntriesDBO.interviewedBeforeDepartment;
				}
				if (!Utils.isNullOrEmpty(empApplnEntriesDBO.interviewedBeforeYear)) {
					employeeApplicantDTO.researchDetailDTO.interviewedBeforeYear = String.valueOf(empApplnEntriesDBO.interviewedBeforeYear);
				}
				if (!Utils.isNullOrEmpty(empApplnEntriesDBO.interviewedBeforeApplicationNo)) {
					employeeApplicantDTO.researchDetailDTO.interviewedBeforeApplicationNo = String.valueOf(empApplnEntriesDBO.interviewedBeforeApplicationNo);
				}
				if (!Utils.isNullOrEmpty(empApplnEntriesDBO.interviewedBeforeSubject)) {
					employeeApplicantDTO.researchDetailDTO.interviewedBeforeSubject = empApplnEntriesDBO.interviewedBeforeSubject;
				}
			} else {
				employeeApplicantDTO.researchDetailDTO.isInterviewedBefore = "No";
			}
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnVacancyInformationDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnVacancyInformationDBO.id)) {
			employeeApplicantDTO.researchDetailDTO.vacancyInformationId = String.valueOf(empApplnEntriesDBO.empApplnVacancyInformationDBO.id);
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.aboutVacancyOthers)) {
			employeeApplicantDTO.researchDetailDTO.aboutVacancyOthers = empApplnEntriesDBO.aboutVacancyOthers;
		}
		if (!Utils.isNullOrEmpty(empApplnEntriesDBO.otherInformation)) {
			employeeApplicantDTO.researchDetailDTO.otherInformation = empApplnEntriesDBO.otherInformation;
		}
		return employeeApplicantDTO;
	}
	public String getRemainingAfterLastSlash(String inputString) {
        int lastSlashIndex = inputString.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return inputString.substring(lastSlashIndex + 1);
        } else {
            return inputString;
        }
    }

	public Map<Integer, Map<Integer, Map<Integer, List<Tuple>>>> convertAdditionalInformationToMap(List<Tuple> tupleList) {
		Map<Integer, Map<Integer, Map<Integer, List<Tuple>>>> map = new HashMap<>();
		if(!Utils.isNullOrEmpty(tupleList)){
			map = tupleList.stream().filter(tuple -> !Utils.isNullOrEmpty(tuple.get("headingId")) && !Utils.isNullOrEmpty(tuple.get("parameterId")) && !Utils.isNullOrEmpty(tuple.get("entriesId")))
					.collect(Collectors.groupingBy(s -> Integer.parseInt(s.get("headingId").toString()), Collectors.groupingBy(s -> Integer.parseInt(s.get("parameterId").toString()),
							Collectors.groupingBy(s -> Integer.parseInt(s.get("entriesId").toString()), Collectors.toList()))));
		}
		return map;
	}

	public EmpApplnAddtnlInfoEntriesDTO convertAdditionalInformationMapToDTO(Map<Integer, Map<Integer, Map<Integer, List<Tuple>>>> additionalInformationMap) {
		EmpApplnAddtnlInfoEntriesDTO empApplnAddtnlInfoEntriesDTO = new EmpApplnAddtnlInfoEntriesDTO();
		empApplnAddtnlInfoEntriesDTO.researchEntriesHeadings = new ArrayList<>();
		List<EmpApplnAddtnlInfoHeadingDTO> empApplnAddtnlInfoHeadingDTOS = new ArrayList<>();
		additionalInformationMap.forEach((headingId, headingParameterMap) -> {
			EmpApplnAddtnlInfoHeadingDTO empApplnAddtnlInfoHeadingDTO = new EmpApplnAddtnlInfoHeadingDTO();
			List<EmpApplnAddtnlInfoParameterDTO> empApplnAddtnlInfoParameterDTOS = new ArrayList<>();
			headingParameterMap.forEach((parameterId, parameterEntriesMap) -> {
				EmpApplnAddtnlInfoParameterDTO empApplnAddtnlInfoParameterDTO = new EmpApplnAddtnlInfoParameterDTO();
				parameterEntriesMap.forEach((entriesId, tuples) -> {
					Tuple tuple = tuples.get(0);
					if(Utils.isNullOrEmpty(empApplnAddtnlInfoHeadingDTO.id)){
						empApplnAddtnlInfoHeadingDTO.id = String.valueOf(tuple.get("headingId"));
						empApplnAddtnlInfoHeadingDTO.groupHeading = !Utils.isNullOrEmpty(tuple.get("headingName")) ? String.valueOf(tuple.get("headingName")) : "";
						empApplnAddtnlInfoHeadingDTO.displayOrder = String.valueOf(tuple.get("headingDisplayOrder"));
					}
					if(Utils.isNullOrEmpty(empApplnAddtnlInfoParameterDTO.id)){
						empApplnAddtnlInfoParameterDTO.id = String.valueOf(tuple.get("parameterId"));
						empApplnAddtnlInfoParameterDTO.researchParameter = !Utils.isNullOrEmpty(tuple.get("parameterName")) ? String.valueOf(tuple.get("parameterName")) : "";
						empApplnAddtnlInfoParameterDTO.displayOrder = String.valueOf(tuple.get("parameterDisplayOrder"));
					}
					if(!Utils.isNullOrEmpty(tuple.get("entriesId"))){
						empApplnAddtnlInfoParameterDTO.parameterValue = !Utils.isNullOrEmpty(tuple.get("additionalInfoValue")) ? String.valueOf(tuple.get("additionalInfoValue")) : "";
					}
					empApplnAddtnlInfoParameterDTOS.add(empApplnAddtnlInfoParameterDTO);
				});
				empApplnAddtnlInfoParameterDTOS.sort(Comparator.comparing(o -> Integer.parseInt(o.displayOrder)));
				empApplnAddtnlInfoHeadingDTO.parameters = empApplnAddtnlInfoParameterDTOS;
			});
			empApplnAddtnlInfoHeadingDTOS.add(empApplnAddtnlInfoHeadingDTO);
		});
		empApplnAddtnlInfoHeadingDTOS.sort(Comparator.comparing(o -> Integer.parseInt(o.displayOrder)));
		empApplnAddtnlInfoEntriesDTO.researchEntriesHeadings = empApplnAddtnlInfoHeadingDTOS;
		return empApplnAddtnlInfoEntriesDTO;
	}
}