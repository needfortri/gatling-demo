
# Prerequisites
# TODO

# Create EC2 instance
INSTANCE_NAME=my-gatling
INSTANCE_SIZE=t3.large
docker-machine create --driver amazonec2 --amazonec2-open-port 8000 --amazonec2-region eu-west-1 --amazonec2-instance-type $INSTANCE_SIZE $INSTANCE_NAME

# Configure env vars
MAX_NB_USERS=10000
TEST_DURATION=60
SERVER_DOMAIN=https://api.example.com
SERVER_ENDPOINT=/api/example
S3_BUCKET=my-bucket
AWS_ACCESS_KEY=AWS_ACCESS_KEY
AWS_SECRET_ACCESS_KEY=AWS_SECRET_ACCESS_KEY

# Clone project
docker-machine ssh my-gatling
cd ~/
git clone https://github.com/needfortri/gatling-demo.git

# Run simulation
sudo docker run -it --rm \
  -e JAVA_OPTS="-DMAX_NB_USERS=${MAX_NB_USERS} -DTEST_DURATION=${TEST_DURATION} -DSERVER_DOMAIN=${SERVER_DOMAIN} -DSERVER_ENDPOINT=${SERVER_ENDPOINT}"\
  -v ~/gatling-demo/conf:/opt/gatling/conf \
  -v ~/gatling-demo/user-files:/opt/gatling/user-files \
  -v ~/gatling-demo/results:/opt/gatling/results \
  denvazh/gatling
  
# Export results on S3 bucket
sudo apt install awscli
mkdir -p ~/.aws && printf "[default]\naws_access_key_id = ${AWS_ACCESS_KEY}\naws_secret_access_key = ${AWS_SECRET_ACCESS_KEY}" > ~/.aws/credentials
aws s3 sync ~/gatling-demo/results s3://${S3_BUCKET}/
  
# Delete EC3 instance
exit
docker-machine rm $INSTANCE_NAME
  
# Troubleshooting

"Error checking and/or regenerating the certs: There was an error validating certificates for host"
docker-machine regenerate-certs my-gatling
docker-machine restart my-gatling