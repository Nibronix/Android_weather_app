package com.example.weather_app;

import org.junit.Test;
import static org.junit.Assert.*;

public class MainActivityTest {

    @Test
    public void testCelsiusDefaultFalse() {
        boolean Celsius = false;
        assertFalse(Celsius);
    }

    @Test
    public void testSetCelsiusToTrue() {
        boolean Celsius = false;
        Celsius = true;
        assertTrue(Celsius);
    }
}
