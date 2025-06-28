#!/bin/bash

echo "COMPOSE_PROJECT_NAME=message-system-message" > .env
echo "HOST_IP=$(route get default | grep 'interface' | awk '{print $2}' | xargs ipconfig getifaddr)" >> .env
