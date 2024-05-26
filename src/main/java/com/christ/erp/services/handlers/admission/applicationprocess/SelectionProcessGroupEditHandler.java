package com.christ.erp.services.handlers.admission.applicationprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailAllotmentDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessGroupEditDTO;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessGroupEditDetailsDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.helpers.admission.applicationprocess.SelectionProcessGroupEditHelper;
import com.christ.erp.services.transactions.admission.applicationprocess.SelectionProcessGroupEditTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.persistence.Tuple;

@Service
public class SelectionProcessGroupEditHandler {

    @Autowired
    private SelectionProcessGroupEditHelper selectionProcessGroupEditHelper;

    @Autowired
    private SelectionProcessGroupEditTransaction selectionProcessGroupEditTransaction;

    public Flux<LookupItemDTO> getSelectionDate(int year) {
        return selectionProcessGroupEditTransaction.getSelectionDate(year)
                .flatMapMany(Flux::fromIterable)
                .map(selectionProcessGroupEditHelper::convertSelectionDateDBOtoDTO);
    }

    public Flux<LookupItemDTO> getSelectionGroup(String date, String timeId) {
        return selectionProcessGroupEditTransaction.getSelectionGroup(date, timeId)
                .flatMapMany(Flux::fromIterable)
                .map(selectionProcessGroupEditHelper::convertSelectionGroupDBOtoDTO);
    }

    public Flux<SelectionProcessGroupEditDetailsDTO> getGroupApplicantsData(String groupId) {
        return selectionProcessGroupEditTransaction.getGroupApplicantsData(groupId)
                .flatMapMany(Flux::fromIterable)
                .map(selectionProcessGroupEditHelper::convertgetGroupApplicantsDataDBOtoDTO);
    }

    public Flux<SelectionProcessGroupEditDetailsDTO> getApplicantData(String applicationNo, String id, String venueCityId) {
        return selectionProcessGroupEditTransaction.getApplicantData(applicationNo, id, venueCityId)
                .flatMapMany(Flux::fromIterable)
                .map(selectionProcessGroupEditHelper::convertgetApplicantDataDBOtoDTO);
    }

    public Mono<ApiResult> saveOrUpdate(Mono<SelectionProcessGroupEditDTO> dto, String userId) {
        List<Integer> studentEntrieIds = new ArrayList<>();
        List<Integer> groupIds = new ArrayList<>();
        HashMap<Integer, Integer> map = new HashMap<>();
        return dto.handle((generateSelectionProcessGroupEditDTO, synchronousSink) -> {
                    List<Object> list1 = null;
                    List<String> values = new ArrayList<String>();
                    generateSelectionProcessGroupEditDTO.getLevels().forEach(sub1 -> {
                        List<Tuple> list2 = selectionProcessGroupEditTransaction.duplicateCheck(generateSelectionProcessGroupEditDTO.getId(), sub1.getStudentEntrieId());
                        list2.forEach(obj -> {
                            values.add(obj.get("applicationNo") + " - " + obj.get("groupName") + " " + obj.get("processTime"));
                            map.put((Integer) obj.get("entriesId"), (Integer) obj.get("groupId"));
                        });
                    });
                    if (Utils.isNullOrEmpty(generateSelectionProcessGroupEditDTO.getId())) {
                        list1 = selectionProcessGroupEditTransaction.duplicateCheckGroup(generateSelectionProcessGroupEditDTO);
                    }
                    if (!Utils.isNullOrEmpty(list1)) {
                        synchronousSink.error(new DuplicateException("Duplicate Group name"));
                    } else if (generateSelectionProcessGroupEditDTO.isFlag() == false && !Utils.isNullOrEmpty(values)) {
                        synchronousSink.error(new GeneralException(String.valueOf(values)));
                    } else {
                        synchronousSink.next(generateSelectionProcessGroupEditDTO);
                    }
                }).cast(SelectionProcessGroupEditDTO.class)
                .map(data -> convertDtoToDbo(data, userId))
                .flatMap(s -> {
                    if (!Utils.isNullOrEmpty(s.getId())) {
                        groupIds.add(s.getId());
                        selectionProcessGroupEditTransaction.update(s);
                        if (!Utils.isNullOrEmpty(map)) {
                            delete(map, studentEntrieIds, groupIds, userId);
                        }
                        if (!Utils.isNullOrEmpty(groupIds)) {
                            selectionProcessGroupEditTransaction.deleteGroup1(groupIds, userId);
                        }
                    } else {
                        selectionProcessGroupEditTransaction.save(s);
                        if (!Utils.isNullOrEmpty(map)) {
                            delete(map, studentEntrieIds, groupIds, userId);
                        }
                        if (!Utils.isNullOrEmpty(groupIds)) {
                            selectionProcessGroupEditTransaction.deleteGroup1(groupIds, userId);
                        }

                    }
                    return Mono.just(Boolean.TRUE);
                }).map(Utils::responseResult);
    }


    public AdmSelectionProcessGroupDBO convertDtoToDbo(SelectionProcessGroupEditDTO dto, String userId) {
        AdmSelectionProcessGroupDBO header = null;
        if (!Utils.isNullOrWhitespace(dto.getId())) {
            header = selectionProcessGroupEditTransaction.edit(dto.getId());
        }
        if (header == null) {
            header = new AdmSelectionProcessGroupDBO();
            header.setCreatedUsersId(Integer.parseInt(userId));
            header.setSelectionProcessGroupName(dto.getGroupName());
            header.setAdmSelectionProcessPlanDetailAllotmentDBO(new AdmSelectionProcessPlanDetailAllotmentDBO());
            header.getAdmSelectionProcessPlanDetailAllotmentDBO().setId(Integer.parseInt(dto.getTimeAllotmentId()));
            List<Object> obj = selectionProcessGroupEditTransaction.groupNo(dto);
            header.setSelectionProcessGroupNo(!Utils.isNullOrEmpty(obj) ? (Integer.valueOf(obj.get(0).toString()) + 1) : 1);
            header.setRecordStatus('A');
        } else {
            header.setModifiedUsersId(Integer.parseInt(userId));
        }
        Set<AdmSelectionProcessGroupDetailDBO> AdmSelectionProcessGroupDetailDBOUpdate = new HashSet<AdmSelectionProcessGroupDetailDBO>();
        Set<AdmSelectionProcessGroupDetailDBO> existDBOSet = header.getAdmSelectionProcessGroupDetailDBOsSet();
        Map<Integer, AdmSelectionProcessGroupDetailDBO> existDBOMap = new HashMap<Integer, AdmSelectionProcessGroupDetailDBO>();
        if (!Utils.isNullOrEmpty(existDBOSet)) {
            existDBOSet.forEach(dbo -> {
                if (dbo.getRecordStatus() == 'A') {
                    existDBOMap.put(dbo.getId(), dbo);
                }
            });
        }
        for (SelectionProcessGroupEditDetailsDTO sub : dto.getLevels()) {
            AdmSelectionProcessGroupDetailDBO dbo = null;
            if (!Utils.isNullOrWhitespace(sub.getId()) && existDBOMap.containsKey(Integer.parseInt(sub.getId()))) {
                dbo = existDBOMap.get((Integer.parseInt(sub.getId())));
                dbo.setModifiedUsersId(Integer.parseInt(userId));
                existDBOMap.remove(Integer.parseInt(sub.getId()));
            } else {
                dbo = new AdmSelectionProcessGroupDetailDBO();
                dbo.setCreatedUsersId(Integer.parseInt(userId));
                StudentApplnEntriesDBO student = new StudentApplnEntriesDBO();
                student.setId(Integer.parseInt(sub.getStudentEntrieId()));
                dbo.setAdmSelectionProcessGroupDBO(header);
                dbo.setStudentApplnEntriesDBO(student);
                dbo.setRecordStatus('A');
            }
            AdmSelectionProcessGroupDetailDBOUpdate.add(dbo);
        }
        if (!Utils.isNullOrEmpty(existDBOMap)) {
            existDBOMap.forEach((entry, value) -> {
                value.setModifiedUsersId(Integer.parseInt(userId));
                value.setRecordStatus('D');
                AdmSelectionProcessGroupDetailDBOUpdate.add(value);
            });
        }
        header.setTotalParticipantsInGroup(dto.getLevels().size());
        header.setAdmSelectionProcessGroupDetailDBOsSet(AdmSelectionProcessGroupDetailDBOUpdate);
        return header;
    }

    @SuppressWarnings("rawtypes")
    public Mono<ApiResult> move(Mono<SelectionProcessGroupEditDTO> dto, String userId) {
        List<Integer> studentEntrieIds = new ArrayList<>();
        List<Integer> groupIds = new ArrayList<>();
        HashMap<Integer, Integer> map = new HashMap<>();
        return dto.handle((generateSelectionProcessGroupEditDTO, synchronousSink) -> {
                    List<String> values = new ArrayList<String>();
                    generateSelectionProcessGroupEditDTO.getLevels().forEach(sub1 -> {
                        List<Tuple> list2 = selectionProcessGroupEditTransaction.duplicateCheck(generateSelectionProcessGroupEditDTO.getId(), sub1.getStudentEntrieId());
                        list2.forEach(obj -> {
                            values.add(obj.get("applicationNo").toString() + " - " + obj.get("groupName").toString() + " " + obj.get("processTime").toString());
                            map.put((Integer) obj.get("entriesId"), (Integer) obj.get("groupId"));

                        });
                    });
                    if (generateSelectionProcessGroupEditDTO.isFlag() == false && !Utils.isNullOrEmpty(values)) {
                        synchronousSink.error(new GeneralException(String.valueOf(values)));
                    } else {
                        synchronousSink.next(generateSelectionProcessGroupEditDTO);
                    }
                }).cast(SelectionProcessGroupEditDTO.class)
                .map(data -> convertDtoToDbo1(data, userId))
                .flatMap(s -> {
                    selectionProcessGroupEditTransaction.moveUpdate(s);
                    for (AdmSelectionProcessGroupDBO admSelectionProcessGroupDBO : s) {
                        groupIds.add(admSelectionProcessGroupDBO.getId());
                    }
                    if (!Utils.isNullOrEmpty(groupIds)) {
                        selectionProcessGroupEditTransaction.deleteGroup1(groupIds, userId);
                    }
                    return Mono.just(Boolean.TRUE);
                }).map(Utils::responseResult);
    }

    public List<AdmSelectionProcessGroupDBO> convertDtoToDbo1(SelectionProcessGroupEditDTO dto, String userId) {
        List<AdmSelectionProcessGroupDBO> db = new ArrayList<AdmSelectionProcessGroupDBO>();
        AdmSelectionProcessGroupDBO header = null;
        header = selectionProcessGroupEditTransaction.move(dto);
        header.setModifiedUsersId(Integer.parseInt(userId));
        header.setTotalParticipantsInGroup(header.getTotalParticipantsInGroup() - 1);
        header.getAdmSelectionProcessGroupDetailDBOsSet().forEach(sub -> {
            AdmSelectionProcessGroupDBO header1 = new AdmSelectionProcessGroupDBO();
            header1.setId(Integer.parseInt(dto.getNewGroupId()));
            sub.setAdmSelectionProcessGroupDBO(header1);
            sub.setModifiedUsersId(Integer.parseInt(userId));
        });
        db.add(header);
        if (Utils.isNullOrEmpty(header))
            header = selectionProcessGroupEditTransaction.edit(dto.getNewGroupId());
        header.setTotalParticipantsInGroup(header.getTotalParticipantsInGroup() + 1);
        header.setModifiedUsersId(Integer.parseInt(userId));
        db.add(header);
        return db;
    }

    public void delete(Map<Integer, Integer> map, List<Integer> studentEntrieId, List<Integer> groupId, String userId) {
        map.forEach((k, v) -> {
            studentEntrieId.add(k);
            groupId.add(v);
        });
        selectionProcessGroupEditTransaction.delete(studentEntrieId, groupId, userId);
    }

    public Mono<ApiResult> deleteGroup(Integer id, String userId) {
        return selectionProcessGroupEditTransaction.deleteGroup(id, userId).map(Utils::responseResult);
    }


}
