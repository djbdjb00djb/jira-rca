#!/bin/bash

echo "Create the configuration file for testing purposes. File: src/test/resources/config.xml"
read -p "JIRA Server URL: " SERVER
read -p "JIRA Username: " USER
stty -echo
read -p "JIRA Password: " PASS; echo
stty echo

SCRIPT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CONFIG_FILE="$SCRIPT_DIR/src/test/resources/config.xml"

echo '<?xml version="1.0" encoding="utf-8"?>' > "$CONFIG_FILE"
echo '<config trustall="true">' >> "$CONFIG_FILE"
echo "    <server>$SERVER</server>" >> "$CONFIG_FILE"
echo "    <username>$USER</username>" >> "$CONFIG_FILE"
echo "    <password>$PASS</password>" >> "$CONFIG_FILE"
echo '</config>' >> "$CONFIG_FILE"