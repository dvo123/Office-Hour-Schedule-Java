package com.cs4800.officehourservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/office-hours")
public class OfficeHourController {
	private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static class OfficeHour {
        private DayOfWeek day;
        private LocalTime time;
        private String instructor;
        private String location;
        private String email;

        public OfficeHour(DayOfWeek day, LocalTime time, String instructor, String location, String email) {
            this.day = day;
            this.time = time;
            this.instructor = instructor;
            this.location = location;
            this.email = email;
        }

        public DayOfWeek getDayOfWeek() {
            return day;
        }
        
        public LocalTime getTime() {
        	return time;
        }

        public String getInstructor() {
            return instructor;
        }

        public String getLocation() {
            return location;
        }

        public String getEmail() {
            return email;
        }
    }
    
    @GetMapping
    public ResponseEntity<String> getOfficeHours() {

        Scanner scanner = new Scanner(System.in);
        String filePath;

        while (true) {
            System.out.print("Enter the path to the input text file: ");
            filePath = scanner.nextLine();
            
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                break;
            } else {
                System.out.println("File not found. Please enter a valid file path.");
            }
        }
        List<OfficeHour> officeHours = new ArrayList<>();
        List<String> validInstructors = new ArrayList<>();
        List<DayOfWeek> validDays = new ArrayList<>();
        List<LocalTime> validHours = new ArrayList<>();
        StringBuilder responseBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            List<List<String>> scheduleByDay = new ArrayList<>();
            for (int i = 0; i < 5; i++) { // Updated to 5 for Monday to Friday only
                scheduleByDay.add(new ArrayList<>());
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String dayOfWeekStr = parts[0].trim();
                    String timeStr = parts[1].trim();
                    String instructor = parts[2].trim();
                    String location = parts[3].trim();
                    String email = parts[4].trim();

                    DayOfWeek dayOfWeek = DayOfWeek.from(dayOfWeekFormatter.parse(dayOfWeekStr));
                    LocalTime time = LocalTime.parse(timeStr, timeFormatter);
                    validInstructors.add(instructor);
                    validDays.add(dayOfWeek);
                    validHours.add(time);

                    int dayIndex = dayOfWeek.getValue();
                    if (dayIndex >= 1 && dayIndex <= 5) { // Updated to include Monday to Friday only
                        String scheduleEntry = String.format("%s - %s - %s<br>",
                            time, instructor, location);
                        scheduleByDay.get(dayIndex - 1).add(scheduleEntry); // Adjusted index for Monday to Friday
                    }
                    OfficeHour officeHour = new OfficeHour(dayOfWeek, time, instructor, location, email);
                    officeHours.add(officeHour);
                }
            }

            StringBuilder scheduleBuilder = new StringBuilder();
            scheduleBuilder.append("<html><head><title>Office Hour Planner</title></head><body><style>td { padding: 5px; white-space: nowrap; }</style><h1>Office Hour Planner</h1><table>");

            // Build the table header
            scheduleBuilder.append("<tr>");
            for (int dayIndex = 1; dayIndex <= 5; dayIndex++) { // Updated to include Monday to Friday only
                DayOfWeek dayOfWeek = DayOfWeek.of(dayIndex);
                scheduleBuilder.append("<th>").append(dayOfWeek).append("</th>");
            }
            scheduleBuilder.append("</tr>");

            // Sort the schedule entries within each day by time
            for (List<String> daySchedule : scheduleByDay) {
                daySchedule.sort(Comparator.comparing(s -> LocalTime.parse(s.split(" - ")[0], timeFormatter)));
            }

            // Build the schedule table
            int maxDayScheduleSize = scheduleByDay.stream().mapToInt(List::size).max().orElse(0);
            for (int i = 0; i < maxDayScheduleSize; i++) {
                scheduleBuilder.append("<tr>");
                for (List<String> daySchedule : scheduleByDay) {
                    if (i < daySchedule.size()) {
                        scheduleBuilder.append("<td>").append(daySchedule.get(i)).append("</td>");
                    } else {
                        scheduleBuilder.append("<td></td>");
                    }
                }
                scheduleBuilder.append("</tr>");
            }

            scheduleBuilder.append("</table></body></html>");

            String schedule = scheduleBuilder.toString();
            responseBuilder.append(schedule);
            
            // Prompt the user for making an appointment
            System.out.print("Do you want to make an appointment? (yes/no): ");
            boolean validResponse = false;
            String response = null;

            while (!validResponse) {
            	response = scanner.nextLine().trim().toLowerCase();

                if (response.equals("yes") || response.equals("no")) {
                	validResponse = true;
                } else {
                    System.out.print("Invalid response. Please try again here: ");
                }
            }

            if (response.equals("yes")) {
            	
                // Get the user's info
            	String userName = null;
            	String userId = null;
            	String emailAddress = null;

            	// Enter the instructor's name
            	System.out.print("Enter your name (required*): ");
            	userName = scanner.nextLine().trim();

            	// Check if the name is empty or contains non-letter or non-space characters
            	while (userName.isEmpty() || !userName.matches("[a-zA-Z\\s]+")) {
            	    System.out.println("Invalid name. Please enter a valid name containing only alphabet letters and spaces.");
            	    
            	    // Prompt the user to re-enter the name
            	    System.out.print("Enter your name (required*): ");
            	    userName = scanner.nextLine().trim();
            	}

            	 // Enter the ID
            	    System.out.print("Enter your ID (required*): ");
            	    userId = scanner.nextLine().trim();

            	    // Check if the ID is empty or contains non-numeric characters
            	    while (userId.isEmpty() || !userId.matches("\\d+")) {
            	        System.out.println("Invalid ID. Please enter a valid ID containing only numbers.");
            	        
            	        // Prompt the user to re-enter the ID
            	        System.out.print("Enter your ID (required*): ");
            	        userId = scanner.nextLine().trim();
            	    }

            	 // Enter the email address
            	    System.out.print("Enter your email address (required*): ");
            	    emailAddress = scanner.nextLine().trim();

            	    // Check if the email address is empty or invalid
            	    while (emailAddress.isEmpty() || !EMAIL_PATTERN.matcher(emailAddress).matches()) {
            	        System.out.println("Invalid email address. Please enter a valid email address.");
            	        
            	        // Prompt the user to re-enter the email address
            	        System.out.print("Enter your email address (required*): ");
            	        emailAddress = scanner.nextLine().trim();
            	    }

                // Print the schedule on the console
                System.out.println("\nHere is your Office Hour List:");
                officeHours.stream()
                        .sorted(Comparator.comparing(OfficeHour::getDayOfWeek).thenComparing(OfficeHour::getTime))
                        .forEach(officeHour -> {
                            String dayOfWeek = officeHour.getDayOfWeek().toString();
                            String time = officeHour.getTime().toString();
                            String instructor = officeHour.getInstructor();
                            String location = officeHour.getLocation();
                            System.out.printf("%s - %s - %s - %s%n", dayOfWeek, time, instructor, location);
                        });
                
                boolean isValidAppointment = false;
                String instructorEmail = null;
                while (!isValidAppointment) {
                    // Get appointment details from the user
                    System.out.print("\nEnter the instructor's name: ");
                    String instructor = scanner.nextLine().trim();

                    // Check if the entered instructor name is valid
                    if (!validInstructors.contains(instructor)) {
                        System.out.println("Invalid instructor name. Please try again. ");
                        continue;
                    }

                    System.out.print("Enter the day of the week (e.g., Monday, Tuesday, etc.): ");
                    String dayOfWeekStr = scanner.nextLine().trim();

                    // Check if the entered day of the week is Saturday or Sunday
                    DayOfWeek dayOfWeek;
                    try {
                        dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr.toUpperCase());
                        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                            System.out.println("Professor does not have office hours on Saturday and Sunday.");
                            continue;
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid day of the week. Please try again.");
                        continue;
                    }

                    System.out.print("Enter the appointment time (HH:mm): ");
                    String timeStr = scanner.nextLine().trim();

                    // Check if the entered appointment time has the correct format
                    if (!timeStr.matches("\\d{2}:\\d{2}")) {
                        System.out.println("Invalid time format. Please use the HH:mm format (e.g., 09:30).");
                        continue;
                    }

                    LocalTime time = LocalTime.parse(timeStr);


                    // Check if the entered day and time match any valid combination
                    isValidAppointment = false;
                    for (int i = 0; i < validInstructors.size(); i++) {
                        if (instructor.equals(validInstructors.get(i)) && dayOfWeek == validDays.get(i)
                                && time.equals(validHours.get(i))) {
                        	instructorEmail = officeHours.get(i).getEmail();
                            isValidAppointment = true;
                            break;
                        }
                    }
                    
                    if (!isValidAppointment) {
                        System.out.println("The office hour you entered for this instructor does not exist. Please try again.");
                    }

                    if (isValidAppointment) {
                      
                    	String appointmentLocation = null;
                        for (OfficeHour officeHour : officeHours) {
                            if (instructor.equals(officeHour.getInstructor()) && dayOfWeek == officeHour.getDayOfWeek()
                                    && time.equals(officeHour.getTime())) {
                                instructorEmail = officeHour.getEmail();
                                appointmentLocation = officeHour.getLocation(); // Get the location from the OfficeHour object
                                break;
                            }
                        }
                        
                    	// Create an Appointment object
                        Appointment appointment = new Appointment(instructor, dayOfWeek, time, appointmentLocation);
                        
                        // Send the email to Instructor
                        EmailService emailService = new EmailService();
                        emailService.sendAppointmentEmailInstructor(instructorEmail, userName, userId, emailAddress, appointment);
                        
                        System.out.println("Appointment is scheduled successfully!");
                        String successMessage = "<p>Appointment is scheduled successfully!</p>";
                        responseBuilder.append(successMessage);
                        
                        // Ask the user if they want an email about the appointment
                        System.out.print("\nDo you want to receive an email with the appointment details? (yes/no): ");       
                        boolean validEmailResponse = false;
                        String emailResponse = null;

                        while (!validEmailResponse) {
                            emailResponse = scanner.nextLine().trim().toLowerCase();

                            if (emailResponse.equals("yes") || emailResponse.equals("no")) {
                                validEmailResponse = true;
                            } else {
                                System.out.print("Invalid response. Please try again here: ");
                            }
                        }

                        if (emailResponse.equals("yes")) {

                            
                            for (OfficeHour officeHour : officeHours) {
                                if (instructor.equals(officeHour.getInstructor()) && dayOfWeek == officeHour.getDayOfWeek()
                                        && time.equals(officeHour.getTime())) {
                                    instructorEmail = officeHour.getEmail();
                                    break;
                                }
                            }

                            // Send the email to user
                            emailService.sendAppointmentEmail(userName, emailAddress, instructorEmail, appointment);
                            String successMessageUser = "<p>Email sent to user successfully!</p>";
                            responseBuilder.append(successMessageUser);
                        }
                        else if (emailResponse.equals("no")) {
                            // User chose not to make an appointment
                            System.out.println("Thank you. Have a nice day!");
                            String goodbyeMessage = "<p>Thank you. Have a nice day!</p>";
                            responseBuilder.append(goodbyeMessage);
                        }
                    } else {
                    	String invalidAppointmentMessage = "<p>Invalid appointment details. Please try again.</p>";
                        responseBuilder.append(invalidAppointmentMessage);
                    }
                }
            }
         // Close the HTML tags
            responseBuilder.append("</table></body></html>");
            String combinedResponse = responseBuilder.toString();
            return ResponseEntity.ok(combinedResponse);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            scanner.close();
        }
    }
}
