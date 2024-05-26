package com.christ.erp.services.common;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.christ.erp.services.dbobjects.common.UrlFolderListDBO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.Constants;
import com.christ.utility.lib.caching.CacheUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class RedisAwsConfig {
	public static final int FOLDER_LIST_ID = 0;
	public static final int BUCKET_NAME = 1;
	public static final int TEMP_BUCKET_NAME = 2;
	public static final int REGION = 3;
	public static final int EXPIRY_UPLOAD = 4;
	public static final int EXPIRY_DOWNLOAD = 5;
	public static final int ACTUAL_PATH = 6;
	public static final int TEMP_PATH = 7;
    
    @Autowired
    CommonApiTransaction commonApiTransaction;

    @PostConstruct
    public void setAwsConfigDataToRedis() {
    	 
       Mono.fromRunnable(() -> {
            CacheUtils.instance.clearMap(Constants.AWS_CONFIG_MAP);
            commonApiTransaction.getAwsConfig().flatMapMany(Flux::fromIterable).flatMap(folderListDBO -> {
            	 return Mono.fromRunnable(() -> CacheUtils.instance.set(Constants.AWS_CONFIG_MAP, folderListDBO.getUploadProcessCode(), createConfigValues(folderListDBO)));
                  })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        })
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();

    }
    public String[] getAwsProperties(String processCode) {
        String configValue= "";
        if(!Utils.isNullOrEmpty(processCode)) {
        	if(!Utils.isNullOrEmpty( CacheUtils.instance.get(Constants.AWS_CONFIG_MAP, processCode))) {
        		configValue =  CacheUtils.instance.get(Constants.AWS_CONFIG_MAP, processCode);
        	}
        }
        String[] values =  configValue.split(",");
       return values;
    }
    public String createConfigValues(UrlFolderListDBO folderListDBO) {
    	String value = String.join(",",Integer.toString(folderListDBO.getId()),
    			folderListDBO.getUrlAwsConfigDBO().getBucketName(), 
    			folderListDBO.getUrlAwsConfigDBO().getTempBucketName(),
    			folderListDBO.getUrlAwsConfigDBO().getRegion(),
    			(!Utils.isNullOrEmpty(folderListDBO.getExpiryUpload())?Integer.toString(folderListDBO.getExpiryUpload()):null),
    			(!Utils.isNullOrEmpty(folderListDBO.getExpiryDownload())?Integer.toString(folderListDBO.getExpiryDownload()):null),
    			folderListDBO.getFolderPath(), 
    			folderListDBO.getTempFolderPath());
		return value;
    	
    }
}
