#!/bin/bash

docker-compose -f docker-compose-build.yaml down
docker-compose -f docker-compose-build.yaml build
docker-compose -f docker-compose-build.yaml up -d
