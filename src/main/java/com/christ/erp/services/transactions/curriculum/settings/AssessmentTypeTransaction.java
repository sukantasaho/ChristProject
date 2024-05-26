package com.christ.erp.services.transactions.curriculum.settings;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.common.AttTypeDBO;
import com.christ.erp.services.dbobjects.common.ExamAssessmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ExamAssessmentModeDBO;
import com.christ.erp.services.dbobjects.common.ExamAssessmentRatioDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExamAssessmentTemplateDBO;
import reactor.core.publisher.Mono;

@Repository
public class AssessmentTypeTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<ExamAssessmentRatioDBO>> getRatio() {
		String query = " select dbo from ExamAssessmentRatioDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, ExamAssessmentRatioDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ExamAssessmentCategoryDBO>> getAssessmentCategory(String examType) {
		String query = " select dbo from ExamAssessmentCategoryDBO dbo where dbo.recordStatus = 'A' and dbo.examAssessmentType = :examType ";
		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, ExamAssessmentCategoryDBO.class).setParameter("examType", examType).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ExamAssessmentModeDBO>> getModeOfExam() {
		String query = " select dbo from ExamAssessmentModeDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, ExamAssessmentModeDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<AttTypeDBO>> getAttendanceType() {
		String query = " select dbo from AttTypeDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, AttTypeDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getGridData() {
		String query = " select distinct  exam_assessment_template.exam_assessment_template_id as id,"
				+ "	exam_assessment_ratio.exam_assessment_ratio_id as rid, exam_assessment_ratio.assessment_ratio as ratioName,"
				+ "	exam_assessment_template.template_name as tempName,"
				+ " aca_course_type.aca_course_type_id as cTypeId , aca_course_type.course_type as cType,"
				+ " exam_assessment_template.cia_total_marks + exam_assessment_template.ese_total_marks as totalMarks,"
				+ "	exam_assessment_template.total_scale_down_marks as totalSD"
				+ "	from exam_assessment_template"
				+ " inner join exam_assessment_ratio on exam_assessment_template.exam_assessment_ratio_id = exam_assessment_ratio.exam_assessment_ratio_id"
				+ " inner join aca_course_type on aca_course_type.aca_course_type_id = exam_assessment_template.aca_course_type_id"
				+ "	where exam_assessment_template.record_status = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(query,Tuple.class).getResultList()).subscribeAsCompletionStage());
	}
	
    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(ExamAssessmentTemplateDBO.class, id)
        		.chain(dbo -> session.fetch(dbo.getExamAssessmentTemplateAttendanceDBOSet())
        		.invoke(subSet -> {
        			subSet.forEach(subDbo -> {
        				subDbo.setRecordStatus('D');
        				subDbo.setModifiedUsersId(userId);
        			});
        		})
        		.chain(dbo1 -> session.fetch(dbo.getExamAssessmentTemplateDetailsDBOSet()))
        		.invoke(subSet1 -> {
        			subSet1.forEach(subDbo1 -> {
        				subDbo1.setRecordStatus('D');
        				subDbo1.setModifiedUsersId(userId);
        			});
        			dbo.setRecordStatus('D');
        			dbo.setModifiedUsersId(userId);
        		})		
        		)).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }
    
    public ExamAssessmentTemplateDBO edit(int id) {
    	String query = " select dbo from ExamAssessmentTemplateDBO dbo "
    			+ " left join fetch dbo.examAssessmentTemplateAttendanceDBOSet eata"
    			+ " left join fetch dbo.ExamAssessmentTemplateDetailsDBOSet eatd"
    			+ " where dbo.id = :id and dbo.recordStatus = 'A' and eata.recordStatus = 'A' and eatd.recordStatus = 'A'";
        return  sessionFactory.withSession(s->s.createQuery(query,ExamAssessmentTemplateDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
    }
    
	public void update(ExamAssessmentTemplateDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ExamAssessmentTemplateDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
	}

	public void save(ExamAssessmentTemplateDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}
	
	public List<String> duplicateCheck (int id,String rid) {
		String query = " select exam_assessment_template.template_name from exam_assessment_template"
				+ " where exam_assessment_ratio_id = :rid and exam_assessment_template_id != :id and record_status = 'A' ";
		return sessionFactory.withSession(s->s.createNativeQuery(query, String.class).setParameter("rid",Integer.parseInt(rid)).setParameter("id",id).getResultList()).await().indefinitely();        
	}

}