package com.christ.erp.services.dbqueries.common;

public class CommonQueries {
    public static final String SELECT_LAST_IDENTITY = "SELECT LAST_INSERT_ID() AS 'ID'";
    public static final String SELECT_LOCATION = "select e.erp_location_id as 'ID', e.location_name as 'Text' from erp_location e where e.record_status='A' order by e.location_name asc";
    public static final String SELECT_COUNTRY_AND_NATIONALITY = "select e.erp_country_id as 'ID', e.country_name as 'Text', e.nationality_name as Nationality, e.phone_code as phoneCode from erp_country e  where e.record_status='A' order by e.country_name asc";
    public static final String SELECT_RELIGION = "select e.erp_religion_id as 'ID', e.religion_name as 'Text', e.is_minority as IsMinority from erp_religion e where e.record_status='A' order by e.religion_name asc";
    public static final String SELECT_BLOOD_GROUP = "select e.erp_blood_group_id as 'ID', e.blood_group_name as 'Text' from erp_blood_group e where e.record_status='A' order by e.blood_group_name asc";
    public static final String SELECT_RESERVATION_CATEGORY = "select e.erp_reservation_category_id as 'ID', e.reservation_category_name as 'Text' from erp_reservation_category e where e.record_status='A' order by e.reservation_category_name asc";
    public static final String SELECT_QUALIFICATION_LEVEL = "select e.erp_qualification_level_id as 'ID', e.qualification_level_name as 'Text', e.qualification_level_degree_order as qualificationLevelDegreeOrder, e.is_add_more as isAdd_more, e.is_mandatory as isMandatory,e.is_status_display as isStatusDisplay, e.board_type as boardType, e.qualification_level_code as qualificationLevelCode from erp_qualification_level e where e.record_status='A' and is_display_for_employee=1 order by e.qualification_level_degree_order asc";
    public static final String SELECT_APPLN_WORK_EXPERIENCE_TYPE = "select e.emp_appln_work_experience_type_id as 'ID', e.work_experience_type_name as 'Text', e.is_experience_type_academic as isExperienceTypeAcademic from emp_appln_work_experience_type e where e.record_status='A' order by e.work_experience_type_name asc";
    public static final String SELECT_APPLN_VACANCY_INFORMATION = "select e.emp_appln_vacancy_information_id as 'ID', e.vacancy_information_name as 'Text' from emp_appln_vacancy_information e where e.record_status='A' order by e.vacancy_information_name asc";
    public static final String SELECT_GENDER = "select e.erp_gender_id as 'ID', e.gender_name as 'Text' from erp_gender e where e.record_status='A' order by e.gender_name asc";
    public static final String SELECT_MARITAL_STATUS = "select e.erp_marital_status_id as 'ID', e.marital_status_name as 'Text' from erp_marital_status e where e.record_status='A' order by e.marital_status_name asc";
    public static final String SELECT_DIFFERENTLY_ABLED = " select e.erp_differently_abled_id as 'ID', e.differently_abled_name as 'Text' from erp_differently_abled e where e.record_status='A' order by e.differently_abled_name asc";
    public static final String SELECT_DEPARTMENT_LIST="select erp_department_id AS 'ID', department_name AS 'Text' from erp_department where record_status= 'A'";
    public static final String SELECT_EMPLOYEE_CAMPUS = "select erp_campus_id as 'ID',campus_name as 'Text'   from erp_campus where record_status='A' order by campus_name";
	public static final String SELECT_ACADEMIC_YEAR_LIST = "select e.erp_academic_year_id as 'ID',e.academic_year as academicYear,e.is_current_academic_year as isCurrent from erp_academic_year e where e.record_status='A' order by e.academic_year DESC";
	public static final String GET_ACADEMIC_YEAR_DETAILS_BY_YEAR = "from ErpAcademicYearDBO bo where bo.recordStatus='A' and academic_year=:academicYear ";
	public static final String GET_ACADEMIC_YEAR_DETAILS="from ErpAcademicYearDBO bo   where  bo.recordStatus='A' order by bo.academicYear DESC ";
	public static final String GET_CURRENT_ACADEMIC_YEAR = "select bo.academicYear from ErpAcademicYearDBO bo where bo.recordStatus='A' and  bo.isCurrentAcademicYear=1";
	public static final String GET_CAMPUS_DURATION="from ErpAcademicYearDetailsDBO bo   where  bo.recordStatus='A' ";
	public static final String GET_ACADEMIC_YEAR = "select erp_academic_year_id as 'ID',academic_year_name as 'Text' from erp_academic_year where record_status='A' order by academic_year DESC";
	public static final String SELECT_STATE = "select e.erp_state_id as 'ID', e.state_name as 'Text' from erp_state e where e.record_status='A' order by e.state_name asc";
	public static final String SELECT_CITY = "select e.erp_city_id as 'ID', e.city_name as 'Text' from erp_city e where e.record_status='A' and e.erp_state_id=:stateId order by e.city_name asc";
    public static final String GET_SYSTEM_MENUS = "select erp_module.module_display_order as module_display_order,erp_module.erp_module_id as erp_module_id, erp_module.module_name as module_name," +
            " erp_module_sub.sub_module_display_order as sub_module_display_order,erp_module_sub.erp_module_sub_id as erp_module_sub_id,erp_module_sub.sub_module_name as sub_module_name," +
            " erp_menu_screen.menu_screen_display_order as menu_screen_display_order,erp_menu_screen.erp_menu_screen_id as erp_menu_screen_id,erp_menu_screen.menu_screen_name as menu_screen_name,erp_menu_screen.menu_component_path_new as menu_component_path," +
            " erp_screen_config_mast.erp_screen_config_mast_id as erp_screen_config_mast_id,erp_screen_config_mast.mapped_table_name as mapped_table_name,erp_module_sub.icon_class_name as icon_class_name " +
            " from erp_menu_screen" +
            " inner join erp_module_sub ON erp_module_sub.erp_module_sub_id = erp_menu_screen.erp_module_sub_id" +
            " inner join erp_module ON erp_module.erp_module_id = erp_module_sub.erp_module_id" +
            " left join erp_screen_config_mast ON erp_screen_config_mast.erp_screen_config_mast_id = erp_menu_screen.erp_screen_config_mast_id and erp_screen_config_mast.record_status='A'" +
            " where erp_menu_screen.record_status='A' and erp_module_sub.record_status='A' and erp_module.record_status='A' and erp_menu_screen.is_displayed=1";
    public static final String SELECT_DEPARTMENTS_BY_CAMPUS_ID = "SELECT e.erp_department_id as ID, e.department_name as Text FROM erp_department e " + 
    		" INNER JOIN erp_department_category e1 ON " + 
    		" e.erp_department_category_id  = e1.erp_department_category_id " + 
    		" INNER JOIN erp_campus e2 ON " + 
    		" e2.erp_campus_id = e1.erp_campus_id " + 
    		" WHERE e2.erp_campus_id =:campusId AND e2.record_status ='A' AND e1.record_status='A' AND e.record_status='A'";  
    public static final String GET_CAMPUS=" from ErpCampusDBO bo where  bo.recordStatus='A' ";
    public static final String SELECT_ERP_CAMPUS_DEPARTMENT_MAPPING_BY_CAMPUS_ID = " select e.erp_campus_department_mapping_id as 'ID', e2.department_name AS 'Text' from erp_campus_department_mapping e " + 
    		" inner join erp_department e2 ON e2.erp_department_id = e.erp_department_id " + 
    		" where e.erp_campus_id =:campusId and e.record_status='A' and e2.record_status = 'A' order by e2.department_name";
    public static final String SELECT_SUB_MODULE_NAME = "select e.sys_menu_module_sub_id as 'ID',e.sub_module_name as 'Text' from sys_menu_module_sub e where e.record_status='A' order by e.sub_module_name asc";
    public static final String SELECT_FORMATTED_TEMPLATE_LIST = "select e.erp_template_id as 'ID',e.sys_menu_module_sub_id as process,e.template_code as templateCode,e.template_name as templateName from erp_template e where e.record_status='A'";// need to change
    public static final String SELECT_DUPLCATE_CHECK_FORMATTED_TEMPLATE ="from ErpTemplateDBO where  recordStatus='A' and templateCode=:templateCode and id != :id";
    public static final String SELECT_STATES_BY_COUNTRY = "select e.erp_state_id as 'ID', e.state_name as 'Text' from erp_state e where erp_country_id=:CountryId and e.record_status='A' order by e.state_name asc";
    public static final String SELECT_PROGRAMMES = "select e.erp_programme_id as 'ID', e.programme_name as 'Text' from erp_programme e where  e.record_status='A' order by e.programme_name asc";
    public static final String SELECT_CAMPUS_BY_PROGRAMME = "select distinct mapping.erp_campus_id as 'ID', erp_campus.campus_name as 'Text' from erp_campus_programme_mapping mapping " + 
    		" inner join erp_campus ON erp_campus.erp_campus_id = mapping.erp_campus_id " + 
    		" where mapping.record_status='A' and erp_campus.record_status='A' and mapping.erp_programme_id =:ProgrammeId ";
    public static final String SELECT_LOCARIONS_BY_PROGRAMME = "select distinct erp_location.erp_location_id as 'ID', erp_location.location_name as 'Text' from erp_campus_programme_mapping mapping " + 
    		" inner join erp_campus ON erp_campus.erp_campus_id = mapping.erp_campus_id " + 
    		" inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id " + 
    		" where mapping.record_status='A' and erp_campus.record_status='A' and erp_location.record_status='A' and mapping.erp_programme_id =:ProgrammeId ";
    public static final String SELECT_QALIFICATION_LIST = "select a.adm_qualification_list_id as 'ID', a.qualification_name as 'Text' from adm_qualification_list a where a.record_status='A' and a.is_additional_document=0 order by a.qualification_order asc";
    public static final String SELECT_QALIFICATION_LIST_FOR_ADDITIONAL_DOC = "select a.adm_qualification_list_id as 'ID', a.qualification_name as 'Text' from adm_qualification_list a where a.record_status='A' and a.is_additional_document=1 order by a.qualification_name asc";
    public static final String SELECT_ERP_PROGRAMME_DEGREE = "select e.erp_programme_degree_id as 'ID', e.programme_degree as 'Text' from erp_programme_degree e where e.record_status='A' order by e.programme_degree asc";
}