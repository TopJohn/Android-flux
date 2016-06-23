package com.john.flux.dispatcher;

import com.john.flux.ActionMethodHolder;
import com.john.flux.EventChangeMethodHolder;
import com.john.flux.actions.Action;
import com.john.flux.stores.Store;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by oceanzhang on 16/2/17.
 */
public class Dispatcher {
    private final Bus bus;

    //用注解方法的参数类型作为key,这个map里存的key为参数类型,value为holder
    private ConcurrentHashMap<String,List<ActionMethodHolder>> registeActionClasses = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,List<EventChangeMethodHolder>> registeEventClasses = new ConcurrentHashMap<>();
    public static Dispatcher get(Bus bus) {
        return new Dispatcher(bus);
    }

    Dispatcher(Bus bus) {
        this.bus = bus;
    }

    /**
     * 将Context中的store以及changeEvent的订阅者一般是Context本身进行register
     * @param cls
     */
    public void register(final Object cls) {
        if(cls instanceof Store) {
            ActionMethodHolder[] holders = ActionMethodHolder.findAllFluxActionMethods(cls);
            if (holders.length == 0) {
                return;
            }
            for (ActionMethodHolder holder : holders) {
                String key = holder.getParameterClass().getName();
                List<ActionMethodHolder> list = registeActionClasses.get(key);
                if (list == null) {
                    list = new ArrayList<ActionMethodHolder>();
                    registeActionClasses.put(key, list);
                }
                list.add(holder);
            }
        }else {
            EventChangeMethodHolder[] holders1 = EventChangeMethodHolder.findAllFluxEventChangeMethods(cls);
            if (holders1.length == 0) {
                return;
            }
            for (EventChangeMethodHolder holder : holders1) {
                String key = holder.getParameterClass().getName();
                List<EventChangeMethodHolder> list = registeEventClasses.get(key);
                if (list == null) {
                    list = new ArrayList<EventChangeMethodHolder>();
                    registeEventClasses.put(key, list);
                }
                list.add(holder);
            }
        }
    }

    /**
     *解绑相印的context以及store
     * @param cls
     */
    public void unregister(final Object cls) {
        ActionMethodHolder[] holders = ActionMethodHolder.findAllFluxActionMethods(cls);
        for (ActionMethodHolder holder : holders) {
            List<ActionMethodHolder> list = registeActionClasses.get(holder.getParameterClass().getName());
            if (list == null) {
                continue;
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                Object receiver = holder.getReceiver();
                if (receiver == null || receiver == cls) {
                    list.remove(i);
                }
            }
        }
        EventChangeMethodHolder[] holders1 = EventChangeMethodHolder.findAllFluxEventChangeMethods(cls);
        for (EventChangeMethodHolder holder : holders1) {
            List<EventChangeMethodHolder> list = registeEventClasses.get(holder.getParameterClass().getName());
            if (list == null) {
                continue;
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                Object receiver = holder.getReceiver();
                if (receiver == null || receiver == cls) {
                    list.remove(i);
                }
            }
        }
    }

    /**
     * store中调用dispatcher的emitChange来分发StoreChangeEvent
     * @param o
     */
    public void emitChange(Store.StoreChangeEvent o) {
        post(o);
    }

    /**
     * actionsCreator中调用dispatcher.dispatch来分发action,action的type 以及 data的key-value 一对一对的,向store分发action,其实是调用onEvent方法,在里面判断是action还是storeChangeEvent
     * @param type
     * @param data
     */
    public void dispatch(String type, Object... data) {
        if (isEmpty(type)) {
            throw new IllegalArgumentException("Type must not be empty");
        }

        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Data must be a valid list of key,value pairs");
        }

        Action.Builder actionBuilder = Action.type(type);
        int i = 0;
        while (i < data.length) {
            String key = (String) data[i++];
            Object value = data[i++];
            actionBuilder.bundle(key, value);
        }
        post(actionBuilder.build());
    }
    public void dispatch(String type,Map<String,Objects> map){
        if (isEmpty(type)) {
            throw new IllegalArgumentException("Type must not be empty");
        }
        Action.Builder actionBuilder = Action.type(type);
        if(map != null && map.size() > 0){
            for(String key:map.keySet()){
                actionBuilder.bundle(key, map.get(key));
            }
        }
        post(actionBuilder.build());
    }
    public void start(){
        bus.register(this);
    }
    public void stop(){
        bus.unregister(this);
    }
    private boolean isEmpty(String type) {
        return type == null || type.isEmpty();
    }

    private void post(final Object event) {
        bus.post(event);
    }


    /**
     * store可以订阅具体的某个action通过action的type区别
     * @param event
     */
    @Subscribe
    public void onEvent(Object event){
        if(event instanceof Store.StoreChangeEvent){
            for(String key:registeEventClasses.keySet()){
                List<EventChangeMethodHolder> holders = registeEventClasses.get(key);
                for(EventChangeMethodHolder holder: holders){
                    holder.call(event);
                }
            }
        }else if(event instanceof Action){
            Action action = (Action) event;
            String type = action.getType();
            for(String key:registeActionClasses.keySet()){
                List<ActionMethodHolder> holders = registeActionClasses.get(key);
                for(ActionMethodHolder holder: holders){
                    if(type.equals(holder.getmActionName())) {
                        holder.call(action.getData());
                        return;
                    }
                }
            }
        }
    }
}
