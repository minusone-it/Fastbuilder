package it.minusone.fastbuilder.utils.eventbus;

import java.lang.reflect.Method;
import java.util.List;

public class Event {

    public Event call() {
        Class<?> c = getClass();
        if (EventManager.Instances.containsKey(c)) {
            List<Object> instances = EventManager.Instances.get(c);
            List<Method> methods = EventManager.Methods.get(c);
            int size = instances.size();
            for (int i = 0; i < size; i++) {
                try {
                    methods.get(i).invoke(instances.get(i), this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return this;
    }
}
