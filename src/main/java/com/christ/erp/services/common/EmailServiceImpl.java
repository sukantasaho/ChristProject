package com.christ.erp.services.common;

import com.christ.erp.services.dbobjects.common.ErpNotificationEmailSenderSettingsDBO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.caching.CacheUtils;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements IEmailService{

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @Autowired
    private CommonApiTransaction commonApiTransaction;

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Properties getEmailProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }

    @Override
    public boolean sendEmail(String recipientEmail, String senderName, String emailSubject, String emailContent, Integer priorityLevelOrder) {
        boolean isMailSend = false;
        try{
            Map<Integer, Map<String, String>> priorityMailsTokenMap = new HashMap<>();
            Map<Integer, LinkedList<String>> priorityMailsMap = new HashMap<>();
            List<ErpNotificationEmailSenderSettingsDBO> erpNotificationEmailSenderSettingsDBOList = commonApiTransaction.getEmailSenderSettings();
            if(!Utils.isNullOrEmpty(erpNotificationEmailSenderSettingsDBOList)) {
                priorityMailsTokenMap = erpNotificationEmailSenderSettingsDBOList.stream()
                        .collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder,
                                Collectors.toMap(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, ErpNotificationEmailSenderSettingsDBO::getToken)));
                priorityMailsMap = erpNotificationEmailSenderSettingsDBOList.stream()
                        .collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder,
                                Collectors.mapping(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, Collectors.toCollection(LinkedList::new))));
                if(!Utils.isNullOrEmpty(priorityMailsMap.get(priorityLevelOrder))){
                    LinkedList<String> priorityMails = priorityMailsMap.get(priorityLevelOrder);
                    String lastUsedPriorityMail = CacheUtils.instance.get("__priority_last_used_emails_map_", String.valueOf(priorityLevelOrder));
                    int index = 0;
                    if(!Utils.isNullOrEmpty(lastUsedPriorityMail)){
                        index = priorityMails.indexOf(lastUsedPriorityMail);
                        if (index == (priorityMails.size() - 1)) {
                            index = 0;
                        } else {
                            index++;
                        }
                    }
                    String nextEmail = checkEmailHasException(priorityLevelOrder, priorityMails, index);
                    if(!Utils.isNullOrEmpty(nextEmail)){
                        String erpIntimationMail = redisSysPropertiesData.getSysProperties(SysProperties.ERP_INTIMATION_EMAIL.name(), null, null);
                        ExecutorService executorService = Executors.newFixedThreadPool(1);
                        Runnable mailWorker = new SingleMailMessageWorker(nextEmail, priorityMailsTokenMap.get(priorityLevelOrder).get(nextEmail), senderName, recipientEmail, emailSubject, emailContent, priorityLevelOrder, erpIntimationMail, sessionFactory);
                        executorService.execute(mailWorker);
                        isMailSend = true;
                    } else {
                        System.out.println("mail sending failed for "+ recipientEmail);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return isMailSend;
    }

    public static synchronized String checkEmailHasException(Integer priorityLevelOrder, LinkedList<String> priorityMails, int index){
        String mail = "";
        try{
            if(!Utils.isNullOrEmpty(priorityMails)){
                int allMailsExceptionCount = 0;
                for (int i = index; i < priorityMails.size(); i++) {
                    String tokenExpiredExceptionTime = CacheUtils.instance.get("__priority_failed_emails_tokenExpired_map_", priorityMails.get(i));
                    if(Utils.isNullOrEmpty(tokenExpiredExceptionTime)){
                        String quotaExceededExceptionTime = CacheUtils.instance.get("__priority_failed_emails_quotaExceeded_map_", priorityMails.get(i));
                        if(Utils.isNullOrEmpty(quotaExceededExceptionTime)){
                            mail = priorityMails.get(i);
                            break;
                        } else {
                            long day = Duration.between(LocalDateTime.parse(quotaExceededExceptionTime), LocalDateTime.now()).toDays();
                            if (day >= 1) {
                                CacheUtils.instance.clearKey("__priority_failed_emails_quotaExceeded_map_", priorityMails.get(i));
                                mail = priorityMails.get(i);
                                break;
                            }
                        }
                    } else {
                        long hours = Duration.between(LocalDateTime.parse(tokenExpiredExceptionTime), LocalDateTime.now()).toHours();
                        if(hours >= 1){
                            CacheUtils.instance.clearKey("__priority_failed_emails_tokenExpired_map_", priorityMails.get(i));
                            mail = priorityMails.get(i);
                            break;
                        }
                    }
                    allMailsExceptionCount++;
                    if (i == (priorityMails.size() - 1)) {
                        i = -1;
                    }
                    if(allMailsExceptionCount == priorityMails.size()){
                        break;
                    }
                }
            }
            if(!Utils.isNullOrEmpty(mail))
                CacheUtils.instance.set("__priority_last_used_emails_map_", String.valueOf(priorityLevelOrder), mail);
        }catch(Exception e){
            e.printStackTrace();
        }
        return mail;
    }

    //    public static synchronized String checkEmailHasException(Integer priorityLevelOrder,
//        LinkedList<String> priorityMails, int index){
//        String mail = "";
//        try{
//            if(!Utils.isNullOrEmpty(priorityMails)){
//                int allMailsExceptionCount = 0;
//                for (int i = index; i < priorityMails.size(); i++) {
//                    String emailExceptionData = CacheUtils.instance.get("__priority_failed_emails_map_", priorityMails.get(i));
//                    if(Utils.isNullOrEmpty(emailExceptionData)){
//                        mail = priorityMails.get(i);
//                        break;
//                    } else {
//                        String[] exceptionDatas = emailExceptionData.split("_");
//                        if("quotaExceeded".equalsIgnoreCase(exceptionDatas[0])){
//                            long day = Duration.between(LocalDateTime.parse(exceptionDatas[1]), LocalDateTime.now()).toDays();
//                            if(day >= 1){
//                                CacheUtils.instance.clearKey("__priority_failed_emails_map_", priorityMails.get(i));
//                                mail = priorityMails.get(i);
//                                break;
//                            }
//                        } else if("tokenExpired".equalsIgnoreCase(exceptionDatas[0])){
//                            long hours = Duration.between(LocalDateTime.parse(exceptionDatas[1]), LocalDateTime.now()).toHours();
//                            if(hours >= 1){
//                                CacheUtils.instance.clearKey("__priority_failed_emails_map_", priorityMails.get(i));
//                            }
//                            mail = priorityMails.get(i);
//                            break;
//                        } else {
//                            mail = priorityMails.get(i);
//                            break;
//                        }
//                        allMailsExceptionCount++;
//                        if (i == (priorityMails.size() - 1)) {
//                            i = -1;
//                        }
//                        if(allMailsExceptionCount == priorityMails.size()){
//                            break;
//                        }
//                    }
//                }
//            }
//            if(!Utils.isNullOrEmpty(mail))
//                CacheUtils.instance.set("__priority_last_used_emails_map_", String.valueOf(priorityLevelOrder), mail);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return mail;
//    }
}
