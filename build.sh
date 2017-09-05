#!/bin/bash
#
#
set -e
set -u
# Find the compiler
JAVA_HOME=$(dirname $(rpm -ql java-1.7.0-openjdk-devel | grep LICENSE))
export JAVA_HOME

echo "Using ${JAVA_HOME}"
ant