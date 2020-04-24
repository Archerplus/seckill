package com.tth.miaosha.validator;

import com.tth.miaosha.util.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import java.lang.annotation.Annotation;
//第一个参数是需要进行校验的注解，第二个参数是校验的数据类型
public class IsMobileValidator implements ConstraintValidator<IsMoble, String> {

    private boolean required = false;

    //初始化方法可以拿到我们的注解
    @Override
    public void initialize(IsMoble isMoble) {
        required = isMoble.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMobile(value);
        }else{
            if(StringUtils.isEmpty(value)){
                return true;
            }else{
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
