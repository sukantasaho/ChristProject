package com.christ.erp.services.controllers.regulations;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.regulations.CategoryResponseDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.regulations.CategoryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/Secured/regulations/categories")
public class CategoryController {

    @Autowired
    private CategoryHandler categoryHandler;

    @PostMapping(value = "/getCategoryDropdownData")
    public Flux<SelectDTO> getCategoryDropdownData(){
        return  categoryHandler.getCategoryDropdownData();
    }
    @PostMapping(value = "/getCategoryGridData")
    public Flux<CategoryResponseDTO> getGridData() {

        return  categoryHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
   /* @PostMapping(value = "/delete")
    public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("id") int id) {
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            result.success =  categoryHandler.delete(id);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
    @PostMapping(value = "/saveOrUpdate")
    public Mono<ApiResult> saveOrUpdate(@RequestBody CategoryUploadDTO dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {
        boolean flag = categoryHandler.saveOrUpdate(dto, userId);
        ApiResult result = new ApiResult();
        result.setSuccess(flag);
        result.setFailureMessage(null);
        result.setDto(null);
        result.setDtoList(null);

        return  Mono.just(result);
    }
*/

}
