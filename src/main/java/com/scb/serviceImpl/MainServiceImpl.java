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

	/*
	 * public List<String> XMLServiceValidation(String XPath) { XPathConverstion
	 * xpathconvert = new XPathConverstion();
	 * List<String>rules=xpathconvert.XPathConversion(XPath); return rules; }
	 */
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
		log.info("Received Request Data : " + requestData);

		// Retrieve business rules
		List<BusinessRules> list = mainservice.getBusinessRulesByTypeandCountryCode(requestData.getTransactionType(),
				requestData.getTransactionSubType());

		log.info("List of Business Rules : " + list);
		Iterator<BusinessRules> idr = list.iterator();
		BusinessRules bussinessRuleVar = null;
		String s1 = "";
		// Map<Long, String> nameMap = new HashMap<>();

		while (idr.hasNext()) {
			bussinessRuleVar = (BusinessRules) idr.next();

			String data = xpathconversion.getXPathExpressionValue(bussinessRuleVar.getOperandOne(), requestData.getPayload());
			log.info("Operand 1: " + bussinessRuleVar.getOperandOne());
			log.info("Value of XPath expression : " + data);

			String operator = bussinessRuleVar.getOperartor();
			log.info("Operator : " + operator);
			String operandTwo = bussinessRuleVar.getOperandTwo();
			log.info("Operand 2 : " + operandTwo);
			String[] values = operandTwo.split(",");

			if (operator.equals("EQUAL")) {
				int found = 0;
				for (String op2 : values) {
					log.info("op2 : " + op2);
					if ((data).equals(op2)) {
						found = 1;
						break;
					} else {
						found = 0;
					}
				}
				if (found == 1) {

				} else {
					s1 += bussinessRuleVar.getErrorMessage() + "\n";
				}

			} else if (operator.equals("NOTEQUAL")) {
				int found = 0;
				for (String op2 : values) {
					log.info("op2 : " + op2);
					if ((data).equals(op2)) {
						found = 1;
						break;
					} else {
						found = 0;
					}
				}
				
				if (found == 1) {
					s1 += bussinessRuleVar.getErrorMessage() + "\n";
				} else {

				}
			}

			else if (operator.equals("NULL")) {
				if ((data).equals("")) {
					// s1=s1;
				} else {
					s1 += bussinessRuleVar.getErrorMessage() + "\n";
				}
			} else if (operator.equals("NOTNULL")) {
				if ((data).equals(" ")) {
					s1 += bussinessRuleVar.getErrorMessage() + "\n";
				} else {
					// s1=s1;
				}
			}
			/*
			 * else if(operator.equals("GREATER")) {
			 * 
			 * } else if(operator.equals("GREATERTHANEQUAL")) {
			 * 
			 * } else if(operator.equals("LESSTHANEQUAL")) {
			 * 
			 * }
			 */

			log.info("----------------------------------------------------");
		}

		log.info("Error in Business: " + s1);
		
		ResponseMessage rm = new ResponseMessage();
		if (s1.isEmpty()) {
			rm.setResponseCode(200);
			rm.setResponseMessage("Business rules valudation successful");
		} else {
			rm.setResponseCode(400);
			rm.setResponseMessage("Business rules valudation failed: " + s1);
		}
		
		return rm;
	}

	public String getCurrentDateTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		return localDateTime.toString();
	}
}
