/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>DTD-based localization file.</p>
 * <p>DTD files are key-value type files, with some oddities:</p>
 * <ul>
 *     <li>Some entries may be <q>PE entities</q>, which are kind of <q>call for other files include</q>,
 *         but they tend to use chrome: URLs, unresolvable inside Java. They have the form of
 *         &lt;!ENTITY % name SYSTEM &quot;chrome://path/to/DTDfile&quot;&gt;</li>
 *     <li>Some entries may be <q>PE references</q>, which the markers for insertion of previously
 *         defined PE entities. They have the form <q>%name</q>, without quotes nor any other marker
 *         in the line.</li>
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

    public DtdFile() {
      super();
      children = new ArrayList<>(25);
    }

    @Override
    public List<LocaleContent> parse() throws ParseException {
        return null;
    }

}
