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
public class ManagedDNSResponse implements Serializable {

	private String [] error;
	private DNSDomainResponse [] data;
}