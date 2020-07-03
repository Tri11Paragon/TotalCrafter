#!/bin/bash

cd /home/brett/Documents/Java/TotalCrafter
git add *
if [ -z "$@" ]; then
	git commit -m $@
else
	git commit -m "automated commit $((9956 + RANDOM % 100000))"
fi
git push -u origin master
