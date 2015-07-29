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
 * LocalizeThat class for marking comments in files (except license headers,
 * which have their own class). It is a kind (ie., subclass) of LTContent, which
 * in turn implements the LocaleContent and LocaleNode interfaces
 * 
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("LTComment")
@XmlRootElement
public class LTComment extends LTContent implements EditableLocaleContent {
    public static final int COMMENTTEXTVALUE_LENGTH = 32672;
    private static final long serialVersionUID = 1L;

    transient protected Collection<Void> children;
    transient private String entityName;
    @Column(name = "LCONTENTTEXTVALUE", nullable = false, length = COMMENTTEXTVALUE_LENGTH)
    private String textValue;
    @Basic(optional = false)
    @Column(name = "LCOMMENTTYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentType commentType;

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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public String getTextValue() {
        return textValue;
    }

    @Override
    public void setTextValue(String textValue) {
        this.textValue = textValue.substring(0, Math.min(textValue.length(), LTComment.COMMENTTEXTVALUE_LENGTH));
    }

    public CommentType getCommentType() {
        return commentType;
    }

    public void setCommentType(CommentType commentType) {
        this.commentType = commentType;
    }

    public boolean isL10NNote() {
        return (this.commentType == CommentType.LOCALIZATION_NOTE);
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
