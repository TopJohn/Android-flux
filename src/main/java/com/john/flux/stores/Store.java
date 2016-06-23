package com.john.flux.stores;


import com.john.flux.dispatcher.Dispatcher;

public class Store {

    protected final Dispatcher dispatcher;
    public Store(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

//    public static<T extends Store> T get(Dispatcher dispatcher,Class<T> cls) {
//        if (instance == null) {
//            try {
//                Class<?> []params = {Dispatcher.class};
//                Constructor<?> constructor = cls.getDeclaredConstructor(params);
//                constructor.setAccessible(true);
//                instance = (T) constructor.newInstance(dispatcher);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return (T)instance;
//    }

    protected void emitStoreChange(StoreChangeEvent event) {
        dispatcher.emitChange(event);
    }

    public class StoreChangeEvent{
        public boolean error;
        public String message;
        public int code;
        public StoreChangeEvent(boolean error, String message) {
            this.code = 0;
            this.error = error;
            this.message = message;
        }
        public StoreChangeEvent(int code,boolean error, String message) {
            this.code = code;
            this.error = error;
            this.message = message;
        }
        public StoreChangeEvent() {
            this.error = false;
        }
    }
}
