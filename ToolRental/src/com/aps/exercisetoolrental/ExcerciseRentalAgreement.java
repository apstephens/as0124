package com.aps.exercisetoolrental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.aps.toolrental.RentalAgreement;

public class ExcerciseRentalAgreement {

	private static String toolCode = null;
	private static LocalDate checkoutDate = null;
	private static int rentalDayCount = 0;
	private static int discountPercent = 0;
	private static boolean quit = false;
	
	private static BufferedReader reader = null;
	private static final String DATE_FORMAT = "MM/dd/yy";
	
	private static String readString(String prompt) {
		String input = null;
		System.out.print(prompt + " ");
		try {
			input = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			quit = true;
		}
		if (input.equalsIgnoreCase("q")) {
			input = null;
			quit = true;
		}
		return input;
	}
	
	private static LocalDate readDate(String prompt) {
		String input = null;
		LocalDate date = null;
		do {
			System.out.print(prompt + " ");
			try {
				input = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				quit = true;
			}
			if (input.equalsIgnoreCase("q")) {
				quit = true;
			} else {
				try {
					date = LocalDate.parse(input, DateTimeFormatter.ofPattern(DATE_FORMAT));
				} catch (DateTimeException e) {
					System.out.println(input + " is not a valid date.");
				}
			}
		} while (!quit && date == null);
		return date;
	}
	
	private static int readInt(String prompt) {
		String input = null;
		Integer intVal = null;
		do {
			System.out.print(prompt + " ");
			try {
				input = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				quit = true;
			}
			if (input.equalsIgnoreCase("q")) {
				quit = true;
			} else {
				try {
					intVal = Integer.decode(input);
				} catch (NumberFormatException e) {
					System.out.println(input + " is not a valid number.");
				}
			}
		} while (!quit && intVal == null);
		return intVal.intValue();
	}
	
	public static void main(String[] args) {
		reader = new BufferedReader(new InputStreamReader(System.in));
		do {
			System.out.println("\nPress 'q' at any prompt to quit.");
			toolCode = readString("Please enter a tool code to rent:");
			if (!quit) {
				checkoutDate = readDate("Please enter a rental date (mm/dd/yy):");
			}
			if (!quit) {
				rentalDayCount = readInt("Please enter the number of days to rent:");
			}
			if (!quit) {
				discountPercent = readInt("Please enter the discount:");
			}
			if (!quit) {
				try {
				RentalAgreement agreement = RentalAgreement.checkout(toolCode, checkoutDate, rentalDayCount, discountPercent);
				agreement.printAgreement();
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				} catch (RuntimeException e) {
					System.out.println("There is an error in the RentalApplication configuration.  Please fix and retry.");
					quit = true;
				}
			}
		} while (!quit);
	}

}
