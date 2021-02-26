for f in *.{wav,WAV}; do ffmpeg -i "$f" -c:a libmp3lame -q:a 2 "${f%.*}.mp3" -c:a libvorbis -q:a 4 "${f%.*}.ogg"; done
