#!/bin/bash
set -euxo pipefail

work_dir=$(pwd)

# setup android sdk tools cli
wget -q "https://dl.google.com/android/repository/commandlinetools-linux-6200805_latest.zip" -O cmd-tools.zip
mkdir -p $ANDROID_SDK_ROOT/cmdline-tools
unzip -qq cmd-tools.zip -d $ANDROID_SDK_ROOT/cmdline-tools

cd $ANDROID_SDK_ROOT/cmdline-tools/tools/bin
mkdir jaxb_lib
wget -q https://repo1.maven.org/maven2/javax/activation/activation/1.1.1/activation-1.1.1.jar -O jaxb_lib/activation.jar
wget -q https://repo1.maven.org/maven2/javax/xml/jaxb-impl/2.1/jaxb-impl-2.1.jar -O jaxb_lib/jaxb-impl.jar
wget -q https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-xjc/2.3.2/jaxb-xjc-2.3.2.jar -O jaxb_lib/jaxb-xjc.jar
wget -q https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-core/2.3.0.1/jaxb-core-2.3.0.1.jar -O jaxb_lib/jaxb-core.jar
wget -q https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-jxc/2.3.2/jaxb-jxc-2.3.2.jar -O jaxb_lib/jaxb-jxc.jar
wget -q https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-runtime/2.3.1/jaxb-runtime-2.3.1.jar -O jaxb_lib/jaxb-runtime.jar
wget -q https://repo1.maven.org/maven2/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar -O jaxb_lib/jaxb-api.jar

# add jaxb to sdkmanager classpath
sed '/CLASSPATH=[$]APP_HOME/ a CLASSPATH=$CLASSPATH:$APP_HOME/bin/jaxb_lib/activation.jar:$APP_HOME/bin/jaxb_lib/jaxb-impl.jar:$APP_HOME/bin/jaxb_lib/jaxb-xjc.jar:$APP_HOME/bin/jaxb_lib/jaxb-runtime.jar:$APP_HOME/bin/jaxb_lib/jaxb-core.jar:$APP_HOME/bin/jaxb_lib/jaxb-jxc.jar:$APP_HOME/bin/jaxb_lib/jaxb-api.jar' sdkmanager > sdkmanager.tmp
chmod +x sdkmanager.tmp
mv -f sdkmanager.tmp sdkmanager

mkdir -p $HOME/.android && touch $HOME/.android/repositories.cfg

cd $work_dir
NDK=$(echo $ANDROID_NDK_VERSION | cut -d "." -f1)
docker build -t cross-linux-android:api$ANDROID_API \
        -f scripts/travis/android/Dockerfile \
        --build-arg NDK=$NDK \
        --build-arg API=$ANDROID_API .
