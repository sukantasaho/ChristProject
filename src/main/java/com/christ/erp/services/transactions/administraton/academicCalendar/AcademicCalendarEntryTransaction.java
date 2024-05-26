package com.christ.erp.services.transactions.administraton.academicCalendar;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarCategoryDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarUserTypesDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsDBO;

@Repository
public class AcademicCalendarEntryTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<ErpCalendarUserTypesDBO> getUserType(){
		String str = " select dbo from ErpCalendarUserTypesDBO dbo "
				+ " where dbo.recordStatus = 'A'";
		List<ErpCalendarUserTypesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarUserTypesDBO> query = s.createQuery(str,ErpCalendarUserTypesDBO.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<ErpCalendarCategoryDBO> getErpCalendarCategory() {
		String str = " select dbo from ErpCalendarCategoryDBO dbo "
				+ " where dbo.recordStatus = 'A' ";
		List<ErpCalendarCategoryDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarCategoryDBO> query = s.createQuery(str,ErpCalendarCategoryDBO.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<ErpCalendarDBO> edit(String academicYearId, String locId) {
		String str = " select DISTINCT dbo from ErpCalendarDBO dbo  "
				+ " left join fetch dbo.erpCalendarCategoryDBO category"
				+ " left join fetch category.erpCalendarCategoryRecipientsDBOSet as eaccrs "
				+ " left join fetch dbo.erpCalendarCampusDBOSet as eaccs "
				+ " left join fetch dbo.erpCalendarUserTypesDetailsDBOSet as eacuts "
				+ " left join fetch dbo.erpCalendarDatesDBOSet as eacds"
				+ " where dbo.recordStatus = 'A' "
				+ " and dbo.erpAcademicYearDBO.recordStatus = 'A' and dbo.erpLocationDBO.recordStatus = 'A' "
				+ " and category.recordStatus = 'A' "
				+ " and eaccrs.recordStatus = 'A' and eaccs.recordStatus = 'A' and eacuts.recordStatus = 'A' and "
				+ " eacds.recordStatus = 'A' and"
				+ " dbo.erpAcademicYearDBO.id = :academicYearId and dbo.erpLocationDBO.id = :locId ORDER BY dbo.fromDate";
		List<ErpCalendarDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarDBO> query = s.createQuery(str,ErpCalendarDBO.class);
			query.setParameter("academicYearId", Integer.parseInt(academicYearId));
			query.setParameter("locId", Integer.parseInt(locId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<LocalDate> getDatesBetweenStartDateEndDate(LocalDate startDate, LocalDate endDate) {
		return startDate.datesUntil(endDate).collect(Collectors.toList());
	}

	public boolean update(List<ErpCalendarDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		return true;
	}

	public List<ErpCampusDBO> getCampusList() {
		String str = " select dbo from ErpCampusDBO dbo "
				+ " where dbo.recordStatus = 'A' and dbo.erpLocationDBO.recordStatus = 'A'";
		List<ErpCampusDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCampusDBO> query = s.createQuery(str,ErpCampusDBO.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<ErpLocationDBO> getLocationList() {
		String str = " select dbo from ErpLocationDBO dbo "
				+ " where dbo.recordStatus = 'A' ";
		List<ErpLocationDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpLocationDBO> query = s.createQuery(str,ErpLocationDBO.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<ErpCalendarDBO> duplicateCheck(String academicYearId, String locId,List<String> activitynameList) {
		String str = " select DISTINCT dbo from ErpCalendarDBO dbo "
				+ " left join fetch dbo.erpCalendarCategoryDBO category "
				+ " left join fetch category.erpCalendarCategoryRecipientsDBOSet as eaccrs "
				+ "	left join fetch dbo.erpCalendarCampusDBOSet as eaccs "
				+ "	left join fetch dbo.erpCalendarUserTypesDetailsDBOSet as eacuts "
				+ "	left join fetch dbo.erpCalendarDatesDBOSet as eacds "
				+ " where dbo.recordStatus = 'A' and dbo.erpAcademicYearDBO.recordStatus = 'A' and dbo.erpLocationDBO.recordStatus = 'A' and "
				+ " dbo.erpAcademicYearDBO.id  = : academicYearId and dbo.erpLocationDBO.id = : locId and category.recordStatus = 'A' and "
				+ " eaccrs.recordStatus = 'A' and eaccs.recordStatus = 'A' and eacuts.recordStatus = 'A' and eacds.recordStatus = 'A' and "
				+ " dbo.activitiesEvents IN (:activitynameList)";
		String finalStr = str;
		List<ErpCalendarDBO> dbList = sessionFactory.withSession( s -> { Mutiny.Query<ErpCalendarDBO> str1 = s.createQuery(finalStr,ErpCalendarDBO.class);
		str1.setParameter("academicYearId",Integer.parseInt(academicYearId));
		str1.setParameter("locId",Integer.parseInt(locId));
		str1.setParameter("activitynameList",activitynameList);
		return str1.getResultList();
		}).await().indefinitely();
		return dbList;
	}
		
	public List<ErpCalendarDBO> getExistDetails(Integer academicId) {
		String str = " select DISTINCT dbo from ErpCalendarDBO dbo  "
				+ " left join fetch dbo.erpCalendarCategoryDBO category"
				+ " left join fetch category.erpCalendarCategoryRecipientsDBOSet as eaccrs "
				+ " left join fetch dbo.erpCalendarCampusDBOSet as eaccs "
				+ " left join fetch dbo.erpCalendarUserTypesDetailsDBOSet as eacuts "
				+ " left join fetch dbo.erpCalendarDatesDBOSet as eacds"
				+ " where dbo.recordStatus = 'A' "
				+ " and dbo.erpAcademicYearDBO.recordStatus = 'A' and dbo.erpLocationDBO.recordStatus = 'A' "
				+ " and category.recordStatus = 'A' "
				+ " and eaccrs.recordStatus = 'A' and eaccs.recordStatus = 'A' and eacuts.recordStatus = 'A' and "
				+ " eacds.recordStatus = 'A' and"
				+ " dbo.erpAcademicYearDBO.id = :academicId ";
		List<ErpCalendarDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarDBO> query = s.createQuery(str,ErpCalendarDBO.class);
			query.setParameter("academicId", academicId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpHolidayEventsDBO> getHolidays(Integer yearId, String locId) {
		String str = " select dbo from EmpHolidayEventsDBO dbo "
				+ " where dbo.recordStatus = 'A' and dbo.erpAcademicYearId.recordStatus = 'A' and "
				+ " dbo.erpLocationId.recordStatus = 'A' and dbo.erpLocationId.id = :locId and "
				+ " dbo.erpAcademicYearId.id = :yearId ";
		List<EmpHolidayEventsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpHolidayEventsDBO> query = s.createQuery(str,EmpHolidayEventsDBO.class);
			query.setParameter("yearId", yearId);
			query.setParameter("locId", Integer.parseInt(locId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
}