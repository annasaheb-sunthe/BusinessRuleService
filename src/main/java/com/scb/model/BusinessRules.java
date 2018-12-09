package com.scb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @Builder @Entity @Table(name="businessrule") @NoArgsConstructor @AllArgsConstructor @ToString @XmlRootElement
public class BusinessRules {
	@Id
	@Column
	private String ruleCode;
	@Column(name="transactiontype", nullable=false)
	private String transactionType;
	@Column(name="transactionsubtype", nullable=false)
	private String transactionSubType;
	@Column(name="rulename", nullable=false)
	private String ruleName;
	@Column(name="applicability", nullable=false)
	private long applicability;
	@Column(name="operartor", nullable=false)
	private String operartor;
	@Column(name="operandOne", nullable=false)
	private String operandOne;
	private String operandTwo;
	private String errorMessage;
	@Column(name="createdon", nullable=false, updatable=false)
	//@CreationTimestamp
	private String createdOn;
	@Column(name="updatedon", nullable=false)
	//@UpdateTimestamp
	private String updatedOn;
}
