package com.christ.erp.services.common;

import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dto.common.ErpSmsDTO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class SMSUtil {

    @Autowired
    WebClient sendSMS;

    public static final String SMS_SEND_URL = "https://api-alerts.kaleyra.com/v4/?api_key=752963615094k9zz8gk1&method=sms&entity_id=1101356700000026901&sender=SMSCHR&";

    public static final String SMS_SEND_STATUS_URL = "https://api-alerts.kaleyra.com/v4/?api_key=752963615094k9zz8gk1&method=sms.status&";

    public static String formatSMSURIString(String to, String messageBody, String templateId) {
        String uriString = UriComponentsBuilder.fromHttpUrl(SMS_SEND_URL)
                .queryParam("to", to)
                //.queryParam("message", URLEncoder.encode(messageBody, "utf-8"))
                .queryParam("template_id", templateId)
                .toUriString();
        return uriString;
    }

    public static String formatSMSStatusCheckURIString(String id) {
        String uriString = UriComponentsBuilder.fromHttpUrl(SMS_SEND_STATUS_URL)
                .queryParam("id", id)
                .toUriString();
        return uriString;
    }

    public String sendRequest(String uriString) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public ErpSmsDTO sendMessageDTO(ErpSmsDTO erpSmsDTO) throws Exception{
        try {
            String response = sendRequest(formatSMSURIString(erpSmsDTO.getRecipientMobileNo(), erpSmsDTO.getSmsContent(), erpSmsDTO.getTemplateId()));
            if(!Utils.isNullOrEmpty(response)){
                String messageStatus = "SEND-FAILED";
                erpSmsDTO.setGatewayResponse(response);
                JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                if(!Utils.isNullOrEmpty(responseData)){
                    String responseStatus = (String) responseData.get("status");
                    if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                        JSONArray data = (JSONArray) responseData.get("data");
                        if(!Utils.isNullOrEmpty(data)){
                            for (Object dataItem : data) {
                                JSONObject item = (JSONObject) dataItem;
                                if(!Utils.isNullOrEmpty(item)){
                                    String status = (String) item.get("status");
                                    String smsTransactionId = (String) item.get("id");
                                    if(!Utils.isNullOrEmpty(smsTransactionId))
                                        erpSmsDTO.setSmsTransactionId(smsTransactionId);
                                    if("AWAITED-DLR".equalsIgnoreCase(status)){
                                        erpSmsDTO.setSmsIsSent(true);
                                        erpSmsDTO.setSmsSentTime(LocalDateTime.now());
                                        messageStatus = status;//or waiting
                                    } else if("INVALID-NUM".equalsIgnoreCase(status)){
                                        messageStatus = status;
                                    }
                                }
                            }
                        }
                    }
                }
                erpSmsDTO.setMessageStatus(messageStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return erpSmsDTO;
    }

    public List<ErpSmsDTO> sendMessageListDTOs(List<ErpSmsDTO> messageList) throws Exception{
        List<ErpSmsDTO> erpSmsDTOS = new ArrayList<>();
        if(!Utils.isNullOrEmpty(messageList)){
            messageList.forEach(erpSmsDTO -> {
                try {
                    String response = sendRequest(formatSMSURIString(erpSmsDTO.getRecipientMobileNo(), erpSmsDTO.getSmsContent(), erpSmsDTO.getTemplateId()));
                    if(!Utils.isNullOrEmpty(response)){
                        String messageStatus = "SEND-FAILED";
                        erpSmsDTO.setGatewayResponse(response);
                        JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                        if(!Utils.isNullOrEmpty(responseData)){
                            String responseStatus = (String) responseData.get("status");
                            if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                                JSONArray data = (JSONArray) responseData.get("data");
                                if(!Utils.isNullOrEmpty(data)){
                                    for (Object dataItem : data) {
                                        JSONObject item = (JSONObject) dataItem;
                                        if(!Utils.isNullOrEmpty(item)){
                                            String status = (String) item.get("status");
                                            String smsTransactionId = (String) item.get("id");
                                            if(!Utils.isNullOrEmpty(smsTransactionId))
                                                erpSmsDTO.setSmsTransactionId(smsTransactionId);
                                            if("AWAITED-DLR".equalsIgnoreCase(status)){
                                                messageStatus = status;
                                                erpSmsDTO.setSmsIsSent(true);
                                                erpSmsDTO.setSmsSentTime(LocalDateTime.now());
                                            } else if("INVALID-NUM".equalsIgnoreCase(status)){
                                                messageStatus = status;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        erpSmsDTO.setMessageStatus(messageStatus);
                    }
                    erpSmsDTOS.add(erpSmsDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return erpSmsDTOS;
    }

//    public static String sendMessage(String to, String messageBody, String templateId) throws Exception{
//        String messageStatus = "FAILED";
//        try {
//            String response = sendRequest(formatSMSURIString(to, messageBody, templateId));
//            if(!Utils.isNullOrEmpty(response)){
//                JSONObject data = (JSONObject) (new JSONParser()).parse(response);
//                if(!Utils.isNullOrEmpty(data)){
//                    String status = (String) data.get("status");
//                    if("OK".equalsIgnoreCase(status) || "200".equalsIgnoreCase(status)){
//                        if(!Utils.isNullOrEmpty((String) data.get("message"))){
//                            messageStatus = "SUCCESS";
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return messageStatus;
//    }

    public Mono<String> formatData(LinkedHashMap response){
        var result = new ArrayList<LinkedHashMap>();
        if(!Utils.isNullOrEmpty(response)) {
            if(!Utils.isNullOrEmpty(response.get("records"))) {
                result = (ArrayList<LinkedHashMap>) response.get("records");
                if(!Utils.isNullOrEmpty(result)) {
                    result.forEach(s-> {

                    });
                }
            }
        }
        return Mono.justOrEmpty("");
    }

    public Mono<String> sendRequest1(String to, String messageBody, String templateId) throws Exception {
        //api_key=752963615094k9zz8gk1&method=sms&entity_id=1101356700000026901&sender=SMSCHR&
//        return sendSMS
//            .get()
//            .uri(uriBuilder -> {
//                try {
//                    return uriBuilder
//                        .queryParam("api_key","752963615094k9zz8gk1")
//                        .queryParam("method","sms")
//                        .queryParam("entity_id","1101356700000026901")
//                        .queryParam("sender", "SMSCHR")
//                        .queryParam("to", to)
//                        .queryParam("message", URLEncoder.encode(messageBody, "utf-8"))
//                        .queryParam("template_id", templateId)
//                        .build();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            })
//            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//            .retrieve()
//            .bodyToMono(String.class);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("api_key", "752963615094k9zz8gk1");
        requestBody.add("method", "sms");
        requestBody.add("entity_id", "1101356700000026901");
        requestBody.add("sender", "SMSCHR");
        requestBody.add("to", to);
        requestBody.add("message", URLEncoder.encode(messageBody, "utf-8"));
        requestBody.add("template_id", templateId);
        return sendSMS
                .post()
                //.uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve()
                .bodyToMono(String.class);
//                .subscribe(response -> {
//                    // Handle the response from the Kaleyra API
//                    System.out.println("response : " + response);
//                });
    }

    public  void sendMessage1(String to, String messageBody, String templateId) throws Exception{
        sendRequest1(to, messageBody, templateId)
            .subscribe(response -> {
                JSONObject responseData = null;
                try {
                    responseData = (JSONObject) (new JSONParser()).parse(response);
                    if(!Utils.isNullOrEmpty(responseData)){
                        String responseStatus = (String) responseData.get("status");
                        if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                            JSONArray data = (JSONArray) responseData.get("data");
                            if(!Utils.isNullOrEmpty(data)){
                                for (Object dataItem : data) {
                                    JSONObject item = (JSONObject) dataItem;
                                    if(!Utils.isNullOrEmpty(item)){
                                        String status = (String) item.get("status");
                                        String smsTransactionId = (String) item.get("id");
                                        if(!Utils.isNullOrEmpty(status)) {
                                            if ("AWAITED-DLR".equalsIgnoreCase(status)) {
                                            } else if ("INVALID-NUM".equalsIgnoreCase(status) || "INV-NUMBER".equalsIgnoreCase(status)) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    public  void sendMessageNew(String to, String messageBody, String templateId) throws Exception{
        sendRequest1(to, messageBody, templateId);
    }

    public  String sendMessage(String to, String messageBody, String templateId) throws Exception{
        return sendRequest(formatSMSURIString(to, messageBody, templateId));
    }

    public List<Object> sendAllMessages(List<ErpSmsDBO> messageList) throws Exception{
        List<Object> erpSmsDBOS = new ArrayList<>();
        if(!Utils.isNullOrEmpty(messageList)){
            messageList.forEach(erpSmsDBO -> {
                try {
                    if(!Utils.isNullOrEmpty(erpSmsDBO.getRecipientMobileNo()) && !Utils.isNullOrEmpty(erpSmsDBO.getSmsContent()) && !Utils.isNullOrEmpty(erpSmsDBO.getTemplateId())){
                        String response = sendRequest(formatSMSURIString(erpSmsDBO.getRecipientMobileNo(), erpSmsDBO.getSmsContent(), erpSmsDBO.getTemplateId()));
                        if(!Utils.isNullOrEmpty(response)){
                            String messageStatus = "SEND-FAILED";
                            erpSmsDBO.setGatewayResponse(response);
                            JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                            if(!Utils.isNullOrEmpty(responseData)){
                                String responseStatus = (String) responseData.get("status");
                                if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                                    JSONArray data = (JSONArray) responseData.get("data");
                                    if(!Utils.isNullOrEmpty(data)){
                                        for (Object dataItem : data) {
                                            JSONObject item = (JSONObject) dataItem;
                                            if(!Utils.isNullOrEmpty(item)){
                                                String status = (String) item.get("status");
                                                String smsTransactionId = (String) item.get("id");
                                                if(!Utils.isNullOrEmpty(smsTransactionId))
                                                    erpSmsDBO.setSmsTransactionId(smsTransactionId);
                                                if(!Utils.isNullOrEmpty(status)) {
                                                    if ("AWAITED-DLR".equalsIgnoreCase(status)) {
                                                        messageStatus = status;
                                                        erpSmsDBO.setSmsIsSent(true);
                                                        erpSmsDBO.setSmsSentTime(LocalDateTime.now());
                                                    } else if ("INVALID-NUM".equalsIgnoreCase(status) || "INV-NUMBER".equalsIgnoreCase(status)) {
                                                        messageStatus = status;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            erpSmsDBO.setMessageStatus(messageStatus);
                        }
                    }
                    erpSmsDBOS.add(erpSmsDBO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return erpSmsDBOS;
    }

    public List<Object> checkAllMessageStatus(List<ErpSmsDBO> smsDBOList) throws Exception{
        List<Object> erpSmsDBOS = new ArrayList<>();
        if(!Utils.isNullOrEmpty(smsDBOList)){
            smsDBOList.forEach(erpSmsDBO -> {
                try {
                    if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsTransactionId())){
                        String response = sendRequest(formatSMSStatusCheckURIString(erpSmsDBO.getSmsTransactionId()));
                        if(!Utils.isNullOrEmpty(response)){
                            erpSmsDBO.setGatewayResponse(response);
                            JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                            if(!Utils.isNullOrEmpty(responseData)){
                                String responseStatus = (String) responseData.get("status");
                                if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                                    JSONArray data = (JSONArray) responseData.get("data");
                                    if(!Utils.isNullOrEmpty(data)){
                                        for (Object dataItem : data) {
                                            JSONObject item = (JSONObject) dataItem;
                                            if(!Utils.isNullOrEmpty(item)){
                                                String status = (String) item.get("status");
                                                if(!Utils.isNullOrEmpty(status)){
                                                    if("DELIVRD".equalsIgnoreCase(status)){
                                                        erpSmsDBO.setSmsIsDelivered(true);
                                                        String deliveryTime = (String) item.get("dlrtime");
                                                        if(!Utils.isNullOrEmpty(deliveryTime))
                                                            erpSmsDBO.setSmsDeliveredTime(Utils.convertStringLocalDateTimeToLocalDateTime(deliveryTime));
                                                    }
                                                    erpSmsDBO.setMessageStatus(status);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    erpSmsDBOS.add(erpSmsDBO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return erpSmsDBOS;
    }

    public ErpSmsDBO checkMessageStatus(ErpSmsDBO erpSmsDBO) throws Exception{
        if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsTransactionId())){
            try {
                String response = sendRequest(formatSMSStatusCheckURIString(erpSmsDBO.getSmsTransactionId()));
                if(!Utils.isNullOrEmpty(response)){
                    erpSmsDBO.setGatewayResponse(response);
                    JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                    if(!Utils.isNullOrEmpty(responseData)){
                        String responseStatus = (String) responseData.get("status");
                        if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                            JSONArray data = (JSONArray) responseData.get("data");
                            if(!Utils.isNullOrEmpty(data)){
                                for (Object dataItem : data) {
                                    JSONObject item = (JSONObject) dataItem;
                                    if(!Utils.isNullOrEmpty(item)){
                                        String status = (String) item.get("status");
                                        if(!Utils.isNullOrEmpty(status)){
                                            if("DELIVRD".equalsIgnoreCase(status)){
                                                erpSmsDBO.setSmsIsDelivered(true);
                                                String deliveryTime = (String) item.get("dlrtime");
                                                if(!Utils.isNullOrEmpty(deliveryTime))
                                                    erpSmsDBO.setSmsDeliveredTime(Utils.convertStringLocalDateTimeToLocalDateTime(deliveryTime));
                                            }
                                            erpSmsDBO.setMessageStatus(status);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return erpSmsDBO;
    }
}
