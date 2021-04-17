package edu.temple.cis3515_assignment9;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookSearchActivity extends AppCompatActivity
{
    Button searchButton;
    Button cancelButton;
    EditText text;
    BookList list = new BookList();
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        cancelButton = findViewById(R.id.cancelButton);
        searchButton = findViewById(R.id.searchButton);
        text = findViewById(R.id.searchText);
        queue = Volley.newRequestQueue(this);

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String url = "https://kamorris.com/lab/cis3515/search.php?term=" + text.getText().toString();

                JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonResponse) {
                        if (jsonResponse.length() > 0) {
                            for (int i = 0; i < jsonResponse.length(); i++) {
                                JSONObject jsonObject;
                                try {
                                    jsonObject = jsonResponse.getJSONObject(i);
                                    list.add(new Book(jsonObject.getInt("id"), jsonObject.getString("title"), jsonObject.getString("author"), jsonObject.getString("cover_url"), jsonObject.getInt("duration")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {

                        }

                        Intent intent = new Intent(BookSearchActivity.this, MainActivity.class);
                        intent.putExtra("list", list);
                        startActivity(intent);
                        finish();
                    }
                },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {

                            }
                        });
                queue.add(jsonRequest);
            }
        });
    }


}