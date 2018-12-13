package com.scb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class ServiceConfig {

	@Value("${GCG.errorLogURL}")
	private String errorLogURL;
	
	@Value("${msbif.lti.auditLogServiceURL}")
	private String auditLogServiceURL;
}
