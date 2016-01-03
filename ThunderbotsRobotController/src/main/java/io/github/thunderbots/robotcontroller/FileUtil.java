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
     * {@code FileUtil} should not be instantiable.
     */
    private FileUtil() {

    }

    /**
     * Returns the cache directory. If the cache directory has not already been created, it will be
     * created here.
     *
     * @return the cache directory.
     */
    public static File getCacheDirectory() {
        if (FileUtil.cacheFile == null) {
            FileUtil.cacheFile = new File(FtcRobotControllerActivity.getPrivateFilesDirectory(),
                    "/thunderbots/");
            FileUtil.cacheFile.mkdirs();
        }
        return FileUtil.cacheFile;
    }

}
