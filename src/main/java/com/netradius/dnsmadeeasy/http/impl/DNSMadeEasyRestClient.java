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
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
		HttpResponse response = client.get(restUrl + "/dns/managed/" + getDeleteRequest(domainId), apiKey, getSecretHash(requestDate), requestDate);
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

	public ManagedDNSResponse updateMultipleDomainConfiguration(String [] ids, String vanityId, String templateId) throws IOException {
		ManagedDNSResponse result = null;
		settMapperProperties();
		ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
		if (vanityId != null && vanityId.length() > 0) {
			domainRequest.setVanityId(vanityId);
		}
		if (templateId != null && templateId.length() > 0) {
			domainRequest.setTemplateId(templateId);
		}
		domainRequest.setIds(ids);
		String json = mapper.writeValueAsString(domainRequest);
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.put(restUrl + "/dns/managed", json, apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}


	public ManagedDNSResponse createDomains(String [] domainNames) throws IOException {
		ManagedDNSResponse result = new ManagedDNSResponse();
		settMapperProperties();
		ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
		domainRequest.setNames(domainNames);
		String json = mapper.writeValueAsString(domainRequest);
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.post(restUrl + "/dns/managed", json, apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				String domainIds = readStream(response.getEntity().getContent());
				result.setData(getDomainResponse(domainIds));
			} else {
				result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	private DNSDomainResponse[] getDomainResponse(String domainIds) {
		String retDomainIds = domainIds.replace("[", "");
		retDomainIds = retDomainIds.replace("]", "");
		String [] domains = retDomainIds.split(",");
		List<DNSDomainResponse> responses = new ArrayList<>();
		for (String domainId : domains) {
			DNSDomainResponse dnsDomainResponse = new DNSDomainResponse();
			dnsDomainResponse.setName(domainId);
			responses.add(dnsDomainResponse);
		}

		DNSDomainResponse[] responseArray = new DNSDomainResponse[responses.size()];
		responseArray = responses.toArray(responseArray);

		return responseArray;
	}

	public ManagedDNSResponse deleteDomains(String [] domainIds) throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.delete(restUrl + "/dns/managed", getDeleteRequest(domainIds), apiKey, getSecretHash
				(requestDate), requestDate);
		ManagedDNSResponse result = null;
		if (response != null) {
			result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	private String getDeleteRequest(String[] domainIds) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		for (int i = 0; i < domainIds.length; i++) {
			stringBuilder.append(domainIds[i]);
			if (i < (domainIds.length - 1)) {
				stringBuilder.append(",");
			}
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	private String getDeleteRequest(String domainId) {
		return "'{[" + domainId + "\"]}'";
	}

	private String getSecretHash(String requestDate) {
		return HmacUtils.hmacSha1Hex(getApiSecret(), requestDate);
	}

	public static String readStream(InputStream input) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

}