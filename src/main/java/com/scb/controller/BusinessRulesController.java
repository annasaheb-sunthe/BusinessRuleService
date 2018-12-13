package com.scb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.scb.model.AuditLog;
import com.scb.model.BusinessRules;
import com.scb.model.RequestData;
import com.scb.model.ResponseMessage;
import com.scb.service.MainService;
import com.scb.serviceImpl.InternalApiInvoker;
import com.scb.util.ServiceUtil;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BusinessRulesController {
	@Autowired
	private MainService mainservice;

	@Autowired
	private ServiceUtil commonMethods;

	@Autowired
	private InternalApiInvoker internalApiInvoker;
	
	@PostMapping("/AddBusinessRules")
	public ResponseEntity<Void> saveBussinessRule(@RequestBody BusinessRules bussinessrules,
			UriComponentsBuilder builder) {
		log.info("Business Rule to add : " + bussinessrules);
		boolean flag = mainservice.saveBussinessRule(bussinessrules);
		if (flag == false) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(builder.path("/getBussinessRules/{transactionId}").buildAndExpand(bussinessrules).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@GetMapping("/getAllBusinessRules")
	public ResponseEntity<List<BusinessRules>> getAllBusinessRules() {
		log.info("Request to get all transaction...");
		List<BusinessRules> list = mainservice.getAllBusinessRules();
		log.info("All configured business rules list :" + list);
		return new ResponseEntity<List<BusinessRules>>(list, HttpStatus.OK);
	}

	@GetMapping("/getBusinessRules/{constraintNumber}")
	public ResponseEntity<BusinessRules> getBusinessRuleById(
			@PathVariable("constraintNumber") String constraintNumber) {
		log.info("Reques to get business rule by ID is received: " + constraintNumber);
		BusinessRules transactionById = mainservice.getBusinessRule(constraintNumber);
		log.info("Transaction Recieved With Id" + constraintNumber + " received: " + transactionById);
		return new ResponseEntity<BusinessRules>(transactionById, HttpStatus.OK);
	}

	@RequestMapping("/BusinessRuleValidate")
	public ResponseEntity<ResponseMessage> businessRuleValidation(@RequestBody RequestData requestData,
			UriComponentsBuilder builder) {
		log.info("Request data - TransactionType :" + requestData.getTransactionType() + ", TransactionSubType :" 
			+ requestData.getTransactionSubType() + ", payloadFormat :" + requestData.getPayloadFormat());
		
		AuditLog auditLog = commonMethods.getAuditLog(requestData, "INITIATED", "Business rules validation request initiated");
		ResponseEntity<AuditLog> responseAuditLog = internalApiInvoker.auditLogApiCall(auditLog);
		
		ResponseMessage responseMessage = mainservice.validateBusinessRules(requestData);
		
		if (responseMessage.getResponseCode() != 200) {
			auditLog = commonMethods.getAuditLog(requestData, "FAILED", "Business rules validation failed for transaction type: " + requestData.getTransactionType());
		} else {
			auditLog = commonMethods.getAuditLog(requestData, "COMPLETED", "Business rules validation for transaction type: " + requestData.getTransactionType() + " successfully");
		}

		log.info("Response message :" + responseMessage); 
		responseAuditLog = internalApiInvoker.auditLogApiCall(auditLog);
		return new ResponseEntity<ResponseMessage>(responseMessage, HttpStatus.OK);
	}

	@PutMapping("/ModifyBusinessRule/{id}")
	public ResponseEntity<BusinessRules> modifyBusinessRule(@RequestBody BusinessRules businessrule) {
		mainservice.ModifyBusinessRules(businessrule);
		return new ResponseEntity<BusinessRules>(businessrule, HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteBusinessRule/{RuleId}")
    public ResponseEntity<Void> deleteBusinessRule(@PathVariable("RuleId") String RuleId) {
        mainservice.deleteBusinessRule(RuleId);
        return new ResponseEntity<Void>(HttpStatus.OK);  	
    }
}