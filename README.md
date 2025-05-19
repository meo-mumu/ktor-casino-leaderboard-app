# Casino Leaderboard API

A Ktor-based REST API for managing player rankings in a tournament.

## Installation and Launch

1. **Start local dynamoDb databases**

After starting your docker engine, run this command
```bash
docker-compose up -d
```
It will launch two local databases instance:
- `dev`: Local development (DynamoDB port 8000)
- `test`: Testing environment (DynamoDB port 8001)

2. **Launch the application - Development mode (default)**

_macOS/Linux:_
```bash
ENVIRONMENT=dev ./gradlew run
```
_Windows PowerShell:_
```bash
$env:ENVIRONMENT="dev"; .\gradlew.bat run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Players Management
- `POST /players` - Add new player
  ```json
  { "pseudo": "player1" }
  ```
- `PUT /players/{pseudo}/points` - Update player points
  ```json
  { "points": 10 }
  ```
- `GET /players/{pseudo}` - Get player with rank
- `GET /players` - List all players sorted by points
- `DELETE /players` - Remove all players

A postman collection is available on this project: `postman\ktor-casino-leaderboard-app.postman_collection.json`

### Validation Rules
- Pseudo: 3-20 characters, alphanumeric and underscores only
- Points: Positive integers only

## Testing
End-to-end tests have been implemented to simulate scenarios. 
It ensures the complete flow from API calls to database operations works correctly. 
Each test starts with a clean database state and follows a specific user story - from adding new players to retrieving 
their updated rankings. The test suite covers both success paths (normal tournament operations) 
and failure scenarios (duplicate players, invalid inputs). 
The testing strategy leverages Koin for dependency injection and uses DynamoDB's test instance 
to provide a realistic but isolated testing environment.

To launch the tests use these commands :

_macOS/Linux:_
```bash
ENVIRONMENT=test ./gradlew test
```
_Windows PowerShell:_
```bash
$env:ENVIRONMENT="test"; .\gradlew.bat test
```

## IntelliJ Run Configurations

Development mode :
![intelliJ run conf - dev.png](doc/intelliJ%20run%20conf%20-%20dev.png)

Tests mode : 
![intelliJ run conf - test.png](doc/intelliJ%20run%20conf%20-%20test.png)

## Remaining Tasks

### AWS Integration
- Configure DynamoDB on AWS
- Set up IAM roles and permissions
- Implement proper credentials management

### Performance Testing
- Implement load tests
- Define performance SLAs
- Set up monitoring dashboards

### CI/CD Pipeline
- Define PR policies
- Testing Automation

### Security Improvements
- Implement authentication/authorization
- Add API rate limiting
- Enable HTTPS
- Set up AWS security groups
- Configure network policies

### Logging Strategy
- Implement structured logging
- Configure log aggregation (e.g., Datadog)
- Set up log retention policies
- Define logging levels
- Implement trace correlation

## Design Choices and Business Logic Considerations

### Performance to get the rank of each player
The ranking system implementation calculates player ranks dynamically during API calls rather than storing them permanently. 
This design choice ensures data consistency and eliminates the need for cascade updates in the database, 
significantly reducing write operations overhead.

**Note on this project, that it is dynamoDb that takes care of the sorting, not the backend.**

For scenarios with high read volumes and lower update frequencies, we could consider an alternative approach: 
storing ranks directly in the database and updating them through a batch process. 
This process would aggregate point changes over configurable time intervals, optimizing the balance between consistency and performance.
It also depends on Business conditions.

### Update points feature
The current points system only supports point addition. 
We could add an updating method to handle the suppression of points (on the same endpoint)

