package com.christ.erp.services.controllers.employee.recruitment;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategoryDepartmentDBO;
import com.christ.erp.services.dbqueries.employee.RecruitmentQueries;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.letter.EmpLetterRequestDTO;
import com.christ.erp.services.dto.employee.recruitment.SubjectCategoryDepartmentDTO;
import com.christ.erp.services.dto.employee.settings.EmpApplnNumberGenerationDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.recruitment.SubjectCategoryDepartmentHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Employee/Recruitment/SubjectCategoryDepartment")
public class SubjectCategoryDepartmentController extends BaseApiController {

	SubjectCategoryDepartmentHandler subjectCategoryDepartmentHandler = SubjectCategoryDepartmentHandler.getInstance();
	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<SubjectCategoryDepartmentDTO>>> getGridData() {
		ApiResult<List<SubjectCategoryDepartmentDTO>> result = new ApiResult<>();
		try {
			List<SubjectCategoryDepartmentDTO> subjectCategoryDepartment = subjectCategoryDepartmentHandler
					.getGridData();
			if (!Utils.isNullOrEmpty(subjectCategoryDepartment)) {
				result.success = true;
				result.dto = subjectCategoryDepartment;
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

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateSubjectDepartment(@RequestBody SubjectCategoryDepartmentDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {

			ApiResult<ModelBaseDTO> to = subjectCategoryDepartmentHandler.saveOrUpdateSubjectDepartment(data,
					userId);
			if (to.failureMessage == null || to.failureMessage.isEmpty()) {
				result.success = true;
			} else {
				result.success = false;
				result.failureMessage = to.failureMessage;
			}

		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<SubjectCategoryDepartmentDTO>> selectsubjectCategoryDepartment(
			@RequestParam("id") String id) {
		ApiResult<SubjectCategoryDepartmentDTO> result = new ApiResult<SubjectCategoryDepartmentDTO>();
		SubjectCategoryDepartmentDTO subjectCategoryDepartmentDTO = subjectCategoryDepartmentHandler
				.selectsubjectCategoryDepartment(id);
		if (subjectCategoryDepartmentDTO.id != null) {
			result.dto = subjectCategoryDepartmentDTO;
			result.success = true;
		} else {
			result.dto = null;
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> deleteSubjectDepartment(@RequestBody SubjectCategoryDepartmentDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			result.success = subjectCategoryDepartmentHandler.deleteSubjectDept(data.category.value, userId);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}