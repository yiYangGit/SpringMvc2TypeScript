package example.model.core;

public class ParametrizedBaseDTO<T extends Number> {
    private T id;
    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
