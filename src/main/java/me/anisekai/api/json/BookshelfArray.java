package me.anisekai.api.json;

import org.json.JSONArray;
import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BookshelfArray extends JSONArray {

    public BookshelfArray() {

        super();
    }

    public BookshelfArray(byte[] bytes) {

        this(new String(bytes, StandardCharsets.UTF_8));
    }

    /**
     * Create a new {@link BookshelfArray} instance from the provided string.
     *
     * @param source
     *         A string representation of a {@link JSONArray}.
     *
     * @throws JSONException
     *         If there is a syntax error.
     */
    public BookshelfArray(String source) throws JSONException {

        super(source);
    }

    /**
     * Create a new {@link BookshelfArray} instance from an existing {@link JSONArray} instance.
     *
     * @param array
     *         The {@link JSONArray} to transform into {@link BookshelfArray}.
     */
    public BookshelfArray(JSONArray array) {

        super(array.toString());
    }

    /**
     * Retrieve the {@link BookshelfJson} instance at the provided index in this {@link BookshelfArray}.
     *
     * @param index
     *         The index in {@link BookshelfArray} from which the {@link BookshelfJson} should be retrieved.
     *
     * @return A new {@link BookshelfJson} instance.
     */
    public BookshelfJson getBookshelfJson(int index) {

        return new BookshelfJson(this.getJSONObject(index));
    }

    /**
     * Retrieve the {@link BookshelfArray} instance at the provided index in this {@link BookshelfArray}.
     *
     * @param index
     *         The index in {@link BookshelfArray} from which the {@link BookshelfArray} should be retrieved.
     *
     * @return A new {@link BookshelfArray} instance.
     */
    public BookshelfArray getBookshelfArray(int index) {

        return new BookshelfArray(this.getJSONArray(index));
    }

    /**
     * Loop through this {@link BookshelfArray} for each {@link BookshelfJson}.
     *
     * @param action
     *         A {@link Consumer} accepting a {@link BookshelfJson} instance.
     */
    public void forEachJson(Consumer<BookshelfJson> action) {

        for (int i = 0; i < this.length(); i++) {
            action.accept(this.getBookshelfJson(i));
        }
    }

    /**
     * Loop through this {@link BookshelfArray} for each {@link BookshelfArray}.
     *
     * @param action
     *         A {@link Consumer} accepting a {@link BookshelfArray} instance.
     */
    public void forEachArray(Consumer<BookshelfArray> action) {

        for (int i = 0; i < this.length(); i++) {
            action.accept(this.getBookshelfArray(i));
        }
    }

    /**
     * Alias method for {@link JSONArray#length()}. Merely used for the IntelliJ IDEA PostFix completion `.fori`.
     *
     * @return The total element count in this {@link BookshelfArray}.
     */
    public int size() {

        return this.length();
    }

    public <T> List<T> map(Function<BookshelfJson, T> mappingFunction) {

        List<T> result = new ArrayList<>();
        this.forEachJson(json -> result.add(mappingFunction.apply(json)));
        return result;
    }

}
