package com.netradius.dnsmadeeasy.assembler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.netradius.dnsmadeeasy.data.DNSDomainResponse;
import com.netradius.dnsmadeeasy.data.DNSZoneExportResponse;

/**
 * Adds Domain Response properties to DNSZoneExport
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class DNSZoneExportResponseAssembler extends Assembler<DNSDomainResponse,
		DNSZoneExportResponse>{
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
	public void merge(@NonNull DNSDomainResponse from, @NonNull DNSZoneExportResponse to) {
		to.setActiveThirdParties(from.getActiveThirdParties());
		to.setAxfrServer(from.getAxfrServer());
		to.setCreated(from.getCreated());
		to.setFolderId(from.getFolderId());
		to.setGtdEnabled(from.getGtdEnabled());
		to.setId(from.getId());
		to.setName(from.getName());
		to.setNameServers(from.getNameServers());
		to.setProcessMulti(from.getProcessMulti());
		to.setPendingActionId(from.getPendingActionId());
		to.setSoaID(from.getSoaID());
		to.setTransferAclId(from.getTransferAclId());
		to.setUpdated(from.getUpdated());
		to.setVanityNameServers(from.getVanityNameServers());
		to.setVanityId(from.getVanityId());
	}
}
