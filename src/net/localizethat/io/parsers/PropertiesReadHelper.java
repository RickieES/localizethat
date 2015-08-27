/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.io.parsers;

import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.localizethat.model.CommentType;
import net.localizethat.model.LTComment;
import net.localizethat.model.LTIniSection;
import net.localizethat.model.LTKeyValuePair;
import net.localizethat.model.LTLicense;
import net.localizethat.model.LocaleContent;

/**
 * This class reads a Mozilla Properties file (which has some differences
 * with regular Java Properties)
 *
 * @author rpalomares
 */
public class PropertiesReadHelper implements ReadHelper {
    private static final int STATUS_NULL = 0;
    private static final int STATUS_KEY = 1;
    private static final int STATUS_VALUE = 2;
    private static final int STATUS_COMMENT = 3;
    private static final int STATUS_L10N_COMMENT = 4;
    private static final int STATUS_LICENSEHEADER = 5;
    private static final int STATUS_INISECTION = 6;
    private LineNumberReader lnr;
    private int parseCurrentStatus;
    private final List<LocaleContent> lcList;

    /**
     * Empty private constructor to force the use of the public constructor requiring an stream in the form of
     * LineNumberReader instance
     */
    private PropertiesReadHelper() {
        lcList = new ArrayList<>(15);
    }

    /**
     * Public constructor
     *
     * @param lnr a LineNumberReader stream
     */
    public PropertiesReadHelper(LineNumberReader lnr) {
        this.lnr = lnr;
        lcList = new ArrayList<>(15);
    }

    @Override
    public void parseStream() throws ParseException {
        int keyDelimiter;
        int pos;
        char c;
        int lineNumber;
        Matcher m;
        Pattern p;
        String line;
        StringBuilder value = null;
        LocaleContent lc = null;

        line = getNextLine();
        while (line != null) {
            lineNumber = lnr.getLineNumber();

            if (parseCurrentStatus == PropertiesReadHelper.STATUS_NULL) {
                lc = null;
                line = skipWhitespaces(line);
            }

            if (line.isEmpty()) {
                parseCurrentStatus = PropertiesReadHelper.STATUS_NULL;
                line = getNextLine();
                continue;
            }

            c = line.charAt(0);

            // If we're not in the middle of a (multiline) value
            // and the string starts with a comment delimiter
            if (parseCurrentStatus != STATUS_VALUE) {
                switch(c) {
                    case '#':
                    case '!':
                    case ';':
                        if ((parseCurrentStatus != STATUS_L10N_COMMENT)
                                && (parseCurrentStatus != STATUS_LICENSEHEADER)) {
                            parseCurrentStatus = STATUS_COMMENT;
                        }

                        // If the line has "Localization note" inside it, mark it
                        parseCurrentStatus = (line.toUpperCase().contains("LOCALIZATION NOTE"))
                                ? STATUS_L10N_COMMENT : parseCurrentStatus;

                        // Have we found an MPL1 / MPL2 license block?
                        if ((parseCurrentStatus == STATUS_COMMENT)
                                && (line.contains("*** END LICENSE BLOCK ***") ||
                                        line.contains("http://mozilla.org/MPL/2.0/"))) {
                            parseCurrentStatus = STATUS_LICENSEHEADER;
                        }
                        break;
                    case '[':
                        parseCurrentStatus = STATUS_INISECTION;
                        lc = null;
                        break;
                    default:
                        parseCurrentStatus = STATUS_KEY;
                        lc = null;
                }
            }

            switch (parseCurrentStatus) {
                case STATUS_NULL:
                    lc = null;
                    break;
                case STATUS_KEY:
                    // IMPORTANT: we assume keys don't split over several lines
                    // Let's look for the key - value separator, usually '='
                    keyDelimiter = line.indexOf('=');
                    keyDelimiter = (keyDelimiter == -1)
                            ? line.indexOf(':') : keyDelimiter;

                    // If no key delimiter has been found, it is likely a
                    // syntax error, but we will just ignore the line
                    if (keyDelimiter == -1) {
                        parseCurrentStatus = STATUS_NULL;
                        line = getNextLine();
                        continue;
                    }

                    if (lc == null) {
                        lc = new LTKeyValuePair();
                        lc.setName(line.substring(0, keyDelimiter).trim());
                        lc.setOrderInFile(lineNumber);
                        lc.setCreationDate(new Date());
                        lc.setLastUpdate(lc.getCreationDate());
                        lcList.add(lc);
                    }

                    // The key is the left side of the delimiter, removing
                    // existing spaces
                    line = line.substring(keyDelimiter + 1);
                    parseCurrentStatus = STATUS_VALUE;
                    // No break here, since we want to process the remaining
                    // content of line as the value
                case STATUS_VALUE:
                    pos = 0;
                    if (value == null) {
                        while (pos < line.length()
                                && Character.isWhitespace(line.charAt(pos))) {
                            pos++;
                        }
                        value = new StringBuilder(line.length() - pos);
                    }

                    while (pos < line.length()) {
                        c = line.charAt(pos++);
                        if (c == '\\') {
                            if (pos != line.length()) {
                                c = line.charAt(pos++);
                                switch (c) {
                                    case '\\':
                                        // We've found an actual double slash sequence
                                        value.append("\\\\");
                                        break;
                                    case 'n':
                                        value.append("\\n");
                                        break;
                                    case 't':
                                        value.append("\\t");
                                        break;
                                    case 'r':
                                        value.append("\\r");
                                        break;
                                    case 'u':
                                        // Hack to deal with shorter than 4 digits Unicode sequences
                                        int unicodeSequenceEnd = pos;

                                        while ((unicodeSequenceEnd < line.length())
                                                && (unicodeSequenceEnd < (pos + 4))
                                                && ("0123456789ABCDEFabcdef".indexOf(line.charAt(unicodeSequenceEnd)) != -1)) {
                                            unicodeSequenceEnd++;
                                        }

                                        char uni = (char) Integer.parseInt(line.substring(pos, unicodeSequenceEnd), 16);
                                        value.append(uni);
                                        pos = unicodeSequenceEnd;
                                        break;
                                    default:
                                        value.append(c);
                                        break;
                                }
                            } else {
                                value.append('\\');
                                value.append('\n');
                            }
                        } else {
                            value.append(c);
                        }
                    }

                    if ((line.length() == 0)
                            || (line.charAt(line.length() - 1) != '\\')) {

                        lc.setTextValue(value.toString());
                        parseCurrentStatus = STATUS_NULL;
                        value = null;
                        lc = null;
                    }
                    break;
                case STATUS_COMMENT:
                    if (lc == null) {
                        lc = new LTComment();
                        lc.setName("-comment@line-" + lineNumber);
                        ((LTComment) lc).setCommentType(CommentType.GENERAL);
                        lc.setOrderInFile(lineNumber);
                        lc.setCreationDate(new Date());
                        lc.setLastUpdate(lc.getCreationDate());
                        lcList.add(lc);
                    }
                    if ((lc.getTextValue() == null) || (lc.getTextValue().isEmpty())) {
                        lc.setTextValue(line);
                    } else {
                        lc.setTextValue(lc.getTextValue() + "\n" + line);
                    }
                    break;
                case STATUS_L10N_COMMENT:
                    if (lc == null) {
                        lc = new LTComment();
                        lc.setName("-comment@line-" + lineNumber);
                        lc.setOrderInFile(lineNumber);
                        lc.setCreationDate(new Date());
                        lc.setLastUpdate(lc.getCreationDate());
                        lcList.add(lc);
                    }

                    // If a comment block ends with, or contains, a localization
                    // note token, we convert the general comment to be one of
                    // localization note type (but the LTContent subclass is the
                    // same, LTComment). That's why I've put the following call
                    // outside of the above if block
                    ((LTComment) lc).setCommentType(CommentType.LOCALIZATION_NOTE);

                    /*
                     * The localization note format should be: LOCALIZATION NOTE (key): comment
                     * and the comment may expand across several lines
                     *
                     * However, sometimes no key is given
                     */
                    p = Pattern.compile("LOCALIZATION NOTE\\s+\\(([^)]+)\\)",
                            Pattern.CASE_INSENSITIVE);
                    m = p.matcher(line);

                    if (m.find()) {
                        ((LTComment) lc).setEntityName(m.group(1));
                    }

                    if ((lc.getTextValue() == null) || (lc.getTextValue().isEmpty())) {
                        lc.setTextValue(line);
                    } else {
                        lc.setTextValue(lc.getTextValue() + "\n" + line);
                    }
                    break;
                case STATUS_LICENSEHEADER:
                    if (lc == null) {
                        lc = new LTLicense();
                        lc.setName("LTLicenseHeader");
                        lc.setOrderInFile(lineNumber);
                        lc.setCreationDate(new Date());
                        lc.setLastUpdate(lc.getCreationDate());
                        lcList.add(lc);
                    } else if(lc instanceof LTComment) {
                        // The string to identify an MPL 2.0 license header appears
                        // in the third line of the license comment, so it will have
                        // been misprocessed as a comment. We need to replace the
                        // object in the LocaleContainer list
                        LTLicense lc2 = new LTLicense();
                        lc2.setName("LTLicenseHeader");
                        lc2.setOrderInFile(lc.getOrderInFile());
                        lc2.setCreationDate(lc.getCreationDate());
                        lc2.setLastUpdate(lc.getLastUpdate());
                        lc2.setTextValue(lc.getTextValue());
                        lcList.remove(lc);
                        lcList.add(lc2);
                        lc = lc2;
                    }

                    if ((lc.getTextValue() == null) || (lc.getTextValue().isEmpty())) {
                        lc.setTextValue(line);
                    } else {
                        lc.setTextValue(lc.getTextValue() + "\n" + line);
                    }
                    // We're looking for the end of the license block to mark the status as
                    // null
                    parseCurrentStatus = STATUS_NULL;
                    value = null;
                    lc = null;
                    break;
                case STATUS_INISECTION:
                    // IMPORTANT: we assume keys don't split over several lines
                    // Let's look for the key - value separator, usually '='
                    keyDelimiter = line.indexOf(']');

                    if (keyDelimiter == -1) {
                        parseCurrentStatus = STATUS_NULL;
                        line = getNextLine();
                        continue;
                    }

                    lc = new LTIniSection();
                    lc.setName(line.substring(1, keyDelimiter));
                    lc.setOrderInFile(lineNumber);
                    lc.setCreationDate(new Date());
                    lc.setLastUpdate(lc.getCreationDate());
                    lcList.add(lc);
                    break;
            }
            line = getNextLine();
        }
    }

    @Override
    public List<LocaleContent> getLocaleContentList() {
        if (lnr == null) {
            return null;
        } else {
            return lcList;
        }
    }

    private String getNextLine() {
        String line;
        try {
            line = lnr.readLine();
        } catch (IOException ex) {
            Logger.getLogger(PropertiesReadHelper.class.getName()).log(Level.SEVERE, null, ex);
            line = null;
        }
        return line;
    }

    private String skipWhitespaces(String line) {
        int i = 0;
        while ((i < line.length()) && (line.charAt(i) == ' ')) {
            i++;
        }
        if (i < line.length()) {
            return line.substring(i);
        } else {
            return "";
        }
    }
}
