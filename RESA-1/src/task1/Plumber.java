package task1;

/**
 * ***************************************************************************************************************
 * File:Plumber.java
 * Course: 17655
 * Project: Assignment 1
 * Copyright: Copyright (c) 2003 Carnegie Mellon University
 * Versions:
 * 1.0 November 2008 - Sample Pipe and Filter code (ajl).
 * <p/>
 * Description:
 * <p/>
 * This class serves as an example to illstrate how to use the PlumberTemplate to create a main thread that
 * instantiates and connects a set of filters. This example consists of three filters: a source, a middle filter
 * that acts as a pass-through filter (it does nothing to the data), and a sink filter which illustrates all kinds
 * of useful things that you can do with the input stream of data.
 * <p/>
 * Parameters: 		None
 * <p/>
 * Internal Methods:	None
 * <p/>
 * ****************************************************************************************************************
 */
public class Plumber {
    public static void main(String argv[]) {
        /****************************************************************************
         * Here we instantiate three filters.
         ****************************************************************************/

        SourceFilter Filter1 = new SourceFilter();
        DeleteFilter deleteFilter1 = new DeleteFilter(1);
        DeleteFilter deleteFilter2 = new DeleteFilter(3);
        DeleteFilter deleteFilter3 = new DeleteFilter(5);
        FeetToMeterFilter feetToMeterFilter = new FeetToMeterFilter(2);
        SinkFilter Filter3 = new SinkFilter(3);

        /****************************************************************************
         * Here we connect the filters starting with the sink filter (Filter 1) which
         * we connect to Filter2 the middle filter. Then we connect Filter2 to the
         * source filter (Filter3).
         ****************************************************************************/

        Filter3.Connect(feetToMeterFilter);
        feetToMeterFilter.Connect(deleteFilter3);
        deleteFilter3.Connect(deleteFilter2);
        deleteFilter2.Connect(deleteFilter1);
        deleteFilter1.Connect(Filter1);

        /****************************************************************************
         * Here we start the filters up. All-in-all,... its really kind of boring.
         ****************************************************************************/

        Filter1.start();
        deleteFilter1.start();
        deleteFilter2.start();
        deleteFilter3.start();
        feetToMeterFilter.start();
        Filter3.start();

    } // main

} // Plumber