package mg.studio.weatherappdesign;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    public int hour;
    public int count;
    public int[] temper = new int[40];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        if(!isConnectIsNomarl()){
            Toast.makeText(this, "The Network is not connected!", Toast.LENGTH_LONG).show();
            return;
        }

        //set the day of a week
        setWeekAndDate();

        //refresh when run the App.
        new DownloadUpdate().execute();
    }

    public void btnClick(View view) {
        if(!isConnectIsNomarl()){
            Toast.makeText(this, "The Network is not connected!", Toast.LENGTH_LONG).show();
            return;
        }
        setWeekAndDate();
        new DownloadUpdate().execute();
        Toast.makeText(this, "The weather has been refreshed!", Toast.LENGTH_LONG).show();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://api.openweathermap.org/data/2.5/forecast?q=Chongqing,cn&mode=json&APPID=aa3d744dc145ef9d350be4a80b16ecab";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            String tem = null, weather = null;
            int min = 50,max = 0;
            try {
                //get the Json from buffer.toString()
                JSONObject forecastJson = new JSONObject(jsonString);
                JSONArray jsonArray = forecastJson.getJSONArray("list");
                // Get all jsonObject from jsonArray
                int temp = 0; //the temperature today

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject mainObject = jsonObject.getJSONObject("main");
                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    temp = (int)Math.round(mainObject.getDouble("temp") - 273.15);
                    temper[i] = temp;
                    if(i == 2)
                    {
                        tem = String.valueOf(temp);
                        weather = weatherObject.getString("main");
                        setWeather(weather,R.id.img_weather_condition);
                    }
                    if(i == 10)
                    {
                        weather = weatherObject.getString("main");
                        setWeather(weather,R.id.iv_day1);
                    }
                    if(i == 18)
                    {
                        weather = weatherObject.getString("main");
                        setWeather(weather,R.id.iv_day2);
                    }
                    if(i == 26)
                    {
                        weather = weatherObject.getString("main");
                        setWeather(weather,R.id.iv_day3);
                    }
                    if(i == 34)
                    {
                        weather = weatherObject.getString("main");
                        setWeather(weather,R.id.iv_day4);
                    }

                }
                for(int m = 10-count;m<(18-count);m++){
                    if(temper[m]<min){
                        min = temper[m];
                    }
                    if(temper[m]>max){
                        max = temper[m];
                    }
                }
                ((TextView) findViewById(R.id.tv_tem1)).setText(String.valueOf(min)+"--"+String.valueOf(max)+"°C");
                min = 50;
                max = 0;
                for(int m = 18-count;m<(26-count);m++){
                    if(temper[m]<min){
                        min = temper[m];
                    }
                    if(temper[m]>max){
                        max = temper[m];
                    }
                }
                ((TextView) findViewById(R.id.tv_tem2)).setText(String.valueOf(min)+"--"+String.valueOf(max)+"°C");
                min = 50;
                max = 0;
                for(int m = 26-count;m<(34-count);m++){
                    if(temper[m]<min){
                        min = temper[m];
                    }
                    if(temper[m]>max){
                        max = temper[m];
                    }
                }
                ((TextView) findViewById(R.id.tv_tem3)).setText(String.valueOf(min)+"--"+String.valueOf(max)+"°C");
                min = 50;
                max = 0;
                for(int m = 34-count;m<(42-count);m++){
                    if(temper[m]<min){
                        min = temper[m];
                    }
                    if(temper[m]>max){
                        max = temper[m];
                    }
                }
                ((TextView) findViewById(R.id.tv_tem4)).setText(String.valueOf(min)+"--"+String.valueOf(max)+"°C");
            } catch (JSONException e) {
                Log.e("FAILED", "Json parsing error: " + e.getMessage());
            }
            //Update the temperature displayed
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(tem);
        }
    }
//get the weather and set the photo.
    public void setWeather(String weather, int id){
        switch(weather){
            case "Clear": {
                ImageView iv = (ImageView) findViewById(id);
                iv.setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));
                break;
            }
            case "Clouds":{
                ImageView iv = (ImageView) findViewById(id);
                iv.setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));
                break;
            }
            case "Rain":{
                ImageView iv = (ImageView) findViewById(id);
                iv.setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
                break;
            }
        }
    }
//set the day.
    public void setWeekAndDate(){
        //set the date
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("HH");
        String time = f.format(d);
        hour = Integer.parseInt(time);
        count = (hour/3);
        CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
        ((TextView) findViewById(R.id.tv_date)).setText(s);

        String daysArray[] = {"Sunday","Monday","Tuesday", "Wednesday","Thursday","Friday", "Saturday"};

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        ((TextView) findViewById(R.id.tv_now)).setText(daysArray[day-1]);
        int day1,day2,day3,day4;
        day1 = (day)%7;
        day2 = (day+1)%7;
        day3 = (day+2)%7;
        day4 = (day+3)%7;
        ((TextView) findViewById(R.id.tv_day1)).setText(daysArray[day1]);
        ((TextView) findViewById(R.id.tv_day2)).setText(daysArray[day2]);
        ((TextView) findViewById(R.id.tv_day3)).setText(daysArray[day3]);
        ((TextView) findViewById(R.id.tv_day4)).setText(daysArray[day4]);
    }
//Determine if the network is connected.
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
//            String intentName = info.getTypeName();
//            Log.i(intentName);
            return true;
        } else {
            return false;
        }
    }
}
