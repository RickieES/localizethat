/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.localizethat.io.parsers.DTDReadHelper;
import net.localizethat.model.LocaleContent;

/**
 * DTD ParseableFileAccess utility class that returns a list of LTContent objects
 * @author rpalomares
 */
public class DtdFileAccess implements ParseableFileAccess {

    @Override
    public List<LocaleContent> parse(Reader is) throws ParseException {
        List<LocaleContent> lcList;
        LineNumberReader lnr;

        try {
            if (is instanceof LineNumberReader) {
                lnr = (LineNumberReader) is;
            } else {
                lnr = new LineNumberReader(is);
            }
            DTDReadHelper dtdReadHelper = new DTDReadHelper(lnr);

            dtdReadHelper.parseStream();
            lcList = dtdReadHelper.getLocaleContentList();
            lnr.close();
        } catch (ParseException ex) {
            Logger.getLogger(DtdFileAccess.class.getName()).log(Level.SEVERE,
                    "Error parsing DTD file", ex);
            lcList = null;
        } catch (IOException ex) {
            Logger.getLogger(DtdFileAccess.class.getName()).log(Level.SEVERE, null, ex);
            lcList = null;
        }
        return lcList;
    }
}
