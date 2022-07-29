#!/bin/sh
if [ -z "$CATALINA_HTTP_PORT" ]
then
 export CATALINA_HTTP_PORT=8080
fi

echo "Catalina HTTP port is set to $CATALINA_HTTP_PORT"
export "JAVA_OPTS=$JAVA_OPTS -Dport.http.nonssl=$CATALINA_HTTP_PORT"
echo "JAVA_OPTS=$JAVA_OPTS"
