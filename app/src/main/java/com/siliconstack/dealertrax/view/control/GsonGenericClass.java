package com.siliconstack.dealertrax.view.control;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public  class GsonGenericClass<X> implements ParameterizedType {

    private Class<?> wrapped;

    public GsonGenericClass(Class<X> wrapped) {
        this.wrapped = wrapped;
    }

    public Type[] getActualTypeArguments() {
        return new Type[]{wrapped};
    }

    public Type getRawType() {
        return List.class;
    }

    public Type getOwnerType() {
        return null;
    }

}

