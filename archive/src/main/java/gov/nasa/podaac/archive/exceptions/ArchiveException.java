//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.archive.exceptions;

import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveStatus;

/**
 * @author clwong
 * 
 * @version $Id: ArchiveException.java 2223 2008-11-02 01:03:49Z clwong $
 */
@SuppressWarnings("serial")
public class ArchiveException extends Exception {

	public enum ErrorType {ARCHIVE, SERVICE_PROFILE, COPY_FILE};
	private static ErrorType errorStatus;
	private static GranuleArchiveStatus granuleStatus = GranuleArchiveStatus.ONLINE;

	public ArchiveException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ArchiveException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ArchiveException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ArchiveException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param status
	 * @param message
	 */
	public ArchiveException(final GranuleArchiveStatus status,
			final String message) {
		super(message);
		ArchiveException.granuleStatus = status;
	}

	/**
	 * @param errorStatus
	 * @param status
	 * @param message
	 */
	public ArchiveException(final ErrorType errorStatus,
			final String message) {
		super(message);
		ArchiveException.errorStatus = errorStatus;
	}
	
	public static GranuleArchiveStatus getGranuleStatus() {
		return granuleStatus;
	}

	public static void setGranuleStatus(GranuleArchiveStatus granuleStatus) {
		ArchiveException.granuleStatus = granuleStatus;
	}

	public static ErrorType getErrorStatus() {
		return errorStatus;
	}

	public static void setErrorStatus(ErrorType errorStatus) {
		ArchiveException.errorStatus = errorStatus;
	}
}
