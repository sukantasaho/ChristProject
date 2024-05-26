package com.christ.erp.services.common;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;

import com.christ.erp.services.exception.FileValidatorException;
import com.christ.erp.services.exception.UploadException;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.core.SdkResponse;

@UtilityClass
//@Slf4j
public class FileUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3FileStorageServiceHandler.class);
    private final String[] contentTypes = {
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/bmp",
            "image/gif",
            "image/ief",
            "image/pipeg",
            "image/svg+xml",
            "image/tiff",
            "multipart/mixed"
    };

    private boolean isValidType(final FilePart filePart) {
        return isSupportedContentType(Objects.requireNonNull(filePart.headers().getContentType()).toString());
    }
    private boolean isEmpty(final FilePart filePart) {
        return StringUtils.isEmpty(filePart.filename())
                && ObjectUtils.isEmpty(filePart.headers().getContentType());
    }

    private boolean isSupportedContentType(final String contentType) {
        return Arrays.asList(contentTypes).contains(contentType);
    }

  
    public ByteBuffer dataBufferToByteBuffer(List<DataBuffer> buffers) {
        LOGGER.info("Creating ByteBuffer from {} chunks", buffers.size());

        int partSize = 0;
        for(DataBuffer b : buffers) {
            partSize += b.readableByteCount();
        }

        ByteBuffer partData = ByteBuffer.allocate(partSize);
        buffers.forEach(buffer -> partData.put(buffer.asByteBuffer()));

        // Reset read pointer to first byte
        partData.rewind();

        LOGGER.info("PartData: capacity={}", partData.capacity());
        return partData;
    } 

    public void checkSdkResponse(SdkResponse sdkResponse) {
        if (AwsSdkUtil.isErrorSdkHttpResponse(sdkResponse)){
            throw new UploadException(MessageFormat.format("{0} - {1}", sdkResponse.sdkHttpResponse().statusCode(), sdkResponse.sdkHttpResponse().statusText()));
        }
    }
    public void filePartValidator(FilePart filePart) {
        if (isEmpty(filePart)){
            throw new FileValidatorException("File cannot be empty or null!");
        }
        if (!isValidType(filePart)){
            throw new FileValidatorException("Invalid file type");
        }
    }

}