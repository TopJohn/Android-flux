package com.john.flux;

import com.john.flux.annotation.BindEvent;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oceanzhang on 16/2/17.
 */
public class EventChangeMethodHolder {
    private WeakReference<?> mObjectHolder;
    private Method mMethod;
    private Class<?> mParameterClass;

    /**
     * 私有构造方法
     * @param object
     * @param method
     * @param parameterClass
     */
    private EventChangeMethodHolder(Object object, Method method, Class<?> parameterClass) {
        this.mObjectHolder = new WeakReference<Object>(object);
        this.mMethod = method;
        this.mParameterClass = parameterClass;
    }

    /**
     *
     * 传入一个obj,获取所有注解中的actionName,参数类型,并保存起来
     * return all action methods in the object
     * @param object
     * @return
     */
    public static EventChangeMethodHolder[] findAllFluxEventChangeMethods(Object object) {
        List<EventChangeMethodHolder> result = new ArrayList<>();
        try {
            Class<?> clazz = object.getClass();
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
//                Annotation[] ann = m.getAnnotations();
//                if(m.getName().contains("onEvent")){
//                    System.out.print("");
//                }
                if (!hasEventAnnotation(m)) {
                    continue;
                }
                Class<?>[] paramTypes = m.getParameterTypes();
                if (paramTypes.length != 1) {
                    continue;
                }
                Class<?> param = paramTypes[0];
                result.add(new EventChangeMethodHolder(object, m, param));
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result.toArray(new EventChangeMethodHolder[0]);
    }

    /**
     * 判断是否有BindEvent的注解在方法上
     * @param method
     * @return
     */
    private static boolean hasEventAnnotation(Method method) {
        Annotation[] ann = method.getAnnotations();
        if (ann == null || ann.length == 0) {
            return false;
        }
        for (Annotation a : ann) {
            if (a instanceof BindEvent) {
                return true;
            }
        }
        return false;
    }

    /**
     * 调用注解标注的方法
     * call holding method.
     * If the object will be released, do nothing in this method.
     * @param parameter
     * @return
     */
    public boolean call(Object parameter) {
        Object receiver = this.mObjectHolder.get();
        if (receiver == null) {
            return false;
        }
        try {
            this.mMethod.invoke(receiver, parameter);
            return true;
        } catch (Throwable e) {
            System.out.println("ErrorHappened:" + e);
            e.printStackTrace();
            return false;
        }
    }
    //accessor
    public Object getReceiver() {
        return this.mObjectHolder.get();
    }
    public Class<?> getParameterClass() {
        return mParameterClass;
    }

}
