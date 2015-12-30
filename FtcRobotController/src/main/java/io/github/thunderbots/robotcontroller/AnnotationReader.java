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

public class AnnotationReader {

    public static boolean isActive(Class<? extends com.qualcomm.robotcore.eventloop.opmode.OpMode> c) {
        return c.isAnnotationPresent(Active.class);
    }

    public static String getOpModeName(Class<? extends com.qualcomm.robotcore.eventloop.opmode.OpMode> c) {
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
