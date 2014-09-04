#!/bin/bash

kill -9 $(ps ax | grep vurf | grep java | awk '{print $1}')
