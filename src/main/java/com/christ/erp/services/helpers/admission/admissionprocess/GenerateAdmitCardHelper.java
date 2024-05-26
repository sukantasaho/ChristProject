package com.christ.erp.services.helpers.admission.admissionprocess;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanCenterBasedDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailProgDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.transactions.admission.applicationprocess.GenerateAdmitCardTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class GenerateAdmitCardHelper {

    @Autowired
    GenerateAdmitCardTransaction generateAdmitCardTransaction;

    public void validateApplicationNumbers(List<Integer> applnNumberList, StringBuffer errorBuffer, List<StudentApplnEntriesDBO> studentsList, Integer sessionId, Integer processTypeId, AdmSelectionProcessPlanDBO admSelectionProcessPlanDBO) {
        String errorMsg = "Admit card for below application numbers are already generated";
        String errorMsg1 = "Programmes for below application numbers are not applicable for the selection session/selection process: ";
        String errorMsg2 = "Below application numbers are not applied for the selected session: ";
        String errorMsg3 = "Below application numbers are not in Submitted Status";
        String errorMsg4 = "Below application numbers are not in Shortlisted Status";
        List<Integer> applnNoNotApplicableToSessionAndTypeList = new ArrayList<>();
        List<Integer> applnNoNotApplicableToSessionList = new ArrayList<>();
        List<Integer> applnNosAppliedList = new ArrayList<>();
        List<Integer> applnNoNotApplicableToCurrentStatusProcessOrder1List = new ArrayList<>();
        List<Integer> applnNoNotApplicableToCurrentStatusProcessOrder2List = new ArrayList<>();
        List<StudentApplnEntriesDBO> studentsListNew = new ArrayList<>();
        Set<AdmSelectionProcessPlanDetailProgDBO> admSelectionProcessPlanDetailProgDBOS = Utils.isNullOrEmpty(admSelectionProcessPlanDBO.getAdmSelectionProcessPlanDetailDBO()) ? null :
            admSelectionProcessPlanDBO.getAdmSelectionProcessPlanDetailDBO().stream()
                .filter(admSelectionProcessPlanDetailDBO -> admSelectionProcessPlanDetailDBO.getRecordStatus() == 'A')
                //.collect(Collectors.toList())
                //.stream()
                .map(AdmSelectionProcessPlanDetailDBO::getAdmSelectionProcessPlanDetailProgDBOs)
                .flatMap(admSelectionProcessPlanDetailProgDBOS1 -> admSelectionProcessPlanDetailProgDBOS1
                    .stream()
                    .filter(admSelectionProcessPlanDetailProgDBO -> admSelectionProcessPlanDetailProgDBO.getRecordStatus() == 'A'))
                .collect(Collectors.toSet());
        //validate application numbers
        studentsList.forEach(studentApplnEntriesDBO -> {
            if (!Utils.isNullOrEmpty(studentApplnEntriesDBO)) {
                if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.admSelectionProcessDBOS) && !Utils.isNullOrEmpty(studentApplnEntriesDBO.admSelectionProcessDBOS.
                        stream().filter(admSelectionProcessDBO -> admSelectionProcessDBO.getRecordStatus() == 'A').collect(Collectors.toSet()))) {
                    studentApplnEntriesDBO.admSelectionProcessDBOS.stream().filter(admSelectionProcessDBO -> admSelectionProcessDBO.getRecordStatus() == 'A').forEach(admSelectionProcessDBO -> {
                        if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO()) && admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getRecordStatus() == 'A'){
                            if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO())
                                    && admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getRecordStatus() == 'A') {
                                if (!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO())
                                        && admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getRecordStatus() == 'A') {
                                    if(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId() == sessionId
                                            && admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getId().equals(processTypeId)){
                                        if(!applnNosAppliedList.contains(studentApplnEntriesDBO.applicationNo)) {
                                            applnNosAppliedList.add(studentApplnEntriesDBO.applicationNo);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        if(Utils.isNullOrEmpty(applnNosAppliedList)) {
            studentsList.forEach(studentApplnEntriesDBO -> {
                if(!Utils.isNullOrEmpty(studentApplnEntriesDBO)){
                    studentsListNew.add(studentApplnEntriesDBO);
                    if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.studentApplnSelectionProcessDatesDBOS)){
                        studentApplnEntriesDBO.studentApplnSelectionProcessDatesDBOS.forEach(studentApplnSelectionProcessDatesDBO -> {
                            boolean needToCheck = false;
                            if(!Utils.isNullOrEmpty(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO())
                                    && studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getRecordStatus() == 'A'){
                                if(!Utils.isNullOrEmpty(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO())
                                        && studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getRecordStatus() == 'A'){
                                    if(!Utils.isNullOrEmpty(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO())
                                            && studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getRecordStatus() == 'A'){
                                        if(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder() == 1){
                                            if(!studentApplnEntriesDBO.applicantCurrentProcessStatus.processCode.equalsIgnoreCase("ADM_APPLN_SUBMITTED")){
                                                if(!applnNoNotApplicableToCurrentStatusProcessOrder1List.contains(studentApplnEntriesDBO.applicationNo)) {
                                                    applnNoNotApplicableToCurrentStatusProcessOrder1List.add(studentApplnEntriesDBO.applicationNo);
                                                }
                                            }else{
                                                needToCheck = true;
                                            }
                                        }else{
                                            if(!studentApplnEntriesDBO.applicantCurrentProcessStatus.processCode.equalsIgnoreCase("ADM_APPLN_SP_1_SHORTLIST")){
                                                if(!applnNoNotApplicableToCurrentStatusProcessOrder2List.contains(studentApplnEntriesDBO.applicationNo)) {
                                                    applnNoNotApplicableToCurrentStatusProcessOrder2List.add(studentApplnEntriesDBO.applicationNo);
                                                }
                                            }else{
                                                needToCheck = true;
                                            }
                                        }
                                        if(needToCheck){
                                            if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.erpCampusProgrammeMappingDBO)){
                                                if(!Utils.isNullOrEmpty(admSelectionProcessPlanDetailProgDBOS)){
                                                    if(admSelectionProcessPlanDetailProgDBOS.stream().filter(admSelectionProcessPlanDetailProgDBO -> admSelectionProcessPlanDetailProgDBO.getErpCampusProgrammeMappingDBO().getRecordStatus() == 'A')
                                                            .toList().stream().noneMatch(admSelectionProcessPlanDetailProgDBO ->
                                                                    admSelectionProcessPlanDetailProgDBO.getErpCampusProgrammeMappingDBO().getId() == studentApplnEntriesDBO.erpCampusProgrammeMappingDBO.id)){
                                                        if(!applnNoNotApplicableToSessionAndTypeList.contains(studentApplnEntriesDBO.applicationNo)) {
                                                            applnNoNotApplicableToSessionAndTypeList.add(studentApplnEntriesDBO.applicationNo);
                                                        }
                                                    }
                                                }
                                            }
                                            if(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId() != sessionId
                                                    && !studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getId().equals(processTypeId)){
                                                if(!applnNoNotApplicableToSessionAndTypeList.contains(studentApplnEntriesDBO.applicationNo)) {
                                                    applnNoNotApplicableToSessionAndTypeList.add(studentApplnEntriesDBO.applicationNo);
                                                }
                                            }else if(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId() != sessionId){
                                                if(!applnNoNotApplicableToSessionList.contains(studentApplnEntriesDBO.applicationNo)) {
                                                    applnNoNotApplicableToSessionList.add(studentApplnEntriesDBO.applicationNo);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                    studentsList.clear();
                    studentsList.addAll(studentsListNew);
                }
            });
        }
        if(!Utils.isNullOrEmpty(applnNosAppliedList)){
            errorBuffer.append(errorMsg).append(" : ").append(applnNosAppliedList.toString().replace("[", "").replace("]", ""));
        }
        if(!Utils.isNullOrEmpty(applnNoNotApplicableToSessionAndTypeList)){
            errorBuffer.append(errorMsg1).append(" : ").append(applnNoNotApplicableToSessionAndTypeList.toString().replace("[", "").replace("]", ""));
        }
        if(!Utils.isNullOrEmpty(applnNoNotApplicableToSessionList)){
            errorBuffer.append(errorMsg2).append(" : ").append(applnNoNotApplicableToSessionList.toString().replace("[", "").replace("]", ""));
        }
        if(!Utils.isNullOrEmpty(applnNoNotApplicableToCurrentStatusProcessOrder1List)){
            errorBuffer.append(errorMsg3).append(" : ").append(applnNoNotApplicableToCurrentStatusProcessOrder1List.toString().replace("[", "").replace("]", ""));
        }
        if(!Utils.isNullOrEmpty(applnNoNotApplicableToCurrentStatusProcessOrder2List)){
            errorBuffer.append(errorMsg4).append(" : ").append(applnNoNotApplicableToCurrentStatusProcessOrder2List.toString().replace("[", "").replace("]", ""));
        }
    }

    public Map<Integer, Map<String, Map<String, Map<Integer, Map<Integer, Tuple4<String,String,String,Integer>>>>>> SetDateTimeVenuePriorityForCenterBasedMap(AdmSelectionProcessPlanDBO admSelectionProcessPlanDBO) {
        //programme,date,time,venueid,priority,list of center info,
        Map<String, Map<String, Map<Integer, Map<Integer, Tuple4<String,String,String,Integer>>>>> dateTimeVenuePriorityMap = new TreeMap<>();
        Map<Integer, Map<String, Map<String, Map<Integer, Map<Integer, Tuple4<String,String,String,Integer>>>>>> programmeDateTimeVenuePriorityMap = new TreeMap<>();
        admSelectionProcessPlanDBO.getAdmSelectionProcessPlanDetailDBO().forEach(admSelectionProcessPlanDetailDBO -> {
            admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanDetailProgDBOs().stream().filter(admSelectionProcessPlanDetailProgDBO -> admSelectionProcessPlanDetailProgDBO.recordStatus=='A')
                .forEach(admSelectionProcessPlanDetailProgDBO -> {
                    //admSelectionProcessPlanDBO.getAdmSelectionProcessPlanDetailDBO().forEach(admSelectionProcessPlanDetailDBO -> {
                        if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode())){
                            if(!Utils.isNullOrEmpty(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanCenterBasedDBOs())){
                                admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanCenterBasedDBOs().stream().filter(admSelectionProcessPlanCenterBasedDBO -> admSelectionProcessPlanCenterBasedDBO.getRecordStatus() == 'A')
                                        .forEach(admSelectionProcessPlanCenterBasedDBO -> {
                                            if(!Utils.isNullOrEmpty(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO())){
                                                if(!Utils.isNullOrEmpty(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getCenterDetailsDBOs())){
                                                    admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getCenterDetailsDBOs().stream()
                                                            .filter(admSelectionProcessCenterDetailsDBO -> admSelectionProcessCenterDetailsDBO.getRecordStatus()=='A')
                                                            .forEach(admSelectionProcessCenterDetailsDBO -> {
                                                                if(dateTimeVenuePriorityMap.containsKey(admSelectionProcessPlanDetailDBO.getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))){
                                                                    Map<String, Map<Integer, Map<Integer, Tuple4<String,String,String,Integer>>>> timeVenuePriorityMap = dateTimeVenuePriorityMap.get(admSelectionProcessPlanDetailDBO.getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                                                                    if(timeVenuePriorityMap.containsKey(admSelectionProcessPlanDetailDBO.getSelectionProcessTime().toString())){
                                                                        Map<Integer, Map<Integer, Tuple4<String,String,String,Integer>>> venuePriorityMap = timeVenuePriorityMap.get(admSelectionProcessPlanDetailDBO.getSelectionProcessTime().toString());
                                                                        if(venuePriorityMap.containsKey(admSelectionProcessCenterDetailsDBO.getAdmSelectionProcessVenueCityDBO().getId())){
                                                                            Map<Integer, Tuple4<String,String,String,Integer>> priorityMap = venuePriorityMap.get(admSelectionProcessCenterDetailsDBO.getAdmSelectionProcessVenueCityDBO().getId());
                                                                            if(!priorityMap.containsKey(admSelectionProcessCenterDetailsDBO.getCenterPriorityOrder())){
                                                                                Tuple4<String,String,String,Integer> tuple4 = Tuples.of(admSelectionProcessCenterDetailsDBO.getCenterName(),admSelectionProcessCenterDetailsDBO.getCenterCode(),
                                                                                        admSelectionProcessCenterDetailsDBO.getCenterAddress(),admSelectionProcessPlanCenterBasedDBO.getVenueAvailableSeats());
                                                                                priorityMap.put(admSelectionProcessCenterDetailsDBO.getCenterPriorityOrder(),tuple4);
                                                                            }
                                                                        }else{
                                                                            Tuple4<String,String,String,Integer> tuple4 = Tuples.of(admSelectionProcessCenterDetailsDBO.getCenterName(),admSelectionProcessCenterDetailsDBO.getCenterCode(),
                                                                                    admSelectionProcessCenterDetailsDBO.getCenterAddress(),admSelectionProcessPlanCenterBasedDBO.getVenueAvailableSeats());
                                                                            Map<Integer, Tuple4<String,String,String,Integer>> priorityMap = new TreeMap<>();
                                                                            priorityMap.put(admSelectionProcessCenterDetailsDBO.getCenterPriorityOrder(),tuple4);
                                                                            venuePriorityMap.put(admSelectionProcessCenterDetailsDBO.getAdmSelectionProcessVenueCityDBO().getId(),priorityMap);
                                                                            timeVenuePriorityMap.put(admSelectionProcessPlanDetailDBO.getSelectionProcessTime().toString(),venuePriorityMap);
                                                                            dateTimeVenuePriorityMap.put(admSelectionProcessPlanDetailDBO.getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),timeVenuePriorityMap);
                                                                        }
                                                                    }else{
                                                                        Tuple4<String,String,String,Integer> tuple4 = Tuples.of(admSelectionProcessCenterDetailsDBO.getCenterName(),admSelectionProcessCenterDetailsDBO.getCenterCode(),
                                                                                admSelectionProcessCenterDetailsDBO.getCenterAddress(),admSelectionProcessPlanCenterBasedDBO.getVenueAvailableSeats());
                                                                        Map<Integer, Tuple4<String,String,String,Integer>> priorityMap = new TreeMap<>();
                                                                        priorityMap.put(admSelectionProcessCenterDetailsDBO.getCenterPriorityOrder(),tuple4);
                                                                        Map<Integer, Map<Integer, Tuple4<String,String,String,Integer>>> venuePriorityMap = new TreeMap<>();
                                                                        venuePriorityMap.put(admSelectionProcessCenterDetailsDBO.getAdmSelectionProcessVenueCityDBO().getId(),priorityMap);
                                                                        timeVenuePriorityMap.put(admSelectionProcessPlanDetailDBO.getSelectionProcessTime().toString(),venuePriorityMap);
                                                                        dateTimeVenuePriorityMap.put(admSelectionProcessPlanDetailDBO.getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),timeVenuePriorityMap);
                                                                    }
                                                                }else{
                                                                    Tuple4<String,String,String,Integer> tuple4 = Tuples.of(admSelectionProcessCenterDetailsDBO.getCenterName(),admSelectionProcessCenterDetailsDBO.getCenterCode(),
                                                                            admSelectionProcessCenterDetailsDBO.getCenterAddress(),admSelectionProcessPlanCenterBasedDBO.getVenueAvailableSeats());
                                                                    Map<Integer, Tuple4<String,String,String,Integer>> priorityMap = new TreeMap<>();
                                                                    priorityMap.put(admSelectionProcessCenterDetailsDBO.getCenterPriorityOrder(),tuple4);
                                                                    Map<Integer, Map<Integer, Tuple4<String,String,String,Integer>>> venuePriorityMap = new TreeMap<>();
                                                                    venuePriorityMap.put(admSelectionProcessCenterDetailsDBO.getAdmSelectionProcessVenueCityDBO().getId(),priorityMap);
                                                                    Map<String, Map<Integer, Map<Integer, Tuple4<String,String,String,Integer>>>> timeVenuePriorityMap = new TreeMap<>();
                                                                    timeVenuePriorityMap.put(admSelectionProcessPlanDetailDBO.getSelectionProcessTime().toString(),venuePriorityMap);
                                                                    dateTimeVenuePriorityMap.put(admSelectionProcessPlanDetailDBO.getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),timeVenuePriorityMap);
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                            programmeDateTimeVenuePriorityMap.put(admSelectionProcessPlanDetailProgDBO.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(),dateTimeVenuePriorityMap);
                        }
                    //});
            });
        });
        return programmeDateTimeVenuePriorityMap;
    }

    public Map<String, Map<Integer,Map<String,Map<Integer,Map<String,Integer>>>>> setAppliedProcessOrderDateVenueTimeSeatMap(List<AdmSelectionProcessDBO> appliedVenueDetails) {
        //already applied venue seats map
        //process type,process order,date,venue,time,applied seat no
        Map<String, Map<Integer,Map<String,Map<Integer,Map<String,Integer>>>>> appliedProcessOrderDateVenueTimeSeatMap = new HashMap<>();
        if(!Utils.isNullOrEmpty(appliedVenueDetails)){
            appliedVenueDetails.forEach(admSelectionProcessDBO -> {
                if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO())){
                    if(appliedProcessOrderDateVenueTimeSeatMap.containsKey(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())){
                        Map<Integer,Map<String,Map<Integer,Map<String,Integer>>>> processOrderDateVenueTimeSeatMap = appliedProcessOrderDateVenueTimeSeatMap.get(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode());
                        if(processOrderDateVenueTimeSeatMap.containsKey(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder())){
                            Map<String,Map<Integer,Map<String,Integer>>> dateVenueTimeSeatMap = processOrderDateVenueTimeSeatMap.get(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder());
                            if(dateVenueTimeSeatMap.containsKey(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))){
                                Map<Integer,Map<String,Integer>> venueTimeSeatMap = dateVenueTimeSeatMap.get(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                                int venueId = admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getId();
                                if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())) {
                                    for (AdmSelectionProcessPlanCenterBasedDBO admSelectionProcessPlanCenterBasedDBO : admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs()) {
                                        if (admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO().getAdmSelectionProcessVenueCityDBO().getId()
                                            == admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId()) {
                                            venueId = admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId();
                                        }
                                    }
                                }
                                if(venueTimeSeatMap.containsKey(venueId)){
                                    Map<String,Integer> timeSeatMap = venueTimeSeatMap.get(venueId);
                                    if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())){
                                        if(timeSeatMap.containsKey(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime().toString())){
                                            timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime().toString(),
                                                timeSeatMap.get(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime().toString())+1);
                                        }
                                    }else{
                                        if(timeSeatMap.containsKey(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailAllotmentDBO().getSelectionProcessTime().toString())){
                                            timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailAllotmentDBO().getSelectionProcessTime().toString(),
                                                timeSeatMap.get(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailAllotmentDBO().getSelectionProcessTime().toString())+1);
                                        }
                                    }
                                    venueTimeSeatMap.put(venueId,timeSeatMap);
                                    dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                                    processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                                }else{
                                    if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())){
                                        if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs())){
                                            Map<String,Integer> timeSeatMap = new HashMap<>();
                                            admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs().stream()
                                                .filter(admSelectionProcessPlanCenterBasedDBO -> admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO().getAdmSelectionProcessVenueCityDBO().getId()
                                                    == admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId())
                                                .forEach(admSelectionProcessPlanCenterBasedDBO -> {
                                                    timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime().toString(),1);
                                                    venueTimeSeatMap.put(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId(),timeSeatMap);
                                                });
                                            dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                                            processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                                        }
                                    }else {
                                        Map<String,Integer> timeSeatMap = new HashMap<>();
                                        timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailAllotmentDBO().getSelectionProcessTime().toString(),1);
                                        admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDetailAllotmentDBOs()
                                            .stream().filter(admSelectionProcessPlanDetailAllotmentDBO -> admSelectionProcessPlanDetailAllotmentDBO.recordStatus=='A').forEach(admSelectionProcessPlanDetailAllotmentDBO -> {
                                                timeSeatMap.put(admSelectionProcessPlanDetailAllotmentDBO.getSelectionProcessTime().toString(),1);
                                            });
                                        venueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getId(),timeSeatMap);
                                        dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                                        processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                                    }
                                }
                            }else{
                                if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())){
                                    if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs())){
                                        Map<String,Integer> timeSeatMap = new HashMap<>();
                                        Map<Integer,Map<String,Integer>> venueTimeSeatMap = new HashMap<>();
                                        admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs().stream()
                                            .filter(admSelectionProcessPlanCenterBasedDBO -> admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO().getAdmSelectionProcessVenueCityDBO().getId()
                                                == admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId())
                                            .forEach(admSelectionProcessPlanCenterBasedDBO -> {
                                                timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime().toString(),1);
                                                venueTimeSeatMap.put(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId(),timeSeatMap);
                                            });
                                        dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                                        processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                                    }
                                }else {
                                    Map<String,Integer> timeSeatMap = new HashMap<>();
                                    timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailAllotmentDBO().getSelectionProcessTime().toString(),1);
                                    admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDetailAllotmentDBOs()
                                        .stream().filter(admSelectionProcessPlanDetailAllotmentDBO -> admSelectionProcessPlanDetailAllotmentDBO.recordStatus=='A').forEach(admSelectionProcessPlanDetailAllotmentDBO -> {
                                            timeSeatMap.put(admSelectionProcessPlanDetailAllotmentDBO.getSelectionProcessTime().toString(),1);
                                        });
                                    Map<Integer,Map<String,Integer>> venueTimeSeatMap = new HashMap<>();
                                    venueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getId(),timeSeatMap);
                                    dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                                    processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                                }
                            }
                        }else{
                            if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())){
                                if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs())){
                                    Map<String,Integer> timeSeatMap = new HashMap<>();
                                    Map<Integer,Map<String,Integer>> venueTimeSeatMap = new HashMap<>();
                                    admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs().stream()
                                        .filter(admSelectionProcessPlanCenterBasedDBO -> admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO().getAdmSelectionProcessVenueCityDBO().getId()
                                            == admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId())
                                        .forEach(admSelectionProcessPlanCenterBasedDBO -> {
                                            timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime().toString(),1);
                                            venueTimeSeatMap.put(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId(),timeSeatMap);
                                        });
                                    Map<String,Map<Integer,Map<String,Integer>>> dateVenueTimeSeatMap = new HashMap<>();
                                    dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                                    processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                                }
                            }else {
                                Map<String,Integer> timeSeatMap = new HashMap<>();
                                timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailAllotmentDBO().getSelectionProcessTime().toString(),1);
                                admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDetailAllotmentDBOs()
                                    .stream().filter(admSelectionProcessPlanDetailAllotmentDBO -> admSelectionProcessPlanDetailAllotmentDBO.recordStatus=='A').forEach(admSelectionProcessPlanDetailAllotmentDBO -> {
                                        timeSeatMap.put(admSelectionProcessPlanDetailAllotmentDBO.getSelectionProcessTime().toString(),1);
                                    });
                                Map<Integer,Map<String,Integer>> venueTimeSeatMap = new HashMap<>();
                                venueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getId(),timeSeatMap);
                                Map<String,Map<Integer,Map<String,Integer>>> dateVenueTimeSeatMap = new HashMap<>();
                                dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                                processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                            }
                        }
                        appliedProcessOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode(),processOrderDateVenueTimeSeatMap);
                    }else{
                        if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())){
                            if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs())){
                                Map<String,Integer> timeSeatMap = new HashMap<>();
                                Map<Integer,Map<String,Integer>> venueTimeSeatMap = new HashMap<>();
                                admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanCenterBasedDBOs().stream()
                                    .filter(admSelectionProcessPlanCenterBasedDBO -> admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO().getAdmSelectionProcessVenueCityDBO().getId()
                                        == admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId())
                                    .forEach(admSelectionProcessPlanCenterBasedDBO -> {
                                        timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime().toString(),1);
                                        venueTimeSeatMap.put(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId(),timeSeatMap);
                                    });
                                Map<String,Map<Integer,Map<String,Integer>>> dateVenueTimeSeatMap = new HashMap<>();
                                dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                                Map<Integer,Map<String,Map<Integer,Map<String,Integer>>>> processOrderDateVenueTimeSeatMap = new HashMap<>();
                                processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                                appliedProcessOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode(),processOrderDateVenueTimeSeatMap);
                            }
                        }else {
                            Map<String,Integer> timeSeatMap = new HashMap<>();
                            timeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailAllotmentDBO().getSelectionProcessTime().toString(),1);
                            admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDetailAllotmentDBOs()
                                .stream().filter(admSelectionProcessPlanDetailAllotmentDBO -> admSelectionProcessPlanDetailAllotmentDBO.recordStatus=='A').forEach(admSelectionProcessPlanDetailAllotmentDBO -> {
                                    timeSeatMap.put(admSelectionProcessPlanDetailAllotmentDBO.getSelectionProcessTime().toString(),1);
                                });
                            Map<Integer,Map<String,Integer>> venueTimeSeatMap = new HashMap<>();
                            venueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getId(),timeSeatMap);
                            Map<String,Map<Integer,Map<String,Integer>>> dateVenueTimeSeatMap = new HashMap<>();
                            dateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),venueTimeSeatMap);
                            Map<Integer,Map<String,Map<Integer,Map<String,Integer>>>> processOrderDateVenueTimeSeatMap = new HashMap<>();
                            processOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),dateVenueTimeSeatMap);
                            appliedProcessOrderDateVenueTimeSeatMap.put(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode(),processOrderDateVenueTimeSeatMap);
                        }
                    }
                }
            });
        }
        return appliedProcessOrderDateVenueTimeSeatMap;
    }

    /*public void setVenueTimeMap(AdmSelectionProcessPlanDBO admSelectionProcessPlanDBO, Map<Integer, Integer> generatedVenueTimeMap, Map<Integer, Integer> generatedVenueTimeMapForCenterBased,
                                Map<String, Map<Integer,Map<String,Map<Integer,Map<String,Integer>>>>> appliedProcessOrderDateVenueTimeSeatMap) {
        Map<String, Map<Integer,Map<String,Map<Integer,Integer>>>> generatedVenueSeatMap = new HashMap<>();
        if(!Utils.isNullOrEmpty(admSelectionProcessPlanDBO)){
            admSelectionProcessPlanDBO.admSelectionProcessPlanDetailDBO.forEach(admSelectionProcessPlanDetailDBO -> {
                if(appliedVenueSeatsMap.containsKey(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode())){
                    Map<Integer,Map<Integer,Integer>> processOrderVenueSeatMap = appliedVenueSeatsMap.get(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode());
                    if(processOrderVenueSeatMap.containsKey(admSelectionProcessPlanDetailDBO.getProcessOrder())){
                        if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode())){
                            if(!Utils.isNullOrEmpty(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanCenterBasedDBOs())){
                                Map<Integer,Integer> venueSeatMap = new HashMap<>();
                                admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanCenterBasedDBOs().forEach(admSelectionProcessPlanCenterBasedDBO -> {
                                    if(admSelectionProcessPlanCenterBasedDBO.recordStatus == 'A'){
                                        venueSeatMap.put(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId(),admSelectionProcessPlanCenterBasedDBO.getVenueAvailableSeats());
                                    }
                                });
                                processOrderVenueSeatMap.put(admSelectionProcessPlanDetailDBO.getProcessOrder(),venueSeatMap);
                                appliedVenueSeatsMap.put(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode(),processOrderVenueSeatMap);
                            }
                        }else{
                            Map<Integer,Integer> venueSeatMap = processOrderVenueSeatMap.get(admSelectionProcessPlanDetailDBO.getProcessOrder());
                            int appliedSeat = 1;
                            if(venueSeatMap.containsKey(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessVenueCityDBO().getId())){
                                appliedSeat = venueSeatMap.get(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessVenueCityDBO().getId())+1;
                            }
                            venueSeatMap.put(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessVenueCityDBO().getId(), appliedSeat);
                            processOrderVenueSeatMap.put(admSelectionProcessPlanDetailDBO.getProcessOrder(),venueSeatMap);
                        }
                    }else{
                        if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode())){
                            if(!Utils.isNullOrEmpty(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanCenterBasedDBOs())){
                                Map<Integer,Integer> venueSeatMap = new HashMap<>();
                                admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanCenterBasedDBOs().forEach(admSelectionProcessPlanCenterBasedDBO -> {
                                    if(admSelectionProcessPlanCenterBasedDBO.recordStatus == 'A'){
                                        venueSeatMap.put(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId(),admSelectionProcessPlanCenterBasedDBO.getVenueAvailableSeats());
                                    }
                                });
                                processOrderVenueSeatMap.put(admSelectionProcessPlanDetailDBO.getProcessOrder(),venueSeatMap);
                                appliedVenueSeatsMap.put(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode(),processOrderVenueSeatMap);
                            }
                        }else{
                            Map<Integer,Integer> venueSeatMap = new HashMap<>();
                            venueSeatMap.put(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessVenueCityDBO().getId(),1);
                            processOrderVenueSeatMap.put(admSelectionProcessPlanDetailDBO.getProcessOrder(),venueSeatMap);
                        }
                    }
                    appliedVenueSeatsMap.put(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode(),processOrderVenueSeatMap);
                }else{
                    if("Center Based Entrance".equalsIgnoreCase(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode())){
                        if(!Utils.isNullOrEmpty(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanCenterBasedDBOs())){
                            Map<Integer,Integer> venueSeatMap = new HashMap<>();
                            admSelectionProcessPlanDetailDBO.getAdmSelectionProcessPlanCenterBasedDBOs().forEach(admSelectionProcessPlanCenterBasedDBO -> {
                                if(admSelectionProcessPlanCenterBasedDBO.recordStatus == 'A'){
                                    venueSeatMap.put(admSelectionProcessPlanCenterBasedDBO.getAdmSelectionProcessVenueCityDBO().getId(),admSelectionProcessPlanCenterBasedDBO.getVenueAvailableSeats());
                                }
                            });
                            Map<Integer,Map<Integer,Integer>> processOrderVenueSeatMap = new HashMap<>();
                            processOrderVenueSeatMap.put(admSelectionProcessPlanDetailDBO.getProcessOrder(),venueSeatMap);
                            appliedVenueSeatsMap.put(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode(),processOrderVenueSeatMap);
                        }
                    }else{
                        Map<Integer,Integer> venueSeatMap = new HashMap<>();
                        venueSeatMap.put(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessVenueCityDBO().getId(),1);
                        Map<Integer,Map<Integer,Integer>> processOrderVenueSeatMap = new HashMap<>();
                        processOrderVenueSeatMap.put(admSelectionProcessPlanDetailDBO.getProcessOrder(),venueSeatMap);
                        appliedVenueSeatsMap.put(admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getMode(),processOrderVenueSeatMap);
                    }
                }
            });
        }
    }*/
}
