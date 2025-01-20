# Authentication Service (auth-service)
This project is a microservice that supports new user creation through a signin endpoint and 
user authentication with a login endpoint. The signin enpoint provides a JWT token must be used in the user authentication with the login endpoint 
and finally the login endpoint response returns a new token for more secure authentication.
## Gradle Wrapper Configuration
We introduce gradle wrapper for build the components, the gradle version we are
targeting with this project is 7.4, then you must execute the following code 
to download the zip

`gradle wrapper --gradle-version 7.4 --distribution-type all`
The following is an example of the generated distribution URL in `gradle/wrapper/gradle-wrapper.properties`:
```
distributionUrl=https\://services.gradle.org/distributions/gradle-7.4-bin.zip
```

## Lombok Library and Dependencies
For Lombok library we introduce these dependencies

```
compileOnly 'org.projectlombok:lombok:1.18.36'
annotationProcessor 'org.projectlombok:lombok:1.18.36'

testCompileOnly 'org.projectlombok:lombok:1.18.36'
testAnnotationProcessor 'org.projectlombok:lombok:1.18.36'
```

## IntelliJ IDEA (Lombok)
The Jetbrains IntelliJ IDEA editor is compatible with lombok without a plugin as of version `2020.3` to version `2023.1`.

For versions prior to `2020.3` or later than `2023.1`, you can add the **Lombok IntelliJ plugin** to add lombok support for IntelliJ:

1. `Go to File > Settings > Plugins`
2. Click on `Browse repositories...`
3. Search for Lombok Plugin
4. Click on `Install plugin`
5. Restart IntelliJ IDEA

## Build Auth Microservice
we build the microservice using gradle wrapper. First time execution of the command download the effective wrapper.
```
$ ./gradlew build
```
After build the microservice, we can run it:
```
./gradlew bootRun
```
* **Note:** The application expose the endpoint en the port `8080` as: `http://localhost:8080/api/auth/signup` and `http://localhost:8080/api/auth/login`.

### Run Tests
```
./gradlew test
```

## Authentication Microservice Components
The Authentication microservice is driven by to actions: signUp and login.
![Components Diagram](/docs/img/auth-service-components.png)

The microservice contains the following components:
- `AuthController`: receives de actions of `singUp` and `login` through and RESTful API controller  and delegate to the services.
- `JwtAtheticationFilter`: Validate `email` and `token` before login action. This a filter configured to capture the user login and validate it.
- `AuthService`: Contains the business logic to run `signUp` and `login` process.
- `UserService`: Provides one method to load User By Email and other to save the user information in a database.
- `UserRepository`: This component interacts with the database to get ans save the user information.
- `AuthenticationProvider`: Validates if user exists based on his email and confirms credentials are valid.
- `JwtManager`: This Component has all the utilitis to generate a JWT token, validate and extract information from it.
- `AuthServiceUsernamePwdAuthenticationProvider`: This component authenticates the user by email and decode and compare the password send by the user login process.


## Authentication MicroService Sequences
### SignUp
![SignUp Sequence Diagram](/docs/img/auth-service-sequences-signup.png)
Sequence steps:
1. External system `Authentication System Frontend` call `/sigup` endpoint.
2. The controller `AuthController` delegates to `AuthService` service the signup process.
3. The service `AuthService` try to load the user by email to validate if the user exists and receive a `UserNotFoundException`
when the user does not exist.
4. After the user validation the `signUp` process generate a JWT Token specific for login after signup.
5. Returns a response with:
   - `id`: User UUID.
   - `created`: Creation date.
   - `lastLogin`: Last User login.
   - `jwt`: JWT Token for first login after signup.
   - `isActive`: User is active.

### Login
![Login Sequence Diagram](/docs/img/auth-service-sequences-login.png)
Sequence steps:
1. External system `Authentication System Frontend` call `/login` endpoint on `AuthController`.
2. The login process is intercepted by a filter before de User-Password_Authentication by the `JwtAuthenticationFilter` filter. This filter extract the `email`, try to load the user by email
if the user exists then validate the token and return the control to the login process.
3. Executing the `login` process on `AuthService`, it calls to `authenticate` method on `AuthServiceUsernamePwdAuthenticationProvider` and return a valid authentication, otherwise an exception with bad request as status. 
4. Load User by email and get the user.
5. Set Last Login date fot the user.
6. Generate JWT Token for user access.
7. Returning a response with:
   - `id`: User UUID.
   - `created`: Creation date.
   - `lastLogin`: Last User login.
   - `token`: JWT Token for first login after signup.
   - `isActive`: User is active.
   - `name`: User name.
   - `email`: User main email.
   - `password`: Encoded password
   - `pones`: List of User phones.