package it.fox.gis.camel.component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.StampedLock;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.util.SoftValueHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a {@link ResourceRegistry} that uses {@link SoftValueHashMap} to cache
 * DataStore instances. The caches are actuallly two
 */
class DefaultResourceRegistry implements ResourceRegistry {

    private SoftValueHashMap<String, DataStore> storeCache;

    private SoftValueHashMap<String, PropertiesWatcher> propertiesCache;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResourceRegistry.class);

    private StampedLock lock = new StampedLock();

    DefaultResourceRegistry() {
        this.storeCache = new SoftValueHashMap<>(0);
        this.propertiesCache = new SoftValueHashMap<>(0);
    }

    @Override
    public DataStore loadDataStore(String name, String propertiesURI) {
        addProperties(name, propertiesURI);
        long stamp = lock.readLock();
        try {
            PropertiesWatcher propertiesWatcher = propertiesCache.get(name);
            DataStore dataStore = storeCache.get(name);
            if (dataStore == null || propertiesWatcher.isModified()) {
                stamp = toWriteLock(stamp);
                if (dataStore == null || propertiesWatcher.isModified()) {
                    try {
                        dataStore = DataStoreFinder.getDataStore(propertiesWatcher.readAsMap());
                        storeCache.put(name, dataStore);
                    } catch (IOException e) {
                        String message =
                                String.format(
                                        "Error while retrieving the datastore for name %s", name);
                        LOGGER.error(message, e);
                        throw new RuntimeException(message, e);
                    }
                }
            }
            return dataStore;
        } finally {
            lock.unlock(stamp);
        }
    }

    private void addProperties(String name, String fileURI) {
        long stamp = lock.readLock();
        try {
            if (!propertiesCache.containsKey(name)) {
                stamp = toWriteLock(stamp);
                if (!propertiesCache.containsKey(name)) {
                    File file = new File(fileURI);
                    PropertiesWatcher propertiesWatcher = new PropertiesWatcher(file);
                    propertiesCache.put(name, propertiesWatcher);
                }
            }
        } finally {
            lock.unlock(stamp);
        }
    }

    private long toWriteLock(long stamp) {
        stamp = lock.tryConvertToWriteLock(stamp);
        if (stamp == 0L) {
            LOGGER.debug("stamp is zero for tryConvertToWriteLock(), so acquiring the write lock");
            stamp = lock.writeLock();
        }
        return stamp;
    }

    static DefaultResourceRegistry getInstance() {
        return ResourceRegistryLoader.INSTANCE;
    }

    private static class ResourceRegistryLoader {
        private static final DefaultResourceRegistry INSTANCE = new DefaultResourceRegistry();
    }
}
