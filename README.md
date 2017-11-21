# NoPass

NoPass is a server backend for password management.
It's written in Scala using the Play! Framework.
MySQL is the database of choice.

To start, you will need to install MySQL and create a database named "nopass", or you can update the application.conf with the database name of your choice.

The application leaves the encryption to the client. The expectation is that password is already encrypted so that the application never has to deal with it.

## Database tables
Database table scehmas can be checked out in conf/evolutions/default.
There are two tables. Accounts and passwords. Passwords table allows for grouping using a concept of folders.
### Accounts table
```
CREATE TABLE `accounts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `verified` tinyint(1) NOT NULL DEFAULT '0',
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8
```

###Passwords table
```
CREATE TABLE `passwords` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL,
  `folder` varchar(255) NOT NULL,
  `data` text,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_id_folder` (`user_id`,`folder`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8
```

`data` field in the `passwords` table is expected to be an encrypted and serialized form of what the client understands. And example of this object is

```
{
  url: String
  username: String
  password: String
  notes: String
}

```

The expectation is that the client will build this object, encrypt and serialize before writing to the server. Or deserialzie and decrypt before showing it to the users.

## Requirements
Scala: 2.12.3

Play!: 2.6.6

SBT: 1.0.2