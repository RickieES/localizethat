/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
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
@Table(name = "APP.LOCALECONTAINER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LocaleContainer.countAll", query = "SELECT COUNT(lc) FROM LocaleContainer lc"),
    @NamedQuery(name = "LocaleContainer.count", query = "SELECT COUNT(lc) FROM LocaleContainer lc")
})

public class LocaleContainer extends AbstractLocaleNode implements Serializable {

}
