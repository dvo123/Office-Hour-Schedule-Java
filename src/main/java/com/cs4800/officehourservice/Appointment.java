package com.cs4800.officehourservice;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Appointment {
    private String instructor;
    private DayOfWeek dayOfWeek;
    private LocalTime time;
    private String email;
    private String location;

    public Appointment(String instructor, DayOfWeek dayOfWeek, LocalTime time, String location) {
        this.instructor = instructor;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.location = location;
    }

    public String getInstructor() {
        return instructor;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getTime() {
        return time;
    }

	public String getEmail() {
		return email;
	}
	
	public String getLocation() {
        return location;
    }
}
