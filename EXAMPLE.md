# Example of flint server with pseudo-GDPR

**flint version: 0.1.0.0**

**eflint file loaded: [a relative link](src/main/resources/pseudo-gdpr-1.eflint)**


## Create an instance
Request
```http request
GET: http://127.0.0.1:4567/create
```

Response
```json
{
    "status": "SUCCESS",
    "message": "d6ad1284-aea5-4206-9d58-f0a5633ded7a"
}
```

## Check if the server is running
Request
```json
{
    "uuid": "d6ad1284-aea5-4206-9d58-f0a5633ded7a",
    "request-type": "command",
    "data": {
        "command": "status"
    }
}
```

Response
```json
{
    "status": "SUCCESS",
    "data": {
        "request": {
            "command": "status"
        },
        "response": {
            "violations": [],
            "response": "success",
            "new-state": 2
        }
    }
}
```

## Do act
to perform the act of 
```asp
Act store data
  Actor controller
  Recipient subject
  Related to
    personal data
  When personal data.person == subject.person
  Creates
    stored data()
```

Request
```json
{
    "uuid": "d6ad1284-aea5-4206-9d58-f0a5633ded7a",
    "request-type": "command",
    "data": {
        "command": "action",
        "act-type": "store data",
        "actor": {
            "fact-type": "controller",
            "value": "Telfort"
        },
        "recipient": {
            "fact-type": "subject",
            "value": [
                {
                    "fact-type": "person",
                    "value": "Alice"
                }
            ]
        },
        "objects": [
            {
                "fact-type": "personal data",
                "value": [
                    {
                        "fact-type": "person",
                        "value": "Alice"
                    },
                    {
                        "fact-type": "datatype",
                        "value": "Address"
                    }
                ]
            }
        ]
    }
}
```

this is the equivalent of 

```json
{
    "uuid": "d6ad1284-aea5-4206-9d58-f0a5633ded7a",
    "request-type": "command",
    "data": {
        "command": "phrase",
        "text" : "store data (controller(Telfort),subject(person(Alice)),personal data(person(Alice),datatype(Address)))"
    }
}
``` 

Response 

```json
{
    "status": "SUCCESS",
    "data": {
        "request": {
            ...
        },
        "response": {
            "violations": [],
            "response": "success",
            "new-state": 3
        }
    }
}
```

## Check if command is successful 
Check is done by the presence of `stored data()` fact

Request
```json
{
    "uuid": "d6ad1284-aea5-4206-9d58-f0a5633ded7a",
    "request-type": "command",
    "data": {
        "command": "test-present",
        "value": {
            "fact-type": "stored data",
            "value": [
                {
                    "fact-type": "controller",
                    "value": "Telfort"
                },
                {
                    "fact-type": "personal data",
                    "value": [
                        {
                            "fact-type": "person",
                            "value": "Alice"
                        },
                        {
                            "fact-type": "datatype",
                            "value": "Address"
                        }
                    ]
                }
            ]
        }
    }
}
```
this is the equivalent of 

```json
{
    "uuid": "d6ad1284-aea5-4206-9d58-f0a5633ded7a",
    "request-type": "command",
    "data": {
        "command": "phrase",
        "text" : "?stored data (controller(Telfort),personal data(person(Alice),datatype(Address)))"
    }
}
```


Response if success

```json
{
    "status": "SUCCESS",
    "data": {
        "request": {
            ...
        },
        "response": {
            "violations": [],
            "response": "success",
            "new-state": 4
        }
    }
}
```

## Roll back

revert to initial state:

Request
```json
{
    "uuid": "d6ad1284-aea5-4206-9d58-f0a5633ded7a",
    "request-type": "command",
    "data": {
        "command": "revert",
        "value" : -1
    }
}
```
Response 

```json
{
    "status": "SUCCESS",
    "data": {
        "request": {
           ...
        },
        "response": {
            "violations": [],
            "response": "success",
            "new-state": 0
        }
    }
}
```

## Redo the query

Request
```json
{
    "uuid": "d6ad1284-aea5-4206-9d58-f0a5633ded7a",
    "request-type": "command",
    "data": {
        "command": "phrase",
        "text" : "?stored data (controller(Telfort),personal data(person(Alice),datatype(Address)))"
    }
}
```

Response
```json
{
    "status": "SUCCESS",
    "data": {
        "request": {
            ...
        },
        "response": {
            "response": "query failed"
        }
    }
}
```