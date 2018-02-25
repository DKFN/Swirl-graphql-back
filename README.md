# Overview

This project was made for EPITA front-end association.
It is a simple GraphQL (Well.. at least in later revisions it will be) backend server who proxies the BetaSeries
API in order to query this API with the GraphQL language.

It is not mandatory to clone and run this project ! You can do it if you wish so, however accessing the backend via
http://swirl.deadlykungfu.ninja:9000/ should be enough.

You will need a BetaSeries API Key if you want to run this server locally. (It is free but you will need some hours before validation)

On some technical side it is built in Scala using Play! Framework and Sangria for the GraphQL part.
If you found some bug(s) using it you can contact me directly, open an issue or submit a PR.

PS: Don't expect to see some explanations on this repo, the subject should be enough to understand what needs to be done
however if you are curious about the server and its implementation I would be happy to talk with a beer, and if there
is too much beer-requests I might write a paper.

## TODO
- Define the Sangria Schema
- Cache requests if possible
- Install GraphiQl to provide a simple space for testing and abusing queries