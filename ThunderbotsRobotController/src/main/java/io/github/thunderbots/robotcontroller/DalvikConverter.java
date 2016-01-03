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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.android.dx.command.Main;

import io.github.thunderbots.robotcontroller.logging.ThunderLog;

/**
 * {@code DalvikConverter} is responsible for converting standard Java JAR files to
 * Dalvik-Compatible JAR files. This conversion is necessary before the class loader is able to
 * access any of the code in the JAR files.
 *
 * @author Zach Ohara
 */
public class DalvikConverter {

    /**
     * Converts the jar files in the given list to dalvik-compatible jar files, and returns a list
     * of the converted files.
     *
     * @param jarList the list of jar files to convert.
     * @return the list of converted jar files.
     */
    public static List<File> convertJars(List<File> jarList) {
        List<File> convertedJars = new LinkedList<File>();
        for (File jar : jarList) {
            File converted = DalvikConverter.convertJar(jar);
            if (converted != null) {
                convertedJars.add(converted);
            }
        }
        return convertedJars;
    }

}
