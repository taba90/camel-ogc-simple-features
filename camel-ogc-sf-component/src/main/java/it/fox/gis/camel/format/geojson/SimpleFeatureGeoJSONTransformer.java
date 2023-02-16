package it.fox.gis.camel.format.geojson;

import java.io.IOException;
import org.geotools.data.geojson.GeoJSONReader;
import org.geotools.data.geojson.GeoJSONWriter;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Class allow conversions of Simple Features and Collections to and from GeoJSON. */
public class SimpleFeatureGeoJSONTransformer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(SimpleFeatureGeoJSONTransformer.class);

    /**
     * Convert a {@link SimpleFeature} instance to its GeoJSON representation.
     *
     * @param simpleFeature a Simple Feature.
     * @return the GeoJSON representation of a Simple Feature.
     */
    public String toGeoJSON(SimpleFeature simpleFeature) {
        return GeoJSONWriter.toGeoJSON(simpleFeature);
    }

    /**
     * Convert a {@link SimpleFeatureCollection} to its GeoJSON representation.
     *
     * @param collection the Simple Feature collection.
     * @return the GeoJSON representation of the SimpleFeature collection.
     */
    public String toGeoJSON(SimpleFeatureCollection collection) {
        return GeoJSONWriter.toGeoJSON(collection);
    }

    /**
     * Convert a GeoJSON representation of a Simple Feature to a {@link SimpleFeature} instance.
     *
     * @param json the GeoJSON.
     * @return the {@link SimpleFeature} instance.
     */
    public SimpleFeature toFeature(String json) {
        try {
            return GeoJSONReader.parseFeature(json);
        } catch (IOException e) {
            LOGGER.error(
                    "Error while transforming a geo JSON feature to a SimpleFeature object. Error is "
                            + e.getMessage(),
                    e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert a GeoJSON features collection to a {@link SimpleFeatureCollection} instance.
     *
     * @param json the GeoJSON.
     * @return the {@link SimpleFeatureCollection} instance.
     */
    public SimpleFeatureCollection toCollection(String json) {
        return GeoJSONReader.parseFeatureCollection(json);
    }
}
