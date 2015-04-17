/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.text.ParseException;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author rpalomares
 */
public class ParseableFileAdapter extends LocaleFile implements ParseableFile {
    @OneToOne
    @JoinColumn(name = "ID")
    LTLicense fileLicense;

    @Override
    public List<LocaleContent> parse() throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LTLicense getLicense() {
        return fileLicense;
    }

    public void setFileLicense(LTLicense fileLicense) {
        this.fileLicense = fileLicense;
    }

    @Override
    public List<LocaleContent> getLObjectCollection() {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
