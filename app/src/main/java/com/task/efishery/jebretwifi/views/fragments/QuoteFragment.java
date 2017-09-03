package com.task.efishery.jebretwifi.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.task.efishery.jebretwifi.R;
import com.task.efishery.jebretwifi.dagger.JebretWifiApplication;
import com.task.efishery.jebretwifi.interfaces.QuoteAPI;
import com.task.efishery.jebretwifi.models.Quote;

import javax.inject.Inject;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuoteFragment extends Fragment {
    @InjectView (R.id.tv_quote_text)
    TextView quoteText;
    @InjectView(R.id.tv_quote_author)
    TextView quoteAuthor;
    @InjectView(R.id.tv_quote_link)
    TextView quoteLink;
    @Inject
    Retrofit retrofit;

    public QuoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quote, container, false);
        ButterKnife.inject(this,view);
        ((JebretWifiApplication) getActivity().getApplication()).getJebretWifiComponent().inject(this);

        String method,lang,format;
        method = getResources().getString(R.string.method);
        lang = getResources().getString(R.string.lang);
        format = getResources().getString(R.string.format);
        QuoteAPI quoteAPI = retrofit.create(QuoteAPI.class);
        Call<Quote> result = quoteAPI.getQuote(method,lang,format);
        result.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if(response.isSuccessful()) {
                    quoteText.setText(response.body().getQuoteText());
                    quoteAuthor.setText(response.body().getQuoteAuthor());
                    quoteLink.setText(response.body().getQuoteLink());
                } else {
                    switch(response.code()) {
                        case 404 :
                            Toast.makeText(getContext(), "server returned error : user not found", Toast.LENGTH_SHORT).show();
                            break;
                        case 500 :
                            Toast.makeText(getContext(), "server returned error : server is broken", Toast.LENGTH_SHORT).show();
                            break;
                        default :
                            Toast.makeText(getContext(), "server returned error : unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                Toast.makeText(getContext(), " on failure " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

}
