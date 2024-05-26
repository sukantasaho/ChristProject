package com.christ.erp.services.dbobjects.account.settings;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="acc_fee_heads")
public class AccFeeHeadsDBO implements Serializable {
	
	private static final long serialVersionUID = -1637064875885958983L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_fee_heads_id")
	public int id;
	
	@Column(name="fee_heads_type")
	public String feeHeadsType;
	
	@Column(name="heading")
	public String heading;
	
	@ManyToOne
	@JoinColumn(name="acc_fee_group_id")
	public AccFeeGroupDBO accFeeGroupDBO;
	
	@Column(name="is_gst_applicable")
	public boolean isGstApplicable;
	
	@Column(name="is_cgst_applicable")
	public boolean isCgstApplicable;
	
	@Column(name="is_sgst_applicable")
	public boolean isSgstApplicable;
	
	@Column(name="is_igst_applicable")
	public boolean isIgstApplicable;
	
	@ManyToOne
	@JoinColumn(name="hostel_id")
	public HostelDBO hostelDBO;
	
	@ManyToOne
	@JoinColumn(name="hostel_room_type_id")
	public HostelRoomTypeDBO hostelRoomTypeDBO;
	
	@Column(name="is_fixed_amount")
	public boolean isFixedAmount;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accFeeHeadsDBO", cascade = CascadeType.ALL)
	public Set<AccFeeHeadsAccountDBO> accFeeHeadsAccountList = new HashSet<>();
}
