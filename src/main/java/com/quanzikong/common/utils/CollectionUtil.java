package com.quanzikong.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Miscellaneous collection utility methods.
 */
public class CollectionUtil {

    /**
     * Return <code>true</code> if the supplied Collection is <code>null</code>
     * or empty. Otherwise, return <code>false</code>.
     *
     * @param collection the Collection to check
     *
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !(collection == null || collection.isEmpty());
    }

    /**
     * Return <code>true</code> if the supplied Map is <code>null</code>
     * or empty. Otherwise, return <code>false</code>.
     *
     * @param map the Map to check
     *
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !(map == null || map.isEmpty());
    }

    /**
     * Convert the supplied array into a List.
     * <p>A <code>null</code> source value will be converted to an
     * empty List.
     *
     * @param <T>    the type of elements of the list
     * @param source the array
     *
     * @return the converted List result
     */
    public static <T> List<T> arrayToList(T[] source) {
        List<T> l = new ArrayList<T>();
        for (T t : source) {
            if (t == null) {
                continue;
            }
            if (t instanceof String) {
                l.add((T)t.toString().trim());
            }
            ;
        }
        return l;
    }

    /**
     * Merge the given array into the given Collection.
     *
     * @param <T>        type of elements of array
     * @param array      the array to merge (may be <code>null</code>)
     * @param collection the target Collection to merge the array into
     */
    public static <T> void mergeArrayIntoCollection(T[] array, Collection<T> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        for (int i = 0; i < array.length; i++) {
            collection.add(array[i]);
        }
    }

    /**
     * Merge the given Properties instance into the given Map,
     * copying all properties (key-value pairs) over.
     * <p>Uses <code>Properties.propertyNames()</code> to even catch
     * default properties linked into the original Properties instance.
     *
     * @param props the Properties instance to merge (may be <code>null</code>)
     * @param map   the target Map to merge the properties into
     */
    public static void mergePropertiesIntoMap(Properties props, Map<? super String, ? super String> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (props != null) {
            for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements(); ) {
                String key = (String)en.nextElement();
                map.put(key, props.getProperty(key));
            }
        }
    }

    /**
     * Check whether the given Iterator contains the given element.
     *
     * @param iterator the Iterator to check
     * @param element  the element to look for
     *
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean contains(Iterator<?> iterator, Object element) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (ObjectUtil.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Enumeration contains the given element.
     *
     * @param enumeration the Enumeration to check
     * @param element     the element to look for
     *
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean contains(Enumeration<?> enumeration, Object element) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (ObjectUtil.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Collection contains the given element instance.
     * <p>Enforces the given instance to be present, rather than returning
     * <code>true</code> for an equal element as well.
     *
     * @param collection the Collection to check
     * @param element    the element to look for
     *
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean containsInstance(Collection<?> collection, Object element) {
        if (collection != null) {
            for (Iterator<?> it = collection.iterator(); it.hasNext(); ) {
                Object candidate = it.next();
                if (candidate.equals(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return <code>true</code> if any element in '<code>candidates</code>' is
     * contained in '<code>source</code>'; otherwise returns <code>false</code>.
     *
     * @param source     the source Collection
     * @param candidates the candidates to search for
     *
     * @return whether any of the candidates has been found
     */
    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return false;
        }
        for (Iterator<?> it = candidates.iterator(); it.hasNext(); ) {
            if (source.contains(it.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the first element in '<code>candidates</code>' that is contained in
     * '<code>source</code>'. If no element in '<code>candidates</code>' is present in
     * '<code>source</code>' returns <code>null</code>. Iteration order is
     * {@link Collection} implementation specific.
     *
     * @param source     the source Collection
     * @param candidates the candidates to search for
     *
     * @return the first present object, or <code>null</code> if not found
     */
    public static Object findFirstMatch(Collection<?> source, Collection<?> candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return null;
        }
        for (Iterator<?> it = candidates.iterator(); it.hasNext(); ) {
            Object candidate = it.next();
            if (source.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Find a single value of the given type in the given Collection.
     *
     * @param collection the Collection to search
     * @param type       the type to look for
     *
     * @return a value of the given type found if there is a clear match,
     * or <code>null</code> if none or more than one such value found
     */
    public static Object findValueOfType(Collection<?> collection, Class<?> type) {
        if (isEmpty(collection)) {
            return null;
        }
        Object value = null;
        for (Iterator<?> it = collection.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            if (type == null || type.isInstance(obj)) {
                if (value != null) {
                    // More than one value found... no clear single value.
                    return null;
                }
                value = obj;
            }
        }
        return value;
    }

    /**
     * Find a single value of one of the given types in the given Collection:
     * searching the Collection for a value of the first type, then
     * searching for a value of the second type, etc.
     *
     * @param collection the collection to search
     * @param types      the types to look for, in prioritized order
     *
     * @return a value of one of the given types found if there is a clear match,
     * or <code>null</code> if none or more than one such value found
     */
    public static Object findValueOfType(Collection<?> collection, Class<?>[] types) {
        if (isEmpty(collection) || ObjectUtil.isEmpty(types)) {
            return null;
        }
        for (int i = 0; i < types.length; i++) {
            Object value = findValueOfType(collection, types[i]);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Determine whether the given Collection only contains a single unique object.
     *
     * @param collection the Collection to check
     *
     * @return <code>true</code> if the collection contains a single reference or
     * multiple references to the same instance, <code>false</code> else
     */
    public static boolean hasUniqueObject(Collection<?> collection) {
        if (isEmpty(collection)) {
            return false;
        }
        boolean hasCandidate = false;
        Object candidate = null;
        for (Iterator<?> it = collection.iterator(); it.hasNext(); ) {
            Object elem = it.next();
            if (!hasCandidate) {
                hasCandidate = true;
                candidate = elem;
            } else if (!candidate.equals(elem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将mainList按照batchCount分成单个List(最后一批可能比batchCount少)
     *
     * @param mainList
     * @param clazz
     * @param batchCount
     * @param <T>
     *
     * @return
     */
    public static <T> List<List<T>> toBatchList(List<T> mainList, Class<T> clazz, int batchCount) {
        // 批量插入数据
        int total = mainList.size();
        // - 将记录按照每批插入数据分成N个List
        List<List<T>> batchList = new ArrayList<List<T>>();
        int batch = total / batchCount, lastCount = total % batchCount;
        if (lastCount > 0) {
            batch += 1;
        }
        for (int i = 0; i < batch; i++) {
            List<T> t = new ArrayList<T>();
            int start = i * batchCount, end = (i + 1) * batchCount;
            if (end > total) {
                end = total;
            }
            t.addAll(mainList.subList(start, end));
            batchList.add(t);
        }

        return batchList;
    }

    /**
     * 找出a集合中有, b集合中没有的元素并返回
     *
     * @param a
     * @param b
     *
     * @return
     */
    public static List<String> findAddedItem(List<String> a, List<String> b) {
        // 纯新增的受影响IP
        List<String> addServers = new ArrayList<String>();
        // 合并原有和新增的受影响IP
        List<String> mergeServers = new ArrayList<String>();
        mergeServers.addAll(a);
        mergeServers.addAll(b);

        Iterator<String> it = mergeServers.iterator();
        while (it.hasNext()) {
            String str = it.next();

            // 老的集合里没有, 就是纯新增的
            if (StringUtil.isNotBlank(str) && a.contains(str) && !b.contains(str)) {
                addServers.add(str);
            }
        }

        return addServers;
    }

    /**
     * 找出a集合中没有, b集合中有的元素并返回
     *
     * @param a
     * @param b
     *
     * @return
     */
    public static List<String> findReducedItem(List<String> a, List<String> b) {
        // 减少的受影响IP
        List<String> reducedServers = new ArrayList<String>();
        // 合并原有和新增的受影响IP
        List<String> mergeServers = new ArrayList<String>();
        mergeServers.addAll(a);
        mergeServers.addAll(b);

        Iterator<String> it = mergeServers.iterator();
        while (it.hasNext()) {
            String str = it.next();

            // 老的集合里没有, 就是纯新增的
            if (!a.contains(str) && b.contains(str)) {
                reducedServers.add(str);
            }
        }

        return reducedServers;
    }
}
