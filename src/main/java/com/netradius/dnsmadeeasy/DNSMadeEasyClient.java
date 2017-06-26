package com.netradius.dnsmadeeasy;

import com.netradius.dnsmadeeasy.data.*;
import com.netradius.dnsmadeeasy.util.DateUtils;
import com.netradius.dnsmadeeasy.util.HttpClient;

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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
/**
 * Client that talks to DNSMadeEasy server using Rest
 *
 * @author Abhijeet C Kale
 */
@Slf4j
@Data
public class DNSMadeEasyClient {

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final HttpClient client = new HttpClient();
	private String restUrl;
	private String apiKey;
	private String apiSecret;

	private void settMapperProperties() {
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	}

	public DNSMadeEasyClient(String restUrl, String apiKey, String apiSecret) {
		this.restUrl = restUrl;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}

	/**
	 * Creates a domain
	 *
	 * @param domainName name for the domain user is interested to create
	 * @return if there is an error, error is populated or else null for data
	 * @throws IOException
	 */
	public ManagedDNSResponse createDomain(String domainName) throws DNSMadeEasyException {
		ManagedDNSResponse result = null;
		settMapperProperties();
		ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
		domainRequest.setName(domainName);
		String json = null;
		try {
			json = mapper.writeValueAsString(domainRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.post(restUrl + "/dns/managed/", json, apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while creating domain : " + domainName);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSResponse getDomains() throws DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.get(restUrl + "/dns/managed/", apiKey, getSecretHash(requestDate), requestDate);
		ManagedDNSResponse result = null;
		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while getting domains " );
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSResponse deleteDomain(String domainId) throws DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.delete(restUrl + "/dns/managed/", getDeleteRequest(domainId), apiKey, getSecretHash(requestDate),
				requestDate);
		ManagedDNSResponse result = null;
		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while deleting domain : " + domainId);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public DNSDomainResponse getDomain(String domainId) throws DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.get(restUrl + "/dns/managed/" + domainId, apiKey, getSecretHash(requestDate), requestDate);
		DNSDomainResponse result = null;

		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), DNSDomainResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while getting info for domain : " + domainId);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSResponse updateDomainConfiguration(String domainId, String vanityId, String templateId) throws DNSMadeEasyException {
		ManagedDNSResponse result = null;
		settMapperProperties();
		ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
		if (vanityId != null && vanityId.length() > 0) {
			domainRequest.setVanityId(vanityId);
		}
		if (templateId != null && templateId.length() > 0) {
			domainRequest.setTemplateId(templateId);
		}
		String json;
		try {
			json = mapper.writeValueAsString(domainRequest);
		} catch (IOException e) {
			log.error("Error occurred while preparing request to update info for domain : " + domainId);
			throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
		}
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.put(restUrl + "/dns/managed/" + domainId, json, apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while updating info for domain : " + domainId);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSResponse updateMultipleDomainConfiguration(String [] ids, String vanityId, String templateId) throws DNSMadeEasyException {
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
		String json;
		try {
			json = mapper.writeValueAsString(domainRequest);
		} catch (IOException e) {
			log.error("Error occurred while preparing request to update info for domains : " + ids);
			throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
		}
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.put(restUrl + "/dns/managed", json, apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while updating info for domains : " + ids);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}


	public ManagedDNSResponse createDomains(String [] domainNames) throws DNSMadeEasyException {
		ManagedDNSResponse result = new ManagedDNSResponse();
		settMapperProperties();
		ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
		domainRequest.setNames(domainNames);
		String json;
		try {
			json = mapper.writeValueAsString(domainRequest);
		} catch (IOException e) {
			log.error("Error occurred while preparing request to create domains : " + domainNames);
			throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
		}
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.post(restUrl + "/dns/managed", json, apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				String domainIds;
				try {
					domainIds = readStream(response.getEntity().getContent());
				} catch (IOException e) {
					log.error("Error occurred while creating domains : " + domainNames);
					throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
				}
				result.setData(getDomainResponse(domainIds));
			} else {
				try {
					result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
				} catch (IOException e) {
					log.error("Error occurred while creating domains : " + domainNames);
					throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
				}
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSResponse deleteDomains(String [] domainIds) throws DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.delete(restUrl + "/dns/managed", getDeleteRequest(domainIds), apiKey, getSecretHash
				(requestDate), requestDate);
		ManagedDNSResponse result = null;
		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while deleting domains : " + domainIds);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSRecordsResponse getDNSRecord(String domainId) throws DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.get(restUrl + "/dns/managed/" + domainId + "/records", apiKey,
				getSecretHash(requestDate), requestDate);
		ManagedDNSRecordsResponse result = null;
		result = getManagedDNSRecordsResponse(domainId, response, result);
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSRecordsResponse getDNSRecordByType(String domainId, String type) throws DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.get(restUrl + "/dns/managed/" + domainId + "/records?type=" + type, apiKey,
				getSecretHash(requestDate), requestDate);
		ManagedDNSRecordsResponse result = null;
		result = getManagedDNSRecordsResponse(domainId, response, result);
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	public ManagedDNSRecordsResponse getDNSRecordByTypeAndRecordName(String domainId, String type, String recordName) throws
			DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.get(restUrl + "/dns/managed/" + domainId + "/records?recordName=" +
				recordName + "&type=" + type, apiKey, getSecretHash(requestDate), requestDate);
		ManagedDNSRecordsResponse result = null;
		result = getManagedDNSRecordsResponse(domainId, response, result);
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	/**
	 *
	 * @param domainId
	 * @param name
	 * @param type
	 * @param value
	 * @param gtdLocation
	 * @param ttl
	 * @return The Record details in the DNSDomainRecordResponse when successful or error
	 * @throws DNSMadeEasyException
	 */
	public DNSDomainRecordResponse createDNSRecord(String domainId, String name, String type, String value,
			String gtdLocation, long ttl) throws DNSMadeEasyException {
		String json;
		String requestDate = DateUtils.dateToStringInGMT();
		DNSDomainRecordRequest dnsDomainRecordRequest = new DNSDomainRecordRequest();
		dnsDomainRecordRequest.setName(name);
		dnsDomainRecordRequest.setType(type);
		dnsDomainRecordRequest.setValue(value);
		dnsDomainRecordRequest.setGtdLocation(gtdLocation);
		dnsDomainRecordRequest.setTtl(ttl);
		try {
			json = mapper.writeValueAsString(dnsDomainRecordRequest);
		} catch (IOException e) {
			log.error("Error occurred while preparing request to create a record for domain with id : " + domainId);
			throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
		}
		HttpResponse response = client.post(restUrl + "/dns/managed/" + domainId + "/records/", json, apiKey,
				getSecretHash(requestDate), requestDate);
		DNSDomainRecordResponse result = null;
		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), DNSDomainRecordResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while creating DNS domain record for Domain: " + domainId);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	/**
	 *
	 * @param domainId
	 * @param name
	 * @param type
	 * @param value
	 * @param gtdLocation
	 * @param ttl
	 * @param id
	 * @return true when update successful and false upon update fails
	 * @throws DNSMadeEasyException
	 */
	public boolean updateDNSRecord(String domainId, String name, String type, String value,
				String gtdLocation, long ttl, long id) throws DNSMadeEasyException {
		String json;
		String requestDate = DateUtils.dateToStringInGMT();
		DNSDomainRecordRequest dnsDomainRecordRequest = new DNSDomainRecordRequest();
		dnsDomainRecordRequest.setName(name);
		dnsDomainRecordRequest.setType(type);
		dnsDomainRecordRequest.setValue(value);
		dnsDomainRecordRequest.setGtdLocation(gtdLocation);
		dnsDomainRecordRequest.setTtl(ttl);
		dnsDomainRecordRequest.setId(id);
		try {
			json = mapper.writeValueAsString(dnsDomainRecordRequest);
		} catch (IOException e) {
			log.error("Error occurred while preparing request to create a record for domain with id : " + domainId);
			throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
		}
		HttpResponse response = client.put(restUrl + "/dns/managed/" + domainId + "/records/" + id, json, apiKey,
				getSecretHash(requestDate), requestDate);
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}


	public boolean deleteManagedDNSRecord(String domainId, String recordId) throws DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.delete(restUrl + "/dns/managed/" + domainId + "/records/" + recordId, apiKey,
				getSecretHash(requestDate), requestDate);
		boolean result = false;
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = true;
			}
		}

		return result;
	}

	public DNSDomainRecordResponse[] createDNSMultiRecord(String domainId, List<DNSDomainRecordRequest> multiRecords)
			throws DNSMadeEasyException {
		String json;
		String requestDate = DateUtils.dateToStringInGMT();
		try {
			json = mapper.writeValueAsString(multiRecords);
		} catch (IOException e) {
			log.error("Error occurred while preparing request to create multi records request for domain with id : " +
					domainId);
			throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
		}
		HttpResponse response = client.post(restUrl + "/dns/managed/" + domainId + "/records/createMulti", json,
				apiKey, getSecretHash(requestDate), requestDate);
		DNSDomainRecordResponse[] result = null;
		if (response != null) {
			try {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
					result = mapper.readValue(response.getEntity().getContent(), DNSDomainRecordResponse[].class);
				} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
					DNSDomainRecordResponse response1 = mapper.readValue(response.getEntity().getContent(),
							DNSDomainRecordResponse.class);
					result = new DNSDomainRecordResponse[] {response1};
				}
			} catch (IOException e) {
				log.error("Error occurred while creating multi DNS domain record for Domain: " + domainId);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
		}
		if (result != null) {
			log.debug(result.toString());
		}

		return result;
	}

	/**
	 *
	 * @param domainId
	 * @param multiRecords
	 * @return
	 * @throws DNSMadeEasyException
	 */
	public boolean updateDNSMultiRecord(String domainId, List<DNSDomainRecordRequest> multiRecords)
			throws DNSMadeEasyException {
		String json;
		String requestDate = DateUtils.dateToStringInGMT();
		try {
			json = mapper.writeValueAsString(multiRecords);
		} catch (IOException e) {
			log.error("Error occurred while preparing request to update multi records request for domain with id : " +
					domainId);
			throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
		}
		HttpResponse response = client.put(restUrl + "/dns/managed/" + domainId + "/records/updateMulti", json,
				apiKey, getSecretHash(requestDate), requestDate);
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return true;
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				return false;
			}
		}
		return false;
	}

	/**
	 *
	 * @param domainId
	 * @param recordIds
	 * @return
	 * @throws DNSMadeEasyException
	 */
	public boolean deleteMultiDNSRecords(String domainId, String[] recordIds) throws DNSMadeEasyException {
		String requestDate = DateUtils.dateToStringInGMT();
		HttpResponse response = client.delete(restUrl + "/dns/managed/" + domainId + "/records?" +
				getRecordIds(recordIds), apiKey, getSecretHash(requestDate), requestDate);
		boolean result = false;
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = true;
			}
		}

		return result;
	}

	private String getRecordIds(String[] recordIds) {
		StringBuilder recordIdsStr = new StringBuilder();
		if (recordIds != null) {
			for (int i = 0; i <recordIds.length; i ++) {
				if (i == 0) {
					recordIdsStr.append("ids=");
				} else {
					recordIdsStr.append("&ids=");
				}
				recordIdsStr.append(recordIds[i]);
			}
		}

		return recordIdsStr.toString();
	}

	private ManagedDNSRecordsResponse getManagedDNSRecordsResponse(String domainId, HttpResponse response,
			ManagedDNSRecordsResponse result) throws DNSMadeEasyException {
		if (response != null) {
			try {
				result = mapper.readValue(response.getEntity().getContent(), ManagedDNSRecordsResponse.class);
			} catch (IOException e) {
				log.error("Error occurred while getting DNS domain records : " + domainId);
				throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
			}
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
		return "[" + domainId + "]";
	}

	private String getSecretHash(String requestDate) {
		return HmacUtils.hmacSha1Hex(getApiSecret(), requestDate);
	}

	public static String readStream(InputStream input) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

	private DNSMadeEasyException getError(Exception x, String message, int status) {
		return new DNSMadeEasyException(status, message , x);
	}

}