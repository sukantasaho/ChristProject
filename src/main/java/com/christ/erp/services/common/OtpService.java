package com.christ.erp.services.common;

import com.christ.utility.lib.caching.CacheUtils;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    /**
     * Generate otp by username and clientId
     * @param userName
     * @param clientId
     * @return otp
     */
    public int generateClientOtp(String userName, String clientId) {
        // Generate a random 6-digit OTP
        int otp = (int) (Math.random() * 900000) + 100000;
        long expirationTimestamp = System.currentTimeMillis() + (2 * 60 * 1000); // 2 minutes in milliseconds
        CacheUtils.instance.set("__otp_client_map_".concat(userName), clientId, otp + "_" + expirationTimestamp);
        return otp;
    }

    /**
     * Verify client otp
     * @param result
     * @param userName
     * @param clientId
     * @param otp
     * @return verified or not
     */
    public boolean verifyClientOtp(ApiResult result, String userName, String clientId, String otp) {
        String cachedOtpData = CacheUtils.instance.get("__otp_client_map_".concat(userName), clientId);
        if(!Utils.isNullOrEmpty(cachedOtpData)){
            String[] storedOtpData = cachedOtpData.split("_");
            if(storedOtpData[0].equalsIgnoreCase(otp)){
                long expirationTimestamp = Long.parseLong(storedOtpData[1]);
                if(expirationTimestamp > System.currentTimeMillis()){
                    CacheUtils.instance.clearKey("__otp_client_map_".concat(userName), clientId);
                    result.success = true;
                    return true;
                } else {
                    result.failureMessage = "OTP is expired. Please generate a new OTP.";
                }
            } else {
                result.failureMessage = "OTP entered is wrong.";
            }
        } else {
            result.failureMessage = "Something went wrong.";
        }
        return false;
    }

    /**
     * Generate otp by userId and token
     * @param userId
     * @param token
     * @return otp
     */
    public int generateTokenOtp(String userId, String token) {
        // Generate a random 6-digit OTP
        int otp = (int) (Math.random() * 900000) + 100000;
        long expirationTimestamp = System.currentTimeMillis() + (2 * 60 * 1000); // 2 minutes in milliseconds
        CacheUtils.instance.set("__otp_token_map_".concat(userId), token, otp + "_" + expirationTimestamp);
        return otp;
    }

    /**
     * Verify client otp
     * @param result
     * @param userId
     * @param token
     * @param otp
     * @return verified or not
     */
    public boolean verifyTokenOtp(ApiResult result, String userId, String token, String otp) {
        String cachedOtpData = CacheUtils.instance.get("__otp_token_map_".concat(userId), token);
        if(!Utils.isNullOrEmpty(cachedOtpData)){
            String[] storedOtpData = cachedOtpData.split("_");
            if(storedOtpData[0].equalsIgnoreCase(otp)){
                long expirationTimestamp = Long.parseLong(storedOtpData[1]);
                if(expirationTimestamp > System.currentTimeMillis()){
                    CacheUtils.instance.clearKey("__otp_token_map_".concat(userId), token);
                    result.success = true;
                    return true;
                } else {
                    result.failureMessage = "OTP is expired. Please generate a new OTP.";
                }
            } else {
                result.failureMessage = "OTP entered is wrong.";
            }
        } else {
            result.failureMessage = "Something went wrong.";
        }
        return false;
    }
}
