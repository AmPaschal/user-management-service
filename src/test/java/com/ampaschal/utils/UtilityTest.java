package com.ampaschal.utils;

import io.quarkus.test.junit.QuarkusTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@QuarkusTest
class UtilityTest {

    @Inject
    Utility utility;

    //@Test
    void validateEmail_passesWhenValid() {

        String email = "basetest@ampaschal.com";

        boolean valid = utility.validateEmail(email, true);

        assertTrue(valid);
    }

    //@Test
    void validateEmail_failsWhenInvalid() {

        String email = "basetestampaschal.com";

        boolean valid = utility.validateEmail(email, true);

        assertFalse(valid);
    }

    //@Test
    void validatePhoneNumber_passesWhenValid() {
        String phoneNumber = "+2347063867854";
        boolean valid = utility.validatePhoneNumber(phoneNumber, true);
        assertTrue(valid);

    }

    //@Test
    void validatePhoneNumber_failsWhenInvalid() {
        String phoneNumber = "2347063867854";
        boolean valid = utility.validatePhoneNumber(phoneNumber, true);
        assertFalse(valid);

    }

    //@Test
    void validateName_passesWhenValid() {
        String name = "ampaschal";
        boolean valid = utility.validateName(name, true);
        assertTrue(valid);
    }

    //@Test
    void validateName_failsWhenInvalid() {
        String name = "ampaschal@_";
        boolean valid = utility.validateName(name, true);
        assertFalse(valid);
    }

    //@Test
    void getDateFromStringTest() {
        String dateString = "2001-03-10T13:23:32.005Z";
        Date date = utility.getDateFromString(dateString);

        assertNotNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(2001, calendar.get(Calendar.YEAR));
        assertEquals(2, calendar.get(Calendar.MONTH));
        assertEquals(10, calendar.get(Calendar.DAY_OF_MONTH));
    }

    //@Test
    public void getFormattedStringFromDateTest() {
        Date date = new Date();

        String dateString = utility.getFormattedStringFromDate(date);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String initialDatePart = inputFormat.format(date);


        assertNotNull(dateString);
        assertEquals(initialDatePart, dateString.substring(0, 10));
    }

    //@Test
    public void getInternationalNumberFormatTest() {
        String testNumber1 = "+2348063867955";
        String testNumber2 = "+234806 386 7955";
        String testNumber3 = "+08063867955";
        String testNumber4 = "08063867955";
        String testNumber5 = "0123456789";
        String testNumber6 = "";
        String testNumber7 = null;
        String testNumber8 = "0806 386 7955";

        boolean validNumber1 = utility.validatePhoneNumber(testNumber1, true);
        boolean validNumber2 = utility.validatePhoneNumber(testNumber2, true);
        boolean validNumber3 = utility.validatePhoneNumber(testNumber3, true);
        boolean validNumber4 = utility.validatePhoneNumber(testNumber4, true);
        boolean validNumber5 = utility.validatePhoneNumber(testNumber5, true);
        boolean validNumber6 = utility.validatePhoneNumber(testNumber6, true);
        boolean validNumber7 = utility.validatePhoneNumber(testNumber7, true);
        boolean validNumber8 = utility.validatePhoneNumber(testNumber8, true);

        assertTrue(validNumber1);
        assertTrue(validNumber2);
        assertFalse(validNumber3);
        assertTrue(validNumber4);
        assertFalse(validNumber5);
        assertFalse(validNumber6);
        assertFalse(validNumber7);
        assertTrue(validNumber8);

        String resultNumber1 = utility.getInternationalNumberFormat(testNumber1);
        String resultNumber2 = utility.getInternationalNumberFormat(testNumber2);
        String resultNumber3 = utility.getInternationalNumberFormat(testNumber3);
        String resultNumber4 = utility.getInternationalNumberFormat(testNumber4);
        String resultNumber5 = utility.getInternationalNumberFormat(testNumber5);
        String resultNumber6 = utility.getInternationalNumberFormat(testNumber6);
        String resultNumber7 = utility.getInternationalNumberFormat(testNumber7);
        String resultNumber8 = utility.getInternationalNumberFormat(testNumber8);

        assertEquals(testNumber1, resultNumber1);
        assertEquals(testNumber1, resultNumber2);
        assertNull(resultNumber3);
        assertEquals(testNumber1, resultNumber4);
        assertNull(resultNumber5);
        assertNull(resultNumber6);
        assertNull(resultNumber7);
        assertEquals(testNumber1, resultNumber8);

    }

}