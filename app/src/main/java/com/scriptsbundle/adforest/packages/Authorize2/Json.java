package com.kathryn.kathryn.packages.Authorize2;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("unused")
public class Json {

    /**
     * Merge given JSON-objects. Same keys are merged for objects and
     * overwritten by last object for primitive types.
     *
     * @param keyCombinations Key names for unique object identification.
     *                        Or empty collection.
     * @param objects         Any amount of JSON-objects to merge.
     * @return Merged JSON-object.
     */
    public static JsonObject mergeObjects(
            @NonNull
                    HashMap<String, String[]> keyCombinations,
            Object... objects) {

        JsonObject mergedObject = new JsonObject();

        for (Object object : objects) {

            JsonObject jsonObject = (JsonObject) object;

            for (String key : jsonObject.keySet()) {

                JsonElement parameter = jsonObject.get(key);

                if (mergedObject.has(key)) {

                    // Key name matches:

                    if (jsonObject.get(key).isJsonObject()) {

                        // This is object - merge:

                        parameter =
                                mergeObjects(
                                        keyCombinations,
                                        mergedObject.get(key).getAsJsonObject(),
                                        jsonObject.get(key).getAsJsonObject());

                    } else if (jsonObject.get(key).isJsonArray()) {

                        // This is array - merge:

                        parameter =
                                mergeArrays(
                                        key,
                                        keyCombinations,
                                        mergedObject.get(key).getAsJsonArray(),
                                        jsonObject.get(key).getAsJsonArray());

                    } else {

                        // This is neither object nor array - replace value:

                        mergedObject.add(key, parameter);
                    }
                }

                // No such field yet - add:

                mergedObject.add(key, parameter);
            }
        }

        return mergedObject;
    }

    /**
     * Alternative - no object identity keys are set.
     * See {@link Json#mergeObjects(HashMap, Object...)}
     */
    public static JsonObject mergeObjects(
            Object... objects) {

        return (
                mergeObjects(
                        new HashMap<>(),
                        objects));
    }

    /**
     * Get GSON-object from string.
     *
     * @param jsonString JSON-object as string.
     * @return JsonObject (GSON).
     */
    public static JsonObject getJsonObject(String jsonString) {

        JsonObject jsonObject = new JsonObject();
        JsonParser parser;

        parser = new JsonParser();

        if (jsonString != null) {

            jsonObject =
                    parser
                            .parse(
                                    jsonString)
                            .getAsJsonObject();
        }

        return jsonObject;
    }

    /**
     * See {@link Json#mergeObjects(HashMap, Object...)}
     */
    public static String mergeObjects(
            HashMap<String, String[]> keyCombinations,
            String... jsonObjects) {

        ArrayList<JsonObject> objects = new ArrayList<>();

        for (String jsonObject : jsonObjects) {

            objects.add(
                    Json.getJsonObject(jsonObject));
        }

        return (
                mergeObjects(
                        keyCombinations,
                        objects.toArray())
                        .toString());
    }

    /**
     * Alternative - no object identity keys are set.
     * See {@link Json#mergeObjects(HashMap, Object...)}
     */
    public static String mergeObjects(
            String... jsonObjects) {

        ArrayList<JsonObject> objects = new ArrayList<>();

        for (String jsonObject : jsonObjects) {

            objects.add(
                    getJsonObject(jsonObject));
        }

        return (
                mergeObjects(
                        new HashMap<>(),
                        objects.toArray())
                        .toString());
    }

    /**
     * See {@link Json#mergeArrays(String, HashMap, Object...)}
     */
    public static String mergeArrays(
            String arrayName,
            HashMap<String, String[]> keyCombinations,
            String... jsonArrays) {

        ArrayList<JsonArray> arrays = new ArrayList<>();

        for (String jsonArray : jsonArrays) {

            arrays.add(
                    getJsonArray(jsonArray));
        }

        return (
                mergeArrays(
                        arrayName,
                        keyCombinations,
                        arrays.toArray())
                        .toString());
    }

    /**
     * Alternative - no object identity keys are set.
     * See {@link Json#mergeArrays(String, HashMap, Object...)}
     */
    public static String mergeArrays(
            String... jsonArrays) {

        ArrayList<JsonArray> arrays = new ArrayList<>();

        for (String jsonArray : jsonArrays) {

            arrays.add(
                    getJsonArray(jsonArray));
        }

        return (
                mergeArrays(
                        "",
                        new HashMap<>(),
                        arrays.toArray())
                        .toString());
    }

    /**
     * Alternative - no object identity keys are set.
     * Seee {@link Json#mergeArrays(String, HashMap, Object...)}
     */
    public static JsonArray mergeArrays(
            Object... jsonArrays) {

        return (
                mergeArrays(
                        "",
                        new HashMap<>(),
                        jsonArrays));
    }

    /**
     * Merge arrays following "Overlay" strategy (overwrite or add).
     * Duplicate elements are added to array until their amount is equal
     * in both arrays. Objects are considered identical if their
     * identifier-keys are present and their values are equal. If no such
     * keys, then objects are considered identical on equal content.
     *
     * @param arrayName       Merged arrays name or empty string.
     *                        Used to choose from key combinations.
     * @param keyCombinations Array objects identifier-key names.
     * @param jsonArrays      Any amount of JSON-arrays to merge.
     * @return Merged array.
     */
    public static JsonArray mergeArrays(
            @NonNull
                    String arrayName,
            @NonNull
                    HashMap<String, String[]> keyCombinations,
            Object... jsonArrays) {

        JsonArray resultArray = new JsonArray();

        for (Object jsonArray : jsonArrays) {

            JsonArray array = (JsonArray) jsonArray;

            for (JsonElement item : array) {

                if (
                        item.isJsonObject() &&
                                keyCombinations.get(arrayName) != null &&
                                keyCombinations.get(arrayName).length > 0) {

                    // Array element is an object with identifier-keys:

                    ArrayList<JsonElement> resultArrayObjectsFound =
                            getArrayObjectsByKeyValues(
                                    resultArray,
                                    item.getAsJsonObject(),
                                    keyCombinations.get(arrayName));

                    if (resultArrayObjectsFound.size() > 0) {

                        // Such field is already present, merge is required:

                        JsonObject resultArrayObjectFound =
                                resultArrayObjectsFound.get(0).getAsJsonObject();

                        JsonObject mergedObject =
                                mergeObjects(
                                        keyCombinations,
                                        resultArrayObjectFound,
                                        item.getAsJsonObject());

                        resultArray.remove(resultArrayObjectFound);
                        resultArray.add(mergedObject);

                        continue;
                    }
                }

                if (!resultArray.contains(item)) {

                    // No such element - add:

                    resultArray.add(item);
                } else if (
                        count(resultArray, item) < count(array, item)) {

                    // There are more duplicates of the element - add:

                    resultArray.add(item);
                }
            }
        }

        return resultArray;
    }

    /**
     * Convert String to JSON-Array (GSON).
     *
     * @param jsonString JSON-array as string.
     * @return JSON-array as GSON-array.
     */
    public static JsonArray getJsonArray(String jsonString) {

        JsonArray jsonArray = new JsonArray();
        JsonParser parser;

        parser = new JsonParser();

        try {

            jsonArray =
                    parser
                            .parse(
                                    jsonString)
                            .getAsJsonArray();

        } catch (Exception ignore) {
        }

        return jsonArray;
    }

    /**
     * Find array objects that have required identity keys and match the values.
     *
     * @param array  Array to search in.
     * @param object Example object for search.
     *               Contains required keys and values.
     * @param keys   Object identity keys.
     * @return Matching JSON-elements.
     */
    public static ArrayList<JsonElement> getArrayObjectsByKeyValues(
            JsonArray array,
            JsonObject object,
            String[] keys) {

        ArrayList<JsonElement> elements = new ArrayList<>();

        for (JsonElement arrayElement : array) {

            if (arrayElement.isJsonObject()) {

                JsonObject jsonObject = arrayElement.getAsJsonObject();

                boolean hasAllKeysThatMatch = true;

                for (String key : keys) {

                    if (!jsonObject.has(key)) {

                        // One of the keys is not found:

                        hasAllKeysThatMatch = false;

                        break;
                    } else {

                        if (
                                jsonObject.get(key).isJsonPrimitive() &&
                                        !jsonObject.get(key).equals(object.get(key))) {

                            // Primitive type key values don't match:

                            hasAllKeysThatMatch = false;

                            break;
                        }

                        if ((
                                jsonObject.get(key).isJsonObject() ||
                                        jsonObject.get(key).isJsonArray()) &&
                                !jsonObject.get(key).toString().equals(
                                        object.get(key).toString())) {

                            // Complex type key values don't match:

                            hasAllKeysThatMatch = false;

                            break;
                        }
                    }
                }

                if (hasAllKeysThatMatch) {

                    // Key values match:

                    elements.add(jsonObject);
                }
            }
        }

        return elements;
    }

    /**
     * Count given elements in array.
     *
     * @param element Element to find.
     * @return Amount of given elements in array.
     */
    public static int count(
            JsonArray array,
            JsonElement element) {

        int count = 0;

        for (JsonElement currentElement : array) {

            if (currentElement.isJsonPrimitive()) {

                // Primitive type:

                if (currentElement.equals(element)) {

                    count++;
                }
            }

            if (
                    currentElement.isJsonObject() ||
                            currentElement.isJsonArray()) {

                // Complex type:

                if (currentElement.toString().equals(element.toString())) {

                    count++;
                }
            }
        }

        return count;
    }

}
