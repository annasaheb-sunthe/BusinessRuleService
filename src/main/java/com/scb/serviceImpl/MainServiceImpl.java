package com.scb.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scb.model.BusinessRules;
import com.scb.model.RequestData;
import com.scb.model.ResponseMessage;
import com.scb.repository.BusinessRulesRepo;
import com.scb.service.MainService;
import com.scb.util.XPathConverstion;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MainServiceImpl implements MainService {
	@Autowired
	private BusinessRulesRepo businessrulesrepo;
	@Autowired
	private MainService mainservice;
	@Autowired
	private XPathConverstion xpathconversion;

	@Override
	public boolean saveBussinessRule(BusinessRules businessRules) {
		log.info("Business Rule received: " + businessRules.getRuleCode());
		businessRules.setCreatedOn(getCurrentDateTime());
		businessRules.setUpdatedOn(getCurrentDateTime());
		BusinessRules persistDataVar = null;
		try {
			persistDataVar = (BusinessRules) businessrulesrepo.findByRuleId(businessRules.getRuleCode());
		} catch (NoSuchElementException ex) {
			log.info("Error in finding rule" + ex.getMessage());
		}
		if (persistDataVar != null) {
			return false;
		} else {
			log.info("Rule details being saved in db");
			businessrulesrepo.save(businessRules);
			log.info("Rule details saved in db");
			return true;
		}
	}

	@Override
	public List<BusinessRules> getAllBusinessRules() {
		List<BusinessRules> list = new ArrayList<>();
		businessrulesrepo.findAll().forEach(e -> list.add(e));
		return list;
	}

	@Override
	public BusinessRules getBusinessRule(String ruleCode) {
		BusinessRules obj = businessrulesrepo.findByRuleId(ruleCode);
		return obj;
	}

	@Override
	public List<BusinessRules> getBusinessRulesByTypeandCountryCode(String transactionType, String transactionSubType) {
		List<BusinessRules> list = businessrulesrepo.getBussinessRulesByTypeandCountryCode(transactionType,
				transactionSubType);
		return list;
	}

	@Override
	public void ModifyBusinessRules(BusinessRules businessrules) {
		businessrulesrepo.save(businessrules);

	}

	@Override
	public ResponseMessage validateBusinessRules(RequestData requestData) {
		log.info("Request data - TransactionType :" + requestData.getTransactionType() + ", TransactionSubType :" 
				+ requestData.getTransactionSubType() + ", payloadFormat :" + requestData.getPayloadFormat());

		// Retrieve business rules
		List<BusinessRules> list = mainservice.getBusinessRulesByTypeandCountryCode(requestData.getTransactionType(),
				requestData.getTransactionSubType());

		log.info("List of Business Rules :" + list);
		Iterator<BusinessRules> idr = list.iterator();
		BusinessRules bussinessRuleVar = null;
		String errorMessage = "";
		// Map<Long, String> nameMap = new HashMap<>();

		while (idr.hasNext()) {
			bussinessRuleVar = (BusinessRules) idr.next();

			String data = xpathconversion.getXPathExpressionValue(bussinessRuleVar.getOperandOne(), requestData.getPayload());
			log.info("Operand 1 :" + bussinessRuleVar.getOperandOne());
			log.info("Value of XPath expression : " + data);

			String operator = bussinessRuleVar.getOperartor();
			log.info("Operator :" + operator);
			String operandTwo = bussinessRuleVar.getOperandTwo();
			log.info("Operand 2 :" + operandTwo);
			String[] values = null;
			List<String> listOfOperandTwoValues = null;
			if(operandTwo != null) {
				values = operandTwo.split(",");
				if(values != null && values.length > 0) {
					listOfOperandTwoValues = new ArrayList<String>();
					for (String op2 : values) {
						listOfOperandTwoValues.add(op2);
					}
				}
			}

			try {
				//Equal biz rule fails, add error message
				if (operator.equals("EQUAL") && !listOfOperandTwoValues.contains(data)) {
					errorMessage += bussinessRuleVar.getErrorMessage() + "\n";
				} else if (operator.equals("NOTEQUAL") && listOfOperandTwoValues.contains(data)) {
					errorMessage += bussinessRuleVar.getErrorMessage() + "\n";
				} else if (operator.equals("NULL") && (data != null && !data.isEmpty())) {
					errorMessage += bussinessRuleVar.getErrorMessage() + "\n";
				} else if (operator.equals("NOTNULL") && (data == null || data.isEmpty())) {
					errorMessage += bussinessRuleVar.getErrorMessage() + "\n";
				} else if(operator.equals("GREATER")) {
					  double operand1 = Double.parseDouble(data);
					  double operand2 = Double.parseDouble(operandTwo);
					  if(!(operand1 <= operand2)) {
						  errorMessage += bussinessRuleVar.getErrorMessage()+bussinessRuleVar.getOperandTwo() + "\n";
					  }
				  
				  } else if(operator.equals("GREATERTHANEQUAL")) {
					  double operand1 = Double.parseDouble(data);
					  double operand2 = Double.parseDouble(operandTwo);
					  if(operand1 < operand2) {
						  errorMessage += bussinessRuleVar.getErrorMessage()+bussinessRuleVar.getOperandTwo() + "\n";
					  }
				  
				  } else if(operator.equals("LESSER")) {
					  double operand1 = Double.parseDouble(data);
					  double operand2 = Double.parseDouble(operandTwo);
					  if(!(operand1 >= operand2)) {
						  errorMessage += bussinessRuleVar.getErrorMessage()+bussinessRuleVar.getOperandTwo() + "\n";
					  }
				  
				  }else if(operator.equals("LESSTHANEQUAL")) {
					  double operand1 = Double.parseDouble(data);
					  double operand2 = Double.parseDouble(operandTwo);
					  if(operand1 > operand2) {
						  errorMessage += bussinessRuleVar.getErrorMessage()+bussinessRuleVar.getOperandTwo() + "\n";
					  }
				  }
			} catch (NumberFormatException e) {
				String exceptionMsg = "NumberFormatException occured while parsing Operand 1 :" + data +", and Operand 2 :" + operandTwo;
				log.info(exceptionMsg);
				errorMessage += exceptionMsg;
				e.printStackTrace();
			}
			  
			log.info("----------------------------------------------------");
		}

		ResponseMessage rm = new ResponseMessage();
		if (errorMessage.isEmpty()) {
			rm.setResponseCode(200);
			rm.setResponseMessage("Business rules valudation successful");
		} else {
			rm.setResponseCode(400);
			rm.setResponseMessage("Business rules valudation failed: " + errorMessage);
			log.info("Error in business rule validation :" + errorMessage);
		}
		
		return rm;
	}

	@Override
	public void deleteBusinessRule(String ruleCode) {
		businessrulesrepo.delete(getBusinessRule(ruleCode));
	}

	public String getCurrentDateTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		return localDateTime.toString();
	}
}
