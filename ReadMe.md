# Get Started
0. For this minimal exempleyou expect to have "Feanaro" database and have "User" Collection in it
1. mvn package
2. running local mongod on your machine or configure your mongod server
3. start your application
4. with postman or other POST at "localhost:8080/Feanaro/User/subscribe" this json {"name":"Balthazar", "pws":"Test", "email":"test___@gmail.com", "role":["TEST"] }
5. then go to "localhost:8080/Feanaro/User"
6. there is no possibilities to retrieve ID with {id:timestamp, date}, i try some converstion, it didn't work.
