package it.minusone.fastbuilder.utils.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * class listener extends CubingEvent {
 *
 * @CubingEventHandler public void a(MyEvent e){
 * //...
 * }
 * }
 * <p>
 * class MyEvent {
 * //...
 * }
 * <p>
 * //register listener
 * LibEventHandler.registerListeners(new listener(),...); //this will auto register MyEvent if MyEvent is not registered like below.
 * //register new event
 * LibEventHandler.createNewEvents(MyEvent.class,...);
 * }
 */
public class EventManager {
    public static final Map<Class<?>, List<Object>> Instances = new HashMap<>();
    public static final Map<Class<?>, List<Integer>> Priorities = new HashMap<>();
    public static final Map<Class<?>, List<Method>> Methods = new HashMap<>();

    public static void createNewEvents(Class<?>... classObjs) {
        for (Class<?> c : classObjs) {
            if (!Priorities.containsKey(c)) {
                Instances.put(c, new ArrayList<>());
                Priorities.put(c, new ArrayList<>());
                Methods.put(c, new ArrayList<>());
            }
        }
    }

}
