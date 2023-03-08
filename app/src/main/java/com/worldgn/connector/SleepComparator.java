package com.worldgn.connector;

import java.util.Comparator;

/**
 * Created by WGN on 05-10-2017.
 */

class SleepComparator {

    //same comparator used for  startsleeptime after adding all sleep datass
    public static final Comparator<SleepData> startSleepComparator = new Comparator<SleepData>() {
        @Override
        public int compare(SleepData sleepData1, SleepData sleepData2) {
            if(sleepData1.getStartSleep() < sleepData2.getStartSleep())
                return -1;
            else if(sleepData1.getStartSleep() == sleepData2.getStartSleep())
                return 0;
            else
                return 1;


        }
    };

    //same comparator used for  stopsleeptime after adding all sleep datass
    public static final Comparator<SleepData> stopSleepComparator = new Comparator<SleepData>() {
        @Override
        public int compare(SleepData sleepData1, SleepData sleepData2) {
            if(sleepData1.getStartSleep() < sleepData2.getStartSleep())
                return 1;
            else if(sleepData1.getStopSleep() == sleepData2.getStopSleep())
                return 0;
            else
                return -1;


        }
    };

}
