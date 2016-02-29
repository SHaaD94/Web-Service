package com.shaad.testtask;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SearchUtil {
    private static final String userID = "ruuxah6217";

    private static final String gisApiVersion = "1.3";

    private static final String searchFirmURL = "http://catalog.api.2gis.ru/search";

    private static final String searchProfileURL = "http://catalog.api.2gis.ru/profile";

    private SearchUtil() {
    }

    public static JsonObject searchFirmInTown(String searchParam, String town) {
        String jsonResponse = null;
        JsonObject result = new JsonObject();
        HttpURLConnection connection = null;
        try {
            String query = searchFirmURL.concat(
                    "?key=" + userID
                            + "&version=" + gisApiVersion
                            + "&where=" + town
                            + "&what=" + URLEncoder.encode(searchParam, "UTF-8")
                            + "&sort=rating"
                            + "&pagesize=5");

            URL url = new URL(query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            jsonResponse = response.toString();
        } catch (Exception e) {
            System.err.println(e.toString());
            jsonResponse = null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        if (null == jsonResponse) {
            return new JsonObject();
        }

        JsonObject object = new JsonObject(jsonResponse);
        Object objectArray = object.getValue("result");
        if (objectArray != null) {
            JsonArray resultArray = object.getJsonArray("result");
            if (resultArray.size() != 0) {
                JsonObject maxRateInTownObject = resultArray.getJsonObject(0);

                String hash = maxRateInTownObject.getString("hash");
                String objID = maxRateInTownObject.getString("id");

                result.put("hash", hash);
                result.put("id", objID);
            }
        }
        return result;
    }

    public static JsonObject getFirmRating(String objectID, String objectHash) {
        String jsonResponse = null;

        HttpURLConnection connection = null;
        try {
            String query = searchProfileURL.concat(
                    "?key=" + userID
                            + "&version=" + gisApiVersion
                            + "&id=" + objectID
                            + "&hash=" + objectHash);

            URL url = new URL(query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            jsonResponse = response.toString();
        } catch (Exception e) {
            System.err.println(e.toString());
            jsonResponse = null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        if (null == jsonResponse) {
            return null;
        }

        JsonObject response = new JsonObject(jsonResponse);
        JsonObject result = new JsonObject();
        result.put("name", response.getValue("name"));
        result.put("address", response.getValue("city_name") + ", " + response.getValue("address"));
        result.put("rating", response.getValue("rating"));
        return result;
    }
}
