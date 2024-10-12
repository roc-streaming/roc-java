#!/usr/bin/env python3

import requests
import re
import subprocess

# last roc-java tag, e.g. "v0.2.1"
git_tag = subprocess.check_output(
    ['git', 'describe', '--tags', '--abbrev=0'], text=True).strip()

# prefix for matching roc-toolkit tags, e.g. "v0.2."
tag_prefix = re.sub(r'\.[^.]+$', '.', git_tag)

# list of roc-toolkit tags sorted in version order
response = requests.get('https://api.github.com/repos/roc-streaming/roc-toolkit/tags')
tags = sorted([tag['name'] for tag in response.json()],
              key=lambda v: tuple(map(int, v.lstrip('v').split('.'))))

# last tag matching prefix, e.g. "v0.2.6"
filtered_tags = [tag for tag in tags if tag.startswith(tag_prefix)]
latest_tag = filtered_tags[-1]

print(latest_tag)
