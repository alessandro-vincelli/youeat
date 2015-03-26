**Dev environment, setup notes:**

Software required:
**Sun JDK 1.6.x**  Apache Maven 2.2.x
**Postgresql 8.4.x**


---

**DB Setup**

Creates a DB user "eat", (superuser?!)
```
CREATE ROLE eat LOGIN ENCRYPTED PASSWORD 'md56301041c2cdedba60df6877c58fd7522' SUPERUSER INHERIT NOCREATEDB NOCREATEROLE;
UPDATE pg_authid SET rolcatupdate=false WHERE rolname='eat';
```

Create two databases: eat (used by the web app), youet\_ci (useb by test and CI) :

```
CREATE DATABASE eat  WITH OWNER = eat ENCODING = 'UTF8' LC_COLLATE = 'it_IT.UTF-8'  LC_CTYPE = 'UTF-8'  CONNECTION LIMIT = -1;
CREATE DATABASE youeat_ci  WITH OWNER = eat ENCODING = 'UTF8' LC_COLLATE = 'it_IT.UTF-8'  LC_CTYPE = 'UTF-8'  CONNECTION LIMIT = -1;
```

Import the latest zero\_data file [eat-zero-data.dump](http://code.google.com/p/youeat/downloads/list)

```
pg_restore -U eat -d eat eat-zero-data.dump
```


---


**Configuration**

Create an empty file /etc/bbox/youeat.properties (improve me)


---


**Lucene Index dir**

Create a dir:

```
sudo mkdir /var/lucene/youeat/luceneIndex
```
```
chown -Rf yourser /var/lucene/youeat/luceneIndex
```

The directory could be modified in the persistence.xml


---


**Checkout your code and run the application**

[Check out the code](http://code.google.com/p/youeat/source/checkout)

Run the application by maven-jetty-plugin and connect to http://localhost:8889
```
mvn jetty:run 
```

Or deploy and run the application on  apache-tomcat-6.0.x with your favorite IDE.