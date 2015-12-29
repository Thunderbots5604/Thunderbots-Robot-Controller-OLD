package io.github.thunderbots.robotcontroller;

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

    private ClassLoader classLoader;
    private List<Class<? extends OpMode>> opModeList;

    public List<Class<? extends OpMode>> loadJars(List<File> fileList) {
        // TODO: call initializeClassLoader before this method

        this.opModeList = new ArrayList<Class<? extends OpMode>>();
        for (File f : fileList) {
            loadJarFile(f);
        }


        URL[] jarURLs = getJarURLs(fileList);
        classLoader = getClassLoader(jarURLs);
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

    private void initializeClassLoader(List<File> fileList) {
        List<URL> jarList = FileLoader.getUrlList(fileList);
        this.classLoader = getClassLoader(jarList);
        Thread.currentThread().setContextClassLoader(this.classLoader);
    }

    private ClassLoader getClassLoader(List<URL> jarList) {
        String pathString = getDelimitedPathString(jarList);
        File cacheFile = new File(FtcRobotControllerActivity.getPrivateFilesDirectory(), "/thunderbots/");
        cacheFile.mkdirs();
        String cacheDir = cacheFile.toString();
        ClassLoader parentLoader = this.getClass().getClassLoader();
        return new DexClassLoader(pathString, cacheDir, null, parentLoader);
    }

    private static String getDelimitedPathString(List<? extends Object> list) {
        String result = "";
        for (Object o : list) {
            result += File.pathSeparator;
            result += o.toString();
        }
        return result.substring(1);
    }


    // everything below is unorganized


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

}
