#!/bin/bash

base_dir="/home/brett/Documents/Java/TotalCrafter"

cp -R $base_dir/lib/used/ lib/
cd lib

for filename in ./*.jar; do
	echo "Extracting $filename"
	jar -v0xf $filename
	echo "Extracting complete!"
done
rm -fr *.jar
