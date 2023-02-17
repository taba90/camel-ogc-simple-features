package it.fox.gis.camel.dataformats;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.fox.gis.camel.component.BaseTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class GeoJSONReadFeatureTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testSingleFeatureReadGeoJSON()
            throws InterruptedException, JsonProcessingException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test");
        endpoint.setExpectedCount(1);
        endpoint.assertIsSatisfied();
        SimpleFeature sf = endpoint.getExchanges().get(0).getIn().getBody(SimpleFeature.class);
        assertFeature(sf);
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreReadGeoJSON.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store-geojson-read-single?featureType=ft1&resultType=STREAM&repeatCount=1&cqlQuery=stringProperty='zero'&propertiesURI="
                                + propsURI)
                        .marshal()
                        .custom("geojson")
                        .unmarshal()
                        .custom("geojson")
                        .log("${body}")
                        .to("mock:test");
            }
        };
    }
}
