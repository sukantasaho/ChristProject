package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "obe_programme_outcome_upload_details")
@Getter
@Setter
public class ObeProgrammeOutcomeUploadDetailsDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "obe_programme_outcome_upload_details_id")
    private int id;
    
   	@ManyToOne
   	@JoinColumn(name = "obe_programme_outcome_id")
   	private ObeProgrammeOutcomeDBO obeProgrammeOutcomeDBO;
    
    @Column(name = "document_url")
   	private String documentUrl;
   	
   	@Column(name = "created_users_id",updatable=false)
   	private Integer createdUsersId;
   	
   	@Column(name = "modified_users_id")
   	private Integer modifiedUsersId;
   	
   	@Column(name = "record_status")
   	private char recordStatus;
   	

}
