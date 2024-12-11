# Deep Purple

This repository contains the backend for the **Deep Purple** project, a text analysis software that deciphers emotions in client communications. The backend is built using Spring Boot and connects to a PostgreSQL database.

## Features

- RESTful API for managing communications.
- Database connectivity RDS postgre
- Hibernate for ORM support.
- Caching support using Caffeine.
- Reactive programming support with Spring WebFlux.
- gpt-4o-mini API for analysis.

## Getting Started

### Prerequisites

- Java 23 or higher
- Maven
- PostgreSQL

### Configuration

1. Clone the repository:

   ```bash
   git clone https://github.com/zylimz107/deeppurple.git
   cd deeppurple/backend

2. Or if using IntelliJ IDEA (Easiest method and less errors).
Import the Project:
Click on "File" > New > project from version control. Select git, put https://github.com/zylimz107/deeppurple.git in the URL box.
Press Clone.


4. Database Setup:

The database runs on aws RDS with postgre.

Your application.properties should look like this:
spring.application.name=backend
spring.datasource.url=jdbc:postgresql://<endpoint URL>:5432/<RDS database name>
spring.datasource.username=RDSusername
spring.datasource.password=RDSpassword


4. Set Up OpenAI API Key and RDS credentials in environment variables:

Add my OpenAI API key, RDSusername, RDSpassword(**ask me when you want to set up, or you can use your own if you have**) to your environment variables: 
if using intelliJ,after running the application once go to run > Edit Configurations: look for Environment Variables under backendapplication: Paste"OPENAI_API_KEY= (openai key here); username=...; password=... ". 

5. Build the Project

IntelliJ:

in maven toolbar on the right side:

the ide would probably have prompted to build.
reload project if not.

In the ide: 
go to Build > build project

6. Run BackendApplication.java on intelliJ


7. If you are familiar with postman, you can test it with the address: http://localhost:8080/api/communications.
Use a json for POST method, example:
{
  "content": "I am real glad you're here",
  "modelName": "gpt-4o-mini",
  "classificationType": "neutral"
}

8. Else, you can setup the simple react provided:
   
**Frontend Instructions**
Note that this frontend is just for testing purposes, there is no aesthetic consideration or extra functionality.

Install dependencies: Navigate to the frontend folder and install the required dependencies:

npm install

Start the React frontend: Run the following command to start the frontend:


npm start

Operations:

Save: Input content and select the "Save" operation to store a new communication.

Update: Provide the communication ID and new content to update an existing communication.

Delete: Enter the communication ID to delete a specific communication.

Get by ID: Fetch a communication by its ID and view the emotional analysis results.

Get All: Retrieve and display all communications stored in the backend.

Ensure that the backend is running and accessible at http://localhost:8080 for proper communication with the React frontend.







