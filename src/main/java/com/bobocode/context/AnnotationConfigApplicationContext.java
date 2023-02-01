package com.bobocode.context;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.text.WordUtils.uncapitalize;

import com.bobocode.Bean;
import com.bobocode.exception.BeanInitializationException;
import com.bobocode.exception.NoSuchBeanException;
import com.bobocode.exception.NoUniqueBeanException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.reflections.Reflections;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private final Map<String, Object> beanNameToBeanMap = new ConcurrentHashMap<>();

    public AnnotationConfigApplicationContext(String basePackage) {
        initContext(basePackage);
    }

    @Override
    public <T> T getBean(Class<T> beanType) {
        Map<String, T> filteredBeansMap = getAllBeans(beanType);
        if (filteredBeansMap.size() > 1) {
            throw new NoUniqueBeanException(beanType);
        }
        return filteredBeansMap.values()
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanException(beanType));
    }

    @Override
    public <T> T getBean(String name, Class<T> beanType) {
        if (!beanNameToBeanMap.containsKey(name)) {
            throw new NoSuchBeanException(name);
        }
        Object bean = beanNameToBeanMap.get(name);
        if (!beanType.isAssignableFrom(bean.getClass())) {
            throw new NoSuchBeanException(name, beanType, bean.getClass());
        }
        return beanType.cast(bean);
    }

    @Override
    public <T> Map<String, T> getAllBeans(Class<T> beanType) {
        return beanNameToBeanMap.entrySet()
                .stream()
                .filter(entry -> beanType.isAssignableFrom(entry.getValue().getClass()))
                .collect(toMap(Map.Entry::getKey, entry -> beanType.cast(entry.getValue())));
    }

    private void initContext(String basePackage) {
        Set<Class<?>> typesAnnotatedWith = new Reflections(basePackage).getTypesAnnotatedWith(Bean.class);
        typesAnnotatedWith.forEach(type -> {
            String beanName = resolveBeanName(type);
            Object bean = createBean(type);
            beanNameToBeanMap.put(beanName, bean);
        });
    }

    private static String resolveBeanName(Class<?> type) {
        String beanNameFromAnnotation = type.getAnnotation(Bean.class).value();
        String resolvedBeanName = beanNameFromAnnotation.isBlank() ? type.getSimpleName() : beanNameFromAnnotation;
        return uncapitalize(resolvedBeanName);
    }

    private static Object createBean(Class<?> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new BeanInitializationException(e.getMessage());
        }
    }
}
