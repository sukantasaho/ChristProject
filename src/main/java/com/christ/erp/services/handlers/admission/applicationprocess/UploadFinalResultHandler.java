package com.christ.erp.services.handlers.admission.applicationprocess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.admission.applicationprocess.UploadFinalResultTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Mono;

@Service
public class UploadFinalResultHandler {
	
	@Autowired
	private UploadFinalResultTransaction uploadFinalResultTransaction;
	
	@Autowired
	CommonApiTransaction commonApiTransaction1;
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Mono<ApiResult>  finalResultUpload(String yearId, Mono<EmpApplnAdvertisementImagesDTO> data,String userId)   {
		Map<Integer, List<String>> map1 = new HashMap<>();
		Map<Integer, Integer> map2 = new HashMap<>();
		List<Integer> duplicateApplicationNo = new ArrayList<Integer>();
		return data.handle((data1,synchronousSink) -> {
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook("ExcelUpload//"+data1.fileName+"."+data1.extension);
			} catch (Exception e) {
				e.printStackTrace();
			}
			XSSFSheet sheet = workbook.getSheetAt(0);
			if(!Utils.isNullOrEmpty(sheet.getRow(0))) {
				int rowLength = sheet.getRow(0).getLastCellNum();
				Integer p = 1;
				for(Row row : sheet) {
	//			sheet.forEach(row -> {
					if(p != 1) {
						map1.put(p,  new ArrayList<String>());
						for(int cn=0; cn<rowLength; cn++) {
							if(cn == 0) {
								Cell cell = row.getCell(cn); 
								if(cell != null) {
									if(!map2.containsKey((int)cell.getNumericCellValue())) {
									    map2.put((int)cell.getNumericCellValue(), (int)cell.getNumericCellValue());
									} else {
										duplicateApplicationNo.add((int)cell.getNumericCellValue());
									}	
								}
							}
							Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
						    if(cell == null) {
						    	String cellValue = null;
						    	map1.get(p).add(cellValue);
						    } else if (cell.getCellType() == CellType.NUMERIC) {
						    	if (DateUtil.isCellDateFormatted(cell)) {
						    		String[] s = cell.getLocalDateTimeCellValue().toLocalTime().toString().split(":"); 
						    		if(Integer.parseInt(s[0]) != 00) {
						    			map1.get(p).add(cell.getLocalDateTimeCellValue().toLocalTime() + "");
						    		} else {
						    			map1.get(p).add(cell.getLocalDateTimeCellValue() + "");
						    		}
						    	} else {
						    		map1.get(p).add(String.valueOf((int)cell.getNumericCellValue()));
			   					}
						    } else {
						    	String cellValue =  cell.toString() ; 
							    map1.get(p).add(cellValue);
						    }
					    }
					}
					p++;
	//			 });
				}
			}
			List<String> applicationNoEmpty = new ArrayList<String>();
			List<String> emptyStatus = new ArrayList<String>();
			List<String> programmeInvalid = new ArrayList<String>();
			List<String> campusInvalid = new ArrayList<String>();
			List<String> selectData =  new ArrayList<String>();
			List<String> notSelectData  = new ArrayList<String>();
			List<Integer> applicationNos  = new ArrayList<Integer>();
			List<String> campusProgrammeInvalid = new ArrayList<String>();
			List<String> campusCodeList = uploadFinalResultTransaction.getCampusCode();
			List<String> programmeCodeList = uploadFinalResultTransaction.getProgrammeCode();
			Integer year = uploadFinalResultTransaction.getyear(Integer.parseInt(yearId));
			List<ErpCampusProgrammeMappingDBO> allDatas = uploadFinalResultTransaction.checkCampusProgrammeValid1();
			Map<String, Integer> start = allDatas.stream().collect(Collectors.toMap(s -> (s.getErpProgrammeDBO().getProgrammeCode()+s.getErpCampusDBO().getShortName()),
					                     s -> !Utils.isNullOrEmpty(s.getProgrammeCommenceYear()) ? s.getProgrammeCommenceYear(): 0));		
			Map<String, Integer> end = allDatas.stream().collect(Collectors.toMap(s -> (s.getErpProgrammeDBO().getProgrammeCode()+s.getErpCampusDBO().getShortName()),
					                   s -> !Utils.isNullOrEmpty(s.getProgrammeInactivatedYear())  ? s.getProgrammeInactivatedYear() : 0));
			map1.forEach((k,v) -> {
				if(!Utils.isNullOrEmpty(v.get(0))) {
					if(!applicationNos.contains(Integer.parseInt( v.get(0)))) {
						applicationNos.add(Integer.parseInt(v.get(0)));
					}
				}
				if(Utils.isNullOrEmpty(v.get(0))) {
					applicationNoEmpty.add(k.toString());
				}
				if(!Utils.isNullOrEmpty(v.get(0))) {
					if(!Utils.isNullOrEmpty(v.get(1))){
						if(v.get(1).equalsIgnoreCase("Selected")) {
							if(Utils.isNullOrEmpty(v.get(2))  || Utils.isNullOrEmpty(v.get(3))  || Utils.isNullOrEmpty(v.get(4))) {
								selectData.add(v.get(0));
							}
						} else {
							if(Utils.isNullOrEmpty(v.get(6))) {
								notSelectData.add(v.get(0));
							}
						}
					} else {
						emptyStatus.add(v.get(0));
					}
					if(!Utils.isNullOrEmpty(v.get(2))) {
						if(!programmeCodeList.contains(v.get(2).trim().replace(" ", ""))) {
							programmeInvalid.add(v.get(0));
						}	
					}
					if(!campusCodeList.contains(v.get(3))) {
						campusInvalid.add(v.get(0));
					}
					if(start.containsKey(v.get(2)+v.get(3))) {
						Integer	programmeCommenceYear = start.get(v.get(2)+v.get(3));
						if(programmeCommenceYear != 0 ) {
							if(!(year >= programmeCommenceYear)) {
								campusProgrammeInvalid.add(v.get(0));
						    }
						}
					}
					if(end.containsKey(v.get(2)+v.get(3))) {
						Integer	programmeInactivatedYear = end.get(v.get(2)+v.get(3));
						if(programmeInactivatedYear != 0) {
							if(!(year < programmeInactivatedYear)) {
								campusProgrammeInvalid.add(v.get(0));
							}	
						}
					}
				}
			});
			List<String> applicationNotValid = new ArrayList<String>();
			Map<Integer, StudentApplnEntriesDBO> mapList =  uploadFinalResultTransaction.getApplicantsDetails(applicationNos).stream().collect(Collectors.toMap(s -> s.getApplicationNo(), s -> s));
			map1.forEach((k,v) -> {
				if(!Utils.isNullOrEmpty(v.get(0))) {
					if(!mapList.containsKey(Integer.parseInt(v.get(0)))) {
						applicationNotValid.add(v.get(0));
					}
				}
			});
			if(Utils.isNullOrEmpty(map1)) {
				synchronousSink.error(new GeneralException("Warning  Excel Sheet is Empty" ));
			} else if(!Utils.isNullOrEmpty(applicationNoEmpty)) {
				synchronousSink.error(new GeneralException("Warning  Application Number is Empty For the row " + applicationNoEmpty));
			} else if(!Utils.isNullOrEmpty(duplicateApplicationNo)) {
				synchronousSink.error(new GeneralException("Warning  Duplicate Appliction Number " + duplicateApplicationNo));
			} else if(!Utils.isNullOrEmpty(applicationNotValid)) {
				synchronousSink.error(new GeneralException("Warning  Invalid  Application Number " + applicationNotValid));
			} else if(!Utils.isNullOrEmpty(emptyStatus)) {
				synchronousSink.error(new GeneralException("Warning  Status is Empty For these Application Number " + emptyStatus));
			} else if(!Utils.isNullOrEmpty(selectData)) {
				synchronousSink.error(new GeneralException("Warning  Programme Code or Campus Code or  Last Date of Fee Payment is Empty  For these Application Number " + selectData));
			} else if(!Utils.isNullOrEmpty(notSelectData)) {
				synchronousSink.error(new GeneralException("Warning  Remarks is Empty For these Application Number" + notSelectData));
			} else if(!Utils.isNullOrEmpty(programmeInvalid)) {
				synchronousSink.error(new GeneralException("Warning  Programme Code is Invalid For these Application Number" + programmeInvalid));
			} else if(!Utils.isNullOrEmpty(campusInvalid)) {
				synchronousSink.error(new GeneralException("Warning  Campus Code is Invalid For these Application Number" + campusInvalid));
			} else if(!Utils.isNullOrEmpty(campusProgrammeInvalid)) {
				synchronousSink.error(new GeneralException("Warning  Campus code and programme code is Invalid for selected year For these Application Number" + campusProgrammeInvalid));
			} else {
				synchronousSink.next(mapList);
			}
		}).map(data2 -> convertDtoToDbo( (Map<Integer, StudentApplnEntriesDBO>) data2,map1,userId))
				.flatMap( s ->{ 
					uploadFinalResultTransaction.update(s);	
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoToDbo(Map<Integer, StudentApplnEntriesDBO> mapList, Map<Integer, List<String>> map1, String userId) {
		List<Object> data = new ArrayList<Object>();
		List<StudentApplnEntriesDBO> value = new ArrayList<StudentApplnEntriesDBO>();
		List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		Tuple selected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_SELECTED_UPLOADED");
		Tuple notSelected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_NOT_SELECTED_UPLOADED");
		Tuple waitListed = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_WAITLISTED_UPLOADED");		
		Map<String,Integer>	erpCampusProgrammingId = uploadFinalResultTransaction.getErpCampusProgrammingId().stream().collect(Collectors.toMap(s -> s.getErpProgrammeDBO().getProgrammeCode()+s.getErpCampusDBO().getShortName(), s -> s.getId()));
		map1.forEach((k,v) -> { 
				Boolean isStatusChanged = false;
				StudentApplnEntriesDBO studentData = mapList.get(Integer.parseInt(v.get(0)));
				if(!Utils.isNullOrEmpty(studentData)) {
					if(!studentData.getApplicationCurrentProcessStatus().processCode.equalsIgnoreCase("ADM_APPLN_SELECTED") 
							&& !studentData.getApplicationCurrentProcessStatus().processCode.equalsIgnoreCase("ADM_APPLN_NOT_SELECTED") 
							&& !studentData.getApplicationCurrentProcessStatus().processCode.equalsIgnoreCase("ADM_APPLN_WAITLISTED" )) {
						if(v.get(1).equalsIgnoreCase("Selected") && studentData.getApplicationCurrentProcessStatus().getId() != Integer.parseInt(selected.get("erp_work_flow_process_id").toString())) {
							studentData.setApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
							studentData.getApplicationCurrentProcessStatus().setId(Integer.parseInt(selected.get("erp_work_flow_process_id").toString()));
							isStatusChanged = true;
							LocalDateTime dateTime = Utils.convertStringDateTimeToLocalDateTime(v.get(4));
							if(!Utils.isNullOrEmpty(v.get(5))) {
								String a[] = v.get(5).split(":");
								Long hr = Long.parseLong(a[0].toString());
								Long min = Long.parseLong(a[1].toString());
								dateTime = dateTime.plusHours(hr);
								dateTime = dateTime.plusMinutes(min);
							}
							studentData.setFeePaymentFinalDateTime(dateTime);
							if(erpCampusProgrammingId.containsKey(v.get(2)+v.get(3))) {
								Integer campusProgrammeMaapingId = erpCampusProgrammingId.get(v.get(2)+v.get(3));
								studentData.setErpCampusProgrammeMappingDBO(new ErpCampusProgrammeMappingDBO());
								studentData.getErpCampusProgrammeMappingDBO().setId(campusProgrammeMaapingId);
							}
						} else if(v.get(1).equalsIgnoreCase("Not Selected") && studentData.getApplicationCurrentProcessStatus().getId() != Integer.parseInt(notSelected.get("erp_work_flow_process_id").toString())) {
							studentData.setApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
							studentData.getApplicationCurrentProcessStatus().setId(Integer.parseInt(notSelected.get("erp_work_flow_process_id").toString()));
							isStatusChanged = true;
							studentData.setSelectionStatusRemarks(v.get(6));
						} else if(studentData.getApplicationCurrentProcessStatus().getId() != Integer.parseInt(waitListed.get("erp_work_flow_process_id").toString())) {
							studentData.setApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
							studentData.getApplicationCurrentProcessStatus().setId(Integer.parseInt(waitListed.get("erp_work_flow_process_id").toString()));
							isStatusChanged = true;
							studentData.setSelectionStatusRemarks(v.get(6));
						}
						LocalDateTime statusDateTime = LocalDateTime.now();
						studentData.setApplicationStatusTime(statusDateTime);
						studentData.setModifiedUsersId(Integer.parseInt(userId));
						value.add(studentData);
						if(isStatusChanged) {
							ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
							erpWorkFlowProcessStatusLogDBO.setEntryId(studentData.getId());
							erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
							erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(studentData.getApplicationCurrentProcessStatus().getId());
							erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
							erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
							statusLogList.add(erpWorkFlowProcessStatusLogDBO);
						}
					}
				}
		});
		data.addAll(value);
		data.addAll(statusLogList);
//		commonApiTransaction1.saveErpWorkFlowProcessStatusLogDBO1(statusLogList);
		return data;
	}
	
}