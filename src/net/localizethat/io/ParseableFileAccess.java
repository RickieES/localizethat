/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.io;

import java.io.Reader;
import java.text.ParseException;
import java.util.List;
import net.localizethat.model.LTContent;

/**
 * Interface for accessing files in reading and writing modes
 * @author rpalomares
 */
public interface ParseableFileAccess {
    /**
     * Reads and parses a (file) stream
     * @param is an InputStream that probably contains a parseable file
     * @return a list of LTContent objects with the result of parsing the file
     * @throws java.text.ParseException if the file can't be properly parsed
     */
    List<LTContent> parse(Reader is) throws ParseException;


}
