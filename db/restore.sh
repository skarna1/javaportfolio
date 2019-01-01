#!/bin/bash
set -e

mysql -u root  < portfolio.sql
mysql -u root < grants.sql
