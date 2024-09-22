# Auto update dingzhenservlet && build
# Make sure you have git and jdk 17 installed
echo "Checkout master..."
git checkout master
echo "Fetching..."
git pull
echo "Building from source..."
bash ./gradlew bootJar
echo "Done."
