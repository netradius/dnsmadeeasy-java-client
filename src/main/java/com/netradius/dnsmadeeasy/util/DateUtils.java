package com.netradius.dnsmadeeasy.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A util class which provide date related functions.
 *
 * @author Abhijeet C Kale
 */
public class DateUtils {

  /**
   * Function to return the date in GMT time and in string format.
   */
  public static  String dateToStringInGMT() {
    final Date currentTime = new Date();
    final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
    // Give it to me in GMT time.
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

    return sdf.format(currentTime);
  }
}
