package com.netradius.dnsmadeeasy.data;

import lombok.Data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Holds the DNS Managed Api responses
 *
 * @author Abhijeet C Kale
 */

@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class ManagedDNSRecordsResponse {

	private String [] error;
	private DNSDomainRecordResponse [] data;
	private String page;
	private String totalPages;
	private String totalRecords;
}