package com.john.flux;


import com.john.flux.annotation.BindAction;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oceanzhang on 16/2/17.
 */
public class ActionMethodHolder {
    private WeakReference<?> mObjectHolder;
    private Method mMethod;
    private Class<?> mParameterClass;
    private String mActionName;

    /**
     * 私有构造方法
     * @param object
     * @param method
     * @param parameterClass
     * @param mActionName
     */
    private ActionMethodHolder(Object object, Method method, Class<?> parameterClass,String mActionName) {
        this.mObjectHolder = new WeakReference<Object>(object);
        this.mMethod = method;
        this.mParameterClass = parameterClass;
        this.mActionName = mActionName;
    }

    /**
     * 传入一个obj,获取所有注解中的actionName,参数类型,并保存起来
     * @param object
     * @return
     */
    public static ActionMethodHolder[] findAllFluxActionMethods(Object object) {
        List<ActionMethodHolder> result = new ArrayList<>();
        try {
            Class<?> clazz = object.getClass();
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
//                Annotation[] ann = m.getAnnotations();
                Annotation annotation = null;
                if ( (annotation = hasActionAnnotation(m)) == null) {
                    continue;
                }
                String actionName = ((BindAction)annotation).value();
                Class<?>[] paramTypes = m.getParameterTypes();
                if (paramTypes.length != 1) {
                    continue;
                }
                Class<?> param = paramTypes[0];
                result.add(new ActionMethodHolder(object, m, param,actionName));
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result.toArray(new ActionMethodHolder[0]);
    }


    /**
     * 判断是否有BindAction的注解在方法上
     * return true when the method has @Flux annotation
     * @param method
     * @return
     */
    private static Annotation hasActionAnnotation(Method method) {
        Annotation[] ann = method.getAnnotations();
        if (ann == null || ann.length == 0) {
            return null;
        }
        for (Annotation a : ann) {
            if (a instanceof BindAction) {
                return a;
            }
        }
        return null;
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

    public String getmActionName() {
        return mActionName;
    }
}
