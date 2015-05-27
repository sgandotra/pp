/*******************************************************************************
 * Copyright (c) 2006-2015, PayPal Pvt Ltd, All rights reserved
 * Project    : loadnperformanceApi
 * Package    : com.paypal.lnp.util
 * Class Name : PrettyDate.java
 * Sub Project: loadnperformanceApi
 * Created on : Apr 9, 2015
 * Created by : gthattiyottu
 ******************************************************************************/
package com.ptaas.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;

// TODO: Auto-generated Javadoc

/**
 * The Class PrettyDate.
 */
public class PrettyDateJoda {

	/** The iso format. */
	public static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	/** The legacy format. */
	public static String LEGACY_FORMAT = "EEE MMM dd hh:mm:ss zzz yyyy";

	/** The Constant utc. */
	private static final DateTimeZone utc = DateTimeZone.forID("UTC");

	/** The Constant legacyFormatter. */
	private static DateTimeFormatter legacyFormatter = new DateTimeFormatterFactory(
			LEGACY_FORMAT).createDateTimeFormatter();

	/** The Constant isoFormatter. */
	private static DateTimeFormatter isoFormatter = new DateTimeFormatterFactory(
			ISO_FORMAT).createDateTimeFormatter();
	static {
		legacyFormatter = legacyFormatter.withZoneUTC();
		isoFormatter = isoFormatter.withZoneUTC();
	}

	public static String now() {
		return PrettyDateJoda.toString(new Date());
	}

	public static String toString(final Date date) {
		DateTime dateTime = new DateTime(date).withZone(DateTimeZone.UTC);
		return isoFormatter.print(dateTime.getMillis());
	}

	public static String toLegacyString(final Date date) {
		DateTime dateTime = new DateTime(date).withZone(DateTimeZone.UTC);
		return legacyFormatter.print(dateTime.getMillis());
	}

	public static String toString(final Date date, final String format) {
		return toString(date, format, "UTC");
	}

	public static String toString(final Date date, final String format,
			final String timezone) {
		final DateTimeZone tz = DateTimeZone.forID(timezone);
		DateTime dateTime = new DateTime(date).withZone(tz);
		final DateTimeFormatter formatter = new DateTimeFormatterFactory(format)
				.createDateTimeFormatter().withZone(tz);
		return formatter.print(dateTime.getMillis());
	}

	public static void main(String args[]) {
		System.out.println("now in ISO Format is : "
				+ toString(new Date(TimeUnit.MILLISECONDS.convert(1431580000,
						TimeUnit.SECONDS))));
		System.out.println("now in legacy Format is : "
				+ toLegacyString(new Date(TimeUnit.MILLISECONDS.convert(
						1431580000, TimeUnit.SECONDS))));
		System.out.println("now in yyyy-MM-dd HH:mm:ss  IST: "
				+ toString(
						new Date(TimeUnit.MILLISECONDS.convert(1431580000,
								TimeUnit.SECONDS)), "yyyy-MM-dd HH:mm:ss",
						"Asia/Kolkata"));
		System.out.println("now in Date  IST: "
				+ toDate("2015-05-14 10:36:40", "yyyy-MM-dd HH:mm:ss",
						"Asia/Kolkata"));
		System.out.println("To anotehr timesone is "
				+ toString(
						toDate("2015-05-14 10:36:40", "yyyy-MM-dd HH:mm:ss",
								"Asia/Kolkata"), ISO_FORMAT,
						"America/Los_Angeles"));
		System.out.println("To userTimezoen date "
				+ toUserTimeZoneString(1431580000, "America/Los_Angeles"));
	}

	public static Date toDate(final String dateString, final String format,
			final String timeZone) {
		final DateTimeZone tz = DateTimeZone.forID(timeZone);
		final DateTimeFormatter formatter = new DateTimeFormatterFactory(format)
				.createDateTimeFormatter().withZone(tz);
		DateTime dateTime = formatter.parseDateTime(dateString);
		dateTime.getMillis();
		return new Date(dateTime.getMillis());
	}

	public static Date toUserTimeZoneDate(final long epochGMTTimeInSeconds,
			String timeZone) {
		final DateTimeZone tz = DateTimeZone.forID(timeZone);
		DateTime dateTime = new DateTime(new Date(
				TimeUnit.MILLISECONDS.convert(epochGMTTimeInSeconds,
						TimeUnit.SECONDS))).withZone(tz);
		return new Date(dateTime.getMillis());
	}

	public static String toUserTimeZoneString(final long epochGMTTimeInSeconds,
			String timeZone) {
		Date result = toUserTimeZoneDate(epochGMTTimeInSeconds, timeZone);
		return toString(result, ISO_FORMAT, timeZone);
	}
}
