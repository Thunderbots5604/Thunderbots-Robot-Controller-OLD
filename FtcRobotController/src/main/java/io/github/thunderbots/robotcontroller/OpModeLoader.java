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

public class OpModeLoader {

    private ClassLoader classLoader;
    private List<File> fileList;
    private List<Class<? extends OpMode>> opModeList;

    public OpModeLoader(List<File> fileList) {
        this.fileList = fileList;
        List<URL> jarList = FileLoader.getUrlList(this.fileList);
        this.classLoader = getClassLoader(jarList);
        Thread.currentThread().setContextClassLoader(this.classLoader);
    }

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

    private void loadClass(Class<?> c) {
        loadOpMode(c);
        for (Class<?> subclass : c.getDeclaredClasses()) {
            loadClass(subclass);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadOpMode(Class<?> c) {
        if (OpMode.class.isAssignableFrom(c) && OpModeLoader.isInstantiable(c)) {
            this.opModeList.add((Class<? extends OpMode>) c);
        }
    }

    private static boolean isInstantiable(Class<?> c) {
        try {
            c.newInstance();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private ClassLoader getClassLoader(List<URL> jarList) {
        String pathString = getDelimitedPathString(jarList);
        String cacheDir = FileLoader.getCacheDirectory().toString();
        ClassLoader parentLoader = this.getClass().getClassLoader();
        return new DexClassLoader(pathString, cacheDir, null, parentLoader);
    }

    private static String getDelimitedPathString(List<?> list) {
        String result = "";
        for (Object o : list) {
            result += File.pathSeparator;
            result += o.toString();
        }
        return result.substring(1);
    }

}
