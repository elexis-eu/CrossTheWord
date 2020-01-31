# Cross the word - Back end
The application Back-End code is divided into two sections:
* MongoDB: it contains the database management classes. They control the database access and its contents, which are user data, crosswords, popup
data and statistics.
* Servlet: it contains the HTTP Servlet management classes. They are used to manage data that flow from the server to the client and vice versa, saving data or reading them from the database.

## How to execute "Cross the word" server:
Once you logged into the server on which Cross the word is hosted you need to check that MongoDB and Apache Tomcat are correctly running.

* As regards MongoDB:
Execute the command “service mongod status” on the bash.
If it is not running execute the command “service mongod start” and check its status again.

* As regards Apache Tomcat:
To start Tomcat Server you need to access the Apache Tomcat /bin directory and execute the command “./startup.sh”. If you need to shut it down use the “./shutdown.sh” command.
