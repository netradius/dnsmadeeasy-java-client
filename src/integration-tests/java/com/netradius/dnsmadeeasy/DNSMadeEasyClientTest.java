package com.netradius.dnsmadeeasy;

import com.netradius.dnsmadeeasy.data.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Integration test class for the client
 *
 * @author Abhijeet C Kale
 */
public class DNSMadeEasyClientTest {

	private TestConfig config = new TestConfig();
	private DNSMadeEasyClient client = new DNSMadeEasyClient(config.getUrl(), config.getApiKey(), config.getApiSecret());

	@BeforeClass
	public static void init() {

	}

	@Test
	public void testCreateDomain() throws DNSMadeEasyException {
		ManagedDNSResponse response = client.createDomain("mytest20.com");
		assertTrue(response != null);
	}

	@Test
	public void testGetDomains() throws DNSMadeEasyException {
		ManagedDNSResponse response = client.getDomains();
		assertTrue(response != null);
	}

	@Test
	public void testDeleteDomain() throws DNSMadeEasyException {
		ManagedDNSResponse response =  client.deleteDomain("877900");
		assertTrue(response != null);
	}

	@Test
	public void testGetDomain() throws DNSMadeEasyException {
		DNSDomainResponse response = client.getDomain("877900");
		assertTrue(response != null);
	}

	@Test
	public void testUpdateDomainConfig() throws DNSMadeEasyException {
		ManagedDNSResponse response =  client.updateDomainConfiguration("877900", "9999", "222");
		assertTrue(response != null);
	}

	@Test
	public void testUpdateMultipleDomainConfig() throws DNSMadeEasyException {
		String [] ids = {"877900", "99999"};
		ManagedDNSResponse response =  client.updateMultipleDomainConfiguration(ids, "9999", "222");
		assertTrue(response != null);
	}

	@Test
	public void testCreateDomains() throws DNSMadeEasyException {
		String [] names = { "mytest17.com", "mytest18.com"};
		ManagedDNSResponse response = client.createDomains(names);
		assertTrue(response != null);
	}

	@Test
	public void testDeleteDomains() throws DNSMadeEasyException {
		String [] ids = { "877900", "877994", "877998"};
		ManagedDNSResponse response =  client.deleteDomains(ids);
		assertTrue(response != null);
	}

	@Test
	public void testGetDNSDomainRecords() throws DNSMadeEasyException {
		ManagedDNSRecordsResponse response =  client.getDNSRecord("877900");
		assertTrue(response != null);
	}

	@Test
	public void testGetDNSDomainRecordsByType() throws DNSMadeEasyException {
		ManagedDNSRecordsResponse response =  client.getDNSRecordByType("877900", "A");
		assertTrue(response != null);
	}

	@Test
	public void testGetDNSDomainRecordsByTypeAndRecordName() throws DNSMadeEasyException {
		ManagedDNSRecordsResponse response =  client.getDNSRecordByTypeAndRecordName("877900", "A", "site1");
		assertTrue(response != null);
	}

	@Test
	public void testDeleteRecord() throws DNSMadeEasyException {
		boolean response =  client.deleteManagedDNSRecord("877900","10154755");
	//	assertTrue(response);
	}

	@Test
	public void testCreateDNSRecord() throws DNSMadeEasyException {
		DNSDomainRecordResponse response =  client.createDNSRecord("877900","site1", "A",
				"0.0.0.0", "DEFAULT", 86400);
		assertTrue(response != null);
	}

	@Test
	public void testUpdateDNSRecord() throws DNSMadeEasyException {
		boolean response =  client.updateDNSRecord("877900","site1", "A",
				"0.0.0.9", "DEFAULT", 1800,  10154864);
		//assertTrue(response);
	}

	@Test
	public void testCreateDNSMultiRecords() throws DNSMadeEasyException {
		DNSDomainRecordRequest dnsDomainRecordRequest1 = new DNSDomainRecordRequest();
		dnsDomainRecordRequest1.setName("site2");
		dnsDomainRecordRequest1.setType("A");
		dnsDomainRecordRequest1.setValue("0.0.0.2");
		dnsDomainRecordRequest1.setGtdLocation("DEFAULT");
		dnsDomainRecordRequest1.setTtl(1800);

		DNSDomainRecordRequest dnsDomainRecordRequest2 = new DNSDomainRecordRequest();
		dnsDomainRecordRequest2.setName("site3");
		dnsDomainRecordRequest2.setType("A");
		dnsDomainRecordRequest2.setValue("0.0.0.3");
		dnsDomainRecordRequest2.setGtdLocation("DEFAULT");
		dnsDomainRecordRequest2.setTtl(1800);

		List<DNSDomainRecordRequest> recordRequestList = new ArrayList<>();
		recordRequestList.add(dnsDomainRecordRequest1);
		recordRequestList.add(dnsDomainRecordRequest2);

		DNSDomainRecordResponse[] response =  client.createDNSMultiRecord("877900",recordRequestList);
		assertTrue(response != null);
	}

	@Test
	public void updateDNSMultiRecords() throws DNSMadeEasyException {
		DNSDomainRecordRequest dnsDomainRecordRequest1 = new DNSDomainRecordRequest();
		dnsDomainRecordRequest1.setName("site2");
		dnsDomainRecordRequest1.setType("A");
		dnsDomainRecordRequest1.setValue("1.0.0.2");
		dnsDomainRecordRequest1.setGtdLocation("DEFAULT");
		dnsDomainRecordRequest1.setTtl(1800);
		dnsDomainRecordRequest1.setId(10154872);

		DNSDomainRecordRequest dnsDomainRecordRequest2 = new DNSDomainRecordRequest();
		dnsDomainRecordRequest2.setName("site3");
		dnsDomainRecordRequest2.setType("A");
		dnsDomainRecordRequest2.setValue("1.0.0.3");
		dnsDomainRecordRequest2.setGtdLocation("DEFAULT");
		dnsDomainRecordRequest2.setTtl(1800);
		dnsDomainRecordRequest2.setId(10154871);

		List<DNSDomainRecordRequest> recordRequestList = new ArrayList<>();
		recordRequestList.add(dnsDomainRecordRequest1);
		recordRequestList.add(dnsDomainRecordRequest2);

		boolean response =  client.updateDNSMultiRecord("877900",recordRequestList);
	//	assertTrue(response );
	}


	@Test
	public void testDeleteRecords() throws DNSMadeEasyException {
		String[] recordIds = {"" , "1", "2"};
		boolean response =  client.deleteMultiDNSRecords("877900", recordIds);
			assertTrue(response);
	}
}