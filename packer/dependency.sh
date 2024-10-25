#!/bin/bash

# update packages
echo "update packages..."
sudo apt-get update -y

# install JDK 17
echo "install JDK 17..."
sudo apt-get install openjdk-17-jdk -y

# install Maven
echo "install Maven..."
sudo apt-get install maven -y



# show information
echo "installation completed"
