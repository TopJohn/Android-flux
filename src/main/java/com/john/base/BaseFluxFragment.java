package com.john.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.john.flux.actions.ActionsCreator;
import com.john.flux.annotation.BindEvent;
import com.john.flux.dispatcher.Dispatcher;
import com.john.flux.stores.Store;
import com.squareup.otto.Bus;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by oceanzhang on 16/2/17.
 */
public class BaseFluxFragment<STORE extends Store,CREATER extends ActionsCreator> extends Fragment{
    protected Dispatcher dispatcher;
    private STORE store;
    private CREATER actionCreater;
    protected boolean flux(){
        return false;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(flux()) {
            dispatcher = Dispatcher.get(new Bus());
            if (store() != null) {
                dispatcher.start();
                dispatcher.register(this);
                dispatcher.register(store());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(flux() && store() != null) {
            dispatcher.stop();
            dispatcher.unregister(this);
            dispatcher.unregister(store());
        }
    }

    @BindEvent
    public void onEvent(Store.StoreChangeEvent event){
        updateView(event);
    }

    /**
     * store状态刷新view
     * @param event
     */
    protected void updateView(Store.StoreChangeEvent event){

    }

    protected final STORE store(){
        if(store == null){
            store = (STORE) newInstance(getType("com.milk.flux.stores.Store"),new Class<?>[]{Dispatcher.class},dispatcher);
        }
        return store;
    }
    protected final CREATER actionsCreator(){
        if(actionCreater == null){
            actionCreater = (CREATER) newInstance(getType("com.milk.flux.actions.ActionsCreator"),new Class<?>[]{Dispatcher.class},dispatcher);
        }
        return actionCreater;
    }
    /**
     * 获取子类的泛型参数
     *
     * @return
     */
    protected Class<?> getType(String name) {
        Type superclass = getClass().getGenericSuperclass();
        while (superclass != null && !(superclass instanceof ParameterizedType)) {
            superclass = ((Class) superclass).getGenericSuperclass();
        }
        if (superclass == null) {
            throw new RuntimeException("Missing type parameter.");
        }
        Type []types = ((ParameterizedType) superclass).getActualTypeArguments();
        for(int i =0 ;i<types.length;i++){
//            Class<?> cls = (Class<?>) types[i];
            Class<?> cls = null;
            if(types[i] instanceof Class){
                cls = (Class<?>) types[i];
            }else {
                Type t = types[i];
                if(t instanceof ParameterizedType){
                    cls = (Class<?>) ((ParameterizedType) t).getRawType();
                }
            }
            if (cls == null) {
                throw new RuntimeException("Missing type parameter.");
            }
            if(cls.getName().equals(name)){
                return cls;
            }
            Class c = cls;
            while ((c = c.getSuperclass()) != null && !c.getName().equals("java.lang.Object")){
                if(c.getName().equals(name)){
                    return cls;
                }
            }
        }
        return null;
    }

    protected <T> T newInstance(Class<T> cls,Class<?> []params,Object ... args){
        try {
            Constructor<?> constructor = cls.getDeclaredConstructor(params);
            constructor.setAccessible(true);
            return (T) constructor.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
