package com.christ.erp.services.common;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import org.springframework.http.ResponseEntity;
//import jakarta.validation.constraints.NotNull;
import org.springframework.http.codec.multipart.FilePart;

import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.itextpdf.text.DocumentException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AWSS3FileStorageService {

    /**
     * Upload object in Amazon S3
     *
     * @param filePart - the request part containing the file to be saved
     * @return Mono of {@link FileResponse} representing the result of the operation
     */
    Mono<FileResponse> uploadObject(FilePart filePart);

    /**
     * Retrieves byte objects from Amazon S3.
     *
     * @param key object key
     * @return object byte[]
     */
    Mono<byte[]> getByteObject(@NotNull String key);

    Mono<ResponseEntity<Flux<ByteBuffer>>> getFile(@NotNull String fileKey);

    /**
     * Delete multiple objects from a bucket
     *
     * @param objectKey object key
     */
    Mono<Void> deleteObject(@NotNull String objectKey);

    /**
     * Returns some or all (up to 1,000) of the objects in a bucket.
     *
     * @return Flux of object key
     */
    Flux<AWSS3Object> getObjects();

    URL generatePresignedUrlOld(String key, Duration expirationTime);

    public Mono<FileUploadDownloadDTO> generatePresignedUrlUpload(FileUploadDownloadDTO fileUploadDownloadDTO);

    public Mono<FileUploadDownloadDTO> generatePresignedUrlForDownload(FileUploadDownloadDTO key);

    //public Mono<ApiResult> sendMessages(List<String> messages);
    public Mono<MoveObjectResult> moveObject(FileUploadDownloadDTO fileUploadDownloadDTO);

    public Mono<Boolean> generatePDFAndUploadFile(String stringContent, String bucketName, String fileName) throws IOException, DocumentException;

    public Flux<MoveObjectResult> moveMultipleObjects(List<FileUploadDownloadDTO> folderKeyDtoList);

    public UrlAccessLinkDBO createURLAccessLinkDBO(UrlAccessLinkDBO urlAccessLinkDBO, String processCode, String uniqueFileName, String orignialFileName, Integer userId);

    public List<FileUploadDownloadDTO> createFileListForActualCopy(String processCode, String uniqueFileName);
}
