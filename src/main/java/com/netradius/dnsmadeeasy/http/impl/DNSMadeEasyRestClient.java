package com.netradius.dnsmadeeasy.http.impl;

import com.netradius.dnsmadeeasy.data.DNSDomainResponse;
import com.netradius.dnsmadeeasy.data.ManagedDNSRequestJson;
import com.netradius.dnsmadeeasy.data.ManagedDNSResponse;
import com.netradius.dnsmadeeasy.util.DateUtils;
import com.netradius.dnsmadeeasy.util.HttpClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
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
@Data
public class DNSMadeEasyRestClient  {

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final HttpClient client = new HttpClient();
	private String restUrl;
	private String apiKey;
	private String apiSecret;

	private void settMapperProperties() {
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	}

	public DNSMadeEasyRestClient(String restUrl, String apiKey, String apiSecret) {
		this.restUrl = restUrl;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}

	public ManagedDNSResponse createDomain(String domainName) throws IOException {
		ManagedDNSResponse result = null;
		settMapperProperties();
		ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
		domainRequest.setName(domainName);
		String json = mapper.writeValueAsString(domainRequest);
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.post(restUrl + "/dns/managed/", json, apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSResponse getDomains()
			throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.get(restUrl + "/dns/managed/", apiKey, getSecretHash(requestDate), requestDate);
		ManagedDNSResponse result = null;
		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSResponse deleteDomain(String domainId) throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.delete(restUrl + "/dns/managed/" + domainId, domainId, apiKey, getSecretHash(requestDate), requestDate);
		ManagedDNSResponse result = null;
		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public DNSDomainResponse getDomain(String domainId) throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.get(restUrl + "/dns/managed/" + domainId, apiKey, getSecretHash(requestDate), requestDate);
		DNSDomainResponse result = null;

		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), DNSDomainResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSResponse updateDomainConfiguration(String domainId, String vanityId, String templateId) throws IOException {
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
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.put(restUrl + "/dns/managed/" + domainId, json, apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	private String getSecretHash(String requestDate) {
		return HmacUtils.hmacSha1Hex(getApiSecret(), requestDate);
	}

}