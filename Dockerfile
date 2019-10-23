FROM jboss/wildfly:17.0.1.Final

WORKDIR /opt/jboss/wildfly

COPY ["./server/target/declab-1.3.1.war", "./standalone/deployments/ROOT.war"]

RUN ["mkdir", "./standalone/data"]
# TODO: We should only give permissions to the jboss group.
RUN ["chmod", "-R", "a+rwx", "./standalone/data"]

CMD ["./bin/standalone.sh", "-b", "0.0.0.0"]