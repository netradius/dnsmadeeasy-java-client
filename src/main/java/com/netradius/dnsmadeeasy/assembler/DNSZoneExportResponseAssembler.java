package com.netradius.dnsmadeeasy.assembler;


import com.netradius.dnsmadeeasy.data.DNSDomainResponse;
import com.netradius.dnsmadeeasy.data.DNSZoneExportResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Adds Domain Response properties to DNSZoneExport
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class DNSZoneExportResponseAssembler extends Assembler<DNSDomainResponse, DNSZoneExportResponse>{
	@Override
	public Class<DNSZoneExportResponse> getType() {
		return DNSZoneExportResponse.class;
	}

	/**
	 * Merged a source object into another.
	 *
	 * @param from the source
	 * @param to   the destination
	 */
	@Override
	public void merge(@Nonnull DNSDomainResponse from, @Nonnull DNSZoneExportResponse to) {
		try {
			BeanUtils.copyProperties(to, from);
		} catch (IllegalAccessException e) {
			log.error("Error occurred while assembling DNSZoneExportResponse from DNSDomainResponse : ", e);
		} catch (InvocationTargetException e) {
			log.error("Error occurred while assembling DNSZoneExportResponse from DNSDomainResponse : ", e);
		}
	}
}
