package me.philio.disqus.api.http;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple wrapper around {@link HttpURLConnection} that returns a {@link HttpResponse} with the
 * HTTP response code and response body
 */
public class HttpRequest {

    /**
     * Logging tag
     */
    private static final String TAG = HttpRequest.class.getName();

    /**
     * User agent
     */
    private static final String USER_AGENT = "Disqus Android/0.1";

    /**
     * Methods
     */
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    /**
     * Timeouts
     */
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    /**
     * Get request
     *
     * @param uri
     * @return
     * @throws IOException
     */
    public HttpResponse get(Uri uri) throws IOException {
        Log.d(TAG, "GET " + uri.toString());
        HttpURLConnection connection = createConnection(uri);
        connection.setRequestMethod(METHOD_GET);
        connection.connect();
        return getResponse(connection);
    }

    /**
     * Post request
     *
     * @param uri
     * @param data
     * @return
     * @throws IOException
     */
    public HttpResponse post(Uri uri, String data) throws IOException {
        Log.d(TAG, "POST " + uri.toString() + "\n" + data);
        HttpURLConnection connection = createConnection(uri);
        connection.setRequestMethod(METHOD_POST);
        if (data != null && data.length() > 0) {
            write(connection, data);
        } else {
            connection.connect();
        }
        return getResponse(connection);
    }

    /**
     * Create new connection
     *
     * @param uri
     * @return
     * @throws IOException
     */
    private HttpURLConnection createConnection(Uri uri) throws IOException {
        URL url = new URL(uri.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setUseCaches(false);
        return connection;
    }

    /**
     * Get response from connection
     *
     * @param connection
     * @return
     * @throws IOException
     */
    private HttpResponse getResponse(HttpURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        String body = null;
        try {
            if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
                body = read(connection.getErrorStream());
            } else {
                body = read(connection.getInputStream());
            }
        } catch (FileNotFoundException e) {
            // Fall through, acceptable failure
        } finally {
            connection.disconnect();
        }
        return new HttpResponse(code, body);
    }

    /**
     * Read data from a stream as string
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line);
        }
        reader.close();

        return builder.toString();
    }

    /**
     * Write string data to a stream
     *
     * @param connection
     * @param data
     * @throws IOException
     */
    private void write(HttpURLConnection connection, String data) throws IOException {
        connection.setDoOutput(true);
        connection.setFixedLengthStreamingMode(data.length());
        OutputStream stream = new BufferedOutputStream(connection.getOutputStream());
        stream.write(data.getBytes());
        stream.flush();
        stream.close();
    }

}