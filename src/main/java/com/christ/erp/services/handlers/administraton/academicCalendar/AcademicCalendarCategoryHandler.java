package com.christ.erp.services.handlers.administraton.academicCalendar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarCategoryRecipientsDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarCategoryDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.administraton.academicCalendar.AcademicCalendarCategoryTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AcademicCalendarCategoryHandler {

	@Autowired
	AcademicCalendarCategoryTransaction academicCalendarCategoryTransaction;

	public Flux<ErpCalendarCategoryDTO> getGridData() {
		return academicCalendarCategoryTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	private ErpCalendarCategoryDTO convertDboToDto(ErpCalendarCategoryDBO dbo) {
		ErpCalendarCategoryDTO dto = new ErpCalendarCategoryDTO();
		BeanUtils.copyProperties(dbo, dto);
		var selectDtoList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(dbo.getErpCalendarCategoryRecipientsDBOSet())) {
			dbo.getErpCalendarCategoryRecipientsDBOSet().forEach(s-> {
				var selectDto = new SelectDTO();
				selectDto.setValue(String.valueOf(s.getEmpDBO().getId()));
				selectDto.setLabel(s.getEmpDBO().getEmpName()+"("+s.getEmpDBO().getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName()+")");	
				selectDtoList.add(selectDto);
			});
			dto.setEmployeList(selectDtoList);
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ErpCalendarCategoryDTO> dto, String userId) {
		return dto.handle((erpAcademicCalendarCategoryDTO, synchronousSink) -> {
			boolean istrue = academicCalendarCategoryTransaction.duplicateCheck(erpAcademicCalendarCategoryDTO);
			if (istrue) {
				synchronousSink.error(new DuplicateException("The Category is already added"));
			} else {
				synchronousSink.next(erpAcademicCalendarCategoryDTO);
			}
		}).cast(ErpCalendarCategoryDTO.class).map(data -> convertDtoToDbo(data,userId)).flatMap(s -> {
			if (!Utils.isNullOrEmpty(s.getId())) {
				academicCalendarCategoryTransaction.update(s);
			} else {
				academicCalendarCategoryTransaction.save(s);
			}
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}

	private ErpCalendarCategoryDBO convertDtoToDbo(ErpCalendarCategoryDTO dto, String userId) {
		ErpCalendarCategoryDBO dbo = Utils.isNullOrEmpty(dto.getId()) ? new ErpCalendarCategoryDBO(): academicCalendarCategoryTransaction.edit(dto.getId());
		dbo.setCategoryName(dto.getCategoryName().replaceAll("\\s+"," ").trim());
		dbo.setReminderDays(dto.getReminderDays());
		var dboRecipientSet = Utils.isNullOrEmpty(dbo.getId()) ? null: dbo.getErpCalendarCategoryRecipientsDBOSet();
		Map<Integer, ErpCalendarCategoryRecipientsDBO> recipientmap1 = new LinkedHashMap<Integer, ErpCalendarCategoryRecipientsDBO>();
		if(!Utils.isNullOrEmpty(dboRecipientSet)) {
			recipientmap1 = dboRecipientSet.stream().filter(s->s.getRecordStatus()=='A').collect(Collectors.toMap(s->s.getEmpDBO().getId(), s->s));
		}		
		var recipientmap = recipientmap1;
		if (!Utils.isNullOrEmpty(dto.getEmployeList())) {
			var recipientSet = new LinkedHashSet<ErpCalendarCategoryRecipientsDBO>();
			dto.getEmployeList().forEach(s-> {
				if (!Utils.isNullOrEmpty(s)) {
					ErpCalendarCategoryRecipientsDBO erpAcademicCalendarCategoryRecipientsDBO = null;
					if (recipientmap.containsKey(Integer.parseInt(s.getValue()))) {
						erpAcademicCalendarCategoryRecipientsDBO = recipientmap.get(Integer.parseInt(s.getValue()));
						erpAcademicCalendarCategoryRecipientsDBO.setModifiedUsersId(Integer.parseInt(userId));
						recipientmap.remove(Integer.parseInt(s.getValue()));
					} else {
						erpAcademicCalendarCategoryRecipientsDBO = new ErpCalendarCategoryRecipientsDBO();
					}
					erpAcademicCalendarCategoryRecipientsDBO.setErpCalendarCategoryDBO(dbo);
					erpAcademicCalendarCategoryRecipientsDBO.setEmpDBO(new EmpDBO());
					erpAcademicCalendarCategoryRecipientsDBO.getEmpDBO().setId(Integer.parseInt(s.getValue()));
					erpAcademicCalendarCategoryRecipientsDBO.setCreatedUsersId(Integer.parseInt(userId));
					erpAcademicCalendarCategoryRecipientsDBO.setRecordStatus('A');			
					recipientSet.add(erpAcademicCalendarCategoryRecipientsDBO);								
				}			
			});
			if (!Utils.isNullOrEmpty(recipientmap)) {
				recipientmap.forEach((emptId, valueEmp) -> {
					if (!Utils.isNullOrEmpty(valueEmp)) {
						valueEmp.setModifiedUsersId(Integer.parseInt(userId));
						valueEmp.setRecordStatus('D');
						recipientSet.add(valueEmp);
					}
				});
			}
			if (!Utils.isNullOrEmpty(dbo.getId())) {
				dbo.setModifiedUsersId(Integer.parseInt(userId));
			}
			dbo.setCreatedUsersId(Integer.parseInt(userId));
			dbo.setRecordStatus('A');	
			dbo.setErpCalendarCategoryRecipientsDBOSet(recipientSet);		
		}
		return dbo;
	}

	public Mono<ErpCalendarCategoryDTO> edit(int id) {		
		return Mono.just( academicCalendarCategoryTransaction.edit(id)).map(this::convertDboToDto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return academicCalendarCategoryTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}
}
