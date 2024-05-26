package com.christ.erp.services.transactions.admission.admissionprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.StudentServicesPath;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleComponentsDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class SelectionProcessRescheduleRequestTransactionWebflux {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    @Autowired
    WebClient studentServices;

    public StudentApplnEntriesDBO getStudentApplnEntriesDBO(String studentApplnEntriesId) {
        return sessionFactory.withSession(session->session.createQuery("select dbo from StudentApplnEntriesDBO dbo " +
            " inner join fetch dbo.admSelectionProcessDBOS "+
            " where dbo.recordStatus='A' " +
            " and dbo.id=:studentApplnEntriesId", StudentApplnEntriesDBO.class)
            .setParameter("studentApplnEntriesId", Integer.parseInt(studentApplnEntriesId)).getSingleResultOrNull()).await().indefinitely();
    }

    public void webClientTestMethod2() {
        Mono<EmpPayScaleComponentsDTO> responseData = studentServices.post()
                .uri(uriBuilder -> uriBuilder.path(StudentServicesPath.STUDENT__COMMON__COMMON_API__GET_SELECTION_PROCESS_PREFFERED_DATES).queryParam("id","110").build())
                .retrieve()
                .bodyToMono(EmpPayScaleComponentsDTO.class);
        responseData.subscribe(salaryComponentDTOWebFlux -> System.out.println(salaryComponentDTOWebFlux.getSalaryComponentName()));
    }

    public Mono<ApiResult> getSelectionProcessPrefferedDates(String erpCampusProgrammeMappingId) {
        Mono<ApiResult> responseData = studentServices.post()
                .uri(uriBuilder -> uriBuilder.path(StudentServicesPath.STUDENT__COMMON__COMMON_API__GET_SELECTION_PROCESS_PREFFERED_DATES).queryParam("erpCampusProgrammeMappingId",erpCampusProgrammeMappingId).build())
                .retrieve()
                .bodyToMono(ApiResult.class);
        //responseData.subscribe(ApiResult -> System.out.println(ApiResult.isSuccess()));
        return responseData;
    }

}
