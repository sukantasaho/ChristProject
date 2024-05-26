package com.christ.erp.services.controllers.regulations;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.RegulationEntriesDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.regulations.RegulationUploadDownloadDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.regulations.RegulationDocumentUploadHandler;
import com.christ.utility.lib.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.persistence.EntityManager;
import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Secured/regulations")
public class RegulationDocumentUploadController {

    @Autowired
    private RegulationDocumentUploadHandler regulationDocumentUploadHandler;

    @PostMapping(value = "/getEntranceTest")
    public Flux<SelectDTO> getEntranceTest() {
        return regulationDocumentUploadHandler.getEntranceTest().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/saveOrUpdate")
    public Mono<ApiResult> RegulationDocumentUpload(@RequestBody RegulationUploadDownloadDTO regReqDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws ParseException {
        return regulationDocumentUploadHandler.saveRegulationDocument(regReqDTO, Integer.parseInt(userId));

    }
    @PostMapping(value = "/RegulationDocumentUploadDownloadFormat")
    public  Mono<FileUploadDownloadDTO> RegulationDocumentUploadDownloadFormat()  {
        return regulationDocumentUploadHandler.RegulationDocumentUploadDownloadFormat().switchIfEmpty(Mono.error(new NotFoundException(null)));

    }
    /* This API Meant For Get Regulation Entries Grid Data With or Without Param */
    @PostMapping(value = "/getGridData")
    public Flux<RegulationEntriesDTO> getGridData(@RequestBody(required = false) SelectDTO documentCategory, @RequestParam(required = false) List<String> typeKeyWords) {

        return regulationDocumentUploadHandler.getGridData(documentCategory, typeKeyWords).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    /* This API Meant For Get Dropdown Data For Regulation Entries Table For Document Category Field */
    /*@PostMapping(value = "/getRegulationCategoryDropdownData")
    public Mono<ApiResult<List<LookupItemDTO>>> getCategoryDropdownData() {
        ApiResult<List<LookupItemDTO>> categories = new ApiResult<List<LookupItemDTO>>();
        try {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {

                    String str="select regulation_entries.regulation_category_id as ID, regulation_entries.regulation_doc_category as Text from regulation_entries where record_status= 'A'";
                    Utils.getDropdownData(categories, context, str.toString(), null);
                }
                @Override
                public void onError(Exception error) {
                    categories.success = false;
                    categories.dto = null;
                    categories.failureMessage = error.getMessage();
                }
            }, true);
        }catch(Exception error) {
            Utils.log(error.getMessage());
        }
        return Utils.monoFromObject(categories);
    }*/
    @PostMapping(value = "/getRegulationCategoryDropdownData")
    public Flux<SelectDTO> getRegulationCategoryDropdownData(){
       return regulationDocumentUploadHandler.getRegulationCategoryDropdownData();
    }

    /* This API Meant For Getting Existing Data When Click On Edit Link */
    @PostMapping(value = "/edit")
    public Mono<ApiResult<RegulationEntriesDTO>> getExistData(@RequestParam("id") int id) throws Exception {
            ApiResult<RegulationEntriesDTO> result = new ApiResult<>();
            RegulationEntriesDTO  resDTO = regulationDocumentUploadHandler.getExistData(id);
            if(!Utils.isNullOrEmpty(resDTO.getRegulationEntryId())) {
                result.dto = resDTO;
                result.success = true;
               // result.tag = regulationUploadDownloadDTO.getRegulationEntriesId().toString();
                return Utils.monoFromObject(result);
            }
        throw  new NotFoundException(null);
    }
    /* This API Meant For Delete Regulation Entry */
    @PostMapping(value = "/delete")
    public Mono<ApiResult> delete(@RequestParam("id") int id) {
        ApiResult result = new ApiResult<>();
        try {
            result.success =  regulationDocumentUploadHandler.delete(id);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

}
