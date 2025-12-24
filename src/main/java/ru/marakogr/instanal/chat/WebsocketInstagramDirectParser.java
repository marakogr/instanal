package ru.marakogr.instanal.chat;

import static ru.marakogr.instanal.chat.Constants.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import ru.marakogr.instanal.chat.model.*;
import ru.marakogr.instanal.utils.Utils;

@Slf4j
public class WebsocketInstagramDirectParser {
  private static final Base64.Decoder DECODER = Base64.getDecoder();
  private static final String URL_PATTERN = "[?&]u=([^&]+)";
  private static final Pattern URL_COMPILED_PATTERN = Pattern.compile(URL_PATTERN);
  private final Map<String, Message> idToMessageMap = new LinkedHashMap<>();
  private final List<JsonNode> steps = new ArrayList<>();
  private final String friendInstagramId;
  private final String chatId;
  private final JsonNode har;

  public WebsocketInstagramDirectParser(
      JsonNode har, String friendInstagramId, String ownerInstagramId) {
    this.har = har;
    this.friendInstagramId = friendInstagramId;
    this.chatId = Utils.getChatId(ownerInstagramId, friendInstagramId);
    parseHAR();
  }

  private void parseHAR() {
    log.info("start parsing har for friend: {}", friendInstagramId);
    idToMessageMap.clear();
    steps.clear();
    var entries = har.path("log").path("entries");
    for (var entry : entries) {
      var resourceType = entry.path("_resourceType").asText("");
      if ("websocket".equals(resourceType)) {
        var messages = entry.path("_webSocketMessages");
        if (!messages.isMissingNode() && messages.isArray()) {
          for (var msgNode : messages) {
            var type = msgNode.path("type");
            if ("receive".equals(type.asText())) {
              var data = msgNode.path("data").asText();
              if (data.length() >= 15) {
                try {
                  String parse = parse(data, friendInstagramId);
                  if (parse != null) {
                    var block = OBJECT_MAPPER.readTree(parse);
                    var step = block.path("step");
                    if (step.isArray()) {
                      processStep(step);
                    }
                  }
                } catch (Exception ignored) {
                  log.error("Error during parsing har file", ignored);
                }
              }
            }
          }
        }
      }
    }
    var messages = new ArrayList<>(idToMessageMap.values());
    messages.sort(Comparator.comparingLong(Message::getTimestamp));
    log.info("finished parsing har for friend: {}", friendInstagramId);
  }

  public List<Message> getMessages() {
    return new ArrayList<>(idToMessageMap.values());
  }

  private static String stripNonJsonPrefix(String s) {
    int idx = s.indexOf('{');
    if (idx == -1) {
      log.warn("No JSON object found in: {}", s);
      return null;
    }
    return s.substring(idx);
  }

  private static String parse(String base64, String friendInstagramId) throws Exception {
    var decoded = DECODER.decode(base64);
    var string = new String(decoded, StandardCharsets.UTF_8);
    var content = stripNonJsonPrefix(string);
    if (content != null) {
      var payloadStr = OBJECT_MAPPER.readTree(content).get("payload").asText();
      if (payloadStr.contains(friendInstagramId)) {
        return payloadStr;
      }
    }
    return null;
  }

  private void processStep(JsonNode step) {
    steps.add(step);
    for (var item : step) {
      if (!item.isArray() || item.size() <= 2) {
        continue;
      }
      if (item.get(0).asInt(-1) != 1) {
        continue;
      }
      var inner = item.get(2);
      if (!inner.isArray() || inner.size() < 3 || inner.get(0).asInt() != 23) {
        continue;
      }
      var events = inner.get(2);
      if (!events.isArray() || events.get(0).asInt() != 1) {
        continue;
      }
      var flatEvents =
          events
              .valueStream()
              .filter(JsonNode::isArray)
              .flatMap(JsonNode::valueStream)
              .filter(JsonNode::isArray)
              .filter(e -> e.get(0).intValue() == 5)
              .toList();
      for (var event : flatEvents) {
        if (!event.isContainerNode()) {
          continue;
        }
        String type = event.get(1).asText("");
        switch (type) {
          case "upsertMessage" -> handleUpsertMessage(event);
          case "insertXmaAttachment" -> handleXmaAttachment(event);
          case "insertAttachmentCta" -> handleAttachmentCta(event);
          case "insertAttachmentItem" -> handleAttachmentItem(event);
          case "upsertReaction" -> handleUpsertReaction(event);
        }
      }
    }
  }

  private void handleUpsertMessage(JsonNode p) {
    var mid = getString(p, 10);
    if (!mid.isEmpty()) {
      var uniqueMessage = idToMessageMap.computeIfAbsent(mid, k -> new Message(mid));
      var upsertMessage = new UpsertMessage();
      uniqueMessage.setUpsertMessage(upsertMessage);
      upsertMessage.setMid(mid);
      var senderId = getString(p, 12);
      upsertMessage.setSenderId(senderId);
      upsertMessage.setChatId(chatId);
      long timestamp = getLong(p, 7);
      upsertMessage.setTimestamp(timestamp);
      var date = LocalDate.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
      upsertMessage.setDate(date);
      for (int i = 2; i < 10; i++) {
        var s = getString(p, i);
        if (!s.startsWith("mid.$") && !s.matches("\\d+") && !s.isEmpty()) {
          upsertMessage.setText(s);
          break;
        }
      }
    }
  }

  private void handleXmaAttachment(JsonNode p) {
    var attachmentId = getString(p, 32);
    if (!attachmentId.isEmpty()) {
      var uniqueMessage = findByAnyMid(attachmentId);
      if (uniqueMessage == null) {
        uniqueMessage = new Message(attachmentId);
        idToMessageMap.put(attachmentId, uniqueMessage);
      }
      var xmaAttachment = new XmaAttachment();
      uniqueMessage.setXmaAttachment(xmaAttachment);
      xmaAttachment.setAttachmentId(attachmentId);
      xmaAttachment.setPreviewSmall(getString(p, 10));
      xmaAttachment.setPreviewLarge(getString(p, 116));
      xmaAttachment.setVideoTitle(getString(p, 79));
      xmaAttachment.setVideoDescription(getString(p, 125));
      xmaAttachment.setVideoAuthor(getString(p, 104));
    }
  }

  private void handleAttachmentCta(JsonNode p) {
    var attachmentId = getString(p, 7);
    var url = getString(p, 11);
    var cleanUrl = extractInstagramDirectUrl(url);
    var uniqueMessage = findByAnyMid(attachmentId);
    if (uniqueMessage != null) {
      uniqueMessage.setAttachmentCta(new AttachmentCta(attachmentId, cleanUrl));
      uniqueMessage.setHasReel(true);
    }
  }

  private void handleAttachmentItem(JsonNode p) {
    var attachmentId = getString(p, 6);
    var uniqueMessage = findByAnyMid(attachmentId);
    if (uniqueMessage != null) {
      uniqueMessage.setAttachmentItem(new AttachmentItem(attachmentId, null));
    }
  }

  private void handleUpsertReaction(JsonNode p) {
    var targetMid = getString(p, 4);
    var senderId = getString(p, 5);
    var reaction = getString(p, 6);
    long ts = getLong(p, 8);
    var reactionDate = LocalDate.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault());
    var uniqueMessage = idToMessageMap.computeIfAbsent(targetMid, Message::new);
    uniqueMessage.setReaction(new Reaction(senderId, null, reaction, ts, reactionDate));
    uniqueMessage.setHasReaction(true);
  }

  private Message findByAnyMid(String id) {
    return idToMessageMap.containsKey(id)
        ? idToMessageMap.get(id)
        : idToMessageMap.entrySet().stream()
            .filter(e -> e.getKey().contains(id) || id.contains(e.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);
  }

  private static String getString(JsonNode node, int index) {
    if (node.size() <= index) return "";
    var n = node.get(index);
    if (n == null || n.isNull()) return "";
    if (n.isTextual()) return n.asText();
    if (n.isArray() && n.size() == 2 && n.get(0).asInt(-1) == 19) {
      return n.get(1).asText("");
    }
    return "";
  }

  private static long getLong(JsonNode node, int index) {
    if (node.size() <= index) return 0L;
    var n = node.get(index);
    if (n == null || n.isNull()) return 0L;
    if (n.isNumber()) return n.asLong();
    if (n.isArray() && n.size() == 2 && n.get(0).asInt(-1) == 19) {
      return n.get(1).asLong(0L);
    }
    try {
      return Long.parseLong(n.asText("0"));
    } catch (Exception e) {
      return 0L;
    }
  }

  private static String extractInstagramDirectUrl(String facebookUrl) {
    if (facebookUrl == null || !facebookUrl.contains("l.facebook.com/l.php")) {
      return facebookUrl;
    }
    try {
      var matcher = URL_COMPILED_PATTERN.matcher(facebookUrl);
      if (matcher.find()) {
        var encoded = matcher.group(1);
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
      }
    } catch (Exception exception) {
      log.error("Error during direct reel url extraction", exception);
    }
    return facebookUrl;
  }
}
