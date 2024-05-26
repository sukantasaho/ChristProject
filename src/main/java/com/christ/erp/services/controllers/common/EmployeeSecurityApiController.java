package com.christ.erp.services.controllers.common;
import java.util.Map;

import com.christ.erp.services.common.*;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnRegistrationsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.transactions.employee.recruitment.EmployeeApplicationTransaction;
import com.christ.utility.lib.jwt.JwtUser;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.christ.erp.services.dto.common.AuthRequestDTO;
import com.christ.erp.services.dto.common.AuthResponseDTO;
import com.christ.erp.services.security.JWTPasswordEncoder;

import reactor.core.publisher.Mono;

import javax.persistence.Tuple;

@RestController
@RequestMapping(value = "/Public/ApplicantSecurity")
public class EmployeeSecurityApiController extends BaseApiController{

    @Autowired
    private EmployeeApplicationTransaction employeeApplicationTransaction;

    @Autowired
    private JWTPasswordEncoder _passwordEncoder;

    @Autowired
    private ValidateReCaptcha _validateReCaptcha;

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    @Autowired
    private OtpService otpService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private SMSUtil smsUtil;

    @RequestMapping(value = "/AuthApplicant", method = RequestMethod.POST)
    public Mono<ResponseEntity<AuthResponseDTO>> auth(@RequestBody AuthRequestDTO request) {
        String str = "select ear.emp_appln_registrations_id as id, ear.email as email from emp_appln_registrations ear where ear.record_status = 'A' and ear.email=:userName and ear.passwd=:password";
        Mono<Tuple> tuple = Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class)
                        .setParameter("userName", request.loginId).setParameter("password", this._passwordEncoder.encode(request.loginPassword)).getSingleResultOrNull())
                .subscribeAsCompletionStage());
        return tuple.filter(user-> !Utils.isNullOrEmpty(user) && !Utils.isNullOrEmpty(user.get("id")) && !Utils.isNullOrEmpty(user.get("email"))).map(user -> {
            AuthResponseDTO authDTO = new AuthResponseDTO();
            JwtUser userDTO = new JwtUser();
            userDTO.id = user.get("id").toString();
            userDTO.name = user.get("email").toString();
            authDTO.empApplicationRegistrationId = Integer.parseInt(user.get("id").toString());
            authDTO.token = JWTUtil.generateToken(userDTO);
            return ResponseEntity.ok(authDTO);
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * Verify applicant emailId
     * @param request
     * @param isForRegister
     * @return ApiResult
     */
    @RequestMapping(value = "/verifyRegisterApplicantEmail", method = RequestMethod.POST)
    public Mono<ApiResult> verifyRegisterApplicantEmail(@RequestBody AuthRequestDTO request, @RequestParam Boolean isForRegister) {
        ApiResult result = new ApiResult();
        try {
                if(Utils.isValidEmail(request.email)){
                    EmpApplnRegistrationsDBO bo = employeeApplicationTransaction.isApplicantRegistered(request.email);
                    if(Utils.isNullOrEmpty(bo)){
                        if(isForRegister){
                            result.success = true;
                        } else {
                            result.success = false;
                            result.failureMessage = "This Email id is not registered with us. Please Sign Up";
                        }
                    }else{
                        if(isForRegister) {
                            result.success = false;
                            result.failureMessage = "This Email ID is already registered with us. Please use Login option";
                        } else {
                            result.success = true;
                        }
                    }
                }else{
                    result.failureMessage = "Email ID is not valid";
                }
        }catch (Exception e) {
            result.success = false;
            result.dto = null;
            result.failureMessage = "An Error! occurred, Couldn't verify the Email";
        }
        return Utils.monoFromObject(result);
    }

    /**
     * Verify applicant and generate OTP
     * @param request
     * @return ApiResult
     */
    @RequestMapping(value = "/verifyRegisterApplicant", method = RequestMethod.POST)
    public Mono<ApiResult> verifyRegisterApplicant(@RequestBody AuthRequestDTO request) {
        ApiResult result = new ApiResult();
        try {
            if(!Utils.isNullOrEmpty(request.email) && !Utils.isNullOrEmpty(request.password)){
                if(Utils.isValidEmail(request.email)){
                    EmpApplnRegistrationsDBO bo = employeeApplicationTransaction.isApplicantRegistered(request.email);
                    if(Utils.isNullOrEmpty(bo)){
                        //send email otp
                        sendOtp(request.email, request.getClientId());
                        result.success = true;
                    }else{
                        result.failureMessage = "This Email ID is already registered with us. Please use Login option";
                    }
                }else{
                    result.failureMessage = "Email ID is not valid";
                }
            }else{
                result.failureMessage = "Please enter credentials";
            }
        }catch (Exception e) {
            result.success = false;
            result.dto = null;
            result.failureMessage = "Something went wrong";
        }
        return Utils.monoFromObject(result);
    }

    /**
     * Verifying otp and registering an applicant
     * @param request
     * @return ApiResult
     */
    @RequestMapping(value = "/RegisterApplicant", method = RequestMethod.POST)
    public Mono<ApiResult> register(@RequestBody AuthRequestDTO request) {
        ApiResult result = new ApiResult();
        try {
            if(!Utils.isNullOrEmpty(request.getOtp())){
                if(Utils.isValidEmail(request.email)){
                    //otp validation
                    boolean isOtpVerified = otpService.verifyClientOtp(result, request.email, request.getClientId(), request.getOtp());
                    if(isOtpVerified){
                        EmpApplnRegistrationsDBO bo = employeeApplicationTransaction.isApplicantRegistered(request.email);
                        if(Utils.isNullOrEmpty(bo)){
                            EmpApplnRegistrationsDBO empApplnRegistrationsDBO = new EmpApplnRegistrationsDBO();
                            empApplnRegistrationsDBO.applicantName = request.name;
                            empApplnRegistrationsDBO.email = request.email;
                            empApplnRegistrationsDBO.passwd = this._passwordEncoder.encode(request.password);
                            empApplnRegistrationsDBO.recordStatus = 'A';
                            boolean isAdded = employeeApplicationTransaction.registerApplicant(empApplnRegistrationsDBO);
                            if(isAdded){
                                AuthResponseDTO authDTO = new AuthResponseDTO();
                                JwtUser userDTO = new JwtUser();
                                userDTO.id = String.valueOf(empApplnRegistrationsDBO.id);
                                userDTO.name = empApplnRegistrationsDBO.email;
                                authDTO.empApplicationRegistrationId = empApplnRegistrationsDBO.id;
                                authDTO.token = JWTUtil.generateToken(userDTO);
                                result.dto = authDTO;
                                result.success = true;
                            }
                        }else{
                            result.failureMessage = "This Email ID is already registered with us. Please use Login option";
                        }
                    }
                }else{
                    result.failureMessage = "Email ID is not valid";
                }
            } else {
                result.failureMessage = "Please enter OTP.";
            }
        }catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.dto = null;
            result.failureMessage = "Something went wrong.";
        }
        return Utils.monoFromObject(result);
    }

    /**
     * resend otp
     * @param request
     * @return ApiResult
     */
    @RequestMapping(value = "/resendOTP", method = RequestMethod.POST)
    public Mono<ApiResult> resendOTP(@RequestBody AuthRequestDTO request) {
        ApiResult result = new ApiResult();
        try {
            if(sendOtp(request.email, request.getClientId()))
                result.success = true;
        }catch (Exception e) {
            result.success = false;
            result.dto = null;
            result.failureMessage = "Something went wrong";
        }
        return Utils.monoFromObject(result);
    }

    /**
     * Verifying applicant email for forgot password request
     * @param request
     * @return ApiResult
     */
    @RequestMapping(value = "/verifyApplicantEmail", method = RequestMethod.POST)
    public Mono<ApiResult> verifyApplicantEmail(@RequestBody AuthRequestDTO request) {
        ApiResult result = new ApiResult();
        try {
            if(!Utils.isNullOrEmpty(request.email)){
                if(Utils.isValidEmail(request.email)){
                    EmpApplnRegistrationsDBO bo = employeeApplicationTransaction.isApplicantRegistered(request.email);
                    if(!Utils.isNullOrEmpty(bo)){
                        //send email otp
                        sendOtp(request.email, request.getClientId());
                        result.success = true;
                    }else{
                        result.failureMessage = "This Email ID is not registered with us.";
                    }
                }else{
                    result.failureMessage = "Email ID is not valid";
                }
            }else{
                result.failureMessage = "Please enter Email ID";
            }
        }catch (Exception e) {
            e.printStackTrace();
            result.failureMessage = "Something went wrong";
        }
        return Utils.monoFromObject(result);
    }

    /**
     * Verify Otp
     * @param request
     * @return ApiResult
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/verifyOtp", method = RequestMethod.POST)
    public Mono<ApiResult> verifyOtp(@RequestBody AuthRequestDTO request) {
        ApiResult result = new ApiResult();
        try {
            if(!Utils.isNullOrEmpty(request.getOtp())){
                if(!Utils.isNullOrEmpty(request.email) && !Utils.isNullOrEmpty(request.getClientId())){
                    otpService.verifyClientOtp(result, request.email, request.getClientId(), request.getOtp());
                } else {
                    result.failureMessage = "Something went wrong.";
                }
            } else{
                result.failureMessage = "Please enter OTP.";
            }
        }catch (Exception e) {
            e.printStackTrace();
            result.failureMessage = "Something went wrong";
        }
        return Utils.monoFromObject(result);
    }

    /**
     * change applicant password if forgot
     * @param request
     * @return ApiResult
     */
    @RequestMapping(value = "/changeApplicantPassword", method = RequestMethod.POST)
    public Mono<ApiResult> changeApplicantPassword(@RequestBody AuthRequestDTO request) {
        ApiResult result = new ApiResult();
        try {
            if(!Utils.isNullOrEmpty(request.email) && !Utils.isNullOrEmpty(request.password)){
                EmpApplnRegistrationsDBO bo = employeeApplicationTransaction.isApplicantRegistered(request.email);
                if(!Utils.isNullOrEmpty(bo)){
                    bo.passwd = this._passwordEncoder.encode(request.password);
                    bo.recordStatus = 'A';
                    boolean updatePassword = employeeApplicationTransaction.updateApplicantPassword(bo);
                    if(updatePassword)
                        result.success = true;
                    else
                        result.failureMessage = "Reset password failed";
                }else{
                    result.failureMessage = "This Email ID is not registered with us.";
                }
            }else{
                result.failureMessage = "Please enter required fields";
            }
        }catch (Exception e) {
            e.printStackTrace();
            result.failureMessage = "Something went wrong";
        }
        return Utils.monoFromObject(result);
    }

    /**
     * Change password of an applicant
     * @param data
     * @return ApiResult
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public Mono<ApiResult> changePassword(@RequestBody Map<String,String> data) {
        ApiResult result = new ApiResult();
        try {
            if(!Utils.isNullOrEmpty(data.get("existingPwd")) && !Utils.isNullOrEmpty(data.get("newPwd"))){
                EmpApplnRegistrationsDBO bo = employeeApplicationTransaction.find(EmpApplnRegistrationsDBO.class, Integer.parseInt(data.get("empApplicationRegistrationId")));
                if(!Utils.isNullOrEmpty(bo)){
                    String newPwd = this._passwordEncoder.encode(data.get("newPwd"));
                    if(!bo.passwd.equalsIgnoreCase(this._passwordEncoder.encode(data.get("existingPwd")))){
                        result.failureMessage = "The current password is incorrect";
                    }else if(bo.passwd.equalsIgnoreCase(newPwd)){
                        result.failureMessage = " Current password and new password cannot be the same";
                    }else {
                        bo.passwd = newPwd;
                        bo.recordStatus = 'A';
                        boolean updatePassword = employeeApplicationTransaction.updateApplicantPassword(bo);
                        if(updatePassword)
                            result.success = true;
                        else
                            result.failureMessage = "Change Password is failed, Please try again";
                    }
                }else{
                    result.failureMessage = "Change Password is failed, Please try again";
                }
            }else{
                result.failureMessage = "Please enter required fields";
            }
        }catch (Exception e) {
            e.printStackTrace();
            result.failureMessage = "Something went wrong";
        }
        return Utils.monoFromObject(result);
    }

    public boolean sendOtp(String email, String clientId){
        boolean isMailSend = false;
        int otp = otpService.generateClientOtp(email, clientId);
        ErpTemplateDBO erpTemplateDBO = employeeApplicationTransaction.getApplicantEmailOtpTemplate("EMPLOYEE_APPLICANT_OTP_EMAIL");
        if(!Utils.isNullOrEmpty(erpTemplateDBO)){
            String otpTemplateContent = erpTemplateDBO.getTemplateContent();
            if(!Utils.isNullOrEmpty(otpTemplateContent))
                otpTemplateContent = otpTemplateContent.replace("[OTP]", String.valueOf(otp));
            isMailSend = emailService.sendEmail(email, erpTemplateDBO.getMailFromName(), erpTemplateDBO.getMailSubject(), otpTemplateContent, 1);
        }
        return isMailSend;
    }
}