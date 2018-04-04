# Overview

This project was made for EPITA front-end association.
It is a simple GraphQL backend server who proxies the BetaSeries
API in order to query this API with the GraphQL language.

It is not mandatory to clone and run this project ! You can do it if you wish so, however accessing the backend via
http://swirl.deadlykungfu.ninja:9000/ should be enough.

You will need a BetaSeries API Key if you want to run this server locally. (It is free but you will need some hours before validation)

On some technical side it is built in Scala using Play! Framework and Sangria for the GraphQL part.
If you found some bug(s) using it you can contact me directly, open an issue or submit a PR.

PS: Don't expect to see some explanations on this repo, the subject should be enough to understand what needs to be done
however if you are curious about the server and its implementation I would be happy to talk with a beer, and if there
is too much beer-requests I might write a paper.

# Usage
You must get the schema from the server by doing a GET request to this URL :
````
GET  http://swirl.deadlykungfu.ninja:9000/schema
````

You can then forward all your GraphQL requests to this route :
```
POST http://swirl.deadlykungfu.ninja:9000/graphql
```

You MUST pass your query in the body, here is an example query fetching the
movie 'Intouchables' with some metas:
```
query {
  movie(id: 135){
    director
    backdrop
    title
    comments {
     id
     login
     avatar
     text
    }
  }
}
```

If there is a syntaxic error with your query it should give you the line number
and the column number that caused the error

# Building
This project is using Scala Build Tools, for first run after installing
sbt by yourself you can launch this command to start te server
```
sbt clean compile run
```

## TODO
- OK = Define the Sangria Schema
- Decent = Cache requests if possible
- Install GraphiQl to provide a simple space for testing and abusing queries