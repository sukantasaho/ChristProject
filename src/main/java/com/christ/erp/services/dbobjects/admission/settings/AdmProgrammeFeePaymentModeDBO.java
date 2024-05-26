package com.christ.erp.services.dbobjects.admission.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.account.fee.AccFeePaymentModeDBO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "adm_programme_fee_payment_mode")
public class AdmProgrammeFeePaymentModeDBO {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_programme_fee_payment_mode_id")
    public int id;
	
	@ManyToOne
    @JoinColumn(name = "adm_programme_settings_id")
    public AdmProgrammeSettingsDBO admProgrammeSettingsDBO;
    
    @ManyToOne
    @JoinColumn(name = "acc_fee_payment_mode_id")
    public AccFeePaymentModeDBO accFeePaymentModeDBO;

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
