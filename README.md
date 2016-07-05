#compile JNI of this project
  1. Download NDK from Android site.
  2. Decompress NDK packet at the <place-to-ndk>.
  3. setup "export NDK=<place-to-ndk>"
  4. setup "export PATH=$PATH:<place-to-ndk>"
  5. type "cd <projectpath>/jni".
  6. type "./build.sh".
     if "../streampusher/libs/" not exist,please mkdir it by yourself.

#compile whole project
  1. open the project at android studio
  2. build streampusher,and get "streampusher.aar" at output dir.
  3. copy "streampusher.aar" to "app/libs/" dir
  4. do "build app"
