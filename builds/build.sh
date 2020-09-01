#!/bin/bash

base_dir="/home/brett/Documents/Java/TotalCrafter"
build_dir="$base_dir/builds"
lib_dir="$base_dir/lib/used"
resource_dir="$base_dir/resources"
main_class="com.brett.Main"

build_name=""

if [ -n "$1" ]; then
	build_name="$1"
else
	echo "Enter the name of the build: "

	#make sure you have: in your bashRC!
	#shopt -s extglob

	read build_name
fi

function buildjar {
	cd cjava
	echo -e "Manifest-Version: 1.0 \nMain-Class: $main_class" > MANIFEST.MF
	jar -cfv0m $build_name.jar MANIFEST.MF *
	mv $build_name.jar ../$build_name.jar
	cd ..
}

if [ -n "$build_name" ]; then
	cd $build_dir
	mkdir build-$build_name
	cd build-$build_name

	mkdir java
	mkdir cjava
	cp -R $base_dir/src/com java/com

	# Make each version only have thier natives + runnable type
	# Won't really be needed when I make the launcher but still nice for now
	mkdir linux_x64
	mkdir linux_arm32
	mkdir linux_arm64
	mkdir windows_x86
	mkdir windows_x64
	mkdir macos
	mkdir all
	mkdir source
	
	cp -R $base_dir/src source/src
	cp -R $lib_dir source/lib
	cp -R $resource_dir source/resources/

	#cp -R $base_dir/lib/used/ build/lib
	cp -R $lib_dir lib/
	cd lib

	for filename in ./*.jar; do
		echo "Extracting $filename"
		jar -v0xf $filename
		echo "Extracting complete!"
	done

	cd ..
	rm -fr lib/*.jar
	cp -R lib/* java/

	cd java
	javac -verbose -d ../cjava/ com/brett/Main.java
	cd ..

	cp -R lib/* cjava/

	rm -fr java

	#build for all
	buildjar
	mv $build_name.jar all/${build_name}.jar

	#build for linux64
	rm -fr cjava/macos
	rm -fr cjava/windows
	rm -fr cjava/linux/arm32
	rm -fr cjava/linux/arm64

	buildjar
	mv $build_name.jar linux_x64/${build_name}_linux.jar
	cp -R lib/* cjava/

	#build for linux arm32
	rm -fr cjava/macos
	rm -fr cjava/windows
	rm -fr cjava/linux/x64
	rm -fr cjava/linux/arm64

	buildjar
	mv $build_name.jar linux_arm32/${build_name}_linux.jar
	cp -R lib/* cjava/

	#build for linux arm64
	rm -fr cjava/macos
	rm -fr cjava/windows
	rm -fr cjava/linux/x64
	rm -fr cjava/linux/arm32

	buildjar
	mv $build_name.jar linux_arm64/${build_name}_linux.jar
	cp -R lib/* cjava/

	#build for windows x86
	rm -fr cjava/macos
	rm -fr cjava/linux
	rm -fr cjava/windows/x64

	buildjar
	mv $build_name.jar windows_x86/${build_name}_windows.jar
	cp -R lib/* cjava/

	#build for windows x64
	rm -fr cjava/macos
	rm -fr cjava/linux
	rm -fr cjava/windows/x86

	buildjar
	mv $build_name.jar windows_x64/${build_name}_windows.jar
	cp -R lib/* cjava/

	#build for macos
	rm -fr cjava/linux
	rm -fr cjava/windows

	buildjar
	mv $build_name.jar macos/${build_name}_macos.jar
	cp -R lib/* cjava/
	
	pwd
	
	cd linux_x64
	echo "java -jar ${build_name}_linux.jar" > run.sh
	chmod +x run.sh
	cd $build_dir/build-$build_name
    
    pwd
    
	cd linux_arm32
	echo "java -jar ${build_name}_linux.jar" > run.sh
	chmod +x run.sh
	cd $build_dir/build-$build_name

	cd linux_arm64
	echo "java -jar ${build_name}_linux.jar" > run.sh
	chmod +x run.sh
	cd $build_dir/build-$build_name
    
    pwd
    
	cp -R $resource_dir all/resources/
	cp -R $resource_dir linux_x64/resources/
	cp -R $resource_dir linux_arm32/resources/
	cp -R $resource_dir linux_arm64/resources/
	cp -R $resource_dir macos/resources/
	cp -R $resource_dir windows_x64/resources/
	cp -R $resource_dir windows_x86/resources/

	zip -vr -9 all.zip all/
	zip -vr -9 linux_x64.zip linux_x64/
	zip -vr -9 linux_arm32.zip linux_arm32/
	zip -vr -9 linux_arm64.zip linux_arm64/
	zip -vr -9 macos.zip macos/
	zip -vr -9 windows_x64.zip windows_x64/
	zip -vr -9 windows_x86.zip windows_x86/
	zip -vr -9 source.zip source/

	rm -fr cjava
	rm -fr lib

else
	echo "Please enter a build name!"
fi
