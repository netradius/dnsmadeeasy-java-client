package com.netradius.dnsmadeeasy.data;

/**
 * Record types in DNS Made Easy
 *
 * @author Abhijeet C Kale
 */
public enum RecordType {
	AAAA("AAAA"),
	ANAME("ANAME"),
	CAA("CAA"),
	CNAME("CNAME"),
	MX("MX"),
	NS("NS"),
	PTR("PTR"),
	A("A"),
	SPF("SPF"),
	SRV("SRV"),
	TXT("TXT"),
	HTTP("HTTP");

	private final String value;

	private RecordType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Returns the enum that corresponds to the indicated value.
	 *
	 * @param value of the enum constant
	 *
	 * @return the enum constant, or <code>null</code> if not found
	 */
	public static RecordType getEnumByValue(String value) {
		for (RecordType type : values()) {
			if (type.getValue().equalsIgnoreCase(value)) {
				return type;
			}
		}
		return null;
	}
}