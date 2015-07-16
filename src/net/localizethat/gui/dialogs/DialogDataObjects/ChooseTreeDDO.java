/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.dialogs.DialogDataObjects;

import java.awt.Component;
import java.util.List;
import javax.persistence.EntityManager;
import net.localizethat.gui.dialogs.ChooseTreePanel;
import net.localizethat.model.L10n;
import net.localizethat.model.LocalePath;
import net.localizethat.model.Product;
import net.localizethat.util.gui.DialogDataObject;

/**
 * DataObject used to retrieve L10n selected by user and the source of the tree content
 * to be displayed in a Edit Content Panel
 * @author rpalomares
 */
public class ChooseTreeDDO implements DialogDataObject {
    private L10n locale;
    private List<LocalePath> colPaths;
    private Product product;
    private EntityManager entityManager;

    @Override
    public void transferTo(Component c) {
    }

    @Override
    public void collectFrom(Component c) {
        ChooseTreePanel ctp = (ChooseTreePanel) c;
        this.locale = ctp.getTargetL10n();
        this.product = ctp.getSelectedProduct();
        this.colPaths = ctp.getSelectedPaths();
        this.entityManager = ctp.getEntityManager();
    }

    public L10n getLocale() {
        return locale;
    }

    public List<LocalePath> getColPaths() {
        return colPaths;
    }

    public Product getProduct() {
        return product;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
