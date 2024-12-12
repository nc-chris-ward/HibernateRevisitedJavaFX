package northcoders.hibernaterevisitedjavafx.repository;

import jakarta.persistence.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import northcoders.hibernaterevisitedjavafx.config.HibernateConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class H2Repository {

    private static final SessionFactory sessionFactory;

    static {
        sessionFactory = HibernateConfig.getSessionFactory();
        sessionFactory.getSchemaManager().exportMappedObjects(true);
    }

    public void persist(Object object, Field[] fields, Map<String, Control> inputFields) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            setObjectFields(object, fields, inputFields, session);
            session.persist(object);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    }

    private void setObjectFields(Object instance, Field[] objectFields, Map<String, Control> inputFields, Session session) throws IllegalAccessException {
        for (Field field : objectFields) {
            field.setAccessible(true);
            Control inputControl = inputFields.get(field.getName());
            if (inputControl != null) {
                if (field.getType() == String.class) {
                    field.set(instance, ((TextField) inputControl).getText());
                } else if (field.getType() == int.class) {
                    field.set(instance, Integer.parseInt(((TextField) inputControl).getText()));
                } else if (field.getType() == double.class) {
                    field.set(instance, Double.parseDouble(((TextField) inputControl).getText()));
                } else if (field.getType() == boolean.class) {
                    field.set(instance, ((CheckBox) inputControl).isSelected());
                } else if (field.isAnnotationPresent(ManyToOne.class)) {
                    int foreignKeyId = Integer.parseInt(((TextField) inputControl).getText());
                    Object associatedEntity = session.get(field.getType(), foreignKeyId);
                    field.set(instance, associatedEntity);
                } else if (field.isAnnotationPresent(OneToMany.class) && field.isAnnotationPresent(JoinTable.class)) {
                    String[] foreignKeyIds = ((TextField) inputControl).getText().split(",");
                    List<Object> associatedEntities = new ArrayList<>();
                    for (String id : foreignKeyIds) {
                        Object associatedEntity = session.get(field.getType().getComponentType(), Integer.parseInt(id.trim()));
                        associatedEntities.add(associatedEntity);
                    }
                    field.set(instance, associatedEntities);
                }  else if (field.isAnnotationPresent(OneToOne.class)) {
                    int foreignKeyId = Integer.parseInt(((TextField) inputControl).getText());
                    Object associatedEntity = session.get(field.getType(), foreignKeyId);
                    field.set(instance, associatedEntity);

                    Field reverseField = Arrays.stream(associatedEntity.getClass().getDeclaredFields())
                            .filter(f -> f.getType().equals(instance.getClass()))
                            .findFirst()
                            .orElse(null);
                    if (reverseField != null) {
                        reverseField.setAccessible(true);
                        reverseField.set(associatedEntity, instance);
                    }
                }  else if (field.isAnnotationPresent(ManyToMany.class)) {
                    String[] foreignKeyIds = ((TextField) inputControl).getText().split(",");
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> entityClass = (Class<?>) listType.getActualTypeArguments()[0];
                    List<Object> associatedEntities = new ArrayList<>();
                    for (String id : foreignKeyIds) {
                        Object associatedEntity = session.get(entityClass, Integer.parseInt(id.trim()));
                        associatedEntities.add(associatedEntity);
                    }
                    field.set(instance, associatedEntities);
                }
            }
        }
    }


}
