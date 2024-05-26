package com.christ.erp.services.handlers.admission.applicationprocess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailAllotmentDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.admission.applicationprocess.GenerateSelectionProcessGrouprCeateDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.admission.applicationprocess.GenerateSelectionProcessGroupTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@SuppressWarnings("rawtypes")
@Service
public class GenerateSelectionProcessGroupHandler {

	@Autowired
	private GenerateSelectionProcessGroupTransaction generateSelectionProcessGroupTransaction;
	
	public Mono<ApiResult> GroupCreate(Mono<GenerateSelectionProcessGrouprCeateDTO> dto, String userId) {
		Mono<Map<GenerateSelectionProcessGrouprCeateDTO, String>> duplicate = dto.map(data -> convertFuncDuplicateCheck.apply(data, userId)).map(item->item)
	    		  .flatMap(i->i);	 
		
	    return  duplicate.handle((data,sink)->{
	    	 Map.Entry<GenerateSelectionProcessGrouprCeateDTO, String> entry = data.entrySet().iterator().next();
	    	 if(!Utils.isNullOrEmpty(entry.getValue()) && entry.getValue().equals("duplicate")) {
	    		  sink.error(new DuplicateException("Duplicate"));
	    	 }
	    	 else if(!Utils.isNullOrEmpty(entry.getValue()) && entry.getValue().equals("empty")) {
	    		 sink.error(new GeneralException("Empty"));
	    	 }
	    	 else {
	    		  sink.next(entry.getKey());
	    	  }
	      }).cast(GenerateSelectionProcessGrouprCeateDTO.class).map(data -> convertFunc.apply(data, userId))
			 .map(i->i).flatMap(t->{
			 return t.flatMap(item->{					  
				  item.forEach(item2->generateSelectionProcessGroupTransaction.save(item2)); 
				  return Mono.just(Boolean.TRUE); }); 
		   }).map(Utils::responseResult);
	}
	
	public BiFunction<Set<Integer>,String,Mono<List<AdmSelectionProcessGroupDBO>>> convertFuncDuplicate = (set,userId) -> {
		Mono<List<AdmSelectionProcessGroupDBO>> dbo = generateSelectionProcessGroupTransaction.getGroupAndGropDetails(set);
		return dbo;
	};
	
	public BiFunction<GenerateSelectionProcessGrouprCeateDTO,String,Mono<Map<GenerateSelectionProcessGrouprCeateDTO, String>>> convertFuncDuplicateCheck = (dto,userId) -> {
		 Mono<Set<Integer>> result = generateSelectionProcessGroupTransaction.getStudentsForGenerateSPGroup(dto)
				 .flatMapMany
				   (Flux::fromIterable)
				   .map(item->{
					   return Integer.parseInt(item[0].toString());})
			        .collect(Collectors.toSet());
			        
		 Mono<Map<GenerateSelectionProcessGrouprCeateDTO, String>>  result2 = result.zipWhen(item->{
			System.out.println(item);
			if(!Utils.isNullOrEmpty(item)) {
			Mono<List<AdmSelectionProcessGroupDBO>> mono = generateSelectionProcessGroupTransaction.getGroupAndGropDetails(item).map(i->i);
			return mono;
			}else {
				List<AdmSelectionProcessGroupDBO> li =new ArrayList<AdmSelectionProcessGroupDBO>();
				return Mono.just(li);
			}
	       }).map(tuple->{
	    	   Map<GenerateSelectionProcessGrouprCeateDTO, String> map = new ConcurrentHashMap<GenerateSelectionProcessGrouprCeateDTO, String>();
	    	   if(Utils.isNullOrEmpty(tuple.getT1())) {
	    		   map.put(dto, "empty");
	    	   }
	    	   else if(!Utils.isNullOrEmpty(tuple.getT2())){
	    		   map.put(dto, "duplicate");
	    	   }else {
	    		   map.put(dto, "sucess");
	    	   }
	    	   return map;
	       });
		 return result2;
	};
	
	public BiFunction<GenerateSelectionProcessGrouprCeateDTO,String,Mono<Set<AdmSelectionProcessGroupDBO>>> convertFunc = (dto,userId) -> {
		 Mono<Map<Integer, Set<AdmSelectionProcessGroupDetailDBO>>> result = 
					fetchApplicationDataToconvert(dto);
		   Set<AdmSelectionProcessGroupDBO> dboSet = new HashSet<AdmSelectionProcessGroupDBO>();   
		   return combineConvert(result, dboSet, dto);
	};
	
	public BiFunction<GenerateSelectionProcessGrouprCeateDTO,String,Mono<Set<AdmSelectionProcessGroupDBO>>> convertReFunc = (dto,userId) -> {
		 Mono<Map<Integer, Set<AdmSelectionProcessGroupDetailDBO>>> result = 
					fetchApplicationDataToconvert(dto);
		   Set<AdmSelectionProcessGroupDBO> dboSet = new HashSet<AdmSelectionProcessGroupDBO>();   
		   Mono<Set<AdmSelectionProcessGroupDBO>> resultNew =  combineConvert(result, dboSet, dto);
			   Mono<Set<AdmSelectionProcessGroupDBO>> resultOldAndNew = resultNew.zipWhen(item->{
				Set<Integer>  set =  item.stream().map(i->i.getAdmSelectionProcessPlanDetailAllotmentDBO().getId()).collect(Collectors.toSet());
			   Mono<List<AdmSelectionProcessGroupDBO>> resultOld = generateSelectionProcessGroupTransaction.getGroupAndGropDetails(set);
			   return resultOld;
			}).map(tuple->{
            	Set<AdmSelectionProcessGroupDBO> set = new HashSet<AdmSelectionProcessGroupDBO>();
//				 Runnable command = new Runnable() {
//		                @Override
//		                public void run() {
		    				List<AdmSelectionProcessGroupDBO> list = tuple.getT2().stream().map(i->{
		    					i.setRecordStatus('D');
		    					Set<AdmSelectionProcessGroupDetailDBO> detailsDbos = i.getAdmSelectionProcessGroupDetailDBOsSet().stream().map(j->{
		    						j.setRecordStatus('D');
		    						return j;
		    					}).collect(Collectors.toSet());
		    					i.setAdmSelectionProcessGroupDetailDBOsSet(detailsDbos);
		    					return i;
		    				}).collect(Collectors.toList());
		    				set.addAll(list);
		    				set.addAll(tuple.getT1());
//		                }
//		                
//				 };
			
			   return set;
		   });
		  return resultOldAndNew;
	};
	
	

	private Mono<Set<AdmSelectionProcessGroupDBO>> combineConvert(Mono<Map<Integer, Set<AdmSelectionProcessGroupDetailDBO>>> result,
			Set<AdmSelectionProcessGroupDBO> dboSet, GenerateSelectionProcessGrouprCeateDTO dto) {

		Set<AdmSelectionProcessGroupDetailDBO> setGroupDetails = new HashSet<>();
		Set<AdmSelectionProcessGroupDBO> setGroup = new HashSet<>();
	    
		Mono<Set<AdmSelectionProcessGroupDBO>> modifiedSet = result
				.map(i->{
			i.entrySet().forEach(j->{
				setGroupDetails.clear();
				Set<AdmSelectionProcessGroupDetailDBO> droupDetails = j.getValue();
				String str = String.valueOf(Double.valueOf(droupDetails.size())/Double.valueOf(dto.getGroupSize().trim())).replaceAll("^\\d*\\.","");
				int calculateSizeOfGPdetails =Integer.parseInt( str.subSequence(0, 1).toString());
//				 int calculateSizeOfGPdetails = Integer.parseInt(String.valueOf(Double.valueOf(droupDetails.size())/Double.valueOf(dto.getGroupSize().trim())).replaceAll("^\\d*\\.",""));
				 Integer groupDetailSize = 0;
				 if(calculateSizeOfGPdetails==0) {
					 groupDetailSize = (int) (Double.valueOf(droupDetails.size())/Double.valueOf(dto.getGroupSize()));
				 }else {
					groupDetailSize =   (int) (Double.valueOf(droupDetails.size())/Double.valueOf(dto.getGroupSize())+1);
				 }
				 AdmSelectionProcessPlanDetailAllotmentDBO allotmentDBO = new AdmSelectionProcessPlanDetailAllotmentDBO();
				 allotmentDBO.setId(j.getKey());
				 setGroup.clear();
				 IntStream.range(0, groupDetailSize).forEach(itemGP->{
						AdmSelectionProcessGroupDBO dbo = new AdmSelectionProcessGroupDBO();
						setGroup.add(dbo);
						Set<AdmSelectionProcessGroupDetailDBO> temp =  droupDetails.stream().skip(setGroupDetails.size()).limit(Integer.parseInt(dto.getGroupSize())).
								map(itemSet->{itemSet.setAdmSelectionProcessGroupDBO(dbo); return itemSet;}).collect(Collectors.toSet());
						dbo.setTotalParticipantsInGroup(temp.size());
						setGroupDetails.addAll(temp);
						dbo.setAdmSelectionProcessPlanDetailAllotmentDBO(allotmentDBO);
						dbo.setSelectionProcessGroupName("Group "+setGroup.size());
						dbo.setSelectionProcessGroupNo(setGroup.size());
						dbo.setAdmSelectionProcessGroupDetailDBOsSet(temp);
						dbo.setRecordStatus('A');
						dboSet.add(dbo);
				 });			    
			});
			return dboSet;
		});
		return modifiedSet;	
	}

	private Mono<Map<Integer, Set<AdmSelectionProcessGroupDetailDBO>>> fetchApplicationDataToconvert(
			GenerateSelectionProcessGrouprCeateDTO dto) {
		return generateSelectionProcessGroupTransaction.getStudentsForGenerateSPGroup(dto)
				 .flatMapMany
				   (Flux::fromIterable)
				   .map(item->{
					   return item;})
			        .collect(Collectors.groupingBy(
			            p -> Integer.parseInt(p[0].toString()), 
			            Collectors.mapping(
			                p -> convertGroupDetails(p),
			                Collectors.toSet()
			            )
			        ));
	}



	private AdmSelectionProcessGroupDetailDBO convertGroupDetails(Object[] item) {
		AdmSelectionProcessGroupDetailDBO groupDetails = new AdmSelectionProcessGroupDetailDBO();
		StudentApplnEntriesDBO studenEntriesDBO = new StudentApplnEntriesDBO();
        studenEntriesDBO.setId(Integer.parseInt(item[1].toString()));
		groupDetails.setStudentApplnEntriesDBO(studenEntriesDBO);
		groupDetails.setRecordStatus('A');
		return groupDetails;
	}



	public Mono<ApiResult> GroupReCreate(Mono<GenerateSelectionProcessGrouprCeateDTO> dto,
			String userId) {
		return dto.map(data -> convertReFunc.apply(data, userId))
				 .map(i->i).flatMap(t->{
					 return t.flatMap(item->{	
						 //temp working
						Set<Integer> li = item.stream().filter(i-> {
							 if(!Utils.isNullOrEmpty(i.getId()) && i.getId()!=0){
							  return true;
							 }  
							 return false;
						 }).map(i-> i.getId()).collect(Collectors.toSet());
						 if(!Utils.isNullOrEmpty(li)) {
							 generateSelectionProcessGroupTransaction.deleteOld(li);
						 }
						 //end
						  item.forEach(item2->generateSelectionProcessGroupTransaction.merge(item2)); 
						  return Mono.just(Boolean.TRUE); }); 
				   }).map(Utils::responseResult);
	}
	
	



}
