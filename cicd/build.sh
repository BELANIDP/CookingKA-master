#!/bin/bash -x

if [ -z ${WORKSPACE} ]; then
	echo "WORSPACE is not set. setting to current working directory"
	WORKSPACE=$(pwd)
else
	echo "WORKSPACE is set" 
fi
echo "WORKSPACE is ${WORKSPACE}"

