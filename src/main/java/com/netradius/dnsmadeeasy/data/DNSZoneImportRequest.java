package com.netradius.dnsmadeeasy.data;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Holds the DNSMadeEasy Rest Api responses
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class DNSZoneImportRequest extends DNSZoneExportResponse implements Serializable {

	private static final long serialVersionUID = -8473248702802113754L;
}
