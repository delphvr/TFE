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

**If successful return boolean value** e.g: 
```
true
```

**Possible Response Codes:**

- `200 OK`: Value successfully retreived.
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

- `200 OK`: Value successfully retreived.
- `204 No Content`: No professions present in the database.

## Project

### Create a project

```
POST "http://localhost:8080/api/projects"
```

**Request Body Example**:

```
{
    name: "Christmas show",
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
- `400 Bad Request`: Invalid request body, invalid email, ending date befor begining date, ... .

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

- `200 OK`: Successfully retreive the list of projects of that user.
- `404 Not Found`: No user found with the given email.
