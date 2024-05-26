/*
package com.christ.erp.services.controllers.employee;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpDailyWageSlabDBO;
import com.christ.erp.services.dbobjects.employee.salary.PayScaleMappingDBO;
import com.christ.erp.services.dbobjects.employee.salary.PayScaleMappingItemDBO;
import com.christ.erp.services.dbqueries.employee.SalaryQueries;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDTO;
import com.christ.erp.services.dto.employee.salary.PayScaleMappingDTO;
import com.christ.erp.services.dto.employee.salary.PayScaleMappingItemDTO;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/Secured/Employee/Salary")
public class SalaryController extends BaseApiController {
    @RequestMapping(value = "/GetAllMapping", method = RequestMethod.POST)
    public Mono<ApiResult<List<PayScaleMappingDTO>>> getAllMapping() {
        ApiResult<List<PayScaleMappingDTO>> result = new ApiResult<List<PayScaleMappingDTO>>();
        try {
            return DBGateway.executeQuery(SalaryQueries.PAY_SCALE_MAPPING_SEARCH_ALL, null, true).flatMap(items -> {
                if(items != null && items.isEmpty() == false) {
                    result.success = true;
                    result.dto = new ArrayList<>();
                    for(int i = 0; i < items.getRows().size(); i++) {
                        PayScaleMappingDTO mappingInfo = new PayScaleMappingDTO();
                        mappingInfo.id = items.getValue(i, "ID").toString();
                        mappingInfo.revisedYear = items.getValue(i, "RevisedYear").toString();
                        mappingInfo.category = new ExModelBaseDTO();
                        mappingInfo.category.text = items.getValue(i, "Category").toString();
                        mappingInfo.grade = new ExModelBaseDTO();
                        mappingInfo.grade.text = items.getValue(i, "Grade").toString();
                        result.dto.add(mappingInfo);
                    }
                }
                return Utils.monoFromObject(result);
            });
        }
        catch(Exception ignored) { }
        return Utils.monoFromObject(result);
    }
    @RequestMapping(value = "/SaveMapping", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveMapping(@RequestBody PayScaleMappingDTO data) {
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            for (PayScaleMappingItemDTO levelInfo: data.levels) {
                levelInfo.id = Utils.setDefaultIfWhitespace(levelInfo.id, "0");
                levelInfo.order = Utils.setDefaultIfWhitespace(levelInfo.order, "0");
            }
            List<Object> args = new ArrayList<>();
            args.add(Integer.parseInt(Utils.setDefaultIfWhitespace(data.id, "0")));
            args.add(Integer.parseInt(Utils.setDefaultIfWhitespace(data.revisedYear, "0")));
            args.add(Integer.parseInt(Utils.setDefaultIfWhitespace(data.category.id, "0")));
            args.add(Integer.parseInt(Utils.setDefaultIfWhitespace(data.grade.id, "0")));
            args.add(new Gson().toJson(data.levels));
            return DBGateway.executeQuery(SalaryQueries.PAY_SCALE_MAPPING_SAVE_OR_UPDATE, args, false).flatMap(table -> {
                if(table != null && table.isEmpty() == false) {
                    try {
                        String id = table.getValue(0,0).toString();
                        if(Utils.isNullOrWhitespace(id) == false) {
                            result.success = true;
                            result.dto = new ModelBaseDTO();
                            result.dto.id = id;
                        }
                    }
                    catch(Exception ignored) { }
                }
                return Utils.monoFromObject(result);
            });
        }
        catch(Exception ignored) { }
        return Utils.monoFromObject(result);
    }
    @RequestMapping(value = "/SelectMapping", method = RequestMethod.POST)
    public Mono<ApiResult<PayScaleMappingDTO>> selectMapping(@RequestParam("ID") String id) {
        ApiResult<PayScaleMappingDTO> result = new ApiResult<PayScaleMappingDTO>();
        try {
            List<Object> args = new ArrayList<>();
            args.add(Integer.parseInt(Utils.setDefaultIfWhitespace(id, "0")));
            return DBGateway.executeQuery(SalaryQueries.PAY_SCALE_MAPPING_SELECT_HEADER, args, true).flatMap(header -> {
                if(header != null && header.isEmpty() == false) {
                    result.success = true;
                    result.dto = new PayScaleMappingDTO();
                    result.dto.id = header.getValue(0, "ID").toString();
                    result.dto.revisedYear = header.getValue(0, "RevisedYear").toString();
                    result.dto.category = new ExModelBaseDTO();
                    result.dto.category.id = header.getValue(0, "Category").toString();
                    result.dto.grade = new ExModelBaseDTO();
                    result.dto.grade.id = header.getValue(0, "Grade").toString();

                    return DBGateway.executeQuery(SalaryQueries.PAY_SCALE_MAPPING_SELECT_DETAIL, args, true).flatMap(detail -> {
                        if (detail != null && detail.isEmpty() == false) {
                            result.dto.levels = new ArrayList<>();
                            for(int i = 0; i < detail.getRows().size(); i++) {
                                PayScaleMappingItemDTO levelInfo = new PayScaleMappingItemDTO();
                                levelInfo.id = detail.getValue(i, "ID").toString();
                                levelInfo.level = detail.getValue(i, "Level").toString();
                                levelInfo.scale = detail.getValue(i, "Scale").toString();
                                levelInfo.order = detail.getValue(i, "Order").toString();
                                result.dto.levels.add(levelInfo);
                            }
                        }
                        return Utils.monoFromObject(result);
                    });
                }
                else {
                    return Utils.monoFromObject(result);
                }
            });
        }
        catch(Exception ignored) { }
        return Utils.monoFromObject(result);
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/ORM/GetAllMapping", method = RequestMethod.POST)
    public Mono<ApiResult<List<PayScaleMappingDTO>>> getAllMappingORM() {
        ApiResult<List<PayScaleMappingDTO>> result = new ApiResult<List<PayScaleMappingDTO>>();
        EntityManager jpaManager = this.getEntityManagerFactory().createEntityManager();
        try {
        	Query query = jpaManager.createNativeQuery(SalaryQueries.PAY_SCALE_MAPPING_SEARCH_ALL, Tuple.class);
        	List<Tuple> mappings = query.getResultList();
        	if(mappings != null && mappings.size() > 0) {
	        	result.success = true;
	            result.dto = new ArrayList<>();
	            for(Tuple mapping : mappings) {
	                PayScaleMappingDTO mappingInfo = new PayScaleMappingDTO();
	                mappingInfo.id = mapping.get("ID").toString();
	                mappingInfo.revisedYear = mapping.get("RevisedYear").toString();
	                mappingInfo.category = new ExModelBaseDTO();
	                mappingInfo.category.text = mapping.get("Category").toString();
	                mappingInfo.grade = new ExModelBaseDTO();
	                mappingInfo.grade.text = mapping.get("Grade").toString();
	                result.dto.add(mappingInfo);
	            }
        	}
        }
        catch(Exception ignored) { }
        finally {
        	jpaManager.close();
		}
        return Utils.monoFromObject(result);
    }
    @RequestMapping(value = "/ORM/SaveMapping", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveMappingORM(@RequestBody PayScaleMappingDTO data) {
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        EntityManager jpaManager = this.getEntityManagerFactory().createEntityManager();
        jpaManager.getTransaction().begin();
        try {
            if(data != null) {
            	PayScaleMappingDBO header = null;
            	if(Utils.isNullOrWhitespace(data.id) == false) {
            		header = jpaManager.find(PayScaleMappingDBO.class, Integer.parseInt(data.id));
            	}
            	if(header == null) {
            		header = new PayScaleMappingDBO();
            	}
            	header.grade = Integer.parseInt(data.grade.id);
        		//header.category = Integer.parseInt(data.category.id);
        		header.revisedYear = Integer.parseInt(data.revisedYear);
        		        		
            	if(header.id == null) { jpaManager.persist(header); }
            	else { jpaManager.merge(header); }
            	
            	if(header.id != 0) {
            		List<Integer> detailIds = new ArrayList<>();
	            	for(PayScaleMappingItemDTO item : data.levels) {
	        			PayScaleMappingItemDBO detail = null;
	        			if(Utils.isNullOrWhitespace(item.id) == false) {
	        				detail = jpaManager.find(PayScaleMappingItemDBO.class, Integer.parseInt(item.id));
	                	}
	        			if(detail == null) {
	        				detail = new PayScaleMappingItemDBO();
	        			}
	        			detail.level = item.level;
	        			detail.order = Integer.parseInt(item.order);
	        			detail.scale = item.scale;
	        			detail.mapping = header;  
	        			
	        			if(detail.id == null) { jpaManager.persist(detail); }
	                	else { jpaManager.merge(detail); }
	        			detailIds.add(detail.id);
	        		}
	            	
	            	Query query = jpaManager.createNativeQuery(SalaryQueries.PAY_SCALE_MAPPING_DELETE_DETAIL);
	            	query.setParameter("header_id", header.id);
	            	query.setParameter("detail_ids", detailIds);
	            	query.executeUpdate();	
	            	
	            	result.dto = new ModelBaseDTO();
	            	result.dto.id = header.id.toString();
            	}
            }
            jpaManager.getTransaction().commit();
        }
        catch(Exception ignored) { 
        	try { jpaManager.getTransaction().rollback(); }
        	catch(Exception error) { }
        }
        finally {
        	try { jpaManager.close(); } 
        	catch(Exception error) { }        	
		}
        return Utils.monoFromObject(result);
    }
    @RequestMapping(value = "/ORM/SelectMapping", method = RequestMethod.POST)
    public Mono<ApiResult<PayScaleMappingDTO>> selectMappingORM(@RequestParam("ID") String id) {
        ApiResult<PayScaleMappingDTO> result = new ApiResult<PayScaleMappingDTO>();
        EntityManager jpaManager = this.getEntityManagerFactory().createEntityManager();
        jpaManager.getTransaction().begin();
        try {
        	PayScaleMappingDBO dbPayScaleMapping11Info = jpaManager.find(PayScaleMappingDBO.class, Integer.parseInt(id));
            if(dbPayScaleMapping11Info != null) {
            	result.dto = new PayScaleMappingDTO();
            	result.dto.id = dbPayScaleMapping11Info.id.toString();
            	result.dto.revisedYear = dbPayScaleMapping11Info.revisedYear.toString();
            	result.dto.category = new ExModelBaseDTO();
            	//result.dto.category.id = dbPayScaleMapping11Info.category.toString();
            	result.dto.grade = new ExModelBaseDTO();
            	result.dto.grade.id = dbPayScaleMapping11Info.grade.toString();
            	result.dto.levels = new ArrayList<>();
            	if(dbPayScaleMapping11Info.levels != null && dbPayScaleMapping11Info.levels.size() > 0) {
            		for(PayScaleMappingItemDBO item : dbPayScaleMapping11Info.levels) {
                		PayScaleMappingItemDTO levelInfo = new PayScaleMappingItemDTO();
                		levelInfo.id = item.id.toString();
                		levelInfo.scale = item.scale.toString();
                		levelInfo.level = item.level.toString();
                		levelInfo.order = item.order.toString();
                		result.dto.levels.add(levelInfo);
                	}
            	}        		           	
            }
            jpaManager.getTransaction().commit();
        }
        catch(Exception ignored) { 
        	try { jpaManager.getTransaction().rollback(); }
        	catch(Exception error) { }
        }
        finally {        	
        	try { jpaManager.close(); } 
        	catch(Exception error) { } 
		}
        return Utils.monoFromObject(result);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/JPA/GetAllMapping", method = RequestMethod.POST)
    public Mono<ApiResult<List<PayScaleMappingDTO>>> getAllMappingJPA() {
        ApiResult<List<PayScaleMappingDTO>> result = new ApiResult<List<PayScaleMappingDTO>>();
        DBGateway.runJPA(new ITransactional() {
            @Override
            public void onRun(EntityManager context) {
                Query query = context.createNativeQuery(SalaryQueries.PAY_SCALE_MAPPING_SEARCH_ALL, Tuple.class);
                List<Tuple> mappings = query.getResultList();
                if(mappings != null && mappings.size() > 0) {
                    result.success = true;
                    result.dto = new ArrayList<>();
                    for(Tuple mapping : mappings) {
                        PayScaleMappingDTO mappingInfo = new PayScaleMappingDTO();
                        mappingInfo.id = mapping.get("ID").toString();
                        mappingInfo.revisedYear = mapping.get("RevisedYear").toString();
                        mappingInfo.category = new ExModelBaseDTO();
                        mappingInfo.category.text = mapping.get("Category").toString();
                        mappingInfo.grade = new ExModelBaseDTO();
                        mappingInfo.grade.text = mapping.get("Grade").toString();
                        result.dto.add(mappingInfo);
                    }
                }
                result.success = true;
            }
            @Override
            public void onError(Exception error) {
                result.success = false;
                result.dto = null;
                result.failureMessage = error.getMessage();
            }
        }, true);
        return Utils.monoFromObject(result);
    }
    @RequestMapping(value = "/JPA/SaveMapping", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveMappingJPA(@RequestBody PayScaleMappingDTO data) {
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
        	return getUserID().flatMap(userId -> {
        		if(data != null) {
                    DBGateway.runJPA(new ITransactional() {
                        @Override
                        public void onRun(EntityManager context) {
                            PayScaleMappingDBO header = null;
                            if(Utils.isNullOrWhitespace(data.id) == false) {
                                header = context.find(PayScaleMappingDBO.class, Integer.parseInt(data.id));
                            }
                            if(header == null) {
                                header = new PayScaleMappingDBO();
                                header.createdUserId = Integer.parseInt(userId);
                            }
                            header.grade = Integer.parseInt(data.grade.id);
                            //header.category = Integer.parseInt(data.category.id);
                            header.revisedYear = Integer.parseInt(data.revisedYear);

                            if(header.id == null) {
                            	context.persist(header); 
                            } else { 
                            	header.modifiedUserId = Integer.parseInt(userId);
                            	context.merge(header); 
                            }

                            if(header.id != 0) {
                                List<Integer> detailIds = new ArrayList<>();
                                for(PayScaleMappingItemDTO item : data.levels) {
                                    PayScaleMappingItemDBO detail = null;
                                    if(Utils.isNullOrWhitespace(item.id) == false) {
                                        detail = context.find(PayScaleMappingItemDBO.class, Integer.parseInt(item.id));
                                    }
                                    if(detail == null) {
                                        detail = new PayScaleMappingItemDBO();
                                        detail.createdUserId = Integer.parseInt(userId);
                                    }
                                    detail.level = item.level;
                                    detail.order = Integer.parseInt(item.order);
                                    detail.scale = item.scale;
                                    detail.mapping = header;

                                    if(detail.id == null) {
                                    	context.persist(detail); 
                                    } else { 
                                    	detail.modifiedUserId = Integer.parseInt(userId);
                                    	context.merge(detail);
                                    }
                                    detailIds.add(detail.id);
                                }

                                Query query = context.createNativeQuery(SalaryQueries.PAY_SCALE_MAPPING_DELETE_DETAIL);
                                query.setParameter("header_id", header.id);
                                query.setParameter("detail_ids", detailIds);
                                query.executeUpdate();

                                result.success = true;
                                result.dto = new ModelBaseDTO();
                                result.dto.id = header.id.toString();
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
                return Utils.monoFromObject(result);
        	});
        } catch (Exception e) {
			
		}
        return Utils.monoFromObject(result);
    }
    @RequestMapping(value = "/JPA/SelectMapping", method = RequestMethod.POST)
    public Mono<ApiResult<PayScaleMappingDTO>> selectMappingJPA(@RequestParam("id") String id) {
        ApiResult<PayScaleMappingDTO> result = new ApiResult<PayScaleMappingDTO>();
        DBGateway.runJPA(new ITransactional() {
            @Override
            public void onRun(EntityManager context) {
                PayScaleMappingDBO dbPayScaleMapping11Info = context.find(PayScaleMappingDBO.class, Integer.parseInt(id));
                if(dbPayScaleMapping11Info != null) {
                    result.success = true;
                    result.dto = new PayScaleMappingDTO();
                    result.dto.id = dbPayScaleMapping11Info.id.toString();
                    result.dto.revisedYear = dbPayScaleMapping11Info.revisedYear.toString();
                    result.dto.category = new ExModelBaseDTO();
                    //result.dto.category.id = dbPayScaleMapping11Info.category.toString();
                    result.dto.grade = new ExModelBaseDTO();
                    result.dto.grade.id = dbPayScaleMapping11Info.grade.toString();
                    result.dto.levels = new ArrayList<>();
                    if(dbPayScaleMapping11Info.levels != null && dbPayScaleMapping11Info.levels.size() > 0) {
                        for(PayScaleMappingItemDBO item : dbPayScaleMapping11Info.levels) {
                            PayScaleMappingItemDTO levelInfo = new PayScaleMappingItemDTO();
                            levelInfo.id = item.id.toString();
                            levelInfo.scale = item.scale.toString();
                            levelInfo.level = item.level.toString();
                            levelInfo.order = item.order.toString();
                            result.dto.levels.add(levelInfo);
                        }
                    }
                }
            }
            @Override
            public void onError(Exception error) {
                result.success = false;
                result.dto = null;
                result.failureMessage = error.getMessage();
            }
        }, true);
        return Utils.monoFromObject(result);
    }

}
*/
