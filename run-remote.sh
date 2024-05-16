#!/bin/bash

docker-compose -f docker-compose-remote.yaml down
docker-compose -f docker-compose-remote.yaml up -d
