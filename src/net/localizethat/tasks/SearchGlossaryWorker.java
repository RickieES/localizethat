/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.tasks;

import net.localizethat.model.FailedEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.SwingWorker;
import net.localizethat.Main;
import net.localizethat.gui.listeners.CheckGlossaryTranslatedTextListener;
import net.localizethat.model.Glossary;
import net.localizethat.model.GlsEntry;
import net.localizethat.model.L10n;
import net.localizethat.util.NoCaseStringComparator;

/**
 * SwingWorker task to check the string in original language against terms of any number
 * of glossaries to build a list of entries to be checked
 * @author rpalomares
 */
public class SearchGlossaryWorker
        extends SwingWorker<List<FailedEntry>, Void> {
    private String original;
    private EntityManager em;
    private List<Glossary> glsToCheckList;
    private List<FailedEntry> failedEntriesList;

    /**
     * Make default constructor private so the class can't be instantiated without
     * supplying needed parameters
     */
    private SearchGlossaryWorker() {
    }

    public SearchGlossaryWorker(String original, EntityManager em, Glossary... glsList) {
        int glsLength = glsList.length;

        this.original = original;

        if (em == null) {
            this.em = Main.emf.createEntityManager();
        } else {
            this.em = em;
        }

        if (glsLength > 0) {
            this.glsToCheckList = Arrays.asList(glsList);
        }
        failedEntriesList = new ArrayList<>(5);
    }

    @Override
    protected List<FailedEntry> doInBackground() throws Exception {
        int stringPos = 0; // Position in original text where next word is found
        int lastStringPos = 0; // Position in original text right after last word was found
        List<String> originalWords;
        List<GlsEntry> entries;
        NoCaseStringComparator ncsComp = new NoCaseStringComparator();

        TypedQuery<GlsEntry> glseQuery = em.createNamedQuery("GlsEntry.findByGlsAndTerm",
                GlsEntry.class);
        TypedQuery<GlsEntry> glse2Query = em.createNamedQuery("GlsEntry.findByGlsAndTermLoCase",
                GlsEntry.class);

        originalWords = slicePhrase(original);

        try {
        // Build a list of words from the original text present in glossaries
        Iterator<String> origWordsIt = originalWords.iterator();
        while (origWordsIt.hasNext()) {
            String word = origWordsIt.next();
            stringPos = original.indexOf(word, stringPos);
            FailedEntry fe = new FailedEntry(word, stringPos, true, null);
            
            glseQuery.setParameter("glseterm", word);
            glse2Query.setParameter("glseterm", word);
            stringPos += word.length();

            for(Glossary g : glsToCheckList) {
                glseQuery.setParameter("glosid", g);
                entries = glseQuery.getResultList();

                if (entries.isEmpty()) {
                    fe.setMatchCase(false);
                    glse2Query.setParameter("glosid", g);
                    entries = glse2Query.getResultList();
                }
                for(GlsEntry ge : entries) {
                    fe.addGe(ge);
                }
            }

            if (fe.getGlsEntriesList().size() > 0) {
                // Just a "potential" failed entry at the moment
                failedEntriesList.add(fe);
            }
        }
        } catch (Exception ex) {
                Logger.getLogger(SearchGlossaryWorker.class.getName()).log(
                        Level.WARNING, "Error during glossary search", ex);

        }
        return failedEntriesList;
    }

    @Override
    public void done() {
        // origStrPane.repaint();
    }

    private List<String> slicePhrase(String phrase) {
        List<String> words = new ArrayList<>(5);

        StringTokenizer st = new StringTokenizer(phrase, " \\.,'");
        while (st.hasMoreTokens()) {
            words.add(st.nextToken());
        }
        return words;
    }
}
