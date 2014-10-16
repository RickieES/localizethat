/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

/**
 * A class representing a node in the hierarchical representation of a localization project.
 * The root node is still to be decided, and it extends to the different elements of a
 * localizable file
 * @author rpalomares
 */
public interface LocaleNode {
    
    /**
     * Sets the name of the node. What exactly represents the name depends on the specific subtype
     * of LocaleNode, but usually it represents the natural name of the node (what a human would
     * use to refer to it), like a file/folder name, a key or entity name.
     * @param name the string that represents the name
     */
    void setName(String name);

    /**
     * Returns the name of the node (the folder or file name, the entity name, etc.)
     * @return a String with the name of the node
     */
    String getName();

    /**
     * Sets the parent of this node. This allows travelling up in the tree
     * @param parent a LocaleNode in whose children list exists this node
     */
    void setParent(LocaleNode parent);

    /**
     * Gets the parent of this node.
     * @return a LocaleNode in whose children list exists this node
     */
    LocaleNode getParent();

    /**
     * Adds a child to this node. Each implementation must control which LocaleNode subclasses
     * allows. For instance, a LocaleContainer (which represents a folder) should only contain
     * LocaleContainer and LocaleFile (and subclasses of these) nodes.
     *
     * A single LocaleNode instance can't be added twice as child of a given LocaleNode
     *
     * @param node a LocaleNode that is considered a child of this one
     * @return true if node could be added, false otherwise (mainly, because it already existed
     * as a child)
     */
    boolean addChild(LocaleNode node);

    /**
     * Checks if the LocaleNode passed is a child of this node
     * @param node the LocaleNode to be checked as a child of this node
     * @return true if it is in the children list of this node
     */
    boolean isChild(LocaleNode node);

    /**
     * Checks if a LocaleNode exists in the children list of this node which name property
     * matches the parameter passed, case insensitive
     * @param name the name property that should have the child we are looking for, without
     *             considering the case of the string
     * @return true if a LocaleNode is found with the same name property, case insensitive
     */
    boolean isChild(String name);

    /**
     * Checks if a LocaleNode exists in the children list of this node which name property
     * matches the parameter passed
     * @param name the name property that should have the child we are looking for
     * @param matchCase whether the case of the name property and name parameter must match or not
     * @return true if a LocaleNode is found with the same name property
     */
    boolean isChild(String name, boolean matchCase);

    /**
     * Finds and returns the first LocaleNode child whose name property matches the parameter
     * passed, case insensitive
     * @param name the name property that should have the child we are looking for, without
     *             considering the case of the string
     * @return the first LocaleNode child child whose name property matches the parameter
     * passed, case insensitive
     */
    LocaleNode getChildByName(String name);

    /**
     * Finds and returns the first LocaleNode child whose name property matches the parameter
     * passed
     * @param name the name property that should have the child we are looking for
     * @param matchCase whether the case of the name property and name parameter must match or not
     * @return the first LocaleNode child child whose name property matches the parameter
     * passed
     */
    LocaleNode getChildByName(String name, boolean matchCase);

    /**
     * Finds and removes the first LocaleNode child whose name property matches the parameter
     * passed (case sensitive)
     * @param name the name property that should have the child we are looking for
     * @return the removed LocaleNode, or null if no LocaleNode child could be found with that name
     */
    LocaleNode removeChild(String name);

    /**
     * Finds and removes the passed LocaleNode from the children list of this node
     * @param node the node to be removed from the children list
     * @return true if the node was successfully removed, false otherwise (mainly, because it was not
     * a child of this)
     */
    boolean removeChild(LocaleNode node);

    /**
     * Removes all the children from this node
     * @return true if the list could be fully cleared, false otherwise
     */
    boolean clearChildren();

    /**
     * Sets the "twin" node of this for the default locale of the Product.
     *
     * A Product has a L10n property that represents the default (source) locale for that
     * product. Every node in that locale is considered <i>the master</i> node in the Product,
     * so the remaining locales <i>ab-CD</i> will have their nodes linked to a master node.
     * Still, there will be some exceptions:<br>
     * <ul>
     * <li>a default locale file due to Productization (P12n) rules may not have equivalent node
     *     (ie.: no other ab-CD file links to it because the locale does not implement it)</li>
     * <li>an ab-CD file due to Productization (P12n) rules may have not correspondence with an
     *     master node (ie. does not link to a master node), if approved by Mozilla L10n drivers</li>
     * <li>both above conditions may happen too with individual keys inside some files subjec to
     *     P12n rules.</li>
     * <li>in L20n, there may be entries inside ab-CD files that does not correspond to equivalent
     *     master nodes, due to differences between the grammar of the default locale and the ab-CD
     *     one.</li>
     * </ul>
     * @param twin the LocaleNode instance equivalent of this in the default locale of the Product
     */
    void setDefLocaleTwinId(LocaleNode twin);

    /**
     * Returns the "twin" node of this for the default locale of the Product.
     *
     * A Product has a L10n property that represents the default (source) locale for that
     * product. Every node in that locale is considered <i>the master</i> node in the Product,
     * so the remaining locales <i>ab-CD</i> will have their nodes linked to a master node.
     * Still, there will be some exceptions:<br>
     * <ul>
     * <li>a default locale file due to Productization (P12n) rules may not have equivalent node
     *     (ie.: no other ab-CD file links to it because the locale does not implement it)</li>
     * <li>an ab-CD file due to Productization (P12n) rules may have not correspondence with an
     *     master node (ie. does not link to a master node), if approved by Mozilla L10n drivers</li>
     * <li>both above conditions may happen too with individual keys inside some files subjec to
     *     P12n rules.</li>
     * <li>in L20n, there may be entries inside ab-CD files that does not correspond to equivalent
     *     master nodes, due to differences between the grammar of the default locale and the ab-CD
     *     one.</li>
     * </ul>
     * @return the LocaleNode instance equivalent of this in the default locale of the Product
     */
    LocaleNode getDefLocaleTwin();
}
