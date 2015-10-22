/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import net.localizethat.io.PropertiesFileAccess;

/**
 *
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("PropertiesFile")
@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "LocaleFile.countAll", query = "SELECT COUNT(lf) FROM LocaleFile lf"),
//    @NamedQuery(name = "LocaleFile.count", query = "SELECT COUNT(lf) FROM LocaleFile lf")
//})
public class PropertiesFile extends ParseableFileAdapter {
    private static final long serialVersionUID = 1L;

    public PropertiesFile() {
      super();
      // children = new ArrayList<>(25);
    }

    @Override
    public List<LTContent> getLObjectCollection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<LocaleContent> beforeParsingHook(LineNumberReader fileReader) throws ParseException {
        PropertiesFileAccess propFA = new PropertiesFileAccess(this);
        List<LocaleContent> parsedContentList = propFA.parse(fileReader);
        return parsedContentList;
    }

    @Override
    protected void afterParsingHook(LineNumberReader fileReader) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTComment lc) {
        pw.println(lc.getTextValue());
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTExternalEntity lc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTIniSection lc) {
        pw.println("[" + lc.getName() + "]");
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTKeyValuePair lc) {
        StringBuilder sb = new StringBuilder(100);

        sb.append(lc.getName());
        sb.append("="); // TODO we might want to have a preference to surround '=' with spaces, like ' = '
        sb.append(lc.getTextValue());
        pw.println(sb.toString());
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTLicense lc) {
        pw.println(lc.getTextValue());
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTWhitespace lc) {
        pw.println(lc.getTextValue());
    }

}
