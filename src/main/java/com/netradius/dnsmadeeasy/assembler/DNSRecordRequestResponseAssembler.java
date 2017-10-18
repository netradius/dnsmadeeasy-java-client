package com.netradius.dnsmadeeasy.assembler;

import com.netradius.dnsmadeeasy.data.DNSDomainRecordRequest;
import com.netradius.dnsmadeeasy.data.DNSDomainRecordResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Adds DNSDomainRecordResponse properties to DNSDomainRecordRequest.
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class DNSRecordRequestResponseAssembler extends Assembler<DNSDomainRecordResponse,
    DNSDomainRecordRequest> {
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
  public void merge(@NonNull DNSDomainRecordResponse from, @NonNull DNSDomainRecordRequest to) {
    to.setName(from.getName());
    to.setGtdLocation(from.getGtdLocation());
    to.setPort(from.getPort());
    to.setId(from.getId());
    to.setPriority(from.getPriority());
    to.setValue(from.getValue());
    to.setWeight(from.getWeight());
    to.setMxLevel(from.getMxLevel());
    to.setSource(from.getSource());
    to.setTtl(Long.parseLong(from.getTtl()));
    to.setType(from.getType());
  }
}
