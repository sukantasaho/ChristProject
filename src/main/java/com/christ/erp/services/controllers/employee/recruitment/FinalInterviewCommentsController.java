package com.christ.erp.services.controllers.employee.recruitment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.employee.attendance.LetterTemplatesDTO;
import com.christ.erp.services.dto.employee.recruitment.FinalInterviewCommentsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.recruitment.FinalInterviewCommentsHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Mono;

@RestController
//@RequestMapping(value = "/Secured/Employee/Recruitment/FinalInterviewComments")
public class FinalInterviewCommentsController extends BaseApiController{
	FinalInterviewCommentsHandler finalInterviewCommentsHandler = FinalInterviewCommentsHandler.getInstance();

	@Autowired
	FinalInterviewCommentsHandler finalInterviewCommentsHandler1;

	@RequestMapping(value="/Secured/Employee/Recruitment/FinalInterviewComments/getFinalInterviewPrintData", method=RequestMethod.POST)
	public Mono<ApiResult<FinalInterviewCommentsDTO>> getFinalInterviewPrintData(@RequestParam("applicationNumber") String applicationNumber) {
		ApiResult<FinalInterviewCommentsDTO> result = new ApiResult<>();
		try {
			FinalInterviewCommentsDTO finalInterviewCommentsDTO = finalInterviewCommentsHandler1.getFinalInterviewPrintData(Integer.parseInt(applicationNumber));
			if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO)) {
				result.success = true;
				result.dto = finalInterviewCommentsDTO;
			}else {
				result.success = false;
				result.dto = null;
			}
		}catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/Protected/Employee/Recruitment/FinalInterviewComments/getLetterTemplate", method = RequestMethod.POST)
	public Mono<ApiResult<List<LetterTemplatesDTO>>> getLetterTemplate(@RequestParam("category") String category) {
		ApiResult<List<LetterTemplatesDTO>> result = new ApiResult<List<LetterTemplatesDTO>>();
		try {
			List<LetterTemplatesDTO>  letterTemplatesDTOs = finalInterviewCommentsHandler1.getLetterTemplate(category);
			if(!Utils.isNullOrEmpty(letterTemplatesDTOs)) {
				result.success = true;
				result.dto = letterTemplatesDTOs;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/FinalInterviewComments/getWorkExperienceView")
	public Mono<FinalInterviewCommentsDTO> getWorkExperienceView(@RequestParam String applicationId) {
		return finalInterviewCommentsHandler1.getWorkExperienceView(applicationId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/Secured/Employee/Recruitment/FinalInterviewComments/saveOrUpdateStage2Comments")
	public Mono<ResponseEntity<ApiResult<FinalInterviewCommentsDTO>>> saveOrUpdateStage2Comments(@RequestBody FinalInterviewCommentsDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return finalInterviewCommentsHandler1.saveOrUpdateStage2Comments(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/FinalInterviewComments/getStage2InterviewCommentsData")
	public Mono<ApiResult<FinalInterviewCommentsDTO>>  getStage2InterviewCommentsData(@RequestParam String applicationNumber) {
		return finalInterviewCommentsHandler1.getStage2InterviewCommentsData(applicationNumber).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/FinalInterviewComments/getStage3InterviewCommentsData")
	public Mono<ApiResult<FinalInterviewCommentsDTO>> getStage3InterviewCommentsData(@RequestParam String applicationNumber) {
		return finalInterviewCommentsHandler1.getStage3InterviewCommentsData(applicationNumber).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/Secured/Employee/Recruitment/FinalInterviewComments/saveOrUpdateStageThree")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateStageThree(@RequestBody Mono<FinalInterviewCommentsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return finalInterviewCommentsHandler1.saveOrUpdateStageThree(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/Secured/Employee/Recruitment/FinalInterviewComments/getOfferLetterPreview")
	public Mono<ApiResult<String>> getOfferLetterPreview(@RequestParam String applicationNumber, @RequestParam String selectionStatus, @RequestParam String templateId) {
		return finalInterviewCommentsHandler1.getOfferLetterPreview(applicationNumber, selectionStatus,templateId ).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	//	private final static String OFFER_LETTER_FILESERVER_PATH;
	//
	//	static {
	//		OFFER_LETTER_FILESERVER_PATH = AppProperties.get("erp.emp.offerletter.fileserver.path");
	//	}
	//		@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
	//		public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody FinalInterviewCommentsDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	//			ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
	//			try {
	//				result.success = finalInterviewCommentsHandler1.saveOrUpdate(data, userId);
	//				if(result.success) {
	//					result.success = true;
	//					result.dto = new ModelBaseDTO();
	//					result.dto.id = String.valueOf(data.id);
	//				}else {
	//					result.success = false;
	//					result.dto = null;
	//				}
	//			}catch (Exception error) {
	//				result.success = false;
	//				result.dto = null;
	//				result.failureMessage = error.getMessage();
	//			}
	//			return Utils.monoFromObject(result);
	//		}

	//		@RequestMapping(value="/getFinalInterviewCommentData", method=RequestMethod.POST)
	//		public Mono<ApiResult<FinalInterviewCommentsDTO>> getFinalInterviewCommentData(@RequestParam("applicationNumber") String applicationNumber) {
	//			ApiResult<FinalInterviewCommentsDTO> result = new ApiResult<>();
	//			try {
	//				FinalInterviewCommentsDTO finalInterviewCommentsDTO = finalInterviewCommentsHandler1.editFinalInterviewComments(Integer.parseInt(applicationNumber));
	//				if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO)) {
	//					result.success = true;
	//					result.dto = finalInterviewCommentsDTO;
	//				}else {
	//					result.success = false;
	//					result.dto = null;
	//				}
	//			}catch (Exception error) {
	//				result.success = false;
	//				result.dto = null;
	//				result.failureMessage = error.getMessage();
	//			}
	//			return Utils.monoFromObject(result);
	//		}

	//
	//		@RequestMapping(value="/generateOfferLetter", method=RequestMethod.POST) // vccomments removed so need to update
	//		public Mono<ApiResult<ModelBaseDTO>> generateOfferLetter(ServerHttpResponse response,@RequestParam("id") String id,@RequestParam("applicationNumber") String applicationNumber,@RequestParam("isOfferLetterRegenerated") Boolean isOfferLetterRegenerated,@RequestParam("isVcComment") Boolean isVcComment, @RequestParam("selected") boolean selected,@RequestParam("vcComments") String vcComments,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	//			ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
	//			try {
	//				String applicationStatus = null;
	//				String filename=OFFER_LETTER_FILESERVER_PATH+"Offerletter"+applicationNumber.trim()+".pdf";
	//				Document document=new Document();
	//				response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Offerletter"+applicationNumber.trim()+".pdf");
	//				response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
	//				ErpTemplateDBO	erpTemplateDBO = finalInterviewCommentsHandler1.getOfferLetterTemplateData(id.trim());
	//				if(erpTemplateDBO!= null) {
	//					String template = finalInterviewCommentsHandler1.getFinalTemplateData(erpTemplateDBO.templateContent,applicationNumber.trim());
	//					try {
	//						PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
	//						document.open();
	//						InputStream is = new ByteArrayInputStream(template.getBytes());
	//						XMLWorkerHelper.getInstance().parseXHtml(writer, document, is, Charset.forName("UTF-8"));
	//						applicationStatus = finalInterviewCommentsHandler1.saveOrUpdateOfferLetterDetails(filename,applicationNumber.trim(),isOfferLetterRegenerated, userId,isVcComment,selected,vcComments,id.trim());
	//						if(!Utils.isNullOrEmpty(applicationStatus)) {
	//							result.success = true;
	//							result.dto = new ModelBaseDTO();
	//							result.dto.tag = applicationStatus;
	//						}else {
	//							result.success = false;
	//							result.dto = null;
	//						}
	//					}catch (DocumentException ex) {
	//						ex.printStackTrace();
	//					}catch ( FileNotFoundException ex) {
	//						ex.printStackTrace();
	//					}catch (IOException e) {
	//						e.printStackTrace();
	//					}
	//					document.close();
	//				}
	//			}catch (Exception error) {
	//				result.success = false;
	//				result.dto = null;
	//				result.failureMessage = error.getMessage();
	//			}
	//			return Utils.monoFromObject(result);
	//		}

	//		@RequestMapping(value="/offerLetter", method=RequestMethod.GET)
	//		public Mono<Void> offerLetter(ServerHttpResponse response,@RequestParam("applicationNumber") String applicationNumber) throws Exception {
	//			ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
	//			response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Offerletter"+applicationNumber+".pdf");
	//			response.getHeaders().setContentType(MediaType.APPLICATION_PDF);
	//			String filename=OFFER_LETTER_FILESERVER_PATH+"Offerletter"+applicationNumber+".pdf";
	//			Resource resource = new FileSystemResource(filename);
	//			File file = resource.getFile();
	//			return zeroCopyResponse.writeWith(file, 0, file.length());
	//		}

}