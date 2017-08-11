package com.netradius.dnsmadeeasy.data;

import lombok.Data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Holds the DNSMadeEasy Rest Api responses
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class DNSZoneImportRequest extends DNSZoneExportResponse {

}
