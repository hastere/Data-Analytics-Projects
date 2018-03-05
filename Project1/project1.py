#!/usr/bin/python

# This sample executes a search request for the specified search term.
# Sample usage:
#   python geolocation_search.py --q=surfing --location-"37.42307,-122.08427" --location-radius=50km --max-results=10
# NOTE: To use the sample, you must provide a developer key obtained
#       in the Google APIs Console. Search for "REPLACE_ME" in this code
#       to find the correct place to provide that key..

import argparse

from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

import datetime
from datetime import timedelta

# Set DEVELOPER_KEY to the API key value from the APIs & auth > Registered apps
# tab of
#   https://cloud.google.com/console
# Please ensure that you have enabled the YouTube Data API for your project.
DEVELOPER_KEY = 'AIzaSyC2ZuC0NXMU8LOkiIuxCCuOGUYhbdOQwhQ'
YOUTUBE_API_SERVICE_NAME = 'youtube'
YOUTUBE_API_VERSION = 'v3'

def viewParser(test_line):
  test_list = test_line.split()
  return int(test_list[-1])

def youtube_search(options):
  youtube = build(YOUTUBE_API_SERVICE_NAME, YOUTUBE_API_VERSION,
    developerKey=DEVELOPER_KEY)

  # Call the search.list method to retrieve results matching the specified
  # query term.
  search_response = youtube.search().list(
    order=options.order,
    publishedAfter=options.publishedAfter,
    q=options.q,
    type='video',
    part='id,snippet',
    maxResults=options.max_results
  ).execute()

  search_videos = []

  # Merge video ids
  for search_result in search_response.get('items', []):
    search_videos.append(search_result['id']['videoId'])
  video_ids = ','.join(search_videos)

  # Call the videos.list method to retrieve location details for each video.
  video_response = youtube.videos().list(
    id=video_ids,
    part='snippet, statistics'
  ).execute()

  videos = []

  # Add each result to the list, and then display the list of matching videos.
  for video_result in video_response.get('items', []):
    videos.append('%s\t%s\t%s' % (video_result['id'],
                              video_result['snippet']['title'],
                              video_result['statistics']['viewCount']))

  return videos


if __name__ == '__main__':
  now = datetime.datetime.now()
  yesterday = now - timedelta(hours=24)
  yesterdays = yesterday.isoformat() + 'Z'
  parser = argparse.ArgumentParser()
  parser.add_argument('--q', help='Search term', default='landslide')
  parser.add_argument('--order', help='Order', default='viewCount')
  parser.add_argument('--publishedAfter', help='Published After', default=yesterdays)
  parser.add_argument('--max-results', help='Max results', default=10)
  args = parser.parse_args()

  try:
    videos = youtube_search(args)
  except HttpError, e:
    print 'An HTTP error %d occurred:\n%s' % (e.resp.status, e.content)

#sort output
  videos = sorted(videos, reverse=True, key=viewParser)
#print output
  file = open('output.txt', 'w+')
  file.write("%s\t%s" % (yesterday, now))
  for video in videos:
    file.write('\n%s' % video.encode('utf-8'))
  file.close()
