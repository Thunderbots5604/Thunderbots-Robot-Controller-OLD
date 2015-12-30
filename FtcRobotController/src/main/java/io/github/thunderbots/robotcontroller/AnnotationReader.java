/* Copyright (C) 2015-2016 Thunderbots Robotics
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.thunderbots.robotcontroller;

import java.lang.annotation.Annotation;

import io.github.thunderbots.lightning.annotation.Active;
import io.github.thunderbots.lightning.annotation.OpMode;

/**
 * {@code AnnotationReader} is resonsible for reading the annotations contained in loaded classes.
 *
 * @author Pranav Mathur
 */
public class AnnotationReader {

    /**
     * Determines if the given class has an {@code Active} annotation.
     *
     * @param c the class to check for an annotation.
     * @return {@code true} if the given class has an {@code Active} annotation; {@code false}
     * otherwise.
     * @see io.github.thunderbots.lightning.annotation.Active
     */
    public static boolean isActive(Class<?> c) {
        return c.isAnnotationPresent(Active.class);
    }

    /**
     * Determines the name of any op mode, as specified by its {@code OpMode} annotation. If no
     * annotation is present, or the name is not defined in the annotation, then the simple name of
     * the class (the name without any package information) is returned instead.
     *
     * @param c the op mode to get the name of.
     * @return the name of the op mode.
     * @see io.github.thunderbots.lightning.annotation.OpMode
     */
    public static String getOpModeName(Class<?> c) {
        Annotation[] annotations = c.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof OpMode) {
                String name = ((OpMode) annotation).name();
                if (name != null && !name.equals("")) {
                    return name;
                }
            }
        }
        return c.getSimpleName();
    }

}
