#!/bin/bash

IP_ADDRESS=localhost
echo "Register service '${SERVICE_NAME}' - ${IP_ADDRESS}:${SERVICE_PORT}"

curl -X PUT http://consul1:8500/v1/agent/service/register \
  -d "{
    \"ID\": \"${SERVICE_NAME}-${IP_ADDRESS}-${SERVICE_PORT}\",
    \"Name\": \"${SERVICE_NAME}\",
    \"Address\": \"${IP_ADDRESS}\",
    \"Port\": ${SERVICE_PORT},
    \"Check\": {
      \"TCP\": \"${INTERNAL_SERVICE_NAME}:80\",
      \"Interval\": \"10s\",
      \"Timeout\": \"3s\"
    }
  }"

nginx -g "daemon off;"
