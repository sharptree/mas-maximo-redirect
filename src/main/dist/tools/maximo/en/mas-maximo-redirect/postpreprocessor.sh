function updateServerXml(){
  # Directory to search
  SEARCH_DIR="/opt/IBM/SMP/maximo/deployment/was-liberty-default"

  # Criteria for matching files (e.g., all XML files)
  CRITERIA="server.xml"

  SED_COMMAND='s|<httpDispatcher.*|<httpDispatcher enableWelcomePage=\"false\" trustedHeaderOrigin=\"*\" trustedSensitiveHeaderOrigin=\"*\" appOrContextRootMissingMessage=\"\&lt;script\&gt;document.location.href=\&quot;/maximo/\&quot;;\&lt;/script\&gt;\"/>|g'

  # Find and process each file
  find "$SEARCH_DIR" -type f -name "$CRITERIA" | while read -r FILE; do
      if grep -q 'httpDispatcher' "$FILE"; then
          if ! (grep -q 'appOrContextRootMissingMessage' "$FILE"); then
              sed -i -e "$SED_COMMAND" "$FILE"
          fi
      fi
  done
}

updateServerXml