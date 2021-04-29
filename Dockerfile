FROM jboss/wildfly:23.0.1.Final

ARG VERSION
WORKDIR /opt/jboss/wildfly

COPY ["./server/target/declab-${VERSION}.war", "./standalone/deployments/ROOT.war"]

RUN ["mkdir", "./standalone/data"]
# TODO: We should only give permissions to the jboss group.
RUN ["chmod", "-R", "a+rwx", "./standalone/data"]

CMD ["./bin/standalone.sh", "-b", "0.0.0.0"]