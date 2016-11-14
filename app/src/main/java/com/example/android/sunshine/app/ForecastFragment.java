package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alhamdulillah on 11/13/16.
 */

public class ForecastFragment extends Fragment{
    /**
     * A placeholder fragment containing a simple view.
     */
    ArrayAdapter<String> mArrayAdapter;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView mListView = (ListView) rootView.findViewById(R.id.listView);
        String[] data = new String[]{"Cuaca1", "Cuaca 2"
                , "Cuaca 3", "Cuaca 4", "Cuaca 5", "Cuaca 6"
                , "Cuaca 7", "Cuaca 8"};

        mArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.list_layout, R.id.tvItem, new ArrayList<String>(Arrays.asList(data)));
        mListView.setAdapter(mArrayAdapter);

        CobaAsyncTask test = new CobaAsyncTask();
        test.execute("kediri");

        return rootView;
    }

    public class CobaAsyncTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL
                        ("http://api.openweathermap.org/data/2.5/forecast/daily?q="+params[0]+"&mode=json&units=metric&cnt=7"+"&APPID=2ef40d513ed8ecdeeea37d6adb248c75");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.i("polinema",forecastJsonStr);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            if (forecastJsonStr!=null){
                try {
                    return handleJson(forecastJsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private String[] handleJson(String forecastJsonStr) throws JSONException {
            List <String> data = new ArrayList<String>();

            JSONObject mJsonObject = new JSONObject(forecastJsonStr);
            JSONArray mList = mJsonObject.getJSONArray("list");

            for (int aa=0; aa<mList.length();aa++) {
                JSONObject item = mList.getJSONObject(aa);
                JSONArray weather = item.getJSONArray("weather");

                JSONObject mainCuaca = weather.getJSONObject(0);
                String main =mainCuaca.getString("main");

                data.add(main);
            }
            String[] array = data.toArray(new String[0]);
            return array;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null){
                mArrayAdapter.clear();
                for (String cuaca:strings) {
                    mArrayAdapter.add(cuaca);
                }
            }
        }
    }
}
