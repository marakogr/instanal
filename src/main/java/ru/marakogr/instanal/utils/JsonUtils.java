package ru.marakogr.instanal.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import ru.marakogr.instanal.chat.Constants;

@UtilityClass
public final class JsonUtils {
  public static ObjectNode readTree(Object object) {
    return object == null ? null : mapper().valueToTree(object);
  }

  public static JsonNode readContainerNode(Object object) {
    if (object == null) {
      return null;
    }
    if (object instanceof Collection<?> collection) {
      var arrayNode = mapper().createArrayNode();
      collection.forEach(item -> arrayNode.add(readContainerNode(item)));
      return arrayNode;
    }
    return mapper().valueToTree(object);
  }

  private static ObjectMapper mapper() {
    return Constants.OBJECT_MAPPER;
  }

  public static String getFileAsString(final String filePath) {
    try (InputStream is = getInputStream(filePath)) {
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Cant read file:%s content as String".formatted(filePath), e);
    }
  }

  public static <T> T getFileAsObject(final String filePath, final Class<T> clazz) {
    String string = getFileAsString(filePath);
    return stringAsObject(string, clazz);
  }

  public static <T> T stringAsObject(String value, TypeReference<T> type) {
    try {
      return mapper().readValue(value, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Can't convert Value to Object", e);
    }
  }

  public static <T> T stringAsObject(String value, Class<T> clazz) {
    try {
      return mapper().readValue(value, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Can't convert Value to Object", e);
    }
  }

  public static <T> List<T> convertCollection(Object collectionObject, Class<T> clazz) {
    try {
      var collectionNode = JsonUtils.readContainerNode(collectionObject);
      var collectionType = getCollectionType(clazz);
      return JsonUtils.mapper().readValue(collectionNode.toString(), collectionType);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Can't convert collection", e);
    }
  }

  public static <T> T convert(ObjectNode objectNode, Class<T> clazz) {
    try {
      return mapper().treeToValue(objectNode, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Can't convert ObjectNode to Object", e);
    }
  }

  /**
   * Return either an object's json string or a plain text values.
   *
   * @param pathToValue path to value
   * @param node root node
   * @return string value
   */
  public static List<String> getValuesByJsonPath(String pathToValue, ObjectNode node) {
    List<JsonNode> result = new ArrayList<>();
    getValuesByJsonPath(pathToValue, node, result);
    return result.stream().map(JsonUtils::getNodeString).filter(Objects::nonNull).toList();
  }

  public static Optional<String> findValueByJsonPath(String pathToValue, ObjectNode node) {
    return getValuesByJsonPath(pathToValue, node).stream()
        .filter(StringUtils::isNotBlank)
        .findAny();
  }

  /**
   * Return either an object's json string or throw exception.
   *
   * @param pathToValue path to value
   * @param node root node
   * @return string value
   */
  public static String getValueByJsonPathOrThrow(String pathToValue, ObjectNode node) {
    return findValueByJsonPath(pathToValue, node)
        .orElseThrow(
            () -> new RuntimeException("Value is not found by path: %s".formatted(pathToValue)));
  }

  /**
   * Return either an object's json string or null
   *
   * @param pathToValue path to value
   * @param node root node
   * @return string value
   */
  public static String getValueByJsonPath(String pathToValue, ObjectNode node) {
    return findValueByJsonPath(pathToValue, node).orElse(null);
  }

  /**
   * Return result ObjectNode or empty if it's terminal value.
   *
   * @param pathToValue path to value
   * @param node root node
   * @return string value
   */
  public static List<ObjectNode> getNodesByJsonPath(String pathToValue, ObjectNode node) {
    if (pathToValue == null || pathToValue.isEmpty()) {
      return Collections.emptyList();
    }
    List<JsonNode> result = new ArrayList<>();
    getValuesByJsonPath(pathToValue, node, result);
    return result.stream().filter(JsonNode::isContainerNode).map(e -> (ObjectNode) e).toList();
  }

  public static ObjectNode getNodeByJsonPath(String pathToValue, ObjectNode node) {
    return getNodesByJsonPath(pathToValue, node).stream()
        .filter(JsonNode::isContainerNode)
        .findAny()
        .orElse(null);
  }

  public static <T> Optional<String> findStringFromObjectByJsonPath(String pathToValue, T object) {
    ObjectNode node = readTree(object);
    return findValueByJsonPath(pathToValue, node);
  }

  private static void getValuesByJsonPath(
      String pathToValue, JsonNode node, List<JsonNode> result) {
    if (node == null) {
      return;
    }
    var split = pathToValue.split("\\.");
    var path = split[0];
    var pathNode = node.at(constructPath(path));
    if (split.length == 1) {
      collectResult(path, node, pathNode, result);
    } else {
      var index = pathToValue.indexOf(".") + 1;
      var subPathToValue = pathToValue.substring(index);
      collectProcess(path, subPathToValue, node, pathNode, result);
    }
  }

  public ObjectNode getJsonFileAsNode(final String filePath) {
    try (InputStream is = getInputStream(filePath)) {
      var text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      return (ObjectNode) mapper().readTree(text);
    } catch (IOException e) {
      throw new RuntimeException("Can't read file:%s content as String".formatted(filePath), e);
    }
  }

  public static <T> List<T> getJsonFileAsList(final String filePath, final Class<T> clazz) {
    try (InputStream is = getInputStream(filePath)) {
      var text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      return mapper().readValue(text, getCollectionType(clazz));
    } catch (IOException e) {
      throw new RuntimeException(
          ("Can't read file:%s content " + "as specific class collection").formatted(filePath), e);
    }
  }

  private static <T> CollectionType getCollectionType(final Class<T> clazz) {
    return mapper().getTypeFactory().constructCollectionType(ArrayList.class, clazz);
  }

  private void collectResult(String path, JsonNode node, JsonNode pathNode, List<JsonNode> result) {
    if ("$".equals(path)) {
      node.fields()
          .forEachRemaining(
              e -> {
                var element = JsonUtils.mapper().createObjectNode().set(e.getKey(), e.getValue());
                result.add(element);
              });
    } else if (pathNode.isArray()) {
      result.addAll(collectArray(pathNode));
    } else {
      result.add(pathNode);
    }
  }

  private void collectProcess(
      String path, String subPathToValue, JsonNode node, JsonNode pathNode, List<JsonNode> result) {
    if ("$".equals(path)) {
      node.fields()
          .forEachRemaining(e -> getValuesByJsonPath(subPathToValue, e.getValue(), result));
    } else if (pathNode.isArray()) {
      for (JsonNode element : pathNode) {
        getValuesByJsonPath(subPathToValue, element, result);
      }
    } else if (pathNode.isObject()) {
      getValuesByJsonPath(subPathToValue, pathNode, result);
    }
  }

  private static List<JsonNode> collectArray(JsonNode node) {
    List<JsonNode> result = new ArrayList<>();
    var elements = node.elements();
    while (elements.hasNext()) {
      result.add(elements.next());
    }
    return result;
  }

  private static String getNodeString(JsonNode node) {
    if (node == null || node.isNull()) {
      return null;
    }
    if (node.isTextual()) {
      return node.asText(null);
    }
    return Optional.ofNullable(node.toString()).filter(StringUtils::isNotEmpty).orElse(null);
  }

  private static String constructPath(String pathToValue) {
    return "/" + pathToValue.replace(".", "/");
  }

  private static InputStream getInputStream(String filePath) {
    var inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
    if (inputStream == null) {
      throw new RuntimeException("Can't read file:%s content as Input Stream".formatted(filePath));
    }
    return inputStream;
  }

  public static <T> T convertObject(Object value, Class<T> clazz) {
    return mapper().convertValue(value, clazz);
  }

  public static ObjectNode setNodeByJsonPath(
      ObjectNode node, String pathToValue, JsonNode value, List<String> arrayPath) {
    var pathChain = pathToValue.split("\\.");
    if (pathChain.length == 1) {
      if (arrayPath != null && arrayPath.contains(pathChain[0])) {
        node.set(pathChain[0], mapper().createArrayNode().add(value));
      } else {
        node.set(pathChain[0], value);
      }
      return node;
    }
    var target = node.at("/" + pathToValue.replace(".", "/"));
    if (target instanceof ArrayNode arrayNode) {
      if (value instanceof ArrayNode arrayValue) {
        arrayNode.removeAll();
        arrayNode.addAll(arrayValue);
      } else {
        arrayNode.add(value);
      }
      return node;
    }
    target = node.at("/" + cutLastChain(pathToValue).replace(".", "/"));
    if (target instanceof ObjectNode objectNode) {
      objectNode.set(
          getLastChain(pathToValue),
          arrayPath != null && arrayPath.contains(getLastChain(pathToValue))
              ? mapper().createArrayNode().add(value)
              : value);
    } else if (arrayPath != null && target instanceof ArrayNode arrayNode) {
      if (isPrimitive(value)) {
        arrayNode.add(
            arrayPath.contains(pathToValue) ? mapper().createArrayNode().add(value) : value);
      } else {
        arrayNode.add(
            arrayPath.contains(pathToValue)
                ? mapper()
                    .createObjectNode()
                    .set(getLastChain(pathToValue), mapper().createArrayNode().add(value))
                : value);
      }
    } else if (arrayPath != null && target.isEmpty()) {
      setNotExistedNode(node, pathToValue, value, arrayPath);
    }
    return node;
  }

  private static void setNotExistedNode(
      ObjectNode node, String pathToValue, Object value, List<String> arrayPath) {
    Map<Boolean, String> existedPath = new HashMap<>();
    var lastExisted = getLastExisted(node, pathToValue, existedPath);
    var created = createMissingNodes(existedPath.get(false), value, arrayPath);
    if (lastExisted instanceof ArrayNode arrayNode) {
      arrayNode.add(created);
    } else if (lastExisted instanceof ObjectNode objectNode) {
      objectNode.setAll(created);
    }
  }

  public static JsonNode getLastExisted(
      ObjectNode node, String pathToValue, Map<Boolean, String> pathToExisted) {
    var target = node.at("/" + pathToValue.replace(".", "/"));
    if (target.isEmpty() && pathToValue.contains(".")) {
      target = getLastExisted(node, cutLastChain(pathToValue), pathToExisted);
    } else if (target.isEmpty()) {
      pathToExisted.putIfAbsent(true, "");
      pathToExisted.put(false, getNotExistedPath(pathToValue, pathToExisted.get(true)));
      return node;
    }
    pathToExisted.putIfAbsent(true, pathToValue);
    pathToExisted.put(false, getNotExistedPath(pathToValue, pathToExisted.get(true)));
    return target;
  }

  private static String getNotExistedPath(String pathToValue, String pathToExisted) {
    if (pathToValue.equals(pathToExisted)) {
      return "";
    }
    var length = pathToExisted.length() + (pathToExisted.startsWith(".") ? 1 : 0);
    return pathToValue.substring(length + (pathToExisted.isEmpty() ? 0 : 1));
  }

  public static ObjectNode setNodeByJsonPath(ObjectNode node, String pathToValue, JsonNode value) {
    return setNodeByJsonPath(node, pathToValue, value, null);
  }

  public static void setValueToNodeByPath(ObjectNode node, String path, Object value) {
    setValueToNodeByPath(node, path, value, null);
  }

  public static void setValueToNodeByPath(
      ObjectNode node, String path, Object value, List<String> arrayPath) {
    if (node == null || StringUtils.isEmpty(path) || value == null) {
      return;
    }
    if (isPrimitive(value)) {
      if (!path.contains(".")) {
        setPrimitiveValueToField(node, path, value);
        return;
      }
      var nodeByPath = findNodeByPath(cutLastChain(path), node);
      nodeByPath.ifPresentOrElse(
          n -> {
            var targetPath = getLastChain(path);
            setPrimitiveValueToField(n, targetPath, value);
          },
          () -> {
            if (arrayPath != null) {
              setNotExistedNode(node, path, value, arrayPath);
            }
          });
    } else {
      setNodeByJsonPath(node, path, readTree(value), arrayPath);
    }
  }

  private static boolean isPrimitive(Object value) {
    return ClassUtils.isPrimitiveOrWrapper(value.getClass())
        || value.getClass().isAssignableFrom(String.class);
  }

  private void setPrimitiveValueToField(ObjectNode node, String field, Object value) {
    if (value instanceof Long l) {
      node.put(field, l);
    } else if (value instanceof String s) {
      node.put(field, s);
    } else if (value instanceof Boolean b) {
      node.put(field, b);
    } else if (value instanceof Integer i) {
      node.put(field, i);
    } else if (value instanceof Character c) {
      node.put(field, c);
    } else if (value instanceof Double d) {
      node.put(field, d);
    } else if (value instanceof Float f) {
      node.put(field, f);
    } else if (value instanceof BigDecimal d) {
      node.put(field, d);
    }
  }

  private void addPrimitiveValueToArrayNode(ArrayNode node, Object value) {
    if (value instanceof Long l) {
      node.add(l);
    } else if (value instanceof String s) {
      node.add(s);
    } else if (value instanceof Boolean b) {
      node.add(b);
    } else if (value instanceof Integer i) {
      node.add(i);
    } else if (value instanceof Character c) {
      node.add(c);
    } else if (value instanceof Double d) {
      node.add(d);
    } else if (value instanceof Float f) {
      node.add(f);
    } else if (value instanceof BigDecimal d) {
      node.add(d);
    }
  }

  public static Optional<ObjectNode> findNodeByPath(String path, ObjectNode node) {
    return Optional.ofNullable(getNodeByJsonPath(path, node));
  }

  private static void removeNullsFromMap(Map<?, ?> map) {
    map.values().removeIf(Objects::isNull);
    for (Object value : map.values()) {
      if (value instanceof Map<?, ?> v) {
        removeNullsFromMap(v);
      } else if (value instanceof Collection<?> collection) {
        collection.forEach(
            e -> {
              if (e instanceof Map<?, ?> m) {
                removeNullsFromMap(m);
              }
            });
      }
    }
  }

  private static Map<String, Object> diffAsMap(
      Map<String, Object> source, Map<String, Object> target) {
    Map<String, Object> result = new HashMap<>();
    source.forEach((k, v) -> fillDiff(target, k, v, result));
    target.forEach((k, v) -> fillDiff(source, k, v, result));
    return result;
  }

  @SuppressWarnings("unchecked")
  private static void fillDiff(
      Map<String, Object> source,
      String targetKey,
      Object targetValue,
      Map<String, Object> result) {
    var sourceValue = source.get(targetKey);
    if ("null".equals(targetValue)) {
      targetValue = null;
    }
    if ("null".equals(sourceValue)) {
      sourceValue = null;
    }
    if (sourceValue == null) {
      result.put(targetKey, targetValue);
    } else if (targetValue == null) {
      result.put(targetKey, sourceValue);
    } else if (sourceValue.equals(targetValue)) {
      result.put(targetKey, null);
    } else if (isPrimitive(sourceValue)) {
      result.put(targetKey, targetValue);
    } else if (sourceValue instanceof Map<?, ?> mapValue) {
      result.putIfAbsent(targetKey, new HashMap<>());
      Map<String, Object> innerDiff = (Map<String, Object>) result.get(targetKey);
      Map<String, Object> targetValueMap = (Map<String, Object>) targetValue;
      targetValueMap.forEach((k, v) -> fillDiff((Map<String, Object>) mapValue, k, v, innerDiff));
    } else if (sourceValue instanceof List<?> listValue) {
      result.putIfAbsent(targetKey, new ArrayList<>());
      List<Object> innerDiff = (List<Object>) result.get(targetKey);
      List<Object> targetValueList = (List<Object>) targetValue;
      List<Object> sourceValueList = (List<Object>) listValue;
      targetValueList.forEach(
          v -> {
            if (!sourceValueList.contains(v)) {
              innerDiff.add(v);
            }
          });
    }
  }

  private String getLastChain(String path) {
    String[] pathChain = path.split("\\.");
    if (pathChain.length > 1) {
      return pathChain[pathChain.length - 1];
    }
    return path;
  }

  private String cutLastChain(String path) {
    String[] pathChain = path.split("\\.");
    if (pathChain.length > 1) {
      return path.substring(0, path.length() - pathChain[pathChain.length - 1].length() - 1);
    }
    return path;
  }

  public ObjectNode getNodeAtPath(String path, ObjectNode node) {
    if (path == null || path.isEmpty() || node == null) {
      return node;
    }
    var result = node.at(constructPath(path));
    if (result instanceof ObjectNode objectNode) {
      return objectNode;
    }
    return null;
  }

  public static <T> String objectAsString(final T body) {
    try {
      return mapper().writeValueAsString(body);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Can't convert Value to String", e);
    }
  }

  public static JsonNode getNodeByJsonPath(JsonNode node, String pathToValue) {
    if (node != null) {
      var target = node.at("/" + pathToValue.replace(".", "/"));
      return target.isNull() ? null : target;
    }
    return null;
  }

  private static ObjectNode createMissingNodes(
      String missedPaths, Object value, List<String> arrayPath) {

    boolean rootIsArray = arrayPath.contains(missedPaths.split("\\.")[0]);
    JsonNode root = rootIsArray ? mapper().createArrayNode() : mapper().createObjectNode();

    String[] paths = missedPaths.split("\\.");
    JsonNode currentNode = root;

    for (int i = 0; i < paths.length; i++) {
      String path = paths[i];

      String currentPath = String.join(".", Arrays.copyOfRange(paths, 0, i + 1));
      boolean isArray = arrayPath.contains(currentPath);

      if (i == paths.length - 1) {
        createNodeWithValue(value, isArray, currentNode, path);
      } else {
        currentNode = createIntermediateNode(isArray, currentNode, path);
      }
    }

    if (rootIsArray && root instanceof ArrayNode arrayNode) {
      ObjectNode wrapper = mapper().createObjectNode();
      wrapper.set(paths[0], arrayNode);
      root = wrapper;
    }

    return (ObjectNode) root;
  }

  private static JsonNode createIntermediateNode(
      boolean isArray, JsonNode currentNode, String path) {
    if (isArray) {
      if (currentNode.isObject()) {
        if (!currentNode.has(path)) {
          ((ObjectNode) currentNode).putArray(path);
        }
        currentNode = currentNode.get(path);
      }
      if (currentNode.isArray()) {
        ArrayNode arrayNode = (ArrayNode) currentNode;
        if (arrayNode.isEmpty() || !arrayNode.get(0).isObject()) {
          arrayNode.add(mapper().createObjectNode());
        }
        currentNode = arrayNode.get(0);
      }
    } else {
      if (currentNode.isObject()) {
        if (!currentNode.has(path)) {
          ((ObjectNode) currentNode).putObject(path);
        }
        currentNode = currentNode.get(path);
      }
    }
    return currentNode;
  }

  private static void createNodeWithValue(
      Object value, boolean isArray, JsonNode currentNode, String path) {
    if (isArray) {
      if (currentNode instanceof ArrayNode arrayNode) {
        arrayNode.addArray();
      } else if (currentNode.isObject()) {
        ArrayNode arrayNode = ((ObjectNode) currentNode).putArray(path);
        if (isPrimitive(value)) {
          addPrimitiveValueToArrayNode(arrayNode, value);
        } else {
          arrayNode.add(readTree(value));
        }
      }
    } else {
      if (currentNode instanceof ObjectNode objectNode) {
        if (isPrimitive(value)) {
          setPrimitiveValueToField(objectNode, path, value);
        } else {
          objectNode.set(path, readTree(value));
        }
      }
    }
  }
}
