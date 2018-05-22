# Ontario Legislative Assembly
[ ![Codeship Status for cannawen/ola](https://app.codeship.com/projects/c14fd800-3cb5-0136-409c-3ac0f0073134/status?branch=master)](https://app.codeship.com/projects/290578)

## Goal
To improve transparency of Ontario's governance, and enable citizens to stay informed

## About
Parses documents from the [Ontario Legislative Assembly website](http://www.ontla.on.ca/pas/house-proceedings/house-documents.xhtml?locale=en) and uses the data to do searches and analytics. Data is saved in json and raw html format in [ola-data](https://github.com/cannawen/ola-data)

[Live demo](https://ola-hansard.cfapps.io) (CI build)

## Development
Developed using Clojure and ClojureScript. Requires Java 8+

### Getting Started
- type `lein repl` into terminal
- type `(-main)` into repl to start the server
- go to `localhost:8080` to view the web site

### Deploying
- type `lein uberjar` into terminal to get an executable `.jar` file
- deploy `.jar` file to server of your choice (currently using Pivotal Cloud Foundry)
- add server environment variables:
  - `ENVIRONMENT=prod`
  - `HTTP_PORT=8080` (or whatever port you want to run on)

## Contributing
All contributions welcome! Feel free to open an issue (or pull request) for bugs, feature requests, ideas, or questions :)
