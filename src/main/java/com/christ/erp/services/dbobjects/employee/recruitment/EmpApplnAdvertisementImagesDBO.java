package com.christ.erp.services.dbobjects.employee.recruitment;
import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "emp_appln_advertisement_images")
@Setter
@Getter
public class EmpApplnAdvertisementImagesDBO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_appln_advertisement_images_id")
	public Integer id;
	
	@Column(name="upload_adv_url")
	public String uploadAdvertisementUrl;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_advertisement_id")
	public EmpApplnAdvertisementDBO empApplnAdvertisementId;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "upload_adv_url_id")
	public UrlAccessLinkDBO urlAccessLinkDBO;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id",updatable = false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
}