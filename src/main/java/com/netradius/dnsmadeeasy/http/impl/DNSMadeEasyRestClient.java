package com.netradius.dnsmadeeasy.http.impl;

import com.netradius.dnsmadeeasy.data.DNSDomainResponse;
import com.netradius.dnsmadeeasy.data.ManagedDNSRequestJson;
import com.netradius.dnsmadeeasy.data.ManagedDNSResponse;
import com.netradius.dnsmadeeasy.http.DNSMadeEasyClient;
import com.netradius.dnsmadeeasy.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.IOException;


/**
 * Client that talks to DNSMadeEasy server using Rest
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class DNSMadeEasyRestClient implements DNSMadeEasyClient {

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final HttpClient client = new HttpClient();

	private void settMapperProperties() {
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	}

	@Override
	public ManagedDNSResponse createDomain(String domainName, String restUrl, String apiKey, String secretHash, String
			requestDate) throws IOException {
		ManagedDNSResponse result = null;
		settMapperProperties();
		ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
		domainRequest.setName(domainName);
		String json = mapper.writeValueAsString(domainRequest);
		HttpResponse response = client.post(restUrl + "/dns/managed/", json, apiKey, secretHash, requestDate);
		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	@Override
	public ManagedDNSResponse getDomains(String restApiUrl, String apiKey, String secretHash, String requestDate)
			throws IOException {

		HttpResponse response = client.get(restApiUrl + "/dns/managed/", apiKey, secretHash,
				requestDate);
		ManagedDNSResponse result = null;

		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	@Override
	public ManagedDNSResponse deleteDomain(String domainId, String restApiUrl, String apiKey, String secretKeyHash,
			String requestDate) throws IOException {
		HttpResponse response = client.delete(restApiUrl + "/dns/managed/" + domainId, domainId, apiKey,
				secretKeyHash, requestDate);
		ManagedDNSResponse result = null;

		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	@Override
	public DNSDomainResponse getDomain(String domainId, String restApiUrl, String apiKey, String secretHash,
			String requestDate) throws IOException {

		HttpResponse response = client.get(restApiUrl + "/dns/managed/" + domainId, apiKey, secretHash,
				requestDate);
		DNSDomainResponse result = null;

		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), DNSDomainResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	@Override
	public ManagedDNSResponse updateDomainConfiguration(String domainId, String vanityId, String templateId, String restApiUrl,
				String apiKey, String secretHash, String requestDate) throws IOException {
		ManagedDNSResponse result = null;
		settMapperProperties();
		ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
		if (vanityId != null && vanityId.length() > 0) {
			domainRequest.setVanityId(vanityId);
		}
		if (templateId != null && templateId.length() > 0) {
			domainRequest.setTemplateId(templateId);
		}
		String json = mapper.writeValueAsString(domainRequest);
		HttpResponse response = client.put(restApiUrl + "/dns/managed/" + domainId, json, apiKey, secretHash, requestDate);
		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

}