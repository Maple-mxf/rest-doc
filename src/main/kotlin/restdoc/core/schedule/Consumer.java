package restdoc.core.schedule;


/**
 * @author ubuntu-m
 */
public interface Consumer<T> {

    /**
     * @param message Scheduler receive message
     * @return target parameter type
     */
    T convert(String message);

    /**
     * @param message Scheduler receive message
     * @return target parameter type
     */
    T convert(byte[] message);

    /**
     * @return consumer identify
     */
    String identify();

    /**
     * When Scheduler complete task. this method will be invoke or callback
     *
     * @param message The Scheduler receive message
     */
    void consume(T message);


}
