/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LocalizeThat class for text content of non parseable text files (txt, (x)html, xml, etc.).
 * It is a kind (ie., subclass) of LTContent, which in turn implements the LocaleContent
 * interface.
 * 
 * LTTextContent returns as getTextValue() the fileContent value of its parent file. This way
 * the behavior is similar to any other entry in parseable files, making easier the edition
 * of the file.
 * 
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("LTTextContent")
@XmlRootElement
public class LTTextContent extends LTContent implements EditableLocaleContent {
    private static final long serialVersionUID = 1L;
    @Basic(optional = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "LCONTENTTRNSSTATUS", nullable = true)
    private TranslationStatus trnsStatus;
    
    public LTTextContent() {
        super();
    }
    
    @Override
    public String getTextValue() {
        if (getParent() instanceof TextFile) {
            return ((TextFile) getParent()).getFileContent();
        } else {
            Logger.getLogger(LTTextContent.class.getName()).log(Level.WARNING,
                    "TextContent node on a non-TextFile parent (ID: {0}, parent file ID/name: {1} - {2}",
                    new Object[]{this.getId(), getParent().getId(), getParent().getName()});
            return "";
        }
    }

    @Override
    public void setTextValue(String textValue) {
        if (getParent() instanceof TextFile) {
            ((TextFile) getParent()).setFileContent(
                    textValue.substring(0, Math.min(textValue.length(),
                            TextFile.TEXTFILE_LENGTH)));
        } else {
            Logger.getLogger(LTTextContent.class.getName()).log(Level.WARNING,
                    "TextContent node on a non-TextFile parent (ID: {0}, parent file ID/name: {1} - {2}",
                    new Object[]{this.getId(), getParent().getId(), getParent().getName()});
        }
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
    public void setKeepOriginal(boolean keepOriginal) {
        super.setKeepOriginal(keepOriginal);
        if (keepOriginal) {
            TextFile parent = (TextFile) getParent();
            TextFile defaultParent = (TextFile) parent.getDefLocaleTwin();
            parent.setMd5Hash(defaultParent.getMd5Hash());
        }
    }

    @Override
    public boolean isEditable() {
        return true;
    }
}
