package tw.brad.apps.brad18;
//1.先在檔案管理加入int,跟明碼傳送
//2.build.gradle=>Module,加上 implementation 'com.android.volley:volley:1.1.1'

//最大寬度,如果不指定就0,0
//Bitmap.Config.ARGB_8888,//影像要如何做組態解碼
   //     null //錯誤訊息

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private MainApp mainApp;
    private TextView tv;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainApp = (MainApp) getApplication();

        tv = findViewById(R.id.tv);
        img = findViewById(R.id.img);
    }
    //按按鈕取得資策會資訊顯示在手機上
    public void test1(View view) {
        StringRequest request = new StringRequest(
                Request.Method.GET, //1.方法
                "https://www.iii.org.tw", //2.url網址字串
                new Response.Listener<String>() { //3.設定的回傳訊息範行<String>
                    @Override
                    public void onResponse(String response) {
                        Log.v("brad", response);
                        tv.setText(response);
                    }
                },
                null //4.錯誤訊息
        );
        mainApp.queue.add(request);
    }

        //抓取農委會資料印在log紀錄
    public void test2(View view) {
        String url = "http://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx"; //url字串網址

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON(response); //自己寫的抓date資料方法
                    }
                },
                null
        );
        mainApp.queue.add(request);
    }

    private void parseJSON(String json){
        try{
            JSONArray root = new JSONArray(json); //一開始是陣列
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i); //裡面是物件取得
                Log.v("brad", row.getString("Name") +
                        ":" +row.getString("Tel"));
            }
        }catch (Exception e){
            Log.v("brad", e.toString());
        }
    }

        //抓取圖片顯示在手機上
    public void test3(View view) {
        ImageRequest request = new ImageRequest(
                "https://ezgo.coa.gov.tw/Uploads/opendata/TainmaMain01/APPLY_D/20151007173924.jpg", //1.url字串網址
                new Response.Listener<Bitmap>() { //2,成功回來的東西
                    @Override
                    public void onResponse(Bitmap response) {
                        img.setImageBitmap(response);
                    }
                },
                0, 0, //3.檔案大小,如果省略的話0,0
                Bitmap.Config.ARGB_8888, //4.組態檔編碼的方式
                null     //5.錯誤訊息
        );
        mainApp.queue.add(request);
    }

        //抓取資料顯示在log,用JsonArrayRequest方式
    public void test4(View view) {
        String url = "http://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx";
        JsonArrayRequest request = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {  //配合我們自訂的jsonarray自動販行陣列方式
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJSON2(response); //下面字寫得串流取得方法
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("brad", error.toString());
                    }
                }
        );
        mainApp.queue.add(request);
    }

    private  void parseJSON2(JSONArray root){
        try{
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                Log.v("brad", row.getString("Name") +
                        ":" +row.getString("Tel"));
            }
        }catch (Exception e){

        }
    }
      //抓pdf下載方式
      private class MyInputStreamRequest extends Request<byte[]>{ //要串流所以犯行用byte[]比較方便
          private final Response.Listener<byte[]> listener;

          public MyInputStreamRequest(int method, String url,
                                      Response.Listener<byte[]> listen,
                                      @Nullable Response.ErrorListener listener) {
              super(method, url, listener);
              this.listener = listen;
          }

          @Override
          protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
              return null;
          }

          @Override
          protected void deliverResponse(byte[] response) {
              listener.onResponse(response);
          }
      }

}