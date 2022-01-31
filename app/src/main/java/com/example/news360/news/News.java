package com.example.news360.news;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mediplus.Covid.user.news.adapter.Adapter;
import com.example.mediplus.Covid.user.news.model.Articles;
import com.example.mediplus.Covid.user.news.model.Headlines;
import com.example.mediplus.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class News extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etQuery;
    private Adapter adapter;
    private List<Articles> articles = new ArrayList<>();
    private final String API_KEY = "ea5fb76a976f4968b44b872c12bb6c21";

    public News() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);

        etQuery = view.findViewById(R.id.etQuery);
        Button btnSearch = view.findViewById(R.id.btnSearch);
        Dialog dialog = new Dialog(requireActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final String country = "in";

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveJson("health", country, API_KEY);
            }
        });
        retrieveJson("health", country, API_KEY);

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!etQuery.getText().toString().equals("")) {
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson(etQuery.getText().toString(), country, API_KEY);
                        }
                    });
                    retrieveJson(etQuery.getText().toString(), country, API_KEY);
                } else {
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson("health", country, API_KEY);
                        }
                    });
                    retrieveJson("health", country, API_KEY);
                }
            }
        });

        return view;
    }

    private String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }

    private void retrieveJson(String query, String country, String apiKey) {

        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;

        if (!etQuery.getText().toString().equals("")) {
            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);
        } else {
            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);
        }

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(@NotNull Call<Headlines> call, @NotNull Response<Headlines> response) {
                assert response.body() != null;
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(getActivity(), articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
