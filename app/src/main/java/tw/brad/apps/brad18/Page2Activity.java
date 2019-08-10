package tw.brad.apps.brad18;
//抓去網路圖片
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Page2Activity extends AppCompatActivity {
    private MainApp mainApp;
    private EditText cname, tel, addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        mainApp = (MainApp)getApplication();

        cname = findViewById(R.id.cname);
        tel = findViewById(R.id.tel);
        addr = findViewById(R.id.addr);

    }

    public void test1(View view) {
        StringRequest request = new StringRequest(
                Request.Method.POST, //post方法
                "https://www.bradchao.com/autumn/addCust.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("brad",response);
                    }
                },
                null

        ){ //進行overrdier方法
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("cname",cname.getText().toString()); //抓到自訂的cname
                params.put("tel",tel.getText().toString());
                params.put("addr",addr.getText().toString());

                return params;
            }
        };
        mainApp.queue.add(request);

    }
}