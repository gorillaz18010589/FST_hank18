package tw.brad.apps.brad18;
//1.先在檔案管理加入int,跟明碼傳送
//2.build.gradle=>Module,加上 implementation 'com.android.volley:volley:1.1.1'

//最大寬度,如果不指定就0,0
//Bitmap.Config.ARGB_8888,//影像要如何做組態解碼
// null //錯誤訊息

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    private MainApp mainApp;
    private TextView tv;
    private ImageView img;
    private boolean isWriteSDCard;
    private File sdcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            //允許存取權限判斷,如果存取成功就true
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }else{
            isWriteSDCard = true;
        }

        sdcard = Environment.getExternalStorageDirectory();
        mainApp = (MainApp) getApplication();

        tv = findViewById(R.id.tv);
        img = findViewById(R.id.img);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isWriteSDCard = true;
        }
    }


    //按鈕1.取得資策會資訊顯示在手機上
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

        //按鈕2.抓取農委會資料印在log紀錄
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

          public MyInputStreamRequest(int method,  //方法
                                      String url,  //網址
                                      Response.Listener<byte[]> listen, //回應範行方式
                                      @Nullable Response.ErrorListener listener) { //錯誤訊息
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
    //引用我們自己寫好的BradInputStreamRequest方法
    public void test5(View view) {
        if (!isWriteSDCard) return; //如果沒有允許存取
        BradInputStreamRequest request = new BradInputStreamRequest(
                Request.Method.GET, //使用get方法
                "https://ezgo.coa.gov.tw/Uploads/opendata/TainmaMain01/APPLY_D/20151007173924.jpg",//url網址
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.v("brad", "len = " + response.length);
                        saveSDCard(response); //呼叫io寫入照片的方法
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("brad", error.toString());
                    }
                },
                null
        );
        mainApp.queue.add(request);
    }
        //處理io把照片存入在donwodn底下,照片叫brad.jpg
    private void saveSDCard(byte[] data){
        File saveFile = new File(sdcard,"Download/brad.jpg");
        try {
            BufferedOutputStream bout =
                    new BufferedOutputStream(new FileOutputStream(saveFile));
            bout.write(data);
            bout.flush();
            bout.close();
            Toast.makeText(this,"接收成功",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.v("brad",e.toString());
        }
    }

        //第二頁設計,可以輸入cname,tel,addr更改
    public void toPage2(View view) {
        Intent intent = new Intent(this, Page2Activity.class);
        startActivity(intent);
    }
}