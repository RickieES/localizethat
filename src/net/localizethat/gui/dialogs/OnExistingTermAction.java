/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.dialogs;

/**
 * Possible actions when finding existing terms on CSVImport
 * @author rpalomares
 */
public enum OnExistingTermAction {
    ADD_NEW_TRANSLATIONS_ONLY("Add new translations only", 0),
    DO_NOT_ADD_TRANSLATIONS("Do not add translations", 1),
    MERGE("Merge (add new and update comments)", 2),
    REPLACE("Replace (remove existing translations)", 3);

    private final String textual;
    private final int order;

    OnExistingTermAction(String textual, int order) {
        this.textual = textual;
        this.order = order;
    }

    @Override
    public String toString() {
        return this.textual;
    }

    public int order() {
        return this.order;
    }
}
