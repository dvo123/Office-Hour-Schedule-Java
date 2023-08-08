package com.cs4800.officehourservice;

import sibModel.*;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {
	private static String API_KEY;
    private static final String SENDER_EMAIL = "officehourplanner@gmail.com";
    
    static {
        Properties prop = new Properties();
        try {
            InputStream input = EmailService.class.getClassLoader().getResourceAsStream("com/cs4800/officehourservice/config.properties");
            prop.load(input);
            API_KEY = prop.getProperty("api.key");
        } catch (IOException e) {
            System.out.println("Failed to load configuration file: " + e.getMessage());
        }
    }

    public void sendAppointmentEmail(String userName, String emailAddress, String instructorEmail, Appointment appointment) {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setApiKey(API_KEY);

        TransactionalEmailsApi transactionalEmailsApi = new TransactionalEmailsApi();

        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email(SENDER_EMAIL));
        email.setTo(new ArrayList<>(Arrays.asList(new SendSmtpEmailTo().email(emailAddress))));
        email.setSubject("Appointment Details");
        email.setTextContent(
            "Dear " + userName + ",\n\n" +
            "Here are your appointment details:\n\n" +
            "Instructor: " + appointment.getInstructor() + "\n" +
            "Day: " + appointment.getDayOfWeek() + "\n" +
            "Time: " + appointment.getTime() + "\n" +
            "Location: " + appointment.getLocation() + "\n" + // Add location information
            "Your instructor's email address: " + instructorEmail + "\n\n" +
            "Thank you for using our service!\n" + "Office Hour Planner");

        try {
            transactionalEmailsApi.sendTransacEmail(email);
            System.out.println("Email sent to user successfully!");
        } catch (ApiException e) {
            System.out.println("\nFailed to send email: Please make sure you enter the correct email for instructors in txt file\nand enter the correct API key in config.properties file." + e.getMessage());
        }
    }

    public void sendAppointmentEmailInstructor(String instructorEmail, String userName, String userId, String emailAddress, Appointment appointment) {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setApiKey(API_KEY);

        TransactionalEmailsApi transactionalEmailsApi = new TransactionalEmailsApi();

        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email(SENDER_EMAIL));
        email.setTo(new ArrayList<>(Arrays.asList(new SendSmtpEmailTo().email(instructorEmail))));
        email.setSubject("Appointment Details");
        email.setTextContent(
            "Dear Professor " + appointment.getInstructor() + ",\n\n" +
            "A student wants to make an appointment with you!\n" +
            "Here are the appointment details:\n\n" +
            "Student: " + userName + "\n" +
            "ID: " + userId + "\n" +
            "Student's email address: " + emailAddress + "\n" +
            "Day: " + appointment.getDayOfWeek() + "\n" +
            "Time: " + appointment.getTime() + "\n" +
            "Location: " + appointment.getLocation() + "\n\n" + // Add location information
            "Thank you for using our service!\n" + "Office Hour Planner");

        try {
            transactionalEmailsApi.sendTransacEmail(email);
            System.out.println("\nEmail sent to instructor successfully!");
        } catch (ApiException e) {
            System.out.println("\nFailed to send email: Please make sure you enter the correct email for instructors in txt file\nand enter the correct API key in config.properties file." + e.getMessage());
        }
    }
}
