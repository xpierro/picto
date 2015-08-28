package com.github.picto.bencode.serialization;

import com.github.picto.bencode.BEncodedDictionary;
import com.github.picto.bencode.annotation.*;
import com.github.picto.bencode.exception.CannotUnserializeException;
import com.github.picto.bencode.type.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This unserializer takes a BEncoded dictionary and converts it into a class.
 * Created by Pierre on 25/08/15.
 */
public class BEncodeUnserializer<T> {

    private final static List<Class<? extends Annotation>> serializableAnnotations = new ArrayList<>();
    static {
        serializableAnnotations.add(BEncodeDictionary.class);
        serializableAnnotations.add(BEncodeByteArray.class);
        serializableAnnotations.add(BEncodeInteger.class);
        serializableAnnotations.add(BEncodeList.class);
    }

    private final BEncodeableDictionary root;

    private final Class<? extends T> targetClass;

    public BEncodeUnserializer(final BEncodeableDictionary dictionary, Class<? extends T> targetClass) throws CannotUnserializeException {
        if (dictionary == null) {
            throw new NullPointerException("Impossible to unserialize a null dictionary.");
        }
        if (targetClass == null) {
            throw new NullPointerException("No target class provided.");
        }
        // We first check if the target has been prepared for unserialization
        if (!targetClass.isAnnotationPresent(BEncodeDictionary.class)) {
            throw new CannotUnserializeException("The target class is not a dictionary.");
        }
        this.targetClass = targetClass;
        this.root = dictionary;
    }

    public BEncodeableDictionary getRoot() {
        return root;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    //TODO: generify, will be complex (need callback, lambdas maybe ??)
    private void unserializeByteArray(final T target, final Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> setterMap) throws InvocationTargetException, IllegalAccessException {
        Map<Annotation, Pair<Method, Class<?>>> byteArrayMap = setterMap.get(BEncodeByteArray.class);
        for (Annotation annotation : byteArrayMap.keySet()) {
            BEncodeByteArray concreteAnnotation = (BEncodeByteArray) annotation;
            Pair<Method, Class<?>> setterReturnTypePair = byteArrayMap.get(annotation);
            Class<?> returnType = setterReturnTypePair.getRight();
            Method setter = setterReturnTypePair.getLeft();

            String name = concreteAnnotation.name();
            Optional<BEncodeableType> byteArrayEncoded = root.get(name);
            if (byteArrayEncoded.isPresent()) {
                BEncodeableByteArray bEncodeableByteArray = (BEncodeableByteArray) byteArrayEncoded.get();
                if (returnType.equals(byte[].class)) {
                    setter.invoke(target, (Object) bEncodeableByteArray.getBytes());
                } else if (returnType.equals(String.class)) {
                    //TODO: nullity, errors of conversion checks
                    setter.invoke(target, bEncodeableByteArray.toUtf8String().get());
                }
            }
        }
    }

    private void unserializeInteger(final T target, final Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> setterMap) throws InvocationTargetException, IllegalAccessException {
        Map<Annotation, Pair<Method, Class<?>>> byteArrayMap = setterMap.get(BEncodeInteger.class);
        for (Annotation annotation : byteArrayMap.keySet()) {
            BEncodeInteger concreteAnnotation = (BEncodeInteger) annotation;
            Pair<Method, Class<?>> setterReturnTypePair = byteArrayMap.get(annotation);
            Class<?> returnType = setterReturnTypePair.getRight();
            Method setter = setterReturnTypePair.getLeft();

            String name = concreteAnnotation.name();
            Optional<BEncodeableType> valueEncoded = root.get(name);
            if (valueEncoded.isPresent()) {
                BEncodeableInteger bEncodeableInteger = (BEncodeableInteger) valueEncoded.get();
                if (returnType.equals(int.class)) {
                    setter.invoke(target, bEncodeableInteger.getInteger());
                }
                //TODO: error check, other types
            }
        }
    }

    private void unserializeDictionary(final T target, final Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> setterMap) throws InvocationTargetException, IllegalAccessException, CannotUnserializeException {
        Map<Annotation, Pair<Method, Class<?>>> byteArrayMap = setterMap.get(BEncodeDictionary.class);
        for (Annotation annotation : byteArrayMap.keySet()) {
            BEncodeDictionary concreteAnnotation = (BEncodeDictionary) annotation;
            Pair<Method, Class<?>> setterReturnTypePair = byteArrayMap.get(annotation);
            Class<?> returnType = setterReturnTypePair.getRight();
            Method setter = setterReturnTypePair.getLeft();

            String name = concreteAnnotation.name();
            Optional<BEncodeableType> valueEncoded = root.get(name);

            if (valueEncoded.isPresent()) {
                BEncodeableDictionary bEncodeableDictionary = (BEncodeableDictionary) valueEncoded.get();
                BEncodeUnserializer<?> innerSerializer = new BEncodeUnserializer<>(bEncodeableDictionary, concreteAnnotation.type());
                Object dictionary = innerSerializer.unserialize();
                setter.invoke(target, dictionary);
            }
        }
    }

    private List<?> unserializeBEncodeableList(final BEncodeableList bEncodeableList, final Class<?> elementType, final BEncodeInnerList[] innerListHierarchy, int index) {
        // First we detect the element type
        Class<? extends BEncodeableType> bencodeableElementType = bEncodeableList.getElementType();

        // Now we have four potential type
        if (bencodeableElementType.equals(BEncodeableByteArray.class)) {
            // 2 main cases: String or byte[]
            if (elementType.equals(String.class)) {

                List<String> result = new ArrayList<>();

                for (BEncodeableType bEncodeable : bEncodeableList) {
                    BEncodeableByteArray byteArray = (BEncodeableByteArray) bEncodeable;
                    result.add(byteArray.toUtf8String().get());
                }
                return result;
            }
        } else if (bencodeableElementType.equals(BEncodeableList.class)) {
            // Here we have a list of list, we need to first unserialise each list
            List<List<?>> result = new ArrayList<>();
            for (BEncodeableType bEncodeable : bEncodeableList) {
                BEncodeableList innerList = (BEncodeableList) bEncodeable;
                // We need to get the inner return type list, but java loses generic information anyway !
                result.add(unserializeBEncodeableList(innerList, innerListHierarchy[index].elementType(), innerListHierarchy, index + 1));

            }
            return result;
        }
        return new ArrayList<>();
    }

    private void unserializeList(final T target, final Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> setterMap) throws InvocationTargetException, IllegalAccessException {
        Map<Annotation, Pair<Method, Class<?>>> listMap = setterMap.get(BEncodeList.class);
        for (Annotation annotation : listMap.keySet()) {
            BEncodeList concreteAnnotation = (BEncodeList) annotation;
            Pair<Method, Class<?>> setterReturnTypePair = listMap.get(annotation);
            Class<?> returnType = setterReturnTypePair.getRight();
            Method setter = setterReturnTypePair.getLeft();

            String name = concreteAnnotation.name();
            Optional<BEncodeableType> valueEncoded = root.get(name);

            if (valueEncoded.isPresent()) {
                BEncodeableList bEncodeableList = (BEncodeableList) valueEncoded.get();


                setter.invoke(target, unserializeBEncodeableList(bEncodeableList, concreteAnnotation.elementType(), concreteAnnotation.innerList(), 0));
            }
        }
    }

    /**
     * Returns an object of the target class, read from the dictionary.
     * @return
     */
    public T unserialize() throws CannotUnserializeException {
        Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> setterMap = getSetters();

        T target = null;

        // The setter map contains all we need to build the object recursively
        try {
            target = targetClass.newInstance();
            // We have to insert into the class type by type
            unserializeByteArray(target, setterMap);
            unserializeInteger(target, setterMap);
            unserializeDictionary(target, setterMap);
            unserializeList(target, setterMap);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CannotUnserializeException("Reflexivity error.", e);
        }

        ((BEncodedDictionary) target).setBEncodeableDictionary(root);
        return target;
    }

    private Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> getInitialSetterMap() {
        Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> result = new HashMap<>();

        // We know the first order key type already
        for (Class<? extends Annotation> clazz : serializableAnnotations) {
            result.put(clazz, new HashMap<>());
        }

        return result;
    }

    private boolean isSerializable(final Method method) {
        for (Class<? extends Annotation> clazz : serializableAnnotations) {
            if (method.isAnnotationPresent(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return A map of BEncode annotation type => Map of BEncode annotation instance => pair of setter method, type we want to insert
     */
    private Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> getSetters() throws CannotUnserializeException {

        Map<Class<? extends Annotation>, Map<Annotation, Pair<Method, Class<?>>>> result = getInitialSetterMap();

        for (Method method : targetClass.getMethods()) {
            if (isSerializable(method)) {
                Method setter = null;
                Class<?> returnType = null;
                // The setter has been annotated
                if (method.getName().startsWith("set")) {
                    setter = method;
                    // The return type is the type of the first object
                    returnType = setter.getParameterTypes()[0];

                    // The getter has been annotated, we need to find its setter.
                } else if (method.getName().startsWith("get")) {
                    try {
                        returnType = method.getReturnType();
                        setter = targetClass.getMethod(method.getName().replaceFirst("get", "set"), method.getReturnType());
                    } catch (NoSuchMethodException e) {
                        throw new CannotUnserializeException("The setter is impossible to find.");
                    }
                } else if (method.getName().startsWith("is")) { //TODO: refactor here
                    try {
                        returnType = method.getReturnType();
                        setter = targetClass.getMethod(method.getName().replaceFirst("is", "set"), method.getReturnType());
                    } catch (NoSuchMethodException e) {
                        throw new CannotUnserializeException("The setter is impossible to find.");
                    }
                }

                if (setter == null) {
                    throw new CannotUnserializeException("No setter has been found for the class");
                }

                if (returnType == null) {
                    throw new CannotUnserializeException("No return type are available.");
                }

                // We can now build add to the map
                for (Class<? extends Annotation> annotationClass : serializableAnnotations) {
                    if (method.isAnnotationPresent(annotationClass)) {
                        result.get(annotationClass).put(method.getAnnotation(annotationClass), new ImmutablePair<>(setter, returnType));

                    }
                }

            }

        }
        return result;
    }
}
