package com.jatin.httpurlconnectionservices;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnShowAll;
    ListView listView;
    ArrayAdapter adapter;
    Handler uiUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uiUpdater = new Handler(){

                    @Override
                    public void handleMessage(Message msg) {
                        if(msg.what == 111)
                        {
                            listView.setAdapter(adapter);
                        }
                    }
                };

                getData();

                listView.setAdapter(adapter);
            }
        });

    }

    private void initViews() {
        btnShowAll = findViewById(R.id.btnShowAll);
        listView = findViewById(R.id.listView);
    }

    private void getData() {

        Thread sendHttpRequestThread = new Thread() {
            @Override
            public void run() {
                // Maintain http url connection.
                HttpURLConnection httpConn = null;

                // Read text input stream.
                InputStreamReader isReader = null;

                // Read text into buffer.
                BufferedReader bufReader = null;

                // Save server response text.
                StringBuffer readTextBuf = new StringBuffer();

                try {
                    // Create a URL object use page url.
                    URL url = new URL("https://rajeevdadwal.000webhostapp.com/select.php");

                    // Open http connection to web server.
                    httpConn = (HttpURLConnection) url.openConnection();

                    // Set http request method to get.
                    httpConn.setRequestMethod("POST");

                    // Set connection timeout and read timeout value.
                    httpConn.setConnectTimeout(10000);
                    httpConn.setReadTimeout(10000);

                    // Get input stream from web url connection.
                    InputStream inputStream = httpConn.getInputStream();

                    // Create input stream reader based on url connection input stream.
                    isReader = new InputStreamReader(inputStream);

                    // Create buffered reader.
                    bufReader = new BufferedReader(isReader);

                    // Read line of text from server response.
                    String line = bufReader.readLine();

                    ArrayList<String> arrayList = new ArrayList<>();
                    String data[];
                    // Loop while return line is not null.
                    while (line != null) {
                        // Append the text to String ArrayList
                        line = line.substring(1,line.length()-1);
                        data = line.split("#");
                        for(String oneLine:data)
                        {
                            Log.e("data: ",oneLine);
                            arrayList.add(oneLine);
                        }
                        // Continue to read text line.
                        line = bufReader.readLine();
                    }

    adapter = new ArrayAdapter(MainActivity.this,
                                android.R.layout.simple_list_item_1,
                                arrayList);

                    Message message = new Message();
                    message.what = 111;

                    uiUpdater.sendMessage(message);

  /*          // Send message to main thread to update response text in TextView after read all.
            Message message = new Message();

            // Set message type.
            message.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;

            // Create a bundle object.
            Bundle bundle = new Bundle();
            // Put response text in the bundle with the special key.
            bundle.putString(KEY_RESPONSE_TEXT, readTextBuf.toString());
            // Set bundle data in message.
            message.setData(bundle);
            // Send message to main thread Handler to process.
            uiUpdater.sendMessage(message);
*/
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (bufReader != null) {
                            bufReader.close();
                        }

                        if (isReader != null) {
                            isReader.close();
                        }

                        if (httpConn != null) {
                            httpConn.disconnect();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        sendHttpRequestThread.start();
    }

}
