/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Date to String converter
 * 
 * @author rpalomares
 */
public class DateToStringConverter extends Converter<Date, String> {
    /**
     * Converts forward the value (from Date to String)
     * 
     * @param value a valid Date object
     * @return the String representation
     */
    @Override
    public String convertForward(Date value) {
        return value.toString();
    }

    /**
     * Converts reverse the value (from String to Date)
     * 
     * @param value an String supposedly containing a Date value
     * @return a Date object with the date value of value, or the current date and
     *         time if the parsing fails
     */
    @Override
    public Date convertReverse(String value) {
        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        try {
            d = sdf.parse(value);
        } catch (ParseException e) {
            Logger.getLogger(DateToStringConverter.class.getName()).log(Level.SEVERE, null, e);
            d = new Date();
        }
        return d;
    }
}
