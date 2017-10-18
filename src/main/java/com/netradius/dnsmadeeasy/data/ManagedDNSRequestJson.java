package com.netradius.dnsmadeeasy.data;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Json Document for Domain Request.
 *
 * @author Abhijeet C Kale
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagedDNSRequestJson {

  String name;
  String vanityId;
  String templateId;
  String[] ids;
  String[] names;

}
