package team9.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.core.convert.converter.Converter;

import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.MediaType;

import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import team9.demo.error.ErrorCode;


@Tag("restdocs")
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest {

    protected MockMvcRequestSpecification mockMvc;

    private RestDocumentationContextProvider restDocumentation;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.restDocumentation = restDocumentation;
    }

    protected MockMvcRequestSpecification given() {
        return mockMvc;
    }

    protected MockMvcRequestSpecification mockController(Object controller, Object handler, HandlerMethodArgumentResolver argumentResolver) {
        return RestAssuredMockMvc.given().mockMvc(createMockMvc(controller, handler, null, argumentResolver));
    }

    protected MockMvcRequestSpecification mockControllerWithAdvice(Object controller, Object advice) {
        return RestAssuredMockMvc.given().mockMvc(createMockMvc(controller, advice, null, null));
    }

    protected MockMvcRequestSpecification mockControllerWithAdviceAndCustomConverter(
            Object controller, Object advice, Converter<String, ?> customConverter, HandlerMethodArgumentResolver argumentResolver
    ) {
        return RestAssuredMockMvc.given().mockMvc(createMockMvc(controller, advice, customConverter, argumentResolver));
    }

    private MockMvc createMockMvc(Object controller, Object advice, Converter<String, ?> customConverter, HandlerMethodArgumentResolver argumentResolver) {
        ObjectMapper mapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);

        StandaloneMockMvcBuilder builder = MockMvcBuilders.standaloneSetup(controller)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .setMessageConverters(converter);

        if (argumentResolver != null) {
            builder.setCustomArgumentResolvers(argumentResolver);
        }

        if (advice != null) {
            builder.setControllerAdvice(advice);
        }

        if (customConverter != null) {
            DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
            conversionService.addConverter(customConverter);
            builder.setConversionService(conversionService);
        }

        return builder.build();
    }

    protected String jsonBody(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    public static MockMvcRequestSpecification setupAuthenticatedJsonRequest(MockMvcRequestSpecification spec, String userId, String token) {
        return spec
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .attribute("userId", userId)
                .header("Authorization", "Bearer " + token);
    }

    public static MockMvcRequestSpecification setupAuthenticatedMultipartRequest(MockMvcRequestSpecification spec, String userId, String token) {
        return spec
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("Authorization", "Bearer " + token);
    }

    public static ValidatableMockMvcResponse assertErrorResponse(ValidatableMockMvcResponse response, HttpStatus status, ErrorCode errorCode) {
        return response
                .statusCode(status.value())
                .body("status", equalTo(status.value()))
                .body("data.errorCode", equalTo(errorCode.getCode()))
                .body("data.message", equalTo(errorCode.getMessage()));
    }

    public static ValidatableMockMvcResponse assertCommonSuccessResponse(ValidatableMockMvcResponse response) {
        return response
                .statusCode(200)
                .body("status", equalTo(200))
                .body("data.message", equalTo("성공"));
    }

    public static ValidatableMockMvcResponse assertCommonSuccessCreateResponse(ValidatableMockMvcResponse response) {
        return response
                .statusCode(201)
                .body("status", equalTo(201))
                .body("data.message", equalTo("생성 완료"));
    }
}

