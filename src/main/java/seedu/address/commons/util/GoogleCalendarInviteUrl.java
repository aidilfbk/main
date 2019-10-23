package seedu.address.commons.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneOffset.UTC;

import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * A class to build a valid Google Calendar invite url.
 */
public class GoogleCalendarInviteUrl {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final String GOOGLE_CALENDAR_INVITE_URL = "https://www.google.com/calendar/render?action=TEMPLATE";
    private static final String PLUS_CHARACTER = Pattern.quote("+");
    private static final String REPLACEMENT_PLUS_CHARACTER = "%20";

    private final Collection<String> invitedEmailAddresses = new HashSet<>();
    private String title = null;
    private String location = null;
    private String description = null;
    private ZonedDateTime startDateTime = null;
    private ZonedDateTime endDateTime = null;

    private static String dateToString(final ZonedDateTime dateTime) {
        Objects.requireNonNull(dateTime);
        return dateTime.withZoneSameInstant(UTC).format(DATE_TIME_FORMATTER);
    }

    /**
     * Encodes the {@code text} to be URL-safe.
     *
     * @param text The text to be URL-safe encoded.
     * @return The URL-safe representation of the {@code text}.
     */
    private static String urlSafeEncoding(final String text) {
        return URLEncoder.encode(text, UTF_8).replaceAll(PLUS_CHARACTER, REPLACEMENT_PLUS_CHARACTER);
    }

    /**
     * Formats a key-value pair to be URL-safe and appendable to a Query String.
     *
     * @param key   The query parameter's key.
     * @param value The query parameter's value.
     * @return The URL-safe, Query String-appendable representation of the {@code key} and {@code value} inputs.
     */
    private static String formatQueryParameter(final String key, final String value) {
        CollectionUtil.requireAllNonNull(key, value);
        if (key.isBlank()) {
            throw new IllegalArgumentException("key cannot be blank");
        }

        final String encodedKey = urlSafeEncoding(key);
        final String encodedValue = urlSafeEncoding(value);
        return String.format("%s=%s", encodedKey, encodedValue);
    }

    public void setTitle(final String title) {
        Objects.requireNonNull(title);
        if (title.isBlank()) {
            throw new IllegalArgumentException("title cannot be blank");
        }
        this.title = title;
    }

    public void setStartDateTime(final ZonedDateTime startDateTime) {
        this.startDateTime = Objects.requireNonNull(startDateTime);
    }

    public void setEndDateTime(final ZonedDateTime endDateTime) {
        this.endDateTime = Objects.requireNonNull(endDateTime);
    }

    public void setLocation(final String location) {
        Objects.requireNonNull(location);
        if (location.isBlank()) {
            throw new IllegalArgumentException("location cannot be blank");
        }
        this.location = location;
    }

    public void setDescription(final String description) {
        Objects.requireNonNull(description);
        if (description.isBlank()) {
            throw new IllegalArgumentException("description cannot be blank");
        }
        this.description = description;
    }

    /**
     * Adds a list of email addresses to be invited to the event.
     *
     * @param emailAddresses A list of valid email addresses.
     */
    public void addGuestInviteEmailAddress(final Collection<String> emailAddresses) {
        CollectionUtil.requireAllNotEmpty(emailAddresses);
        if (emailAddresses.stream().anyMatch(StringUtil::isNullOrEmpty)) {
            throw new IllegalArgumentException("emailAddress cannot be blank");
        }
        invitedEmailAddresses.addAll(emailAddresses);
    }

    /**
     * Adds an email address to be invited to the event.
     *
     * @param emailAddress A valid email address.
     */
    public void addGuestInviteEmailAddress(final String emailAddress) {
        Objects.requireNonNull(emailAddress);
        addGuestInviteEmailAddress(List.of(emailAddress));
    }

    /**
     * Generates the Google Calendar invite url based on data provided earlier.
     *
     * @return The Google Calendar invite url representing this event.
     */
    public String generateUrl() {
        CollectionUtil.requireAllNonNull(title, startDateTime, endDateTime);

        final StringJoiner queryParams = new StringJoiner("&");
        queryParams.add(GOOGLE_CALENDAR_INVITE_URL);

        queryParams.add(formatQueryParameter(ParameterKeys.EVENT_TITLE, title));

        final String mergedDateTime = String.format("%s/%s", dateToString(startDateTime), dateToString(endDateTime));
        queryParams.add(formatQueryParameter(ParameterKeys.EVENT_DATES, mergedDateTime));

        if (location != null) {
            queryParams.add(formatQueryParameter(ParameterKeys.EVENT_LOCATION, location));
        }

        if (description != null) {
            queryParams.add(formatQueryParameter(ParameterKeys.EVENT_DESCRIPTION, description));
        }

        for (final String emailAddress : invitedEmailAddresses) {
            queryParams.add(formatQueryParameter(ParameterKeys.EVENT_INVITE_EMAIL_ADDRESS, emailAddress));
        }

        return queryParams.toString();
    }

    /**
     * Represents the valid parameter keys to build a Google Calendar invite url.
     * Based on this helpful answer on StackOverflow: https://stackoverflow.com/a/23495015
     */
    static final class ParameterKeys {
        static final String EVENT_TITLE = "text";
        static final String EVENT_DATES = "dates";
        static final String EVENT_LOCATION = "location";
        static final String EVENT_INVITE_EMAIL_ADDRESS = "add";
        static final String EVENT_DESCRIPTION = "details";
    }

}
