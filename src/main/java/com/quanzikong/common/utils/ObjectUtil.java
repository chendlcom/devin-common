package com.quanzikong.common.utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 有关<code>Object</code>处理的工具类。
 * <p>
 * 这个类中的每个方法都可以“安全”地处理<code>null</code>，而不会抛出<code>NullPointerException</code>。
 * </p>
 */
public class ObjectUtil {

    private static final int INITIAL_HASH = 7;
    private static final int MULTIPLIER = 31;

    private static final String EMPTY_STRING = StringUtil.EMPTY_STRING;
    private static final String NULL_STRING = StringUtil.NULL_STRING;
    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;
    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";

    /**
     * 用于表示<code>null</code>的常量。
     * <p>
     * 例如，<code>HashMap.get(Object)</code>方法返回<code>null</code>有两种可能： 值不存在或值为 <code>null</code>。而这个singleton可用来区别这两种情形。
     * </p>
     * <p>
     * 另一个例子是，<code>Hashtable</code>的值不能为<code>null</code>。
     * </p>
     */
    public static final Object NULL = new Serializable() {

        private static final long serialVersionUID = 7092611880189329093L;

        private Object readResolve() {
            return NULL;
        }
    };

    /**
     * 如果对象为<code>null</code>，则返回指定默认对象，否则返回对象本身。
     *
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param object       要测试的对象
     * @param defaultValue 默认值
     *
     * @return 对象本身或默认对象
     */
    public static Object defaultIfNull(Object object, Object defaultValue) {
        return (object != null) ? object : defaultValue;
    }

    /**
     * 比较两个对象是否完全相等。
     * <p>
     * 此方法可以正确地比较多维数组。
     *
     * <pre>
     * ObjectUtil.equals(null, null)                  = true
     * ObjectUtil.equals(null, "")                    = false
     * ObjectUtil.equals("", null)                    = false
     * ObjectUtil.equals("", "")                      = true
     * ObjectUtil.equals(Boolean.TRUE, null)          = false
     * ObjectUtil.equals(Boolean.TRUE, "true")        = false
     * ObjectUtil.equals(Boolean.TRUE, Boolean.TRUE)  = true
     * ObjectUtil.equals(Boolean.TRUE, Boolean.FALSE) = false
     * </pre>
     *
     * </p>
     *
     * @param object1 对象1
     * @param object2 对象2
     *
     * @return 如果相等, 则返回<code>true</code>
     */
    public static boolean equals(Object object1, Object object2) {
        return ArrayUtil.equals(object1, object2);
    }

    /**
     * 取得对象的hash值, 如果对象为<code>null</code>, 则返回<code>0</code>。
     * <p>
     * 此方法可以正确地处理多维数组。
     * </p>
     *
     * @param object 对象
     *
     * @return hash值
     */
    public static int hashCode(Object object) {
        return ArrayUtil.hashCode(object);
    }

    /**
     * 取得对象的原始的hash值, 如果对象为<code>null</code>, 则返回<code>0</code>。
     * <p>
     * 该方法使用<code>System.identityHashCode</code>来取得hash值，该值不受对象本身的 <code>hashCode</code>方法的影响。
     * </p>
     *
     * @param object 对象
     *
     * @return hash值
     */
    public static int identityHashCode(Object object) {
        return (object == null) ? 0 : System.identityHashCode(object);
    }

    /**
     * 取得对象自身的identity，如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出。
     *
     * <pre>
     * ObjectUtil.identityToString(null)          = null
     * ObjectUtil.identityToString("")            = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE)  = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0])    = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0]) = "java.lang.Object[]@7fa"
     * </pre>
     *
     * @param object 对象
     *
     * @return 对象的identity，如果对象是<code>null</code>，则返回<code>null</code>
     */
    public static String identityToString(Object object) {
        if (object == null) {
            return null;
        }

        return appendIdentityToString(null, object).toString();
    }

    /**
     * 取得对象自身的identity，如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出。
     *
     * <pre>
     * ObjectUtil.identityToString(null, "NULL")            = "NULL"
     * ObjectUtil.identityToString("", "NULL")              = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE, "NULL")    = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0], "NULL")      = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0], "NULL")   = "java.lang.Object[]@7fa"
     * </pre>
     *
     * @param object  对象
     * @param nullStr 如果对象为<code>null</code>，则返回该字符串
     *
     * @return 对象的identity，如果对象是<code>null</code>，则返回指定字符串
     */
    public static String identityToString(Object object, String nullStr) {
        if (object == null) {
            return nullStr;
        }

        return appendIdentityToString(null, object).toString();
    }

    /**
     * 将对象自身的identity——如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出——追加到
     * <code>StringBuffer</code>中。
     *
     * <pre>
     * ObjectUtil.appendIdentityToString(*, null)            = null
     * ObjectUtil.appendIdentityToString(null, "")           = "java.lang.String@1e23"
     * ObjectUtil.appendIdentityToString(null, Boolean.TRUE) = "java.lang.Boolean@7fa"
     * ObjectUtil.appendIdentityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
     * ObjectUtil.appendIdentityToString(buf, new int[0])    = buf.append("int[]@7fa")
     * ObjectUtil.appendIdentityToString(buf, new Object[0]) = buf.append("java.lang.Object[]@7fa")
     * </pre>
     *
     * @param buffer <code>StringBuffer</code>对象，如果是<code>null</code>，则创建新的
     * @param object 对象
     *
     * @return <code>StringBuffer</code>对象，如果对象为<code>null</code>，则返回 <code>null</code>
     */
    public static StringBuffer appendIdentityToString(StringBuffer buffer, Object object) {
        if (object == null) {
            return null;
        }

        if (buffer == null) {
            buffer = new StringBuffer();
        }

        buffer.append(ClassUtil.getClassNameForObject(object));

        return buffer.append('@').append(Integer.toHexString(identityHashCode(object)));
    }

    /**
     * 复制一个对象。如果对象为<code>null</code>，则返回<code>null</code>。
     * <p>
     * 此方法调用<code>Object.clone</code>方法，默认只进行“浅复制”。 对于数组，调用 <code>ArrayUtil.clone</code>方法更高效。
     * </p>
     *
     * @param array 要复制的数组
     *
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     *
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object clone(Object array) throws Exception {
        if (array == null) {
            return null;
        }

        // 对数组特殊处理
        if (array instanceof Object[]) {
            return ArrayUtil.clone((Object[])array);
        }

        if (array instanceof long[]) {
            return ArrayUtil.clone((long[])array);
        }

        if (array instanceof int[]) {
            return ArrayUtil.clone((int[])array);
        }

        if (array instanceof short[]) {
            return ArrayUtil.clone((short[])array);
        }

        if (array instanceof byte[]) {
            return ArrayUtil.clone((byte[])array);
        }

        if (array instanceof double[]) {
            return ArrayUtil.clone((double[])array);
        }

        if (array instanceof float[]) {
            return ArrayUtil.clone((float[])array);
        }

        if (array instanceof boolean[]) {
            return ArrayUtil.clone((boolean[])array);
        }

        if (array instanceof char[]) {
            return ArrayUtil.clone((char[])array);
        }

        // Not cloneable
        if (!(array instanceof Cloneable)) {
            throw new CloneNotSupportedException("Object of class " + array.getClass().getName() + " is not Cloneable");
        }

        // 用reflection调用clone方法
        Class clazz = array.getClass();
        Method cloneMethod = clazz.getMethod("clone", ArrayUtil.EMPTY_CLASS_ARRAY);

        return cloneMethod.invoke(array, ArrayUtil.EMPTY_OBJECT_ARRAY);

    }

    /**
     * 检查两个对象是否属于相同类型。<code>null</code>将被看作任意类型。
     *
     * @param object1 对象1
     * @param object2 对象2
     *
     * @return 如果两个对象有相同的类型，则返回<code>true</code>
     */
    public static boolean isSameType(Object object1, Object object2) {
        if ((object1 == null) || (object2 == null)) {
            return true;
        }

        return object1.getClass().equals(object2.getClass());
    }

    /**
     * 取得对象的<code>toString()</code>的值，如果对象为<code>null</code>，则返回空字符串 <code>""</code>。
     *
     * <pre>
     * ObjectUtil.toString(null)         = ""
     * ObjectUtil.toString("")           = ""
     * ObjectUtil.toString("bat")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE) = "true"
     * ObjectUtil.toString([1, 2, 3])    = "[1, 2, 3]"
     * </pre>
     *
     * @param object 对象
     *
     * @return 对象的<code>toString()</code>的返回值，或空字符串<code>""</code>
     */
    public static String toString(Object object) {
        return (object == null) ? StringUtil.EMPTY_STRING : (object.getClass().isArray() ? ArrayUtil.toString(object) : object.toString());
    }

    /**
     * 取得对象的<code>toString()</code>的值，如果对象为<code>null</code>，则返回指定字符串。
     *
     * <pre>
     * ObjectUtil.toString(null, null)           = null
     * ObjectUtil.toString(null, "null")         = "null"
     * ObjectUtil.toString("", "null")           = ""
     * ObjectUtil.toString("bat", "null")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE, "null") = "true"
     * ObjectUtil.toString([1, 2, 3], "null")    = "[1, 2, 3]"
     * </pre>
     *
     * @param object  对象
     * @param nullStr 如果对象为<code>null</code>，则返回该字符串
     *
     * @return 对象的<code>toString()</code>的返回值，或指定字符串
     */
    public static String toString(Object object, String nullStr) {
        return (object == null) ? nullStr : (object.getClass().isArray() ? ArrayUtil.toString(object) : object.toString());
    }

    /**
     * 将对象a的所有属性值赋值给对象b（不包括id属性），并返回对象b<br/>
     * 适用于POJO对象
     *
     * @param a
     * @param b
     * @param clazz
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T attrFromA2B(Object a, Object b, Class<T> clazz) {
        if (!a.getClass().getName().equals(b.getClass().getName())) {
            return (T)b;
        }
        String id = "id", is = "is";
        try {
            for (Field field : clazz.getDeclaredFields()) {
                String fieldName = field.getName();
                if (fieldName.equalsIgnoreCase(id)) {
                    continue;
                }

                if (fieldName.substring(0, 2).equalsIgnoreCase(is)) {
                    fieldName = fieldName.substring(2, fieldName.length());
                } else {
                    fieldName = StringUtil.toHump(fieldName);
                }

                Method setter = null, getter = null;

                Class<?> fieldType = field.getType();
                setter = clazz.getDeclaredMethod("set" + fieldName, fieldType);
                if ("boolean".equalsIgnoreCase(fieldType.getName())) {
                    getter = clazz.getDeclaredMethod("is" + fieldName);
                } else {
                    getter = clazz.getDeclaredMethod("get" + fieldName);
                }

                Object val = null;
                if ((val = getter.invoke(a)) == null) {
                    continue;
                }

                setter.invoke(b, val);
            }
        } catch (Exception e) {
        }

        return (T)b;
    }

    /**
     * Return whether the given array is empty: that is, <code>null</code> or of
     * zero length.
     *
     * @param array the array to check
     *
     * @return whether the given array is empty
     */
    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * Return whether the given throwable is a checked exception: that is,
     * neither a RuntimeException nor an Error.
     *
     * @param ex the throwable to check
     *
     * @return whether the throwable is a checked exception
     *
     * @see Exception
     * @see RuntimeException
     * @see Error
     */
    public static boolean isCheckedException(Throwable ex) {
        return !(ex instanceof RuntimeException || ex instanceof Error);
    }

    /**
     * Check whether the given exception is compatible with the exceptions
     * declared in a throws clause.
     *
     * @param ex                 the exception to checked
     * @param declaredExceptions the exceptions declared in the throws clause
     *
     * @return whether the given exception is compatible
     */
    public static boolean isCompatibleWithThrowsClause(Throwable ex,
                                                       Class<? extends Throwable>[] declaredExceptions) {
        if (!isCheckedException(ex)) {
            return true;
        }
        if (declaredExceptions != null) {
            for (int i = 0; i < declaredExceptions.length; i++) {
                if (declaredExceptions[i].isAssignableFrom(ex.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given array contains the given element.
     *
     * @param array   the array to check (may be <code>null</code>, in which case
     *                the return value will always be <code>false</code>)
     * @param element the element to check for
     *
     * @return whether the element has been found in the given array
     */
    public static boolean containsElement(Object[] array, Object element) {
        if (array == null) {
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            if (nullSafeEquals(array[i], element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Append the given Object to the given array, returning a new array
     * consisting of the input array contents plus the given Object.
     *
     * @param array the array to append to (can be <code>null</code>)
     * @param obj   the Object to append
     *
     * @return the new array (of the same component type; never
     * <code>null</code>)
     */
    public static Object[] addObjectToArray(Object[] array, Object obj) {
        Class<?> compType = Object.class;
        if (array != null) {
            compType = array.getClass().getComponentType();
        } else if (obj != null) {
            compType = obj.getClass();
        }
        int newArrLength = (array != null ? array.length + 1 : 1);
        Object[] newArr = (Object[])Array.newInstance(compType, newArrLength);
        if (array != null) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }

    /**
     * Convert the given array (which may be a primitive array) to an object
     * array (if necessary of primitive wrapper objects).
     * <p>
     * A <code>null</code> source value will be converted to an empty Object
     * array.
     *
     * @param source the (potentially primitive) array
     *
     * @return the corresponding object array (never <code>null</code>)
     *
     * @throws IllegalArgumentException if the parameter is not an array
     */
    public static Object[] toObjectArray(Object source) {
        if (source instanceof Object[]) {
            return (Object[])source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: "
                + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class<?> wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[])Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }

    /**
     * Return the same value as <code>{@link Boolean#hashCode()}</code>.
     *
     * @param bool boolean
     *
     * @return hash code
     *
     * @see Boolean#hashCode()
     */
    public static int hashCode(boolean bool) {
        return bool ? 1231 : 1237;
    }

    /**
     * Return the same value as <code>{@link Double#hashCode()}</code>.
     *
     * @param dbl double
     *
     * @return hash code
     *
     * @see Double#hashCode()
     */
    public static int hashCode(double dbl) {
        long bits = Double.doubleToLongBits(dbl);
        return hashCode(bits);
    }

    /**
     * Return the same value as <code>{@link Float#hashCode()}</code>.
     *
     * @param flt float
     *
     * @return hash code
     *
     * @see Float#hashCode()
     */
    public static int hashCode(float flt) {
        return Float.floatToIntBits(flt);
    }

    /**
     * Return the same value as <code>{@link Long#hashCode()}</code>.
     *
     * @param lng long
     *
     * @return hash code
     *
     * @see Long#hashCode()
     */
    public static int hashCode(long lng) {
        return (int)(lng ^ (lng >>> 32));
    }

    // ---------------------------------------------------------------------
    // Convenience methods for toString output
    // ---------------------------------------------------------------------

    /**
     * Return a hex String form of an object's identity hash code.
     *
     * @param obj the object
     *
     * @return the object's identity code in hex notation
     */
    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    /**
     * Return a content-based String representation if <code>obj</code> is not
     * <code>null</code>; otherwise returns an empty String.
     * <p>
     * Differs from {@link #nullSafeToString(Object)} in that it returns an
     * empty String rather than "null" for a <code>null</code> value.
     *
     * @param obj the object to build a display String for
     *
     * @return a display String representation of <code>obj</code>
     *
     * @see #nullSafeToString(Object)
     */
    public static String getDisplayString(Object obj) {
        if (obj == null) {
            return EMPTY_STRING;
        }
        return nullSafeToString(obj);
    }

    // ---------------------------------------------------------------------
    // Convenience methods for content-based equality/hash-code handling
    // ---------------------------------------------------------------------

    /**
     * Determine if the given objects are equal, returning <code>true</code> if
     * both are <code>null</code> or <code>false</code> if only one is
     * <code>null</code>.
     * <p>
     * Compares arrays with <code>Arrays.equals</code>, performing an equality
     * check based on the array elements rather than the array reference.
     *
     * @param o1 first Object to compare
     * @param o2 second Object to compare
     *
     * @return whether the given objects are equal
     *
     * @see Arrays#equals
     */
    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1.equals(o2)) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            if (o1 instanceof Object[] && o2 instanceof Object[]) {
                return Arrays.equals((Object[])o1, (Object[])o2);
            }
            if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
                return Arrays.equals((boolean[])o1, (boolean[])o2);
            }
            if (o1 instanceof byte[] && o2 instanceof byte[]) {
                return Arrays.equals((byte[])o1, (byte[])o2);
            }
            if (o1 instanceof char[] && o2 instanceof char[]) {
                return Arrays.equals((char[])o1, (char[])o2);
            }
            if (o1 instanceof double[] && o2 instanceof double[]) {
                return Arrays.equals((double[])o1, (double[])o2);
            }
            if (o1 instanceof float[] && o2 instanceof float[]) {
                return Arrays.equals((float[])o1, (float[])o2);
            }
            if (o1 instanceof int[] && o2 instanceof int[]) {
                return Arrays.equals((int[])o1, (int[])o2);
            }
            if (o1 instanceof long[] && o2 instanceof long[]) {
                return Arrays.equals((long[])o1, (long[])o2);
            }
            if (o1 instanceof short[] && o2 instanceof short[]) {
                return Arrays.equals((short[])o1, (short[])o2);
            }
        }
        return false;
    }

    /**
     * Return as hash code for the given object; typically the value of
     * <code>{@link Object#hashCode()}</code>. If the object is an array, this
     * method will delegate to any of the <code>nullSafeHashCode</code> methods
     * for arrays in this class. If the object is <code>null</code>, this method
     * returns 0.
     *
     * @param obj object
     *
     * @return hash code
     *
     * @see #nullSafeHashCode(Object[])
     * @see #nullSafeHashCode(boolean[])
     * @see #nullSafeHashCode(byte[])
     * @see #nullSafeHashCode(char[])
     * @see #nullSafeHashCode(double[])
     * @see #nullSafeHashCode(float[])
     * @see #nullSafeHashCode(int[])
     * @see #nullSafeHashCode(long[])
     * @see #nullSafeHashCode(short[])
     */
    public static int nullSafeHashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                return nullSafeHashCode((Object[])obj);
            }
            if (obj instanceof boolean[]) {
                return nullSafeHashCode((boolean[])obj);
            }
            if (obj instanceof byte[]) {
                return nullSafeHashCode((byte[])obj);
            }
            if (obj instanceof char[]) {
                return nullSafeHashCode((char[])obj);
            }
            if (obj instanceof double[]) {
                return nullSafeHashCode((double[])obj);
            }
            if (obj instanceof float[]) {
                return nullSafeHashCode((float[])obj);
            }
            if (obj instanceof int[]) {
                return nullSafeHashCode((int[])obj);
            }
            if (obj instanceof long[]) {
                return nullSafeHashCode((long[])obj);
            }
            if (obj instanceof short[]) {
                return nullSafeHashCode((short[])obj);
            }
        }
        return obj.hashCode();
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of objects
     *
     * @return hash code
     */
    public static int nullSafeHashCode(Object[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + nullSafeHashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of booleans
     *
     * @return hash code
     */
    public static int nullSafeHashCode(boolean[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of bytes
     *
     * @return hash code
     */
    public static int nullSafeHashCode(byte[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of chars
     *
     * @return hash code
     */
    public static int nullSafeHashCode(char[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of doubles
     *
     * @return hash code
     */
    public static int nullSafeHashCode(double[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of floats
     *
     * @return hash code
     */
    public static int nullSafeHashCode(float[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of ints
     *
     * @return hash code
     */
    public static int nullSafeHashCode(int[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of longs
     *
     * @return hash codes
     */
    public static int nullSafeHashCode(long[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array of shorts
     *
     * @return hash code
     */
    public static int nullSafeHashCode(short[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }

    /**
     * Determine the class name for the given object.
     * <p>
     * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
     *
     * @param obj the object to introspect (may be <code>null</code>)
     *
     * @return the corresponding class name
     */
    public static String nullSafeClassName(Object obj) {
        return (obj != null ? obj.getClass().getName() : NULL_STRING);
    }

    /**
     * Return a String representation of the specified Object.
     * <p>
     * Builds a String representation of the contents in case of an array.
     * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
     *
     * @param obj the object to build a String representation for
     *
     * @return a String representation of <code>obj</code>
     */
    public static String nullSafeToString(Object obj) {
        if (obj == null) {
            return NULL_STRING;
        }
        if (obj instanceof String) {
            return (String)obj;
        }
        if (obj instanceof Object[]) {
            return nullSafeToString((Object[])obj);
        }
        if (obj instanceof boolean[]) {
            return nullSafeToString((boolean[])obj);
        }
        if (obj instanceof byte[]) {
            return nullSafeToString((byte[])obj);
        }
        if (obj instanceof char[]) {
            return nullSafeToString((char[])obj);
        }
        if (obj instanceof double[]) {
            return nullSafeToString((double[])obj);
        }
        if (obj instanceof float[]) {
            return nullSafeToString((float[])obj);
        }
        if (obj instanceof int[]) {
            return nullSafeToString((int[])obj);
        }
        if (obj instanceof long[]) {
            return nullSafeToString((long[])obj);
        }
        if (obj instanceof short[]) {
            return nullSafeToString((short[])obj);
        }
        String str = obj.toString();
        return (str != null ? str : EMPTY_STRING);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(Object[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(String.valueOf(array[i]));
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(boolean[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }

            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(byte[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(char[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append("'").append(array[i]).append("'");
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(double[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }

            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(float[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }

            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(int[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(long[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     *
     * @param array the array to build a String representation for
     *
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(short[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
}