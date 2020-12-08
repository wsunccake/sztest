#!/bin/bash

USERNAME=
PASSWORD=
SERVER=

# create lbs
./lbs.py -u $USERNAME -p $PASSWORD -s $SERVER -a create

# find lbs psk
./lbs.py -u $USERNAME -p $PASSWORD -s $SERVER -a psk

