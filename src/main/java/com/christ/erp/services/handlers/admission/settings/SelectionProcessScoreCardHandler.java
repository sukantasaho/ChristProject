package com.christ.erp.services.handlers.admission.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.Valid;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualitativeParamterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardQualitativeParameterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardQuantitativeParameterDBO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardDTO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardQualitativeParameterDTO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardQuantitativeParameterDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.transactions.admission.settings.SelectionProcessScoreCardTransaction;

public class SelectionProcessScoreCardHandler {
	
	private static volatile SelectionProcessScoreCardHandler  admSelectionProcessScoreCardHandler=null;
	
	SelectionProcessScoreCardTransaction admSelectionProcessScoreCardTransaction=SelectionProcessScoreCardTransaction.getInstance();
	
	public static SelectionProcessScoreCardHandler getInstance() {
        if(admSelectionProcessScoreCardHandler==null) {
        	admSelectionProcessScoreCardHandler = new SelectionProcessScoreCardHandler();
        }
        return admSelectionProcessScoreCardHandler;
    }
	
	public Boolean saveOrUpdate(@Valid AdmScoreCardDTO admScoreCardDTO, String userId) throws Exception {
		AdmScoreCardDBO admScoreCardDBO=null;
		Boolean isTrueOrFalse=null;
		if(Utils.isNullOrEmpty(admScoreCardDTO.id)) {
			admScoreCardDBO=new AdmScoreCardDBO();
			admScoreCardDBO.scorecardTemplateName=admScoreCardDTO.scoreCardTemplateName;
			admScoreCardDBO.recordStatus='A';
			admScoreCardDBO.createdUsersId=Integer.parseInt(userId);
			admScoreCardDBO.admScoreCardQualitativeParameterDBO=new HashSet<>();
			if(!Utils.isNullOrEmpty(admScoreCardDTO.qualitativeparameters)) {
				for (AdmScoreCardQualitativeParameterDTO admscorecardqualidto : admScoreCardDTO.qualitativeparameters) {
					AdmScoreCardQualitativeParameterDBO admScoreCardQualitativeParameterdbo = new AdmScoreCardQualitativeParameterDBO();
					if (!Utils.isNullOrEmpty(admscorecardqualidto.qualitativeparameter.id)) {
						admScoreCardQualitativeParameterdbo.admQualitativeParamterDBO = new AdmQualitativeParamterDBO();
						admScoreCardQualitativeParameterdbo.admQualitativeParamterDBO.id = Integer.parseInt(admscorecardqualidto.qualitativeparameter.id);
					}
					admScoreCardQualitativeParameterdbo.admScoreCardDBO = admScoreCardDBO;
					admScoreCardQualitativeParameterdbo.createdUsersId = Integer.parseInt(userId);
					admScoreCardQualitativeParameterdbo.recordStatus = 'A';
					admScoreCardQualitativeParameterdbo.orderNo = Integer.parseInt(admscorecardqualidto.orderNo);
					admScoreCardDBO.admScoreCardQualitativeParameterDBO.add(admScoreCardQualitativeParameterdbo);
				}
			}
			admScoreCardDBO.admScoreCardQuantitativeParameterDBO=new HashSet<>();
			for(AdmScoreCardQuantitativeParameterDTO admscorecardquantidto:admScoreCardDTO.quantitativeparameters) {
				AdmScoreCardQuantitativeParameterDBO admScoreCardQuantitativeParameterDBO=new AdmScoreCardQuantitativeParameterDBO();
				admScoreCardQuantitativeParameterDBO.admScoreCardDBO=admScoreCardDBO;
				admScoreCardQuantitativeParameterDBO.orderNo=admscorecardquantidto.orderNo;
				admScoreCardQuantitativeParameterDBO.parameterName=admscorecardquantidto.parameterName;
				admScoreCardQuantitativeParameterDBO.maxValue=admscorecardquantidto.maxValue;
				admScoreCardQuantitativeParameterDBO.intervalValue=admscorecardquantidto.valueInterval;
				admScoreCardQuantitativeParameterDBO.createdUsersId=Integer.parseInt(userId);
				admScoreCardQuantitativeParameterDBO.recordStatus='A';
				admScoreCardDBO.admScoreCardQuantitativeParameterDBO.add(admScoreCardQuantitativeParameterDBO);
			}
			 isTrueOrFalse=admSelectionProcessScoreCardTransaction.saveOrUpdate(admScoreCardDBO);
		}else {
			admScoreCardDBO=admSelectionProcessScoreCardTransaction.get(admScoreCardDTO.id);
			admScoreCardDBO.scorecardTemplateName=admScoreCardDTO.scoreCardTemplateName;
			admScoreCardDBO.recordStatus='A';
			admScoreCardDBO.modifiedUsersId=Integer.parseInt(userId);
			Map<Integer,AdmScoreCardQuantitativeParameterDBO> quantitativeParameterDBOMap=new HashMap<Integer, AdmScoreCardQuantitativeParameterDBO>();
			Map<Integer,AdmScoreCardQuantitativeParameterDTO> quantitativeParameterDTOMap=new HashMap<Integer, AdmScoreCardQuantitativeParameterDTO>();
			for(AdmScoreCardQuantitativeParameterDTO quantitativeDTO:admScoreCardDTO.quantitativeparameters) {
				if(!Utils.isNullOrEmpty(quantitativeDTO.id)) {
					quantitativeParameterDTOMap.put(Integer.parseInt(quantitativeDTO.id), quantitativeDTO);
				}
			}
            for(AdmScoreCardQuantitativeParameterDBO quantitativeDBO:admScoreCardDBO.admScoreCardQuantitativeParameterDBO) {
				quantitativeParameterDBOMap.put(quantitativeDBO.id, quantitativeDBO);
			}
            for(Entry<Integer, AdmScoreCardQuantitativeParameterDTO> quantitativeObj:quantitativeParameterDTOMap.entrySet()) {
            	if(quantitativeParameterDBOMap.containsKey(quantitativeObj.getKey())) {
					quantitativeParameterDBOMap.remove(quantitativeObj.getKey());
            	}
            }
            Map<Integer,AdmScoreCardQualitativeParameterDBO> qualitativeParameterDBOMap=new HashMap<Integer, AdmScoreCardQualitativeParameterDBO>();
			Map<Integer,AdmScoreCardQualitativeParameterDTO> qualitativeParameterDTOMap=new HashMap<Integer, AdmScoreCardQualitativeParameterDTO>();
			if(!Utils.isNullOrEmpty(admScoreCardDTO.qualitativeparameters)) {
				for (AdmScoreCardQualitativeParameterDTO qualitativeDTO : admScoreCardDTO.qualitativeparameters) {
					if (!Utils.isNullOrEmpty(qualitativeDTO.id)) {
						qualitativeParameterDTOMap.put(Integer.parseInt(qualitativeDTO.id), qualitativeDTO);
					}
				}
				for (AdmScoreCardQualitativeParameterDBO qualitativeDBO : admScoreCardDBO.admScoreCardQualitativeParameterDBO) {
					qualitativeParameterDBOMap.put(qualitativeDBO.id, qualitativeDBO);
				}
				for (Entry<Integer, AdmScoreCardQualitativeParameterDTO> qualitativeObj : qualitativeParameterDTOMap.entrySet()) {
					if (qualitativeParameterDBOMap.containsKey(qualitativeObj.getKey())) {
						qualitativeParameterDBOMap.remove(qualitativeObj.getKey());
					}
				}
				for (AdmScoreCardQualitativeParameterDTO qualitativeParameterDTO : admScoreCardDTO.qualitativeparameters) {
					if (!Utils.isNullOrEmpty(qualitativeParameterDTO.id)) {
						for (AdmScoreCardQualitativeParameterDBO qualitativeParameterDBO : admScoreCardDBO.admScoreCardQualitativeParameterDBO) {
							if (!Utils.isNullOrEmpty(qualitativeParameterDBO.id)) {
								if (qualitativeParameterDBOMap.containsKey(qualitativeParameterDBO.id)) {
									qualitativeParameterDBO.modifiedUsersId = Integer.parseInt(userId);
									qualitativeParameterDBO.recordStatus = 'D';
									admScoreCardDBO.admScoreCardQualitativeParameterDBO.add(qualitativeParameterDBO);
								} else {
									if (Integer.parseInt(qualitativeParameterDTO.id) == qualitativeParameterDBO.id) {
										if (!Utils.isNullOrEmpty(qualitativeParameterDTO.qualitativeparameter.id)) {
											qualitativeParameterDBO.admQualitativeParamterDBO.id = Integer.valueOf(qualitativeParameterDTO.qualitativeparameter.id);
										}
										qualitativeParameterDBO.admScoreCardDBO = admScoreCardDBO;
										qualitativeParameterDBO.modifiedUsersId = Integer.parseInt(userId);
										qualitativeParameterDBO.orderNo = Integer.parseInt(qualitativeParameterDTO.orderNo);
										qualitativeParameterDBO.recordStatus = 'A';
										admScoreCardDBO.admScoreCardQualitativeParameterDBO.add(qualitativeParameterDBO);
									}
								}
							}
						}
					}
					if (Utils.isNullOrEmpty(qualitativeParameterDTO.id)) {
						AdmScoreCardQualitativeParameterDBO admScoreCardQualitativeParameterDBO = new AdmScoreCardQualitativeParameterDBO();
						if (!Utils.isNullOrEmpty(qualitativeParameterDTO.qualitativeparameter.id)) {
							admScoreCardQualitativeParameterDBO.admQualitativeParamterDBO = new AdmQualitativeParamterDBO();
							admScoreCardQualitativeParameterDBO.admQualitativeParamterDBO.id = Integer.valueOf(qualitativeParameterDTO.qualitativeparameter.id);
						}
						admScoreCardQualitativeParameterDBO.admScoreCardDBO = admScoreCardDBO;
						admScoreCardQualitativeParameterDBO.recordStatus = 'A';
						admScoreCardQualitativeParameterDBO.orderNo = Integer.parseInt(qualitativeParameterDTO.orderNo);
						admScoreCardQualitativeParameterDBO.createdUsersId = Integer.parseInt(userId);
						admScoreCardDBO.admScoreCardQualitativeParameterDBO.add(admScoreCardQualitativeParameterDBO);
					}
				}
			}
			else{
				if(!Utils.isNullOrEmpty(admScoreCardDBO.admScoreCardQualitativeParameterDBO)){
					for (AdmScoreCardQualitativeParameterDBO qualitativeParameterDBO : admScoreCardDBO.admScoreCardQualitativeParameterDBO) {
						if (!Utils.isNullOrEmpty(qualitativeParameterDBO)) {
							qualitativeParameterDBO.modifiedUsersId = Integer.parseInt(userId);
							qualitativeParameterDBO.recordStatus = 'D';
							admScoreCardDBO.admScoreCardQualitativeParameterDBO.add(qualitativeParameterDBO);
						}
					}
				}
			}
			for(AdmScoreCardQuantitativeParameterDTO quantitativeParameterDTO:admScoreCardDTO.quantitativeparameters) {
				if(!Utils.isNullOrEmpty(quantitativeParameterDTO.id)) {
					for(AdmScoreCardQuantitativeParameterDBO obj:admScoreCardDBO.admScoreCardQuantitativeParameterDBO) {
						if(!Utils.isNullOrEmpty(obj.id)) {
							if(quantitativeParameterDBOMap.containsKey(obj.id)) {
								obj.recordStatus='D';
								obj.modifiedUsersId=Integer.parseInt(userId);
								admScoreCardDBO.admScoreCardQuantitativeParameterDBO.add(obj);
							} else {
								if(Integer.parseInt(quantitativeParameterDTO.id)==obj.id) {
									obj.admScoreCardDBO=admScoreCardDBO;
									obj.orderNo=quantitativeParameterDTO.orderNo;
									obj.parameterName=quantitativeParameterDTO.parameterName;
									obj.maxValue=quantitativeParameterDTO.maxValue;
									obj.intervalValue=quantitativeParameterDTO.valueInterval;
									obj.recordStatus='A';
									obj.modifiedUsersId=Integer.parseInt(userId);
									admScoreCardDBO.admScoreCardQuantitativeParameterDBO.add(obj);
								}
							}
						}
					}
				}
				if(Utils.isNullOrEmpty(quantitativeParameterDTO.id)) {
					AdmScoreCardQuantitativeParameterDBO admScoreCardQuantitativeParameterDBO=new AdmScoreCardQuantitativeParameterDBO();
					admScoreCardQuantitativeParameterDBO.admScoreCardDBO=admScoreCardDBO;
					admScoreCardQuantitativeParameterDBO.orderNo=quantitativeParameterDTO.orderNo;
					admScoreCardQuantitativeParameterDBO.parameterName=quantitativeParameterDTO.parameterName;
					admScoreCardQuantitativeParameterDBO.maxValue=quantitativeParameterDTO.maxValue;
					admScoreCardQuantitativeParameterDBO.intervalValue=quantitativeParameterDTO.valueInterval;
					admScoreCardQuantitativeParameterDBO.recordStatus='A';
					admScoreCardQuantitativeParameterDBO.createdUsersId=Integer.parseInt(userId);
					admScoreCardDBO.admScoreCardQuantitativeParameterDBO.add(admScoreCardQuantitativeParameterDBO);
				}
			}
			isTrueOrFalse=admSelectionProcessScoreCardTransaction.saveOrUpdate(admScoreCardDBO);	
		}
		return isTrueOrFalse;
	}
	
	public List<AdmScoreCardDTO> getGridData() throws Exception {
		List<AdmScoreCardDTO> listofdto=new ArrayList<AdmScoreCardDTO>();
		List<AdmScoreCardDBO> listdbo=admSelectionProcessScoreCardTransaction.getGridData();
		for(AdmScoreCardDBO admScoreCardDBO:listdbo) {
			AdmScoreCardDTO admScoreCardDTO=new AdmScoreCardDTO();
			admScoreCardDTO.id=String.valueOf(admScoreCardDBO.id);
			admScoreCardDTO.scoreCardTemplateName=admScoreCardDBO.scorecardTemplateName;
			admScoreCardDTO.qualitativeparameters=new HashSet<>();
			for(AdmScoreCardQualitativeParameterDBO obj:admScoreCardDBO.admScoreCardQualitativeParameterDBO) {
				AdmScoreCardQualitativeParameterDTO admScoreCardQualitativeParameterDTO=new AdmScoreCardQualitativeParameterDTO();
				admScoreCardQualitativeParameterDTO.id=String.valueOf(obj.id);
				obj.admQualitativeParamterDBO=new AdmQualitativeParamterDBO();
				admScoreCardQualitativeParameterDTO.orderNo=obj.orderNo.toString();
				admScoreCardDTO.qualitativeparameters.add(admScoreCardQualitativeParameterDTO);
			}
			admScoreCardDTO.quantitativeparameters =new HashSet<>();
			for(AdmScoreCardQuantitativeParameterDBO obj:admScoreCardDBO.admScoreCardQuantitativeParameterDBO) {
				AdmScoreCardQuantitativeParameterDTO admScoreCardQuantitativeParameterDTO=new AdmScoreCardQuantitativeParameterDTO();
				admScoreCardQuantitativeParameterDTO.id=String.valueOf(obj.id);
				admScoreCardQuantitativeParameterDTO.orderNo=obj.orderNo;
				admScoreCardQuantitativeParameterDTO.parameterName=obj.parameterName;
				admScoreCardQuantitativeParameterDTO.maxValue=obj.maxValue;
				admScoreCardQuantitativeParameterDTO.valueInterval=obj.intervalValue;
				admScoreCardDTO.quantitativeparameters.add(admScoreCardQuantitativeParameterDTO);
			}
			listofdto.add(admScoreCardDTO);
		}
		return listofdto;
	}
	
	public Boolean delete(String id, String userId) throws Exception {
		AdmScoreCardDBO admScoreCardDBO=admSelectionProcessScoreCardTransaction.get(id);
		admScoreCardDBO.recordStatus='D';
		admScoreCardDBO.modifiedUsersId=Integer.parseInt(userId);
		for(AdmScoreCardQualitativeParameterDBO admScoreCardQualitativeParameterDBO:admScoreCardDBO.admScoreCardQualitativeParameterDBO) {
			admScoreCardQualitativeParameterDBO.recordStatus='D';
			admScoreCardQualitativeParameterDBO.modifiedUsersId=Integer.parseInt(userId);
		}
		for(AdmScoreCardQuantitativeParameterDBO admScoreCardQuantitativeParameterDBO:admScoreCardDBO.admScoreCardQuantitativeParameterDBO) {
			admScoreCardQuantitativeParameterDBO.recordStatus='D';
			admScoreCardQuantitativeParameterDBO.modifiedUsersId=Integer.parseInt(userId);
		}
		Boolean isTrueOrFalse=admSelectionProcessScoreCardTransaction.delete(admScoreCardDBO);
		return isTrueOrFalse;
	}
	
	public AdmScoreCardDTO edit(String id) throws NumberFormatException, Exception {
		AdmScoreCardDBO admScoreCardDBO=admSelectionProcessScoreCardTransaction.get(id);
		AdmScoreCardDTO admScoreCardDTO =new AdmScoreCardDTO();
		admScoreCardDTO.id=String.valueOf(admScoreCardDBO.id);
		admScoreCardDTO.scoreCardTemplateName=admScoreCardDBO.scorecardTemplateName;
		admScoreCardDTO.qualitativeparameters=new HashSet<>();
		for(AdmScoreCardQualitativeParameterDBO obj:admScoreCardDBO.admScoreCardQualitativeParameterDBO) {
			if(obj.recordStatus=='A') {
				AdmScoreCardQualitativeParameterDTO admScoreCardQualitativeParameterDTO=new AdmScoreCardQualitativeParameterDTO();
				admScoreCardQualitativeParameterDTO.id=String.valueOf(obj.id);
				admScoreCardQualitativeParameterDTO.orderNo=obj.orderNo.toString();
				if( !Utils.isNullOrEmpty(obj) && !Utils.isNullOrEmpty(obj.admQualitativeParamterDBO) && !Utils.isNullOrEmpty(obj.admQualitativeParamterDBO.id) ) {
				 	ExModelBaseDTO objref=new ExModelBaseDTO();
				 	objref.id=obj.admQualitativeParamterDBO.id.toString();	
				 	objref.text=obj.admQualitativeParamterDBO.qualitativeParameterLabel;
					admScoreCardQualitativeParameterDTO.qualitativeparameter=objref;
				}else {
					ExModelBaseDTO objref=new ExModelBaseDTO();
				 	objref.id="";
				 	objref.text="";
					admScoreCardQualitativeParameterDTO.qualitativeparameter=objref;
					
				}
				if(!Utils.isNullOrEmpty(obj.admScoreCardDBO.id)) {
					ExModelBaseDTO objref=new ExModelBaseDTO();
					objref.id=String.valueOf(obj.admScoreCardDBO.id) ;
					admScoreCardQualitativeParameterDTO.admscorecardid=objref;	
				}
				admScoreCardDTO.qualitativeparameters.add(admScoreCardQualitativeParameterDTO);	
			}
		}
		admScoreCardDTO.quantitativeparameters=new HashSet<>();
		for(AdmScoreCardQuantitativeParameterDBO obj:admScoreCardDBO.admScoreCardQuantitativeParameterDBO) {
			if(obj.recordStatus=='A') {
				AdmScoreCardQuantitativeParameterDTO admScoreCardQuantitativeParameterDTO=new AdmScoreCardQuantitativeParameterDTO();
				admScoreCardQuantitativeParameterDTO.id=String.valueOf(obj.id);
				ExModelBaseDTO objref=new ExModelBaseDTO();
				objref.id=String.valueOf(obj.admScoreCardDBO.id);
				admScoreCardQuantitativeParameterDTO.admscorecardid=objref;
				admScoreCardQuantitativeParameterDTO.orderNo=obj.orderNo;
				admScoreCardQuantitativeParameterDTO.parameterName=obj.parameterName;
				admScoreCardQuantitativeParameterDTO.maxValue=obj.maxValue;
				admScoreCardQuantitativeParameterDTO.valueInterval=obj.intervalValue;
				admScoreCardDTO.quantitativeparameters.add(admScoreCardQuantitativeParameterDTO);	
			}	
		}
		return admScoreCardDTO;
	}

	public Boolean duplicateCheck(@Valid AdmScoreCardDTO admScoreCardDTO) throws Exception {
		return admSelectionProcessScoreCardTransaction.duplicateCheck(admScoreCardDTO);
	}
}
