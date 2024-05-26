package com.christ.erp.services.controllers.employee.recruitment;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import javax.persistence.Tuple;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.AppProperties;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpAppointmentLetterDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.GenerateLetterofAppointmentDTO;
import com.christ.erp.services.handlers.employee.recruitment.GenerateLetterForAppointmentHandler;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/Secured/Employee/Recruitment/GenerateLetterForAppointment")
public class GenerateLetterForAppointmentController extends BaseApiController {
	
	GenerateLetterForAppointmentHandler generateLetterForAppointmentHandler=GenerateLetterForAppointmentHandler.getInstance();
	
	private final static String APPOINTMENT_LETTER_FILESERVER_PATH;
	
	static {
		APPOINTMENT_LETTER_FILESERVER_PATH = AppProperties.get("erp.emp.appointmentletter.fileserver.path");
	}
	
	@RequestMapping(value="/getGenerateLetterofAppointmentPDF", method=RequestMethod.GET)
	public Mono<Void> saveOrUpdate(ServerHttpResponse response,@RequestParam("id") String id,@RequestParam("empid") String empid) throws Exception {
		ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Appointmentletter"+empid+".pdf");
        response.getHeaders().setContentType(MediaType.APPLICATION_PDF);
        try {
        	ErpTemplateDBO	erpTemplateDBO	=generateLetterForAppointmentHandler.getAppointmentLetterTemplateData(id);
        	Tuple employee=null;
        	if(erpTemplateDBO!= null)
        	{
        	    employee=generateLetterForAppointmentHandler.getEmpDetailsBasedOnEmpId(empid);
            	String template=erpTemplateDBO.templateContent.replace("[NAME]", String.valueOf(employee.get("EmployeeName"))).replace("[DESIGNATION]", String.valueOf(employee.get("Designation"))).replace("[DOJ]", String.valueOf(employee.get("DateofJoining"))).replace("[SCALE]", String.valueOf(employee.get("Scale"))).replace("[BASIC]", String.valueOf(employee.get("Basic"))).replace("[DEPARTMENT]", String.valueOf(employee.get("DepartmentName")));
//                	String filepath=APPOINTMENT_LETTER_FILESERVER_PATH;
//                	Map<String,byte[]> filemap=new HashMap<String, byte[]>();
//                	filemap.put("Appointmentletter"+empid+".pdf", template.getBytes());
//                	Utils.generateFiles(filemap, filepath);
            	//GenerateLetterForAppointmentHandler.saveEmpAppointmentLatter(employee,empid,"1");
                Document document=new Document();
                try {
                	String filename=APPOINTMENT_LETTER_FILESERVER_PATH+"Appointmentletter"+empid+".pdf";
					PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
                    document.open();
                    InputStream is = new ByteArrayInputStream(template.getBytes());
					XMLWorkerHelper.getInstance().parseXHtml(writer, document, is, Charset.forName("UTF-8"));
				} catch (DocumentException ex) {
					ex.printStackTrace();
				}catch ( FileNotFoundException ex) {
					ex.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
                document.close();   
       	    }
        }catch (IOException error) {
        }
        Resource resource = new FileSystemResource(APPOINTMENT_LETTER_FILESERVER_PATH+"Appointmentletter"+empid+".pdf");
        File file = resource.getFile();
        return zeroCopyResponse.writeWith(file, 0, file.length());
	}
	 
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestParam("empid") String empid) {
		ApiResult result = new ApiResult();
        try {
            EmpAppointmentLetterDBO empAppointmentLatter=generateLetterForAppointmentHandler.getEmpAppointmentLetter(empid);
            if(empAppointmentLatter != null) {
            	File file = new File(empAppointmentLatter.generatedAppointmentLetterUrl);
            	file.delete();
            	generateLetterForAppointmentHandler.delete(empAppointmentLatter);
                result.success = true; 
                result.dto = new ModelBaseDTO();
            }
        }catch (Exception e) {
             
            result.success = false;
            result.failureMessage = e.getMessage();
        }
	    return Utils.monoFromObject(result);
	}
	 
    @RequestMapping(value = "/getEmpAppointmentLetter", method = RequestMethod.GET)
    public Mono<Void> getEmpAppointmentLetter(ServerHttpResponse response,@RequestParam("empid") String empid) throws IOException {
    	ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
        EmpAppointmentLetterDBO ealdbo=new EmpAppointmentLetterDBO();
        try {
        	if (empid != null) {
        		EmpAppointmentLetterDBO header=generateLetterForAppointmentHandler.getEmpAppointmentLetter(empid);
                ealdbo.generatedAppointmentLetterUrl=header.generatedAppointmentLetterUrl;
            }
        }catch(Exception ex){
        }
        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Appointmentletter"+empid+".pdf");
        response.getHeaders().setContentType(MediaType.APPLICATION_PDF);
        Resource resource = new FileSystemResource(ealdbo.generatedAppointmentLetterUrl);
        File file = resource.getFile();
        return zeroCopyResponse.writeWith(file, 0, file.length());
    }
	        
	@RequestMapping(value = "/getGenerateLetterofAppointmentList", method = RequestMethod.POST)
	public Mono<ApiResult<List<GenerateLetterofAppointmentDTO>>> getGenerateLetterofAppointmentList(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate ,@RequestParam("location") String location,@RequestParam("campus") String campus)    {
	    ApiResult<List<GenerateLetterofAppointmentDTO>> result = new ApiResult<List<GenerateLetterofAppointmentDTO>>();
	    try {
           result.dto= generateLetterForAppointmentHandler.getGenerateLetterofAppointmentList(startDate,endDate,location,campus);
           result.success = true;
	    }catch(Exception error) {
	           Utils.log(error.getMessage());
	    }
	    return Utils.monoFromObject(result);
	}
	    
	@RequestMapping(value = "/getGenerateLetterofAppointmentPending", method = RequestMethod.POST)
	public Mono<ApiResult<List<GenerateLetterofAppointmentDTO>>> getGenerateLetterofAppointmentPending(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,@RequestParam("location") String location,@RequestParam("campus") String campus )    {
	   ApiResult<List<GenerateLetterofAppointmentDTO>> result = new ApiResult<List<GenerateLetterofAppointmentDTO>>();
       try {
           result.dto=  generateLetterForAppointmentHandler.getGenerateLetterofAppointmentPending(startDate,endDate,location,campus);
           result.success = true; 
       }
       catch(Exception error) {
           Utils.log(error.getMessage());
       }
       return Utils.monoFromObject(result);
	} 
}
