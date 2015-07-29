/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LocalizeThat class for marking regular key-value pairs. It is a kind (ie.,
 * subclass) of LTContent, which in turn implements the LocaleContent and
 * LocaleNode interfaces.
 * 
 * Key-value pairs are present, for example, in DTD files and Properties files.
 *
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("LTKeyValuePair")
@XmlRootElement
public class LTKeyValuePair extends LTContent implements EditableLocaleContent {
    public static final int TEXTVALUE_LENGTH = 32672;
    private static final long serialVersionUID = 1L;

    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    // transient protected Collection<Void> children;

    @Basic(optional = false)
    @Column(name = "LCONTENTTEXTVALUE", nullable = false, length = TEXTVALUE_LENGTH)
    private String textValue;
    @OneToOne(optional = true)
    @JoinColumn(name = "LCONTENTCOMMENT", referencedColumnName = "ID", nullable = true)
    private LTComment comment;
    @OneToOne(optional = true)
    @JoinColumn(name = "LCONTENTAKEY", referencedColumnName = "ID", nullable = true)
    private LTKeyValuePair connAccesskey;
    @OneToOne(optional = true)
    @JoinColumn(name = "LCONTENTCKEY", referencedColumnName = "ID", nullable = true)
    private LTKeyValuePair connCommandkey;
    @Basic(optional = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "LCONTENTTRNSSTATUS", nullable = true)
    private TranslationStatus trnsStatus;

    @Override
    public String getTextValue() {
        return textValue;
    }

    @Override
    public void setTextValue(String textValue) {
        this.textValue = textValue.substring(0, Math.min(textValue.length(), LTKeyValuePair.TEXTVALUE_LENGTH));
    }

    public LTComment getComment() {
        return comment;
    }

    public void setComment(LTComment Comment) {
        this.comment = Comment;
    }

    public TranslationStatus getTrnsStatus() {
        return trnsStatus;
    }

    public void setTrnsStatus(TranslationStatus trnsStatus) {
        this.trnsStatus = trnsStatus;
    }

    public LTKeyValuePair getConnAccesskey() {
        return connAccesskey;
    }

    public void setConnAccesskey(LTKeyValuePair connAccesskey) {
        this.connAccesskey = connAccesskey;
    }

    public LTKeyValuePair getConnCommandkey() {
        return connCommandkey;
    }

    public void setConnCommandkey(LTKeyValuePair connCommandkey) {
        this.connCommandkey = connCommandkey;
    }
}
