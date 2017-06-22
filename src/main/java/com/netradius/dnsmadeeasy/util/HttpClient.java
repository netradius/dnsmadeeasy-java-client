package com.netradius.dnsmadeeasy.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * Rest Client which will make calls to the DNS Rest API exposed
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class HttpClient {

	public HttpResponse post(String url, String json, String apiKey, String secretKeyHash, String requestDate) {

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost post = getHttpPost(url, json, apiKey, secretKeyHash, requestDate);
		HttpResponse response = null;

		try {
			response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED || response.getStatusLine()
					.getStatusCode() != HttpStatus.SC_OK ) {
				log.error("Error occurred while trying to post to Rest API. ", response.getStatusLine().getReasonPhrase());
			}
		} catch (IOException e) {
			log.error("Unable to connect to  " + e.getMessage(), e);
		}

		return response;
	}

	private HttpPost getHttpPost(String url, String json, String apiKey, String secretKeyHash, String requestDate) {
		HttpPost post = new HttpPost(url);
		post.setHeader("content-type", "application/json");
		post.setHeader("accept", "application/json");
		post.setHeader("x-dnsme-apiKey", apiKey);
		post.setHeader("x-dnsme-hmac", secretKeyHash);
		post.setHeader("x-dnsme-requestDate", requestDate);
		StringEntity entity = new StringEntity(json, "UTF-8" );
		entity.setContentType("application/json");
		post.setEntity(entity);
		return post;
	}

	public HttpResponse get(String url,  String apiKey, String secretKeyHash, String requestDate) {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = getHttpGet(url, apiKey, secretKeyHash, requestDate);

		return getHttpResponse(httpClient, get);
	}

	private HttpResponse getHttpResponse(CloseableHttpClient httpClient, HttpGet get) {
		HttpResponse response = null;
		try {
			response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				log.error("Error occurred while trying to post to Rest API. ", response.getStatusLine().getReasonPhrase());
			}
		}
			catch(IOException e){
				log.error("Unable to connect to  " + e.getMessage(), e);
			}
			return response;
	}

	public HttpResponse delete(String url, String domainId, String apiKey, String secretKeyHash, String requestDate) {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpDeleteWithBody deleteWithBody = getHttpDelete(url, apiKey, secretKeyHash, requestDate);
		StringEntity entity = new StringEntity("'{[" + domainId + "\"]}'", "UTF-8");
		entity.setContentType("application/json");
		deleteWithBody.setEntity(entity);
		HttpResponse response = null;
		try {
			response = httpClient.execute(deleteWithBody);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				log.error("Error occurred while trying to post to Rest API. ", response.getStatusLine().getReasonPhrase());
			}
		}
		catch(IOException e) {
				log.error("Unable to connect to  " + e.getMessage(), e);
			}
		return response;
	}

	public HttpResponse put(String url, String json, String apiKey, String secretKeyHash, String requestDate) {

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPut put = getHttpPut(url, json, apiKey, secretKeyHash, requestDate);
		HttpResponse response = null;

		try {
			response = httpClient.execute(put);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED || response.getStatusLine()
					.getStatusCode() != HttpStatus.SC_OK ) {
				log.error("Error occurred while trying to put to Rest API. ", response.getStatusLine().getReasonPhrase());
			}
		} catch (IOException e) {
			log.error("Unable to connect to  " + e.getMessage(), e);
		}

		return response;
	}

	private HttpPut getHttpPut(String url, String json, String apiKey, String secretKeyHash, String requestDate) {
		HttpPut put = new HttpPut(url);
		put.setHeader("content-type", "application/json");
		put.setHeader("accept", "application/json");
		put.setHeader("x-dnsme-apiKey", apiKey);
		put.setHeader("x-dnsme-hmac", secretKeyHash);
		put.setHeader("x-dnsme-requestDate", requestDate);
		StringEntity entity = new StringEntity(json, "UTF-8" );
		entity.setContentType("application/json");
		put.setEntity(entity);
		return put;
	}

	private HttpDeleteWithBody getHttpDelete(String url, String apiKey, String secretKeyHash, String requestDate) {
		HttpDeleteWithBody deleteWithBody = new HttpDeleteWithBody(url);
		deleteWithBody.setHeader("content-type", "application/json");
		deleteWithBody.setHeader("accept", "application/json");
		deleteWithBody.setHeader("x-dnsme-apiKey", apiKey);
		deleteWithBody.setHeader("x-dnsme-hmac", secretKeyHash);
		deleteWithBody.setHeader("x-dnsme-requestDate", requestDate);
		return deleteWithBody;
	}


	private HttpGet getHttpGet(String url, String apiKey, String secretKeyHash, String requestDate) {
		HttpGet get = new HttpGet(url);
		get.setHeader("content-type", "application/json");
		get.setHeader("accept", "application/json");
		get.setHeader("x-dnsme-apiKey", apiKey);
		get.setHeader("x-dnsme-hmac", secretKeyHash);
		get.setHeader("x-dnsme-requestDate", requestDate);
		return get;
	}

}