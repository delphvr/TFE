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

## Create a User

```
POST "http://localhost:8080/api/users"
```

**Request Body Example**:

```
{
    firstName: Del, 
    lastName: vr, 
    email: del.vr@mail.com,
    professions: [Danseur], 
    isOrganizer: true
}
```

**If successful return newly created user** e.g: 
```
{
    id : 23,
    firstName: Del, 
    lastName: vr, 
    email: del.vr@mail.com,
}
```

**Possible Response Codes:**

- `201 Created`: User successfully created.
- `409 Conflict`: Email already exists.
- `400 Bad Request`: Invalid request body, email field does not contains an email format string, ... .

## Get is User an Organizer

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