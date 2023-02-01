package com.bobocode.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.Bean;
import com.bobocode.exception.NoSuchBeanException;
import com.bobocode.exception.NoUniqueBeanException;
import com.bobocode.service.PaymentService;
import com.bobocode.service.PrintService;
import com.bobocode.testinfrastructure.MockPaymentService;
import com.bobocode.testinfrastructure.MockPdfPrintService;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

class ApplicationContextTest {

    private static final String BASE_PACKAGE_NAME = "com.bobocode.testinfrastructure";

    private static final int expectedBeansCountInBasePackage = 4;

    private static final int expectedPrintServiceBeansCountInBasePackage = 3;

    private static final int expectedPaymentServiceBeansCountInBasePackage = 1;


    @BeforeAll
    static void assertBeansCountInBasePackage() {
        Reflections reflections = new Reflections(BASE_PACKAGE_NAME);
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Bean.class);
        assertEquals(expectedBeansCountInBasePackage, typesAnnotatedWith.size());
        assertEquals(expectedPrintServiceBeansCountInBasePackage, typesAnnotatedWith.stream().filter(PrintService.class::isAssignableFrom).count());
        assertEquals(expectedPaymentServiceBeansCountInBasePackage, typesAnnotatedWith.stream().filter(PaymentService.class::isAssignableFrom).count());
    }

    @Test
    void shouldReturnAllBeansByType() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        Map<String, PrintService> actualBeans = appContext.getAllBeans(PrintService.class);
        int expectedBeansSize = 3;
        assertEquals(expectedBeansSize, actualBeans.size());
    }

    @Test
    void shouldReturnEmptyMapIfBeansByTypeNotFound() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        Map<String, ApplicationContext> actualBeans = appContext.getAllBeans(ApplicationContext.class);
        int expectedBeansSize = 0;
        assertEquals(expectedBeansSize, actualBeans.size());
    }

    @Test
    void shouldReturnBeanByType() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        PaymentService actualBean = appContext.getBean(PaymentService.class);
        assertEquals(MockPaymentService.class, actualBean.getClass());
    }

    @Test
    void shouldResolveBeanName() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        Map<String, PrintService> printServiceBeans = appContext.getAllBeans(PrintService.class);
        Set<String> actualBeanNames = printServiceBeans.keySet();
        Set<String> expectedBeanNames = new HashSet<>();
        expectedBeanNames.add("coolBeanService");
        expectedBeanNames.add("mockPdfPrintService");
        expectedBeanNames.add("mockPlainTextPrintService");
        assertEquals(actualBeanNames, expectedBeanNames);
    }

    @Test
    void shouldThrowNoUniqueBeanExceptionWhenMultipleBeansWithTheSameBeanByTypeFound() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        assertThrows(NoUniqueBeanException.class, () -> appContext.getBean(PrintService.class));
    }

    @Test
    void shouldThrowNoSuchBeanExceptionWhenNoBeanByTypeFound() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        assertThrows(NoSuchBeanException.class, () -> appContext.getBean(ApplicationContext.class));
    }

    @Test
    void shouldReturnBeanByNameAndType() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        PrintService actualBean = appContext.getBean("mockPdfPrintService", PrintService.class);
        assertEquals(MockPdfPrintService.class, actualBean.getClass());
    }

    @Test
    void shouldThrowNoSuchBeanExceptionWhenNoBeanByNameFound() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        assertThrows(NoSuchBeanException.class, () -> appContext.getBean("nonExistingBeanName", PrintService.class));
    }

    @Test
    void shouldThrowNoSuchBeanExceptionWhenBeanWithNameExistsButOfAnotherType() {
        var appContext = new AnnotationConfigApplicationContext(BASE_PACKAGE_NAME);
        assertThrows(NoSuchBeanException.class, () -> appContext.getBean("mockPdfPrintService", PaymentService.class));
    }
}
