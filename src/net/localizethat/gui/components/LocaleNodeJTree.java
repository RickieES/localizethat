/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.components;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.LocaleNode;

/**
 * This class is not used anyhwere at this moment
 * @author rpalomares
 */
public class LocaleNodeJTree extends JTree {
    
    public LocaleNodeJTree() {
        super();
        addTreeSelectionListener(new LocaleNodeTreeSelectionListener(this));
        this.addTreeExpansionListener(null);
        this.addTreeWillExpandListener(null);
    }

    class LocaleNodeTreeSelectionListener implements TreeSelectionListener {
        private final LocaleNodeJTree tree;

        LocaleNodeTreeSelectionListener(LocaleNodeJTree tree) {
            this.tree = tree;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    tree.getLastSelectedPathComponent();

            if (node == null) {
                return;
            }

            LocaleNode nodeObject = (LocaleNode) node.getUserObject();
            if (node.isLeaf()) {
                if (nodeObject instanceof LocaleFile) {
                    // TODO Send signal to update panels
                }
            }
        }
    }
}
