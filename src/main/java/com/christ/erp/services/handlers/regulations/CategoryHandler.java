package com.christ.erp.services.handlers.regulations;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.RegulationCategoryDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.regulations.CategoryResponseDTO;
import com.christ.erp.services.transactions.regulations.CategoryTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CategoryHandler {

    @Autowired
    private CategoryTransaction categoryTransaction;
    public Flux<CategoryResponseDTO> getGridData() {

        return categoryTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
    }
    public CategoryResponseDTO convertDboToDto (RegulationCategoryDBO dbo) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
            dto.setId(dbo.getId());
            dto.setRegulationCategoryName(dbo.getRegulationCategoryName());
        return dto;
    }
   /* public boolean delete(Integer id) throws Exception {
        return  categoryTransaction.delete(id);
    }
    public boolean saveOrUpdate(CategoryUploadDTO dto, String userId) throws Exception {

       return categoryTransaction.saveOrUpdate(dto, userId);
    }*/
   public Flux<SelectDTO> getCategoryDropdownData(){
       return categoryTransaction.getCategoryDropdownData().flatMapMany(Flux::fromIterable).map(this::convertDBOToSelectDTO);
   }
    public SelectDTO convertDBOToSelectDTO(RegulationCategoryDBO dbo){
        SelectDTO dto = new SelectDTO();
        if(!Utils.isNullOrEmpty(dbo) && !Utils.isNullOrEmpty(dbo))
        {
            dto.setValue(dbo.getId().toString());
            dto.setLabel(dbo.getRegulationCategoryName());
        }
        return dto;
    }
}
