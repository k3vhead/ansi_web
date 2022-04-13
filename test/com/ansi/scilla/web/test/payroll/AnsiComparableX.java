package com.ansi.scilla.web.test.payroll;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;

/**
 * Compare two objects by comparing a list of fields, which must exist and have "getter" methods in both classes.
 * If "this" field value implements AnsiComparable, then an ansiEquals check is made; else a generic "equals" is called.
 * The ansiEquals method is null-safe. If both objects are null, they are equal. If one is null and one is not, they are unequal.
 * 
 * @author dclewis
 *
 */
public interface AnsiComparableX {

	/**
	 * Create a list of fieldNames to compare when determining equality. A getter method must exist in both
	 * comparator classes. A null value is returned if a generic Java "equals" method is to be called
	 * 
	 * @param className
	 * @return a list of fieldNames to compare, or null if the class does not use ansiEquals
	 */
	public abstract AnsiComparison[] makeFieldNameList(String className);
	
	
	
	default public boolean ansiEquals(Object obj) throws Exception {
		boolean equals = true;
	
		String className = obj.getClass().getName(); 
		AnsiComparison[] fieldNameList = makeFieldNameList(className);
		
		if ( fieldNameList == null ) {
			return this.equals(obj);
		} else {
			for ( AnsiComparison ansiComparison : fieldNameList ) {
				String myGetterName = makeGetterName(ansiComparison.getLocalFieldName());
				String hisGetterName = makeGetterName(ansiComparison.getRemoteFieldName());
				Method myGetter = this.getClass().getMethod(myGetterName, (Class[])null);
				try {
					Method hisGetter = obj.getClass().getMethod(hisGetterName, (Class[])null);
					
					Class<?> getterReturnClass = myGetter.getReturnType();
					Class<?>[] getterInterfaceList = getterReturnClass.getInterfaces();
					boolean isAnsiComparable = IterableUtils.contains(Arrays.asList(getterInterfaceList), AnsiComparableX.class);
					
					boolean fieldMatches =  isAnsiComparable ? doAnsiCompare(myGetter, hisGetter, obj) : doCompare(myGetter, hisGetter, obj, ansiComparison.getComparator());
					if ( ! fieldMatches ) {
						equals = false;
					}
				} catch ( NoSuchMethodException e ) {
					System.out.println("NSM");
					equals = false;
				}
			}
		}
		
		return equals;
	}

	
	private boolean doAnsiCompare(Method myGetter, Method hisGetter, Object obj) throws Exception {
		AnsiComparableX myValue = (AnsiComparableX)myGetter.invoke(this, (Object[])null);
		Object hisValue = hisGetter.invoke(obj, (Object[])null);

		boolean equals = true;
		if ( myValue == null && hisValue == null ) {
			equals = true;
		} else if ( myValue != null && hisValue != null ) {
			equals = myValue.ansiEquals(hisValue);
		} else {
			equals = false;
		}
		
		return equals;
	}

	private boolean doCompare(Method myGetter, Method hisGetter, Object obj, AnsiComparator ansiComparator) throws Exception {
		Object myValue = myGetter.invoke(this, (Object[])null);
		Object hisValue = hisGetter.invoke(obj, (Object[])null);

		System.out.println("\t" + myValue);
		System.out.println("\t" + hisValue);
		boolean equals = true;
		if ( myValue == null && hisValue == null ) {
			equals = true;
		} else if ( myValue != null && hisValue != null ) {
			equals = ansiComparator.fieldsAreEqual(myValue, hisValue);
		} else {
			equals = false;
		}
		
		return equals;
	}

	
	private String makeGetterName(String fieldName) {
		return "get" + StringUtils.capitalize(fieldName);
	}
	
	
	
	
	public class AnsiComparison extends ApplicationObject {

		private static final long serialVersionUID = 1L;
		private String localFieldName;
		private String remoteFieldName;
		private AnsiComparator comparator;
		
		public AnsiComparison(String localFieldName, String remoteFieldName, AnsiComparator comparator) {
			super();
			this.localFieldName = localFieldName;
			this.remoteFieldName = remoteFieldName;
			this.comparator = comparator;
		}
		public String getLocalFieldName() {
			return localFieldName;
		}
		public String getRemoteFieldName() {
			return remoteFieldName;
		}
		public AnsiComparator getComparator() {
			return comparator;
		}
	}
}
