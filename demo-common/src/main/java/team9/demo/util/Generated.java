package team9.demo.util;

import java.lang.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Generated {
}