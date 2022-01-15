#!/bin/bash

export JAVA_OPTS="-Xmx64m"
kotlin -cp . ve.usb.grafoLib.HeuristicaRPP $*