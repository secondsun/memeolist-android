#!/bin/bash
mkdir -p ./app/src/main/graphql/org/aerogear/apps/memeolist
apollo-codegen download-schema http://localhost:8000/graphql --output ./app/src/main/graphql/org/aerogear/apps/memeolist/schema.json

