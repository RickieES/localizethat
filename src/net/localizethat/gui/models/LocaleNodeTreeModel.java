/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.models;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.LocaleNode;
import net.localizethat.model.LocalePath;
import net.localizethat.model.Product;

/**
 *
 * @author rpalomares
 */
public class LocaleNodeTreeModel extends DefaultTreeModel {

    public static LocaleNodeTreeModel createFromProduct(Product p) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Paths for " + p.getName());

        L10n l = p.getL10nId();

        for(LocalePath lp : p.getChildren()) {
            // Let's add only the default locale paths
            if (lp.getL10nId().equals(l)) {
                // Not really the paths, but the associated locale containers
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(lp.getLocaleContainer());
                root.add(child);
            }
        }
        return new LocaleNodeTreeModel(root);
    }

    public static LocaleNodeTreeModel createFromLocalePath(LocalePath... lpArray) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        for(LocalePath lp : lpArray) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(lp.getLocaleContainer());
            root.add(child);
        }
        return new LocaleNodeTreeModel(root);
    }

    public LocaleNodeTreeModel(TreeNode root) {
        super(root);
        // LocaleNode nodeObject = (LocaleNode) node.getUserObject();
        for(Enumeration e = root.children(); e.hasMoreElements();) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement();
            LocaleNode nodeObject = (LocaleNode) childNode.getUserObject();
            if (nodeObject instanceof LocaleContainer) {
                loadGrandChildNodes(childNode, (LocaleContainer) nodeObject);
            }
        }
    }

    public LocaleNodeTreeModel(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    private void loadGrandChildNodes(DefaultMutableTreeNode childNode, LocaleContainer childNodeObject) {
        for(LocaleNode grandChildNodeObject : childNodeObject.getChildren()) {
            DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(grandChildNodeObject);
            childNode.add(grandChildNode);
        }
        for(LocaleFile grandChildNodeObject : childNodeObject.getFileChildren()) {
            DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(grandChildNodeObject);
            childNode.add(grandChildNode);
        }
    }
}
