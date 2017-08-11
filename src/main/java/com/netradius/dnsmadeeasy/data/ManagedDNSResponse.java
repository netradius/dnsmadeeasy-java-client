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
public class ManagedDNSResponse {

	private String [] error;
	private String message;
	private DNSDomainResponse [] data;
}