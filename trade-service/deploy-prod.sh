#!/usr/bin/env bash
ssh $(whoami)@47.94.144.56 << soff
sudo su - deploy<<EOF
cd /srv/qingboat-backend

echo "=============git pull begin============="
git pull git@github.com:qingboat-tech/qingboat-backend.git
echo "=============git pull done=============="
sleep 1

EOF

echo "=========trade-service stop begin=========="
sudo systemctl stop qingboat-ts.service
echo "========= trade-service stop end =========="

echo "=========build service=========="
cd /srv/qingboat-backend
sudo mvn clean install

echo "=========trade-service restart begin=========="
sudo systemctl restart qingboat-ts.service
echo "========= trade-service restart end =========="
sleep 1
exit
soff