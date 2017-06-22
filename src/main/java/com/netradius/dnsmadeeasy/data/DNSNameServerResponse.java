package com.netradius.dnsmadeeasy.data;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Holds the DNS Name server response
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class DNSNameServerResponse implements Serializable {

	private String fqdn;
	private String ipv4;
	private String ipv6;

}
