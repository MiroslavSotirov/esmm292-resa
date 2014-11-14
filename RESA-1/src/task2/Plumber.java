package task2;

/**
 * ***************************************************************************************************************
 * File:Plumber.java
 * <p/>
 * Description:
 * <p/>
 * This class instantiates and connects all of our Filters to create the output.
 * <p/>
 * ****************************************************************************************************************
 */
public class Plumber {
    public static void main(String argv[]) {

        // instantiate three filters.
        SourceFilter Filter1 = new SourceFilter();
        DeleteFilter deleteFilter1 = new DeleteFilter(1);
        DeleteFilter deleteFilter5 = new DeleteFilter(5);
        FeetToMeterFilter feetToMeterFilter = new FeetToMeterFilter(2);
        FahrenheitToCelsiusFilter fahrenheitToCelsiusFilter = new FahrenheitToCelsiusFilter(4);
        PressureWildPointsFilter wildpointsFilter = new PressureWildPointsFilter(3, 10);
        SinkFilter Filter3 = new SinkFilter(new int[]{0, 4, 2, 3});
        SinkFilter wildpointsOutput = new SinkFilter(new int[]{0, 3});

        // connect the filters to each other
        Filter3.Connect(wildpointsFilter, 0, 0);
        wildpointsOutput.Connect(wildpointsFilter, 0, 1);
        wildpointsFilter.Connect(fahrenheitToCelsiusFilter, 0, 0);
        fahrenheitToCelsiusFilter.Connect(feetToMeterFilter, 0, 0);
        feetToMeterFilter.Connect(deleteFilter5, 0, 0);
        deleteFilter5.Connect(deleteFilter1, 0, 0);
        deleteFilter1.Connect(Filter1, 0, 0);

        // start the filters
        Filter1.start();
        deleteFilter1.start();
        deleteFilter5.start();
        feetToMeterFilter.start();
        fahrenheitToCelsiusFilter.start();
        wildpointsFilter.start();
        Filter3.start();
        wildpointsOutput.start();

    }
}