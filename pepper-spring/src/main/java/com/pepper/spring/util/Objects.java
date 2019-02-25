package com.pepper.spring.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.spring.annotation.PepperField;
import com.pepper.spring.enums.EDataType;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import static cn.com.lemon.annotation.Reflections.fields;
import static cn.com.lemon.annotation.Reflections.get;

import static cn.com.people.data.util.HtmlUtil.getText;

import static cn.com.lemon.base.Strings.isNullOrEmpty;
import static cn.com.lemon.base.Preasserts.checkArgument;

import static cn.com.lemon.base.DateUtil.format;

/**
 * Java {@link Object} change {@link Document} utilities
 * 
 * @author shishb
 * @version 1.0
 */
public final class Objects {
	private static final Logger LOG = LoggerFactory.getLogger(Objects.class.getName());

	private Objects() {
	}

	/**
	 * Java {@link Object} change {@link Document} object
	 * <p>
	 * Index objects by the annotation content and values in the class attribute
	 * aspect.
	 * 
	 * @param clazz
	 *            Java {@link Object}
	 * @return {@link Document}
	 */
	public static Document doc(Object clazz) {
		if (null == clazz)
			return null;
		List<java.lang.reflect.Field> fields = fields(clazz.getClass());
		// Java object is contains fields
		if (null != fields && fields.size() > 0) {
			Document doc = new Document();
			for (Field field : fields) {
				if (null != field) {
					PepperField pepperField = field.getAnnotation(PepperField.class);
					/**
					 * Matches whether the Java {@link Object} {@link Field}
					 * class type matches the {@link PepperField} annotation
					 * {@link EDataType} data type
					 */
					Object value = get(clazz, field.getName());
					if (null != value) {
						if (type(field, value, pepperField)) {
							// doc.add(field(pepperField, value));
							field(pepperField, value, doc);
						}
					}
				}
			}
			return doc;
		}
		return null;
	}

	/* ========private utilities======== */

	private static void object(Object clazz, Document doc) {
		if (null != clazz) {
			List<java.lang.reflect.Field> fields = fields(clazz.getClass());
			if (null != fields && fields.size() > 0) {
				for (Field field : fields) {
					if (null != field) {
						PepperField pepperField = field.getAnnotation(PepperField.class);
						/**
						 * Matches whether the Java {@link Object} {@link Field}
						 * class type matches the annotation data type
						 * <p>
						 */
						Object value = get(clazz, field.getName());
						if (null != value) {
							if (type(field, value, pepperField)) {
								// doc.add(field(pepperField, value));
								field(pepperField, value, doc);
							}
						}
					}
				}
			}
		}
	}

	private static boolean type(Field field, Object value, PepperField pepperField) {
		if (null != pepperField && null != field) {
			Class<?> fieldType = EDataType.type(pepperField.type());
			Class<?> valueType = field.getType();
			if (null != fieldType) {
				/** Java enumerated {@link Enum} type values */
				if (value instanceof Enum) {
					valueType = Enum.class;
				}
				/**
				 * Non-enumerated type determination, except for a few Java base
				 * types specified.Others only support Java object types.
				 */
				else {
					String valueTypeName = valueType.getName();
					if (valueTypeName.equals("int") || value.equals("java.lang.Integer")) {
						valueType = Integer.TYPE;
					} else if (valueTypeName.equals("long") || value.equals("java.lang.Long")) {
						valueType = Long.TYPE;
					} else if (valueTypeName.equals("double") || value.equals("java.lang.Double")) {
						valueType = Double.TYPE;
					} else if (valueTypeName.equals("float") || value.equals("java.lang.Float")) {
						valueType = Float.TYPE;
					} else if (valueTypeName.equals("java.lang.String")) {
						valueType = String.class;
					} else if (valueTypeName.equals("java.math.BigDecimal")) {
						valueType = BigDecimal.class;
					} else if (valueTypeName.equals("java.math.BigInteger")) {
						valueType = BigInteger.class;
					} else if (valueTypeName.equals("java.util.Date")) {
						valueType = Date.class;
					} else {
						valueType = Object.class;
					}
				}
			}
			return fieldType.equals(valueType);
		}
		return false;
	}

	private static void field(PepperField field, Object value, Document doc) {
		checkArgument(!isNullOrEmpty(field.key()) && null != value);
		LOG.debug("Data scanning:" + field.key() + "=" + value);
		// Initialize metadata
		org.apache.lucene.document.Field.Store store = null != field.store() ? field.store()
				: org.apache.lucene.document.Field.Store.NO;
		org.apache.lucene.document.Field.Index index = null != field.index() ? field.index()
				: org.apache.lucene.document.Field.Index.NO;
		org.apache.lucene.document.Field.TermVector termVector = null != field.termVector() ? field.termVector()
				: org.apache.lucene.document.Field.TermVector.NO;
		NumericField docField = null;
		EDataType dataType = field.type();
		switch (dataType) {
		case INTEGER:
			docField = new NumericField(field.key(), store, index.ordinal() == 0 ? false : true);
			docField.setIntValue(Integer.parseInt(value.toString()));
			doc.add(docField);
			break;
		case LONG:
			docField = new NumericField(field.key(), store, index.ordinal() == 0 ? false : true);
			docField.setLongValue(Long.parseLong(value.toString()));
			doc.add(docField);
			break;
		case FLOAT:
			docField = new NumericField(field.key(), store, index.ordinal() == 0 ? false : true);
			docField.setFloatValue(Float.parseFloat(value.toString()));
			doc.add(docField);
			break;
		case DOUBLE:
			docField = new NumericField(field.key(), store, index.ordinal() == 0 ? false : true);
			docField.setDoubleValue(Double.parseDouble(value.toString()));
			doc.add(docField);
			break;
		case BIGDECIMAL:
			docField = new NumericField(field.key(), store, index.ordinal() == 0 ? false : true);
			docField.setLongValue(((BigDecimal) value).longValue());
			doc.add(docField);
			break;
		case BIGINTEGER:
			docField = new NumericField(field.key(), store, index.ordinal() == 0 ? false : true);
			docField.setLongValue(((BigInteger) value).longValue());
			doc.add(docField);
			break;
		case DATE:
		case DATETIME:
			docField = new NumericField(field.key(), store, index.ordinal() == 0 ? false : true);
			docField.setLongValue(Long.parseLong(format((Date) value, "yyyyMMddHHmmss")));
			doc.add(docField);
			break;
		case ENUM:
			docField = new NumericField(field.key(), store, index.ordinal() == 0 ? false : true);
			/** Dynamic proxies generate enum objects */
			FastClass enumClazz = FastClass.create(value.getClass());
			/** Default calls the enumerated {@link enum#ordinal} method */
			FastMethod enumMethod = enumClazz.getMethod("ordinal", null);
			try {
				Object intObject = enumMethod.invoke(value, null);
				docField.setIntValue(((Integer) intObject).intValue());
				doc.add(docField);
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
				LOG.error("The name[" + field.key() + "] of Field created error!");
			}
			break;
		case OBJECT:
			object(value, doc);
			break;
		case STRING:
		default:
			try {
				doc.add(new org.apache.lucene.document.Field(field.key(), getText(String.valueOf(value)), store, index,
						termVector));
			} catch (IOException e) {
				LOG.error("The name[" + field.key() + "] of Field created error!");
			}
			break;
		}
	}
}
