package edu.calvin.cs262.lab06;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CS 262, Homework02
 *
 * @author Paige Brinks, plb7
 * @version Oct 24, 2016x
 *
 */
public class MainActivity extends AppCompatActivity {

    private EditText idInputText;
    private Button fetchButton;

    private List<Monopoly> monopolyList = new ArrayList<>();
    private ListView itemsListView;

     private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idInputText = (EditText) findViewById(R.id.idInputText);
        fetchButton = (Button) findViewById(R.id.fetchButton);
        itemsListView = (ListView) findViewById(R.id.monopolyListView);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissKeyboard(idInputText);
                new GetMonopolyTask().execute(createURL(idInputText.getText().toString()));

            }
        });
    }

    /**
     * Formats a URL for the webservice specified in the string resources.
     *
     * @param idText the id of person to show
     * @return URL
     */
    private URL createURL(String idText) {
        try {
            String urlString = "http://cs262.cs.calvin.edu:8089/monopoly/player";
            if(idText.length() > 0) {
                urlString = urlString + "/" + idText;
            }
            else {
                urlString = urlString + "s";
            }
            Log.i("The id is ", idText);
            Log.i("The URL is ", urlString);
            return new URL(urlString);

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    /**
     * Deitel's method for programmatically dismissing the keyboard.
     *
     * @param view the TextView currently being edited
     */
    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Inner class for GETing the the player
     */
    private class GetMonopolyTask extends AsyncTask<URL, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(URL... params) {
            HttpURLConnection connection = null;
            StringBuilder result = new StringBuilder();
            try {
                connection = (HttpURLConnection) params[0].openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    if(result.toString().substring(0,1).equals("{")) {
                        String temp = result.toString();
                        temp = "[" + temp + "]";
                        return new JSONArray(temp);
                    }
                    return new JSONArray(result.toString());
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return null;
        }



        @Override
        protected void onPostExecute(JSONArray monopoly) {
            if (monopoly != null) {
                //Log.d(TAG, weather.toString());
                convertJSONtoArrayList(monopoly);
                MainActivity.this.updateDisplay();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     *  creates ArrayList of players to display
     *
     * @param monopolyPlayers
     */
    private void convertJSONtoArrayList(JSONArray monopolyPlayers) {
        monopolyList.clear(); // clear old weather data

        for(int i = 0; i < monopolyPlayers.length(); i++) {
            try {
                JSONObject player = monopolyPlayers.getJSONObject(i);
                monopolyList.add(new Monopoly(
                        player.getInt("id"),
                        player.getString("name"),
                        player.getString("emailaddress")
                ));
            } catch (JSONException e) {
                try {
                    JSONObject player = monopolyPlayers.getJSONObject(i);
                    monopolyList.add(new Monopoly(
                            player.getInt("id"),
                            "No Name",
                            player.getString("emailaddress")
                    ));
                } catch (JSONException f) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Refresh the player data
     */
    private void updateDisplay() {
        if (monopolyList == null) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (Monopoly item : monopolyList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", Integer.toString(item.getPlayerID()));
            map.put("name", item.getPlayerName());
            map.put("emailaddress", item.getPlayerEmail());
            data.add(map);
        }

        int resource = R.layout.weather_item;
        String[] from = {"id", "name", "emailaddress"};
        int[] to = {R.id.id_textView, R.id.name_textView, R.id.email_textView};

        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);
    }

}