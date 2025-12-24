package ru.marakogr.instanal.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@UtilityClass
public class ReflectUtils {

  public static String getValueByPathAsStringOrNull(String path, Object source) {
    var value = getValueByPathAsObjectOrNull(path, source);
    return value == null ? null : String.valueOf(value);
  }

  public static Object getValueByPathAsObjectOrNull(String path, Object source) {
    if (source == null) {
      return null;
    }
    String[] paths = path.split("\\.");
    Object from = source;
    Object value = null;
    for (int i = 0; i < paths.length; i++) {
      switch (from) {
        case Map<?, ?> map -> {
          value = map.get(paths[i]);
          if (value == null) {
            return null;
          }
          from = value;
          continue;
        }
        case Collection<?> collection -> {
          int index = i;
          var valueCollect =
              collection.stream()
                  .map(
                      e ->
                          getValueByPathAsStringOrNull(
                              String.join(".", Arrays.copyOfRange(paths, index, paths.length)), e))
                  .filter(Objects::nonNull)
                  .collect(Collectors.joining(", "));

          if (!StringUtils.hasText(valueCollect)) {
            return null;
          }
          return valueCollect;
        }
        case ObjectNode node -> {
          return JsonUtils.getNodesByJsonPath(path, node);
        }
        default -> {}
      }

      var field = ReflectionUtils.findField(from.getClass(), paths[i]);
      if (field == null) {
        return null;
      }
      field.setAccessible(true);
      value = ReflectionUtils.getField(field, from);
      if (value == null) {
        return null;
      }
      from = value;
    }
    return value;
  }

  public static Optional<String> findValueByPathAsString(String path, Object source) {
    return Optional.ofNullable(getValueByPathAsStringOrNull(path, source));
  }

  public static List<String> findAllValuesByPathAsStringList(String path, Object source) {
    if (source == null) {
      return List.of();
    }
    List<String> result = new ArrayList<>();
    String[] paths = path.split("\\.");
    Object from = source;
    Object value = null;
    for (int i = 0; i < paths.length; i++) {
      if (from instanceof Map<?, ?> map) {
        value = map.get(paths[i]);
        if (value == null) {
          return result;
        }
        from = value;
        continue;
      }
      if (from instanceof Collection<?> collection) {
        int index = i;
        var valueCollect =
            collection.stream()
                .flatMap(
                    e ->
                        findAllValuesByPathAsStringList(
                            String.join(".", Arrays.copyOfRange(paths, index, paths.length)), e)
                            .stream())
                .toList();

        if (CollectionUtils.isEmpty(valueCollect)) {
          return result;
        }
        result.addAll(valueCollect);
        return result;
      }
      var field = ReflectionUtils.findField(from.getClass(), paths[i]);
      if (field == null) {
        return result;
      }
      field.setAccessible(true);
      value = ReflectionUtils.getField(field, from);
      if (value == null) {
        return result;
      }
      from = value;
    }
    if (value != null) {
      result.add(String.valueOf(value));
    }
    return result;
  }

  public static <T> List<Map<String, Object>> listAsFlatMap(List<T> objs) {
    if (objs == null) {
      return List.of();
    }
    if (isPrimitive(objs)) {
      return List.of(Map.of("value", objs));
    }
    var declaredFields = new HashMap<Class<?>, List<Field>>();
    var result = new ArrayList<Map<String, Object>>(objs.size());
    for (Object obj : objs) {
      result.add(asFlatMap(obj, declaredFields, false));
    }
    return result;
  }

  public static Map<String, Object> asFlatMap(Object obj) {
    return asFlatMap(obj, false);
  }

  public static Map<String, Object> asFlatMapWithNested(Object obj) {
    return asFlatMap(obj, true);
  }

  private static Map<String, Object> asFlatMap(Object obj, boolean withNested) {
    if (obj == null) {
      return Map.of();
    }
    if (isPrimitive(obj)) {
      return Map.of("value", obj);
    }
    var declaredFields = new HashMap<Class<?>, List<Field>>();
    return asFlatMap(obj, declaredFields, withNested);
  }

  private static Map<String, Object> asFlatMap(
      Object obj, Map<Class<?>, List<Field>> fields, boolean withNested) {
    var result = new HashMap<String, Object>();
    fillMap(obj, "", result, fields, withNested);
    return result;
  }

  private static void fillMap(
      Object obj,
      String rootPath,
      Map<String, Object> result,
      Map<Class<?>, List<Field>> fields,
      boolean withNested) {
    if (obj == null) {
      result.put(rootPath, obj);
      return;
    }
    if (obj instanceof Map<?, ?> map) {
      if (CollectionUtils.isEmpty(map)) {
        result.put(rootPath, null);
        return;
      }
      if (withNested) {
        result.put(rootPath, map);
      }
      map.forEach(
          (k, v) ->
              fillMap(
                  v,
                  rootPath.isEmpty() ? (String) k : rootPath + "." + k,
                  result,
                  fields,
                  withNested));
      return;
    }
    if (obj instanceof List<?> list) {
      if (CollectionUtils.isEmpty(list)) {
        result.put(rootPath, list);
        return;
      }
      if (withNested) {
        result.put(rootPath, list);
      }
      int size = list.size();
      for (int i = 0; i < size; i++) {
        var value = list.get(i);
        fillMap(value, rootPath + "[%d]".formatted(i), result, fields, withNested);
      }
      return;
    }
    if (isPrimitive(obj)) {
      result.put(rootPath, obj);
      return;
    }
    if (obj instanceof Enum<?> val) {
      result.put(rootPath, String.valueOf(val));
      return;
    }
    if (withNested) {
      result.put(rootPath, JsonUtils.objectAsString(obj));
    }
    var declaredFields = getAccessibleFields(obj, fields);
    for (var field : declaredFields) {
      var value = ReflectionUtils.getField(field, obj);
      var path = rootPath.isEmpty() ? field.getName() : rootPath + "." + field.getName();
      fillMap(value, path, result, fields, withNested);
    }
  }

  private static boolean isPrimitive(Object value) {
    return ClassUtils.isPrimitiveOrWrapper(value.getClass())
        || value.getClass().isAssignableFrom(String.class);
  }

  private static List<Field> getAccessibleFields(Object object, Map<Class<?>, List<Field>> fields) {
    var cls = object.getClass();
    return fields.computeIfAbsent(cls, ReflectUtils::getAccessibleFields);
  }

  private static List<Field> getAccessibleFields(Class<?> cls) {
    var fields = new ArrayList<Field>();
    while (!cls.equals(Object.class)) {
      fields.addAll(Arrays.asList(cls.getDeclaredFields()));
      cls = cls.getSuperclass();
    }
    for (Field field : fields) {
      field.setAccessible(true);
    }
    return fields;
  }

  public static void setValueByPath(String path, Object value, Object source) {
    if (source == null || value == null || path == null) {
      return;
    }
    if (source instanceof LinkedHashMap<?, ?> map) {
      setValueByPath(path, value, map);
    } else {
      if (path.contains(".")) {
        var root = path.split("\\.")[0];
        var accessibleField = getAccessibleField(root, source);
        if (accessibleField.isEmpty()) {
          return;
        }
        var containerValue = ReflectionUtils.getField(accessibleField.get(), source);
        if (containerValue == null) {
          log.debug("There is no value in path {} class {}", root, source.getClass());
          return;
        }
        var subPath = path.substring(path.indexOf(".") + 1);
        setValueByPath(subPath, value, containerValue);
      }
      getAccessibleField(path, source)
          .ifPresent(field -> ReflectionUtils.setField(field, source, value));
    }
  }

  public static Optional<Field> getAccessibleField(String path, Object source) {
    var field = ReflectionUtils.findField(source.getClass(), path);
    if (field == null) {
      log.debug("There is no field {} in class {}", path, source.getClass());
      return Optional.empty();
    }
    field.setAccessible(true);
    return Optional.of(field);
  }

  private static void setValueByPath(String path, Object value, LinkedHashMap source) {
    if (source == null || value == null || path == null) {
      return;
    }
    if (path.contains(".")) {
      var root = path.split("\\.")[0];
      setValueByPath(path.substring(path.indexOf(".") + 1), value, source.get(root));
    } else {
      source.put(path, value);
    }
  }

  public static Class<?> getFieldClass(String fieldName, Class<?> clazz) {
    for (var field : fieldName.split("\\.")) {
      try {
        var declaredField = getField(clazz, field);
        clazz = declaredField.getType();
        if (Collection.class.isAssignableFrom(clazz)) {
          var type = (ParameterizedType) declaredField.getGenericType();
          clazz = (Class<?>) type.getActualTypeArguments()[0];
        }
      } catch (NoSuchFieldException e) {
        return String.class;
      }
    }
    return clazz;
  }

  public static boolean hasField(String path, Object source) {
    if (source == null || path == null || path.isEmpty()) {
      return false;
    }

    String[] paths = path.split("\\.");
    Object current = source;

    for (String pathPart : paths) {
      switch (current) {
        case null -> {
          return false;
        }
        case Map<?, ?> map -> {
          if (!map.containsKey(pathPart)) {
            return false;
          }
          current = map.get(pathPart);
          continue;
        }
        case Collection<?> collection -> {
          return collection.stream()
              .anyMatch(
                  element ->
                      hasField(
                          String.join(
                              ".",
                              Arrays.copyOfRange(
                                  paths, Arrays.asList(paths).indexOf(pathPart), paths.length)),
                          element));
        }
        case ObjectNode objectNode -> {
          try {
            return JsonUtils.getNodesByJsonPath(path, objectNode) != null;
          } catch (Exception e) {
            return false;
          }
        }
        default -> log.debug("Unknown type of object {} by path {}", current, pathPart);
      }

      Field field = ReflectionUtils.findField(current.getClass(), pathPart);
      if (field == null) {
        return false;
      }

      field.setAccessible(true);
      current = ReflectionUtils.getField(field, current);
    }

    return true;
  }

  private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    try {
      return clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      if (clazz.getSuperclass() != null) {
        return getField(clazz.getSuperclass(), fieldName);
      }
      throw e;
    }
  }
}
