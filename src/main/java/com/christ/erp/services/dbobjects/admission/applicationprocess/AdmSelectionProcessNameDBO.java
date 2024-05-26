package com.christ.erp.services.dbobjects.admission.applicationprocess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="adm_selection_process_name")
@Getter
@Setter
public class AdmSelectionProcessNameDBO  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_name_id")
	public int id;
	
	@Column(name = "selection_process_name")
	public String selectionProcessName;
	
	@Column(name = "short_name")
	public String shortName;
	
	@Column(name = "selection_process_type")
	public String selectionProcessType;
	
	@Column(name = "description")
	public String description;
	
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
	
	
}
