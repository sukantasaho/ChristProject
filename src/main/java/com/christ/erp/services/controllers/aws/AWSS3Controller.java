package com.christ.erp.services.controllers.aws;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.FileUtils;
import com.christ.erp.services.common.SuccessResponse;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Protected/fileUploadDownLoad")
@Validated
public class AWSS3Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3FileStorageServiceHandler.class);
    private final AWSS3FileStorageService fileStorageService;
    
    @PostMapping(path= "/upload/presigned")
    public Mono<ResponseEntity<Flux<FileUploadDownloadDTO>>> generateMutiplePresignedUrlUpload(@RequestBody List<FileUploadDownloadDTO> keys) {
        Flux<FileUploadDownloadDTO> responseFlux = Flux.fromIterable(keys)
                .flatMap(key -> fileStorageService.generatePresignedUrlUpload(key)
                        .switchIfEmpty(Mono.error(new NotFoundException(null)))
                );
        return Mono.just(ResponseEntity.ok(responseFlux));
    }
    @PostMapping(path = "/download/presigned")
    public Mono<ResponseEntity<Flux<FileUploadDownloadDTO>>> generatePresignedUrlForDownload(@RequestBody List<FileUploadDownloadDTO> keys) {
        Flux<FileUploadDownloadDTO> responseFlux = Flux.fromIterable(keys)
        		.flatMap(key -> fileStorageService.generatePresignedUrlForDownload(key)
                .switchIfEmpty(Mono.error(new NotFoundException(null)))
        );
        return Mono.just(ResponseEntity.ok(responseFlux));
    }
    @PostMapping("/upload")
    public Mono<SuccessResponse> upload(@RequestPart("files") Mono<FilePart> filePart) {

        return filePart
                .map(file -> {
                    FileUtils.filePartValidator(file);
                    return file;
                })
                .flatMap(fileStorageService::uploadObject)
                .map(fileResponse -> new SuccessResponse(fileResponse, "Upload successfully"));

    }

    @PostMapping(value ="/uploadMutipleFiles")
    public Mono<SuccessResponse> uploadMutipleFiles(@RequestPart("file-data") Flux<FilePart> fileParts) {
    	int concurrencyLimit = 5;
	    return fileParts.flatMap(file -> {
	        try {
	            FileUtils.filePartValidator(file);
	            return fileStorageService.uploadObject(file)
	                .onErrorResume(throwable -> {
	                    LOGGER.error("Error occurred during upload: {}", throwable.getMessage());
	                    return Mono.error(throwable); // Rethrow the error
	                })
	                .subscribeOn(Schedulers.parallel());
	        } catch (Exception e) {
	             LOGGER.error("Error occurred during upload: {}", e.getMessage());
	            return Mono.error(e); // Rethrow the error
	        }
	    }, concurrencyLimit)
	    .collectList()
	    .map(fileResponses -> new SuccessResponse(fileResponses, "Upload successful"));
    }
    @GetMapping(path="/downloadByte/{fileKey}")
    public  Mono<SuccessResponse>  downloadByte(@PathVariable("fileKey") String fileKey) {
    	 return fileStorageService.getByteObject(fileKey)
                 .map(objectKey -> new SuccessResponse(objectKey, "Object byte response"));
    }
    @GetMapping("/downloadFiles/{fileNames}")
    public Mono<List<SuccessResponse>> downloadFiles(@PathVariable List<String> fileNames) {
        return Flux.fromIterable(fileNames)
                .flatMap(fileKey -> fileStorageService.getByteObject(fileKey)
                        .map(objectKey -> new SuccessResponse(objectKey, "Object byte response"))
                        .onErrorResume(error -> {
                            //ErrorResponse errorResponse = new ErrorResponse();
                            //errorResponse.set(fileKey);
                            //errorResponse.setErrorMessage(error.getMessage());
                            return Mono.just(new SuccessResponse("error", error.getMessage()));
                        }))
                .collectList();
    }
    
    @GetMapping(path="/download/{fileKey}")
    public Mono<ResponseEntity<Flux<ByteBuffer>>> download(@PathVariable("fileKey") String fileKey) {
        return fileStorageService.getFile(fileKey);
              
    }
    @DeleteMapping(path="/{objectKey}")
    public Mono<SuccessResponse> deleteFile(@PathVariable("objectKey") String objectKey) {
        return fileStorageService.deleteObject(objectKey)
                .map(resp -> new SuccessResponse(null, MessageFormat.format("Object with key: {0} deleted successfully", objectKey)));
    }

    @GetMapping(path="/getAllObj")
    public Flux<SuccessResponse> getObject() {
        return fileStorageService.getObjects()
                .map(objectKey -> new SuccessResponse(objectKey, "Result found"));
    }
    
    @PostMapping(path="/moveObjects")
    public Flux<SuccessResponse> moveFiles(@RequestBody String fileNames) {
    	List<FileUploadDownloadDTO> uploadDTOList = new ArrayList<FileUploadDownloadDTO>();
    	List<String> fileNameList = Arrays.asList(fileNames.split(","));
    	
    	 if(!Utils.isNullOrEmpty(fileNameList)) {
    		 fileNameList.forEach(name->{
    			 FileUploadDownloadDTO uploadDTO = new  FileUploadDownloadDTO();
    			 uploadDTO.setUniqueFileName(name);
    			 uploadDTO.setBucketName("christerp");
    			 uploadDTO.setTempBucketName("christerp");
    			 uploadDTO.setActualPath("actual/");
    			 uploadDTO.setTempPath("temp/");
    			 uploadDTOList.add(uploadDTO);
    		 });
    		  
    	 }
        return fileStorageService.moveMultipleObjects(uploadDTOList)
                .map(objectKey -> new SuccessResponse(objectKey, "Files moved successfully"));
    }
    
    /*
    @PostMapping(path="/sendMessages")
    public Mono<ApiResult> sendMessagesToSQS(@RequestBody List<String> messageString) {
        return fileStorageService.sendMessages(messageString);
              
    }
    */

}

