package com.christ.erp.services.controllers.common;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.AppConstants;
import com.christ.erp.services.common.Utils;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;

@RestController
@RequestMapping(value = "/Public/Advertisement")
public class AdvertisementApiController {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    /**
     * Get advertisement content for employee application
     * @return
     */
    @RequestMapping(value = "/getAdvertisementContent")
    public Mono<ApiResult> getEmployeeApplicationAdvertisementContent() {
        ApiResult result = new ApiResult();
        try {
            if(checkAdvertisementContent()){
                String str = "select emp_appln_advertisement.advertisement_content as advertisementContent, 0 as isCommonAdvertisement " +
                        " from emp_appln_advertisement " +
                        " inner join erp_academic_year on erp_academic_year.erp_academic_year_id = emp_appln_advertisement.erp_academic_year_id and erp_academic_year.record_status='A' " +
                        " where emp_appln_advertisement.record_status='A'  " +
                        " and (curdate() >= emp_appln_advertisement.advertisement_start_date and curdate() <= emp_appln_advertisement.advertisement_end_date) " +
                        " union " +
                        " select emp_appln_advertisement.advertisement_content as advertisementContent, 1 as isCommonAdvertisement " +
                        " from emp_appln_advertisement " +
                        " inner join erp_academic_year on erp_academic_year.erp_academic_year_id = emp_appln_advertisement.erp_academic_year_id and erp_academic_year.record_status='A' " +
                        " where emp_appln_advertisement.record_status='A'  and emp_appln_advertisement.is_common_advertisement=1 " +
                        " order by isCommonAdvertisement asc limit 1";
                Tuple tuple = sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class)
                        .getSingleResultOrNull()).await().indefinitely();
                setAdvertisementContent(tuple);
            }
            result.dto = getAdvertisementContent();
            result.success = true;
        }catch (Exception e) {
            result.success = false;
            result.dto = null;
        }
        return Utils.monoFromObject(result);
    }

    public static synchronized void setAdvertisementContent(Tuple tuple) {
        if(!Utils.isNullOrEmpty(tuple) && !Utils.isNullOrEmpty(tuple.get("advertisementContent"))){
            if("1".equalsIgnoreCase(String.valueOf(tuple.get("isCommonAdvertisement")))){
                AppConstants.ADVERTISEMENT_COMMON_CONTENT = Utils.getBlobObjectToString(tuple.get("advertisementContent"));
                AppConstants.ADVERTISEMENT_CONTENT = "";
            } else {
                AppConstants.ADVERTISEMENT_CONTENT = Utils.getBlobObjectToString(tuple.get("advertisementContent"));
            }
        } else {
            AppConstants.ADVERTISEMENT_CONTENT = "";
            AppConstants.ADVERTISEMENT_COMMON_CONTENT = "";
        }
    }

    public static synchronized boolean checkAdvertisementContent() {
        if(Utils.isNullOrEmpty(AppConstants.ADVERTISEMENT_CONTENT)){
            if(Utils.isNullOrEmpty(AppConstants.ADVERTISEMENT_COMMON_CONTENT)){
               return true;
            }
        }
        return false;
    }

    public static synchronized String getAdvertisementContent() {
        String advertisementContent = "";
        if(!Utils.isNullOrEmpty(AppConstants.ADVERTISEMENT_CONTENT)){
            advertisementContent = AppConstants.ADVERTISEMENT_CONTENT;
        } else if(!Utils.isNullOrEmpty(AppConstants.ADVERTISEMENT_COMMON_CONTENT)) {
            advertisementContent = AppConstants.ADVERTISEMENT_COMMON_CONTENT;
        }
        return advertisementContent;
    }

}
