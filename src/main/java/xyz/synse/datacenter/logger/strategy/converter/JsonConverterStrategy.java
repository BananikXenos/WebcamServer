package xyz.synse.datacenter.logger.strategy.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.synse.datacenter.logger.internal.Utils;

/**
 * convert string/JSONObject/JSONArray to format string.
 */
public class JsonConverterStrategy implements ConverterStrategy {

    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 4;
    private static final int DEFAULT_PRIORITY = 600;

    private final int indent;

    public JsonConverterStrategy() {
        this(JSON_INDENT);
    }

    public JsonConverterStrategy(int indent) {
        this.indent = indent;
    }

    @Override
    public String convert(@Nullable String message, @NotNull Object object, int level) {
        if (object instanceof String) {

            String json = (String) object;
            json = json.trim();
            try {
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    return Utils.concat(message, jsonObject.toString(indent), DEFAULT_DIVIDER);
                }
                if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    return Utils.concat(message, jsonArray.toString(indent), DEFAULT_DIVIDER);
                }
            } catch (JSONException e) {
                return null;
            }
        }

        if (object instanceof JSONObject) {
            return Utils.concat(message, ((JSONObject) object).toString(indent), DEFAULT_DIVIDER);
        }

        if (object instanceof JSONArray) {
            return Utils.concat(message, ((JSONArray) object).toString(indent), DEFAULT_DIVIDER);
        }

        return null;
    }

    @Override
    public int priority() {
        return DEFAULT_PRIORITY;
    }
}
