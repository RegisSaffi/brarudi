package rw.itdevs.brarudi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

import rw.itdevs.brarudi.model.news;

public class DetailsActivity extends BaseActivity {

    ImageView image;
    WebView wv1;
    ProgressBar load;
    TextView title,date,author;

    FloatingActionButton fab;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
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

        image=findViewById(R.id.header_image);
        load=findViewById(R.id.load);
        date=findViewById(R.id.publish_date);
        author=findViewById(R.id.publish_author);
        title=findViewById(R.id.single_news_title);
        wv1=findViewById(R.id.webview);
        fab=findViewById(R.id.fab);

        final Bundle bundle=getIntent().getExtras();
        //String news_id=bundle.getString("id_new");
        final String tit=bundle.getString("title");
        url=bundle.getString("url");
        final String dat=bundle.getString("date");
        final String auth=bundle.getString("author");
        final String img=bundle.getString("image");

        //Toast.makeText(context, ""+news_id, Toast.LENGTH_LONG).show();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            title.setText(Html.fromHtml(tit,Html.FROM_HTML_MODE_LEGACY));
        } else {
            title.setText(Html.fromHtml(tit));
        }

        date.setText(dat);
        author.setText(auth);


        Picasso.with(this)
                .load(img)
                .placeholder(R.drawable.loading)
                .into(image);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String shareble=Html.fromHtml( tit)+"\n\nYanditswe na: "+author+"\nkuwa: "+dat+"\n"+bundle.getString("desc");
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, shareble);
                startActivity(Intent.createChooser(share, "Sangiza iyi nkuru.."));
            }
        });

        wv1.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
               // Toast.makeText(DetailsActivity.this, "Failed lading page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });


        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.getSettings().setLayoutAlgorithm(WebSettings. LayoutAlgorithm.SINGLE_COLUMN);
        wv1.getSettings().setLoadWithOverviewMode(true);


        new newsLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);

    }


    protected class newsLoader extends AsyncTask<String , Void ,String> {
        String server_response = "none";

        @Override
        protected void onPreExecute() {
            findViewById(R.id.load).setVisibility(View.VISIBLE);
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

            findViewById(R.id.load).setVisibility(View.GONE);
            if (server_response.equals("none")) {

                Toast.makeText(DetailsActivity.this, "No internet", Toast.LENGTH_SHORT).show();
            } else {

                decode(server_response);

            }
        }
    }

    void decode(String server_response){

        try {
            JSONArray array=new JSONArray(server_response);
            for(int a=0;a<array.length();a++){
                JSONObject object=array.getJSONObject(a);

                String title=object.getString("title");
                String author=object.getString("author");
                String body=object.getString("body");
                wv1.loadData(body,"text/html","utf-8");

            }


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
