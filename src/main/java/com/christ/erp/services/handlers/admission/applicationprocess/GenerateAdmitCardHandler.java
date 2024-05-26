package com.christ.erp.services.handlers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailAllotmentDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessCenterDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnSelectionProcessDatesDBO;
import com.christ.erp.services.dto.admission.applicationprocess.GenerateAdmitCardDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.helpers.admission.admissionprocess.GenerateAdmitCardHelper;
import com.christ.erp.services.transactions.admission.applicationprocess.GenerateAdmitCardTransaction;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import javax.persistence.Tuple;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
public class GenerateAdmitCardHandler {

    @Autowired
    private GenerateAdmitCardHelper generateAdmitCardHelper;

    @Autowired
    private GenerateAdmitCardTransaction generateAdmitCardTransaction;

    public Mono<ApiResult> saveOrUpdate(Mono<GenerateAdmitCardDTO> dto, String userId) {
        ApiResult result = new ApiResult();
        // List<StudentApplnEntriesDBO> studentsList = new ArrayList<>();
        AtomicReference<AdmSelectionProcessPlanDBO> admSelectionProcessPlanDBO = new AtomicReference<>();
        AtomicInteger sessionId = new AtomicInteger(0);
        AtomicInteger processTypeId = new AtomicInteger(0);
        return dto
        .handle((generateAdmitCardDTO, synchronousSink) -> {
            try {
                StringBuffer errorBuffer = new StringBuffer();
                List<Integer> applnNumberList = new ArrayList<>();
                List<Integer> duplicateApplicationNo = new ArrayList<>();
                //54321,181,187,169
                // AtomicBoolean isEmpty = new AtomicBoolean(true);
                boolean isEmpty = true;
                XSSFWorkbook workbook = null;
                try {
                    workbook = new XSSFWorkbook("SelectioProcessExcelUpload//" + generateAdmitCardDTO.getExcelFileName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                XSSFSheet sheet = workbook.getSheetAt(0);
                if (!Utils.isNullOrEmpty(sheet.getRow(0))) {
                    int rowLength = sheet.getRow(0).getLastCellNum();
                    for (Row row : sheet) {
                        if (row.getRowNum() != 0) {
                            for (int i = 0; i < rowLength; i++) {
                                if (i == 0) {
                                    Cell cell = row.getCell(i);
                                    if (cell != null) {
                                        isEmpty = false;
                                        if (cell.getCellType() == CellType.NUMERIC) {
                                            if (!applnNumberList.contains((int) cell.getNumericCellValue())) {
                                                applnNumberList.add((int) cell.getNumericCellValue());
                                            } else {
                                                duplicateApplicationNo.add((int) cell.getNumericCellValue());
                                            }
                                        } else {
                                            String cellValue = cell.toString();
                                            if (!applnNumberList.contains(Integer.parseInt(cellValue))) {
                                                applnNumberList.add(Integer.parseInt(cellValue));
                                            } else {
                                                duplicateApplicationNo.add(Integer.parseInt(cellValue));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                workbook.close();
                if (!Utils.isNullOrEmpty(duplicateApplicationNo)) {
                    errorBuffer.append("Duplicate Application numbers found : ").append(duplicateApplicationNo.toString().replace("[", "").replace("]", ""));
                }
                sessionId.set(Integer.parseInt(generateAdmitCardDTO.getSession().getValue()));
                processTypeId.set(Integer.parseInt(generateAdmitCardDTO.getSelectionProcessType().getValue()));
                admSelectionProcessPlanDBO.set(generateAdmitCardTransaction.getSelectionProcessPlanByProcessType(sessionId.get(), processTypeId.get()));
                List<StudentApplnEntriesDBO> studentsList = new ArrayList<>(generateAdmitCardTransaction.getApplicantsDetails(applnNumberList));
                List<Integer> invalidApplicationNumbers = studentsList.stream().map(StudentApplnEntriesDBO::getApplicationNo).filter(applnNo -> !applnNumberList.contains(applnNo)).toList();
                if (!Utils.isNullOrEmpty(invalidApplicationNumbers)) {
                    errorBuffer.append("Invalid Application numbers found : ").append(invalidApplicationNumbers.toString().replace("[", "").replace("]", ""));
                }
                //validate application numbers
                generateAdmitCardHelper.validateApplicationNumbers(applnNumberList, errorBuffer, studentsList, sessionId.get(), processTypeId.get(), admSelectionProcessPlanDBO.get());
                if (!Utils.isNullOrEmpty(errorBuffer.toString())) {
                    synchronousSink.error(new GeneralException(errorBuffer.toString()));
                } else {
                    synchronousSink.next(studentsList);
                }
            } catch (IOException e) {
                e.printStackTrace();
                synchronousSink.error(new GeneralException("Exception occured"));
            }
            }).map(data -> convertDtoToDbo((List<StudentApplnEntriesDBO>) data, sessionId.get(), processTypeId.get(), admSelectionProcessPlanDBO.get(), userId))
            .flatMap(s ->{
                // generateAdmitCardTransaction.update(s);
                return Mono.just(Boolean.TRUE);
            }).map(Utils::responseResult);
    }

    private List<AdmSelectionProcessDBO> convertDtoToDbo(List<StudentApplnEntriesDBO> studentsList, int sessionId, int processTypeId, AdmSelectionProcessPlanDBO admSelectionProcessPlanDBO, String userId) {
        List<AdmSelectionProcessDBO> selectionProcessDBOList = new ArrayList<>();
        boolean isShortListFlag = admSelectionProcessPlanDBO.getAdmSelectionProcessPlanDetailDBO().stream().filter(admSelectionProcessPlanDetailDBO -> admSelectionProcessPlanDetailDBO.getRecordStatus() == 'A'
            && admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getRecordStatus() == 'A' && processTypeId == admSelectionProcessPlanDetailDBO.getAdmSelectionProcessTypeDBO().getId())
            .findAny().get().getAdmSelectionProcessTypeDBO().getIsShortlistAfterThisStage();
        Map<Integer, Integer> generatedVenueTimeMap = new HashMap<>();
        Map<Integer, Integer> generatedVenueTimeMapForCenterBased = new HashMap<>();
        Map<String, Map<Integer, StudentApplnEntriesDBO>> processTypeStudentMap = new HashMap<>();
        //process type,student id,process order,shortlist,process date dbo
        Map<String, Map<Integer, Tuple3<Integer, Boolean, StudentApplnSelectionProcessDatesDBO>>> processTypeStudentsMap = new HashMap<>();
        //academicYearId,studentEntriesId,planDetailId,allotmentId,centerDetailsId
        Map<Integer, Map<Integer, Tuple3<Integer, Integer, Integer>>> selectionProcessDetailsMap = new HashMap<>();
        //programme,date,time,venueid,priority,list of center info,
        Map<Integer, Map<String, Map<String, Map<Integer, Map<Integer, Tuple4<String, String, String, Integer>>>>>> programmeDateTimeVenuePriorityCenterMap = generateAdmitCardHelper.SetDateTimeVenuePriorityForCenterBasedMap(admSelectionProcessPlanDBO);
        List<AdmSelectionProcessDBO> appliedVenueDetails = generateAdmitCardTransaction.getAdmSelectionProcessPlanDetailDBOSBySessionAndProcessType(sessionId, processTypeId);
        //process type,process order,date,venue,time,applied seat no
        Map<String, Map<Integer, Map<String, Map<Integer, Map<String, Integer>>>>> appliedProcessOrderDateVenueTimeSeatMap = generateAdmitCardHelper.setAppliedProcessOrderDateVenueTimeSeatMap(appliedVenueDetails);
        //generateAdmitCardHelper.setVenueTimeMap(admSelectionProcessPlanDBO,generatedVenueTimeMap,generatedVenueTimeMapForCenterBased,appliedProcessOrderDateVenueTimeSeatMap);

        //List<AdmSelectionProcessDBO> admSelectionProcessDBOS = generateAdmitCardTransaction.getExistingGeneratedAdmitCard(sessionId, processTypeId);
        Set<AdmSelectionProcessPlanDetailDBO> admSelectionProcessPlanDetailDBOS = admSelectionProcessPlanDBO.admSelectionProcessPlanDetailDBO.stream()
            .filter(admSelectionProcessPlanDetailDBO -> admSelectionProcessPlanDetailDBO.recordStatus == 'A').collect(Collectors.toSet());
        //convert student list with random application numbers
        studentsList = convertStudentListWithRandomApplnNos(studentsList);
        Map<Integer, Tuple3<Integer, Boolean, StudentApplnSelectionProcessDatesDBO>> shortlistStudentMap = new HashMap<>();
        Map<Integer, Tuple3<Integer, Boolean, StudentApplnSelectionProcessDatesDBO>> NotShortlistStudentMap = new HashMap<>();
        studentsList.forEach(studentApplnEntriesDBO -> {
            if (!Utils.isNullOrEmpty(studentApplnEntriesDBO.studentApplnSelectionProcessDatesDBOS)) {
                studentApplnEntriesDBO.studentApplnSelectionProcessDatesDBOS.forEach(studentApplnSelectionProcessDatesDBO -> {
                    if (studentApplnSelectionProcessDatesDBO.getRecordStatus() == 'A') {
                        if (!Utils.isNullOrEmpty(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO())
                                && studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getRecordStatus() == 'A') {
                            if (!Utils.isNullOrEmpty(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO())
                                    && studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getRecordStatus() == 'A') {
                                if (studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getId() == processTypeId
                                        && studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId() == sessionId) {
                                    if ("Center Based Entrance".equalsIgnoreCase(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())) {
                                        if (studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getIsShortlistAfterThisStage()) {
                                            //shortlistStudentMap.put()
                                        }
                                    } else {

                                    }
                                }
                                if (processTypeStudentsMap.containsKey(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode())) {
                                    Map<Integer, Tuple3<Integer, Boolean, StudentApplnSelectionProcessDatesDBO>> studentMap = processTypeStudentsMap.get(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode());
                                    Tuple3<Integer, Boolean, StudentApplnSelectionProcessDatesDBO> studentTuple = Tuples.of(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),
                                            studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getIsShortlistAfterThisStage(),
                                            studentApplnSelectionProcessDatesDBO);
                                    studentMap.put(studentApplnEntriesDBO.id, studentTuple);
                                    processTypeStudentsMap.put(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode(), studentMap);
                                } else {
                                    Map<Integer, Tuple3<Integer, Boolean, StudentApplnSelectionProcessDatesDBO>> studentMap = new HashMap<>();
                                    Tuple3<Integer, Boolean, StudentApplnSelectionProcessDatesDBO> studentTuple = Tuples.of(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder(),
                                            studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getIsShortlistAfterThisStage(),
                                            studentApplnSelectionProcessDatesDBO);
                                    studentMap.put(studentApplnEntriesDBO.id, studentTuple);
                                    processTypeStudentsMap.put(studentApplnSelectionProcessDatesDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getMode(), studentMap);
                                }
                            }
                        }
                    }
                });
            }
        });

        if (!Utils.isNullOrEmpty(processTypeStudentMap)) {
            //process type,student id,process order,shortlist,process date dbo
            processTypeStudentsMap.forEach((processType, studentMap) -> {
                if ("Center Based Entrance".equalsIgnoreCase(processType)) {
                    studentMap.forEach((key, studentTuple) -> {
                        if (studentTuple.getT2()) {

                        }
                    });
                } else {
                    //selection processes other than Center based entrance
                    studentMap.forEach((key, studentTuple) -> {
                        if (studentTuple.getT2()) {//isShortlistAfterThisStage yes
                            shortlistStudentMap.put(key, studentTuple);
                        } else {
                            NotShortlistStudentMap.put(key, studentTuple);
                        }
                    });
                }
            });
        }
        if (!Utils.isNullOrEmpty(selectionProcessDetailsMap)) {
            selectionProcessDetailsMap.forEach((academicYear, studentMap) -> {
                studentMap.forEach((studentApplnEntriesId, details) -> {
                    AdmSelectionProcessDBO admSelectionProcessDBO = new AdmSelectionProcessDBO();
                    ErpAcademicYearDBO erpAcademicYearDBO = new ErpAcademicYearDBO();
                    erpAcademicYearDBO.setId(academicYear);
                    admSelectionProcessDBO.setErpAcademicYearDBO(erpAcademicYearDBO);
                    StudentApplnEntriesDBO studentApplnEntriesDBO = new StudentApplnEntriesDBO();
                    studentApplnEntriesDBO.setId(studentApplnEntriesId);
                    admSelectionProcessDBO.setStudentApplnEntriesDBO(studentApplnEntriesDBO);
                    AdmSelectionProcessPlanDetailDBO admSelectionProcessPlanDetailDBO = new AdmSelectionProcessPlanDetailDBO();
                    admSelectionProcessPlanDetailDBO.setId(details.getT1());
                    admSelectionProcessDBO.setAdmSelectionProcessPlanDetailDBO(admSelectionProcessPlanDetailDBO);
                    AdmSelectionProcessPlanDetailAllotmentDBO admSelectionProcessPlanDetailAllotmentDBO = new AdmSelectionProcessPlanDetailAllotmentDBO();
                    admSelectionProcessPlanDetailAllotmentDBO.setId(details.getT2());
                    admSelectionProcessDBO.setAdmSelectionProcessPlanDetailAllotmentDBO(admSelectionProcessPlanDetailAllotmentDBO);
                    AdmSelectionProcessCenterDetailsDBO admSelectionProcessCenterDetailsDBO = new AdmSelectionProcessCenterDetailsDBO();
                    admSelectionProcessCenterDetailsDBO.setId(details.getT3());
                    admSelectionProcessDBO.setAdmSelectionProcessCenterDetailsDBO(admSelectionProcessCenterDetailsDBO);
                    admSelectionProcessDBO.setRecordStatus('A');
                    admSelectionProcessDBO.setCreatedUsersId(Integer.parseInt(userId));

                    selectionProcessDBOList.add(admSelectionProcessDBO);
                });
            });
        }
        return selectionProcessDBOList;
    }

    private List<StudentApplnEntriesDBO> convertStudentListWithRandomApplnNos(List<StudentApplnEntriesDBO> studentsList) {
        Random rand = new Random();
        List<Integer> applnNumberList = Arrays.asList(111,112,113,114,115,116,117,118,119);
        applnNumberList.sort(Integer::compareTo);
        Collections.shuffle(applnNumberList);
        System.out.println(List.of(applnNumberList));
        studentsList = studentsList.stream().sorted(Comparator.comparing(o -> o.applicationNo)).collect(Collectors.toList());
        System.out.println(List.of(studentsList));
        Collections.shuffle(studentsList);
        System.out.println(List.of(studentsList));
//        List<StudentApplnEntriesDBO> studentListWithRandomApplnNos = new ArrayList<>();
//        for (int i = 0; i <= studentsList.size(); i++) {
//            int randomIndex = rand.nextInt(studentsList.size());
//            studentListWithRandomApplnNos.add(studentsList.get(randomIndex));
//            studentsList.remove(randomIndex);
//        }
//        System.out.println(List.of(studentListWithRandomApplnNos));
        return studentsList;
    }

    public Mono<ApiResult> uploadFiles(Flux<FilePart> data, String filePath, String[] fileTypeAccept) {
        ApiResult result = new ApiResult();
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(true);
        final Path basePath = Paths.get(filePath);
        return data
                .flatMap(fp ->fp.transferTo(basePath.resolve(fp.filename())))
                .doOnComplete(()-> {
                    Tika tika = new Tika();
                    data.handle((fp, synchronousSink) -> {
                        String detectType =  tika.detect(filePath+fp.filename());
                        if(!(Arrays.stream(fileTypeAccept).anyMatch(detectType::contains))) {
                            atomicBoolean.set(false);
                            File file = new File(filePath+fp.filename());
                            if(file.exists()) {
                                file.delete();
                            }
                            synchronousSink.complete();
                        }else{
                            try {
                                File file = new File(filePath+fp.filename());
                                XSSFWorkbook myExcelBook = null;
                                myExcelBook = new XSSFWorkbook(new FileInputStream(file));
                                XSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
                                XSSFRow row = myExcelSheet.getRow(0);
                                System.out.println("name : " + row.getCell(0).getCellType());
                                myExcelBook.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).subscribe();
                    result.setSuccess(atomicBoolean.get());
                    if(result.success==false) {
                        result.setFailureMessage("File is not supported");
                    }
                }).then(Mono.just(result));
//        return data.map(filePart -> {
//            File file = new File(filePath+filePart.filename());
//            if (file.exists())
//                file.delete();
//            try {
//                file.createNewFile();
//                filePart.transferTo(file);
////                String detectFileType = tika.detect(file);
////                System.out.println(detectFileType);
//                FileOutputStream xlsOutputStream = null;
//                HSSFWorkbook workbook = null;
//                XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
//                XSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
//                XSSFRow row = myExcelSheet.getRow(0);
//                System.out.println("name : " + row.getCell(0).getCellType());
//                myExcelBook.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return true;
//        }).then(Mono.just(result));




//        return data.takeWhile(item -> {
//            //String path = filePath.getAbsolutePath();
//            //fileLocation = path.substring(0, path.length() - 1) + file.getOriginalFilename();
//            File file = new File(filePath+item.filename());
//            //item.transferTo(file);
//
//
//
////            File file2 = new File(".");
////            for(String fileNames : file2.list()){
////                System.out.println(fileNames);
////            }
//            FileOutputStream xlsOutputStream = null;
//            HSSFWorkbook workbook = null;
//            try {
//                XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
//                XSSFSheet myExcelSheet = myExcelBook.getSheet("Birthdays");
//                XSSFRow row = myExcelSheet.getRow(0);
//                System.out.println("name : " + row.getCell(0).getCellType());
//                myExcelBook.close();
//
//
//                xlsOutputStream = new FileOutputStream(new File(filePath+item.filename()));
//                workbook = new HSSFWorkbook();
//                HSSFSheet sheet = workbook.createSheet("firstSheet");
//                workbook.write(xlsOutputStream);
////                    if (!file.exists()) {
////                        file.createNewFile();
////                    }
////                File file = null;
////                Path path = Files.createTempFile("temp",item.filename().substring(1));
////                item.transferTo(path);
////                file = path.toFile();
//
//
////                item.transferTo(file);
////                String detectFileType = tika.detect(file);
////                result.success = Arrays.stream(fileTypeAccept).anyMatch(detectFileType::contains);//----Improves performance if size of the array is less.
//                //result.success = (Arrays.stream(fileTypeAccept).parallel().anyMatch(detectFileType::contains)) ? true : false;
//                result.success = true;
//                if(!result.success) {
//                    result.failureMessage = "notSupported";
//                }
//            }
//            catch (IOException e) {
//                result.success = false;
//                result.failureMessage= e.getMessage();
//            }
//            finally {
//                if(!result.success) {
//                    data.map(item1 -> {
//                        File file1 = new File(filePath + item1.filename());
//                        if(file1.exists()) {
//                            file1.delete();
//                        }
//                        return Mono.just(result);
//                    }).subscribe();
//                    return false;
//                }
//                try {
//                    if (workbook != null) {
//                        workbook.close();
//                    }
//                    if (xlsOutputStream != null) {
//                        xlsOutputStream.close();
//                    }
//                } catch (IOException e) {
//                }
//            }
//            return true;
//        }).then(Mono.just(result));
    }

    public Flux<GenerateAdmitCardDTO> getGridData(Integer admissionYearId) {
       return generateAdmitCardTransaction.getGridData(admissionYearId).flatMapMany(Flux::fromIterable).map(this::convertTupleToGridDto);
    }

    public GenerateAdmitCardDTO convertTupleToGridDto(Tuple tuple) {
        GenerateAdmitCardDTO dto = new GenerateAdmitCardDTO();
        if (!Utils.isNullOrEmpty(tuple)) {
            if (!Utils.isNullOrEmpty(tuple.get("adm_selection_process_plan_id")) && !Utils.isNullOrEmpty(tuple.get("selectionProcessSession"))) {
                dto.setSession(new SelectDTO());
                dto.getSession().setLabel(String.valueOf(tuple.get("selectionProcessSession")));
                dto.getSession().setValue(String.valueOf(tuple.get("adm_selection_process_plan_id")));
            }
            if (!Utils.isNullOrEmpty(tuple.get("selection_process_start_date")) && !Utils.isNullOrEmpty(tuple.get("selection_process_end_date"))) {
                dto.setSessionStartDateEndDate(Utils.convertLocalDateTimeToStringDateTime(Utils.convertStringDateTimeToLocalDateTime(String.valueOf(tuple.get("selection_process_start_date")))) +" - "+ Utils.convertLocalDateTimeToStringDateTime(Utils.convertStringDateTimeToLocalDateTime(String.valueOf(tuple.get("selection_process_end_date")))));
            }
            if (!Utils.isNullOrEmpty(tuple.get("applicationReceived"))) {
                dto.setApplicationRecieved(String.valueOf(tuple.get("applicationReceived")));
            } else {
                dto.setApplicationRecieved("0");
            }
            if (!Utils.isNullOrEmpty(tuple.get("admitCardGenerated"))) {
                dto.setAdmitCardGenerated(String.valueOf(tuple.get("admitCardGenerated")));
            } else {
                dto.setAdmitCardGenerated("0");
            }
            if (!Utils.isNullOrEmpty(tuple.get("admitCardPublished"))) {
                dto.setAdmitCardPublished(String.valueOf(tuple.get("admitCardPublished")));
            } else {
                dto.setAdmitCardPublished("0");
            }
        }
        return dto;
    }

    public Flux<GenerateAdmitCardDTO> getAdmitCardDetailsBySession(Integer sessionPlanId) {
       return generateAdmitCardTransaction.getAdmitCardDetailsBySession(sessionPlanId).flatMapMany(Flux::fromIterable).map(this::convertTupleToGnerationDto);
    }

    public GenerateAdmitCardDTO convertTupleToGnerationDto(Tuple tuple) {
        GenerateAdmitCardDTO dto = new GenerateAdmitCardDTO();
        if (!Utils.isNullOrEmpty(tuple)) {
            if (!Utils.isNullOrEmpty(tuple.get("erp_programme_id")) && !Utils.isNullOrEmpty(tuple.get("programme_name_for_application"))) {
                dto.setErpProgrammeDto(new SelectDTO());
                dto.getErpProgrammeDto().setLabel(String.valueOf(tuple.get("programme_name_for_application")));
                dto.getErpProgrammeDto().setValue(String.valueOf(tuple.get("erp_programme_id")));
            }
            if (!Utils.isNullOrEmpty(tuple.get("selectionDate"))) {
                dto.setSelectionProcessDate(String.valueOf(tuple.get("selectionDate")));
            }
            if (!Utils.isNullOrEmpty(tuple.get("applicationReceived"))) {
                dto.setApplicationRecieved(String.valueOf(tuple.get("applicationReceived")));
            } else {
                dto.setApplicationRecieved("0");
            }
            if (!Utils.isNullOrEmpty(tuple.get("admitCardGenerated"))) {
                dto.setAdmitCardGenerated(String.valueOf(tuple.get("admitCardGenerated")));
            } else {
                dto.setAdmitCardGenerated("0");
            }
            if (!Utils.isNullOrEmpty(tuple.get("admitCardPublished"))) {
                dto.setAdmitCardPublished(String.valueOf(tuple.get("admitCardPublished")));
            } else {
                dto.setAdmitCardPublished("0");
            }
        }
        return dto;
    }

//    public DoubleStream generateRegeneratePublishAdmitCard(String userId) {
//        return null;
//    }
}
