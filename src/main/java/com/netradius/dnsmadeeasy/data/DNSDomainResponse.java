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
public class DNSDomainResponse implements Serializable {
	private static final long serialVersionUID = -5027327191685785995L;

	private String name;
	private String id;
	private Boolean gtdEnabled;
	private String pendingActionId;
	private String folderId;
	private String created;
	private String updated;
	private DNSNameServerResponse [] nameServers;

}
