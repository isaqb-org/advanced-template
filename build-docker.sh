#!/bin/sh
# Thin launcher; real logic lives in the gradle-tools submodule (gradle-tools/docker/).
exec "$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)/gradle-tools/docker/build-docker.sh" "$@"
