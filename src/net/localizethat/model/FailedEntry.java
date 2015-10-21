/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.util.ArrayList;
import java.util.List;
import net.localizethat.model.GlsEntry;

/**
 * Used in glossary checks, this class representes a (potential or real) failed entry
 * in a glossary
 * @author rpalomares
 */
public class FailedEntry {
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
