package com.christ.erp.services.handlers.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisSysPropertiesData;
import com.christ.erp.services.common.SysProperties;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.GUIMenuShortcutLinkDBO;
import com.christ.erp.services.dbobjects.common.SysMenuDBO;
import com.christ.erp.services.dto.common.GUIMenuShortcutLinkDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.common.GUITransaction;

import reactor.core.publisher.Mono;

@Service
public class GUIHandler {

	@Autowired
	GUITransaction guiTransaction;
	
	@Autowired
	RedisSysPropertiesData redisSysPropertiesData;

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrDeleteFavouriteAndRecent(Mono<GUIMenuShortcutLinkDTO> dto, String userId) {
		return dto.map(data ->{
			var apiResult = new ApiResult();
			var idsToRemove = new ArrayList<Integer>();
			if(data.getQuickLinkType().equalsIgnoreCase("Q")) {
				if(!Utils.isNullOrEmpty(data.getId())) {
					idsToRemove.add(data.getId());
					guiTransaction.deleteFavouriteAndRecent(idsToRemove);
					apiResult.setSuccess(true);
				} else {
					var dbo = convertDtoToDbo(data,userId);
					if(!Utils.isNullOrEmpty(dbo)) {
						guiTransaction.save(dbo);
						apiResult.setSuccess(true);
					}
				}
			} else if(data.getQuickLinkType().equalsIgnoreCase("R")) {
				String max = redisSysPropertiesData.getSysProperties(SysProperties.RECENT_TAB_COUNT_MAX.name(), null, null);
				var list = guiTransaction.getGUIMenuShortcutLinkDBOByUserIdAndType(Integer.valueOf(userId),data.getQuickLinkType());
				Integer extra = null;
				if(!Utils.isNullOrEmpty(max) && !Utils.isNullOrEmpty(list)) {
					if(Integer.valueOf(max) <=list.size()) {
						extra = list.size() - Integer.valueOf(max);
						idsToRemove.addAll(list.stream().limit(extra+1).collect(Collectors.toList()));
						//				idsToRemove.addAll(list.stream().sorted(Comparator.comparingInt(s->s)).limit(extra+1).collect(Collectors.toList()));
					}
				}
				if(!Utils.isNullOrEmpty(data.getId())) {
					if(!Utils.isNullOrEmpty(idsToRemove)) {
						if(!idsToRemove.contains(data.getId())){
							idsToRemove.set(idsToRemove.size()-1, data.getId());
						}
					} else {
						idsToRemove.add(data.getId());
					}
				}
				if(!Utils.isNullOrEmpty(idsToRemove)) {
					guiTransaction.deleteFavouriteAndRecent(idsToRemove);
				}				
				var dbo = convertDtoToDbo(data,userId);
				if(!Utils.isNullOrEmpty(dbo)) {
					guiTransaction.save(dbo);
					apiResult.setSuccess(true);
				}
			}
			return apiResult;
		});
	}
	
	private GUIMenuShortcutLinkDBO convertDtoToDbo(GUIMenuShortcutLinkDTO dto, String userId) {
		GUIMenuShortcutLinkDBO dbo = new GUIMenuShortcutLinkDBO();
		if(!Utils.isNullOrEmpty(userId)) {
			dbo.setErpUsersDBO(new ErpUsersDBO());
			dbo.getErpUsersDBO().setId(Integer.valueOf(userId));
			dbo.setCreatedUsersId(Integer.valueOf(userId));
		}
		if(!Utils.isNullOrEmpty(dto.getSysMenuDTO())) {
			if(!Utils.isNullOrEmpty(dto.getSysMenuDTO().getValue())) {
				dbo.setSysMenuDBO(new SysMenuDBO());
				dbo.getSysMenuDBO().setId(Integer.valueOf(dto.getSysMenuDTO().getValue()));
			}
		}
		if(dto.getQuickLinkType().equalsIgnoreCase("Q")) {
			dbo.setQuickLinkType("Q");
		}
		if(dto.getQuickLinkType().equalsIgnoreCase("R")) {
			dbo.setQuickLinkType("R");
		}
		dbo.setRecordStatus('A');
		return dbo;
	}

	public Mono<GUIMenuShortcutLinkDTO> getFavouriteAndRecentList(String userId) {		
		return	 guiTransaction.getGUIMenuShortcutLinkDBOByUserId(Integer.valueOf(userId)).flatMap(s-> {
			return convertDboToDto(s) ;	
		}); 
	}

	private Mono<GUIMenuShortcutLinkDTO> convertDboToDto(List<GUIMenuShortcutLinkDBO> dboList) {
		GUIMenuShortcutLinkDTO dto =null;
		if(!Utils.isNullOrEmpty(dboList)) {
			dto = new GUIMenuShortcutLinkDTO();
			var favouriteList = new ArrayList<GUIMenuShortcutLinkDTO>();
			var recentTabList = new ArrayList<GUIMenuShortcutLinkDTO>();
			AtomicInteger count = new AtomicInteger(1);
			dboList.forEach(s->{
				var	dto1 = new GUIMenuShortcutLinkDTO();
				if(s.getQuickLinkType().equalsIgnoreCase("Q")) {
					favouriteList.add(dboToDto(s,dto1));
				} else if(s.getQuickLinkType().equalsIgnoreCase("R")) {
					dboToDto(s,dto1).setLinkDisplayOrder(count.getAndIncrement());
					recentTabList.add(dto1);
				}
			});
			dto.setFavouriteList(favouriteList);
//			Collections.sort(recentTabList, Comparator.comparingInt(GUIMenuShortcutLinkDTO::getId).reversed());
			dto.setRecentTabList(recentTabList);
		}
		return !Utils.isNullOrEmpty(dto)? Mono.just(dto) : Mono.empty();
	}

	private GUIMenuShortcutLinkDTO dboToDto(GUIMenuShortcutLinkDBO dbo, GUIMenuShortcutLinkDTO dto) {
//		GUIMenuShortcutLinkDTO dto = new GUIMenuShortcutLinkDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setId(dbo.getId());
		}
		if(!Utils.isNullOrEmpty(dbo.getSysMenuDBO())) {
			dto.setSysMenuDTO(new SelectDTO());
			dto.getSysMenuDTO().setValue(String.valueOf(dbo.getSysMenuDBO().getId()));
			dto.getSysMenuDTO().setLabel(dbo.getSysMenuDBO().getMenuScreenName());
			dto.setSysMenuComponentPath(dbo.getSysMenuDBO().getMenuComponentPath());
			if(!Utils.isNullOrEmpty(dbo.getSysMenuDBO().getSysMenuModuleSubDBO())) {
				dto.setIconClassName(dbo.getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getIconClassName());
			}
			if(!Utils.isNullOrEmpty(dbo.getSysMenuDBO().getErpScreenConfigMastDBO())) {
				dto.setMasterTablename(dbo.getSysMenuDBO().getErpScreenConfigMastDBO().getMappedTableName());
			}
		}
		if(!Utils.isNullOrEmpty(dbo.getQuickLinkType())) {
			dto.setQuickLinkType(dbo.getQuickLinkType());
		}	
		return dto;
	}
	
//	public Mono<GUIUserViewPreferenceDTO> getUserMeuLog(String userId) {		
//		//	 guiTransaction.getUserMeuLog(Integer.valueOf(userId));
//			 return null;
//	}
}
