package Logger;

import io.resurface.*;
import org.json.*;


public class HttpLoggerForAzureEH {
    private final HttpLogger logger;
    private HttpServletRequestImpl request;
    private HttpServletResponseImpl response;
    private String request_body = "";
    private String response_body = "";

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
        request = new HttpServletRequestImpl();
        response = new HttpServletResponseImpl();
        JSONObject jsonMessage = new JSONObject(httpMessage);

        JSONObject req = new JSONObject(jsonMessage.getString("request"));
        this.request.setMethod(req.getString("method"));
        this.request.setRequestURL(req.getString("url"));
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
    }

    public void send(byte[] httpMessage) {
        try {
            parseHttp(new String(httpMessage));
            HttpMessage.send(logger, request, response, response_body, request_body);
            System.out.println("Message sent");
        } catch (JSONException e) {
            System.err.printf("Message not sent due to parsing issue: %s\n", e.getMessage());
        }
    }
}
