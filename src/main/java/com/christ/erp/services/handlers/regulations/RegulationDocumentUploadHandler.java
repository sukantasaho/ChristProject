package com.christ.erp.services.handlers.regulations;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisAwsConfig;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreEntryDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreEntryDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardQuantitativeParameterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.RegulationCategoryDBO;
import com.christ.erp.services.dbobjects.common.RegulationUploadDownloadDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.*;
import com.christ.erp.services.dto.regulations.RegulationUploadDownloadDTO;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.regulations.RegulationDocumentUploadTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class  RegulationDocumentUploadHandler {

    @Autowired
    private RegulationDocumentUploadTransaction regulationDocumentUploadTransaction;

    @Autowired
    RedisAwsConfig redisAwsConfig;

    @Autowired
    AWSS3FileStorageServiceHandler aWSS3FileStorageServiceHandler;

    @Autowired
    AWSS3FileStorageService aWSS3FileStorageService;

    @Autowired
    CommonApiTransaction commonApiTransaction;

    public Flux<SelectDTO> getEntranceTest() {
        return regulationDocumentUploadTransaction.getEntranceTest().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
    }
    public SelectDTO convertDBOToDTO(AdmSelectionProcessTypeDBO dbo){
        SelectDTO dto = new SelectDTO();
        if (!Utils.isNullOrEmpty(dbo)) {
            dto.setValue(String.valueOf(dbo.getId()));
            dto.setLabel(dbo.getSelectionStageName());
        }
        return dto;
    }
    private List<AdmSelectionProcessScoreDBO> convertDtoToDbo(Map<String, Integer> mapList, Map<Integer, List<String>> map1, List<Integer> values, String userId) {
        List<AdmSelectionProcessScoreDBO> value = new ArrayList<AdmSelectionProcessScoreDBO>();
        List<Integer> selectionProcessId = mapList.values().stream().collect(Collectors.toList());
        //fetch records which r already in table
        Map<String, AdmSelectionProcessScoreDBO> admSelectionProcessScoreDBOMap =  regulationDocumentUploadTransaction.getAdmSelectionProcessScoreDBODetails(selectionProcessId,values.get(0)).stream().collect(Collectors.toMap(s -> s.getAdmSelectionProcessDBO().getId()+"-"+s.getAdmSelectionProcessTypeDetailsDBO().getId(), s -> s));
        map1.forEach((k,v)-> {
            if(mapList.containsKey(values.get(4)+v.get(0)+values.get(5))) {
                Integer admSelectionProcessDBOId = mapList.get(values.get(4)+v.get(0)+values.get(5));
                if(!admSelectionProcessScoreDBOMap.containsKey(admSelectionProcessDBOId+"-"+values.get(0))) {
                    AdmSelectionProcessScoreDBO admSelectionProcessScoreDBO = new AdmSelectionProcessScoreDBO();
                    admSelectionProcessScoreDBO.setAdmSelectionProcessDBO(new AdmSelectionProcessDBO());
                    admSelectionProcessScoreDBO.getAdmSelectionProcessDBO().setId(admSelectionProcessDBOId);
                    admSelectionProcessScoreDBO.setAdmSelectionProcessTypeDetailsDBO(new AdmSelectionProcessTypeDetailsDBO());
                    admSelectionProcessScoreDBO.getAdmSelectionProcessTypeDetailsDBO().setId(values.get(0));
                    admSelectionProcessScoreDBO.setCreatedUsersId(Integer.parseInt(userId));
                    admSelectionProcessScoreDBO.setRecordStatus('A');
                    admSelectionProcessScoreDBO.setAdmSelectionProcessScoreEntryDBOSet(new HashSet<AdmSelectionProcessScoreEntryDBO>());
                    BigDecimal maxScore = new BigDecimal("000000.00");
                    BigDecimal scoreEntered = new BigDecimal("000000.00");
                    AdmSelectionProcessScoreEntryDBO subDbo =  new AdmSelectionProcessScoreEntryDBO();
                    subDbo.setAdmSelectionProcessScoreDBO(admSelectionProcessScoreDBO);
                    subDbo.setAdmScoreCardDBO(new AdmScoreCardDBO());
                    subDbo.getAdmScoreCardDBO().setId(values.get(3));
                    subDbo.setErpUsersDBO( new ErpUsersDBO());
                    subDbo.getErpUsersDBO().setId(Integer.parseInt(userId));
                    maxScore = maxScore.add(BigDecimal.valueOf(values.get(2)));
                    subDbo.setMaxScore(maxScore);
                    scoreEntered =scoreEntered.add(BigDecimal.valueOf(Double.parseDouble(v.get(1))));
                    subDbo.setScoreEntered(scoreEntered);
                    subDbo.setCreatedUsersId(Integer.parseInt(userId));
                    subDbo.setRecordStatus('A');
                    subDbo.setAdmSelectionProcessScoreEntryDetailsDBOSet(new HashSet<AdmSelectionProcessScoreEntryDetailsDBO>());
                    AdmSelectionProcessScoreEntryDetailsDBO subDbo1 = new AdmSelectionProcessScoreEntryDetailsDBO();
                    subDbo1.setAdmSelectionProcessScoreEntryDBO(subDbo);
                    subDbo1.setAdmScoreCardQuantitativeParameterDBO(new AdmScoreCardQuantitativeParameterDBO());
                    subDbo1.getAdmScoreCardQuantitativeParameterDBO().setId(values.get(1));
                    subDbo1.setQuantitativeParameterMaxScore(maxScore);
                    subDbo1.setQuantitativeParameterScoreEntered(scoreEntered);
                    subDbo1.setCreatedUsersId(Integer.parseInt(userId));
                    subDbo1.setRecordStatus('A');
                    subDbo.getAdmSelectionProcessScoreEntryDetailsDBOSet().add(subDbo1);
                    admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet().add(subDbo);
                    value.add(admSelectionProcessScoreDBO);
                }
                else {
                    AdmSelectionProcessScoreDBO admSelectionProcessScoreDBO = admSelectionProcessScoreDBOMap.get(admSelectionProcessDBOId+"-"+values.get(0));
                    BigDecimal maxScore = new BigDecimal("000000.00");
                    BigDecimal scoreEntered = new BigDecimal("000000.00");
                    AdmSelectionProcessScoreEntryDBO subDbo = admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet().iterator().next();
                    maxScore = maxScore.add(BigDecimal.valueOf(values.get(2)));
                    subDbo.setMaxScore(maxScore);
                    scoreEntered =scoreEntered.add(BigDecimal.valueOf(Double.parseDouble(v.get(1))));
                    subDbo.setScoreEntered(scoreEntered);
                    AdmSelectionProcessScoreEntryDetailsDBO subDbo1 = subDbo.getAdmSelectionProcessScoreEntryDetailsDBOSet().iterator().next();
                    subDbo1.setQuantitativeParameterMaxScore(maxScore);
                    subDbo1.setQuantitativeParameterScoreEntered(scoreEntered);
                    subDbo.getAdmSelectionProcessScoreEntryDetailsDBOSet().add(subDbo1);
                    admSelectionProcessScoreDBO.getAdmSelectionProcessScoreEntryDBOSet().add(subDbo);
                    value.add(admSelectionProcessScoreDBO);
                }
            }
        });
        return value;
    }
    public Mono<FileUploadDownloadDTO> RegulationDocumentUploadDownloadFormat() {
        Tuple tuple = regulationDocumentUploadTransaction.regulationDocumentUploadDownloadFormat();
        var apiResult = new FileUploadDownloadDTO();
        apiResult.setActualPath(tuple.get("fileNameUnique").toString());
        apiResult.setProcessCode(tuple.get("uploadProcessCode").toString());
        apiResult.setOriginalFileName(tuple.get("fileNameOriginal").toString());
        return Mono.just(apiResult);
    }
    public   Mono<ApiResult> saveRegulationDocument(RegulationUploadDownloadDTO regReqDTO , Integer userId ) throws ParseException {

        CommonUploadDownloadDTO uploadDocumentDto =
        uploadDocumentDto = regReqDTO.getUploadData();
         ApiResult result = new ApiResult();

        //moving files from TEMP to ACTUAL folder
        List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
        uniqueFileNameList.addAll(aWSS3FileStorageService.createFileListForActualCopy("REGULATION_DOCUMENT", uploadDocumentDto.getUniqueFileName()));
        if ((!Utils.isNullOrEmpty(uniqueFileNameList))) {
            aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList).subscribe();
        }
        // Update  REGULATION_ENTRIES & URL_ACCESS_LINK
        String[] awsConfig = redisAwsConfig.getAwsProperties("REGULATION_DOCUMENT");
        List<FileUploadDownloadDTO> uploadDTOList = new ArrayList<FileUploadDownloadDTO>();

        FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
        fileUploadDownloadDTO.setTempPath(awsConfig[RedisAwsConfig.TEMP_PATH] );
        fileUploadDownloadDTO.setActualPath(awsConfig[RedisAwsConfig.ACTUAL_PATH]);
        fileUploadDownloadDTO.setUniqueFileName(uploadDocumentDto.getUniqueFileName());
        fileUploadDownloadDTO.setBucketName(awsConfig[RedisAwsConfig.BUCKET_NAME]);
        fileUploadDownloadDTO.setTempBucketName(awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME]);
        uploadDTOList.add(fileUploadDownloadDTO);

        RegulationUploadDownloadDBO regdbo = setUploadDocumentUrl(uploadDocumentDto, userId, uploadDTOList );
        if(!Utils.isNullOrEmpty(regReqDTO.getRegulationEntriesDescription()))
        {
            regdbo.setRegulationEntriesDescription(regReqDTO.getRegulationEntriesDescription());
        }
        //regdbo.setUrlAccessLinkDBO(regdbo.getUrlAccessLinkDBO());
        if(!Utils.isNullOrEmpty(regdbo.getUrlAccessLinkDBO()))
        {
            regdbo.setUrlAccessLinkDBO(regdbo.getUrlAccessLinkDBO());
        }
        regdbo.setRegulationDocReferenceNo(regReqDTO.getRegulationDocReferenceNo());
        regdbo.setRegulationDocTitle(regReqDTO.getRegulationDocTitle());
        regdbo.setRegulationDocCategory(regReqDTO.getRegulationDocCategory().getLabel());
        if(!Utils.isNullOrEmpty(regReqDTO.getRegulationDocCategory()))
        {
            RegulationCategoryDBO cdbo = new RegulationCategoryDBO();
            cdbo.setRegulationCategoryName(regReqDTO.getRegulationDocCategory().getLabel());
            cdbo.setId(Integer.parseInt(regReqDTO.getRegulationDocCategory().getValue()));
            regdbo.setRegulationCategoryDBO(cdbo);
        }
        if(!Utils.isNullOrEmpty(regReqDTO.getSearchTags()))
        {
            regdbo.setRegulationSearchTags(regReqDTO.getSearchTags());
        }
        if(!Utils.isNullOrEmpty(regReqDTO.getRegulationEntryId()))
        {
            regdbo.setId(regReqDTO.getRegulationEntryId());
        }
        if(!Utils.isNullOrEmpty(regReqDTO.getRegulationDocPublishDate()))
        {
            //LocalDate ldpd = Utils.convertStringDateToLocalDate(regReqDTO.getDocPublishedDate().toString());
            //LocalDateTime publishDate =  ldpd.atStartOfDay();
            regdbo.setRegulationDocPublishDate(regReqDTO.getRegulationDocPublishDate());
        }

        if(!Utils.isNullOrEmpty(regReqDTO.getRegulationDocValidFrom()))
        {
           // LocalDate ldvfd=  Utils.convertStringDateToLocalDate(regReqDTO.getDocValidFromDate().toString());
           // LocalDateTime validFromDate =  ldvfd.atStartOfDay();
            regdbo.setRegulationDocValidFrom(regReqDTO.getRegulationDocValidFrom());
        }
        if(!Utils.isNullOrEmpty(regReqDTO.getRegulationDocValidTill()))
        {
            //LocalDate ldvtd=  Utils.convertStringDateToLocalDate(regReqDTO.getDocValidToDate());
            //LocalDateTime validToDate =  ldvtd.atStartOfDay();
            regdbo.setRegulationDocValidTill(regReqDTO.getRegulationDocValidTill());
        }
        regdbo.setRegulationDocVersion(regReqDTO.getRegulationDocVersion());
        regdbo.setCreatedUserId(userId);
        regdbo.setModifiedUserId(userId);
        EmpDBO emp = new EmpDBO();
         //emp.setId(Integer.parseInt(regReqDTO.getEmpId()));
         //regdbo.setEmpId(emp);
        regdbo.setRegulationDocAccessPolicy(regReqDTO.getRegulationDocAccessPolicy());
        regdbo.setRecordStatus('A');
        boolean flag = regulationDocumentUploadTransaction.saveRegulations(regdbo);
         if(flag == true)
         {
             result.success = true;
         }
         return  Mono.just(result);
    }
    public RegulationUploadDownloadDBO setUploadDocumentUrl(CommonUploadDownloadDTO uploadDocumentDto, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
        String regulationDocumentAWSProcessCode = "REGULATION_DOCUMENT";
        UrlAccessLinkDBO urlAccessLinkDbo = null;
        //  if (Utils.isNullOrEmpty(empLeaveEntryDBO.getLeaveDocumentUrlDBO())) {
        if (!Utils.isNullOrEmpty(uploadDocumentDto) && !Utils.isNullOrEmpty(uploadDocumentDto.getNewFile()) &&
                !Utils.isNullOrEmpty(uploadDocumentDto.getOriginalFileName())) {
            urlAccessLinkDbo = new UrlAccessLinkDBO();
            urlAccessLinkDbo.setCreatedUsersId(userId);
            urlAccessLinkDbo.setRecordStatus('A');
        }
        // }
        if (!Utils.isNullOrEmpty(uploadDocumentDto) && !Utils.isNullOrEmpty(uploadDocumentDto.getNewFile())  ) {
            urlAccessLinkDbo = aWSS3FileStorageService.createURLAccessLinkDBO(urlAccessLinkDbo, regulationDocumentAWSProcessCode, uploadDocumentDto.getUniqueFileName(), uploadDocumentDto.getOriginalFileName(), userId);
        }
        if (!Utils.isNullOrEmpty(uploadDocumentDto) && !Utils.isNullOrEmpty(uploadDocumentDto.getProcessCode()) && !Utils.isNullOrEmpty(uploadDocumentDto.getUniqueFileName()) && !Utils.isNullOrEmpty(uploadDocumentDto.getNewFile())  ) {
            uniqueFileNameList.addAll(aWSS3FileStorageService.createFileListForActualCopy(regulationDocumentAWSProcessCode, uploadDocumentDto.getUniqueFileName()));
        }
        RegulationUploadDownloadDBO dbo = new RegulationUploadDownloadDBO();
        dbo.setUrlAccessLinkDBO(urlAccessLinkDbo);

        return dbo;
    }
    public Flux<RegulationEntriesDTO> getGridData(SelectDTO regulationDocCategory, List<String> typeKeyWords) {
        return regulationDocumentUploadTransaction.getGridData(regulationDocCategory, typeKeyWords).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
    }
    public RegulationEntriesDTO getExistData(int id) {

        RegulationEntriesDTO resDto = new RegulationEntriesDTO();
        RegulationUploadDownloadDBO dbo =  regulationDocumentUploadTransaction.getExistData(id);

        if(!Utils.isNullOrEmpty(dbo))
        {
            resDto.setRegulationDocTitle(dbo.getRegulationDocTitle());
               // regulationUploadDownloadDTO.setDocCategory(regulationUploadDownloadDBO.getRegulationDocCategory());
                SelectDTO categoryDTO = new SelectDTO();
                if(!Utils.isNullOrEmpty(dbo.getRegulationCategoryDBO()))
                {
                    categoryDTO.setValue(dbo.getRegulationCategoryDBO().getId().toString());
                    categoryDTO.setLabel(dbo.getRegulationCategoryDBO().getRegulationCategoryName());
                    resDto.setRegulationDocCategory(categoryDTO);
                }
            if(!Utils.isNullOrEmpty(dbo.getRegulationDocPublishDate()))
            {
                resDto.setRegulationDocPublishDate(dbo.getRegulationDocPublishDate());
            }
            if(!Utils.isNullOrEmpty(dbo.getRegulationDocValidFrom()))
            {
                 resDto.setRegulationDocValidFrom(dbo.getRegulationDocValidFrom());
            }
            if(!Utils.isNullOrEmpty(dbo.getRegulationDocValidTill()))
            {
                 resDto.setRegulationDocValidTill(dbo.getRegulationDocValidTill());
            }
           // regulationUploadDownloadDTO.setDocPublishedDate(regulationUploadDownloadDBO.getRegulationDocPublishDate().toString());
             resDto.setRegulationDocVersion(dbo.getRegulationDocVersion());
            //regulationUploadDownloadDTO.setDocValidFromDate(regulationUploadDownloadDBO.getRegulationDocValidFrom().toString());
           // regulationUploadDownloadDTO.setDocValidToDate(regulationUploadDownloadDBO.getRegulationDocValidTill().toString());
             resDto.setRegulationDocReferenceNo(dbo.getRegulationDocReferenceNo());
             resDto.setRegulationDocAccessPolicy(dbo.getRegulationDocAccessPolicy());
            if(!Utils.isNullOrEmpty(dbo.getRegulationSearchTags()))
            {
                resDto.setSearchTags(dbo.getRegulationSearchTags());
            }
            CommonUploadDownloadDTO uploadData = new CommonUploadDownloadDTO();
            if(!Utils.isNullOrEmpty(dbo.getUrlAccessLinkDBO()))
            {
               // regulationUploadDownloadDTO.setUploadData();
                    resDto.setActualPath(dbo.getUrlAccessLinkDBO().getFileNameUnique());
                    resDto.setOriginalFileName(dbo.getUrlAccessLinkDBO().getFileNameOriginal());
                    resDto.setUrl_access_link(dbo.getUrlAccessLinkDBO().getId());
                    UploadDataDTO udto = new UploadDataDTO();
                    udto.setActualPath(dbo.getUrlAccessLinkDBO().getFileNameUnique());
                    udto.setOriginalFileName(dbo.getUrlAccessLinkDBO().getFileNameOriginal());
                    udto.setProcessCode("REGULATION_DOCUMENT");
                    resDto.setUploadData(udto);
            }
            resDto.setRegulationEntriesDescription(dbo.getRegulationEntriesDescription());
            resDto.setRegulationEntryId(dbo.getId());

        }
        return !Utils.isNullOrEmpty(resDto)?resDto:null;
    }
    public RegulationEntriesDTO convertDboToDto (RegulationUploadDownloadDBO dbo) {
        RegulationEntriesDTO dto = new RegulationEntriesDTO();
         SelectDTO categoryDTO = new SelectDTO();
         if(!Utils.isNullOrEmpty(dbo.getRegulationCategoryDBO()))
         {
             categoryDTO.setValue(dbo.getRegulationCategoryDBO().getId().toString());
             categoryDTO.setLabel(dbo.getRegulationCategoryDBO().getRegulationCategoryName());
             dto.setRegulationDocCategory(categoryDTO);
         }
         dto.setRegulationDocReferenceNo(dbo.getRegulationDocReferenceNo());
        dto.setRegulationDocTitle(dbo.getRegulationDocTitle());
        dto.setRegulationEntryId(dbo.getId());
        dto.setRegulationDocVersion(dbo.getRegulationDocVersion());
        dto.setRegulationEntriesDescription(dbo.getRegulationEntriesDescription());
        dto.setSearchTags(dbo.getRegulationSearchTags());
        //dto.setDocAuthor(dbo.getEmpId().empName);
        dto.setRegulationDocAccessPolicy(dbo.getRegulationDocAccessPolicy());
        if(!Utils.isNullOrEmpty(dbo.getRegulationDocPublishDate()))
        {
            dto.setRegulationDocPublishDate(dbo.getRegulationDocPublishDate());
        }
        if(!Utils.isNullOrEmpty(dbo.getRegulationDocValidFrom()))
        {
            dto.setRegulationDocValidFrom(dbo.getRegulationDocValidFrom());
        }
        if(!Utils.isNullOrEmpty(dbo.getRegulationDocValidTill()))
        {
            dto.setRegulationDocValidTill(dbo.getRegulationDocValidTill());
        }
         if(!Utils.isNullOrEmpty(dbo.getUrlAccessLinkDBO()))
         {
             dto.setActualPath(dbo.getUrlAccessLinkDBO().getFileNameUnique());
             dto.setOriginalFileName(dbo.getUrlAccessLinkDBO().getFileNameOriginal());
             dto.setUrl_access_link(dbo.getUrlAccessLinkDBO().getId());
             UploadDataDTO udto = new UploadDataDTO();
             udto.setActualPath(dbo.getUrlAccessLinkDBO().getFileNameUnique());
             udto.setOriginalFileName(dbo.getUrlAccessLinkDBO().getFileNameOriginal());
             udto.setProcessCode("REGULATION_DOCUMENT");
             dto.setUploadData(udto);

         }
        return dto;
    }
    public boolean delete(Integer id) throws Exception {
        return  regulationDocumentUploadTransaction.delete(id);
    }
    public Flux<SelectDTO> getRegulationCategoryDropdownData(){
        return  regulationDocumentUploadTransaction.getRegulationCategoryDropdownData().flatMapMany(Flux::fromIterable).map(this::convertDBOToSelectDTO);
    }
    public SelectDTO convertDBOToSelectDTO(RegulationUploadDownloadDBO dbo){
        SelectDTO dto = new SelectDTO();
        if(!Utils.isNullOrEmpty(dbo) && !Utils.isNullOrEmpty(dbo.getRegulationCategoryDBO()))
        {
             dto.setValue(dbo.getRegulationCategoryDBO().getId().toString());
             dto.setLabel(dbo.getRegulationCategoryDBO().getRegulationCategoryName());
        }
        return dto;
    }

}

