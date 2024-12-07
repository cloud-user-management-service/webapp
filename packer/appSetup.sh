#!/bin/bash

set -e

sudo cp /tmp/webapp.jar /opt/myapp/webapp.jar
# sudo cp /tmp/app /opt/myapp/app
sudo cp /tmp/app.service /etc/systemd/system/csye6225.service
sudo cp /tmp/cloundWatchAgentConfig.json /opt/cloudwatch-config.json

# sudo cp /tmp/app.properties /opt/myapp/app.properties

sudo systemctl daemon-reload
sudo systemctl enable csye6225

sudo chown -R csye6225:csye6225 /opt/myapp
sudo chown -R csye6225:csye6225 /var/log/statsd


#AWS_CA_BUNDLE=/User/tejas.parikh/Cloudflare_CA.pem AWS_PROFILE=neu packer build aws.par.hcl 