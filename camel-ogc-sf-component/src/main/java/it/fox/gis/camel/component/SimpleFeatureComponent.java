package it.fox.gis.camel.component;

import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.PropertyConfigurer;
import org.apache.camel.support.DefaultComponent;

/**
 * Component implementation allowing to perform CRUD operations over a {@link
 * org.geotools.data.DataStore} in a camel context
 */
public class SimpleFeatureComponent extends DefaultComponent {

    private DefaultResourceRegistry registry;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters)
            throws Exception {
        // if we don't have spring managing this let's use the default resgistry.
        registry = DefaultResourceRegistry.getInstance();
        SimpleFeaturesEndpoint endpoint = new SimpleFeaturesEndpoint(registry, remaining, this);
        return endpoint;
    }

    @Override
    public PropertyConfigurer getEndpointPropertyConfigurer() {
        return new SimpleFeaturePropertyConfigurer();
    }

    /**
     * Allows setting of a registry different from the default one.
     *
     * @param registry a registry istance.
     */
    public void setRegistry(DefaultResourceRegistry registry) {
        this.registry = registry;
    }
}
