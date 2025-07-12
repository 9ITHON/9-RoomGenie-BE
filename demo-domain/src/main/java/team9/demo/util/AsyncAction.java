package team9.demo.util;

@FunctionalInterface
public interface AsyncAction<T> {
    void execute(T item) throws Exception;
}