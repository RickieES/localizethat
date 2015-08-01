/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.io.parsers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.localizethat.model.CommentType;
import net.localizethat.model.LTExternalEntity;
import net.localizethat.model.LTComment;
import net.localizethat.model.LTKeyValuePair;
import net.localizethat.model.LTLicense;
import net.localizethat.model.LTContent;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.Locator2;
import org.xml.sax.ext.Locator2Impl;

/**
 * A DTD parser that overrides DefaultHandler2 (a class implementing empty bodies for
 * all methods of several XML/DTD SAX interfaces)
 *
 * As we are using an XML parser to parse a DTD, we need to cheat it into believing it
 * is parsing a real XML stream and then switch the InputSource to the real DTD file. This
 * is a clever idea used by Henrik Lynggaard in MozillaTranslator
 *
 * @author rpalomares
 */
public class DTDReadHelper extends DefaultHandler2 {
    // In order to get the SAXParser work properly with a DTD file not bound to any XML,
    // we need to trick it into believing it is working with an XML file
    private static final String dummyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<!DOCTYPE dialog SYSTEM \"LocalizeThat\">"
            + "<dialog></dialog>";
    private static final String brandDummyDtd = "";
    private static final Logger fLogger = Logger.getLogger(DTDReadHelper.class.getPackage().
            getName());
    private final LineNumberReader is;
    private final List<LTContent> lcList;
    private Locator2 locator;
    
    // FIXME This is not actually a *line* counter, but a *managed token* counter.
    // For every callback invoked, we will add 1 to this counter and use it as if
    // it was the line of the file.
    private int lineCount;

    /**
     * Creates a new instance of DTDReadHelper
     * @param dtdReader
     */
    public DTDReadHelper(LineNumberReader dtdReader) {
        super();
        this.is = dtdReader;
        lcList = new ArrayList<>(15);
        locator = new Locator2Impl();
    }

    public void setLocator2(Locator2 locator) {
        this.locator = locator;
    }

    public List<LTContent> getLocaleContentList() {
        if (is == null) {
            return null;
        } else {
            return lcList;
        }
    }

    public void parseStream() throws ParseException {
        lineCount = 0;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser saxParser = factory.newSAXParser();

            XMLReader xmlread = saxParser.getXMLReader();
            xmlread.setProperty("http://xml.org/sax/properties/lexical-handler", this);
            xmlread.setProperty("http://xml.org/sax/properties/declaration-handler", this);

            saxParser.parse(new ByteArrayInputStream(dummyXml.getBytes()), this);
        } catch (ParserConfigurationException e) {
            throw new ParseException("Error in the parser configuration", 0);
        } catch (SAXException e) {
            throw new ParseException("Generic SAX error", is.getLineNumber());
        } catch (IOException e) {
            throw new ParseException("Load file error", 0);
        }
    }

    /****************************************
     * Start of SAX parsing overriden methods
     ****************************************/

    /**
     * Processes a comment, that may be a regular comment or a license header
     * @param ch The collection of chars parsed as a comment
     * @param start The start position of the comment
     * @param length The length
     * @throws SAXException if something goes wrong while parsing
     */
    @Override
    public void comment(char ch[], int start, int length) throws SAXException {
        String thisComment = new String(ch, start, length);
        String entityName = null;
        LTLicense thisFileLicense;
        Pattern p;
        Matcher m;
        int lineNumber = lineCount++;

        // Have we found an MPL1 / MPL2 license block?
        if (thisComment.contains("*** BEGIN LICENSE BLOCK ***") ||
                thisComment.contains("http://mozilla.org/MPL/2.0/")) {

            thisFileLicense = new LTLicense();
            thisFileLicense.setName("LTLicenseHeader");
            thisFileLicense.setTextValue(thisComment);
            thisFileLicense.setOrderInFile(lineNumber);
            thisFileLicense.setCreationDate(new Date());
            thisFileLicense.setLastUpdate(thisFileLicense.getCreationDate());
            lcList.add(thisFileLicense);

        } else if (thisComment.toUpperCase().contains("LOCALIZATION NOTE")) {
            /* The localization note format should be:
             *   LOCALIZATION NOTE (entity): comment
             * comment may expand several lines
             *
             * However, sometimes no entity is given
             */

            p = Pattern.compile("LOCALIZATION NOTE\\s+\\(([^).]+)\\):?[\\n.]+$",
                    Pattern.CASE_INSENSITIVE);
            m = p.matcher(thisComment);

            if (m.matches()) {
                entityName = m.group(1);
            }

            LTComment ltComment = new LTComment();
            ltComment.setName("-comment@line-" + lineNumber);
            ltComment.setCommentType(CommentType.LOCALIZATION_NOTE);
            ltComment.setTextValue(thisComment);
            ltComment.setOrderInFile(lineNumber);
            ltComment.setCreationDate(new Date());
            ltComment.setLastUpdate(ltComment.getCreationDate());
            // If we've got a localization note referencing an entity, we save it for further search
            // in the model
            if (entityName != null) {
                ltComment.setEntityName(entityName);
            }
            lcList.add(ltComment);

        } else {
            LTComment ltComment = new LTComment();
            ltComment.setName("-comment@line-" + lineNumber);
            ltComment.setCommentType(CommentType.GENERAL);
            ltComment.setTextValue(thisComment);
            ltComment.setOrderInFile(lineNumber);
            ltComment.setCreationDate(new Date());
            ltComment.setLastUpdate(ltComment.getCreationDate());
            lcList.add(ltComment);
            // TODO set other properties (like DefaultLocaleTwin)
            // TODO persist thisFileLicense and commit changes to it and dtdFile
        }
    }

    @Override
    public void externalEntityDecl(String name, String publicId, String systemId) {
        int lineNumber = lineCount++;
        LTExternalEntity ltExtEntity = new LTExternalEntity(name, publicId, systemId);
        ltExtEntity.setName(name);
        // publicId is currently a transient property and is NOT persisted in DB
        ltExtEntity.setPublicId(publicId);
        ltExtEntity.setSystemId(systemId);
        ltExtEntity.setOrderInFile(lineNumber);
        ltExtEntity.setCreationDate(new Date());
        ltExtEntity.setLastUpdate(ltExtEntity.getCreationDate());
        lcList.add(ltExtEntity);
    }

    @Override
    public void internalEntityDecl(String name, String value) {
        int lineNumber = lineCount++;
        LTKeyValuePair ltKvp = new LTKeyValuePair();
        ltKvp.setName(name);
        ltKvp.setTextValue(value);
        ltKvp.setCreationDate(new Date());
        ltKvp.setLastUpdate(ltKvp.getCreationDate());
        ltKvp.setOrderInFile(lineNumber);
        lcList.add(ltKvp);
        // TODO set other properties (like DefaultLocaleTwin)
        // TODO persist thisFileLicense and commit changes to it and dtdFile
    }

    @Override
    public InputSource resolveEntity(java.lang.String name, java.lang.String publicId,
            java.lang.String baseURI, java.lang.String systemId) {

        if (name == null) {
            switch (systemId) {
                // Trick to get a SAX XML Parser to parse a DTD
                case "LocalizeThat":
                    return new InputSource(is);

                // Trick to resolve references to brand.dtd without
                // actually having to resolve, load and parse
                // the chrome: URI
                case "chrome://branding/locale/brand.dtd":
                default:
                    return new InputSource(new ByteArrayInputStream(brandDummyDtd.getBytes()));
            }
        } else {
            return new InputSource(new StringReader(""));
        }
    }
}
