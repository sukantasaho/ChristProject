package com.christ.erp.services.common;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.core.SdkResponse;

@UtilityClass
//@Slf4j
public class AwsSdkUtil {

    public boolean isErrorSdkHttpResponse(SdkResponse sdkResponse) {
        return sdkResponse.sdkHttpResponse() == null || !sdkResponse.sdkHttpResponse().isSuccessful();
    }
}
