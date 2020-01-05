package com.team2073.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author pbriggs
 */
public abstract class StreamUtil {

    /**
     * Allows running distinct on a specific property while still returning the root object (unlike
     * {@link Stream#distinct()}). Use in combination with {@link Stream#filter(Predicate)}.
     * Ex: stream.filter(StreamUtils.distinctByKey(e -{@literal >} e.authorName))
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static <T, R> List<? extends R> mapToList(Collection<T> col, Function<? super T, ? extends R> mapper) {
        return col.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T, R> Set<? extends R> mapToSet(Collection<T> col, Function<? super T, ? extends R> mapper) {
        return col.stream().map(mapper).collect(Collectors.toSet());
    }

}
