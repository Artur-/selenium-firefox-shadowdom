To test

1. Start a hub with Firefox on localhost:4444 by running
```
docker run -d -p 4444:4444 -v /dev/shm:/dev/shm selenium/standalone-firefox:3.14.0-krypton
```

2. Run `mvn clean verify`
