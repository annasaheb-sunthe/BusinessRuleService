package com.scb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.scb.model.BusinessRules;



@RepositoryRestResource
public interface BusinessRulesRepo extends JpaRepository<BusinessRules, Long> {

	@Query(value="SELECT * FROM businessrule sd WHERE sd.transactiontype = ?1 AND sd.transactionsubtype=?2 AND sd.applicability='1'",nativeQuery=true)
	List<BusinessRules> getBussinessRulesByTypeandCountryCode(String transactionType,String transactionSubType);
	
	@Query(value="SELECT * FROM businessrule sd WHERE sd.ruleCode=?1",nativeQuery=true)
	BusinessRules findByRuleId(String RuleCode);
}