#!/usr/bin/python

#-----------------------------------------------------------------------
# twitter-stream-format:
#  - ultra-real-time stream of twitter's public timeline.
#    does some fancy output formatting.
#-----------------------------------------------------------------------

from twitter import *
import re
import time
import json
import datetime

#search_term = 'bieber'

#-----------------------------------------------------------------------
# import a load of external features, for text display and date handling
# you will need the termcolor module:
#
# pip install termcolor
#-----------------------------------------------------------------------
from time import strftime
from textwrap import fill
from termcolor import colored
from email.utils import parsedate
from datetime import timedelta

#-----------------------------------------------------------------------
# load our API credentials
#-----------------------------------------------------------------------
config = {}
execfile('config.py', config)

#-----------------------------------------------------------------------
# create twitter API object
#-----------------------------------------------------------------------

auth = OAuth(config['access_key'], config['access_secret'], config['consumer_key'], config['consumer_secret'])
stream = TwitterStream(auth = auth, secure = True)

#-----------------------------------------------------------------------
# iterate over tweets matching this filter text
#-----------------------------------------------------------------------

iterator = stream.statuses.sample()
start = datetime.datetime.now()
end = start + timedelta(minutes=10)
t_end = time.time() + (60 * 10)
my_dict = {'-1' : 0}
for tweet in iterator:
	if time.time() > t_end:
  		break
	else:
 		holder = json.loads(json.dumps(tweet))
 		if 'entities' in holder:
 			count = 0
 			while count < len(holder['entities']['hashtags']):
				hashtag = holder['entities']['hashtags'][count]['text']
				if hashtag in my_dict:
					my_dict[hashtag] = my_dict[hashtag] + 1
				else:
					my_dict[hashtag] = 1
  				count += 1

file = open('output.txt', 'w+')
file.write("%s\t%s" % (start, end))

for hashtag in sorted(my_dict, key = my_dict.__getitem__, reverse = True)[:10]:
	file.write('\n%s\t%d' % (hashtag.encode('utf-8'), my_dict[hashtag]))
