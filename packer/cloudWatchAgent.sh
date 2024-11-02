#!/bin/bash

#!/bin/bash

echo "Starting CloudWatch Agent installation..."

# Set the working directory to /tmp
cd /tmp

# Download CloudWatch Agent
echo "Downloading Amazon CloudWatch Agent..."
wget https://amazoncloudwatch-agent.s3.amazonaws.com/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb -O amazon-cloudwatch-agent.deb

# Install CloudWatch Agent
echo "Installing Amazon CloudWatch Agent..."
sudo dpkg -i amazon-cloudwatch-agent.deb

# Start CloudWatch Agent with the configuration
echo "Starting Amazon CloudWatch Agent..."
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/cloudwatch-config.json -s

# Enable the CloudWatch Agent to start on boot
sudo systemctl enable amazon-cloudwatch-agent

echo "Amazon CloudWatch Agent installation and configuration completed."

	
