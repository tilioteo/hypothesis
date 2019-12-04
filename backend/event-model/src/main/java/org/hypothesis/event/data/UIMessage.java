/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.data;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class UIMessage extends JsonMessage {

    private static final String MESSAGE_TYPE = "TYPE";
    private static final String MESSAGE_GROUP = "GROUP";
    private static final String MESSAGE_USER = "USER";
    private static final String MESSAGE_SENDER = "SENDER";
    private static final String MESSAGE_ROLES = "ROLES";
    private static final String MESSAGE_PACK = "PACK";
    private static final String MESSAGE_VIEW = "VIEW";

    protected UIMessage() {
    }

    public UIMessage(String type) {
        initJson();

        setType(type);
    }

    public static UIMessage fromJson(String string) {
        JsonObject json;
        try {
            json = Json.parse(string);
            String className = json.getString(MESSAGE_CLASS);
            if (UIMessage.class.getName().equals(className)) {
                UIMessage message = new UIMessage();
                message.json = json;

                return message;
            }

        } catch (Exception e) {
        }

        return null;
    }

    public String getType() {
        return getString(MESSAGE_TYPE);
    }

    protected void setType(String type) {
        setString(MESSAGE_TYPE, type);
    }

    public Long getGroupId() {
        return getLong(MESSAGE_GROUP);
    }

    public void setGroupId(Long groupId) {
        setLong(MESSAGE_GROUP, groupId);
    }

    public Long getUserId() {
        return getLong(MESSAGE_USER);
    }

    public void setUserId(Long userId) {
        setLong(MESSAGE_USER, userId);
    }

    public Long getSenderId() {
        return getLong(MESSAGE_SENDER);
    }

    public void setSenderId(Long userId) {
        setLong(MESSAGE_SENDER, userId);
    }

    public Long getPackId() {
        return getLong(MESSAGE_PACK);
    }

    public void setPackId(Long packId) {
        setLong(MESSAGE_PACK, packId);
    }

    public String getViewUid() {
        return getString(MESSAGE_VIEW);
    }

    public void setViewUid(String viewUid) {
        setString(MESSAGE_VIEW, viewUid);
    }

    public List<String> getRoles() {
        try {
            return toStringList(json.getArray(MESSAGE_ROLES));
        } catch (Exception e) {
        }

        return Collections.emptyList();
    }

    public void setRoles(String... roles) {
        if (roles != null && roles.length > 0) {
            json.put(MESSAGE_ROLES, toJsonArray(roles));
        } else {
            json.put(MESSAGE_ROLES, Json.createNull());
        }
    }

    private Long getLong(String type) {
        try {
            return (long) json.getNumber(type);
        } catch (Exception e) {
        }

        return null;
    }

    private void setLong(String type, Long value) {
        if (value != null) {
            json.put(type, value);
        } else {
            json.put(type, Json.createNull());
        }
    }

    private String getString(String type) {
        try {
            return json.getString(type);
        } catch (Exception e) {
        }

        return null;
    }

    private void setString(String type, String value) {
        if (value != null) {
            json.put(type, value);
        } else {
            json.put(type, Json.createNull());
        }
    }

    private List<String> toStringList(JsonArray array) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); ++i) {
            list.add(array.getString(i));
        }

        return list;
    }

    private JsonArray toJsonArray(String[] values) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < values.length; ++i) {
            array.set(i, values[i]);
        }
        return array;
    }
}
