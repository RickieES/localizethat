/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.util;

/**
 * This class mimics the definition of org.jdesktop.beansbinding.Converter, so
 * we can remove the dependency on Beans Binding library while retaining
 * compatibility in case we need to readd it again.
 * @author rpalomares
 * @param <S> source type for conversion
 * @param <T> target type for conversion
 */


public abstract class Converter<S, T> {
    /**
     * Converts a value from the source type to the target type. Replicating
     * from original org.jdesktop.beansbinding.Converter class, this method
     * implementation could throw a RuntimeException if there is a problem
     * during the conversion.
     * @param value source type
     * @return target type form of source value
     */
    public abstract T convertForward(S value);
    
    /**
     * Convert a value from the target type to the source type. Replicating
     * from original org.jdesktop.beansbinding.Converter class, this method
     * implementation could throw a RuntimeException if there is a problem
     * during the conversion.
     * @param value target type
     * @return source type form of target value
     */
    public abstract S convertReverse(T value);
}
