package com.netradius.dnsmadeeasy.http;


import com.netradius.dnsmadeeasy.data.DNSDomainResponse;
import com.netradius.dnsmadeeasy.data.ManagedDNSResponse;

import java.io.IOException;
import java.io.Serializable;

/**
 * Contract for communicating with the DNS Made Easy Server
 *
 * @author Abhijeet C Kale
 */
//TODO no need of interface
public interface DNSMadeEasyClient extends Serializable {

	ManagedDNSResponse createDomain(String domainName, String restApiUrl, String apiKey, String secretHash, String
			requestDate) throws IOException;

	ManagedDNSResponse getDomains(String restApiUrl, String apiKey, String secretHash, String requestDate)
			throws IOException;

	ManagedDNSResponse deleteDomain(String domainId, String restApiUrl, String apiKey, String secretHash, String
			requestDate) throws IOException;

	DNSDomainResponse getDomain(String domainId, String restApiUrl, String apiKey, String secretHash,
			String requestDate) throws IOException;

	ManagedDNSResponse updateDomainConfiguration(String domainId, String vanityId, String templateId, String restApiUrl, String apiKey, String secretHash, String
			requestDate) throws IOException;

}
