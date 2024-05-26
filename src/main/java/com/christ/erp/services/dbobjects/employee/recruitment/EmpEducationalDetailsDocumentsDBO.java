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

@Entity
@Table(name="emp_educational_details_documents")
@Setter
@Getter
public class EmpEducationalDetailsDocumentsDBO implements Serializable {

    private static final long serialVersionUID = -6880493775294376437L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_educational_details_documents_id")
    public int id;

    @ManyToOne
    @JoinColumn(name="emp_educational_details_id")
    public EmpEducationalDetailsDBO empEducationalDetailsDBO;

    @Column(name = "educational_documents_url")
    public String educationalDocumentsUrl;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
  	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "educational_documents_url_id")
	public UrlAccessLinkDBO educationalDocumentsUrlDBO;
}
