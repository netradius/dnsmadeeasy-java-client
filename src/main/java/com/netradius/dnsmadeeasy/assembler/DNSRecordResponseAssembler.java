package com.netradius.dnsmadeeasy.assembler;

import com.netradius.dnsmadeeasy.data.DNSDomainRecordRequest;
import com.netradius.dnsmadeeasy.data.DNSDomainRecordResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;


/**
 * Adds DNSDomainRecordRequest properties to DNSDomainRecordResponse.
 *
 * @author Abhijeet C Kale
 */
@Slf4j
public class DNSRecordResponseAssembler extends Assembler<DNSDomainRecordRequest,
    DNSDomainRecordResponse> {
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
  public void merge(@NonNull DNSDomainRecordRequest from, @NonNull DNSDomainRecordResponse to) {
    to.setName(from.getName());
    to.setValue(from.getValue());
    to.setGtdLocation(from.getGtdLocation());
    to.setId(from.getId());
    to.setType(from.getType());
    to.setTtl(String.valueOf(from.getTtl()));
    to.setSource(from.getSource());
    to.setMxLevel(from.getMxLevel());
    to.setWeight(from.getWeight());
    to.setPriority(from.getPriority());
    to.setPort(from.getPort());
  }
}
