#!/bin/bash
curl -x POST -H 'Content-Type:application/x-sdf' --data-binary @../syntax.xml/xml.def http://127.0.0.1:8080/grammar?module=xml

for i in {1..100}
do
	curl -X GET -H 'Content-Type: application/binary' -d @../renderer.imp/plugin.xml http://127.0.0.1:8080/parse/-993726346
done
