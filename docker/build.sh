#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
ENV_FILE="${SCRIPT_DIR}/.env"
ENV_EXAMPLE_FILE="${SCRIPT_DIR}/.env.example"

if [[ -f "${ENV_FILE}" ]]; then
  ENV_SOURCE_FILE="${ENV_FILE}"
elif [[ -f "${ENV_EXAMPLE_FILE}" ]]; then
  ENV_SOURCE_FILE="${ENV_EXAMPLE_FILE}"
  echo "docker/.env not found. Using docker/.env.example."
else
  echo "Neither docker/.env nor docker/.env.example was found."
  exit 1
fi

set -a
source "${ENV_SOURCE_FILE}"
set +a

if [[ -z "${APP_IMAGE:-}" ]]; then
  echo "APP_IMAGE is not set in ${ENV_SOURCE_FILE}."
  exit 1
fi

if [[ "$(uname -s)" == "Darwin" ]] && [[ -x "/usr/libexec/java_home" ]]; then
  JAVA_HOME="$("/usr/libexec/java_home" -v 25)"
  export JAVA_HOME
  export PATH="${JAVA_HOME}/bin:${PATH}"
fi

echo "Building application jar"
(
  cd "${REPO_ROOT}"
  ./gradlew --no-daemon clean bootJar -x test
)

echo "Building ${APP_IMAGE} from ${REPO_ROOT}/docker/Dockerfile"
docker build -f "${REPO_ROOT}/docker/Dockerfile" -t "${APP_IMAGE}" "${REPO_ROOT}"

echo "Pushing ${APP_IMAGE} to Docker Hub"
docker push "${APP_IMAGE}"

echo "Done: ${APP_IMAGE}"
