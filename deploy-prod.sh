#!/bin/bash

# 可选参数-s传递需要部署的服务,默认为全部部署
while getopts "s:" opt; do
    case "$opt" in
    s)  SVAR="$OPTARG"
        ;;
    *)  echo "Illegal argument."; exit 1
        ;;
    esac
done

echo "SVAR set to: $SVAR"


# 开始执行打包命令
ssh qingboat@39.105.29.208<< soff

cd /srv/qingboat-backend

echo "=============git pull begin============="
# git pull git@github.com:qingboat-tech/qingboat-backend.git
echo "=============git pull done=============="
sleep 1


echo "=========build service=========="
cd /srv/qingboat-backend
sudo mvn clean install

echo "=========service restart begin=========="

case "$SVAR" in
  ts)
    sudo systemctl restart qingboat-ts.service
      ;;
  as)
    sudo systemctl restart qingboat-as.service
      ;;
  us)
    sudo systemctl restart qingboat-us.service
      ;;
  *)
    sudo systemctl restart qingboat-ts.service
    sudo systemctl restart qingboat-as.service
    sudo systemctl restart qingboat-us.service

      ;;
  esac


echo "========= service restart end =========="
sleep 1
exit
soff