package com.christ.erp.services.controllers.employee.letter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.common.CommonDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.employee.letter.LetterGenerateIssueDTO;
import com.christ.erp.services.dto.employee.letter.LetterGenerateIssueListDTO;
import com.christ.erp.services.dto.employee.recruitment.InterviewScoreEntryDTO;
import com.christ.erp.services.handlers.employee.letter.LetterGenerateIssueHandler;
import com.christ.utility.lib.Constants;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Employee/Letter/LetterGenerateIssue")
public class LetterGenerateIssueController {

	@Autowired
	LetterGenerateIssueHandler letterGenerateIssueHandler;
	
	@RequestMapping(value = "/getLocationofUser", method = RequestMethod.POST)
	public Mono<ApiResult<LetterGenerateIssueDTO>> getLoggedInUserId(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<LetterGenerateIssueDTO> result = new ApiResult<LetterGenerateIssueDTO>();
		result.dto = new LetterGenerateIssueDTO();
		//return getUserID().flatMap(userId -> {
			result.dto.loggedinUserId = Integer.parseInt(userId);
			if(Integer.parseInt(userId) >0) {
				try {
					letterGenerateIssueHandler.getEmployeeLocation(Integer.parseInt(userId),result);
				} catch (Exception e) {
					result.dto = null;
					result.success=false;
					result.failureMessage = "error in getting Location information";
					e.printStackTrace();
				}
			}
			return Mono.justOrEmpty(result);
		//});
	}
	
	@RequestMapping(value = "/getLetterRequestStatusList", method = RequestMethod.POST)
    public Mono<ApiResult<List<CommonDTO>>> getLetterRequestStatusList() {
        ApiResult<List<CommonDTO>> result = new ApiResult<List<CommonDTO>>();
        try {
        	letterGenerateIssueHandler.getLetterRequestStatusList(result);
        }
        catch(Exception error) {
        	result.dto = null;
        	result.failureMessage = error.getMessage();
        	result.success = false;
            Utils.log(error.getMessage());
        }
        return Mono.justOrEmpty(result);
    }
	
	@RequestMapping(value = "/getLetterRequestlist", method = RequestMethod.POST)
	public Mono<ApiResult<LetterGenerateIssueDTO>> getLetterRequestlist(@RequestBody InterviewScoreEntryDTO interviewScoreEntryDTO , @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId ){
		ApiResult<LetterGenerateIssueDTO> result = new ApiResult<LetterGenerateIssueDTO>();
		result.dto = new LetterGenerateIssueDTO();
		//return getUserID().flatMap(userId -> {
			result.dto.loggedinUserId = Integer.parseInt(userId);
			if(Integer.parseInt(userId) >0) {
				try {
					List<LetterGenerateIssueListDTO> list = letterGenerateIssueHandler.getLetterRequestlist(Integer.parseInt(userId),result);
					if(!Utils.isNullOrEmpty(list)) {
						result.dto.requestList = list;
						result.success = true;
					}else {
						result.success=false;
						result.failureMessage = "No Data for the Search Criteria";
					}
				} catch (Exception e) {
					result.dto = null;
					result.success=false;
					result.failureMessage = "error in getting Location information";
					e.printStackTrace();
				}
			}
			return Mono.justOrEmpty(result);
		//});
	}
	
	@RequestMapping(value = "/getRequestListBySearch", method = RequestMethod.POST)
	public Mono<ApiResult<LetterGenerateIssueDTO>> getRequestListBySearch(@RequestBody LetterGenerateIssueDTO dto , @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<LetterGenerateIssueDTO> result = new ApiResult<LetterGenerateIssueDTO>();
		result.dto = new LetterGenerateIssueDTO();
		//return getUserID().flatMap(userId -> {
			result.dto.loggedinUserId = Integer.parseInt(userId);
			if(Integer.parseInt(userId) >0) {
				try {
					List<LetterGenerateIssueListDTO> list = letterGenerateIssueHandler.getLetterRequestlistBySearch(dto);
					if(!Utils.isNullOrEmpty(list)) {
						result.dto.requestList = list;
						result.success = true;
					}else {
						result.success=false;
						result.failureMessage = "No Data for the Search Criteria";
					}
				} catch (Exception e) {
					result.dto = null;
					result.success=false;
					result.failureMessage = "error in getting Location information";
					e.printStackTrace();
				}
			}
			return Mono.justOrEmpty(result);
		//});
	}
	

	@RequestMapping(value = "/updateSelectedRequests", method = RequestMethod.POST)
	public Mono<ApiResult<LetterGenerateIssueDTO>> updateSelectedRequests(@RequestBody LetterGenerateIssueDTO dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId ) throws IOException{
		ApiResult<LetterGenerateIssueDTO> result = new ApiResult<LetterGenerateIssueDTO>();
		//return getUserID().flatMap(userId -> {
			result.dto = new LetterGenerateIssueDTO();
			result.dto.loggedinUserId = Integer.parseInt(userId);
			dto.loggedinUserId = Integer.parseInt(userId);
			try {
				letterGenerateIssueHandler.updateRequestStatus(dto,result);
				} catch (Exception e) {
					result.dto = null;
					result.success=false;
					result.failureMessage = "error in getting Location information";
					e.printStackTrace();
				}
			 return Mono.justOrEmpty(result);
		//});
	       

	}
	
	 @RequestMapping(value = "/getLetterRequestPrint", method = RequestMethod.POST, headers = { "Accept=application/json" })
	public Mono<ApiResult<LetterGenerateIssueDTO>> getLetterPrint( @RequestBody LetterGenerateIssueListDTO dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws IOException{
		ApiResult<LetterGenerateIssueDTO> result = new ApiResult<LetterGenerateIssueDTO>();
		result.dto = new LetterGenerateIssueDTO();
		result.dto.loggedinUserId = Integer.parseInt(userId);
		String template = "";
			Document document=new Document();
			try {
				template = letterGenerateIssueHandler.getLetterPrint(dto, result);
				if (!Utils.isNullOrEmpty(template)) {
					template = template.replace("<br>", "<br />");
					result.dto.template = "<style>table,th,td{border-collapse: collapse;border: 1px solid black; !important;}</style>"+ template;
					String filename = result.dto.letterUrl;
					PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
					document.open();
					InputStream is = new ByteArrayInputStream(template.getBytes());
					try {
						XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
					} catch (Exception e) {
						e.printStackTrace();
						result.dto = null;
						result.success = false;
						result.failureMessage = "Failed to create pdf file";
					}
					document.close();
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_PDF);
					filename = "output.pdf";
					headers.setContentDispositionFormData(filename, filename);
					headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
					if (template.isEmpty()) {
						result.success = false;
						result.failureMessage = "Failed to get Template";
					} else {
						result.dto.file = new ResponseEntity<>(template.getBytes(), headers, HttpStatus.OK);
						result.success = true;
					}
				}else {
					result.dto = null;
					result.success=false;
					result.failureMessage = "Failed to get Template";
				}

			} catch (Exception e) {
						result.dto = null;
						result.success=false;
						result.failureMessage = "error in getting Location information";
						e.printStackTrace();
					}
					 
				
				    return  Mono.justOrEmpty(result);

	}
	 @RequestMapping(value = "/getEmpRequestLetter", method = RequestMethod.POST, headers = { "Accept=application/json" })
	    public Mono<ApiResult<LetterGenerateIssueDTO>> getEmpRequestLetter(@RequestParam("requestId") String requestId) throws IOException {
	    	ApiResult<LetterGenerateIssueDTO> result = new ApiResult<LetterGenerateIssueDTO>();
	        try {
	        	if (requestId != null) {
	        		letterGenerateIssueHandler.getEmpRequestLetter(requestId,result);
	            }
	        }catch(Exception ex){
	        }

	        return Mono.justOrEmpty(result);
	    }
	 @RequestMapping(value = "/getEmpRequestLetter1", method = RequestMethod.POST, headers = { "Accept=application/json" })
	    public Mono<Void> getEmpRequestLetternew(ServerHttpResponse response,@RequestParam("requestId") String requestId) throws IOException {
	    	ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
	    	ApiResult<LetterGenerateIssueDTO> result = new ApiResult<LetterGenerateIssueDTO>();
	        try {
	        	if (requestId != null) {
	        		letterGenerateIssueHandler.getEmpRequestLetter(requestId,result);
	            }
	        }catch(Exception ex){
	        }

	        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+result.dto.fileName+".pdf");
	        response.getHeaders().setContentType(MediaType.APPLICATION_PDF);
	        Resource resource = new FileSystemResource(result.dto.letterUrl);
	        File file = resource.getFile();
	        return zeroCopyResponse.writeWith(file, 0, file.length());
	    }
//		@RequestMapping(value = "/getLetterRequestPrint", method = RequestMethod.POST)
//		public Mono<ApiResult<LetterGenerateIssueDTO>> getLetterPrint(@RequestParam("requestId") String requestId,@RequestParam("employeeId") String employeeId) throws IOException{
//			ApiResult<LetterGenerateIssueDTO> result = new ApiResult<LetterGenerateIssueDTO>();
//			ApiResult<LookupItemDTO> dtoresult = new ApiResult<LookupItemDTO>();
//////			ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
//////	        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=LetterRequestGenerate"+dto.employeeId+".pdf");
//////	        response.getHeaders().setContentType(MediaType.APPLICATION_PDF);
//				Document document=new Document();
//					try {
//						String template = LetterGenerateIssueHandler.getInstance().getLetterPrint(requestId,employeeId,result);
//						System.out.println(template);
//						String filename=EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH+requestId+".pdf";
//						PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
//	                    document.open();
//	                    InputStream is = new ByteArrayInputStream(template.getBytes());
//						XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
//					} catch (Exception e) {
//						result.dto = null;
//						result.success=false;
//						result.failureMessage = "error in getting Location information";
//						e.printStackTrace();
//					}
//					 document.close();
//					 
//				//Resource resource = new FileSystemResource(EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH+dto.employeeId+".pdf");
//		        //File file = resource.getFile();
//		        return Mono.justOrEmpty(result);
//	
//		}
	 
//	@RequestMapping(value="/getGenerateLetterofAppointmentPDF", method=RequestMethod.GET)
//	public Mono<Void> saveOrUpdate(ServerHttpResponse response,@RequestParam("id") String id,@RequestParam("empid") String empid) throws Exception {
//	ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
//	        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Appointmentletter"+empid+".pdf");
//	        response.getHeaders().setContentType(MediaType.APPLICATION_PDF);
//	        try {
//	        ErpTemplateDBO erpTemplateDBO =generateLetterForAppointmentHandler.getAppointmentLetterTemplateData(id);
//	        Tuple employee=null;
//	        if(erpTemplateDBO!= null)
//	        {
//	           //employee=generateLetterForAppointmentHandler.getEmpDetailsBasedOnEmpId(empid);
//	           System.out.println(employee.get("EmployeeName").toString());
//	            String template=erpTemplateDBO.templateContent.replace("[NAME]", employee.get("EmployeeName").toString()).replace("[DESIGNATION]", employee.get("Designation").toString()).replace("[DOJ]", employee.get("DateofJoining").toString()).replace("[SCALE]", employee.get("Scale").toString()).replace("[BASIC]", employee.get("Basic").toString()).replace("[DEPARTMENT]", employee.get("DepartmentName").toString());
//////	                 String filepath=APPOINTMENT_LETTER_FILESERVER_PATH;
//////	                 Map<String,byte[]> filemap=new HashMap<String, byte[]>();
//////	                 filemap.put("Appointmentletter"+empid+".pdf", template.getBytes());
//////	                 Utils.generateFiles(filemap, filepath);
//	            //GenerateLetterForAppointmentHandler.saveEmpAppointmentLatter(employee,empid,"1");
//	                Document document=new Document();
//	                try {
//	                String filename=EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH+"Appointmentletter"+empid+".pdf";
//	PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
//	                    document.open();
//	                    InputStream is = new ByteArrayInputStream(template.getBytes());
//	XMLWorkerHelper.getInstance().parseXHtml(writer, document, is, Charset.forName("UTF-8"));
//	} catch (DocumentException ex) {
//	ex.printStackTrace();
//	}catch ( FileNotFoundException ex) {
//	ex.printStackTrace();
//	}catch (IOException e) {
//	e.printStackTrace();
//	}
//	                document.close();  
//	           }
//	        }catch (IOException error) {
//	        }
//	        Resource resource = new FileSystemResource(EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH+"Appointmentletter"+empid+".pdf");
//	        File file = resource.getFile();
//	        return zeroCopyResponse.writeWith(file, 0, file.length());
//	}

	 
//	 @GetMapping("/downloadFile/{fileName:.+}")   
//	 public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) { 
//		//  Load file as Resource  
//		 Resource resource = fileStorageService.loadFileAsResource(fileName); 
//		 // Try to determine file's content type    
//		 String contentType = null;   
//		 try {      
//			 contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());   
//			 } catch (IOException ex) {   
//				 }      
//		 // Fallback to the default content type if type could not be determined  
//		 if(contentType == null) {     
//			 contentType = "application/octet-stream";  
//			 }        return ResponseEntity.ok()    
//					 .contentType(MediaType.parseMediaType(contentType))          
//					 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")   
//					 .body(resource);    
//			 }
//		 public Resource loadFileAsResource(String fileName) {
//			 try {
//				 Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
//				 Resource resource = new UrlResource(filePath.toUri());
//				 if(resource.exists()) {
//					 return resource;
//					 } else {
//						 throw new MyFileNotFoundException("File not found " + fileName);
//						 }        
//				 } catch (MalformedURLException ex) {
//					 throw new MyFileNotFoundException("File not found " + fileName, ex);
//					 }
//			 }
//	 
	 
	 
	
}
