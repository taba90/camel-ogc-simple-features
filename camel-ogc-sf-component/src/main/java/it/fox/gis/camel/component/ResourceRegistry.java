package it.fox.gis.camel.component;

import org.geotools.data.DataStore;

/**
 * Basic interface for a ResourceRegistry. A resource registry should be a singleton that loads a
 * geotools {@link DataStore} from a cache or creates it.
 */
public interface ResourceRegistry {

    /**
     * Loads a {@link DataStore}.
     *
     * @param name the name identifying the datastore.
     * @param propertiesURI the URI to the properties file containing connections parameters to the
     *     store.
     * @return a {@link DataStore} instance
     */
    DataStore loadDataStore(String name, String propertiesURI);
}
