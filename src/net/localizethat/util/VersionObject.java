/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.util;

/**
 * Object representing a version of the application.
 * 
 * @author rpalomares
 */
public class VersionObject implements Comparable<VersionObject> {
    public static final int MAJOR = 0;
    public static final int MINOR = 1;
    public static final int BUGRELEASE = 2;
    public static final int VERSION_CHUNKS = 3;
    private int majorValue;
    private int minorValue;
    private String bugReleaseValue;

    /**
     * payLoad is a container for reference objects thay may be associated to the
     * version
     */
    private Object payLoad;

    public VersionObject(String versionString) {
        String[] parts;

        parts = versionString.split("\\.", VERSION_CHUNKS);
        majorValue = Integer.parseInt(parts[MAJOR]);
        minorValue = Integer.parseInt(parts[MINOR]);
        bugReleaseValue = parts[BUGRELEASE];
    }

    public int getMajorValue() {
        return majorValue;
    }

    public void setMajorValue(int majorValue) {
        this.majorValue = majorValue;
    }

    public int getMinorValue() {
        return minorValue;
    }

    public void setMinorValue(int minorValue) {
        this.minorValue = minorValue;
    }

    public String getBugReleaseValue() {
        return bugReleaseValue;
    }

    public void setBugReleaseValue(String bugReleaseValue) {
        this.bugReleaseValue = bugReleaseValue;
    }

    public Object getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(Object payLoad) {
        this.payLoad = payLoad;
    }

    @Override
    public String toString() {
        StringBuilder stringVersion = new StringBuilder(10);

        stringVersion.append(getMajorValue());
        stringVersion.append(".");
        stringVersion.append(getMinorValue());
        stringVersion.append(".");
        stringVersion.append(getBugReleaseValue());
        return stringVersion.toString();
    }

    @Override
    public int compareTo(VersionObject v2) {

        if (v2 == null) {
            throw new NullPointerException(
                    "The passed parameter to this VersionObject.compareTo method invocation is null");
        }

        if (this.getMajorValue() == v2.getMajorValue()) {
            if (this.getMinorValue() == v2.getMinorValue()) {
                return this.getBugReleaseValue().compareTo(v2.getBugReleaseValue());
            } else {
                return (this.getMinorValue() - v2.getMinorValue());
            }
        } else {
            return (this.getMajorValue() - v2.getMajorValue());
        }
    }
}
