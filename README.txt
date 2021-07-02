06-29-2021
----------------------
Scheduling Application v1.1
Author: Sven Fulenchek
svenfulen@gmail.com
----------------------
IDE Information:
IntelliJ IDEA 2020.3.4 (Community Edition)
Build #IC-203.8084.24, built on April 27, 2021
Runtime version: 11.0.10+8-b1145.96 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
System: Windows 10 Home x64

Libraries:
Java JDK: OpenJDK version 15.0.2
JavaFX SDK: 11.0.2
MySQL Driver: MySQL Connector 8.0.25

-----------------------
Instructions:

To use the application, set the root folder as source, and create
a build configuration that points to the main class, "sample.Main".

The default login information is "test" for both username and password.
----------------------
This application can be used to manage appointments for different customers using a relational database.

Functionalities:
Create, Update, Delete appointments
Create, Update, Delete customers
View appointments by timeframe (week or month)
Alerts the user when there is an appointment soon (within 15 minutes)

----------------------
Reporting Functionalities

The application can generate the following reports:
1. Tracking the number of appointments by month
2. Tracking the number of appointments by week
3. Tracking the number of appointment types in a given month
4. Tracking the number of appointment types in a given week
5. Showing all types of appointments and the number of each type of appointment in a given month
6. Showing all types of appointments and the number of each type of appointment in a given week
7. Showing a schedule of appointments for a given contact in a given month
8. Showing a schedule of appointments for a given contact in a given week

To use this functionality, 
1. Select a date from the date picker in the top-right of the main GUI
2. Click the "View Reports" button near the bottom-left of the main GUI
.. The Reports screen will show available reports for the selected time period.
