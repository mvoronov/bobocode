package com.bobocode.exception;

public class NoSuchBeanException extends RuntimeException {

    public NoSuchBeanException(String beanName) {
        super(String.format("Bean with name %s is not found", beanName));
    }

    public NoSuchBeanException(Class<?> beanType) {
        super(String.format("Bean with type %s is not found", beanType.getSimpleName()));
    }

    public NoSuchBeanException(String beanName, Class<?> beanType, Class<?> beanTypeInContext) {
        super(String.format("Bean with name %s and type %s is not found. Instead found bean with name %s and type %s",
                beanName, beanType.getSimpleName(), beanName, beanTypeInContext.getSimpleName()));
    }

}
