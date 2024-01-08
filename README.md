# POI and POSITION Tracking System

## Overview

This project aims to provide a backend API for querying the duration that vehicles have spent within each Point of Interest (POI). The API will be consumed by a frontend application, enabling the display of a table with summarized information for the user. Users can filter the information by date and/or vehicle plate on the frontend. The backend API is responsible for delivering the filtered information to the frontend, with no processing on the client side.

## Mandatory Functional Requirements

### 1. Query API for Vehicle Duration within POIs

The backend API should have endpoints that allow the frontend to query and retrieve the duration that vehicles from a list of positions have spent within each Point of Interest (POI) from the list of POIs registered by the client.

### 2. Frontend Integration

The API responses will be integrated into the frontend application to present summarized information to the user. This could involve displaying the data in a table format for user-friendly consumption.

### 3. Filtering Options

Users should be able to filter the information based on date and/or vehicle plate directly from the frontend. The backend API should handle these filters and provide the relevant information to the frontend.

### 4. No Frontend Processing

There should be no processing of data on the frontend side. The backend API is responsible for delivering the filtered and processed information to the frontend for display.

## Usage

To set up and run the project, follow the steps below:

1. Clone the repository to your local machine.
2. Set up and run the backend API according to the provided instructions.
3. Integrate the API responses into your frontend application.
4. Ensure that filtering options are working as expected on the frontend.
5. Run the frontend application and view the summarized information.


## Swagger Documentation

The API is documented using Swagger, which provides an interactive documentation interface. You can explore and test the API endpoints using the Swagger UI.

### Swagger UI

After setting up and running the project, you can access the Swagger UI documentation at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html). This interface allows you to interactively explore the available endpoints, their request parameters, and response structures.

### Importing Postman Collection

To test and interact with the backend API, you can also import the Postman collection file `position-api.postman_collection.json` located in the `test/resources/data` folder.

For testing purposes, you can import sample data into the application. Place the files `pois.csv` and `positions.csv` in the `test/resources/data` folder.

```plaintext
/your-project-root
|-- src
|-- test
    |-- resources
        |-- data
            |-- pois.csv
            |-- positions.csv
