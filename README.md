# Spring Boot Actuator Dashboard


This dashboard unlocks [extended Cloud Foundry actuator support](http://docs.spring.io/spring-boot/docs/1.5.6.RELEASE/reference/html/production-ready-cloudfoundry.html) to non cloudfoundry apps.

Pseudo Cloud Controller and UAA are implemented to access `/cloudfoundryapplication` endpoints.

* No Authentication/Authorization are implemented yet.

## Register a target app

Dashboard runs on `9933` port by default.

```
$ curl http://localhost:9933/api/applications -H "Content-Type: application/json" -d '{"applicationName":"My Application", "url":"http://localhost:8080"}'
{"applicationId":"138159a7-877b-4837-96eb-4502bd70dee8","applicationName":"My Application","url":"http://localhost:8080","readSensitiveData":true}
```

## Issue an access token

```
$ curl -XPOST http://localhost:9933/api/applications/138159a7-877b-4837-96eb-4502bd70dee8/token
{"applicationId":"138159a7-877b-4837-96eb-4502bd70dee8","token":"eyJraWQiOiJpbml0IiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZSI6WyJhY3R1YXRvci5yZWFkIl0sImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo5OTMzXC9vYXV0aFwvdG9rZW4iLCJleHAiOjE1MDI3MTc2NTgsImlhdCI6MTUwMjYzMTI1OH0.d3wccwXFKqd8-3-QyQxrJLSlUQKX2XDK2YJsTNy0GYjINk4bFPENTJaK-j8NtsKE5xWBv9SJXV37k7ucgetAZdNVqj43EHxKZsW7OTw-JBilw8Pu03BnSyH0Z_gQ1eNRh44h7HvDhCgPxW_dO27IjDa4AFMoUwIDBNBIOKlGBVqMD7voiLzccKBdk4LZYS7IEjz8MspUmRWLCAVfYtB3HvBp1rnP7vTB-XKHp0IW-mVJ0geulKp-phZr9PDKJJOOM2lmjhBGc3FYJ1FMq1bT3Q044q-6fAxnxr4pxroc_QHqwsQLwW36Wrz2HII-43rWe387E0bIkMgQf5d4TglFRA"}
```

## Configuration in the target app

`application.properties`

``` properties
VCAP_APPLICATION={}
VCAP_SERVICES={}
vcap.application.application_id=138159a7-877b-4837-96eb-4502bd70dee8
vcap.application.cf_api=http://localhost:9933
# management.cloudfoundry.skip-ssl-validation=true
```

then, you can access `/cloudfoundryapplication` endpoints with the access token above.


```
$ token=...
$ curl -H "Authorization: bearer $token"  http://localhost:8080/cloudfoundryapplication/health
{"status":"UP","diskSpace":{"status":"UP","total":498937626624,"free":71285088256,"threshold":10485760},"db":{"status":"UP","database":"MySQL","hello":1}}
```

## Proxy access to `/cloudfoundryapplication` endpoints

```
$ curl http://localhost:9933/proxy/138159a7-877b-4837-96eb-4502bd70dee8/health
{"status":"UP","diskSpace":{"status":"UP","total":498937626624,"free":70210977792,"threshold":10485760},"db":{"status":"UP","database":"MySQL","hello":1}}
```

## Dashboard UI

You can also register apps and access actuator endpoints from UI.

![image](https://user-images.githubusercontent.com/106908/29252328-c2e033de-809f-11e7-9a92-cf9ad261b19b.png)


![image](https://user-images.githubusercontent.com/106908/29252330-d143447a-809f-11e7-8b65-3e2faf55507f.png)

----
_Licensed under [Apache Software License 2.0](www.apache.org/licenses/LICENSE-2.0)_
