#!/usr/bin/env bash
set -e

if [ "$TODO_API_URL" ]
then
  TODO_TEXT="Read $(curl -sI 'https://en.wikipedia.org/wiki/Special:Random' | sed -n 's/location: //p')"
  # shellcheck disable=SC2016
  curl -d "$TODO_TEXT" -H "Content-Type: application/json" -s -X POST "$TODO_API_URL"
  echo "A new daily TODO (""$TODO_TEXT"") is posted"
fi