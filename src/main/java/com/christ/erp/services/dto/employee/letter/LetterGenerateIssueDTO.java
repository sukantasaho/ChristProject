package com.christ.erp.services.dto.employee.letter;	

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.christ.erp.services.dto.common.SelectDTO;
public class LetterGenerateIssueDTO {
	public Integer loggedinUserId;
	public String location;
	public String department;
	public String deneary;
	public String employeeName;
	public String employeeId;
	public SelectDTO status;
	public String fromDate;
	public String toDate;
	public List<LetterGenerateIssueListDTO> requestList;
	public String letterUrl;
	public String fileName;
	public String template; 
	public ResponseEntity<byte[]> file;
}

