package it.minusone.fastbuilder.utils.utils;

public class Threads {
    public static Thread newThread(String name, Runnable target) {
        Thread thread = new Thread(target);
        thread.setName(name);
        return thread;
    }
}
