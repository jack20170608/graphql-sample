#!/bin/sh

PIDFILE="$PWD/logs/mgc.pid" 

if [ -f $PIDFILE ]; then
  echo "pid exists ...."
  PID=$(cat $PIDFILE)
  echo $PID
  kill -9 $PID
fi
