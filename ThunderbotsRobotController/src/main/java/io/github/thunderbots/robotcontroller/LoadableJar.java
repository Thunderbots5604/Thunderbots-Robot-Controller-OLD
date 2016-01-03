package io.github.thunderbots.robotcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.github.thunderbots.robotcontroller.logging.ThunderLog;

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
     * The maximum allowed (major) Java compiler version. Currently, all Java code must be compiled
     * with Java 1.7 (major version 51) or earlier.
     */
    private static final int MAX_JAVA_VERSION = 51;

    /**
     * The number of bytes that should be read from the beginning of each class file in order to
     * verify bytecode version compatibility. The actual version code of the class will be in the
     * last two bytes of the chunk.
     */
    private static final int CLASS_CHUNK_LENGTH = 8;

    /**
     * Constructs a new loadable jar that represents the  given file.
     *
     * @param f the file to represent as a loadable jar.
     */
    public LoadableJar(File f) {
        this.file = f;
    }

    /**
     * Converts this jar file to a dalvik-compatible jar file.
     */
    public void convertToDalvik() {
        File outputFile = this.getConvertedFile();
        String[] args = {
                "--dex",
                "--output=" + outputFile.getAbsolutePath(),
                this.file.getAbsolutePath(),
        };
        // TODO: modify command-line arguments to work with spaced filenames
        com.android.dx.command.Main.main(args);
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

    /**
     * Determines if this jar file can be converted to a dalvik-compatible jar. The Java
     * compiler version of each class file will be checked, and any found issues will be logged.
     *
     * @return {@code true} if this file can be converted, or {@code false} if it cannot be
     * converted.
     */
    public boolean isConvertable() {
        try {
            if (this.isValidJavaVersion()) {
                return true;
            } else {
                ThunderLog.i(this.file.getName()
                        + " uses an incompatible version of Java, and cannot be loaded");
                return false;
            }
        } catch (IOException e) {
            ThunderLog.e("There was an error while checking the version information for "
                    + this.file.getName() + ". It will not be loaded.");
            return false;
        }
    }

    /**
     * Checks that this jar uses a compatible version of Java. The bytecode compliance level
     * is extracted from the header bytes of every class file, and this version is compared against
     * the maximum supported version as specified by {@link #MAX_JAVA_VERSION}.
     *
     * @return {@code true} if the jar is entirely compatible, or {@code false} if one or more class
     * files in the jar use an incompatible version of Java.
     * @throws IOException if there are any errors while reading the class files, or if the file
     * does not exist.
     */
    public boolean isValidJavaVersion() throws IOException {
        ZipInputStream zip = this.getZipStream();
        ZipEntry entry = zip.getNextEntry();
        while (entry != null) {
            if (entry.getName().toLowerCase().endsWith(".class")) {
                byte[] chunk = new byte[LoadableJar.CLASS_CHUNK_LENGTH];
                if (zip.read(chunk) != LoadableJar.CLASS_CHUNK_LENGTH) {
                    throw new IOException();
                }
                int version = ((chunk[chunk.length - 2]) << 8) + (chunk[chunk.length - 1]);
                if (version > LoadableJar.MAX_JAVA_VERSION) {
                    return false;
                }
            }
            entry = zip.getNextEntry();
        }
        return true;
    }

    /**
     * Constructs a {@code ZipInputStream} that represents this file.
     *
     * @return a constructed zip input stream.
     * @throws FileNotFoundException if the file does not exist.
     */
    private ZipInputStream getZipStream() throws FileNotFoundException {
        return new ZipInputStream(new FileInputStream(this.file));
    }

}
