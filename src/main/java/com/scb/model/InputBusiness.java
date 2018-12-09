package com.scb.model;



import javax.persistence.Column;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @XmlRootElement
public class InputBusiness {
	@Id
    @Column
    private long transactionID;
    @Column
    private String transactionType;
    @Column
    private String transactionSubType;
    @Column
    private String payloadFormat;
    @Column
    private String sourceSystem;
    @Column (length=10000)
    private String payload;
    @Column
    private String status;
    @Column 
    private String createdOn; //current timestamp
    @Column 
    private String updatedOn; //current timestamp

}
