/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.tasks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.localizethat.Main;
import net.localizethat.model.Glossary;
import net.localizethat.model.GlsEntry;
import net.localizethat.model.GlsTranslation;
import net.localizethat.model.L10n;
import net.localizethat.util.NoCaseStringComparator;

/**
 * SwingWorker task to check two strings (one in the original language and the other in a
 * localized one) against terms of any number of glossaries
 * @author rpalomares
 */
public class CheckGlossaryWorker
        extends SwingWorker<List<CheckGlossaryWorker.FailedEntry>, Void> {
    private StyledDocument doc;
    private String original;
    private String translated;
    private L10n locale;
    private EntityManager em;
    private JTextPane origStrPane;
    private List<Glossary> glsToCheckList;
    private List<CheckGlossaryWorker.FailedEntry> failedEntriesList;

    /**
     * Make default constructor private so the class can't be instantiated without
     * supplying needed parameters
     */
    private CheckGlossaryWorker() {
    }

    public CheckGlossaryWorker(String original, String translated, L10n locale,
            EntityManager em, JTextPane origStrPane, Glossary... glsList) {
        int glsLength = glsList.length;

        this.original = original;
        this.translated = translated;
        this.locale = locale;

        if (em == null) {
            this.em = Main.emf.createEntityManager();
        } else {
            this.em = em;
        }

        this.origStrPane = origStrPane;

        if (glsLength > 0) {
            this.glsToCheckList = Arrays.asList(glsList);
        }
        failedEntriesList = new ArrayList<>(5);
    }

    @Override
    protected List<CheckGlossaryWorker.FailedEntry> doInBackground() throws Exception {
        SimpleAttributeSet regularStyle = new SimpleAttributeSet();
        SimpleAttributeSet failedCheckStyle = new SimpleAttributeSet();
        SimpleAttributeSet suggestionStyle;
        int docPos = 0;
        int stringPos = 0; // Position in original text where next word is found
        int lastStringPos = 0; // Position in original text right after last word was found
        long start = System.currentTimeMillis();
        long finish;
        List<String> originalWords;
        List<String> translatedWords;
        List<GlsEntry> entries;
        NoCaseStringComparator ncsComp = new NoCaseStringComparator();
        ImageIcon failedEntryIcon = new ImageIcon(CheckGlossaryWorker.class.getResource(
                    "/net/localizethat/resources/16-sug-button.png"));

        TypedQuery<GlsEntry> glseQuery = em.createNamedQuery("GlsEntry.findByGlsAndTerm",
                GlsEntry.class);
        TypedQuery<GlsEntry> glse2Query = em.createNamedQuery("GlsEntry.findByGlsAndTermLoCase",
                GlsEntry.class);

        // Define style for regular text (not failed or not a glossary entry)
        StyleConstants.setBold(regularStyle, false);
        StyleConstants.setForeground(regularStyle, Color.black);
        // ...and another one for failed words
        StyleConstants.setBold(failedCheckStyle, true);
        StyleConstants.setForeground(failedCheckStyle, Color.red);

        originalWords = slicePhrase(original);
        translatedWords = slicePhrase(translated);
        Collections.sort(translatedWords, ncsComp);
        doc = origStrPane.getStyledDocument();
        doc.remove(0, doc.getLength());

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

        // For each original word, we must search if any of the possible translations is in the
        // list of translated words. If it is, we remove the "potentially failed entry" and the
        // translated word; otherwise, the "potentially" failed entry turns into real failed
        // entry, being kept in the list
        Iterator<FailedEntry> feIterator = failedEntriesList.iterator();
        while (feIterator.hasNext()) {
            FailedEntry fe = feIterator.next();
            boolean translationFound = false;

            // Insert the text not matched as glossary entry
            stringPos = fe.getPos();
            doc.insertString(docPos, original.substring(lastStringPos, stringPos), regularStyle);
            docPos += (stringPos - lastStringPos);
            lastStringPos += (stringPos - lastStringPos);
        
            for(GlsEntry ge : fe.getGlsEntriesList()) {
                for(GlsTranslation gt : ge.getGlsTranslationCollection()) {
                    // Check only if this translation belongs to the wanted L10n
                    if (gt.getL10nId().equals(locale)) {
                        int index;
                        if (fe.isMatchCase()) {
                            index = Collections.binarySearch(translatedWords, gt.getValue());
                        } else {
                            index = Collections.binarySearch(translatedWords, gt.getValue(), ncsComp);
                        }
                        if (index >= 0) {
                            translatedWords.remove(index);
                            translationFound = true;
                            break;
                        }
                    }
                }
                if (translationFound) {
                    break;
                }
            }
            if (translationFound) {
                // Insert the not failed glossary entry as regular text
                doc.insertString(docPos, fe.getWord(), regularStyle);
                docPos += fe.getWord().length();
                lastStringPos += fe.getWord().length();
                
                // And remove the not failed entry
                feIterator.remove();
            } else {
                // Insert the failed word with the failed check style
                doc.insertString(docPos, fe.getWord(), failedCheckStyle);
                docPos += fe.getWord().length();
                lastStringPos += fe.getWord().length();

                // Create an icon to display suggestions
                StringBuilder sb = new StringBuilder(20);
                sb.append("<html>");
                for (GlsEntry ge : fe.getGlsEntriesList()) {
                    for (GlsTranslation gt : ge.getGlsTranslationCollection()) {
                        sb.append("<b>");
                        sb.append(gt.getValue());
                        sb.append("</b>");
                        sb.append(" (from ");
                        sb.append(ge.getGlosId().getName());
                        sb.append(")");
                        sb.append("<br>");
                    }
                }
                sb.append("</html>");
                JLabel l = new JLabel(failedEntryIcon);
                l.setToolTipText(sb.toString());

                // Create a new style (because the label must be different each time to
                // hold the corresponding suggestions) and attach to the style the label
                suggestionStyle = new SimpleAttributeSet();
                StyleConstants.setComponent(suggestionStyle, l);
                doc.insertString(docPos, " ", suggestionStyle);
                docPos = doc.getLength();
            }
        }

        // Insert the remaining text
        stringPos = original.length();
        doc.insertString(docPos, original.substring(lastStringPos, stringPos), regularStyle);
        docPos += (stringPos - lastStringPos);
        lastStringPos += (stringPos - lastStringPos);

        finish = System.currentTimeMillis();
        Logger.getLogger(CheckGlossaryWorker.class.getName()).log(Level.INFO,
                "Check glossary exec time: {0} ms", (finish - start));
        return failedEntriesList;
    }

    @Override
    public void done() {
        origStrPane.repaint();
    }

    private List<String> slicePhrase(String phrase) {
        List<String> words = new ArrayList<>(5);

        StringTokenizer st = new StringTokenizer(phrase, " \\.,'");
        while (st.hasMoreTokens()) {
            words.add(st.nextToken());
        }
        return words;
    }

    public static class FailedEntry {
        private String word;
        private int pos;
        private boolean matchCase;
        private List<GlsEntry> GlsEntriesList;

        public FailedEntry(String word, int pos, boolean matchCase, GlsEntry ge) {
            this.word = word;
            this.pos = pos;
            this.GlsEntriesList = new ArrayList<>(0);
            if (ge != null) {
                GlsEntriesList.add(ge);
            }
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public boolean isMatchCase() {
            return matchCase;
        }

        public void setMatchCase(boolean matchCase) {
            this.matchCase = matchCase;
        }

        public List<GlsEntry> getGlsEntriesList() {
            return GlsEntriesList;
        }

        public void addGe(GlsEntry ge) {
            if (ge != null) {
                GlsEntriesList.add(ge);
            }
        }

        public void clearLge() {
            GlsEntriesList = new ArrayList<>(0);
        }
    }

}
