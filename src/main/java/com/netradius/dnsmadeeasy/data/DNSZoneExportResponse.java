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
public class DNSZoneExportResponse implements Serializable {
	private static final long serialVersionUID = 9190853521564687354L;

	private String name;
	private long id;
	private Boolean gtdEnabled;
	private String pendingActionId;
	private String folderId;
	private String created;
	private String updated;
	private DNSNameServerResponse [] nameServers;
	private Boolean processMulti;
	private DNSNameServerResponse axfrServer;
	private String[] activeThirdParties;
	private long vanityId;
	private long templateId;
	private long transferAclId;
	private long soaID;
	private DNSNameServerResponse[]  vanityNameServers;
	private DNSDomainRecordResponse [] records;
 }
