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
    professions: ["Danseur"], 
    isOrganizer: true
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

### Delete User based on its id

```
DELETE "/api/users/{id}"
```

**Possible Response Codes:**

- `204 No Content`: User delete successfully.
- `404 Not Found`: No user found with that id.

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

### Add role to a user participating in the project

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
    name: "General rehearsal", 
    description: "Last rehearsal with everyone",
    "date": "2025-07-01",
    "duration": "'PT3H'",
    "projectId": "2"
}
```

**If successful return newly created project** e.g: 
```
{
    id : 3,
    name: "General rehearsal", 
    description: "Last rehearsal with everyone",
    "date": "2025-07-01",
    "duration": "'PT3H'",
    "projectId": "2"
}
```

**Possible Response Codes:**

- `201 Created`: Rehearsal successfully created.
- `400 Bad Request`: Invalid date (has to be between the project dates).
- `404 Not Found`: No project found with the given id.