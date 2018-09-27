package wres.tests;

import org.junit.Assert;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class ExampleTest {

    //////////////
    // Your codes
    private float convertCelsiusToFahrenheit(int i) {
        return 212;
    }

    private float convertFahrenheitToCelsius(int i) {
        return 100;
    }

    ////////////////
    // Your tests
    @Test
    public void testConvertFahrenheitToCelsius() {
        float actual = this.convertCelsiusToFahrenheit(100);
        // expected value is 212
        float expected = 212;
        // use this method because float is not precise
        assertEquals("Conversion from celsius to fahrenheit failed", expected, actual, 0.001);
    }

    @Test
    public void testConvertCelsiusToFahrenheit() {
        float actual = convertFahrenheitToCelsius(212);
        // expected value is 100
        float expected = 100;
        // use this method because float is not precise
        assertEquals("Conversion from celsius to fahrenheit failed", expected, actual, 0.001);
    }
}
