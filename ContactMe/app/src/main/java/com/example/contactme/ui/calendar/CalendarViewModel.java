package com.example.contactme.ui.calendar;

import androidx.lifecycle.ViewModel;

import custom.EventList;
import custom.MyApp;

public class CalendarViewModel extends ViewModel {

    private String path = MyApp.getAppContext().getFilesDir().getAbsolutePath();

    private EventList events = new EventList();

    public CalendarViewModel() {

    }
}