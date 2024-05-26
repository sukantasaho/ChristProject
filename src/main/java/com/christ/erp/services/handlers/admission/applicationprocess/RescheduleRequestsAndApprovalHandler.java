package com.christ.erp.services.handlers.admission.applicationprocess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanCenterBasedDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailAllotmentDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnSelectionProcessRescheduleDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnSelectionProcessDatesDBO;
import com.christ.erp.services.dto.admission.admissionprocess.SelectionProcessRescheduleRequestDTO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.admission.applicationprocess.RescheduleRequestsAndApprovalTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RescheduleRequestsAndApprovalHandler {
	
	@Autowired
	private RescheduleRequestsAndApprovalTransaction rescheduleRequestsAndApprovalTransaction;
	
	public Flux<SelectDTO> getApplicantNoList(int applicationNo) {
		ErpAcademicYearDBO year = rescheduleRequestsAndApprovalTransaction.getCurrentYear();
		return rescheduleRequestsAndApprovalTransaction.getApplicantNoList(applicationNo,year.getId()).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}
	
	public Flux<SelectDTO> getApplicantNameList(String applicantName) {
		ErpAcademicYearDBO year = rescheduleRequestsAndApprovalTransaction.getCurrentYear();
		return rescheduleRequestsAndApprovalTransaction.getApplicantNameList(applicantName,year.getId()).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}
	
	public SelectDTO convertDBOToDTO(Tuple dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.get(0)));
			dto.setLabel(dbo.get(1).toString());
		}
		return dto;
	}

	public Mono<SelectionProcessRescheduleRequestDTO> getApplicantDetails(String studentApplnEntriesId) {
		StudentApplnEntriesDBO data = rescheduleRequestsAndApprovalTransaction.getApplicantDetails(studentApplnEntriesId);
		 List<StudentApplnSelectionProcessRescheduleDBO> rcount = rescheduleRequestsAndApprovalTransaction.getRescheduleCount(studentApplnEntriesId);
		return  this.convertDBOToDTO(data,rcount);
	}
	
	public Mono<SelectionProcessRescheduleRequestDTO> convertDBOToDTO(StudentApplnEntriesDBO dbo,List<StudentApplnSelectionProcessRescheduleDBO> rcount){
		SelectionProcessRescheduleRequestDTO dto = new SelectionProcessRescheduleRequestDTO();
		Map<String,StudentApplnSelectionProcessRescheduleDBO> map = new HashMap<String, StudentApplnSelectionProcessRescheduleDBO>();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setName(dbo.getApplicantName());
			dto.setStudentEnteriesId(dbo.getId());
			dto.setApplicationNo(dbo.getApplicationNo());
			dto.setProgrammeName(new SelectDTO());
			dto.getProgrammeName().setValue(String.valueOf(dbo.getErpCampusProgrammeMappingDBO().getId()));
			dto.getProgrammeName().setLabel(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
			dto.setCampusName(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
			dbo.getStudentApplnSelectionProcessDatesDBOS().forEach(selectionDbo -> {
				if(!Utils.isNullOrEmpty(selectionDbo)) {
					if(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getProcessOrder() == 1) {
						dto.setSelectionProcessPlanDetailsId(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getId());
						dto.setSelectionProcessType(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessTypeDBO().getId());
						dto.setSelectionProcessSessionName(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getSelectionProcessSession());
						if(!Utils.isNullOrEmpty(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO())) {
							dto.setSelectionProcessVenue(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
						} else {
							dto.setSelectionProcessVenue(selectionDbo.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
						}
						dto.setSelectionProcessDate(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate());
					}
					if(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getProcessOrder() == 2) {
						if(!Utils.isNullOrEmpty(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO())) {
							dto.setSelectionProcess2Venue(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
						} else {
							dto.setSelectionProcess2Venue(selectionDbo.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
						}
						dto.setSelectionProcess2Date(selectionDbo.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate());
					}
					rcount.forEach( data -> {
						//System.out.println(String.valueOf(data.getRequestReceivedDateTime())+data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId()+"hello");
						if(!map.containsKey(String.valueOf(data.getRequestReceivedDateTime())+data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId())) {
							map.put(String.valueOf(data.getRequestReceivedDateTime())+data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId(), data);
						}
					});
					dto.setRescheduleCount(map.size());
				}
			});
		}
		return Mono.just(dto);
	}

	public Mono<List<SelectionProcessRescheduleRequestDTO>> getRescheduleDetails(String studentApplnEntriesId) {
		List<StudentApplnSelectionProcessRescheduleDBO> data = rescheduleRequestsAndApprovalTransaction.getRescheduleCount(studentApplnEntriesId);
		return this.convertDboToDto12(data);
	}
	
	public Mono<List<SelectionProcessRescheduleRequestDTO>> convertDboToDto12(List<StudentApplnSelectionProcessRescheduleDBO> rcount){
		List<SelectionProcessRescheduleRequestDTO> dtos = new ArrayList<SelectionProcessRescheduleRequestDTO>();
		Map<String,StudentApplnSelectionProcessRescheduleDBO> map = new HashMap<String, StudentApplnSelectionProcessRescheduleDBO>();
		rcount.forEach( data -> {
//			System.out.println(String.valueOf(data.getRequestReceivedDateTime())+data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId()+"hello");
			if(!map.containsKey(String.valueOf(data.getRequestReceivedDateTime())+data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId())) {
				SelectionProcessRescheduleRequestDTO dto = new SelectionProcessRescheduleRequestDTO();
				dto.setRequestReceivedDateTime(data.getRequestReceivedDateTime());
				dto.setProcess1Date(data.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate());
				dto.setProgrammeName(new SelectDTO());
				dto.getProgrammeName().setLabel(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
				dto.setCampusName(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO()) 
						? data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName()
						: data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
				map.put(String.valueOf(data.getRequestReceivedDateTime())+data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId(), data);
				dtos.add(dto);
			}
		});
		return Mono.just(dtos);
		
	}

	public Mono<List<SelectionProcessRescheduleRequestDTO>> getSelectionProcessDates(String erpCampusProgrammeMappingId,String selectionProcessType) {
		 List<AdmSelectionProcessPlanDetailDBO> list = rescheduleRequestsAndApprovalTransaction.getSelectionProcessDates(erpCampusProgrammeMappingId,selectionProcessType);
		 return convertDBOToDTO1(list);
	}
	
	public Mono<List<SelectionProcessRescheduleRequestDTO>> convertDBOToDTO1(List<AdmSelectionProcessPlanDetailDBO> dbos){
		List<SelectionProcessRescheduleRequestDTO> dtos = new ArrayList<SelectionProcessRescheduleRequestDTO>();
		Map<LocalDate,SelectionProcessRescheduleRequestDTO> map = new HashMap<LocalDate, SelectionProcessRescheduleRequestDTO>();
		dbos.forEach( dbo -> {
			if(!map.containsKey(dbo.getSelectionProcessDate())) {
				SelectionProcessRescheduleRequestDTO dto = new SelectionProcessRescheduleRequestDTO();
				dto.setSelectionDate(new SelectDTO());
				dto.setProcess1Date(dbo.getSelectionProcessDate());
				dto.getSelectionDate().setValue(String.valueOf(dbo.getId()));
				dto.getSelectionDate().setLabel(dbo.getSelectionProcessDate().toString());
				dto.setShortList(dbo.getAdmSelectionProcessTypeDBO().getIsShortlistAfterThisStage());
				dto.setVenus(new ArrayList<AdmSelectionProcessVenueCityDTO>());
				if(dbo.getAdmSelectionProcessTypeDBO().getMode().trim().equalsIgnoreCase("Center Based Entrance")) {
					dbo.getAdmSelectionProcessPlanCenterBasedDBOs().forEach(data -> {
						if(!Utils.isNullOrEmpty(data)) {
							AdmSelectionProcessVenueCityDTO venue = new AdmSelectionProcessVenueCityDTO();
							venue.id = String.valueOf(data.getAdmSelectionProcessVenueCityDBO().getId());
							venue.tag = String.valueOf(dbo.getId());
							venue.setVenue(data.getAdmSelectionProcessVenueCityDBO().getVenueName());
							List<Tuple> list1 = rescheduleRequestsAndApprovalTransaction.getFilledCountCenterBased(dbo.getId(),data.getId());
							venue.setMaxSeats(String.valueOf(data.getVenueAvailableSeats() - list1.size()));
							dto.getVenus().add(venue);
					    }
				    });
			    } else {
			    	AdmSelectionProcessVenueCityDTO venue = new AdmSelectionProcessVenueCityDTO();
			    	venue.id = String.valueOf(dbo.getAdmSelectionProcessVenueCityDBO().getId());
			    	venue.tag = String.valueOf(dbo.getId());
			    	venue.setVenue(dbo.getAdmSelectionProcessVenueCityDBO().getVenueName());
			    	List<Tuple> list1 = rescheduleRequestsAndApprovalTransaction.getFilledCount(dbo.getId());
					int count = 0 ;
					for(AdmSelectionProcessPlanDetailAllotmentDBO allmentseat : dbo.getAdmSelectionProcessPlanDetailAllotmentDBOs()) {
						if(allmentseat.getAdmSelectionProcessPlanDetailDBO().getId() == dbo.getId()) {
							count = count + allmentseat.getSelectionProcessSeats();
						}
					}
			    	venue.setMaxSeats(String.valueOf(count - list1.size()));
					dto.getVenus().add(venue);
			    }
				map.put(dbo.getSelectionProcessDate(), dto);
			} else {
				SelectionProcessRescheduleRequestDTO dto = map.get(dbo.getSelectionProcessDate());
				if(dbo.getAdmSelectionProcessTypeDBO().getMode().trim().equalsIgnoreCase("CenterBasedEntrance")) {
					dbo.getAdmSelectionProcessPlanCenterBasedDBOs().forEach(data -> {
						if(!Utils.isNullOrEmpty(data)) {
							AdmSelectionProcessVenueCityDTO venue = new AdmSelectionProcessVenueCityDTO();
							venue.id = String.valueOf(data.getAdmSelectionProcessVenueCityDBO().getId());
							venue.tag = String.valueOf(dbo.getId());
							venue.setVenue(data.getAdmSelectionProcessVenueCityDBO().getVenueName());
							List<Tuple> list1 = rescheduleRequestsAndApprovalTransaction.getFilledCountCenterBased(dbo.getId(),data.getId());
							venue.setMaxSeats( String.valueOf(data.getVenueAvailableSeats() - list1.size()));
							dto.getVenus().add(venue);
					    }
				    });
			    } else {
			    	AdmSelectionProcessVenueCityDTO venue = new AdmSelectionProcessVenueCityDTO();
			    	venue.id = String.valueOf(dbo.getAdmSelectionProcessVenueCityDBO().getId());
			    	venue.tag = String.valueOf(dbo.getId());
			    	venue.setVenue(dbo.getAdmSelectionProcessVenueCityDBO().getVenueName());
			    	List<Tuple> list1 = rescheduleRequestsAndApprovalTransaction.getFilledCount(dbo.getId());
			    	int count = 0 ;
					for(AdmSelectionProcessPlanDetailAllotmentDBO allmentseat : dbo.getAdmSelectionProcessPlanDetailAllotmentDBOs()) {
						if(allmentseat.getAdmSelectionProcessPlanDetailDBO().getId() == dbo.getId()) {
							count = count + allmentseat.getSelectionProcessSeats();
						}
					}
			    	venue.setMaxSeats(String.valueOf(count - list1.size()));
					dto.getVenus().add(venue);
			    }
				map.replace(dbo.getSelectionProcessDate(), dto);
			}
		});
		map.forEach((k,v) -> {
			dtos.add(v);
		});
		dtos.sort(Comparator.comparing(SelectionProcessRescheduleRequestDTO::getProcess1Date));
		return Mono.just(dtos) ;
	}

	public Mono<List<SelectionProcessRescheduleRequestDTO>>  getSelectionProcessDatesBySelectionProcessPlanDetailsId(String selectionProcessPlanDetailsId, String selectedVenueId) {
		AdmSelectionProcessPlanDetailDBO data = rescheduleRequestsAndApprovalTransaction.getSelectionProcessPlanId(selectionProcessPlanDetailsId, selectedVenueId);
		List<AdmSelectionProcessPlanDetailDBO> list = rescheduleRequestsAndApprovalTransaction.getSelectionProcessDatesBySelectionProcessPlanDetailsId(data.getAdmSelectionProcessPlanDBO().getId());
		return convertDBOToDTO2(list);
	}
	
	public Mono<List<SelectionProcessRescheduleRequestDTO>> convertDBOToDTO2(List<AdmSelectionProcessPlanDetailDBO> dbos) {
		List<SelectionProcessRescheduleRequestDTO> dtos = new ArrayList<SelectionProcessRescheduleRequestDTO>();
		Map<LocalDate,SelectionProcessRescheduleRequestDTO> map2 = new HashMap<LocalDate, SelectionProcessRescheduleRequestDTO>();
		dbos.forEach(dbo -> {
			if(!map2.containsKey(dbo.getSelectionProcessDate())) {
				SelectionProcessRescheduleRequestDTO dto = new SelectionProcessRescheduleRequestDTO();
				dto.setProcess1Date(dbo.getSelectionProcessDate());
				if(dbo.getIsCandidateChooseSpDate()) {
					dto.setSelectionDate(new SelectDTO());
					dto.getSelectionDate().setValue(String.valueOf(dbo.getId()));
					dto.getSelectionDate().setLabel(dbo.getSelectionProcessDate().toString());
				}
				if(dbo.getIsCandidateChooseSpVenue()) {
					dto.setVenus(new ArrayList<AdmSelectionProcessVenueCityDTO>());
					if(dbo.getAdmSelectionProcessTypeDBO().getMode().trim().equals("Center Based Entrance")) {
						dbo.getAdmSelectionProcessPlanCenterBasedDBOs().forEach(data -> {
							if(!Utils.isNullOrEmpty(data)) {
								AdmSelectionProcessVenueCityDTO venue = new AdmSelectionProcessVenueCityDTO();
								venue.id = String.valueOf(data.getAdmSelectionProcessVenueCityDBO().getId());
								venue.tag = String.valueOf(dbo.getId());
								venue.setVenue(data.getAdmSelectionProcessVenueCityDBO().getVenueName());
								List<Tuple> list1 = rescheduleRequestsAndApprovalTransaction.getFilledCountCenterBased(dbo.getId(),data.getId());
								venue.setMaxSeats(String.valueOf(data.getVenueAvailableSeats() - list1.size()));
								dto.getVenus().add(venue);
						    }
					    });
					} else {
						AdmSelectionProcessVenueCityDTO venue = new AdmSelectionProcessVenueCityDTO();
				    	venue.id = String.valueOf(dbo.getAdmSelectionProcessVenueCityDBO().getId());
				    	venue.tag = String.valueOf(dbo.getId());
				    	venue.setVenue(dbo.getAdmSelectionProcessVenueCityDBO().getVenueName());
				    	List<Tuple> list1 = rescheduleRequestsAndApprovalTransaction.getFilledCount(dbo.getId());
						int count = 0 ;
						for(AdmSelectionProcessPlanDetailAllotmentDBO allmentseat : dbo.getAdmSelectionProcessPlanDetailAllotmentDBOs()) {
							if(allmentseat.getAdmSelectionProcessPlanDetailDBO().getId() == dbo.getId()) {
								count = count + allmentseat.getSelectionProcessSeats();
							}
						}
				    	venue.setMaxSeats(String.valueOf(count - list1.size()));
						dto.getVenus().add(venue);
					}
				}
				map2.put(dbo.getSelectionProcessDate(), dto);
			} else {
				SelectionProcessRescheduleRequestDTO dto = map2.get(dbo.getSelectionProcessDate());
				dto.setProcess1Date(dbo.getSelectionProcessDate());
				if(dbo.getIsCandidateChooseSpDate()) {
					dto.setSelectionDate(new SelectDTO());
					dto.getSelectionDate().setValue(String.valueOf(dbo.getId()));
					dto.getSelectionDate().setLabel(dbo.getSelectionProcessDate().toString());
				}
				if(dbo.getIsCandidateChooseSpVenue()) {
					if(dbo.getAdmSelectionProcessTypeDBO().getMode().trim().equalsIgnoreCase("CenterBasedEntrance")) {
						dbo.getAdmSelectionProcessPlanCenterBasedDBOs().forEach(data -> {
							if(!Utils.isNullOrEmpty(data)) {
								AdmSelectionProcessVenueCityDTO venue = new AdmSelectionProcessVenueCityDTO();
								venue.id = String.valueOf(data.getAdmSelectionProcessVenueCityDBO().getId());
								venue.tag = String.valueOf(dbo.getId());
								venue.setVenue(data.getAdmSelectionProcessVenueCityDBO().getVenueName());
								List<Tuple> list1 = rescheduleRequestsAndApprovalTransaction.getFilledCountCenterBased(dbo.getId(),data.getId());
								venue.setMaxSeats( String.valueOf(data.getVenueAvailableSeats() - list1.size()));
								dto.getVenus().add(venue);
							}
					    });
				    } else {
				    	AdmSelectionProcessVenueCityDTO venue = new AdmSelectionProcessVenueCityDTO();
				    	venue.id = String.valueOf(dbo.getAdmSelectionProcessVenueCityDBO().getId());
				    	venue.tag = String.valueOf(dbo.getId());
				    	venue.setVenue(dbo.getAdmSelectionProcessVenueCityDBO().getVenueName());
				    	List<Tuple> list1 = rescheduleRequestsAndApprovalTransaction.getFilledCount(dbo.getId());
				    	int count = 0 ;
						for(AdmSelectionProcessPlanDetailAllotmentDBO allmentseat : dbo.getAdmSelectionProcessPlanDetailAllotmentDBOs()) {
							if(allmentseat.getAdmSelectionProcessPlanDetailDBO().getId() == dbo.getId()) {
								count = count + allmentseat.getSelectionProcessSeats();
							}
						}
				    	venue.setMaxSeats(String.valueOf(count - list1.size()));
						dto.getVenus().add(venue);
						dto.getVenus().stream().sorted(Comparator.comparing(AdmSelectionProcessVenueCityDTO::getVenue));
				    }
				}
				map2.replace(dbo.getSelectionProcessDate(), dto);
			}
		});
		map2.forEach((k,v) -> {
			dtos.add(v);
		});
		dtos.sort(Comparator.comparing(SelectionProcessRescheduleRequestDTO::getProcess1Date));
		return Mono.just(dtos);
		
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult>  saveRescheduleData(Mono<SelectionProcessRescheduleRequestDTO> dto, String userId) {
		return dto.handle((selectionProcessRescheduleRequestDTOWebflux, synchronousSink) ->  {
			 List<Tuple> data = rescheduleRequestsAndApprovalTransaction.checkAdmitCard(selectionProcessRescheduleRequestDTOWebflux);
			 if(!Utils.isNullOrEmpty(data)) {
				 if(!selectionProcessRescheduleRequestDTOWebflux.isOkmsg()) {
						synchronousSink.error(new DuplicateException("Displayed selection process dates has admit card already generated.This will be removed on approval"));
				 } else {
						synchronousSink.next(selectionProcessRescheduleRequestDTOWebflux);
				 }
			 } else {
				synchronousSink.next(selectionProcessRescheduleRequestDTOWebflux);
			}
		}).cast(SelectionProcessRescheduleRequestDTO.class)
		  .map(data -> convertDtoToDbo(data, userId))
		  .flatMap( s -> {
	    	  if (!Utils.isNullOrEmpty(s)) {
	    		  rescheduleRequestsAndApprovalTransaction.save(s);
              }
		  return Mono.just(Boolean.TRUE);
	      }).map(Utils::responseResult);
	}
	
	public List<StudentApplnSelectionProcessRescheduleDBO> convertDtoToDbo(SelectionProcessRescheduleRequestDTO dto, String userId) {
		List<StudentApplnSelectionProcessRescheduleDBO> dboList = new ArrayList<StudentApplnSelectionProcessRescheduleDBO>();
		StudentApplnSelectionProcessRescheduleDBO dbo = new StudentApplnSelectionProcessRescheduleDBO();
		dbo.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
		dbo.getStudentApplnEntriesDBO().setId(dto.getStudentEnteriesId());
		dbo.setAdmSelectionProcessPlanDetailDBO(new AdmSelectionProcessPlanDetailDBO());
		dbo.getAdmSelectionProcessPlanDetailDBO().setId(Integer.parseInt(dto.getProcessSelection1Date().getValue()));
		AdmSelectionProcessPlanDetailDBO data = rescheduleRequestsAndApprovalTransaction.getAdmSelectionProcessCenterBasedId(dto.getProcessSelection1Date().getValue());
		if(Utils.isNullOrEmpty(data.getAdmSelectionProcessVenueCityDBO())) {
			dbo.setAdmSelectionProcessPlanCenterBasedDBO(new AdmSelectionProcessPlanCenterBasedDBO());
			data.getAdmSelectionProcessPlanCenterBasedDBOs().forEach( centerDbo ->{
				if(centerDbo.getAdmSelectionProcessVenueCityDBO().getId() == Integer.parseInt(dto.getProcess1Venue().id)) {
					dbo.getAdmSelectionProcessPlanCenterBasedDBO().setId(centerDbo.getId());
				}
			});
		}
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		dbo.setRecordStatus('A');
		dbo.setRequestReceivedDateTime(LocalDateTime.now());
		dboList.add(dbo);
		if(!Utils.isNullOrEmpty(dto.getProcessSelection2Date()) || !Utils.isNullOrEmpty(dto.getProcess2Venue())) {
			StudentApplnSelectionProcessRescheduleDBO dbo1 = new StudentApplnSelectionProcessRescheduleDBO();
			dbo1.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
			dbo1.getStudentApplnEntriesDBO().setId(dto.getStudentEnteriesId());
			dbo1.setAdmSelectionProcessPlanDetailDBO(new AdmSelectionProcessPlanDetailDBO());
			AdmSelectionProcessPlanDetailDBO data1 = null;
			if(!Utils.isNullOrEmpty(dto.getProcessSelection2Date())) {
				dbo1.getAdmSelectionProcessPlanDetailDBO().setId(Integer.parseInt(dto.getProcessSelection2Date().getValue()));
				data1 = rescheduleRequestsAndApprovalTransaction.getAdmSelectionProcessCenterBasedId(dto.getProcessSelection2Date().getValue());
			} else {
				dbo1.getAdmSelectionProcessPlanDetailDBO().setId(Integer.parseInt(dto.getProcess2Venue().tag));
				data1 = rescheduleRequestsAndApprovalTransaction.getAdmSelectionProcessCenterBasedId(dto.getProcess2Venue().tag);
			}
			if(!Utils.isNullOrEmpty(dto.getProcess2Venue())) {
				if(Utils.isNullOrEmpty(data1.getAdmSelectionProcessVenueCityDBO())) {
					dbo1.setAdmSelectionProcessPlanCenterBasedDBO(new AdmSelectionProcessPlanCenterBasedDBO());
					data1.getAdmSelectionProcessPlanCenterBasedDBOs().forEach( centerDbo1 ->{
						if(centerDbo1.getAdmSelectionProcessVenueCityDBO().getId() == Integer.parseInt(dto.getProcess2Venue().id)) {
							dbo1.getAdmSelectionProcessPlanCenterBasedDBO().setId(centerDbo1.getId());
						}
					});
				}
			}
			dbo1.setCreatedUsersId(Integer.parseInt(userId));
			dbo1.setRecordStatus('A');
			dbo1.setRequestReceivedDateTime(LocalDateTime.now());
			dboList.add(dbo1);
		}
		return dboList;
	}

	public Mono<List<SelectionProcessRescheduleRequestDTO>> approvalRescheduleDetails() {
		List<StudentApplnSelectionProcessRescheduleDBO> list = rescheduleRequestsAndApprovalTransaction.approvalRescheduleDetails();
		return this.convertDboToDto(list);
	}
	
	public Mono<List<SelectionProcessRescheduleRequestDTO>> convertDboToDto(List<StudentApplnSelectionProcessRescheduleDBO> dbo){
		List<SelectionProcessRescheduleRequestDTO> list = new ArrayList<SelectionProcessRescheduleRequestDTO>();
		Map<String,SelectionProcessRescheduleRequestDTO> map = new HashMap<String, SelectionProcessRescheduleRequestDTO>();
		if(!Utils.isNullOrEmpty(dbo)) {
			dbo.forEach(data -> {
				if(!map.containsKey(String.valueOf(data.getStudentApplnEntriesDBO().getId()) + data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId()+data.getRequestReceivedDateTime())) {
					SelectionProcessRescheduleRequestDTO dto = new SelectionProcessRescheduleRequestDTO();
					dto.setStudentEnteriesId(data.getStudentApplnEntriesDBO().getId());
					dto.setReschudeleId(data.getId());
					dto.setRequestReceivedDateTime(data.getRequestReceivedDateTime());
					dto.setApplicationNo(data.getStudentApplnEntriesDBO().getApplicationNo());
					dto.setName(data.getStudentApplnEntriesDBO().getApplicantName());
					dto.setProgrammeName(new SelectDTO());
					dto.getProgrammeName().setValue(String.valueOf(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getId()));
					dto.getProgrammeName().setLabel(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().programmeName);
					dto.setCampusName(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO()) 
							? data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName()
							: data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
					if(data.getAdmSelectionProcessPlanDetailDBO().getProcessOrder() == 1) {
						dto.setProcessSelection1Date(new SelectDTO());
						dto.setProcess1Date(data.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate());
						dto.getProcessSelection1Date().setValue(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId 
						dto.getProcessSelection1Date().setLabel(data.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().toString());
						if(!Utils.isNullOrEmpty(data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO())) {
							dto.setProcess1Venue(new AdmSelectionProcessVenueCityDTO());
							dto.getProcess1Venue().id=(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId()));
							dto.getProcess1Venue().setVenue(data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
						} else {
							dto.setProcess1Venue(new AdmSelectionProcessVenueCityDTO()); 
							dto.getProcess1Venue().id=(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
							dto.getProcess1Venue().setVenue(data.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
						}
					}
					if(data.getAdmSelectionProcessPlanDetailDBO().getProcessOrder() == 2) {
						dto.setProcessSelection2Date(new SelectDTO());
						dto.getProcessSelection2Date().setValue(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
						dto.getProcessSelection2Date().setLabel(data.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().toString());
						if(!Utils.isNullOrEmpty(data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO())) {
							dto.setProcess2Venue(new AdmSelectionProcessVenueCityDTO());
							dto.getProcess2Venue().id=(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
							dto.getProcess2Venue().setVenue(data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
						} else {
							dto.setProcess2Venue(new AdmSelectionProcessVenueCityDTO());
							dto.getProcess2Venue().id=(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
							dto.getProcess2Venue().setVenue(data.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
						}
					}
					map.put(String.valueOf(data.getStudentApplnEntriesDBO().getId()) + data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId()+data.getRequestReceivedDateTime(), dto);
				} else {
					SelectionProcessRescheduleRequestDTO dto = map.get(String.valueOf(data.getStudentApplnEntriesDBO().getId()) + data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId()+data.getRequestReceivedDateTime());
					if(Utils.isNullOrEmpty(dto.getProcessSelection1Date())) {
						if(data.getAdmSelectionProcessPlanDetailDBO().getProcessOrder() == 1) {
							dto.setProcessSelection1Date(new SelectDTO());
							dto.getProcessSelection1Date().setValue(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
							dto.getProcessSelection1Date().setLabel(data.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().toString());
							if(!Utils.isNullOrEmpty(data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO())) {
								dto.setProcess1Venue(new AdmSelectionProcessVenueCityDTO());
								dto.getProcess1Venue().id= (String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
								dto.getProcess1Venue().setVenue(data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
							} else {
								dto.setProcess1Venue(new AdmSelectionProcessVenueCityDTO());
								dto.getProcess1Venue().id=(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
								dto.getProcess1Venue().setVenue(data.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
							}
						}
					}
					if(Utils.isNullOrEmpty(dto.getProcessSelection2Date())) {
						if(data.getAdmSelectionProcessPlanDetailDBO().getProcessOrder() == 2) {
							dto.setProcessSelection2Date(new SelectDTO());
							dto.getProcessSelection2Date().setValue(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
							dto.getProcessSelection2Date().setLabel(data.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().toString());
							if(!Utils.isNullOrEmpty(data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO())) {
								dto.setProcess2Venue(new AdmSelectionProcessVenueCityDTO());
								dto.getProcess2Venue().id=(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
								dto.getProcess2Venue().setVenue(data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
							} else {
								dto.setProcess2Venue(new AdmSelectionProcessVenueCityDTO());
								dto.getProcess2Venue().id=(String.valueOf(data.getAdmSelectionProcessPlanDetailDBO().getId())); //its selectionProcessPlanDetailsId
								dto.getProcess2Venue().setVenue(data.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
							}
						}
					}
					map.replace(String.valueOf(data.getStudentApplnEntriesDBO().getId()) + data.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId()+data.getRequestReceivedDateTime(), dto);
				}
			});	
		}
		map.forEach((k,v) -> {
			list.add(v);
		});
		return Mono.just(list);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveApprovalDetails(Mono<List<SelectionProcessRescheduleRequestDTO>> dto, String userId) {
		return dto.map(data -> saveApprovalDetailsDtoToDbo(data,userId))
				.flatMap( s -> { rescheduleRequestsAndApprovalTransaction.save1(s);
	               return Mono.just(Boolean.TRUE);
                }).map(Utils::responseResult);
	}
	
	public List<StudentApplnEntriesDBO> saveApprovalDetailsDtoToDbo(List<SelectionProcessRescheduleRequestDTO> dto1, String userId) {
		List<StudentApplnEntriesDBO> value = new ArrayList<StudentApplnEntriesDBO>();
		List<Integer> studentIds = new ArrayList<Integer>();
		dto1.forEach( d -> {
			studentIds.add(d.getStudentEnteriesId());
		});
		List<StudentApplnEntriesDBO> dboDatas1 = rescheduleRequestsAndApprovalTransaction.getRescheduleData(studentIds);
		Map<String,StudentApplnEntriesDBO> dboDatas2 =new HashMap<String, StudentApplnEntriesDBO>();
		dboDatas1.forEach(dboDatas -> {
			dboDatas.getStudentApplnSelectionProcessRescheduleDBOs().forEach( abc -> {
				dboDatas2.put(String.valueOf(abc.getStudentApplnEntriesDBO().getId())+abc.getAdmSelectionProcessPlanDetailDBO().getId(),dboDatas );
			});
		});
		dto1.forEach(dto -> {
			if(dto.isApproved() || dto.isRejected()) {
				 String process1 = !Utils.isNullOrEmpty(dto.getProcess1Venue()) ? String.valueOf(dto.getStudentEnteriesId())+dto.getProcess1Venue().id : String.valueOf(dto.getStudentEnteriesId())+dto.getProcessSelection1Date().getValue();
			        if(dboDatas2.containsKey(process1)) {
						Set<StudentApplnSelectionProcessRescheduleDBO> rescheduleDbo = new HashSet<StudentApplnSelectionProcessRescheduleDBO>();
						Set<AdmSelectionProcessDBO> selectionProcessData = new HashSet<AdmSelectionProcessDBO>();
						Set<StudentApplnSelectionProcessDatesDBO> selectionProcessDatesDbo = new HashSet<StudentApplnSelectionProcessDatesDBO>();
						StudentApplnEntriesDBO oldData = dboDatas2.get(process1);
						oldData.getStudentApplnSelectionProcessRescheduleDBOs().forEach( oldDbo ->{
							if(oldDbo.getAdmSelectionProcessPlanDetailDBO().getId() == Integer.parseInt(dto.getProcess1Venue().id) ) {
								if(dto.getRequestReceivedDateTime().equals(oldDbo.getRequestReceivedDateTime())) {
									if(dto.isApproved()) {
										oldDbo.setIsRequestAuthorized(dto.isApproved());
										oldDbo.setModifiedUsersId(Integer.parseInt(userId));
										StudentApplnSelectionProcessDatesDBO dbo1 = new StudentApplnSelectionProcessDatesDBO();
										dbo1.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
										dbo1.getStudentApplnEntriesDBO().setId(oldDbo.getStudentApplnEntriesDBO().getId());
										dbo1.setAdmSelectionProcessPlanDetailDBO(new AdmSelectionProcessPlanDetailDBO());
										dbo1.getAdmSelectionProcessPlanDetailDBO().setId(oldDbo.getAdmSelectionProcessPlanDetailDBO().getId());
//										dbo1.setSelectionProcessDate(oldDbo.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().atTime(oldDbo.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime()));
										if(!Utils.isNullOrEmpty(oldDbo.getAdmSelectionProcessPlanCenterBasedDBO())) {
											dbo1.setAdmSelectionProcessPlanCenterBasedDBO(new AdmSelectionProcessPlanCenterBasedDBO());
											dbo1.getAdmSelectionProcessPlanCenterBasedDBO().setId(oldDbo.getAdmSelectionProcessPlanCenterBasedDBO().getId());
										}
										dbo1.setRecordStatus('A');
										dbo1.setCreatedUsersId(Integer.parseInt(userId));
										selectionProcessDatesDbo.add(dbo1);
									}
									if(dto.isRejected()) {
										oldDbo.setIsRequestRejected(dto.isRejected());
										oldDbo.setModifiedUsersId(Integer.parseInt(userId));
									}
							    }
							}
						    String process2 = null;
							if(!Utils.isNullOrEmpty(dto.getProcessSelection2Date()) || !Utils.isNullOrEmpty(dto.getProcess2Venue())) {
								process2 = !Utils.isNullOrEmpty(dto.getProcess2Venue()) ? String.valueOf(dto.getStudentEnteriesId())+dto.getProcess2Venue().id : String.valueOf(dto.getStudentEnteriesId())+dto.getProcessSelection2Date().getValue();
							}
						    if(dboDatas2.containsKey(process2)) {
						    	if(oldDbo.getAdmSelectionProcessPlanDetailDBO().getId() == Integer.parseInt(dto.getProcess2Venue().id)) {
						    		if(dto.getRequestReceivedDateTime().equals(oldDbo.getRequestReceivedDateTime())) {
							    		if(dto.isApproved()) {
							    			oldDbo.setIsRequestAuthorized(dto.isApproved());
											oldDbo.setModifiedUsersId(Integer.parseInt(userId));
											StudentApplnSelectionProcessDatesDBO dbo2 = new StudentApplnSelectionProcessDatesDBO();
											dbo2.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
											dbo2.getStudentApplnEntriesDBO().setId(oldDbo.getStudentApplnEntriesDBO().getId());
											dbo2.setAdmSelectionProcessPlanDetailDBO(new AdmSelectionProcessPlanDetailDBO());
											dbo2.getAdmSelectionProcessPlanDetailDBO().setId(oldDbo.getAdmSelectionProcessPlanDetailDBO().getId());
//											dbo2.setSelectionProcessDate(oldDbo.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().atTime(oldDbo.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime()));
											if(!Utils.isNullOrEmpty(oldDbo.getAdmSelectionProcessPlanCenterBasedDBO())) {
												dbo2.setAdmSelectionProcessPlanCenterBasedDBO(new AdmSelectionProcessPlanCenterBasedDBO());
												dbo2.getAdmSelectionProcessPlanCenterBasedDBO().setId(oldDbo.getAdmSelectionProcessPlanCenterBasedDBO().getId());
											}
											dbo2.setRecordStatus('A');
											dbo2.setCreatedUsersId(Integer.parseInt(userId));
											selectionProcessDatesDbo.add(dbo2);
										}
										if(dto.isRejected()) {
											oldDbo.setIsRequestRejected(dto.isRejected());
											oldDbo.setModifiedUsersId(Integer.parseInt(userId));
										}
										rescheduleDbo.add(oldDbo);
						    	    }
								}
							}
							rescheduleDbo.add(oldDbo);
						});	
						if(dto.isApproved()) {
							oldData.getAdmSelectionProcessDBOS().forEach(admitCard -> {
								 admitCard.setRecordStatus('D');
								 admitCard.setModifiedUsersId(Integer.parseInt(userId));
								 selectionProcessData.add(admitCard);
							});
							oldData.getStudentApplnSelectionProcessDatesDBOS().forEach(  data ->{
								data.setRecordStatus('D');
								data.setModifiedUsersId(Integer.parseInt(userId));
								selectionProcessDatesDbo.add(data);	
							});
						}
						oldData.setStudentApplnSelectionProcessRescheduleDBOs(rescheduleDbo);
						oldData.setAdmSelectionProcessDBOS(selectionProcessData);
						oldData.setStudentApplnSelectionProcessDatesDBOS(selectionProcessDatesDbo);
						value.add(oldData);
					}
			}
		});	
		return value;
	}
	
}
