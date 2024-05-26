package com.christ.erp.services.dbobjects.hostel.settings;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
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

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hostel_seat_availability")
@SuppressWarnings("serial")
public class HostelSeatAvailabilityDBO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_seat_availability_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
	public ErpAcademicYearDBO academicYearDBO;
	
	@ManyToOne
	@JoinColumn(name = "hostel_id")
	public HostelDBO hostelDBO;
	
    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
	
    @OneToMany(mappedBy ="hostelSeatAvailabilityDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   	public Set<HostelSeatAvailabilityDetailsDBO> hostelSeatAvailabilityDetailsDBO = new HashSet<>();
}
