// © 2016-2024 Graylog, Inc.

package Logger;

import io.resurface.*;
import org.json.*;

import java.util.function.Consumer;

public class HttpLoggerForAzureEH {
    private final HttpLogger logger;
    private HttpServletRequestImpl request;
    private HttpServletResponseImpl response;
    private String request_body, response_body;
    private long now;
    private double interval;

    public HttpLoggerForAzureEH() {
        logger = new HttpLogger();
    }

    public HttpLoggerForAzureEH(String url, String rules) {
        logger = new HttpLogger(url, rules);
    }

    public HttpLoggerForAzureEH(String url, Boolean enabled, String rules) {
        logger = new HttpLogger(url, enabled, rules);
    }

    private void parseHttp(String httpMessage) throws JSONException {
        this.request = new HttpServletRequestImpl();
        this.response = new HttpServletResponseImpl();
        this.request_body = "";
        this.response_body = "";

        JSONObject jsonMessage = new JSONObject(httpMessage);

        JSONObject req = new JSONObject(jsonMessage.getString("request"));
        this.request.setMethod(req.getString("method"));
        String[] url = req.getString("url").split("\\?");
        this.request.setRequestURL(url[0]);
        if (url.length > 1) {
            request.setQueryString(url[1]);
            for (String param : url[1].split("&")) {
                int sepIdx = param.indexOf('=');
                if (sepIdx >= 0) {
                    String key = param.substring(0, sepIdx);
                    String val = param.length() == sepIdx + 1 ? "" : param.substring(sepIdx + 1);
                    this.request.addParam(key, val);
                }
            }
        }
        for (Object header : req.getJSONArray("headers")) {
            String[] h = header.toString().split(" ?: ?");
            this.request.addHeader(h[0], h[1]);
        }
        this.request_body = req.get("body").toString();

        JSONObject res = new JSONObject(jsonMessage.getString("response"));
        this.response.setStatus((int) res.get("status"));
        for (Object header : res.getJSONArray("headers")) {
            String[] h = header.toString().split(" ?: ?");
            this.response.addHeader(h[0], h[1]);
        }
        this.response_body = res.get("body").toString();

        this.interval = jsonMessage.getDouble("interval");
        this.now = jsonMessage.getLong("now");
    }

    public void send(byte[] httpMessage) {
        send(httpMessage, System.out::println, System.err::println);
    }

    public void send(byte[] httpMessage, Consumer<String> log, Consumer<String> error) {
        try {
            parseHttp(new String(httpMessage));
            HttpMessage.send(logger, request, response, response_body, request_body, now, interval);
            log.accept(String.format("Message added to queue. Messages sent: %d", logger.getSubmitSuccesses()));
        } catch (JSONException e) {
            error.accept(String.format("Message NOT SENT due to parsing issue: %s", e.getMessage()));
        }
    }

    public boolean isEnabled() {
        return logger.isEnabled();
    }
}
