package com.christ.erp.services.transactions.administraton.academicCalendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarDatesDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarPersonalDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarToDoListDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpReminderNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

import reactor.core.publisher.Mono;

@Repository
public class MonthViewTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<ErpCalendarDBO> getEventView(String year) {
		String str = " select DISTINCT dbo from ErpCalendarDBO dbo "
				+ " left join fetch dbo.erpCalendarDatesDBOSet as eacds "
				+ " left join fetch dbo.erpCalendarUserTypesDetailsDBOSet as ecuds"
				+ " left join fetch dbo.erpCalendarCampusDBOSet as eccds "
				+ " left join fetch dbo.erpCalendarPersonalDBOSet as ecpds "
				+ " where dbo.recordStatus = 'A' and eacds.recordStatus = 'A' and "
				+ " dbo.erpAcademicYearDBO.recordStatus = 'A' and "
				+ " YEAR(dbo.fromDate) = :year and dbo.published = 1 ";
		List<ErpCalendarDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarDBO> query = s.createQuery(str,ErpCalendarDBO.class);
			query.setParameter("year", Utils.convertStringDateToLocalDate(year).getYear());
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Integer getEmpCampus(String employeeId) {
		String str = " select erp_campus.erp_campus_id from erp_users   "
				+ " left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A'  "
				+ " left join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status ='A'  "
				+ " left join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status='A'  "
				+ " where erp_users.record_status='A' and emp.emp_id =:employeeId";
		return  sessionFactory.withSession(s->s.createNativeQuery(str,Integer.class).setParameter("employeeId", Integer.parseInt(employeeId)).getSingleResultOrNull()).await().indefinitely();
	}

	public Integer getApplicableFor(String employeeId) {
		String str = " select emp_employee_category.emp_employee_category_id from erp_users   "
				+ " left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A'  "
				+ " left join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id and emp_employee_category.record_status = 'A'  "
				+ " left join erp_calendar_user_types on erp_calendar_user_types.emp_employee_category_id = emp_employee_category.emp_employee_category_id and erp_calendar_user_types.record_status ='A'  "
				+ " where erp_users.record_status='A' and emp.emp_id =:employeeId";
		return  sessionFactory.withSession(s->s.createNativeQuery(str,Integer.class).setParameter("employeeId", Integer.parseInt(employeeId)).getSingleResultOrNull()).await().indefinitely();
	}

	public List<ErpCalendarDatesDBO> getDay(String date) {
		String str = " select DISTINCT dbo from ErpCalendarDatesDBO dbo "
				+ " left join fetch dbo.erpCalendarDBO as ecd "
				+ " left join fetch ecd.erpCalendarUserTypesDetailsDBOSet as ecutds "
				+ " left join fetch ecd.erpCalendarCampusDBOSet as eccds "
				+ " left join fetch ecd.erpCalendarPersonalDBOSet as ecps "
				+ " left join fetch ecps.erpReminderNotificationsDBOSet as ernds "
				+ " left join fetch ernds.erpNotificationsDBOSet as ends "
				+ " left join fetch ernds.erpSmsDBOSet as esds "
				+ " left join fetch ernds.erpEmailsDBOSet as emds "
				+ " where dbo.recordStatus='A' and dbo.date =: date and ecd.published = 1";
		List<ErpCalendarDatesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarDatesDBO> query = s.createQuery(str,ErpCalendarDatesDBO.class);
			query.setParameter("date",Utils.convertStringDateToLocalDate(date));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<ErpCalendarPersonalDBO> getToDoDay(String date,String userId) {
		String str = " select DISTINCT dbo from ErpCalendarPersonalDBO dbo "
				+ " left join fetch dbo.erpReminderNotificationsDBOSet as toDoReminder "
				+ " left join fetch toDoReminder.erpNotificationsDBOSet as ends "
				+ " left join fetch toDoReminder.erpSmsDBOSet as esds "
				+ " left join fetch toDoReminder.erpEmailsDBOSet as emds "
				+ " where dbo.recordStatus='A' and dbo.erpCalendarToDoListDBO.recordStatus='A' and"
				+ " dbo.empDBO.recordStatus='A' and "
				+ " dbo.erpCalendarToDoListDBO.toDoDate =: date and dbo.empDBO.id =: userId ";
		List<ErpCalendarPersonalDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarPersonalDBO> query = s.createQuery(str,ErpCalendarPersonalDBO.class);
			query.setParameter("date",Utils.convertStringDateToLocalDate(date));
			query.setParameter("userId", Integer.parseInt(userId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public boolean update1(ErpCalendarDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpCalendarDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
		return true;
	}

	public ErpCalendarPersonalDBO getCalendarData(int id) {
		String str = " select dbo from ErpCalendarPersonalDBO dbo "
				+ " left join fetch dbo.erpCalendarDBO as dbo1 "
				+ " left join fetch dbo1.erpCalendarCampusDBOSet as eccds "
				+ " left join fetch dbo1.erpCalendarUserTypesDetailsDBOSet as ecutds "
				+ " left join fetch dbo1.erpCalendarDatesDBOSet as ecdds "
				+ " left join fetch dbo.erpReminderNotificationsDBOSet as personalReminder "
				+ " left join fetch personalReminder.erpNotificationsDBOSet as ends "
				+ " left join fetch personalReminder.erpSmsDBOSet as esds "
				+ " left join fetch personalReminder.erpEmailsDBOSet as emds "
				+ " where dbo.recordStatus = 'A' and dbo.id =:id ";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpCalendarPersonalDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}
	
	public void update(Object dbo, int id) {
		if(!Utils.isNullOrEmpty(dbo.getClass())) {
			sessionFactory.withTransaction((session, tx) -> session.find(dbo.getClass(), id).call(() -> session.mergeAll(dbo))).await().indefinitely();
		}
	}

	public void save(Object dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo.getClass())).subscribeAsCompletionStage();
	}

	public void save1(ErpCalendarToDoListDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}

	public ErpCalendarToDoListDBO getTodoDetails(int id) {
		String str = " select dbo from ErpCalendarToDoListDBO dbo "
				+ " left join fetch dbo.erpCalendarPersonalDBOSet as toDoPersonal "
				+ " left join fetch toDoPersonal.erpReminderNotificationsDBOSet as toDoReminder "
				+ " left join fetch toDoReminder.erpNotificationsDBOSet as ends "
				+ " left join fetch toDoReminder.erpSmsDBOSet as esds "
				+ " left join fetch toDoReminder.erpEmailsDBOSet as emds "
				+ " where dbo.recordStatus ='A' and dbo.id =: id";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpCalendarToDoListDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public List<ErpCalendarToDoListDBO> getToDoList(String userId,String date) {
		LocalDate currentDate = LocalDate.now();
		String dateString = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String str = " select dbo from ErpCalendarToDoListDBO dbo "
				+ " left join fetch dbo.erpCalendarPersonalDBOSet as todoPersonal "
				+ " left join fetch todoPersonal.erpReminderNotificationsDBOSet as reminderTodo"
				+ " left join fetch reminderTodo.erpNotificationsDBOSet as ends "
				+ " left join fetch reminderTodo.erpSmsDBOSet as esds "
				+ " left join fetch reminderTodo.erpEmailsDBOSet as emds "
				+ " where dbo.recordStatus ='A' "
				+ " and dbo.empDBO.recordStatus ='A' "
				+ " and dbo.empDBO.id = :userId "
				+ " and (dbo.toDoDate is not null and YEAR(dbo.toDoDate) = :date and dbo.recordStatus ='A') "
				+ "   or (dbo.toDoDate is null and YEAR(dbo.createdTime) = :dateString and dbo.recordStatus ='A')";
		var list = sessionFactory.withSession(s -> s.createQuery(str,ErpCalendarToDoListDBO.class).setParameter("userId", Integer.parseInt(userId)).setParameter("date", Utils.convertStringDateToLocalDate(date).getYear()).setParameter("dateString", Utils.convertStringDateToLocalDate(dateString).getYear()).getResultList()).await().indefinitely();
	return list;
	}

	public Mono<Boolean> deleteToDo(int id, String userId) {
		sessionFactory.withTransaction((session, tx) -> session.createQuery( " select dbo from ErpCalendarToDoListDBO dbo "
				+ " left join fetch dbo.erpCalendarPersonalDBOSet as toDoPersonal "
				+ "	left join fetch toDoPersonal.erpReminderNotificationsDBOSet as toDoReminder "
				+ "	left join fetch toDoReminder.erpNotificationsDBOSet as ends "
				+ "	left join fetch toDoReminder.erpSmsDBOSet as esds "
				+ "	left join fetch toDoReminder.erpEmailsDBOSet as emds "
				+ "	where dbo.recordStatus ='A' and dbo.id =: id", ErpCalendarToDoListDBO.class).setParameter("id", id).getSingleResultOrNull()
				.chain(dbo1 -> session.fetch(dbo1.getErpCalendarPersonalDBOSet()) 
						.invoke(calendarPersonalDBOSet -> {
							calendarPersonalDBOSet.forEach(calenderPersonalDbo -> {
								calenderPersonalDbo.setRecordStatus('D');
								calenderPersonalDbo.setModifiedUsersId(Integer.parseInt(userId));
								Set<ErpReminderNotificationsDBO> erpReminderNotificationsDBOSet = calenderPersonalDbo.getErpReminderNotificationsDBOSet().stream().peek(reminderDbo -> {
									reminderDbo.setRecordStatus('D');
									reminderDbo.setModifiedUsersId(Integer.parseInt(userId));
									Set<ErpNotificationsDBO> erpNotificationsDBOSet = reminderDbo.getErpNotificationsDBOSet().stream().peek(notificationDbo -> {
										notificationDbo.setRecordStatus('D');
										notificationDbo.setModifiedUsersId(Integer.parseInt(userId));
									}).collect(Collectors.toSet());
									reminderDbo.setErpNotificationsDBOSet(erpNotificationsDBOSet);
									Set<ErpSmsDBO> erpSmsDBOSet = reminderDbo.getErpSmsDBOSet().stream().peek(smsDbo -> {
										smsDbo.setRecordStatus('D');
										smsDbo.setModifiedUsersId(Integer.parseInt(userId));
									}).collect(Collectors.toSet());
									reminderDbo.setErpSmsDBOSet(erpSmsDBOSet);
									Set<ErpEmailsDBO> erpEmailsDBOSet = reminderDbo.getErpEmailsDBOSet().stream().peek(emailDbo -> {
										emailDbo.setRecordStatus('D');
										emailDbo.setModifiedUsersId(Integer.parseInt(userId));
									}).collect(Collectors.toSet());
									reminderDbo.setErpEmailsDBOSet(erpEmailsDBOSet);
								}).collect(Collectors.toSet());		
								calenderPersonalDbo.setErpReminderNotificationsDBOSet(erpReminderNotificationsDBOSet);
								calenderPersonalDbo.setRecordStatus('D');
								calenderPersonalDbo.setModifiedUsersId(Integer.parseInt(userId));
							});
							dbo1.setRecordStatus('D');
							dbo1.setModifiedUsersId(Integer.parseInt(userId));					
						}))).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public ErpCalendarDBO getCalendarDetails(int id) {
		String str = " select dbo from ErpCalendarDBO dbo "
				+ " left join fetch dbo.erpCalendarPersonalDBOSet as toDoPersonal "
				+ " left join fetch toDoPersonal.erpReminderNotificationsDBOSet as toDoReminder "
				+ "	left join fetch toDoReminder.erpNotificationsDBOSet as ends "
				+ "	left join fetch toDoReminder.erpSmsDBOSet as esds "
				+ "	left join fetch toDoReminder.erpEmailsDBOSet as emds "
				+ " where dbo.recordStatus ='A' and dbo.id =: id";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpCalendarDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public boolean update2(ErpCalendarToDoListDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpCalendarDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
		return true;
	}

	public Integer getDepartment(String employeeId) {
		String str = "select erp_department.erp_department_id from erp_users "
				+ "	left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ "	left join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status ='A' "
				+ "	left join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id and erp_department.record_status='A' "
				+ " where erp_users.record_status='A' and emp.emp_id =:employeeId";
		return  sessionFactory.withSession(s->s.createNativeQuery(str,Integer.class).setParameter("employeeId", Integer.parseInt(employeeId)).getSingleResultOrNull()).await().indefinitely();
	}

	public ErpTemplateDBO getErpTemplateByTemplateCodeAndTemplateType(String templateType, String templateCode) {
		String str = " select bo from ErpTemplateDBO bo where bo.recordStatus = 'A' and bo.templateType=:templateType and bo.templateCode=:templateCode ";
		return  sessionFactory.withSession(s->s.createQuery(str,ErpTemplateDBO.class).setParameter("templateType", templateType).setParameter("templateCode", templateCode).getSingleResultOrNull()).await().indefinitely();
	}

	public Integer getEmpId(String employeeId) {
		String str = "select erp_users_id from erp_users "
				+ " left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status = 'A' " 
				+ " where erp_users.record_status = 'A' and emp.emp_id =:employeeId ";
		return  sessionFactory.withSession(s->s.createNativeQuery(str,Integer.class).setParameter("employeeId", Integer.parseInt(employeeId)).getSingleResultOrNull()).await().indefinitely();
	}

	public String getEmpName(String userId) {
		String str = " select emp.emp_name from erp_users "
				+ " left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' " 
				+ " where erp_users.record_status='A' and emp.emp_id =:userId ";
		return  sessionFactory.withSession(s->s.createNativeQuery(str,String.class).setParameter("userId", Integer.parseInt(userId)).getSingleResultOrNull()).await().indefinitely();
	}

	public String getEmpMobile(String userId) {
		String str = "  select emp.mobile_no from erp_users "
				+ " left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ " where erp_users.record_status='A' and emp.emp_id =:userId ";
		return  sessionFactory.withSession(s->s.createNativeQuery(str,String.class).setParameter("userId", Integer.parseInt(userId)).getSingleResultOrNull()).await().indefinitely();
	}

	public String getEmpPersonalEmail(String userId) {
		String str = "  select emp.emp_personal_email from erp_users "
				+ " left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ " where erp_users.record_status='A' and emp.emp_id =:userId ";
		return  sessionFactory.withSession(s->s.createNativeQuery(str,String.class).setParameter("userId", Integer.parseInt(userId)).getSingleResultOrNull()).await().indefinitely();
	}

	public List<ErpCalendarDBO> printDay(String fDate, String tDate) {
		String str = " select DISTINCT dbo from ErpCalendarDBO dbo "
				+ " where dbo.recordStatus='A'  and dbo.published = 1 and dbo.fromDate >= :fDate and dbo.toDate <=:tDate ";
		List<ErpCalendarDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarDBO> query = s.createQuery(str,ErpCalendarDBO.class);
			query.setParameter("fDate",Utils.convertStringDateToLocalDate(fDate));
			query.setParameter("tDate",Utils.convertStringDateToLocalDate(tDate));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Mono<Boolean> deleteReminder(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpReminderNotificationsDBO.class, id).invoke(dbo -> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
}