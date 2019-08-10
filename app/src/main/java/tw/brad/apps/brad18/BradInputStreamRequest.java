package tw.brad.apps.brad18;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

public class BradInputStreamRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> listener;
    private Map<String,String> responseHeaders;
    private Map<String,String> params;

    public BradInputStreamRequest(int method, String url,
                                  Response.Listener<byte[]> listen,
                                  @Nullable Response.ErrorListener listener,
                                  Map<String,String> params) {
        super(method, url, listener);
        this.listener = listen;
        this.params = params;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        responseHeaders = response.headers;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        listener.onResponse(response);
    }

}