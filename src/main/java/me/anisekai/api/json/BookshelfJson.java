package me.anisekai.api.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("ChainOfInstanceofChecks")
public class BookshelfJson extends JSONObject {

    public static final String EMPTY_JSON_STRING = "{}";

    private static final String S_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public BookshelfJson() {

        super();
    }

    /**
     * Construct a JSONObject from a byte source.
     *
     * @param bytes
     *         A byte array describing a JSON String
     *
     * @throws JSONException
     *         If there is a syntax error in the source string or a duplicated key.
     */
    public BookshelfJson(byte[] bytes) {

        this(new String(bytes, StandardCharsets.UTF_8));
    }

    /**
     * Construct a JSONObject from a source JSON text string. This is the most commonly used JSONObject constructor.
     *
     * @param source
     *         A string beginning with <code>{</code>&nbsp;<small>(left brace)</small> and ending with
     *         <code>}</code> &nbsp;<small>(right brace)</small>.
     *
     * @throws JSONException
     *         If there is a syntax error in the source string or a duplicated key.
     */
    public BookshelfJson(String source) throws JSONException {

        super(source);
    }

    /**
     * Construct a JSONObject from a Map.
     *
     * @param map
     *         A map object that can be used to initialize the contents of the JSONObject.
     *
     * @throws JSONException
     *         If a value in the map is non-finite number.
     * @throws NullPointerException
     *         If a key in the map is <code>null</code>
     */
    public BookshelfJson(Map<?, ?> map) {

        super(map);
    }

    /**
     * Construct a JSONObject from a ResourceBundle.
     *
     * @param baseName
     *         The ResourceBundle base name.
     * @param locale
     *         The Locale to load the ResourceBundle for.
     *
     * @throws JSONException
     *         If any JSONExceptions are detected.
     */
    public BookshelfJson(String baseName, Locale locale) throws JSONException {

        super(baseName, locale);
    }

    /**
     * Constructor to specify an initial capacity of the internal map. Useful for library internal calls where we know,
     * or at least can best guess, how big this JSONObject will be.
     *
     * @param initialCapacity
     *         initial capacity of the internal map.
     */
    public BookshelfJson(int initialCapacity) {

        super(initialCapacity);
    }

    /**
     * Create a new instance of {@link BookshelfJson} from an existing {@link JSONObject}.
     *
     * @param source
     *         A {@link JSONObject} from which this {@link BookshelfJson} will be initialized.
     */
    public BookshelfJson(JSONObject source) {

        this(source.toString());
    }

    private Object getOrNull(String key) throws JSONException {

        return this.has(key) ? this.get(key) : NULL;
    }

    /**
     * Return the value of the provided key in the current {@link JSONObject}. If the key doesn't exist,
     * <code>null</code> will be returned.
     *
     * @param key
     *         The JSON key
     *
     * @return The value of the provided key.
     */
    public Integer readInt(String key) {

        return this.getOrNull(key) == NULL ? null : this.getInt(key);
    }

    public Short readShort(String key) {

        return this.getOrNull(key) == NULL ? null : Short.parseShort(String.valueOf(this.getInt(key)));
    }

    /**
     * Return the value of the provided key in the current {@link JSONObject}. If the key doesn't exist,
     * <code>null</code> will be returned.
     *
     * @param key
     *         The JSON key
     *
     * @return The value of the provided key.
     */
    public String readString(String key) {

        return this.getOrNull(key) == NULL ? null : this.getString(key);
    }

    public Character getCharacter(String key) {

        String content = this.getString(key);
        if (content.length() != 1) {
            throw new JSONException(String.format("JSONObject[\"%s\"] is not a character.", key));
        }
        return content.charAt(0);
    }

    public Character readCharacter(String key) {

        return this.getOrNull(key) == NULL ? null : this.getCharacter(key);
    }

    /**
     * Return the value of the provided key in the current {@link JSONObject}. If the key doesn't exist,
     * <code>null</code> will be returned.
     *
     * @param key
     *         The JSON key
     *
     * @return The value of the provided key.
     */
    public Double readDouble(String key) {

        return this.getOrNull(key) == NULL ? null : this.getDouble(key);
    }

    /**
     * Return the value of the provided key in the current {@link JSONObject}. If the key doesn't exist,
     * <code>null</code> will be returned.
     *
     * @param key
     *         The JSON key
     *
     * @return The value of the provided key.
     */
    public Long readLong(String key) {

        return this.getOrNull(key) == NULL ? null : this.getLong(key);
    }

    /**
     * Return the value of the provided key in the current {@link JSONObject}. If the key doesn't exist,
     * <code>null</code> will be returned.
     *
     * @param key
     *         The JSON key
     *
     * @return The value of the provided key.
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public Boolean readBoolean(String key) {

        return this.getOrNull(key) == NULL ? null : this.getBoolean(key);
    }

    /**
     * Return the value of the provided key in the current {@link JSONObject}. If the key doesn't exist,
     * <code>null</code> will be returned.
     *
     * @param key
     *         The JSON key
     *
     * @return The value of the provided key.
     */
    public LocalDateTime readTime(String key) {

        String time = this.readString(key);

        if (time != null) {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(S_DATE_FORMAT));
        }
        return null;
    }


    /**
     * Return a {@link List} of the provided key with the provided type.
     *
     * @param key
     *         The JSON key
     * @param type
     *         The list type
     * @param <T>
     *         The list type
     *
     * @return A {@link List} containing the provided key values.
     */
    public <T> List<T> readArray(String key, Class<T> type) {

        if (!this.has(key)) {
            return null;
        }

        List<T> list = new ArrayList<>();

        JSONArray array = this.getJSONArray(key);

        for (int i = 0; i < array.length(); i++) {
            Object o = array.get(i);

            if (type.isInstance(o)) {
                list.add(type.cast(o));
            }
        }

        return list;
    }

    /**
     * Return a {@link List} of the provided key with the provided type.
     *
     * @param key
     *         The JSON key
     *
     * @return A {@link List} containing the provided key values.
     */
    public List<Long> readLongArray(String key) {

        if (!this.has(key)) {
            return null;
        }

        List<Long> list  = new ArrayList<>();
        JSONArray  array = this.getJSONArray(key);

        for (int i = 0; i < array.length(); i++) {
            Object o = array.get(i);

            if (o instanceof Integer) {
                list.add(Long.valueOf((Integer) o));
            } else if (o instanceof Long) {
                list.add((Long) o);
            }
        }

        return list;
    }

    public BookshelfArray readBookshelfArray(String key) {

        if (!this.has(key)) {
            return null;
        }

        return new BookshelfArray(this.getJSONArray(key));
    }

    public BookshelfJson readBookshelfJson(String key) {

        if (!this.has(key)) {
            return null;
        }

        return new BookshelfJson(this.getJSONObject(key));
    }

    /**
     * Call the provided function if a {@link JSONObject} exists under the provided key and returns the function result.
     * Returns <code>null</code> if the key doesn't exist.
     *
     * @param key
     *         The JSON key
     * @param supplier
     *         The function to call if the JSON key if found
     * @param <T>
     *         Type returned by function
     *
     * @return The result of the function call, or <code>null</code> if the key doesn't exist.
     */
    public <T> T read(String key, Function<BookshelfJson, ? extends T> supplier) {

        return this.getOrNull(key) == NULL ? null : supplier.apply(new BookshelfJson(this.getJSONObject(key)
                                                                                         .toString()));
    }

    /**
     * Call the provided function if a {@link JSONArray} exists under the provided key and returns the function result.
     * Returns <code>null</code> if the key doesn't exist.
     *
     * @param key
     *         The JSON key
     * @param supplier
     *         The function to call if the JSON key if found
     * @param <T>
     *         Type returned by function
     *
     * @return The result of the function call, or <code>null</code> if the key doesn't exist.
     */
    public <T> List<T> readAll(String key, Function<BookshelfJson, T> supplier) {

        return Optional.ofNullable(this.readBookshelfArray(key))
                       .map(array -> array.map(supplier))
                       .orElseGet(Collections::emptyList);
    }

    @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
    private JSONObject putInPath(String key, Object value) throws JSONException {

        List<String> pathToConfig = Arrays.asList(key.split("\\."));

        JSONObject object = this;
        for (int i = 0; i < pathToConfig.size() - 1; i++) {
            if (!object.has(pathToConfig.get(i))) {
                object.put(pathToConfig.get(i), new JSONObject());
            }
            object = object.getJSONObject(pathToConfig.get(i));
        }
        return object.put(pathToConfig.get(pathToConfig.size() - 1), value);
    }

    @Override
    public JSONObject put(String key, boolean value) throws JSONException {

        if (key.contains(".")) {
            return this.putInPath(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public JSONObject put(String key, Collection<?> value) throws JSONException {

        if (key.contains(".")) {
            return this.putInPath(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public JSONObject put(String key, double value) throws JSONException {

        if (key.contains(".")) {
            return this.putInPath(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public JSONObject put(String key, float value) throws JSONException {

        if (key.contains(".")) {
            return this.putInPath(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public JSONObject put(String key, int value) throws JSONException {

        if (key.contains(".")) {
            return this.putInPath(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public JSONObject put(String key, long value) throws JSONException {

        if (key.contains(".")) {
            return this.putInPath(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public JSONObject put(String key, Map<?, ?> value) throws JSONException {

        if (key.contains(".")) {
            return this.putInPath(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public JSONObject put(String key, Object value) throws JSONException {

        if (key.contains(".")) {
            return this.putInPath(key, value == null ? JSONObject.NULL : value);
        }
        return super.put(key, value == null ? JSONObject.NULL : value);
    }

    public JSONObject put(String key, LocalDateTime date) throws JSONException {

        if (date == null) {
            return this.put(key, (Object) null);
        }

        String format = DateTimeFormatter.ofPattern(S_DATE_FORMAT).format(date);
        return this.put(key, format);
    }

    private <T> T executeInPath(String key, BiFunction<JSONObject, String, T> ifJsonObj, BiFunction<JSONArray, Integer, T> ifJsonArray) {

        List<String> pathToConfig = Arrays.asList(key.split("\\."));

        Object object = this;
        for (int i = 0; i < pathToConfig.size(); i++) {
            if (object instanceof JSONObject jsonObj) {
                if (i == pathToConfig.size() - 1) {
                    return ifJsonObj.apply(jsonObj, pathToConfig.get(i));
                } else if (jsonObj.has(pathToConfig.get(i))) {
                    object = jsonObj.get(pathToConfig.get(i));
                } else {
                    throw new JSONException("JSONObject[" + quote(key) + "{{" + i + "}}] not found.");
                }
            } else if (object instanceof JSONArray jsonArr) {
                int idx = Integer.parseInt(pathToConfig.get(i));
                if (i == pathToConfig.size() - 1) {
                    return ifJsonArray.apply(jsonArr, idx);
                } else if (idx < jsonArr.length()) {
                    object = jsonArr.get(idx);
                } else {
                    throw new JSONException("JSONArray[" + quote(key) + "{{" + i + "}}] not found.");
                }
            } else {
                throw new IllegalStateException("Unrecognized type");
            }
        }

        // We should never get here, but this is to suppress error/warning messages.
        throw new JSONException("JSONObject[" + quote(key) + "] not found.");
    }

    @Override
    public Object get(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::get, JSONArray::get);
        }
        return super.get(key);
    }

    @Override
    public boolean getBoolean(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::getBoolean, JSONArray::getBoolean);
        }
        return super.getBoolean(key);
    }

    @Override
    public double getDouble(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::getDouble, JSONArray::getDouble);
        }
        return super.getDouble(key);
    }

    @Override
    public float getFloat(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::getFloat, JSONArray::getFloat);
        }
        return super.getFloat(key);
    }

    @Override
    public int getInt(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::getInt, JSONArray::getInt);
        }
        return super.getInt(key);
    }

    @Override
    public JSONArray getJSONArray(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::getJSONArray, JSONArray::getJSONArray);
        }
        return super.getJSONArray(key);
    }

    @Override
    public JSONObject getJSONObject(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::getJSONObject, JSONArray::getJSONObject);
        }
        return super.getJSONObject(key);
    }

    @Override
    public long getLong(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::getLong, JSONArray::getLong);
        }
        return super.getLong(key);
    }

    @Override
    public String getString(String key) throws JSONException {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::getString, JSONArray::getString);
        }
        return super.getString(key);
    }

    private boolean hasInPath(String key) throws JSONException {

        List<String> pathToConfig = Arrays.asList(key.split("\\."));

        Object object = this;
        for (int i = 0; i < pathToConfig.size(); i++) {

            if (object instanceof JSONObject jsonObj) {
                if (i == pathToConfig.size() - 1) {
                    return jsonObj.has(pathToConfig.get(i));
                } else if (jsonObj.has(pathToConfig.get(i))) {
                    object = jsonObj.get(pathToConfig.get(i));
                } else {
                    throw new JSONException("JSONObject[" + quote(key) + "{{" + i + "}}] not found.");
                }
            } else if (object instanceof JSONArray jsonArr) {
                int idx = Integer.parseInt(pathToConfig.get(i));
                if (i == pathToConfig.size() - 1) {
                    return idx < jsonArr.length();
                } else if (idx < jsonArr.length()) {
                    object = jsonArr.get(idx);
                } else {
                    throw new JSONException("JSONArray[" + quote(key) + "{{" + i + "}}] not found.");
                }
            } else {
                throw new IllegalStateException("Unrecognized type");
            }
        }

        // We should never get here, but this is to suppress error/warning messages.
        return false;
    }

    /**
     * Get an optional value associated with a key.
     *
     * @param key
     *         A key string.
     *
     * @return An object which is the value, or null if there is no value.
     */
    @Override
    public Object opt(String key) {

        if (key.contains(".")) {
            return this.executeInPath(key, JSONObject::opt, JSONArray::opt);
        }
        return super.opt(key);
    }

    @Override
    public boolean has(String key) {

        if (key.contains(".")) {
            return this.hasInPath(key);
        }
        return super.has(key);
    }

    @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
    private Object removePath(String key) {

        List<String> pathToConfig = Arrays.asList(key.split("\\."));

        JSONObject object = this;
        for (int i = 0; i < pathToConfig.size(); i++) {
            if (i == pathToConfig.size() - 1) {
                return object.remove(pathToConfig.get(i));
            }
            object = object.getJSONObject(pathToConfig.get(i));
        }
        return null;
    }

    @Override
    public Object remove(String key) {

        if (key.contains(".")) {
            return this.removePath(key);
        }
        return super.remove(key);
    }

}
