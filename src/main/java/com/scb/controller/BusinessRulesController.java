package com.scb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.scb.model.BusinessRules;
import com.scb.model.RequestData;
import com.scb.model.ResponseMessage;
import com.scb.service.MainService;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BusinessRulesController {
	@Autowired
	private MainService mainservice;

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
		log.info(" Get All Transaction received: ");
		List<BusinessRules> list = mainservice.getAllBusinessRules();
		log.info("Transaction Recieved " + list);
		return new ResponseEntity<List<BusinessRules>>(list, HttpStatus.OK);
	}

	@GetMapping("/getBusinessRules/{constraintNumber}")
	public ResponseEntity<BusinessRules> getBusinessRuleById(
			@PathVariable("constraintNumber") String constraintNumber) {
		log.info(" Get Transaction By ID received: " + constraintNumber);
		BusinessRules transactionById = mainservice.getBusinessRule(constraintNumber);
		log.info("Transaction Recieved With Id" + constraintNumber + " received: " + transactionById);
		return new ResponseEntity<BusinessRules>(transactionById, HttpStatus.OK);
	}

	@RequestMapping("/BusinessRuleValidate")
	public ResponseEntity<ResponseMessage> businessRuleValidation(@RequestBody RequestData requestData,
			UriComponentsBuilder builder) {
		log.info("Request Data : " + requestData);
		ResponseMessage responseMessage = mainservice.validateBusinessRules(requestData);
		return new ResponseEntity<ResponseMessage>(responseMessage, HttpStatus.OK);
	}

	@PutMapping("/ModifyBusinessRule/{id}")
	public ResponseEntity<BusinessRules> modifyBusinessRule(@RequestBody BusinessRules businessrule) {
		mainservice.ModifyBusinessRules(businessrule);
		return new ResponseEntity<BusinessRules>(businessrule, HttpStatus.OK);
	}
}