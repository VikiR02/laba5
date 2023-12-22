package org.example;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class Injector {
    public Object inject(Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Properties properties = loadProperties();


        for (Field field : fields) {
            if (field.isAnnotationPresent(AutoInjectable.class)) {
                Class<?> fieldType = field.getType();
                String implementationClassName = properties.getProperty(fieldType.getName());

                validateImplementation(implementationClassName, fieldType.getName());

                Object implementationInstance = createInstance(implementationClassName);

                setField(object, field, implementationInstance);
            }
        }

        return object;
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try { properties.load(new FileInputStream("C:\\Users\\user\\AppData\\Local\\Temp\\laba5\\src\\main\\resources\\my.properties"));
        } catch (IOException e)
        { throw new RuntimeException("Failed to load my.properties", e); }
        return properties; }

    private void validateImplementation(String implementationClassName, String propertyKey) {
        if (implementationClassName == null) {
            throw new RuntimeException("No implementation specified for " + propertyKey); }
    }

    private Object createInstance(String implementationClassName) {
        try { Class<?> implementationClass = Class.forName(implementationClassName);
            return implementationClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance", e); }
    }

    private void setField(Object object, Field field, Object value) {
        try { field.setAccessible(true); field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field: " + field.getName(), e); }
    }
}
