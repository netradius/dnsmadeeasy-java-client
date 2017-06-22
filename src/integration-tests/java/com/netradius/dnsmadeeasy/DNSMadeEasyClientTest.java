package com.netradius.dnsmadeeasy;

import com.netradius.dnsmadeeasy.data.DNSDomainResponse;
import com.netradius.dnsmadeeasy.data.ManagedDNSResponse;
import com.netradius.dnsmadeeasy.http.impl.DNSMadeEasyRestClient;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

/**
 * Integration test class for the client
 *
 * @author Abhijeet C Kale
 */
public class DNSMadeEasyClientTest {

	private TestConfig config = new TestConfig();
	private DNSMadeEasyRestClient client = new DNSMadeEasyRestClient(config.getUrl(), config.getApiKey(), config.getApiSecret());

	@BeforeClass
	public static void init() {

	}

	@Test
	public void testCreateDomain() throws IOException {
		ManagedDNSResponse response = client.createDomain("mytest5.com");
		assertTrue(response != null);
	}

	@Test
	public void testGetDomains() throws IOException {
		ManagedDNSResponse response = client.getDomains();
		assertTrue(response != null);
	}

	@Test
	public void testDeleteDomain() throws IOException {
		ManagedDNSResponse response =  client.deleteDomain("877900");
		assertTrue(response != null);
	}

	@Test
	public void testGetDomain() throws IOException {
		DNSDomainResponse response = client.getDomain("877900");
		assertTrue(response != null);
	}

	@Test
	public void testUpdateDomainConfig() throws IOException {
		ManagedDNSResponse response =  client.updateDomainConfiguration("877900", "9999", "222");
		assertTrue(response != null);
	}

	@Test
	public void testUpdateMultipleDomainConfig() throws IOException {
		String [] ids = {"877900", "99999"};
		ManagedDNSResponse response =  client.updateMultipleDomainConfiguration(ids, "9999", "222");
		assertTrue(response != null);
	}

	@Test
	public void testCreateDomains() throws IOException {
		String [] names = { "mytest17.com", "mytest18.com"};
		ManagedDNSResponse response = client.createDomains(names);
		assertTrue(response != null);
	}

	@Test
	public void testDeleteDomains() throws IOException {
		String [] ids = { "877900", "877994", "877998"};
		ManagedDNSResponse response =  client.deleteDomains(ids);
		assertTrue(response != null);
	}

}