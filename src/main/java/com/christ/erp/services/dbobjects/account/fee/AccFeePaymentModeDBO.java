package com.christ.erp.services.dbobjects.account.fee;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="acc_fee_payment_mode")
public class AccFeePaymentModeDBO implements Serializable {
	
	private static final long serialVersionUID = -9073612182483969980L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_fee_payment_mode_id")
	public int id;
	
	@Column(name="payment_mode")
	public String paymentMode;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;

}
