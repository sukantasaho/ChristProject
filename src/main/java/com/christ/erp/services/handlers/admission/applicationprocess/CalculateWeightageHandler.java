package com.christ.erp.services.handlers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentEducationalMarkDetailsDBO;
import com.christ.erp.services.dto.admission.applicationprocess.CalculateWeightageDTOWebFlux;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.helpers.admission.applicationprocess.CalculateWeightageHelper;
import com.christ.erp.services.transactions.admission.applicationprocess.CalculateWeightageTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CalculateWeightageHandler {

    @Autowired
    private CalculateWeightageHelper helper;

    @Autowired
    private CalculateWeightageTransaction transaction;

    public Mono<ApiResult> saveOrUpdate(Mono<CalculateWeightageDTOWebFlux> data, String userId) {
        Set<StudentApplnEntriesDBO> studentApplnEntriesDBOSet = new HashSet<>();
        Map<Integer, Tuple4<Integer, Integer, Integer, Map<String, Map<Integer, String>>>> erpCampusMappingWeigtageDefinitionMap = new HashMap<>();
        Mono<CalculateWeightageDTOWebFlux> calculateWeightageDTO = data.handle((calculateWeightageDTOWebFlux, synchronousSink) -> {
            int academicYearId = Integer.parseInt(calculateWeightageDTOWebFlux.getAcademicYear().id);
            int sessionId = Integer.parseInt(calculateWeightageDTOWebFlux.getSession().id);
            List<Integer> erpCampusProgrammeMappingIds;
            if(!Utils.isNullOrEmpty(calculateWeightageDTOWebFlux.getProgramPreference())){
                erpCampusProgrammeMappingIds = calculateWeightageDTOWebFlux.getProgramPreference().stream().filter(campusMappingId -> !Utils.isNullOrEmpty(campusMappingId))
                    .map(ProgramPreferenceDTO::getCampusMappingId).map(Integer::parseInt).collect(Collectors.toList());
            }else{
                erpCampusProgrammeMappingIds = transaction.getErpCampusProgrammeMappingIds(sessionId);
            }
            String errorMsg = "";
            List<AdmWeightageDefinitionDBO> admWeightageDefinitionDBOS = transaction.getAdmWeightageDefinitionDBOS(erpCampusProgrammeMappingIds,academicYearId);
            if(!Utils.isNullOrEmpty(admWeightageDefinitionDBOS)){
                List<StudentApplnEntriesDBO> studentApplnEntriesDBOList = transaction.getStudentApplnEntries(sessionId,erpCampusProgrammeMappingIds);
                if(Utils.isNullOrEmpty(studentApplnEntriesDBOList)){
                    errorMsg = "No students found in this ";
                    if(!calculateWeightageDTOWebFlux.getSession().getText().contains("session"))
                        errorMsg = "No students found in this "+ calculateWeightageDTOWebFlux.getSession().getText() + " session" ;
                    else
                        errorMsg += calculateWeightageDTOWebFlux.getSession().getText();
                }else{
                    helper.setCampusMappingWeightageDefinitionMap(admWeightageDefinitionDBOS,erpCampusMappingWeigtageDefinitionMap);
                    studentApplnEntriesDBOSet.addAll(studentApplnEntriesDBOList);
                    synchronousSink.next(calculateWeightageDTOWebFlux);
                }
            }else{
                errorMsg = erpCampusProgrammeMappingIds.size()>1 ? "Weightage definitions not found for the selected programmes" : "Weightage definition not found for the selected programme";
            }
            synchronousSink.error(new NotFoundException(errorMsg));
        }).cast(CalculateWeightageDTOWebFlux.class);
        return calculateWeightageDTO.map(calculateWeightageDTOWebFlux -> Mono.just(calculateWeightage(calculateWeightageDTOWebFlux, studentApplnEntriesDBOSet, erpCampusMappingWeigtageDefinitionMap, userId)))
            .flatMap(t -> t.flatMap(studentApplnEntriesDBO -> {
                transaction.saveWeightageScore(studentApplnEntriesDBO);
                return Mono.just(Boolean.TRUE);
            })).map(Utils::responseResult);
    }

    private Set<StudentApplnEntriesDBO> calculateWeightage(CalculateWeightageDTOWebFlux calculateWeightageDTOWebFlux, Set<StudentApplnEntriesDBO> studentApplnEntriesDBOSet,
            Map<Integer, Tuple4<Integer, Integer, Integer, Map<String, Map<Integer, String>>>> erpCampusMappingWeigtageDefinitionMap, String userId) {
        //StudentApplnEntriesDBO studentApplnEntriesDBO1 = new StudentApplnEntriesDBO();
        studentApplnEntriesDBOSet.forEach(studentApplnEntriesDBO -> {
            if(Utils.isNullOrEmpty(studentApplnEntriesDBO.totalWeightage) && !Utils.isNullOrEmpty(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO())){
                double totalScore = 0;
                int totalPreRequesiteScore = 0;
                AtomicReference<Double> totalEducationalScore = new AtomicReference<>((double) 0);
                AtomicReference<Double> totalInterviewWeightageScore = new AtomicReference<>((double) 0);
                Tuple4<Integer, Integer, Integer, Map<String, Map<Integer, String>>> tuples = erpCampusMappingWeigtageDefinitionMap.get(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getId());
                if(!Utils.isNullOrEmpty(tuples.getT4().get("PreRequisiteWeigtage"))){
                    Map<Integer, String> scoreMap = tuples.getT4().get("PreRequisiteWeigtage");
                    if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentApplnPrerequisiteDBO()) && studentApplnEntriesDBO.getStudentApplnPrerequisiteDBO().getRecordStatus() == 'A'){
                        if(scoreMap.containsKey(studentApplnEntriesDBO.getStudentApplnPrerequisiteDBO().getId())){
//                            totalPreRequesiteScore = (studentApplnEntriesDBO.getStudentApplnPrerequisiteDBO().getMarksObtained()/100) * Integer.parseInt(scoreMap.get(studentApplnEntriesDBO.getStudentApplnPrerequisiteDBO().getId()));
                        }
                    }
                }
                System.out.println("totalPreRequesiteScore : "+totalPreRequesiteScore);
                if(!Utils.isNullOrEmpty(tuples.getT4().get("EducationWeightage"))){
                    Map<Integer, String> scoreMap = tuples.getT4().get("EducationWeightage");
                    if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentEducationalDetailsDBOS())){
                        studentApplnEntriesDBO.getStudentEducationalDetailsDBOS().stream().filter(studentEducationalDetailsDBO -> studentEducationalDetailsDBO.getRecordStatus() == 'A')
                        .forEach(studentEducationalDetailsDBO -> {
                            if(scoreMap.containsKey(studentEducationalDetailsDBO.getAdmQualificationListDBO().getId())){
                                double avgMark = 0.0;
                                if(!Utils.isNullOrEmpty(studentEducationalDetailsDBO.getConsolidatedMarksObtained())
                                    && !Utils.isNullOrEmpty(studentEducationalDetailsDBO.getConsolidatedMaximumMarks())){
//                                    avgMark = ((double) studentEducationalDetailsDBO.getConsolidatedMarksObtained() / (double) studentEducationalDetailsDBO.getConsolidatedMaximumMarks()) * 100;
                                }else if(!Utils.isNullOrEmpty(studentEducationalDetailsDBO.getStudentEducationalMarkDetailsDBOSet())){
//                                    int totalMarksObtained = studentEducationalDetailsDBO.getStudentEducationalMarkDetailsDBOSet().stream()
//                                        .filter(studentEducationalMarkDetailsDBO -> !Utils.isNullOrEmpty(studentEducationalMarkDetailsDBO) && !Utils.isNullOrEmpty(studentEducationalMarkDetailsDBO.getMarksObtained()))
//                                        .mapToInt(StudentEducationalMarkDetailsDBO::getMarksObtained).sum();
//                                    int totalMaxMarks = studentEducationalDetailsDBO.getStudentEducationalMarkDetailsDBOSet().stream()
//                                            .filter(studentEducationalMarkDetailsDBO -> !Utils.isNullOrEmpty(studentEducationalMarkDetailsDBO) && !Utils.isNullOrEmpty(studentEducationalMarkDetailsDBO.getMaximumMarks()))
//                                            .mapToInt(StudentEducationalMarkDetailsDBO::getMaximumMarks).sum();
//                                    avgMark = ((double) totalMarksObtained / (double) totalMaxMarks) * 100;
                                }
                                totalEducationalScore.set(totalEducationalScore.get() + ((avgMark / 100) * Integer.parseInt(scoreMap.get(studentEducationalDetailsDBO.getAdmQualificationListDBO().getId()))));
                            }
                        });
                    }
                }
                System.out.println("totalEducationalScore : "+totalEducationalScore.get());
                if(!Utils.isNullOrEmpty(tuples.getT4().get("InterviewWeightage"))){
                    Map<Integer, String> scoreMap = tuples.getT4().get("InterviewWeightage");
                    if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getAdmSelectionProcessDBOS())){
                        studentApplnEntriesDBO.getAdmSelectionProcessDBOS().stream().filter(admSelectionProcessDBO -> admSelectionProcessDBO.getRecordStatus() == 'A')
                        .forEach(admSelectionProcessDBO -> {
                            int admSelectionProcessTypeDetailsId;
                            if(scoreMap.containsKey(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getId())){
                                int score = Integer.parseInt(scoreMap.get(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getId()).split("_")[1]);
                                admSelectionProcessTypeDetailsId = Integer.parseInt(scoreMap.get(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getId()).split("_")[0]);
                                if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessScoreDBOS())){
                                    admSelectionProcessDBO.getAdmSelectionProcessScoreDBOS().stream().filter(admSelectionProcessScoreDBO -> admSelectionProcessScoreDBO.getRecordStatus() == 'A')
                                    .forEach(admSelectionProcessScoreDBO -> {
                                        if(admSelectionProcessScoreDBO.getAdmSelectionProcessTypeDetailsDBO().getId().equals(admSelectionProcessTypeDetailsId)){
                                            if(!Utils.isNullOrEmpty(admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet())){
                                                AtomicReference<Double> totalPercentageScore = new AtomicReference<>((double) 0);
                                                admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet().stream().filter(admSelectionProcessScoreEntryDBO -> admSelectionProcessScoreEntryDBO.getRecordStatus() == 'A').forEach(admSelectionProcessScoreEntryDBO -> {
                                                    totalPercentageScore.set(totalPercentageScore.get() + (admSelectionProcessScoreEntryDBO.getScoreEntered().doubleValue() / admSelectionProcessScoreEntryDBO.getMaxScore().doubleValue()));
                                                });
                                                int count = Integer.parseInt(String.valueOf(admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet().stream().filter(admSelectionProcessScoreEntryDBO -> admSelectionProcessScoreEntryDBO.getRecordStatus() == 'A').count()));
                                                totalInterviewWeightageScore.set(totalInterviewWeightageScore.get() + ((totalPercentageScore.get() / count) * score));
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
                System.out.println("totalInterviewWeightageScore : "+totalInterviewWeightageScore.get());
                totalScore = ((double) totalPreRequesiteScore * tuples.getT1()) + (totalEducationalScore.get() * tuples.getT1()) + (totalInterviewWeightageScore.get() * tuples.getT1());
                System.out.println("totalScore : "+totalScore);
                studentApplnEntriesDBO.setTotalWeightage(new BigDecimal(totalScore));
            }
        });
        //transaction.saveWeightageScore(studentApplnEntriesDBOSet);
        return studentApplnEntriesDBOSet;
    }
}
