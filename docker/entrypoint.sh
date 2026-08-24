#!/bin/sh
set -eu

# Fargate bind volumes are initially owned by root. Make only the ephemeral
# temp volume writable, then drop privileges before starting the JVM.
chmod 1777 /tmp

exec setpriv \
  --reuid=appuser \
  --regid=appgroup \
  --init-groups \
  java \
  -XX:+ExitOnOutOfMemoryError \
  -XX:+AlwaysActAsServerClassMachine \
  -javaagent:agent.jar \
  -jar app.jar
