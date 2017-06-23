package com.netradius.dnsmadeeasy.data;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;


/**
 * Holds the DNS Managed Api responses
 *
 * @author Abhijeet C Kale
 */

@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class ManagedDNSRecordsResponse implements Serializable {
	private static final long serialVersionUID = -2218375262213914941L;

	private String [] error;
	private DNSDomainRecordResponse [] data;
	private String page;
	private String totalPages;
	private String totalRecords;
}