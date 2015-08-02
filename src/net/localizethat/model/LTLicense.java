/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LocalizeThat class for marking file license headers. It is a kind (ie., subclass)
 of LTContent, which in turn implements the LocaleNode interface
 *
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("LTLicense")
@XmlRootElement
public class LTLicense extends LTContent implements EditableLocaleContent {
    private static final long serialVersionUID = 1L;
   
    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    transient protected Collection<Void> children;
    @Basic(optional = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "LCONTENTTRNSSTATUS", nullable = true)
    private TranslationStatus trnsStatus;

    @Override
    public boolean addChild(LocaleNode node) {
        return false;
    }

    @Override
    public boolean hasChild(LocaleNode node) {
        return false;
    }

    @Override
    public boolean hasChild(String name) {
        return false;
    }

    @Override
    public boolean hasChild(String name, boolean matchCase) {
        return false;
    }

    @Override
    public LocaleNode getChildByName(String name) {
        return null;
    }

    @Override
    public LocaleNode getChildByName(String name, boolean matchCase) {
        return null;
    }

    @Override
    public Collection<? extends LocaleNode> getChildren() {
        return null;
    }

    @Override
    public LocaleNode removeChild(String name) {
        return null;
    }

    @Override
    public LocaleNode removeChild(String name, boolean matchCase) {
        return null;
    }

    @Override
    public boolean removeChild(LocaleNode node) {
        return false;
    }

    @Override
    public boolean clearChildren() {
        return false;
    }

    @Override
    public TranslationStatus getTrnsStatus() {
        return trnsStatus;
    }

    @Override
    public void setTrnsStatus(TranslationStatus trnsStatus) {
        this.trnsStatus = trnsStatus;
    }

    @Override
    public boolean isEditable() {
        return true;
    }
}
