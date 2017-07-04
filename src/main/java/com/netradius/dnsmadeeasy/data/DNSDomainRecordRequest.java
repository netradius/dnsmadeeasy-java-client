package com.netradius.dnsmadeeasy.data;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Holds the data for making DNS Record Request
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class DNSDomainRecordRequest implements Serializable {
	private static final long serialVersionUID = -1972087261966662261L;

	private String name;
	private String value;
	private long id;
	private String type;
	private long ttl;
	private String gtdLocation;
	private int source;
	private int mxLevel;
	private int weight;
	private int priority;
	private int port;
}
