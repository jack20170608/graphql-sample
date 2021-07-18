#!/bin/sh

nohup java -Xmx2G -Xms2G -cp "hazelcast-management-center-3.12.10/hazelcast-mancenter-3.12.10.war" Launcher 10010 hz > logs/hzcenter-3.12.10.log 2>&1 & echo $! > logs/mgc.pid
