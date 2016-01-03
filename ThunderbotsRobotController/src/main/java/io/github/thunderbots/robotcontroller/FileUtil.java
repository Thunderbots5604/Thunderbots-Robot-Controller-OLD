package io.github.thunderbots.robotcontroller;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;

import java.io.File;

/**
 *
 */
public class FileUtil {

    /**
     * The cache directory. This is not a true cache directory, but instead is a directory
     * in the application's private storage that is used to store temporary files.
     */
    private static File cacheFile;

    /**
     * The cache directory, inside the app's private cache file, that will contain all the
     * Dalvik-converted JAR files.
     */
    private static File jarCacheFile;

    /**
     * The sub-directory, inside the app's private cache file, that will contain all the
     * Dalvik-converted JAR files.
     */
    private static final String JAR_CACHE_DIRECTORY = "/converted/";

    /**
     * {@code FileUtil} should not be instantiable.
     */
    private FileUtil() {

    }

    /**
     * Returns the cache directory. If the cache directory has not already been created, it will be
     * created here.
     *
     * @return the cache directory.
     * @see #cacheFile
     */
    public static File getCacheDirectory() {
        if (FileUtil.cacheFile == null) {
            FileUtil.cacheFile = new File(FtcRobotControllerActivity.getPrivateFilesDirectory(),
                    "/thunderbots/");
            if (FileUtil.cacheFile.exists()) {
                FileUtil.cacheFile.delete();
            }
            FileUtil.cacheFile.mkdirs();
        }
        return FileUtil.cacheFile;
    }

    /**
     * Returns the jar cache directory. If the directory has not already been created, it will be
     * created here.
     *
     * @return the jar cache directory.
     * @see  #jarCacheFile
     */
    public static File getJarCacheDirectory() {
        if (FileUtil.jarCacheFile == null) {
            FileUtil.jarCacheFile = new File(FileUtil.getCacheDirectory(), FileUtil.JAR_CACHE_DIRECTORY);
        }
        return FileUtil.jarCacheFile;
    }

}
