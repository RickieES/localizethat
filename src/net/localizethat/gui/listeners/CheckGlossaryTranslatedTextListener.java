/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.listeners;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import net.localizethat.model.FailedEntry;
import net.localizethat.model.Glossary;
import net.localizethat.model.L10n;
import net.localizethat.tasks.CheckGlossaryWorker;
import net.localizethat.tasks.SearchGlossaryWorker;

public class CheckGlossaryTranslatedTextListener extends AbstractSimpleDocumentListener {
    private CheckGlossaryWorker cgw;
    private SearchGlossaryWorker sgw;
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
        if (original.length() < 2048) { // We only check glossaries for short strings
            // Original string has changed, so we rebuild the list of glossary entries
            if ((sgw != null) && (!sgw.isDone())) {
                sgw.cancel(true);
            }
            sgw = new SearchGlossaryWorker(original, em, glsList);
            sgw.execute();
            doTask(null);
        }
    }

    public void setLocale(L10n locale) {
        this.locale = locale;
        cancelTask();
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
        if ((sgw != null) && (sgw.isDone())
                && (cgw == null || cgw.isDone())) {
            try {
                List<FailedEntry> feList = sgw.get();
                cgw = new CheckGlossaryWorker(original, translatedTextArea.getText(),
                        locale, origStrPane, feList);
                cgw.execute();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(CheckGlossaryTranslatedTextListener.class.getName()).log(
                        Level.WARNING, "Glossary check couldn't complete", ex);
            }
        }
    }
}
