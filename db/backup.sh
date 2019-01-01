#!/bin/bash
set -e
# backup database
mysqldump -u root --databases portfolio > portfolio.sql

# backup portfolio user
mysql -u root -BNe "select concat('\'',user,'\'@\'',host,'\'') from mysql.user where user = 'portfolio'" | \
while read uh; do mysql -u root -BNe "show grants for $uh" | sed 's/$/;/; s/\\\\/\\/g'; done > grants.sql


