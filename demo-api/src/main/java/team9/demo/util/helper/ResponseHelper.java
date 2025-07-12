package team9.demo.util.helper;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import team9.demo.response.HttpResponse;
import team9.demo.response.SuccessCreateResponse;
import team9.demo.response.SuccessOnlyResponse;

public class ResponseHelper {

    public static <T> ResponseEntity<HttpResponse<T>> success(T data) {
        HttpResponse<T> response = new HttpResponse<>(HttpStatus.OK.value(), data);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<HttpResponse<SuccessOnlyResponse>> successOnly() {
        HttpResponse<SuccessOnlyResponse> response = new HttpResponse<>(HttpStatus.OK.value(), new SuccessOnlyResponse());
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<HttpResponse<SuccessCreateResponse>> successCreateOnly() {
        HttpResponse<SuccessCreateResponse> response = new HttpResponse<>(HttpStatus.CREATED.value(), new SuccessCreateResponse());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static <T> ResponseEntity<HttpResponse<T>> successCreate(T data) {
        HttpResponse<T> response = new HttpResponse<>(HttpStatus.CREATED.value(), data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static <T> ResponseEntity<HttpResponse<T>> error(HttpStatus status, T data) {
        HttpResponse<T> response = new HttpResponse<>(status.value(), data);
        return ResponseEntity.status(status).body(response);
    }
}