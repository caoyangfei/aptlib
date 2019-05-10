package com.flyang.annotation.apt;

import com.flyang.annotation.apt.inter.ListenerClass;
import com.flyang.annotation.apt.inter.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnClickListener",
        type = "android.view.View.OnClickListener",
        method = @ListenerMethod(
                name = "onClick",
                parameters = "android.view.View"
        )
)
public @interface OnClick {
    String[] value();
}