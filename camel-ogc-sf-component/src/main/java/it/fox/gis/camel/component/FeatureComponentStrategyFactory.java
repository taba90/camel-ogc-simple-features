package it.fox.gis.camel.component;

import org.apache.camel.Consumer;
import org.apache.camel.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A factory for {@link FeatureComponentStrategy}. */
class FeatureComponentStrategyFactory {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(FeatureComponentStrategyFactory.class);

    /**
     * Create a {@link FeatureComponentStrategy} based on the operation and result type.
     *
     * @param operation the {@link Operation} coming from the endpoint.
     * @param resultType the {@link ResultType} coming from the endpoint.
     * @return a {@link FeatureComponentStrategy} matching the passed operation and resultType.
     */
    static FeatureComponentStrategy createStrategy(
            Service service, Operation operation, ResultType resultType) {
        FeatureComponentStrategy strategy;
        switch (operation) {
            case GET:
                strategy = strategyForGET(resultType, service);
                break;
            case ADD:
                strategy = new AddStrategy(service);
                break;
            case DELETE:
                strategy = new DeleteStrategy(service);
                break;
            default:
                throw new RuntimeException("No suitable strategy found for op:" + operation.name());
        }
        return strategy;
    }

    private static FeatureComponentStrategy strategyForGET(ResultType resultType, Service service) {
        FeatureComponentStrategy strategy;
        switch (resultType) {
            case COLLECTION:
                strategy = new FeatureCollectionStrategy(service);
                break;
            case ITERATOR:
                strategy = new FeatureIteratorStrategy(service);
                break;
            case LIST:
                strategy = new FeatureListStrategy(service);
                break;
            case STREAM:
                if (service instanceof Consumer) {
                    strategy = new FeatureStreamStrategy(service);
                } else {
                    // not a consumer... let's be lenient and return an ITERATOR that actually allow
                    // streaming.
                    strategy = new FeatureIteratorStrategy(service);
                    LOGGER.warn(
                            "STREAM resultType was chosen for producer, but is supported only for consumer. Using ITERATOR as a resultType...");
                }
                break;
            default:
                throw new RuntimeException(
                        "No suitable strategy found for type:" + resultType.name());
        }
        return strategy;
    }
}
