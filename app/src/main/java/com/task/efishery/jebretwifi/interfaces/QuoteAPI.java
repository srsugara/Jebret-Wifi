package com.task.efishery.jebretwifi.interfaces;

import com.task.efishery.jebretwifi.models.Quote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by syauqi on 30/08/17.
 */

public interface QuoteAPI {
    @GET("api/1.0/")
    Call<Quote> getQuote(@Query("method") String method,
                         @Query("lang") String from,
                         @Query("format") String to);
}
