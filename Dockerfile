FROM jboss/wildfly:16.0.0.Final
WORKDIR /opt/jboss/wildfly

COPY ["./server/target/tester-1.0.0.war", "./standalone/deployments/ROOT.war"]

CMD ["./bin/standalone.sh", "-b", "0.0.0.0"]