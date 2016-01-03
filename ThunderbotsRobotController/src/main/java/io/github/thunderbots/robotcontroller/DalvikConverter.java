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
     * The maximum allowed (major) Java compiler version.
     */
    private static final int MAX_JAR_VERSION = 51;

    /**
     * The number of bytes that should be read from the beginning of each class file in order to
     * verify bytecode version compatibility. The actual version code of the class will be in the
     * last two bytes of the chunk.
     */
    private static final int CHUNK_LENGTH = 8;

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

    /**
     * Converts the given input file to a dalvik-compatible jar file, then returns the corresponding
     * output file. If, for any reason, the file cannot be converted, then {@code null} is returned.
     * All other consequences of the failure will be handled by this method.
     *
     * @param jar the file to convert.
     * @return the converted jar file, or {@code null} if the file cannot be converted.
     */
    public static File convertJar(File jar) {
        if (!isJarConvertable(jar)) {
            return null;
        }
        File output = getOutputFile(jar);
        String[] args = {
                "--dex",
                "--output=" + output.getAbsolutePath(),
                jar.getAbsolutePath(),
        };
        // TODO: modify command-line arguments to work with spaced filenames
        Main.main(args);
        return output;
    }

    /**
     * Determines if the given jar file can be converted to a dalvik-compatible jar. The Java
     * compiler version of each class file will be checked, and any found issues will be logged.
     *
     * @param jar the jarj file to check.
     * @return {@code true} if the jar file can be converted, or {@code false} if it cannot be
     * converted.
     */
    public static boolean isJarConvertable(File jar) {
        try {
            if (checkJarVersion(jar)) {
                return true;
            } else {
                ThunderLog.i(jar.getName()
                        + " uses an incompatible version of Java, and cannot be loaded");
                return false;
            }
        } catch (IOException e) {
            ThunderLog.e("There was an error while checking the version information for "
                    + jar.getName() + ". It will not be loaded.");
            return false;
        }
    }

    /**
     * Checks that the given jar uses a compatible version of Java. The bytecode compliance level
     * is extracted from the header bytes of every class file, and this version is compared against
     * the maximum supported version, as specified by {@link #MAX_JAR_VERSION}.
     *
     * @param file the jar file to check.
     * @return {@code true} if the jar is entirely compatible, or {@code false} if one or more class
     * files in the jar use an incompatible version of Java.
     * @throws IOException if there are any errors while reading the class files, or if the file
     * does not exist.
     */
    public static boolean checkJarVersion(File file) throws IOException {
        ZipInputStream zip = getZipStream(file);
        ZipEntry entry = zip.getNextEntry();
        while (entry != null) {
            if (entry.getName().toLowerCase().endsWith(".class")) {
                byte[] chunk = new byte[CHUNK_LENGTH];
                if (zip.read(chunk) != CHUNK_LENGTH) {
                    throw new IOException();
                }
                int version = ((chunk[chunk.length - 2]) << 8) + (chunk[chunk.length - 1]);
                if (version > DalvikConverter.MAX_JAR_VERSION) {
                    return false;
                }
            }
            entry = zip.getNextEntry();
        }
        return true;
    }

    /**
     * Constructs a {@code ZipInputStream} that represents the given file.
     *
     * @param file the file to represent as a zip input stream.
     * @return a constructed zip input stream.
     * @throws FileNotFoundException if the file does not exist.
     */
    public static ZipInputStream getZipStream(File file) throws FileNotFoundException {
        return new ZipInputStream(new FileInputStream(file));
    }

}
