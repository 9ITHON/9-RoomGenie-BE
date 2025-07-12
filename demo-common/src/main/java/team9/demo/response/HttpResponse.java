package team9.demo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpResponse<T> {
    private final int status;
    private final T data;
}