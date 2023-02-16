package it.fox.gis.camel.component;

import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.PropertyConfigurer;
import org.apache.camel.support.DefaultComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Component implementation allowing to perform CRUD operations over a {@link
 * org.geotools.data.DataStore} in a camel context
 */
@Component("ogc-sf")
public class SimpleFeatureComponent extends DefaultComponent {

    @Autowired(required = false)
    private DefaultResourceRegistry registry;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters)
            throws Exception {
        // if we don't have spring managing this let's use the default resgistry.
        if (registry == null) registry = DefaultResourceRegistry.getInstance();
        SimpleFeaturesEndpoint endpoint = new SimpleFeaturesEndpoint(registry, remaining, this);
        return endpoint;
    }

    @Override
    public PropertyConfigurer getEndpointPropertyConfigurer() {
        return new SimpleFeaturePropertyConfigurer();
    }
}
