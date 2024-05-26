package com.christ.erp.services.handlers.employee.salary;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDetailDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleMatrixDetailDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleGradeMappingDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleGradeMappingDetailDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleMatrixDetailDTO;
import com.christ.erp.services.transactions.employee.salary.PayScaleMatrixTransaction;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import javax.persistence.Tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class PayScaleMatrixHandler {

    private static volatile PayScaleMatrixHandler payScaleMatrixHandler = null;
    PayScaleMatrixTransaction payScaleMatrixTransaction = PayScaleMatrixTransaction.getInstance();

    public static PayScaleMatrixHandler getInstance() {
        if(payScaleMatrixHandler==null) {
            payScaleMatrixHandler = new PayScaleMatrixHandler();
        }
        return payScaleMatrixHandler;
    }

    public EmpPayScaleGradeMappingDTO getMatrixData(Map<String,String> data) throws Exception {
        List<Tuple> list = payScaleMatrixTransaction.getPayScaleMappingData(data.get("payScaleGradeId"),data.get("revisedYear"));
        if(!Utils.isNullOrEmpty(list)) {
            EmpPayScaleGradeMappingDTO matrix = new EmpPayScaleGradeMappingDTO();
            ArrayList<EmpPayScaleGradeMappingDetailDTO> payScaleLevels = new ArrayList<>();
            ArrayList<EmpPayScaleGradeMappingDetailDTO> counterList = new ArrayList<>();
            matrix.levels = new ArrayList<>();
            //Edit Start
            matrix.revisedYear = new ExModelBaseDTO();
            matrix.revisedYear.id = data.get("revisedYear");
            matrix.grade = new ExModelBaseDTO();
            matrix.grade.id = data.get("payScaleGradeId");
            if(!Utils.isNullOrEmpty(data.get("categoryId"))) {
                matrix.category = new ExModelBaseDTO();
                matrix.category.id = data.get("categoryId");
            }
            if(!Utils.isNullOrEmpty(data.get("payScaleGradeName"))) {
                matrix.grade.text = data.get("payScaleGradeName");
            }
            //Edit End
            list.forEach(tuple-> {
                EmpPayScaleGradeMappingDetailDTO levelDTO = new EmpPayScaleGradeMappingDetailDTO();
                levelDTO.payScale = tuple.get("pay_scale").toString();                
                levelDTO.payScaleLevel = tuple.get("emp_pay_scale_level").toString(); // sambath
         //       levelDTO.payScaleLevel = tuple.get("pay_scale_level").toString(); // sambath
                levelDTO.id = tuple.get("emp_pay_scale_grade_mapping_detail_id").toString();
                levelDTO.displayOrder = tuple.get("pay_scale_display_order").toString();
                payScaleLevels.add(levelDTO);
            });
            matrix.levels = payScaleLevels;
            int toNo = 20;
            List<Tuple> matrixDataList = payScaleMatrixTransaction.getMatrixDataList(data.get("payScaleGradeId"),data.get("revisedYear"));
            Map<Integer, Map<Integer, Tuple2<Integer,String>>> matrixDataMap = new HashMap<>();
            if(!Utils.isNullOrEmpty(matrixDataList)) {
                int highestCellValue=0;
                for(Tuple tuple : matrixDataList) {
                    if(!Utils.isNullOrEmpty(tuple.get("level_cell_no"))) {
                        if (highestCellValue < Integer.parseInt(tuple.get("level_cell_no").toString())) {
                            highestCellValue = Integer.parseInt(tuple.get("level_cell_no").toString());
                        }
                        if(matrixDataMap.containsKey(Integer.parseInt(tuple.get("emp_pay_scale_grade_mapping_detail_id").toString()))) {
                            Map<Integer,Tuple2<Integer,String>> counterMap = matrixDataMap.get(Integer.parseInt(tuple.get("emp_pay_scale_grade_mapping_detail_id").toString()));
                            if(!counterMap.containsKey(Integer.parseInt(tuple.get("level_cell_no").toString()))) {
                                Tuple2<Integer, String> tuple2 = Tuples.of(Integer.parseInt(tuple.get("emp_pay_scale_matrix_detail_id").toString()), !Utils.isNullOrEmpty(tuple.get("level_cell_value")) ? tuple.get("level_cell_value").toString().split("\\.")[0] : "");
                                counterMap.put(Integer.parseInt(tuple.get("level_cell_no").toString()),tuple2);
                                matrixDataMap.put(Integer.parseInt(tuple.get("emp_pay_scale_grade_mapping_detail_id").toString()),counterMap);
                            }
                        }
                        else {
                            Map<Integer, Tuple2<Integer, String>> counterMap = new HashMap<>();
                            Tuple2<Integer, String> tuple2 = Tuples.of(Integer.parseInt(tuple.get("emp_pay_scale_matrix_detail_id").toString()), !Utils.isNullOrEmpty(tuple.get("level_cell_value")) ? tuple.get("level_cell_value").toString().split("\\.")[0] : "");
                            counterMap.put(Integer.parseInt(tuple.get("level_cell_no").toString()),tuple2);
                            matrixDataMap.put(Integer.parseInt(tuple.get("emp_pay_scale_grade_mapping_detail_id").toString()),counterMap);
                        }
                    }
                }
                if(highestCellValue!=0) {
                    toNo = highestCellValue;
                }
            }
            IntStream.rangeClosed(1, toNo).forEach(counter-> {
                EmpPayScaleGradeMappingDetailDTO payScaleMatrixData = new EmpPayScaleGradeMappingDetailDTO();
                payScaleMatrixData.displayOrder = String.valueOf(counter);
                payScaleMatrixData.payScaleMatrixDataDetailList = new ArrayList<>();
                list.forEach(tuple-> {
                    EmpPayScaleMatrixDetailDTO matrixDetailDTO = new EmpPayScaleMatrixDetailDTO();
                    matrixDetailDTO.levelCellNo = counter;
                    matrixDetailDTO.mappingDetailId = Integer.parseInt(tuple.get("emp_pay_scale_grade_mapping_detail_id").toString());
                    matrixDetailDTO.displayOrder = tuple.get("pay_scale_display_order").toString();
                    if(matrixDataMap.containsKey(matrixDetailDTO.mappingDetailId) && matrixDataMap.get(matrixDetailDTO.mappingDetailId).containsKey(counter)) {
                        matrixDetailDTO.levelCellValue = matrixDataMap.get(matrixDetailDTO.mappingDetailId).get(counter).getT2();
                        matrixDetailDTO.id = matrixDataMap.get(matrixDetailDTO.mappingDetailId).get(counter).getT1();
                    }
                    else {
                        matrixDetailDTO.levelCellValue="";
                    }
                    payScaleMatrixData.payScaleMatrixDataDetailList.add(matrixDetailDTO);
                });
                counterList.add(payScaleMatrixData);
            });
            matrix.payScaleMatrixDataList = counterList;
            return matrix;
        }
        return null;
    }

    public boolean saveOrUpdate(EmpPayScaleGradeMappingDTO data, String userId) throws Exception {
        List<EmpPayScaleMatrixDetailDBO> dboList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(data)) {
            List<Tuple> matrixDataList = payScaleMatrixTransaction.getMatrixDataList(data.grade.id,data.revisedYear.id);
            Set<Integer> matrixIds = new HashSet<>();
            if(!Utils.isNullOrEmpty(matrixDataList)) {
                matrixDataList.forEach(tuple -> matrixIds.add(Integer.parseInt(tuple.get("emp_pay_scale_matrix_detail_id").toString())));
            }
            if(!Utils.isNullOrEmpty(data.payScaleMatrixDataList)) {
                Collections.sort(data.payScaleMatrixDataList);
                data.payScaleMatrixDataList.forEach(item -> {
                    if(!Utils.isNullOrEmpty(item.payScaleMatrixDataDetailList)) {
                        item.payScaleMatrixDataDetailList.forEach(subItem -> {
                            if(!Utils.isNullOrEmpty(subItem.levelCellValue)) {
                                EmpPayScaleMatrixDetailDBO dbo = new EmpPayScaleMatrixDetailDBO();
                                if(!Utils.isNullOrEmpty(subItem.id)) {
                                    dbo.id =  subItem.id;
                                    dbo.modifiedUsersId = Integer.parseInt(userId);
                                    if(matrixIds.contains(subItem.id)) {
                                        matrixIds.remove((subItem.id));
                                    }
                                }
                                dbo.empPayScaleGradeMappingDetailDBO = new EmpPayScaleGradeMappingDetailDBO();
                                dbo.empPayScaleGradeMappingDetailDBO.id = subItem.mappingDetailId;
                                dbo.levelCellNo = Integer.parseInt(item.displayOrder);
                                dbo.levelCellValue = new BigDecimal(subItem.levelCellValue);
                                dbo.createdUsersId = Integer.parseInt(userId);
                                dbo.recordStatus = 'A';
                                dboList.add(dbo);
                            }
                        });
                    }
                });
            }
            if(!Utils.isNullOrEmpty(matrixIds)) {
                payScaleMatrixTransaction.delete(matrixIds);
            }
            if(!Utils.isNullOrEmpty(dboList)) {
                return payScaleMatrixTransaction.saveOrUpdate(dboList);
            }
        }
        return false;
    }

    public List<EmpPayScaleGradeMappingDTO> getGridData() throws Exception {
        List<Tuple> list = payScaleMatrixTransaction.getGridData();
        List<EmpPayScaleGradeMappingDTO> gridList = null;
        if(!Utils.isNullOrEmpty(list)) {
            gridList= new ArrayList<>();
            for(Tuple tuple : list) {
                EmpPayScaleGradeMappingDTO gridDTO = new EmpPayScaleGradeMappingDTO();
         
                if(!Utils.isNullOrEmpty(tuple.get("emp_pay_scale_grade_id")) && !Utils.isNullOrEmpty(tuple.get("grade_name"))) {
                	gridDTO.grade = new ExModelBaseDTO();
//                  gridDTO.grade.id = tuple.get("pay_scale_revised_year").toString();
                    gridDTO.grade.id = tuple.get("emp_pay_scale_grade_id").toString();
                    gridDTO.grade.text = tuple.get("grade_name").toString();
                }
                if(!Utils.isNullOrEmpty(tuple.get("emp_employee_category_id")) && !Utils.isNullOrEmpty(tuple.get("employee_category_name"))) {
                	gridDTO.category = new ExModelBaseDTO();
                    gridDTO.category.id = tuple.get("emp_employee_category_id").toString();
                    gridDTO.category.text = tuple.get("employee_category_name").toString();
                }
                if(!Utils.isNullOrEmpty(tuple.get("pay_scale_revised_year"))) {
                	gridDTO.revisedYear = new ExModelBaseDTO();
                    gridDTO.revisedYear.id =  tuple.get("pay_scale_revised_year").toString();
                    gridDTO.revisedYear.text = tuple.get("pay_scale_revised_year").toString();
                }
                gridList.add(gridDTO);
            }
            gridList.sort((o1, o2) -> {
            	if(!Utils.isNullOrEmpty( o1.revisedYear) && !Utils.isNullOrEmpty( o2.revisedYear)) {
	                int comp = o1.revisedYear.text.compareTo(o2.revisedYear.text);
	                if(comp == 0) {
	                	if(!Utils.isNullOrEmpty( o1.category) && !Utils.isNullOrEmpty( o2.category)) {
	                        int comp1 = o1.category.text.compareTo(o2.category.text);
		                    if(comp1 == 0) {
		                        return o1.grade.text.compareTo(o2.grade.text);
		                    }
		                    else {
		                        return comp1;
		                    }
	                	}
	                }
	                else {
	                    return comp;
	                }
            	}
				return 0;
            });
        }
        return gridList;
    }

    public boolean delete(Map<String, String> data) throws Exception {
        List<Tuple> matrixDataList = payScaleMatrixTransaction.getMatrixDataList(data.get("payScaleGradeId"),data.get("revisedYear"));
        Set<Integer> matrixIds = new HashSet<>();
        if(!Utils.isNullOrEmpty(matrixDataList)) {
            matrixDataList.forEach(tuple-> matrixIds.add(Integer.parseInt(tuple.get("emp_pay_scale_matrix_detail_id").toString())));
            return payScaleMatrixTransaction.delete(matrixIds);
        }
        return false;
    }
}