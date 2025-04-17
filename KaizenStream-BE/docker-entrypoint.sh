#!/bin/bash
set -a

# Chỉ export biến từ file .env nếu nó CHƯA tồn tại
if [ -f .env ]; then
  while read -r line || [[ -n "$line" ]]; do
    # Bỏ qua dòng trống và comment
    if [[ ! "$line" =~ ^# && "$line" =~ ^[^=]+=[^=]+$ ]]; then
      varname=$(echo "$line" | cut -d '=' -f 1)
      if [ -z "${!varname}" ]; then
        export "$line"
      fi
    fi
  done < .env
fi

set +a

exec java -jar app.jar
