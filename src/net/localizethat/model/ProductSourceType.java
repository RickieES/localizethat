/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

/**
 * Product source types represent the update type of the product: Mercurial, Git,
 * SVN, CVS or manual.
 * @author rpalomares
 */
public enum ProductSourceType {
    HG, GIT, SVN, CVS, MANUAL;
}
