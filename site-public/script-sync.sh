#!/bin/bash

# Variables
DIRECTORY_TO_WATCH="/var/www/wordpress/wp-content/export_static/"
WORDPRESS_UPLOAD_DIR="/var/www/wordpress/wp-content/uploads/"
BASE_BRANCH="demo"
ORIGINAL_REPO="betagouv/monprojetsup"
FORKED_REPO="devs-mps/monprojetsup"
BRANCH_NAME="wp-static-$(date +'%Y%m%d%H%M%S')"
COMMIT_MESSAGE="Mise à jour de l'export statique WP $(date +'%Y%m%d%H%M%S')"
PR_TITLE="Mise à jour de l'export statique WP $(date +'%Y%m%d%H%M%S')"
PR_BODY="Pull request générée automatiquement"
FOLDER="monprojetsup"
GITHUB_USER="devs-mps"
DIRECTORY_FOR_STATIC_FILES_IN_REPO="site-public/statics/"
DIRECTORY_FOR_UPLOAD_FILES_IN_REPO="site-public/statics/wp-content/uploads/"
CACHE_FILE="./md5-wp-static.txt"
LOCK_FILE="/tmp/wp-static.lock"

submit_pull_request() {
  gh repo fork $ORIGINAL_REPO --clone

  cd $FOLDER

  mkdir -p $DIRECTORY_FOR_STATIC_FILES_IN_REPO
  mkdir -p $DIRECTORY_FOR_UPLOAD_FILES_IN_REPO

  cp -r $DIRECTORY_TO_WATCH/. $DIRECTORY_FOR_STATIC_FILES_IN_REPO
  rsync -av --progress --include='20*/' --include='20*/**' --exclude='*' $WORDPRESS_UPLOAD_DIR $DIRECTORY_FOR_UPLOAD_FILES_IN_REPO

  git config --global user.email "devs@monprojetsup.fr"
  git config --global user.name "Devs MPS"

  git checkout -b $BRANCH_NAME

  git add .

  git commit -m "$COMMIT_MESSAGE"

  git push origin $BRANCH_NAME

  gh pr create --repo $ORIGINAL_REPO --title "$PR_TITLE" --body "$PR_BODY" --head "$GITHUB_USER:$BRANCH_NAME" --base $BASE_BRANCH

  cd ..
  rm -Rf $FOLDER
}

check_for_changes() {
  current_state=$(find "$DIRECTORY_TO_WATCH" -type f -exec md5sum {} \; | sort -k 2 | md5sum)
  if [ -f "$CACHE_FILE" ]; then
    previous_state=$(cat "$CACHE_FILE")
  else
    previous_state=""
  fi

  if [ "$current_state" != "$previous_state" ]; then
    if [ "$previous_state" != "" ]; then
      echo "Changes detected."
	if find "$DIRECTORY_TO_WATCH" -type f -mmin -1 | grep -q '.'; then
	  echo "Last changes was less than 1 minutes ago. We are waiting for futher changes. Exiting."
        else
	  echo "$current_state" > "$CACHE_FILE"
	  submit_pull_request
        fi
    else
      echo "Non existing md5 signature for folder. Exiting for this one see you next time."
    fi
  else
    echo "No changes. Exiting"
  fi
}

if [ -e "$LOCK_FILE" ] && kill -0 "$(cat "$LOCK_FILE")"; then
  echo "Script is already running."
  exit
fi

echo $$ > "$LOCK_FILE"

trap 'rm -f "$LOCK_FILE"' EXIT

check_for_changes