package io.github.thunderbots.robotcontroller.fileloader;

import android.os.Environment;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import io.github.thunderbots.robotcontroller.logging.ThunderLog;

public class OpModeClassLoader {

    private static ClassLoader classLoader;
    private static List<Class<? extends OpMode>> opModeList;

    public static final String FILE_LOCATION = "FIRST";

    public static List<Class<? extends OpMode>> loadJars(List<File> fileList) {
        URL[] jarurls = getJarURLs(fileList);
        classLoader = getClassLoader(jarurls);
        Thread.currentThread().setContextClassLoader(classLoader);
        opModeList = new ArrayList<Class<? extends OpMode>>();
        for (File jarfile : fileList) {
            if (jarfile.getName().endsWith(".jar")) {
                try {
                    loadJarFile(jarfile);
                } catch (Exception ignore) {}
            }
        }
        return opModeList;
    }

    private static ClassLoader getClassLoader(URL[] jarurls) {
        String pathString = getDelimitedPathString(jarurls);
        File cacheFile = new File(FtcRobotControllerActivity.getPrivateFilesDirectory(), "/thunderbots/");
        cacheFile.mkdirs();
        String cacheDir = cacheFile.toString();
        ClassLoader parentLoader = OpModeClassLoader.class.getClassLoader();
        return new DexClassLoader(pathString, cacheDir, null, parentLoader);
    }

    private static <T> String getDelimitedPathString(T[] arr) {
        String result = "";
        for (T obj : arr) {
            result += File.pathSeparator;
            result += obj.toString();
        }
        return result.substring(1);
    }

    private static void loadJarFile(File jarfile) throws IOException {
        File cache = new File(FtcRobotControllerActivity.getPrivateFilesDirectory(), "/thunderbots/temp");
        DexFile jarobj = DexFile.loadDex(jarfile.getAbsolutePath(), cache.getAbsolutePath(), 0);
        Enumeration<String> jarentries = jarobj.entries();
        while (jarentries.hasMoreElements()) {
            String entry = jarentries.nextElement();
            try {
                Class<?> c = classLoader.loadClass(entry);
                attemptLoadClass(c);
            }
            catch (Throwable ex) {

            }
        }
        jarobj.close();
    }

    private static void attemptLoadClass(Class<?> c) {
        attemptLoadOpMode(c);
        for (Class<?> i : c.getDeclaredClasses())
            attemptLoadClass(i);
    }

    @SuppressWarnings("unchecked")
    private static void attemptLoadOpMode(Class<?> c) {
        try {
            if (OpMode.class.isAssignableFrom(c) && attemptInstantiate((Class<OpMode>) c)) {
                ThunderLog.i("Found " + c.getSimpleName() + " as an op mode");
                opModeList.add((Class<OpMode>) c);
            }
        } catch (Throwable ignore) {

        }
    }

    private static boolean attemptInstantiate(Class<? extends OpMode> c) {
        try {
            Object instance = c.newInstance();
        } catch (IllegalAccessException ex) {
            return false;
        } catch (InstantiationException ex) {
            return false;
        }
        return true;
    }

    private static URL[] getJarURLs(List<File> fileList) {
        List<URL> jarList = new ArrayList<URL>();
        for (File f : fileList) {
            if (f.isFile() && f.getName().endsWith(".jar")) {
                try {
                    jarList.add(f.getAbsoluteFile().toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return jarList.toArray(new URL[jarList.size()]);
    }

    public static List<File> getFileSet() {
        List<File> fileList = new ArrayList<File>();
        getFilesInDirectory(getBaseDirectory(), fileList);
        return fileList;
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
    private static File getBaseDirectory() {
        File sdcard = Environment.getExternalStorageDirectory();
        return new File(sdcard, FILE_LOCATION);
    }

}
