package com.scb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scb.model.BusinessRules;
import com.scb.model.RequestData;
import com.scb.model.ResponseMessage;

@Service
public interface MainService {
	boolean saveBussinessRule(BusinessRules businessRules);

	List<BusinessRules> getAllBusinessRules();

	BusinessRules getBusinessRule(String ruleCode);

	List <BusinessRules> getBusinessRulesByTypeandCountryCode(String transactionType, String transactionSubType);
	
	void ModifyBusinessRules(BusinessRules businessrules);

	ResponseMessage validateBusinessRules(RequestData requestData);
}
