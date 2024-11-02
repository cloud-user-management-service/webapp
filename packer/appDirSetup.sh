#!/bin/bash

set -e
DIR="/opt/myapp"
LOG_DIR="/var/log/statsd"

sudo mkdir -p "${DIR}"

sudo mkdir -p "${LOG_DIR}"

sudo useradd --system -s /usr/sbin/nologin csye6225
# ??
