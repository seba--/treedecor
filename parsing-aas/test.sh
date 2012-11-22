#!/bin/bash

# Call with host:port as first argument, e.g.
# sh test.sh vh12009.mathematik.uni-marburg.de:55123

HOST=$1

curl -X POST -H 'Content-Type:application/x-sdf' --data-binary @../syntax.xml/xml.def "http://$HOST/grammar?module=xml"

for i in {1..100}
do
	curl -X GET -H 'Content-Type: application/binary' -d @../renderer.imp/plugin.xml "http://$HOST/parse/-993726346?disableSourceLocationInformation=true"
done

for i in {1..100}
do
	curl -X GET -H 'Content-Type: application/binary' -d @../renderer.imp/plugin.xml "http://$HOST/parse/-993726346"
done
