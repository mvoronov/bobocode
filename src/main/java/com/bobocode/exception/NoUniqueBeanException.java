package com.bobocode.exception;

public class NoUniqueBeanException extends RuntimeException {

    public NoUniqueBeanException(Class<?> beanType) {
        super(String.format("Multiple beans found with the same type %s. Try to get bean by it's name and type", beanType.getSimpleName()));
    }

}
