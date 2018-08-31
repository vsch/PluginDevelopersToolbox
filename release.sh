#!/usr/bin/env bash
HOME_DIR="/Users/vlad/src/projects/PluginDevelopersToolbox"

cd ${HOME_DIR}/libprep

# copy production to local for jarring
if [ -d "PluginDevelopersToolbox-raw" ]; then
    echo deleting PluginDevelopersToolbox-raw
    rm -fr ./PluginDevelopersToolbox-raw
fi

cp -R ../out/production/PluginDevelopersToolbox ./PluginDevelopersToolbox-raw

# create javafx classes and move them from the main jar dir
if [ -d "PluginDevelopersToolbox-javafx" ]; then
    echo deleting PluginDevelopersToolbox-javafx
    rm -fr ./PluginDevelopersToolbox-javafx
fi

# create the main jar without the javafx dependent files
if [ -f "PluginDevelopersToolbox.jar" ]; then
    echo deleting PluginDevelopersToolbox.jar
    rm -fr ./PluginDevelopersToolbox.jar
fi
cd ./PluginDevelopersToolbox-raw
zip -rq ../PluginDevelopersToolbox.jar \
./icons \
./intentionDescriptions \
./com \
./META-INF

cd ..

# create distribution directory then zip it
if [ -d "PluginDevelopersToolbox" ]; then
    echo deleting PluginDevelopersToolbox
    rm -fr ./PluginDevelopersToolbox
fi
mkdir -p ./PluginDevelopersToolbox
cp -R ../lib ./PluginDevelopersToolbox
#rm ./PluginDevelopersToolbox/lib/*source*.jar
cp PluginDevelopersToolbox.jar ./PluginDevelopersToolbox/lib/PluginDevelopersToolbox.jar
#cp PluginDevelopersToolbox-javafx.jar ./PluginDevelopersToolbox/lib/PluginDevelopersToolbox-javafx.jar
#cp -R PluginDevelopersToolbox-raw/META-INF ./PluginDevelopersToolbox

# delete kotlin-runtime-sources.jar
if [ -f "./PluginDevelopersToolbox/lib/kotlin-runtime-sources.jar" ]; then
    echo deleting ./PluginDevelopersToolbox/lib/kotlin-runtime-sources.jar
    rm -f ./PluginDevelopersToolbox/lib/kotlin-runtime-sources.jar
fi

# create the distribution zip
if [ -f "PluginDevelopersToolbox.zip" ]; then
    echo deleting PluginDevelopersToolbox.zip
    rm -fr ./PluginDevelopersToolbox.zip
fi
zip -r ./PluginDevelopersToolbox.zip \
PluginDevelopersToolbox/lib \
PluginDevelopersToolbox/META-INF

# copy zip to main directory
cp ./PluginDevelopersToolbox.zip ../PluginDevelopersToolbox.zip

if [ -d "PluginDevelopersToolbox-zip" ]; then
    echo deleting PluginDevelopersToolbox-zip
    rm -fr ./PluginDevelopersToolbox-zip
fi
unzip -bq ./PluginDevelopersToolbox.zip -d ./PluginDevelopersToolbox-zip

# unzip jars from distribution directory
#if [ -d "PluginDevelopersToolbox-javafx-jar" ]; then
#    echo deleting PluginDevelopersToolbox-javafx-jar
#    rm -fr ./PluginDevelopersToolbox-javafx-jar
#fi
#unzip -bq ./PluginDevelopersToolbox-zip/PluginDevelopersToolbox/lib/PluginDevelopersToolbox-javafx.jar -d ./PluginDevelopersToolbox-javafx-jar

if [ -d "PluginDevelopersToolbox-jar" ]; then
    echo deleting PluginDevelopersToolbox-jar
    rm -fr ./PluginDevelopersToolbox-jar
fi
unzip -bq ./PluginDevelopersToolbox-zip/PluginDevelopersToolbox/lib/PluginDevelopersToolbox.jar -d ./PluginDevelopersToolbox-jar

rm -fr /Users/vlad/Library/Caches/IntelliJIdea15/plugins-sandbox-15eap/plugins/PluginDevelopersToolbox
unzip -bq ../PluginDevelopersToolbox.zip -d /Users/vlad/Library/Caches/IntelliJIdea15/plugins-sandbox-15eap/plugins

rm -fr /Users/vlad/Library/Caches/IntelliJIdea15/plugins-sandbox-15ce/plugins/PluginDevelopersToolbox
unzip -bq ../PluginDevelopersToolbox.zip -d /Users/vlad/Library/Caches/IntelliJIdea15/plugins-sandbox-15ce/plugins



# update all the sandbox directories
function Upd() {
    PRODUCT=$1
    for SANDBOX in ${1:+"$@"}
    do
        if [ -d /Users/vlad/Library/Caches/${PRODUCT}/${SANDBOX}/plugins ]; then
            echo updating ${PRODUCT}/${SANDBOX}
        else
            echo creating ${PRODUCT}/${SANDBOX}
            mkdir -p /Users/vlad/Library/Caches/${PRODUCT}/${SANDBOX}/plugins
        fi

        rm -fr /Users/vlad/Library/Caches/${PRODUCT}/${SANDBOX}/plugins/PluginDevelopersToolbox
        unzip -bq ../PluginDevelopersToolbox.zip -d /Users/vlad/Library/Caches/${PRODUCT}/${SANDBOX}/plugins
    done
}

#Upd "IdeaIC15" "plugins-sandbox-14ce" "plugins-sandbox-15ce" "plugins-sandbox-16ce" "plugins-sandbox-15eap" "plugins-sandbox-16eap"
#Upd "IdeaIC16" "plugins-sandbox-14ce" "plugins-sandbox-15ce" "plugins-sandbox-16ce" "plugins-sandbox-15eap" "plugins-sandbox-16eap"
#Upd "IntelliJIdea15" "plugins-sandbox-14ce" "plugins-sandbox-15ce" "plugins-sandbox-16ce" "plugins-sandbox-15eap" "plugins-sandbox-16eap"
#Upd "IntelliJIdea16" "plugins-sandbox-14ce" "plugins-sandbox-15ce" "plugins-sandbox-16ce" "plugins-sandbox-15eap" "plugins-sandbox-16eap"

Upd "IdeaIC2016.1" "plugins-sandbox-mn" "plugins-sandbox-php" "plugins-sandbox-16ce"
Upd "IdeaIC2016-2" "plugins-sandbox-mn" "plugins-sandbox-php" "plugins-sandbox-16ce"
