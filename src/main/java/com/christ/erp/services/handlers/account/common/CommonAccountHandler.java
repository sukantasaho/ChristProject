package com.christ.erp.services.handlers.account.common;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisVaultKeyConfig;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dto.student.common.FirstYearStudentDTO;
import com.christ.utility.lib.Constants;
import com.google.gson.Gson;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.account.common.CommonAccountTransaction;

import reactor.core.publisher.Flux;

@Service
public class CommonAccountHandler {
	@Autowired
	CommonAccountTransaction commonAccountTransaction;
	@Autowired
	RedisVaultKeyConfig redisVaultKeyConfig;
	
	public Flux<SelectDTO> getBatchYear() {
		return commonAccountTransaction.getBatchYear().flatMapMany(Flux::fromIterable).map(this::convertErpProgrammeBatchwiseSettingsDBOToBatchYearSelectTO);
	}
	
	public SelectDTO convertErpProgrammeBatchwiseSettingsDBOToBatchYearSelectTO(ErpAcademicYearDBO erpAcademicYearDBO) {
		SelectDTO batchYearDTO = null;
		if(!Utils.isNullOrEmpty(erpAcademicYearDBO)) {
			batchYearDTO = new SelectDTO();
			batchYearDTO.setValue(Integer.toString(erpAcademicYearDBO.getId()));
			batchYearDTO.setLabel(erpAcademicYearDBO.getAcademicYearName());
		}
		return batchYearDTO;
		
	}
	
	public Flux<SelectDTO> getProgrammesByBatchYear(int batchYearId) {
		return commonAccountTransaction.getProgrammesByBatchYear(batchYearId).flatMapMany(Flux::fromIterable).map(this::convertErpProgrammeBatchwiseSettingsDBOToProgramSelectTO);
	}
	
	public SelectDTO convertErpProgrammeBatchwiseSettingsDBOToProgramSelectTO(ErpProgrammeDBO erpProgrammeDBO) {
		SelectDTO erpProgramDTO = null;
		if(!Utils.isNullOrEmpty(erpProgrammeDBO)) {
			erpProgramDTO = new SelectDTO();
			erpProgramDTO.setValue(Integer.toString(erpProgrammeDBO.getId()));
			erpProgramDTO.setLabel(erpProgrammeDBO.getProgrammeName());
		}
		return erpProgramDTO;
		
	}
	public String createJwsObject(String jsonString) {
		try {
			String secretKey = redisVaultKeyConfig.getServiceKeys(Constants.SERVICE_SIGN_KEY);
			JWSSigner signer = new MACSigner(secretKey.getBytes());
			JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
					.type(JOSEObjectType.JOSE_JSON)
					.build();
			JWSObject jwsObject = new JWSObject(header, new Payload(jsonString));
			jwsObject.sign(signer);
			return jwsObject.serialize();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	public ApiResult<FirstYearStudentDTO> verifyPaymentStatusRequest(String jwsObjectString)  {
		ApiResult<FirstYearStudentDTO> apiResult = new ApiResult<FirstYearStudentDTO>();
		try {
			String secretKey = redisVaultKeyConfig.getServiceKeys(Constants.SERVICE_SIGN_KEY);
			JWSObject jwsObject = JWSObject.parse(jwsObjectString);
			String payload;
			if (!Utils.isNullOrEmpty(jwsObject.getPayload())) {
				payload = jwsObject.getPayload().toString();
				Gson gson = new Gson();
				apiResult = gson.fromJson(payload, ApiResult.class);
				JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
				if(!jwsObject.verify(verifier)){
					apiResult.setSuccess(false);
					apiResult.setFailureMessage("Authentication failed");
				}
			}
		} catch (Exception e) {
			System.out.println("verifyPaymentStatusRequest exception:  "+e.getMessage());
		}
		return apiResult;
	}
	public Flux<SelectDTO> getBatchNameByProgramAndYear(int programId, int batchYearId) {
		return commonAccountTransaction.getBatchNameByProgramAndYear(programId, batchYearId).flatMapMany(Flux::fromIterable).map(this::convertAcaBatchDBOToAcaBatchSelectTO);
	}
	public SelectDTO convertAcaBatchDBOToAcaBatchSelectTO(AcaBatchDBO acaBatchDBO) {
		SelectDTO batchYearDTO = null;
		if(!Utils.isNullOrEmpty(acaBatchDBO)) {
			if(!Utils.isNullOrEmpty(acaBatchDBO.getBatchName())) {
				batchYearDTO = new SelectDTO();
				batchYearDTO.setValue(Integer.toString(acaBatchDBO.getId()));
				if(!Utils.isNullOrEmpty(acaBatchDBO.getErpCampusProgrammeMappingDBO()) && !Utils.isNullOrEmpty(acaBatchDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO())) {
					batchYearDTO.setLabel(acaBatchDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
				}
			}
		}
		return batchYearDTO;
	}

}
