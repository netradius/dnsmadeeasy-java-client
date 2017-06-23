package com.netradius.dnsmadeeasy;

import lombok.Data;

/**
 * Thrown when an error occurs talking to DNSMadeEasy.
 *
 * @author Abhijeet C Kale
 */
@Data
public class DNSMadeEasyException extends Exception {

	private int httpStatus;

	public DNSMadeEasyException(int httpStatus, String msg, Throwable t) {
		super(msg, t);
		this.httpStatus = httpStatus;
	}
}
