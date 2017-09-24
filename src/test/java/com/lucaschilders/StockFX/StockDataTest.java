package com.lucaschilders.StockFX;

import jdk.internal.org.objectweb.asm.Type;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Lucas on 9/24/2017.
 */
public class StockDataTest {
    StockData stockData;

    public StockDataTest() {
        stockData = new StockData("GOOG");
    }

    @Test
    public void getTodayPrice() throws Exception {
        assertTrue(stockData.getTodayPrice() > 0);
    }

    @Test
    public void getYesterdayPrice() throws Exception {
        assertTrue(stockData.getYesterdayPrice() > 0);
    }

    @Test
    public void getTicker() throws Exception {
        assertTrue(stockData.getTicker() != "");
    }

    @Test
    public void getVolume() throws Exception {
        assertTrue(stockData.volume > 0);
    }

    @Test
    public void getDayHigh() throws Exception {
        assertTrue(stockData.getDayHigh() > 0);
    }

    @Test
    public void getDayLow() throws Exception {
        assertTrue(stockData.getDayLow() > 0);
    }

    @Test
    public void getCompanyName() throws Exception {
        assertTrue(stockData.getCompanyName() != "");
    }

}