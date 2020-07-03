#!/bin/bash

dt=$(date +'%Y-%m-%d-%H-%M')

cd /home/brett/Documents/Java/TotalCrafter/builds/Builder
mkdir build-$dt
cd build-$dt
cp -R /home/brett/Documents/Java/TotalCrafter/src src/

if [ -n "$1" ]; then
	jar cf $1 src
else
	jar cf $dt.jar src
fi
