package io.github.thunderbots.robotcontroller;

import java.io.File;

/**
 * A loadable jar represents a jar file that op modes or other usable code can be extracted
 * from.
 *
 * @author Zach Ohara
 */
public class LoadableJar {

    /**
     * The file address of this jar.
     */
    private File file;

    /**
     * Constructs a new loadable jar that represents the  given file.
     *
     * @param f the file to represent as a loadable jar.
     */
    public LoadableJar(File f) {
        this.file = f;
    }

    /**
     * Gets the output file that corresponds to this file. The name of the output file is
     * identical to this file, but it will be located in the app's private directory, so that
     * other apps or users will not be able to see it.
     *
     * @return the corresponding output file.
     */
    private File getConvertedFile() {
        return new File(FileUtil.getJarCacheDirectory(), this.file.getName() + "_converted");
    }

}
