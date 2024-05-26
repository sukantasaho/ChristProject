package com.christ.erp.services.dbobjects.hostel.settings;

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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "hostel_block")
public class HostelBlockDBO implements Serializable {

    private static final long serialVersionUID = 6128413708442235672L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hostel_block_id")
    public Integer id;

    @Column(name="hostel_block_name")
    public String blockName;

    @ManyToOne
    @JoinColumn(name="hostel_id")
    public HostelDBO hostelDBO;

    @Column(name="block_location_longitutde")
    public String blockLocationLongitude;

    @Column(name="block_location_lattitude")
    public String blockLocationLattitude;

   @Column(name = "record_status")
    public char recordStatus;

    @Column(name = "created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;
    
    @OneToMany(mappedBy = "hostelBlockDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<HostelBlockUnitDBO> hostelBlockUnitDBOSet = new HashSet<>();
}
