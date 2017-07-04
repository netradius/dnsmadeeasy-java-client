package com.netradius.dnsmadeeasy.assembler;


import com.netradius.dnsmadeeasy.data.DNSDomainRecordRequest;
import com.netradius.dnsmadeeasy.data.DNSDomainRecordResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Adds DNSDomainRecordResponse properties to DNSDomainRecordRequest
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class DNSRecordRequestResponseAssembler extends Assembler<DNSDomainRecordResponse, DNSDomainRecordRequest>{
	@Override
	public Class<DNSDomainRecordRequest> getType() {
		return DNSDomainRecordRequest.class;
	}

	/**
	 * Merged a source object into another.
	 *
	 * @param from the source
	 * @param to   the destination
	 */
	@Override
	public void merge(@Nonnull DNSDomainRecordResponse from, @Nonnull DNSDomainRecordRequest to) {
		try {
			BeanUtils.copyProperties(to, from);
		} catch (IllegalAccessException e) {
			log.error("Error occurred while assembling DNSDomainRecordRequest from DNSDomainResponse : ", e);
		} catch (InvocationTargetException e) {
			log.error("Error occurred while assembling DNSDomainRecordRequest from DNSDomainResponse : ", e);
		}
	}
}
