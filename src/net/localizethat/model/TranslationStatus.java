/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.model;

import java.awt.Color;

/**
 * Translation possible statuses
 * @author rpalomares
 */
public enum TranslationStatus {
    Untranslated ("non-empty string with no translation nor Keep Original flag",
            Color.RED),
    Modified ("en-US string that has been modified since last time it was "
            + "translated", new Color(244, 192, 32)),
    Approximated ("en-US string that has been auto-translated based on a non-100% "
            + "match with the best possible coincidence", new Color(244, 220, 102)),
    Proposed ("en-US string that has been auto-translated based on a 100% match "
            + "with the most repeated translation among two or more",
            new Color(224, 255, 153)),
    Copied ("en-US string that has been auto-translated based on a 100% match "
            + "with only one translation", new Color(51, 255, 51)),
    Translated ("en-US string with Keep Original set or manually translated",
            Color.WHITE);

    private final String description;
    private final Color displayedColor;

    TranslationStatus(String description, Color displayedColor) {
        this.description = description;
        this.displayedColor = displayedColor;
    }

    public String description() {
        return description;
    }

    public Color displayedColor() {
        return displayedColor;
    }

    public String colorAsRGBString() {
        return Integer.toString(displayedColor.getRed()) + ","
             + Integer.toString(displayedColor.getGreen()) + ","
             + Integer.toString(displayedColor.getBlue());
    }
}
