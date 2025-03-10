package top.speedcubing.lib.utils.collection;

import java.util.Collections;
import java.util.HashSet;

public class Sets {
    public static <E> HashSet<E> hashSet(E... elements) {
        HashSet<E> set = new HashSet<>();
        Collections.addAll(set, elements);
        return set;
    }
}
