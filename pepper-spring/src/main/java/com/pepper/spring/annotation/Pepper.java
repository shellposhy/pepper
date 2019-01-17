package com.pepper.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.pepper.spring.enums.EIndexOperate;

/**
 * Annotation tag for lucene service
 *
 * @author shellpo shih
 * @version 1.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Pepper {

	/** The name of the bean definition that serves. */
	Class<?> value();

	/** The bean create lucene index save path */
	String path() default "";

	/** index file operate type,default add new {@link document} into index */
	EIndexOperate type() default EIndexOperate.INSERT;
}
