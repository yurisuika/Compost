package dev.yurisuika.compost.api;

import dev.yurisuika.compost.util.Configure;
import dev.yurisuika.compost.world.Composition;

import java.util.Map;

public class CompostApi {

    /**
     * <p>Retrieves a map of compositions.
     *
     * <p>Compost stores its configuration in a map. Each composition is a value to a key, so that the user may easily
     * identify it. A composition contains both a compost object and a world set. As such, pretty much everything can
     * be done from this one method.
     *
     * @return a {@link Map} of {@link String} keys and {@link Composition} value
     */
    public static Map<String, Composition> getCompositions() {
        return Configure.getCompositions();
    }

    /**
     * <p>Retrieves a composition.
     *
     * @param name a key to a named composition {@link String}
     *
     * @return a {@link Composition}
     */
    public static Composition getComposition(String name) {
        return Configure.getComposition(name);
    }

    /**
     * <p>Adds a composition.
     *
     * @param name a key to a named composition {@link String}
     * @param composition a composition value {@link Composition}
     *
     * @see #getComposition(String)
     */
    public static void addComposition(String name, Composition composition) {
        Configure.addComposition(name, composition);
    }

    /**
     *<p>Removes a composition.
     *
     * @param name a key to a named composition {@link String}
     *
     * @see #getComposition(String)
     */
    public static void removeComposition(String name) {
        Configure.removeComposition(name);
    }

}