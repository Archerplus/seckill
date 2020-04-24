package com.tth.miaosha.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//系统看到这个注解会调用这个校验器，进行数据校验
@Constraint(
        validatedBy = {IsMobileValidator.class}
)
//自定义注解
public @interface IsMoble {

    boolean required() default true;

    //这三个参数是必须的
    //如果校验不通过，应该提示什么信息
    String message() default "手机号码格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
