#!/bin/sh

# get location of this script to find the neighbouring jar
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ] ; do SOURCE="$(readlink "$SOURCE")"; done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

# echo `date` >> log
# echo "Run parser.sh with following args:" >> log
# echo $@ >> log
# echo "Done" >> log
# echo `date` >> log

# tee stdin | java -jar "$DIR/treedecor-parser-0.0.5.jar" $@ | tee stdout

java -jar "$DIR/treedecor-parser-0.0.6.jar" $@
