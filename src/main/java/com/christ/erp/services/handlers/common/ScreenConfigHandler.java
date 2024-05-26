package com.christ.erp.services.handlers.common;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpNotificationUsersReadDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dto.common.*;
import com.christ.erp.services.transactions.common.ScreenConfigTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.persistence.Tuple;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("rawtypes")
@Service
public class ScreenConfigHandler {
    @Autowired
    ScreenConfigTransaction screenConfigTransaction;
    
    
    public Mono<List<ModuleDTO>> getSystemMenus(String userId, String campusIds) {
        return screenConfigTransaction.getSystemMenus(userId,campusIds).flatMapMany(Flux::fromIterable)
                .filter(tuple -> !Utils.isNullOrEmpty(tuple.get("sys_menu_module_id")) && !Utils.isNullOrEmpty(tuple.get("sys_menu_module_sub_id")) && !Utils.isNullOrEmpty(tuple.get("sys_menu_id")))
                .collect(Collectors.groupingBy(s -> Integer.parseInt(s.get("sys_menu_module_id").toString()),
                        Collectors.groupingBy(r -> Integer.parseInt(r.get("sys_menu_module_sub_id").toString()), Collectors.groupingBy(r -> Integer.parseInt(r.get("sys_menu_id").toString()), Collectors.toList()))))
                .map(this::convertSystemMenuMapToDTO);
    }

    public List<ModuleDTO> convertSystemMenuMapToDTO(Map<Integer, Map<Integer, Map<Integer, List<Tuple>>>> map) {
        List<ModuleDTO> list = new ArrayList<>();
        map.forEach((module,subModules) -> {
            ModuleDTO moduleDTO = new ModuleDTO();
            List<ModuleSubDTO> subDTOS = new ArrayList<>();
            subModules.forEach((submodule,menuScreens) -> {
                ModuleSubDTO subDTO = new ModuleSubDTO();
                List<MenuScreenDTO> menuScreenDTOS = new ArrayList<>();
                menuScreens.forEach((menuScreenId,tupleList) -> {
                    Tuple tuple = tupleList.get(0);
                    if (Utils.isNullOrEmpty(moduleDTO.id)) {
                        moduleDTO.setId(tuple.get("sys_menu_module_id").toString());
                        moduleDTO.setText(tuple.get("module_name").toString());
                        moduleDTO.setDisplayOrder(Integer.valueOf(tuple.get("module_display_order").toString()));
                        if(!Utils.isNullOrEmpty(tuple.get("icon_class_name"))) {
                        	moduleDTO.setIconClassName(tuple.get("icon_class_name").toString());
                        }
                    }
                    if (Utils.isNullOrEmpty(subDTO.ID)) {
                        subDTO.setID(tuple.get("sys_menu_module_sub_id").toString());
                        subDTO.setText(tuple.get("sub_module_name").toString());
                        subDTO.setDisplayOrder(Integer.parseInt(tuple.get("sub_module_display_order").toString()));
//                        if(!Utils.isNullOrEmpty(tuple.get("icon_class_name"))) {
//                            subDTO.setIconClassName(tuple.get("icon_class_name").toString());
//                        }
                    }
                    MenuScreenDTO menuScreenDTO = new MenuScreenDTO();
                    menuScreenDTO.setID(tuple.get("sys_menu_id").toString());
                    menuScreenDTO.setText(tuple.get("menu_screen_name").toString());
                    menuScreenDTO.setDisplayOrder(Integer.parseInt(tuple.get("menu_screen_display_order").toString()));
                    if(!Utils.isNullOrEmpty(tuple.get("menu_component_path"))) {
                        menuScreenDTO.setComponentPath(tuple.get("menu_component_path").toString());
                    }
                    if(!Utils.isNullOrEmpty(tuple.get("mapped_table_name"))) {
                        menuScreenDTO.setScreen(tuple.get("mapped_table_name").toString());
                    }
					if(!Utils.isNullOrEmpty(tuple.get("quick_link_type")) && tuple.get("quick_link_type").toString().equalsIgnoreCase("Q")) {
						menuScreenDTO.setQuickMenu(true);
					}
//                    if(!Utils.isNullOrEmpty(tuple.get("icon_class_name"))) {
//                        menuScreenDTO.setIconClassName(tuple.get("icon_class_name").toString());
//                    }
                    menuScreenDTOS.add(menuScreenDTO);
                });

                menuScreenDTOS.sort(Comparator.comparing(MenuScreenDTO::getDisplayOrder));
                subDTO.Items = menuScreenDTOS;
                subDTOS.add(subDTO);
            });
            subDTOS.sort(Comparator.comparing(ModuleSubDTO::getDisplayOrder).thenComparing(ModuleSubDTO::getID));
            moduleDTO.menus = subDTOS;
            list.add(moduleDTO);
        });
        list.sort(Comparator.comparing(ModuleDTO::getDisplayOrder).thenComparing(ModuleDTO::getDisplayOrder));
        return list;
    }

    public Mono<Map<Integer,Map<String,Map<Integer, List<Tuple>>>>> getUserDataForRedis1(String userId) {
        return screenConfigTransaction.getSystemMenus(userId,null).flatMapMany(Flux::fromIterable)
                .filter(tuple -> !Utils.isNullOrEmpty(tuple.get("erp_users_id")) && !Utils.isNullOrEmpty(tuple.get("access_token")) && !Utils.isNullOrEmpty(tuple.get("erp_campus_id"))
                        && !Utils.isNullOrEmpty(tuple.get("allowed")) && tuple.get("allowed").toString().equals("1"))
                .collect(Collectors.groupingBy(t->Integer.parseInt(t.get("erp_users_id").toString()),
                        Collectors.groupingBy(s->s.get("access_token").toString().trim(),Collectors.groupingBy(s->Integer.parseInt(s.get("erp_campus_id").toString()), Collectors.toList()))));
    }

    public Flux<ErpUsersCampusDTO> getUserCampus(String userId) {
        return screenConfigTransaction.getUserDetails(userId).flatMapMany(Flux::fromIterable)
				.filter(tuple -> !Utils.isNullOrEmpty(tuple.get("erp_users_campus_id")) &&
						!Utils.isNullOrEmpty(tuple.get("erp_campus_id")))
				.map(this::convertDboToDto);
    }

    public ErpUsersCampusDTO convertDboToDto(Tuple tuple) {
        ErpUsersCampusDTO dto = new ErpUsersCampusDTO();
        dto.setId(Integer.parseInt(tuple.get("erp_users_campus_id").toString()));
        dto.setCampus(new SelectDTO());
        dto.getCampus().setValue(tuple.get("erp_campus_id").toString());
        if(!Utils.isNullOrEmpty(tuple.get("campus_name"))) {
			dto.getCampus().setLabel(tuple.get("campus_name").toString());
		}
        if(!Utils.isNullOrEmpty(tuple.get("is_preferred"))) {
			dto.setIsPreferred( tuple.get("is_preferred").toString().toLowerCase().matches("1|true"));
		}
		if(!Utils.isNullOrEmpty(tuple.get("short_name"))) {
			dto.setShortName(tuple.get("short_name").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("erp_users_name"))) {
			dto.setUserName(tuple.get("erp_users_name").toString());
		}
        return dto;
    }

	public Mono<ApiResult> saveUserCampus(Flux<ErpUsersCampusDTO> dto, String userId) {
        return dto
                .filter(erpUsersCampusDTO -> !Utils.isNullOrEmpty(erpUsersCampusDTO.isPreferred) && erpUsersCampusDTO.isPreferred)
                .map(erpUsersCampusDTO -> Integer.parseInt(erpUsersCampusDTO.getCampus().getValue())).collectList()
                .flatMap(preferredCampusIds -> screenConfigTransaction.getUserCampus(userId).map(erpUsersCampusDBOS -> {
                    erpUsersCampusDBOS.forEach(erpUsersCampusDBO -> {
                        if(preferredCampusIds.contains(erpUsersCampusDBO.getErpCampusDBO().getId())) {
                            erpUsersCampusDBO.setIsPreferred(true);
                        }else {
                            erpUsersCampusDBO.setIsPreferred(false);
                        }
                    });
                    return erpUsersCampusDBOS;
                })).map(screenConfigTransaction::saveUserCampus).map(Utils::responseResult);
    }


    public Mono<Map<String,Map<String,String>>> getUnAuthMessages() {
       return screenConfigTransaction.getUnAuthMessages().flatMapMany(Flux::fromIterable)
                .filter(tuple -> !Utils.isNullOrEmpty(tuple.get("erp_users_id")) && !Utils.isNullOrEmpty(tuple.get("access_token")) && !Utils.isNullOrEmpty(tuple.get("display_message")))
                .collect(Collectors.groupingBy(tuple -> tuple.get("erp_users_id").toString(),Collectors.toMap(tuple -> tuple.get("access_token").toString(),

                        tuple -> tuple.get("display_message").toString(),(function1, function2) -> {
                            System.out.println("duplicate key found!");
                            return function1;
                        })));
    }
    
    public Flux<ErpNotificationsDTO> getERPNotification(String userId) {
		List<Tuple> list = screenConfigTransaction.getERPNotification(userId);
		return convertDboToDto(list,userId);
	}

	private Flux<ErpNotificationsDTO> convertDboToDto(List<Tuple> list,String userId) {
		List<ErpNotificationsDTO> erpNotificationsDTOList = new ArrayList<ErpNotificationsDTO>();
		Map<Integer, Tuple> UMap = !Utils.isNullOrEmpty(list) ? list.stream()
				.filter(s -> s.get("Notification_type").toString().trim().equalsIgnoreCase("U") &&
						!Utils.isNullOrEmpty(s.get("erp_notifications_id")))
				.collect(Collectors.toMap(
						s -> Integer.parseInt(s.get("erp_notifications_id").toString()),
						s -> s)) : new HashMap<Integer, Tuple>();
		
		Map<Integer, Tuple> RMap = !Utils.isNullOrEmpty(list) ? list.stream()
				.filter(s -> s.get("Notification_type").toString().trim().equalsIgnoreCase("R") &&
						!Utils.isNullOrEmpty(s.get("erp_notifications_id")))
				.collect(Collectors.toMap(
						s -> Integer.parseInt(s.get("erp_notifications_id").toString()),
						s -> s)) : new HashMap<Integer, Tuple>();
		Map<String, Tuple> WMap = !Utils.isNullOrEmpty(list) ? list.stream()
				.filter(s -> s.get("Notification_type").toString().trim().equalsIgnoreCase("W") &&
						!Utils.isNullOrEmpty(s.get("code")))
				.collect(Collectors.toMap(
						s -> s.get("code").toString(),
						s -> s)) : new HashMap<String, Tuple>();
		List<String> processCodeList = !Utils.isNullOrEmpty(list) ? list.stream().filter(s -> !Utils.isNullOrEmpty(s.get("code")) && s.get("Notification_type").toString().trim().equalsIgnoreCase("W")).map(s-> s.get("code").toString()).collect(Collectors.toList()) : new ArrayList<String>();
		List<Integer> idList = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(tuple -> {
				if(!Utils.isNullOrEmpty(tuple.get("min_val")) && !Utils.isNullOrEmpty(tuple.get("max_val")) && tuple.get("Notification_type").toString().trim().equalsIgnoreCase("W")) {
					  IntStream.rangeClosed(Integer.parseInt(tuple.get("min_val").toString()), Integer.parseInt(tuple.get("max_val").toString())).forEach(data -> {
						  idList.add(data);
					  });
				}
			});
		}
		List<Tuple> erpWorkFlowNotificationList = !Utils.isNullOrEmpty(processCodeList) && !Utils.isNullOrEmpty(idList) ? screenConfigTransaction.getERPWorkFlowNotification(processCodeList,idList,userId) : new ArrayList<Tuple>();
		Map<String, List<Tuple>> erpWorkFlowNotificationMap = !Utils.isNullOrEmpty(erpWorkFlowNotificationList)
		        ? erpWorkFlowNotificationList.stream()
		                .collect(Collectors.groupingBy(s -> s.get("process_code").toString()))
		        : new HashMap<String, List<Tuple>>();		
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				ErpNotificationsDTO erpNotificationsDTO = null;
				if(!Utils.isNullOrEmpty(UMap) && !Utils.isNullOrEmpty(data.get("erp_notifications_id")) && UMap.containsKey(Integer.parseInt(data.get("erp_notifications_id").toString()))) {
					Tuple tuple = UMap.get(Integer.parseInt(data.get("erp_notifications_id").toString()));
					erpNotificationsDTO = new ErpNotificationsDTO();
					if(!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(tuple.get("erp_notifications_id")))){
						if(!Utils.isNullOrEmpty(tuple.get("erp_notifications_id").toString())) {
							erpNotificationsDTO.setId(Integer.parseInt(tuple.get("erp_notifications_id").toString()));
						}
					}
					erpNotificationsDTO.setNotificationType(!Utils.isNullOrEmpty(tuple.get("Notification_type")) ? tuple.get("Notification_type").toString() : null);
					erpNotificationsDTO.setErpNotificationUserEntriesId(!Utils.isNullOrEmpty(tuple.get("erp_notification_user_entries_id")) ? Integer.parseInt(tuple.get("erp_notification_user_entries_id").toString()) : null);
					erpNotificationsDTO.setCount(!Utils.isNullOrEmpty(tuple.get("cnt")) ? Integer.parseInt(tuple.get("cnt").toString()) : null);
					if(!Utils.isNullOrEmpty(tuple.get("notification_content"))){
						String str = screenConfigTransaction.getBlobObjectToString(tuple.get("notification_content"));
						erpNotificationsDTO.setNotificationContent(!Utils.isNullOrEmpty(str) ? str : null);
					}
					if(!Utils.isNullOrEmpty(tuple.get("is_notification_seen")))
						erpNotificationsDTO.setNotificationSeen(true);
					else
						erpNotificationsDTO.setNotificationSeen(false);
					if(!Utils.isNullOrEmpty(tuple.get("notification_component_path"))){
						String str = screenConfigTransaction.getBlobObjectToString(tuple.get("notification_component_path"));
						erpNotificationsDTO.setNotificationComponentPath(!Utils.isNullOrEmpty(str) ? str : null);
					}
					erpNotificationsDTO.setNotificationDescription(!Utils.isNullOrEmpty(tuple.get("notification_description")) ? tuple.get("notification_description").toString() : null);
					erpNotificationsDTOList.add(erpNotificationsDTO);
				}
				if(!Utils.isNullOrEmpty(RMap) && !Utils.isNullOrEmpty(data.get("erp_notifications_id")) && RMap.containsKey(Integer.parseInt(data.get("erp_notifications_id").toString()))) {
					Tuple tuple = RMap.get(Integer.parseInt(data.get("erp_notifications_id").toString()));
					erpNotificationsDTO = new ErpNotificationsDTO();
					if(!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(tuple.get("erp_notifications_id")))){
						if(!Utils.isNullOrEmpty(tuple.get("erp_notifications_id").toString())) {
							erpNotificationsDTO.setId(Integer.parseInt(tuple.get("erp_notifications_id").toString()));
						}
					}
					erpNotificationsDTO.setNotificationType(!Utils.isNullOrEmpty(tuple.get("Notification_type")) ? tuple.get("Notification_type").toString() : null);
					erpNotificationsDTO.setErpNotificationUserEntriesId(!Utils.isNullOrEmpty(tuple.get("erp_notification_user_entries_id")) ? Integer.parseInt(tuple.get("erp_notification_user_entries_id").toString()) : null);
					erpNotificationsDTO.setCount(!Utils.isNullOrEmpty(tuple.get("cnt")) ? Integer.parseInt(tuple.get("cnt").toString()) : null);
					if(!Utils.isNullOrEmpty(tuple.get("notification_content"))){
						String str = screenConfigTransaction.getBlobObjectToString(tuple.get("notification_content"));
						erpNotificationsDTO.setNotificationContent(!Utils.isNullOrEmpty(str) ? str : null);
					}					
					if(!Utils.isNullOrEmpty(tuple.get("is_notification_seen")))
						erpNotificationsDTO.setNotificationSeen(true);
					else
						erpNotificationsDTO.setNotificationSeen(false);
					if(!Utils.isNullOrEmpty(tuple.get("notification_component_path"))){
						String str = screenConfigTransaction.getBlobObjectToString(tuple.get("notification_component_path"));
						erpNotificationsDTO.setNotificationComponentPath(!Utils.isNullOrEmpty(str) ? str : null);
					}				
					erpNotificationsDTO.setNotificationDescription(!Utils.isNullOrEmpty(tuple.get("notification_description")) ? tuple.get("notification_description").toString() : null);
					erpNotificationsDTOList.add(erpNotificationsDTO);
				}
				if(!Utils.isNullOrEmpty(erpWorkFlowNotificationMap) && erpWorkFlowNotificationMap.containsKey(data.get("code").toString()) && !Utils.isNullOrEmpty(WMap) && WMap.containsKey(data.get("code").toString())) {
					Tuple tuple = WMap.get(data.get("code").toString());
					List<ErpNotificationsDTO> erpNotificationsDTOs = new ArrayList<ErpNotificationsDTO>();
					ErpNotificationsDTO erpNotificationsDTO2 = new ErpNotificationsDTO();
					List<Tuple> tupleList = erpWorkFlowNotificationMap.get(data.get("code").toString());
					if(!Utils.isNullOrEmpty(tupleList) && !Utils.isNullOrEmpty(tuple)) {
						tupleList.forEach(data1 -> {
							if(!Utils.isNullOrEmpty(data.get("notification_content"))){
								String str = screenConfigTransaction.getBlobObjectToString(data.get("notification_content"));
								if(!Utils.isNullOrEmpty(tuple.get("cnt")) && !Utils.isNullOrEmpty(str)) {
									Integer count =  Integer.parseInt(tuple.get("cnt").toString());
									erpNotificationsDTO2.setMessage(str+ " ("+count+")");
								}
							}
							erpNotificationsDTO2.setNotificationType(!Utils.isNullOrEmpty(data1.get("Notification_type")) ? data1.get("Notification_type").toString() : null);
							ErpNotificationsDTO erpNotificationsDTO1 = new ErpNotificationsDTO();
							if(!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(data1.get("W_U_R_id")))){
								if(!Utils.isNullOrEmpty(String.valueOf(data1.get("W_U_R_id")))) {
									erpNotificationsDTO1.setId(Integer.parseInt(data1.get("W_U_R_id").toString()));
								}
							}						
							erpNotificationsDTO1.setNotificationType(!Utils.isNullOrEmpty(data1.get("Notification_type")) ? data1.get("Notification_type").toString() : null);
							if(!Utils.isNullOrEmpty(data1.get("notification_content"))){
								String str = screenConfigTransaction.getBlobObjectToString(data1.get("notification_content"));
								erpNotificationsDTO1.setNotificationContent(!Utils.isNullOrEmpty(str) ? str : null);
							}			
							if(!Utils.isNullOrEmpty(data1.get("is_notification_seen")))
								erpNotificationsDTO1.setNotificationSeen(true);
							else
								erpNotificationsDTO1.setNotificationSeen(false);
							if(!Utils.isNullOrEmpty(data1.get("notification_hyperlink"))){
								String str = screenConfigTransaction.getBlobObjectToString(data1.get("notification_hyperlink"));
								erpNotificationsDTO1.setNotificationComponentPath(!Utils.isNullOrEmpty(str) ? str : null);
							}
							erpNotificationsDTO1.setNotificationDescription(!Utils.isNullOrEmpty(data1.get("notification_description")) ? data1.get("notification_description").toString() : null);
							erpNotificationsDTOs.add(erpNotificationsDTO1);
							erpNotificationsDTO2.setErpNotificationsDTOList(erpNotificationsDTOs);
						});
						erpNotificationsDTOList.add(erpNotificationsDTO2);	
					}
				}
			});
		}
		return Flux.fromIterable(erpNotificationsDTOList);
	}

	@SuppressWarnings("unchecked")
	public Mono<ApiResult> saveOrUpdateERPNotificationRead(String userId,Mono<ErpNotificationsDTO> data) {
		return data.handle((erpNotificationsDTO, synchronousSink) -> {
			synchronousSink.next(erpNotificationsDTO);
		}).cast(ErpNotificationsDTO.class)
				.map(data1 ->  convertDtoToDbo(data1,userId))
				.flatMap( s ->{ 
						screenConfigTransaction.saveOrUpdate(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult); 
	}

	private ErpNotificationUsersReadDBO convertDtoToDbo(ErpNotificationsDTO dto, String userId) {
		ErpNotificationUsersReadDBO erpNotificationUsersReadDBO = screenConfigTransaction.getData(dto.getId());
		if(!Utils.isNullOrEmpty(erpNotificationUsersReadDBO)) {
			erpNotificationUsersReadDBO.setModifiedUsersId(Integer.parseInt(userId));
		}else {
			erpNotificationUsersReadDBO = new ErpNotificationUsersReadDBO();
			erpNotificationUsersReadDBO.setCreatedUsersId(Integer.parseInt(userId));
			erpNotificationUsersReadDBO.setRecordStatus('A');
		}
		if(!Utils.isNullOrEmpty(userId)) {
			ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
			erpUsersDBO.setId(Integer.parseInt(userId));
			erpNotificationUsersReadDBO.setErpUsersDBO(erpUsersDBO);
		}
		if(!Utils.isNullOrEmpty(dto.getId())) {
			ErpNotificationsDBO erpNotificationsDBO = new ErpNotificationsDBO();
			erpNotificationsDBO.setId(dto.getId());
			erpNotificationUsersReadDBO.setErpNotificationsDBO(erpNotificationsDBO);
		}
		if(!Utils.isNullOrEmpty(dto.isNotificationSeen())) {
			erpNotificationUsersReadDBO.setNotificationSeen(dto.isNotificationSeen());
		}
		return erpNotificationUsersReadDBO;
	}
}
