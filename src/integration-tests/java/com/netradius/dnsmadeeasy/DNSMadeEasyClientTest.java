package com.netradius.dnsmadeeasy;

import com.netradius.dnsmadeeasy.data.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;


/**
 * Integration test class for the client
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class DNSMadeEasyClientTest {

	public static final String DOMAIN_NAME_PREFIX = "testdomain";
	public static final String DOMAIN_NAME_SUFFIX = ".com";
	public static final String TEMPLATE_ID = "222";
	public static final String VANITY_ID = "9999";
	public static final String SITE_1 = "site1";
	public static final String SITE_1_VALUE = "0.0.0.0";
	public static final String SITE_1_VALUE_UPDATE = "0.0.0.9";
	public static final String TYPE_A = "A";
	public static final String DEFAULT_GTD_LOCATION = "DEFAULT";
	public static final long TTL = 1800;
	public static final String SITE_2 = "site2";
	public static final String SITE_2_VALUE = "1.0.0.0";
	public static final String SITE_2_VALUE_UPDATE = "0.0.0.9";
	public static final String SITE_3 = "site3";
	public static final String SITE_3_VALUE = "1.0.0.0";
	public static final String SITE_3_VALUE_UPDATE = "0.0.0.9";

	private TestConfig config = new TestConfig();
	private DNSMadeEasyClient client = new DNSMadeEasyClient(config.getUrl(), config.getApiKey(), config.getApiSecret());

	@BeforeClass
	public static void init() {
	}

	@Test
	public void testSingleDomain() {
		Random rand = new Random();

		try {
			log.info("Testing Create Domain");
			StringBuilder domainName = new StringBuilder();
			domainName.append(DOMAIN_NAME_PREFIX);
			domainName.append(Integer.toString(rand.nextInt(100000) + 1));
			domainName.append(DOMAIN_NAME_SUFFIX);
			ManagedDNSResponse domainCreateResponse = client.createDomain(domainName.toString());
			log.info("Domain : " + domainName + " Created successfully");
			assert(domainCreateResponse.getError() == null);

			DNSDomainResponse domainDetails = null;
			// get the domain information
			ManagedDNSResponse domains = client.getDomains();
			log.info("Domains fetched successfully");
			for (DNSDomainResponse dnsDomainResponse : domains.getData()) {
				if (dnsDomainResponse.getName().equalsIgnoreCase(domainName.toString())) {
					domainDetails = dnsDomainResponse;
				}
			}

			log.info("Getting Domain information for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName());
			DNSDomainResponse getDomainResponse = client.getDomain(domainDetails.getId());
			// check for the domain name received
			assertTrue(getDomainResponse.getId() == domainDetails.getId());

			log.info("Updating Domain configuration for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName());
			ManagedDNSResponse updateDomainConfresponse =  client.updateDomainConfiguration(domainDetails.getId(), VANITY_ID, TEMPLATE_ID);
			assertTrue(updateDomainConfresponse != null);

			String [] ids = {String.valueOf(domainDetails.getId()), String.valueOf(domains.getData()[0].getId())};
			log.info("Updating Multiple Domain configuration for domains with  : " + domainDetails.getId() + " and "
					+ domains.getData()[0].getId());
			ManagedDNSResponse updateMultipleDomainConfResponse =  client.updateMultipleDomainConfiguration(ids,
					VANITY_ID, TEMPLATE_ID);
			assertTrue(updateMultipleDomainConfResponse != null);

			log.info("Creating a DNS record for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName());
			DNSDomainRecordResponse dnsRecordSite1 =  client.createDNSRecord(domainDetails.getId(),SITE_1, TYPE_A,
					SITE_1_VALUE, DEFAULT_GTD_LOCATION, TTL);

			log.info("Updating a DNS record for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName());
			boolean updateDNSRecord =  client.updateDNSRecord(domainDetails.getId(),SITE_1, TYPE_A,
					SITE_1_VALUE_UPDATE, DEFAULT_GTD_LOCATION, TTL, dnsRecordSite1.getId());
			assertTrue(updateDNSRecord);

			log.info("Fetching a records for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName());
			ManagedDNSRecordsResponse dnsRecord1 =  client.getDNSRecord(domainDetails.getId());
			assertTrue(dnsRecord1.getData() !=  null);
			assertTrue(dnsRecord1.getData()[0].getName().equalsIgnoreCase(SITE_1));

			log.info("Fetching a record for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName() + " with type : " + TYPE_A);
			ManagedDNSRecordsResponse dnsRecordByTypeResponse =  client.getDNSRecordByType(domainDetails.getId(), TYPE_A);
			assertTrue(dnsRecordByTypeResponse.getData()[0].getName().equalsIgnoreCase(SITE_1));

			log.info("Fetching a record for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName() + " with type : " + TYPE_A + " and record name " + SITE_1);
			ManagedDNSRecordsResponse  dnsRecordByTypeAndRecordNameResponse =  client.getDNSRecordByTypeAndRecordName
					(domainDetails.getId(), TYPE_A, SITE_1);
			assertTrue(dnsRecordByTypeAndRecordNameResponse.getData()[0].getName().equalsIgnoreCase(SITE_1));
			assertTrue(dnsRecordByTypeAndRecordNameResponse.getData()[0].getType().equalsIgnoreCase(TYPE_A));

			log.info("Deleting a record for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName() + " and record name : " + dnsRecordSite1.getName());
			boolean deleteRecord =  client.deleteManagedDNSRecord(domainDetails.getId(),dnsRecordSite1.getId());
			assertTrue(deleteRecord);

			DNSDomainRecordRequest dnsDomainRecordRequest1 = new DNSDomainRecordRequest();
			dnsDomainRecordRequest1.setName(SITE_2);
			dnsDomainRecordRequest1.setType(TYPE_A);
			dnsDomainRecordRequest1.setValue(SITE_2_VALUE);
			dnsDomainRecordRequest1.setGtdLocation(DEFAULT_GTD_LOCATION);
			dnsDomainRecordRequest1.setTtl(TTL);

			DNSDomainRecordRequest dnsDomainRecordRequest2 = new DNSDomainRecordRequest();
			dnsDomainRecordRequest2.setName(SITE_3);
			dnsDomainRecordRequest2.setType(TYPE_A);
			dnsDomainRecordRequest2.setValue(SITE_3_VALUE);
			dnsDomainRecordRequest2.setGtdLocation(DEFAULT_GTD_LOCATION);
			dnsDomainRecordRequest2.setTtl(TTL);

			List<DNSDomainRecordRequest> recordRequestList = new ArrayList<>();
			recordRequestList.add(dnsDomainRecordRequest1);
			recordRequestList.add(dnsDomainRecordRequest2);
			log.info("Creating multiple records for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName());
			DNSDomainRecordResponse[] clientDNSMultiRecordResponse = client.createDNSMultiRecord(domainDetails.getId(), recordRequestList);
			assertTrue(clientDNSMultiRecordResponse != null && clientDNSMultiRecordResponse.length == 2);

			List<DNSDomainRecordRequest> recordUpdateRequestList = new ArrayList<>();
			dnsDomainRecordRequest1.setValue(SITE_2_VALUE_UPDATE);
			dnsDomainRecordRequest1.setId(clientDNSMultiRecordResponse[1].getId());
			dnsDomainRecordRequest2.setValue(SITE_3_VALUE_UPDATE);
			dnsDomainRecordRequest2.setId(clientDNSMultiRecordResponse[0].getId());
			recordRequestList.add(dnsDomainRecordRequest1);
			recordRequestList.add(dnsDomainRecordRequest2);
			log.info("Updating multiple records for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName());
			boolean updateDNSMultiRecordResponse =  client.updateDNSMultiRecord(domainDetails.getId(),recordUpdateRequestList);
			assertTrue(updateDNSMultiRecordResponse);

			String[] recordIds = {String.valueOf(clientDNSMultiRecordResponse[1].getId()), String.valueOf(clientDNSMultiRecordResponse[0].getId())};
			log.info("Deleting multiple records for domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName() + " Record names : " + clientDNSMultiRecordResponse[1].getName() + " and " +
					clientDNSMultiRecordResponse[0].getName());
			boolean deleteMultiDNSRecordsResponse =  client.deleteMultiDNSRecords(domainDetails.getId(), recordIds);
			assertTrue(deleteMultiDNSRecordsResponse);

			log.info("Deleting domain with id : " + domainDetails.getId() + " and name : " +
					domainDetails.getName() + " Record names : " + clientDNSMultiRecordResponse[1].getName() + " and " +
					clientDNSMultiRecordResponse[0].getName());
			ManagedDNSResponse deleteDomainResponse =  client.deleteDomain(String.valueOf(domainDetails.getId()));
			assertTrue(deleteDomainResponse != null);

		} catch (DNSMadeEasyException e) {
			log.error(e.toString(), e);
			assertTrue(false);
		}
	}

	//@Test commented out since it will create more domains which will sometime in future
	// run out the supported domains number under the account early
	public void testMultiDomain() {
		Random rand = new Random();
		StringBuilder domainName1 = new StringBuilder();
		domainName1.append(DOMAIN_NAME_PREFIX);
		domainName1.append(Integer.toString(rand.nextInt(100000) + 1));
		domainName1.append(DOMAIN_NAME_SUFFIX);

		StringBuilder domainName2 = new StringBuilder();
		domainName2.append(DOMAIN_NAME_PREFIX);
		domainName2.append(Integer.toString(rand.nextInt(100000) + 1));
		domainName2.append(DOMAIN_NAME_SUFFIX);
		try {
			String[] names = { domainName1.toString(), domainName2.toString()};
			ManagedDNSResponse response = client.createDomains(names);
			assertTrue(response != null);

			ManagedDNSResponse domains = client.getDomains();

			String [] ids = { String.valueOf(domains.getData()[0].getId()), String.valueOf(domains.getData()[1].getId())};
			response =  client.deleteDomains(ids);
			assertTrue(response != null);
		} catch (DNSMadeEasyException e) {
			log.error(e.toString(), e);
			assertTrue(false);
		}
	}
}