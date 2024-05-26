package com.christ.erp.services.dbobjects.hostel.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="hostel_fine_category")
@Getter
@Setter
public class HostelFineCategoryDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="hostel_fine_category_id")
	private int id;
	
	@Column(name ="fine_category")
	private String fineCategory;
	
	@Column(name ="fine_amount")
	private Integer fineAmount;
	
	@ManyToOne
	@JoinColumn(name ="hostel_id")
	private HostelDBO hostelDBO;
	
	@Column(name ="is_absent_fine")
	private Boolean isAbsentFine;
	
	@Column(name ="is_disciplinary_fine")
	private Boolean isDisciplinaryFine;
	
	@Column(name ="is_others_fine")
	private Boolean isOthersFine;
	
	@ManyToOne
	@JoinColumn(name ="acc_fee_heads_id")
	private AccFeeHeadsDBO accFeeHeadsDBO;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
}
