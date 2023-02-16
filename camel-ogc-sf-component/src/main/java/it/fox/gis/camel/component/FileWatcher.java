package it.fox.gis.camel.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class allows to load data from a file converting it to the desired type and tracks
 * modifications to the file in order to allow for autorefresh. Subclass must implement the method
 * {@link FileWatcher#convert(InputStream)} to return the desired type.
 *
 * @param <T>
 */
public abstract class FileWatcher<T> {

    protected File file;
    protected long lastModified = Long.MIN_VALUE;
    protected long lastCheck;
    protected boolean stale;

    public FileWatcher(File file) {
        if (!file.exists())
            throw new RuntimeException("The file " + file.getAbsolutePath() + " doesn't exist...");
        this.file = file;
    }

    /**
     * Reads the file updating the last check timestamp and returning an istance of type T.
     *
     * @return the file content as a T instance.
     */
    public T read() throws IOException {
        T result = null;

        try (InputStream is = new FileInputStream(file)) {
            result = convert(is);

            lastModified = file.lastModified();
            lastCheck = System.currentTimeMillis();
            stale = false;
        }

        return result;
    }

    /** Convert the contents of the file to the defined T type. */
    protected abstract T convert(InputStream in) throws IOException;

    /** Determines if the underlying file has been modified since the last check. */
    public boolean isModified() {
        long now = System.currentTimeMillis();
        if ((now - lastCheck) > 1000) {
            lastCheck = now;
            stale = file.lastModified() != lastModified;
        }
        return stale;
    }
}
