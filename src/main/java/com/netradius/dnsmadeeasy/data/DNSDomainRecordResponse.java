package com.netradius.dnsmadeeasy.data;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Holds the DNSMadeEasy Rest Api responses
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class DNSDomainRecordResponse implements Serializable {

	private String name;
	private String value;
	private long id;
	private String type;
	private String ttl;
	private String sourceId;
	private Boolean failover;
	private Boolean monitor;
	private String gtdLocation;
	private Boolean dynamicDns;
	private Boolean failed;
	private Boolean hardLink;
	private String title;
	private String redirectType;
	private String keywords;
	private String description;
	private int mxLevel;
	private int weight;
	private int priority;
	private int port;
	private int source;

	private String [] error;
	private boolean updateSuccess;
}
