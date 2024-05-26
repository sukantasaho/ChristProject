package com.christ.erp.services.helpers.admission.applicationprocess;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Tuple;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanCenterBasedDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailAllotmentDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailProgDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanProgrammeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessVenueCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dto.admission.applicationprocess.*;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.admission.applicationprocess.SelectionProcessPlanTransaction;

public class SelectionProcessPlanHelper {
private static volatile SelectionProcessPlanHelper  selectionProcessPlanHelper=null;

	public static SelectionProcessPlanHelper getInstance() {
        if(selectionProcessPlanHelper==null) {
        	selectionProcessPlanHelper = new SelectionProcessPlanHelper();
        }
        return selectionProcessPlanHelper;
    }
	SelectionProcessPlanTransaction selectionProcessPlanTransaction = SelectionProcessPlanTransaction.getInstance();
	public AdmSelectionProcessPlanProgrammeDBO convertChildDTOtoDBO(SelectDTO programmeDto, AdmSelectionProcessPlanProgrammeDBO childDbo, AdmSelectionProcessPlanDBO dbo) {
//		if(!Utils.isNullOrEmpty(programme.campusMappingId)) {
//			ErpCampusProgrammeMappingDBO campusProgrammeMappingDBO = new ErpCampusProgrammeMappingDBO();
//			campusProgrammeMappingDBO.id = Integer.parseInt(programme.campusMappingId);
//			childDbo.erpCampusProgrammeMappingDBO = campusProgrammeMappingDBO;
//		}
//		if(!Utils.isNullOrEmpty(programme.acaBatchId)) {
//			AcaBatchDBO acaBatchDBO = new AcaBatchDBO();
//			acaBatchDBO.setId(Integer.parseInt(programme.acaBatchId));
//			childDbo.setAcaBatchDBO(acaBatchDBO);
//		}
		childDbo.setAdmSelectionProcessPlanDBO(dbo);
		if(!Utils.isNullOrEmpty(programmeDto)){
			childDbo.setAdmProgrammeBatchDBO(new AdmProgrammeBatchDBO());
			childDbo.getAdmProgrammeBatchDBO().setId(Integer.parseInt(programmeDto.getValue()));
		}
		childDbo.recordStatus = 'A';
		childDbo.admSelectionProcessPlanDBO = dbo;
		return childDbo;
	}
	
	public void convertDetailDBOsToDTOs(AdmSelectionProcessPlanDBO dbo, AdmSelectionProcessPlanDTO dto) throws Exception {
		AdmSelectionProcessPlanDetailDTO detailDTO = new AdmSelectionProcessPlanDetailDTO();
		if(!Utils.isNullOrEmpty(dbo.admSelectionProcessPlanDetailDBO) && dbo.admSelectionProcessPlanDetailDBO.size()>0) {
			detailDTO.slotslist = new ArrayList<AdmSelectionProcessPlanDetailAddSlotDTO>();
			detailDTO.venueslist = new ArrayList<AdmSelectionProcessPlanDetailAddVenueDTO>();
			List<Integer> detailsIds = new ArrayList<Integer>();
			for (AdmSelectionProcessPlanDetailDBO detailsDBO : dbo.admSelectionProcessPlanDetailDBO) {	
				if(!Utils.isNullOrEmpty(detailsDBO.id) && !Utils.isNullOrEmpty(detailsDBO.recordStatus) && detailsDBO.recordStatus =='A') {
					detailsIds.add(detailsDBO.id);
					if(Utils.isNullOrEmpty(detailsDBO.admSelectionProcessVenueCityDBO) && !Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO)
							&& !Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO) && !Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO.mode)
							&& detailsDBO.admSelectionProcessTypeDBO.mode.equals("Center Based Entrance")){
						
						AdmSelectionProcessPlanDetailAddSlotDTO addSlotDTO = new AdmSelectionProcessPlanDetailAddSlotDTO();
						addSlotDTO = convertAddSlotDboToDto(detailsDBO, addSlotDTO, dbo);
						detailDTO.slotslist.add(addSlotDTO);
						Collections.sort(detailDTO.slotslist);
					}else if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessVenueCityDBO)){
						AdmSelectionProcessPlanDetailAddVenueDTO addVenueDTO = new AdmSelectionProcessPlanDetailAddVenueDTO();
						addVenueDTO = convertAddVenueDboToDto(detailsDBO, addVenueDTO, dbo);	
						detailDTO.venueslist.add(addVenueDTO);
						Collections.sort(detailDTO.venueslist);
					}
				}
			}
			if(!Utils.isNullOrEmpty(detailsIds)) {
				List<Tuple> details = selectionProcessPlanTransaction.getStudentApplnSPDatesBasedSPDetails(detailsIds);
				if(!Utils.isNullOrEmpty(details) && !Utils.isNullOrEmpty(detailDTO)) {
					details.forEach(item->{
						detailDTO.slotslist.forEach(itemSlot->{
							if(!Utils.isNullOrEmpty(item.get("detailId")) && !Utils.isNullOrEmpty(itemSlot.id) &&
									!Utils.isNullOrEmpty(item.get("totalStudentAvailble")) && Integer.parseInt(item.get("detailId").toString()) == Integer.parseInt(itemSlot.id)) {
								itemSlot.totalStudentFilled = item.get("totalStudentAvailble").toString();
							}
						});
						detailDTO.venueslist.forEach(itemVenue->{
							if(!Utils.isNullOrEmpty(item.get("detailId")) && !Utils.isNullOrEmpty(itemVenue.id) &&
									!Utils.isNullOrEmpty(item.get("totalStudentAvailble")) && Integer.parseInt(item.get("detailId").toString()) == Integer.parseInt(itemVenue.id)) {
								itemVenue.totalStudentFilled = item.get("totalStudentAvailble").toString();
							}
						});
					});
				}
			}
			dto.details = detailDTO;
		}
	}

	public AdmSelectionProcessPlanDetailAddSlotDTO convertAddSlotDboToDto(AdmSelectionProcessPlanDetailDBO detailsDBO,AdmSelectionProcessPlanDetailAddSlotDTO addSlotDTO, AdmSelectionProcessPlanDBO dbo) {
		addSlotDTO.parentId = String.valueOf(dbo.id);
		if(!Utils.isNullOrEmpty(detailsDBO.id))
			addSlotDTO.id = String.valueOf(detailsDBO.id);
		if(!Utils.isNullOrEmpty(detailsDBO.processOrder))
			addSlotDTO.processOrder = String.valueOf(detailsDBO.processOrder);
		if(!Utils.isNullOrEmpty(detailsDBO.selectionProcessDate))
			addSlotDTO.selectionprocessdate = detailsDBO.selectionProcessDate;
		if(!Utils.isNullOrEmpty(detailsDBO.slot))
			addSlotDTO.slot = String.valueOf(detailsDBO.slot);
		if(!Utils.isNullOrEmpty(detailsDBO.selectionProcessTime))
			addSlotDTO.time = String.valueOf(detailsDBO.selectionProcessTime);
		if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO)) {
			addSlotDTO.mode = !Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO.mode)?String.valueOf(detailsDBO.admSelectionProcessTypeDBO.mode):"";
			ExModelBaseDTO spBaseDTO = new ExModelBaseDTO();
			spBaseDTO.id = String.valueOf(detailsDBO.admSelectionProcessTypeDBO.id);
			spBaseDTO.text = !Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO.selectionStageName)?detailsDBO.admSelectionProcessTypeDBO.selectionStageName:"";
			addSlotDTO.selectionProcessName = spBaseDTO;
			if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO.isShortlistAfterThisStage)) {
				addSlotDTO.isShortListFlag = detailsDBO.admSelectionProcessTypeDBO.isShortlistAfterThisStage;
			}
		}
		if(!Utils.isNullOrEmpty(detailsDBO.processOrder)) {
			addSlotDTO.processOrder = String.valueOf(detailsDBO.processOrder);
		}
		if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessPlanDetailProgDBOs) && detailsDBO.admSelectionProcessPlanDetailProgDBOs.size()>0) {
			addSlotDTO.detailProg = new ArrayList<AdmSelectionProcessPlanDetailProgDTO>();
			for (AdmSelectionProcessPlanDetailProgDBO progDBO : detailsDBO.admSelectionProcessPlanDetailProgDBOs) {
				if(!Utils.isNullOrEmpty(progDBO.id) && !Utils.isNullOrEmpty(progDBO.recordStatus) && progDBO.recordStatus == 'A')
				convertProgDBOstoDTOs(progDBO, addSlotDTO.detailProg);
			}
		}
		if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessPlanCenterBasedDBOs) && detailsDBO.admSelectionProcessPlanCenterBasedDBOs.size()>0) {
			addSlotDTO.citieslist = new ArrayList<AdmSelectionProcessPlanCenterBasedDTO>();
			for (AdmSelectionProcessPlanCenterBasedDBO centerBasedDBO : detailsDBO.admSelectionProcessPlanCenterBasedDBOs) {
				if(!Utils.isNullOrEmpty(centerBasedDBO.id) && !Utils.isNullOrEmpty(centerBasedDBO.recordStatus) && centerBasedDBO.recordStatus == 'A') {
					convertCenterBaseDBOsToDTOs(centerBasedDBO,addSlotDTO.citieslist);
				}
			}
		}
		return addSlotDTO;
	}

	public AdmSelectionProcessPlanDetailAddVenueDTO convertAddVenueDboToDto(AdmSelectionProcessPlanDetailDBO detailsDBO,
			AdmSelectionProcessPlanDetailAddVenueDTO addVenueDTO, AdmSelectionProcessPlanDBO dbo) {
		addVenueDTO.parentId = String.valueOf(dbo.id);
		if(!Utils.isNullOrEmpty(detailsDBO.id))
			addVenueDTO.id = String.valueOf(detailsDBO.id);
		if(!Utils.isNullOrEmpty(detailsDBO.processOrder))
			addVenueDTO.processOrder = String.valueOf(detailsDBO.processOrder);
		if(!Utils.isNullOrEmpty(detailsDBO.selectionProcessDate))
			addVenueDTO.selectionprocessdate = detailsDBO.selectionProcessDate;
		if(!Utils.isNullOrEmpty(detailsDBO.availableSeats))
			addVenueDTO.avaliableseats = String.valueOf(detailsDBO.availableSeats);
		if(!Utils.isNullOrEmpty(detailsDBO.processOrder) && detailsDBO.processOrder == 2) {
//			if(!Utils.isNullOrEmpty(detailsDBO.isCandidateChooseSp2Venue) && !Utils.isNullOrEmpty(detailsDBO.isCandidateChooseSp2Date)) {
//				addVenueDTO.secondRoundVenueSp = String.valueOf(detailsDBO.isCandidateChooseSp2Venue);
//				addVenueDTO.secondRoundDateSp = String.valueOf(detailsDBO.isCandidateChooseSp2Date);
//				addVenueDTO.secondRoundEligibility = "true";
//			}
//			else if(!Utils.isNullOrEmpty(detailsDBO.isCandidateChooseSpDate) && !Utils.isNullOrEmpty(detailsDBO.isCandidateChooseSpVenue)) {
//				addVenueDTO.dateSelectionProcess = String.valueOf(detailsDBO.isCandidateChooseSpDate);
//				addVenueDTO.venueSelectionProcess = String.valueOf(detailsDBO.isCandidateChooseSpVenue);
//				addVenueDTO.secondRoundEligibility = "false";
//			}
			if(!Utils.isNullOrEmpty(detailsDBO.isCandidateChooseSp2Venue)) {
				addVenueDTO.secondRoundVenueSp = String.valueOf(detailsDBO.isCandidateChooseSp2Venue);
			}
			if(!Utils.isNullOrEmpty(detailsDBO.isCandidateChooseSp2Date)) {
				addVenueDTO.secondRoundDateSp = String.valueOf(detailsDBO.isCandidateChooseSp2Date);
			}
			if(!Utils.isNullOrEmpty(detailsDBO.isfollowTheSameVenueForSp2)) {
				addVenueDTO.followSameVenue = String.valueOf(detailsDBO.isfollowTheSameVenueForSp2);
			}

		}
		if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO)) {
			addVenueDTO.mode = !Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO.mode)?String.valueOf(detailsDBO.admSelectionProcessTypeDBO.mode):"";
			ExModelBaseDTO spBaseDTO = new ExModelBaseDTO();
			spBaseDTO.id = String.valueOf(detailsDBO.admSelectionProcessTypeDBO.id);
			spBaseDTO.text = !Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO.selectionStageName)?detailsDBO.admSelectionProcessTypeDBO.selectionStageName:"";
			addVenueDTO.selectionProcessName = spBaseDTO;
			if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessTypeDBO.isShortlistAfterThisStage)) {
				addVenueDTO.isShortListFlag = detailsDBO.admSelectionProcessTypeDBO.isShortlistAfterThisStage;
			}
		}
		if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessPlanDetailProgDBOs) && detailsDBO.admSelectionProcessPlanDetailProgDBOs.size()>0) {
			addVenueDTO.detailProg = new ArrayList<AdmSelectionProcessPlanDetailProgDTO>();
			for (AdmSelectionProcessPlanDetailProgDBO progDBO : detailsDBO.admSelectionProcessPlanDetailProgDBOs) {
				if(!Utils.isNullOrEmpty(progDBO.id) && !Utils.isNullOrEmpty(progDBO.recordStatus) && progDBO.recordStatus == 'A')
				convertProgDBOstoDTOs(progDBO, addVenueDTO.detailProg);
			}
		}
		if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessVenueCityDBO)) {
			ExModelBaseDTO baseDTO = new ExModelBaseDTO();
			baseDTO.id = String.valueOf(detailsDBO.admSelectionProcessVenueCityDBO.id);
			baseDTO.text = !Utils.isNullOrEmpty(detailsDBO.admSelectionProcessVenueCityDBO.venueName)?detailsDBO.admSelectionProcessVenueCityDBO.venueName:"";
			addVenueDTO.venue = baseDTO;
		}
		if(!Utils.isNullOrEmpty(detailsDBO.admSelectionProcessPlanDetailAllotmentDBOs)) {
			addVenueDTO.timewithseats = new ArrayList<AdmSelectionProcessPlanDetailAllotmentDTO>();
			for (AdmSelectionProcessPlanDetailAllotmentDBO allotmentDBO : detailsDBO.admSelectionProcessPlanDetailAllotmentDBOs) {
				if(!Utils.isNullOrEmpty(allotmentDBO.recordStatus) && allotmentDBO.recordStatus == 'A') {
					AdmSelectionProcessPlanDetailAllotmentDTO allotmentDTO = new AdmSelectionProcessPlanDetailAllotmentDTO();
					allotmentDTO = convertAllotedDtoToDbo(allotmentDTO, allotmentDBO);
					addVenueDTO.timewithseats.add(allotmentDTO);
				}
			}
		}
		return addVenueDTO;
	}

	private AdmSelectionProcessPlanDetailAllotmentDTO convertAllotedDtoToDbo(AdmSelectionProcessPlanDetailAllotmentDTO allotmentDTO,
			AdmSelectionProcessPlanDetailAllotmentDBO allotmentDBO) {
		if(!Utils.isNullOrEmpty(allotmentDBO.id)) {
			allotmentDTO.id = String.valueOf(allotmentDBO.id);
		}
		if(!Utils.isNullOrEmpty(allotmentDBO.selectionProcessTime)) {
			allotmentDTO.time = String.valueOf(allotmentDBO.selectionProcessTime);
		}
		if(!Utils.isNullOrEmpty(allotmentDBO.selectionProcessSeats)) {
			allotmentDTO.seat = String.valueOf(allotmentDBO.selectionProcessSeats);
		}
		return allotmentDTO;
	}

	private void convertCenterBaseDBOsToDTOs(AdmSelectionProcessPlanCenterBasedDBO centerBasedDBO,List<AdmSelectionProcessPlanCenterBasedDTO> citieslist) {
		AdmSelectionProcessPlanCenterBasedDTO centerBasedDTO = new AdmSelectionProcessPlanCenterBasedDTO();
		centerBasedDTO.id = String.valueOf(centerBasedDBO.id);
		if(!Utils.isNullOrEmpty(centerBasedDBO.admSelectionProcessVenueCityDBO)) {
			ExModelBaseDTO cityBaseDTO = new ExModelBaseDTO();
			cityBaseDTO.id = String.valueOf(centerBasedDBO.admSelectionProcessVenueCityDBO.id);
			cityBaseDTO.text = !Utils.isNullOrEmpty(centerBasedDBO.admSelectionProcessVenueCityDBO.venueName)?centerBasedDBO.admSelectionProcessVenueCityDBO.venueName:"";
			centerBasedDTO.city = cityBaseDTO;
			centerBasedDTO.venueTypeId =  String.valueOf(centerBasedDBO.admSelectionProcessVenueCityDBO.id);
		}
		if(!Utils.isNullOrEmpty(centerBasedDBO.admSelectionProcessVenueCityDBO)){
			if(!Utils.isNullOrEmpty(centerBasedDBO.admSelectionProcessVenueCityDBO.getErpCountryDBO())){
				centerBasedDTO.setCountry(new SelectDTO());
				centerBasedDTO.getCountry().setValue(String.valueOf(centerBasedDBO.admSelectionProcessVenueCityDBO.getErpCountryDBO().getId()));
				centerBasedDTO.getCountry().setLabel(centerBasedDBO.admSelectionProcessVenueCityDBO.getErpCountryDBO().getCountryName());
			}
			if(!Utils.isNullOrEmpty(centerBasedDBO.admSelectionProcessVenueCityDBO.getErpStateDBO())){
				centerBasedDTO.setState(new SelectDTO());
				centerBasedDTO.getState().setValue(String.valueOf(centerBasedDBO.admSelectionProcessVenueCityDBO.getErpStateDBO().getId()));
				centerBasedDTO.getState().setLabel(centerBasedDBO.admSelectionProcessVenueCityDBO.getErpStateDBO().getStateName());
			}
		}

		if(!Utils.isNullOrEmpty(centerBasedDBO.venueMaxSeats)) {
			centerBasedDTO.maxseats = String.valueOf(centerBasedDBO.venueMaxSeats);
		}
		if(!Utils.isNullOrEmpty(centerBasedDBO.venueAvailableSeats)) {
			centerBasedDTO.avaliableseats = String.valueOf(centerBasedDBO.venueAvailableSeats);
		}
		citieslist.add(centerBasedDTO);
	}

	private void convertProgDBOstoDTOs(AdmSelectionProcessPlanDetailProgDBO progDBO,List<AdmSelectionProcessPlanDetailProgDTO> detailProgDTOList) {
		AdmSelectionProcessPlanDetailProgDTO detailProgDTO = new AdmSelectionProcessPlanDetailProgDTO();
			detailProgDTO.id = String.valueOf(progDBO.id);	
//			if(!Utils.isNullOrEmpty(progDBO.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(progDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
//				detailProgDTO.campusMappingId = String.valueOf(progDBO.erpCampusProgrammeMappingDBO.id);
//				if(!Utils.isNullOrEmpty(progDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
//					ExModelBaseDTO programmeBaseDTO = new ExModelBaseDTO();
//					programmeBaseDTO.id =  String.valueOf(progDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id);
//					detailProgDTO.programmme = programmeBaseDTO;
//				}
//				if(!Utils.isNullOrEmpty(progDBO.erpCampusProgrammeMappingDBO.erpCampusDBO)) {
//					ExModelBaseDTO campusBaseDTO = new ExModelBaseDTO();
//					campusBaseDTO.id =  String.valueOf(progDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id);
//					detailProgDTO.campus = campusBaseDTO;
//					detailProgDTO.preferance = String.valueOf(progDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id);
//					detailProgDTO.preferenceOption = 'C';
//				}
//				else if(!Utils.isNullOrEmpty(progDBO.erpCampusProgrammeMappingDBO.erpLocationDBO)) {
//					ExModelBaseDTO locationBaseDTO = new ExModelBaseDTO();
//					locationBaseDTO.id =  String.valueOf(progDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.id);
//					detailProgDTO.campus = locationBaseDTO;
//					detailProgDTO.preferance = String.valueOf(progDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.id);
//					detailProgDTO.preferenceOption = 'L';
//				}
//			}

				if(!Utils.isNullOrEmpty(progDBO.getAdmProgrammeBatchDBO())) {
					ExModelBaseDTO programmeBaseDTO = new ExModelBaseDTO();
					programmeBaseDTO.id =  String.valueOf(progDBO.getAdmProgrammeBatchDBO().getId());

					AdmProgrammeBatchDBO data = progDBO.getAdmProgrammeBatchDBO();
					String value = data.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeNameForApplication();
					if(!Utils.isNullOrEmpty(data) && !Utils.isNullOrEmpty(data.getErpCampusProgrammeMappingDBO().erpCampusDBO)){
						value += " ("+data.getErpCampusProgrammeMappingDBO().erpCampusDBO.getShortName()+")";
					} else if(!Utils.isNullOrEmpty(data) && !Utils.isNullOrEmpty(data.getErpCampusProgrammeMappingDBO().erpLocationDBO)) {
						value += " ("+data.getErpCampusProgrammeMappingDBO().erpLocationDBO.locationShortName+")";
					}
					value +="-"+data.getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO().getAdmissionType();
					programmeBaseDTO.text = value;
					detailProgDTO.programmme = programmeBaseDTO;
				}
			
		detailProgDTOList.add(detailProgDTO);
	}

	public AdmSelectionProcessPlanDetailAllotmentDBO venueTimewithseatsDtoToDbo(AdmSelectionProcessPlanDetailAllotmentDTO detailAllotmentDTO,
			AdmSelectionProcessPlanDetailAllotmentDBO allotmentDBO, AdmSelectionProcessPlanDetailDBO detailDbo) throws ParseException {
		if(!Utils.isNullOrEmpty(detailAllotmentDTO.id)) {
			allotmentDBO.selectionProcessSeats = Integer.parseInt(detailAllotmentDTO.id);
		}
		if(!Utils.isNullOrEmpty(detailAllotmentDTO.seat)) {
			allotmentDBO.selectionProcessSeats = Integer.parseInt(detailAllotmentDTO.seat);
		}
		if(!Utils.isNullOrEmpty(detailAllotmentDTO.time)) {
			allotmentDBO.selectionProcessTime = Utils.convertStringTimeToLocalTime(detailAllotmentDTO.time);
		}
		allotmentDBO.admSelectionProcessPlanDetailDBO = detailDbo;
		allotmentDBO.recordStatus = 'A';
		return allotmentDBO;
	}

	public AdmSelectionProcessPlanDetailProgDBO convertProgPrefDTOtoDBO(ProgramPreferenceDTO preferenceDTO,AdmSelectionProcessPlanDetailProgDBO planDetailProgDBO, AdmSelectionProcessPlanDetailDBO detailDbo) {
//		if(!Utils.isNullOrEmpty(preferenceDTO.campusMappingId)) {
//			ErpCampusProgrammeMappingDBO campusProgrammeMappingDBO = new ErpCampusProgrammeMappingDBO();
//			campusProgrammeMappingDBO.id = Integer.parseInt(preferenceDTO.campusMappingId);
//			planDetailProgDBO.erpCampusProgrammeMappingDBO = campusProgrammeMappingDBO;
//		}
//		if(!Utils.isNullOrEmpty(preferenceDTO.acaBatchId)) {
//			AcaBatchDBO acaBatchDBO = new AcaBatchDBO();
//			acaBatchDBO.setId(Integer.parseInt(preferenceDTO.acaBatchId));
//			planDetailProgDBO.setAcaBatchDBO(acaBatchDBO);
//		}
		if(!Utils.isNullOrEmpty(preferenceDTO)){
			planDetailProgDBO.setAdmProgrammeBatchDBO(new AdmProgrammeBatchDBO());
			planDetailProgDBO.getAdmProgrammeBatchDBO().setId(Integer.parseInt(preferenceDTO.getValue()));
		}
		planDetailProgDBO.recordStatus = 'A';
		planDetailProgDBO.admSelectionProcessPlanDetailDBO = detailDbo;
		return planDetailProgDBO;
	}

	public AdmSelectionProcessPlanCenterBasedDBO convertCenterBasedDtoToDbo(
			AdmSelectionProcessPlanCenterBasedDTO centerBasedDto, AdmSelectionProcessPlanCenterBasedDBO centerBasedDBO,
			AdmSelectionProcessPlanDetailDBO detailDbo) {
		if(!Utils.isNullOrEmpty(centerBasedDto.venueTypeId)) {
			AdmSelectionProcessVenueCityDBO venueCityDBO = new AdmSelectionProcessVenueCityDBO();
			venueCityDBO.id = Integer.parseInt(centerBasedDto.city.id);
			centerBasedDBO.admSelectionProcessVenueCityDBO = venueCityDBO;
		}
		if(!Utils.isNullOrEmpty(centerBasedDto.maxseats)) {
			centerBasedDBO.venueMaxSeats = Integer.parseInt(centerBasedDto.maxseats);
		}
		if(!Utils.isNullOrEmpty(centerBasedDto.avaliableseats)) {
			centerBasedDBO.venueAvailableSeats = Integer.parseInt(centerBasedDto.avaliableseats);
		}
		centerBasedDBO.admSelectionProcessPlanDetailDBO = detailDbo;
		centerBasedDBO.recordStatus = 'A';
		centerBasedDBO.admSelectionProcessPlanDetailDBO = detailDbo;
		return centerBasedDBO;
	}

}
