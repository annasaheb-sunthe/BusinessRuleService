package com.scb.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.scb.config.ServiceConfig;
import com.scb.model.AuditLog;
import com.scb.model.MsErrorLog;
import com.scb.util.ServiceUtil;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class InternalApiInvoker {
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ServiceConfig customerConfig;
	
	@Autowired
	private ServiceUtil commonMethods;
	
	public ResponseEntity<AuditLog> auditLogApiCall(AuditLog auditLog) {
		ResponseEntity<AuditLog> responseAuditLog = null;
		try {
			log.debug("AuditLogService call...");
			HttpEntity<AuditLog> entity = new HttpEntity<AuditLog>(auditLog);
			responseAuditLog = restTemplate.exchange(customerConfig.getAuditLogServiceURL(), HttpMethod.POST, entity,
					AuditLog.class);
		} catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerEx) {
			MsErrorLog msErrorLog = commonMethods.getErrorLogDetails(httpClientOrServerEx);
			msErrorLog.setErrorCode(httpClientOrServerEx.getStatusCode().toString());
			//msErrorLog.setUuid(auditLog.getUuid());
			msErrorLog.setTimeStamp(auditLog.getTimestamp().toString());
			if (HttpStatus.INTERNAL_SERVER_ERROR.equals(httpClientOrServerEx.getStatusCode())) {
				msErrorLogApiCall(msErrorLog);
				// retry logic goes here
			} else {
				// do something
			}
		} catch (Exception e) {
			MsErrorLog msErrorLog = commonMethods.getErrorLogDetails(e);
			//msErrorLog.setUuid(auditLog.getUuid());
			msErrorLog.setTimeStamp(auditLog.getTimestamp().toString());
			msErrorLogApiCall(msErrorLog);
		}
		return responseAuditLog;
	}

	public void msErrorLogApiCall(MsErrorLog msErrorLog) {
		try {
			restTemplate.postForObject(customerConfig.getErrorLogURL(), msErrorLog, MsErrorLog.class);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
