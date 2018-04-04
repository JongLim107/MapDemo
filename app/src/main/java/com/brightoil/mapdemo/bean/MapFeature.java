package com.brightoil.mapdemo.bean;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by JongLim on 2018-03-08.
 */

public class MapFeature {

    private class Geometry {
        private String type;
        private float[] coordinates;
    }

    private String type;
    private String id;
    private Geometry geometry;
    private String geometry_name;
    private JsonObject properties;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGeometryName() {
        return geometry_name;
    }

    public void setGeometryName(String name) {
        this.geometry_name = name;
    }

    public JsonObject getProperties() {
        if (properties == null) {
            properties = new JsonObject();
        }
        return properties;
    }

    public void setProperties(JsonObject properties) {
        this.properties = properties;
    }

    public String getGeometryType() {
        return this.geometry.type;
    }

    public void setGeometryType(String type) {
        this.geometry.type = type;
    }

    public float[] getGeometryCoordinate() {
        return geometry.coordinates;
    }

    public void setGeometryCoordinate(float lat, float lng) {
        this.geometry.coordinates[0] = lng;
        this.geometry.coordinates[1] = lat;
    }

    public static MapFeature fromJson(String json) {
        GsonBuilder gson = new GsonBuilder();
        return gson.create().fromJson(json, MapFeature.class);
    }

    public String toJson() {
        GsonBuilder gson = new GsonBuilder();
        return gson.create().toJson(this);
    }

    /**
     * Convenience method to add a String member.
     *
     * @param key   name of the member
     * @param value the String value associated with the member
     * @since 1.0.0
     */
    public void addStringProperty(String key, String value) {
        getProperties().addProperty(key, value);
    }

    /**
     * Convenience method to add a Number member.
     *
     * @param key   name of the member
     * @param value the Number value associated with the member
     * @since 1.0.0
     */
    public void addNumberProperty(String key, Number value) {
        getProperties().addProperty(key, value);
    }

    /**
     * Convenience method to add a Boolean member.
     *
     * @param key   name of the member
     * @param value the Boolean value associated with the member
     * @since 1.0.0
     */
    public void addBooleanProperty(String key, Boolean value) {
        getProperties().addProperty(key, value);
    }

    /**
     * Convenience method to add a Character member.
     *
     * @param key   name of the member
     * @param value the Character value associated with the member
     * @since 1.0.0
     */
    public void addCharacterProperty(String key, Character value) {
        getProperties().addProperty(key, value);
    }

    /**
     * Convenience method to add a JsonElement member.
     *
     * @param key   name of the member
     * @param value the JsonElement value associated with the member
     * @since 1.0.0
     */
    public void addProperty(String key, JsonElement value) {
        getProperties().add(key, value);
    }

    /**
     * Convenience method to get a String member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    public String getStringProperty(String key) {
        return getProperties().get(key).getAsString();
    }

    /**
     * Convenience method to get a Number member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    public Number getNumberProperty(String key) {
        return getProperties().get(key).getAsNumber();
    }

    /**
     * Convenience method to get a Boolean member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    public Boolean getBooleanProperty(String key) {
        return getProperties().get(key).getAsBoolean();
    }

    /**
     * Convenience method to get a Character member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    public Character getCharacterProperty(String key) {
        return getProperties().get(key).getAsCharacter();
    }

    /**
     * Convenience method to get a JsonElement member.
     *
     * @param key name of the member
     * @return the value of the member, null if it doesn't exist
     * @since 1.0.0
     */
    public JsonElement getProperty(String key) {
        return getProperties().get(key);
    }

    /**
     * Removes the property from the object properties
     *
     * @param key name of the member
     * @return Removed {@code property} from the key string passed in through the parameter.
     * @since 1.0.0
     */
    public JsonElement removeProperty(String key) {
        return getProperties().remove(key);
    }

    /**
     * Convenience method to check if a member with the specified name is present in this object.
     *
     * @param key name of the member
     * @return true if there is the member has the specified name, false otherwise.
     * @since 1.0.0
     */
    public boolean hasProperty(String key) {
        return getProperties().has(key);
    }

    /**
     * Convenience method to check for a member by name as well as non-null value.
     *
     * @param key name of the member
     * @return true if member is present with non-null value, false otherwise.
     * @since 1.3.0
     */
    public boolean hasNonNullValueForProperty(String key) {
        return hasProperty(key) && !getProperty(key).isJsonNull();
    }
}
