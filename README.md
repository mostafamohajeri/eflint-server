## To run

go to `web-server` directory


```
$ cd [project-home]/web-server
```
### Compile

`mvn compile`

### Run
`mvn exec:java -Dexec.mainClass="eflint.Main"`

## Temporary settings

(ALL JUST FOR NOW)

`src/main/java/eflint/InstanceManager.java` :

```java
    // limit of instances
    private int limit = 3;
    // eflint-server executable address
    private static final String EFLINT_COMMAND = "eflint-server"; // if on path
    // private static final String EFLINT_COMMAND = "/path/to/eflint-server"; // if not on path (recommended)
```

## API Endpoints

### create

endpoint: POST `/create`


description: creates an eflint instance and returns a unique uuid to it.



request:
```json
{
	"template-name": "/absolute/path/to/pseudo-pesudo-gdpr.eflint",
	"values" : {
		"purposes": "Revenue + Research",
		"datatypes": "Address + Phone"
	}
}
```

the ```values``` can be empty ```values: {}```  or omitted if the template file is a pure `eflint` file (not a `st4` template)

response: 
```json
{
    "status": "SUCCESS",
    "message": "f378eafa-1c61-4bc4-a8de-5ea2b144e964"
}
```
there is a limit to how many instances can be on the server and if exceeded the response will be 
```json
{
    "status": "ERROR",
    "message": "limit of 3 instances reached"
}
```


### kill
endpoint: POST `/kill`

kills an instance of eflint provided the uuid

request :
```json
{
	"uuid": "b2c911f8-0fc5-40e8-8daa-7d49f4901b14"
}
```

response : 
```json
{
    "status": "SUCCESS",
    "message": "flint exited nicely :)"
}
```

### kill_all

endpoint: POST `/kill_all`

kills all instances

request: none

response: 
```json
{
    "status": "SUCCESS"
}
```


### get_all

GET `/get_all`

get a list of available efint instances as uuids

request: none

response: 
```json
{
    "status": "SUCCESS",
    "data": [
        "8de2a283-e095-4c01-bae5-776ca46975ad",
        "9bc120e2-2d69-4171-9dfc-ddb383dab526"
    ]
}
```

### command

POST `/command`

sends an **EFLINT** command to an instance of eflint and gets the response

example request: 
```json
{
	"uuid": "9b34343f-654a-454e-9a7d-2155f62586f3",
	"request-type": "command",
	"data": 
	{
		"command": "revert",
		"value": -1
	}
}
```
`data` is the main eflint request as described in its documentations

`uuid` is the instance intended for the request

example response:
```json
{
    "status": "SUCCESS",
    "data": {
        "request": {
            "command": "revert",
            "value": -1
        },
        "response": {
            "violations": [],
            "response": "success",
            "new-state": 0
        }
    }
}
```