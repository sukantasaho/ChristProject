package com.christ.erp.services.controllers.common;

import com.christ.erp.services.common.*;
import com.christ.erp.services.dbqueries.common.CommonQueries;
import com.christ.erp.services.dto.common.*;

import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.handlers.common.ScreenConfigHandler;
import com.christ.utility.lib.Constants;
import com.christ.utility.lib.caching.CacheUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.*;

@RestController
@RequestMapping(value = "/Protected/ScreenConfig")
public class ScreenConfigApiController extends BaseApiController {
    private final String SP_GET_SCREEN_CONFIG;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenConfigApiController.class);    

    @Autowired
    ScreenConfigHandler screenConfigHandler;

    {
        SP_GET_SCREEN_CONFIG = "erp_sp_readonly_get_screen_config";
    }

    @RequestMapping(value = "/GetScreens", method = RequestMethod.POST)
    public Mono<List<ScreenInfoDTO>> getScreens(ServerHttpRequest request) {
        try {
        	LOGGER.info("entered into getScreens");
            /*Some lines are commented because screen information are storing in static variable,
            Adding new screen will not get reflected until restart. ---- n1^1n */

            /*return ServerCacheUtils.getScreenConfigDetails().flatMap(cachedScreens -> {
                if(cachedScreens == null || cachedScreens.isEmpty()) {*/
                    return DBGateway.executeQuery(SP_GET_SCREEN_CONFIG).map(table -> {
                    	LOGGER.info("after query execution from GetScreens");
                        Map<String, ScreenInfoDTO> screens = new HashMap<>();
                        if(table != null && table.getRows() != null) {
                            for(int i = 0; i < table.getRows().size(); i++) {
                                DataRow row = table.getRows().get(i);
                                String screen_id = row.getString(table.getColumnNames().indexOf("ScreenID"));
                                ScreenInfoDTO screenInfoDTO = null;
                                if(screens.containsKey(screen_id)) {
                                    screenInfoDTO = screens.get(screen_id);
                                } else {
                                    screenInfoDTO = new ScreenInfoDTO();
                                    screenInfoDTO.id = screen_id;
                                    screenInfoDTO.title = row.getString(table.getColumnNames().indexOf("ScreenTitle"));
                                    screenInfoDTO.fields = new ArrayList<>();
                                    screens.put(screen_id, screenInfoDTO);
                                }
                                screenInfoDTO.fields.add(new ScreenFieldInfoDTO(
                                        row.getString(table.getColumnNames().indexOf("ID")),
                                        row.getString(table.getColumnNames().indexOf("Text")),
                                        row.getBoolean(table.getColumnNames().indexOf("Required")),
                                        row.getInt(table.getColumnNames().indexOf("Length")),
                                        row.getInt(table.getColumnNames().indexOf("Width")),
                                        row.getString(table.getColumnNames().indexOf("Type")),
                                        row.getString(table.getColumnNames().indexOf("LoadBy"))
                                ));
                            }
                        }
                        List<ScreenInfoDTO> values = new ArrayList<>(screens.values());
                        try { ServerCacheUtils.loadScreenConfigDetails(values); }
                        catch(Exception ignored) { }
                        LOGGER.info("exit from getScreens method");
                        return values;
                       
                    });
               /* }
                else {
                    return Utils.monoFromObject(cachedScreens);
                }*/
            //});
        }
        catch(Exception ignored) { }
    	LOGGER.info("exit from getScreens, call from logger");
        return Utils.monoFromObject(new ArrayList<ScreenInfoDTO>());
    }

    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    public Mono<ApiResult> delete(@RequestParam("ScreenID") String screenId, @RequestParam("ID") String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            return this.buildDeleteQuery(screenId, id)
                    .flatMap(query ->  {
                        try {
                            return new QueryBuilder(false)
                                .addUpdateQuery(query)
                                .execute()
                                .flatMap(dataset -> {
                                    if(dataset != null && dataset.size() > 0) {
                                        result.success=true;
                                    }
                                    return Utils.monoFromObject(result);
                                });
                        }
                        catch(Exception ignored) { }
                        return Utils.monoFromObject(result);
                    });
        }
        catch(Exception ignored) { }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/Save", method = RequestMethod.POST)
    public Mono<ApiResult> save(@RequestParam("ScreenID") String screenId, @RequestParam("ID") String id, @RequestBody Map<String,Object> fields, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            return this.buildSaveQuery(screenId, id)
                    .flatMap(query ->  {
                        try {
                            /*if(userId == null || userId.trim().length() == 0) {
                                userId = "1";
                            }*/
                            return this.buildSaveQueryArgs(screenId, id, userId, fields).flatMap(args -> {
                                try {
                                    boolean isEdit = (id != null && id.trim().length() > 0);
                                    return new QueryBuilder(false)
                                            .addUpdateQuery(query, args)
                                            //.addSelectQuery(isEdit ? "SELECT 'Y' AS Status;" : "SELECT LAST_INSERT_ID() AS ID;")
                                            .execute()
                                            .flatMap(dataset -> {
                                                if(dataset != null && dataset.size() > 0
                                                      /*  && dataset.get(0).getRows() != null && dataset.get(0).getRows().size() > 0
                                                        && dataset.get(0).getColumnNames() != null && dataset.get(0).getColumnNames().size() > 0*/) {
                                                    result.success=true;
                                                }
                                                /*if(response != null && response.trim().length() > 0 && response.compareToIgnoreCase("Y") == 0 && isEdit == true) {
                                                    response = id;
                                                }*/
                                                return Utils.monoFromObject(result);
                                            });
                                }
                                catch(Exception ex) { }
                                return Utils.monoFromObject(result);
                            });
                        }
                        catch(Exception ignored) { }
                        return Utils.monoFromObject(result);
                    });
        }
        catch(Exception ignored) { }
        return Utils.monoFromObject(result);
    }
    @RequestMapping(value = "/Select", method = RequestMethod.POST)
    public Mono<Map<String,Object>> select(@RequestParam("ScreenID") String screenId, @RequestParam("ID") String id) {
        try {
            return this.buildSelectQuery(screenId)
                    .flatMap(query -> {
                        List<Object> args = new ArrayList<>();
                        args.add(id);
                        return DBGateway.executeWritableQuery(query, args).map(table -> {
                            Map<String, Object> fields = new HashMap<>();
                            if (table != null &&
                                    table.getRows() != null &&
                                    table.getRows().size() > 0 &&
                                    table.getColumnNames() != null &&
                                    table.getColumnNames().size() > 0) {
                                String fieldId;
                                Object fieldValue;
                                for (int i = 0; i < table.getColumnNames().size(); i++) {
                                    fieldId = table.getColumnNames().get(i);
                                    fieldValue = table.getRows().get(0).get(i);
                                    if(fieldId.compareToIgnoreCase("record_status") == 0) {
                                        fieldValue = (fieldValue.toString().compareToIgnoreCase("A") == 0);
                                    }
                                    fields.put(fieldId, fieldValue);
                                }
                            };
                            return fields;
                        });
                    });
        }
        catch(Exception ignored) { }
        return Utils.monoFromObject(new HashMap<>());
    }
    @RequestMapping(value = "/Search", method = RequestMethod.POST)
    public Mono<List<Map<String,Object>>> search(@RequestParam("ScreenID") String screenId) {
        try {
        	LOGGER.info("entered into Search method");
            return this.buildSearchQuery(screenId, null)
                    .flatMap(query -> {
                        return DBGateway.executeReadonlyQuery(query).map(table -> {
                            List<Map<String, Object>> items = new ArrayList<>();
                            if (table != null && table.getRows() != null) {
                                List<String> columns = table.getColumnNames();
                                List<DataRow> rows = table.getRows();
                                if (rows != null) {
                                    for (int i = 0; i < rows.size(); i++) {
                                        Map<String, Object> item = new HashMap<>();
                                        DataRow row = rows.get(i);
                                        String fieldId;
                                        Object fieldValue;
                                        for (int j = 0; j < row.size(); j++) {
                                            fieldId = columns.get(j);
                                            fieldValue = row.get(j);
                                            if(fieldId.compareToIgnoreCase("record_status") == 0) {
                                                fieldValue = (fieldValue.toString().compareToIgnoreCase("A") == 0);
                                            }
                                            item.put(fieldId, fieldValue);
                                        }
                                        items.add(item);
                                    }
                                }
                            }
                            LOGGER.info("exit wth success from Search method");
                            return items;
                        });
                    });
        }
        catch(Exception ignored) { }
        LOGGER.info("exit wth no data from Search method");
        return Utils.monoFromObject(new ArrayList<>());
    }

    @RequestMapping(value = "/LookUp", method = RequestMethod.POST)
    public Mono<List<LookupItemDTO>> lookup(@RequestParam("ScreenID") String screenId, @RequestParam("FilterBy") String filterBy, @RequestParam("FilterByID") String filterByID, @RequestParam("FieldTypeName") String fieldTypeName) {
        try {
            return this.buildLookupQuery(screenId, filterBy, fieldTypeName)
                    .flatMap(query -> {
                        List<LookupItemDTO> items = new ArrayList<>();
                        List<Object> args = new ArrayList<>();
                        if(filterBy != null && filterBy.trim().length() > 0) {
                            if(filterByID == null || filterByID.trim().length() == 0) {
                                args.add("0");
                            }
                            else { args.add(filterByID.trim()); }
                        }
                        return DBGateway.executeReadonlyQuery(query, args).map(table -> {
                            if (table != null && table.getRows() != null) {
                                List<DataRow> rows = table.getRows();
                                if (rows != null) {
                                    for (int i = 0; i < rows.size(); i++) {
                                        DataRow row = rows.get(i);
                                        items.add(new LookupItemDTO(
                                                row.get("value").toString(),
                                                row.get("label").toString(),
                                                row.get("status").toString().compareToIgnoreCase("Y") == 0));
                                    }
                                }
                            }
                            return items;
                        });
                    });
        }
        catch(Exception ignored) { }
        return Utils.monoFromObject(new ArrayList<>());
    }

    private Mono<String> buildLookupQuery(String screenId, String filterBy, String fieldTypeName) {
        try {
            return ServerCacheUtils.getScreenConfig(screenId)
                    .map(screen -> {
                        String idField = screen.fields.get(0).id;
                        String query = "SELECT "
                                + idField + " AS value, "
                                + fieldTypeName + " AS label, record_status AS status FROM "
                                + screen.id
                                + ((filterBy != null && filterBy.trim().length() > 0) ? (" WHERE " + filterBy + " = ? and record_status='A' ") : " WHERE record_status='A' ")
                                + " ORDER BY label ASC";
                        return query;
                    });
        }
        catch(Exception ex) { }
        return Utils.monoFromObject("");
    }
    private Mono<String> buildSelectQuery(String screenId) {
        try {
            return ServerCacheUtils.getScreenConfig(screenId)
                    .flatMap(screen -> this.buildSearchQuery(screenId, screen)
                            .map(query -> query + "AND MST." + screen.fields.get(0).id + " = ? \r\n"));
        }
        catch(Exception ex) { }
        return Utils.monoFromObject("");
    }
    private Mono<String> buildSearchQuery(String screenId, ScreenInfoDTO data) {
        try {
            Mono<ScreenInfoDTO> screenInfo = (data == null ? ServerCacheUtils.getScreenConfig(screenId) : Utils.monoFromObject(data));
            return screenInfo.map(screen -> {
                String query = "";
                try {
                    String query_columns = "";
                    String query_where = "";
                    if(screen != null && screen.fields != null) {
                        for(int i = 0; i < screen.fields.size(); i++) {
                            if(query_columns.trim().length() > 0) {
                                query_columns += ", ";
                            }
                            ScreenFieldInfoDTO field = screen.fields.get(i);
                            query_columns += " MST." + field.id;
                            if(field.type != null && (field.type).startsWith("LIST") == true) {
                                String[] parts = field.type.split("\\|");
                                query_columns += ", TB" + i + "." + parts[2];
                                query += " LEFT JOIN " + parts[1] + " AS TB" + i + " ON ";
                                query += " TB" + i + "." + field.id + " = MST." + field.id + " \r\n";
                            }
                        }
                        /*----- Audit Info -----*/
                        query_columns += ", USR.user_name AS modified_by, DATE_FORMAT(MST.modified_time, '%d %b %Y %r') AS modified_on";
                        query += "LEFT JOIN erp_users AS USR ON USR.erp_users_id = MST.modified_users_id \r\n";
                        query_where += "WHERE MST.record_status='A'";
                        /*----- Generate Query -----*/
                        query = "SELECT " + query_columns + " \r\n" + "FROM " + screen.id + " AS MST \r\n" + query + query_where;
                    }
                }
                catch(Exception ex) { }
                return query;
            });
        }
        catch(Exception ex) { }
        return Utils.monoFromObject("");
    }
    private Mono<String> buildSaveQuery(String screenId, String id) {
        try {
            return ServerCacheUtils.getScreenConfig(screenId).map(screen -> {
                String query = "";
                try {
                    if(id == null || id.trim().length() == 0) {
                        String values = "VALUES (";
                        query += "INSERT INTO " + screen.id + "(";
                        for(int i = 1; i < screen.fields.size(); i++) {
                            query += ((i > 1 ? "," : "") + screen.fields.get(i).id);
                            values += ((i > 1 ? "," : "") + "?");
                        }
                        query += ",modified_users_id,modified_time,created_users_id,created_time)";
                        values += ",?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP);";
                        query = query + values;
                    }
                    else {
                        query += "UPDATE " + screen.id + " SET ";
                        for(int i = 1; i < screen.fields.size(); i++) {
                            query += ((i > 1 ? "," : "") + screen.fields.get(i).id) + " = ?";
                        }
                        query += ",modified_users_id = ?,modified_time = NOW() WHERE " + screen.fields.get(0).id + " = ?;";
                    }
                }
                catch(Exception ex) { }
                return query;
            });
        }
        catch(Exception ex) { }
        return Utils.monoFromObject("");
    }
    private Mono<List<Object>> buildSaveQueryArgs(String screenId, String id, String userId, Map<String,Object> fields) {
        try {
            return ServerCacheUtils.getScreenConfig(screenId).map(screen -> {
                List<Object> args = new ArrayList<Object>();
                try {
                    for(int i = 1; i < screen.fields.size(); i++) {
                        String fieldId = screen.fields.get(i).id;
                        Object value = fields.get(fieldId);
                        if(fieldId.compareToIgnoreCase("record_status") == 0) {
                            boolean status = (boolean) value;
                            value = status ? "A" : "I";
                        }
                        args.add(value);
                    }
                    args.add(userId); //modified_user_id
                    /*----- Insert ID if it is in update mode -----*/
                    if(id != null && id.trim().length() > 0) {
                        args.add(id);
                    }
                    else {
                        args.add(userId); //created_user_id
                    }
                }
                catch(Exception ex) { }
                return args;
            });
        }
        catch(Exception ex) { }
        return Utils.monoFromObject(new ArrayList<Object>());
    }

    private Mono<String> buildDeleteQuery(String screenId, String id) {
        try {
            return ServerCacheUtils.getScreenConfig(screenId).map(screen -> {
                String query = "";
                try {
                    query += "UPDATE " + screen.id + " SET record_status='D' WHERE " + screen.fields.get(0).id + " = "+id;
                }
                catch(Exception ex) { }
                return query;
            });
        }
        catch(Exception ex) { }
        return Utils.monoFromObject("");
    }

    //@RequestMapping(value = "/getSystemMenus", method = RequestMethod.POST)
    public Mono<ApiResult<ArrayList<ModuleDTO>>> getSyStemMenus(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,@RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String preferredCampusIds) {
        //System.out.println(CacheUtils.instance.get(userId,Constants.HEADER_JWT_USER_NAME));
        //System.out.println((CacheUtils.instance.get("version")));
        //System.out.println(preferredCampusIds);
        ApiResult<ArrayList<ModuleDTO>> result = new ApiResult<ArrayList<ModuleDTO>>();
        Map<Integer,Map<Integer, Map<Integer, MenuScreenDTO>>> systemMenusMap = new HashMap<Integer, Map<Integer, Map<Integer, MenuScreenDTO>>>();
        Map<Integer,ModuleDTO> moduleInfoMap = new HashMap<Integer, ModuleDTO>();
        Map<Integer, ModuleSubDTO> moduleSubInfoMap = new HashMap<Integer, ModuleSubDTO>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    Query query = context.createNativeQuery(CommonQueries.GET_SYSTEM_MENUS, Tuple.class);
                    List<Tuple> list = query.getResultList();
                    if(!Utils.isNullOrEmpty(list)) {
                        list.forEach(tuple -> {
                            if(!Utils.isNullOrEmpty(tuple.get("erp_module_id")) && !Utils.isNullOrEmpty(tuple.get("erp_module_sub_id")) && !Utils.isNullOrEmpty(tuple.get("erp_menu_screen_id"))) {
                                if(systemMenusMap.containsKey(Integer.parseInt(tuple.get("erp_module_id").toString()))) {
                                    Map<Integer, Map<Integer, MenuScreenDTO>> subModuleMap = systemMenusMap.get(Integer.parseInt(tuple.get("erp_module_id").toString()));
                                    if(subModuleMap.containsKey(Integer.parseInt(tuple.get("erp_module_sub_id").toString()))) {
                                        Map<Integer, MenuScreenDTO> menuScreenMap = subModuleMap.get(Integer.parseInt(tuple.get("erp_module_sub_id").toString()));
                                        if(!menuScreenMap.containsKey(Integer.parseInt(tuple.get("erp_menu_screen_id").toString()))) {
                                            MenuScreenDTO menuSceenDetails = new MenuScreenDTO();
                                            menuSceenDetails.ID = tuple.get("erp_menu_screen_id").toString();
                                            menuSceenDetails.DisplayOrder = Integer.parseInt(tuple.get("menu_screen_display_order").toString());
                                            menuSceenDetails.Text = tuple.get("menu_screen_name").toString();
                                            if(!Utils.isNullOrEmpty(tuple.get("menu_component_path"))) {
                                                menuSceenDetails.ComponentPath = tuple.get("menu_component_path").toString();
                                            }
                                            if(!Utils.isNullOrEmpty(tuple.get("mapped_table_name"))) {
                                                menuSceenDetails.Screen = tuple.get("mapped_table_name").toString();
                                            }
//                                            if(!Utils.isNullOrEmpty(tuple.get("icon_class_name"))) {
//                                                menuSceenDetails.IconClassName = tuple.get("icon_class_name").toString();
//                                            }
                                            menuScreenMap.put(Integer.parseInt(tuple.get("erp_menu_screen_id").toString()),menuSceenDetails);
                                            subModuleMap.put(Integer.parseInt(tuple.get("erp_module_sub_id").toString()),menuScreenMap);
                                            systemMenusMap.put(Integer.parseInt(tuple.get("erp_module_id").toString()),subModuleMap);
                                        }
                                    }
                                    else {
                                        Map<Integer, MenuScreenDTO> menuScreenMap = new HashMap<Integer, MenuScreenDTO>();
                                        MenuScreenDTO menuSceenDetails = new MenuScreenDTO();
                                        menuSceenDetails.ID = tuple.get("erp_menu_screen_id").toString();
                                        menuSceenDetails.DisplayOrder = Integer.parseInt(tuple.get("menu_screen_display_order").toString());
                                        menuSceenDetails.Text = tuple.get("menu_screen_name").toString();
                                        if(!Utils.isNullOrEmpty(tuple.get("menu_component_path"))) {
                                            menuSceenDetails.ComponentPath = tuple.get("menu_component_path").toString();
                                        }
                                        if(!Utils.isNullOrEmpty(tuple.get("mapped_table_name"))) {
                                            menuSceenDetails.Screen = tuple.get("mapped_table_name").toString();
                                        }
//                                        if(!Utils.isNullOrEmpty(tuple.get("icon_class_name"))) {
//                                            menuSceenDetails.IconClassName = tuple.get("icon_class_name").toString();
//                                        }
                                        menuScreenMap.put(Integer.parseInt(tuple.get("erp_menu_screen_id").toString()),menuSceenDetails);
                                        subModuleMap.put(Integer.parseInt(tuple.get("erp_module_sub_id").toString()),menuScreenMap);
                                        systemMenusMap.put(Integer.parseInt(tuple.get("erp_module_id").toString()),subModuleMap);
                                    }
                                }
                                else {
                                    Map<Integer, Map<Integer, MenuScreenDTO>> subModuleMap = new HashMap<Integer, Map<Integer, MenuScreenDTO>>();
                                    Map<Integer, MenuScreenDTO> menuScreenMap = new HashMap<Integer, MenuScreenDTO>();
                                    MenuScreenDTO menuSceenDetails = new MenuScreenDTO();
                                    menuSceenDetails.ID = tuple.get("erp_menu_screen_id").toString();
                                    menuSceenDetails.DisplayOrder = Integer.parseInt(tuple.get("menu_screen_display_order").toString());
                                    menuSceenDetails.Text = tuple.get("menu_screen_name").toString();
                                    if(!Utils.isNullOrEmpty(tuple.get("menu_component_path"))) {
                                        menuSceenDetails.ComponentPath = tuple.get("menu_component_path").toString();
                                    }
                                    if(!Utils.isNullOrEmpty(tuple.get("mapped_table_name"))) {
                                        menuSceenDetails.Screen = tuple.get("mapped_table_name").toString();
                                    }
//                                    if(!Utils.isNullOrEmpty(tuple.get("icon_class_name"))) {
//                                        menuSceenDetails.IconClassName = tuple.get("icon_class_name").toString();
//                                    }
                                    menuScreenMap.put(Integer.parseInt(tuple.get("erp_menu_screen_id").toString()),menuSceenDetails);
                                    subModuleMap.put(Integer.parseInt(tuple.get("erp_module_sub_id").toString()),menuScreenMap);
                                    systemMenusMap.put(Integer.parseInt(tuple.get("erp_module_id").toString()),subModuleMap);
                                }
                                if(!moduleSubInfoMap.containsKey(Integer.parseInt(tuple.get("erp_module_sub_id").toString()))) {
                                    ModuleSubDTO moduleSubDetails = new ModuleSubDTO();
                                    moduleSubDetails.ID = tuple.get("erp_module_sub_id").toString();
                                    moduleSubDetails.Text = tuple.get("sub_module_name").toString();
                                    moduleSubDetails.DisplayOrder = Integer.parseInt(tuple.get("sub_module_display_order").toString());
//                                    if(!Utils.isNullOrEmpty(tuple.get("icon_class_name"))) {
//                                        moduleSubDetails.IconClassName = tuple.get("icon_class_name").toString();
//                                    }
                                    moduleSubInfoMap.put(Integer.parseInt(tuple.get("erp_module_sub_id").toString()),moduleSubDetails);
                                }
                                if(!moduleInfoMap.containsKey(Integer.parseInt(tuple.get("erp_module_id").toString()))) {
                                    ModuleDTO moduleDetails = new ModuleDTO();
                                    moduleDetails.id = tuple.get("erp_module_id").toString();
                                    moduleDetails.text = tuple.get("module_name").toString();
                                    moduleDetails.displayOrder = Integer.parseInt(tuple.get("module_display_order").toString());
                                    moduleInfoMap.put(Integer.parseInt(tuple.get("erp_module_id").toString()),moduleDetails);
                                }

                            }
                        });
                        ArrayList<ModuleDTO> moduleDetailsList = new ArrayList<ModuleDTO>();
                        systemMenusMap.forEach((moduleId,subModuleItems) -> {
                            ModuleDTO moduleDetails = moduleInfoMap.get(moduleId);
                            ArrayList<ModuleSubDTO> moduleSubDetailsList = new ArrayList<ModuleSubDTO>();
                            subModuleItems.forEach((moduleSubId,menuScreenItems)-> {
                                ModuleSubDTO moduleSubDetails = moduleSubInfoMap.get(moduleSubId);
                                ArrayList<MenuScreenDTO> menuScreenDetailsList = new ArrayList<MenuScreenDTO>();
                                menuScreenItems.forEach((menuScreenId,menuSceenDetails) -> {
                                    menuScreenDetailsList.add(menuSceenDetails);
                                });
                                Collections.sort(menuScreenDetailsList);
                                moduleSubDetails.Items = menuScreenDetailsList;
                                moduleSubDetailsList.add(moduleSubDetails);
                            });
                            Collections.sort(moduleSubDetailsList);
                            moduleDetails.menus = moduleSubDetailsList;
                            moduleDetailsList.add(moduleDetails);
                        });
                        Collections.sort(moduleDetailsList);
                        result.dto = moduleDetailsList;
                        result.success = true;
                    }
                }

                @Override
                public void onError(Exception error) {
                    result.success = false;
                    result.dto = null;
                    result.failureMessage = error.getMessage();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getSystemMenus", method = RequestMethod.POST)
    public Mono<List<ModuleDTO>>  getSystemMenus(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,@RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String preferredCampusIds) {
        return screenConfigHandler.getSystemMenus(userId,preferredCampusIds);
    }
    @PostMapping(value = "/saveUserCampus")
    public Mono<ResponseEntity<ApiResult>> saveUserCampus(@RequestBody Flux<ErpUsersCampusDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return screenConfigHandler.saveUserCampus(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/getUserCampus")
    public Flux<ErpUsersCampusDTO> getUserCampus(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return screenConfigHandler.getUserCampus(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping("/getErpNotification")
    public Flux<ErpNotificationsDTO> getERPNotification(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return screenConfigHandler.getERPNotification(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdateErpNotificationRead")
    public  Mono<ResponseEntity<ApiResult>> saveOrUpdateERPNotificationRead(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId, @RequestBody Mono<ErpNotificationsDTO> data){
    	return screenConfigHandler.saveOrUpdateERPNotificationRead(userId,data).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
