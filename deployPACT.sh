sudo apt update && sudo apt install default-jdk -y && sudo apt install maven -y && sudo apt install docker.io docker-compose -y
sudo docker-compose -f "pact-broker/docker-compose.yml" up -d --build

