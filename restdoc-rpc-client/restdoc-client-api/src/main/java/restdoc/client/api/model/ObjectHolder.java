package restdoc.client.api.model;


/**
 * ObjectHolder
 *
 * @param <T> object type
 * @author Maple
 */
public class ObjectHolder<T> {

    public String className;

    public T value;

    public ObjectHolder() {
    }

    public ObjectHolder(String className, T value) {
        this.className = className;
        this.value = value;
    }
}
