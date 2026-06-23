package top.flyingjack.common.config.anotation;

import org.springframework.context.annotation.Import;
import top.flyingjack.common.config.JacksonAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用全局 Jackson 配置：Long 序列化为 JSON 字符串，防止前端精度丢失
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(JacksonAutoConfiguration.class)
public @interface EnableGlobalJackson {
}
