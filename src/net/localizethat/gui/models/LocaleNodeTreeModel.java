/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
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
    private static final long serialVersionUID = 1L;

    public static LocaleNodeTreeModel createFromProduct(Product p) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Paths for " + p.getName());
        List<LocalePath> lpList = new ArrayList<>(p.getChildren().size());

        lpList.addAll(p.getChildren());
        Collections.sort(lpList);
        L10n l = p.getL10nId();

        for(LocalePath lp : lpList) {
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
        List<LocalePath> lpList = Arrays.asList(lpArray);

        Collections.sort(lpList);
        for(LocalePath lp : lpList) {
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

    public final void loadGrandChildNodes(DefaultMutableTreeNode childNode, LocaleContainer childNodeObject) {
        List<LocaleNode> lcList = new ArrayList<>(childNodeObject.getChildren());
        List<LocaleFile> lfList = new ArrayList<>(childNodeObject.getFileChildren());

        Collections.sort(lcList);
        Collections.sort(lfList);

        for(LocaleNode grandChildNodeObject : lcList) {
            DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(grandChildNodeObject);
            childNode.add(grandChildNode);
        }
        for(LocaleFile grandChildNodeObject : lfList) {
            DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(grandChildNodeObject);
            childNode.add(grandChildNode);
        }
    }
}
