
# Créer l'instance ec2
INSTANCE_NAME=my-gatling
INSTANCE_SIZE=t3.large
docker-machine create --driver amazonec2 --amazonec2-open-port 8000 --amazonec2-region eu-west-1 --amazonec2-instance-type $INSTANCE_SIZE $INSTANCE_NAME

# Importer le projet
docker-machine ssh my-gatling
cd ~/
git clone https://github.com/needfortri/gatling-demo.git
sudo apt install awscli
mkdir -p ~/.aws && printf "[default]\naws_access_key_id = ${AWS_ACCESS_KEY\}naws_secret_access_key = ${AWS_SECRET_ACCESS_KEY}" > ~/.aws/credentials

# Faire tourner le projet
sudo docker run -it --rm \
  -e JAVA_OPTS="-DSERVER_DOMAIN=https://api.example.com -DSERVER_ENDPOINT=/api/example"\
  -v ~/gatling-demo/conf:/opt/gatling/conf \
  -v ~/gatling-demo/user-files:/opt/gatling/user-files \
  -v ~/gatling-demo/results:/opt/gatling/results \
  denvazh/gatling
  
# Exporter les résultats sur S3
aws s3 sync ~/gatling-demo/results s3://my-gatling/
  
# Supprimer la machine
exit
docker-machine rm $INSTANCE_NAME
  
  
