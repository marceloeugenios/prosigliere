#!/usr/bin/env bash

# Exit immediately if a any command exits with a non-zero status
set -e

# Set variables
REPOSITORY=$1

echo "Get Last Commit '$REPOSITORY'"
curl https://api.bitbucket.org/2.0/repositories/$BITBUCKET_REPO_OWNER/$REPOSITORY/commits/?pagelen=1 -u $ATLASSIAN_ACCOUNT_EMAIL:$ATLASSIAN_API_TOKEN -o arq1.hash
sed -i 's/.\{40\}//' arq1.hash
cut -c -40 arq1.hash > arq2.hash
LAST_COMMIT_HASH=$(cat arq2.hash)
rm -f arq*.hash
echo "Last Commit: " $LAST_COMMIT_HASH

# Create new tag
echo "Creating Tag"
curl -X POST https://api.bitbucket.org/2.0/repositories/$BITBUCKET_REPO_OWNER/$REPOSITORY/refs/tags \
  -u $ATLASSIAN_ACCOUNT_EMAIL:$ATLASSIAN_API_TOKEN \
  --fail --show-error --silent \
  -H 'Content-Type: application/json' \
  -d '{
      "name": "release-$REPOSITORY-'$BITBUCKET_BUILD_NUMBER'",
        "target": {
                "hash": "'$LAST_COMMIT_HASH'"
      }
  }'
echo "fim"