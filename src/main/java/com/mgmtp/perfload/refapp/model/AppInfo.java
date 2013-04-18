package com.mgmtp.perfload.refapp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author rnaegele
 */
@XmlRootElement
public class AppInfo {
	private static final String APP_NAME = "perfLoad Reference Application";

	private String string;
	private String revision;

	@SuppressWarnings("unused")
	private AppInfo() {
		// no-arg constructor required by JAXB
		throw new UnsupportedOperationException("illegal call to private constructor");
	}

	public AppInfo(final String version, final String revision) {
		this.string = version;
		this.revision = revision;
	}

	@XmlAttribute
	public String getName() {
		return APP_NAME;
	}

	/**
	 * @return the string
	 */
	@XmlAttribute
	public String getString() {
		return string;
	}

	/**
	 * @return the revision
	 */
	@XmlAttribute
	public String getRevision() {
		return revision;
	}
}
