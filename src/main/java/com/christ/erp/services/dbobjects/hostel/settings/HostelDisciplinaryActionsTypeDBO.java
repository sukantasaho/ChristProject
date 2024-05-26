package com.christ.erp.services.dbobjects.hostel.settings;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelDisciplinaryActionsDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="hostel_disciplinary_actions_type")
@Getter
@Setter 

public class HostelDisciplinaryActionsTypeDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_disciplinary_actions_type_id")
	private int id;
	
	@Column(name = "hostel_disciplinary_actions")
	private String hostelDisciplinaryActions;
	
	@Column(name = "fine_amount")
	private int fineAmount;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy = "hostelDisciplinaryActionsTypeDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   	private Set<HostelDisciplinaryActionsDBO> hostelDisciplinaryActionsDBOSet = new HashSet<>();
	
	
}
