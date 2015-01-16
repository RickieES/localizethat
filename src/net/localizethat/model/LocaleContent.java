/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rpalomares
 */
@Entity
@Table(name = "APP.LOCALECONTENT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LocaleContent.countAll", query = "SELECT COUNT(lc) FROM LocaleContent lc"),
    @NamedQuery(name = "LocaleContentt.count", query = "SELECT COUNT(lc) FROM LocaleContent lc")
})
public class LocaleContent extends AbstractLocaleNode<LocaleContainer, LocaleContent, LocaleContent> implements Serializable {

    public LocaleContent() {
      super();
      children = new ArrayList<>(25);
    }

    public File getFile() {
        return new File(getFilePath());
    }

    /**
     * public List <L10nObject> parse() throws ParseException
     */

}
