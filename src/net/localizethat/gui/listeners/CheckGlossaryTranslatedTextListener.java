/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.gui.listeners;

import javax.persistence.EntityManager;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import net.localizethat.model.Glossary;
import net.localizethat.model.L10n;
import net.localizethat.tasks.CheckGlossaryWorker;


public class CheckGlossaryTranslatedTextListener extends AbstractSimpleDocumentListener {
    private CheckGlossaryWorker cgw;
    private String original;
    private final JTextArea translatedTextArea;
    private L10n locale;
    private EntityManager em;
    private final JTextPane origStrPane;
    Glossary[] glsList;

    public CheckGlossaryTranslatedTextListener(String original, JTextArea translatedTextArea,
            L10n locale, EntityManager em, JTextPane origStrPane, Glossary... glsList) {
        this.original = original;
        this.translatedTextArea = translatedTextArea;
        this.locale = locale;
        this.em = em;
        this.origStrPane = origStrPane;
        this.glsList = glsList;
    }

    public void setOriginal(String original) {
        this.original = original;
        cancelTask();
        doTask(null);
    }

    public void setLocale(L10n locale) {
        this.locale = locale;
        cancelTask();
        doTask(null);
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    private void cancelTask() {
        if ((cgw != null) && (!cgw.isDone())) {
            cgw.cancel(true);
            cgw = null;
        }
    }

    @Override
    protected void doTask(DocumentEvent e) {
        if ((cgw == null) || (cgw.isDone())) {
            cgw = new CheckGlossaryWorker(original, translatedTextArea.getText(),
                    locale, em, origStrPane, glsList);
            cgw.execute();
        }
    }
}
