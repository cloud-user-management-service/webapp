#!/bin/bash

#dependency.sh
# # check and uninstall JDK
# if type java >/dev/null 2>&1; then
#     echo "JDK installed and is uninstalling"
#     sudo apt-get remove --purge openjdk* -y
#     sudo apt-get autoremove -y
# else
#     echo "JDK is not installed"
# fi

# # check and uninstalling Maven
# if type mvn >/dev/null 2>&1; then
#     echo "Maven is installed and is uninstalling..."
#     sudo apt-get remove --purge maven -y
#     sudo apt-get autoremove -y
# else
#     echo "Maven is uninstalled"
# fi

# # check and uninstall MySQL
# if type mysql >/dev/null 2>&1; then
#     echo "MySQL is installed and is uninstalling..."
#     sudo apt-get remove --purge mysql-server mysql-client mysql-common mysql-server-core-* mysql-client-core-* -y
#     sudo apt-get autoremove -y
# else
#     echo "MySQL is not installed"
# fi

# update packages
echo "update packages..."
sudo apt-get update -y

# install JDK 17
echo "install JDK 17..."
sudo apt-get install openjdk-17-jdk -y

# install Maven
echo "install Maven..."
sudo apt-get install maven -y

# install MySQL
sudo apt-get update
sudo apt-get install mysql-server -y

# Start MySQL service
sudo systemctl start mysql
sleep 5

# set MySQL root username and psaaword
echo "setting MySQL..."
sudo mysql <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '$MYSQL_ROOT_PASSWORD';
FLUSH PRIVILEGES;
CREATE DATABASE $DB_NAME;
EOF

# # set environment variables
# export DB_URL=$DB_URL
# export DB_USERNAME=$DB_USERNAME
# export DB_PASSWORD=$MYSQL_ROOT_PASSWORD


# Create .env file
echo "Creating .env file..."
echo "DB_URL=jdbc:mysql://localhost:3306/$DB_NAME?createDatabaseIfNotExist=true" | sudo tee /opt/myapp/.env
echo "DB_USERNAME=$DB_USERNAME" | sudo tee -a /opt/myapp/.env
echo "DB_PASSWORD=$DB_PASSWORD" | sudo tee -a /opt/myapp/.env

# # start MySQL 
# echo "start MySQL ..."
# sudo service mysql start

# show information
echo "installation completed"
# echo "installed JDK, Maven and MySQL."
# echo "created database: $DB_NAME"
