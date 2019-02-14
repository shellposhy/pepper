package com.pepper.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.lucene.document.Field;

import com.pepper.spring.enums.EDataType;

/**
 * Annotation tag for java bean {@code java.lang.reflect.Field}
 *
 * @author shellpo shih
 * @version 1.0
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PepperField {
	/** {@link Field} name should be setted. */
	String key() default "";

	/** Specifies whether and how a field should be stored. */
	Field.Store store() default Field.Store.NO;

	/** Specifies whether and how a field should be indexed. */
	Field.Index index() default Field.Index.NOT_ANALYZED;

	/** Specifies whether and how a field should have term vectors. */
	Field.TermVector termVector() default Field.TermVector.NO;

	/** The base java data type */
	EDataType type() default EDataType.STRING;
}
