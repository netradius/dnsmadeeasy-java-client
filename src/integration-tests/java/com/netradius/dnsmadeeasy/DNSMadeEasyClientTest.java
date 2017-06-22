package com.netradius.dnsmadeeasy;

import com.netradius.dnsmadeeasy.http.impl.DNSMadeEasyRestClient;
import com.netradius.dnsmadeeasy.util.DateUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Integration test class for the client
 *
 * @author Abhijeet C Kale
 */
public class DNSMadeEasyClientTest {

	private static DNSMadeEasyRestClient client = new DNSMadeEasyRestClient();
	TestConfig config = new TestConfig();

	@BeforeClass
	public static void init() {

	}

	@Test
	public void testCreateDomain() throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		String hexSecret = HmacUtils.hmacSha1Hex(config.getApiSecret(), requestDate);
		client.createDomain("mytest3.com", config.getUrl(), config.getApiKey(), hexSecret, requestDate);
	}

	@Test
	public void testGetDomains() throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		String hexSecret = HmacUtils.hmacSha1Hex(config.getApiSecret(), requestDate);
		client.getDomains(config.getUrl(), config.getApiKey(), hexSecret, requestDate);
	}

	@Test
	public void testDeleteDomain() throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		String hexSecret = HmacUtils.hmacSha1Hex(config.getApiSecret(), requestDate);
		client.deleteDomain("877900", config.getUrl(), config.getApiKey(), hexSecret, requestDate);
	}

	@Test
	public void testGetDomain() throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		String hexSecret = HmacUtils.hmacSha1Hex(config.getApiSecret(), requestDate);
		client.getDomain("877900", config.getUrl(), config.getApiKey(), hexSecret, requestDate);
	}

	@Test
	public void testUpdateDomainConfig() throws IOException {
		String requestDate = DateUtils.dateToStringInGMT();
		String hexSecret = HmacUtils.hmacSha1Hex(config.getApiSecret(), requestDate);
		client.updateDomainConfiguration("877900", "9999", "", config.getUrl(), config.getApiKey(), hexSecret,
				requestDate);
	}
}