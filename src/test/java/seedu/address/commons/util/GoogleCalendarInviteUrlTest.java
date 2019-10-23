package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.junit.jupiter.api.Test;

class GoogleCalendarInviteUrlTest {
    private static final String BASE_URL = "https://www.google.com/calendar/render?action=TEMPLATE";
    private static final String EMPTY_STRING = "";

    private static final ZoneId SINGAPORE_TIMEZONE = ZoneId.of("Asia/Singapore");

    private static final ZonedDateTime VALID_START_TIME = ZonedDateTime.of(2019, 12, 31, 0, 0, 0, 0, SINGAPORE_TIMEZONE);
    private static final String ENCODED_VALID_START_TIME = "20191230T160000Z";
    private static final ZonedDateTime VALID_END_TIME = ZonedDateTime.of(2019, 12, 31, 23, 59, 59, 0, SINGAPORE_TIMEZONE);
    private static final String ENCODED_VALID_END_TIME = "20191231T155959Z";
    private static final String ENCODED_VALID_DATES = ENCODED_VALID_START_TIME + "%2F" + ENCODED_VALID_END_TIME;
    private static final String VALID_DATES_QUERYPARAM = "dates=" + ENCODED_VALID_DATES;

    private static final String VALID_TITLE = "a valid title";
    private static final String ENCODED_VALID_TITLE = "a%20valid%20title";
    private static final String VALID_TITLE_QUERYPARAM = "text=" + ENCODED_VALID_TITLE;

    private static final String VALID_LOCATION = "a valid location";
    private static final String ENCODED_VALID_LOCATION = "a%20valid%20location";
    private static final String VALID_LOCATION_QUERYPARAM = "location=" + ENCODED_VALID_LOCATION;

    private static final String VALID_DESCRIPTION = "a valid description";
    private static final String ENCODED_VALID_DESCRIPTION = "a%20valid%20description";
    private static final String VALID_DESCRIPTION_QUERYPARAM = "details=" + ENCODED_VALID_DESCRIPTION;

    private static final String VALID_EMAIL_ADDRESS_ONE = "disposable.style.email.with+symbol@example.com";
    private static final String ENCODED_VALID_EMAIL_ADDRESS_ONE = "disposable.style.email.with%2Bsymbol%40example.com";
    private static final String VALID_INVITED_EMAIL_ONE_QUERYPARAM = "add=" + ENCODED_VALID_EMAIL_ADDRESS_ONE;

    private static final String VALID_EMAIL_ADDRESS_TWO = "person@example.com";
    private static final String ENCODED_VALID_EMAIL_ADDRESS_TWO = "person%40example.com";
    private static final String VALID_INVITED_EMAIL_TWO_QUERYPARAM = "add=" + ENCODED_VALID_EMAIL_ADDRESS_TWO;

    /**
     * Builds a Google Calendar Invite URL by concatenating the base url with all the {@code parts}, delimited with an
     * ampersand. Meant to be used with the {@code *_QUERYPARAM} variables (e.g.
     * {@link GoogleCalendarInviteUrlTest#VALID_DATES_QUERYPARAM}).
     *
     * @param parts Strings to be concatenated. Should be the {@code *_QUERYPARAM} variables of
     *              {@link GoogleCalendarInviteUrlTest} (e.g.
     *              {@link GoogleCalendarInviteUrlTest#VALID_DATES_QUERYPARAM}).
     * @return A Google Calendar Invite URL
     */
    private static String buildUrl(final String... parts) {
        final StringJoiner url = new StringJoiner("&");
        url.add(BASE_URL);
        for (final String part : parts) {
            url.add(part);
        }
        return url.toString();
    }

    /**
     * Creates a valid GoogleCalendarInviteUrl instance, pre-populated with valid values for {@code title},
     * {@code startDateTime} and {@code endDateTime}.
     *
     * @return A valid GoogleCalendarInviteUrl instance
     */
    private static GoogleCalendarInviteUrl minimalValidInstance() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = new GoogleCalendarInviteUrl();
        googleCalendarInviteUrl.setTitle(VALID_TITLE);
        googleCalendarInviteUrl.setStartDateTime(VALID_START_TIME);
        googleCalendarInviteUrl.setEndDateTime(VALID_END_TIME);

        return googleCalendarInviteUrl;
    }

    @Test
    void generateUrl_minimalFields_success() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();

        final String expectedUrl = buildUrl(VALID_TITLE_QUERYPARAM, VALID_DATES_QUERYPARAM);
        final String actualUrl = googleCalendarInviteUrl.generateUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void generateUrl_withLocation_success() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        googleCalendarInviteUrl.setLocation(VALID_LOCATION);

        final String expectedUrl = buildUrl(VALID_TITLE_QUERYPARAM, VALID_DATES_QUERYPARAM, VALID_LOCATION_QUERYPARAM);
        final String actualUrl = googleCalendarInviteUrl.generateUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void generateUrl_withDescription_success() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        googleCalendarInviteUrl.setDescription(VALID_DESCRIPTION);

        final String expectedUrl = buildUrl(VALID_TITLE_QUERYPARAM, VALID_DATES_QUERYPARAM, VALID_DESCRIPTION_QUERYPARAM);
        final String actualUrl = googleCalendarInviteUrl.generateUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void generateUrl_withOneEmailInvite_success() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        googleCalendarInviteUrl.addGuestInviteEmailAddress(VALID_EMAIL_ADDRESS_ONE);

        final String expectedUrl = buildUrl(VALID_TITLE_QUERYPARAM, VALID_DATES_QUERYPARAM, VALID_INVITED_EMAIL_ONE_QUERYPARAM);
        final String actualUrl = googleCalendarInviteUrl.generateUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void generateUrl_withManyEmailInvite_success() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        googleCalendarInviteUrl.addGuestInviteEmailAddress(VALID_EMAIL_ADDRESS_ONE);
        googleCalendarInviteUrl.addGuestInviteEmailAddress(VALID_EMAIL_ADDRESS_TWO);

        final String expectedUrl = buildUrl(
                VALID_TITLE_QUERYPARAM,
                VALID_DATES_QUERYPARAM,
                VALID_INVITED_EMAIL_ONE_QUERYPARAM,
                VALID_INVITED_EMAIL_TWO_QUERYPARAM
        );
        final String actualUrl = googleCalendarInviteUrl.generateUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void generateUrl_withListEmailInvite_success() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        googleCalendarInviteUrl.addGuestInviteEmailAddress(List.of(
                VALID_EMAIL_ADDRESS_ONE,
                VALID_EMAIL_ADDRESS_TWO
        ));

        final String expectedUrl = buildUrl(
                VALID_TITLE_QUERYPARAM,
                VALID_DATES_QUERYPARAM,
                VALID_INVITED_EMAIL_ONE_QUERYPARAM,
                VALID_INVITED_EMAIL_TWO_QUERYPARAM
        );
        final String actualUrl = googleCalendarInviteUrl.generateUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void setTitle_null_throwsNullPointerException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(NullPointerException.class, () -> {
            googleCalendarInviteUrl.setTitle(null);
        });
    }

    @Test
    void setTitle_blankTitle_throwsIllegalArgumentException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(IllegalArgumentException.class, () -> {
            googleCalendarInviteUrl.setTitle(EMPTY_STRING);
        });
    }

    @Test
    void setLocation_null_throwsNullPointerException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(NullPointerException.class, () -> {
            googleCalendarInviteUrl.setLocation(null);
        });
    }

    @Test
    void setLocation_blankLocation_throwsIllegalArgumentException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(IllegalArgumentException.class, () -> {
            googleCalendarInviteUrl.setLocation(EMPTY_STRING);
        });
    }

    @Test
    void setDescription_null_throwsNullPointerException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(NullPointerException.class, () -> {
            googleCalendarInviteUrl.setDescription(null);
        });
    }

    @Test
    void setDescription_blankDescription_throwsIllegalArgumentException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(IllegalArgumentException.class, () -> {
            googleCalendarInviteUrl.setDescription(EMPTY_STRING);
        });
    }

    @Test
    void addGuestInviteEmailAddress_nullStringObject_throwsNullPointerException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(NullPointerException.class, () -> {
            googleCalendarInviteUrl.addGuestInviteEmailAddress((String) null);
        });
    }

    @Test
    void addGuestInviteEmailAddress_nullListObject_throwsNullPointerException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(NullPointerException.class, () -> {
            googleCalendarInviteUrl.addGuestInviteEmailAddress((List<String>) null);
        });
    }

    @Test
    void addGuestInviteEmailAddress_blankEmailAddress_throwsIllegalArgumentException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        assertThrows(IllegalArgumentException.class, () -> {
            googleCalendarInviteUrl.addGuestInviteEmailAddress(EMPTY_STRING);
        });
    }

    @Test
    void addGuestInviteEmailAddress_listOfBlankEmailAddress_throwsIllegalArgumentException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        final List<String> emailList = List.of(EMPTY_STRING);
        assertThrows(IllegalArgumentException.class, () -> {
            googleCalendarInviteUrl.addGuestInviteEmailAddress(emailList);
        });
    }

    @Test
    void addGuestInviteEmailAddress_listOfNull_throwsIllegalArgumentException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        final List<String> emailList = new ArrayList<>();
        emailList.add(null);

        assertThrows(IllegalArgumentException.class, () -> {
            googleCalendarInviteUrl.addGuestInviteEmailAddress(emailList);
        });
    }

    @Test
    void addGuestInviteEmailAddress_listContainingBlank_throwsIllegalArgumentException() {
        final GoogleCalendarInviteUrl googleCalendarInviteUrl = minimalValidInstance();
        final List<String> emailList = List.of(VALID_EMAIL_ADDRESS_ONE, EMPTY_STRING);
        assertThrows(IllegalArgumentException.class, () -> {
            googleCalendarInviteUrl.addGuestInviteEmailAddress(emailList);
        });
    }
}