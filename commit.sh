#!/bin/bash

cd /home/brett/Documents/Java/RMS
git add *
if [ -n "$1" ]; then
	git commit -m $1
else
	git commit -m "automated commit $((9956 + RANDOM % 100000))"
fi
git push -u origin master
