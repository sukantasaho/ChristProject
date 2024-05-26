package com.christ.erp.services.transactions.common;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.DatabaseConfig;
import com.christ.erp.services.common.StudentServicesPath;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBOWebflux;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDetailsDBOWebflux;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBOWebflux;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpCampusProgrammeMappingDetailsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammeBatchwiseSettingsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleComponentsDTO;
import com.christ.erp.services.exception.NotFoundException;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.impl.pool.ConnectionPool;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.mysqlclient.impl.MySQLConnectionFactory;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.spi.ConnectionFactory;
import org.hibernate.Hibernate;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.session.ReactiveSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Tuple;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class TestTransactionWebflux {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    @Autowired
    WebClient studentServices;




    public Mono<List<ErpCountryDBOWebflux>> getCountryList() {
        String queryString = "select bo from ErpCountryDBOWebflux bo where bo.recordStatus = 'A' order by bo.countryName";
        return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, ErpCountryDBOWebflux.class).getResultList()).subscribeAsCompletionStage());
    }

    public List<ErpCountryDBOWebflux> getCountryList1() {
        String queryString = "select bo from ErpCountryDBOWebflux bo where bo.recordStatus = 'A' order by bo.countryName";
        return sessionFactory.withSession(s->s.createQuery(queryString,ErpCountryDBOWebflux.class).getResultList()).await().indefinitely();
    }

    public void getCountryList2() {
        String queryString = "select c.country_name as country_name, c.phone_code as phone_code from erp_country c";
        Mono<List<Object[]>> list = Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(queryString, Object[].class).getResultList()).subscribeAsCompletionStage());
    }

    public Mono<List<Object[]>> getMenuScreenList() {
        String queryString = "select erp_module.module_display_order as module_display_order,erp_module.erp_module_id as erp_module_id, erp_module.module_name as module_name," +
                "             erp_module_sub.sub_module_display_order as sub_module_display_order,erp_module_sub.erp_module_sub_id as erp_module_sub_id,erp_module_sub.sub_module_name as sub_module_name," +
                "             erp_menu_screen.menu_screen_display_order as menu_screen_display_order,erp_menu_screen.erp_menu_screen_id as erp_menu_screen_id,erp_menu_screen.menu_screen_name as menu_screen_name,erp_menu_screen.menu_component_path as menu_component_path," +
                "             erp_screen_config_mast.erp_screen_config_mast_id as erp_screen_config_mast_id,erp_screen_config_mast.mapped_table_name as mapped_table_name,erp_module_sub.icon_class_name as icon_class_name " +
                "             from erp_menu_screen" +
                "             inner join erp_module_sub ON erp_module_sub.erp_module_sub_id = erp_menu_screen.erp_module_sub_id" +
                "             inner join erp_module ON erp_module.erp_module_id = erp_module_sub.erp_module_id" +
                "             left join erp_screen_config_mast ON erp_screen_config_mast.erp_screen_config_mast_id = erp_menu_screen.erp_screen_config_mast_id and erp_screen_config_mast.record_status='A'" +
                "             where erp_menu_screen.record_status='A' and erp_module_sub.record_status='A' and erp_module.record_status='A' and erp_menu_screen.is_displayed=1";
        return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(queryString, Object[].class).getResultList()).subscribeAsCompletionStage());
    }

    public Mono<Object[]> get(int applicationNumber,String mode) {
        String queryString = "select sae.applicant_name as ApplicantName from adm_selection_process asp"
                + " inner join student_appln_entries sae ON sae.student_appln_entries_id = asp.student_appln_entries_id"
                + " where sae.application_no=:appNo"; // MySql query
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Object[].class).setParameter("appNo", applicationNumber).getSingleResult()).subscribeAsCompletionStage());
    }


    public void testMethod() {
        ErpAcademicYearDBOWebflux dbo = sessionFactory.withSession(s->s.createQuery(" select dbo from ErpAcademicYearDBOWebflux dbo " +
                " left join fetch dbo.academicYearDetails where dbo.id=96", ErpAcademicYearDBOWebflux.class).getSingleResultOrNull()).await().indefinitely();

        Set<ErpAcademicYearDetailsDBOWebflux> subBoset = dbo.getAcademicYearDetails();
        ErpAcademicYearDetailsDBOWebflux subBo = new ErpAcademicYearDetailsDBOWebflux();
        subBo.setAcademicYear(dbo);
        ErpCampusDBO campusDBO = new ErpCampusDBO();
        campusDBO.setId(1);
        subBo.setCampus(campusDBO);
        subBo.setRecordStatus('A');
        subBoset.add(subBo);
        dbo.setRecordStatus('A');
        dbo.setAcademicYearDetails(subBoset);
        sessionFactory.withTransaction((session, transaction) -> session.merge(dbo)).await().indefinitely();
    }

    public void testMethod1(){
        ErpAcademicYearDBOWebflux dbo = sessionFactory.withSession(s->s.createQuery(" select dbo from ErpAcademicYearDBOWebflux dbo " +
                " left join fetch dbo.academicYearDetails where dbo.id=96", ErpAcademicYearDBOWebflux.class).getSingleResultOrNull()).await().indefinitely();
        Set<ErpAcademicYearDetailsDBOWebflux> subBoset = dbo.getAcademicYearDetails();
        subBoset.forEach(item->{
            item.setAcademicYear(dbo);
            item.getCampus().setId(2);
            item.setRecordStatus('A');
        });
        dbo.setRecordStatus('A');
        sessionFactory.withTransaction((session, transaction) -> session.merge(dbo)).await().indefinitely();
    }

    public void webClientTestMethod1() {
        //Get request and returns flux
        Flux<EmpPayScaleComponentsDTO> list = studentServices.get()
                .uri(StudentServicesPath.EMPLOYEE__SALARY__SALARY_COMPONENT__GET_GRID_DATA)
                .retrieve()
                .bodyToFlux(EmpPayScaleComponentsDTO.class);
        list.subscribe(salaryComponentDTOWebFlux -> System.out.println(salaryComponentDTOWebFlux.getSalaryComponentName()));
    }


    public void webClientTestMethod2() {
        //post request with request parameter and returns Mono
        Mono<EmpPayScaleComponentsDTO> responseData = studentServices.post()
                .uri(uriBuilder -> uriBuilder.path(StudentServicesPath.EMPLOYEE__SALARY__SALARY_COMPONENT__EDIT).queryParam("id","110").build())
                .retrieve()
                .bodyToMono(EmpPayScaleComponentsDTO.class);
        responseData.subscribe(salaryComponentDTOWebFlux -> System.out.println(salaryComponentDTOWebFlux.getSalaryComponentName()));
    }

    public void webClientTestMethod3() {
        //Passing request parameter and Mono request body
        EmpPayScaleComponentsDTO requestDto = new EmpPayScaleComponentsDTO();
        requestDto.setSalaryComponentName("request data");
        Mono<EmpPayScaleComponentsDTO> responseData = studentServices.post()
                .uri(uriBuilder -> uriBuilder.path(StudentServicesPath.EMPLOYEE__SALARY__SALARY_COMPONENT__EDIT).queryParam("id","110").build())
                .body(BodyInserters.fromPublisher(Mono.just(requestDto),EmpPayScaleComponentsDTO.class))
                .retrieve()
                .bodyToMono(EmpPayScaleComponentsDTO.class);
        responseData.subscribe(salaryComponentDTOWebFlux -> System.out.println(salaryComponentDTOWebFlux.getSalaryComponentName()));
    }

    public void webClientTestMethod4() {
        Mono<Map<String, EmpPayScaleComponentsDTO>>  responseData = studentServices.post()
                .uri(uriBuilder -> uriBuilder.path(StudentServicesPath.EMPLOYEE__SALARY__SALARY_COMPONENT__EDIT).queryParam("id","110").build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>(){});
    }

   /* public void webClientTestMethod5() {
        //Passing request parameter and object request body
        SalaryComponentDTOWebFlux requestDto = new SalaryComponentDTOWebFlux();
        requestDto.setSalaryComponentName("request data");
        Mono<SalaryComponentDTOWebFlux> responseData = studentServices.post()
                .uri(uriBuilder -> uriBuilder.path(StudentServicesPath.EMPLOYEE__SALARY__SALARY_COMPONENT__EDIT).queryParam("id","110").build())
                .body(BodyInserters.fromValue(requestDto))
                .retrieve()
                .bodyToMono(SalaryComponentDTOWebFlux.class);
        responseData.subscribe(salaryComponentDTOWebFlux -> System.out.println(salaryComponentDTOWebFlux.getSalaryComponentName()));
    }*/
    
//  @EventListener(ApplicationReadyEvent.class)
//    public Mono<List<ErpCommitteeDBO>> getEmpEntries() {
//        String queryString = "select dbo from ErpCommitteeDBO dbo left join fetch dbo.";
//        Mono<List<ErpCommitteeDBO>>   getEmpEntriesList =(Mono<List<ErpCommitteeDBO>>) Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, ErpCommitteeDBO.class).getResultList()).subscribeAsCompletionStage());
//        getEmpEntriesList.log().subscribe();
//        return  getEmpEntriesList;
//    }

//@Transactional
//@ReadOnlyProperty
//@EventListener(ApplicationReadyEvent.class)
public void testMethod111() {
    long startTime = System.nanoTime();
    System.out.println("start time"+startTime);
    String str2 = "select epb from ErpProgrammeBatchwiseSettingsDBO as epb"
            + " left join fetch epb.erpProgrammeSecondLanguageSessionDBOSet as sl"
             + " left join fetch epb.erpProgrammeAccreditationMappingDBOSet"
            + " left join fetch epb.erpProgrammeSpecializationMappingDBOSet"
            + " left join fetch epb.erpCampusProgrammeMappingDetailsDBOSet"
            + " left join fetch epb.erpProgrammeAddOnCoursesDBOSet"
            + " left join fetch epb.erpProgrammeSpecializationSessionMappingDBOSet"
            + " left join fetch epb.acaBatchDBOSet as ab"
            + " left join fetch ab.acaDurationDetailDBO as acad"
            + " left join fetch epb.erpProgrammeOutcomeDBOSet as eo"
            + " left join fetch eo.erpProgrammeOutcomeUploadDetailsDBOSet"
            + " left join fetch eo.erpProgrammeOutcomeDetailsDBOSet as eod"
            + " left join fetch eod.erpProgrammePeoMissionMatrixDBOSet"
            + " where epb.recordStatus = 'A'"
// + " and (ab is null or ab.recordStatus = 'A')"
// + " and (sl is null or sl.recordStatus = 'A')"
            + " and epb.erpProgrammeDBO.id= :id  and epb.erpAcademicYearDBO.id = :batchYearId";

    //System.out.println("begin");
    //ErpProgrammeBatchwiseSettingsDBO bo = sessionFactory.withSession(s -> s.createQuery(str2, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("id", 574)
      //      .setParameter("batchYearId", 101).getSingleResultOrNull()).await().indefinitely();
    //System.out.println("size is------" + list.size());

/*    ErpProgrammeBatchwiseSettingsDBO bo = sessionFactory.withStatelessSession(session -> session.createQuery("select epb from ErpProgrammeBatchwiseSettingsDBO as epb join epb.erpProgrammeAddOnCoursesDBOSet where epb.erpProgrammeDBO.id= :id " +
                                    "  and epb.erpAcademicYearDBO.id = :batchYearId "
                    ,ErpProgrammeBatchwiseSettingsDBO.class).setParameter("id", 574)
                  .setParameter("batchYearId", 101).getSingleResult()
                    .chain(o->session.fetch(o.getErpCampusProgrammeMappingDetailsDBOSet())).invoke(i-> System.out.println(i.size()))
            *//*.chain(o -> session.fetch(o.getErpCampusProgrammeMappingDetailsDBOSet()).invoke(t-> System.out.println("size is"+t.size())))*//*).await().indefinitely();*/
    System.out.println("initialize");

    //Uni<Set<ErpCampusProgrammeMappingDetailsDBO>> set = Mutiny.fetch(bo.getErpCampusProgrammeMappingDetailsDBOSet());
    System.out.println("after");
    long endTime = System.nanoTime();
    System.out.println("endTime time"+endTime);
    long duration = (endTime - startTime);
    //sessionFactory.withStatelessSession()

    System.out.println("time "+ TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS));

        //for(ErpProgrammeBatchwiseSettingsDBO bo : list) {
          /*  System.out.println("second language------" + bo.getErpProgrammeSecondLanguageSessionDBOSet().size());
            System.out.println("specialization mappin------" + bo.getErpProgrammeSpecializationMappingDBOSet().size());
            System.out.println("program mapping details------" + bo.getErpCampusProgrammeMappingDetailsDBOSet().size());
            System.out.println("program addon course------" + bo.getErpProgrammeAddOnCoursesDBOSet().size());
            System.out.println("program specialization course------" + bo.getErpProgrammeSpecializationSessionMappingDBOSet().size());
            System.out.println("aca batch------" + bo.getAcaBatchDBOSet().size());
            bo.getAcaBatchDBOSet().forEach(acaBatchDBO ->  System.out.println("aca batch duration------" + acaBatchDBO.getAcaDurationDetailDBO().size()));*/
       // }



    //bo.forEach(erpProgrammeBatchwiseSettingsDBO -> System.out.println(erpProgrammeBatchwiseSettingsDBO.getId()));
    //list.getErpProgrammeSecondLanguageSessionDBOSet().forEach(subDBo -> System.out.println("id----"+subDBo.getId()));

// var lis= sessionFactory.withSession(s -> s.createQuery(str2, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("id", id)
// .setParameter("batchYearId", batchYearId).getResultList()).await().indefinitely();
// return null;


}


 /*   public void testmethod11() {
        String queryString = "select * from erp_template where erp_tem  plate_id=30";
        System.out.println(sessionFactory.withSession(s->s.createNativeQuery(queryString, Tuple.class).getSingleResultOrNull()).await().indefinitely().get("template_content").toString());
        sessionFactory.withSession(
                session -> session.find(ErpProgrammeBatchwiseSettingsDBO.class, id).invokeUni(
                        u -> fetch(u.userToken).chain(ut -> fetch(ut.sampleManyToOne))
                )
        ).await().indefinitely();
    }*/

    public void testMethod2() {
    /*    MySQLConnectOptions connectOptions = new MySQLConnectOptions();
        connectOptions.set
        public ConnectionFactory connectionFactory() {
            return new MySQLConnectionFactory(
                    PostgresqlConnectionConfiguration.builder()
                            .host("localhost")
                            .port(5432)
                            .database("postgres-database")
                            .username("postgres-username")
                            .password("postgres-password")
                            .build()
            );*/
        String jdbcUrl = "jdbc:mysql://localhost:3306/mydatabase";
        String username = "root";
        String password = "mypassword";

        sessionFactory.withTransaction(s->s.createQuery("").executeUpdate()).await().indefinitely();



    }

    //@Bean
/*    public MySQLPool mysqlPool() throws Exception {
        // Define MySQL connection properties
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setHost("localhost")
                .setPort(3306)
                .setDatabase("mydatabase")
                .setUser("root")
                .setPassword("mypassword");
  *//*
         .setHost(hostname)
                .setPort(port)
                .setDatabase(dbname)
                .setUser(user)
                .setPassword(password)
                .setCachePreparedStatements(true)
                .setPreparedStatementCacheMaxSize(250)
                .setPreparedStatementCacheSqlLimit(2048)
                .setReconnectAttempts(1)
                .setReconnectInterval(10);
*//*
        // Create and return the Vert.x MySQL pool
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        return MySQLPool.pool(getVertx(), connectOptions, poolOptions);
  //  */}

   /* @Bean
    public Mutiny.SessionFactory sessionFactory(MySQLPool mysqlPool) {
        // Create a Mutiny SessionFactory using the connection pool
        Persistence.
    }*/

   // private static Future<Vertx> vertxFuture = (Future<Vertx>) Vertx.vertx(); // Start asynchronous Vertx initialization

    // ... Your database configuration code here ...

    // Wait for Vertx to be fully initialized before using it
 /*   public static Vertx getVertx() throws Exception {
        return vertxFuture.result();
    }*/


   /* public void testMethod3() {
        sessionFactory.withSession(s->s.createQuery("", EmpPayScaleComponentsDBO.class).getSingleResultOrNull().onItem().ifNotNull().call(()-> System.out.println("")))
    }*/
  /* @Bean
   public DataSource dataSource() {
       MySQLConnectOptions options = MySQLConnectOptions.builder()
               .setJdbcUrl("jdbc:mysql://localhost:3306/mydatabase")
               .setUsername("username")
               .setPassword("password")
               .build();
       return new MySQLDataSource(options);
   }

    @Bean
    public Mutiny.SessionFactory reactiveSessionFactory() {
        return new ReactiveSessionConfigImpl()
                .buildSessionFactory(dataSource);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(Mutiny.SessionFactory reactiveSessionFactory) {
        return new LocalContainerEntityManagerFactoryBean()
                .setPersistenceUnitName("myPersistenceUnit")
                .setSessionFactory(reactiveSessionFactory)
                .build();
    }
*/

