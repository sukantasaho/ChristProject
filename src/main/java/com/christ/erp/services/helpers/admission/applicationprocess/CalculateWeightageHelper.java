package com.christ.erp.services.helpers.admission.applicationprocess;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionGeneralDBO;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalculateWeightageHelper {

    public void setGeneralWeightageScore(Map<Integer, String> scoreMap, AdmWeightageDefinitionGeneralDBO admWeightageDefinitionGeneralDBO) {
        if(!Utils.isNullOrEmpty(admWeightageDefinitionGeneralDBO.getErpReligionDBO())){
            scoreMap.put(admWeightageDefinitionGeneralDBO.getErpReligionDBO().getId(),String.valueOf(admWeightageDefinitionGeneralDBO.getScore()));
        }else if(!Utils.isNullOrEmpty(admWeightageDefinitionGeneralDBO.getErpReservationCategoryDBO())){
            scoreMap.put(admWeightageDefinitionGeneralDBO.getErpReservationCategoryDBO().getId(),String.valueOf(admWeightageDefinitionGeneralDBO.getScore()));
        }else if(!Utils.isNullOrEmpty(admWeightageDefinitionGeneralDBO.getErpGenderDBO())){
            scoreMap.put(admWeightageDefinitionGeneralDBO.getErpGenderDBO().getErpGenderId(),String.valueOf(admWeightageDefinitionGeneralDBO.getScore()));
        }else if(!Utils.isNullOrEmpty(admWeightageDefinitionGeneralDBO.getErpInstitutionDBO())){
            scoreMap.put(admWeightageDefinitionGeneralDBO.getErpInstitutionDBO().getId(),String.valueOf(admWeightageDefinitionGeneralDBO.getScore()));
        }else if(!Utils.isNullOrEmpty(admWeightageDefinitionGeneralDBO.getErpResidentCategoryDBO())){
            scoreMap.put(admWeightageDefinitionGeneralDBO.getErpResidentCategoryDBO().getId(),String.valueOf(admWeightageDefinitionGeneralDBO.getScore()));
        }else if(!Utils.isNullOrEmpty(admWeightageDefinitionGeneralDBO.getAdmQualificationDegreeListDBO())){
            scoreMap.put(admWeightageDefinitionGeneralDBO.getAdmQualificationDegreeListDBO().getId(),String.valueOf(admWeightageDefinitionGeneralDBO.getScore()));
        }else if(!Utils.isNullOrEmpty(admWeightageDefinitionGeneralDBO.getAdmWeightageDefinitionWorkExperienceDBO())){
            scoreMap.put(admWeightageDefinitionGeneralDBO.getAdmWeightageDefinitionWorkExperienceDBO().getId(),String.valueOf(admWeightageDefinitionGeneralDBO.getScore()));
        }
    }

    public void setCampusMappingWeightageDefinitionMap(List<AdmWeightageDefinitionDBO> admWeightageDefinitionDBOS,
        Map<Integer, Tuple4<Integer, Integer, Integer, Map<String, Map<Integer, String>>>> erpCampusMappingWeigtageDefinitionMap) {
        admWeightageDefinitionDBOS.forEach(admWeightageDefinitionDBO -> {
            Map<String, Map<Integer, String>> weigtageDefinitionTypeMap = new HashMap<>();
            if(!Utils.isNullOrEmpty(admWeightageDefinitionDBO.getAdmWeightageDefinitionDetailDBOsSet())){
                admWeightageDefinitionDBO.getAdmWeightageDefinitionDetailDBOsSet().forEach(admWeightageDefinitionDetailDBO -> {
                    if(!Utils.isNullOrEmpty(admWeightageDefinitionDetailDBO.getAdmPrerequisiteExamDBO())){
                        Map<Integer, String> scoreMap = new HashMap<>();
                        if(weigtageDefinitionTypeMap.containsKey("PreRequisiteWeigtage")){
                            scoreMap = weigtageDefinitionTypeMap.get("PreRequisiteWeigtage");
                            scoreMap.put(admWeightageDefinitionDetailDBO.getAdmPrerequisiteExamDBO().getId(),String.valueOf(admWeightageDefinitionDetailDBO.getScore()));
                        }else{
                            scoreMap.put(admWeightageDefinitionDetailDBO.getAdmPrerequisiteExamDBO().getId(),String.valueOf(admWeightageDefinitionDetailDBO.getScore()));
                        }
                        weigtageDefinitionTypeMap.put("PreRequisiteWeigtage",scoreMap);
                    }else if(!Utils.isNullOrEmpty(admWeightageDefinitionDetailDBO.getAdmQualificationListDBO())){
                        Map<Integer, String> scoreMap = new HashMap<>();
                        if(weigtageDefinitionTypeMap.containsKey("EducationWeightage")){
                            scoreMap = weigtageDefinitionTypeMap.get("EducationWeightage");
                            scoreMap.put(admWeightageDefinitionDetailDBO.getAdmQualificationListDBO().getId(),String.valueOf(admWeightageDefinitionDetailDBO.getScore()));
                        }else{
                            scoreMap.put(admWeightageDefinitionDetailDBO.getAdmQualificationListDBO().getId(),String.valueOf(admWeightageDefinitionDetailDBO.getScore()));
                        }
                        weigtageDefinitionTypeMap.put("EducationWeightage",scoreMap);
                    }else if(!Utils.isNullOrEmpty(admWeightageDefinitionDetailDBO.getAdmSelectionProcessPlanDetailDBO()) && !Utils.isNullOrEmpty(admWeightageDefinitionDetailDBO.getAdmSelectionProcessTypeDetailsDBO())){
                        Map<Integer, String> scoreMap = new HashMap<>();
                        if(weigtageDefinitionTypeMap.containsKey("InterviewWeightage")){
                            scoreMap = weigtageDefinitionTypeMap.get("InterviewWeightage");
                            scoreMap.put(admWeightageDefinitionDetailDBO.getAdmSelectionProcessPlanDetailDBO().getId(),""+ admWeightageDefinitionDetailDBO.getAdmSelectionProcessTypeDetailsDBO().getId()+"_"+admWeightageDefinitionDetailDBO.getScore());
                        }else{
                            scoreMap.put(admWeightageDefinitionDetailDBO.getAdmSelectionProcessPlanDetailDBO().getId(),""+ admWeightageDefinitionDetailDBO.getAdmSelectionProcessTypeDetailsDBO().getId()+"_"+admWeightageDefinitionDetailDBO.getScore());
                        }
                        weigtageDefinitionTypeMap.put("InterviewWeightage",scoreMap);
                    }
                });
            }
            if(!Utils.isNullOrEmpty(admWeightageDefinitionDBO.getAdmWeightageDefinitionGeneralDBOsSet())){
                admWeightageDefinitionDBO.getAdmWeightageDefinitionGeneralDBOsSet().forEach(admWeightageDefinitionGeneralDBO -> {
                    Map<Integer, String> scoreMap = new HashMap<>();
                    if(weigtageDefinitionTypeMap.containsKey("General")){
                        scoreMap = weigtageDefinitionTypeMap.get("General");
                        setGeneralWeightageScore(scoreMap,admWeightageDefinitionGeneralDBO);
                    }else{
                        setGeneralWeightageScore(scoreMap,admWeightageDefinitionGeneralDBO);
                    }
                    weigtageDefinitionTypeMap.put("General",scoreMap);
                });
            }
            if(!Utils.isNullOrEmpty(admWeightageDefinitionDBO.getAdmWeightageDefinitionLocationCampusDBOsSet())){
                admWeightageDefinitionDBO.getAdmWeightageDefinitionLocationCampusDBOsSet().forEach(admWeightageDefinitionLocationCampusDBO -> {
                    Tuple4<Integer, Integer, Integer, Map<String, Map<Integer, String>>> tuples = Tuples.of(admWeightageDefinitionDBO.getPreRequisiteWeigtageTotal(),admWeightageDefinitionDBO.getEducationWeightageTotal(),
                            admWeightageDefinitionDBO.getInterviewWeightageTotal(),weigtageDefinitionTypeMap);
                    erpCampusMappingWeigtageDefinitionMap.put(admWeightageDefinitionLocationCampusDBO.getId(),tuples);
                });
            }
        });
    }
}
