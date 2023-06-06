How to start
0. open project using intellij IDEA, execute mvn clean, mvn install
1. execute ddl under ddls.sql in localhost mysql
2. change mysql username/password under */application-dev.yml if different; default is root:123456
3. start up account-svc, bid-svc, bwic-svc, gateway and pricer-svc
4. under frontend/online-bidding; run ``npm install`` and ``npm run start``
5. open http://localhost to check
   
How to setup skywalking:
1. docker run --name skywalking-bk -p 127.0.0.1:12800:12800 -p 127.0.0.1:11800:11800 --restart always -d apache/skywalking-oap-server
2. docker run --name skywalking-ui -p 127.0.0.1:8080:8080 -e SW_OAP_ADDRESS=http://host.docker.internal:12800 --restart always -d apache/skywalking-ui
3. download skywalking agent: https://skywalking.apache.org/downloads/
4. add vm options in services: -javaagent:/{YOUR_PATH}/skywalking-agent/skywalking-agent.jar=agent.service_name={service-name}

How to setup ELK:


How to package docker
1. run `mvn package` at the project level
2. run `npm run build` in the frontend\online-bidding folder
3. run `docker compose build`
4. run `docker compose up -d`
5. open http://localhost to check

test works fine.