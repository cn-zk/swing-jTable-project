package com.naii.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NaiiRetention {
	
	String name() default "";
	
	String type() default "input";
	
	boolean temp() default false;
	
	boolean hide() default false;
	
	String link() default "";
}
