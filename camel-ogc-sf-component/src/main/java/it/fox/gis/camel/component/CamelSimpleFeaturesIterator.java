package it.fox.gis.camel.component;

import java.util.Iterator;
import java.util.function.Consumer;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.collection.DecoratingSimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A {@link SimpleFeatureIterator} that implements the {@link java.util.Iterator} interface to allow
 * split EIP to work fine with it.
 */
class CamelSimpleFeaturesIterator extends DecoratingSimpleFeatureIterator
        implements Iterator<SimpleFeature> {
    CamelSimpleFeaturesIterator(SimpleFeatureIterator delegate) {
        super(delegate);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove from a SimpleFeature Iterator");
    }

    @Override
    public void forEachRemaining(Consumer<? super SimpleFeature> action) {
        Iterator.super.forEachRemaining(action);
    }
}
