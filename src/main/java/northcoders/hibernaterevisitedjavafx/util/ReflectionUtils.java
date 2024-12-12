package northcoders.hibernaterevisitedjavafx.util;

import jakarta.persistence.Entity;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.TypesAnnotated;

public class ReflectionUtils {
    public static List<Class<?>> getClasses() {
        Reflections reflections = new Reflections("northcoders.hibernaterevisitedjavafx.model", TypesAnnotated);
        return reflections.get(TypesAnnotated.with(Entity.class).asClass())
                .stream()
                .filter(clazz -> !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
                .collect(Collectors.toList());
    }
}
