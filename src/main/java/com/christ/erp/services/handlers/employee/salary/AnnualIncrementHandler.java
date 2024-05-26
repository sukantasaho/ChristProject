package com.christ.erp.services.handlers.employee.salary;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.salary.*;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.employee.salary.*;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.salary.AnnualIncrementTransaction;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AnnualIncrementHandler {

    private static volatile AnnualIncrementHandler annualIncrementHandler = null;
    AnnualIncrementTransaction annualIncrementTransaction = new AnnualIncrementTransaction();
    CommonApiTransaction commonApiTransaction = new CommonApiTransaction();

    public static AnnualIncrementHandler getInstance() {
        if (annualIncrementHandler == null) {
            annualIncrementHandler = new AnnualIncrementHandler();
        }
        return annualIncrementHandler;
    }

    public List<PayScaleMappingDTO> getAllGradeLevelCell() throws Exception {
        List<Tuple> dataList = annualIncrementTransaction.getAllGradeLevelCell();
        List<PayScaleMappingDTO> gridList = new ArrayList<>();
        Map<Integer, ArrayList<LookupItemDTO>> gradeMap = new HashMap<>();
        Map<Integer, ArrayList<CellDataObjDTO>> levelMap = new HashMap<>();
        for (Tuple data : dataList){
            ArrayList<LookupItemDTO> gradeList = new ArrayList<>();
            ArrayList<CellDataObjDTO> levelList = new ArrayList<>();
            LookupItemDTO levelObj = new LookupItemDTO();
            CellDataObjDTO cellObj = new CellDataObjDTO();
            levelObj.value = String.valueOf(data.get("levelId"));
            levelObj.label = String.valueOf(data.get("scaleLevel"));
            cellObj.value = String.valueOf(data.get("cellId"));
            cellObj.label = String.valueOf(data.get("cellName"));
            cellObj.amount = String.valueOf(data.get("basicAmount"));
            Integer gradeId = Integer.parseInt(String.valueOf(data.get("gradeId")));
            Integer levelId =  Integer.parseInt(String.valueOf(data.get("levelId")));
            if(gradeMap.containsKey(gradeId)){
                boolean isInclude = false;
                for (LookupItemDTO gradeObj : gradeMap.get(gradeId)){
                    if (gradeObj.value.equals(levelObj.value)) {
                        isInclude = true;
                        break;
                    }
                }
                if(!isInclude){
                    gradeList =gradeMap.get(gradeId);
                    gradeList.add(levelObj);
                }
            }else{
                gradeList.add(levelObj);
                gradeMap.put(gradeId,gradeList);
            }
            if(levelMap.containsKey(levelId)){
                levelList =levelMap.get(levelId);
                levelList.add(cellObj);
            }else{
                levelList.add(cellObj);
                levelMap.put(levelId,levelList);
            }
        }
        for (Map.Entry<Integer, ArrayList<LookupItemDTO>> entry : gradeMap.entrySet()){
            PayScaleMappingDTO dto = new PayScaleMappingDTO();
            ExModelBaseDTO gradeObj = new ExModelBaseDTO();
            gradeObj.id = String.valueOf(entry.getKey());
            dto.grade = gradeObj ;
            List<PayScaleMappingItemDTO> levelList = new ArrayList<>();
            for(LookupItemDTO levelEntry : entry.getValue()){
                PayScaleMappingItemDTO levelDto = new PayScaleMappingItemDTO();
                levelDto.label = String.valueOf(levelEntry.label);
                levelDto.value = String.valueOf(levelEntry.value);
                levelDto.cellData = levelMap.get(Integer.parseInt(levelEntry.value));
                levelList.add(levelDto);
            }
            dto.levels=levelList;
            gridList.add(dto);
        }
        return gridList;
    }

    public List<AnnualIncrementDTO> getAllEmployee(AnnualIncrementSearchDTO data, String isForReview) throws Exception {
        List<AnnualIncrementDTO> dataList = new ArrayList<>();
        boolean isCurrent = false;
        Tuple statusIdObj;
        Integer statusId = null;
        if(!Utils.isNullOrEmpty(data.status) && !Utils.isNullOrEmpty(data.status.value)){
            if(data.status.value.equals("0")){
                isCurrent = true;
            }
            statusIdObj = CommonApiTransaction.getInstance().getErpWorkFlowProcessIdbyProcessCode(data.status.value);
            if(!Utils.isNullOrEmpty(statusIdObj))
                statusId = Integer.parseInt(String.valueOf(statusIdObj.get("erp_work_flow_process_id")));
        }
        List<Integer> yearList = new ArrayList<>();
        if (isForReview.equals("true")){
            List<Tuple> locationDBOS = annualIncrementTransaction.getAllLocation();
            for (Tuple year: locationDBOS) {
                Integer yearId = annualIncrementTransaction.getOpenAcademicYearByLocationId(String.valueOf(year.get("value")));
                if(!Utils.isNullOrEmpty(yearId)) yearList.add(yearId);
            }
        }else{
            Integer year = annualIncrementTransaction.getOpenAcademicYearByLocationId(data.location.value);
            yearList.add(year);
        }
        List<Tuple> ApiDataList = annualIncrementTransaction.getAllEmployee(data,isCurrent,yearList,statusId);
        if(!Utils.isNullOrEmpty(ApiDataList)){
            for(Tuple dbo: ApiDataList) {
                AnnualIncrementDTO dto = new AnnualIncrementDTO();
                if (!Utils.isNullOrEmpty(dbo.get("ID")) && !isCurrent) dto.ID = String.valueOf(dbo.get("ID"));
                else dto.ID = "";
                dto.empId = !Utils.isNullOrEmpty(dbo.get("empID")) ? String.valueOf(dbo.get("empID")) : "";
                dto.empName = (Utils.isNullOrEmpty(dbo.get("name")) || Utils.isNullOrEmpty(dbo.get("empID"))) ? "" :
                        dbo.get("name") + " ("+dbo.get("empID").toString()+")";//concat name and id
                if (isCurrent) {
                    if (!Utils.isNullOrEmpty(dbo.get("currentTotal"))) {
                        dto.revisedTotal = (BigDecimal) dbo.get("currentTotal");
                        dto.currentTotal = (BigDecimal) dbo.get("currentTotal");
                    }
                } else {
                    if (!Utils.isNullOrEmpty(dbo.get("currentTotal")))
                        dto.revisedTotal = (BigDecimal) dbo.get("currentTotal");
                    if (!Utils.isNullOrEmpty(dbo.get("previousTotal")))
                        dto.currentTotal = (BigDecimal) dbo.get("previousTotal");
                }
                dto.applnEntryId = !Utils.isNullOrEmpty(dbo.get("applnId")) ? String.valueOf(dbo.get("applnId")) : "";
                dto.percentage = "0";
                if (!Utils.isNullOrEmpty(dbo.get("statusId"))) {
                    dto.statusId = String.valueOf(dbo.get("statusId"));
                }
                dto.lastModified = !Utils.isNullOrEmpty(dbo.get("modifiedDate")) ? String.valueOf(dbo.get("modifiedDate")) : "";
                if(!Utils.isNullOrEmpty(dbo.get("joiningDate")) ){
                	dto.dateOfjoin = Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(dbo.get("dateOfJoining").toString())); 
                //	dto.dateOfjoin = Utils.convertLocalDateToStringDate(Utils.convertStringDateTimeToLocalDate(dbo.get("joiningDate").toString())); 
                }
//                for location
                LookupItemDTO locationObj = new LookupItemDTO();
                locationObj.value = !Utils.isNullOrEmpty(dbo.get("locationValue")) ? String.valueOf(dbo.get("locationValue")) : "";
                locationObj.label = !Utils.isNullOrEmpty(dbo.get("locationLabel")) ? String.valueOf(dbo.get("locationLabel")) : "";
                dto.location = locationObj;
//                for department
                LookupItemDTO departmentObj = new LookupItemDTO();
                departmentObj.value = !Utils.isNullOrEmpty(dbo.get("departmentValue")) ? String.valueOf(dbo.get("departmentValue")) : "";
                departmentObj.label = !Utils.isNullOrEmpty(dbo.get("departmentLabel")) ? String.valueOf(dbo.get("departmentLabel")) : "";
                dto.department = departmentObj;
                //for designation
                LookupItemDTO designationObj = new LookupItemDTO();
                designationObj.value = !Utils.isNullOrEmpty(dbo.get("designationValue")) ? String.valueOf(dbo.get("designationValue")) : "";
                designationObj.label = !Utils.isNullOrEmpty(dbo.get("designationLabel")) ? String.valueOf(dbo.get("designationLabel")) : "";
                dto. designation = designationObj;

                dto.effectiveDate = !Utils.isNullOrEmpty(dbo.get("effectiveDate")) ? String.valueOf(dbo.get("effectiveDate")) : "";
                dto.payScaleType = !Utils.isNullOrEmpty(dbo.get("payScaleType")) ? String.valueOf(dbo.get("payScaleType")) : "";
                if (!Utils.isNullOrEmpty(dto.payScaleType)) {
                    switch (dto.payScaleType) {
                        case "SCALE PAY":
                            LookupItemDTO currentCellObj = new LookupItemDTO();
                            currentCellObj.value = !Utils.isNullOrEmpty(dbo.get("cellValue")) ? String.valueOf(dbo.get("cellValue")) : "";
                            currentCellObj.label = !Utils.isNullOrEmpty(dbo.get("cellLabel")) ? String.valueOf(dbo.get("cellLabel")) : "";
                            dto.currentCell = currentCellObj;
                            dto.revisedCell = currentCellObj;

                            LookupItemDTO currentLevelObj = new LookupItemDTO();
                            currentLevelObj.value = !Utils.isNullOrEmpty(dbo.get("levelValue")) ? String.valueOf(dbo.get("levelValue")) : "";
                            currentLevelObj.label = !Utils.isNullOrEmpty(dbo.get("scaleLevel")) ? String.valueOf(dbo.get("scaleLevel")) : "";
                            dto.currentLevel = currentLevelObj;
                            dto.revisedLevel = currentLevelObj;

                            dto.currentLevelAndCell = dto.currentLevel.label + "/" + dto.currentCell.label;
                            LookupItemDTO gradeObj = new LookupItemDTO();
                            gradeObj.value = !Utils.isNullOrEmpty(dbo.get("gradeValue")) ? String.valueOf(dbo.get("gradeValue")) : "";
                            gradeObj.label = !Utils.isNullOrEmpty(dbo.get("gradeLabel")) ? String.valueOf(dbo.get("gradeLabel")) : "";
                            dto.grade = gradeObj;
                            break;
                        case "DAILY":
                            if (!Utils.isNullOrEmpty(dbo.get("currentWagePerDay")))
                                dto.currentWagePerDay = (BigDecimal) dbo.get("currentWagePerDay");
                            dto.revisedWagePerDay = dto.currentWagePerDay;
                            break;
                    }
                }
                if (!Utils.isNullOrEmpty(data.status.value)) {
                    switch (data.status.value) {
                        case "INCREMENT_INITIATED":
                            dto.isIncremented = true;
                            break;
                        case "INCREMENT_SEND_FOR_REVIEW":
                        case "INCREMENT_COMMENTED_BY_REVIEWER":
                            dto.sentToReview = true;
                            break;
                        case "INCREMENT_SEND_FOR_APPROVAL":
                            dto.sentToApproval = true;
                            break;
                    }
                }
                List<Tuple> commentsArray = null;
                if (!Utils.isNullOrEmpty(dto.ID)) {
                    commentsArray = annualIncrementTransaction.getUpdatedComments(dto.ID);
                }
                List<EmpPayScaleDetailsCommentsDTO> commentsList = new ArrayList<>();
                if (!Utils.isNullOrEmpty(commentsArray)) {
                    commentsArrayHandler(commentsArray, commentsList);
                }
                dto.comments = commentsList;
                List<SalaryComponentDTO> salaryComponentDTOS = new ArrayList<>();
                if (!Utils.isNullOrEmpty(dto.ID)) {
                    List<Tuple> allowanceArray = annualIncrementTransaction.getPayScaleDetailComponent(dto.ID);
                    for (Tuple allowanceDbo : allowanceArray) {
                        SalaryComponentDTO allowanceDto = new SalaryComponentDTO();
                        allowanceDto.id = !Utils.isNullOrEmpty(allowanceDbo.get("id")) ? String.valueOf(allowanceDbo.get("id")) : "";
                        allowanceDto.amount = !Utils.isNullOrEmpty(allowanceDbo.get("amount")) ? String.valueOf(allowanceDbo.get("amount")) : "";
                        allowanceDto.allowanceType = !Utils.isNullOrEmpty(allowanceDbo.get("salaryName")) ? String.valueOf(allowanceDbo.get("salaryName")) : "";
                        allowanceDto.calculationType = allowanceDbo.get("isPercentage") == "1";
                        allowanceDto.displayOrder = !Utils.isNullOrEmpty(allowanceDbo.get("displayOrder")) ? String.valueOf(allowanceDbo.get("displayOrder")) : "";
                        String isBasic = !Utils.isNullOrEmpty(allowanceDbo.get("isBasic")) ? String.valueOf(allowanceDbo.get("isBasic")) : "";
                        allowanceDto.isBasic = isBasic.equals("1");
                        allowanceDto.mentionPercentage = !Utils.isNullOrEmpty(allowanceDbo.get("percentage")) ? String.valueOf(allowanceDbo.get("percentage")) : "";
                        allowanceDto.payScaleType = !Utils.isNullOrEmpty(allowanceDbo.get("scaleType")) ? String.valueOf(allowanceDbo.get("scaleType")) : "";
                        allowanceDto.shortName = !Utils.isNullOrEmpty(allowanceDbo.get("shortName")) ? String.valueOf(allowanceDbo.get("shortName")) : "";
                        allowanceDto.payScaleDetailsComponentId = !Utils.isNullOrEmpty(allowanceDbo.get("payScaleDetailsComponentId")) ? String.valueOf(allowanceDbo.get("payScaleDetailsComponentId")) : "";
                        salaryComponentDTOS.add(allowanceDto);
                    }
                }
                dto.allowances = salaryComponentDTOS;
                dataList.add(dto);
            }
        }
        return dataList;
    }

    private void commentsArrayHandler(List<Tuple> commentsArray, List<EmpPayScaleDetailsCommentsDTO> commentsList) {
        for (Tuple commentsObj: commentsArray){
            EmpPayScaleDetailsCommentsDTO empPayScaleDetailsCommentsDTO = new EmpPayScaleDetailsCommentsDTO();
            if(!Utils.isNullOrEmpty(commentsObj.get("ID"))){
                if(!Utils.isNullOrEmpty(String.valueOf(commentsObj.get("ID")))){
                    empPayScaleDetailsCommentsDTO.id = String.valueOf(commentsObj.get("ID"));
                }
            }
            empPayScaleDetailsCommentsDTO.payScaleId = !Utils.isNullOrEmpty(commentsObj.get("payScaleId")) ? String.valueOf(commentsObj.get("payScaleId")) : "";
            empPayScaleDetailsCommentsDTO.comments = !Utils.isNullOrEmpty(commentsObj.get("comments")) ? String.valueOf(commentsObj.get("comments")) : "";
            empPayScaleDetailsCommentsDTO.commentedBy = !Utils.isNullOrEmpty(commentsObj.get("userName")) ? String.valueOf(commentsObj.get("userName")) : "";
            if(!Utils.isNullOrEmpty(commentsObj.get("times")) ){
                empPayScaleDetailsCommentsDTO.commentedTime = String.valueOf(commentsObj.get("times"));
            }
            commentsList.add(empPayScaleDetailsCommentsDTO);
        }
    }

    public List<SalaryComponentDTO> getEmpPayScaleComponentsForIncrement(String payScaleType) throws Exception {
        List<SalaryComponentDTO> salaryComponentDTOs = new ArrayList<>();
        List<Tuple> empPayScaleComponentsList = annualIncrementTransaction.getEmpPayScaleComponentsForIncrement(payScaleType);
        if(!Utils.isNullOrEmpty(empPayScaleComponentsList)) {
            if(empPayScaleComponentsList.size() > 0) {
                for(Tuple mapping : empPayScaleComponentsList) {
                    SalaryComponentDTO mappingInfo = new SalaryComponentDTO();
                    mappingInfo.id = mapping.get("ID").toString();
                    mappingInfo.allowanceType = !Utils.isNullOrEmpty(mapping.get("salaryComponentName")) ? mapping.get("salaryComponentName").toString() : "";
                    mappingInfo.shortName = !Utils.isNullOrEmpty(mapping.get("salaryComponentShortName")) ? mapping.get("salaryComponentShortName").toString() : "";
                    mappingInfo.displayOrder = !Utils.isNullOrEmpty(mapping.get("salaryComponentDisplayOrder")) ? mapping.get("salaryComponentDisplayOrder").toString() : "";
                    mappingInfo.isBasic =  !Utils.isNullOrEmpty(mapping.get("isComponentBasic")) ? (Boolean)mapping.get("isComponentBasic") : Boolean.valueOf("");
                    mappingInfo.mentionPercentage = !Utils.isNullOrEmpty(mapping.get("percentage")) ? mapping.get("percentage").toString() : "";
                    mappingInfo.calculationType = !Utils.isNullOrEmpty(mapping.get("isCaculationTypePercentage")) ? (Boolean) mapping.get("isCaculationTypePercentage"): Boolean.valueOf("");
                    mappingInfo.payScaleType = !Utils.isNullOrEmpty(mapping.get("payScaleType")) ? mapping.get("payScaleType").toString() : "";
                    mappingInfo.amount = "";
                    salaryComponentDTOs.add(mappingInfo);
                }
            }
        }
        return salaryComponentDTOs;
    }

    public List<LookupItemDTO> getIncrementStatus() throws Exception {
        List<ErpWorkFlowProcessDBO> apiData = annualIncrementTransaction.getIncrementStatus();
        List<LookupItemDTO> statusList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(apiData)) {
            LookupItemDTO dtoDummy = new LookupItemDTO();
            dtoDummy.value = String.valueOf(0);
            dtoDummy.label = "Not Initiated";
            statusList.add(dtoDummy);
            for (ErpWorkFlowProcessDBO dbo : apiData) {
                LookupItemDTO dto = new LookupItemDTO();
                if(!Utils.isNullOrEmpty(dbo.applicationStatusDisplayText)){
                    dto.value = String.valueOf(dbo.processCode);
                    dto.label = dbo.applicationStatusDisplayText;
                    statusList.add(dto);
                }
            }
        }
        return statusList;
    }

    public List<LookupItemDTO> getReviewersAndApproversList() throws Exception {
        List<Tuple> apiData = annualIncrementTransaction.getReviewersAndApproversList();
        List<LookupItemDTO> statusList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(apiData)) {
            for (Tuple dbo : apiData) {
                LookupItemDTO dto = new LookupItemDTO();
                if(!Utils.isNullOrEmpty(dbo.get("id"))){
                    dto.value = String.valueOf(dbo.get("id"));
                    dto.label = String.valueOf(dbo.get("label"));
                    statusList.add(dto);
                }
            }
        }
        return statusList;
    }

    public List<EmpPayScaleDetailsCommentsDTO> saveOrUpdateComments( String comments, String userId,String payscaleId,boolean isReviewer) throws Exception {
        List<Tuple> updatedComments;
        List<EmpPayScaleDetailsCommentsDTO> commentsList = new ArrayList<>();
        EmpPayScaleDetailsCommentsDBO empPayScaleDetailsCommentsDBOS = new EmpPayScaleDetailsCommentsDBO();
        if (!Utils.isNullOrEmpty(comments)) {
            empPayScaleDetailsCommentsDBOS.payScaleComments = comments;
        }
        boolean isPayScaleDetailsTableUpdated = false;
        if (!Utils.isNullOrEmpty(payscaleId)) {
            EmpPayScaleDetailsDBO empPayScaleDetailsDBO = commonApiTransaction.find(EmpPayScaleDetailsDBO.class, Integer.parseInt(payscaleId));
            if(!Utils.isNullOrEmpty(empPayScaleDetailsDBO)){
                Tuple statusIdObj = CommonApiTransaction.getInstance().getErpWorkFlowProcessIdbyProcessCode(isReviewer ? "INCREMENT_COMMENTED_BY_REVIEWER" : "INCREMENT_COMMENTED_BY_APPROVER");
                Integer statusId = Integer.parseInt(String.valueOf(statusIdObj.get("erp_work_flow_process_id")));
                if (!Utils.isNullOrEmpty(statusId)){
                    empPayScaleDetailsDBO.status_id = statusId;
                    isPayScaleDetailsTableUpdated = annualIncrementTransaction.updatePayscaleDetailsTableWhenCommetnsAreUpdated(empPayScaleDetailsDBO);
                }
                empPayScaleDetailsCommentsDBOS.empPayScaleDetailsDBO = empPayScaleDetailsDBO;
                Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
                if (!Utils.isNullOrEmpty(empId)) {
                    EmpDBO empDBO = new EmpDBO();
                    empDBO.id = empId;
                    empPayScaleDetailsCommentsDBOS.empDBO = empDBO;
                }
                empPayScaleDetailsCommentsDBOS.recordStatus = 'A';
                if(isPayScaleDetailsTableUpdated){
                    boolean isSavedComments = annualIncrementTransaction.saveOrUpdateComments(empPayScaleDetailsCommentsDBOS);
                    if(isSavedComments){
                        updatedComments = annualIncrementTransaction.getUpdatedComments(payscaleId);
                        commentsArrayHandler(updatedComments, commentsList);
                    }
                    return commentsList;
                }
            }
        }
        return null;
    }

    public boolean saveOrUpdate(List<AnnualIncrementDTO> data, String userId, List<LookupItemDTO> reviewersOrApproverList) throws Exception {
        boolean isSaved = false;
        if(!data.isEmpty()) {
            List<EmpPayScaleDetailsDBO> dboList = new ArrayList<>();
            for (AnnualIncrementDTO dto : data) {
                EmpPayScaleDetailsDBO dbo;
                if (!Utils.isNullOrEmpty(dto.ID)) {
//                    dbo = commonApiTransaction.find(EmpPayScaleDetailsDBO.class, Integer.parseInt(dto.ID));
//                    EmpPayScaleDetailsDBO dboList = ;
                    dbo = annualIncrementTransaction.getPayScaleDetailsDbo(Integer.parseInt(dto.ID));
                } else {
                    dbo = new EmpPayScaleDetailsDBO();
                }
                if (!Utils.isNullOrEmpty(dto.empId)) {
                    EmpDBO empDBO = new EmpDBO();
                    empDBO.id = Integer.valueOf(dto.empId);
                    dbo.empDBO = empDBO;
                }
                if (!Utils.isNullOrEmpty(dto.effectiveDate)) {
                    dbo.payScaleEffectiveDate = Utils.convertStringDateToLocalDate(dto.effectiveDate);
                }
                if (!Utils.isNullOrEmpty(dto.payScaleType)) {
                    dbo.payScaleType = dto.payScaleType;
                }
                if (!Utils.isNullOrEmpty(dto.revisedCell)) {
                    EmpPayScaleMatrixDetailDBO empPayScaleMatrixDetailDBO = new EmpPayScaleMatrixDetailDBO();
                    empPayScaleMatrixDetailDBO.id = Integer.parseInt(dto.revisedCell.value);
                    dbo.empPayScaleMatrixDetailDBO = empPayScaleMatrixDetailDBO;
                }
                if (dto.payScaleType.equals("DAILY") && !Utils.isNullOrEmpty(dto.dailyWageSlabId)) {
                    EmpDailyWageSlabDBO empDailyWageSlabDBO = new EmpDailyWageSlabDBO();
                    empDailyWageSlabDBO.id = Integer.valueOf(dto.dailyWageSlabId);
                    dbo.empDailyWageSlabDBO = empDailyWageSlabDBO;
                }
                if (dto.payScaleType.equals("DAILY") && !Utils.isNullOrEmpty(dto.revisedWagePerDay)) {
                    dbo.wageRatePerType = dto.revisedWagePerDay;
                }
                Tuple statusIdObj = CommonApiTransaction.getInstance().getErpWorkFlowProcessIdbyProcessCode(dto.statusId);
                Integer statusId = Integer.parseInt(String.valueOf(statusIdObj.get("erp_work_flow_process_id")));
                if (!Utils.isNullOrEmpty(statusId)) dbo.status_id = statusId;
                dbo.recordStatus = 'A';
                if (!Utils.isNullOrEmpty(dto.location.value)){
                    dbo.getErpAcademicYearDBO().setId(annualIncrementTransaction.getOpenAcademicYearByLocationId(dto.location.value));
                }
                if (dto.statusId.equals("INCREMENT_INITIATED") || Utils.isNullOrEmpty(dto.ID)) {
                    dbo.id = null;
                    dbo.createdUsersId = Integer.valueOf(userId);
                } else if (dto.statusId.equals("INCREMENT_SEND_FOR_REVIEW") || dto.statusId.equals("INCREMENT_SEND_FOR_APPROVAL")) {
                    dbo.id = Integer.valueOf(dto.ID);
                    //reviewers or approvers settings
                    if (!Utils.isNullOrEmpty(reviewersOrApproverList)) {
                        Set<EmpPayScaleDetailsReviewerAndApproverDBO> empPayScaleDetailsReviewerAndApproverDBOS = new HashSet<>();
                        for (LookupItemDTO reviewerObj : reviewersOrApproverList) {
                            EmpPayScaleDetailsReviewerAndApproverDBO empPayScaleDetailsReviewerAndApproverDBO;
                            if (!Utils.isNullOrEmpty(dbo.id)) {
                                empPayScaleDetailsReviewerAndApproverDBO = annualIncrementTransaction.getApproverOrReviewerIdByEmpIdAndPayScaleId(reviewerObj.value, dbo.id);
                            } else {
                                empPayScaleDetailsReviewerAndApproverDBO = new EmpPayScaleDetailsReviewerAndApproverDBO();
                            }
                            if (Utils.isNullOrEmpty(empPayScaleDetailsReviewerAndApproverDBO)) {
                                empPayScaleDetailsReviewerAndApproverDBO = new EmpPayScaleDetailsReviewerAndApproverDBO();
                            }
                            if (!Utils.isNullOrEmpty(reviewerObj.value)) {
                                EmpDBO empDBO = new EmpDBO();
                                empDBO.id = Integer.valueOf(reviewerObj.value);
                                empPayScaleDetailsReviewerAndApproverDBO.empDBO = empDBO;
                            }
                            EmpPayScaleDetailsDBO empPayScaleDetailsDBO = new EmpPayScaleDetailsDBO();
                            empPayScaleDetailsDBO.id = dbo.id;
                            empPayScaleDetailsReviewerAndApproverDBO.empPayScaleDetailsDBO = empPayScaleDetailsDBO;
                            if (dto.statusId.equals("INCREMENT_SEND_FOR_REVIEW")) {
                                empPayScaleDetailsReviewerAndApproverDBO.reviewerOrApproveType = "reviewer";
                            } else if (dto.statusId.equals("INCREMENT_SEND_FOR_APPROVAL")) {
                                empPayScaleDetailsReviewerAndApproverDBO.reviewerOrApproveType = "approver";
                            }
                            empPayScaleDetailsReviewerAndApproverDBO.createdUsersId = Integer.valueOf(userId);
                            empPayScaleDetailsReviewerAndApproverDBO.recordStatus = 'A';
                            empPayScaleDetailsReviewerAndApproverDBOS.add(empPayScaleDetailsReviewerAndApproverDBO);
                        }
                        dbo.empPayScaleDetailsReviewerAndApproverDBOS = empPayScaleDetailsReviewerAndApproverDBOS;
                    } else {
                        dbo.id = Integer.valueOf(dto.ID);
                    }
                } else if (dto.statusId.equals("INCREMENT_APPROVED")){
                    dbo.id = Integer.valueOf(dto.ID);
                } else{
                    dbo.id = Integer.valueOf(dto.ID);
                }
                if(!Utils.isNullOrEmpty(dto.applnEntryId)) {
                    EmpApplnEntriesDBO empApplnEntriesDBO = new EmpApplnEntriesDBO();
                    empApplnEntriesDBO.id = Integer.valueOf(dto.applnEntryId);
                    dbo.empApplnEntriesDBO = empApplnEntriesDBO;
                }

                //comments settings
                Set<EmpPayScaleDetailsCommentsDBO> empPayScaleDetailsCommentsDBOS = new HashSet<>();
                for (EmpPayScaleDetailsCommentsDTO empPayScaleDetailsCommentsDTO : dto.comments) {
                    EmpPayScaleDetailsCommentsDBO empPayScaleDetailsCommentsDBO;
                    if (!Utils.isNullOrEmpty(empPayScaleDetailsCommentsDTO.id)) {
                        empPayScaleDetailsCommentsDBO = commonApiTransaction.find(EmpPayScaleDetailsCommentsDBO.class, Integer.parseInt(empPayScaleDetailsCommentsDTO.id));
                        empPayScaleDetailsCommentsDBO.id = Integer.valueOf(empPayScaleDetailsCommentsDTO.id);
                        empPayScaleDetailsCommentsDBO.modifiedUsersId = Integer.valueOf(userId);
                    } else {
                        empPayScaleDetailsCommentsDBO = new EmpPayScaleDetailsCommentsDBO();
                        empPayScaleDetailsCommentsDBO.createdUsersId = Integer.valueOf(userId);
                        empPayScaleDetailsCommentsDBO.recordStatus = 'A';
                    }
                    if (!Utils.isNullOrEmpty(empPayScaleDetailsCommentsDTO.comments)) {
                        empPayScaleDetailsCommentsDBO.payScaleComments = empPayScaleDetailsCommentsDTO.comments;
                    }
                    EmpPayScaleDetailsDBO empPayScaleDetailsDBO = new EmpPayScaleDetailsDBO();
                    if(!Utils.isNullOrEmpty(dbo.id)){
                        empPayScaleDetailsDBO.id = dbo.id;
                        empPayScaleDetailsCommentsDBO.empPayScaleDetailsDBO = empPayScaleDetailsDBO;
                    }else empPayScaleDetailsCommentsDBO.empPayScaleDetailsDBO = dbo;

                    Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
                    if (!Utils.isNullOrEmpty(empId)) {
                        EmpDBO empDBO = new EmpDBO();
                        empDBO.id = empId;
                        empPayScaleDetailsCommentsDBO.empDBO = empDBO;
                    }
                    empPayScaleDetailsCommentsDBOS.add(empPayScaleDetailsCommentsDBO);
                }
                dbo.empPayScaleDetailsCommentsDBOS = empPayScaleDetailsCommentsDBOS;
                //pay scale components settings
                if(!Utils.isNullOrEmpty(dto.allowances)) {
                    Set<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOs = new HashSet<>();
                    for (SalaryComponentDTO salaryComponentDTO : dto.allowances) {
                        EmpPayScaleDetailsComponentsDBO empPayScaleDetailsComponentsDBO;
                        if (Utils.isNullOrEmpty(salaryComponentDTO.payScaleDetailsComponentId)) {
                            empPayScaleDetailsComponentsDBO = new EmpPayScaleDetailsComponentsDBO();
                            empPayScaleDetailsComponentsDBO.createdUsersId = Integer.valueOf(userId);
                        } else {
                            empPayScaleDetailsComponentsDBO = commonApiTransaction.find(EmpPayScaleDetailsComponentsDBO.class, Integer.parseInt(salaryComponentDTO.payScaleDetailsComponentId));
                            empPayScaleDetailsComponentsDBO.modifiedUsersId = Integer.valueOf(userId);
                        }
                        EmpPayScaleDetailsDBO empPayScaleDetailsDBO = new EmpPayScaleDetailsDBO();
                        if(!Utils.isNullOrEmpty(dbo.id)){
                            empPayScaleDetailsDBO.id = dbo.id;
                            empPayScaleDetailsComponentsDBO.empPayScaleDetailsDBO = empPayScaleDetailsDBO;
                        }
                        else  empPayScaleDetailsComponentsDBO.empPayScaleDetailsDBO = dbo;

                        EmpPayScaleComponentsDBO empPayScaleComponentsDBO = new EmpPayScaleComponentsDBO();
                        if (!Utils.isNullOrEmpty(salaryComponentDTO.id)) {
                            empPayScaleComponentsDBO.id = Integer.valueOf(salaryComponentDTO.id);
                        }
                        empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO = empPayScaleComponentsDBO;
                        if (!Utils.isNullOrEmpty(salaryComponentDTO.amount))
                            empPayScaleDetailsComponentsDBO.empSalaryComponentValue = new BigDecimal(salaryComponentDTO.amount);
                        empPayScaleDetailsComponentsDBO.recordStatus = 'A';

                        empPayScaleDetailsComponentsDBOs.add(empPayScaleDetailsComponentsDBO);
                    }
                    dbo.empPayScaleDetailsComponentsDBOs = empPayScaleDetailsComponentsDBOs;
                }
                dbo.grossPay = dto.revisedTotal;
                dbo.previousGrossPay = dto.currentTotal;
                dboList.add(dbo);
            }
            isSaved = annualIncrementTransaction.saveOrUpdateEmpData(dboList);
        }
        return isSaved;
    }

    public AnnualIncrementReviewOrAppraisalListDTO getReviewOrApprovalRequests(String userId) throws Exception {
        Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
        List<Tuple> reviewerOrApproverType = annualIncrementTransaction.getEmployeeIsReviewerOrApprover(String.valueOf(empId));
        if(!Utils.isNullOrEmpty(reviewerOrApproverType)){
            boolean isReviewer = false;
            for (Tuple reviewOrAprrove: reviewerOrApproverType){
                isReviewer = reviewOrAprrove.get("reviewer_approver_type").equals("reviewer");
            }
            String statusCode = isReviewer ? "INCREMENT_SEND_FOR_REVIEW" : "INCREMENT_SEND_FOR_APPROVAL";
            List<Tuple> searchDTOS = annualIncrementTransaction.getReviewOrApprovalRequests(String.valueOf(empId),statusCode);
            List<AnnualIncrementSearchDTO> dtoList = new ArrayList<>();
            for (Tuple dto : searchDTOS){
                AnnualIncrementSearchDTO annualIncrementSearchDTO = new AnnualIncrementSearchDTO();
                annualIncrementSearchDTO.notificationsLength = !Utils.isNullOrEmpty(dto.get("notificationCount")) ? (BigInteger) dto.get("notificationCount") : BigInteger.valueOf(0);
                //for pay scale type
                LookupItemDTO payScaleTypeObj = new LookupItemDTO();
                payScaleTypeObj.value = !Utils.isNullOrEmpty(dto.get("payScale")) ? String.valueOf(dto.get("payScale")) : "";
                payScaleTypeObj.label = !Utils.isNullOrEmpty(dto.get("payScale")) ? String.valueOf(dto.get("payScale")) : "";
                annualIncrementSearchDTO.payScaleType = payScaleTypeObj;
                //status settings
                LookupItemDTO status = new LookupItemDTO();
                status.value = isReviewer ? "INCREMENT_SEND_FOR_REVIEW" : "INCREMENT_SEND_FOR_APPROVAL";
                annualIncrementSearchDTO.status = status;
                dtoList.add(annualIncrementSearchDTO);
            }
            AnnualIncrementReviewOrAppraisalListDTO annualIncrementReviewOrAppraisalListDTO = new AnnualIncrementReviewOrAppraisalListDTO();
            annualIncrementReviewOrAppraisalListDTO.isReviewer = isReviewer;
            annualIncrementReviewOrAppraisalListDTO.notificationList = dtoList;
            return annualIncrementReviewOrAppraisalListDTO;
        }
        return null;
    }
}