package it.fox.gis.camel.component;

import java.util.List;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.ScheduledPollEndpoint;
import org.geotools.data.Query;
import org.opengis.filter.expression.PropertyName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/** A SimpleFeature endpoint supporting both consuming and producing. */
@UriEndpoint(
        firstVersion = "1.2.0",
        scheme = "ogc-sf",
        title = "OGC-SimpleFeatures",
        syntax = "ogc-sfs:dataStoreName")
public class SimpleFeaturesEndpoint extends ScheduledPollEndpoint {

    private ResourceRegistry registry;

    @UriPath private String propertiesURI;

    @UriPath
    @Metadata(required = true)
    private String dataStoreName;

    @UriParam
    @Metadata(defaultValue = "EPSG:4326")
    private CoordinateReferenceSystem crs;

    @UriParam
    @Metadata(required = true)
    private String featureType;

    @UriParam
    @Metadata(defaultValue = "INCLUDE")
    private Query cqlQuery;

    @UriParam
    @Metadata(defaultValue = "LIST")
    private ResultType resultType;

    @UriParam
    @Metadata(defaultValue = "GET")
    private Operation operation;

    @UriParam private List<PropertyName> properties;

    public SimpleFeaturesEndpoint(
            ResourceRegistry registry, String dataStoreName, SimpleFeatureComponent component) {
        setComponent(component);
        this.registry = registry;
        this.dataStoreName = dataStoreName;
        this.operation = Operation.GET;
        this.resultType = ResultType.LIST;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new SimpleFeatureProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        SimpleFeatureConsumer consumer = new SimpleFeatureConsumer(this, processor);
        consumer.setDelay(getDelay());
        consumer.setRepeatCount(getRepeatCount());
        consumer.setInitialDelay(getInitialDelay());
        consumer.setGreedy(isGreedy());
        return consumer;
    }

    public String getDataStoreName() {
        return dataStoreName;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getPropertiesURI() {
        return propertiesURI;
    }

    public void setPropertiesURI(String propertiesURI) {
        this.propertiesURI = propertiesURI;
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public Query getCqlQuery() {
        return cqlQuery;
    }

    public void setCqlQuery(Query cqlQuery) {
        this.cqlQuery = cqlQuery;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public ResourceRegistry getRegistry() {
        return registry;
    }

    public List<PropertyName> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyName> properties) {
        this.properties = properties;
    }

    @Override
    public String getEndpointUri() {
        String path = dataStoreName != null ? "//:" + dataStoreName : "";
        return "ogc-sf" + path;
    }
}
