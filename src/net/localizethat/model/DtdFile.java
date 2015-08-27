/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import net.localizethat.io.DtdFileAccess;

/**
 * <p>DTD-based localization file.</p>
 * <p>DTD files are key-value type files, with some oddities:</p>
 * <ul>
 *     <li>Some entries may be &quot;PE entities&quot;, which are kind of &quot;call for
 *         other files include&quot;, but they tend to use chrome: URLs, unresolvable
 *         inside Java. They have the form of &lt;!ENTITY % name SYSTEM
 *         &quot;chrome://path/to/DTDfile&quot;&gt;</li>
 *     <li>Some entries may be &quot;PE references&quot;, which the markers for insertion
 *         of previously defined PE entities. They have the form &quot;%name&quot;, without
 *         quotes nor any other marker in the line.</li>
 * </ul>
 *
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("DTDFile")
@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "LocaleFile.countAll", query = "SELECT COUNT(lf) FROM LocaleFile lf"),
//    @NamedQuery(name = "LocaleFile.count", query = "SELECT COUNT(lf) FROM LocaleFile lf")
//})
public class DtdFile extends ParseableFileAdapter {
    private static final long serialVersionUID = 1L;

    public DtdFile() {
      super();
      children = new ArrayList<>(25);
    }

    @Override
    protected List<LocaleContent> beforeParsingHook(LineNumberReader fileReader) throws ParseException {
        DtdFileAccess dtdFA = new DtdFileAccess();
        List<LocaleContent> parsedContentList = dtdFA.parse(fileReader);
        return parsedContentList;
    }

    @Override
    protected void afterParsingHook(LineNumberReader fileReader) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTComment lc) {
        StringBuilder sb = new StringBuilder(100);
        
        sb.append("<!-- ");
        sb.append(lc.getTextValue());
        sb.append(" -->");
        pw.println(sb.toString());
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTExternalEntity lc) {
        StringBuilder sb = new StringBuilder(100);
        
        sb.append("<!ENTITY % ");
        sb.append(lc.getName());
        sb.append(" SYSTEM \"");
        sb.append(lc.getTextValue());
        sb.append("\">");
        sb.append("\n");
        sb.append("%");
        sb.append(lc.getName());
        sb.append(";");
        pw.println(sb.toString());
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTIniSection lc) {
        throw new UnsupportedOperationException("No INI sections allowd in DTD files");
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTKeyValuePair lc) {
        StringBuilder sb = new StringBuilder(100);
        
        sb.append("<!ENTITY ");
        sb.append(lc.getName());
        sb.append(" \"");
        sb.append(lc.getTextValue());
        sb.append("\">");
        pw.println(sb.toString());
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTLicense lc) {
        StringBuilder sb = new StringBuilder(100);
        
        sb.append("<!-- ");
        sb.append(lc.getTextValue());
        sb.append(" -->");
        pw.println(sb.toString());
    }

    @Override
    public void printLocaleContent(PrintWriter pw, LTWhitespace lc) {
        pw.println(lc.getTextValue());
    }
}
