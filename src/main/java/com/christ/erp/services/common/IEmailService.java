package com.christ.erp.services.common;

public interface IEmailService {

    public boolean sendEmail(String recipientEmail, String senderName, String emailSubject, String emailContent, Integer priorityLevelOrder);
}
