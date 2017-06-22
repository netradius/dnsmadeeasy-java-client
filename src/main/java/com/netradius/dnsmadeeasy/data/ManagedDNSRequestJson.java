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

	private static final long serialVersionUID = -7156545634908767685L;

	String name;
	String vanityId;
	String templateId;

}
