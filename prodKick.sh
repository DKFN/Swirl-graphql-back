delOld="rm -rf ./Swirl-graphql-back/swirl-graphql-backend-0.1/"
playRun="./swirl-graphql-backend-0.1/bin/swirl-graphql-backend -Dplay.http.secret.key=SwirlIsMagic"

git pull --rebase
sbt clean compile dist
$delOld
unzip ./target/universal/swirl-graphql-backend-0.1.zip

#while true; do $dellOld; $playRun; done
$playRun & disown
