package com.netradius.dnsmadeeasy.data;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Json Document for Domain Request
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class ManagedDNSRequestJson implements Serializable {
	private static final long serialVersionUID = -8477554856279865763L;

	String name;
	String vanityId;
	String templateId;
	String [] ids;
	String [] names;

}
