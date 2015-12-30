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

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import io.github.thunderbots.robotcontroller.logging.ThunderLog;

/**
 * {@code OpModeLoader} is responsible for extracting valid op modes from a list of JAR files. The
 * files must have already been converted to Dalvik-compatible files.
 *
 * @author Zach Ohara
 */
public class OpModeLoader {

    /**
     * The class loader that is used to load all classes from the file system.
     */
    private ClassLoader classLoader;

    /**
     * The list of files that will be searched for op modes.
     */
    private List<File> fileList;

    /**
     * The list of op modes that can be found inside any of the searched files.
     */
    private List<Class<? extends OpMode>> opModeList;

    /**
     * Constructs an {@code OpModeLoader} that will load op modes from all of the files in the given
     * list.
     *
     * @param fileList the list of files that will be searched for op modes.
     * @see #fileList
     */
    public OpModeLoader(List<File> fileList) {
        this.fileList = fileList;
        List<URL> jarList = FileLoader.getUrlList(this.fileList);
        this.classLoader = getClassLoader(jarList);
        Thread.currentThread().setContextClassLoader(this.classLoader);
    }

    /**
     * Gets a list of all op modes inside any of the searched files.
     *
     * @return all op modes found in the searched files.
     */
    public List<Class<? extends OpMode>> getOpModes() {
        this.opModeList = new ArrayList<Class<? extends OpMode>>();
        for (File f : this.fileList) {
            try {
                this.loadJar(f);
            } catch (IOException e) {
                ThunderLog.e(f.getAbsolutePath() + " cannot be opened");
                e.printStackTrace();
            }
        }
        return this.opModeList;
    }

    /**
     * Loads the given JAR file and searches it for valid op modes.
     *
     * @param jarFile the file to search for op modes.
     * @throws IOException if the file cannot be opened.
     */
    private void loadJar(File jarFile) throws IOException {
        File cache = new File(FileLoader.getCacheDirectory() + "/temp/");
        DexFile jar = DexFile.loadDex(jarFile.getAbsolutePath(), cache.getAbsolutePath(), 0);
        Enumeration<String> entries = jar.entries();
        while(entries.hasMoreElements()) {
            String entry  = entries.nextElement();
            try {
                Class<?> c = this.classLoader.loadClass(entry);
                this.loadClass(c);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        jar.close();
    }

    /**
     * Loads a given class and searches it for any valid op modes. Nested classes will also be
     * searched recursively.
     *
     * @param c the class to search for valid op modes.
     */
    private void loadClass(Class<?> c) {
        loadOpMode(c);
        for (Class<?> subclass : c.getDeclaredClasses()) {
            loadClass(subclass);
        }
    }

    /**
     * Attempts to load an op mode from the given class. If the given class is not an op mode, or
     * is not instantiable, then this method returns without any action being taken. If the given
     * class is found to be a valid op mode (it extends {@code OpMode} and is instantiable), then
     * it is added to to the op mode list.
     *
     * @param c the class to attempt to load an op mode from.
     * @see #opModeList
     */
    @SuppressWarnings("unchecked")
    private void loadOpMode(Class<?> c) {
        if (OpMode.class.isAssignableFrom(c) && OpModeLoader.isInstantiable(c)) {
            this.opModeList.add((Class<? extends OpMode>) c);
        }
    }

    /**
     * Determines if a given class is legally instantiable.
     *
     * @param c the class to check for instantiability.
     * @return {@code true} if the class is instantiable, or {@code false} otherwise.
     */
    private static boolean isInstantiable(Class<?> c) {
        try {
            c.newInstance();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the class loader that will be used to load all classes from the JAR files.
     *
     * @param jarList the list of jars that will be added to the path string of the class loader.
     * @return a constructed class loader.
     */
    private ClassLoader getClassLoader(List<URL> jarList) {
        String pathString = getDelimitedPathString(jarList);
        String cacheDir = FileLoader.getCacheDirectory().toString();
        ClassLoader parentLoader = this.getClass().getClassLoader();
        return new DexClassLoader(pathString, cacheDir, null, parentLoader);
    }

    /**
     * Gets a string representation of the given list, with each entry in the list being delimited
     * by the system path separator.
     *
     * @param list the list to convert to a string.
     * @return a string representation of the given list.
     */
    private static String getDelimitedPathString(List<?> list) {
        String result = "";
        for (Object o : list) {
            result += File.pathSeparator;
            result += o.toString();
        }
        return result.substring(1);
    }

}
