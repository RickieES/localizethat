/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.util;

/**
 * Object representing a version of the application.
 * @author rpalomares
 */
public class VersionObject implements Comparable {
    public static int MAJOR = 0;
    public static int MINOR = 1;
    public static int BUGRELEASE = 2;
    public static int VERSION_CHUNKS = 3;
    private int majorValue;
    private int minorValue;
    private String bugRelease;

    /**
     * payLoad is a container for reference objects thay may be associated to the version
     */
    private Object payLoad;

    public VersionObject(String versionString) {
        String[] parts;

        parts = versionString.split("\\.", VERSION_CHUNKS);
        majorValue = Integer.parseInt(parts[MAJOR]);
        minorValue = Integer.parseInt(parts[MINOR]);
        bugRelease = parts[BUGRELEASE];
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

    public String getBugRelease() {
        return bugRelease;
    }

    public void setBugRelease(String bugRelease) {
        this.bugRelease = bugRelease;
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
        stringVersion.append(getBugRelease());
        return stringVersion.toString();
    }

    @Override
    public int compareTo(Object o) throws NullPointerException, ClassCastException {

        if (o == null) {
            throw new NullPointerException(
                    "The passed parameter to this VersionObject.compareTo method invocation is null");
        }

        if (!(o instanceof VersionObject)) {
            throw new ClassCastException(
                    "The passed parameter to this VersionObject.compareTo method invocation is not of VersionObject type");
        }

        VersionObject v2 = (VersionObject) o;

        if (this.getMajorValue() == v2.getMajorValue()) {
            if (this.getMinorValue() == v2.getMinorValue()) {
                return this.getBugRelease().compareTo(v2.getBugRelease());
            } else {
                return (this.getMinorValue() - v2.getMinorValue());
            }
        } else {
            return (this.getMajorValue() - v2.getMajorValue());
        }
    }
}
