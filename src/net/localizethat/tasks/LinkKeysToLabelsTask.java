/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.tasks;

import java.util.ArrayList;
import java.util.List;
import net.localizethat.model.LocaleContent;
import net.localizethat.model.LocaleFile;

/**
 * This class accepts a list (which may come from a LocaleFile getChildren method)
 * and tries to find connections between labels and accesskeys/commandkeys
 * 
 * This class does not try to persist the changes made to the list; that would
 * be the responsability of the caller. This allows this class to not have
 * dependencies on JPA classes/interfaces
 * 
 * This class implements Runnable so it can be called as a task, if desired
 * 
 * @author rpalomares
 */


public class LinkKeysToLabelsTask implements Runnable {
    private final List<LocaleContent> localeContentList;
    private int totalAccessKeysLinksMade = 0;
    private int totalCommandKeysLinksMade = 0;
    
    public LinkKeysToLabelsTask(List<LocaleContent> localeContentList) {
        this.localeContentList = localeContentList;
    }
    
    public LinkKeysToLabelsTask(LocaleFile lf) {
        this.localeContentList = new ArrayList<>(lf.getChildren());
    }
    
    public int makeLinks() {
        return 0;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
