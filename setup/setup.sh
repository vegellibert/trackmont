#!/bin/sh

if which java &>/dev/null
then
  if java -jar test.jar
  then
    mkdir -p /opt/trackmont
    cp -r * /opt/trackmont
    rm -r ../out
    rm /opt/trackmont/setup.sh
    chmod -R go+rX /opt/trackmont
    /opt/trackmont/bin/installDaemon.sh
  else
    echo 'Java 7 or higher is required'
  fi
else
  echo 'Java runtime is required'
fi
