package com.christ.erp.services.handlers.hostel.application;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationRoomTypePreferenceDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDisciplinaryActionsTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.admission.applicationprocess.ErpResidentCategoryDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeLevelDTO;
import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelDisciplinaryActionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationRoomTypePreferenceDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDisciplinaryActionsTypeDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddressDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddtnlDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.application.SelectionProcessTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SelectionProcessHandler {

	@Autowired
	private SelectionProcessTransaction selectionProcessTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	public Flux<HostelApplicationDTO> getGridData(String yearId, String hostelId) {
		List<HostelApplicationDBO> hostelApplicationDBOList = selectionProcessTransaction.getGridData(yearId, hostelId);
		return this.convertDboToDto(hostelApplicationDBOList,yearId, hostelId );
	}

	private Flux<HostelApplicationDTO> convertDboToDto(List<HostelApplicationDBO> hostelApplicationDBOList, String yearId, String hostelId ) {
		List<HostelApplicationDTO> hostelApplicationDTOList = new ArrayList<HostelApplicationDTO>();
		if(!Utils.isNullOrEmpty(hostelApplicationDBOList)) {
			hostelApplicationDBOList.forEach(dbo -> {
				HostelApplicationDTO dto = new HostelApplicationDTO();
				dto.setId(String.valueOf(dbo.getId()));
				if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
					dto.setHostelApplicationNum(dbo.getApplicationNo());
				}
				if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO())) {
					dto.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
					dto.getStudentApplnEntriesDTO().setId(dbo.getStudentApplnEntriesDBO().getId());
					if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getApplicantName())) {
						dto.getStudentApplnEntriesDTO().setApplicantName(dbo.getStudentApplnEntriesDBO().getApplicantName());
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getApplicationNo())) {
						dto.getStudentApplnEntriesDTO().setApplicationNumber(String.valueOf(dbo.getStudentApplnEntriesDBO().getApplicationNo()));
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getStudentPersonalDataDBO())) {
						dto.getStudentApplnEntriesDTO().setStudentPersonalDataDTO(new StudentPersonalDataDTO());
						if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getStudentPersonalDataDBO().getStudentPersonalDataAddressDBO())) {
							dto.getStudentApplnEntriesDTO().getStudentPersonalDataDTO().setStudentPersonalDataAddtnlDTO(new StudentPersonalDataAddtnlDTO());
							dto.getStudentApplnEntriesDTO().getStudentPersonalDataDTO().getStudentPersonalDataAddtnlDTO().setProfilePhotoUrl(dbo.getStudentApplnEntriesDBO().getStudentPersonalDataDBO().getStudentPersonalDataAddtnlDBO().getProfilePhotoUrl());
						}
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getErpResidentCategoryDBO())) {
						dto.getStudentApplnEntriesDTO().setErpResidentCategoryDTO(new SelectDTO());
						dto.getStudentApplnEntriesDTO().getErpResidentCategoryDTO().setValue(String.valueOf(dbo.getStudentApplnEntriesDBO().getErpResidentCategoryDBO().getId()));
						dto.getStudentApplnEntriesDTO().getErpResidentCategoryDTO().setLabel(dbo.getStudentApplnEntriesDBO().getErpResidentCategoryDBO().getResidentCategoryName());
					}
				}
				if(!Utils.isNullOrEmpty(dbo.getStudentDBO())) {
					dto.setStudent(new StudentDTO());
					dto.getStudent().setId(dbo.getStudentDBO().getId());
					if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentName())) {
						dto.getStudent().setStudentName(dbo.getStudentDBO().getStudentName());
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getRegisterNo())) {
						dto.getStudent().setRegisterNo(dbo.getStudentDBO().getRegisterNo());
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getErpCampusProgrammeMappingId())) {
						dto.getStudent().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
						dto.getStudent().getErpCampusProgrammeMappingId().setProgramName(dbo.getStudentDBO().getErpCampusProgrammeMappingId().getErpProgrammeDBO().getProgrammeName());
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentPersonalDataId())) {
						dto.getStudent().setStudentPersonalDataDTO(new StudentPersonalDataDTO());
						if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO())) {
							dto.getStudent().getStudentPersonalDataDTO().setStudentPersonalDataAddressDTO(new StudentPersonalDataAddressDTO());
							if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getCurrentAddressLine1())) {
								dto.getStudent().getStudentPersonalDataDTO().getStudentPersonalDataAddressDTO().setCurrentAddressLine1(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getCurrentAddressLine1());
							}
							if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getCurrentAddressLine2())) {
								dto.getStudent().getStudentPersonalDataDTO().getStudentPersonalDataAddressDTO().setCurrentAddressLine2(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getCurrentAddressLine2());
							}
							if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getPermanentAddressLine1())) {
								dto.getStudent().getStudentPersonalDataDTO().getStudentPersonalDataAddressDTO().setPermanentAddressLine1(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getPermanentAddressLine1());
							}
							if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getPermanentAddressLine2())) {
								dto.getStudent().getStudentPersonalDataDTO().getStudentPersonalDataAddressDTO().setPermanentAddressLine2(dbo.getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getPermanentAddressLine2());
							}
						}
					}
				}
				if(!Utils.isNullOrEmpty(dbo.getIsOffline())) {
					String returnValue = String.valueOf(dbo.getIsOffline());
					if(returnValue.equals("true")) {
						dto.setModeOfApplication("offline"); 
					} else {
						dto.setModeOfApplication("online"); 
					}
				}
				String processCode = commonApiTransaction1.getErpWorkFlowProcessCodebyId(dbo.getHostelApplicantCurrentProcessStatus().getId());
				if(processCode.equalsIgnoreCase("HOSTEL_APPLICATION_NEW_APPLIED")) {
					dto.setAdmissionStatus("New Admission");
				} else if(processCode.equalsIgnoreCase("HOSTEL_APPLICATION_RENEWAL_APPLIED"))  {
					HostelAdmissionsDBO data = selectionProcessTransaction.getAdmissionId(String.valueOf(dbo.getId()));
					if(!Utils.isNullOrEmpty(data)) {
						List<HostelDisciplinaryActionsTypeDBO> result = selectionProcessTransaction.getDisciplpinaryCount(dbo.getErpAcademicYearDBO().getId(), data.getId());
						Long disciplinaryCount = result.stream().count();
						if(!Utils.isNullOrEmpty(disciplinaryCount)) {
							StringBuffer stringbuffer = new StringBuffer("");
							stringbuffer.append("Readmission");
							stringbuffer.append("(" + disciplinaryCount + ")");
							dto.setAdmissionStatus(stringbuffer.toString());
							result.forEach(disciplinaryData -> {
								HostelDisciplinaryActionsTypeDTO actionDTO = new HostelDisciplinaryActionsTypeDTO();
								actionDTO.setId(disciplinaryData.getId());
								if(!Utils.isNullOrEmpty(disciplinaryData.getHostelDisciplinaryActions())) {
								actionDTO.setHostelDisciplinaryActions(disciplinaryData.getHostelDisciplinaryActions());
								}
								if(!Utils.isNullOrEmpty(disciplinaryData.getFineAmount())) {
									actionDTO.setFineAmount(disciplinaryData.getFineAmount());
								}
								if(!Utils.isNullOrEmpty(disciplinaryData.getHostelDisciplinaryActionsDBOSet())) {
								List<HostelDisciplinaryActionsDTO> disciplinaryActionsList = new ArrayList<HostelDisciplinaryActionsDTO>();
								disciplinaryData.getHostelDisciplinaryActionsDBOSet().forEach(subDbo -> {
									HostelDisciplinaryActionsDTO dtos = new HostelDisciplinaryActionsDTO();
									dtos.setId(subDbo.getId());
									if(!Utils.isNullOrEmpty(subDbo.getRemarks())) {
										dtos.setRemarks(subDbo.getRemarks());
									}
									disciplinaryActionsList.add(dtos);
								});	
								actionDTO.setHostelDisciplinaryActionsDTO(disciplinaryActionsList);
								dto.setHostelDisciplinaryActionsTypeDTO(actionDTO);
								}
							});
						} 
					} else {
						dto.setAdmissionStatus("Readmission");
					}
				}
				if(!Utils.isNullOrEmpty(dbo.getRemarks())) {
					dto.setRemarks(dbo.getRemarks());
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelApplicationRoomTypePreferenceDBO())) {
				List<HostelApplicationRoomTypePreferenceDTO> roomTypePreferenceList = new ArrayList<HostelApplicationRoomTypePreferenceDTO>();
				dbo.getHostelApplicationRoomTypePreferenceDBO().forEach(subDbo -> {
					HostelApplicationRoomTypePreferenceDTO subDTO = new HostelApplicationRoomTypePreferenceDTO();
					subDTO.setId(subDbo.getId());
					if(!Utils.isNullOrEmpty(subDbo.getPreferenceOrder())) {
					subDTO.setPreferenceOrder(subDbo.getPreferenceOrder());
					}
					if(!Utils.isNullOrEmpty(subDbo.getHostelRoomTypesDBO())) {
					subDTO.setHostelRoomTypesDTO(new SelectDTO());
					subDTO.getHostelRoomTypesDTO().setValue(String.valueOf(subDbo.getHostelRoomTypesDBO().getId()));
					subDTO.getHostelRoomTypesDTO().setLabel(subDbo.getHostelRoomTypesDBO().getRoomType());
					roomTypePreferenceList.add(subDTO);
					}
				});
				dto.setHostelApplicationRoomTypePreferenceDTO(roomTypePreferenceList);
				hostelApplicationDTOList.add(dto);
				}
			});
		}
		return Flux.fromIterable(hostelApplicationDTOList);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> update(Mono<List<HostelApplicationDTO>> dto, String userId) {
		return dto.map(data -> convertDtoDbo(data,userId))
				.flatMap(s -> { 

					selectionProcessTransaction.merge(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoDbo(List<HostelApplicationDTO> dto, String userId) {
		List<Object> data = new ArrayList<Object>();
		List<Integer> applicationIds = new ArrayList<Integer>();
		List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		dto.forEach(dtos -> {
			applicationIds.add(Integer.parseInt(dtos.getId()));
		});
		List<HostelApplicationDBO> hostelApplicationDBOList = selectionProcessTransaction.getData(applicationIds);
		Map<Integer, HostelApplicationDBO> hostelApplicationMap = new HashMap<Integer, HostelApplicationDBO>();
		hostelApplicationDBOList.forEach(exist -> {
			hostelApplicationMap.put(exist.getId(), exist);
		});
		dto.forEach(hostelApplicationDTO -> {
			HostelApplicationDBO dbo = null;
			if(hostelApplicationMap.containsKey(Integer.parseInt(hostelApplicationDTO.getId()))) {
				dbo = hostelApplicationMap.get(Integer.parseInt(hostelApplicationDTO.getId()));
				if(!Utils.isNullOrEmpty(hostelApplicationDTO.getStatus()) && hostelApplicationDTO.getStatus().equalsIgnoreCase("Selected")) {	
					Tuple selected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_SELECTED_UPLOADED");
					if(selected.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(selected.get("application_status_display_text").toString())) {
						ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
						application.id =Integer.parseInt(selected.get("erp_work_flow_process_id").toString());
						dbo.hostelApplicationCurrentProcessStatus = application;
						dbo.setHostelApplicationStatusTime(LocalDateTime.now());
					}	
					if(!Utils.isNullOrEmpty(hostelApplicationDTO.getAllotedRoomType())) {
						dbo.setAllottedHostelRoomTypeDBO(new HostelRoomTypeDBO());
						dbo.getAllottedHostelRoomTypeDBO().setId(Integer.parseInt(hostelApplicationDTO.getAllotedRoomType().getValue()));
					}
				} else if(!Utils.isNullOrEmpty(hostelApplicationDTO.getStatus()) && hostelApplicationDTO.getStatus().equalsIgnoreCase("Not Selected")) {
					Tuple notSelected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_NOT_SELECTED_UPLOADED");
					if(notSelected.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(notSelected.get("application_status_display_text").toString())) {
						ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
						application.id =Integer.parseInt(notSelected.get("erp_work_flow_process_id").toString());
						dbo.hostelApplicationCurrentProcessStatus = application;
						dbo.setHostelApplicationStatusTime(LocalDateTime.now());
					}	
					if(!Utils.isNullOrEmpty(hostelApplicationDTO.getRemarks())) {
						dbo.setRemarks(hostelApplicationDTO.getRemarks());
					}
				}
			}
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.setEntryId(Integer.parseInt(hostelApplicationDTO.getId()));
			erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
			erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(dbo.getHostelApplicationCurrentProcessStatus().getId());
			erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
			erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			statusLogList.add(erpWorkFlowProcessStatusLogDBO);
		});
		data.addAll(statusLogList);
		data.addAll(hostelApplicationDBOList);
		return data;
	}

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public Mono<ApiResult> selectionProcessUpload(String yearId, String hostelId, Mono<HostelApplicationDTO> data, String userId) {
		Map<Integer, List<String>> map1 = new HashMap<>();
		Map<String, Integer> map4 = new HashMap();
		List<HostelRoomTypeDBO> result = selectionProcessTransaction.getRoomTypeForStudent(hostelId);
		Map<String, Integer> map3 = result.stream().collect(Collectors.toMap(s -> s.getRoomType(), s -> s.getId()));
		return data.handle((data1, synchronousSink) -> {
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook("ExcelFileUpload//"+data1.getFileName()+"."+data1.getExtension());
			} catch (Exception e) {
				e.printStackTrace();
			}
			XSSFSheet sheet = workbook.getSheetAt(0);
			if(!Utils.isNullOrEmpty(sheet.getRow(0))) {
				int rowLength = sheet.getRow(0).getLastCellNum();
				Integer p = 1;
				for(Row row : sheet) {
					if(p != 1) {
						map1.put(p, new ArrayList<String>());
						for(int cn = 0; cn<rowLength; cn++) {
							Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
							if(cell == null) {
								String cellValue = null;
								map1.get(p).add(cellValue);
							} else if(cell.getCellType() == CellType.NUMERIC) {
								map1.get(p).add(String.valueOf((int)cell.getNumericCellValue()));
							} else {
								String cellValue =  cell.toString() ; 
								map1.get(p).add(cellValue);
							}
						}
					}
					p++;
				}
			}
			List<String> emptyStatus = new ArrayList<String>();
			List<String> selectData =  new ArrayList<String>();
			List<String> notSelectData = new ArrayList<String>();
			List<Integer> applicationNo = new ArrayList<Integer>();
			List<String> roomTypeEmpty = new ArrayList<String>();
			List<String> roomtype = new ArrayList<String>();
			map1.forEach((k,v) -> {
				if(!Utils.isNullOrEmpty(v.get(1))) {
					if(!applicationNo.contains(Integer.parseInt(v.get(1)))) {
						applicationNo.add(Integer.parseInt(v.get(1)));
					}
					if(!Utils.isNullOrEmpty(v.get(10))){
						if(v.get(10).equalsIgnoreCase("Selected")) {
							if(Utils.isNullOrEmpty(v.get(11))) {
								selectData.add(v.get(1));
							}
						} else {
							if(Utils.isNullOrEmpty(v.get(12))) {
								notSelectData.add(v.get(1));
							}
						} 
					} else {
						emptyStatus.add(v.get(1));
					}
					if(v.get(10).equalsIgnoreCase("Selected")) {
						if(!map4.containsKey(v.get(11))) {
							roomtype.add(v.get(11));
							map4.put(v.get(11), 1);
						} else {
							Integer counts = map4.get(v.get(11));
							counts ++;
							map4.replace(v.get(11), counts);
						}
					}
				}
			});
			roomtype.forEach(k1 -> {
				if(map3.containsKey(k1)) {
					Integer availableSeatsCount = selectionProcessTransaction.getAvailableSeats(hostelId, yearId, map3.get(k1));
					if(availableSeatsCount != 0) {
						if(map4.get(k1) > availableSeatsCount) {
							roomTypeEmpty.add(k1);
						}
					}
				}	
			});
			Map<Integer, HostelApplicationDBO> mapList1 = selectionProcessTransaction.getHostelApplicantDetails(applicationNo).stream().collect(Collectors.toMap(s ->s.getStudentApplnEntriesDBO().getApplicationNo(), s -> s));
			List<String> applicationNoNotValid = new ArrayList<String>();
			List<Integer> entriesId = selectionProcessTransaction.checkIsStudents(applicationNo);
			Map<Integer, StudentDBO> mapList = selectionProcessTransaction.checkEntriesId(entriesId).stream().collect(Collectors.toMap(s -> s.getStudentApplnEntriesDBO().getApplicationNo(), s -> s));
			map1.forEach((k,v) -> {
				if(!Utils.isNullOrEmpty(v.get(1))) {
					if(!mapList.containsKey(Integer.parseInt(v.get(1)))) {
						applicationNoNotValid.add(v.get(1));
					}
				}
			});
			if(Utils.isNullOrEmpty(map1)) {
				synchronousSink.error(new GeneralException("Warning Excel Sheet is Empty"));
			} else if(!Utils.isNullOrEmpty(applicationNoNotValid)) {
				synchronousSink.error(new GeneralException("This applicant’s programme admission process is not completed. Selection to the hostel can be done only once the admission process is completed" + applicationNoNotValid));
			} else if(!Utils.isNullOrEmpty(selectData)) {
				synchronousSink.error(new GeneralException("Warning Room Type is empty for these Application Number " + selectData));
			} else if(!Utils.isNullOrEmpty(notSelectData)) {
				synchronousSink.error(new GeneralException("Warning Remarks is Empty For these Application Number" + notSelectData));
			} else if(!Utils.isNullOrEmpty(emptyStatus)) {
				synchronousSink.error(new GeneralException("Warning Status is Empty For these Application Number " + emptyStatus));
			} else if(!Utils.isNullOrEmpty(roomTypeEmpty)) {
				synchronousSink.error(new GeneralException("This room type does not have the capacity to accommodate the selected no.of students " +roomTypeEmpty));
			} else {
				synchronousSink.next(mapList1);
			}
		}).map(data2 -> convertDtoToDbo2((Map<Integer, HostelApplicationDBO>) data2, map1, map3, userId))
				.flatMap( s -> { 
					selectionProcessTransaction.update(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoToDbo2(Map<Integer, HostelApplicationDBO> mapList1, Map<Integer, List<String>> map1, Map<String, Integer> map3, String userId) {
		List<Object> data = new ArrayList<Object>();
		List<HostelApplicationDBO> result = new ArrayList<HostelApplicationDBO>();
		List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		Tuple selected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_SELECTED_UPLOADED");
		Tuple notSelected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_NOT_SELECTED_UPLOADED");
		map1.forEach((k,v) -> { 
			HostelApplicationDBO hostelData = mapList1.get(Integer.parseInt(v.get(1)));
			if(!Utils.isNullOrEmpty(hostelData)) { 
				if(v.get(10).equalsIgnoreCase("Selected")) {
					hostelData.setHostelApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
					hostelData.getHostelApplicationCurrentProcessStatus().setId(Integer.parseInt(selected.get("erp_work_flow_process_id").toString()));
					hostelData.setAllottedHostelRoomTypeDBO(new HostelRoomTypeDBO());
					hostelData.getAllottedHostelRoomTypeDBO().setId(map3.get(v.get(11)));
				} else if(v.get(10).equalsIgnoreCase("Not Selected")) {
					hostelData.setHostelApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
					hostelData.getHostelApplicationCurrentProcessStatus().setId(Integer.parseInt(notSelected.get("erp_work_flow_process_id").toString()));
					hostelData.setRemarks(v.get(12));
				}
			}
			hostelData.setHostelApplicationStatusTime(LocalDateTime.now());
			hostelData.setModifiedUsersId(Integer.parseInt(userId));
			result.add(hostelData);
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.setEntryId(hostelData.getId());
			erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
			erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(hostelData.getHostelApplicationCurrentProcessStatus().getId());
			erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
			erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			statusLogList.add(erpWorkFlowProcessStatusLogDBO);
		});
		data.addAll(statusLogList);
	//	commonApiTransaction1.saveErpWorkFlowProcessStatusLogDBO1(statusLogList);
		data.addAll(result);
		return data;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> updateReviewed(Mono<HostelApplicationDTO> dto, String userId) {
		return dto.map(data -> convertDtoDbo4(data,userId))
				.flatMap( s -> { 
					selectionProcessTransaction.update(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoDbo4(HostelApplicationDTO dto, String userId) {
		List<Object> data = new ArrayList<Object>();
		List<ErpWorkFlowProcessStatusLogDBO> statusList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		HostelApplicationDBO dbo = null;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo = selectionProcessTransaction.getStudentReviewedData(Integer.parseInt(dto.getId()));
		}
		if(!Utils.isNullOrEmpty(dto.getStatus()) && dto.getStatus().equalsIgnoreCase("Reviewed")) {
			Tuple reviewed = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_REVIEWED");
			dbo.setHostelApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
			dbo.getHostelApplicationCurrentProcessStatus().setId(Integer.parseInt(reviewed.get("erp_work_flow_process_id").toString()));
			dbo.setHostelApplicationStatusTime(LocalDateTime.now());
		}
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.setEntryId(Integer.parseInt(dto.getId()));
		erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
		erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(dbo.getHostelApplicationCurrentProcessStatus().getId());
		erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
		erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
		statusList.add(erpWorkFlowProcessStatusLogDBO);
		data.addAll(statusList);
		data.add(dbo);
		return data;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> downloadExcelSheet(String yearId, String hostelId) {
		ApiResult apiResult =  new ApiResult();
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		List<HostelApplicationDBO> list = selectionProcessTransaction.getGridData(yearId, hostelId);
		String filePath = "D:\\HostelSelectionProcessDataFinalList.xlsx"; 
		int rowCount = 0;
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		XSSFSheet sheet = workbook.createSheet("HostelSelectionProcessSheet");
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);

		XSSFRow rowhead = sheet.createRow((short)rowCount++); 
		rowhead.createCell(0).setCellValue("Reg.No");
		sheet.autoSizeColumn(0);
		rowhead.getCell(0).setCellStyle(style);
		rowhead.createCell(1).setCellValue("Application No");
		sheet.autoSizeColumn(1);
		rowhead.getCell(1).setCellStyle(style);
		rowhead.createCell(2).setCellValue("Hostel Application No");
		sheet.autoSizeColumn(2);
		rowhead.getCell(2).setCellStyle(style);
		rowhead.createCell(3).setCellValue("Name");
		sheet.autoSizeColumn(3);
		rowhead.getCell(3).setCellStyle(style);
		rowhead.createCell(4).setCellValue("Programme");
		sheet.autoSizeColumn(4);
		rowhead.getCell(4).setCellStyle(style);
		rowhead.createCell(5).setCellValue("Resident Category");
		sheet.autoSizeColumn(5);
		rowhead.getCell(5).setCellStyle(style);
		rowhead.createCell(6).setCellValue("Hostel Name");
		sheet.autoSizeColumn(6);
		rowhead.getCell(6).setCellStyle(style);
		rowhead.createCell(7).setCellValue("Mode of Application");
		sheet.autoSizeColumn(7);
		rowhead.getCell(7).setCellStyle(style);
		rowhead.createCell(8).setCellValue("First Preference");
		sheet.autoSizeColumn(8);
		rowhead.getCell(8).setCellStyle(style);
		rowhead.createCell(9).setCellValue("Second Preference");
		sheet.autoSizeColumn(9);
		rowhead.getCell(9).setCellStyle(style);
		rowhead.createCell(10).setCellValue("Status");
		sheet.autoSizeColumn(10);
		rowhead.getCell(10).setCellStyle(style);
		rowhead.createCell(11).setCellValue("Selected Room Type"); 
		sheet.autoSizeColumn(11);
		rowhead.getCell(11).setCellStyle(style);
		rowhead.createCell(12).setCellValue("Remarks");
		sheet.autoSizeColumn(12);
		rowhead.getCell(12).setCellStyle(style);
		List<String> roomtype = new ArrayList<String>();
		List<HostelRoomTypeDBO> roomTypes = selectionProcessTransaction.getRoomTypeForStudent(hostelId);
		Map<Integer, String> map3 = roomTypes.stream().sorted(Comparator.comparingInt(s -> s.getTotalOccupants())).collect(Collectors.toMap(s -> s.getId(), s -> s.getRoomType()));
		map3.forEach((k,v) -> {
			roomtype.add(v);
		});
		String roomTypeList = roomtype.toString().replace("[", "").replace("]", "");
		for(HostelApplicationDBO a : list) {
			rowhead = sheet.createRow(rowCount);
			rowhead.createCell(0).setCellValue(Integer.parseInt(a.getStudentDBO().getRegisterNo()));
			sheet.autoSizeColumn(0);
			rowhead.createCell(1).setCellValue(a.getStudentApplnEntriesDBO().getApplicationNo());
			sheet.autoSizeColumn(1);
			rowhead.createCell(2).setCellValue(a.getApplicationNo());
			sheet.autoSizeColumn(2);
			rowhead.createCell(3).setCellValue(a.getStudentApplnEntriesDBO().getApplicantName());
			sheet.autoSizeColumn(3);
			rowhead.createCell(4).setCellValue(a.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
			sheet.autoSizeColumn(4);
			rowhead.createCell(5).setCellValue(a.getStudentApplnEntriesDBO().getErpResidentCategoryDBO().getResidentCategoryName());
			sheet.autoSizeColumn(5);
			rowhead.createCell(6).setCellValue(a.getHostelDBO().getHostelName());
			sheet.autoSizeColumn(6);
			if(a.getIsOffline().equals(true)) {
				rowhead.createCell(7).setCellValue("offline");	
			} else {
				rowhead.createCell(7).setCellValue("online");
			}
			for(HostelApplicationRoomTypePreferenceDBO dbos : a.getHostelApplicationRoomTypePreferenceDBO()) {
				if(dbos.getPreferenceOrder() == 1) {
					rowhead.createCell(8).setCellValue(dbos.getHostelRoomTypesDBO().getRoomType());
					sheet.autoSizeColumn(8);
				} 
				if(dbos.getPreferenceOrder() == 2) {
					rowhead.createCell(9).setCellValue(dbos.getHostelRoomTypesDBO().getRoomType()); 
					sheet.autoSizeColumn(9);
				}	 
			}
			DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
			CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, 10, 10);
			DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(new String[] {"Selected", "Not Selected"});
			DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
			dataValidation.setSuppressDropDownArrow(true);	
			sheet.addValidationData(dataValidation);
			CellRangeAddressList addressList1 = new CellRangeAddressList(1, rowCount, 11, 11);
			DataValidationConstraint constraint1 = validationHelper.createExplicitListConstraint(new String[] {roomTypeList.toString()});
			DataValidation dataValidation1 = validationHelper.createValidation(constraint1, addressList1);
			dataValidation1.setSuppressDropDownArrow(true);	
			sheet.addValidationData(dataValidation1);
			rowCount++;	
		}
		try {
			workbook.write(fileOut);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		apiResult.setSuccess(true);
		return Mono.just(apiResult);
	}

	public Mono<Boolean> checkIsStudent(String applnId ) {
		return Mono.just(applnId).handle((value, synchronousSink) -> {
			boolean result = selectionProcessTransaction.checkIsStudent(applnId);
			if(result == false) {
				synchronousSink.error(new DuplicateException("Selection to the hostel can be done only once the admission process is completed"));
			}
		});
	}

	public Flux<ErpProgrammeLevelDTO> getProgrammeLevelCount(String yearId, String hostelId) {
		List<Tuple> programmeLevelList = selectionProcessTransaction.getProgrammeLevel(yearId, hostelId);
		return this.convertDboToDto5(programmeLevelList,yearId, hostelId );
	}

	private Flux<ErpProgrammeLevelDTO> convertDboToDto5(List<Tuple> programmeLevelList, String yearId, String hostelId) {
		List<ErpProgrammeLevelDTO> dto = new ArrayList<ErpProgrammeLevelDTO>();
		Map<Integer, ErpProgrammeDTO> programMap = new HashMap<Integer, ErpProgrammeDTO>();
		programmeLevelList.forEach(data -> {
			ErpProgrammeLevelDTO programDTO = new ErpProgrammeLevelDTO();
			programDTO.setValue(data.get("student_count").toString());
			programDTO.setLabel(data.get("programme_level").toString());
			List<Tuple> programme = selectionProcessTransaction.getProgrammeCategory(yearId, hostelId,data.get("erp_programme_level_id").toString());
			List<ErpProgrammeDTO> programmeList = new ArrayList<ErpProgrammeDTO>();	
			programme.forEach(result -> {
				if(!programMap.containsKey(Integer.parseInt(result.get("erp_programme_id").toString()))) {
					ErpProgrammeDTO erpProgrammeDTO = new ErpProgrammeDTO();
					erpProgrammeDTO.setProgrammeName(result.get("programme_name").toString());
					List<ErpResidentCategoryDTO> residentList = new ArrayList<ErpResidentCategoryDTO>();
					ErpResidentCategoryDTO residentDTO = new ErpResidentCategoryDTO();	
					residentDTO.setResidentCategoryName(result.get("resident_category_name").toString());
					residentDTO.setValue(Integer.parseInt(result.get("student_count").toString()));
					residentList.add(residentDTO);	
					erpProgrammeDTO.setErpResidentCategoryDTO(residentList);
					programmeList.add(erpProgrammeDTO);
					programDTO.setChildren(programmeList);
					programMap.put(Integer.parseInt(result.get("erp_programme_id").toString()), erpProgrammeDTO);
				} else {
					ErpProgrammeDTO dtos = programMap.get(Integer.parseInt(result.get("erp_programme_id").toString()));
					programMap.replace(Integer.parseInt(result.get("erp_programme_id").toString()), dtos);
				}
			});
			dto.add(programDTO);	
		});
		return Flux.fromIterable(dto);
	}

}