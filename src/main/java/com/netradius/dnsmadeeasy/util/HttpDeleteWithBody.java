package com.netradius.dnsmadeeasy.util;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;


/**
 *
 *
 * @author Abhijeet C Kale
 */
public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
	public static final String METHOD_NAME = "DELETE";

	public String getMethod() {
		return METHOD_NAME;
	}

	public HttpDeleteWithBody(final String uri) {
		setURI(URI.create(uri));
	}

	public HttpDeleteWithBody(final URI uri) {
		setURI(uri);
	}

	public HttpDeleteWithBody() {
	}
}
