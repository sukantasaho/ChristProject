package com.christ.erp.services.dto.hostel.settings;

import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter

public class HostelDTO {

    public String id;
    public String hostelName;
    public String addressLineOne;
    public String addressLineTwo;
    public String phoneNumberOne;
    public String phoneNumberTwo;
    public String email;
    public String faxNo;
    public String pincode;
    public String pincodeId;
    public Integer onlineCancellationDays;
    public LookupItemDTO country;
    public LookupItemDTO state;
    public LookupItemDTO city;
    public LookupItemDTO forGender;
    public List<String> checked;
    private SelectDTO campus; 
    private List<HostelRoomTypeDTO> hostelRoomTypeDTO;
    private String hostelInformation;
    private String uploadImageUrl;
    public List<HostelImagesDTO> hostelImagesDTO;
    private String fileName;
	private String fileExtension;
	private HostelBlockDTO hostelBlockDTO;
	private List<HostelBlockDTO> hostelBlockDTOList;
	private List<SelectDTO> blockSelect;
}
