# SQL Shell

Connect to the database :
```console
postgres=# \c calendar_app
```

List the tables present in the database
```console
calendar_app=# \dt
```

# API 

## User

### Create a User

```
POST "http://localhost:8080/api/users"
```

**Request Body Example**:

```
{
    firstName: "Del", 
    lastName: "vr", 
    email: "del.vr@mail.com",
    professions: ["Danseur"]
}
```

**If successful return newly created user** e.g: 
```
{
    id : 23,
    firstName: "Del", 
    lastName: "vr", 
    email: "del.vr@mail.com"
}
```

**Possible Response Codes:**

- `201 Created`: User successfully created.
- `409 Conflict`: Email already exists.
- `400 Bad Request`: Invalid request body, email field does not contains an email format string, ... .

### Get a User with its id

```
GET "http://localhost:8080/api/users/{id}"
```

**If successful return the user with the given id** e.g: 
```
{
    id : 23,
    firstName: "Del", 
    lastName: "vr", 
    email: "del.vr@mail.com"
}
```

**Possible Response Codes:**

- `200 Ok`: User successfully retrieved.
- `404 Not Found`: No user found with that id.

### Get a User with its email

```
GET "http://localhost:8080/api/users?email={email}"
```

**If successful return the user with the given email** e.g: 
```
{
    id : 23,
    firstName: "Del", 
    lastName: "vr", 
    email: "del.vr@mail.com"
}
```

**Possible Response Codes:**

- `200 Ok`: User successfully retrieved.
- `404 Not Found`: No user found with that email.

### Update a User

```
PUT "http://localhost:8080/api/users/{id}"
```

**Request Body Example**:

```
{
    firstName: "Del", 
    lastName: "vr", 
    email: "del.vr@mail.com",
    professions: ["Danseur"]
}
```

**If successful return newly created user** e.g: 
```
{
    id : 23,
    firstName: "Del", 
    lastName: "vr", 
    email: "del.vr@mail.com"
}
```

**Possible Response Codes:**

- `201 Created`: User successfully updated.
- `409 Conflict`: Email already exists.
- `400 Bad Request`: Invalid request body, email field does not contains an email format string, ... .

### Delete User based on its email

```
DELETE "/api/users/{email}"
```

**Possible Response Codes:**

- `204 No Content`: User delete successfully.
- `404 Not Found`: No user found with that email.

### Get is User an Organizer

```
GET "/api/users/organizer/{mail}"
```

**If successful return Boolean value** e.g: 
```
true
```

**Possible Response Codes:**

- `200 OK`: Value successfully retrieved.
- `404 Not Found`: No user found with that email address.

### Get User Professions

```
GET "/api/users/{email}/professions"
```

**If successful return list of professions** e.g: 
```
[
    "Danseur"
]
```

**Possible Response Codes:**

- `200 OK`: Value successfully retrieved.
- `404 Not Found`: No user found with that email address.

## Profession

### Get all the professions

```
GET "/api/professions"
```

**If successful return all the professions** e.g: 
```
[
    {profession: "Directeur artistique"},
    {profession: "Danseur"}
]
```

**Possible Response Codes:**

- `200 OK`: Value successfully retrieved.
- `204 No Content`: No professions present in the database.

## Project

### Create a project

```
POST "http://localhost:8080/api/projects"
```

**Request Body Example**:

```
{
    "name": "Christmas show",
    "description": "Winter show with santa...",
    "beginningDate": "2020-07-01",
    "endingDate": "2020-12-26",
    "organizerEmail": "del.vr@mail.com"
}
```

**If successful return newly created project** e.g: 
```
{
    id : 3,
    name: "Christmas show", 
    description: "Winter show with santa...", 
    beginningDate: "2020-07-01",
    endingDate: "2020-12-26"
}
```

**Possible Response Codes:**

- `201 Created`: Project successfully created.
- `400 Bad Request`: Invalid request body, invalid email, ending date before beginning date, ... .

### Get a project

```
GET "http://localhost:8080/api/projects/{id}"
```

**If successful return the project** e.g: 
```
{
    id : 3,
    name: "Christmas show", 
    description: "Winter show with santa...", 
    beginningDate: "2020-07-01",
    endingDate: "2020-12-26"
}
```

**Possible Response Codes:**

- `200 OK`: Project successfully retrieved.
- `404 Not Found`: No project found with the given id.

### Get the projects of a user

```
GET "http://localhost:8080/api/projects/user/{email}"
```

**If successful return the list of projects of the user** e.g: 
```
[
    {
        "id": 1,
        "name": "Christmas show",
        "description": "Winter show with santa...",
        "beginningDate": "2020-07-01",
        "endingDate": "2020-12-26"
    }
]
```

**Possible Response Codes:**

- `200 OK`: Successfully retrieve the list of projects of that user.
- `404 Not Found`: No user found with the given email.

### Update a project

```
PUT "http://localhost:8080/api/projects/{id}"
```

**Request Body Example**:

```
{
    "name": "Christmas show 2.0",
    "description": "Winter show with Santa and its elves",
    "beginningDate": "2020-07-01",
    "endingDate": "2020-12-26",
}
```

**If successful return newly created project** e.g: 
```
{
    "id": 3,
    "name": "Christmas show 2.0",
    "description": "Winter show with Santa and its elves",
    "beginningDate": "2020-07-01",
    "endingDate": "2020-12-26"
}
```

**Possible Response Codes:**

- `200 OK`: Project successfully updated.
- `404 Not Found`: No project found with the given ID.

### Delete a project

```
DELETE "http://localhost:8080/api/projects/{projectId}"
```

**Possible Response Codes:**

- `204 No Content`: Successfully delete the project with the given id.
- `404 Not Found`: No project or role found with the given id.

### Add a user to the project

```
POST "http://localhost:8080/api/userProjects"
```

**Request Body Example**:

```
{
    "userEmail": "eve.ld@mail.com",
    "projectId": 3,
    "roles": ["Danseur", "Organizer"]
}
```

**If successful return** e.g: 
```
{
    "projectId": 3,
    "userId": 2,
    "roles": ["Danseur", "Organizer"]
}
```

**Possible Response Codes:**

- `201 Created`: User successfully added to the project with roles.
- `404 Not Found`: No user or project found with the given email or id.
- `400 Bad Request`: Invalid request body, email field does not contains an email format string, ...

### Get the project for which the user is an organizer

```
POST "http://localhost:8080/api/userProjects/organizer/{mail}"
```


**If successful return** e.g: 
```
[
    {
        "id": 20,
        "name": "Christmas show 2.0",
        "description": "Winter show with Santa and its elves",
        "beginningDate": "2020-07-01",
        "endingDate": "2020-12-26"
    }
]
```

**Possible Response Codes:**

- `20O OK`: Successfully retrieves all the project of the organizer.
- `404 Not Found`: No user found with the given email.

### Get the users participating in the project

```
POST "http://localhost:8080/api/userProjects/{id}"
```


**If successful return** e.g: 
```
[
    {
        "id": 18,
        "firstName": "Del",
        "lastName": "vr",
        "email":"del.vr@mail.com"
    },
    {
        "id": 19,
        "firstName": "Eve",
        "lastName": "ld",
        "email": "eve.ln@mail.com"
    }
]
```

**Possible Response Codes:**

- `20O OK`: Successfully retrieves all the user of the project with the given id.
- `404 Not Found`: No Project found with the given id.

### Get the user roles in the project

```
POST "http://localhost:8080/api/projects/{projectId}/users/{userId}/roles"
```


**If successful return** e.g: 
```
[
    "Organizer",
    "Danseur"
]
```

**Possible Response Codes:**

- `200 OK`: Successfully retrieves all roles of the user with the given id for the project with the given id.
- `404 Not Found`: No user or project found with the given id.

### Delete a user role in the project

```
DELETE "http://localhost:8080/api/projects/{projectId}/users/{userId}/roles/{role}"
```

**Possible Response Codes:**

- `204 No Content`: Successfully delete the user role in the project.
- `404 Not Found`: No user or project or role found with the given id.
- `400 Bad Request`: Can't delete the last user on the project.

### Add roles to a user participating in the project

```
POST "http://localhost:8080/api/projects/{projectId}/users/{userId}/roles"
```

**Request Body Example**:

```
{
    "roles": ["Danseur", "Organizer"]
}
```


**If successful return** e.g: 
```
{
    "projectId": 3,
    "userId": 2,
    "roles": ["Danseur", "Organizer"]
}
```

**Possible Response Codes:**

- `201 Created`: User roles successfully added to the project.
- `404 Not Found`: No Project or user found with the given id.

### Update the roles of a user participating in the project

```
PUT "http://localhost:8080/api/projects/{projectId}/users/{userId}/roles"
```

**Request Body Example**:

```
{
    "roles": ["Danseur", "Organizer"]
}
```


**If successful return** e.g: 
```
{
    "projectId": 3,
    "userId": 2,
    "roles": ["Danseur", "Organizer"]
}
```

**Possible Response Codes:**

- `201 Created`: User roles successfully added to the project.
- `404 Not Found`: No Project or user found with the given id.

### Remove a participant from a given project

```
DELETE "http://localhost:8080/api/projects/{projectId}/users/{userId}"
```

**Possible Response Codes:**

- `204 No Content`: Successfully delete the participant with the given id from the project with the given id.
- `404 Not Found`: No project or user found with the given id.

## Rehearsal

### Create a rehearsal

```
POST "http://localhost:8080/api/rehearsals"
```

**Request Body Example**:

```
{
    "name": "General rehearsal", 
    "description": "Last rehearsal with everyone",
    "date": "2025-07-01",
    "duration": "PT3H",
    "projectId": "2",
    "participantsIds": []
}
```

**If successful return newly created rehearsal** e.g: 
```
{
    "id" : 3,
    "name": "General rehearsal", 
    "description": "Last rehearsal with everyone",
    "date": "2025-07-01",
    "duration": "PT3H",
    "projectId": "2"
}
```

**Possible Response Codes:**

- `201 Created`: Rehearsal successfully created.
- `400 Bad Request`: Invalid date (has to be between the project dates).
- `404 Not Found`: No project or participant found with the given id.


### Get the rehearsals of a project

```
GET "http://localhost:8080/api/projects/{id}/rehearsals"
```

**If successful return** e.g: 
```
[
    {
        "id" : 3,
        "name": "General rehearsal", 
        "description": "Last rehearsal with everyone",
        "date": "2025-07-01",
        "duration": "PT3H",
        "projectId": "2",
        "participantsIds": []
    }
]
```

**Possible Response Codes:**

- `20O Ok`: Successfully retrieved the project rehearsals.
- `404 Not Found`: No project found with the given id.


### Delete a rehearsal

```
DELETE "http://localhost:8080/api/rehearsals/{id}"
```

**Possible Response Codes:**

- `204 No Content`: User delete successfully.


### Update a rehearsal

```
PUT "http://localhost:8080/api/rehearsals/{id}"
```

**Request Body Example**:

```
{
    "name": "General rehearsal", 
    "description": "Last rehearsal with everyone",
    "date": "2025-07-01",
    "duration": "PT3H",
    "projectId": 2,
    "participants": [18]
}
```

**If successful return newly created rehearsal** e.g: 
```
{
    "id" : 3,
    "name": "General rehearsal", 
    "description": "Last rehearsal with everyone",
    "date": "2025-07-01",
    "duration": "PT3H",
    "projectId": "2"
}
```

**Possible Response Codes:**

- `200 OK`: Rehearsal successfully updated.
- `404 Not Found`: No rehearsal found with the given id.

### Get the users info of the participants of a rehearsal

```
GET "http://localhost:8080/api/rehearsals/{id}/participants"
```

**If successful return list of users** e.g: 
```
[
    {
        id : 23,
        firstName: "Del", 
        lastName: "vr", 
        email: "del.vr@mail.com"
    },
    {
        id : 45,
        firstName: "Eve", 
        lastName: "pl", 
        email: "Eve.pl@mail.com"
    }
]
```

**Possible Response Codes:**

- `200 OK`: Rehearsal successfully updated.
- `404 Not Found`: No rehearsal found with the given id.

### Get a rehearsal based on its id

```
GET "http://localhost:8080/api/rehearsals/{id}"
```

**If successful return the rehearsal** e.g: 
```
{
    "id" : 3,
    "name": "General rehearsal", 
    "description": "Last rehearsal with everyone",
    "date": "2025-07-01",
    "duration": "PT3H",
    "projectId": "2"
}
```

**Possible Response Codes:**

- `200 OK`: Rehearsal successfully retrieved.
- `404 Not Found`: No rehearsal found with the given id.

### Get a rehearsal of a user on a project

```
GET "http://localhost:8080/api/users/{email}/projects/{projectId}/rehearsals"
```

**If successful return the rehearsals** e.g: 
```
[
    {
        "id" : 3,
        "name": "General rehearsal", 
        "description": "Last rehearsal with everyone",
        "date": "2025-07-01",
        "duration": "PT3H",
        "projectId": "2",
        "participantsIds": []
    }
]
```

**Possible Response Codes:**

- `200 OK`: Rehearsals successfully retrieved.
- `404 Not Found`: No user or project found with the given email or id.