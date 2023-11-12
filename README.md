# turbo-octo-telegram
Experimenting Microservices using Spring Boot, Kafka, and Postgres

> It's expected you have docker installed.

## How to run

After clone the repository... and in the terminal.

1. `cd infra`
2. `docker compose down && docker compose up -d && docker compose logs -f`
3. Open another terminal window in the project root (previous is showing logs)
4. `cd wakanda-space-agency-service`
5. `mvn spring-boot:run` (I hope you have `maven` installed)

## How to interact

> I need to develop the `wakanda-weather-station-service`, which is (will be) responsible for managing the satellites, receiving (async) weather data readings and make the forecast.

... until there ...

You have to use a tool like `postman`, `curl`, or a `browser` to perform calls to [localhost:8081/satellite]().
I have used a VS Code extension called `REST Client` and the commands found in [satellite_example_call.rest]() are ready to be used with it.

## Compose bundle

In the compose you will find the kafka (and its partner zookeeper), postgreSQL, pgAdmin (to check the database), and kafkaDrop (a kafka's client).

> This system is so secure that the data are volatile. I'm kidding. There's no persistent data because this is a development environment.