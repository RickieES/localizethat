/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LocalizeThat class for marking regular key-value pairs. It is a kind (ie., subclass)
 * of LocaleContent, which in turn implements the LocaleNode interface.
 *
 * Key-value pairs are present, for example, in DTD files and Properties files.
 *
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("LTKeyValuePair")
@XmlRootElement
public class LTKeyValuePair extends LocaleContent {
    public static final int TEXTVALUE_LENGTH = 32672;

    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    transient protected Collection<Void> children;

    @Column(name = "LCONTENTTEXTVALUE", nullable = false, length = TEXTVALUE_LENGTH)
    private String textValue;
    
    @JoinColumn(name = "LCONTENTCOMMENT", referencedColumnName = "ID", nullable = true)
    @OneToOne(optional = true)
    private LTComment ltComment;


    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue.substring(0, Math.min(textValue.length(), LTKeyValuePair.TEXTVALUE_LENGTH));
    }

    public LTComment getLtComment() {
        return ltComment;
    }

    public void setLtComment(LTComment ltComment) {
        this.ltComment = ltComment;
    }

}
