package com.netradius.dnsmadeeasy;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Test related config details
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class TestConfig {

	public static final String[] CONFIG_LOCATIONS = {
			"C:\\dnsmadeeasy-client.properties",
			"/etc/dnsmadeeasy-client.properties",
			System.getProperty("user.home") + "/dnsmadeeasy-client.properties",
			System.getProperty("user.dir") + "/dnsmadeeasy-client.properties",
			"src/main/resources/dnsmadeeasyclient.properties"};

	private Properties properties;

	private Properties loadConfig() {
		for (String location : CONFIG_LOCATIONS) {
			File file = new File(location);
			if (file.exists() && file.canRead()) {
				try (InputStream in = new FileInputStream(file)) {
					Properties props = new Properties();
					props.load(in);
					return props;
				} catch (IOException x) {
					log.error("Error loading config file [" + location + "]: " + x.getMessage(), x);
				}
			}
		}
		throw new IllegalStateException("Failed to load configuration for DNSMadeEasy tests");
	}

	public String getUrl() {
		if (properties == null) {
			properties = loadConfig();
		}
		return properties.getProperty("rest.api.url");
	}

	public String getApiKey() {
		if (properties == null) {
			properties = loadConfig();
		}
		return properties.getProperty("rest.api.key");
	}

	public String getApiSecret() {
		if (properties == null) {
			properties = loadConfig();
		}
		return properties.getProperty("rest.api.secret");
	}
}