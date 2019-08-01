package rw.itdevs.brarudi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rw.itdevs.brarudi.adapters.newsAdapter;
import rw.itdevs.brarudi.model.news;
import rw.itdevs.brarudi.utils.LocaleManager;

public class NewsActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<news> newsList;
    newsAdapter newsAdapter;
    SharedPreferences prefs;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.actualites);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }

            });
        }

        prefs = getSharedPreferences("news",0);

        newsList =new ArrayList<>();
        recyclerView=findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        newsAdapter =new newsAdapter(newsList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);


        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                String url;
                if(new LocaleManager(getApplicationContext()).getLanguage().equals("rn")){
                    url="http://nawe.bi/spip.php?page=json_primusic";
                }else{
                    url="http://nawe.bi/spip.php?page=json_primusic_fr";
                }
                new newsLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);


            }
        });

        if(prefs.contains("data")){
            decode(prefs.getString("data","data"));
        }

        String url;
        if(new LocaleManager(this).getLanguage().equals("rn")){
            url="http://nawe.bi/spip.php?page=json_primusic";
        }else{
            url="http://nawe.bi/spip.php?page=json_primusic_fr";
        }

        new newsLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);

        newsAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_settings){
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        }



        return true;
    }


    protected class newsLoader extends AsyncTask<String , Void ,String> {
        String server_response = "none";

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            // TODO: Implement this method
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.e("CatalogClient", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            if (server_response.equals("none")) {
                Toast.makeText(NewsActivity.this, "No internet", Toast.LENGTH_SHORT).show();

            } else {

                decode(server_response);

            }
        }
    }

    void decode(String server_response){
        prefs.edit().putString("data",server_response).apply();
        try {
            newsList.clear();

            JSONArray array=new JSONArray(server_response);
            for(int a=0;a<array.length();a++){
                JSONObject object=array.getJSONObject(a);

                String title=object.getString("title");
                String author=object.getString("author");
                String body=object.getString("body");
                String date=object.getString("created");
                String image=object.getString("field_image_fid");
                String url=object.getString("news_url");

                newsList.add(new news(title,body,image,"http://nawe.bi/spip.php?page=json_article&id_article="+object.getString("news_id"),author,date));


            }

            newsAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}
