package com.christ.erp.services.handlers.hostel.student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBedDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomsDBO;
import com.christ.erp.services.dto.admission.applicationprocess.ErpResidentCategoryDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBedDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockUnitDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFloorDTO;
import com.christ.erp.services.dto.hostel.settings.HostelRoomsDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.hostel.student.RoomAllocationTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoomAllocationHandler {

	@Autowired
	RoomAllocationTransaction roomAllocationTransaction;

	public Flux<SelectDTO> getRoomTypeForStudent(String hostelId) {
		return roomAllocationTransaction.getRoomTypeForStudent(hostelId).flatMapMany(Flux::fromIterable).map(this::convertHostelRoomTypeDboToDto);
	}

	public SelectDTO convertHostelRoomTypeDboToDto(HostelRoomTypeDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getRoomType());
		}
		return dto;
	}

	public Flux<SelectDTO> getRoomByUnitAndFloor(String unitId) {
		return roomAllocationTransaction.getRoomByUnitAndFloor(unitId).flatMapMany(Flux::fromIterable).map(this::convertHostelRoomDboToDto);
	}

	public SelectDTO convertHostelRoomDboToDto(HostelRoomsDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getRoomNo());
		}
		return dto;
	}

	public Flux<SelectDTO> getBedByRoomId(String roomId) {
		return roomAllocationTransaction.getBedByRoomId(roomId).flatMapMany(Flux::fromIterable).map(this::convertHostelBedDboToDto);
	}

	public SelectDTO convertHostelBedDboToDto(HostelBedDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getBedNo());
		}
		return dto;
	}

	public Flux<HostelAdmissionsDTO> getGridData(String academicYearId,String hostelId,String roomTypeId){
		List<HostelAdmissionsDBO> hostelAdmissionsDBOList = roomAllocationTransaction.getGridData(academicYearId,hostelId,roomTypeId);
		return this.convertDboToDto(hostelAdmissionsDBOList);
	}

	public Flux<HostelAdmissionsDTO> convertDboToDto(List<HostelAdmissionsDBO> hostelAdmissionsDBOList) {
		List<HostelAdmissionsDTO> hostelAdmissionsDTOList = new ArrayList<HostelAdmissionsDTO>();
		if(!Utils.isNullOrEmpty(hostelAdmissionsDBOList)) {
			hostelAdmissionsDBOList.forEach(data -> {
				HostelAdmissionsDTO hostelAdmissionsDTO = new HostelAdmissionsDTO();
				if(!Utils.isNullOrEmpty(data.getId())) {
					hostelAdmissionsDTO.setId(data.getId());
				}
				hostelAdmissionsDTO.setHostelApplicationDTO(new HostelApplicationDTO());
				if(!Utils.isNullOrEmpty(data.getHostelApplicationDBO().getApplicationNo())) {
					hostelAdmissionsDTO.getHostelApplicationDTO().setHostelApplicationNo(String.valueOf(data.getHostelApplicationDBO().getApplicationNo()));
				}
				if(!Utils.isNullOrEmpty(data.getHostelApplicationDBO().getAllottedHostelRoomTypeDBO())) {
					hostelAdmissionsDTO.getHostelApplicationDTO().setHostelRoomType(data.getHostelApplicationDBO().getAllottedHostelRoomTypeDBO().getRoomType());
				}
				hostelAdmissionsDTO.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
				if(!Utils.isNullOrEmpty(data.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicantName())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicantName(data.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicantName());
				}
				if(!Utils.isNullOrEmpty(data.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicationNumber(String.valueOf(data.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo()));
				}
				if(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setProgramName(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
				}
				if(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getErpResidentCategoryDBO().getResidentCategoryName())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setErpResidentCategory(new ErpResidentCategoryDTO());
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().getErpResidentCategory().setResidentCategoryName(data.getStudentApplnEntriesDBO().getErpResidentCategoryDBO().getResidentCategoryName());
				}	
				hostelAdmissionsDTO.setStudentDTO(new StudentDTO());
				if(!Utils.isNullOrEmpty(data.getStudentDBO().getRegisterNo())) {
					hostelAdmissionsDTO.getStudentDTO().setRegisterNo(data.getStudentDBO().getRegisterNo());
				}
				if(!Utils.isNullOrEmpty(data.getStudentDBO().getAcaClassDBO())) {
					hostelAdmissionsDTO.getStudentDTO().setAcaClassDTO(data.getStudentDBO().getAcaClassDBO().getClassName());
				}
				if(!Utils.isNullOrEmpty(data.getHostelBedDBO())){
					if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getId())) {
						hostelAdmissionsDTO.setBedNo(new SelectDTO());
						hostelAdmissionsDTO.getBedNo().setValue(String.valueOf(data.getHostelBedDBO().getId()));
						hostelAdmissionsDTO.getBedNo().setLabel(data.getHostelBedDBO().getBedNo());
					}
					if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO())) {
						if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getId())) {
							hostelAdmissionsDTO.setRoomNo(new SelectDTO());
							hostelAdmissionsDTO.getRoomNo().setValue(String.valueOf(data.getHostelBedDBO().getHostelRoomsDBO().getId()));
							hostelAdmissionsDTO.getRoomNo().setLabel(data.getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
						}
					}
					if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO())){
						if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getId())) {
							hostelAdmissionsDTO.setUnit(new SelectDTO());
							hostelAdmissionsDTO.getUnit().setValue(String.valueOf(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getId()));
							hostelAdmissionsDTO.getUnit().setLabel(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelUnit());
						}
					}
					if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO())){
						if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getId())) {
							hostelAdmissionsDTO.setBlock(new SelectDTO());
							hostelAdmissionsDTO.getBlock().setValue(String.valueOf(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getId()));
							hostelAdmissionsDTO.getBlock().setLabel(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getBlockName());
						}
					}
				}
				hostelAdmissionsDTOList.add(hostelAdmissionsDTO);
			});
		}
		return  Flux.fromIterable(hostelAdmissionsDTOList);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Mono<ApiResult> update(Mono<List<HostelAdmissionsDTO>> dto, String hostelId,String admissionYearId, String userId) {
		List<String> errorList = new ArrayList<String>();
		return dto
				.handle((hostelAdmissionsDTO,synchronousSink) -> {
					List<Integer> bedNoList = hostelAdmissionsDTO.stream().filter( p -> !Utils.isNullOrEmpty(p.getBedNo()) && !Utils.isNullOrEmpty(p.getBedNo().getValue()))
							.map(data -> Integer.parseInt(data.getBedNo().getValue())).collect(Collectors.toList());
					List<Integer> hostelAdmissionIds = hostelAdmissionsDTO.stream().map(data -> data.getId()).collect(Collectors.toList());
					Map<Integer,HostelBedDBO> existData = roomAllocationTransaction.getData(hostelAdmissionIds).stream().filter(d -> !Utils.isNullOrEmpty(d.getHostelBedDBO())).collect(Collectors.toMap(s -> s.getId(), s -> s.getHostelBedDBO()));
					if(!Utils.isNullOrEmpty(existData)) {
						hostelAdmissionsDTO.forEach((ele) -> {
							if(existData.containsKey(ele.getId())) {
								HostelBedDBO hostelBedDBO = existData.get(ele.getId());
								if(!Utils.isNullOrEmpty(hostelBedDBO)) {
									if(!Utils.isNullOrEmpty(ele.getBedNo())) {
										if(!Utils.isNullOrEmpty(ele.getBedNo().getLabel())) {
											if(hostelBedDBO.getBedNo().equals(ele.getBedNo().getLabel())) {
												bedNoList.remove(hostelBedDBO.getId());
											}
										}
									}
								}
							}
						});
					}
					List<HostelAdmissionsDBO> hostelAdmissionsDBOList  = roomAllocationTransaction.duplicateCheck(bedNoList, hostelId, admissionYearId);
					if(!Utils.isNullOrEmpty(hostelAdmissionsDBOList)) {
						hostelAdmissionsDBOList.forEach(dbo -> {
							errorList.add(dbo.getHostelBedDBO().getBedNo() + " already allotted to "
									+ dbo.getStudentApplnEntriesDBO().getApplicantName()+ " and " +
									dbo.getHostelApplicationDBO().getApplicationNo());
						});
					}
					if(!Utils.isNullOrEmpty(errorList)) {
						synchronousSink.error(new GeneralException(errorList.toString().replace("[", "").replace("]", "")));
					}else {
						synchronousSink.next(hostelAdmissionsDTO);
					}
				}).map(data -> convertDtoDbo((List<HostelAdmissionsDTO>) data,hostelId,admissionYearId,userId))
				.flatMap( s ->{ 
					roomAllocationTransaction.update(s);	
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);   
	}

	private List<HostelAdmissionsDBO> convertDtoDbo(List<HostelAdmissionsDTO> data,String hostelId,String admissionYearId, String userId) {
		List<Integer> hosetAdmissionIds = new ArrayList<Integer>();
		List<Integer> bedList = new ArrayList<Integer>();
		data.forEach(dto -> {
			hosetAdmissionIds.add(dto.getId());
			if(!Utils.isNullOrEmpty(dto.getBedNo())) {
				if(!Utils.isNullOrEmpty(dto.getBedNo().getValue())) {
					bedList.add(Integer.parseInt(dto.getBedNo().getValue()));
				}
			}
		});
		Map<Integer,HostelBedDBO> hostelBedDboMap  =  !Utils.isNullOrEmpty(bedList) ? roomAllocationTransaction.getHostelBed(bedList).stream().collect(Collectors.toMap(s->s.getId(), s->s)) : null; 
		List<HostelAdmissionsDBO> hostelAdmissionsDBOList = roomAllocationTransaction.getData(hosetAdmissionIds);
		Map<Integer,HostelAdmissionsDBO> hostelAdmissionsDBOMap = new HashMap<Integer, HostelAdmissionsDBO>();
		hostelAdmissionsDBOList.forEach(exist -> {
			hostelAdmissionsDBOMap.put(exist.getId(), exist);
		});
		List<HostelBedDBO> hostelBedDBOList = new ArrayList<HostelBedDBO>();
		List<HostelAdmissionsDBO> hostelAdmissionsDBOList1 = new ArrayList<HostelAdmissionsDBO>();
		data.forEach((hostelAdmissionDTO) -> {
			if(hostelAdmissionsDBOMap.containsKey(hostelAdmissionDTO.getId())) {
				HostelAdmissionsDBO hostelAdmissionDBO = hostelAdmissionsDBOMap.get(hostelAdmissionDTO.getId());
				if(!Utils.isNullOrEmpty(hostelAdmissionDTO.getBedNo())) {
					if(!Utils.isNullOrEmpty(hostelAdmissionDTO.getBedNo().getValue())) {
						if(!Utils.isNullOrEmpty(hostelAdmissionDBO)) {
							if(!Utils.isNullOrEmpty(hostelAdmissionDBO.getHostelBedDBO())) {
								if(hostelAdmissionDBO.getHostelBedDBO().getId() != Integer.parseInt(hostelAdmissionDTO.getBedNo().getValue())) {
									HostelBedDBO hostelBedDBO = hostelAdmissionDBO.getHostelBedDBO();
									hostelBedDBO.setOccupied(false);
									hostelBedDBOList.add(hostelBedDBO);
								}
							}
							if(!Utils.isNullOrEmpty(hostelBedDboMap)) {
								if(hostelBedDboMap.containsKey(Integer.parseInt(hostelAdmissionDTO.getBedNo().getValue()))) {
									HostelBedDBO hostelBedDBO = hostelBedDboMap.get(Integer.parseInt(hostelAdmissionDTO.getBedNo().getValue()));
									hostelBedDBO.setOccupied(true);
									hostelBedDBO.setModifiedUsersId(Integer.parseInt(userId));
									hostelAdmissionDBO.setHostelBedDBO(hostelBedDBO);
									hostelBedDboMap.remove(Integer.parseInt(hostelAdmissionDTO.getBedNo().getValue()));
								}
							}
							hostelAdmissionDBO.setModifiedUsersId(Integer.parseInt(userId));
						}	
					}
				}
			}
		});
		if(!Utils.isNullOrEmpty(hostelBedDBOList)) {
			roomAllocationTransaction.update1(hostelBedDBOList);
		}
		if(!Utils.isNullOrEmpty(hostelAdmissionsDBOList1)) {
			hostelAdmissionsDBOList.addAll(hostelAdmissionsDBOList1);
		}
		return hostelAdmissionsDBOList;		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Mono<ApiResult> roomAllocationUpload(String admissionYearId,Mono<EmpApplnAdvertisementImagesDTO> data, String userId) {
		Map<Integer,List<String>> map1 = new HashMap<Integer, List<String>>();
		Map<Integer,Integer> map = new HashMap<Integer, Integer>();
		Map<String,String> map2 = new HashMap<String, String>();
		Map<Integer,Integer> bedNoList = new HashMap<Integer, Integer>();
		List<String> errorList = new ArrayList<String>();
		List<Integer> applicationNos  = new ArrayList<Integer>();
		List<String> blockName =  new ArrayList<String>();
		List<String> unitName = new ArrayList<String>();
		List<String> roomNo = new ArrayList<String>();
		Map<Integer,Integer> roomApplicationNo = new HashMap<Integer, Integer>();
		List<Integer> bedDupli = new ArrayList<Integer>();
		List<String> bedDupli1 = new ArrayList<String>();
		return data
				.handle((data1,synchronousSink) -> {
					String hostelName = null;
					XSSFWorkbook workbook = null;
					try {
						workbook = new XSSFWorkbook("ExcelUpload//"+data1.fileName+"."+data1.extension);
					}catch (Exception e) {
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
									if(cn == 12) {
										Cell cell = row.getCell(cn);
										if(cell != null && cell.getCellType() == CellType.NUMERIC) {
											if(!map.containsKey((int)cell.getNumericCellValue())){
												map.put((int)cell.getNumericCellValue(), (int)cell.getNumericCellValue());
											}else {
												bedDupli.add((int)cell.getNumericCellValue());
											}
										}else if(cell != null && cell.getCellType() == CellType.STRING) {
											if(!map2.containsKey(cell.getStringCellValue())) {
												map2.put((cell.getStringCellValue()), (cell.getStringCellValue()));
											}else {
												bedDupli1.add(cell.getStringCellValue());
											}
										}
									}
									if(cn == 2) {
										Cell cell = row.getCell(cn);
										if(cell != null) {
											if(!applicationNos.contains((int)cell.getNumericCellValue())) {
												applicationNos.add((int)cell.getNumericCellValue());
											}
										}
									}
									if(cn == 6) {
										Cell cell = row.getCell(cn);
										if(cell != null) {
											hostelName = cell.getStringCellValue();
										}
									}
									if(cn == 9) {
										Cell cell = row.getCell(cn);
										if(cell != null) {
											if(!blockName.contains(cell.getStringCellValue())) {
												blockName.add(cell.getStringCellValue());
											}
										}
									}
									if(cn == 10) {
										Cell cell = row.getCell(cn);
										if(cell != null) {
											if(!unitName.contains(cell.getStringCellValue())) {
												unitName.add(cell.getStringCellValue());
											}
										}
									}
									if(cn == 11) {
										Cell cell = row.getCell(cn);
										if(cell != null && cell.getCellType() == CellType.NUMERIC) {
											Integer value = (int)cell.getNumericCellValue();
											if(!roomNo.contains(value.toString())) {
												roomNo.add(value.toString());
											}
										}else if(cell != null && cell.getCellType() == CellType.STRING) {
											if(!roomNo.contains(cell.getStringCellValue())) {
												roomNo.add(cell.getStringCellValue());
											}
										}
									}
									Cell cell = row.getCell(cn,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
									if(cell == null) {
										String cellValue = null;
										map1.get(p).add(cellValue);
									}else if (cell.getCellType() == CellType.NUMERIC) {
										map1.get(p).add(String.valueOf((int)cell.getNumericCellValue()));
									}else {
										String cellValue = cell.toString();
										map1.get(p).add(cellValue);
									}
								}	
							}
							p++;
						}
					}
					Map<String,Integer> blockListMap = !Utils.isNullOrEmpty(blockName) ? roomAllocationTransaction.getblockList(blockName).stream().collect(Collectors.toMap( s -> s.getBlockName(),s -> s.getId())) : null;
					Map<Integer,List<HostelBlockUnitDBO>> hostelBlockUnitListMap = !Utils.isNullOrEmpty(blockName) ? roomAllocationTransaction.getBlockAndUnit(blockName).stream().collect(Collectors.groupingBy(s->s.getHostelBlockDBO().getId(),Collectors.toList())) : null;			
					Map<String, Integer> unitListMap = !Utils.isNullOrEmpty(unitName) ? roomAllocationTransaction.getUnitList(unitName).stream().collect(Collectors.toMap(s -> s.getHostelUnit(),s -> s.getId())) : null;
					List<HostelRoomsDBO> hostelRoomDBOList1 = !Utils.isNullOrEmpty(unitName) ? roomAllocationTransaction.getUnitRoom(unitName): null;
					Map<Integer,List<HostelRoomsDBO>> hostelUnitRoomListMap = !Utils.isNullOrEmpty(hostelRoomDBOList1) ? hostelRoomDBOList1.stream().collect(Collectors.groupingBy(s -> s.getHostelFloorDBO().getHostelBlockUnitDBO().getId(),Collectors.toList())): null;
					List<Integer> unitIds = new ArrayList<Integer>(); 
					if(!Utils.isNullOrEmpty(unitListMap)) {
						unitListMap.forEach((k,v) -> {
							unitIds.add(v);
						});
					}
					List<HostelRoomsDBO> hostelRoomDBOList = !Utils.isNullOrEmpty(roomNo) && !Utils.isNullOrEmpty(unitIds)? roomAllocationTransaction.getRoomList(roomNo,unitIds): null;
					Map<String,Integer> roomListMap = !Utils.isNullOrEmpty(hostelRoomDBOList)? hostelRoomDBOList.stream().collect(Collectors.toMap(s -> s.getRoomNo(),s -> s.getId())) : null;
					List<Integer> roomIds = new ArrayList<Integer>();
					Map<String,String> roomTypeMap = new HashMap<String,String>();
					if(!Utils.isNullOrEmpty(roomListMap)) {
						roomListMap.forEach((k,v) -> {
							roomIds.add(v);
						});
					}
					hostelRoomDBOList1.forEach((ele) -> {
						roomTypeMap.put(ele.getRoomNo(), ele.getHostelRoomTypeDBO().getRoomType());
					});
					List<Object> obj = new ArrayList<Object>();
					if(!Utils.isNullOrEmpty(bedDupli)) {
						obj.add(bedDupli);
					}
					if(!Utils.isNullOrEmpty(bedDupli1)) {
						obj.add(bedDupli1);
					}
					Map<Integer,List<HostelBedDBO>> hostelRoomBedListMap = !Utils.isNullOrEmpty(roomIds) && !Utils.isNullOrEmpty(unitIds) ? roomAllocationTransaction.getRoomBedList(roomIds,unitIds).stream().collect(Collectors.groupingBy(s->s.getHostelRoomsDBO().getId(),Collectors.toList())) : null;
					Map<String,Integer> bedListMap = new HashMap<String,Integer>();
					List<Integer> blockNoEmpty = new ArrayList<Integer>();
					List<Integer> unitNoEmpty = new ArrayList<Integer>();
					List<Integer> roomNoEmpty = new ArrayList<Integer>();
					List<String> bedNoEmpty = new ArrayList<String>();
					List<String> unitBlockList = new ArrayList<String>();
					List<String> unitWrongNo = new ArrayList<String>();
					List<String> roomUnitList = new ArrayList<String>();
					List<String> roomWrongNo = new ArrayList<String>();
					List<String> bedRoomList = new ArrayList<String>();
					List<String> bedWrongNo = new ArrayList<String>();
					List<String> roomTypeWrong = new ArrayList<String>();
					map1.forEach((k,v) -> {
						if(Utils.isNullOrEmpty(v.get(9))) {
							if(!blockNoEmpty.contains(Integer.parseInt(v.get(2))))
								blockNoEmpty.add(Integer.parseInt(v.get(2)));
						}else if(!Utils.isNullOrEmpty(v.get(9))) {
							if(!Utils.isNullOrEmpty(blockListMap)) {
								if(blockListMap.containsKey(v.get(9))) {
									Integer blockId = blockListMap.get(v.get(9));
									if(!Utils.isNullOrEmpty(hostelBlockUnitListMap)) {
										if(hostelBlockUnitListMap.containsKey(blockId)) {
											List<HostelBlockUnitDBO> list = hostelBlockUnitListMap.get(blockId);
											if(!Utils.isNullOrEmpty(list)) {
												list.forEach(ele -> {
													unitBlockList.add(ele.getHostelUnit());
												});
												if(!Utils.isNullOrEmpty(v.get(10))) {
													if(!unitBlockList.contains(v.get(10))) {
														unitWrongNo.add("wrong unit for "+ v.get(9)+" for Application Number "+v.get(2));
													}
													unitBlockList.clear();
												}
											}
										}
									}
								}	
							}
						}
						if(Utils.isNullOrEmpty(v.get(10))) {
							if(!unitNoEmpty.contains(Integer.parseInt(v.get(2))))
								unitNoEmpty.add(Integer.parseInt(v.get(2)));
						}else if(!Utils.isNullOrEmpty(v.get(10))) {
							if(!Utils.isNullOrEmpty(unitListMap)) {
								if(unitListMap.containsKey(v.get(10))) {
									Integer unitId = unitListMap.get(v.get(10));
									if(!Utils.isNullOrEmpty(hostelUnitRoomListMap)) {
										if(hostelUnitRoomListMap.containsKey(unitId)) {
											List<HostelRoomsDBO> list = hostelUnitRoomListMap.get(unitId);
											if(!Utils.isNullOrEmpty(list)) {
												list.forEach(ele -> {
													roomUnitList.add(ele.getRoomNo());
												});
												if(!Utils.isNullOrEmpty(v.get(11))) {
													if(!roomUnitList.contains(v.get(11))) {
														roomWrongNo.add("wrong room for "+ v.get(10)+" for Application Number "+v.get(2));
													}
													roomUnitList.clear();
													if(!Utils.isNullOrEmpty(roomTypeMap)) {
														if(roomTypeMap.containsKey(v.get(11))) {
															String roomTypeString = roomTypeMap.get(v.get(11));
															if(!roomTypeString.equalsIgnoreCase(v.get(7))) {
																roomTypeWrong.add("wrong roomType for Application Number "+v.get(2) + " the roomNo type is a "+roomTypeString);
															}
														}
													}
												}
											}
										}
									}
								}	
							}
						}
						if(Utils.isNullOrEmpty(v.get(11))) {
							roomNoEmpty.add(Integer.parseInt(v.get(2)));
						}else if(!Utils.isNullOrEmpty(v.get(11))) {
							if(!Utils.isNullOrEmpty(roomListMap)) {
								if(roomListMap.containsKey(v.get(11))) {
									Integer roomId = roomListMap.get(v.get(11));
									if(!Utils.isNullOrEmpty(hostelRoomBedListMap)) {
										if(hostelRoomBedListMap.containsKey(roomId)) {
											List<HostelBedDBO> list = hostelRoomBedListMap.get(roomId);
											roomApplicationNo.put(Integer.parseInt(v.get(2)), roomId);
											if(!Utils.isNullOrEmpty(list)) {
												list.forEach(ele -> {
													bedRoomList.add(ele.getBedNo());
													bedListMap.put(ele.getBedNo(),ele.getId());
												});
												if(!Utils.isNullOrEmpty(v.get(12))) {
													if(!bedListMap.containsKey(v.get(12))) {
														bedWrongNo.add("wrong bed "+ v.get(12)+" for Application Number "+v.get(2));
													}else if(bedListMap.containsKey(v.get(12))) {
														Integer bedId = bedListMap.get(v.get(12));
														bedNoList.put(Integer.parseInt(v.get(2)),bedId);
													}
													bedRoomList.clear();
												}
											}
										}
									}
								}	
							}
						}
						if(Utils.isNullOrEmpty(v.get(12))) {
							bedNoEmpty.add(v.get(2));
						}
					});
					Integer hostelId  = null;
					if(!Utils.isNullOrEmpty(hostelName)){	
						hostelId = roomAllocationTransaction.getHostelId(hostelName);
					}
					List<HostelAdmissionsDBO> hostelAdmissionDBOList1 = !Utils.isNullOrEmpty(applicationNos) && !Utils.isNullOrEmpty(admissionYearId) && !Utils.isNullOrEmpty(hostelId) ? roomAllocationTransaction.getUploadData(applicationNos,admissionYearId,hostelId.toString()) : null;
					Map<Integer, HostelAdmissionsDBO> mapList = !Utils.isNullOrEmpty(hostelAdmissionDBOList1) ? hostelAdmissionDBOList1.stream().collect(Collectors.toMap(s->s.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo(),s->s)): null;
					Map<Integer,Integer> existhostelBedDBOListMap = !Utils.isNullOrEmpty(hostelAdmissionDBOList1) ? hostelAdmissionDBOList1.stream().filter(p -> !Utils.isNullOrEmpty(p.getHostelBedDBO())).collect(Collectors.toMap(s ->  s.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo(),s -> s.getHostelBedDBO().getId())) : null;
					List<Integer> bed = new ArrayList<Integer>();
					if(!Utils.isNullOrEmpty(existhostelBedDBOListMap)) {
						if(!Utils.isNullOrEmpty(bedNoList)) {
							bedNoList.forEach((k,v) -> {
								if(!Utils.isNullOrEmpty(k)) {
									if(existhostelBedDBOListMap.containsKey(k)) {
										Integer bedId = existhostelBedDBOListMap.get(k);
										if(!bedId.equals(v)) {
											bed.add(v);
										}
									}
								}
							});
						}
					}
					List<HostelAdmissionsDBO> hostelAdmissionDBOList = !Utils.isNullOrEmpty(bed) ? roomAllocationTransaction.duplicateCheck(bed, hostelId.toString(),admissionYearId ) : null;
					if(!Utils.isNullOrEmpty(hostelAdmissionDBOList)) {
						hostelAdmissionDBOList.forEach(dbo -> {
							errorList.add(dbo.getHostelBedDBO().getBedNo() + " already allotted to "
									+ dbo.getStudentApplnEntriesDBO().getApplicantName()+ " and " +
									dbo.getHostelApplicationDBO().getApplicationNo());
						});
					}
					if(Utils.isNullOrEmpty(map1)) {
						synchronousSink.error(new GeneralException("Excel sheet is empty"));
					}else if(Utils.isNullOrEmpty(mapList)) {
						synchronousSink.error(new GeneralException("Selected year datas not matching with uploaded data"));
					}else if(!Utils.isNullOrEmpty(unitWrongNo)) {
						synchronousSink.error(new GeneralException(unitWrongNo.stream().collect(Collectors.joining(System.getProperty("line.separator")))));
					}else if(!Utils.isNullOrEmpty(roomWrongNo)) {
						synchronousSink.error(new GeneralException(roomWrongNo.toString().replace("[","").replace("]", "")));
					}else if(!Utils.isNullOrEmpty(roomTypeWrong)) {
						synchronousSink.error(new GeneralException(roomTypeWrong.toString().replace("[","").replace("]", "")));
					}else if(!Utils.isNullOrEmpty(obj)) {
						synchronousSink.error(new GeneralException("Duplicate values for bed"));
					}else if(!Utils.isNullOrEmpty(bedWrongNo)) {
						synchronousSink.error(new GeneralException(bedWrongNo.toString().replace("[","").replace("]", "")));
					}else if(!Utils.isNullOrEmpty(blockNoEmpty)) {
						synchronousSink.error(new GeneralException("Block is empty for application numbers "+ blockNoEmpty));
					}else if(!Utils.isNullOrEmpty(unitNoEmpty)) {
						synchronousSink.error(new GeneralException("Unit is empty for application numbers "+ unitNoEmpty));
					}else if(!Utils.isNullOrEmpty(roomNoEmpty)) {
						synchronousSink.error(new GeneralException("Room is empty for application numbers " + roomNoEmpty));
					}else if(!Utils.isNullOrEmpty(bedNoEmpty)) {
						synchronousSink.error(new GeneralException("Bed is Empty for application numbers " + bedNoEmpty));
					}else if(!Utils.isNullOrEmpty(errorList)) {
						synchronousSink.error(new GeneralException(errorList.toString().replace("[", "").replace("]", "")));
					}else {
						synchronousSink.next(mapList);
					}
				}).map(data2 -> convertDtoMapToDbo((Map<Integer,  HostelAdmissionsDBO>) data2,roomApplicationNo,map1,userId))
				.flatMap( s ->{ 
					roomAllocationTransaction.update(s);	
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<HostelAdmissionsDBO> convertDtoMapToDbo(Map<Integer,  HostelAdmissionsDBO> mapList,Map<Integer,Integer> roomApplicationNo,Map<Integer, List<String>> map1, String userId) {
		List<HostelAdmissionsDBO> hostelAdmissionsDBOList = new ArrayList<HostelAdmissionsDBO>();
		List<String> bedList = new ArrayList<String>();
		List<Integer> roomId = new ArrayList<Integer>();
		map1.forEach((k,v) -> {
			if(!Utils.isNullOrEmpty(v.get(12))) {
				bedList.add(v.get(12));	
			}
		});
		if(!Utils.isNullOrEmpty(roomApplicationNo)) {
			roomApplicationNo.forEach((k,v)-> {
				roomId.add(v);
			});
		}
		List<HostelBedDBO> hostelBedDBOList = !Utils.isNullOrEmpty(bedList) && !Utils.isNullOrEmpty(roomId) ? roomAllocationTransaction.getBedIds(bedList,roomId) : null;
		Map<String,Integer> bedNoMap = !Utils.isNullOrEmpty(hostelBedDBOList) ?  hostelBedDBOList.stream().collect(Collectors.toMap(s -> s.getBedNo(),s -> s.getId())) : null;
		Map<Integer,HostelBedDBO> hostelBedDBOMap = !Utils.isNullOrEmpty(hostelBedDBOList) ? hostelBedDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : null;
		List<HostelBedDBO> hostelBedDBOList1 = new ArrayList<HostelBedDBO>();
		map1.forEach((k,v) -> {
			if(!Utils.isNullOrEmpty(bedNoMap)) {
				if(bedNoMap.containsKey(v.get(12))) {
					Integer bedId = bedNoMap.get(v.get(12));
					Integer applicationNumber = Integer.parseInt(v.get(2));
					if(mapList.containsKey(applicationNumber)) {
						HostelAdmissionsDBO hostelAdmissionsDBO = mapList.get(applicationNumber);
						if(!Utils.isNullOrEmpty(hostelAdmissionsDBO)) {
							if(!Utils.isNullOrEmpty(hostelAdmissionsDBO.getHostelBedDBO())) {
								if(!Utils.isNullOrEmpty(bedId)) {
									if(!bedId.equals(hostelAdmissionsDBO.getHostelBedDBO().getId())) {
										HostelBedDBO hostelBedDBO = hostelAdmissionsDBO.getHostelBedDBO();
										hostelBedDBO.setOccupied(false);
										hostelBedDBOList1.add(hostelBedDBO);
									}
								}
							}
							if(!Utils.isNullOrEmpty(hostelBedDBOMap)) {
								if(!Utils.isNullOrEmpty(bedId)) {
									if(hostelBedDBOMap.containsKey(bedId)) {
										HostelBedDBO hostelBedDBO = hostelBedDBOMap.get(bedId);
										hostelBedDBO.setOccupied(true);
										hostelBedDBO.setModifiedUsersId(Integer.parseInt(userId));
										hostelAdmissionsDBO.setHostelBedDBO(hostelBedDBO);
										hostelAdmissionsDBO.setModifiedUsersId(Integer.parseInt(userId));
									}
								}
							}
							hostelAdmissionsDBOList.add(hostelAdmissionsDBO);
						}
					}
				}
			}
		});
		if(!Utils.isNullOrEmpty(hostelBedDBOList1)) {
			roomAllocationTransaction.update1(hostelBedDBOList1);
		}
		return hostelAdmissionsDBOList;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> download(String admissionYearId, String hostelId) {
		ApiResult apiResult =  new ApiResult();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Hostel Data");
		int rowCount = 0;
		FileOutputStream fileOutputStream = null;
		File fileName = new File("D://RoomAllocation.xlsx");
		try {
			fileOutputStream = new FileOutputStream(fileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		XSSFRow rowhead = sheet.createRow((short)rowCount++); 
		rowhead.createCell(0).setCellValue("Sl.No");
		rowhead.createCell(1).setCellValue("Register No");
		rowhead.createCell(2).setCellValue("Application No");
		rowhead.createCell(3).setCellValue("Name");
		rowhead.createCell(4).setCellValue("Programme");
		rowhead.createCell(5).setCellValue("Resident Category"); 
		rowhead.createCell(6).setCellValue("Hostel Name"); 
		rowhead.createCell(7).setCellValue("Room Type"); 
		rowhead.createCell(8).setCellValue("Application Through"); 
		rowhead.createCell(9).setCellValue("Block"); 
		rowhead.createCell(10).setCellValue("Unit"); 
		rowhead.createCell(11).setCellValue("Room No"); 
		rowhead.createCell(12).setCellValue("Bed"); 
		List<HostelAdmissionsDBO> hostelAdmissionsDBOList = roomAllocationTransaction.getGridData(admissionYearId,hostelId,null);
		List<HostelBlockDBO> hostelBlock = roomAllocationTransaction.getBlockByHostelId(hostelId);
		List<String> hostelBlockString = new ArrayList<String>();
		List<Integer> hostelBlockIds = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(hostelBlock)) {
			hostelBlock.forEach(data -> {
				if(!hostelBlockString.contains(data.getBlockName())) {
					hostelBlockString.add(data.getBlockName());
				}
				hostelBlockIds.add(data.getId());
			});
		}
		List<String> hostelBlockUnitString = new ArrayList<String>();
		List<Integer> hostelUnitIds = new ArrayList<Integer>();
		List<HostelBlockUnitDBO> hostelBlockUnitList = roomAllocationTransaction.getUnitByBlock(hostelBlockIds);
		if(!Utils.isNullOrEmpty(hostelBlockUnitList)) {
			hostelBlockUnitList.forEach(data -> {
				if(!hostelBlockUnitString.contains(data.getHostelUnit())) {
					hostelBlockUnitString.add(data.getHostelUnit());
				}
				hostelUnitIds.add(data.getId());
			});
		}
		List<String> hostelUnitRoomString = new ArrayList<String>();
		List<Integer> hostelRoomIds = new ArrayList<Integer>();
		List<HostelRoomsDBO> hostelRoomDBOList = roomAllocationTransaction.getRoomListByUnit(hostelUnitIds);
		if(!Utils.isNullOrEmpty(hostelRoomDBOList)) {
			hostelRoomDBOList.forEach(data -> {
				if(!hostelUnitRoomString.contains(data.getRoomNo())) {
					hostelUnitRoomString.add(data.getRoomNo());
				}
				hostelRoomIds.add(data.getId());
			});
		}
		List<String> hostelRoomBedString = new ArrayList<String>();
		List<Integer> hostelBedIds = new ArrayList<Integer>();
		List<HostelBedDBO> hostelBedDBOList = roomAllocationTransaction.getBedListByRoom(hostelRoomIds);
		if(!Utils.isNullOrEmpty(hostelBedDBOList)) {
			hostelBedDBOList.forEach(data -> {
				if(!hostelRoomBedString.contains(data.getBedNo())) {
					hostelRoomBedString.add(data.getBedNo());	
				}
				hostelBedIds.add(data.getId());
			});
		}
		for(HostelAdmissionsDBO dbo : hostelAdmissionsDBOList) {
			rowhead = sheet.createRow(rowCount);
			rowhead.createCell(0).setCellValue(rowCount);
			sheet.autoSizeColumn(0);
			rowhead.createCell(1).setCellValue(dbo.getStudentDBO().getRegisterNo());
			sheet.autoSizeColumn(1);
			rowhead.createCell(2).setCellValue(dbo.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo());
			sheet.autoSizeColumn(2);
			rowhead.createCell(3).setCellValue(dbo.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicantName());
			sheet.autoSizeColumn(3);
			rowhead.createCell(4).setCellValue(dbo.getHostelApplicationDBO().getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
			sheet.autoSizeColumn(4);
			rowhead.createCell(5).setCellValue(dbo.getHostelApplicationDBO().getStudentApplnEntriesDBO().getErpResidentCategoryDBO().getResidentCategoryName()); 
			sheet.autoSizeColumn(5);
			rowhead.createCell(6).setCellValue(dbo.getHostelDBO().getHostelName()); 
			sheet.autoSizeColumn(6);
			rowhead.createCell(7).setCellValue(dbo.getHostelApplicationDBO().getAllottedHostelRoomTypeDBO().getRoomType()); 
			sheet.autoSizeColumn(7);
			if(dbo.getHostelApplicationDBO().getIsOffline()) {
				rowhead.createCell(8).setCellValue("Offline"); 
				sheet.autoSizeColumn(8);	
			}else {
				rowhead.createCell(8).setCellValue("Online"); 
				sheet.autoSizeColumn(8);	
			}
			if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO())) {
				rowhead.createCell(12).setCellValue(dbo.getHostelBedDBO().getBedNo()); 
				sheet.autoSizeColumn(12);
				if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO())) {
					rowhead.createCell(11).setCellValue(dbo.getHostelBedDBO().getHostelRoomsDBO().getRoomNo()); 
					sheet.autoSizeColumn(11);
					if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO())) {
						rowhead.createCell(10).setCellValue(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelUnit()); 
						sheet.autoSizeColumn(10);
						if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO())) {
							rowhead.createCell(9).setCellValue(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getBlockName()); 
							sheet.autoSizeColumn(9);
						}
					}
				}
			}
			DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
			if(hostelBlockString.size() > 1) {
				CellRangeAddressList blockAddressList = new CellRangeAddressList(1, rowCount,9, 9);//new CellRangeAddressList(0, 9,9, 9);
				DataValidationConstraint blockConstraint = validationHelper.createExplicitListConstraint(new String[] {hostelBlockString.toString().replace("[", "").replace("]", "")});
				DataValidation blockDataValidation = validationHelper.createValidation(blockConstraint, blockAddressList);
				blockDataValidation.setSuppressDropDownArrow(true);
				sheet.addValidationData(blockDataValidation);	
			}else if(hostelBlockString.size() == 1) {
				rowhead.createCell(9).setCellValue(hostelBlockString.toString().replace("[", "").replace("]", "")); 
				sheet.autoSizeColumn(9);
			}
			if(hostelBlockUnitString.size()>1) {
				CellRangeAddressList unitAddressList = new CellRangeAddressList(1, rowCount,10, 10);
				DataValidationConstraint unitConstraint = validationHelper.createExplicitListConstraint(new String[] {hostelBlockUnitString.toString().replace("[", "").replace("]", "")});
				DataValidation unitDataValidation = validationHelper.createValidation(unitConstraint, unitAddressList);
				unitDataValidation.setSuppressDropDownArrow(true);
				sheet.addValidationData(unitDataValidation);	
			}else if(hostelBlockUnitString.size()==1) {
				rowhead.createCell(10).setCellValue(hostelBlockUnitString.toString().replace("[", "").replace("]", "")); 
				sheet.autoSizeColumn(10);
			}
			if(hostelUnitRoomString.size()>1) {
				CellRangeAddressList roomAddressList = new CellRangeAddressList(1,rowCount,11,11);
				DataValidationConstraint roomConstraint = validationHelper.createExplicitListConstraint(new String[] {hostelUnitRoomString.toString().replace("[", "").replace("]", "")});
				DataValidation roomDataValidation = validationHelper.createValidation(roomConstraint, roomAddressList);
				roomDataValidation.setSuppressDropDownArrow(true);
				sheet.addValidationData(roomDataValidation);
			}else if(hostelUnitRoomString.size() == 1) {
				rowhead.createCell(11).setCellValue(hostelUnitRoomString.toString().replace("[", "").replace("]", "")); 
				sheet.autoSizeColumn(11);
			}
			if(hostelRoomBedString.size()>1) {
				CellRangeAddressList bedAddressList = new CellRangeAddressList(1,rowCount,12,12);
				DataValidationConstraint bedConstraint = validationHelper.createExplicitListConstraint(new String[] {hostelRoomBedString.toString().replace("[", "").replace("]", "")});
				DataValidation bedDataValidation = validationHelper.createValidation(bedConstraint, bedAddressList);
				bedDataValidation.setSuppressDropDownArrow(true);
				sheet.addValidationData(bedDataValidation);
			}else if(hostelRoomBedString.size() == 1) {
				rowhead.createCell(12).setCellValue(hostelRoomBedString.toString().replace("[", "").replace("]", "")); 
				sheet.autoSizeColumn(12);	
			}
			rowCount++;
		}
		try {
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		apiResult.setSuccess(true);
		//String filePath = this.excelBrowserDownload(fileName);
		//apiResult.setTag(filePath);
		return Mono.just(apiResult);
	}

	//	private String excelBrowserDownload(File fileName) {
	////		System.out.println(fileName+"fileName");
	////		System.out.println(fileName.getAbsolutePath()+" fileName loc");
	//		String filePath = fileName.getAbsolutePath();
	//		return filePath;
	//	}

	public Flux<SelectDTO> getRoomByUnitAndFloor1(String unitId,String roomType) {
		return roomAllocationTransaction.getRoomByUnitAndFloor1(unitId,roomType).flatMapMany(Flux::fromIterable).map(this::convertHostelRoomDboToDto1);
	}

	public SelectDTO convertHostelRoomDboToDto1(HostelRoomsDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getRoomNo());
		}
		return dto;
	}

	public Flux<HostelDTO> gethostelDetails(String hostelId) {
		List<Tuple> list = roomAllocationTransaction.gethostelDetails(hostelId);
		return this.convertHostelRoomDboToDto2(list); 
	}

	private Flux<HostelDTO> convertHostelRoomDboToDto2(List<Tuple> list) {
		Map<String, Map<String, Map<String, Map<String, List<HostelBedDTO>>>>> map = new LinkedHashMap<String, Map<String, Map<String, Map<String, List<HostelBedDTO>>>>>();
		Map<String, String> blockmap = new HashMap<String, String>();
		Map<String, String> unitmap = new HashMap<String, String>();
		Map<String, String> floormap = new HashMap<String, String>();
		Map<String, String> roommap = new HashMap<String, String>();
		Map<String,String> roomType = new HashMap<String, String>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(dbos -> {
				if(map.containsKey(dbos.get("hostel_block_id").toString())) {
					Map<String, Map<String, Map<String, List<HostelBedDTO>>>> unitMap = map.get(dbos.get("hostel_block_id").toString());
					blockmap.put(dbos.get("hostel_block_id").toString(), dbos.get("hostel_block_name").toString());
					if(Utils.isNullOrEmpty(unitMap)) {
						unitMap = new HashMap<String, Map<String, Map<String, List<HostelBedDTO>>>>();
					}
					if(unitMap.containsKey(dbos.get("hostel_block_unit_id").toString())) {
						Map<String, Map<String, List<HostelBedDTO>>> floorMap = unitMap.get(dbos.get("hostel_block_unit_id").toString());
						unitmap.put(dbos.get("hostel_block_unit_id").toString(), dbos.get("hostel_unit").toString());
						if(Utils.isNullOrEmpty(floorMap)) {
							floorMap = new HashMap<String, Map<String, List<HostelBedDTO>>>();
						}
						if(floorMap.containsKey(dbos.get("hostel_floor_id").toString())) {
							Map<String, List<HostelBedDTO>> roomMap = floorMap.get(dbos.get("hostel_floor_id").toString());
							floormap.put(dbos.get("hostel_floor_id").toString(), dbos.get("floor_no").toString());
							if(Utils.isNullOrEmpty(roomMap)) {
								roomMap = new HashMap<String, List<HostelBedDTO>>();
							}	
							if(roomMap.containsKey(dbos.get("hostel_rooms_id").toString())) {
								List<HostelBedDTO> bedDTOS = roomMap.get(dbos.get("hostel_rooms_id").toString());
								bedDTOS.add(setRoom(dbos));
								roomMap.put(dbos.get("hostel_rooms_id").toString(), bedDTOS);
								roommap.put(dbos.get("hostel_rooms_id").toString(), dbos.get("room_no").toString());						    
								roomType.put(dbos.get("hostel_rooms_id").toString(), dbos.get("room_type").toString());
							} else {
								List<HostelBedDTO> bedDTOS = new ArrayList<HostelBedDTO>();
								bedDTOS.add(setRoom(dbos));
								roomMap.put(dbos.get("hostel_rooms_id").toString(), bedDTOS);
								roommap.put(dbos.get("hostel_rooms_id").toString(), dbos.get("room_no").toString());	
								roomType.put(dbos.get("hostel_rooms_id").toString(), dbos.get("room_type").toString());
							}
							floorMap.put(dbos.get("hostel_floor_id").toString(), roomMap);
						} else {
							Map<String, List<HostelBedDTO>> roomMap = new LinkedHashMap<String, List<HostelBedDTO>>();
							List<HostelBedDTO> bedDTOS = new ArrayList<HostelBedDTO>();
							bedDTOS.add(setRoom(dbos));
							roomMap.put(dbos.get("hostel_rooms_id").toString(), bedDTOS);
							roommap.put(dbos.get("hostel_rooms_id").toString(), dbos.get("room_no").toString());						    
							floorMap.put(dbos.get("hostel_floor_id").toString(), roomMap);
							floormap.put(dbos.get("hostel_floor_id").toString(), dbos.get("floor_no").toString());
							roomType.put(dbos.get("hostel_rooms_id").toString(), dbos.get("room_type").toString());

						}
						unitMap.put(dbos.get("hostel_block_unit_id").toString(), floorMap);
					} else {
						Map<String, List<HostelBedDTO>> roomMap = new LinkedHashMap<String, List<HostelBedDTO>>();
						List<HostelBedDTO> bedDTOS = new ArrayList<HostelBedDTO>();
						bedDTOS.add(setRoom(dbos));
						roomMap.put(dbos.get("hostel_rooms_id").toString(), bedDTOS);
						Map<String, Map<String, List<HostelBedDTO>>> floorMap = new LinkedHashMap<String, Map<String, List<HostelBedDTO>>>();
						floorMap.put(dbos.get("hostel_floor_id").toString(), roomMap);
						unitMap.put(dbos.get("hostel_block_unit_id").toString(), floorMap);
						unitmap.put(dbos.get("hostel_block_unit_id").toString(), dbos.get("hostel_unit").toString());
					}
				} else {
					Map<String, List<HostelBedDTO>> roomMap = new LinkedHashMap<String, List<HostelBedDTO>>();
					List<HostelBedDTO> bedDTOS = new ArrayList<HostelBedDTO>();
					bedDTOS.add(setRoom(dbos));
					roomMap.put(dbos.get("hostel_rooms_id").toString(), bedDTOS);
					roommap.put(dbos.get("hostel_rooms_id").toString(), dbos.get("room_no").toString());
					roomType.put(dbos.get("hostel_rooms_id").toString(), dbos.get("room_type").toString());
					Map<String, Map<String, List<HostelBedDTO>>> floorMap = new LinkedHashMap<String, Map<String, List<HostelBedDTO>>>();
					floorMap.put(dbos.get("hostel_floor_id").toString(), roomMap);
					floormap.put(dbos.get("hostel_floor_id").toString(), dbos.get("floor_no").toString());
					Map<String, Map<String, Map<String, List<HostelBedDTO>>>> unitMap = new LinkedHashMap<String, Map<String, Map<String, List<HostelBedDTO>>>>();
					unitMap.put(dbos.get("hostel_block_unit_id").toString(), floorMap);
					unitmap.put(dbos.get("hostel_block_unit_id").toString(), dbos.get("hostel_unit").toString());
					map.put(dbos.get("hostel_block_id").toString(), unitMap);
					blockmap.put(dbos.get("hostel_block_id").toString(), dbos.get("hostel_block_name").toString());
				}
			});
		}
		List<HostelDTO> blockDtos = new ArrayList<HostelDTO>();
		map.forEach((k, v )-> {
			HostelDTO dto = new HostelDTO();
			dto.setHostelBlockDTO(new HostelBlockDTO());
			dto.getHostelBlockDTO().setId(Integer.parseInt(k));
			dto.getHostelBlockDTO().setBlockName(blockmap.get(k).toString());
			List<HostelBlockUnitDTO> unitDtos = new ArrayList<HostelBlockUnitDTO>();
			v.forEach((y, z)-> {
				HostelBlockUnitDTO udto = new HostelBlockUnitDTO();
				udto.setId(y);
				udto.setHostelUnit(unitmap.get(y));
				List<HostelFloorDTO> floorDtos = new ArrayList<HostelFloorDTO>();
				z.forEach((a, b)-> {
					HostelFloorDTO fDto = new HostelFloorDTO();  
					fDto.setId(a);
					fDto.setFloorNumber(floormap.get(a));
					List<HostelRoomsDTO> roomDTO = new ArrayList<HostelRoomsDTO>();
					b.forEach((m, n)-> {
						roomType.forEach((v1,k1) -> {
							if(v1.equals(m)) {
								HostelRoomsDTO rDto = new HostelRoomsDTO();
								rDto.setId(m);
								rDto.setHostelRoomTypeDTO(new SelectDTO());
								rDto.getHostelRoomTypeDTO().setLabel(roomType.get(v1));
								rDto.getHostelRoomTypeDTO().setValue(k1);
								rDto.setRoomNumber(roommap.get(m));
								rDto.setBedDetails(n);
								roomDTO.add(rDto);
							}
						});
					});
					roomDTO.sort(Comparator.comparing(HostelRoomsDTO::getId));
					fDto.setRoomDetails(roomDTO);
					floorDtos.add(fDto);
				});
				floorDtos.sort(Comparator.comparing(HostelFloorDTO::getFloorNumber));
				udto.setHostelFloorDTOSet(floorDtos);
				unitDtos.add(udto);
			});
			unitDtos.sort(Comparator.comparing(HostelBlockUnitDTO::getHostelUnit));
			dto.getHostelBlockDTO().setHostelBlockUnitDTOSet(unitDtos);
			blockDtos.add(dto);
		});
		return Flux.fromIterable(blockDtos);
	}

	private HostelBedDTO setRoom(Tuple dbos) {
		HostelBedDTO bDto = new HostelBedDTO();
		bDto.setId(dbos.get("hostel_bed_id").toString());
		bDto.setBedName(dbos.get("bed_no").toString());
		return bDto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		HostelAdmissionsDBO hostelAdmissionsDBO = roomAllocationTransaction.edit(id);
		if(!Utils.isNullOrEmpty(hostelAdmissionsDBO)) {
			hostelAdmissionsDBO.getHostelBedDBO().setOccupied(false);
			hostelAdmissionsDBO.setModifiedUsersId(Integer.parseInt(userId));
		}
		roomAllocationTransaction.updateHostelAdmissionsDBO(hostelAdmissionsDBO);	
	return Mono.just(Boolean.TRUE).map(Utils::responseResult);
	}

}