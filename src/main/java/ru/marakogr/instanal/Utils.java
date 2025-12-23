package ru.marakogr.instanal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.marakogr.instanal.chat.Constants;
import ru.marakogr.instanal.db.model.FriendRelation;

public class Utils {

  public static String getChatId(String ownerId, String friendId) {
    return Stream.of(ownerId, friendId).sorted().collect(Collectors.joining("_"));
  }

  public static String getChatId(FriendRelation friendRelation) {
    return getChatId(
        friendRelation.getOwner().getInstagramId(),
        friendRelation.getFriendSuperUser().getInstagramId());
  }

  public static ObjectNode object(Object... kv) {
    ObjectNode node = Constants.OBJECT_MAPPER.createObjectNode();
    for (int i = 0; i < kv.length; i += 2) {
      String key = (String) kv[i];
      Object val = kv[i + 1];

      switch (val) {
        case String s -> node.put(key, s);
        case Integer n -> node.put(key, n);
        case Long n -> node.put(key, n);
        case Boolean b -> node.put(key, b);
        case JsonNode j -> node.set(key, j);
        case null, default -> throw new IllegalArgumentException("Unsupported: " + val);
      }
    }
    return node;
  }

  public static ArrayNode array(Object... values) {
    ArrayNode arr = Constants.OBJECT_MAPPER.createArrayNode();
    for (Object v : values) {
      switch (v) {
        case String s -> arr.add(s);
        case Long l -> arr.add(l);
        case Integer i -> arr.add(i);
        case null, default -> throw new IllegalArgumentException("Unsupported: " + v);
      }
    }
    return arr;
  }
}
