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

## 1. Create a User

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

**Possible Response Codes:**

- `201 Created`: User successfully created.
- `409 Conflict`: Email already exists.
- `400 Bad Request`: Invalid request body, email field does not contains an email format string, ... .
