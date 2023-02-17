package it.fox.gis.camel.dataformats;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.fox.gis.camel.component.BaseTestSupport;
import java.io.IOException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class GeoJSONReadFeatureCollectionTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testReadFeatureCollection() throws InterruptedException, IOException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test");
        endpoint.setExpectedCount(1);
        endpoint.assertIsSatisfied();
        SimpleFeatureCollection result =
                endpoint.getExchanges().get(0).getIn().getBody(SimpleFeatureCollection.class);
        assertEquals(4, result.size());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreReadGeoJSONColl.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store-geojson-read-coll?featureType=ft1&resultType=COLLECTION&repeatCount=1&propertiesURI="
                                + propsURI)
                        .marshal()
                        .custom("geojson")
                        .log("${body}")
                        .unmarshal()
                        .custom("geojson")
                        .to("mock:test");
            }
        };
    }
}
