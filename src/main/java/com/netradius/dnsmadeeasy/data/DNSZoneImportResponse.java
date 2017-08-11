package com.netradius.dnsmadeeasy.data;

import lombok.Data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Holds the DNSMadeEasy Rest Api responses
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class DNSZoneImportResponse {

	private String name;
	private long id;
	private List<DNSDomainRecordResponse> records;
	private ManagedDNSResponse domainConfUpdateResponse;
 }
