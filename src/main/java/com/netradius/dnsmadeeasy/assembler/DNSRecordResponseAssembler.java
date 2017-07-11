package com.netradius.dnsmadeeasy.assembler;


import com.netradius.dnsmadeeasy.data.DNSDomainRecordRequest;
import com.netradius.dnsmadeeasy.data.DNSDomainRecordResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Adds DNSDomainRecordRequest properties to DNSDomainRecordResponse
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class DNSRecordResponseAssembler extends Assembler<DNSDomainRecordRequest,DNSDomainRecordResponse >{
	@Override
	public Class<DNSDomainRecordResponse> getType() {
		return DNSDomainRecordResponse.class;
	}

	/**
	 * Merged a source object into another.
	 *
	 * @param from the source
	 * @param to   the destination
	 */
	@Override
	public void merge(@Nonnull DNSDomainRecordRequest from, @Nonnull DNSDomainRecordResponse to) {
		try {
			BeanUtils.copyProperties(to, from);
		} catch (IllegalAccessException e) {
			log.error("Error occurred while assembling DNSDomainRecordResponse from DNSDomainRecordRequest : ", e);
		} catch (InvocationTargetException e) {
			log.error("Error occurred while assembling DNSDomainRecordResponse from DNSDomainRecordRequest : ", e);
		}
	}
}
