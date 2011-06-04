package fi.evident.cissa.utils;

import java.util.Iterator;

public class CollectionUtils {

    private CollectionUtils() { }

    public static String join(Iterable<?> items) {
        return join(items, "");
    }

    public static String join(Iterable<?> items, String separator) {
        StringBuilder sb = new StringBuilder();

        for (Iterator<?> it = items.iterator(); it.hasNext(); ) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append(separator);
        }

        return sb.toString();
    }
}
