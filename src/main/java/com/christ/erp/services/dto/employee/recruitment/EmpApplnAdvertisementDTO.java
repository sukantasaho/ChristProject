package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpApplnAdvertisementDTO extends ExModelBaseDTO {
	public String id;
	public SelectDTO academicYear;
	public String advertisementNo;
//	public String startDate;
//	public String endDate;
	public String advertisementContent;
	public String uploadAdvertisementUrl;
	public String otherInfo;
	public char recordStatus;
	public List<EmpApplnAdvertisementImagesDTO> empApplnAdvertisementImages;
	public String fileName;
	public String fileExtension;
	public String year;
	public MultipartFile formData;
	public Boolean isCommonAdvertisement;
	public String templateType;
	private LocalDate startDate;
	private LocalDate endDate;
}
