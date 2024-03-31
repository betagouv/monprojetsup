#!/usr/bin/env python3

import json

with open("../java/tmp/profiles.json", "r") as r:
  users = json.load(r)["users"].keys()
  print(users)

