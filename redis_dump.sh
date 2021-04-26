#!/bin/bash

#redis 源ip
src_ip=172.16.250.38
#redis 源port
src_port=6379

#redis 目的ip
dest_ip=172.16.250.39
#redis 目的port
dest_port=6379

#要迁移的key前缀
key_prefix=

i=1

redis-cli -h $src_ip -p $src_port -n 1 keys "${key_prefix}*" | while read key
do
  redis-cli -h $dest_ip -p $dest_port -a 1qaz2wsx -n 1 del $key
  redis-cli -h $src_ip -p $src_port -n 1 --raw dump $key | perl -pe 'chomp if eof' | redis-cli -h $dest_ip -p $dest_port -a 1qaz2wsx -n 1 -x restore $key 0
  echo "$i migrate key $key"
  ((i++))
done