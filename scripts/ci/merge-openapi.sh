#!/bin/bash

echo "Current Directory: $(pwd)"

swagger-cli bundle -o build/resources/main/static/openapi-bundled.yaml src/main/resources/static/openapi.yaml

if [ -f build/resources/main/static/openapi-bundled.yaml ]; then
    echo "openapi-bundled.yaml SUCCESS"
else
    echo "openapi-bundled.yaml FAILURE"
fi
