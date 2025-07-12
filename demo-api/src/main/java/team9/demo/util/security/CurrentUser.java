package team9.demo.util.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인한 사용자 정보를 주입받기 위한 커스텀 어노테이션입니다.
 * 주로 Spring MVC 컨트롤러 메서드의 파라미터에 사용됩니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {

}
