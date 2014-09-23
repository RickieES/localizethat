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
import org.jdesktop.beansbinding.Converter;

/**
 * A Date to String converter
 * @author rpalomares
 */
public class DateToStringConverter extends Converter {
    /**
     * Converts forward the value (from Date to String)
     * @param value a valid Date object
     * @return the String representation, or "(undefined)" if the conversion raises an exception
     */
    @Override
    public Object convertForward(Object value) {
        Date v;

        try {
            v = (Date) value;
            return v.toString();
        } catch (ClassCastException e) {
            return "(undefined)";
        }
    }

    /**
     * Converts reverse the value (from String to Date)
     * @param value an String supposedly containing a Date value
     * @return a Date object with the date value of value, or the current date and time if the parsing fails
     */
    @Override
    public Object convertReverse(Object value) {
        String s;
        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        try {
            s = (String) value;
            d = sdf.parse(s);
        } catch (ClassCastException | ParseException e) {
            Logger.getLogger(DateToStringConverter.class.getName()).log(Level.SEVERE, null, e);
            d = new Date();
        }
        return d;
    }
}
