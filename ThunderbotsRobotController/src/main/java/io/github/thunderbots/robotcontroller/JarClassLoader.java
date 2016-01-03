package io.github.thunderbots.robotcontroller;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import dalvik.system.DexClassLoader;

public class JarClassLoader {

    /**
     * The class loader that is used to load all classes from the file system.
     */
    private ClassLoader classLoader;

    /**
     * The list of files that will be searched for op modes.
     */
    private List<File> fileList;

    /**
     * Constructs an {@code JarClassLoader} that will load op modes from all of the files in the given
     * list.
     *
     * @param loadableJarList the list of files that will be searched for op modes.
     * @see #fileList
     */
    public JarClassLoader(List<LoadableJar> loadableJarList) {
        this.fileList = new LinkedList<File>();
        for (LoadableJar jar : loadableJarList) {
            this.fileList.add(jar.getFile());
        }
        List<URL> jarList = FileLoader.getUrlList(this.fileList);
        this.constructClassLoader(jarList);
        Thread.currentThread().setContextClassLoader(this.classLoader);
    }

    /**
     * Gets the constructed class loader.
     *
     * @return the constructed class loader.
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * Constructs the class loader that will be used to load all classes from the JAR files.
     *
     * @param jarList the list of jars that will be added to the path string of the class loader.
     */
    private void constructClassLoader(List<URL> jarList) {
        String pathString = getDelimitedPathString(jarList);
        String cacheDir = FileUtil.getCacheDirectory().toString();
        ClassLoader parentLoader = this.getClass().getClassLoader();
        this.classLoader = new DexClassLoader(pathString, cacheDir, null, parentLoader);
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
