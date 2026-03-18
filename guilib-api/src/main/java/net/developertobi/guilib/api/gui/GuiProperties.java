package net.developertobi.guilib.api.gui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GuiProperties {
    String id();
    int rows() default 1;
    int columns() default 9;
    String permission() default "";
    boolean closeable() default true;
    boolean playSoundOnClick() default true;
    boolean playSoundOnOpen() default true;
    boolean playSoundOnClose() default true;
    boolean playSoundOnPageSwitch() default true;
}
