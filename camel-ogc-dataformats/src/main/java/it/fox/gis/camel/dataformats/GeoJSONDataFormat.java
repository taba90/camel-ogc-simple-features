package it.fox.gis.camel.dataformats;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultDataFormat;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.geojson.GeoJSONReader;
import org.geotools.data.geojson.GeoJSONWriter;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/** Custom formatter to and from GeoJSON. */
public class GeoJSONDataFormat extends DefaultDataFormat {
    private static final String FEATURE = "Feature";
    private static final String COLLECTION = "FeatureCollection";

    private static final int DEFAULT_MARK_LIMIT = 8 * 1024;

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        try (GeoJSONWriter geoJSONWriter = new GeoJSONWriter(stream)) {
            geoJSONWriter.setPrettyPrinting(true);
            if (graph instanceof SimpleFeature) {
                geoJSONWriter.setSingleFeature(true);
                geoJSONWriter.write((SimpleFeature) graph);
            } else if (graph instanceof SimpleFeatureCollection) {
                writeCollection(geoJSONWriter, (SimpleFeatureCollection) graph);
            } else if (graph instanceof List) {
                List<SimpleFeature> features = (List<SimpleFeature>) graph;
                Optional<SimpleFeatureType> feature =
                        features.stream().findAny().map(f -> f.getFeatureType());
                writeCollection(
                        geoJSONWriter, new ListFeatureCollection(feature.orElse(null), features));

            } else {
                throw new RuntimeException(
                        "This data format doesn't support type "
                                + graph.getClass().getSimpleName());
            }
        }
    }

    private void writeCollection(GeoJSONWriter geoJSONWriter, SimpleFeatureCollection collection)
            throws IOException {
        geoJSONWriter.setEncodeFeatureBounds(true);
        geoJSONWriter.setEncodeFeatureCollectionCRS(true);
        geoJSONWriter.writeFeatureCollection(collection);
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        Object result = null;
        if (!stream.markSupported()) stream = new BufferedInputStream(stream);
        stream.mark(DEFAULT_MARK_LIMIT);
        int counter = 0;
        boolean isColl = false;
        boolean isSingle = false;
        while (counter <= 8) {
            byte[] bytes = stream.readNBytes(1024);
            String readJsonPortion = new String(bytes);
            if (readJsonPortion.contains(COLLECTION)) {
                isColl = true;
                break;
            } else if (readJsonPortion.contains(FEATURE)) {
                isSingle = true;
                break;
            }
            counter++;
        }
        stream.reset();

        try (GeoJSONReader reader = new GeoJSONReader(stream)) {
            if (isColl) result = reader.getFeatures();
            else if (isSingle) result = reader.getFeature();
            else {
                // we cannot trust JSON order so let's try to parse as a single feature
                stream.mark(Integer.MAX_VALUE);
                try {
                    result = reader.getFeature();
                } catch (RuntimeException e) {
                    // last try to read as a feature collection
                    stream.reset();
                    result = reader.getFeatures();
                }
            }
            if (result == null) throw new RuntimeException("Unable to read JSON as a GeoJSON.");
            return result;
        }
    }
}
