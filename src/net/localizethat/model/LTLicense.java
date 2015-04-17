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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LocalizeThat class for marking file license headers. It is a kind (ie., subclass)
 * of LocaleContent, which in turn implements the LocaleNode interface
 *
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("LTLicense")
@XmlRootElement
public class LTLicense extends LocaleContent {
    public static final int TEXTVALUE_LENGTH = 32672;
   
    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    transient protected Collection<Void> children;

    @Column(name = "LCONTENTTEXTVALUE", nullable = false, length = TEXTVALUE_LENGTH)
    private String textValue;

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

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue.substring(0, Math.min(textValue.length(), TEXTVALUE_LENGTH));
    }

}
