package rw.itdevs.brarudi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rw.itdevs.brarudi.adapters.newsAdapter;
import rw.itdevs.brarudi.model.news;
import rw.itdevs.brarudi.utils.LocaleManager;

public class VoteActivity extends BaseActivity {


    RecyclerView recyclerView;
    news news;
    List<news> newsList;
    newsAdapter newsAdapter;

    ProgressDialog progress;
    SwipeRefreshLayout swipeRefreshLayout;

    boolean voted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.vote);
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

        newsList =new ArrayList<>();
        recyclerView=findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        newsAdapter =new newsAdapter(newsList, this, new newsAdapter.voteListener() {
            @Override
            public void onVote(String name, String image,String keyword) {

                if(!voted) {
                    vote(name, image, keyword);
                }else{
                    votedAlready();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newsAdapter.notifyDataSetChanged();
                    }

                },1000);
            }
        });

        voted=getSharedPreferences("brarudi",0).getBoolean("voted",false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);

        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                String url;
                if(new LocaleManager(getApplicationContext()).getLanguage().equals("rn")){
                    url="http://primusicbi.com/api/singers.php";
                }else{
                    url="http://primusicbi.com/api/singers.php";
                }

               new getSingers().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);

            }
        });
        new getSingers().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://primusicbi.com/api/singers.php");

    }


    public class vote extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {

            progress= ProgressDialog.show(VoteActivity.this,"",getString(R.string.wait));
            // TODO: Implement this method
            super.onPreExecute();
        }


        protected String doInBackground(String... params) {

            String address = "http://primusicbi.com/api/";

            try {

                url = new URL(address);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String query = params[0];

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();
                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {
          //  Toast.makeText(VoteActivity.this, result, Toast.LENGTH_SHORT).show();
            progress.dismiss();
            if (!result.equals("exception")) {
                try {
                    JSONObject object=new JSONObject(result);
                    JSONArray array=object.getJSONArray("primusic");
                    String status=array.getJSONObject(0).getString("error");
                    if(!status.equals("0")){
                        done("Pas vote");
                    }else{
                        done("vote");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(VoteActivity.this, "Problemme de resoux", Toast.LENGTH_SHORT).show();
            }
        }
    }


    protected class getSingers extends AsyncTask<String , Void ,String> {
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
                Toast.makeText(VoteActivity.this, "No internet", Toast.LENGTH_SHORT).show();

            } else {

                decode(server_response);

            }
        }
    }

    void decode(String server_response){

        try {
            newsList.clear();

            JSONArray array=new JSONArray(server_response);
            for(int a=0;a<array.length();a++){
                JSONObject object=array.getJSONObject(a);

                String title=object.getString("names");
                String author=object.getString("keyword");
                String image=object.getString("photo");

                news=new news(title,"no noe",image,"http://nawe.bi/spip.php?page=json_article&id_article=",author,"no date");
                news.setIsVoting(true);

                newsList.add(news);

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


    public void vote(String nm, String img, final String key){

        final Dialog dialog=new Dialog(VoteActivity.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.votefor,null);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        ImageView image=dialog.findViewById(R.id.image);
        TextView name=dialog.findViewById(R.id.name);
        name.setText(nm);

        Picasso.with(getApplicationContext())
                .load(img)
                .placeholder(R.id.imgContactProfile)
                .into(image);

        Button done=dialog.findViewById(R.id.done);
        done.setText(getResources().getString(R.string.vote));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

                String android_id = Secure.getString(getContentResolver(),
                        Secure.ANDROID_ID);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("votes", "app")
                        .appendQueryParameter("keyword", key)
                        .appendQueryParameter("phone", android_id);

                String query = builder.build().getEncodedQuery();
                new vote().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,query);
            }
        });

        dialog.show();
    }

    public void done(final String status){

        final Dialog dialog=new Dialog(VoteActivity.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.voted,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ImageView image=dialog.findViewById(R.id.image);
        TextView name=dialog.findViewById(R.id.name);

        if(status.equals("vote")) {
            name.setText(getResources().getString(R.string.voted));
            image.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_done_black_24dp, getTheme()));

            getSharedPreferences("brarudi",0).edit().putBoolean("voted",true).apply();

        }else{
            name.setText(status);
            image.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_block_black_24dp, getTheme()));
        }

        Button done=dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

                if(status.equals("vote") || status.equals("not")) {
                    finish();
                }
            }
        });

        dialog.show();
    }


    public void votedAlready(){

        final Dialog dialog=new Dialog(VoteActivity.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.voted,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ImageView image=dialog.findViewById(R.id.image);
        TextView name=dialog.findViewById(R.id.name);


            name.setText(getResources().getString(R.string.mwatoye));
            image.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_block_black_24dp, getTheme()));

        Button done=dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

                    finish();

            }
        });

        dialog.show();
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
}
