# NoPass

NoPass is a server backend for password management.
It's written in Scala using the Play! Framework.
MySQL is the database of choice.

To start, you will need to install MySQL and create a database named "nopass", or you can update the application.conf with the database name of your choice.

The application leaves the encryption to the client. The expectation is that password is already encrypted so that the application never has to deal with it.

## Database tables
Database table scehmas can be checked out in conf/evolutions/default.
There are two tables. Accounts and passwords. Passwords table allows for grouping using a concept of folders.

## Requirements
Scala: 2.12.3

Play!: 2.6.6

SBT: 1.0.2