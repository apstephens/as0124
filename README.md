# Tool Rental Application

A simple java package to rent tools from a fictional warehouse store

## Description

This is a sample solution and sample high-level design that specifies and implements a 
simple tool rental application, as per an undisclosed functional specification.  

The HLD makes indirect references to the functional spec, to avoid disclosure.

### Dependencies
* Expects to be run on a Java 17 JDK

### Repository Contents
A Java package "com.aps.toolrental" that implements a public RentalApplication class.
This class has a factory method ".checkout()" that creates rental agreement instances
per the supplied input, and an additional method to print the generated agreement to the console.

An additional package "com.aps.exercisetoolrental" is supplied to exercise the RentalApplication class.
It has a single class with a main() method that accepts input from the console and invokes the
"checkout" method in a loop. The loop may be terminated by entering "q" at any input prompt.

A companion HLD is located here: ([ToolRental/ToolRentalApplicationHighLevelDesign.pdf](https://github.com/apstephens/as0124/blob/master/ToolRental/ToolRentalApplicationHighLevelDesign.pdf))

## Authors
A. S.

## Version History

* 1.0
    * Initial Release

## Potential Improvements
* Enhance the package to be thread safe
* Change the configuration file types from ".properties" files to something more cleanly serializable in Java (e.g.:  JSON)
* Extend the "HolidayType" enumeration, HolidaySpec class, and RentalCalendar class to support "last XXXday of month" holidays, i.e.: US Memorial day

## License

This project is freely available to download.
