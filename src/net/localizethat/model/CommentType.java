/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

/**
 * Enum type to label different types of comments. It is not intended to set apart comments
 * by filetype, but by semantic meaning (general comment, localization note, etc.)
 * @author rpalomares
 */
public enum CommentType {
    // Max length: 20->|
    // 45678901234567890
    GENERAL,
    // Max length: 20->|
    LOCALIZATION_NOTE;
    // Max length: 20->|
}
