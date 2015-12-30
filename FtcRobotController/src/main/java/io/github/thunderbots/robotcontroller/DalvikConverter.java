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
import java.util.LinkedList;
import java.util.List;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.command.Main;

import io.github.thunderbots.robotcontroller.logging.ThunderLog;

public class DalvikConverter {

    private static final String OUTPUT_DIRECTORY = "/compiled/"; // in private files

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
            File output = getOutputFile(jar);
            String[] args = {
                    "--dex",
                    "--output=" + output.getAbsolutePath(),
                    jar.getAbsolutePath(),
            };
            try {
                Main.main(args);
                convertedJars.add(output);
            } catch (ParseException e) {
                ThunderLog.e(jar.getName() + " was probably generated using the wrong compiler!");
                ThunderLog.e("Make sure to use Java 1.6");
            }
        }
        return convertedJars;
    }

    /**
     * Gets the output file that corresponds to any given input file. The name of the output file is
     * identical to the input file, but it will be located in the app's private directory, so that
     * other apps or users will not be able to see it.
     *
     * @param inputFile the input file.
     * @return the output file corresponding to the input file.
     */
    private static File getOutputFile(File inputFile) {
        // TODO: find out how the app responds to files with spaces in the name
        File output = new File(FileLoader.getCacheDirectory(), OUTPUT_DIRECTORY);
        if (output.exists()) {
            output.delete();
        }
        output.mkdirs();
        return new File(output, "compiled-" + inputFile.getName());
    }

}
