package com.christ.erp.services.dbobjects.employee.settings;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "acc_slip_book")
public class AccSlipBookDBO implements Serializable {

	private static final long serialVersionUID = 3745612374997924746L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acc_slip_book_id")
	public Integer id;

	@Column(name = "slip_book_type")
	public String slipBookType;

	@Column(name = "book_no_prefix")
	public String bookNoPrefix;

	@Column(name = "slip_book_no")
	public String slipBookNo;

	@Column(name = "no_prefix")
	public String noPrefix;

	@Column(name = "start_slip_no")
	public Integer startSlipNo;

	@Column(name = "end_slip_no")
	public Integer endSlipNo;

	@Column(name = "issued_date")
	public LocalDate issuedDate;

	@Column(name = "issued_to")
	public String issuedTo;

	@Column(name = "issued_by")
	public String issuedBy;

	@Column(name = "created_users_id")
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_Status")
	public char recordStatus;
}
