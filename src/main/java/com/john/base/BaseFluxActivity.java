package com.john.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
public abstract class BaseFluxActivity<STORE extends Store,CREATER extends ActionsCreator> extends AppCompatActivity {
    protected Dispatcher dispatcher;
    private STORE store;
    private CREATER actionCreater;

    /**
     * 是否启用flux模式
     * @return
     */
    protected boolean flux(){
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(flux()) {
            dispatcher = Dispatcher.get(new Bus());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //进行订阅注册
        if(flux() && store() != null) {
            dispatcher.start();
            dispatcher.register(this);
            dispatcher.register(store());
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        //解绑
        if(flux() && store() != null) {
            dispatcher.stop();
            dispatcher.unregister(this);
            dispatcher.unregister(store());
        }
    }

    /**
     * store 的订阅者方法,用于store.emitStoreChange的响应
     * @param event
     */
    @BindEvent
    public void onEvent(Store.StoreChangeEvent event){
        updateView(event);
    }


    /**
     * UI的更新
     * @param event
     */
    protected  void updateView(Store.StoreChangeEvent event){}

    /**
     * 实例化store
     * @return
     */
    protected final STORE store(){
        if(store == null){
            store = (STORE) newInstance(getType("com.milk.flux.stores.Store"),new Class<?>[]{Dispatcher.class},dispatcher);
        }
        return store;
    }

    /**
     * 实例化actionsCreator
     * @return
     */
    protected final CREATER actionsCreator(){
        if(actionCreater == null){
            actionCreater = (CREATER) newInstance(getType("com.milk.flux.actions.ActionsCreator"),new Class<?>[]{Dispatcher.class},dispatcher);
        }
        return actionCreater;
    }
    /**
     * 在实现类中向上寻找,获取范型的实际类型,并将类型返回
     *
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

    /**
     * 实例化某个类,通过反射的构造方法生成实例
     * @param cls
     * @param params
     * @param args
     * @param <T>
     * @return
     */
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
