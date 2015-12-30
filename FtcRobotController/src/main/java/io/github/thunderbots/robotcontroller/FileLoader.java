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

import android.os.Environment;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import io.github.thunderbots.robotcontroller.logging.ThunderLog;

public class FileLoader {

    public static final String FILE_LOCATION = "FIRST"; // on the SD card

    /**
     * Constructs a list of URL's from the given list of Files. If any given file cannot be
     * converted to a URL, the exception will be caught and logged, and the method will continue
     * to convert subsequent files.
     *
     * @param fileList the list of files to convert to URL's.
     * @return a list of URL's.
     */
    public static List<URL> getUrlList(List<File> fileList) {
        List<URL> urlList = new LinkedList<URL>();
        for (File f : fileList) {
            try {
                urlList.add(f.getAbsoluteFile().toURI().toURL());
            } catch (MalformedURLException e) {
                ThunderLog.e("Cannot convert a file to URL:");
                e.printStackTrace();
            }
        }
        return urlList;
    }

    /**
     * Builds a list of all the JAR files that exist within the target directory. The list is built
     * in the {@link #getFileSet()} method, and then all the non-JAR files are removed from the list.
     *
     * @return a list of the JAR files in the target directory.
     */
    public static List<File> getJarList() {
        List<File> fileList = getFileSet();
        filterForType(fileList, "jar");
        return fileList;
    }

    /**
     * Filter out all items from a file list that are not the given type.
     *
     * @param fileList the list of files to filter.
     * @param filetype the file type to isolate.
     */
    private static void filterForType(List<File> fileList, String filetype) {
        if (!filetype.startsWith(".")) {
            filetype = "." + filetype;
        }
        for (int i = fileList.size() - 1; i >= 0; i--) {
            if (!fileList.get(i).getName().toLowerCase().endsWith(filetype)) {
                fileList.remove(i);
            }
        }
    }

    /**
     * Builds a list of every file that exists within the target directory. This method essentially
     * acts as a delegate to {@link #getFilesInDirectory(File)} with {@link #getTargetDirectory()}
     * as an argument.
     *
     * @return a list of every file that exists within the target directory.
     * @see #getFilesInDirectory(File)
     * @see #getTargetDirectory()
     */
    public static List<File> getFileSet() {
        return getFilesInDirectory(getTargetDirectory());
    }

    /**
     * Recursively finds all the files in a given base directory. This method essentially
     * acts as a delegate to {@link #getFilesInDirectory(File, List)}
     *
     * @param baseDirectory the directory to search for files and subdirectories.
     * @return the list of all files in the base directory.
     * @see #getFilesInDirectory(File, List)
     */
    private static List<File> getFilesInDirectory(File baseDirectory) {
        return getFilesInDirectory(baseDirectory, new LinkedList<File>());
    }

    /**
     * Recursively finds all the files in a given base directory, and adds them to the given list.
     *
     * @param baseDirectory the directory to search for files and subdirectories.
     * @param foundFiles the list of all files that have been found.
     * @return the list of all files in the base directory.
     */
    private static List<File> getFilesInDirectory(File baseDirectory, List<File> foundFiles) {
        for (File f : baseDirectory.listFiles()) {
            if (f.isFile()) {
                foundFiles.add(f);
            } else if (f.isDirectory()) {
                getFilesInDirectory(f, foundFiles);
            }
        }
        return foundFiles;
    }

    /**
     * Returns the directory that should be searched for jar files.
     *
     * @return the base directory for all jar files.
     */
    private static File getTargetDirectory() {
        File sdcard = Environment.getExternalStorageDirectory();
        return new File(sdcard, FileLoader.FILE_LOCATION);
    }

    /**
     * Returns the cache directory. This is not a true cache directory, but instead is a directory
     * in the application's private storage that is used to store temporary files.
     *
     * @return the cache directory.
     */
    public static File getCacheDirectory() {
        File cache = new File(FtcRobotControllerActivity.getPrivateFilesDirectory(), "/thunderbots/");
        cache.mkdirs();
        return cache;
    }

}
