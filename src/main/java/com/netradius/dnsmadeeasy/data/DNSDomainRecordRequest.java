package com.netradius.dnsmadeeasy.data;

import lombok.Data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Holds the data for making DNS Record Request
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class DNSDomainRecordRequest {

	private String name;
	private String value;
	private Long id;
	private String type;
	private long ttl;
	private String gtdLocation;
	private int source;
	private int mxLevel;
	private int weight;
	private int priority;
	private int port;
}
