### Build
gradle build

### Тестовый curl
curl --request GET \
--url http://localhost:8080/v1/throttling \
--header 'X-Forwarded-For: 10.0.0.1' \

### Docker
docker build -t myorg/myapp .  
docker run -p 8080:8080 myorg/myapp  

