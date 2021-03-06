package com.netradius.dnsmadeeasy;

import com.netradius.dnsmadeeasy.assembler.DNSRecordRequestResponseAssembler;
import com.netradius.dnsmadeeasy.assembler.DNSRecordResponseAssembler;
import com.netradius.dnsmadeeasy.assembler.DNSZoneExportResponseAssembler;
import com.netradius.dnsmadeeasy.data.DNSDomainRecordRequest;
import com.netradius.dnsmadeeasy.data.DNSDomainRecordResponse;
import com.netradius.dnsmadeeasy.data.DNSDomainResponse;
import com.netradius.dnsmadeeasy.data.DNSZoneExportResponse;
import com.netradius.dnsmadeeasy.data.DNSZoneImportRequest;
import com.netradius.dnsmadeeasy.data.DNSZoneImportResponse;
import com.netradius.dnsmadeeasy.data.ManagedDNSRecordsResponse;
import com.netradius.dnsmadeeasy.data.ManagedDNSRequestJson;
import com.netradius.dnsmadeeasy.data.ManagedDNSResponse;
import com.netradius.dnsmadeeasy.data.RecordType;
import com.netradius.dnsmadeeasy.util.DateUtils;
import com.netradius.dnsmadeeasy.util.HttpClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.netradius.dnsmadeeasy.data.RecordType.ANAME;

/**
 * Client that talks to DNSMadeEasy server using Rest.
 *
 * @author Abhijeet C Kale
 */
@Slf4j
@Data
public class DNSMadeEasyClient {

  public static final String DEFINITION_SEPARATOR = ";";
  public static final String NAME_SEPARATOR = "\\.";
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final HttpClient client = new HttpClient();
  private String restUrl;
  private String apiKey;
  private String apiSecret;
  private static final DNSZoneExportResponseAssembler dNSZoneExportResponseAssembler = new
      DNSZoneExportResponseAssembler();
  private static final DNSRecordRequestResponseAssembler dNSRecordRequestResponseAssembler = new
      DNSRecordRequestResponseAssembler();
  private static final DNSRecordResponseAssembler dNSRecordResponseAssembler = new
      DNSRecordResponseAssembler();

  private void settMapperProperties() {
    mapper.setSerializationInclusion(Inclusion.NON_NULL);
    mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
  }

  public DNSMadeEasyClient(String restUrl, String apiKey, String apiSecret) {
    this.restUrl = restUrl;
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
  }

  /**
   * Creates a domain.
   *
   * @param domainName name for the domain user is interested to create
   * @return if there is an error, error is populated or else null for data
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSResponse createDomain(String domainName) throws DNSMadeEasyException {
    ManagedDNSResponse result = null;
    settMapperProperties();
    ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
    domainRequest.setName(domainName);
    String json;
    try {
      json = mapper.writeValueAsString(domainRequest);
    } catch (IOException e) {
      log.error("Error occurred while creating domains json request. " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.post(restUrl + "/dns/managed/", json, apiKey,
        getSecretHash(requestDate), requestDate);
    if (response != null) {
      try {
        result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
      } catch (IOException e) {
        log.error("Error occurred while creating domain : " + domainName + ". "
            + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Fetches all the domains under the account.
   *
   * @return All the domains information
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSResponse getDomains() throws DNSMadeEasyException {
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.get(restUrl + "/dns/managed/", apiKey,
        getSecretHash(requestDate), requestDate);
    ManagedDNSResponse result = null;
    if (response != null) {
      try {
        result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
      } catch (IOException e) {
        log.error("Error occurred while getting domains . " + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Deletes the domain for the given domain id, if the domain is allowed to be deleted.
   *
   * @param domainId of the domain to be deleted
   * @return nothing if successful or error in case the delete is not allowed
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSResponse deleteDomain(String domainId) throws DNSMadeEasyException {
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.delete(restUrl + "/dns/managed/", getDeleteRequest(domainId),
        apiKey, getSecretHash(requestDate), requestDate);
    ManagedDNSResponse result = null;
    if (response != null) {
      try {
        result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
      } catch (IOException e) {
        log.error("Error occurred while deleting domain : " + domainId + ". " + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Fetches the domain information.
   *
   * @param domainId The domain user is interested in
   * @return Domain information if found
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public DNSDomainResponse getDomain(long domainId) throws DNSMadeEasyException {
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.get(restUrl + "/dns/managed/" + domainId, apiKey,
        getSecretHash(requestDate), requestDate);
    DNSDomainResponse result = null;

    if (response != null) {
      try {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NOT_FOUND) {
          result = mapper.readValue(response.getEntity().getContent(), DNSDomainResponse.class);
        } else {
          throw new DNSMadeEasyException(HttpStatus.SC_NOT_FOUND, "Unable to find domain with id: "
              + domainId, null);
        }
      } catch (IOException e) {
        log.error("Error occurred while getting info for domain : " + domainId + ". "
            + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Updates the domain config data like the vanity id or template id if valid data sent
   * otherwise reports the error.
   *
   * @param domainId   The domain user is interested in
   * @param vanityId   A valid vanity id
   * @param templateId A valid template id
   * @return Error if the sent ids are invalid
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSResponse updateDomainConfiguration(long domainId, String vanityId,
                                                      String templateId)
      throws DNSMadeEasyException {
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
      log.error("Error occurred while preparing request to update info for domain : " + domainId
          + ". " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.put(restUrl + "/dns/managed/" + domainId, json, apiKey,
        getSecretHash(requestDate), requestDate);
    if (response != null) {
      try {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
          result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          String content = readStream(response.getEntity().getContent());
          result = new ManagedDNSResponse();
          result.setMessage(content);
        }
      } catch (IOException e) {
        log.error("Error occurred while updating info for domain : " + domainId + ". "
            + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Updates multiple the domains config data like the vanity id or template id if valid data sent
   * otherwise reports the error.
   *
   * @param ids        identifiers for multiple domains
   * @param vanityId   a valid vanity id
   * @param templateId a valid template id
   * @return Error if the sent ids are invalid
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSResponse updateMultipleDomainConfiguration(String[] ids, String vanityId,
                                                              String templateId)
      throws DNSMadeEasyException {
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
      log.error("Error occurred while preparing request to update info for domains : " + ids
          + ". " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.put(restUrl + "/dns/managed", json, apiKey,
        getSecretHash(requestDate), requestDate);
    if (response != null) {
      try {
        result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
      } catch (IOException e) {
        log.error("Error occurred while updating info for domains : " + ids + ". "
            + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Create Multiple domains.
   *
   * @param domainNames for which new domains to be created
   * @return Domain ids of new created domains or error if unsuccessful
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSResponse createDomains(String[] domainNames) throws DNSMadeEasyException {
    ManagedDNSResponse result = new ManagedDNSResponse();
    settMapperProperties();
    ManagedDNSRequestJson domainRequest = new ManagedDNSRequestJson();
    domainRequest.setNames(domainNames);
    String json;
    try {
      json = mapper.writeValueAsString(domainRequest);
    } catch (IOException e) {
      log.error("Error occurred while preparing request to create domains : " + domainNames
          + ". " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.post(restUrl + "/dns/managed", json, apiKey,
        getSecretHash(requestDate), requestDate);
    if (response != null) {
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
        String domainIds;
        try {
          domainIds = readStream(response.getEntity().getContent());
        } catch (IOException e) {
          log.error("Error occurred while creating domains : " + domainNames + ". "
              + e.getMessage(), e);
          throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
        }
        result.setData(getDomainResponse(domainIds));
      } else {
        try {
          result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
        } catch (IOException e) {
          log.error("Error occurred while creating domains : " + domainNames + ". "
              + e.getMessage(), e);
          throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
        }
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Delete Multiple domains.
   *
   * @param domainIds list of domain ids to be deleted
   * @return Nothing if successful or error when
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSResponse deleteDomains(String[] domainIds) throws DNSMadeEasyException {
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.delete(restUrl + "/dns/managed", getDeleteRequest(domainIds),
        apiKey, getSecretHash(requestDate), requestDate);
    ManagedDNSResponse result = null;
    if (response != null) {
      try {
        result = mapper.readValue(response.getEntity().getContent(), ManagedDNSResponse.class);
      } catch (IOException e) {
        log.error("Error occurred while deleting domains : " + domainIds + ". "
            + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Get the records for the domain.
   *
   * @param domainId The domain user is interested in
   * @return Records if any under the domain
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSRecordsResponse getDNSRecord(long domainId) throws DNSMadeEasyException {
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

  /**
   * Get records by type for a domain.
   *
   * @param domainId The domain user is interested in
   * @param type     the type of record to be fetched
   * @return record details if found
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSRecordsResponse getDNSRecordByType(long domainId, String type)
      throws DNSMadeEasyException {
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.get(restUrl + "/dns/managed/" + domainId + "/records?type="
            + type, apiKey,
        getSecretHash(requestDate), requestDate);
    ManagedDNSRecordsResponse result = null;
    result = getManagedDNSRecordsResponse(domainId, response, result);
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Fetch a record under a domain for given type and record name.
   *
   * @param domainId   The domain user is interested in
   * @param type       the type of record to be fetched
   * @param recordName name of the record associated with domain
   * @return record details if found
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public ManagedDNSRecordsResponse getDNSRecordByTypeAndRecordName(long domainId, String type,
                                                                   String recordName)
      throws DNSMadeEasyException {
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.get(restUrl + "/dns/managed/" + domainId
        + "/records?recordName=" + recordName + "&type=" + type, apiKey,
        getSecretHash(requestDate), requestDate);
    ManagedDNSRecordsResponse result = null;
    result = getManagedDNSRecordsResponse(domainId, response, result);
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Creates a record under a specified domain.
   * Record type must be one of: [A, A6, AAAA, AAAANAME, ANAME, CNAME, DNAME, HTTPRED, MX, NAPTR,
   * NS, PTR, SPF, SRV, TXT].
   *
   * @param domainId The domain user is interested in
   * @param dnsDomainRecordRequest the wrapper for record request
   * @return The Record details in the DNSDomainRecordResponse when successful or error
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public DNSDomainRecordResponse createDNSRecord(long domainId, DNSDomainRecordRequest
      dnsDomainRecordRequest) throws DNSMadeEasyException {
    String json;
    String requestDate = DateUtils.dateToStringInGMT();
    try {
      json = mapper.writeValueAsString(dnsDomainRecordRequest);
    } catch (IOException e) {
      log.error("Error occurred while preparing request to create a record for domain with id : "
          + domainId + ". " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    HttpResponse response = client.post(restUrl + "/dns/managed/" + domainId + "/records/", json,
        apiKey, getSecretHash(requestDate), requestDate);
    DNSDomainRecordResponse result = null;
    if (response != null) {
      try {
        result = mapper.readValue(response.getEntity().getContent(),
            DNSDomainRecordResponse.class);
      } catch (IOException e) {
        log.error("Error occurred while creating DNS domain record for Domain: " + domainId + ". "
            + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  private void createDNSRecord(Long domainId, List<DNSDomainRecordResponse> recordResponses,
                               DNSDomainRecordRequest dnsDomainRecordRequest) {
    DNSDomainRecordResponse dnsDomainRecordResponse;
    try {
      dnsDomainRecordResponse = createDNSRecord(domainId, dnsDomainRecordRequest);
    } catch (DNSMadeEasyException e) {
      dnsDomainRecordResponse = new DNSDomainRecordResponse();
      String[] error = { "Unable to create the record with name : "
          + dnsDomainRecordRequest.getName() };
      dnsDomainRecordResponse.setError(error);
    }
    recordResponses.add(dnsDomainRecordResponse);
  }

  /**
   * Updates a record under a domain.
   * Record type must be one of: [A, A6, AAAA, AAAANAME, ANAME, CNAME, DNAME, HTTPRED, MX,
   * NAPTR, NS, PTR, SPF, SRV, TXT].
   *
   * @param domainId The domain user is interested in
   * @param dnsDomainRecordRequest the wrapper for record request
   * @return true when update successful and false upon update fails
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public boolean updateDNSRecord(long domainId, DNSDomainRecordRequest dnsDomainRecordRequest)
      throws DNSMadeEasyException {
    String json;
    String requestDate = DateUtils.dateToStringInGMT();
    try {
      json = mapper.writeValueAsString(dnsDomainRecordRequest);
    } catch (IOException e) {
      log.error("Error occurred while preparing request to create a record for domain with id : "
          + domainId + ". " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    HttpResponse response = client.put(restUrl + "/dns/managed/" + domainId + "/records/"
            + dnsDomainRecordRequest.getId(), json, apiKey, getSecretHash(requestDate),
        requestDate);
    if (response != null) {
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        return true;
      } else {
        return false;
      }
    }

    return false;
  }

  private void updateDNSRecord(long domainId, List<DNSDomainRecordResponse> recordResponses,
                               DNSDomainRecordRequest recordRequest) {
    DNSDomainRecordResponse dnsDomainRecordResponse;
    Boolean result = false;
    try {
      result = updateDNSRecord(domainId, recordRequest);
    } catch (DNSMadeEasyException e) {
      dnsDomainRecordResponse = new DNSDomainRecordResponse();
      String[] error = {"Unable to update the record with name : " + recordRequest.getName()
          + " and type : " + recordRequest.getType()};
      dnsDomainRecordResponse.setUpdateSuccess(false);
      dnsDomainRecordResponse.setError(error);
    }
    dnsDomainRecordResponse = dNSRecordResponseAssembler.assemble(recordRequest);
    dnsDomainRecordResponse.setUpdateSuccess(result);
    recordResponses.add(dnsDomainRecordResponse);
  }

  /**
   * Deletes a record under the domain.
   *
   * @param domainId The domain user is interested in
   * @param recordId identifier for the record to be deleted
   * @return true if deleted else false if delete fails
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public boolean deleteManagedDNSRecord(long domainId, long recordId) throws DNSMadeEasyException {
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.delete(restUrl + "/dns/managed/" + domainId + "/records/"
            + recordId, apiKey, getSecretHash(requestDate), requestDate);
    boolean result = false;
    if (response != null) {
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        result = true;
      }
    }

    return result;
  }

  /**
   * Create multiple records under a domain.
   *
   * @param domainId The domain user is interested in
   * @param multiRecords list of data for the records to be created under the domain
   * @return The new records details or error if creation fails
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public DNSDomainRecordResponse[] createDNSMultiRecord(long domainId,
                                                        List<DNSDomainRecordRequest> multiRecords)
      throws DNSMadeEasyException {
    String json;
    String requestDate = DateUtils.dateToStringInGMT();
    try {
      json = mapper.writeValueAsString(multiRecords);
    } catch (IOException e) {
      log.error("Error occurred while preparing request to create multi records request for"
          + " domain with id : " + domainId + ". " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    HttpResponse response = client.post(restUrl + "/dns/managed/" + domainId
            + "/records/createMulti", json, apiKey, getSecretHash(requestDate), requestDate);
    DNSDomainRecordResponse[] result = null;
    if (response != null) {
      try {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
          result = mapper.readValue(response.getEntity().getContent(),
              DNSDomainRecordResponse[].class);
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
          DNSDomainRecordResponse response1 = mapper.readValue(response.getEntity().getContent(),
              DNSDomainRecordResponse.class);
          result = new DNSDomainRecordResponse[]{response1};
        }
      } catch (IOException e) {
        log.error("Error occurred while creating multi DNS domain record for Domain: " + domainId
            + ". " + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    if (result != null) {
      log.debug(result.toString());
    }

    return result;
  }

  /**
   * Update multiple records under a domain.
   *
   * @param domainId The domain user is interested in
   * @param multiRecords list of data for the records that need to be updated
   * @return true if update successful otherwise false on error
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public boolean updateDNSMultiRecord(long domainId, List<DNSDomainRecordRequest> multiRecords)
      throws DNSMadeEasyException {
    String json;
    String requestDate = DateUtils.dateToStringInGMT();
    try {
      json = mapper.writeValueAsString(multiRecords);
    } catch (IOException e) {
      log.error("Error occurred while preparing request to update multi records request for domain"
          + " with id : " + domainId + ". " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    HttpResponse response = client.put(restUrl + "/dns/managed/" + domainId
            + "/records/updateMulti", json, apiKey, getSecretHash(requestDate), requestDate);
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
   * Delete multiple records under a domain.
   *
   * @param domainId The domain user is interested in
   * @param recordIds the ids of the records to be deleted
   * @return true if successful otherwise false
   * @throws DNSMadeEasyException thrown in case of an error
   */
  public boolean deleteMultiDNSRecords(long domainId, String[] recordIds)
      throws DNSMadeEasyException {
    String requestDate = DateUtils.dateToStringInGMT();
    HttpResponse response = client.delete(restUrl + "/dns/managed/" + domainId + "/records?"
        + getRecordIds(recordIds), apiKey, getSecretHash(requestDate), requestDate);
    boolean result = false;
    if (response != null) {
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        result = true;
      }
    }

    return result;
  }

  /**
   * Exports the domain.
   *
   * @param domainName domain in which the user is interested in
   * @return The Zone information
   * @throws DNSMadeEasyException thrown in case of error
   */
  public String exportZone(String domainName) throws DNSMadeEasyException {
    DNSZoneExportResponse result;
    DNSDomainResponse domainDetails = null;
    ManagedDNSResponse domains = getDomains();
    for (DNSDomainResponse dnsDomainResponse : domains.getData()) {
      if (dnsDomainResponse.getName().equalsIgnoreCase(domainName)) {
        domainDetails = dnsDomainResponse;
      }
    }

    DNSDomainResponse dnsDomainResponse = getDomain(domainDetails.getId());
    result = dNSZoneExportResponseAssembler.assemble(dnsDomainResponse);

    // add records under the domain to response
    ManagedDNSRecordsResponse managedDNSRecordsResponse = getDNSRecord(domainDetails.getId());
    if (managedDNSRecordsResponse != null && managedDNSRecordsResponse.getData() != null) {
      result.setRecords(managedDNSRecordsResponse.getData());
    }
    if (result != null) {
      log.debug(result.toString());
    }
    String json;
    try {
      json = mapper.writeValueAsString(result);
    } catch (IOException e) {
      log.error("Error occurred while preparing json response for Zone export of domain : "
          + domainName  + ". " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }
    return json;
  }

  /**
   * Allows to import a Zone from the done definition file, the file should be of type
   * src/integration-tests/resources/zoneimport.txt.
   * Note this is kept just incase we need to support a zone import similar
   * to the one provided by the webapplication.
   *
   * @param zoneDefinition file which contains zone import definition
   * @return The imported zone details are import successful
   * @throws DNSMadeEasyException in case any error occurs
   */
  public DNSZoneImportResponse importZoneFromZoneDefinition(File zoneDefinition)
      throws DNSMadeEasyException {
    DNSZoneImportResponse result = null;
    // read the file using Scanner, try-with-resources
    String nextLine;
    List<DNSDomainRecordRequest> recordRequests = new ArrayList<>();
    try (Scanner scanner = new Scanner(zoneDefinition)) {
      while (scanner.hasNext()) {
        nextLine = scanner.nextLine();
        if (nextLine != null && !nextLine.isEmpty()) {
          if (nextLine.startsWith(DEFINITION_SEPARATOR)) {
            continue;
          }
          // parse the record in the definition file
          String[] row = nextLine.split(" ");
          if (row != null && row.length >= 5) {
            DNSDomainRecordRequest dnsDomainRecordRequest = new DNSDomainRecordRequest();
            dnsDomainRecordRequest.setName(row[0]); // name of the record
            dnsDomainRecordRequest.setTtl(Long.parseLong(row[1])); // TTL value
            dnsDomainRecordRequest.setType(row[3]); // type of the record
            // check the type
            if (row[3] != null && !row[3].isEmpty()) {
              RecordType type = RecordType.getEnumByValue(row[3]);
              if (type != null) {
                switch (type) {
                  case MX:
                    populateMXType(dnsDomainRecordRequest, row);
                    break;
                  case CAA:
                    populateCAAType(dnsDomainRecordRequest, row);
                    break;
                  case SRV:
                    populateSRVType(dnsDomainRecordRequest, row);
                    break;
                  case ANAME:
                    populateANameType(dnsDomainRecordRequest, row);
                    break;
                  default:
                    populateDefaultType(dnsDomainRecordRequest, row);
                    break;
                }
              }
            }
            // finally
            recordRequests.add(dnsDomainRecordRequest);
          }
        }
      }
    } catch (IOException e) {
      log.error("Error occurred while importing Zone Definition file : " + e.getMessage(), e);
      throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
    }

    // start importing the records
    // get the domain name from the first record
    if (recordRequests != null && !recordRequests.isEmpty()) {
      result = importRecords(recordRequests);
    }
    // consolidate the errors response
    return result;
  }

  /**
   * This will clone the records from a Domain to another Domain.
   * Note the method will not create a domain if not found.
   *
   * @param fromDomain zone/domain from which the
   * @param toDomain an existing zone/domain must exist
   * @return contains the details of the Zone transfer
   * @throws DNSMadeEasyException in case any error occurs
   */
  public DNSZoneImportResponse cloneZone(String fromDomain, String toDomain)
      throws DNSMadeEasyException {
    DNSZoneImportResponse result = new DNSZoneImportResponse();

    // get the domain details
    DNSDomainResponse fromDomainDetails = getDomainDetails(fromDomain, false);
    if (fromDomainDetails == null) {
      throw new DNSMadeEasyException(HttpStatus.SC_NOT_FOUND, "Unable to find domain : "
          + fromDomain, null);
    }
    DNSDomainResponse toDomainDetails = getDomainDetails(toDomain, false);
    if (toDomainDetails == null) {
      throw new DNSMadeEasyException(HttpStatus.SC_NOT_FOUND, "Unable to find domain : "
          + toDomain, null);
    }
    if (toDomainDetails.getId() == fromDomainDetails.getId()) {
      throw new DNSMadeEasyException(HttpStatus.SC_BAD_REQUEST, "From and To Domains must be "
          + "different", null);
    }
    // get the records from the domain
    ManagedDNSRecordsResponse managedDNSRecordsResponse = getDNSRecord(fromDomainDetails.getId());
    // create the DNSDomainRecordRequest
    List<DNSDomainRecordRequest> recordRequests = new ArrayList<>();
    if (managedDNSRecordsResponse.getData() != null && managedDNSRecordsResponse.getData()
        .length > 0) {
      for (DNSDomainRecordResponse dnsDomainRecordResponse : managedDNSRecordsResponse.getData()) {
        DNSDomainRecordRequest recordRequest
            = dNSRecordRequestResponseAssembler.assemble(dnsDomainRecordResponse);
        recordRequest.setId(null);
        recordRequests.add(recordRequest);
      }
      result = importRecords(recordRequests, toDomainDetails.getId());
    }
    if (fromDomainDetails != null) {
      result.setName(fromDomainDetails.getName());
      result.setId(fromDomainDetails.getId());
    }
    // handle the template id and vanity id
    updateDomainConfig(toDomainDetails.getId(), toDomainDetails.getName(),
        String.valueOf(fromDomainDetails.getVanityId()),
        String.valueOf(fromDomainDetails.getTemplateId()), result);
    return result;
  }

  /**
   * Imports a Zone for the given JSON information, if there is no zone/domain found
   * for the given id then a zone/domain is created and then information is added to the
   * new zone.
   *
   * @param jsonDomainFile File containing zone information in JSON format
   * @return the Zone import process response
   * @throws DNSMadeEasyException thrown incase of error
   */
  public DNSZoneImportResponse importZone(File jsonDomainFile) throws DNSMadeEasyException {
    DNSZoneImportResponse result = new DNSZoneImportResponse();
    DNSZoneImportRequest jsonImport;
    try {
      jsonImport = mapper.readValue(jsonDomainFile, DNSZoneImportRequest.class);
    } catch (IOException e) {
      log.error("Error occurred while parsing the JSON file : " + e.getMessage(), e);
      throw new DNSMadeEasyException(HttpStatus.SC_NOT_FOUND, "Unable to parse the JSON File", e);
    }

    return getDnsZoneImportResponse(result, jsonImport);
  }

  /**
   * Imports a Zone for the given JSON information, if there is no zone/domain found
   * for the given id then a zone/domain is created and then information is added to the
   * new zone.
   *
   * @param jsonDomain the string containing zone information in JSON format
   * @return the Zone import process response
   * @throws DNSMadeEasyException thrown incase of error
   */
  public DNSZoneImportResponse importZone(String jsonDomain) throws DNSMadeEasyException {
    DNSZoneImportResponse result = new DNSZoneImportResponse();
    DNSZoneImportRequest jsonImport;
    try {
      jsonImport = mapper.readValue(jsonDomain, DNSZoneImportRequest.class);
    } catch (IOException e) {
      log.error("Error occurred while parsing the JSON String : " + e.getMessage(), e);
      throw new DNSMadeEasyException(HttpStatus.SC_NOT_FOUND, "Unable to parse the JSON String",
          e);
    }

    return getDnsZoneImportResponse(result, jsonImport);
  }

  private DNSZoneImportResponse getDnsZoneImportResponse(DNSZoneImportResponse result,
                                                         DNSZoneImportRequest jsonImport)
      throws DNSMadeEasyException {
    if (jsonImport != null) {
      // check if zone is existing in the system
      if (jsonImport.getId() == null) {
        // create the domain
        ManagedDNSResponse managedDNSResponse = createDomain(jsonImport.getName());
        handleDomainResponse(jsonImport, managedDNSResponse);
      } else {
        try {
          DNSDomainResponse dnsDomainResponse = getDomain(jsonImport.getId());
          if (dnsDomainResponse == null) {
            // create the domain
            ManagedDNSResponse managedDNSResponse = createDomain(jsonImport.getName());
            handleDomainResponse(jsonImport, managedDNSResponse);
          }
        } catch (DNSMadeEasyException e) {
          log.error("Domain not found for Id : " + jsonImport.getId() + ". " + e.getMessage(), e);
          if (e.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
            log.info("Creating domain with name: " + jsonImport.getName());
            ManagedDNSResponse managedDNSResponse = createDomain(jsonImport.getName());
            handleDomainResponse(jsonImport, managedDNSResponse);
          }
        }
      }
      result = processZoneImport(jsonImport);
    }
    return result;
  }

  private void handleDomainResponse(DNSZoneImportRequest jsonImport,
                                    ManagedDNSResponse managedDNSResponse)
      throws DNSMadeEasyException {
    if (managedDNSResponse.getError() == null) {
      log.info("Domain created successfully");
      DNSDomainResponse domainDetails = null;
      ManagedDNSResponse domains = getDomains();
      log.info("Domains fetched successfully");
      for (DNSDomainResponse dnsDomainResponse : domains.getData()) {
        if (dnsDomainResponse.getName().equalsIgnoreCase(jsonImport.getName())) {
          domainDetails = dnsDomainResponse;
        }
      }
      if (domainDetails != null) {
        jsonImport.setId(domainDetails.getId());
        jsonImport.setName(domainDetails.getName());
      } else {
        throw new DNSMadeEasyException(HttpStatus.SC_BAD_REQUEST, "Unable to create domain "
            + "with name : " + jsonImport.getName(), null);
      }
    } else {
      DNSDomainResponse domainDetails = null;
      ManagedDNSResponse domains = getDomains();
      log.info("Domains fetched successfully");
      for (DNSDomainResponse dnsDomainResponse : domains.getData()) {
        if (dnsDomainResponse.getName().equalsIgnoreCase(jsonImport.getName())) {
          domainDetails = dnsDomainResponse;
        }
      }
      if (domainDetails != null) {
        jsonImport.setId(domainDetails.getId());
        jsonImport.setName(domainDetails.getName());
      } else {
        throw new DNSMadeEasyException(HttpStatus.SC_BAD_REQUEST, "Unable to create or fetch"
            + " domain with name : " + jsonImport.getName(), null);
      }
    }
  }

  private DNSZoneImportResponse processZoneImport(DNSZoneImportRequest jsonImport) {
    DNSZoneImportResponse retDNSZoneImportResponse;
    List<DNSDomainRecordRequest> recordRequests = new ArrayList<>();
    for (DNSDomainRecordResponse recordResponse : jsonImport.getRecords()) {
      DNSDomainRecordRequest recordRequest
          = dNSRecordRequestResponseAssembler.assemble(recordResponse);
      recordRequest.setId(null);
      recordRequests.add(recordRequest);
    }
    retDNSZoneImportResponse = importRecords(recordRequests, jsonImport.getId());
    retDNSZoneImportResponse.setId(jsonImport.getId());
    retDNSZoneImportResponse.setName(jsonImport.getName());
    // handle the template id and vanity id
    updateDomainConfig(jsonImport.getId(), jsonImport.getName(),
        String.valueOf(jsonImport.getVanityId()), String.valueOf(jsonImport.getTemplateId()),
        retDNSZoneImportResponse);

    return retDNSZoneImportResponse;
  }

  private void updateDomainConfig(Long domainId, String domainName, String vanityId,
                                  String templateId, DNSZoneImportResponse
                                      retDNSZoneImportResponse) {
    log.info("Updating Domain configuration for domain with id : " + domainId + " and name : "
        + domainName);
    try {
      ManagedDNSResponse updateDomainConfresponse = updateDomainConfiguration(domainId, vanityId,
          templateId);
      retDNSZoneImportResponse.setDomainConfUpdateResponse(updateDomainConfresponse);
    } catch (DNSMadeEasyException e) {
      log.error("Unable to update the domain configuration information for domain with id : "
          + domainId + " and name : " + domainName + ". " + e.getMessage(), e);
      ManagedDNSResponse updateDomainConfresponse = new ManagedDNSResponse();
      String[] error = { "Unable to update the domain configuration information. Please have a "
          + "look at logs for more details. " + e.getMessage() };
      updateDomainConfresponse.setError(error);
      retDNSZoneImportResponse.setDomainConfUpdateResponse(updateDomainConfresponse);
    }
  }

  private DNSZoneImportResponse importRecords(List<DNSDomainRecordRequest> recordRequests,
                                              long domainId) {
    DNSZoneImportResponse result = new DNSZoneImportResponse();
    List<DNSDomainRecordResponse> recordResponses = new ArrayList<>();
    if (recordRequests != null && !recordRequests.isEmpty()) {
      List<DNSDomainRecordRequest> sortedRecordRequest = recordRequests.stream()
          .sorted((r1, r2) -> r1.getType().compareTo(r2.getType())).collect(Collectors.toList());
      for (DNSDomainRecordRequest recordRequest : sortedRecordRequest) {
        DNSDomainRecordResponse dnsDomainRecordResponse;
        // check if the record already exists
        ManagedDNSRecordsResponse recordsResponse;
        try {
          recordsResponse = getDNSRecordByTypeAndRecordName(domainId, recordRequest.getType(),
              recordRequest.getName());
        } catch (DNSMadeEasyException e) {
          log.error("Error occurred while trying to fetch if record already existed in DNS. "
              + e.getMessage(), e);
          dnsDomainRecordResponse = new DNSDomainRecordResponse();
          String[] error = { "Unable to fetch the record with name : " + recordRequest.getName()
              + " and " + "type : " + recordRequest.getType()};
          dnsDomainRecordResponse.setError(error);
          continue;
        }
        if (recordsResponse != null && recordsResponse.getData().length == 0) {
          log.info("Creating record with name : " + recordRequest.getName() + " and type : "
              + recordRequest.getType());
          // create the record
          if (recordRequest.getType().equalsIgnoreCase(ANAME.toString())) {
            recordRequest.setName("");
          }
          createDNSRecord(domainId, recordResponses, recordRequest);
        } else {
          log.info("Updating record with name : " + recordRequest.getName() + " and type : "
              + recordRequest.getType());
          // update the record
          recordRequest.setId(recordsResponse.getData()[0].getId());
          updateDNSRecord(domainId, recordResponses, recordRequest);
        }
      }
    }
    result.setRecords(recordResponses);

    return result;
  }

  private DNSZoneImportResponse importRecords(List<DNSDomainRecordRequest> recordRequests)
      throws DNSMadeEasyException {
    DNSZoneImportResponse result = new DNSZoneImportResponse();
    List<DNSDomainRecordResponse> recordResponses = new ArrayList<>();
    DNSDomainResponse domain = null;
    // iterate through the list
    for (DNSDomainRecordRequest dnsDomainRecordRequest : recordRequests) {
      // get the domain details from the record name
      try {
        domain = getDomainDetails(dnsDomainRecordRequest.getName(), true);
      } catch (DNSMadeEasyException e) {
        throw e;
      }
      String recordName = getRecordName(dnsDomainRecordRequest.getName());
      if (!dnsDomainRecordRequest.getType().equalsIgnoreCase(ANAME.toString())) {
        dnsDomainRecordRequest.setName(recordName);
      } else {
        dnsDomainRecordRequest.setName("");
      }
      if (domain != null) {
        DNSDomainRecordResponse dnsDomainRecordResponse;
        try {
          dnsDomainRecordResponse = createDNSRecord(domain.getId(), dnsDomainRecordRequest);
        } catch (DNSMadeEasyException e) {
          dnsDomainRecordResponse = new DNSDomainRecordResponse();
          String[] error = { "Unable to create record with name : "
              + dnsDomainRecordRequest.getName() };
          dnsDomainRecordResponse.setError(error);
        }
        recordResponses.add(dnsDomainRecordResponse);
      } else {
        DNSDomainRecordResponse dnsDomainRecordResponse = new DNSDomainRecordResponse();
        String[] error = { "Unable to find domain  for the record with name : "
            + dnsDomainRecordRequest.getName() + " to be imported" };
        dnsDomainRecordResponse.setError(error);
        recordResponses.add(dnsDomainRecordResponse);
      }
    }
    result.setRecords(recordResponses);
    if (domain != null) {
      result.setName(domain.getName());
      result.setId(domain.getId());
    }

    return result;
  }

  private String getRecordName(String name) {
    if (name != null && !name.isEmpty()) {
      String[] recordNameParts = name.split(NAME_SEPARATOR);
      return recordNameParts[0];
    }

    return name;
  }

  private DNSDomainResponse getDomainDetails(String name, Boolean useSplit)
      throws DNSMadeEasyException {
    DNSDomainResponse result = null;
    String domainName = name;
    if (name != null && !name.isEmpty()) {
      if (useSplit) {
        String[] recordNameParts = name.split(NAME_SEPARATOR, 2);
        domainName = recordNameParts[1];
        if (domainName.endsWith(".")) {
          domainName = domainName.substring(0, domainName.length() - 1);
        }
      }
      // get the domain information
      ManagedDNSResponse domains;
      try {
        domains = getDomains();
      } catch (DNSMadeEasyException e) {
        log.error("Error occurred while getting domains. " + e.getMessage(), e);
        throw getError(e, e.getMessage(), HttpStatus.SC_BAD_REQUEST);
      }
      for (DNSDomainResponse dnsDomainResponse : domains.getData()) {
        if (dnsDomainResponse.getName().equalsIgnoreCase(domainName)) {
          log.info("Domain : " + domainName + " found successfully");
          result = dnsDomainResponse;
        }
      }
    }

    return result;
  }

  private void populateCAAType(DNSDomainRecordRequest dnsDomainRecordRequest, String[] row) {
    if (row.length >= 7) {
      dnsDomainRecordRequest.setValue(row[6]); // value for the record
    }
    if (row.length >= 8) {
      dnsDomainRecordRequest.setGtdLocation(row[7]); // the GTD Location
    }
  }

  private void populateMXType(DNSDomainRecordRequest dnsDomainRecordRequest, String[] row) {
    if (row.length >= 5 && row[4] != null && !Objects.equals(row[4], "")) {
      dnsDomainRecordRequest.setMxLevel(Integer.parseInt(row[4]));
    }
    if (row.length >= 6) {
      dnsDomainRecordRequest.setValue(row[5]); // value for the record
    }
    if (row.length >= 7) {
      dnsDomainRecordRequest.setGtdLocation(row[6]); // the GTD Location
    }
  }

  private void populateANameType(DNSDomainRecordRequest dnsDomainRecordRequest, String[] row) {
    if (row.length >= 5) {
      dnsDomainRecordRequest.setValue(row[4]); // value for the record
    }
    if (row.length >= 6) {
      dnsDomainRecordRequest.setGtdLocation(row[5]); // the GTD Location
    }
    if (row.length >= 7) {
      dnsDomainRecordRequest.setName("");
    }
  }

  private void populateSRVType(DNSDomainRecordRequest dnsDomainRecordRequest, String[] row) {
    if (row.length >= 5 && row[4] != null && !Objects.equals(row[4], "")) {
      dnsDomainRecordRequest.setPriority(Integer.parseInt(row[4]));
    }
    if (row.length >= 6 && row[5] != null && !Objects.equals(row[5], "")) {
      dnsDomainRecordRequest.setWeight(Integer.parseInt(row[5]));
    }
    if (row.length >= 7 && row[6] != null && !Objects.equals(row[6], "")) {
      dnsDomainRecordRequest.setPort(Integer.parseInt(row[6]));
    }
    if (row.length >= 8) {
      dnsDomainRecordRequest.setValue(row[7]); // value for the record
    }
    if (row.length >= 9) {
      dnsDomainRecordRequest.setGtdLocation(row[8]); // the GTD Location
    }
  }

  private void populateDefaultType(DNSDomainRecordRequest dnsDomainRecordRequest, String[] row) {
    if (row.length >= 5) {
      dnsDomainRecordRequest.setValue(row[4]); // value for the record
    }
    if (row.length >= 6) {
      dnsDomainRecordRequest.setGtdLocation(row[5]); // the GTD Location
    }
  }

  private String getRecordIds(String[] recordIds) {
    StringBuilder recordIdsStr = new StringBuilder();
    if (recordIds != null) {
      for (int i = 0; i < recordIds.length; i++) {
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

  private ManagedDNSRecordsResponse getManagedDNSRecordsResponse(long domainId,
                                                                 HttpResponse response,
                                                                 ManagedDNSRecordsResponse result)
      throws DNSMadeEasyException {
    if (response != null) {
      try {
        result = mapper.readValue(response.getEntity().getContent(),
            ManagedDNSRecordsResponse.class);
      } catch (IOException e) {
        log.error("Error occurred while getting DNS domain records : " + domainId + ". "
            + e.getMessage(), e);
        throw getError(e, e.getMessage(), response.getStatusLine().getStatusCode());
      }
    }
    return result;
  }

  private DNSDomainResponse[] getDomainResponse(String domainIds) {
    String retDomainIds = domainIds.replace("[", "");
    retDomainIds = retDomainIds.replace("]", "");
    String[] domains = retDomainIds.split(",");
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
      if (i < domainIds.length - 1) {
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
    return new DNSMadeEasyException(status, message, x);
  }

}
