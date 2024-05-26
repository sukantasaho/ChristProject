package com.christ.erp.services.common;

import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.utility.lib.caching.CacheUtils;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;

public class SingleMailMessageWorker implements Runnable{

    private String userName;
    private String token;
    private String senderName;
    private String recipientEmail;
    private String emailSubject;
    private String emailContent;
    private Integer priorityLevelOrder;
    private String erpIntimationMail;
    private Mutiny.SessionFactory sessionFactory;

    public SingleMailMessageWorker(String userName, String token, String senderName, String recipientEmail, String emailSubject, String emailContent, Integer priorityLevelOrder, String erpIntimationMail, Mutiny.SessionFactory sessionFactory) {
        this.userName = userName;
        this.token = token;
        this.senderName = senderName;
        this.recipientEmail = recipientEmail;
        this.emailSubject = emailSubject;
        this.emailContent = emailContent;
        this.priorityLevelOrder = priorityLevelOrder;
        this.erpIntimationMail = erpIntimationMail;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        if(!Utils.isNullOrEmpty(userName)){
            SMTPTransport transport = null;
            Session session;
            try{
                Properties props = new Properties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.starttls.enable", "true");
                session = Session.getDefaultInstance(props);
                if(!Utils.isNullOrEmpty(session)){
                    String recipientEmail = this.recipientEmail;
                    if(Utils.isValidEmail(recipientEmail)){
                        MimeMessage message = new MimeMessage(session);
                        InternetAddress from = new InternetAddress(userName, this.senderName);
                        InternetAddress to = new InternetAddress(recipientEmail);
                        message.setFrom(from);
                        message.addRecipient(Message.RecipientType.TO, to);
                        message.setSubject(this.emailSubject);
                        MimeMultipart mimeMultipart = new MimeMultipart();
                        MimeBodyPart mimeBodyPart = new MimeBodyPart();
                        mimeBodyPart.setContent(this.emailContent, "text/html");
                        mimeMultipart.addBodyPart(mimeBodyPart);
                        message.setContent(mimeMultipart);
                        transport = new SMTPTransport(session, null);
                        transport.connect("smtp.gmail.com", this.userName, null);
                        transport.issueCommand("AUTH XOAUTH2 " + new String(BASE64EncoderStream.encode(String.format("user=%s\1auth=Bearer %s\1\1", this.userName, this.token ).getBytes())), 235);
                        if(transport.isConnected()){
                            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
                        }
                        transport.close();
                    }
                }
            }catch (MessagingException m) {
                m.printStackTrace();
                if (transport != null) {
                    try {
                        transport.close();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("--------------------------------------------------------------------------------------------------------");
                System.out.println("Exception occurred in AdminService while sending mail frm '" + userName + "' by "+ Thread.currentThread().getName());
                System.out.println(m.toString());
                if(m.toString().contains("Daily user sending quota exceeded") || m.toString().contains("550 5.4.5")){
                    System.out.println("Email Changed");
                    String emailSubject = "Daily user sending quota exceeded";
                    String senderName = "Christ University";
                    String emailContent = "Daily user sending quota exceeded for email: "+this.userName;
                    sendIntimationToERP(emailContent, emailSubject, senderName, 2, this.userName, "quotaExceeded");
                } else if(m.toString().contains("334") || m.toString().contains("jakarta.mail.MessagingException: 334")){
                    System.out.println("Token expired");
                    String emailSubject = "Token Expired";
                    String senderName = "Christ University";
                    String emailContent = "Token has been expired for email: "+this.userName;
                    sendIntimationToERP(emailContent, emailSubject, senderName, 2, this.userName, "tokenExpired");
                } else {
                    System.out.println("Email Sending Failed");
                    String emailSubject = "Email Sending Failed";
                    String senderName = "Christ University";
                    String emailContent = "Something went wrong for email: "+this.userName;
                    sendIntimationToERP(emailContent, emailSubject, senderName, 2, this.userName, "wentwrong");
                }
                System.out.println("--------------------------------------------------------------------------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (transport != null && transport.isConnected()) {
                    try {
                        transport.close();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void sendIntimationToERP(String emailContent, String emailSubject, String senderName, Integer priorityLevelOrder, String userName, String exceptionName){
        boolean isEmailSendRequired = true;
        String tokenExpiredExceptionTime = CacheUtils.instance.get("__priority_failed_emails_tokenExpired_map_", userName);
        String quotaExceededExceptionTime = CacheUtils.instance.get("__priority_failed_emails_quotaExceeded_map_", userName);
        if(!Utils.isNullOrEmpty(tokenExpiredExceptionTime)){
            long hours = Duration.between(LocalDateTime.parse(tokenExpiredExceptionTime), LocalDateTime.now()).toHours();
            if(hours <= 1){
                isEmailSendRequired = false;
            }
        } else if(!Utils.isNullOrEmpty(quotaExceededExceptionTime)){
            long day = Duration.between(LocalDateTime.parse(quotaExceededExceptionTime), LocalDateTime.now()).toDays();
            if(day <= 1){
                isEmailSendRequired = false;
            }
        } else {
            if("tokenExpired".equalsIgnoreCase(exceptionName)){
                CacheUtils.instance.set("__priority_failed_emails_tokenExpired_map_", userName, "" + (LocalDateTime.now()));
            }
            if("quotaExceeded".equalsIgnoreCase(exceptionName)){
                CacheUtils.instance.set("__priority_failed_emails_quotaExceeded_map_", userName, "" + (LocalDateTime.now()));
            }
        }
        if(isEmailSendRequired && !Utils.isNullOrEmpty(this.erpIntimationMail) && Utils.isValidEmail(this.erpIntimationMail)){
            ErpEmailsDBO emailsDBO = new ErpEmailsDBO();
            emailsDBO.setSenderName(senderName);
            emailsDBO.setEmailContent(emailContent);
            emailsDBO.setEmailSubject(emailSubject);
            emailsDBO.setPriorityLevelOrder(priorityLevelOrder);
            emailsDBO.setEmailIsSent(false);
            emailsDBO.setRecipientEmail(this.erpIntimationMail);
            emailsDBO.setRecordStatus('A');
            saveErpEmailsDBO(emailsDBO);
        }
    }

    public void saveErpEmailsDBO(ErpEmailsDBO erpEmailsDBO){
        this.sessionFactory.withTransaction((s, tx) -> s.persist(erpEmailsDBO)).subscribeAsCompletionStage();
    }

    public void updateErpEmailsDBO(ErpEmailsDBO erpEmailsDBO){
        this.sessionFactory.withTransaction((s, tx) -> s.merge(erpEmailsDBO)).subscribeAsCompletionStage();
    }

//    public void sendIntimationToERP(String emailContent, String emailSubject, String senderName, Integer priorityLevelOrder, String userName, String exceptionName){
//        boolean isEmailSendRequired = true;
//        String emailExceptionData = CacheUtils.instance.get("__priority_failed_emails_map_", userName);
//        if(!Utils.isNullOrEmpty(emailExceptionData)){
//            if("quotaExceeded".equalsIgnoreCase(exceptionName)){
//                long day = Duration.between(LocalDateTime.parse(emailExceptionData.split("_")[1]), LocalDateTime.now()).toDays();
//                if(day <= 1){
//                    isEmailSendRequired = false;
//                }
//            } else if("tokenExpired".equalsIgnoreCase(exceptionName)){
//                long hours = Duration.between(LocalDateTime.parse(emailExceptionData.split("_")[1]), LocalDateTime.now()).toHours();
//                if(hours <= 1){
//                    isEmailSendRequired = false;
//                }
//            }
//        } else {
//            if("quotaExceeded".equalsIgnoreCase(exceptionName) || "tokenExpired".equalsIgnoreCase(exceptionName)) {
//                CacheUtils.instance.set("__priority_failed_emails_map_", userName, exceptionName + "_" + (LocalDateTime.now()));
//            }
//        }
//        if(isEmailSendRequired && !Utils.isNullOrEmpty(this.erpIntimationMail) && Utils.isValidEmail(this.erpIntimationMail)){
//            ErpEmailsDBO emailsDBO = new ErpEmailsDBO();
//            emailsDBO.setSenderName(senderName);
//            emailsDBO.setEmailContent(emailContent);
//            emailsDBO.setEmailSubject(emailSubject);
//            emailsDBO.setPriorityLevelOrder(priorityLevelOrder);
//            emailsDBO.setEmailIsSent(false);
//            emailsDBO.setRecipientEmail(this.erpIntimationMail);
//            emailsDBO.setRecordStatus('A');
//            saveErpEmailsDBO(emailsDBO);
//        }
//    }

}
