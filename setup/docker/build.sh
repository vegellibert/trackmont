#!/bin/bash

which mvn &> /dev/null || { echo >&2 "Maven package cant be found on path. Aborting."; exit 1; }
which awk &> /dev/null  || { echo >&2 "Awk package cant be found on path. Aborting."; exit 1; }
which docker &> /dev/null  || { echo >&2 "Docker package cant be found on path. Aborting."; exit 1; }
mvn package || { echo >&2 "Maven package has failed. Aborting."; exit 1; }

export company=${1:-"tananaev"}
export software=${2:-"trackmont"}
export _version=$(head -n 10 ./pom.xml |grep version|cut -d ">" -f2|cut -d"<" -f1)
export version=${3:-$_version}

tmp="./setup/docker/tmp"

mkdir -p ${tmp}

cat ./setup/trackmont.xml | awk '/config.default/ && !modif { print;printf("    <entry key=\"web.debug\">true</entry>\n");next; modif=1 } {print}'  > ${tmp}/trackmont.xml
cp -rf ./setup/default.xml ${tmp}
cp -rf ./schema ${tmp}/schema
cp -rf ./templates ${tmp}/templates
cp -rf ./target/tracker-server.jar ${tmp}/trackmont-server.jar
cp -rf ./target/lib ${tmp}/lib
if [ -d ./trackmont-web/web ]; then
  cp -rf ./trackmont-web/web ${tmp}/web
else
  mkdir ${tmp}/web
fi

docker build -t ${company}/${software}:${version} ./setup/docker/

rm -rf ${tmp}
