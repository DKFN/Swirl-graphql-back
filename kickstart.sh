## Basic dependencies
sudo apt update
sudo apt upgrade
sudo apt install git default-jdk default-jre unzip

## SBT INSTALL

echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt

## CLONE

git clone https://github.com/DKFN/Swirl-graphql-back.git
./prodKick.sh
