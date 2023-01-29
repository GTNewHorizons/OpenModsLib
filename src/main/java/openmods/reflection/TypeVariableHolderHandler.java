package openmods.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import openmods.Log;

import org.objectweb.asm.Type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;

public class TypeVariableHolderHandler {

    public void fillAllHolders(ASMDataTable data) {
        final Map<Field, Class<?>> classTargetToSource = Maps.newHashMap();
        final Map<Field, Class<?>> fieldTargetToSource = Maps.newHashMap();

        for (ASMData target : data.getAll(TypeVariableHolder.class.getName()))
            findTargets(target, classTargetToSource, fieldTargetToSource);

        fillFields(classTargetToSource, fieldTargetToSource);
    }

    public static void initializeClass(Class<?> targetClass) {
        new TypeVariableHolderHandler().fillHolders(targetClass);
    }

    public void fillHolders(Class<?> targetClass) {
        final Map<Field, Class<?>> targetToSource = Maps.newHashMap();

        fillHolders(targetClass, targetToSource);

        for (Class<?> inner : targetClass.getDeclaredClasses()) fillHolders(inner, targetToSource);

        fillFields(targetToSource);
    }

    private static void fillHolders(Class<?> targetClass, final Map<Field, Class<?>> targetToSource) {
        final Field[] fields = targetClass.getDeclaredFields();

        final TypeVariableHolder clsAnnotation = targetClass.getAnnotation(TypeVariableHolder.class);
        if (clsAnnotation != null) {
            final Class<?> sourceClass = findSourceClass(clsAnnotation, targetClass);

            for (Field f : fields) {
                if (isValidField(f)) targetToSource.put(f, sourceClass);
            }
        }

        for (Field f : fields) {
            final TypeVariableHolder fieldAnnotation = f.getAnnotation(TypeVariableHolder.class);
            if (fieldAnnotation != null) {
                Preconditions.checkArgument(
                        isValidField(f),
                        "Field %s marked with TypeVariableHolder annotation must be static, non-final and have TypeVariable type",
                        f);
                final Class<?> sourceClass = findSourceClass(fieldAnnotation, targetClass);
                targetToSource.put(f, sourceClass);
            }
        }
    }

    private static Class<?> findSourceClass(TypeVariableHolder annotation, Class<?> targetClass) {
        if (annotation.value() == TypeVariableHolder.UseDeclaringType.class) return targetClass;
        else return annotation.value();
    }

    private static final Type USE_DECLARING_TYPE_MARKER = Type.getType(TypeVariableHolder.UseDeclaringType.class);

    private static void findTargets(ASMData target, Map<Field, Class<?>> classTargetToSource,
            Map<Field, Class<?>> fieldTargetToSource) {
        final String targetClassName = target.getClassName();
        final String targetObject = target.getObjectName();
        final Type sourceClassName = (Type) target.getAnnotationInfo().get("value");

        try {
            final Class<?> targetClass = Class.forName(targetClassName);
            final Class<?> sourceClass;
            if (sourceClassName == null || sourceClassName.equals(USE_DECLARING_TYPE_MARKER)) sourceClass = targetClass;
            else sourceClass = Class.forName(sourceClassName.getClassName());

            if (targetClassName.equals(targetObject)) addClassFields(classTargetToSource, targetClass, sourceClass);
            else addField(fieldTargetToSource, targetClass, targetObject, sourceClass);
        } catch (Exception e) {
            Log.warn(e, "Failed to fill type variable holder at %s:%s", targetClassName, targetObject);
        }
    }

    private static boolean isValidField(Field field) {
        final int modifiers = field.getModifiers();
        return field.getType() == TypeVariable.class && Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
    }

    private static void addField(Map<Field, Class<?>> fieldTargetToSource, Class<?> targetClass, String fieldName,
            Class<?> sourceClass) throws Exception {
        final Field f = targetClass.getDeclaredField(fieldName);
        Preconditions.checkArgument(
                isValidField(f),
                "Field %s marked with TypeVariableHolder annotation must be static, non-final and have TypeVariable type",
                f);
        fieldTargetToSource.put(f, sourceClass);
    }

    private static void addClassFields(Map<Field, Class<?>> classTargetToSource, Class<?> targetClass,
            Class<?> sourceClass) {
        for (Field f : targetClass.getDeclaredFields()) {
            if (isValidField(f)) classTargetToSource.put(f, sourceClass);
        }
    }

    private void fillFields(Map<Field, Class<?>> classTargetToSource, Map<Field, Class<?>> fieldTargetToSource) {
        // field parameters are more specific, so they override class-level entries
        final Map<Field, Class<?>> targetToSource = Maps.newHashMap(classTargetToSource);
        targetToSource.putAll(fieldTargetToSource);

        fillFields(targetToSource);
    }

    private void fillFields(Map<Field, Class<?>> fieldTargetToSource) {
        for (Map.Entry<Field, Class<?>> e : fieldTargetToSource.entrySet()) fillField(e.getKey(), e.getValue());
    }

    private final Map<Class<?>, Map<String, TypeVariable<?>>> sourceCache = Maps.newHashMap();

    private void fillField(Field targetField, Class<?> sourceClass) {
        try {
            final Map<String, TypeVariable<?>> sourceVariables = getSourceTypeVariables(sourceClass);

            final String variableName = targetField.getName();
            final TypeVariable<?> sourceTypeVariable = sourceVariables.get(variableName);
            Preconditions.checkState(
                    sourceTypeVariable != null,
                    "Can't find type variable '%s' in class '%s",
                    variableName,
                    sourceClass);

            targetField.setAccessible(true);
            targetField.set(null, sourceTypeVariable);
        } catch (Exception e) {
            Log.warn(e, "Failed to set field %s", targetField);
        }
    }

    private Map<String, TypeVariable<?>> getSourceTypeVariables(Class<?> sourceClass) {
        Map<String, TypeVariable<?>> result = sourceCache.get(sourceClass);
        if (result == null) {
            result = Maps.newHashMap();
            for (TypeVariable<?> t : sourceClass.getTypeParameters()) result.put(t.getName(), t);
            sourceCache.put(sourceClass, result);
        }
        return result;
    }

}
