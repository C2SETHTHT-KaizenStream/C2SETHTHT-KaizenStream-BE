# FROM eclipse-temurin:21-jdk
#
# WORKDIR /app
# RUN apt-get update && apt-get install -y ffmpeg
#
# COPY target/*.jar app.jar
# COPY .env .env
# COPY docker-entrypoint.sh docker-entrypoint.sh
#
# RUN chmod +x docker-entrypoint.sh
#
# ENTRYPOINT ["./docker-entrypoint.sh"]

FROM eclipse-temurin:21-jdk

WORKDIR /app

# Cài các gói cần thiết: ffmpeg, redis-server, nano, wget, ufw và thiết lập mc (MinIO client)
RUN apt-get update && apt-get install -y \
    ffmpeg \
    redis-server \
    nano \
    wget \
    ufw \
 && wget https://dl.min.io/client/mc/release/linux-amd64/mc \
 && chmod +x mc \
 && mv mc /usr/local/bin/mc \
 && rm -rf /var/lib/apt/lists/*

# Cho phép port Redis (nếu bạn expose trong container, nhưng thường dùng Redis bên ngoài)
RUN ufw allow 1935/tcp || true

COPY target/*.jar app.jar
COPY .env .env
COPY docker-entrypoint.sh docker-entrypoint.sh

RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["./docker-entrypoint.sh"]
