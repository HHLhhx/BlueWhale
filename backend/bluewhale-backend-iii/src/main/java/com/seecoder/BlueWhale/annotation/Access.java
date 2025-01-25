package com.seecoder.BlueWhale.annotation;

import com.seecoder.BlueWhale.enums.RoleEnum;

import java.lang.annotation.*;

import static com.seecoder.BlueWhale.enums.RoleEnum.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Access {
    RoleEnum[] roles() default {CUSTOMER, STAFF, MANAGER, CEO};
}
