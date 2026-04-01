package team9.demo.util.security;

import java.lang.annotation.*;

/**
 * 현재 로그인한 사용자 정보를 주입받기 위한 커스텀 어노테이션입니다.
 * 주로 Spring MVC 컨트롤러 메서드의 파라미터에 사용됩니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented // JavaDoc 등 문서 생성 시 이 어노테이션이 사용된 대상에 표시되도록 함
public @interface CurrentUser {

}
