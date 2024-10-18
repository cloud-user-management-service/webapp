#!/bin/bash

#appDirSetup.sh
set -e
DIR="/opt/myapp"

sudo mkdir -p "${DIR}"

sudo useradd --system -s /usr/sbin/nologin csye6225
# ??
# sudo chown -R csye6225:csye6225 "${DIR}"