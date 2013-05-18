cryptomainia
============

Utility for encrypting arguments passed to Java  command-line applications.

Some essential java applications take passwords as command-line arguments. 
(e.g Liquibase) This is unfortunate because command-line arguments are visible
in log files and in process tables and hence the passwords are easily viewable
by anyone with access to the machine. 
 
The goal of this utility is hide passwords from the operating system by the user
encrypting them then having them decrypted within the Java process before they
 are passed to the application. 
 
 Further, the utility should be general enough for masking passwords for any Java 
 application, not just Liquibase.
 
 
 
