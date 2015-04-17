/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.text.ParseException;
import java.util.List;

/**
 *
 * @author rpalomares
 */
public interface ParseableFile extends LocaleNode {

    /**
     * Parses a text file (like a DTD file, a Properties file, etc.)
     * @return a list of LocaleContent objects
     * @throws ParseException in case the parsing fails
     */
    List<LocaleContent> parse() throws ParseException;

    LTLicense getLicense();

    // List<LObject> getLObjectCollection();
    List<LocaleContent> getLObjectCollection();

}
