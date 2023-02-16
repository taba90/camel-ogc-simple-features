package it.fox.gis.camel.component;

/** A factory for {@link FeatureComponentStrategy}. */
class FeatureComponentStrategyFactory {

    /**
     * Create a {@link FeatureComponentStrategy} based on the operation and result type.
     *
     * @param operation the {@link Operation} coming from the endpoint.
     * @param resultType the {@link ResultType} coming from the endpoint.
     * @return a {@link FeatureComponentStrategy} matching the passed operation and resultType.
     */
    static FeatureComponentStrategy createStrategy(Operation operation, ResultType resultType) {
        FeatureComponentStrategy strategy;
        if (operation.equals(Operation.GET)) {
            switch (resultType) {
                case COLLECTION:
                    strategy = new FeatureCollectionStrategy();
                    break;
                case STREAM:
                    strategy = new FeatureIteratorStrategy();
                    break;
                case LIST:
                    strategy = new FeatureListStrategy();
                    break;
                default:
                    throw new RuntimeException(
                            "No suitable strategy found for type:" + resultType.name());
            }
        } else {
            switch (operation) {
                case ADD:
                    strategy = new AddStrategy();
                    break;
                case DELETE:
                    strategy = new DeleteStrategy();
                    break;
                default:
                    throw new RuntimeException(
                            "No suitable strategy found for op:" + operation.name());
            }
        }
        return strategy;
    }
}
