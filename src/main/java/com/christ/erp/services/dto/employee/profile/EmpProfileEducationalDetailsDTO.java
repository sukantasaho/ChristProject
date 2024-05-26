package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class EmpProfileEducationalDetailsDTO {
    private int id;
    private SelectDTO qualification;
    private Integer qualificationId;
    private String qualificationOthers;
    private String status;
    private String course;
    private String specialisation;
    private Integer yearOfRegistration;
    private String gradeOrPercentage;
    private SelectDTO country;
    private SelectDTO state;
    private String boardOther;
    private SelectDTO board;
    private SelectDTO institute;
    private String instituteOther;
    private String stateOther;
    private SelectDTO highestQualification;
    private String highestQualificationForAlbum;
    private List<EmpProfileEdnDetailsDocumentsDTO> EmpProfileEdnDetailsDocumentsDTOList;
    public EmpProfileEducationalDetailsDTO(){

    }

    public EmpProfileEducationalDetailsDTO(Integer id, Integer qualificationId, String qualificationName,
           String status, String course, String specialisation, Integer yearOfRegistration, Integer countryId, String countryName, Integer stateId,
           String stateName, String boardOther, Integer instituteId, String instituteName, Integer highestQualificationId,  String highestQualificationName,
           String highestQualificationForAlbum, Integer boardId, String boardName, String instituteOther, String stateOther, String qualificationOthers, String gradeOrPercentage){

        if(!Utils.isNullOrEmpty(qualificationId)) {
            this.qualificationId = qualificationId;
            this.qualification = new SelectDTO();
            this.qualification.setLabel(qualificationName);
            this.qualification.setValue(Integer.toString(qualificationId));
        }
        this.id = id;
        this.status = status;
        this.course = course;
        this.specialisation = specialisation;
        this.yearOfRegistration = yearOfRegistration;
        this.gradeOrPercentage = gradeOrPercentage;
        if(!Utils.isNullOrEmpty(countryId)) {
            this.country = new SelectDTO();
            this.country.setLabel(countryName);
            this.country.setValue(Integer.toString(countryId));
        }
        if(!Utils.isNullOrEmpty(stateId)) {
            this.state = new SelectDTO();
            this.state.setLabel(stateName);
            this.state.setValue(Integer.toString(stateId));
        }
        this.boardOther = boardOther;
        if(!Utils.isNullOrEmpty(instituteId)) {
            this.institute = new SelectDTO();
            this.institute.setLabel(instituteName);
            this.institute.setValue(Integer.toString(instituteId));
        }
        if(!Utils.isNullOrEmpty(highestQualificationId)) {
            this.highestQualification = new SelectDTO();
            this.highestQualification.setLabel(highestQualificationName);
            this.highestQualification.setValue(Integer.toString(highestQualificationId));
        }
        if(!Utils.isNullOrEmpty(boardId)) {
            this.board = new SelectDTO();
            this.board.setLabel(boardName);
            this.board.setValue(Integer.toString(boardId));
        }
        this.highestQualificationForAlbum = highestQualificationForAlbum;
        this.instituteOther = instituteOther;
        this.stateOther = stateOther;
        this.qualificationOthers = qualificationOthers;


    }
}
