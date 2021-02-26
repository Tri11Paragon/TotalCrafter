find . -type f -name '*.wav' -exec bash -c 'ffmpeg -i "$0" -c:a libmp3lame -q:a 2 "${0/%wav/mp3}" -c:a libvorbis -q:a 4 "${f/%wav/ogg}' '{}' \;
