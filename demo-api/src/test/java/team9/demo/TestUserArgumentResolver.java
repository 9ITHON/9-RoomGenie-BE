package team9.demo;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import team9.demo.model.user.UserId;
import team9.demo.util.security.CurrentUser;

@Component
public class TestUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentUser 어노테이션 붙은 UserId 파라미터만 처리
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
                UserId.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
        // 테스트용 하드코딩 UserId 반환
        return UserId.of("testUserId123");
    }
}