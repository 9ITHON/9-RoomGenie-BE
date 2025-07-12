package team9.demo.util;


@FunctionalInterface
public interface AsyncFunction<T, R> {
    R apply(T item) throws Exception;
}
