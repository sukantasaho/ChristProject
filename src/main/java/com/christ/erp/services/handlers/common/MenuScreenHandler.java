package com.christ.erp.services.handlers.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpScreenConfigMastDBO;
import com.christ.erp.services.dbobjects.common.SysFunctionDBO;
import com.christ.erp.services.dbobjects.common.SysMenuDBO;
import com.christ.erp.services.dbobjects.common.SysMenuModuleDBO;
import com.christ.erp.services.dbobjects.common.SysMenuModuleSubDBO;
import com.christ.erp.services.dto.common.MenuScreenDTO;
import com.christ.erp.services.dto.common.ModuleSubDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.common.SysFunctionDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.common.MenuScreenTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
@Service
public class MenuScreenHandler {

	@Autowired
	private MenuScreenTransaction menuScreenTransaction;
	
	
	public Flux<SelectDTO> getModule() {
		return menuScreenTransaction.getModule().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	
	public SelectDTO convertDBOToDTO(SysMenuModuleDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getModuleName());
		}
		return dto;
	}
	
	public Mono<List<ModuleSubDTO>> getSubModule(String moduleId) {
		List<SysMenuModuleSubDBO> list = menuScreenTransaction.getSubModule(moduleId);
		return this.convertDBOToDTO1(list);
	}
	
	public Mono<List<ModuleSubDTO>> convertDBOToDTO1(List<SysMenuModuleSubDBO> dbos){
		List<ModuleSubDTO> dtos = new ArrayList<ModuleSubDTO>();
		dbos.forEach(dbo -> {
			Integer order =!Utils.isNullOrEmpty(menuScreenTransaction.getDisplayOrder(dbo.getId()))?menuScreenTransaction.getDisplayOrder(dbo.getId())+1:1;
			ModuleSubDTO dto = new ModuleSubDTO();
			dto.setSubModule(new SelectDTO());
			if (!Utils.isNullOrEmpty(dbo)) {
				dto.getSubModule().setValue(String.valueOf(dbo.getId()));
				dto.getSubModule().setLabel(dbo.getSubModuleName());
				dto.setMenuDisplayOrder(order);
				dtos.add(dto);
			}
		});
		return Mono.just(dtos);
	}
	
	public Flux<SelectDTO> getMasterScreenReference() {
		return menuScreenTransaction.getMasterScreenReference().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	
	public SelectDTO convertDBOToDTO(ErpScreenConfigMastDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getTitle());
		}
		return dto;
	}
	
	public Mono<List<MenuScreenDTO>> getGridData() {
		 List<SysMenuDBO> datas = menuScreenTransaction.getGridData();
		return this.convertDBOToDTO(datas);
	}
	
	public Mono<List<MenuScreenDTO>> convertDBOToDTO(List<SysMenuDBO> dbos){
		List<MenuScreenDTO> dtos = new ArrayList<MenuScreenDTO>();
		Map<String, MenuScreenDTO> map = new HashMap<String, MenuScreenDTO>();
		if (!Utils.isNullOrEmpty(dbos)) {
			dbos.forEach( dbo -> {
				if(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getRecordStatus() == 'A') {
					if(dbo.getSysMenuModuleSubDBO().getRecordStatus() == 'A') {
							if(!map.containsKey(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName())) {
								MenuScreenDTO dto = new MenuScreenDTO();
								dto.setModuleId(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId());
								dto.setModuleName(new SelectDTO());
								dto.getModuleName().setValue(String.valueOf(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId()));
								dto.getModuleName().setLabel(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName());
								dto.setModuleSubDTO(new ArrayList<ModuleSubDTO>());
								ModuleSubDTO subDto = new ModuleSubDTO();
								subDto.setMenuId(String.valueOf(dbo.getId()));
								subDto.setSubModule(new SelectDTO());
								subDto.getSubModule().setValue(String.valueOf(dbo.getSysMenuModuleSubDBO().getId()));
								subDto.getSubModule().setLabel(dbo.getSysMenuModuleSubDBO().getSubModuleName());
								subDto.setMenuName(dbo.getMenuScreenName());
								subDto.setMenuDisplayOrder(dbo.getMenuScreenDisplayOrder());
								subDto.setSubmoduleName(dbo.getSysMenuModuleSubDBO().getSubModuleName());
								dto.getModuleSubDTO().add(subDto);
								map.put(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName(), dto);
							} else {
								MenuScreenDTO dto = map.get(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName());
								ModuleSubDTO subDto = new ModuleSubDTO();
								subDto.setMenuId(String.valueOf(dbo.getId()));
								subDto.setSubModule(new SelectDTO());
								subDto.getSubModule().setValue(String.valueOf(dbo.getSysMenuModuleSubDBO().getId()));
								subDto.getSubModule().setLabel(dbo.getSysMenuModuleSubDBO().getSubModuleName());
								subDto.setMenuName(dbo.getMenuScreenName());
								subDto.setMenuDisplayOrder(dbo.getMenuScreenDisplayOrder());
								subDto.setSubmoduleName(dbo.getSysMenuModuleSubDBO().getSubModuleName());
								dto.getModuleSubDTO().stream().collect(Collectors.groupingBy(ModuleSubDTO::getSubmoduleName));
								dto.getModuleSubDTO().add(subDto);
								map.replace(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName(), dto);
							}
					}
			    }
			});
			map.forEach((key,value) -> {
				dtos.add(value);
			});
		}
		dtos.sort(Comparator.comparing(MenuScreenDTO::getModuleId));
		return Mono.just(dtos);
	}
	
	public Mono<MenuScreenDTO> edit(int id) {
		SysMenuDBO dbo = menuScreenTransaction.edit(id);
		return this.convertDboToDto(dbo);
	}
	
	public Mono<MenuScreenDTO> convertDboToDto(SysMenuDBO dbo) {
		MenuScreenDTO dto = new MenuScreenDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setMenuId(dbo.getId());
			dto.setModuleName(new SelectDTO());
			dto.getModuleName().setValue(String.valueOf(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId()));
			dto.getModuleName().setLabel(dbo.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName());
			dto.setSubModule(new SelectDTO());
			dto.getSubModule().setValue(String.valueOf(dbo.getSysMenuModuleSubDBO().getId()));
			dto.getSubModule().setLabel(dbo.getSysMenuModuleSubDBO().getSubModuleName());
			dto.setMenuName(dbo.getMenuScreenName());
			dto.setMenuComponentPath(dbo.getMenuComponentPath());
			dto.setDisplayOrderNo(dbo.getMenuScreenDisplayOrder().toString());
			if(!Utils.isNullOrEmpty(dbo.getErpScreenConfigMastDBO())) {
				dto.setMasterScreenReference(new SelectDTO());
				dto.getMasterScreenReference().setValue(String.valueOf(dbo.getErpScreenConfigMastDBO().getId()));
				dto.getMasterScreenReference().setLabel(dbo.getErpScreenConfigMastDBO().getTitle());
			}
			dto.setMenuLink(dbo.getIsMenuLink());
			if(!Utils.isNullOrEmpty(dbo.getIsReportMenu())) {	
				dto.setReportMenu(dbo.getIsReportMenu());
			}
			if(!Utils.isNullOrEmpty(dbo.getIsUserSpecificReport())) {
				dto.setUserSpecificReport(dbo.getIsUserSpecificReport());	
			}
			dto.setOtpRequired(dbo.getIsOtpRequired());
			dto.setOtpForEveryInstance(dbo.getIsOtpRequiredOnEveryInstance());
			dto.setSysFunctionDTO(new ArrayList<SysFunctionDTO>());
			dbo.getSysFunctionDBOSet().forEach( subDbo -> {
				SysFunctionDTO subDto = new SysFunctionDTO();
				subDto.id = String.valueOf(subDbo.getId());
				subDto.setFunctionName(subDbo.getFunctionName());
				subDto.setFunctionDescription(subDbo.getFunctionDescription());
//				subDto.setFunctionComponent(subDbo.getFunctionComponentName());
				subDto.setAccessToken(subDbo.getAccessToken());
				subDto.setUnauthorisedMessage(subDbo.getDisplayMessage());
				dto.getSysFunctionDTO().add(subDto);
			});
		}
		return Mono.just(dto);
	}
	
	public Mono<ApiResult> delete(int id, String userId) {
        return menuScreenTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
    }

	public Mono<ApiResult> saveOrUpdate(Mono<MenuScreenDTO> dto, String userId) {
		return dto.handle((menuScreenDTO,synchronousSink) -> {
			boolean duplicate = menuScreenTransaction.duplicateCheck(menuScreenDTO);
			if(duplicate) {
				synchronousSink.error(new DuplicateException("Already Menu Name or Display Order for given Sub Module exist"));
			} else {
				synchronousSink.next(menuScreenDTO);
			}
		}).cast(MenuScreenDTO.class)
		  .map(data -> convertDtoToDbo(data, userId))
		  .flatMap( s -> {
			  if (!Utils.isNullOrEmpty(s.getId())) {
				  menuScreenTransaction.update(s);
		      } else {
		          menuScreenTransaction.save(s);
		      }
			  return Mono.just(Boolean.TRUE);
		  }).map(Utils::responseResult);
	}
	
	public SysMenuDBO convertDtoToDbo(MenuScreenDTO dto, String userId) {
		SysMenuDBO dbo ;
		if(!Utils.isNullOrEmpty(dto.getMenuId())) {
		    dbo =  menuScreenTransaction.edit(dto.getMenuId());
	    } else {
	    	dbo = new SysMenuDBO();
	    }
		dbo.setMenuScreenName(dto.getMenuName());
		dbo.setMenuComponentPath(dto.getMenuComponentPath());
		dbo.setMenuScreenDisplayOrder(Integer.parseInt(dto.getDisplayOrderNo()));
		dbo.setSysMenuModuleSubDBO(new SysMenuModuleSubDBO());
		dbo.getSysMenuModuleSubDBO().setId(Integer.parseInt(dto.getSubModule().getValue()));
		if(!Utils.isNullOrEmpty(dto.getMasterScreenReference())) {
			dbo.setErpScreenConfigMastDBO(new ErpScreenConfigMastDBO());
			dbo.getErpScreenConfigMastDBO().setId(Integer.parseInt(dto.getMasterScreenReference().getValue()));
		}
		dbo.setIsReportMenu(dto.isReportMenu());
		dbo.setIsMenuLink(dto.isMenuLink());
		dbo.setIsOtpRequired(dto.isOtpRequired());
		dbo.setIsOtpRequiredOnEveryInstance(dto.isOtpForEveryInstance());
		dbo.setIsUserSpecificReport(dto.isUserSpecificReport());
		dbo.setRecordStatus('A');
		dbo.setIsDisplayed(true);
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		if(!Utils.isNullOrEmpty(dto.getMenuId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		Set<SysFunctionDBO> sysFunctionDBOExist = !Utils.isNullOrEmpty(dbo.getSysFunctionDBOSet()) ?  dbo.getSysFunctionDBOSet(): null;
		Map<Integer,SysFunctionDBO> existDBOMap = new HashMap<Integer, SysFunctionDBO>();
		if(!Utils.isNullOrEmpty(sysFunctionDBOExist)) {
			sysFunctionDBOExist.forEach( data -> {
				if(data.getRecordStatus() == 'A') {
					existDBOMap.put(data.getId(), data);
				}
			});
		}
		dbo.setSysFunctionDBOSet(new HashSet<SysFunctionDBO>());
		dto.getSysFunctionDTO().forEach( subDto -> {
			SysFunctionDBO subDbo = null;
			if (!Utils.isNullOrEmpty(subDto.id) && existDBOMap.containsKey(Integer.parseInt(subDto.id))) {	
				subDbo = existDBOMap.get((Integer.parseInt(subDto.id)));
				subDbo.modifiedUsersId = Integer.parseInt(userId);
    			existDBOMap.remove(Integer.parseInt(subDto.id));
            } else {
            	subDbo = new SysFunctionDBO();
            	subDbo.createdUsersId = Integer.parseInt(userId);
			}
			subDbo.setSysMenuDBO(dbo);
			subDbo.setFunctionName(subDto.getFunctionName());
			subDbo.setFunctionDescription(subDto.getFunctionDescription());
//			subDbo.setFunctionComponentName(subDto.getFunctionComponent());
			subDbo.setAccessToken(subDto.getAccessToken());
			if(!Utils.isNullOrEmpty(subDto.getUnauthorisedMessage())) {
				subDbo.setDisplayMessage(subDto.getUnauthorisedMessage());
			}
			subDbo.setRecordStatus('A');
			dbo.getSysFunctionDBOSet().add(subDbo);
		});
		if(!Utils.isNullOrEmpty(existDBOMap)) {
			existDBOMap.forEach((key,value) -> {
				value.setRecordStatus('D');
				value.setModifiedUsersId(Integer.parseInt(userId));
				dbo.getSysFunctionDBOSet().add(value);
			});
		}
		return dbo;
	}
	
}
