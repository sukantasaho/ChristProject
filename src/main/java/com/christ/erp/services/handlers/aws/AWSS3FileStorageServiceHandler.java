package com.christ.erp.services.handlers.aws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import javax.validation.constraints.NotNull;

import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.common.UrlFolderListDBO;
import com.christ.erp.services.dto.aws.URLFolderListDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.AWSS3Object;
import com.christ.erp.services.common.AwsProperties;
import com.christ.erp.services.common.FileResponse;
import com.christ.erp.services.common.FileUtils;
import com.christ.erp.services.common.MoveObjectResult;
import com.christ.erp.services.common.RedisAwsConfig;
import com.christ.erp.services.common.RedisSysPropertiesData;
import com.christ.erp.services.common.UploadStatus;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Service
public class AWSS3FileStorageServiceHandler implements AWSS3FileStorageService {
	@Autowired
	RedisSysPropertiesData redisSysPropertiesData;
	@Autowired
	CommonApiHandler commonApiHandler;
	
	@Autowired
	RedisAwsConfig redisAwsConfig;

    //private final SqsAsyncClient sqsAsyncClient;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3FileStorageServiceHandler.class);

    private final S3AsyncClient s3AsyncClient;
    private final AwsProperties s3ConfigProperties;
    private final AwsCredentialsProvider awsCredentialsProvider;
    
    @Autowired
    RedisAwsConfig redisSysAwsConfig;

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<AWSS3Object> getObjects() {
        LOGGER.info("Listing objects in S3 bucket: {}", s3ConfigProperties.getS3BucketName());
        return Flux.from(s3AsyncClient.listObjectsV2Paginator(ListObjectsV2Request.builder()
                        .bucket(s3ConfigProperties.getS3BucketName())
                        .build()))
                .flatMap(response -> Flux.fromIterable(response.contents()))
                .map(s3Object -> new AWSS3Object(s3Object.key(), s3Object.lastModified(),s3Object.eTag(), s3Object.size()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> deleteObject(@NotNull String objectKey) {
        LOGGER.info("Delete Object with key: {}", objectKey);
        return Mono.just(DeleteObjectRequest.builder().bucket(s3ConfigProperties.getS3BucketName()).key(objectKey).build())
                .map(s3AsyncClient::deleteObject)
                .flatMap(Mono::fromFuture)
                .then();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<byte[]> getByteObject(@NotNull String key) {
        LOGGER.debug("Fetching object as byte array from S3 bucket: {}, key: {}", s3ConfigProperties.getS3BucketName(), key);
        return Mono.just(GetObjectRequest.builder().bucket(s3ConfigProperties.getS3BucketName()).key(key).build())
                .map(it -> s3AsyncClient.getObject(it, AsyncResponseTransformer.toBytes()))
                .flatMap(Mono::fromFuture)
                .map(BytesWrapper::asByteArray);
    }
    @Override
    public Mono<ResponseEntity<Flux<ByteBuffer>>> getFile(@NotNull String fileKey) {
        LOGGER.debug("Fetching object as byte array from S3 bucket: {}, key: {}", s3ConfigProperties.getS3BucketName(), fileKey);
        GetObjectRequest request = GetObjectRequest.builder()
  		      .bucket(s3ConfigProperties.getS3BucketName())
  		      .key(fileKey)
  		      .build();
  		    
  		    return Mono.fromFuture(s3AsyncClient.getObject(request, AsyncResponseTransformer.toPublisher()))
  		      .map(response -> {
  		        checkResult(response.response());
  		        String filename = getMetadataItem(response.response(),"filename",fileKey);            
  		        return ResponseEntity.ok()
  		          .header(HttpHeaders.CONTENT_TYPE, response.response().contentType())
  		          .header(HttpHeaders.CONTENT_LENGTH, Long.toString(response.response().contentLength()))
  		          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
  		          .body(Flux.from(response));
  		      });
    }
    
    private String getMetadataItem(GetObjectResponse sdkResponse, String key, String defaultValue) {
        for (Entry<String, String> entry : sdkResponse.metadata()
            .entrySet()) {
            if (entry.getKey()
                .equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return defaultValue;
    }
    private static void checkResult(GetObjectResponse response) {
        SdkHttpResponse sdkResponse = response.sdkHttpResponse();
        if (sdkResponse != null && sdkResponse.isSuccessful()) {
            return;
        }

        //throw new DownloadFailedException(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<FileResponse> uploadObject(FilePart filePart) {

        String filename = filePart.filename();
        Map<String, String> metadata = Map.of("filename", filename);
        // get media type
        MediaType mediaType = ObjectUtils.defaultIfNull(filePart.headers().getContentType(), MediaType.APPLICATION_OCTET_STREAM);

        CompletableFuture<CreateMultipartUploadResponse> s3AsyncClientMultipartUpload = s3AsyncClient
                .createMultipartUpload(CreateMultipartUploadRequest.builder()
                        .contentType(mediaType.toString())
                        .key(filename)
                        .metadata(metadata)
                        .bucket(s3ConfigProperties.getS3BucketName())
                        .build());

        UploadStatus uploadStatus = new UploadStatus(Objects.requireNonNull(filePart.headers().getContentType()).toString(), filename);

        return Mono.fromFuture(s3AsyncClientMultipartUpload)
                .flatMapMany(response -> {
                    FileUtils.checkSdkResponse(response);
                    uploadStatus.setUploadId(response.uploadId());
                    LOGGER.info("Upload object with ID={}", response.uploadId());
                    return filePart.content();
                })
                .bufferUntil(dataBuffer -> {
                    // Collect incoming values into multiple List buffers that will be emitted by the resulting Flux each time the given predicate returns true.
                    uploadStatus.addBuffered(dataBuffer.readableByteCount());

                    if (uploadStatus.getBuffered() >= s3ConfigProperties.getMultipartMinPartSize()) {
                        LOGGER.info("BufferUntil - returning true, bufferedBytes={}, partCounter={}, uploadId={}",
                                uploadStatus.getBuffered(), uploadStatus.getPartCounter(), uploadStatus.getUploadId());

                        // reset buffer
                        uploadStatus.setBuffered(0);
                        return true;
                    }

                    return false;
                })
                .map(FileUtils::dataBufferToByteBuffer)
                // upload part
                .flatMap(byteBuffer -> uploadPartObject(uploadStatus, byteBuffer))
                .onBackpressureBuffer()
                .reduce(uploadStatus, (status, completedPart) -> {
                    LOGGER.info("Completed: PartNumber={}, etag={}", completedPart.partNumber(), completedPart.eTag());
                    (status).getCompletedParts().put(completedPart.partNumber(), completedPart);
                    return status;
                })
                .flatMap(uploadStatus1 -> completeMultipartUpload(uploadStatus))
                .map(response -> {
                    FileUtils.checkSdkResponse(response);
                    LOGGER.info("upload result: {}", response.toString());
                    return new FileResponse(filename, uploadStatus.getUploadId(), response.location(), uploadStatus.getContentType(), response.eTag());
                });
    }

    /**
     * Uploads a part in a multipart upload.
     */
    private Mono<CompletedPart> uploadPartObject(UploadStatus uploadStatus, ByteBuffer buffer) {
        final int partNumber = 5;//uploadStatus.getAddedPartCounter();
        LOGGER.info("UploadPart - partNumber={}, contentLength={}", partNumber, buffer.capacity());

        CompletableFuture<UploadPartResponse> uploadPartResponseCompletableFuture = s3AsyncClient.uploadPart(UploadPartRequest.builder()
                        .bucket(s3ConfigProperties.getS3BucketName())
                        .key(uploadStatus.getFileKey())
                        .partNumber(partNumber)
                        .uploadId(uploadStatus.getUploadId())
                        .contentLength((long) buffer.capacity())
                        .build(),
                AsyncRequestBody.fromPublisher(Mono.just(buffer)));

        return Mono
                .fromFuture(uploadPartResponseCompletableFuture)
                .map(uploadPartResult -> {
                    FileUtils.checkSdkResponse(uploadPartResult);
                    LOGGER.info("UploadPart - complete: part={}, etag={}", partNumber, uploadPartResult.eTag());
                    return CompletedPart.builder()
                            .eTag(uploadPartResult.eTag())
                            .partNumber(partNumber)
                            .build();
                });
    }

    /**
     * This method is called when a part finishes uploading. It's primary function is to verify the ETag of the part
     * we just uploaded.
     */
    private Mono<CompleteMultipartUploadResponse> completeMultipartUpload(UploadStatus uploadStatus) {
        LOGGER.info("CompleteUpload - fileKey={}, completedParts.size={}",
                uploadStatus.getFileKey(), uploadStatus.getCompletedParts().size());

        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
                .parts(uploadStatus.getCompletedParts().values())
                .build();

        return Mono.fromFuture(s3AsyncClient.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(s3ConfigProperties.getS3BucketName())
                .uploadId(uploadStatus.getUploadId())
                .multipartUpload(multipartUpload)
                .key(uploadStatus.getFileKey())
                .build()));
    }
    
    public URL generatePresignedUrlOld(String key, Duration expirationTime) {
        try {
        	
        	S3Presigner presigner = S3Presigner.builder()
        		    .region(Region.of(s3ConfigProperties.getRegion()))
        		    .credentialsProvider(awsCredentialsProvider)
        		    .build();
        	  GetObjectRequest getObjectRequest =
                      GetObjectRequest.builder()
                                      .bucket(s3ConfigProperties.getS3BucketName())
                                      .key(key)
                                      .build();

              // Create a GetObjectPresignRequest to specify the signature duration
              GetObjectPresignRequest getObjectPresignRequest =
                  GetObjectPresignRequest.builder()
                                         .signatureDuration(expirationTime)
                                         .getObjectRequest(getObjectRequest)
                                         .build();
              PresignedGetObjectRequest presignedGetObjectRequest =
                      presigner.presignGetObject(getObjectPresignRequest);
              presigner.close();
            return  presignedGetObjectRequest.url();
        } catch (S3Exception e) {
            // Handle any exceptions that may occur during presigned URL generation
            e.printStackTrace();
        }

        return null;
    }    
    /*
    public URL generatePresignedUrlUpload(String key, Duration expirationTime) {
        try {
        	UUID uuid = UUID.randomUUID();
            String filename = uuid.toString();
            System.out.println(filename);
             
        	S3Presigner presigner = S3Presigner.builder()
        		    .region(Region.of(s3ConfigProperties.getRegion()))
        		    .credentialsProvider(awsCredentialsProvider)
        		    .build();
        	  PutObjectRequest getObjectRequest =
                      PutObjectRequest.builder()
                                      .bucket(s3ConfigProperties.getS3BucketName())
                                      .key(key)
                                      .build();

              // Create a GetObjectPresignRequest to specify the signature duration
              PutObjectPresignRequest getObjectPresignRequest =
                  PutObjectPresignRequest.builder()
                                         .signatureDuration (expirationTime)
                                         .putObjectRequest(getObjectRequest)
                                         .build();
              PresignedPutObjectRequest presignedGetObjectRequest =
                      presigner.presignPutObject(getObjectPresignRequest);
              presigner.close();
            return  presignedGetObjectRequest.url();
        } catch (S3Exception e) {
            // Handle any exceptions that may occur during presigned URL generation
            e.printStackTrace();
        }

        return null;
    }    
    */
    public Mono<FileUploadDownloadDTO> generatePresignedUrlUpload(FileUploadDownloadDTO fileUploadDownloadDTO) {
        try {
        	//URLFolderListDTO folderListDTO = commonApiHandler.getAllFolderListForMenu(fileUploadDownloadDTO.getProcessCode());
        	String[] awsConfig = redisAwsConfig.getAwsProperties(fileUploadDownloadDTO.getProcessCode());

        	Duration expirationTime = Duration.ofMinutes(Integer.parseInt(awsConfig[RedisAwsConfig.EXPIRY_UPLOAD]));
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "." + getFileExtension(fileUploadDownloadDTO.getOriginalFileName());
            S3Presigner presigner = S3Presigner.builder()
                    .region(Region.of(awsConfig[RedisAwsConfig.REGION]))
                    .credentialsProvider(awsCredentialsProvider)
                    .build(); 
            PutObjectRequest getObjectRequest = PutObjectRequest.builder()
                    .bucket(awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME])
                    .key(awsConfig[RedisAwsConfig.TEMP_PATH]+fileName)
                    .build();

            PutObjectPresignRequest getObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(expirationTime)
                    .putObjectRequest(getObjectRequest)
                    .build();
            PresignedPutObjectRequest presignedGetObjectRequest = presigner.presignPutObject(getObjectPresignRequest);
            presigner.close();
            fileUploadDownloadDTO.setPreSignedUrl(presignedGetObjectRequest.url().toString());
            fileUploadDownloadDTO.setUniqueFileName(fileName);
            fileUploadDownloadDTO.setOriginalFileName(fileUploadDownloadDTO.getOriginalFileName());
            
            return Mono.just(fileUploadDownloadDTO);
        } catch (S3Exception e) {
            e.printStackTrace();
        }

        return Mono.empty();
    }
    public Mono<FileUploadDownloadDTO> generatePresignedUrlForDownload(FileUploadDownloadDTO fileUploadDownloadDTO) {
    	try {
        	S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(s3ConfigProperties.getRegion()))
                .credentialsProvider(awsCredentialsProvider)
                .build();
        	//URLFolderListDTO folderListDTO = commonApiHandler.getAllFolderListForMenu(fileUploadDownloadDTO.getProcessCode());
        	String[] awsConfig = redisAwsConfig.getAwsProperties(fileUploadDownloadDTO.getProcessCode());
        	Duration expirationTime = Duration.ofMinutes(Integer.parseInt(awsConfig[RedisAwsConfig.EXPIRY_UPLOAD]));

        	String fileKey = "";
        	String bucketName = "";
        	if(!Utils.isNullOrEmpty(fileUploadDownloadDTO.getTempPath())) {
        		fileKey = fileUploadDownloadDTO.getTempPath();
        		bucketName = awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME];
        	}
        	if(!Utils.isNullOrEmpty(fileUploadDownloadDTO.getActualPath())) {
        		fileKey = fileUploadDownloadDTO.getActualPath();
        		bucketName = awsConfig[RedisAwsConfig.BUCKET_NAME];
        	}
	        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
	                .bucket(bucketName)
	                .key(fileKey)
	                 .build();
	       
	        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
	                .signatureDuration(expirationTime)
	                .getObjectRequest(getObjectRequest)
	                .build();
    
	        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
	        fileUploadDownloadDTO.setPreSignedUrl(presignedGetObjectRequest.url().toString());
	        fileUploadDownloadDTO.setOriginalFileName(fileUploadDownloadDTO.getOriginalFileName());
            presigner.close();
            return Mono.just(fileUploadDownloadDTO); 
        } catch (S3Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }

    }


    public static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }   
    /*
    public Mono<ApiResult> sendMessages(List<String> messages) {
        String queueName = "erp-queue";
        SendMessageRequest.Builder requestBuilder = SendMessageRequest.builder()
                .queueUrl(queueName);
        
        messages.forEach(messageString -> {
            SendMessageRequest sendMessageRequest = requestBuilder.messageBody(messageString).build();

            sqsAsyncClient.sendMessage(sendMessageRequest)
                    .whenComplete((result, exception) -> {
                        if (result != null) {
                            System.out.println("Message sent successfully. Message ID: " + result.messageId());
                        } else {
                            System.err.println("Error sending message: " + exception.getMessage());
                        }
                    });
        });
		return null;
    }  
	*/
    public Flux<MoveObjectResult> moveMultipleObjects(List<FileUploadDownloadDTO> folderKeyDtoList) {
        return Flux.fromIterable(folderKeyDtoList)
                .flatMap(fileUploadDownloadDTO -> moveObject(fileUploadDownloadDTO));
    }

    public Mono<MoveObjectResult> moveObject(FileUploadDownloadDTO fileUploadDownloadDTO) {
        String sourceKey = fileUploadDownloadDTO.getTempPath()+ fileUploadDownloadDTO.getUniqueFileName(); 
        String destinationKey = fileUploadDownloadDTO.getActualPath()+ fileUploadDownloadDTO.getUniqueFileName();
        String destinationFolder = fileUploadDownloadDTO.getActualPath();
        
        HeadObjectRequest sourceHeadRequest = HeadObjectRequest.builder()
                .bucket(fileUploadDownloadDTO.getTempBucketName())
                .key(sourceKey)
                .build();

        Mono<HeadObjectResponse> sourceHeadResponseMono = Mono.fromCompletionStage(() ->
                s3AsyncClient.headObject(sourceHeadRequest));

        return sourceHeadResponseMono.flatMap(sourceHeadResponse -> {
            // Source object exists, now check if the destination folder exists
            ListObjectsV2Request destinationListRequest = ListObjectsV2Request.builder()
                    .bucket(fileUploadDownloadDTO.getBucketName())
                    .prefix(destinationFolder)
                    .maxKeys(1)
                    .build();

            Mono<ListObjectsV2Response> destinationListResponseMono = Mono.fromCompletionStage(() ->
                    s3AsyncClient.listObjectsV2(destinationListRequest));

            return destinationListResponseMono.flatMap(destinationListResponse -> {
                if (!destinationListResponse.contents().isEmpty()) {
                    // Destination folder already exists, proceed with the copy operation
                    CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                            .sourceBucket(fileUploadDownloadDTO.getTempBucketName())
                            .destinationBucket(fileUploadDownloadDTO.getBucketName())
                            .sourceKey(sourceKey)
                            .destinationKey(destinationKey)
                            .build();

                    Mono<CopyObjectResponse> copyResponseMono = Mono.fromCompletionStage(() ->
                            s3AsyncClient.copyObject(copyRequest));

                    return copyResponseMono.flatMap(copyResponse -> {
                        if (copyResponse.sdkHttpResponse().isSuccessful()) {
                            // Copy operation succeeded, delete the source object
                            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                                    .bucket(fileUploadDownloadDTO.getTempBucketName())
                                    .key(sourceKey)
                                    .build();

                            return Mono.fromCompletionStage(() ->
                                    s3AsyncClient.deleteObject(deleteRequest))
                                    .thenReturn(new MoveObjectResult(true, null));
                        } else {
                            return Mono.just(new MoveObjectResult(false, "Copy operation failed."));
                        }
                    });
                } else {
                    // Destination folder does not exist, return an error
                    return Mono.just(new MoveObjectResult(false, "Destination folder does not exist."));
                }
            });
        }).onErrorResume(NoSuchKeyException.class, throwable ->
                Mono.just(new MoveObjectResult(false, "Source object does not exist.")))
                .onErrorResume(throwable -> Mono.just(new MoveObjectResult(false, throwable.getMessage())));
          
        
    }
    public Mono<Boolean> checkFolderExists(S3AsyncClient s3AsyncClient, String bucketName, String folderKey) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderKey)
                .maxKeys(1)
                .build();

        return Mono.fromCompletionStage(() -> s3AsyncClient.listObjectsV2(listRequest))
                .map(response -> response.keyCount() > 0)
                .onErrorResume(throwable -> Mono.error(new RuntimeException("Failed to check folder existence.", throwable)));
    }
 

    public Mono<Boolean> generatePDFAndUploadFile(String stringContent, String bucketName, String fileName) throws IOException, DocumentException {
	    Document document = new Document();
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    PdfWriter writer = PdfWriter.getInstance(document, outputStream);
	    document.open();
	    InputStream is = new ByteArrayInputStream(stringContent.getBytes());
	    try {
	        XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    document.close();
	
	    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
	            .bucket(bucketName)
	            .key(fileName)
	            .build();
	
	    return Mono.fromCompletionStage(s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(outputStream.toByteArray())))
	    	    .doFinally(signalType -> {
	    	        try {
	    	            outputStream.close();
	    	        } catch (IOException e) {
	    	            e.printStackTrace();
	    	        }
	    	    })
	    	    .map(response -> {
	    	        System.out.println("File uploaded successfully!");
	        return true;
	    })
	    .onErrorResume(throwable -> {
	        System.err.println("Error uploading file: " + throwable.getMessage());
		        return Mono.just(false);
		    });
	}
    public UrlAccessLinkDBO createURLAccessLinkDBO(UrlAccessLinkDBO urlAccessLinkDBO, String processCode, String uniqueFileName, String orignialFileName, Integer userId) {
        String[] awsConfig = redisAwsConfig.getAwsProperties(processCode);
        urlAccessLinkDBO.setFileNameUnique(awsConfig[RedisAwsConfig.ACTUAL_PATH] + uniqueFileName);
        urlAccessLinkDBO.setTempFileNameUnique(awsConfig[RedisAwsConfig.TEMP_PATH]  + uniqueFileName);
        UrlFolderListDBO folderListDBO = new UrlFolderListDBO();
        if(!Utils.isNullOrEmpty(awsConfig[RedisAwsConfig.FOLDER_LIST_ID])) {
            folderListDBO.setId(Integer.parseInt(awsConfig[RedisAwsConfig.FOLDER_LIST_ID]));
        }
        urlAccessLinkDBO.setUrlFolderListDBO(folderListDBO);
        urlAccessLinkDBO.setIsQueued(false);
        urlAccessLinkDBO.setIsServiced(true);
        urlAccessLinkDBO.setFileNameOriginal(orignialFileName);
        urlAccessLinkDBO.setCreatedUsersId(userId);
        urlAccessLinkDBO.setModifiedUsersId(userId);
        urlAccessLinkDBO.setRecordStatus('A');
        return urlAccessLinkDBO;
    }
    public List<FileUploadDownloadDTO> createFileListForActualCopy(String processCode, String uniqueFileName) {
        String[] awsConfig = redisAwsConfig.getAwsProperties(processCode);
        List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
        FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
        fileUploadDownloadDTO.setTempPath(awsConfig[RedisAwsConfig.TEMP_PATH] );
        fileUploadDownloadDTO.setActualPath(awsConfig[RedisAwsConfig.ACTUAL_PATH]);
        fileUploadDownloadDTO.setUniqueFileName(uniqueFileName);
        fileUploadDownloadDTO.setBucketName(awsConfig[RedisAwsConfig.BUCKET_NAME]);
        fileUploadDownloadDTO.setTempBucketName(awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME]);
        uniqueFileNameList.add(fileUploadDownloadDTO);
        return uniqueFileNameList;
    }
    
    public S3Client getS3Client(String processCode) {
    	String[] awsConfig = redisAwsConfig.getAwsProperties(processCode);
        S3Client s3Client = S3Client.builder()
                .region(Region.of(awsConfig[RedisAwsConfig.REGION]))
                .credentialsProvider(awsCredentialsProvider)
                .build(); 
        return s3Client;
    }
    
    public GetObjectRequest getObjectRequest(String processCode, String uniqueFileName) {
    	String[] awsConfig = redisAwsConfig.getAwsProperties(processCode);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME])
                .key(awsConfig[RedisAwsConfig.TEMP_PATH]+uniqueFileName)
                .build();
        return getObjectRequest;
    }
}