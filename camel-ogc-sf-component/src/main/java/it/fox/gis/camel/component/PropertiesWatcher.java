package it.fox.gis.camel.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** A FileWatcher implementation for {@link Properties} file. */
public class PropertiesWatcher extends FileWatcher<Properties> {
    public PropertiesWatcher(File file) {
        super(file);
    }

    @Override
    protected Properties convert(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.load(in);
        return properties;
    }

    /**
     * Read and convert the {@link Properties} instance to a {@link Map}.
     *
     * @return a {@link Map<String,Object>} from the {@link Properties}.
     * @throws IOException
     */
    public Map<String, Object> readAsMap() throws IOException {
        Properties properties = read();
        Map<String, Object> result = new HashMap<>(properties.size());
        for (Object o : properties.keySet()) {
            if (o != null) result.put(o.toString(), properties.getProperty(o.toString()));
        }
        return result;
    }
}
