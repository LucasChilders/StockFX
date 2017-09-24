package com.lucaschilders.StockFX;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

import java.io.IOException;

/**
 * Created by Lucas on 9/24/2017.
 */
public class StockData {
    Stock stock;
    StockQuote quote;

    public double today;
    public double yesterday;
    public double dayHigh;
    public double dayLow;
    public double change;
    public String ticker;
    public String companyName;
    public long volume;

    public StockData(String thisTicker) {
        try {
            stock = YahooFinance.get(thisTicker);
            quote = stock.getQuote();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!stock.isValid()) {
            today = 0.0;
            yesterday = 1;
            dayHigh = 0;
            dayLow = 0;
            change = 0;
            ticker = "";
            companyName = "";
            volume = 0;
        } else {
            today = getTodayPrice();
            yesterday = getYesterdayPrice();
            dayHigh = getDayHigh();
            dayLow = getDayLow();
            change = getChange();
            ticker = getTicker();
            companyName = getCompanyName();
            volume = getVolume();
        }
    }

    public double getTodayPrice() {
        return quote.getPrice().doubleValue();
    }

    public double getYesterdayPrice() {
        return quote.getPreviousClose().doubleValue();
    }

    public String getTicker() {
        return quote.getSymbol().toUpperCase();
    }

    public long getVolume() {
        return quote.getVolume();
    }

    public double getChange() {
        return quote.getPrice().doubleValue() - quote.getPreviousClose().doubleValue();
    }

    public double getDayHigh() {
        return quote.getDayHigh().doubleValue();
    }

    public double getDayLow() {
        return quote.getDayLow().doubleValue();
    }

    public String getCompanyName() {
        return stock.getName();
    }
}
