# API

## CHECKIN

`https://<service fqdn>/api/checkin`

## CHECKOUT

`https://<service fqdn>/api/checkout`

### JSON POST Request

Sample
```
{
  "authToken" : "<your api auth token>",
  "pin" : "<room pin>",
  "location" : "<location>",
  "phone" : "<user phonenumber>",
  "username" : "<user name>",
  "password" : "<user password>"
}
```

### JSON Response 

Sample
```
{
  "code": 4,
  "message": "Dieser Account ist für 180s gesperrt!"
}
```

### Error Codes
```
  OK = 0
  ERROR = 1
  INVALID_CREDENTIALS = 2
  UNKNOWN_ROOM = 3
  ACCOUNT_BLOCKED = 4
  PHONENUMBER_IS_MISSING = 5
```

## BUILD Information

`https://<service fqdn>/api/build`

Sample
```
{
  javaVersion: "11.0.7",
  javaVendor: "BellSoft",
  projectName: "sonia-webapp-qr-travel",
  projectVersion: "1.0.0-SNAPSHOT",
  timestamp: "2020-07-13 10:25:51"
}
```
