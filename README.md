# OAuth2 Resource Server
'spring-security-oauth2' 라이브러리를 사용하는 간단한 OAuth2 리소스 서버입니다.

Overview
-----------
`authorization-server` 모듈은 인증 서버입니다.
클라이언트가 인증 서버에 로그인을 하고 토큰을 발급받아 리소스 서버에 요청을 보낼 수 있습니다.
토큰 발급과 인증을 위해 간단하게 구현되어 있습니다.

- GET /.well-known/jwks.json 토큰 서명에 사용 된 공개키 목록 조회
- POST /token 토큰 발급 요청 및 응답

`resource-server` 모듈은 보호된 자원을 제공하는 Stateless Api Server 입니다.

클라이언트가 요청을 보낼 때마다 토큰을 함께 보내야 합니다. `Json Web Tokens(JWT)`을 사용하여 토큰을 발급받고 검증합니다.
토큰 헤더에는 `kid`가 포함되어 있습니다. 이 `kid`를 사용하여 인증 서버의 공개키를 가져와 토큰을 검증합니다.

Getting Started
-----------

1. 서브 프로젝트를 실행합니다.

* 인증 서버를 실행합니다.

    ```bash
    $ ./gradlew authorization-server:clean bootRun 
    ```

* 리소스 서버를 실행합니다.

    ```bash
    $ ./gradlew resource-server:clean bootRun
    ```

서버가 전부 기동 된 후 루트 패키지에 있는 authorize.http 파일을 통해 테스트할 수 있습니다.

Usage
-----------

1. 토큰 발급

```http request
POST http://localhost:9090/token
```

2. 리소스 요청

```http request
GET http://localhost:8090/resource
Authorization: Bearer {access_token}
Content-Type: application/json
```
