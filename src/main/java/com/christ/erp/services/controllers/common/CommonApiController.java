package com.christ.erp.services.controllers.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.AppProperties;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbqueries.common.CommonQueries;
import com.christ.erp.services.dbqueries.employee.CommonEmployeeQueries;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.admission.settings.AdmissionSelectionProcessTypeDTO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.aws.URLFolderListDTO;
import com.christ.erp.services.dto.common.CommonDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ErpCampusDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.ErpCityDTO;
import com.christ.erp.services.dto.common.ErpPincodeDTO;
import com.christ.erp.services.dto.common.ErpUniversityBoardDTO;
import com.christ.erp.services.dto.common.ErpUsersDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.common.SysRoleDTO;
import com.christ.erp.services.dto.employee.attendance.LetterTemplatesDTO;
import com.christ.erp.services.dto.employee.common.EmployeeApplicationDTO;
import com.christ.erp.services.dto.employee.settings.ErpTemplateDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Protected/Common")
public class CommonApiController extends BaseApiController {

    private final static String IS_BASIC_NATIONALITY;
//    CommonApiHandler commonApiHandler = CommonApiHandler.getInstance();

    static {
        IS_BASIC_NATIONALITY = AppProperties.get("erp.country.isbasic.nationality");
    }
    
    @Autowired
    private CommonApiHandler commonApiHandler;
    
    @Autowired
    private CommonApiTransaction commonApiTransaction1;
    
    @RequestMapping(value = "/getLocation", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getLocation() {
        ApiResult<List<LookupItemDTO>> locations = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(locations, context, CommonQueries.SELECT_LOCATION, null);
                }
                @Override
                public void onError(Exception error) {
                    locations.success = false;
                    locations.dto = null;
                    locations.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(locations);
    }
    
	@RequestMapping(value = "/getMotherTongue", method = RequestMethod.POST)
	public Mono<ApiResult<List<SelectDTO>>> getMotherTongue() {
		ApiResult<List<SelectDTO>> result = new ApiResult<>();
		try {
			commonApiHandler.getMotherTongue(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getOccupation", method = RequestMethod.POST)
	public Mono<ApiResult<List<SelectDTO>>> getOccupation() {
		ApiResult<List<SelectDTO>> result = new ApiResult<>();
		try {
			commonApiHandler.getOccupation(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getSports", method = RequestMethod.POST)
	public Mono<ApiResult<List<SelectDTO>>> getSports() {
		ApiResult<List<SelectDTO>> result = new ApiResult<>();
		try {
			commonApiHandler.getSports(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getSportsLevel", method = RequestMethod.POST)
	public Mono<ApiResult<List<SelectDTO>>> getSportsLevel() {
		ApiResult<List<SelectDTO>> result = new ApiResult<>();
		try {
			commonApiHandler.getSportsLevel(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getExtraCurricular", method = RequestMethod.POST)
	public Mono<ApiResult<List<SelectDTO>>> getExtraCurricular() {
		ApiResult<List<SelectDTO>> result = new ApiResult<>();
		try {
			commonApiHandler.getExtraCurricular(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getSalutations", method = RequestMethod.POST)
	public Mono<ApiResult<List<SelectDTO>>> getSalutations() {
		ApiResult<List<SelectDTO>> result = new ApiResult<>();
		try {
			commonApiHandler.getSalutations(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getUniversityBoard", method = RequestMethod.POST)
	public Mono<ApiResult<List<ErpUniversityBoardDTO>>> getUniversityBoard() {
		ApiResult<List<ErpUniversityBoardDTO>> result = new ApiResult<>();
		try {
			commonApiHandler.getUniversityBoard(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getInstitutionReference", method = RequestMethod.POST)
	public Mono<ApiResult<List<SelectDTO>>> getInstitutionReference() {
		ApiResult<List<SelectDTO>> result = new ApiResult<>();
		try {
			commonApiHandler.getInstitutionReference(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getCountryAndNationality", method = RequestMethod.POST)
    public Mono<ApiResult<List<CommonDTO>>> getCountryAndNationality() {
        ApiResult<List<CommonDTO>> countries = new ApiResult<List<CommonDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Query query = context.createNativeQuery(CommonQueries.SELECT_COUNTRY_AND_NATIONALITY, Tuple.class);
                    List<Tuple> mappings = query.getResultList();
                    if(mappings != null && mappings.size() > 0) {
                        countries.success = true;
                        countries.dto = new ArrayList<>();
                        for(Tuple mapping : mappings) {
                            CommonDTO common = new CommonDTO();
                            common.value = String.valueOf(mapping.get("ID"));
                            common.label = String.valueOf(mapping.get("Text"));
                            common.nationality = !Utils.isNullOrEmpty(mapping.get("Nationality")) ? mapping.get("Nationality").toString():"";
                            common.phoneCode = !Utils.isNullOrEmpty(mapping.get("phoneCode")) ? mapping.get("phoneCode").toString():"";
                            countries.dto.add(common);
                        }
                    }
                }
                @Override
                public void onError(Exception error) {
                    countries.success = false;
                    countries.dto = null;
                    countries.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(countries);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getReligion", method = RequestMethod.POST)
    public Mono<ApiResult<List<CommonDTO>>> getReligion() {
        ApiResult<List<CommonDTO>> religions = new ApiResult<List<CommonDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Query query = context.createNativeQuery(CommonQueries.SELECT_RELIGION, Tuple.class);
                    List<Tuple> mappings = query.getResultList();
                    if(mappings != null && mappings.size() > 0) {
                        religions.success = true;
                        religions.dto = new ArrayList<>();
                        for(Tuple mapping : mappings) {
                            CommonDTO common = new CommonDTO();
                            common.value = String.valueOf(mapping.get("ID"));
                            common.label = String.valueOf(mapping.get("Text"));
                            common.isMinority = !Utils.isNullOrEmpty(mapping.get("IsMinority")) ? Boolean.valueOf(mapping.get("IsMinority").toString()) : false;
                            religions.dto.add(common);
                        }
                    }
                }
                @Override
                public void onError(Exception error) {
                    religions.success = false;
                    religions.dto = null;
                    religions.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(religions);
    }

    @RequestMapping(value = "/getBloodGroup", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getBloodGroup() {
        ApiResult<List<LookupItemDTO>> bloodGroups = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(bloodGroups, context, CommonQueries.SELECT_BLOOD_GROUP, null);
                }
                @Override
                public void onError(Exception error) {
                    bloodGroups.success = false;
                    bloodGroups.dto = null;
                    bloodGroups.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(bloodGroups);
    }

    @RequestMapping(value = "/getReservationCategory", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getReservationCategory() {
        ApiResult<List<LookupItemDTO>> reservationCategories = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(reservationCategories, context, CommonQueries.SELECT_RESERVATION_CATEGORY, null);
                }
                @Override
                public void onError(Exception error) {
                    reservationCategories.success = false;
                    reservationCategories.dto = null;
                    reservationCategories.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(reservationCategories);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getQualificationLevel", method = RequestMethod.POST)
    public Mono<ApiResult<List<EmployeeApplicationDTO>>> getQualificationLevel() {
        ApiResult<List<EmployeeApplicationDTO>> qualificationLevels = new ApiResult<List<EmployeeApplicationDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Query query = context.createNativeQuery(CommonQueries.SELECT_QUALIFICATION_LEVEL, Tuple.class);
                    List<Tuple> mappings = query.getResultList();
                    if(mappings != null && mappings.size() > 0) {
                        qualificationLevels.success = true;
                        qualificationLevels.dto = new ArrayList<>();
                        for(Tuple mapping : mappings) {
                            EmployeeApplicationDTO common = new EmployeeApplicationDTO();
                            common.value = String.valueOf(mapping.get("ID"));
                            common.label = String.valueOf(mapping.get("Text"));
                            common.qualificationLevelDegreeOrder = !Utils.isNullOrEmpty(mapping.get("QualificationLevelDegreeOrder")) ? mapping.get("QualificationLevelDegreeOrder").toString():"";
                            common.isAddMore = !Utils.isNullOrEmpty(mapping.get("IsAdd_more")) ? Boolean.valueOf(mapping.get("IsAdd_more").toString()) : false;
                            common.isMandatory = !Utils.isNullOrEmpty(mapping.get("IsMandatory")) ? Boolean.valueOf(mapping.get("IsMandatory").toString()) : false;
                            common.isStatusDisplay = !Utils.isNullOrEmpty(mapping.get("isStatusDisplay")) ? Boolean.valueOf(mapping.get("isStatusDisplay").toString()) : false;
                            common.boardType = !Utils.isNullOrEmpty(mapping.get("boardType")) ? mapping.get("boardType").toString() : null;
                            common.qualificationLevelCode = !Utils.isNullOrEmpty(mapping.get("qualificationLevelCode")) ? mapping.get("qualificationLevelCode").toString() : null;
                            qualificationLevels.dto.add(common);
                        }
                    }
                }
                @Override
                public void onError(Exception error) {
                    qualificationLevels.success = false;
                    qualificationLevels.dto = null;
                    qualificationLevels.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(qualificationLevels);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getApplnWorkExperienceType", method = RequestMethod.POST)
    public Mono<ApiResult<List<EmployeeApplicationDTO>>> GetApplnWorkExperienceType() {
        ApiResult<List<EmployeeApplicationDTO>> experienceTypes = new ApiResult<List<EmployeeApplicationDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Query query = context.createNativeQuery(CommonQueries.SELECT_APPLN_WORK_EXPERIENCE_TYPE, Tuple.class);
					List<Tuple> mappings = query.getResultList();
                    if(mappings != null && mappings.size() > 0) {
                        experienceTypes.success = true;
                        experienceTypes.dto = new ArrayList<>();
                        for(Tuple mapping : mappings) {
                            EmployeeApplicationDTO common = new EmployeeApplicationDTO();
                            common.value = String.valueOf(mapping.get("ID"));
                            common.label = String.valueOf(mapping.get("Text"));
                            common.isExperienceTypeAcademic = !Utils.isNullOrEmpty(mapping.get("isExperienceTypeAcademic")) ? Boolean.valueOf(mapping.get("isExperienceTypeAcademic").toString()) : false;
                            experienceTypes.dto.add(common);
                        }
                    }
                }
                @Override
                public void onError(Exception error) {
                    experienceTypes.success = false;
                    experienceTypes.dto = null;
                    experienceTypes.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(experienceTypes);
    }

    @RequestMapping(value = "/getApplnVacancyInformation", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> GetApplnVacancyInformation() {
        ApiResult<List<LookupItemDTO>> vacancyInformations = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(vacancyInformations, context, CommonQueries.SELECT_APPLN_VACANCY_INFORMATION, null);
                }
                @Override
                public void onError(Exception error) {
                    vacancyInformations.success = false;
                    vacancyInformations.dto = null;
                    vacancyInformations.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(vacancyInformations);
    }

    @RequestMapping(value = "/getGender", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getGender() {
        ApiResult<List<LookupItemDTO>> genderList = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(genderList, context, CommonQueries.SELECT_GENDER, null);
                }
                @Override
                public void onError(Exception error) {
                    genderList.success = false;
                    genderList.dto = null;
                    genderList.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(genderList);
    }

    @RequestMapping(value = "/getMaritalStatus", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getMaritalStatus() {
        ApiResult<List<LookupItemDTO>> maritalList = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(maritalList, context, CommonQueries.SELECT_MARITAL_STATUS, null);
                }
                @Override
                public void onError(Exception error) {
                    maritalList.success = false;
                    maritalList.dto = null;
                    maritalList.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(maritalList);
    }

    @RequestMapping(value = "/getMonthList", method = RequestMethod.POST)
    public  Mono<ApiResult<List<LookupItemDTO>>> getMonthList() {
        ApiResult<List<LookupItemDTO>> monthList = new ApiResult<>();
        try {
            monthList.dto = new ArrayList<>();
            monthList.dto.addAll(commonApiHandler.getMonthList());
            monthList.success = true;
        }
        catch(Exception error) {
            monthList.success = false;
            monthList.dto = null;
            monthList.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(monthList);
    }

    @RequestMapping(value = "/getDifferentlyAbled", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getDifferentlyAbled() {
        ApiResult<List<LookupItemDTO>> differentlyAbled = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(differentlyAbled, context, CommonQueries.SELECT_DIFFERENTLY_ABLED, null);
                }
                @Override
                public void onError(Exception error) {
                    differentlyAbled.success = false;
                    differentlyAbled.dto = null;
                    differentlyAbled.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(differentlyAbled);
    }

    @RequestMapping(value = "/getDepartments", method = RequestMethod.POST)
    public  Mono<ApiResult<List<LookupItemDTO>>> getDepartmentsList() {
        ApiResult<List<LookupItemDTO>> departments = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(departments, context, CommonQueries.SELECT_DEPARTMENT_LIST, null);
                }
                @Override
                public void onError(Exception error) {
                    departments.success = false;
                    departments.dto = null;
                    departments.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(departments);
    }

    @RequestMapping(value = "/getAcademicYear", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getAcademicYear() {
        ApiResult<List<LookupItemDTO>> academicYear = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(academicYear, context, CommonQueries.GET_ACADEMIC_YEAR, null);
                }
                @Override
                public void onError(Exception error) {
                    academicYear.success = false;
                    academicYear.dto = null;
                    academicYear.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(academicYear);
    }

    @RequestMapping(value = "/getState", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getState() {
        ApiResult<List<LookupItemDTO>> campus = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(campus, context, CommonQueries.SELECT_STATE, null);
                }
                @Override
                public void onError(Exception error) {
                    campus.success = false;
                    campus.dto = null;
                    campus.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(campus);
    }

    @RequestMapping(value = "/getCity", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getCity(@RequestParam("stateId") String stateId) {
        ApiResult<List<LookupItemDTO>> campus = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Map<String, Object> args = new HashMap<String, Object>();
                    args.put("stateId", stateId);
                    Utils.getDropdownData(campus, context, CommonQueries.SELECT_CITY, args);
                }
                @Override
                public void onError(Exception error) {
                    campus.success = false;
                    campus.dto = null;
                    campus.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(campus);
    }

    @RequestMapping(value = "/getQualificationLevelCurrentStatus", method = RequestMethod.POST)
    public Mono<List<LookupItemDTO>> getQualificationLevelCurrentStatus() {
        List<LookupItemDTO> qualificationLevelCurrentStatus = new ArrayList<>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    qualificationLevelCurrentStatus.add(new LookupItemDTO("Pursuing","Pursuing"));
                    qualificationLevelCurrentStatus.add(new LookupItemDTO("Completed","Completed"));
                }
                @Override
                public void onError(Exception error) {
                    Utils.log(error.getMessage());
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(qualificationLevelCurrentStatus);
    }

    @RequestMapping(value = "/getBasicNationality", method = RequestMethod.POST)
    public  Mono<LookupItemDTO> getBasicNationality() {
        LookupItemDTO isbasicnationlity = new LookupItemDTO();
        try {
            isbasicnationlity.value=IS_BASIC_NATIONALITY;
            isbasicnationlity.label=IS_BASIC_NATIONALITY;
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(isbasicnationlity);
    }

    @RequestMapping(value = "/getDepartmentsByCampusId", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getDepartmentsByCampusId(@RequestParam("campusId") String campusId) {
        ApiResult<List<LookupItemDTO>> departments = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Map<String, Object> args = new HashMap<String, Object>();
                    args.put("campusId", campusId);
                    Utils.getDropdownData(departments, context, CommonQueries.SELECT_DEPARTMENTS_BY_CAMPUS_ID, args);
                }
                @Override
                public void onError(Exception error) {
                    departments.success = false;
                    departments.dto = null;
                    departments.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(departments);
    }

    @RequestMapping(value = "/getDepartmentByLocation", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getDepartmentByLocation(@RequestParam("locationId") String locationId) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            if((locationId.isEmpty())) {
                result.failureMessage = "Please select location";
            }else {
                commonApiHandler.getDepartmentbyLocation(locationId, result);
            }
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }


        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getCampusByLoc", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getCampusByLoc(@RequestParam("locId") String locId) {
        ApiResult<List<LookupItemDTO>> location = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Map<String, Object> args = new HashMap<String, Object>();
                    if(!Utils.isNullOrEmpty(locId)) {
                        args.put("locId", locId);
                        Utils.getDropdownData(location, context, CommonEmployeeQueries.SELECT_CAMPUS_BY_LOC, args);
                    }
                }
                @Override
                public void onError(Exception error) {
                    location.success = false;
                    location.dto = null;
                    location.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(location);
    }

    @RequestMapping(value = "/getGroupTemplateIDSForAppointmentLetter", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getGroupTemplateIDS() {
        ApiResult<List<LookupItemDTO>> academicYear = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    String str="select e.erp_template_id as ID, e.template_name as Text from erp_template e\n" +
                            "inner join erp_template_group e2 on e.erp_template_group_id = e2.erp_template_group_id\n" +
                            "where e2.template_group_name=\"Appointment Letter\" and e2.record_status='A' and e.record_status='A'";
                    Utils.getDropdownData(academicYear, context, String.valueOf(str), null);
                }
                @Override
                public void onError(Exception error) {
                    academicYear.success = false;
                    academicYear.dto = null;
                    academicYear.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(academicYear);
    }

    @RequestMapping(value = "/getUsers", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getUsers() {
        ApiResult<List<LookupItemDTO>> usersList = new ApiResult<List<LookupItemDTO>>();
        try {
            usersList = commonApiHandler.getUsers();
            if (!Utils.isNullOrEmpty(usersList)) {
                usersList.success = true;
            } else {
                usersList.success = false;
            }
        } catch (Exception error) {
            usersList.success = false;
            usersList.dto = null;
            usersList.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(usersList);
    }

    @RequestMapping(value = "/getErpCampusDepartmentMappingByCampusId", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getErpCampusDepartmentMappingByCampusId(@RequestParam("campusId") String campusId){
        ApiResult<List<LookupItemDTO>> departments = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Map<String, Object> args = new HashMap<String, Object>();
                    args.put("campusId", Integer.parseInt(campusId));
                    Utils.getDropdownData(departments, context, CommonQueries.SELECT_ERP_CAMPUS_DEPARTMENT_MAPPING_BY_CAMPUS_ID, args);
                }
                @Override
                public void onError(Exception error) {
                    departments.success = false;
                    departments.dto = null;
                    departments.failureMessage = error.getMessage();
                }
            }, true);
        }catch (Exception e) {
            Utils.log(e.getMessage());
        }
        return Mono.justOrEmpty(departments);

    }

    @RequestMapping(value = "/getGeneratedYear", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getGeneratedYear() {
        ApiResult<List<LookupItemDTO>> generatedYear = new ApiResult<List<LookupItemDTO>>();
        try {
            generatedYear = commonApiHandler.getGeneratedYear();
            if (!Utils.isNullOrEmpty(generatedYear)) {
                generatedYear.success = true;
            } else {
                generatedYear.success = false;
            }
        } catch (Exception error) {
            generatedYear.success = false;
            generatedYear.dto = null;
            generatedYear.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(generatedYear);
    }

    @RequestMapping(value = "/getCampus", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getCampus() {
        ApiResult<List<LookupItemDTO>> campus = new ApiResult<List<LookupItemDTO>>();
        try {
            campus = commonApiHandler.getCampus(null);
            if (!Utils.isNullOrEmpty(campus) && !Utils.isNullOrEmpty(campus.dto)) {
                campus.success = true;
            } else {
                campus.success = false;
            }
        } catch (Exception error) {
            campus.success = false;
            campus.dto = null;
            campus.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(campus);
    }

    @RequestMapping(value = "/getSubModuleList", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getCaterogysList() {
        ApiResult<List<LookupItemDTO>> processes = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(processes, context, CommonQueries.SELECT_SUB_MODULE_NAME, null);
                }
                @Override
                public void onError(Exception error) {
                    processes.success = false;
                    processes.dto = null;
                    processes.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(processes);
    }

    @RequestMapping(value = "/getType", method = RequestMethod.POST)
    public Mono<List<LookupItemDTO>> getType() {
        List<LookupItemDTO> types = new ArrayList<>();
        try {
            types.add(new LookupItemDTO("Mail","Mail"));
            types.add(new LookupItemDTO("SMS","SMS"));
            types.add(new LookupItemDTO("Letter","Letter"));
            types.add(new LookupItemDTO("Print","Print"));
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(types);
    }

    @RequestMapping(value = "/getEmployeesOrUsers", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getEmployeesOrUsers() {
        ApiResult<List<LookupItemDTO>> employeeOrusersList = new ApiResult<List<LookupItemDTO>>();
        try {
            employeeOrusersList = commonApiHandler.getEmployeesOrUsers();
            if (!Utils.isNullOrEmpty(employeeOrusersList)) {
                employeeOrusersList.success = true;
            } else {
                employeeOrusersList.success = false;
            }
        } catch (Exception error) {
            employeeOrusersList.success = false;
            employeeOrusersList.dto = null;
            employeeOrusersList.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(employeeOrusersList);
    }
    @RequestMapping(value = "/getDeanery", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getDeanery() {
        ApiResult<List<LookupItemDTO>> deaneryList = new ApiResult<List<LookupItemDTO>>();
        try {
            deaneryList = commonApiHandler.getDeanery();
            if (!Utils.isNullOrEmpty(deaneryList)) {
                deaneryList.success = true;
            } else {
                deaneryList.success = false;
            }
        } catch (Exception error) {
            deaneryList.success = false;
            deaneryList.dto = null;
            deaneryList.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(deaneryList);
    }

    @RequestMapping(value = "/getDepartmentCategory", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getDepartmentCategory() {
        ApiResult<List<LookupItemDTO>> departmentCategoryList = new ApiResult<List<LookupItemDTO>>();
        try {
            departmentCategoryList = commonApiHandler.getDepartmentCategory();
            if (!Utils.isNullOrEmpty(departmentCategoryList)) {
                departmentCategoryList.success = true;
            } else {
                departmentCategoryList.success = false;
            }
        } catch (Exception error) {
            departmentCategoryList.success = false;
            departmentCategoryList.dto = null;
            departmentCategoryList.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(departmentCategoryList);
    }
/*
    @RequestMapping(value = "/getMenus", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getMenus() {
        ApiResult<List<LookupItemDTO>> menus = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    String query="select e.erp_module_id as 'ID', e.module_name as 'Text' from erp_module e where e.record_status='A'";
                    Utils.getDropdownData(menus, context, query, null);
                }
                @Override
                public void onError(Exception error) {
                    menus.success = false;
                    menus.dto = null;
                    menus.failureMessage = error.getMessage();
                }
            }, true);
        }catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(menus);
    }

    @RequestMapping(value = "/getSubModuleByModule", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getSubModuleByModule(@RequestParam("moduleId") String moduleId) {
        ApiResult<List<LookupItemDTO>> location = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Map<String, Object> args = new HashMap<String, Object>();
                    if(!Utils.isNullOrEmpty(moduleId)) {
                        args.put("moduleId", moduleId);
                        String s="select e.erp_module_sub_id AS ID, e.sub_module_name AS Text from erp_module_sub e  where e.record_status='A'  and e.erp_module_id=:moduleId";
                        Utils.getDropdownData(location, context, s, args);
                    }
                }
                @Override
                public void onError(Exception error) {
                    location.success = false;
                    location.dto = null;
                    location.failureMessage = error.getMessage();
                }
            }, true);
        }catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(location);
    }

    @RequestMapping(value = "/getDisplayOrder",method = RequestMethod.POST)
    public Mono<ApiResult<LookupItemDTO>> getSubModuleByModule(@RequestParam("moduleId") String moduleId, @RequestParam("ProcessId") String processId) {
        ApiResult<LookupItemDTO> location = new ApiResult<LookupItemDTO>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context){
//                    System.out.println(moduleId);
//                    System.out.println(processId);
                    try {
                        location.dto=  commonApiHandler.getDisplayOrder(moduleId,processId);
                        location.success=true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Exception error) {
                    location.success = false;
                    location.dto = null;
                    location.failureMessage = error.getMessage();
                }
            }, true);
        }catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(location);
    }
*/
    @RequestMapping(value = "/getCurrency", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getCurrencys() {
        ApiResult<List<LookupItemDTO>> categories = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    String str="select erp_currency_id as ID, currency_code as Text from erp_currency  where record_status= 'A'";
                    Utils.getDropdownData(categories, context, str.toString(), null);
                }
                @Override
                public void onError(Exception error) {
                    categories.success = false;
                    categories.dto = null;
                    categories.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(categories);
    }

    @RequestMapping(value = "/getCampuses", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getCampuses() {
        ApiResult<List<LookupItemDTO>> campus = new ApiResult<List<LookupItemDTO>>();
        try {
            campus = commonApiHandler.getCampuses();
            if (!Utils.isNullOrEmpty(campus)) {
                campus.success = true;
            } else {
                campus.success = false;
            }
        } catch (Exception error) {
            campus.success = false;
            campus.dto = null;
            campus.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(campus);
    }

    @RequestMapping(value = "/getGroupTemplateIdsForOfferLetter", method = RequestMethod.POST)
    public Mono<ApiResult<List<LetterTemplatesDTO>>> getGroupTemplateIdsForOfferLetter(@RequestParam("employeeCategoryID") String employeeCategoryId) {
        ApiResult<List<LetterTemplatesDTO>> result = new ApiResult<List<LetterTemplatesDTO>>();
        try {
            List<LetterTemplatesDTO>  letterTemplatesDTOs = commonApiHandler.getOfferLetterTemplate(Integer.parseInt(employeeCategoryId));
            if(!Utils.isNullOrEmpty(letterTemplatesDTOs)) {
                result.success = true;
                result.dto = letterTemplatesDTOs;
            } else {
                result.success = false;
            }
        } catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/validatePincode", method = RequestMethod.POST)
    public Mono<ApiResult> validatePincode(@RequestParam("pincode") String pincode) {
        ApiResult result = new ApiResult();
        try {
            if(!Utils.isNullOrEmpty(pincode)){
                boolean isValid = commonApiHandler.validatePincode(pincode);
                if(isValid) {
                    result.success = true;
                }
            }
        } catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getProgramme", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getProgramme() {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            commonApiHandler.getProgramme(result);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getPrerequisiteName", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getPrerequisiteName() {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            commonApiHandler.getPrerequisiteName(result);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getStatesByCountry", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getStateByCountry(@RequestParam("countryId") String countryId) {
        ApiResult<List<LookupItemDTO>> states = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Map<String, Object> args = new HashMap<String, Object>();
                    args.put("CountryId", Integer.parseInt(countryId));
                    Utils.getDropdownData(states, context, CommonQueries.SELECT_STATES_BY_COUNTRY, args);
                }
                @Override
                public void onError(Exception error) {
                    states.success = false;
                    states.dto = null;
                    states.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(states);
    }

    @RequestMapping(value = "/getCampusByProgramme", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getCampusByProgramme(@RequestParam("programmeId") String programmeId) {
        ApiResult<List<LookupItemDTO>> getLocationsAndCampus = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Map<String, Object> args = new HashMap<String, Object>();
                    args.put("ProgrammeId", Integer.parseInt(programmeId));
                    Utils.getDropdownData(getLocationsAndCampus, context, CommonQueries.SELECT_CAMPUS_BY_PROGRAMME, args);
                }
                @Override
                public void onError(Exception error) {
                    getLocationsAndCampus.success = false;
                    getLocationsAndCampus.dto = null;
                    getLocationsAndCampus.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(getLocationsAndCampus);
    }

    @RequestMapping(value = "/getLocationsByProgramme", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getLocationsByProgramme(@RequestParam("programmeId") String programmeId) {
        ApiResult<List<LookupItemDTO>> getLocationsAndCampus = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Map<String, Object> args = new HashMap<String, Object>();
                    args.put("ProgrammeId", Integer.parseInt(programmeId));
                    Utils.getDropdownData(getLocationsAndCampus, context, CommonQueries.SELECT_LOCARIONS_BY_PROGRAMME, args);
                }
                @Override
                public void onError(Exception error) {
                    getLocationsAndCampus.success = false;
                    getLocationsAndCampus.dto = null;
                    getLocationsAndCampus.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(getLocationsAndCampus);
    }

    @RequestMapping(value = "/getQualificationList", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getQualificationList() {
        ApiResult<List<LookupItemDTO>> getLocationsAndCampus = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(getLocationsAndCampus, context, CommonQueries.SELECT_QALIFICATION_LIST, null);
                }
                @Override
                public void onError(Exception error) {
                    getLocationsAndCampus.success = false;
                    getLocationsAndCampus.dto = null;
                    getLocationsAndCampus.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(getLocationsAndCampus);
    }

    @RequestMapping(value = "/getQualificationListForAdditionalDocument", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getQualificationListForAdditionalDocument() {
        ApiResult<List<LookupItemDTO>> getLocationsAndCampus = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(getLocationsAndCampus, context, CommonQueries.SELECT_QALIFICATION_LIST_FOR_ADDITIONAL_DOC, null);
                }
                @Override
                public void onError(Exception error) {
                    getLocationsAndCampus.success = false;
                    getLocationsAndCampus.dto = null;
                    getLocationsAndCampus.failureMessage = error.getMessage();
                }
            }, true);
        } catch (Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(getLocationsAndCampus);
    }
    @RequestMapping(value = "/getBlocksByCampus", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getBlocksByCampus(@RequestParam("campusId") String campusId) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            if(!(campusId.isEmpty())) {
                commonApiHandler.getBlockByCampus(campusId, result);
            }
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }


        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getRoomsByBlock", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getRoomsByBlock(@RequestParam("blockId") String blockId) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            if(!(blockId.isEmpty())) {
                commonApiHandler.getRoomsByBlock(blockId, result);
            }
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }


        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getFloorsByBlock", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getFloorsByBlock(@RequestParam("blockId") String blockId) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            if(!(blockId.isEmpty())) {
                commonApiHandler.getFloorsByBlock(blockId, result);
            }
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }


        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getAreaAndDistrict", method = RequestMethod.POST)
    public Mono<ApiResult<ErpPincodeDTO>> getAreaAndDistrict(@RequestParam("pincode") String pincode) {
        ApiResult<ErpPincodeDTO> result = new ApiResult<ErpPincodeDTO>();
        try {
            if(!(pincode.isEmpty())) {
                commonApiHandler.getAreaAndDistrict(pincode, result);
            }
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getSemesters", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getSemesters() {
        ApiResult<List<LookupItemDTO>> semeters = new ApiResult<List<LookupItemDTO>>();
        try {
            semeters = commonApiHandler.getSemesters();
            if (!Utils.isNullOrEmpty(semeters)) {
                semeters.success = true;
            } else {
                semeters.success = false;
            }
        } catch (Exception error) {
            semeters.success = false;
            semeters.dto = null;
            semeters.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(semeters);
    }
@EventListener(ApplicationReadyEvent.class)
    @RequestMapping(value = "/getPhoneCode", method = RequestMethod.POST)
    @SuppressWarnings("unchecked")
    public Mono<ApiResult<List<CommonDTO>>> getPhoneCode() {
        ApiResult<List<CommonDTO>> countries = new ApiResult<List<CommonDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Query query = context.createNativeQuery(CommonQueries.SELECT_COUNTRY_AND_NATIONALITY, Tuple.class);
					List<Tuple> mappings = query.getResultList();
                    if(mappings != null && mappings.size() > 0) {
                        countries.success = true;
                        countries.dto = new ArrayList<>();
                        for(Tuple mapping : mappings) {
                            CommonDTO common = new CommonDTO();
                            if(!Utils.isNullOrEmpty(mapping.get("phoneCode"))) {
                            	 String phonecode = !Utils.isNullOrEmpty(mapping.get("phoneCode")) ? String.valueOf(mapping.get("phoneCode")):"";
                            	 common.value = String.valueOf(mapping.get("ID"));
                                 common.label = phonecode;
                                 common.phoneCode = String.valueOf(mapping.get("Text"))+"("+phonecode+")";	
                                 countries.dto.add(common);
                            }
                        }
                    }
                }
                @Override
                public void onError(Exception error) {
                    countries.success = false;
                    countries.dto = null;
                    countries.failureMessage = error.getMessage();
                }
            }, true); 
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(countries);
    }
    
    @RequestMapping(value = "/getProgramPreference", method = RequestMethod.POST)
    public Mono<ApiResult<List<ProgramPreferenceDTO>>> getProgramPreference(@RequestParam("yearValue") Integer yearValue) {
        ApiResult<List<ProgramPreferenceDTO>> result = new ApiResult<List<ProgramPreferenceDTO>>();
        try {
            commonApiHandler.getProgramPreference(result,yearValue);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getBlocks", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getBlocks() {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            commonApiHandler.getBlocks(result);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getErpProgrammeDegree", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getErpProgrammeDegree() {
        ApiResult<List<LookupItemDTO>> locations = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Utils.getDropdownData(locations, context, CommonQueries.SELECT_ERP_PROGRAMME_DEGREE, null);
                }
                @Override
                public void onError(Exception error) {
                    locations.success = false;
                    locations.dto = null;
                    locations.failureMessage = error.getMessage();
                }
            }, true);
        }
        catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(locations);
    }

    @RequestMapping(value = "/getPaymentMode", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getPaymentMode() {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            commonApiHandler.getPaymentMode(result);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getSelectionProcessType", method = RequestMethod.POST)
    public Mono<ApiResult<List<AdmissionSelectionProcessTypeDTO>>> getSelectionProcessType() {
        ApiResult<List<AdmissionSelectionProcessTypeDTO>> result = new ApiResult<List<AdmissionSelectionProcessTypeDTO>>();
        try {
            commonApiHandler.getSelectionProcessType(result);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getVenueCityList", method = RequestMethod.POST)
    public Mono<ApiResult<List<AdmSelectionProcessVenueCityDTO>>> getVenueCityList() {
        ApiResult<List<AdmSelectionProcessVenueCityDTO >> result = new ApiResult<List<AdmSelectionProcessVenueCityDTO>>();
        try {
            commonApiHandler.getVenueCityList(result);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getTemplateNamesforaGroup", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getTemplateNamesforaGroup(@RequestParam("groupCode") String groupCode) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            commonApiHandler.getTemplateNamesforaGroup(result,groupCode);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getAllAccAccount",method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getAllAccAccount() {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<>();
        try {
            List<LookupItemDTO> data = commonApiHandler.getAllAccAccount();
            if(!Utils.isNullOrEmpty(data)) {
                result.success = true;
                result.dto = data;
            }
            else {
                result.success = false;
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getCurrentAcademicYear", method = RequestMethod.POST)
    public Mono<ApiResult<LookupItemDTO>> getCurrentAcademicYear() {
        ApiResult<LookupItemDTO> result = new ApiResult<LookupItemDTO>();
        try {
            LookupItemDTO currentAcademicYear = commonApiHandler.getCurrentAcademicYear();
            if (!Utils.isNullOrEmpty(currentAcademicYear)) {
                result.success = true;
                result.dto = currentAcademicYear;
            } else {
                result.success = false;
            }
        } catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    /*@RequestMapping(value = "/getPreferredCampus", method = RequestMethod.POST)
    public Mono<ApiResult<List<ErpUsersPreferredCampusDTO>>> getPreferredCampus(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult<List<ErpUsersPreferredCampusDTO>> result = new ApiResult<>();
        List<ErpUsersPreferredCampusDTO> erpUsersPreferredCampusDTO = new ArrayList<>();
        try {
            erpUsersPreferredCampusDTO = commonApiHandler.getPreferredCampus(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!Utils.isNullOrEmpty(erpUsersPreferredCampusDTO)) {
            result.dto = erpUsersPreferredCampusDTO;
            result.success = true;
        } else {
            result.dto = null;
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }
*/
   /* @RequestMapping(value = "/savePreferredCampus", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> savePreferredCampus(@RequestBody List<ErpUsersPreferredCampusDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            ApiResult<ModelBaseDTO> to = commonApiHandler.savePreferredCampus(data, userId);
            if(to.failureMessage==null || to.failureMessage.isEmpty()){
                result.success = true;
            }
            else{
                result.success = false;
                result.failureMessage = to.failureMessage;
            }
        } catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }*/

    @RequestMapping(value = "/getSysRoleGroup",method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getSysRoleGroup() {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<>();
        try {
            List<LookupItemDTO> data = commonApiHandler.getSysRoleGroup();
            if(!Utils.isNullOrEmpty(data)) {
                result.success = true;
                result.dto = data;
            }
            else {
                result.success = false;
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getRoles",method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getRoles() {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<>();
        try {
            List<LookupItemDTO> data = commonApiHandler.getRoles();
            if(!Utils.isNullOrEmpty(data)) {
                result.success = true;
                result.dto = data;
            }
            else {
                result.success = false;
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

//    @RequestMapping(value = "/getUserAndRoles",method = RequestMethod.POST)
//    public Mono<ApiResult<List<SysRoleDTO>>> getUserAndRoles() {
//        ApiResult<List<SysRoleDTO>> result = new ApiResult<>();
//        try {
//            List<SysRoleDTO> data = commonApiHandler.getUserAndRoles();
//            if(!Utils.isNullOrEmpty(data)) {
//                result.success = true;
//                result.dto = data;
//            }
//            else {
//                result.success = false;
//            }
//        }
//        catch (Exception error) {
//            result.success = false;
//            result.dto = null;
//            result.failureMessage = error.getMessage();
//        }
//        return Utils.monoFromObject(result);
//    }

    @RequestMapping(value = "/getEmployeesAndUsers", method = RequestMethod.POST)
    public Mono<ApiResult<List<ErpUsersDTO>>> getEmployeesAndUsers(@RequestParam("employeeId") String employeeId, @RequestParam("isActive") boolean isActive) {
        ApiResult<List<ErpUsersDTO>> employeeOrusersList = new ApiResult<>();
        try {
            List<ErpUsersDTO> list = commonApiHandler.getEmployeesAndUsers(employeeId, isActive);
            if (!Utils.isNullOrEmpty(list)) {
                employeeOrusersList.dto = list;
                employeeOrusersList.success = true;
            } else {
                employeeOrusersList.success = false;
            }
        } catch (Exception error) {
            employeeOrusersList.success = false;
            employeeOrusersList.dto = null;
            employeeOrusersList.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(employeeOrusersList);
    }

    @RequestMapping(value = "/getEmployees", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getEmployees() {
        ApiResult<List<LookupItemDTO>> employeeOrusersList = new ApiResult<List<LookupItemDTO>>();
        try {
            employeeOrusersList = commonApiHandler.getEmployees(employeeOrusersList);
            if (!Utils.isNullOrEmpty(employeeOrusersList.dto)) {
                employeeOrusersList.success = true;
            } else {
                employeeOrusersList.success = false;
            }
        } catch (Exception error) {
            employeeOrusersList.success = false;
            employeeOrusersList.dto = null;
            employeeOrusersList.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(employeeOrusersList);
    }


    @RequestMapping(value = "/getCampusDetails", method = RequestMethod.POST)
    public Mono<ApiResult<List<ErpCampusDTO>>> getCampusDetails() {
        ApiResult<List<ErpCampusDTO>> campus = new ApiResult<List<ErpCampusDTO>>();
        try {
            campus.dto = commonApiHandler.getCampusDetails();
            if (!Utils.isNullOrEmpty(campus.dto)) {
                campus.success = true;
            } else {
                campus.success = false;
            }
        } catch (Exception error) {
            campus.success = false;
            campus.dto = null;
            campus.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(campus);
    }
    

    @RequestMapping(value = "/getCampusProgrammeMapping", method = RequestMethod.POST)
   	public Mono<ApiResult<List<ErpCampusProgrammeMappingDTO>>> getCampusProgrammeMapping() {
   		ApiResult<List<ErpCampusProgrammeMappingDTO>> result = new ApiResult<List<ErpCampusProgrammeMappingDTO>>();
   		try {
   			result.dto = commonApiHandler.getCampusProgrammeMapping();
   			if (!Utils.isNullOrEmpty(result.dto)) {
   				result.success = true;
   			} else {
   				result.success = false;
   			}
   		} catch (Exception error) {
   			result.success = false;
   			result.dto = null;
   			result.failureMessage = error.getMessage();
   		}
   		return Utils.monoFromObject(result);
   	}
 
    @PostMapping(value = "/getProgrammeLevel")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LookupItemDTO> getProgrammeLevel() {
        return commonApiHandler.getProgrammeLevel().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getProgrammeDegreeByLevel")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LookupItemDTO> getProgrammeDegreeByLevel(@RequestParam int id) {
        return commonApiHandler.getProgrammeDegreeByLevel(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getProgrammeByDegreeAndLevel")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LookupItemDTO> getProgrammeByDegreeAndLevel(@RequestParam int degreeId) {
        return commonApiHandler.getProgrammeByDegreeAndLevel(degreeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getErpReservationCategory")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LookupItemDTO> getErpReservationCategory() {
        return commonApiHandler.getErpReservationCategory().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getErpInstitution")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LookupItemDTO> getErpInstitution() {
        return commonApiHandler.getErpInstitution().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getErpResidentCategory")
    public Flux<LookupItemDTO> getErpResidentCategory() {
        return commonApiHandler.getErpResidentCategory().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getProgramPreferenceByProgram")
    public Flux<ProgramPreferenceDTO> getProgramPreferenceByProgram(@RequestParam int programId) {
    	 return commonApiHandler.getProgramPreferenceByProgram(programId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getAcademicYearNew")
    public Flux<ErpAcademicYearDTO> getAcademicYearNew() {
    	 return commonApiHandler.getAcademicYear().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getStudentApplicationNumbers1")
    public Flux<LookupItemDTO> getStudentApplicationNumbers1(@RequestParam String applicationNumber, @RequestParam String yearId) {
        return commonApiHandler.getStudentApplicationNumbers1(applicationNumber, yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
//    
//        @PostMapping(value = "/getStudentNames")
//        public Flux<LookupItemDTO> getStudentNames(@RequestParam String studentName) {
//            return commonApiHandler.getStudentNames(studentName).switchIfEmpty(Mono.error(new NotFoundException(null)));
//        }

    @RequestMapping(value = "/getApplicationNumbers", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getApplicationNumbers(@RequestParam("applicationNumber") String applicationNumber,@RequestParam(required =false) String yearId) {
        ApiResult<List<LookupItemDTO>> list = new ApiResult<>();
        try {
            commonApiHandler.getStudentApplicationNumbers(applicationNumber,list,yearId);
        } catch (Exception error) {
            list.success = false;
            list.dto = null;
            list.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(list);
    }

    @RequestMapping(value = "/getApplicantNames", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getApplicantNames(@RequestParam("applicantName") String applicantName,@RequestParam(required =false) String yearId) {
        ApiResult<List<LookupItemDTO>> list = new ApiResult<>();
        try {
            commonApiHandler.getApplicantNames(applicantName,list,yearId);
        } catch (Exception error) {
            list.success = false;
            list.dto = null;
            list.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(list);
    }
    @PostMapping(value="/getCampusByEmployee")
	public Mono<SelectDTO> getCampusByEmployee(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return commonApiHandler.getCampusByEmployee(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
    
    @PostMapping(value = "/getLevels") 
    public Flux<SelectDTO> getLevels() {
    	 return commonApiHandler.getLevels().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    
	@PostMapping(value = "/getProgrammeByLevelAndCampus") 
    public Flux<SelectDTO> getProgrammeByLevelAndCampus(@RequestParam String level,@RequestParam String campus) {
    	 return commonApiHandler.getProgrammeByLevelAndCampus(level,campus).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getErpWorkFlowProcess")
    public Flux<LookupItemDTO> getErpWorkFlowProcess() {
    	 return commonApiHandler.getErpWorkFlowProcess().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getSMSorEmailTemplate")
    public Flux<LookupItemDTO> getSMSorEmailTemplate(@RequestParam String Type) {
    	 return commonApiHandler.getSMSorEmailTemplate(Type).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getAdmissionCategory") 
    public Flux<SelectDTO> getAdmissionCategory() {
    	 return commonApiHandler.getAdmissionCategory().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
	@PostMapping(value = "/getActiveProgrammeByYearValue") 
    public Flux<SelectDTO> getActiveProgrammeByYearValue(@RequestParam String yearValue) {
    	 return commonApiHandler.getActiveProgrammeByYearValue(yearValue).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getCampusBySelectedProgramme") 
    public Flux<SelectDTO> getCampusBySelectedProgramme(@RequestParam String programmeId) {
    	 return commonApiHandler.getCampusBySelectedProgramme(programmeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
    @PostMapping(value = "/getProgrammeByLevelOrCampus") 
    public Flux<SelectDTO> getProgrammeByLevelOrCampus(@RequestParam String level,@RequestParam String campus) {
    	 return commonApiHandler.getProgrammeByLevelOrCampus(level,campus).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getDegreeByLevelAndLocationOrCampus") 
    public Flux<SelectDTO> getDegreeByLevelAndLocationOrCampus(@RequestParam String levelId,@RequestParam List<Integer> campusId,@RequestParam List<Integer> locationId) {
    	 return commonApiHandler.getDegreeByLevelAndLocationOrCampus(levelId,campusId,locationId).switchIfEmpty(Mono.error(new NotFoundException("Degree not found")));
    }
	
	@PostMapping(value = "/getProgrammeByDegreeAndLocationOrCampus") 
    public Flux<SelectDTO> getProgrammeByDegreeAndLocationOrCampus(@RequestParam String degreeId,@RequestParam List<Integer> campusId,@RequestParam List<Integer> locationId) {
    	 return commonApiHandler.getProgrammeByDegreeAndLocationOrCampus(degreeId,campusId,locationId).switchIfEmpty(Mono.error(new NotFoundException("Programme not found")));
    }

    @PostMapping(value = "/getProgramPreferenceWithBatch")
    public Flux<ProgramPreferenceDTO> getProgramPreferenceWithBatch(@RequestParam("academicYear") String academicYear) {
        return commonApiHandler.getProgramPreferenceWithBatch(academicYear).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getRegisterNumbers")
    public Flux<SelectDTO> getRegisterNumbers(@RequestParam String registerNumber, @RequestParam String yearId) {
        return commonApiHandler.getRegisterNumbers(registerNumber, yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }	
	
	@PostMapping(value = "/getDataByApplnNoOrRegisterNumbersOrName")
    public Flux<StudentApplnEntriesDTO> getDataByApplnNoOrRegisterNumbersOrName(@RequestParam String data, @RequestParam String yearId) {
        return commonApiHandler.getDataByApplnNoOrRegisterNumbersOrName(data, yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getCampusForLocation")
	public Flux<SelectDTO> getCampusForLocation(@RequestParam String locId){
        return commonApiHandler.getCampusForLocation(locId).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/isUserAdded")
	public Mono<ResponseEntity<ApiResult>> isUserAdded(@RequestParam Integer userId) {
		return commonApiHandler.isUserAdded(userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/getPincodeDetailsOrValidate")
    public Flux<Object> getPincodeDetailsOrValidate(@RequestParam String pincode, @RequestParam Boolean isPincodeList) {
        return commonApiHandler.getPincodeDetailsOrValidate(pincode,isPincodeList).switchIfEmpty(Mono.error(new NotFoundException("Invalid pincode")));
    }
	
	@PostMapping(value = "/getErpRoomByBlock")
	public Flux<SelectDTO> getErpRoomByBlock(@RequestParam String blockId){
        return commonApiHandler.getErpRoomByBlock(blockId).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getErpCampusDepartmentMapping")
	public Flux<SelectDTO> getErpCampusDepartmentMapping(){
        return commonApiHandler.getErpCampusDepartmentMapping().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getUserSpecificCampusList")
	public Flux<SelectDTO> getUserSpecificCampusList(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        return commonApiHandler.getUserSpecificCampusList(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getInstitutionList")
	public Flux<SelectDTO> getInstitutionList(@RequestParam(required=false) String countryId,@RequestParam(required=false) String stateId , @RequestParam(required=false) String name, String boardType){
        return commonApiHandler.getInstitutionList(name,countryId,stateId,boardType).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getCityAndStateByPincode")
    public Mono<ErpCityDTO> getCityAndStateByPincode(@RequestParam String pincodeId) {
        return commonApiHandler.getCityAndStateByPincode(pincodeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getLocationOrCampusByProgramme")
	public Flux<SelectDTO> getLocationOrCampusByProgramme(@RequestParam String yearValue, @RequestParam String programId, @RequestParam(required =false) Boolean isLocation) {
        return commonApiHandler.getLocationOrCampusByProgramme(yearValue, programId, isLocation).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getEmployeeOrUsers1")
	public Flux<SelectDTO> getEmployeesOrUsers1(){
        return commonApiHandler.getEmployeeOrUsers1().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getGraduateAttributes")
	public Flux<SelectDTO> getGraduateAttributes(){
        return commonApiHandler.getGraduateAttributes().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getApproverLevel")
	public Flux<SelectDTO> getApproverLevel(){
        return commonApiHandler.getApproverLevel().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getDepartmentByUser")
	public Flux<SelectDTO> getDepartmentByUser(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        return commonApiHandler.getDepartmentByUser(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getApplicationCancellationReasons")
	public Flux<SelectDTO> getApplicationCancellationReasons(){
        return commonApiHandler.getApplicationCancellationReasons().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getErpTemplateByGroupCode")
    public Flux<ErpTemplateDTO> getErpTemplateByGroupCode(@RequestParam String groupCode) {
        return commonApiHandler.getErpTemplateByGroupCode(groupCode).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getUniversityBoardList")
    public Flux<SelectDTO> getUniversityBoardList(@RequestParam(required=false) Integer countryId,@RequestParam(required=false) Integer stateId, String boardType, @RequestParam(required=false) String name) {
        return commonApiHandler.getUniversityBoardList(countryId,stateId,boardType,name).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getEmployeemByUserId")
    public Mono<SelectDTO> getEmployeemByUserId(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return commonApiHandler.getEmployeemByUserId(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getSubModulesList")
    public Flux<SelectDTO> getSubModulesList() {
        return commonApiHandler.getSubModulesList().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	@PostMapping(value = "/getFolderListForMenu")
	public Flux<URLFolderListDTO> getFolderListForMenu(@RequestParam String processCode){
        return commonApiHandler.getFolderListForMenu(processCode).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getEmployeeOrUserWithDepartment")
	public Flux<SelectDTO> getEmployeeOrUserWithDepartment(){
        return commonApiHandler.getEmployeeOrUserWithDepartment().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getUserDepartment")
    public Mono<SelectDTO> getUserDepartment(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return commonApiHandler.getUserDepartment(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getUserLocation")
    public Mono<SelectDTO> getUserLocation(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return commonApiHandler.getUserLocation(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getUserAndRoles")
    public Mono<List<SysRoleDTO>> getUserAndRoles() {
        return commonApiHandler.getUserAndRoles().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getSubjectCategorySpecialization")
    public Flux<SelectDTO> getSubjectCategorySpecialization(@RequestParam List<Integer> subjectCategoryIds) {
        return commonApiHandler.getSubjectCategorySpecialization(subjectCategoryIds).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getDepartmentFromCampusDeptMap")
	 public Mono<SelectDTO> getDepartmentFromCampusDeptMap(@RequestParam String campusDeptMappingId) {
       return commonApiHandler.getDepartmentFromCampusDeptMap(campusDeptMappingId).switchIfEmpty(Mono.error(new NotFoundException(null)));
   }
	
	@PostMapping(value = "/getUsersEmployeeDepartment")
	public Flux<SelectDTO> getUsersEmployeeDepartment() throws Exception {
		return commonApiHandler.getUsersEmployeeDepartment().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	@PostMapping(value = "/getCountryCode")
	public Mono<List<SelectDTO>> getCountryCode() throws Exception {
		return commonApiHandler.getCountryCode().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

    @PostMapping(value = "/getErpBlocks")
    public Mono<List<SelectDTO>> getErpBlocks(Integer campusId) throws Exception {
        return commonApiHandler.getErpBlocks(campusId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }
    @PostMapping(value = "/getErpFloors")
    public Mono<List<SelectDTO>> getErpFloors(Integer blockId) throws Exception {
        return commonApiHandler.getErpBlocks(blockId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }
    @PostMapping(value = "/getErpRoomTypes")
    public Mono<List<SelectDTO>> getErpRoomTypes() throws Exception {
        return commonApiHandler.getErpRoomTypes().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }
    @PostMapping(value = "/getErpRooms")
    public Mono<List<SelectDTO>> getErpRooms(Integer roomTypeId, Integer blockId, Integer floorId) throws Exception {
        return commonApiHandler.getErpRooms(roomTypeId, blockId, floorId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }

    @PostMapping(value = "/getDepartmentByCampusId")
    public Flux<SelectDTO> getDepartmentByCampusId(@RequestParam String campusId) throws Exception {
        return commonApiHandler.getDepartmentByCampusId(Integer.parseInt(campusId)).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }
    @PostMapping(value = "/getErpTemplateByGroupCodeAndProg")
    public Flux<ErpTemplateDTO> getErpTemplateByGroupCodeAndProg(@RequestParam String groupCode,@RequestParam String programmeId) {
        return commonApiHandler.getErpTemplateByGroupCodeAndProg(groupCode,programmeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getProgrammeByLevelAndYear")
    public Flux<SelectDTO> getProgrammeByLevelAndYear(@RequestParam(required = false) String levelId, @RequestParam String yearId)  {
        return commonApiHandler.getProgrammeByLevelAndYear(levelId,yearId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }

    @PostMapping(value = "/getApplicationDeclarations")
    public Flux<SelectDTO> getApplicationDeclarations() {
        return commonApiHandler.getApplicationDeclarations().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }

    @PostMapping(value = "/getCampusProgrammeByYearAndProgrammeLevel")
    public Flux<SelectDTO> getCampusProgrammeByYearAndProgrammeLevel(@RequestParam (required = false) String levelId, @RequestParam String yearId)  {
        return commonApiHandler.getCampusProgrammeByYearAndProgrammeLevel(levelId,yearId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }

    @PostMapping(value = "/getCampusForLocations")
    public Flux<SelectDTO> getCampusForLocations(@RequestParam List<Integer> locIds){
        return commonApiHandler.getCampusForLocations(locIds).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getHolidaysByCampusAndYear")
    public Flux<SelectDTO> getHolidaysByCampusAndYear(@RequestParam String campusId,@RequestParam String year) throws Exception {
        return commonApiHandler.getHolidaysByCampusAndYear(campusId,year).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }
}