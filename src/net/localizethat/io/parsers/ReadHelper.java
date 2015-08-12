/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.io.parsers;

import java.text.ParseException;
import java.util.List;
import net.localizethat.model.LocaleContent;

/**
 * Interface that read helpers (parsers) must implement
 * @author rpalomares
 */
public interface ReadHelper {

    /**
     * Parses the stream associated to the class implementing ReadHelper interface
     * @throws ParseException
     */
    void parseStream() throws ParseException;

    /**
     * Returns a list of LocaleContent entities after parsing the stream
     * @return a list of LocaleContent entities
     */
    List<LocaleContent> getLocaleContentList();

}
